pragma solidity ^0.4.18;

import '../juzixtoken/ParameterizedToken.sol';

contract FtcTokenTest is ParameterizedToken {

    function FtcTokenTest() public ParameterizedToken("FTC测试", "FTC", 18, 3000000000) {
    }

}