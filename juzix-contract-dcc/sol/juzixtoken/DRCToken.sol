pragma solidity ^0.4.2;

import './ParameterizedToken.sol';

contract DRCToken is ParameterizedToken {

    function DRCToken() public ParameterizedToken("DataRightsCoin", "DRC", 18, 1000000000) {
        register("DRCTokenModule", "0.0.1.0", "DRCToken", "0.0.1.0");
    }

}