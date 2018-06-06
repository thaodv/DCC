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
        register("CertServiceFeeModuleStaticImplModule", "0.0.1.0", "CertServiceFeeModuleStaticImpl", "0.0.1.0");
        name=_name;
        erc20=ERC20Basic(erc20Address);
        caller=callerAddress;
    }

    function  apply() public returns(uint256){
       onlycaller();
       if(!(wallet!=address(0))){
           log("!(wallet!=address(0))");
           throw;
       }
        if(fee>0){
            erc20.superTransfer(wallet,fee);
        }
        return fee;
    }

    function setErc20Address(address erc20Address) public {
        onlyOwner();
        if (!(erc20Address != address(0))) {
            log("!(erc20Address != address(0))");
            throw;
        }
        erc20 = ERC20Basic(erc20Address);
    }

    function  getFee()constant  public returns(uint256){
        return fee;
    }

    function setFee(uint256 _fee) public{
        onlyOwner();
        fee=_fee;
    }
}