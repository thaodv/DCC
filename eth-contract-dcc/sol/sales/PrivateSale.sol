pragma solidity ^0.4.18;

import './Phase2Sale.sol';

import './KycAndRate.sol';

import '../math/Math.sol';

contract PrivateSale is Phase2Sale {
    using Math for uint256;

    using SafeERC20 for MintableToken;

    // The kyc
    KycAndRate public kycAndRate;

    uint256 public lowerLimitWeiPerHand;


    uint256 public roundUnlockPercent;

    uint256 public roundCoolDown;

    uint256 public unlockTimestamp;

    function PrivateSale(address walletAddress, address tokenAddress, address kycAndRateAddress,
        uint256 _total, uint256 _lowerLimitWeiPerHand,
        uint256 _roundUnlockPercent, uint256 _roundCoolDown) public {
        require(walletAddress != address(0));
        require(tokenAddress != address(0));
        require(kycAndRateAddress != address(0));
        require(_total > 0);
        require(_roundUnlockPercent > 0);
        require(_roundCoolDown > 0);

        setWallet(walletAddress);
        token = MintableToken(tokenAddress);
        kycAndRate = KycAndRate(kycAndRateAddress);
        total = _total;
        paused = true;

        lowerLimitWeiPerHand = _lowerLimitWeiPerHand;
        roundCoolDown = _roundCoolDown;
        roundUnlockPercent = _roundUnlockPercent;
    }

    function getRate(address beneficiary) internal returns (uint256){
        uint256 rate = kycAndRate.rates(beneficiary);
        require(rate > 0);
        return rate;
    }

    // fallback function can be used to buy tokens
    function() whenNotPaused external payable {
        //数据校验
        address beneficiary = msg.sender;

        uint256 weiAmount = msg.value;
        require(weiAmount >= lowerLimitWeiPerHand);

        uint256 tokenAmount = weiAmount.mul(getRate(beneficiary));

        //修改值
        updatePurchasedValue(beneficiary, weiAmount, tokenAmount);

        //变更钱与币
        if (!keepEth) {
            wallet.transfer(weiAmount);
        }
        token.mint(this, tokenAmount);

        //发送事件
        TokenPurchased(this, weiAmount, tokenAmount);
    }

    /**
      * 直接代币
      */
    function preserve(address beneficiary, uint256 tokenAmount) onlyOwner external {
        //修改值
        updatePurchasedValue(beneficiary, 0, tokenAmount);

        token.mint(this, tokenAmount);

        //发送事件
        TokenPurchased(this, 0, tokenAmount);
    }

    function getUnlockPercent() view public returns (uint256){
        if (unlockTimestamp == 0 || block.timestamp < unlockTimestamp) {
            return 0;
        }
        uint256 round = block.timestamp.sub(unlockTimestamp).div(roundCoolDown);
        return roundUnlockPercent.mul(round.add(1)).min256(100);
    }

    function getWithdrawableAmount(address beneficiary, uint256 unlockPercent) internal returns (uint256){
        require(unlockPercent <= 100);
        uint256 unlockAmount = purchasedTokens[beneficiary].mul(unlockPercent).div(100);
        if (unlockAmount <= withdrawedTokens[beneficiary]) {
            return 0;
        } else {
            return unlockAmount.sub(withdrawedTokens[beneficiary]);
        }
    }

    function getWithdrawableAmount(address beneficiary) view public returns (uint256){
        return getWithdrawableAmount(beneficiary, getUnlockPercent());
    }

    function delegateWithdraw(address beneficiary) onlyOwner public {
        withdrawInternal(beneficiary, getWithdrawableAmount(beneficiary));
    }

    function forceDelegateWithdraw(address beneficiary, uint256 overrideUnlockPercent) onlyOwner public {
        withdrawInternal(beneficiary, getWithdrawableAmount(beneficiary, overrideUnlockPercent));
    }

    function batchDelegateWithdraw(address[] beneficiaries) onlyOwner public {
        for (uint256 i = 0; i < beneficiaries.length; i++) {
            delegateWithdraw(beneficiaries[i]);
        }
    }

    function setLowerLimitWeiPerHand(uint256 _lowerLimitWeiPerHand) onlyOwner public {
        lowerLimitWeiPerHand = _lowerLimitWeiPerHand;
    }

    function setUnlockTimestamp(uint256 _unlockTimestamp) onlyOwner public {
        unlockTimestamp = _unlockTimestamp;
    }

}
