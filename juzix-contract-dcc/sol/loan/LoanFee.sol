pragma solidity ^0.4.2;

contract LoanFee {

    //申请费用流程接口
    function apply(uint256 orderId, address borrower, uint256 totalAmount) external;

    //取消订单费用流程接口
    function cancel(uint256 orderId) external;

    //审核订单费用流程接口
    function audit(uint256 orderId) external;

    //拒绝订单费用流程接口
    function reject(uint256 orderId) external;

    //审核通过费用流程接口
    function approve(uint256 orderId) external;

    //获取预期费用
    function getMinFee() external constant returns (uint256);

    //获取_orderId的费用订单
    function getFeeOrderById(uint256 _orderId) constant external returns (
        uint256 orderId,
        address borrower,
        uint256 availableAmount,
        uint256 totalAmount
    );
}
