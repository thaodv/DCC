pragma solidity ^0.4.2;

import "../permission/OperatorPermission.sol";

contract UserIpfsKeyHash is OperatorPermission {

    mapping(address => bytes) public ipfsKeyHashMapping;

    uint256 IPFS_KEYHASH_MAXSIZE = 100;

    event ipfsKeyPut(address userAddress, bytes ipfsKeyHash);
    event ipfsKeyUpdated(address userAddress, bytes ipfsKeyHash);
    event ipfsKeyDeleted(address userAddress);

    function UserIpfsKeyHash(){
        register("UserIpfsKeyHashModule", "0.0.1.0", "UserIpfsKeyHash", "0.0.1.0");
    }

    function putIpfsKey(address userAddress, bytes ipfsKeyHash) public {
        onlyOperator();
        if(!(userAddress != 0)){
            log("!(userAddress != 0)");
            throw;
        }
        if(!(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE)){
            log("!(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE)");
            throw;
        }
        if(!(ipfsKeyHashMapping[userAddress].length == 0)){
            log("!(ipfsKeyHashMapping[userAddress].length == 0)");
            throw;
        }

        ipfsKeyHashMapping[userAddress] = ipfsKeyHash;
        ipfsKeyPut(userAddress, ipfsKeyHash);
    }

    function updateIpfsKey(address userAddress, bytes ipfsKeyHash) public {
        onlyOperator();
        if(!(userAddress != 0)){
            log("!(userAddress != 0)");
            throw;
        }
        if(!(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE)){
            log("!(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE)");
            throw;
        }
        if(!(ipfsKeyHashMapping[userAddress].length != 0)){
            log("!(ipfsKeyHashMapping[userAddress].length != 0)");
            throw;
        }

        ipfsKeyHashMapping[userAddress] = ipfsKeyHash;
        ipfsKeyUpdated(userAddress, ipfsKeyHash);
    }

    function deleteIpfsKey(address userAddress) public {
        onlyOperator();
        if(!(userAddress != 0)){
            log("!(userAddress != 0)");
            throw;
        }
        if(!(ipfsKeyHashMapping[userAddress].length != 0)){
            log("!(ipfsKeyHashMapping[userAddress].length != 0)");
            throw;
        }

        delete ipfsKeyHashMapping[userAddress];
        ipfsKeyDeleted(userAddress);
    }

}