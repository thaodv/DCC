pragma solidity ^0.4.18;

import './StandardToken.sol';

/**
 * @title Mintable token
 * @dev Simple ERC20 Token example, with mintable token creation
 * @dev Issue: * https://github.com/OpenZeppelin/zeppelin-solidity/issues/120
 * Based on code by TokenMarketNet: https://github.com/TokenMarketNet/ico/blob/master/contracts/MintableToken.sol
 */

contract MintableToken is StandardToken {
    event Mint(address indexed to, uint256 amount);

    event MintFinished();

    event Burn(address minter, uint256 amount);

    bool public mintingFinished = false;

    mapping(address => bool) public minters;

    modifier canMint() {
        require(!mintingFinished);
        _;
    }
    modifier onlyMinters() {
        require(minters[msg.sender] || msg.sender == owner);
        _;
    }
    function addMinter(address _addr) public onlyOwner {
        minters[_addr] = true;
    }

    function deleteMinter(address _addr) public onlyOwner {
        delete minters[_addr];
    }

    //销毁币
    function burn(uint256 amount) onlyMinters public returns (bool){
        //校验
        require(amount > 0);
        require(totalSupply >= amount);
        require(balances[msg.sender] >= amount);
        //将msg.sender的币销毁
        balances[msg.sender] = balances[msg.sender].sub(amount);
        //币总量减少
        totalSupply = totalSupply.sub(amount);

        Burn(msg.sender, amount);

        return true;
    }

    /**
     * @dev Function to mint tokens
     * @param _to The address that will receive the minted tokens.
     * @param _amount The amount of tokens to mint.
     * @return A boolean that indicates if the operation was successful.
     */
    function mint(address _to, uint256 _amount) onlyMinters canMint public returns (bool) {
        require(_to != address(0));
        totalSupply = totalSupply.add(_amount);
        balances[_to] = balances[_to].add(_amount);
        Mint(_to, _amount);
        Transfer(address(0), _to, _amount);
        return true;
    }

    /**
     * @dev Function to stop minting new tokens.
     * @return True if the operation was successful.
     */
    function finishMinting() onlyOwner canMint public returns (bool) {
        mintingFinished = true;
        MintFinished();
        return true;
    }
}