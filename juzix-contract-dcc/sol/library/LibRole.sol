/**
* @file LibRole.sol
* @author liaoyan
* @time 2016-11-29
* @desc the defination of Role library
*/
pragma solidity ^0.4.2;
 
import "../utillib/LibInt.sol";
import "../utillib/LibString.sol";

library LibRole {
    using LibInt for *;
    using LibString for *;
    using LibRole for *;

    /**
    * @dev Contruction
    */
    struct Role {
        string moduleName;
        string moduleVersion;
        string id;
        string name;
        uint status;
        string moduleId;
        string contractId;             // -
        string description;
        uint creTime;
        uint updTime;
        bool deleted;
        string[] actionIdList;
        address creator;
    }

    /**
    * @dev Convert self to json string
    * @param _self Self object
    * @return Return the converted json string
    */
    function toJson(Role storage _self) internal constant returns(string _json) {
        _json = _json.concat("{");
        _json = _json.concat( _self.moduleName.toKeyValue("moduleName"), "," );
        _json = _json.concat( _self.moduleVersion.toKeyValue("moduleVersion"), "," );
        _json = _json.concat( _self.id.toKeyValue("id"), "," );
        _json = _json.concat( _self.name.toKeyValue("name"), "," );
        _json = _json.concat( _self.moduleId.toKeyValue("moduleId"), "," );
        _json = _json.concat( _self.contractId.toKeyValue("contractId"), "," );
        _json = _json.concat( uint(_self.status).toKeyValue("status"), "," );
        _json = _json.concat( _self.description.toKeyValue("description"), "," );
        _json = _json.concat( uint(_self.creTime).toKeyValue("creTime"), "," );
        _json = _json.concat( uint(_self.updTime).toKeyValue("updTime"), "," );
        _json = _json.concat( _self.actionIdList.toKeyValue("actionIdList"),"," );
        _json = _json.concat( uint(_self.creator).toAddrString().toKeyValue("creator"));
        _json = _json.concat("}");
    }

    /**
    * @dev Construct self from json string
    * @param _self Self object
    * @param _json The json string
    * @return _succ The result of Construction, true or false
    */
    function fromJson(Role storage _self, string _json) internal constant returns(bool _succ) {
        _self.clear();

        _self.moduleName = _json.getStringValueByKey("moduleName");
        _self.moduleVersion = _json.getStringValueByKey("moduleVersion");
        _self.id = _json.getStringValueByKey("id");
        _self.name = _json.getStringValueByKey("name");
        _self.moduleId = _json.getStringValueByKey("moduleId");
        _self.contractId = _json.getStringValueByKey("contractId");
        _self.status = uint(_json.getIntValueByKey("status"));
        _self.description = _json.getStringValueByKey("description");
        _self.creTime = uint(_json.getIntValueByKey("creTime"));
        _self.updTime = uint(_json.getIntValueByKey("updTime"));
        _json.getArrayValueByKey("actionIdList", _self.actionIdList);
        _self.creator = _json.getStringValueByKey("creator").toAddress();

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
    function clear(Role storage _self) internal {
        _self.moduleName = "";
        _self.moduleVersion = "";
        _self.id = "";
        _self.name = "";
        _self.moduleId = "";
        _self.contractId = "";
        _self.status = 0;
        _self.description = "";
        _self.creTime = 0;
        _self.updTime = 0;
        _self.deleted = false;
        delete _self.actionIdList;
        _self.creator = address(0);
    }
}