/**
*@file      LibAction.sol
*@author    kelvin
*@time      2016-11-29
*@desc      the defination of LibAction
*/
pragma solidity ^0.4.2;

import "../utillib/LibInt.sol";
import "../utillib/LibString.sol";

library LibAction {

    using LibInt for *;
    using LibString for *;
    
    /** @brief User state: invalid, valid, activated */
    enum ActionState { INVALID, VALID, ACTIVATE }

    /** @brief Action Structure  */
    struct Action {
        string      moduleName;
        string      moduleVersion;
        string      id;
        string      name;
        string      contractId;
        string      moduleId;
        uint        level;
        uint        Type;
        uint        enable;         // 0 false ,1 true
        string      parentId;
        string      url;
        string      description;
        string      resKey;
        string      opKey;
        string      version;
        uint        createTime;
        uint        updateTime;
        ActionState state;
        address     creator; 
    }


    function toJson(Action storage _self) internal returns(string _strjson) {
        if (_self.state == ActionState.INVALID)
        {
            _strjson = "";
            return _strjson;
        }

        _strjson = "{";
        _strjson = _strjson.concat(_self.moduleName.toKeyValue("moduleName"), ",");
        _strjson = _strjson.concat(_self.moduleVersion.toKeyValue("moduleVersion"), ",");
        _strjson = _strjson.concat(_self.id.toKeyValue("id"), ",");
        _strjson = _strjson.concat(_self.name.toKeyValue("name"), ",");

        _strjson = _strjson.concat(_self.moduleId.toKeyValue("moduleId"), ",");
        _strjson = _strjson.concat(_self.contractId.toKeyValue("contractId"), ",");

        _strjson = _strjson.concat(uint(_self.level).toKeyValue("level"), ",");
        _strjson = _strjson.concat(uint(_self.Type).toKeyValue("type"), ",");
        _strjson = _strjson.concat(uint(_self.enable).toKeyValue("enable"), ",");
        _strjson = _strjson.concat(_self.parentId.toKeyValue("parentId"), ",");
        _strjson = _strjson.concat(_self.url.toKeyValue("url"), ",");
        _strjson = _strjson.concat(_self.description.toKeyValue("description"), ",");
        _strjson = _strjson.concat(_self.resKey.toKeyValue("resKey"), ",");
        _strjson = _strjson.concat(_self.opKey.toKeyValue("opKey"), ",");
        _strjson = _strjson.concat(_self.version.toKeyValue("version"), ",");
        _strjson = _strjson.concat(uint(_self.createTime).toKeyValue("createTime"), ",");
        _strjson = _strjson.concat(uint(_self.updateTime).toKeyValue("updateTime"), "}");
    }

    function jsonParse(Action storage _self, string _strjson) internal returns(bool) {
        _self.moduleName = _strjson.getStringValueByKey("moduleName");
        _self.moduleVersion = _strjson.getStringValueByKey("moduleVersion");
        _self.id = _strjson.getStringValueByKey("id");
        _self.name = _strjson.getStringValueByKey("name");
        _self.contractId = _strjson.getStringValueByKey("contractId");
        _self.moduleId = _strjson.getStringValueByKey("moduleId");
        _self.level = uint(_strjson.getIntValueByKey("level"));
        _self.Type = uint(_strjson.getIntValueByKey("type"));
        _self.enable = uint(_strjson.getIntValueByKey("enable"));
        _self.parentId = _strjson.getStringValueByKey("parentId");
        _self.url = _strjson.getStringValueByKey("url");
        _self.description = _strjson.getStringValueByKey("description");
        _self.resKey = _strjson.getStringValueByKey("resKey");
        _self.opKey = _strjson.getStringValueByKey("opKey");
        _self.version = _strjson.getStringValueByKey("version");
        if (_self.id.equals("") && _self.createTime == 0)
            return false;

        return true;
    }

    // reset the action member
    function resetAction(Action storage _self) internal{
        _self.moduleName = "";
        _self.moduleVersion = "";
        _self.id = "";
        _self.name = "";
        _self.moduleId = "";
        _self.contractId = "";
        _self.level = 0;
        _self.Type = 0;
        _self.enable = 0;
        _self.parentId = "";
        _self.url = "";
        _self.description = "";
        _self.resKey  = "";
        _self.opKey = "";
        _self.createTime = 0;
        _self.updateTime = 0;
        _self.version = "";
        _self.state = ActionState.INVALID;
    }
}
