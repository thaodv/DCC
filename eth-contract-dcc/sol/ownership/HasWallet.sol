pragma solidity ^0.4.18;

import './Ownable.sol';

contract HasWallet is Ownable {
    address public wallet;

    function setWallet(address walletAddress) public onlyOwner {
        require(walletAddress != address(0));
        wallet = walletAddress;
    }
}
