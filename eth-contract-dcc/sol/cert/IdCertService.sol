pragma solidity ^0.4.2;

import "./CertService3.sol";
import "../utils/ByteUtils.sol";

contract IdCertService is CertService3 {

    using ByteUtils for bytes;

    function IdCertService(bytes _name, DigestIntegrity _digest1Integrity, DigestIntegrity _digest2Integrity, DigestIntegrity _expiredIntegrity)
    CertService3(_name, _digest1Integrity, _digest2Integrity, _expiredIntegrity) {

    }

    //实名认证申请
    function apply(bytes digest1, bytes digest2, uint256 expired) public returns (uint256 _orderId){

        if (digest1Integrity == DigestIntegrity.APPLICANT) {
            require(digest1.length > 0 && digest1.length <= 100);
        } else {
            require(digest1.length == 0);
        }

        if (digest2Integrity == DigestIntegrity.APPLICANT) {
            require(digest2.length > 0 && digest2.length <= 100);
        } else {
            require(digest2.length == 0);
        }

        if (expiredIntegrity == DigestIntegrity.APPLICANT) {
            require(expired != 0);
        } else {
            require(expired == 0);
        }

        var (d1,) = getData();

        if (d1.length > 0) {
            assert(d1.bytesEqual(digest1));
        }

        uint256 fee = 0;

        if (certServiceFeeModule != address(0)) {
            fee = certServiceFeeModule.apply();
        }
        return insertOrder(msg.sender, Status.APPLIED, Content(digest1, digest2, expired), fee);
    }

}
