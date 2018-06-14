pragma solidity ^0.4.2;

import "../ownership/OperatorPermission.sol";
import "../utils/ByteUtils.sol";

contract TagService is OperatorPermission {

    using ByteUtils for bytes;
    mapping(address => mapping( uint256 => string)) public tags;

    mapping(address=>uint256[]) tagKeys;

    string[] tagNames;

    event tagAdded(address indexed applicant, string tagName,string tagValue);

    function TagService(){
        uint256 index=tagNames.length;
        tagNames.push("");
        tagKeys[0].push(index);
        tags[0][index]="";
    }

    function addTag(address add, string tagName,string tagValue)public onlyOperator{
        require(add!=0);
        require(bytes(tagName).length>0);
        require(bytes(tagValue).length>0);

        //判读当前地址是否存在标签，如果存在就更新标签的值，然后退出
        uint256 index=getExistTagNameIndex(add,tagName);
        if(getExistTagNameIndex(add,tagName)!=0){
            tags[add][index]=tagValue;
            return;
        }

        //当前地址添加新标签
        index=tagNames.length;
        tagNames.push(tagName);
        tagKeys[add].push(index);
        tags[add][index]=tagValue;

        tagAdded(add,tagName,tagValue);
    }

    function getTag(address addr,string tagName) public view returns(address,uint256, string,string){
        require(addr!=0);
        require(bytes(tagName).length>0);

        uint256 tagNameIndex= getExistTagNameIndex(addr,tagName);
        if(tagNameIndex!=0){
            return(addr,tagNameIndex,tagName,tags[addr][tagNameIndex]);
        }
        return(addr,tagNameIndex,tagName,"");
    }

    function getExistTagNameIndex(address addr,string tagName) internal returns(uint256){
        uint256[] memory keys= tagKeys[addr];
        for(uint i=0;i<keys.length;i++){
            if(bytes(tagName).bytesEqual(bytes(tagNames[keys[i]]))){
                return keys[i];
            }
        }
        return 0;
    }
}