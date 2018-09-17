pragma solidity ^0.4.2;

import "./UserIpfsKeyHash.sol";
import "../ownership/Ownable.sol";

contract UserIpfsToken is Ownable {

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

    uint256 constant CIPHER_MAXSIZE = 100;

    uint256 constant IV_MAXSIZE = 100;

    event ipfsTokenPut(address indexed userAddress, address indexed contractAddress, uint256 version, string cipher, string token, bytes iv, bytes digest1, bytes digest2, uint256 keyHashVersion);

    function UserIpfsToken(address userIpfsKeyHashAddress) public {
        require(userIpfsKeyHashAddress != 0);
        userIpfsKeyHash = UserIpfsKeyHash(userIpfsKeyHashAddress);
    }

    function putIpfsToken(address contractAddress, uint256 version, string cipher, string token, bytes iv, bytes digest1, bytes digest2, bytes32 keyHash) public {
        require(contractAddress != 0);
        require(version != 0);
        require(bytes(cipher).length > 0 && bytes(cipher).length <= CIPHER_MAXSIZE);
        require(bytes(token).length > 0 && bytes(token).length <= IPFS_TOKEN_MAXSIZE);
        require(iv.length > 0 && iv.length <= IV_MAXSIZE);
        require(digest1.length <= IPFS_DIGEST_MAXSIZE);
        require(digest2.length <= IPFS_DIGEST_MAXSIZE);
        require(keyHash != 0);
        //比对keyHash
        var (v1, v2) = userIpfsKeyHash.getIpfsKeyHash(msg.sender);
        require(v2 == keyHash);

        ipfsCertData[msg.sender][contractAddress] = IpfsToken(version, cipher, token, iv, digest1, digest2, v1);
        ipfsTokenPut(msg.sender, contractAddress, version, cipher, token, iv, digest1, digest2, v1);
    }

    function getIpfsToken(address contractAddress) public view returns (
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

    function getIpfsToken(address userAddress,address contractAddress) public view returns (
        address _userAddress,
        address _contractAddress,
        uint256 _version,
        string _cipher,
        string _token,
        bytes _iv,
        bytes _digest1,
        bytes _digest2,
        uint256 _keyHashVersion){
        require(userAddress!=0);
        require(contractAddress != 0);
        IpfsToken memory ipfsToken = ipfsCertData[userAddress][contractAddress];
        var (v1,) = userIpfsKeyHash.getIpfsKeyHash(userAddress);
        require(v1 == ipfsToken.keyHashVersion);
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

    function setUserIpfsKeyHash(address userIpfsKeyHashAddress) public onlyOwner {
        require(userIpfsKeyHashAddress != 0);
        userIpfsKeyHash = UserIpfsKeyHash(userIpfsKeyHashAddress);
    }
}