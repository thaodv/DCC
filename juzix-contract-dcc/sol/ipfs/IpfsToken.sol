pragma solidity ^0.4.2;
import "./IpfsKeyHash.sol";
import "../permission/OwnerPermission.sol";
contract IpfsToken is OwnerPermission{
    struct IpfsTokenData {
        uint256 version;
        string cipher;
        string token;
        bytes iv;
        bytes digest1;
        bytes digest2;
        uint256 keyHashVersion;
    }

    IpfsKeyHash public ipfsKeyHash;

    mapping(address => mapping(address => IpfsTokenData)) ipfsCertData;

    uint256 constant IPFS_TOKEN_MAXSIZE = 10 * 1024;

    uint256 constant IPFS_DIGEST_MAXSIZE = 100;

    uint256 constant CIPHER_MAXSIZE=100;

    uint256 constant IV_MAXSIZE=100;

    event ipfsTokenPut(address indexed userAddress, address indexed contractAddress, uint256 version,string cipher,string token,bytes iv,bytes digest1,bytes digest2,uint256 keyHashVersion);

    function IpfsToken(address ipfsKeyHashAddress) public{
        register("IpfsTokenModule", "0.0.1.0", "IpfsToken", "0.0.1.0");
        if(!(ipfsKeyHashAddress!=0)){
            log("!(IpfsKeyHashAddress!=0)");
            throw;
        }
        ipfsKeyHash=IpfsKeyHash(ipfsKeyHashAddress);
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
        var (v1,v2)=ipfsKeyHash.getIpfsKeyHash(msg.sender);
        if(!(v2==keyHash)){
            log("!(v2==keyHash)");
            throw;
        }

        ipfsCertData[msg.sender][contractAddress] = IpfsTokenData(version,cipher,token,iv,digest1,digest2,v1);
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
        return getIpfsToken(msg.sender,contractAddress);
    }

    function getIpfsToken(address userAddress,address contractAddress) public constant returns (
        address _userAddress,
        address _contractAddress,
        uint256 _version,
        string _cipher,
        string _token,
        bytes _iv,
        bytes _digest1,
        bytes _digest2,
        uint256 _keyHashVersion){
        if(!(userAddress!=0)){
            log("!(userAddress!=0)");
            throw;
        }
        if(!(contractAddress != 0)){
            log("!(contractAddress != 0)");
            throw;
        }
        IpfsTokenData memory ipfsToken=ipfsCertData[userAddress][contractAddress];
        var (v1,)=ipfsKeyHash.getIpfsKeyHash(userAddress);
        if(!(v1==ipfsToken.keyHashVersion)){
            log("!(v1==ipfsToken.keyHashVersion)");
            throw;
        }
        return (
        userAddress,
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

    function setIpfsKeyHash(address ipfsKeyHashAddress) public {
        onlyOwner();
        if(!(ipfsKeyHashAddress!=0)){
            log("!(ipfsKeyHashAddress!=0)");
            throw;
        }
        ipfsKeyHash=IpfsKeyHash(ipfsKeyHashAddress);
    }
}