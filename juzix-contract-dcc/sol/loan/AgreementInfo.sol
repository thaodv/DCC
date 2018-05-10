pragma solidity ^0.4.2;
//AgreementInfo接口
contract AgreementInfo {

    function createAgreement(
        uint256 version,
        uint256 orderId,
        address _borrower,
        bytes32 idHash,
        bytes applicationDigest,
        bytes agreementDigest,
        bytes repayDigest)
    external returns (uint256 agreementId);

    function finishAgreement(uint256 id) external;

    function updateRepayDigest(uint256 id, bytes repayDigest) external;
}