pragma solidity ^0.4.2;

import "../permission/OperatorPermission.sol";
import "../utils/ByteUtils.sol";
//标签认证合约
contract TagService is OperatorPermission {

    using ByteUtils for bytes;
    mapping(address => mapping(uint256 => string)) public tags;

    uint256[] public tagKeys;

    uint256 public maxTagKey = 1;

    mapping(uint256 => string) public tagNames;

    uint256 constant TAGNAME_MAXSIZE = 50;
    uint256 constant TAGVALUE_MAXSIZE = 10 * 1024;

    event tagPuted(address indexed applicant, string tagName, string tagValue);
    event tagDeleted(address indexed applicant, string tagName, string tagValue);
    event tagNameAdded(uint256 indexed key, string tagName);
    event tagNameDeleted(uint256 indexed key, string tagName);

    function TagService(){
        register("TagServiceModule", "0.0.1.0", "TagService", "0.0.1.0");
    }

    function putTag(address addr, string tagName, string tagValue) public {
        onlyOperator();
        if(!(addr!=0)){
            log("!(addr!=0)");
            throw;
        }
        if(!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)){
            log("!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)");
            throw;
        }
        if(!(bytes(tagValue).length>0 && bytes(tagValue).length<=TAGVALUE_MAXSIZE)){
            log("!(bytes(tagValue).length>0 && bytes(tagValue).length<=TAGVALUE_MAXSIZE)");
            throw;
        }

        var (key,) = getTagKey(tagName);
        if (key == 0) {
            if(!(false)){
                log("!(false)");
                throw;
            }
        }
        tags[addr][key] = tagValue;
        tagPuted(addr, tagName, tagValue);
    }

    function deleteTag(address addr, string tagName) public {
        onlyOperator();
        if(!(addr!=0)){
            log("!(addr!=0)");
            throw;
        }
        if(!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)){
            log("!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)");
            throw;
        }

        var (key,) = getTagKey(tagName);
        if (key == 0) {
            if(!(false)){
                log("!(false)");
                throw;
            }
        }
        delete tags[addr][key];
        tagDeleted(addr, tagName, "");
    }

    function getTag(address addr, string tagName) public constant returns (address _addr,
        string _tagName, string _tagValue){

        if(!(addr!=0)){
            log("!(addr!=0)");
            throw;
        }
        if(!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)){
            log("!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)");
            throw;
        }

        var (key,) = getTagKey(tagName);
        if (key == 0) {
            if(!(false)){
                log("!(false)");
                throw;
            }
        }
        return (addr, tagName, tags[addr][key]);
    }

    function addTagName(string tagName) public {
        onlyOwner();
        if(!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)){
            log("!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)");
            throw;
        }
        var (key,) = getTagKey(tagName);
        if (key != 0) {
            if(!(false)){
                log("!(false)");
                throw;
            }
        }
        tagKeys.push(maxTagKey);
        tagNames[maxTagKey] = tagName;
        tagNameAdded(maxTagKey, tagName);

        maxTagKey++;
    }

    function deleteTagName(string tagName) public {
        onlyOwner();
        if(!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)){
            log("!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)");
            throw;
        }

        var (key, keyIndex) = getTagKey(tagName);
        if (key == 0) {
            if(!(false)){
                log("!(false)");
                throw;
            }
        }
        for (uint256 i = keyIndex; i < tagKeys.length - 1; i++) {
            tagKeys[i] = tagKeys[i + 1];
        }

        tagKeys.length--;
        delete tagNames[key];
        tagNameDeleted(key, tagName);
    }

    function getTagKey(string tagName) public constant returns (uint256 key, uint256 keyIndex){
        for (keyIndex = 0; keyIndex < tagKeys.length; keyIndex++) {
            key = tagKeys[keyIndex];
            if (bytes(tagName).bytesEqual(bytes(tagNames[key]))) {
                return (key, keyIndex);
            }
        }
        return (0, 0);
    }

    function tagKeysLength() public constant returns (uint256){
        return tagKeys.length;
    }

}