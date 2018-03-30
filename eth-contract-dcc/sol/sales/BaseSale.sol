pragma solidity ^0.4.18;

import '../ownership/Ownable.sol';
import '../lifecycle/Pausable.sol';
import '../ownership/HasMintableToken.sol';
import '../ownership/WalletUsage.sol';

contract BaseSale is Pausable, HasMintableToken, WalletUsage {
    using SafeMath for uint256;

    /**
     * 总共可销售token
     */
    uint256 public total;

    /**
     * 已出售token
     */
    uint256 public tokenSold;

    // amount of raised money in wei
    uint256 public weiRaised;

    mapping(address => uint256) public purchasedTokens;

    mapping(address => uint256) public costs;

    /**
     * event for token purchase logging
     * @param beneficiary who got the tokens
     * @param value weis paid for purchase
     * @param amount amount of tokens purchased
     */
    event TokenPurchased(address indexed beneficiary, uint256 value, uint256 amount);

    function getTokenOnSale() view public returns (uint256){
        return total.sub(tokenSold);
    }

    function validatePurchasedValue(address beneficiary, uint256 tokenAmount) internal {
        //数据校验
        require(beneficiary != address(0));
        require(tokenAmount > 0);
        require(getTokenOnSale() >= tokenAmount);
    }

    function updatePurchasedValue(address beneficiary, uint256 weiAmount, uint256 tokenAmount) internal {
        //修改计数
        purchasedTokens[beneficiary] = purchasedTokens[beneficiary].add(tokenAmount);
        tokenSold = tokenSold.add(tokenAmount);
        if (weiAmount > 0) {
            costs[beneficiary] = costs[beneficiary].add(weiAmount);
            weiRaised = weiRaised.add(weiAmount);
        }
    }


}
