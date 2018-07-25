pragma solidity ^0.4.2;

contract UserIpfsToken {

    struct IpfsToken {
        bytes nonce;
        string token;
    }

    mapping(address => mapping(address => IpfsToken)) ipfsTokens;

    uint256 constant IPFS_TOKEN_MAXSIZE = 10 * 1024;

    uint256 constant IPFS_NONCE_MAXSIZE = 10 * 1024;

    event ipfsTokenPut(address indexed userAddress, address contractAddress, bytes nonce, string token);
    event ipfsTokenDeleted(address indexed userAddress, address contractAddress);

    function putIpfsToken(address contractAddress, bytes nonce, string token) public {
        require(contractAddress != 0);
        require(nonce.length <= IPFS_NONCE_MAXSIZE);
        require(bytes(token).length > 0 && bytes(token).length <= IPFS_TOKEN_MAXSIZE);

        ipfsTokens[msg.sender][contractAddress] = IpfsToken(nonce, token);
        ipfsTokenPut(msg.sender, contractAddress, nonce, token);
    }

    function deleteIpfsToken(address contractAddress) public {
        require(contractAddress != 0);

        delete ipfsTokens[msg.sender][contractAddress];
        ipfsTokenDeleted(msg.sender, contractAddress);
    }

    function getIpfsToken(address contractAddress) public constant returns (address _userAddress, address _contractAddress, bytes _nonce, string _token){
        require(contractAddress != 0);

        return (msg.sender, contractAddress, ipfsTokens[msg.sender][contractAddress].nonce, ipfsTokens[msg.sender][contractAddress].token);
    }
}