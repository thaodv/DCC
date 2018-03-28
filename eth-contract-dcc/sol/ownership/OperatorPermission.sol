pragma solidity ^0.4.11;

import "./Ownable.sol";

/**
 * 操作员和owner可以操作的行为
 */
contract OperatorPermission is Ownable {
    /**
     * 操作人
     */
    mapping(address => bool) public operators;

    modifier onlyOperator() {
        require(operators[msg.sender] || msg.sender == owner);
        _;
    }
    function addOperator(address operator) public onlyOwner {
        operators[operator] = true;
    }

    function deleteOperator(address operator) public onlyOwner {
        delete operators[operator];
    }

}
