pragma solidity ^0.4.2;

import "./OwnerPermission.sol";

/**
 * 操作员和owner可以操作的行为
 */
contract SuperTransferPermission is OwnerPermission {
    /**
      * 操作人
      */
    mapping(address => bool) public superTransferPermissions;

    event SuperTransferPermissionAdded(address indexed permissionAddress);

    event SuperTransferPermissionDeleted(address indexed permissionAddress);

    function onlySuperTransfer() public {
        if (!(inSuperTransferPermission(msg.sender))) {
            log("!(inSuperTransferPermission(msg.sender))");
            throw;
        }
    }

    function addSuperTransferPermission(address superTransfer) public {
        onlyOwner();
        superTransferPermissions[superTransfer] = true;
        SuperTransferPermissionAdded(superTransfer);
    }

    function deleteSuperTransferPermission(address superTransfer) public {
        onlyOwner();
        delete superTransferPermissions[superTransfer];
        SuperTransferPermissionDeleted(superTransfer);
    }

    function inSuperTransferPermission(address addr) constant public returns (bool){
        if (!(addr != address(0))) {
            log("!(addr!=address(0))");
            throw;
        }
        return superTransferPermissions[addr] || (addr == owner);
    }
}