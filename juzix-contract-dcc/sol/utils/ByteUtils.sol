pragma solidity ^0.4.2;

library ByteUtils{

        function bytesEqual(bytes _digest1,bytes _digest2) public returns (bool) {

           if (_digest1.length !=_digest2.length){
               return false;
           }

           for (uint i = 0; i <_digest1.length; i ++){
               if (_digest1[i] !=_digest2[i]){
                   return false;
               }
           }

           return true;
       }
}