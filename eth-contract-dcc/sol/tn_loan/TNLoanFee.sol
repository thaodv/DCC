pragma solidity ^0.4.2;

contract TNLoanFee {

    function receive(uint256 amount) external;

    function send(address to,uint256 amount) external;

    function getMinFee() external view returns (uint256);

}