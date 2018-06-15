pragma solidity ^0.4.2;

import "../permission/OperatorPermission.sol";
import "../utils/ByteUtils.sol";
contract TagService is OperatorPermission {

    using ByteUtils for bytes;
    mapping(address => mapping( uint256 => string)) public tags;

    uint256[] public tagKeys;

    uint256 public tagKeysMaxIndex=1;

    mapping(uint256=>string) public tagNames;

    uint256 constant TAGNAME_MAXSIZE=50;
    uint256 constant TAGVALUE_MAXSIZE=10*1024;

    event tagAdded(address indexed applicant, string tagName,string tagValue);

    function TagService(){
        register("TagServiceModule", "0.0.1.0", "TagService", "0.0.1.0");
        tagKeys.push(0);
        tagNames[0]="";
    }

    function addTag(address add, string tagName,string tagValue)public{
        onlyOperator();
        if(!(add!=0)){
            log("!(add!=0)");
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

        uint256 tagNameIndex= getExistedTagKey(tagName);
        if(tagNameIndex==0){
            if(!(false)){
                log("!(false)");
                throw;
            }
        }
        tags[add][tagNameIndex]=tagValue;
        tagAdded(add,tagName,tagValue);
    }

    function getTag(address addr,string tagName) public constant returns(address _addr,uint256 _tagNameIndex,
        string _tagName,string _tagValue){

        if(!(addr!=0)){
            log("!(addr!=0)");
            throw;
        }
        if(!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)){
            log("!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)");
            throw;
        }

        uint256 tagNameIndex= getExistedTagKey(tagName);
        if(tagNameIndex==0){
            if(!(false)){
                log("!(false)");
                throw;
            }
        }
        return(addr,tagNameIndex,tagName,tags[addr][tagNameIndex]);
    }

    function addTagName(string tagName) public {
        onlyOperator();
        if(!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)){
            log("!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)");
            throw;
        }
        if(getExistedTagKey(tagName)!=0){
            if(!(false)){
                log("!(false)");
                throw;
            }
        }
        tagKeys.push(tagKeysMaxIndex);
        tagNames[tagKeysMaxIndex]=tagName;

        tagKeysMaxIndex++;
    }


    function deleteTageName(string tagName)public {
        onlyOperator();
        if(!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)){
            log("!(bytes(tagName).length>0 && bytes(tagName).length<=TAGNAME_MAXSIZE)");
            throw;
        }

        uint256 tagNameIndex= getExistedTagKey(tagName);
        if(tagNameIndex==0){
            if(!(false)){
                log("!(false)");
                throw;
            }
        }
        for(uint256 i=tagNameIndex;i<tagKeys.length-1;i++){
            tagKeys[i]=tagKeys[i+1];
        }

        tagKeys.length--;
        delete tagNames[tagNameIndex];
    }

    function getExistedTagKey(string tagName) internal returns(uint256){
        for(uint256 i=1;i<tagKeys.length;i++){
            uint256 key=tagKeys[i];
            if(bytes(tagName).bytesEqual(bytes(tagNames[key]))){
                return key;
            }
        }
        return 0;
    }

    function tagKeysLength()public constant returns(uint256){
        return tagKeys.length;
    }

}