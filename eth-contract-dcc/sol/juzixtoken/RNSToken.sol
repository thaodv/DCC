pragma solidity ^0.4.2;

import './ParameterizedToken.sol';

contract RNSToken is ParameterizedToken {

    function RNSToken() public ParameterizedToken("RNS测试", "RNS", 18, 10000000000) {}

}