pragma solidity ^0.4.2;
import "../sysbase/OwnerNamed.sol";
contract IpfsKeyHash is OwnerNamed {

    struct KeyHash{
        uint256 version;
        bytes32 value;
    }

    mapping(address => KeyHash) ipfsKeyHashMapping;

    event ipfsKeyHashAdded(address indexed userAddress,uint256 indexed version,bytes32 value);
    event ipfsKeyHashDeleted(address indexed userAddress);

    function IpfsKeyHash(){
        register("IpfsKeyHashModule", "0.0.1.0", "IpfsKeyHash", "0.0.1.0");
    }

    function addIpfsKeyHash(bytes32 value) public {
        if(!(value>0)){
            log("!(value>0)");
            throw;
        }
        if(!(ipfsKeyHashMapping[msg.sender].value == 0)){
            log("!(ipfsKeyHashMapping[msg.sender].value == 0)");
            throw;
        }

        ipfsKeyHashMapping[msg.sender].value = value;
        ipfsKeyHashMapping[msg.sender].version++;

        ipfsKeyHashAdded(msg.sender,ipfsKeyHashMapping[msg.sender].version,ipfsKeyHashMapping[msg.sender].value);
    }

    function deleteIpfsKeyHash() public {
        if(!(ipfsKeyHashMapping[msg.sender].value!= 0)){
            log("!(ipfsKeyHashMapping[msg.sender].value!= 0)");
            throw;
        }
        ipfsKeyHashMapping[msg.sender].version++;
        delete ipfsKeyHashMapping[msg.sender].value;
        ipfsKeyHashDeleted(msg.sender);
    }

    function getIpfsKeyHash() public constant returns(uint256 version, bytes32 value){
        return getIpfsKeyHash(msg.sender);
    }

    function getIpfsKeyHash(address addr) public constant returns(uint256 version, bytes32 value){
        return (ipfsKeyHashMapping[addr].version,ipfsKeyHashMapping[addr].value);
    }
}