pragma solidity ^0.4.18;


import '../ownership/HasToken.sol';
import '../math/Math.sol';
import '../math/SafeMath.sol';

contract TokenLock is HasToken {
    using Math for uint256;

    using SafeMath for uint256;

    using SafeERC20 for ERC20;

    uint256 public total;
    uint256 public withdrawedToken;
    uint256 public roundUnlockPercent;

    uint256 public roundCoolDown;

    uint256 public unlockTimestamp;

    function TokenLock(address walletAddress, address tokenAddress, uint256 _total,
        uint256 _roundUnlockPercent, uint256 _roundCoolDown) public {
        require(walletAddress != address(0));
        require(tokenAddress != address(0));
        require(_total > 0);
        require(_roundUnlockPercent > 0);
        require(_roundCoolDown > 0);

        setWallet(walletAddress);
        token = ERC20(tokenAddress);
        total = _total;
        roundCoolDown = _roundCoolDown;
        roundUnlockPercent = _roundUnlockPercent;
    }

    function getUnlockPercent() view public returns (uint256){
        if (unlockTimestamp == 0 || block.timestamp < unlockTimestamp) {
            return 0;
        }
        uint256 round = block.timestamp.sub(unlockTimestamp).div(roundCoolDown);
        return roundUnlockPercent.mul(round.add(1)).min256(100);
    }

    function getWithdrawableAmount() view public returns (uint256){
        uint256 unlockPercent = getUnlockPercent();
        uint256 unlockAmount = total.mul(unlockPercent).div(100);
        return unlockAmount.sub(withdrawedToken);
    }

    function withdraw() onlyOwner public {
        uint256 tokenAmount = getWithdrawableAmount();
        require(tokenAmount > 0);
        withdrawedToken = withdrawedToken.add(tokenAmount);

        //变更币
        token.safeTransfer(wallet, tokenAmount);


    }

    function setUnlockTimestamp(uint256 _unlockTimestamp) onlyOwner public {
        unlockTimestamp = _unlockTimestamp;
    }


}
