pragma solidity ^0.4.18;

import '../sales/PrivateSale.sol';

contract MockSale is PrivateSale {

    function MockSale() public PrivateSale(
        msg.sender, //walletAddress
        0xeE2A06eF4281C7a4AA3B4c6bBC21354e7E87C26e, //tokenAddress
        0x843a638341CfDF4773e9961E8B059E958F1B2A01, //kycAndRateAddress
        (10 ** 18) * 100002, //_total
        (10 ** 18) * 0, //_lowerLimitWeiPerHand
        25, //_roundUnlockPercent
        60 * 60 * 24 * 60 //_roundCoolDown
    ) {
    }
}
