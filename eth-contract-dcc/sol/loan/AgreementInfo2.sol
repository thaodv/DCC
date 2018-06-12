pragma solidity ^0.4.2;

contract AgreementInfo2 {

    function createAgreement(
        uint256 version,
        uint256 orderId,
        address _borrower,
        bytes32 idHash,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest,
        string status)
    external returns (uint256 agreementId);

    function updateStatus(uint256 orderId,string status,string expectedStatus) external;

    function updateRepayDigest(uint256 orderId, bytes repayDigest) external;
}