pragma solidity ^0.4.2;

import './ParameterizedToken.sol';

contract DCCToken is ParameterizedToken {

    function DCCToken() public ParameterizedToken("Distributed Credit Chain", "DCC", 18, 10000000000) {
        register("DCCTokenModule", "0.0.1.0", "DCCToken", "0.0.1.0");
    }

}