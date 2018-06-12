pragma solidity ^0.4.2;

import "../ownership/OperatorPermission.sol";
import "../ownership/GateControl.sol";
import './AgreementInfo2.sol';
import "./LoanFee.sol";
import "../math/SafeMath.sol";
import "../utils/FastFailure.sol";

contract MortgageLoanService is OperatorPermission, GateControl, FastFailure {

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
        string content;
    }

    enum Status {INVALID, DELIVERIED, REPAID, VIOLATED}

    Order[] public orders;

    mapping(address => uint256[]) public borrowerIndex;

    mapping(bytes32 => uint256[]) public idHashIndex;

    LoanFee public loanFee;

    AgreementInfo2 public agreement2Contract;

    uint256 public constant MAX_SIZE = 300;

    uint256 public constant CONTENT_LENGTH = 100 * 1024;

    event orderUpdated(uint256 indexed id, address indexed borrower, Status status);

    function MortgageLoanService(address agreement2ContractAddress) public {
        setAgreement2Contract(agreement2ContractAddress);
        orders.push(Order(0, 0, address(0), "", Status.INVALID, 0, "", "", "", ""));
    }

    function setAgreement2Contract(address agreement2ContractAddress) public onlyOwner {
        require(agreement2ContractAddress != address(0));
        agreement2Contract = AgreementInfo2(agreement2ContractAddress);
    }

    function setLoanFeeAddress(address loanFeeAddress) public onlyOwner {
        loanFee = LoanFee(loanFeeAddress);
    }

    function insertOrder(uint256 _version, address _borrower, bytes32 _idHash, Status _status, uint256 _fee, bytes _applicationDigest, bytes _agreementDigest, bytes _repayDigest, string _content)
    internal returns (uint256 newOrderId){

        newOrderId = orders.push(Order(0, _version, _borrower, _idHash, _status, _fee, _applicationDigest, _agreementDigest, _repayDigest, _content)) - 1;
        orders[newOrderId].id = newOrderId;
        borrowerIndex[_borrower].push(newOrderId);
        idHashIndex[_idHash].push(newOrderId);

        orderUpdated(newOrderId, _borrower, _status);

    }

    function deliver(uint256 version, bytes32 _idHash, bytes _applicationDigest, bytes _agreementDigest, bytes _repayDigest, string _content, uint256 inputFee) onlyOperator onlyGateOpen external returns
    (uint256 newOrderId){
        //由于调用超级转账接口，所以最终服务合约有义务校验使用者直接调用，防止钓鱼
        require(msg.sender == tx.origin);
        require(version > 0);
        require(uint256(_idHash) != 0);
        require(_applicationDigest.length > 0 && _applicationDigest.length <= MAX_SIZE);
        require(_agreementDigest.length > 0 && _agreementDigest.length <= MAX_SIZE);
        require(_repayDigest.length > 0 && _repayDigest.length <= MAX_SIZE);
        require(bytes(_content).length > 0 && bytes(_content).length <= CONTENT_LENGTH);

        //输入的费用就是订单的费用，如果费用不合法，事务会回滚
        newOrderId = insertOrder(version, msg.sender, "", Status.DELIVERIED, inputFee, "", "", "", "");
        orders[newOrderId].idHash = _idHash;
        orders[newOrderId].applicationDigest = _applicationDigest;
        orders[newOrderId].agreementDigest = _agreementDigest;
        orders[newOrderId].repayDigest = _repayDigest;
        orders[newOrderId].content = _content;

        Order memory order = orders[newOrderId];
        if (loanFee != address(0)) {
            loanFee.apply(order.id, order.borrower, order.fee);
        }

        agreement2Contract.createAgreement(
            order.version,
            order.id,
            order.borrower,
            order.idHash,
            order.applicationDigest,
            order.agreementDigest,
            order.repayDigest,
            enumToString(order.status)
        );

    }

    function confirmRepay(uint256 orderId) onlyOperator external {
        updateLoanAndAgreement2Status(orderId, Status.REPAID,Status.DELIVERIED);
    }

    function violated(uint256 orderId) onlyOperator external {
        updateLoanAndAgreement2Status(orderId, Status.VIOLATED,Status.DELIVERIED);
    }

    function updateLoanAndAgreement2Status(uint256 orderId, Status nextStatus, Status expectedStatus) internal {
        require(agreement2Contract != address(0));

        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] =expectedStatus;

        getOrderAndChangeStatus(orderId, exptectedStatusArray, nextStatus);
        agreement2Contract.updateStatus(orderId, enumToString(nextStatus),enumToString(expectedStatus));
    }

    function updateRepayDigest(uint256 orderId, bytes repayDigest) onlyOperator external {
        require(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE);

        Order storage order = innerGetOrder(orderId);
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.DELIVERIED;
        checkStatus(order.status, exptectedStatusArray);

        order.repayDigest = repayDigest;
        agreement2Contract.updateRepayDigest(orderId, repayDigest);
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

    function checkAndChangeStatus(Order storage order, Status[] fromStatusArray, Status nextStatus)
    internal {
        require(checkStatus(order.status, fromStatusArray));
        justChangeStatus(order, nextStatus);
    }

    function checkStatus(Status inputStatus, Status[] expectedStatusArray) internal returns (bool){
        for (uint256 i = 0; i < expectedStatusArray.length; i++) {
            if (inputStatus == expectedStatusArray[i]) {
                return true;
            }
        }
        return false;
    }

    function justChangeStatus(Order storage order, Status nextStatus) internal {
        //状态机跃迁
        order.status = nextStatus;
        orderUpdated(order.id, order.borrower, nextStatus);
    }
    //将enum类型转换为对应的字符串类型
    function enumToString(Status status) pure internal returns(string){
        if(status==Status.INVALID){
            return "INVALID";
        }else if(status==Status.DELIVERIED){
            return "DELIVERIED";
        }else if(status==Status.REPAID){
            return "REPAID";
        }else{
            return "VIOLATED";
        }
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
        string content) {
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
        order.content);
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

    function getMinFee() public view returns (uint256 fee){
        if (loanFee != address(0)) {
            fee = loanFee.getMinFee();
        }
    }
}