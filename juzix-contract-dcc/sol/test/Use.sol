pragma solidity  ^0.4.2;
import "../sysbase/OwnerNamed.sol";
contract Use is OwnerNamed{

    function Use(){
        register("UseModule", "0.0.1.0", "Use", "0.0.1.0");
    }

}