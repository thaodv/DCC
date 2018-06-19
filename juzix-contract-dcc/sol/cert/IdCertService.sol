pragma solidity ^0.4.2;

import "./CertService3.sol";
import "../utils/ByteUtils.sol";

contract IdCertService is CertService3{

    using ByteUtils for bytes;

    function IdCertService(bytes _name,DigestIntegrity _digest1Integrity,DigestIntegrity _digest2Integrity,DigestIntegrity _expiredIntegrity)
    CertService3( _name,_digest1Integrity,_digest2Integrity, _expiredIntegrity){
        register("IdCertServiceModule", "0.0.1.0", "IdCertService", "0.0.1.0");
    }

    //digest1如果有值，那么只能和当前参数值一样
    function apply(bytes digest1, bytes digest2,uint256 expired) public returns (uint256 _orderId){

         if(digest1Integrity == DigestIntegrity.APPLICANT){
                if(!(digest1.length>0 && digest1.length<=100)){
                   log("!(digest1.length>0 && digest1.length<=100)");
                   throw;
                }
          }else{
                if(!(digest1.length==0)){
                   log("!(digest1.length==0)");
                   throw;
                }
          }

          if(digest2Integrity == DigestIntegrity.APPLICANT){
                  if(!(digest2.length>0 && digest2.length<=100)){
                     log("!(digest2.length>0 && digest2.length<=100)");
                     throw;
                  }
            }else{
                  if(!(digest2.length==0)){
                     log("!(digest2.length==0)");
                     throw;
                  }
            }

           if(expiredIntegrity == DigestIntegrity.APPLICANT){
                if(!(expired!=0)){
                   log("!(expired!=0)");
                   throw;
                }
          }else{
                if(!(expired==0)){
                   log("!(expired==0)");
                   throw;
                }
          }

        var (d1,)= getData();

        if(d1.length>0){
            if(!d1.bytesEqual(digest1)){
              log("!d1.bytesEqual(digest1)");
              throw;
            }
        }
        uint256 fee=0;

       if(certServiceFeeModule!=address(0)){
         fee=certServiceFeeModule.apply();
       }

        return insertOrder(msg.sender, Status.APPLIED, Content(digest1,digest2,expired),fee);
    }

}
