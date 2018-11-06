pragma solidity ^0.4.2;

contract CertService4{
    function apply(bytes digest1, bytes digest2,bytes digest3,uint256 expired) external returns (uint256 _orderId);
    function pass(uint256 orderId,bytes digest1, bytes digest2,bytes digest3,uint256 expired) external;
    function reject(uint256 orderId) external;
    function revoke(address applicant) external;
    function getCertOrder(address applicant,uint256 index) view public returns (
        uint256 _orderId,
        address _applicant,
        uint8 status,
        bytes digest1,
        bytes digest2,
        bytes digest3,
        uint256 expired,
        uint256 feeDcc
    );
    function getLastCertOrder(address applicant)view public returns (
        uint256 _orderId,
        address _applicant,
        uint8 status,
        bytes digest1,
        bytes digest2,
        bytes digest3,
        uint256 expired,
        uint256 feeDcc
    );
}