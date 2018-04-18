pragma solidity ^0.4.2;

contract AgreementInfo {

    function createAgreement(uint256 version,uint256 orderId,bytes repayDigest,bytes agrementDigest,bytes idHash,address _borrower,bytes applicationDigest) external;

    function finishAgreement(uint256 id) external;

    function updateRepayDigest(uint256 id,bytes repayDigest)  external;
}