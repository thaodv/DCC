/**
* @file LibFilter.sol
* @author ximi
* @time 2017-05-08
* @desc the defination of Filter library
*/

pragma solidity ^0.4.2;

import "../library/Errnos.sol";
import "../utillib/LibInt.sol";
import "../utillib/LibString.sol";

library LibFilter {
    
    using Errnos for *;
    using LibInt for *;
    using LibString for *;
    using LibFilter for *;
    
    /**
    * @dev Contruction
    */
    struct Filter {
        string id;
        string name;
        string version;
        uint _type;
        uint state;
        uint enable;
        string desc;
        address addr;
    }
    
    /**
    * @dev Convert self to json string
    * @param _self Self object
    * @return Return the converted json string
    */
    function toJson(Filter storage _self) internal constant returns(string _json) {
        _json = _json.concat("{");
        _json = _json.concat(_self.id.toKeyValue("id"), ",");
        _json = _json.concat(_self.name.toKeyValue("name"), ",");
        _json = _json.concat(_self.version.toKeyValue("version"), ",");
        _json = _json.concat(uint(_self._type).toKeyValue("type"), ",");
        _json = _json.concat(uint(_self.state).toKeyValue("state"), ",");
        _json = _json.concat(uint(_self.enable).toKeyValue("enable"), ",");
        _json = _json.concat(_self.desc.toKeyValue("desc"), ",");
        _json = _json.concat(uint(_self.addr).toAddrString().toKeyValue("addr"));
        _json = _json.concat("}");
    }
    
    /**
    * @dev Construct self from json string
    * @param _self Self object
    * @param _json The json string
    * @return _succ The result of Construction, true or false
    */
    function fromJson(Filter storage _self, string _json) internal constant returns(bool _succ) {
        _self.clear();
        
        _self.id = _json.getStringValueByKey("id");
        _self.name = _json.getStringValueByKey("name");
        _self.version = _json.getStringValueByKey("version");
        _self._type = uint(_json.getIntValueByKey("type"));
        _self.state = uint(_json.getIntValueByKey("state"));
        _self.enable = uint(_json.getIntValueByKey("enable"));
        _self.desc = _json.getStringValueByKey("desc");
        _self.addr = _json.getStringValueByKey("addr").toAddress();
        
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
    function clear(Filter storage _self) internal {
        _self.id = "";
        _self.name = "";
        _self.version = "";
        _self._type = uint(Errnos.FilterType.FILTER_TYPE_START);
        _self.state = uint(Errnos.State.STATE_INVALID);
        _self.enable = 0;
        _self.desc = "";
        _self.addr = 0;
    }
}