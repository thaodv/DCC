pragma solidity ^0.4.2;

import "./AgreementInfo.sol";
import "./AgreementInfo2.sol";
import "../ownership/OperatorPermission.sol";
import "../utils/ByteUtils.sol";
import "../math/SafeMath.sol";

contract AgreementService2 is OperatorPermission, AgreementInfo, AgreementInfo2 {

    using SafeMath for uint256;
    struct Agreement {
        uint256 id;
        uint256 version;
        address caller;
        uint256 orderId;
        address borrower;
        bytes32 idHash;
        string status;
        bytes applicationDigest;
        bytes agreementDigest;
        bytes repayDigest;
    }

    event agreementUpdated(uint256 indexed id, address indexed caller, uint256 indexed orderId, address borrower, string status
    );

    event repayDigestUpdated(uint256 indexed id, address indexed caller, uint256 indexed orderId, address borrower, bytes repayDigest
    );

    Agreement[] public agreements;

    mapping(address => uint256[]) public borrowIndex;

    mapping(bytes32 => uint256[]) public idHashIndex;

    mapping(address => mapping(uint256 => uint256)) public orderIdIndex;

    uint256 public constant MAX_SIZE = 300;

    function AgreementService2() public {
        agreements.push(Agreement(0, 0, address(0), 0, address(0), "", "", "", "", ""));
    }

    function createAgreement(
        uint256 version,
        uint256 orderId,
        address _borrower,
        bytes32 idHash,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest)
    onlyOperator external returns (uint256 agreementId) {
        innerCreateAgreement(version,msg.sender, orderId, _borrower, idHash, applicationDigest, agreementDigest, repayDigest, "CREATED");
    }

    function createAgreement(
        uint256 version,
        address caller,
        uint256 orderId,
        address _borrower,
        bytes32 idHash,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest,
        string status)
    onlyOwner external returns (uint256 agreementId) {
        innerCreateAgreement(version,caller, orderId, _borrower, idHash, applicationDigest, agreementDigest, repayDigest, status);
    }

    function createAgreement(
        uint256 version,
        uint256 orderId,
        address _borrower,
        bytes32 idHash,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest,
        string status)
    onlyOperator external returns (uint256 agreementId){
        innerCreateAgreement(version,msg.sender, orderId, _borrower, idHash, applicationDigest, agreementDigest, repayDigest, status);
    }

    function innerCreateAgreement(
        uint256 version,
        address caller,
        uint256 orderId,
        address _borrower,
        bytes32 idHash,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest,
        string status)
    internal returns (uint256 agreementId){
        require(version > 0);
        require(orderId > 0);
        require(_borrower != address(0));
        require(caller != address(0));
        require(uint256(idHash) != 0);
        require(applicationDigest.length > 0 && applicationDigest.length <= MAX_SIZE);
        require(agreementDigest.length > 0 && agreementDigest.length <= MAX_SIZE);
        require(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE);
        require(bytes(status).length > 0 && bytes(status).length <= MAX_SIZE);
        //不允许重复插入相同调用者+订单号
        require(orderIdIndex[caller][orderId] == 0);

        agreementId = agreements.push(Agreement(0, 0, caller, 0, _borrower, idHash, status,
            applicationDigest, agreementDigest, repayDigest)) - 1;
        agreements[agreementId].id = agreementId;
        //version和orderId额外赋值是因為编译不通过,报错:Stack too deep
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

    function finishAgreement(uint256 orderId) onlyOperator external {
        innerUpdateStatus(orderId, "FULFILLED",msg.sender);
    }

    function updateStatus(uint256 orderId, string status) onlyOperator external {
        innerUpdateStatus(orderId, status,msg.sender);
    }

    function updateStatus(uint256 orderId, string status,address caller) onlyOwner external {
        innerUpdateStatus(orderId, status,caller);
    }

    function innerUpdateStatus(uint256 orderId, string status,address caller) internal {
        require(orderId > 0);
        require(caller !=address(0));
        uint256 agreementId = orderIdIndex[caller][orderId];
        require(agreementId > 0 && agreementId < agreements.length);

        Agreement storage agreement = agreements[agreementId];
        agreement.status = status;
        agreementUpdated(agreement.id, agreement.caller, agreement.orderId, agreement.borrower, agreement.status);
    }

    function updateRepayDigest(uint256 orderId, bytes repayDigest) onlyOperator external {
        innerUpdateRepayDigest(orderId,repayDigest,msg.sender);
    }

    function updateRepayDigest(uint256 orderId, bytes repayDigest,address caller) onlyOwner external {
        innerUpdateRepayDigest(orderId,repayDigest,caller);
    }

    function innerUpdateRepayDigest(uint256 orderId, bytes repayDigest,address caller) internal{
        require(orderId > 0);
        require(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE);
        require(caller!=address(0));

        uint256 agreementId = orderIdIndex[caller][orderId];
        require(agreementId > 0);
        Agreement storage agreement = agreements[agreementId];
        agreement.repayDigest = repayDigest;
        repayDigestUpdated(agreement.id,agreement.caller,agreement.orderId,agreement.borrower,agreement.repayDigest);
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
        string status,
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
        string status,
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

}