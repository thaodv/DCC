pragma solidity ^0.4.2;

import "../permission/OperatorPermission.sol";

contract UserIpfsToken is OperatorPermission {

    struct ipfs_metadata {
        uint256 nonce;
        string token;
    }

    mapping(address => mapping(address => ipfs_metadata)) userIpfsTokens;

    uint256 constant USER_IPFS_TOKEN_MAXSIZE = 10 * 1024;

    event userIpfsTokenPuted(address indexed addr, address subAddr, uint256 nonce, string token);
    event userIpfsTokenDeleted(address indexed addr, address subAddr, uint256 nonce, string token);

    function putUserIpfsToken(address addr, address subAddr, uint256 nonce, string token) onlyOperator public {
        if (!(addr != 0)) {
            log("!(addr!=0)");
            throw;
        }
        if (!(subAddr != 0)) {
            log("!(subAddr!=0)");
            throw;
        }
        if (!(nonce != 0)) {
            log("!(nonce!=0)");
            throw;
        }
        if (!(bytes(token).length > 0 && bytes(token).length <= USER_IPFS_TOKEN_MAXSIZE)) {
            log("!(bytes(token).length>0 && bytes(token).length<=USER_IPFS_TOKEN_MAXSIZE)");
            throw;
        }

        userIpfsTokens[addr][subAddr] = ipfs_metadata(nonce, token);
        userIpfsTokenPuted(addr, subAddr, nonce, token);
    }

    function deleteUserIpfsToken(address addr, address subAddr) onlyOperator public {
        if (!(addr != 0)) {
            log("!(addr!=0)");
            throw;
        }
        if (!(subAddr != 0)) {
            log("!(subAddr!=0)");
            throw;
        }

        delete userIpfsTokens[addr][subAddr];
        userIpfsTokenDeleted(addr, subAddr, 0, "");
    }

    function getUserIpfsToken(address addr, address subAddr) public constant returns (address _addr, address _subAddr, uint256 _nonce, string _token){
        if (!(addr != 0)) {
            log("!(addr!=0)");
            throw;
        }
        if (!(subAddr != 0)) {
            log("!(subAddr!=0)");
            throw;
        }

        return (addr, subAddr, userIpfsTokens[addr][subAddr].nonce, userIpfsTokens[addr][subAddr].token);
    }
}