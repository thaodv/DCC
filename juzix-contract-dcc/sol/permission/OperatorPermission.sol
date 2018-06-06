pragma solidity ^0.4.2;

import "./OwnerPermission.sol";

contract OperatorPermission is OwnerPermission {
    /**
      * 操作人
      */
    mapping(address => bool) public operators;

    event OperatorPermissionAdded(address indexed operatorAddress);

    event OperatorPermissionDeleted(address indexed operatorAddress);

    function onlyOperator() internal {
        if (!(operators[msg.sender] || msg.sender == owner) ) {
            log("!(operators[msg.sender] || meg.sender == owner)");
            throw;
        }
    }

    function addOperator(address operator)  {
        onlyOwner();
        operators[operator] = true;
        OperatorPermissionAdded(operator);
    }

    function deleteOperator(address operator)  {
        onlyOwner();
        delete operators[operator];
        OperatorPermissionDeleted(operator);
    }

}
