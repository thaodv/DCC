pragma solidity  ^0.4.2;
import "../sysbase/OwnerNamed.sol";
contract TestEvent is OwnerNamed{
    event event1(
       address indexed _from,
       bytes32 indexed _id,
       uint256 _value
     );

    event event2(
      address indexed _from,
      bytes32 indexed _id,
      uint256 indexed _value
    );

    function TestEvent() public{
        register("TestEventModule", "0.0.1.0", "TestEvent", "0.0.1.0");
    }

   function emitEvent1(bytes32 _id) public{
       event1(msg.sender, _id, msg.value);
   }

   function emitEvent2(bytes32 _id,uint256 _value) public{
        event2(msg.sender, _id,_value);
    }
}