pragma solidity ^0.4.18;

import '../token/ParameterizedToken.sol';

contract MockToken is ParameterizedToken {

    function MockToken() public ParameterizedToken("高端币", "GDB", 18, 3000000000) {
    }

}