pragma solidity ^0.4.2;

contract UserIpfsToken {

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

    function putIpfsToken(address contractAddress, bytes nonce, string token, bytes digest1, bytes digest2) public {
        require(contractAddress != 0);
        require(nonce.length <= IPFS_NONCE_MAXSIZE);
        require(bytes(token).length > 0 && bytes(token).length <= IPFS_TOKEN_MAXSIZE);
        require(digest1.length > 0 && digest1.length < IPFS_DIGEST_MAXSIZE);
        require(digest2.length > 0 && digest2.length < IPFS_DIGEST_MAXSIZE);

        ipfsTokens[msg.sender][contractAddress] = IpfsToken(nonce, token, digest1, digest2);
        ipfsTokenPut(msg.sender, contractAddress, nonce, token, digest1, digest2);
    }

    function deleteIpfsToken(address contractAddress) public {
        require(contractAddress != 0);

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
        require(contractAddress != 0);
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