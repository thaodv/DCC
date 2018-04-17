pragma solidity ^0.4.2;

import "../ownership/OperatorPermission.sol";
import "../cert/CertServiceFeeModule.sol";
import "../utils/ByteUtils.sol";
import './AgreementInfo.sol';

contract LoanService is OperatorPermission {

    using ByteUtils for bytes;

    struct Order {
        uint256 version;
        address borrower;
        bytes idHash;
        Status status;
        uint256 fee;
        bytes applicationDigest;
        bytes agreementDigest;
        bytes repayDigest;
    }

    enum Status {INVALID, CREATED, CANCELLED, AUDITING, REJECTED, APPROVED, FAILURE, DELIVERIED, RECEIVIED, REPAID}

    Order[] public orders;

    mapping(address => uint256[]) public borrowerIndex;

    mapping(bytes32 => uint256[]) public idHashIndex;

    CertServiceFeeModule public certServiceFeeModule;

    AgreementInfo public agreementContract;

    uint256 public constant MAX_SIZE = 300;

    event orderUpdated(uint256 indexed id, address indexed borrower, Status status);

    function LoanService(address agreementContractAddress) public {
        setAggreementContract(agreementContractAddress);
        orders.push(Order(0, address(0), Status.INVALID, 0, "", "", ""));
    }

    function setAggreementContract(address agreementContractAddress) public onlyOwner {
        require(agreementContractAddress != address(0));
        agreementContract = AgreementInfo(agreementContractAddress);
    }

    function setCertServiceFeeModuleAddress(address certServiceFeeModuleAddress) public onlyOwner {
        certServiceFeeModule = CertServiceFeeModule(certServiceFeeModuleAddress);
    }


    function insertOrder(uint256 _version, address _borrower, bytes _idHash, Status _status, uint256 _fee, bytes _applicationDigest)
    internal returns (uint256 newOrderId){

        newOrderId = orders.push(Order(_version, _borrower, _idHash, _status, _fee, _applicationDigest, "", "")) - 1;

        orders[newOrderId].id = newOrderId;

        borrowerIndex[_borrower].push(newOrderId);
        idHashIndex[_idHash.bytesToBytes32(0)].push(newOrderId);

        orderUpdated(newOrderId, _borrower, _status);

    }

    function apply(uint256 _version, bytes _idHash, bytes _applicationDigest) external returns (uint256 newOrderId){
        require(_version > 0);
        require(_idHash.length > 0 && _idHash.length <= MAX_SIZE);
        require(_applicationDigest.length > 0 && _applicationDigest.length <= MAX_SIZE);

        uint256[] memory userOrders = borrowerIndex[msg.sender];
        if (userOrders.length > 0) {
            uint256 lastOrderId = userOrders[userOrders.length - 1];
            require(!checkStatus(lastOrderId, Status.CREATED));
        }

        uint256 fee = 0;
        if (certServiceFeeModule != address(0)) {
            fee = certServiceFeeModule.apply();
        }

        return insertOrder(_version, msg.sender, _idHash, Status.CREATED, fee, _applicationDigest);
    }

    function cancel(uint256 id) external {
        //TODO onlyBorrower
        getOrderAndChangeStatus(id, Status.CREATED, Status.CANCELLED, false);
    }

    function audit(uint256 id) onlyOperator external {
        getOrderAndChangeStatus(id, Status.CREATED, Status.AUDITING, false);
    }

    function reject(uint256 id) onlyOperator external {
        getOrderAndChangeStatus(id, Status.AUDITING, Status.REJECTED, false);
    }


    function approve(uint256 id) onlyOperator external {
        getOrderAndChangeStatus(id, Status.AUDITING, Status.APPROVED, false);
    }

    function deliver(uint256 id, bytes repayDigest, bytes agreementDigest) onlyOperator external {
        require(agreementContract != address(0));
        require(id > 0 && id < orders.length);
        require(agreementDigest.length >= 0 && agreementDigest.length < MAX_SIZE);
        require(repayDigest.length >= 0 && repayDigest.length < MAX_SIZE);

        Order storage order = getOrderAndChangeStatus(id, Status.APPROVED, Status.DELIVERIED, false);
        order.agreementDigest = agreementDigest;
        order.repayDigest = repayDigest;

        agreementContract.createAgreement(order.version, order.id, order.repayDigest, order.agreementDigest, order.idHash, order.borrower, order.applicationDigest);
    }

    function deliverFailure(uint256 id) onlyOperator external {
        getOrderAndChangeStatus(id, Status.APPROVED, Status.FAILURE, false);
    }

    function receive(uint256 id) external {
        //TODO onlyBorrower
        getOrderAndChangeStatus(id, Status.DELIVERIED, Status.RECEIVIED, false);
    }

    function confirmRepay(uint256 id) onlyOperator external {
        require(agreementContract != address(0));
        Status[] expectedStatusArray;
        expectedStatusArray.push(Status.RECEIVIED);
        expectedStatusArray.push(Status.DELIVERIED);
        getOrderAndChangeStatus(id, expectedStatusArray, Status.REPAID, false);
        agreementContract.finishAgreement(id);
    }

    function updateRepayDigest(uint256 id, bytes repayDigest) onlyOperator external {
        require(id > 0 && id < orders.length);
        require(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE);

        Order storage order = orders[id];
        require(order.status == Status.DELIVERIED || order.status == Status.RECEIVIED);

        order.repayDigest = repayDigest;

        agreementContract.updateRepayDigest(id, repayDigest);
    }

    function checkStatus(uint256 ordersIndex, Status[] expectedStatusArray) internal returns (bool){
        Status status = orders[ordersIndex].status;
        return checkStatus(status, expectedStatusArray);
    }

    function checkStatus(Status inputStatus, Status[] expectedStatusArray) internal returns (bool){
        for (int i = 0; i < expectedStatusArray.length; i++) {
            if (inputStatus == expectedStatusArray[i]) {
                return true;
            }
            return false;
        }
    }

    function getOrderAndChangeStatus(uint256 id, Status[] fromStatusArray, Status nextStatus, bool force)
    returns (Order storage returnOrder){
        require(id > 0 && id < orders.length);
        Order storage order = orders[id];

        require(force == true || checkStatus(order.status, fromStatusArray));

        justChangeStatus(id, order, nextStatus);
        return order;
    }


    function justChangeStatus(uint256 orderId, Order storage order, Status nextStatus) internal {
        //状态机跃迁
        order.status = nextStatus;
        orderUpdated(orderId, order.borrower, nextStatus);
    }
    
    //TODO 修改顺序
    function getOrder(uint256 _orderId) view external returns (uint256 version, uint256 id, Status status, uint256 fee,
        bytes repayDigest, bytes agreementDigest, bytes idHash, address borrower, bytes applicationDigest) {
        require(_orderId < orders.length);
        Order memory order = orders[_orderId];
        return (order.version, order.id, order.status, order.fee, order.repayDigest, order.agreementDigest, order
        .idHash, order.borrower, order.applicationDigest);
    }

    function getOrderLength() view public returns (uint256 length) {
        return orders.length;
    }

    function queryOrderIdArrayByBorrowerIndex(address _borrower) public view returns (uint256[]){
        require(_borrower != address(0));
        return borrowerIndex[_borrower];
    }

    function getOrderArrayLengthByBorrowerIndex(address _borrower) public view returns (uint256){
        require(_borrower != address(0));
        return borrowerIndex[_borrower].length;
    }

    function queryOrderIdArrayByIdHashIndex(bytes _idHash) public view returns (uint256[]){
        require(_idHash.length > 0 && _idHash.length <= MAX_SIZE);
        return idHashIndex[_idHash.bytesToBytes32(0)];
    }

    function getOrderArrayLengthByIdHashIndex(bytes _idHash) public view returns (uint256){
        require(_idHash.length > 0 && _idHash.length <= MAX_SIZE);
        return idHashIndex[_idHash.bytesToBytes32(0)].length;
    }
}