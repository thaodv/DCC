pragma solidity ^0.4.2;

import "./CertService4.sol";
import "./CertRepo.sol";
import "./CertServiceFeeModule.sol";
import "../math/SafeMath.sol";

contract CertService4Impl is CertService4, CertRepo{

    using SafeMath for uint256;

    event orderUpdated(address indexed applicant, uint256 indexed orderId, Status status);

    enum Status {INVALID, APPLIED,ACCEPTED, PASSED, REJECTED, REVOKED}

    struct CertOrder{
        uint256 orderId;
        address applicant;
        Content content;
        uint256 fee;
        Status status;
    }

    CertOrder[]  certOrders;

    mapping(address => uint256[]) public applicantIndex;

    CertServiceFeeModule public certServiceFeeModule;

    function setCertServiceFeeModuleAddress(address certServiceFeeModuleAddress) public  onlyOwner{
        certServiceFeeModule=CertServiceFeeModule(certServiceFeeModuleAddress);
    }


    function CertService4Impl(bytes _name, DigestIntegrity _digest1Integrity, DigestIntegrity _digest2Integrity,
        DigestIntegrity _expiredIntegrity) public CertRepo(_name,_digest1Integrity,_digest2Integrity,_expiredIntegrity){
        insertOrder(address(0), Status.INVALID, Content("", "", 0),0);
    }

    function insertOrder(address applicant, Status status, Content icc,uint256  fee) internal returns (uint256 orderId){
        orderId = certOrders.push(CertOrder(0,applicant, icc,fee,status))-1;
        certOrders[orderId].orderId=orderId;
        applicantIndex[applicant].push(orderId);
        orderUpdated(applicant,orderId, status);
        return orderId;
    }

    function apply(bytes digest1, bytes digest2,uint256 expired) external returns (uint256 _orderId){

        uint256 len=applicantIndex[msg.sender].length;
        if(len>0){
            //uint256 lastOrderId=applicantIndex[msg.sender][len-1];
            //Status lastOrderStatus=certOrders[lastOrderId].status;
            CertOrder memory lastCertOrder=innerGetLastCertOrder(msg.sender);
            //require(lastOrderStatus==Status.PASSED ||lastOrderStatus==Status.REJECTED || lastOrderStatus==Status.REVOKED);
            require(lastCertOrder.status==Status.PASSED ||lastCertOrder.status==Status.REJECTED || lastCertOrder.status==Status.REVOKED);
        }

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

    function accept(uint256 orderId) external onlyOperator{
        require(orderId < certOrders.length);
        CertOrder storage order = certOrders[orderId];
        require(order.status == Status.APPLIED);
        changeStatus(order, orderId, Status.ACCEPTED);
    }


    function pass(uint256 orderId,bytes digest1, bytes digest2,uint256 expired) external onlyOperator{
        CertOrder storage order = certOrders[orderId];
        Content storage content=order.content;

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


    function reject(uint256 orderId) external onlyOperator{
        audit(orderId, Status.REJECTED);
    }


    function audit(uint256 orderId, Status nextStatus) internal {
        require(orderId < certOrders.length);
        require(nextStatus == Status.PASSED || nextStatus == Status.REJECTED);

        CertOrder storage order = certOrders[orderId];

        require(order.status == Status.ACCEPTED);

        //查询验证结果
        if (nextStatus == Status.REJECTED) {
            changeStatus(order, orderId, Status.REJECTED);
            return;
        }

        //订单设置通过
        changeStatus(order, orderId, Status.PASSED);

        //压栈
        appendElement(checkpoints[order.applicant], orderId, order.content);

    }


    function changeStatus(CertOrder storage order, uint256 orderId, Status nextStatus) internal {
        order.status = nextStatus;
        orderUpdated(order.applicant, orderId, nextStatus);
    }


    function getCertOrder(address applicant,uint256 index) view public returns (uint256 _orderId,address _applicant, uint8 status, bytes digest1, bytes digest2, uint256 expired,uint256 feeDcc) {
        require(applicant!=0);
        uint256[] memory applicantList=applicantIndex[applicant];
        if(!(index<applicantList.length && applicantList.length>0)){
            return (0,0,0,"","",0,0);
        }
        CertOrder memory order= certOrders[applicantList[index]];
        return (order.orderId,order.applicant, uint8(order.status), order.content.digest1, order.content.digest2, order.content.expired,order.fee);
    }

    function getLastCertOrder(address applicant)view public returns (uint256 _orderId,address _applicant, uint8 status, bytes digest1, bytes digest2, uint256 expired,uint256 feeDcc) {
        require(applicant!=0);
        uint256[] memory applicantList=applicantIndex[applicant];
        if(!(applicantList.length>0)){
            return (0,0,0,"","",0,0);
        }
        return getCertOrder(applicant,applicantList.length-1);
    }

    function innerGetLastCertOrder(address applicant)view internal returns(CertOrder certOrder) {
        require(applicant!=0);
        uint256[] memory applicantList=applicantIndex[applicant];
        return certOrders[applicantList[applicantList.length-1]];
    }


    function getCertOrderLength() view public returns (uint256 length) {
        return certOrders.length;
    }


    function getOrderArrayLengthByApplicantIndex(address applicant) public view returns (uint256){
        require(applicant != address(0));
        return applicantIndex[applicant].length;
    }


    function queryOrderIdListByApplicantIndex(address applicant, uint256 from, uint256 to) public view returns (uint256[]){
        require(to < getOrderArrayLengthByApplicantIndex(applicant));
        require(from <= to);

        uint256 length = to.sub(from).add(1);
        uint256[] memory orderIdList = new uint256[](length);
        for (uint256 i = from; i <= to; i++) {
            orderIdList[i.sub(from)] = applicantIndex[applicant][i];
        }
        return orderIdList;
    }
}
