pragma solidity ^0.4.2;

import "./OwnerPermission.sol";

contract OperatorPermission is OwnerPermission {
    /**
      * 操作人
      */
    mapping(address => bool) public operators;

    function onlyOperator() internal {
        if (!(operators[msg.sender] || msg.sender == owner) ) {
            log("!(operators[msg.sender] || meg.sender == owner)");
            throw;
        }
    }

    function addOperator(address operator)  {
        onlyOwner();
        operators[operator] = true;
    }

    function deleteOperator(address operator)  {
        onlyOwner();
        delete operators[operator];
    }

}
