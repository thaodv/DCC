pragma solidity ^0.4.2;

import "../juzixtoken/ERC20Basic.sol";
import "../ownership/OperatorPermission.sol";
import "../math/SafeMath.sol";
import "./LoanFee.sol";

contract LoanFeeService is OperatorPermission, LoanFee {

    using SafeMath for uint256;

    struct FeeOrder {
        uint256 orderId;
        address borrower;
        uint256 availableAmount;
        uint256 totalAmount;
    }

    uint256 public incentivePercent = 10;

    uint256 public riskPercent = 0;

    uint256 public constant feeRateDenominator = 100;

    address public incentiveAddress;

    address public riskAddress;

    address public fundAddress;

    uint256 public minFee;

    FeeOrder[] public feeOrders;

    mapping(uint256 => uint256) public orderIdIndex;

    address public caller;

    ERC20Basic public erc20;

    modifier onlyCaller{
        require(caller == msg.sender);
        _;
    }

    event FeeIncomingEvent(uint256 indexed orderId, address indexed from, uint256 amount);
    event FeeOutgoingEvent(uint256 indexed orderId, address indexed to, uint256 amount);

    function LoanFeeService(address _caller, address erc20Address) public {
        require(_caller != address(0));
        require(erc20Address != address(0));
        caller = _caller;
        erc20 = ERC20Basic(erc20Address);
        feeOrders.push(FeeOrder(0, address(0), 0, 0));
    }

    function insertOrder(uint256 orderId, address borrower, uint256 availableAmount, uint256 totalAmount)
    internal {
        require(orderIdIndex[orderId] == 0);
        uint256 newFeeOrderId = feeOrders.push(FeeOrder(orderId, borrower, availableAmount, totalAmount)) - 1;
        orderIdIndex[orderId] = newFeeOrderId;
    }

    function apply(uint256 orderId, address borrower, uint256 totalAmount) onlyCaller external {
        require(orderId > 0);
        require(totalAmount >= minFee);
        require(borrower != address(0));
        insertOrder(orderId, borrower, totalAmount, totalAmount);
        if (totalAmount > 0) {
            incoming(orderId, totalAmount);
        }
    }

    function incoming(uint256 orderId, uint256 amount) internal {
        erc20.superTransfer(this, amount);
        FeeIncomingEvent(orderId, tx.origin, amount);
    }

    function cancel(uint256 orderId) onlyCaller external {
        FeeOrder storage feeOrder = innerGetOrder(orderId);
        uint256 availableAmount = feeOrder.availableAmount;
        if (availableAmount > 0) {
            outgoing(feeOrder, feeOrder.borrower, availableAmount);
        }
    }

    function innerGetOrder(uint256 orderId) internal returns (FeeOrder storage feeOrder){
        require(orderId > 0);
        feeOrder = feeOrders[orderIdIndex[orderId]];
        require(feeOrder.orderId > 0);
        return feeOrder;
    }

    function getIncentiveFee(uint256 _totalAmount) view internal returns (uint256 _fee){
        return _totalAmount.mul(incentivePercent).div(feeRateDenominator);
    }

    function getRiskFee(uint256 _totalAmount) view internal returns (uint256 _fee){
        return _totalAmount.mul(riskPercent).div(feeRateDenominator);
    }

    function audit(uint256 orderId) onlyCaller external {
        FeeOrder storage feeOrder = innerGetOrder(orderId);
        uint256 totalAmount = feeOrder.totalAmount;

        if (incentiveAddress != address(0)) {
            uint256 incentiveFee = getIncentiveFee(totalAmount);
            if (incentiveFee > 0) {
                outgoing(feeOrder, incentiveAddress, incentiveFee);
            }
        }

        if (riskAddress != address(0)) {
            uint256 riskFee = getRiskFee(totalAmount);
            if (riskFee > 0) {
                outgoing(feeOrder, riskAddress, riskFee);
            }
        }

    }

    function outgoing(FeeOrder storage feeOrder, address to, uint256 amount) internal {
        erc20.transfer(to, amount);
        FeeOutgoingEvent(feeOrder.orderId, to, amount);
        feeOrder.availableAmount = feeOrder.availableAmount.sub(amount);
    }


    function reject(uint256 orderId) onlyCaller external {
        toFunder(orderId);
    }

    function approve(uint256 orderId) onlyCaller external {
        toFunder(orderId);
    }

    function toFunder(uint256 orderId) internal {
        require(orderId > 0);

        FeeOrder storage feeOrder = feeOrders[orderIdIndex[orderId]];

        uint256 availableAmount = feeOrder.availableAmount;
        if (availableAmount > 0) {
            outgoing(feeOrder, fundAddress, availableAmount);
        }
    }

    function getFeeOrderById(uint256 _orderId) view external returns (
        uint256 orderId,
        address borrower,
        uint256 availableAmount,
        uint256 totalAmount)
    {
        FeeOrder storage feeOrder = innerGetOrder(_orderId);
        return (
        feeOrder.orderId,
        feeOrder.borrower,
        feeOrder.availableAmount,
        feeOrder.totalAmount
        );
    }

    function setMinFee(uint256 _minFee) onlyOwner public {
        minFee = _minFee;
    }

    function setFundAddress(address _fundAddress) onlyOwner public {
        fundAddress = _fundAddress;
    }

    function setRiskAddress(address _riskAddress) onlyOwner public {
        riskAddress = _riskAddress;
    }

    function setIncentiveAddress(address _incentiveAddress) onlyOwner public {
        incentiveAddress = _incentiveAddress;
    }

    function setRiskPercent(uint256 _riskPercent) onlyOwner public {
        require(_riskPercent <= feeRateDenominator);
        riskPercent = _riskPercent;
    }

    function setIncentivePercent(uint256 _incentivePercent) onlyOwner public {
        require(_incentivePercent <= feeRateDenominator);
        incentivePercent = _incentivePercent;
    }

    function getMinFee() external view returns (uint256){
        return minFee;
    }

}