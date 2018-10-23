pragma solidity ^0.4.2;

contract CertService4 {
    function apply(bytes digest1, bytes digest2, uint256 expired) public returns (uint256 _orderId);

    function pass(uint256 orderId, bytes digest1, bytes digest2, uint256 expired) public;

    function reject(uint256 orderId) public;

    function revoke(address applicant) public;

    function getCertOrder(address applicant, uint256 index) constant public returns (
        uint256 _orderId,
        address _applicant,
        uint8 status,
        bytes digest1,
        bytes digest2,
        uint256 expired,
        uint256 feeDcc
    );

    function getLastCertOrder(address applicant) constant public returns (
        uint256 _orderId,
        address _applicant,
        uint8 status,
        bytes digest1,
        bytes digest2,
        uint256 expired,
        uint256 feeDcc
    );
}
