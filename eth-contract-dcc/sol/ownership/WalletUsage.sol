pragma solidity ^0.4.18;

import './HasWallet.sol';

contract WalletUsage is HasWallet {


    /**
      * 合约自己是否保留eth.
      */
    bool public keepEth;


    /**
      * 为避免默认方法被占用，特别开指定方法接受以太坊
      */
    function depositEth() public payable {
    }

    function withdrawEth2Wallet(uint256 weiAmount) public onlyOwner {
        require(wallet != address(0));
        require(weiAmount > 0);
        wallet.transfer(weiAmount);
    }

    function setKeepEth(bool _keepEth) public onlyOwner {
        keepEth = _keepEth;
    }

}
