pragma solidity ^0.4.2;

import "../ownership/OperatorPermission.sol";

contract UserIpfsKeyHash is OperatorPermission {

    mapping(address => bytes) public ipfsKeyHashMapping;

    uint256 IPFS_KEYHASH_MAXSIZE = 100;

    event ipfsKeyPut(address userAddress, bytes ipfsKeyHash);
    event ipfsKeyUpdated(address userAddress, bytes ipfsKeyHash);
    event ipfsKeyDeleted(address userAddress);

    function putIpfsKey(address userAddress, bytes ipfsKeyHash) public onlyOperator {
        require(userAddress != 0);
        require(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE);
        require(ipfsKeyHashMapping[userAddress].length == 0);

        ipfsKeyHashMapping[userAddress] = ipfsKeyHash;
        ipfsKeyPut(userAddress, ipfsKeyHash);
    }

    function updateIpfsKey(address userAddress, bytes ipfsKeyHash) public onlyOperator {
        require(userAddress != 0);
        require(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE);
        require(ipfsKeyHashMapping[userAddress].length != 0);

        ipfsKeyHashMapping[userAddress] = ipfsKeyHash;
        ipfsKeyUpdated(userAddress, ipfsKeyHash);
    }

    function deleteIpfsKey(address userAddress) public onlyOperator {
        require(userAddress != 0);
        require(ipfsKeyHashMapping[userAddress].length != 0);

        delete ipfsKeyHashMapping[userAddress];
        ipfsKeyDeleted(userAddress);
    }

}