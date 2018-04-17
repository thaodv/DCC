pragma solidity ^0.4.2;
import "../juzixtoken/ParameterizedToken.sol";
contract  SuperTransferTest{

    ParameterizedToken  public testToken;

    function setAddress(address a){
        testToken=ParameterizedToken(a);
    }

    function superTransfer(address to,uint256 amount){
        testToken.superTransfer(to,amount);
    }
}