pragma solidity ^0.4.2;
import "./UserIpfsKeyHash.sol";
import "../permission/OwnerPermission.sol";
contract UserIpfsToken is OwnerPermission{

    struct IpfsToken {
        uint256 version;
        string cipher;
        string token;
        bytes iv;
        bytes digest1;
        bytes digest2;
        uint256 keyHashVersion;
    }

    UserIpfsKeyHash public userIpfsKeyHash;

    mapping(address => mapping(address => IpfsToken)) public ipfsCertData;

    uint256 constant IPFS_TOKEN_MAXSIZE = 10 * 1024;

    uint256 constant IPFS_DIGEST_MAXSIZE = 100;

    uint256 constant CIPHER_MAXSIZE=100;

    uint256 constant IV_MAXSIZE=100;

    event ipfsTokenPut(address indexed userAddress, address indexed contractAddress, uint256 version,string cipher,string token,bytes iv,bytes digest1,bytes digest2,uint256 keyHashVersion);

    function UserIpfsToken(address userIpfsKeyHashAddress) public{
        register("UserIpfsTokenModule", "0.0.1.0", "UserIpfsToken", "0.0.1.0");
        if(!(userIpfsKeyHashAddress!=0)){
            log("!(userIpfsKeyHashAddress!=0)");
            throw;
        }
        userIpfsKeyHash=UserIpfsKeyHash(userIpfsKeyHashAddress);
    }

    function putIpfsToken(address contractAddress,uint256 version,string cipher,string token,bytes iv,bytes digest1,bytes digest2,bytes32 keyHash)  public {
        if(!(contractAddress != 0)){
            log("!(contractAddress != 0)");
            throw;
        }
        if(!(version!=0)){
            log("!(version!=0)");
            throw;
        }
        if(!(bytes(cipher).length>0 && bytes(cipher).length<=CIPHER_MAXSIZE)){
            log("!(bytes(cipher).length>0 && bytes(cipher).length<=CIPHER_MAXSIZE)");
            throw;
        }
        if(!(bytes(token).length > 0 && bytes(token).length <= IPFS_TOKEN_MAXSIZE)){
            log("!(bytes(token).length > 0 && bytes(token).length <= IPFS_TOKEN_MAXSIZE)");
            throw;
        }
        if(!(iv.length>0 && iv.length<=IV_MAXSIZE)){
            log("!(iv.length>0 && iv.length<=IV_MAXSIZE)");
            throw;
        }
        if(!(digest1.length <= IPFS_DIGEST_MAXSIZE)){
            log("!(digest1.length <= IPFS_DIGEST_MAXSIZE)");
            throw;
        }
        if(!(digest2.length <= IPFS_DIGEST_MAXSIZE)){
            log("!(digest2.length <= IPFS_DIGEST_MAXSIZE)");
            throw;
        }
        if(!(keyHash!=0)){
            log("!(keyHash!=0)");
            throw;
        }
        //比对keyHash
        var (v1,v2)=userIpfsKeyHash.getIpfsKeyHash(msg.sender);
        if(!(v2==keyHash)){
            log("!(v2==keyHash)");
            throw;
        }

        ipfsCertData[msg.sender][contractAddress] = IpfsToken(version,cipher,token,iv,digest1,digest2,v1);
        ipfsTokenPut(msg.sender, contractAddress,version,cipher,token,iv,digest1,digest2,v1);
    }

    function getIpfsToken(address contractAddress) public constant returns (
        address _userAddress,
        address _contractAddress,
        uint256 _version,
        string _cipher,
        string _token,
        bytes _iv,
        bytes _digest1,
        bytes _digest2,
        uint256 _keyHashVersion){
        if(!(contractAddress != 0)){
            log("!(contractAddress != 0)");
            throw;
        }
        IpfsToken memory ipfsToken=ipfsCertData[msg.sender][contractAddress];
        var (v1,)=userIpfsKeyHash.getIpfsKeyHash(msg.sender);
        if(!(v1==ipfsToken.keyHashVersion)){
            log("!(v1==ipfsToken.keyHashVersion)");
            throw;
        }
        return (
        msg.sender,
        contractAddress,
        ipfsToken.version,
        ipfsToken.cipher,
        ipfsToken.token,
        ipfsToken.iv,
        ipfsToken.digest1,
        ipfsToken.digest2,
        ipfsToken.keyHashVersion
        );
    }

    function setUserIpfsKeyHash(address userIpfsKeyHashAddress) public {
        onlyOwner();
        if(!(userIpfsKeyHashAddress!=0)){
            log("!(userIpfsKeyHashAddress!=0)");
            throw;
        }
        userIpfsKeyHash=UserIpfsKeyHash(userIpfsKeyHashAddress);
    }
}