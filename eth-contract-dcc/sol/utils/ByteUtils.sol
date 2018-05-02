pragma solidity ^0.4.2;
import "../math/SafeMath.sol";
library ByteUtils{

    using SafeMath for uint256;

    function bytesEqual(bytes _digest1,bytes _digest2) internal returns (bool) {

           if (_digest1.length !=_digest2.length){
               return false;
           }

           for (uint i = 0; i <_digest1.length; i ++)
           if (_digest1[i] !=_digest2[i]){
               return false;
           }

           return true;
       }
/*
    function bytesToBytes32(bytes b, uint offset) internal  returns (bytes32) {
          bytes32 out;

          for (uint i = 0; i <b.length; i++) {
            out |= bytes32(b[offset + i] & 0xFF) >> (i * 8);
          }
          return out;
     }
*/
     function bytesToBytes32(bytes b,uint256 off) internal  returns (bytes32) {
         uint  account=256;
         uint  a=0;
         if(b.length>64){
           uint256 loops=64;
         }else{
           loops=b.length;
         }
         for(uint i=0;i<loops;i++){
             account=account.sub(4);
             uint temp= uint(b[i]);
             if(temp>=97){
                 temp=temp.sub(87);
             }else{
                 temp=temp.sub(48);
             }
             a=a.add(temp.mul(2**account));
         }
         return (bytes32)(a);
     }

}