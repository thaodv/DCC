pragma solidity ^0.4.18;

import '../sales/TokenLock.sol';

contract MockLock is TokenLock {

    function MockLock() public TokenLock(
        msg.sender, //walletAddress
        0xeE2A06eF4281C7a4AA3B4c6bBC21354e7E87C26e, //tokenAddress
        (10 ** 18) * 132, //_total
        25, //_roundUnlockPercent
        60 * 60 * 24 * 60 //_roundCoolDown
    ) {
    }

}