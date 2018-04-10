pragma solidity ^0.4.11;

import "../ownership/OperatorPermission.sol";
import "./CertServiceFeeModule.sol";
import "./CertServiceIntf.sol";
contract CertService3 is OperatorPermission ,CertServiceIntf{

    mapping(address => Checkpoint[]) checkpoints;

    Order[] orders;

    enum Status {INVALID, APPLIED, PASSED, REJECTED, DISCARDED, REVOKED}

    enum DigestIntegrity {EMPTY,APPLICANT,VERIFIER}

    DigestIntegrity public digest1Integrity;
    DigestIntegrity public digest2Integrity;
    DigestIntegrity public expiredIntegrity;

    event orderUpdated(address indexed applicant, uint256 indexed orderId, Status status);

    struct Order {

        address applicant;

        Status status;

        Content content;

        uint256 feeDcc;
    }

    struct Checkpoint {

        // `fromBlock` is the block number that the value was generated from
        uint256 fromBlock;

        uint256 dataVersion;

        // `value`
        Content content;

    }

    struct Content {

        bytes digest1;

        bytes digest2;

        uint256 expired;

    }

    bytes public name;

    CertServiceFeeModule public certServiceFeeModule;

    function setDigest1Integrity(DigestIntegrity  _digest1Integrity) public  onlyOwner{
        digest1Integrity=_digest1Integrity;
     }

     function setDigest2Integrity(DigestIntegrity  _digest2Integrity) public  onlyOwner{
        digest2Integrity=_digest2Integrity;
     }

    function setExpiredIntegrity(DigestIntegrity  _expiredIntegrity) public  onlyOwner{
         expiredIntegrity= _expiredIntegrity;
      }

     function setCertServiceFeeModuleAddress(address certServiceFeeModuleAddress) public  onlyOwner{
         certServiceFeeModule=CertServiceFeeModule(certServiceFeeModuleAddress);
      }


    function CertService3(bytes _name,DigestIntegrity _digest1Integrity,DigestIntegrity _digest2Integrity,DigestIntegrity _expiredIntegrity) public {
        name=_name;
        digest1Integrity=_digest1Integrity;
        digest2Integrity=_digest2Integrity;
        expiredIntegrity= _expiredIntegrity;
        insertOrder(address(0), Status.INVALID, Content("", "", 0),0);
    }

    function apply(bytes digest1, bytes digest2,uint256 expired) public returns (uint256 _orderId){

        if(digest1Integrity == DigestIntegrity.APPLICANT){
            require(digest1.length>0 && digest1.length<=100);
        }else{
            require(digest1.length==0);
        }

        if(digest2Integrity == DigestIntegrity.APPLICANT){
            require(digest2.length>0 && digest2.length<=100);
        }else{
            require(digest2.length==0);
        }

        if(expiredIntegrity == DigestIntegrity.APPLICANT){
             require(expired!=0);
         }else{
             require(expired==0);
         }


        uint256  fee=0;

        if(certServiceFeeModule!=address(0)){
          fee=certServiceFeeModule.apply();
        }
        return insertOrder(msg.sender, Status.APPLIED, Content(digest1,digest2,expired),fee);
    }

    function insertOrder(address applicant, Status intialStatus, Content icc,uint256  fee) internal returns (uint256 _orderId){
        uint256 orderId = orders.push(Order(applicant, intialStatus, icc,fee));
        orderUpdated(applicant, orderId, intialStatus);
        return orderId;
    }

    function revoke(address applicant) public onlyOperator returns (uint256 _orderId) {
        require(applicant != address(0));

        Checkpoint memory cp = getCheckpointAt(applicant);

        //表示有有效的验证信息
        require(cp.content.digest1.length > 0);

        //插入订单
        Content memory icc = Content("", "", 0);
        uint256 orderId = insertOrder(applicant, Status.REVOKED, icc,0);

        //压栈
        appendElement(checkpoints[applicant], orderId, icc);

        return orderId;
    }

    function pass(uint256 orderId,bytes digest1, bytes digest2,uint256 expired) public onlyOperator {

        Order storage order = orders[orderId];
        Content content=order.content;

        if(digest1Integrity == DigestIntegrity.VERIFIER){
            require(digest1.length>0 && digest1.length<=100);
            content.digest1=digest1;
        }else{
            require(digest1.length==0);
        }

        if(digest2Integrity == DigestIntegrity.VERIFIER){
            require(digest2.length>0 && digest2.length<=100);
            content.digest2=digest2;
        }else{
            require(digest2.length==0);
        }

        if(expiredIntegrity == DigestIntegrity.VERIFIER){
             require(expired!=0);
             content.expired=expired;
          }else{
               require(expired==0);
          }
        audit(orderId, Status.PASSED);
    }


    function reject(uint256 orderId) public onlyOperator {
        audit(orderId, Status.REJECTED);
    }

    function audit(uint256 orderId, Status result) internal {
        require(orderId < orders.length);
        require(result == Status.PASSED || result == Status.REJECTED);

        Order storage order = orders[orderId];

        require(order.status == Status.APPLIED);

        Checkpoint memory cp = getCheckpointAt(order.applicant);

        //检查身份验证数据版本
        if (orderId < cp.dataVersion) {
            changeStatus(order, orderId, Status.DISCARDED);
            return;
        }

        //查询验证结果
        if (result == Status.REJECTED) {
            changeStatus(order, orderId, Status.REJECTED);
            return;
        }

        //订单设置通过
        changeStatus(order, orderId, Status.PASSED);

        //压栈
        appendElement(checkpoints[order.applicant], orderId, order.content);

    }

    function changeStatus(Order storage order, uint256 orderId, Status nextStatus) internal {
        order.status = nextStatus;
        orderUpdated(order.applicant, orderId, nextStatus);
    }


    function appendElement(Checkpoint[] storage checkpointList, uint256 dataVersion, Content memory icd) internal returns (uint256) {
        uint256 length = checkpointList.push(Checkpoint(block.number, dataVersion, icd));
        return length - 1;
    }



    /// @dev `getCheckpointAt` retrieves the number of tokens at a given block number
    /// @param checkpointList The history of values being queried
    /// @param _block The block number to retrieve the value at
    /// @return The number of tokens being queried
    function getCheckpointAt(Checkpoint[] storage checkpointList, uint _block) internal view returns (Checkpoint) {
        if (checkpointList.length == 0) {
            return Checkpoint(0, 0, Content("", "", 0));
        }

        // Shortcut for the actual value
        if (_block >= checkpointList[checkpointList.length - 1].fromBlock)
            return checkpointList[checkpointList.length - 1];
        if (_block < checkpointList[0].fromBlock) {
            return Checkpoint(0, 0, Content("", "", 0));
        }

        // Binary search of the value in the array
        uint min = 0;
        uint max = checkpointList.length - 1;
        while (max > min) {
            uint mid = (max + min + 1) / 2;
            if (checkpointList[mid].fromBlock <= _block) {
                min = mid;
            } else {
                max = mid - 1;
            }
        }
        return checkpointList[min];
    }

    function getCheckpointAt(address _owner) internal view returns (Checkpoint){
        return getCheckpointAt(checkpoints[_owner], block.number);
    }

    function getDataAt(address _owner, uint256 _atBlock) view public returns (bytes digest1, bytes digest2, uint256 expired, uint256 dataVersion) {
        Checkpoint memory cp = getCheckpointAt(checkpoints[_owner], _atBlock);
        return (cp.content.digest1, cp.content.digest2, cp.content.expired, cp.dataVersion);
    }

    function getData(address _owner) view public returns (bytes digest1, bytes digest2, uint256 expired, uint256 dataVersion) {
        return getDataAt(_owner, block.number);
    }

    function getData() view public returns (bytes digest1, bytes digest2, uint256 expired, uint256 dataVersion) {
        return getData(msg.sender);
    }

    function getOrder(uint256 _orderId) view public returns (address applicant, Status status, bytes digest1, bytes digest2, uint256 expired,uint256 feeDcc) {
        Order memory rf = orders[_orderId];
        return (rf.applicant, rf.status, rf.content.digest1, rf.content.digest2, rf.content.expired,rf.feeDcc);
    }

    function getOrderLength() view public returns (uint256 length) {
        return orders.length;
    }

    function getOwner() view public returns (string _ret) {
        return "";
    }

     function  getExpectedFeeDcc()view  public returns(uint256){
         if(certServiceFeeModule!=address(0)){
              return certServiceFeeModule.getFee();
         }
     }

}