pragma solidity ^0.4.2;

import "../ownership/OperatorPermission.sol";
import "../utils/ByteUtils.sol";

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

    function putTag(address addr, string tagName, string tagValue) public onlyOperator {
        require(addr != 0);
        require(bytes(tagName).length > 0 && bytes(tagName).length <= TAGNAME_MAXSIZE);
        require(bytes(tagValue).length > 0 && bytes(tagValue).length <= TAGVALUE_MAXSIZE);

        var (key,) = getTagKey(tagName);
        if (key == 0) {
            require(false);
        }
        tags[addr][key] = tagValue;
        tagPuted(addr, tagName, tagValue);
    }

    function deleteTag(address addr, string tagName) public onlyOperator {
        require(addr != 0);
        require(bytes(tagName).length > 0 && bytes(tagName).length <= TAGNAME_MAXSIZE);

        var (key,) = getTagKey(tagName);
        if (key == 0) {
            require(false);
        }
        delete tags[addr][key];
        tagDeleted(addr, tagName, "");
    }

    function getTag(address addr, string tagName) public view returns (address _addr,
        string _tagName, string _tagValue){

        require(addr != 0);
        require(bytes(tagName).length > 0 && bytes(tagName).length <= TAGNAME_MAXSIZE);

        var (key,) = getTagKey(tagName);
        if (key == 0) {
            require(false);
        }
        return (addr, tagName, tags[addr][key]);
    }

    function addTagName(string tagName) public onlyOwner {
        require(bytes(tagName).length > 0 && bytes(tagName).length <= TAGNAME_MAXSIZE);
        var (key,) = getTagKey(tagName);
        if (key != 0) {
            require(false);
        }
        tagKeys.push(maxTagKey);
        tagNames[maxTagKey] = tagName;
        tagNameAdded(maxTagKey, tagName);

        maxTagKey++;
    }

    function deleteTagName(string tagName) public onlyOwner {
        require(bytes(tagName).length > 0 && bytes(tagName).length <= TAGNAME_MAXSIZE);

        var (key, keyIndex) = getTagKey(tagName);
        if (key == 0) {
            require(false);
        }
        for (uint256 i = keyIndex; i < tagKeys.length - 1; i++) {
            tagKeys[i] = tagKeys[i + 1];
        }

        tagKeys.length--;
        delete tagNames[key];
        tagNameDeleted(key, tagName);
    }

    function getTagKey(string tagName) public view returns (uint256 key, uint256 keyIndex){
        for (keyIndex = 0; keyIndex < tagKeys.length; keyIndex++) {
            key = tagKeys[keyIndex];
            if (bytes(tagName).bytesEqual(bytes(tagNames[key]))) {
                return (key, keyIndex);
            }
        }
        return (0, 0);
    }

    function tagKeysLength() public view returns (uint256){
        return tagKeys.length;
    }

}