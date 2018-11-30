pragma solidity ^0.4.2;

import "../juzixtoken/ERC20Basic.sol";
import "../ownership/OperatorPermission.sol";
import "../math/SafeMath.sol";
import "./TNLoanFee.sol";
contract TNLoanFeeService is OperatorPermission, TNLoanFee {

    using SafeMath for uint256;

    uint256 public minFee;

    address public caller;

    ERC20Basic public erc20;

    modifier onlyCaller{
        require(caller == msg.sender);
        _;
    }

    event FeeIncomingEvent(uint256 indexed amount);
    event FeeOutgoingEvent(address indexed to, uint256 amount);

    function TNLoanFeeService(address _caller, address erc20Address) public {
        require(_caller != address(0));
        require(erc20Address != address(0));
        caller = _caller;
        erc20 = ERC20Basic(erc20Address);
    }

    function receive(uint256 amount) external onlyCaller{
        incoming(amount);
    }

    function send(address to,uint256 amount) external onlyCaller{
        outgoing(to,amount);
    }


    function incoming(uint256 amount) internal {
        erc20.superTransfer(this, amount);
        FeeIncomingEvent(amount);
    }


    function outgoing(address to, uint256 amount) internal {
        erc20.transfer(to, amount);
        FeeOutgoingEvent(to, amount);
    }


    function setMinFee(uint256 _minFee) onlyOwner public {
        minFee = _minFee;
    }

    function getMinFee() external view returns (uint256){
        return minFee;
    }

    function setCaller(address _caller) onlyOwner public {
        require(_caller != address(0));
        caller = _caller;
    }

    function setErc20Address(address erc20Address) onlyOwner public {
        require(erc20Address != address(0));
        erc20 = ERC20Basic(erc20Address);
    }

    function balanceOf() view public returns (uint256) {
        return erc20.balanceOf(this);
    }

    function withdrawToken(address to,uint256 tokenAmount) onlyOwner public {
        require(tokenAmount > 0);
        require((to != address(0)));
        erc20.transfer(to, tokenAmount);
    }
}