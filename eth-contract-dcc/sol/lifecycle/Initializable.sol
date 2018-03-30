pragma solidity ^0.4.18;


import "../ownership/Ownable.sol";


/**
 * @title Initializable
 
 */
contract Initializable is Ownable {
    event Initialized();

    bool public initialized;

    /**
     */
    modifier beforeInitialized() {
        require(!initialized);
        _;
    }

    /**
     */
    modifier afterInitialized() {
        require(initialized);
        _;
    }

}