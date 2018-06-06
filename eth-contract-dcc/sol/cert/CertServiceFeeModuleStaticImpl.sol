pragma solidity ^0.4.11;
import "./CertServiceFeeModule.sol";
import "../juzixtoken/ERC20Basic.sol";
import "../ownership/HasWallet.sol";
contract CertServiceFeeModuleStaticImpl  is CertServiceFeeModule ,HasWallet{

    uint256  public  fee;

    ERC20Basic public erc20;

    address public caller;

    bytes  public  name;

     modifier onlycaller(){
        require(msg.sender==caller);
        _;
    }

    function CertServiceFeeModuleStaticImpl(bytes _name,address  erc20Address,address callerAddress) public{
        name=_name;
        erc20=ERC20Basic(erc20Address);
        caller=callerAddress;
    }

    function  apply() public  onlycaller returns(uint256){
        require(wallet!=address(0));
        if(fee>0){
            erc20.superTransfer(wallet,fee);
        }
        return fee;
    }

    function  getFee()view  public returns(uint256){
        return fee;
    }

    function setFee(uint256 _fee) public onlyOwner{
        fee=_fee;
    }

    function setErc20Address(address erc20Address) onlyOwner public {
        require(erc20Address != address(0));
        erc20 = ERC20Basic(erc20Address);
    }

    function  refund() public onlycaller{}
    function  settle() public onlycaller{}
}