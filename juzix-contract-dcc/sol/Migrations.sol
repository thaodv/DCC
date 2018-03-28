pragma solidity ^0.4.4;

import "./sysbase/OwnerNamed.sol";

contract Migrations is OwnerNamed {
  address public owner;
  uint public last_completed_migration;

  modifier restricted() {
    if (msg.sender == owner) _;
  }

  function Migrations() {
    owner = msg.sender;
    register("SimpleModule", "0.0.1.0", "Migrations", "0.0.1.0");
  }

  function setCompleted(uint completed) restricted {
    last_completed_migration = completed;
  }

  function upgrade(address new_address) restricted {
    Migrations upgraded = Migrations(new_address);
    upgraded.setCompleted(last_completed_migration);
  }
}
