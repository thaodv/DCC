pragma solidity ^0.4.2;

import "./OwnerPermission.sol";

contract HasWallet is OwnerPermission {
    address public wallet;

    function setWallet(address walletAddress) public {
        onlyOwner();
        if(!(walletAddress != address(0))){
           log("!(walletAddress != address(0))");
           throw;
        }
        wallet = walletAddress;
    }


}
