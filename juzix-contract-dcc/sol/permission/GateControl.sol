pragma solidity ^0.4.0;

import "./OwnerPermission.sol";

contract GateControl is OwnerPermission {
    bool public openFlag = true;

    function GateControl(){
    }

    /**
     *
     */
    function onlyGateOpen() {
       if(!(openFlag)){
          log("!(openFlag))");
          throw;
       }
    }

    function isOpen() public constant returns (bool){
        return openFlag;
    }

    function openGate() public {
        onlyOwner();
        if (!openFlag) {
            openFlag = true;
            GateSwitched(openFlag);
        }

    }

    function closeGate() public  {
        onlyOwner();
        if (openFlag) {
            openFlag = false;
            GateSwitched(openFlag);
        }
    }

    event GateSwitched(bool openFlag);

}
