pragma solidity ^0.4.18;

import './HasWallet.sol';

contract HasETH is HasWallet {

    function withdrawETH2Wallet(uint256 weiAmount) public onlyOwner {
        require(weiAmount > 0);
        require(wallet!=0);
        wallet.transfer(weiAmount);
    }
}