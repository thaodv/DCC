pragma solidity ^0.4.2;
import "../sysbase/OwnerNamed.sol";
contract UserIpfsToken is OwnerNamed{

    struct IpfsToken {
        bytes nonce;
        string token;
        bytes digest1;
        bytes digest2;
    }

    mapping(address => mapping(address => IpfsToken)) ipfsTokens;

    uint256 constant IPFS_TOKEN_MAXSIZE = 10 * 1024;

    uint256 constant IPFS_NONCE_MAXSIZE = 10 * 1024;

    uint256 constant IPFS_DIGEST_MAXSIZE = 100;

    event ipfsTokenPut(address indexed userAddress, address contractAddress, bytes nonce, string token, bytes digest1, bytes digest2);
    event ipfsTokenDeleted(address indexed userAddress, address contractAddress);

    function UserIpfsToken(){
        register("UserIpfsTokenModule", "0.0.1.0", "UserIpfsToken", "0.0.1.0");
    }

    function putIpfsToken(address contractAddress, bytes nonce, string token, bytes digest1, bytes digest2) public {
        if(!(contractAddress != 0)){
            log("!(contractAddress != 0)");
            throw;
        }
        if(!(nonce.length <= IPFS_NONCE_MAXSIZE)){
            log("!(nonce.length <= IPFS_NONCE_MAXSIZE)");
            throw;
        }
        if(!(bytes(token).length > 0 && bytes(token).length <= IPFS_TOKEN_MAXSIZE)){
            log("!(bytes(token).length > 0 && bytes(token).length <= IPFS_TOKEN_MAXSIZE)");
            throw;
        }
        if(!(digest1.length > 0 && digest1.length < IPFS_DIGEST_MAXSIZE)){
            log("!(digest1.length > 0 && digest1.length < IPFS_DIGEST_MAXSIZE)");
            throw;
        }
        if(!(digest2.length > 0 && digest2.length < IPFS_DIGEST_MAXSIZE)){
            log("!(digest2.length > 0 && digest2.length < IPFS_DIGEST_MAXSIZE)");
            throw;
        }

        ipfsTokens[msg.sender][contractAddress] = IpfsToken(nonce, token, digest1, digest2);
        ipfsTokenPut(msg.sender, contractAddress, nonce, token, digest1, digest2);
    }

    function deleteIpfsToken(address contractAddress) public {
        if(!(contractAddress != 0)){
            log("!(contractAddress != 0)");
            throw;
        }

        delete ipfsTokens[msg.sender][contractAddress];
        ipfsTokenDeleted(msg.sender, contractAddress);
    }

    function getIpfsToken(address contractAddress) public constant returns (
        address _userAddress,
        address _contractAddress,
        bytes _nonce,
        string _token,
        bytes _digest1,
        bytes _digest2){
        if(!(contractAddress != 0)){
            log("!(contractAddress != 0)");
            throw;
        }
        return (
        msg.sender,
        contractAddress,
        ipfsTokens[msg.sender][contractAddress].nonce,
        ipfsTokens[msg.sender][contractAddress].token,
        ipfsTokens[msg.sender][contractAddress].digest1,
        ipfsTokens[msg.sender][contractAddress].digest2
        );
    }
}