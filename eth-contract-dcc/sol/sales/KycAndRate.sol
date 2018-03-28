pragma solidity ^0.4.18;

import '../ownership/Ownable.sol';

contract KycAndRate is Ownable {

    mapping(address => uint256) public rates;

    event AddedToRates(address indexed participator, uint256 rate);

    event DeletedFromRates(address indexed participator);

    function getRate(address addr) view public returns (uint256){
        return rates[addr];
    }

    function addRate(address addr, uint256 rate) public onlyOwner {
        rates[addr] = rate;
        AddedToRates(addr, rate);
    }

    function deleteRate(address addr) public onlyOwner {
        delete rates[addr];
        DeletedFromRates(addr);
    }


}