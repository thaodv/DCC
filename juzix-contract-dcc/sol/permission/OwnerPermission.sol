pragma solidity ^0.4.2;

import "../sysbase/OwnerNamed.sol";

contract OwnerPermission is OwnerNamed {

    function onlyOwner() internal {
        if (msg.sender != owner) {
            log("msg.sender != owner");
            throw;
        }
    }

    event OwnershipTransferred(address indexed previousOwner, address indexed newOwner);
    /**
     * @dev Allows the current owner to transfer control of the contract to a newOwner.
     * @param newOwner The address to transfer ownership to.
     */
    function transferOwnership(address newOwner) public {
        log("transferOwnership");
        onlyOwner();
        if(newOwner == address(0)){
            log("newOwner == address(0)");
            throw;
        }
        owner = newOwner;
        OwnershipTransferred(owner, newOwner);

    }
}
