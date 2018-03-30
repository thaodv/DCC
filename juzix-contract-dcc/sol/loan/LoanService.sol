pragma solidity ^0.4.2;

import "./AgreementIntf.sol";
import "../permission/OperatorPermission.sol";

contract  LoanService is OperatorPermission{

         Order[]  public orders;

        AgreementIntf public agreementContract;

        mapping(address => uint256[]) public creditIndex;

        mapping(address => uint256[]) public debitIndex;

        enum Status {INVALID, PROCESSING, APPROVED, REJECTED, DEPOSITED, PAID_OFF}

        event orderUpdated(uint256 indexed orderId, address indexed debit, address indexed credit, Status status);

        struct Order {

            address debit;

            address credit;

            bytes creditIdHash;

            Status orderStatus;

            bytes orderHash;

            bytes agreementHash;

            uint256 appliedTime;

        }

        mapping(uint256 => RepayPlan[]) public repayPlans;

        struct RepayPlan {

            uint256 expectedTime;

            uint256 actualTime;

        }

        function LoanService(address agreementContractAddress) public {
            register("LoanServiceModule", "0.0.1.0", "LoanService", "0.0.1.0");
            setAggreementContract(agreementContractAddress);
            orders.push(Order(address(0), address(0),"", Status.INVALID, "", "", 0));
        }

        function setAggreementContract(address agreementContractAddress) public{
                onlyOperator();
                if(!(agreementContractAddress != address(0))){
                   log("!(agreementContractAddress != address(0))");
                   throw;
                }
                agreementContract = AgreementIntf(agreementContractAddress);
        }

        function apply(address debit,bytes creditIdHash, uint256 appliedTime, bytes orderHash) public returns (uint256 _orderId){
            if(!(debit != address(0))){
               log("!(debit != address(0))");
               throw;
             }

             if(!(debit != msg.sender)){
               log("!(debit != msg.sender)");
               throw;
             }

             if(!(orderHash.length > 0 && orderHash.length <= 100)){
                log("!(orderHash.length > 0 && orderHash.length <= 100)");
                throw;
             }
            return insertOrder(debit, msg.sender,creditIdHash, Status.PROCESSING, orderHash,appliedTime);
        }

        function insertOrder(address debit, address credit,bytes creditIdHash, Status intialStatus, bytes orderHash, uint256 appliedTime) internal returns (uint256 newOrderId){
            newOrderId = orders.push(Order(debit, credit,creditIdHash, intialStatus, orderHash, "", appliedTime)) - 1;
             debitIndex[debit].push(newOrderId);
             creditIndex[credit].push(newOrderId);
            orderUpdated(newOrderId, debit, credit, intialStatus);
        }

        function getOrderAndChangeStatus(uint256 orderId, Status fromStatus, Status nextStatus,bool force) internal returns (Order storage returnOrder){

            if(!(orderId < orders.length)){
              log("!(orderId < orders.length)");
              throw;
            }
            Order storage order = orders[orderId];
            if(!(order.debit == msg.sender)){
              log("!(order.debit == msg.sender)");
              throw;
            }
           if(!(force == true  || order.orderStatus == fromStatus)){
             log("!(force == true  || order.orderStatus == fromStatus)");
             throw;
           }

            //状态机跃迁
            justChangeStatus(orderId, order, nextStatus);
            return order;
        }

        function justChangeStatus(uint256 orderId, Order storage order, Status nextStatus) internal {
            //状态机跃迁
            order.orderStatus = nextStatus;
            orderUpdated(orderId, order.debit, order.credit, nextStatus);
        }

        function approve(uint256 orderId) public {
            getOrderAndChangeStatus(orderId, Status.PROCESSING, Status.APPROVED,false);
        }

        function reject(uint256 orderId) public {
            getOrderAndChangeStatus(orderId, Status.PROCESSING, Status.REJECTED,false);
        }

        function deposit(uint256 orderId, bytes agreementHash, uint256[] exptectedRepayTimes) public {
            if(!(agreementHash.length > 0)){
                log("!(agreementHash.length > 0)");
                throw;
              }

             if(!(exptectedRepayTimes.length > 0)){
              log("!(exptectedRepayTimes.length > 0)");
              throw;
             }

            Order storage order = getOrderAndChangeStatus(orderId, Status.APPROVED, Status.DEPOSITED, false);
            order.agreementHash = agreementHash;

            for (uint256 i = 0; i < exptectedRepayTimes.length; i++) {
                if(!(exptectedRepayTimes[i] > 0)){
                    log("!(exptectedRepayTimes[i] > 0)");
                    throw;
                 }
                repayPlans[orderId].push(RepayPlan(exptectedRepayTimes[i], 0));
            }

            agreementContract.submit(orderId, order.debit, order.credit,order.creditIdHash, agreementHash);

        }

        function recordRepay(uint256 orderId, uint256 offset, uint256 actualRepayTime) public {

            if(!(orderId < orders.length)){
               log("!(orderId < orders.length)");
               throw;
             }

             if(!(actualRepayTime > 0)){
               log("!(actualRepayTime > 0)");
               throw;
             }
            Order storage order = orders[orderId];

            if(!(order.orderStatus == Status.DEPOSITED)){
               log("!(order.orderStatus == Status.DEPOSITED)");
               throw;
             }
             if(!(order.debit == msg.sender)){
               log("!(order.debit == msg.sender)");
               throw;
             }
             if(!(offset > 0 && offset <= repayPlans[orderId].length)){
                log("!(offset > 0 && offset <= repayPlans[orderId].length)");
                throw;
              }
            repayPlans[orderId][offset - 1].actualTime = actualRepayTime;
        }

        function payOff(uint256 orderId) public {
             getOrderAndChangeStatus(orderId, Status.DEPOSITED, Status.PAID_OFF, false);
        }

        function forceUpdateStatus(uint256 orderId, Status nextStatus) public {
            getOrderAndChangeStatus(orderId, Status.INVALID, nextStatus, true);
        }

        function getOrder(uint256 _orderId)constant  public  returns (uint256 orderId, address debit, address credit,bytes creditIdHash, Status
            status, bytes orderHash, bytes agreementHash) {
            if(!(_orderId < orders.length)){
               log("!(_orderId < orders.length)");
               throw;
             }
            Order memory order = orders[_orderId];
            return (_orderId, order.debit, order.credit, order.creditIdHash, order.orderStatus, order.orderHash, order.agreementHash);
        }

        function getReplayPlanLength(uint256 _orderId)constant public  returns (uint256 repayPlansLength) {
            if(!(_orderId < orders.length)){
               log("!(_orderId < orders.length)");
               throw;
             }
            return repayPlans[_orderId].length;
        }

        function getReplayPlan(uint256 _orderId, uint256 offset)constant  public  returns (uint256 expectedTime, uint256 actualTime) {
            if(!(_orderId < orders.length)){
               log("!(_orderId < orders.length)");
               throw;
             }

             if(!(offset > 0 && offset <= repayPlans[_orderId].length)){
               log("!(offset > 0 && offset <= repayPlans[_orderId].length)");
               throw;
             }
            RepayPlan memory rp = repayPlans[_orderId][offset - 1];
            return (rp.expectedTime, rp.actualTime);
        }

        function getOrderLength()constant  public returns (uint256 length) {
            return orders.length;
        }

        function queryOrderIdArrayByCreditIndex(address _credit)constant  public  returns (uint256[]){
                if(!(_credit != address(0))){
                  log("!(_credit != address(0))");
                  throw;
                }
                return creditIndex[_credit];
            }

        function getOrderArrayLengthByCreditIndex(address _credit) constant public  returns (uint256){
            if(!(_credit != address(0))){
              log("!(_credit != address(0))");
              throw;
            }
            return creditIndex[_credit].length;
        }

        function queryOrderIdArrayByDebitIndex(address _debit) constant public  returns (uint256[]){
                if(!(_debit != address(0))){
                 log("!(_debit  != address(0))");
                 throw;
               }
                return debitIndex[_debit];
            }

        function getOrderArrayLengthByDebitIndex(address _debit) constant public  returns (uint256){
             if(!(_debit != address(0))){
                 log("!(_debit != address(0))");
                 throw;
               }
            return debitIndex[_debit].length;
        }

}