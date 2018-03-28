pragma solidity ^0.4.18;

import '../token/SafeERC20.sol';
import './BaseSale.sol';

contract Phase2Sale is BaseSale {

    using SafeERC20 for MintableToken;

    mapping(address => uint256) public withdrawedTokens;

    event TokenWithdrawed(address indexed beneficiary, uint256 amount);

    function validateWithdrawValue(address beneficiary, uint256 tokenAmount) internal {
        //数据校验
        require(beneficiary != address(0));
        require(tokenAmount > 0);
        require(purchasedTokens[beneficiary].sub(withdrawedTokens[beneficiary]) >= tokenAmount);
    }

    function updateWithdrawValue(address beneficiary, uint256 tokenAmount) internal {
        validateWithdrawValue(beneficiary, tokenAmount);
        //修改计数
        withdrawedTokens[beneficiary] = withdrawedTokens[beneficiary].add(tokenAmount);
    }

    function withdrawInternal(address beneficiary, uint256 tokenAmount) internal {
        updateWithdrawValue(beneficiary, tokenAmount);

        //变更币
        token.safeTransfer(beneficiary, tokenAmount);

        //发送事件
        TokenWithdrawed(beneficiary, tokenAmount);

    }

}
