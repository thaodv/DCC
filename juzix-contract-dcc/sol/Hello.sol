pragma solidity ^0.4.2;

import "sysbase/OwnerNamed.sol";

contract Hello is OwnerNamed {

    string public message;

    function setMessage(string input) public {
        message = input;
    }
    function Hello() public {
        register("HelloModule", "0.0.1.0", "Hello", "0.0.1.0");
    }
}
