pragma solidity ^0.4.2;

import "../permission/OperatorPermission.sol";
import "../permission/GateControl.sol";
import "./TNLoanFee.sol";
import "../utils/ByteUtils.sol";
import './AgreementInfo.sol';
import "../math/SafeMath.sol";
import "../utils/FastFailure.sol";

contract LoanService is OperatorPermission, GateControl ,FastFailure{

    using SafeMath for uint256;
    struct Order {
        uint256 id;
        uint256 version;
        address borrower;
        bytes32 idHash;
        Status status;
        uint256 fee;
        bytes applicationDigest;
        bytes agreementDigest;
        bytes repayDigest;
        string receiverAddress;
    }

    enum Status {INVALID, CREATED, CANCELLED, AUDITING, FAILURE, DELIVERIED, RECEIVIED, REPAID}

    Order[] public orders;

    mapping(address => uint256[]) public borrowerIndex;

    mapping(bytes32 => uint256[]) public idHashIndex;

    TNLoanFee public loanFee;

    AgreementInfo public agreementContract;

    uint256 public constant MAX_SIZE = 300;

    event orderUpdated(uint256 indexed id, address indexed borrower, Status status);

    function LoanService(address agreementContractAddress) public {
        register("TNLoanServiceModule", "0.0.1.0", "TNLoanService", "0.0.1.0");
        setAgreementContract(agreementContractAddress);
        orders.push(Order(0, 0, address(0), "", Status.INVALID, 0, "", "", "", ""));
    }


    function insertOrder(uint256 _version, address _borrower, bytes32 _idHash, Status _status, uint256 _fee, bytes _applicationDigest, string receiverAddress)
    internal returns (uint256 newOrderId){

        newOrderId = orders.push(Order(0, _version, _borrower, _idHash, _status, _fee, _applicationDigest, "", "", receiverAddress)) - 1;
        orders[newOrderId].id = newOrderId;
        borrowerIndex[_borrower].push(newOrderId);
        idHashIndex[_idHash].push(newOrderId);

        orderUpdated(newOrderId, _borrower, _status);

    }

    function apply(uint256 _version, bytes32 _idHash, bytes _applicationDigest, uint256 inputFee, string receiverAddress) external returns
    (uint256 newOrderId){
        onlyGateOpen();
        //由于调用超级转账接口，所以最终服务合约有义务校验使用者直接调用，防止钓鱼
        if (!(msg.sender == tx.origin)) {
            log("!(msg.sender == tx.origin)");
            throw;
        }

        if (!(_version > 0)) {
            log("!(_version > 0)");
            throw;
        }

        if (!(uint256(_idHash) != 0)) {
            log("!(uint256(_idHash)!=0)");
            throw;
        }

        if (!(_applicationDigest.length > 0 && _applicationDigest.length <= MAX_SIZE)) {
            log("!(_applicationDigest.length > 0 && _applicationDigest.length <= MAX_SIZE)");
            throw;
        }

        if (!(bytes(receiverAddress).length > 0 && bytes(receiverAddress).length <= MAX_SIZE)) {
            log("!(bytes(receiverAddress).length > 0 && bytes(receiverAddress).length<=MAX_SIZE)");
            throw;
        }
        uint256[] memory userOrders = borrowerIndex[msg.sender];
        if (userOrders.length > 0) {
            Order previousOrder = orders[userOrders[userOrders.length - 1]];
            Status[] memory exptectedStatusArray = new  Status[](1);
            exptectedStatusArray[0] = Status.CREATED;
            if (!(!checkStatus(previousOrder.status, exptectedStatusArray))) {
                log("!(!checkStatus(previousOrder.status, exptectedStatusArray))");
                throw;
            }
        }
        //输入的费用就是订单的费用，如果费用不合法，事务会回滚
        newOrderId = insertOrder(_version, msg.sender, _idHash, Status.CREATED, inputFee, _applicationDigest, receiverAddress);
        if (loanFee != address(0)) {
            loanFee.receive(inputFee);
        }

    }

    function cancel(uint256 id) external {
        Order storage order = innerGetOrder(id);

        if(!((operators[msg.sender] || msg.sender == owner) || (order.borrower == msg.sender))){
            log("!((operators[msg.sender] || msg.sender == owner) || (order.borrower == msg.sender))");
            throw;
        }
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.CREATED;

        checkAndChangeStatus(order, exptectedStatusArray, Status.CANCELLED);
        if (loanFee != address(0)) {
            loanFee.send(order.borrower, order.fee);
        }
    }

    function audit(uint256 id) external {
        onlyOperator();
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.CREATED;
        getOrderAndChangeStatus(id, exptectedStatusArray, Status.AUDITING);
    }

    function deliver(uint256 id, bytes repayDigest, bytes agreementDigest) external {
        onlyOperator();
        if (!(agreementContract != address(0))) {
            log("!(agreementContract != address(0))");
            throw;
        }

        if (!(agreementDigest.length >= 0 && agreementDigest.length < MAX_SIZE)) {
            log("!(agreementDigest.length >= 0 && agreementDigest.length < MAX_SIZE)");
            throw;
        }

        if (!(repayDigest.length >= 0 && repayDigest.length < MAX_SIZE)) {
            log("!(repayDigest.length >= 0 && repayDigest.length < MAX_SIZE)");
            throw;
        }

        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.AUDITING;
        Order storage order = innerGetOrder(id);
        checkAndChangeStatus(order, exptectedStatusArray, Status.DELIVERIED);
        order.agreementDigest = agreementDigest;
        order.repayDigest = repayDigest;

        agreementContract.createAgreement(
            order.version,
            order.id,
            order.borrower,
            order.idHash,
            order.applicationDigest,
            order.agreementDigest,
            order.repayDigest);
    }

    function deliverFailure(uint256 id) external {
        onlyOperator();
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.AUDITING;
        getOrderAndChangeStatus(id, exptectedStatusArray, Status.FAILURE);
        if (loanFee != address(0)) {
            loanFee.send(order.borrower, order.fee);
        }
    }

    function receive(uint256 id) external {
        Order storage order = innerGetOrder(id);
        onlyBorrower(order);
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.DELIVERIED;
        checkAndChangeStatus(order, exptectedStatusArray, Status.RECEIVIED);
    }

    function onlyBorrower(Order storage order) internal{
        onlyGateOpen();
        if (!(order.borrower == msg.sender)) {
            log("!(order.borrower == msg.sender)");
            throw;
        }
    }

    function confirmRepay(uint256 id) external {
        onlyOperator();
        if (!(agreementContract != address(0))) {
            log("!(agreementContract != address(0))");
            throw;
        }
        Status[] memory exptectedStatusArray = new  Status[](2);
        exptectedStatusArray[0] = Status.RECEIVIED;
        exptectedStatusArray[1] = Status.DELIVERIED;

        getOrderAndChangeStatus(id, exptectedStatusArray, Status.REPAID);
        agreementContract.finishAgreement(id);
    }

    function updateRepayDigest(uint256 id, bytes repayDigest) external {
        onlyOperator();
        if (!(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE)) {
            log("!(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE)");
            throw;
        }

        Order storage order = innerGetOrder(id);
        Status[] memory exptectedStatusArray = new  Status[](2);
        exptectedStatusArray[0] = Status.RECEIVIED;
        exptectedStatusArray[1] = Status.DELIVERIED;
        checkStatus(order.status, exptectedStatusArray);

        order.repayDigest = repayDigest;
        agreementContract.updateRepayDigest(id, repayDigest);
    }


    function getOrderAndChangeStatus(uint256 id, Status[] fromStatusArray, Status nextStatus) internal {
        Order storage order = innerGetOrder(id);
        checkAndChangeStatus(order, fromStatusArray, nextStatus);
    }

    function innerGetOrder(uint256 orderId) internal returns (Order storage order){
        if (!(orderId > 0)) {
            log("!(orderId > 0)");
            throw;
        }
        order = orders[orderId];
        if (!(order.id > 0)) {
            log("!(order.id > 0)");
            throw;
        }
    }

    function checkStatus(Status inputStatus, Status[] expectedStatusArray) internal returns (bool){
        for (uint256 i = 0; i < expectedStatusArray.length; i++) {
            if (inputStatus == expectedStatusArray[i]) {
                return true;
            }
        }
        return false;
    }

    function checkAndChangeStatus(Order storage order, Status[] fromStatusArray, Status nextStatus)
    internal {
        if (!(checkStatus(order.status, fromStatusArray))) {
            log("!(checkStatus(order.status, fromStatusArray))");
            throw;
        }
        justChangeStatus(order, nextStatus);
    }

    function justChangeStatus(Order storage order, Status nextStatus) internal {
        //状态机跃迁
        order.status = nextStatus;
        orderUpdated(order.id, order.borrower, nextStatus);
    }

    function getOrder(uint256 _orderId) constant external returns (
        uint256 id,
        uint256 version,
        address borrower,
        bytes32 idHash,
        Status status,
        uint256 fee,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest,
        string receiverAddress) {
        if (!(_orderId > 0 && _orderId < orders.length)) {
            log("!(_orderId > 0 && _orderId < orders.length)");
            throw;
        }
        Order memory order = orders[_orderId];
        return (
        order.id,
        order.version,
        order.borrower,
        order.idHash,
        order.status,
        order.fee,
        order.applicationDigest,
        order.agreementDigest,
        order.repayDigest,
        order.receiverAddress);
    }

    function getOrderLength() constant external returns (uint256 length) {
        return orders.length;
    }

    function getOrderArrayLengthByBorrowerIndex(address _borrower) public constant returns (uint256){
        if (!(_borrower != address(0))) {
            log("!(_borrower != address(0))");
            throw;
        }
        return borrowerIndex[_borrower].length;
    }

    function getOrderArrayLengthByIdHashIndex(bytes32 _idHash) public constant returns (uint256){
        if (!(uint256(_idHash) != 0)) {
            log("!(_uint256(_idHash)!=0)");
            throw;
        }
        return idHashIndex[_idHash].length;
    }

    function queryOrderIdListByIdHashIndex(bytes32 _idHash, uint256 from, uint256 to) public constant returns (uint256[]){
        if (!(to < getOrderArrayLengthByIdHashIndex(_idHash))) {
            log("!(to < getOrderArrayLengthByIdHashIndex(_idHash))");
            throw;
        }
        if (!(from <= to)) {
            log("!(from <= to)");
            throw;
        }

        uint256 length = to.sub(from).add(1);
        uint256[] memory orderIdList = new uint256[](length);
        for (uint256 i = from; i <= to; i++) {
            orderIdList[i.sub(from)] = idHashIndex[_idHash][i];
        }
        return orderIdList;
    }

    function queryOrderIdListByBorrowIndex(address borrower, uint256 from, uint256 to) public constant returns (uint256[]){
        if (!(to < getOrderArrayLengthByBorrowerIndex(borrower))) {
            log("!(to < getOrderArrayLengthByBorrowerIndex(borrower))");
            throw;
        }
        if (!(from <= to)) {
            log("!(from <= to)");
            throw;
        }

        uint256 length = to.sub(from).add(1);
        uint256[] memory orderIdList = new uint256[](length);
        for (uint256 i = from; i <= to; i++) {
            orderIdList[i.sub(from)] = borrowerIndex[borrower][i];
        }
        return orderIdList;
    }

    function setAgreementContract(address agreementContractAddress) public {
        onlyOwner();
        if (!(agreementContractAddress != address(0))) {
            log("!(agreementContractAddress != address(0))");
            throw;
        }
        agreementContract = AgreementInfo(agreementContractAddress);
    }

    function setLoanFeeAddress(address loanFeeAddress) public {
        onlyOwner();
        loanFee = TNLoanFee(loanFeeAddress);
    }


    function getMinFee() public constant returns (uint256 fee){
        if (loanFee != address(0)) {
            fee = loanFee.getMinFee();
        }
    }

}