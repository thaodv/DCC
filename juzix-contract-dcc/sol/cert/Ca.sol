pragma solidity ^0.4.2;

import "../sysbase/OwnerNamed.sol";

contract Ca is OwnerNamed{
    
   uint256 public constant max = 2048;
    
    mapping(address => bytes) publickeys;
   
    // Triggered when _publickey is put.
    event KeyPut(address indexed _sender, bytes _publickey);

    // Triggered when _publickey is deleted.
    event KeyDeleted(address indexed _sender, bytes _publickey);

    // Constructor
    function Ca() {
      register("CaModule", "0.0.1.0", "Ca", "0.0.1.0");
    }

 
    function putKey(bytes _publickey) public returns (bool _result){
         if(_publickey.length > max && _publickey.length == 0 ){
             return false;
         }
         
        publickeys[msg.sender] = _publickey;
        KeyPut(msg.sender,_publickey);
        return true;
    }
    
    function deleteKey()  public  returns(bool _result) {
       bytes deleted =publickeys[msg.sender];
       if(deleted.length == 0){
           return false;
       }
       
       delete publickeys[msg.sender];
       
       KeyDeleted(msg.sender,deleted);
       return true;
    }
    
    function getKey(address _owner) constant public returns(bytes _data) {
      return publickeys[_owner];
    }


    function getKey() constant public returns(bytes _data) {
      return getKey(msg.sender);
    }

}
