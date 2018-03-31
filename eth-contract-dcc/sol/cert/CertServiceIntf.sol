pragma solidity ^0.4.11;


contract CertServiceIntf {

    function apply(bytes digest1, bytes digest2) public returns (uint256 _orderId);

    function pass(uint256 orderId, bytes digest1, bytes digest2, uint256 expired) public;

    function reject(uint256 orderId) public;
}
