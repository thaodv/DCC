pragma solidity ^0.4.2;

contract UserIpfsKeyHash {

    struct KeyHash {
        uint256 version;
        bytes32 value;
    }

    mapping(address => KeyHash) ipfsKeyHashMapping;

    event ipfsKeyHashAdded(address indexed userAddress, uint256 version, bytes32 value);
    event ipfsKeyHashDeleted(address indexed userAddress);

    function addIpfsKeyHash(bytes32 value) public {
        require(value > 0);
        require(ipfsKeyHashMapping[msg.sender].value == 0);

        ipfsKeyHashMapping[msg.sender].value = value;
        ipfsKeyHashMapping[msg.sender].version++;

        ipfsKeyHashAdded(msg.sender, ipfsKeyHashMapping[msg.sender].version, ipfsKeyHashMapping[msg.sender].value);
    }

    function deleteIpfsKeyHash() public {
        require(ipfsKeyHashMapping[msg.sender].value != 0);
        ipfsKeyHashMapping[msg.sender].version++;
        delete ipfsKeyHashMapping[msg.sender].value;
        ipfsKeyHashDeleted(msg.sender);
    }

    function getIpfsKeyHash() public view returns (uint256 version, bytes32 value){
        return getIpfsKeyHash(msg.sender);
    }

    function getIpfsKeyHash(address addr) public view returns (uint256 version, bytes32 value){
        return (ipfsKeyHashMapping[addr].version, ipfsKeyHashMapping[addr].value);
    }
}