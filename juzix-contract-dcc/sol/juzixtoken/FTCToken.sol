pragma solidity ^0.4.2;

import './ParameterizedToken.sol';

contract FTCToken is ParameterizedToken {

    function FTCToken() public ParameterizedToken("FTC", "FTC", 18, 1000000000) {
        register("FTCTokenModule", "0.0.1.0", "FTCToken", "0.0.1.0");
    }

}