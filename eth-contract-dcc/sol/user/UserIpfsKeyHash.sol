pragma solidity ^0.4.2;

contract UserIpfsKeyHash {

    mapping(address => bytes) ipfsKeyHashMapping;

    uint256 IPFS_KEYHASH_MAXSIZE = 100;

    event ipfsKeyPut(address userAddress, bytes ipfsKeyHash);
    event ipfsKeyUpdated(address userAddress, bytes ipfsKeyHash);
    event ipfsKeyDeleted(address userAddress);

    function putIpfsKey(bytes ipfsKeyHash) public {
        require(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE);
        require(ipfsKeyHashMapping[msg.sender].length == 0);

        ipfsKeyHashMapping[msg.sender] = ipfsKeyHash;
        ipfsKeyPut(msg.sender, ipfsKeyHash);
    }

    function updateIpfsKey(bytes ipfsKeyHash) public {
        require(ipfsKeyHash.length > 0 && ipfsKeyHash.length < IPFS_KEYHASH_MAXSIZE);
        require(ipfsKeyHashMapping[msg.sender].length != 0);

        ipfsKeyHashMapping[msg.sender] = ipfsKeyHash;
        ipfsKeyUpdated(msg.sender, ipfsKeyHash);
    }

    function deleteIpfsKey() public {
        require(ipfsKeyHashMapping[msg.sender].length != 0);

        delete ipfsKeyHashMapping[msg.sender];
        ipfsKeyDeleted(msg.sender);
    }

    function getIpfsKey() public view returns(bytes ipfsKey){
        return ipfsKeyHashMapping[msg.sender];
    }

}