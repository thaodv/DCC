pragma solidity ^0.4.2;

import './MintableToken.sol';


/**
 * @title Capped token
 * @dev Mintable token with a token cap.
 */

contract CappedToken is MintableToken {

    uint256 public cap;

    function CappedToken(uint256 _cap) public {
        if (!(_cap > 0)) {
            throw;
        }
        cap = _cap;
    }

    /**
     * @dev Function to mint tokens
     * @param _to The address that will receive the minted tokens.
     * @param _amount The amount of tokens to mint.
     * @return A boolean that indicates if the operation was successful.
     */
    function mint(address _to, uint256 _amount) public returns (bool) {
        onlyOwner();
        canMint();
        if (!(totalSupply.add(_amount) <= cap)) {
            throw;
        }

        return super.mint(_to, _amount);
    }

}