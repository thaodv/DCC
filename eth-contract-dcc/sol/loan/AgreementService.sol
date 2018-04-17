pragma solidity ^0.4.2;
import "../ownership/OperatorPermission.sol";
import "../utils/ByteUtils.sol";

contract AgreementService is OperatorPermission{

   using ByteUtils for bytes;

   struct Agreement{
        uint256 id;
        address caller;
        uint256 version;
        uint256 orderId;
        Status  status;
        bytes repayDigest;
        bytes agrementDigest;
        bytes idHash;
        address borrower;
        bytes applicationDigest;
   }

   enum Status {INVALID,CREATED, FULFILLED}

   event agreementUpdate(uint256 indexed id,address indexed caller,uint256 indexed version, uint256 orderId,Status  status,bytes repayDigest,bytes agrementDigest,
        bytes idHash,address borrower,bytes applicationDigest);

   Agreement[] public agreements;

   mapping(address => uint256[]) public applicantIndex;

   mapping(bytes32 => uint256[]) public idHashIndex;

   mapping(address => mapping(uint256 => uint256)) public orderIdIndex;

   uint256 public constant MAX_SIZE = 100;

   function AgreementService() public {
        agreements.push(Agreement(0,address(0),0,0,Status.INVALID,"","","",address(0),""));
   }

   function createAgreement(uint256 version,uint256 orderId,bytes repayDigest,bytes agrementDigest,bytes idHash,address _borrower,bytes applicationDigest)
   onlyOperator external returns(uint256 agreementId) {
        require(orderId > 0);
        require(version == 1);
        require(_borrower != address(0));
        require(repayDigest.length > 0 && repayDigest.length <= MAX_SIZE);
        require(agrementDigest.length > 0 && agrementDigest.length <= MAX_SIZE);
        require(idHash.length > 0 && idHash.length <= MAX_SIZE);
        require(applicationDigest.length > 0 && applicationDigest.length <= MAX_SIZE);


        agreementId=agreements.push(Agreement(0,msg.sender,0,orderId,Status.CREATED,repayDigest,agrementDigest,idHash,_borrower,applicationDigest));
        agreementId-=1;
        agreements[agreementId].version=version;
        agreements[agreementId].id=agreementId;

        createAgreementHelp(agreements[agreementId],agreementId);

   }

   function createAgreementHelp(Agreement agreement,uint256 agreementId) internal{
        agreementUpdate(agreement.id,agreement.caller,agreement.version, agreement.orderId,agreement.status,agreement.repayDigest,agreement.agrementDigest,
        agreement.idHash,agreement.borrower,agreement.applicationDigest);

        applicantIndex[agreement.borrower].push(agreementId);
        idHashIndex[(agreement.idHash).bytesToBytes32(0)].push(agreementId);
        orderIdIndex[msg.sender][agreement.orderId]=agreementId;
   }

    function finishAgreement(uint256 id)onlyOperator external{
        require(id>0);
        require(orderIdIndex[msg.sender][id]>0);

        uint256 agreementId = orderIdIndex[msg.sender][id];
        getAgreementAndChangeStatus(agreementId,Status.CREATED,Status.FULFILLED,false);
    }

     function updateRepayDigest(uint256 id,bytes repayDigest) onlyOperator external{
        require(id>0);
        require(repayDigest.length>0 && repayDigest.length<=MAX_SIZE);

        uint256  agreementId = orderIdIndex[msg.sender][id];
        agreements[agreementId].repayDigest=repayDigest;
     }

    function getAgreementAndChangeStatus(uint256 agreementId, Status fromStatus, Status nextStatus, bool force) internal
    returns (Agreement storage agreement){
        require(agreementId < agreements.length);
        agreement = agreements[agreementId];
        require(force == true || agreement.status == fromStatus);

        justChangeStatus(agreementId, agreement, nextStatus);
        return agreement;
    }

    function justChangeStatus(uint256 agreementId, Agreement storage agreement, Status nextStatus) internal {
        //状态机跃迁
        agreement.status = nextStatus;
        agreementUpdate(agreementId,agreement.caller,agreement.version, agreement.orderId,agreement.status,agreement.repayDigest,agreement.agrementDigest,
        agreement.idHash,agreement.borrower,agreement.applicationDigest);
    }

    function getAgreementLength() public view returns (uint256 length) {
        return agreements.length;
    }

    function getAgreement(uint256 _agreementId) public view returns (uint256 id,address caller,uint256 version, uint256 orderId,Status  status,bytes repayDigest,bytes agrementDigest,
        bytes idHash,address borrower,bytes applicationDigest){
        require(_agreementId > 0 && _agreementId < agreements.length);
        Agreement memory agreement = agreements[_agreementId];
        return (_agreementId, agreement.caller, agreement.version, agreement.orderId, agreement.status,
        agreement.repayDigest, agreement.agrementDigest,agreement.idHash,agreement.borrower,agreement.applicationDigest);
    }

   function getAgreementByOrderId(address _caller, uint256 _orderId) public view returns (uint256 id,address caller,uint256 version, uint256 orderId,Status  status,bytes repayDigest,bytes agrementDigest,
        bytes idHash,address borrower,bytes applicationDigest){
        require(_caller != address(0));
        require(_orderId > 0);
        uint256 _agreementId = orderIdIndex[_caller][_orderId];
        return getAgreement(_agreementId);
   }

    function queryOrderIdArrayByApplicantIndex(address _applicant) public view returns (uint256[]){
        require(_applicant != address(0));
        return applicantIndex[_applicant];
    }

    function getOrderArrayLengthByApplicantIndex(address _applicant) public view returns (uint256){
        require(_applicant != address(0));
        return applicantIndex[_applicant].length;
    }

    function queryOrderIdArrayByIdHashIndex(bytes _idHash) public view returns (uint256[]){
        require(_idHash.length>0 && _idHash.length<=MAX_SIZE);
        return idHashIndex[_idHash.bytesToBytes32(0)];
    }

    function getOrderArrayLengthByIdHashIndex(bytes _idHash) public view returns (uint256){
        require(_idHash.length>0 && _idHash.length<=MAX_SIZE);
        return idHashIndex[_idHash.bytesToBytes32(0)].length;
    }
}