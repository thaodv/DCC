pragma solidity ^0.4.18;

contract AgreementIntf {
    function submit(uint256 orderId, address debit, address credit, bytes creditIdHash, bytes agreementHash) public
    returns (uint256
        agreementId);
}