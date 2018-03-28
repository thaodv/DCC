pragma solidity ^0.4.11;

import './AgreementIntf.sol';
import '../ownership/Ownable.sol';

contract LoanService is Ownable{

    Order[] orders;

    mapping(address => uint256[]) public debitIndex;

    mapping(address => uint256[]) public creditIndex;

    AgreementIntf public agreementContract;

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

    mapping(uint256 => RepayPlan[]) repayPlans;

    struct RepayPlan {

        uint256 expectedTime;

        uint256 actualTime;

    }

    function LoanService(address agreementContractAddress) public {
        setAggreementContract(agreementContractAddress);
        orders.push(Order(address(0), address(0), "", Status.INVALID, "", "", 0));
    }

    function setAggreementContract(address agreementContractAddress) public onlyOwner {
        require(agreementContractAddress != address(0));
        agreementContract = AgreementIntf(agreementContractAddress);
    }

    function apply(address debit, bytes creditIdHash, uint256 appliedTime, bytes orderHash) public returns (uint256
        _orderId){
        require(debit != address(0) && debit != msg.sender);
        require(orderHash.length > 0 && orderHash.length <= 100);
        return insertOrder(debit, msg.sender, creditIdHash, Status.PROCESSING, orderHash, appliedTime);
    }

    function insertOrder(address debit, address credit, bytes creditIdHash, Status intialStatus, bytes orderHash, uint256 appliedTime)
    internal returns (uint256 newOrderId){
        newOrderId = orders.push(Order(debit, credit, creditIdHash, intialStatus, orderHash, "", appliedTime)) - 1;
        debitIndex[debit].push(newOrderId);
        creditIndex[credit].push(newOrderId);
        orderUpdated(newOrderId, debit, credit, intialStatus);
    }

    function getOrderAndChangeStatus(uint256 orderId, Status fromStatus, Status nextStatus, bool force) internal
    returns (Order storage returnOrder){
        require(orderId < orders.length);
        Order storage order = orders[orderId];
        require(order.debit == msg.sender);
        require(force == true || order.orderStatus == fromStatus);

        justChangeStatus(orderId, order, nextStatus);
        return order;
    }

    function justChangeStatus(uint256 orderId, Order storage order, Status nextStatus) internal {
        //状态机跃迁
        order.orderStatus = nextStatus;
        orderUpdated(orderId, order.debit, order.credit, nextStatus);
    }

    function approve(uint256 orderId) public {
        getOrderAndChangeStatus(orderId, Status.PROCESSING, Status.APPROVED, false);
    }

    function reject(uint256 orderId) public {
        getOrderAndChangeStatus(orderId, Status.PROCESSING, Status.REJECTED, false);
    }

    function deposit(uint256 orderId, bytes agreementHash, uint256[] exptectedRepayTimes) public {
        require(agreementHash.length > 0);
        require(exptectedRepayTimes.length > 0);
        Order storage order = getOrderAndChangeStatus(orderId, Status.APPROVED, Status.DEPOSITED, false);
        order.agreementHash = agreementHash;

        for (uint256 i = 0; i < exptectedRepayTimes.length; i++) {
            require(exptectedRepayTimes[i] > 0);
            repayPlans[orderId].push(RepayPlan(exptectedRepayTimes[i], 0));
        }
        agreementContract.submit(orderId, order.debit, order.credit,order.creditIdHash, agreementHash);
    }

    function recordRepay(uint256 orderId, uint256 offset, uint256 actualRepayTime) public {
        require(orderId < orders.length);
        require(actualRepayTime > 0);

        Order storage order = orders[orderId];
        require(order.orderStatus == Status.DEPOSITED);
        require(order.debit == msg.sender);

        require(offset > 0 && offset <= repayPlans[orderId].length);
        repayPlans[orderId][offset - 1].actualTime = actualRepayTime;
    }

    function payOff(uint256 orderId) public {
        getOrderAndChangeStatus(orderId, Status.DEPOSITED, Status.PAID_OFF, false);
    }

    function forceUpdateStatus(uint256 orderId, Status nextStatus) public {
        getOrderAndChangeStatus(orderId, Status.INVALID, nextStatus, true);
    }

    function getOrder(uint256 _orderId) view external returns (uint256 orderId, address debit, address credit,
        bytes creditIdHash, Status status, bytes orderHash, bytes agreementHash) {
        require(_orderId < orders.length);
        Order memory order = orders[_orderId];
        return (_orderId, order.debit, order.credit, order.creditIdHash, order.orderStatus, order.orderHash, order
        .agreementHash);
    }

    function getReplayPlanLength(uint256 _orderId) view external returns (uint256 repayPlansLength) {
        require(_orderId < orders.length);
        return repayPlans[_orderId].length;
    }

    function getReplayPlan(uint256 _orderId, uint256 offset) view external returns (uint256 expectedTime, uint256 actualTime) {
        require(_orderId < orders.length);
        require(offset > 0 && offset <= repayPlans[_orderId].length);
        RepayPlan memory rp = repayPlans[_orderId][offset - 1];
        return (rp.expectedTime, rp.actualTime);
    }

    function getOrderLength() view public returns (uint256 length) {
        return orders.length;
    }

    function getOwner() view public returns (string _ret) {
        return "";
    }

    function queryOrderIdArrayByCreditIndex(address _credit) public view returns (uint256[]){
        require(_credit != address(0));
        return creditIndex[_credit];
    }

    function getOrderArrayLengthByCreditIndex(address _credit) public view returns (uint256){
        require(_credit != address(0));
        return creditIndex[_credit].length;
    }

    function queryOrderIdArrayByDebitIndex(address _debit) public view returns (uint256[]){
        require(_debit != address(0));
        return debitIndex[_debit];
    }

    function getOrderArrayLengthByDebitIndex(address _debit) public view returns (uint256){
        require(_debit != address(0));
        return debitIndex[_debit].length;
    }

}
