pragma solidity ^0.4.2;

import "../ownership/OperatorPermission.sol";
import "../utils/ByteUtils.sol";
import "./AgreementInfo.sol";
import "../math/SafeMath.sol";

//借贷协议订单储存合约
contract AgreementService is OperatorPermission, AgreementInfo {

    using SafeMath for uint256;

    struct Agreement {
        uint256 id;
        uint256 version;
        address caller;
        uint256 orderId;
        address borrower;
        bytes32 idHash;
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
        agreements.push(Agreement(0, 0, address(0), 0, address(0), "", Status.INVALID, "", "", ""));
    }

    //创建借贷协议
    function createAgreement(
        uint256 version,
        uint256 orderId,
        address _borrower,
        bytes32 idHash,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest)
    onlyOperator external returns (uint256 agreementId) {
        require(version > 0);
        require(orderId > 0);
        require(_borrower != address(0));
        require(uint256(idHash) != 0);
        require(applicationDigest.length > 0 && applicationDigest.length <= MAX_SIZE);
        require(agreementDigest.length > 0 && agreementDigest.length <= MAX_SIZE);
        require(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE);
        //不允许重复插入相同调用者+订单号
        require(orderIdIndex[msg.sender][orderId] == 0);

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
        require(orderId > 0);
        uint256 agreementId = orderIdIndex[msg.sender][orderId];
        require(agreementId > 0);

        Status[] memory exptectedStatusArray = new  Status[](1);
        exptectedStatusArray[0] = Status.CREATED;
        getAgreementAndChangeStatus(agreementId, exptectedStatusArray, Status.FULFILLED, false);
    }

    function getAgreementAndChangeStatus(uint256 agreementId, Status[] fromStatusArray, Status nextStatus, bool force) internal
    {
        require(agreementId < agreements.length);
        Agreement storage agreement = agreements[agreementId];
        require(force == true || checkStatus(agreement.status, fromStatusArray));
        justChangeStatus(agreement, nextStatus);

    }

    function updateRepayDigest(uint256 orderId, bytes repayDigest) onlyOperator external {
        require(orderId > 0);
        require(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE);

        uint256 agreementId = orderIdIndex[msg.sender][orderId];
        require(agreementId > 0);
        Agreement storage agreement = agreements[agreementId];
        // 可能清偿后，再变更还款摘要
        //        Status[] memory exptectedStatusArray = new  Status[](1);
        //        exptectedStatusArray[0] = Status.CREATED;
        //        require(checkStatus(agreement.status, exptectedStatusArray));
        agreement.repayDigest = repayDigest;
    }


    function getAgreementLength() public view returns (uint256 length) {
        return agreements.length;
    }

    function getAgreement(uint256 _agreementId) public view returns (
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
        require(_agreementId > 0 && _agreementId < agreements.length);
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

    function getAgreementByOrderId(address _caller, uint256 _orderId) public view returns (
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
        require(_caller != address(0));
        require(_orderId > 0);
        uint256 _agreementId = orderIdIndex[_caller][_orderId];
        require(_agreementId > 0);
        return getAgreement(_agreementId);
    }


    function getAgreementArrayLengthByBorrowerIndex(address _borrower) public view returns (uint256){
        require(_borrower != address(0));
        return borrowIndex[_borrower].length;
    }


    function getAgreementArrayLengthByIdHashIndex(bytes32 _idHash) public view returns (uint256){
        require(uint256(_idHash) != 0);
        return idHashIndex[_idHash].length;
    }

    function queryOrderIdListByIdHashIndex(bytes32 _idHash, uint256 from, uint256 to) public view returns (uint256[]){
        require(to < getAgreementArrayLengthByIdHashIndex(_idHash));
        require(from <= to);

        uint256 length = to.sub(from).add(1);
        uint256[] memory orderIdList = new uint256[](length);
        for (uint256 i = from; i <= to; i++) {
            orderIdList[i.sub(from)] = idHashIndex[_idHash][i];
        }
        return orderIdList;
    }

    function queryOrderIdListByBorrowerIndex(address borrower, uint256 from, uint256 to) public view returns (uint256[]){
        require(to < getAgreementArrayLengthByBorrowerIndex(borrower));
        require(from <= to);

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