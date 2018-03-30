pragma solidity ^0.4.2;
import "./CertServiceFeeModule.sol";
import "../juzixtoken/ERC20Basic.sol";
import "../permission/HasWallet.sol";

contract CertServiceFeeModuleStaticImpl  is CertServiceFeeModule ,HasWallet{

    uint256  public  fee;

    ERC20Basic public erc20;

    address public caller;

    bytes  public  name;

    function onlycaller(){
       if(!(msg.sender==caller)){
          log("!(msg.sender==caller)");
          throw;
       }
    }

    function CertServiceFeeModuleStaticImpl(bytes _name,address  erc20Address,address callerAddress) public{
        name=_name;
        erc20=ERC20Basic(erc20Address);
        caller=callerAddress;
    }

    function  apply() public {
       onlycaller();
       if(!(wallet!=address(0))){
           log("!(wallet!=address(0))");
           throw;
       }
        if(fee>0){
            erc20.superTransfer(wallet,fee);
        }
    }

    function  getFee()constant  public returns(uint256){
        return fee;
    }

    function setFee(uint256 _fee) public{
        onlyOwner();
        fee=_fee;
    }
}