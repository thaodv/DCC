pragma solidity ^0.4.2;
contract UserIpfsToken {

    struct IpfsToken {
        uint256 version;
        string cipher;
        bytes nonce;
        string token;
        bytes digest1;
        bytes digest2;
    }

    mapping(address => mapping(address => IpfsToken)) ipfsTokens;

    uint256 constant IPFS_TOKEN_MAXSIZE = 10 * 1024;

    uint256 constant IPFS_NONCE_MAXSIZE = 10 * 1024;

    uint256 constant IPFS_DIGEST_MAXSIZE = 100;

    uint256 constant CIPHER_MAXSIZE=100;

    event ipfsTokenPut(address indexed userAddress, address contractAddress,uint256 version,string cipher, bytes nonce, string token, bytes digest1, bytes digest2);
    event ipfsTokenDeleted(address indexed userAddress, address contractAddress);

    function putIpfsToken(address contractAddress,uint256 version,string cipher, bytes nonce, string token, bytes digest1, bytes digest2) public {
        require(contractAddress != 0);
        require(version!=0);
        require(bytes(cipher).length>0 && bytes(cipher).length<CIPHER_MAXSIZE);
        require(nonce.length <= IPFS_NONCE_MAXSIZE);
        require(bytes(token).length > 0 && bytes(token).length <= IPFS_TOKEN_MAXSIZE);
        require(digest1.length > 0 && digest1.length < IPFS_DIGEST_MAXSIZE);
        require(digest2.length > 0 && digest2.length < IPFS_DIGEST_MAXSIZE);

        ipfsTokens[msg.sender][contractAddress] = IpfsToken(version,cipher,nonce, token, digest1, digest2);
        ipfsTokenPut(msg.sender, contractAddress,version,cipher, nonce, token, digest1, digest2);
    }

    function deleteIpfsToken(address contractAddress) public {
        require(contractAddress != 0);

        delete ipfsTokens[msg.sender][contractAddress];
        ipfsTokenDeleted(msg.sender, contractAddress);
    }

    function getIpfsToken(address contractAddress) public view returns (
        address _userAddress,
        address _contractAddress,
        uint256 _version,
        string _cipher,
        bytes _nonce,
        string _token,
        bytes _digest1,
        bytes _digest2){
        require(contractAddress != 0);
        IpfsToken ipfsToken=ipfsTokens[msg.sender][contractAddress];
        return (
        msg.sender,
        contractAddress,
        ipfsToken.version,
        ipfsToken.cipher,
        ipfsToken.nonce,
        ipfsToken.token,
        ipfsToken.digest1,
        ipfsToken.digest2
        );
    }
}