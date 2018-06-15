pragma solidity ^0.4.2;

import "../ownership/OperatorPermission.sol";
import "../utils/ByteUtils.sol";
contract TagService is OperatorPermission {

    using ByteUtils for bytes;
    mapping(address => mapping( uint256 => string)) public tags;

    uint256[] public tagKeys;

    mapping(uint256=>string) public tagNames;

    uint256 constant TAGNAME_MAXSIZE=50;
    uint256 constant TAGVALUE_MAXSIZE=10*1024;

    event tagAdded(address indexed applicant, string tagName,string tagValue);

    function TagService(){
        tagKeys.push(0);
        tagNames[0]="";
    }

    function getExistedTagNameIndex(string tagName) public view returns(uint256){
        for(uint256 i=1;i<tagKeys.length;i++){
            uint256 key=tagKeys[i];
            if(bytes(tagName).bytesEqual(bytes(tagNames[key]))){
                return key;
            }
        }
        return 0;
    }

    function addTagName(string tagName) public onlyOperator returns(uint256){
        require(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE);
        if(getExistedTagNameIndex(tagName)==0){
            uint256 index=tagKeys.length;
            tagKeys.push(index);
            tagNames[index]=tagName;
        }
        return index;
    }

    function deleteTageName(string tagName)public onlyOperator{
        require(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE);

        uint256 tagNameIndex= getExistedTagNameIndex(tagName);
        if(tagNameIndex==0){
            require(false);
        }
        for(uint256 i=tagNameIndex;i<tagKeys.length-1;i++){
            tagKeys[i]=tagKeys[i+1];
        }

        tagKeys.length--;
        delete tagNames[tagNameIndex];
    }

    function addTag(address add, string tagName,string tagValue)public onlyOperator{
        require(add!=0);
        require(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE);
        require(bytes(tagValue).length>0 && bytes(tagValue).length<=TAGVALUE_MAXSIZE);

        uint256 tagNameIndex= getExistedTagNameIndex(tagName);
        if(tagNameIndex==0){
            require(false);
        }
        tags[add][tagNameIndex]=tagValue;
        tagAdded(add,tagName,tagValue);
    }

    function getTag(address addr,string tagName) public view returns(address _addr,uint256 _tagNameIndex,
        string _tagName,string _tagValue){

        require(addr!=0);
        require(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE);

        uint256 tagNameIndex= getExistedTagNameIndex(tagName);
        if(tagNameIndex==0){
            require(false);
        }
        return(addr,tagNameIndex,tagName,tags[addr][tagNameIndex]);
    }

    function tagKeysLength()public view returns(uint256){
        return tagKeys.length;
    }

}