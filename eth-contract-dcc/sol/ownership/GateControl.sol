pragma solidity ^0.4.0;

import "./Ownable.sol";

contract GateControl is Ownable {
    bool public openFlag = true;

    function GateControl(){
    }

    /**
     *
     */
    modifier onlyGateOpen() {
        require(openFlag);
        _;
    }

    function isOpen() public view returns (bool){
        return openFlag;
    }

    function openGate() public onlyOwner {
        if (!openFlag) {
            openFlag = true;
            GateSwitched(openFlag);
        }

    }

    function closeGate() public onlyOwner {
        if (openFlag) {
            openFlag = false;
            GateSwitched(openFlag);
        }
    }

    event GateSwitched(bool openFlag);

}
