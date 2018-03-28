pragma solidity ^0.4.18;

import '../token/SafeERC20.sol';
import '../token/ERC20.sol';
import './HasWallet.sol';

contract HasToken is HasWallet {

    using SafeERC20 for ERC20;

    ERC20 public token;

    function withdrawToken2Wallet(uint256 tokenAmount) public onlyOwner {
        require(tokenAmount > 0);
        token.safeTransfer(wallet, tokenAmount);
    }
}
