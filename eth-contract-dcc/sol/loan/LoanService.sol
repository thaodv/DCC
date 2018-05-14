pragma solidity ^0.4.2;

import "../ownership/OperatorPermission.sol";
import "../ownership/GateControl.sol";
import "../utils/FastFailure.sol";
import './AgreementInfo.sol';
import "./LoanFee.sol";
import "../math/SafeMath.sol";
import "../utils/FastFailure.sol";

//借贷的主要处理逻辑
contract LoanService is OperatorPermission, GateControl, FastFailure {

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

    enum Status {INVALID, CREATED, CANCELLED, AUDITING, REJECTED, APPROVED, FAILURE, DELIVERIED, RECEIVIED, REPAID}

    Order[] public orders;

    mapping(address => uint256[]) public borrowerIndex;

    mapping(bytes32 => uint256[]) public idHashIndex;

    LoanFee public loanFee;

    AgreementInfo public agreementContract;

    uint256 public constant MAX_SIZE = 300;

    event orderUpdated(uint256 indexed id, address indexed borrower, Status status);

    function LoanService(address agreementContractAddress) public {
        setAggreementContract(agreementContractAddress);
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

    function apply(uint256 _version, bytes32 _idHash, bytes _applicationDigest, uint256 inputFee, string receiverAddress) onlyGateOpen external returns
    (uint256 newOrderId){
        //由于调用超级转账接口，所以最终服务合约有义务校验使用者直接调用，防止钓鱼
        require(msg.sender == tx.origin);
        require(_version > 0);
        require(uint256(_idHash) != 0);
        require(_applicationDigest.length > 0 && _applicationDigest.length <= MAX_SIZE);
        require(bytes(receiverAddress).length > 0 && bytes(receiverAddress).length <= MAX_SIZE);

        uint256[] memory userOrders = borrowerIndex[msg.sender];
        if (userOrders.length > 0) {
            Order previousOrder = orders[userOrders[userOrders.length - 1]];
            Status[] memory exptectedStatusArray = new  Status[](1);
            exptectedStatusArray[0] = Status.CREATED;
            require(!checkStatus(previousOrder.status, exptectedStatusArray));
        }
        //输入的费用就是订单的费用，如果费用不合法，事务会回滚
        newOrderId = insertOrder(_version, msg.sender, _idHash, Status.CREATED, inputFee, _applicationDigest, receiverAddress);
        if (loanFee != address(0)) {
            Order memory order = orders[newOrderId];
            loanFee.apply(order.id, order.borrower, order.fee);
        }

    }

    function cancel(uint256 id) external {
        Order storage order = innerGetOrder(id);
        require((operators[msg.sender] || msg.sender == owner) || (msg.sender == order.borrower));

        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.CREATED;

        checkAndChangeStatus(order, exptectedStatusArray, Status.CANCELLED);
        if (loanFee != address(0)) {
            loanFee.cancel(order.id);
        }
    }

    function audit(uint256 id) onlyOperator external {
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.CREATED;
        getOrderAndChangeStatus(id, exptectedStatusArray, Status.AUDITING);
        if (loanFee != address(0)) {
            loanFee.audit(id);
        }

    }

    function reject(uint256 id) onlyOperator external {
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.AUDITING;
        getOrderAndChangeStatus(id, exptectedStatusArray, Status.REJECTED);
        if (loanFee != address(0)) {
            loanFee.reject(id);
        }
    }


    function approve(uint256 id) onlyOperator external {
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.AUDITING;
        getOrderAndChangeStatus(id, exptectedStatusArray, Status.APPROVED);
        if (loanFee != address(0)) {
            loanFee.approve(id);
        }
    }

    function deliver(uint256 id, bytes repayDigest, bytes agreementDigest) onlyOperator external {
        require(agreementContract != address(0));
        require(agreementDigest.length >= 0 && agreementDigest.length < MAX_SIZE);
        require(repayDigest.length >= 0 && repayDigest.length < MAX_SIZE);

        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.APPROVED;
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

    function deliverFailure(uint256 id) onlyOperator external {
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.APPROVED;
        getOrderAndChangeStatus(id, exptectedStatusArray, Status.FAILURE);

    }

    function receive(uint256 id) external {
        Order storage order = innerGetOrder(id);
        onlyBorrower(order);
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.DELIVERIED;
        checkAndChangeStatus(order, exptectedStatusArray, Status.RECEIVIED);
    }

    function onlyBorrower(Order storage order) internal onlyGateOpen {
        require(order.borrower == msg.sender);
    }

    function confirmRepay(uint256 id) onlyOperator external {
        require(agreementContract != address(0));
        Status[] memory exptectedStatusArray = new  Status[](2);
        exptectedStatusArray[0] = Status.RECEIVIED;
        exptectedStatusArray[1] = Status.DELIVERIED;

        getOrderAndChangeStatus(id, exptectedStatusArray, Status.REPAID);
        agreementContract.finishAgreement(id);
    }

    function updateRepayDigest(uint256 id, bytes repayDigest) onlyOperator external {
        require(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE);

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
        require(orderId > 0);
        order = orders[orderId];
        require(order.id > 0);
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
        require(checkStatus(order.status, fromStatusArray));
        justChangeStatus(order, nextStatus);
    }

    function justChangeStatus(Order storage order, Status nextStatus) internal {
        //状态机跃迁
        order.status = nextStatus;
        orderUpdated(order.id, order.borrower, nextStatus);
    }

    function getOrder(uint256 _orderId) view external returns (
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
        require(_orderId > 0 && _orderId < orders.length);
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

    function getOrderLength() view public returns (uint256 length) {
        return orders.length;
    }

    function getOrderArrayLengthByBorrowerIndex(address _borrower) public view returns (uint256){
        require(_borrower != address(0));
        return borrowerIndex[_borrower].length;
    }

    function getOrderArrayLengthByIdHashIndex(bytes32 _idHash) public view returns (uint256){
        require(uint256(_idHash) != 0);
        return idHashIndex[_idHash].length;
    }

    function queryOrderIdListByIdHashIndex(bytes32 _idHash, uint256 from, uint256 to) public view returns (uint256[]){
        require(to < getOrderArrayLengthByIdHashIndex(_idHash));
        require(from <= to);

        uint256 length = to.sub(from).add(1);
        uint256[] memory orderIdList = new uint256[](length);
        for (uint256 i = from; i <= to; i++) {
            orderIdList[i.sub(from)] = idHashIndex[_idHash][i];
        }
        return orderIdList;
    }

    function queryOrderIdListByBorrowerIndex(address borrower, uint256 from, uint256 to) public view returns (uint256[]){
        require(to < getOrderArrayLengthByBorrowerIndex(borrower));
        require(from <= to);

        uint256 length = to.sub(from).add(1);
        uint256[] memory orderIdList = new uint256[](length);
        for (uint256 i = from; i <= to; i++) {
            orderIdList[i.sub(from)] = borrowerIndex[borrower][i];
        }
        return orderIdList;
    }

    function setAggreementContract(address agreementContractAddress) public onlyOwner {
        require(agreementContractAddress != address(0));
        agreementContract = AgreementInfo(agreementContractAddress);
    }

    function setLoanFeeAddress(address loanFeeAddress) public onlyOwner {
        loanFee = LoanFee(loanFeeAddress);
    }

    function getMinFee() public view returns (uint256 fee){
        if (loanFee != address(0)) {
            fee = loanFee.getMinFee();
        }
    }
}