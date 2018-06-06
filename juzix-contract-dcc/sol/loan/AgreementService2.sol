pragma solidity ^0.4.2;

import "./AgreementInfo.sol";
import "./AgreementInfo2.sol";
import "../permission/OperatorPermission.sol";
import "../utils/ByteUtils.sol";
import "../math/SafeMath.sol";

contract AgreementService2 is OperatorPermission, AgreementInfo, AgreementInfo2 {

    using SafeMath for uint256;
    using ByteUtils for bytes;

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
        register("AgreementService2Module", "0.0.1.0", "AgreementService2", "0.0.1.0");
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
    external returns (uint256 agreementId) {
        onlyOperator();
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
    external returns (uint256 agreementId) {
        onlyOwner();
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
    external returns (uint256 agreementId){
        onlyOperator();
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
        if (!(caller != address(0))) {
            log("!(caller!= address(0))");
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

        if(!(bytes(status).length > 0 && bytes(status).length <= MAX_SIZE)){
            log("!(bytes(status).length > 0 && bytes(status).length <= MAX_SIZE)");
            throw;
        }
        //不允许重复插入相同调用者+订单号
        if (!(orderIdIndex[msg.sender][orderId] == 0)) {
            log("!(orderIdIndex[msg.sender][orderId] == 0)");
            throw;
        }
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

    function finishAgreement(uint256 orderId)  external {
        onlyOperator();
        string[] memory expectedStatusArray=new string[](1);
        expectedStatusArray[0]="CREATED";
        innerUpdateStatus(orderId, "FULFILLED",msg.sender,expectedStatusArray);
    }

    function updateStatus(uint256 orderId, string status,string[] expectedStatusArray) external {
        onlyOperator();
        innerUpdateStatus(orderId, status,msg.sender,expectedStatusArray);
    }

    function updateStatus(uint256 orderId, string status,address caller,string[] expectedStatusArray) external {
        onlyOwner();
        innerUpdateStatus(orderId, status,caller,expectedStatusArray);
    }

    function innerUpdateStatus(uint256 orderId, string status,address caller,string[] expectedStatusArray) internal {
        if (!(orderId > 0)) {
            log("!(orderId > 0)");
            throw;
        }
        if (!(caller != address(0))) {
            log("!(caller!= address(0))");
            throw;
        }
        uint256 agreementId = orderIdIndex[caller][orderId];
        if(!(agreementId > 0 && agreementId < agreements.length)){
            log("!(agreementId > 0 && agreementId < agreements.length)");
            throw;
        }

        Agreement storage agreement = agreements[agreementId];
        //检查状态
        if(!(checkStatus(agreement.status,expectedStatusArray))){
            log("!(checkStatus(agreement.status,expectedStatusArray))");
            throw;
        }
        //修改状态
        agreement.status = status;
        agreementUpdated(agreement.id, agreement.caller, agreement.orderId, agreement.borrower, agreement.status);
    }

    function updateRepayDigest(uint256 orderId, bytes repayDigest) external {
        onlyOperator();
        innerUpdateRepayDigest(orderId,repayDigest,msg.sender);
    }

    function updateRepayDigest(uint256 orderId, bytes repayDigest,address caller) external {
        onlyOwner();
        innerUpdateRepayDigest(orderId,repayDigest,caller);
    }

    function innerUpdateRepayDigest(uint256 orderId, bytes repayDigest,address caller) internal{
        if (!(orderId > 0)) {
            log("!(orderId > 0)");
            throw;
        }
        if(!(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE)){
            log("!(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE)");
            throw;
        }
        if (!(caller != address(0))) {
            log("!(caller!= address(0))");
            throw;
        }

        uint256 agreementId = orderIdIndex[caller][orderId];
        if (!(agreementId > 0)) {
            log("!(agreementId > 0)");
            throw;
        }
        Agreement storage agreement = agreements[agreementId];
        agreement.repayDigest = repayDigest;
        repayDigestUpdated(agreement.id,agreement.caller,agreement.orderId,agreement.borrower,agreement.repayDigest);
    }

    function checkStatus(string inputStatus, string[] expectedStatusArray) internal returns (bool){
        if(expectedStatusArray.length==0){
            return true;
        }
        for (uint256 i = 0; i < expectedStatusArray.length; i++) {
            if (bytes(inputStatus).bytesEqual(bytes(expectedStatusArray[i]))) {
                return true;
            }
        }
        return false;
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
        string status,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest){
        if (!(_agreementId > 0 && _agreementId < agreements.length)) {
            log("!(_agreementId > 0 && _agreementId < agreements.length)");
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

    function getAgreementByOrderId(address _caller, uint256 _orderId) public constant returns (
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


    function getAgreementArrayLengthByIdHashIndex(bytes32 _idHash) public constant returns (uint256){
        if (!(uint256(_idHash) != 0)) {
            log("!(uint256(idHash)!=0)");
            throw;
        }
        return idHashIndex[_idHash].length;
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

    function queryOrderIdListByBorrowerIndex(address borrower, uint256 from, uint256 to) public constant returns (uint256[]){
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

}