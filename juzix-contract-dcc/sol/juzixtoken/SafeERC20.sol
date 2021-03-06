pragma solidity ^0.4.2;

import './ERC20Basic.sol';
import './ERC20.sol';

/**
 * @title SafeERC20
 * @dev Wrappers around ERC20 operations that throw on failure.
 * To use this library you can add a `using SafeERC20 for ERC20;` statement to your contract,
 * which allows you to call the safe operations as `token.safeTransfer(...)`, etc.
 */
library SafeERC20 {
    function safeTransfer(ERC20Basic token, address to, uint256 value) internal {
        if (!(token.transfer(to, value))) {
            throw;
        }
    }

    function safeTransferFrom(ERC20 token, address from, address to, uint256 value) internal {
        if (!(token.transferFrom(from, to, value))) {
            throw;
        }
    }

    function safeApprove(ERC20 token, address spender, uint256 value) internal {
        if (!(token.approve(spender, value))) {
            throw;
        }
    }
}