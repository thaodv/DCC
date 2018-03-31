pragma solidity ^0.4.2;

contract  CertServiceFeeModule{
    function  apply() public returns(uint256);
    function  getFee()constant  public returns(uint256);
}