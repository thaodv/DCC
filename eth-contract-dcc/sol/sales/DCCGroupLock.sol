pragma solidity ^0.4.18;

import '../ownership/HasToken.sol';

import '../math/Math.sol';
import '../math/SafeMath.sol';

contract DCCGroupLock is HasToken {
    using Math for uint256;

    using SafeMath for uint256;

    using SafeERC20 for ERC20;

    mapping(address => uint256) public preservedTokens;

    mapping(address => uint256) public withdrawedTokens;

    uint256 public totalPreservedToken;

    uint256 public totalWithdrawedToken;

    uint256 public roundUnlockTimes;

    uint256 public roundCoolDown;

    uint256 public unlockTimestamp;

    string public name;

    function DCCGroupLock(string _name, address walletAddress, address tokenAddress,
        uint256 _roundUnlockTimes, uint256 _roundCoolDown) public {
        require(walletAddress != address(0));
        require(tokenAddress != address(0));
        require(_roundUnlockTimes > 0);
        require(_roundCoolDown > 0);

        name = _name;
        wallet = walletAddress;
        token = ERC20(tokenAddress);
        roundCoolDown = _roundCoolDown;
        roundUnlockTimes = _roundUnlockTimes;
    }

    function addMember(address memberAddress, uint256 preservedToken) onlyOwner public {
        require(memberAddress != address(0));
        require(preservedToken > 0);
        require(preservedTokens[memberAddress] == 0);
        //没有重复
        preservedTokens[memberAddress] = preservedToken;
        totalPreservedToken = totalPreservedToken.add(preservedToken);
    }

    function getSpareToken() view public returns (uint256){
        uint256 actualHold = token.balanceOf(this);
        return actualHold.sub(totalPreservedToken.sub(totalWithdrawedToken));
    }

    function getRound() view public returns (uint256){
        if (unlockTimestamp == 0 || block.timestamp < unlockTimestamp) {
            return 0;
        }
        return block.timestamp.sub(unlockTimestamp).div(roundCoolDown).add(1).min256(roundUnlockTimes);
    }

    function getUnlockAmount(address memberAddress) view public returns (uint256){
        require(memberAddress != address(0));
        uint256 round = getRound();
        if (round == 0) {
            return 0;
        }
        return preservedTokens[memberAddress].mul(round).div(roundUnlockTimes);

    }

    function getWithdrawableAmount(address memberAddress) view public returns (uint256){
        return getUnlockAmount(memberAddress).sub(withdrawedTokens[memberAddress]);
    }

    function batchWithdraw(address[] _memberAddressArray) public {
        for (uint256 i = 0; i < _memberAddressArray.length; i++) {
            withdraw(_memberAddressArray[i]);
        }
    }

    function withdraw(address memberAddress) public {
        uint256 tokenAmount = getWithdrawableAmount(memberAddress);
        require(tokenAmount > 0);

        withdrawedTokens[memberAddress] = withdrawedTokens[memberAddress].add(tokenAmount);
        totalWithdrawedToken = totalWithdrawedToken.add(tokenAmount);

        //变更币
        token.safeTransfer(memberAddress, tokenAmount);

    }

    function setUnlockTimestamp(uint256 _unlockTimestamp) onlyOwner public {
        unlockTimestamp = _unlockTimestamp;
    }


}
