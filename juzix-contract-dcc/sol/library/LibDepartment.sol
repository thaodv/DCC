/**
* @file LibDepartment.sol
* @author liaoyan
* @time 2016-12-01
* @desc the defination of Department library
*/
pragma solidity ^0.4.2;
 
import "../utillib/LibInt.sol";
import "../utillib/LibString.sol";

library LibDepartment {
    using LibInt for *;
    using LibString for *;
    using LibDepartment for *;

    /* struct Enode {              // *
        string pubkey;
        string ip;
        uint port;
        uint activated;
    } */

    /**
    * @dev Contruction
    */
    struct Department {
        string id;
        string name;
        uint departmentLevel;
        //uint serialNum;                 // * 不明
        string parentId;
        string description;
        uint creTime;
        uint updTime;
        string commonName;
        string stateName;
        string countryName;
        //string email;                   // *
        //uint _type;                     // *
        address admin;                  
        address creator;                
        bool deleted;
        string orgaShortName;           // -
        //Enode[] enodeList;            // *
        //string[] roleIdList;          // *
        //uint[] fileIdList;            // * 
        uint status;                    // 0 禁用 1 激活
        string groupPubkey;             // 群公钥
        string icon;                
    }

    /**
    * @dev Convert self to json string
    * @param _self Self object
    * @return Return the converted json string
    */
    function toJson(Department storage _self) internal constant returns(string _json) {
        _json = _json.concat("{");
        _json = _json.concat( _self.id.toKeyValue("id"), "," );
        _json = _json.concat( _self.name.toKeyValue("name"), "," );
        _json = _json.concat( uint(_self.departmentLevel).toKeyValue("departmentLevel"), "," );
        //_json = _json.concat( uint(_self.serialNum).toKeyValue("serialNum"), "," );
        _json = _json.concat( _self.parentId.toKeyValue("parentId"), "," );
        _json = _json.concat( _self.description.toKeyValue("description"), "," );
        _json = _json.concat( uint(_self.creTime).toKeyValue("creTime"), "," );
        _json = _json.concat( uint(_self.updTime).toKeyValue("updTime"), "," );
        _json = _json.concat( _self.commonName.toKeyValue("commonName"), "," );
        _json = _json.concat( _self.stateName.toKeyValue("stateName"), "," );
        _json = _json.concat( _self.countryName.toKeyValue("countryName"), "," );
        //_json = _json.concat( _self.email.toKeyValue("email"), "," );
        //_json = _json.concat( uint(_self._type).toKeyValue("type"), "," );
        _json = _json.concat( _self.orgaShortName.toKeyValue("orgaShortName"),"," );
        _json = _json.concat( uint(_self.admin).toAddrString().toKeyValue("admin"), "," );
        /* _json = _json.concat("\"enodeList\":[");
        for (uint i=0; i<_self.enodeList.length; ++i) {
            if (i > 0) {
                _json = _json.concat(",");
            }
            _json = _json.concat("{");
            _json = _json.concat( _self.enodeList[i].pubkey.toKeyValue("pubkey"), "," );
            _json = _json.concat( _self.enodeList[i].ip.toKeyValue("ip"), "," );
            _json = _json.concat( uint(_self.enodeList[i].port).toKeyValue("port"), "," );
            _json = _json.concat( uint(_self.enodeList[i].activated).toKeyValue("activated") );
            _json = _json.concat("}");
        }
        _json = _json.concat("],"); */
        _json = _json.concat( uint(_self.status).toKeyValue("status"),",");
        _json = _json.concat( _self.icon.toKeyValue("icon"),",");
        _json = _json.concat( _self.groupPubkey.toKeyValue("groupPubkey"));
        //_json = _json.concat( _self.roleIdList.toKeyValue("roleIdList") );
        _json = _json.concat("}");
    }

    /**
    * @dev Construct self from json string
    * @param _self Self object
    * @param _json The json string
    * @return succ The result of Construction, true or false
    */
    function fromJson(Department storage _self, string _json) internal constant returns(bool succ) {
        _self.clear();

        _self.id = _json.getStringValueByKey("id");
        _self.name = _json.getStringValueByKey("name");
        _self.departmentLevel = uint(_json.getIntValueByKey("departmentLevel"));
        //_self.serialNum = uint(_json.getIntValueByKey("serialNum"));
        _self.parentId = _json.getStringValueByKey("parentId");
        _self.description = _json.getStringValueByKey("description");
        _self.creTime = uint(_json.getIntValueByKey("creTime"));
        _self.updTime = uint(_json.getIntValueByKey("updTime"));
        _self.commonName = _json.getStringValueByKey("commonName");
        _self.stateName = _json.getStringValueByKey("stateName");
        _self.countryName = _json.getStringValueByKey("countryName");
        //_self.email = _json.getStringValueByKey("email");
        //_self._type = uint(_json.getIntValueByKey("type"));
        _self.orgaShortName = _json.getStringValueByKey("orgaShortName");
        _self.admin = _json.getStringValueByKey("admin").toAddress();
        _self.status = uint(_json.getIntValueByKey("status"));
        _self.groupPubkey = _json.getStringValueByKey("groupPubkey");
        /* string memory enodeListStr = _json.getArrayValueByKey("enodeList");
        while (true) {
            string memory enodeStr;
            int braketPos = enodeListStr.indexOf("}"); "{"; //"{" compiler bug
            if (braketPos > 0) {
                enodeStr = enodeListStr.substr(0, uint(braketPos)+1);
                enodeListStr = enodeListStr.substr(uint(braketPos)+1, uint(-1));
            } else {
                enodeStr = enodeListStr;
            }

            enodeStr = enodeStr.trim();
            if (bytes(enodeStr).length == 0) {
                break;
            }

            Enode memory enode;
            enode.pubkey = enodeStr.getStringValueByKey("pubkey");
            enode.ip = enodeStr.getStringValueByKey("ip");
            enode.port = uint(enodeStr.getIntValueByKey("port"));
            enode.activated = uint(enodeStr.getIntValueByKey("activated"));

            _self.enodeList.push(enode);

            if (braketPos <= 0) {
                break;
            }
        } */

        //_json.getArrayValueByKey("roleIdList", _self.roleIdList);
        
        if (bytes(_self.id).length == 0) {
            return false;
        }

        return true;
    }

    /**
    * @dev Clear self data
    * @param _self Self object
    * @return No return
    */
    function clear(Department storage _self) internal {
        _self.admin = 0;
        _self.id = "";
        _self.name = "";
        _self.departmentLevel = 0;
        //_self.serialNum = 0;
        _self.parentId = "";
        _self.description = "";
        _self.creTime = 0;
        _self.updTime = 0;
        _self.commonName = "";
        _self.stateName = "";
        _self.countryName = "";
        //_self.email = "";
        _self.orgaShortName = "";
        //_self._type = 0;
        //delete _self.enodeList;
        //delete _self.roleIdList;
        _self.deleted = false;
        _self.status=1;
        _self.groupPubkey = "";
        _self.icon = "";
    }
}
