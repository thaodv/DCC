pragma solidity ^0.4.18;

import '../sales/PrivateSale.sol';

contract ProdSale is PrivateSale {

    function ProdSale() public PrivateSale(
        0xffc76f9e43ee85cbeb837dd63741ce24ef03205d, //walletAddress
        0x3E41C0eb7026914EF15b4aA5Fc25f5570a1811bB, //tokenAddress
        0x2395e6cd89c702512de3f801ca3cdf95ec9f7901, //kycAndRateAddress
        (10 ** 18) * 2500000000, //_total
        (10 ** 18) * 100, //_lowerLimitWeiPerHand
        25, //_roundUnlockPercent
        60 * 60 * 24 * 60 //_roundCoolDown
    ) {
    }
}
