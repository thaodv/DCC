pragma solidity ^0.4.11;

import "../ownership/OperatorPermission.sol";
import "./CertServiceFeeModule.sol";
/**
 * deprecated
 */
contract CertService2 is OperatorPermission {

    mapping(address => Checkpoint[]) checkpoints;

    Order[] orders;

    enum Status {INVALID, APPLIED, PASSED, REJECTED, DISCARDED, REVOKED}

    event orderUpdated(address indexed applicant, uint256 indexed orderId, Status status);

    struct Order {

        address applicant;

        Status status;

        Content content;
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

    CertServiceFeeModule  certServiceFeeModule;

    function setCertServiceFeeModuleAddress(address certServiceFeeModuleAddress) public  onlyOperator{
        certServiceFeeModule=CertServiceFeeModule(certServiceFeeModuleAddress);
     }

    function CertService2(bytes _name,address certServiceFeeModuleAddress) public {
        name=_name;
        certServiceFeeModule=CertServiceFeeModule(certServiceFeeModuleAddress);
        insertOrder(address(0), Status.INVALID, Content("", "", 0));
    }


    function apply(uint256 expired) public returns (uint256 _orderId){
        require(expired > 0);
        certServiceFeeModule.apply();
        return insertOrder(msg.sender, Status.APPLIED, Content("","",expired));
    }

    function insertOrder(address applicant, Status intialStatus, Content icc) internal returns (uint256 _orderId){
        uint256 orderId = orders.push(Order(applicant, intialStatus, icc));
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
        uint256 orderId = insertOrder(applicant, Status.REVOKED, icc);

        //压栈
        appendElement(checkpoints[applicant], orderId, icc);

        return orderId;
    }

    function pass(uint256 orderId,bytes digest1, bytes digest2) public onlyOperator {
        require(digest1.length > 0 && digest1.length <= 100);
        require(digest2.length <= 100);
        audit(orderId, Status.PASSED,digest1,digest2);
    }

    function reject(uint256 orderId,bytes digest1, bytes digest2) public onlyOperator {
        require(digest1.length > 0 && digest1.length <= 100);
        require(digest2.length <= 100);
        audit(orderId, Status.REJECTED,digest1,digest2);
    }

    function audit(uint256 orderId, Status result,bytes digest1, bytes digest2) internal {
        require(orderId < orders.length);
        require(result == Status.PASSED || result == Status.REJECTED);

        Order storage order = orders[orderId];

        Content content=order.content;

        content.digest1=digest1;

        content.digest2=digest2;

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

    function getOrder(uint256 _orderId) view public returns (address applicant, Status status, bytes digest1, bytes digest2, uint256 expired) {
        Order memory rf = orders[_orderId];
        return (rf.applicant, rf.status, rf.content.digest1, rf.content.digest2, rf.content.expired);
    }

    function getOrderLength() view public returns (uint256 length) {
        return orders.length;
    }

    function getOwner() view public returns (string _ret) {
        return "";
    }

     function  getFee()view  public returns(uint256){
         return certServiceFeeModule.getFee();
     }
}
