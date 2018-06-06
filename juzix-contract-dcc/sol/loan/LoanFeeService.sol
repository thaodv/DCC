pragma solidity ^0.4.2;

import "../juzixtoken/ERC20Basic.sol";
import "../permission/OperatorPermission.sol";
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

    function onlyCaller(){
        if (!(caller == msg.sender)) {
            log("!(caller == msg.sender)");
            throw;
        }
    }

    event FeeIncomingEvent(uint256 indexed orderId, address indexed from, uint256 amount);
    event FeeOutgoingEvent(uint256 indexed orderId, address indexed to, uint256 amount);

    function LoanFeeService(address _caller, address erc20Address) public {
        register("LoanFeeServiceModule", "0.0.1.0", "LoanFeeService", "0.0.1.0");
        if (!(_caller != address(0))) {
            log("!(_caller != address(0))");
            throw;
        }

        if (!(erc20Address != address(0))) {
            log("!(erc20Address != address(0))");
            throw;
        }
        caller = _caller;
        erc20 = ERC20Basic(erc20Address);
        feeOrders.push(FeeOrder(0, address(0), 0, 0));
    }

    function insertOrder(uint256 orderId, address borrower, uint256 availableAmount, uint256 totalAmount)
    internal {
        if (!(orderIdIndex[orderId] == 0)) {
            log("!(orderIdIndex[orderId] == 0)");
            throw;
        }
        uint256 newFeeOrderId = feeOrders.push(FeeOrder(orderId, borrower, availableAmount, totalAmount)) - 1;
        orderIdIndex[orderId] = newFeeOrderId;
    }

    function apply(uint256 orderId, address borrower, uint256 totalAmount) external {
        onlyCaller();
        if (!(orderId > 0)) {
            log("!(orderId > 0)");
            throw;
        }

        if (!(totalAmount >= minFee)) {
            log("!(totalAmount >= minFee)");
            throw;
        }

        if (!(borrower != address(0))) {
            log("!(borrower != address(0))");
            throw;
        }
        insertOrder(orderId, borrower, totalAmount, totalAmount);
        if (totalAmount > 0) {
            incoming(orderId, totalAmount);
        }
    }

    function incoming(uint256 orderId, uint256 amount) internal {
        erc20.superTransfer(this, amount);
        FeeIncomingEvent(orderId, tx.origin, amount);
    }

    function cancel(uint256 orderId) external {
        onlyCaller();
        FeeOrder storage feeOrder = innerGetOrder(orderId);
        uint256 availableAmount = feeOrder.availableAmount;
        if (availableAmount > 0) {
            outgoing(feeOrder, feeOrder.borrower, availableAmount);
        }
    }

    function innerGetOrder(uint256 orderId) internal returns (FeeOrder storage feeOrder){
        if (!(orderId > 0)) {
            log("!(orderId > 0)");
            throw;
        }
        feeOrder = feeOrders[orderIdIndex[orderId]];
        if (!(feeOrder.orderId > 0)) {
            log("!(feeOrder.orderId > 0)");
            throw;
        }
        return feeOrder;
    }

    function getIncentiveFee(uint256 _totalAmount) constant internal returns (uint256 _fee){
        return _totalAmount.mul(incentivePercent).div(feeRateDenominator);
    }

    function getRiskFee(uint256 _totalAmount) constant internal returns (uint256 _fee){
        return _totalAmount.mul(riskPercent).div(feeRateDenominator);
    }

    function audit(uint256 orderId) external {
        onlyCaller();
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


    function reject(uint256 orderId) external {
        onlyCaller();
        toFunder(orderId);
    }

    function approve(uint256 orderId) external {
        onlyCaller();
        toFunder(orderId);
    }

    function toFunder(uint256 orderId) internal {
        if (!(orderId > 0)) {
            log("!(orderId > 0)");
            throw;
        }
        FeeOrder storage feeOrder = feeOrders[orderIdIndex[orderId]];

        uint256 availableAmount = feeOrder.availableAmount;
        if (availableAmount > 0) {
            outgoing(feeOrder, fundAddress, availableAmount);
        }
    }

    function getFeeOrderById(uint256 _orderId) constant external returns (
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

    function setMinFee(uint256 _minFee) public {
        onlyOwner();
        minFee = _minFee;
    }

    function setFundAddress(address _fundAddress) public {
        onlyOwner();
        fundAddress = _fundAddress;
    }

    function setRiskAddress(address _riskAddress) public {
        onlyOwner();
        riskAddress = _riskAddress;
    }

    function setIncentiveAddress(address _incentiveAddress) public {
        onlyOwner();
        incentiveAddress = _incentiveAddress;
    }

    function setRiskPercent(uint256 _riskPercent) public {
        onlyOwner();
        if (!(_riskPercent <= feeRateDenominator)) {
            log("!(_riskPercent <= feeRateDenominator)");
            throw;
        }
        riskPercent = _riskPercent;
    }

    function setIncentivePercent(uint256 _incentivePercent) public {
        onlyOwner();
        if (!(_incentivePercent <= feeRateDenominator)) {
            log("!(_incentivePercent <= feeRateDenominator)");
            throw;
        }
        incentivePercent = _incentivePercent;
    }

    function getMinFee() external constant returns (uint256){
        return minFee;
    }

    function setCaller(address _caller) public {
        onlyOwner();
        if (!(_caller != address(0))) {
            log("!(_caller != address(0))");
            throw;
        }
        caller = _caller;
    }

    function setErc20Address(address erc20Address) public {
        onlyOwner();
        if (!(erc20Address != address(0))) {
            log("!(erc20Address != address(0))");
            throw;
        }
        erc20 = ERC20Basic(erc20Address);
    }

    function balanceOf() constant public returns (uint256) {
        return erc20.balanceOf(this);
    }

    function withdrawToken(uint256 tokenAmount, address to) public {
        onlyOwner();
        if (!(tokenAmount > 0)) {
            log("!(tokenAmount > 0)");
            throw;
        }
        if (!(to != 0)) {
            log("!(to != 0)");
            throw;
        }
        erc20.transfer(to, tokenAmount);
    }
}