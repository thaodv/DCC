pragma solidity  ^0.4.2;
import "../sysbase/OwnerNamed.sol";
contract TestEvent is OwnerNamed{

    function f1(){
       bytes32  a="abc";
       a=a>>2;
    }



}