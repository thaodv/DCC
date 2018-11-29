pragma solidity ^0.4.2;

contract TNLoanFee {

    function receive(uint256 amount) public;

    function send(address to,uint256 amount) public;

    function getMinFee() public constant returns (uint256);

}