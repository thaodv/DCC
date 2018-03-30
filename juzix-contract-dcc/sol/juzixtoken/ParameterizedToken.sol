pragma solidity ^0.4.2;

import './CappedToken.sol';

contract ParameterizedToken is CappedToken {
    string public version = "1.3";

    string public name;

    string public symbol;

    uint256 public decimals;

    function ParameterizedToken(string _name, string _symbol, uint256 _decimals, uint256 _capIntPart) public CappedToken(_capIntPart * 10 ** _decimals) {
        //register("ParameterizedTokenModule", "0.0.1.0", "ParameterizedTokenModule", "0.0.1.0");
        name = _name;
        symbol = _symbol;
        decimals = _decimals;
    }

}