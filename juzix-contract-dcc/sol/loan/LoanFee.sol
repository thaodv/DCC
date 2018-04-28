pragma solidity ^0.4.2;

contract LoanFee {

    function apply(uint256 orderId, address borrower, uint256 totalAmount) external;

    function cancel(uint256 orderId) external;

    function audit(uint256 orderId) external;

    function reject(uint256 orderId) external;

    function approve(uint256 orderId) external;

    function getMinFee() external constant returns (uint256);

    function getFeeOrderById(uint256 _orderId) constant external returns (
        uint256 orderId,
        address borrower,
        uint256 availableAmount,
        uint256 totalAmount
    );
}
