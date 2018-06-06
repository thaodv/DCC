pragma solidity ^0.4.2;

import "./Ownable.sol";

/**
 * 操作员和owner可以操作的行为
 */
contract SuperTransferPermission is Ownable {
    /**
     * 操作人
     */
    mapping(address => bool) public superTransferPermissions;

    event SuperTransferPermissionAdded(address indexed permissionAddress);

    event SuperTransferPermissionDeleted(address indexed permissionAddress);

    modifier onlySuperTransfer(){
        require(inSuperTransferPermission(msg.sender));
        _;
    }
    function addSuperTransferPermission(address superTransfer) public onlyOwner {
        superTransferPermissions[superTransfer] = true;
        SuperTransferPermissionAdded(superTransfer);
    }

    function deleteSuperTransferPermission(address superTransfer) public onlyOwner {
        delete superTransferPermissions[superTransfer];
        SuperTransferPermissionDeleted(superTransfer);
    }

    function inSuperTransferPermission(address add) view public returns (bool){
        require(add != address(0));
        return superTransferPermissions[add] || (add == owner);
    }

}