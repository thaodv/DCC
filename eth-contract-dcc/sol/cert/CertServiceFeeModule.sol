pragma solidity ^0.4.11;

contract  CertServiceFeeModule{
    function  apply() public returns(uint256 fee);
    function  getFee()view  public returns(uint256);
}