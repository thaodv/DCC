pragma solidity ^0.4.2;

import '../juzixtoken/SafeERC20.sol';
import '../juzixtoken/ERC20.sol';
import './HasWallet.sol';

contract HasToken is HasWallet {

    using SafeERC20 for ERC20;

    ERC20 public token;

    function withdrawToken2Wallet(uint256 tokenAmount) public  {
        onlyOwner();
        if(!(tokenAmount > 0)){
            log("!(tokenAmount > 0)");
            throw;
        }
        token.safeTransfer(wallet, tokenAmount);
    }
}
