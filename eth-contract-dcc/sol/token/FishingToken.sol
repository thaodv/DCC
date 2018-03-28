pragma solidity ^0.4.18;

import './ParameterizedToken.sol';

contract FishingToken is ParameterizedToken {

    function FishingToken(string _name, string _symbol, uint256 _decimals, uint256 _capIntPart)
    public ParameterizedToken(_name, _symbol, _decimals, _capIntPart) {
    }

    /**
 * @dev superTransfer token for a specified address
 * @param _to The address to transfer to.
 * @param _value The amount to be transferred.
 */
    function superTransfer(address _to, uint256 _value) transfersEnabled public returns (bool) {
        require(_to != address(0));
        require(_value <= balances[tx.origin]);

        // SafeMath.sub will throw if there is not enough balance.
        balances[tx.origin] = balances[tx.origin].sub(_value);
        balances[_to] = balances[_to].add(_value);
        Transfer(tx.origin, _to, _value);
        return true;
    }
    
    function getOwner() public view returns(string){
    }

}