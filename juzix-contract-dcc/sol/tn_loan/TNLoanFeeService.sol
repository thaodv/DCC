pragma solidity ^0.4.2;

import "../juzixtoken/ERC20Basic.sol";
import "../permission/OperatorPermission.sol";
import "../math/SafeMath.sol";
import "./TNLoanFee.sol";

contract TNLoanFeeService is OperatorPermission, TNLoanFee {

    using SafeMath for uint256;

    uint256 public minFee;

    address public caller;

    ERC20Basic public erc20;

    function onlyCaller(){
        if(!(caller == msg.sender)){
            log("!(caller == msg.sender)");
            throw;
        }
    }

    event FeeIncomingEvent(uint256 indexed amount);
    event FeeOutgoingEvent(address indexed to, uint256 amount);

    function TNLoanFeeService(address _caller, address erc20Address) public {
        register("TNLoanFeeServiceModule", "0.0.1.0", "TNLoanFeeService", "0.0.1.0");
        if(!(_caller != address(0))){
            log("!(_caller != address(0))");
            throw;
        }
        if(!(erc20Address != address(0))){
            log("!(erc20Address != address(0))");
            throw;
        }
        caller = _caller;
        erc20 = ERC20Basic(erc20Address);
    }

    function receive(uint256 amount) public {
        onlyCaller();
        incoming(amount);
    }

    function send(address to, uint256 amount) public {
        onlyCaller();
        outgoing(to, amount);
    }


    function incoming(uint256 amount) internal {
        erc20.superTransfer(this, amount);
        FeeIncomingEvent(amount);
    }


    function outgoing(address to, uint256 amount) internal {
        erc20.transfer(to, amount);
        FeeOutgoingEvent(to, amount);
    }


    function setMinFee(uint256 _minFee) public {
        onlyOwner();
        minFee = _minFee;
    }

    function getMinFee() public constant returns (uint256){
        return minFee;
    }

    function setCaller(address _caller) public {
        onlyOwner();
        if(!(_caller != address(0))){
            log("!(_caller != address(0))");
            throw;
        }
        caller = _caller;
    }

    function setErc20Address(address erc20Address) public {
        onlyOwner();
        if(!(erc20Address != address(0))){
            log("!(erc20Address != address(0))");
            throw;
        }
        erc20 = ERC20Basic(erc20Address);
    }

    function balanceOf() constant public returns (uint256) {
        return erc20.balanceOf(this);
    }

    function withdrawToken(address to, uint256 tokenAmount) public {
        onlyOwner();
        if(!(tokenAmount > 0)){
            log("!(tokenAmount > 0)");
            throw;
        }
        if(!((to != address(0)))){
            log("!((to != address(0)))");
            throw;
        }
        erc20.transfer(to, tokenAmount);
    }
}