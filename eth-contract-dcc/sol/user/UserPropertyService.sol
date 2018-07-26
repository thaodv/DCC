pragma solidity ^0.4.2;

import "../ownership/OperatorPermission.sol";
import "../utils/ByteUtils.sol";
contract UserPropertyService is OperatorPermission {

    using ByteUtils for bytes;
    mapping(address => mapping(uint256 => string)) public properties;

    uint256[] public propertyKeys;

    uint256 public maxPropertyKey = 1;

    mapping(uint256 => string) public propertyNames;

    uint256 constant PROPERTYNAME_MAXSIZE = 50;
    uint256 constant PROPERTYVALUE_MAXSIZE = 10 * 1024;

    event propertyPut(address indexed applicant, string propertyName, string propertyValue);
    event propertyDeleted(address indexed applicant, string propertyName, string propertyValue);
    event propertyNameAdded(uint256 indexed key, string propertyName);
    event propertyNameDeleted(uint256 indexed key, string propertyName);

    function putProperty(address addr, string propertyName, string propertyValue) public onlyOperator {
        require(addr != 0);
        require(bytes(propertyName).length > 0 && bytes(propertyName).length <= PROPERTYNAME_MAXSIZE);
        require(bytes(propertyValue).length > 0 && bytes(propertyValue).length <= PROPERTYVALUE_MAXSIZE);

        var (key,) = getPropertyKey(propertyName);
        if (key == 0) {
            require(false);
        }
        properties[addr][key] = propertyValue;
        propertyPut(addr, propertyName, propertyValue);
    }

    function deleteProperty(address addr, string propertyName) public onlyOperator {
        require(addr != 0);
        require(bytes(propertyName).length > 0 && bytes(propertyName).length <= PROPERTYNAME_MAXSIZE);

        var (key,) = getPropertyKey(propertyName);
        if (key == 0) {
            require(false);
        }
        delete properties[addr][key];
        propertyDeleted(addr, propertyName, "");
    }

    function getProperty(address addr, string propertyName) public view returns (address _addr,
        string _propertyName, string _propertyValue){

        require(addr != 0);
        require(bytes(propertyName).length > 0 && bytes(propertyName).length <= PROPERTYNAME_MAXSIZE);

        var (key,) = getPropertyKey(propertyName);
        if (key == 0) {
            require(false);
        }
        return (addr, propertyName, properties[addr][key]);
    }

    function addPropertyName(string propertyName) public onlyOwner {
        require(bytes(propertyName).length > 0 && bytes(propertyName).length <= PROPERTYNAME_MAXSIZE);
        var (key,) = getPropertyKey(propertyName);
        if (key != 0) {
            require(false);
        }
        propertyKeys.push(maxPropertyKey);
        propertyNames[maxPropertyKey] = propertyName;
        propertyNameAdded(maxPropertyKey, propertyName);

        maxPropertyKey++;
    }

    function deletePropertyName(string propertyName) public onlyOwner {
        require(bytes(propertyName).length > 0 && bytes(propertyName).length <= PROPERTYNAME_MAXSIZE);

        var (key, keyIndex) = getPropertyKey(propertyName);
        if (key == 0) {
            require(false);
        }
        for (uint256 i = keyIndex; i < propertyKeys.length - 1; i++) {
            propertyKeys[i] = propertyKeys[i + 1];
        }

        propertyKeys.length--;
        delete propertyNames[key];
        propertyNameDeleted(key, propertyName);
    }

    function getPropertyKey(string propertyName) public view returns (uint256 key, uint256 keyIndex){
        for (keyIndex = 0; keyIndex < propertyKeys.length; keyIndex++) {
            key = propertyKeys[keyIndex];
            if (bytes(propertyName).bytesEqual(bytes(propertyNames[key]))) {
                return (key, keyIndex);
            }
        }
        return (0, 0);
    }

    function propertyKeysLength() public view returns (uint256){
        return propertyKeys.length;
    }

}