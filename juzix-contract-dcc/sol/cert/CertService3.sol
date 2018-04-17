pragma solidity ^0.4.2;

import "../permission/OperatorPermission.sol";
import "./CertServiceFeeModule.sol";
import "./CertServiceIntf.sol";

contract CertService3 is OperatorPermission,CertServiceIntf{

    mapping(address => Checkpoint[]) datas;

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
        Content value;

    }

    struct Content {

        bytes digest1;

        bytes digest2;

        uint256 expired;

    }

    bytes public name;

    CertServiceFeeModule public certServiceFeeModule;

     function setDigest1Integrity(DigestIntegrity  _digest1Integrity) public{
        onlyOwner();
        digest1Integrity=_digest1Integrity;
     }

     function setDigest2Integrity(DigestIntegrity  _digest2Integrity) public{
        onlyOwner();
        digest2Integrity=_digest2Integrity;
     }

      function setExpiredIntegrity(DigestIntegrity  _expiredIntegrity) public{
          onlyOwner();
          expiredIntegrity= _expiredIntegrity;
       }


    function setCertServiceFeeModuleAddress(address certServiceFeeModuleAddress) public{
        onlyOwner();
        certServiceFeeModule=CertServiceFeeModule(certServiceFeeModuleAddress);
     }

    function CertService3(bytes _name,DigestIntegrity _digest1Integrity,DigestIntegrity _digest2Integrity,DigestIntegrity _expiredIntegrity) public {
        register("CertService3Module", "0.0.1.0", "CertService3", "0.0.1.0");
        digest1Integrity=_digest1Integrity;
        digest2Integrity=_digest2Integrity;
        expiredIntegrity= _expiredIntegrity;
        name=_name;
        insertOrder(address(0), Status.INVALID, Content("", "", 0),0);
    }


    function apply(bytes digest1, bytes digest2,uint256 expired) public returns (uint256 _orderId){

         if(digest1Integrity == DigestIntegrity.APPLICANT){
                if(!(digest1.length>0 && digest1.length<=100)){
                   log("!(digest1.length>0 && digest1.length<=100)");
                   throw;
                }
          }else{
                if(!(digest1.length==0)){
                   log("!(digest1.length==0)");
                   throw;
                }
          }

          if(digest2Integrity == DigestIntegrity.APPLICANT){
                  if(!(digest2.length>0 && digest2.length<=100)){
                     log("!(digest2.length>0 && digest2.length<=100)");
                     throw;
                  }
            }else{
                  if(!(digest2.length==0)){
                     log("!(digest2.length==0)");
                     throw;
                  }
            }

           if(expiredIntegrity == DigestIntegrity.APPLICANT){
                if(!(expired!=0)){
                   log("!(expired!=0)");
                   throw;
                }
          }else{
                if(!(expired==0)){
                   log("!(expired==0)");
                   throw;
                }
          }

        uint256 fee=0;

       if(certServiceFeeModule!=address(0)){
         fee=certServiceFeeModule.apply();
       }

        return insertOrder(msg.sender, Status.APPLIED, Content(digest1,digest2,expired),fee);
    }

    function insertOrder(address applicant, Status intialStatus, Content icc,uint256  fee) internal returns (uint256 _orderId){
        //订单号=数组长度-1
        uint256 orderId = orders.push(Order(applicant, intialStatus, icc,fee)) - 1;
        orderUpdated(applicant, orderId, intialStatus);
        return orderId;
    }

    function revoke(address applicant) public returns (uint256 _orderId) {
        if(!(applicant != address(0))){
            throw;
        }
        onlyOperator();
        Checkpoint memory cp = getCheckpointAt(applicant);

        //表示有有效的验证信息
        if(!(cp.value.digest1.length > 0)){
            log("!(cp.value.digest1.length > 0)");
            throw;
        }

        //插入订单
        Content memory icc = Content("", "", 0);
        uint256 orderId = insertOrder(applicant, Status.REVOKED, icc,0);

        //压栈
        appendElement(datas[applicant], orderId, icc);

        return orderId;
    }

    function pass(uint256 orderId,bytes digest1, bytes digest2,uint256 expired) public  {
        onlyOperator();

        Order storage order = orders[orderId];
        Content content=order.content;

        if(digest1Integrity == DigestIntegrity.VERIFIER){
            if(!(digest1.length>0 && digest1.length<=100)){
               log("!(digest1.length>0 && digest1.length<=100)");
               throw;
            }
             content.digest1=digest1;
        }else{
            if(!(digest1.length==0)){
               log("!(digest1.length==0)");
               throw;
            }
        }

        if(digest2Integrity == DigestIntegrity.VERIFIER){
            if(!(digest2.length>0 && digest2.length<=100)){
               log("!(digest2.length>0 && digest2.length<=100)");
               throw;
            }
            content.digest2=digest2;
        }else{
            if(!(digest2.length==0)){
              log("!(digest2.length==0)");
              throw;
            }
        }

        if(expiredIntegrity == DigestIntegrity.VERIFIER){
                if(!(expired!=0)){
                   log("!(expired!=0)");
                   throw;
                }
             content.expired=expired;
          }else{
                if(!(expired==0)){
                   log("!(expired==0)");
                   throw;
                }
          }

        audit(orderId, Status.PASSED);

    }

    function reject(uint256 orderId) public  {
        onlyOperator();
        audit(orderId, Status.REJECTED);
    }


    function audit(uint256 orderId, Status result) internal {
        if(!(orderId < orders.length)){
            log("!(orderId < orders.length)");
            throw;
        }
        if(!(result == Status.PASSED || result == Status.REJECTED)){
            log("!(result == Status.PASSED || result == Status.REJECTED)");
            throw;
        }

        Order storage order = orders[orderId];

        if(!(order.status == Status.APPLIED)){
            log("!(order.status == Status.APPLIED)");
            throw;
        }

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
        appendElement(datas[order.applicant], orderId, order.content);

    }

    function changeStatus(Order storage order, uint256 orderId, Status nextStatus) internal {
        order.status = nextStatus;
        orderUpdated(order.applicant, orderId, nextStatus);
    }




    function appendElement(Checkpoint[] storage checkpointList, uint256 dataVersion, Content memory icd) internal returns (uint256) {
        uint256 length = checkpointList.push(Checkpoint(block.number, dataVersion, icd));
        return length - 1;
    }




    function getCheckpointAt(Checkpoint[] storage checkpointList, uint _block) internal constant returns (Checkpoint) {
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

    function getCheckpointAt(address _owner) internal constant returns (Checkpoint){
        return getCheckpointAt(datas[_owner], block.number);
    }

    function getDataAt(address _owner, uint256 _atBlock) constant public returns (bytes digest1, bytes digest2, uint256 expired, uint256 dataVersion) {
        Checkpoint memory cp = getCheckpointAt(datas[_owner], _atBlock);
        return (cp.value.digest1, cp.value.digest2, cp.value.expired, cp.dataVersion);
    }

    function getData(address _owner) constant public returns (bytes digest1, bytes digest2, uint256 expired, uint256 dataVersion) {
        return getDataAt(_owner, block.number);
    }

    function getData() constant public returns (bytes digest1, bytes digest2, uint256 expired, uint256 dataVersion) {
        return getData(msg.sender);
    }

    function getOrder(uint256 _orderId) constant public returns (address applicant, Status status, bytes digest1, bytes digest2, uint256 expired,uint256 feeDcc) {
        Order memory rf = orders[_orderId];
        return (rf.applicant, rf.status, rf.content.digest1, rf.content.digest2, rf.content.expired,rf.feeDcc);
    }

    function getOrderLength() constant public returns (uint256 length) {
        return orders.length;
    }

     function  getExpectedFeeDcc()constant  public returns(uint256){
         if(certServiceFeeModule!=address(0)){
            return certServiceFeeModule.getFee();
         }
     }

}