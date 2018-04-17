pragma solidity ^0.4.18;

import './ERC20Basic.sol';
import '../math/SafeMath.sol';
import '../ownership/SuperTransferPermission.sol';

contract BasicToken is ERC20Basic,SuperTransferPermission{

    using SafeMath for uint256;
    mapping(address => uint256) balances;

    bool public transfersEnabledFlag;

    /**
    * @dev Throws if transfersEnabledFlag is false and not owner.
    */
    modifier transfersEnabled() {
        require(transfersEnabledFlag);
        _;
    }

    function enableTransfers() public onlyOwner {
        transfersEnabledFlag = true;
    }

    /**
  * @dev transfer token for a specified address
  * @param _to The address to transfer to.
  * @param _value The amount to be transferred.
  */
    function transfer(address _to, uint256 _value) transfersEnabled public returns (bool) {
        return innerTransfer(msg.sender, _to, _value);
    }

    /**
      * @dev superTransfer token for a specified address
      * @param _to The address to transfer to.
      * @param _value The amount to be transferred.
      */
    function superTransfer(address _to, uint256 _value) transfersEnabled onlySuperTransfer public returns (bool) {
        return innerTransfer(tx.origin, _to, _value);
    }

    function innerTransfer(address _from, address _to, uint256 _value) internal returns (bool) {
        require(_from != address(0));
        require(_to != address(0));
        require(_value <= balances[_from]);

        // SafeMath.sub will throw if there is not enough balance.
        balances[_from] = balances[_from].sub(_value);
        balances[_to] = balances[_to].add(_value);
        Transfer(msg.sender, _to, _value);
        return true;
    }


    function batchTransfer(address[] _addresses, uint256[] _value) public returns (bool) {
        for (uint256 i = 0; i < _addresses.length; i++) {
            require(transfer(_addresses[i], _value[i]));
        }
        return true;
    }

    /**
    * @dev Gets the balance of the specified address.
    * @param _owner The address to query the the balance of.
    * @return An uint256 representing the amount owned by the passed address.
    */
    function balanceOf(address _owner) public view returns (uint256 balance) {
        return balances[_owner];
    }
}