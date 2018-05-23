pragma solidity ^0.4.2;

contract Ca {
    uint256 public constant max = 1024;
    
   mapping(address => bytes) publickeys;
   
   // Triggered when _publickey is put.
    event KeyPut(address indexed _sender, bytes _publickey);

   // Triggered when _publickey is deleted.
    event KeyDeleted(address indexed _sender, bytes _publickey);
 
     function putKey(bytes _publickey) public returns (bool _result){
         if(_publickey.length > max ){
             return false;
         }
         
        publickeys[msg.sender] = _publickey;
        return true;
    }

    function getKey() constant public returns(bytes _data) {
      return publickeys[msg.sender];
    }
    
     function removeKey()  public {
       delete publickeys[msg.sender];
    }
}
