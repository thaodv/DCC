/**
*@file      LibContract.sol
*@author    anrui
*@time      2017-05-08
*@desc      the defination of LibContract
*/
pragma solidity ^0.4.2;

import '../utillib/LibInt.sol';
import '../utillib/LibString.sol';
import '../interfaces/IRegisterManager.sol';

library LibContract{

	using LibInt for *;
	using LibString for *;
	using LibContract for *;

	struct OpenAction{
		string id;
		string funcHash;
	}

	struct ContractInfo{
		string 					contractId;
		string 					contractName;
		string 					contractVersion;
	}

	struct Contract{
		string 					moduleName;
		string 					moduleVersion;
		string 				moduleId;
		string 		   		cctId;
		string 		   		cctName;
		string 		   		cctVersion;
		bool				deleted;
		uint 		   		enable;
		string 		   		description;
		uint 		   		createTime;
		uint 		   		updateTime;
		address		  		creator;
		string[] 	   		actionIdList;
		//string[]  	  		openMenuIdList;
		OpenAction[]   		openActionList;
		uint				blockNum;
	}

	function toJson(Contract storage _self) internal constant returns (string _json) {

		_json = _json.concat("{");
		_json = _json.concat( _self.moduleId.toKeyValue("moduleId"),"," );
		_json = _json.concat( _self.cctId.toKeyValue("cctId"),",");
		_json = _json.concat( _self.moduleName.toKeyValue("moduleName"),"," );
		_json = _json.concat( _self.moduleVersion.toKeyValue("moduleVersion"),"," );
		_json = _json.concat( _self.cctName.toKeyValue("cctName"),",");
		_json = _json.concat( _self.cctVersion.toKeyValue("cctVersion"),",");
		if (_self.deleted)
				_json = _json.concat("\"deleted\":true,");
		else
				_json = _json.concat("\"deleted\":false,");
		_json = _json.concat( uint(_self.enable).toKeyValue("enable"),",");
		_json = _json.concat( _self.description.toKeyValue("description"),",");
		_json = _json.concat( uint(_self.createTime).toKeyValue("createTime"),",");
		_json = _json.concat( uint(_self.updateTime).toKeyValue("updateTime"),",");
		_json = _json.concat( uint(_self.creator).toAddrString().toKeyValue("creator"),"," );
		IRegisterManager rm = IRegisterManager(0x0000000000000000000000000000000000000011);
		address cctAddr = rm.getContractAddress(_self.moduleName, _self.moduleVersion, _self.cctName, _self.cctVersion);
		_json = _json.concat( uint(cctAddr).toAddrString().toKeyValue("cctAddr"),"," );
		_json = _json.concat( _self.actionIdList.toKeyValue("actionIdList"),"," );
		//_json = _json.concat( _self.openMenuIdList.toKeyValue("openMenuIdList"),"," );
		_json = _json.concat( uint(_self.blockNum).toKeyValue("blockNum"),",");

		// build openAction
		_json = _json.concat("\"openActionList\":[");
		for(uint i = 0; i<_self.openActionList.length; ++i){
			if(i > 0){
				_json = _json.concat(",");
			}
			_json = _json.concat("{");
			_json = _json.concat(_self.openActionList[i].id.toKeyValue("id"),",");
			_json = _json.concat(_self.openActionList[i].funcHash.toKeyValue("funcHash"));
			_json = _json.concat("}");
		}

		_json = _json.concat("]}");
	}

	function fromJson(Contract storage _self, string _json) internal constant returns(bool succ){
		
		_self.clear();
		_self.moduleId = _json.getStringValueByKey("moduleId");
		_self.cctId = _json.getStringValueByKey("cctId");
		_self.moduleName = _json.getStringValueByKey("moduleName");
		_self.moduleVersion = _json.getStringValueByKey("moduleVersion");
		_self.cctName = _json.getStringValueByKey("cctName");
		_self.cctVersion = _json.getStringValueByKey("cctVersion");
		_self.enable = uint(_json.getIntValueByKey("enable"));
		_self.description = _json.getStringValueByKey("description");
		_self.createTime = uint(_json.getIntValueByKey("createTime"));
		_self.updateTime = uint(_json.getIntValueByKey("updateTime"));
		_self.creator = _json.getStringValueByKey("creator").toAddress();
		_self.blockNum = uint(_json.getIntValueByKey("blockNum"));

		// get data from storage array
		_json.getArrayValueByKey("actionIdList", _self.actionIdList);
		//_json.getArrayValueByKey("openMenuIdList", _self.openMenuIdList);

		string memory openActionListStr = _json.getArrayValueByKey("openActionList");
		while(true){
			string memory openActionStr;
			int braketPos = openActionListStr.indexOf("}"); "{";
			if(braketPos > 0) {
				openActionStr = openActionListStr.substr(0, uint(braketPos) + 1);
				openActionListStr = openActionListStr.substr(uint(braketPos) + 1, uint(-1));
			}else{
				openActionStr = openActionListStr;
			}
			openActionStr = openActionStr.trim();
			if (bytes(openActionStr).length == 0){
				break;
			}

			OpenAction memory action;
           	action.id = openActionStr.getStringValueByKey("id");
			action.funcHash = openActionStr.getStringValueByKey("funcHash");
            _self.openActionList.push(action);

            if(braketPos <= 0){
                break;
            }

		}

		return true;
	}

	function clear(Contract storage _self) internal {
		_self.moduleId = "";
		_self.cctId = "";
		_self.moduleName = "";
		_self.moduleVersion = "";
		_self.cctName = "";
		_self.cctVersion = "";
		_self.enable = 0;
		_self.deleted = false;
		_self.description = "";
		_self.createTime = 0;
		_self.updateTime = 0;
		_self.creator = address(0);
		delete _self.actionIdList;
		//delete _self.openMenuIdList;
		delete _self.openActionList;
		_self.blockNum = 0;
	}
}
