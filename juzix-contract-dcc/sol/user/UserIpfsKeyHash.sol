pragma solidity ^0.4.2;
import "../sysbase/OwnerNamed.sol";
contract UserIpfsKeyHash is OwnerNamed{

    mapping(address => bytes) ipfsKeyHashMapping;

    uint256 IPFS_KEYHASH_MAXSIZE = 100;

    event ipfsKeyPut(address userAddress, bytes ipfsKeyHash);
    event ipfsKeyUpdated(address userAddress, bytes ipfsKeyHash);
    event ipfsKeyDeleted(address userAddress);

    function UserIpfsKeyHash(){
        register("UserIpfsKeyHashModule", "0.0.1.0", "UserIpfsKeyHash", "0.0.1.0");
    }

    function putIpfsKey(bytes ipfsKeyHash) public {

        if(!(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE)){
            log("!(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE)");
            throw;
        }
        if(!(ipfsKeyHashMapping[msg.sender].length == 0)){
            log("!(ipfsKeyHashMapping[userAddress].length == 0)");
            throw;
        }

        ipfsKeyHashMapping[msg.sender] = ipfsKeyHash;
        ipfsKeyPut(msg.sender, ipfsKeyHash);
    }

    function updateIpfsKey(bytes ipfsKeyHash) public {
        if(!(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE)){
            log("!(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE)");
            throw;
        }
        if(!(ipfsKeyHashMapping[msg.sender].length != 0)){
            log("!(ipfsKeyHashMapping[userAddress].length != 0)");
            throw;
        }

        ipfsKeyHashMapping[msg.sender] = ipfsKeyHash;
        ipfsKeyUpdated(msg.sender, ipfsKeyHash);
    }

    function deleteIpfsKey() public {
        if(!(ipfsKeyHashMapping[msg.sender].length != 0)){
            log("!(ipfsKeyHashMapping[userAddress].length != 0)");
            throw;
        }

        delete ipfsKeyHashMapping[msg.sender];
        ipfsKeyDeleted(msg.sender);
    }

    function getIpfsKey() public returns(bytes ipfsKey){
        return ipfsKeyHashMapping[msg.sender];
    }

}