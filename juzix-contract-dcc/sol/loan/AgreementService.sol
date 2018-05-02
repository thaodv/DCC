pragma solidity ^0.4.2;

import "../permission/OperatorPermission.sol";
import "../utils/ByteUtils.sol";
import "./AgreementInfo.sol";
import "../math/SafeMath.sol";

contract AgreementService is OperatorPermission, AgreementInfo {
    using SafeMath for uint256;
    struct Agreement {
        uint256 id;
        uint256 version;
        address caller;
        uint256 orderId;
        address borrower;
        bytes32 idHash;
        Status status;
        bytes applicationDigest;
        bytes agreementDigest;
        bytes repayDigest;
    }

    enum Status {INVALID, CREATED, FULFILLED}

    event agreementUpdated(uint256 indexed id, address indexed caller, uint256 indexed orderId, address borrower, Status status
    );

    Agreement[] public agreements;

    mapping(address => uint256[]) public borrowIndex;

    mapping(bytes32 => uint256[]) public idHashIndex;

    mapping(address => mapping(uint256 => uint256)) public orderIdIndex;

    uint256 public constant MAX_SIZE = 300;

    function AgreementService() public {
        register("AgreementServiceModule", "0.0.1.0", "AgreementService", "0.0.1.0");
        agreements.push(Agreement(0, 0, address(0), 0, address(0), "", Status.INVALID, "", "", ""));
    }

    function createAgreement(
        uint256 version,
        uint256 orderId,
        address _borrower,
        bytes32 idHash,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest)
    external returns (uint256 agreementId) {
        onlyOperator();
        if (!(version > 0)) {
            log("!(version > 0)");
            throw;
        }
        if (!(orderId > 0)) {
            log("!(orderId > 0)");
            throw;
        }
        if (!(_borrower != address(0))) {
            log("!(_borrower != address(0))");
            throw;
        }
        if (!(uint256(idHash) != 0)) {
            log("!(uint256(idHash)!=0)");
            throw;
        }

        if (!(applicationDigest.length > 0 && applicationDigest.length <= MAX_SIZE)) {
            log("!(applicationDigest.length > 0 && applicationDigest.length <= MAX_SIZE)");
            throw;
        }
        if (!(agreementDigest.length > 0 && agreementDigest.length <= MAX_SIZE)) {
            log("!(agreementDigest.length > 0 && agreementDigest.length <= MAX_SIZE)");
            throw;
        }

        if (!(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE)) {
            log("!(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE)");
            throw;
        }
        //不允许重复插入相同调用者+订单号
        if (!(orderIdIndex[msg.sender][orderId] == 0)) {
            log("!(orderIdIndex[msg.sender][orderId] == 0)");
            throw;
        }
        agreementId = agreements.push(Agreement(0, 0, msg.sender, 0, _borrower, idHash, Status.CREATED,
            applicationDigest, agreementDigest, repayDigest)) - 1;
        agreements[agreementId].id = agreementId;
        agreements[agreementId].version = version;
        agreements[agreementId].orderId = orderId;
        createAgreementHelp(agreements[agreementId]);
    }

    function createAgreementHelp(Agreement agreement) internal {
        agreementUpdated(agreement.id, agreement.caller, agreement.orderId, agreement.borrower, agreement.status);
        borrowIndex[agreement.borrower].push(agreement.id);
        idHashIndex[agreement.idHash].push(agreement.id);
        orderIdIndex[agreement.caller][agreement.orderId] = agreement.id;
    }

    function finishAgreement(uint256 orderId) external {
        if (!(orderId > 0)) {
            log("!(orderId > 0)");
            throw;
        }
        uint256 agreementId = orderIdIndex[msg.sender][orderId];

        if (!(agreementId > 0)) {
            log("!(agreementId > 0)");
            throw;
        }
        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.CREATED;
        getAgreementAndChangeStatus(agreementId, exptectedStatusArray, Status.FULFILLED, false);
    }

    function getAgreementAndChangeStatus(uint256 agreementId, Status[] fromStatusArray, Status nextStatus, bool force) internal
    {
        if (!(agreementId < agreements.length)) {
            log("!(agreementId < agreements.length)");
            throw;
        }
        Agreement storage agreement = agreements[agreementId];
        if (!(force == true || checkStatus(agreement.status, fromStatusArray))) {
            log("!(force == true || checkStatus(agreement.status, fromStatusArray))");
            throw;
        }

        justChangeStatus(agreement, nextStatus);

    }

    function updateRepayDigest(uint256 orderId, bytes repayDigest) external {
        onlyOperator();
        if (!(orderId > 0)) {
            log("!(orderId > 0)");
            throw;
        }
        if (!(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE)) {
            log("!(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE)");
            throw;
        }

        uint256 agreementId = orderIdIndex[msg.sender][orderId];
        if (!(agreementId > 0)) {
            log("!(agreementId > 0)");
            throw;
        }
        Agreement storage agreement = agreements[agreementId];
        // 可能清偿后，再变更还款摘要
        //        Status[] memory exptectedStatusArray = new  Status[](1);
        //        exptectedStatusArray[0] = Status.CREATED;
        //        require(checkStatus(agreement.status, exptectedStatusArray));
        agreement.repayDigest = repayDigest;
    }


    function getAgreementLength() public constant returns (uint256 length) {
        return agreements.length;
    }

    function getAgreement(uint256 _agreementId) public constant returns (
        uint256 id,
        uint256 version,
        address caller,
        uint256 orderId,
        address borrower,
        bytes32 idHash,
        Status status,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest){
        if (!(_agreementId > 0 && _agreementId < agreements.length)) {
            log("!(agreementId > 0)");
            throw;
        }
        Agreement memory agreement = agreements[_agreementId];

        return (
        agreement.id,
        agreement.version,
        agreement.caller,
        agreement.orderId,
        agreement.borrower,
        agreement.idHash,
        agreement.status,
        agreement.applicationDigest,
        agreement.agreementDigest,
        agreement.repayDigest);
    }

    function getAgreementByOrderId(address _caller, uint256 _orderId) external constant returns (
        uint256 id,
        uint256 version,
        address caller,
        uint256 orderId,
        address borrower,
        bytes32 idHash,
        Status status,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest){
        if (!(_caller != address(0))) {
            log("!(_caller != address(0))");
            throw;
        }
        if (!(_orderId > 0)) {
            log("!(_orderId > 0)");
            throw;
        }
        uint256 _agreementId = orderIdIndex[_caller][_orderId];
        if (!(_agreementId > 0)) {
            log("!(_agreementId > 0)");
            throw;
        }
        return getAgreement(_agreementId);
    }

    function getAgreementArrayLengthByBorrowerIndex(address _borrower) public constant returns (uint256){
        if (!(_borrower != address(0))) {
            log("!(_borrower != address(0))");
            throw;
        }
        return borrowIndex[_borrower].length;
    }


    function getAgreementArrayLengthByIdHashIndex(bytes32 idHash) public constant returns (uint256){
        if (!(uint256(idHash) != 0)) {
            log("!(uint256(idHash)!=0)");
            throw;
        }
        return idHashIndex[idHash].length;
    }

    function queryOrderIdListByIdHashIndex(bytes32 _idHash, uint256 from, uint256 to) public constant returns (uint256[]){
        if (!(to < getAgreementArrayLengthByIdHashIndex(_idHash))) {
            log("!(to < getAgreementArrayLengthByIdHashIndex(_idHash))");
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
        if (!(to < getAgreementArrayLengthByBorrowerIndex(borrower))) {
            log("!(to < getAgreementArrayLengthByBorrowerIndex(borrower))");
            throw;
        }
        if (!(from <= to)) {
            log("!(from <= to)");
            throw;
        }

        uint256 length = to.sub(from).add(1);
        uint256[] memory orderIdList = new uint256[](length);
        for (uint256 i = from; i <= to; i++) {
            orderIdList[i.sub(from)] = borrowIndex[borrower][i];
        }
        return orderIdList;
    }

    function checkStatus(Status inputStatus, Status[] expectedStatusArray) internal returns (bool){
        for (uint256 i = 0; i < expectedStatusArray.length; i++) {
            if (inputStatus == expectedStatusArray[i]) {
                return true;
            }
        }
        return false;
    }

    function justChangeStatus(Agreement storage agreement, Status nextStatus) internal {
        //状态机跃迁
        agreement.status = nextStatus;
        agreementUpdated(agreement.id, agreement.caller, agreement.orderId, agreement.borrower, agreement.status);
    }


}