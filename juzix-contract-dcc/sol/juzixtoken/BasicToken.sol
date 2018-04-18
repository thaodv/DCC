pragma solidity ^0.4.2;

import './ERC20Basic.sol';
import '../math/SafeMath.sol';

import "../permission/SuperTransferPermission.sol";

contract BasicToken is ERC20Basic,SuperTransferPermission{

    using SafeMath for uint256;
    mapping(address => uint256) balances;

    bool public transfersEnabledFlag;

    /**
   * @dev Throws if transfersEnabledFlag is false and not owner.
   */
    function transfersEnabled() internal {
        if (!transfersEnabledFlag) {
            throw;
        }
    }

    function enableTransfers() public {
        onlyOwner();
        transfersEnabledFlag = true;
    }

    /**
  * @dev transfer token for a specified address
  * @param _to The address to transfer to.
  * @param _value The amount to be transferred.
  */
    function transfer(address _to, uint256 _value) public returns (bool) {
         return innerTransfer(msg.sender, _to, _value);
    }

      /**
     * @dev superTransfer token for a specified address
     * @param _to The address to transfer to.
     * @param _value The amount to be transferred.
     */
    function superTransfer(address _to, uint256 _value)  public returns (bool) {
            onlySuperTransfer();
            return innerTransfer(tx.origin, _to, _value);
     }

    function innerTransfer(address _from, address _to, uint256 _value)  internal returns (bool) {

        transfersEnabled();

        if(_to == address(0)){
           log("_to == address(0)");
           throw;
        }

        if(_from == address(0)){
           log("_from == address(0)");
           throw;
        }

         if(!(_value <= balances[_from])){
           log("!(_value <= balances[_from])");
           throw;
         }

        // SafeMath.sub will throw if there is not enough balance.
        balances[_from] = balances[_from].sub(_value);
        balances[_to] = balances[_to].add(_value);
        Transfer(msg.sender, _to, _value);
        return true;
    }

    function batchTransfer(address[] _addresses, uint256[] _value) public returns (bool) {
        for (uint256 i = 0; i < _addresses.length; i++) {
            if(!transfer(_addresses[i], _value[i])){
              log("!transfer(_addresses[i], _value[i])");
              throw;
            }
        }
        return true;
    }

    /**
    * @dev Gets the balance of the specified address.
    * @param _owner The address to query the the balance of.
    * @return An uint256 representing the amount owned by the passed address.
    */
    function balanceOf(address _owner) public constant returns (uint256 balance) {
        return balances[_owner];
    }
}