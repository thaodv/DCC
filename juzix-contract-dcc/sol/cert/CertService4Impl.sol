pragma solidity ^0.4.2;

import "./CertService4.sol";
import "./CertRepo.sol";
import "./CertServiceFeeModule.sol";
import "../math/SafeMath.sol";

contract CertService4Impl is CertService4, CertRepo{

    using SafeMath for uint256;

    event orderUpdated(address indexed applicant, uint256 indexed orderId, Status status);

    enum Status {INVALID, APPLIED,CANCELED,ACCEPTED, PASSED, REJECTED}

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

    function setCertServiceFeeModuleAddress(address certServiceFeeModuleAddress) public{
        onlyOwner();
        certServiceFeeModule=CertServiceFeeModule(certServiceFeeModuleAddress);
    }


    function CertService4Impl(bytes _name, DigestIntegrity _digest1Integrity, DigestIntegrity _digest2Integrity,
        DigestIntegrity _digest3Integrity,DigestIntegrity _expiredIntegrity) public CertRepo(_name,_digest1Integrity,_digest2Integrity,_digest3Integrity,_expiredIntegrity){
        register("CertService4ImplModule", "0.0.1.0", "CertService4Impl", "0.0.1.0");
        insertOrder(address(0), Status.INVALID, Content("", "","", 0),0);
    }

    function insertOrder(address applicant, Status status, Content icc,uint256  fee) internal returns (uint256 orderId){
        orderId = certOrders.push(CertOrder(0,applicant, icc,fee,status))-1;
        certOrders[orderId].orderId=orderId;
        applicantIndex[applicant].push(orderId);
        orderUpdated(applicant,orderId, status);
        return orderId;
    }

    function apply(bytes digest1, bytes digest2,bytes digest3,uint256 expired) public returns (uint256 _orderId){

        uint256 len=applicantIndex[msg.sender].length;
        if(len>0){
            //uint256 lastOrderId=applicantIndex[msg.sender][len-1];
            //Status lastOrderStatus=certOrders[lastOrderId].status;
            CertOrder memory lastCertOrder=innerGetLastCertOrder(msg.sender);
            //require(lastOrderStatus==Status.PASSED ||lastOrderStatus==Status.REJECTED);
            if(!(lastCertOrder.status==Status.PASSED ||lastCertOrder.status==Status.REJECTED || lastCertOrder.status==Status.CANCELED)){
                log("!(lastCertOrder.status==Status.PASSED ||lastCertOrder.status==Status.REJECTED || lastCertOrder.status==Status.CANCELED");
                throw;
            }
        }

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

        if(digest3Integrity == DigestIntegrity.APPLICANT){
            if(!(digest3.length>0 && digest3.length<=100)){
                log("!(digest3.length>0 && digest3.length<=100)");
                throw;
            }
        }else{
            if(!(digest3.length==0)){
                log("!(digest3.length==0)");
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

        uint256  fee=0;

        if(certServiceFeeModule!=address(0)){
            fee=certServiceFeeModule.apply();
        }

        return insertOrder(msg.sender, Status.APPLIED, Content(digest1,digest2,digest3,expired),fee);
    }

    function cancel(uint256 orderId) external{
        if(!(orderId < certOrders.length)){
            log("!(orderId < certOrders.length)");
            throw;
        }
        CertOrder storage order = certOrders[orderId];
        if(!((operators[msg.sender] || msg.sender == owner) || order.applicant==msg.sender)){
            log("!((operators[msg.sender] || msg.sender == owner) || order.applicant==msg.sender)");
            throw;
        }
        if(!(order.status == Status.APPLIED)){
            log("!(order.status == Status.APPLIED)");
            throw;
        }
        changeStatus(order, orderId, Status.CANCELED);
    }

    function accept(uint256 orderId) public{
        onlyOperator();
        if(!(orderId < certOrders.length)){
            log("!(orderId < certOrders.length)");
            throw;
        }
        CertOrder storage order = certOrders[orderId];
        if(!(order.status == Status.APPLIED)){
            log("!(order.status == Status.APPLIED)");
            throw;
        }
        changeStatus(order, orderId, Status.ACCEPTED);
    }


    function pass(uint256 orderId,bytes digest1, bytes digest2,bytes digest3,uint256 expired) public{
        onlyOperator();
        CertOrder storage order = certOrders[orderId];
        Content storage content=order.content;

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

        if(digest3Integrity == DigestIntegrity.VERIFIER){
            if(!(digest3.length>0 && digest3.length<=100)){
                log("!(digest3.length>0 && digest3.length<=100)");
                throw;
            }
            content.digest3=digest3;
        }else{
            if(!(digest3.length==0)){
                log("!(digest3.length==0)");
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


    function reject(uint256 orderId) public{
        onlyOperator();
        audit(orderId, Status.REJECTED);
    }


    function audit(uint256 orderId, Status nextStatus) internal {
        if(!(orderId < certOrders.length)){
            log("!(orderId < certOrders.length)");
            throw;
        }
        if(!(nextStatus == Status.PASSED || nextStatus == Status.REJECTED)){
            log("!(nextStatus == Status.PASSED || nextStatus == Status.REJECTED)");
            throw;
        }

        CertOrder storage order = certOrders[orderId];

        if(!(order.status == Status.ACCEPTED)){
            log("!(order.status == Status.ACCEPTED)");
            throw;
        }

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


    function getCertOrder(address applicant,uint256 index) constant public returns (uint256 _orderId,address _applicant, uint8 status, bytes digest1, bytes digest2,bytes digest3, uint256 expired,uint256 feeDcc) {
        if(!(applicant!=0)){
            log("!(applicant!=0)");
            throw;
        }
        uint256[] memory applicantList=applicantIndex[applicant];
        if(!(index<applicantList.length && applicantList.length>0)){
            return (0,0,0,"","","",0,0);
        }
        CertOrder memory order= certOrders[applicantList[index]];
        return (order.orderId,order.applicant, uint8(order.status), order.content.digest1, order.content.digest2,order.content.digest3, order.content.expired,order.fee);
    }

    function getLastCertOrder(address applicant)constant public returns (uint256 _orderId,address _applicant, uint8 status, bytes digest1, bytes digest2,bytes digest3, uint256 expired,uint256 feeDcc) {
        if(!(applicant!=0)){
            log("!(applicant!=0)");
            throw;
        }
        uint256[] memory applicantList=applicantIndex[applicant];
        if(!(applicantList.length>0)){
            return (0,0,0,"","","",0,0);
        }
        return getCertOrder(applicant,applicantList.length-1);
    }

    function innerGetLastCertOrder(address applicant)constant internal returns(CertOrder certOrder) {
        if(!(applicant!=0)){
            log("!(applicant!=0)");
            throw;
        }
        uint256[] memory applicantList=applicantIndex[applicant];
        return certOrders[applicantList[applicantList.length-1]];
    }

    function getCertOrderLength() constant public returns (uint256 length) {
        return certOrders.length;
    }


    function getOrderArrayLengthByApplicantIndex(address applicant) public constant returns (uint256){
        if(!(applicant != address(0))){
            log("!(applicant != address(0))");
            throw;
        }
        return applicantIndex[applicant].length;
    }


    function queryOrderIdListByApplicantIndex(address applicant, uint256 from, uint256 to) public constant returns (uint256[]){
        if(!(to < getOrderArrayLengthByApplicantIndex(applicant))){
            log("!(to < getOrderArrayLengthByApplicantIndex(applicant))");
            throw;
        }
        if(!(from <= to)){
            log("!(from <= to)");
            throw;
        }

        uint256 length = to.sub(from).add(1);
        uint256[] memory orderIdList = new uint256[](length);
        for (uint256 i = from; i <= to; i++) {
            orderIdList[i.sub(from)] = applicantIndex[applicant][i];
        }
        return orderIdList;
    }

    function  getExpectedFeeDcc()constant  public returns(uint256){
        if(certServiceFeeModule!=address(0)){
            return certServiceFeeModule.getFee();
        }
    }

    function getCertOrder(uint256 orderId) constant public returns (uint256 _orderId, address applicant,Status status, bytes digest1, bytes digest2,bytes digest3, uint256 expired,uint256 fee) {
        CertOrder memory order = certOrders[orderId];
        return (orderId,order.applicant,order.status, order.content.digest1, order.content.digest2,order.content.digest3, order.content.expired,order.fee);
    }
}
