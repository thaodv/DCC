pragma solidity ^0.4.2;

import "../utillib/LibInt.sol";
import "../utillib/LibString.sol";
import "../library/LibContract.sol";

library LibModule{

	using LibInt for *;
	using LibString for *;
	using LibModule for *;
	using LibContract for *;

	struct Module{
		string 					moduleId;
		string 					moduleName;
		string 					moduleVersion;
		bool 					deleted;
		uint 					moduleEnable;			// 0 false 1 true
		string 					moduleDescription;//detail description
		string				    	moduleText;//usually chinese module name
		uint					moduleCreateTime;
		uint					moduleUpdateTime;
		address					moduleCreator;
		LibContract.ContractInfo[] 		contractInfos;
		string[] 				roleIds;
		string 					moduleUrl;
		string 					icon;
		uint 					publishTime;
		uint 					moduleType;	// 模块类型：1 系统模块
	}

	function toJson(Module storage _self) constant internal returns(string _json){
		_json = _json.concat("{");
		_json = _json.concat( _self.moduleId.toKeyValue("moduleId"),",");
		_json = _json.concat( _self.moduleName.toKeyValue("moduleName"),",");
		_json = _json.concat( _self.moduleText.toKeyValue("moduleText"),",");
		_json = _json.concat( _self.moduleVersion.toKeyValue("moduleVersion"),",");
		if (_self.deleted)
				_json = _json.concat("\"deleted\":true,");
		else
				_json = _json.concat("\"deleted\":false,");
		_json = _json.concat( uint(_self.moduleEnable).toKeyValue("moduleEnable"),",");
		_json = _json.concat( _self.moduleDescription.toKeyValue("moduleDescription"),",");
		_json = _json.concat( uint(_self.moduleCreateTime).toKeyValue("moduleCreateTime"),",");
		_json = _json.concat( uint(_self.moduleUpdateTime).toKeyValue("moduleUpdateTime"),",");
		_json = _json.concat( uint(_self.moduleCreator).toAddrString().toKeyValue("moduleCreator"),",");
		_json = _json.concat("\"contractInfos\":[");
		for(uint i = 0; i<_self.contractInfos.length; ++i){
			if(i > 0){
				_json = _json.concat(",");
			}
			_json = _json.concat("{");
			_json = _json.concat(_self.contractInfos[i].contractId.toKeyValue("contractId"),",");
			_json = _json.concat(_self.contractInfos[i].contractName.toKeyValue("contractName"),",");
			_json = _json.concat(_self.contractInfos[i].contractVersion.toKeyValue("contractVersion"));
			_json = _json.concat("}");
		}
		_json = _json.concat("],");
		_json = _json.concat( _self.moduleUrl.toKeyValue("moduleUrl"),",");
		_json = _json.concat( _self.icon.toKeyValue("icon"),",");
		_json = _json.concat( uint(_self.publishTime).toKeyValue("publishTime"),",");
		_json = _json.concat( uint(_self.moduleType).toKeyValue("moduleType"),",");
		_json = _json.concat( _self.roleIds.toKeyValue("roleIds") );

		_json = _json.concat("}");
	}

	function fromJson(Module storage _self, string _json) constant internal returns(bool succ){
		_self.clear();
		
		_self.moduleId = _json.getStringValueByKey("moduleId");
		_self.moduleName = _json.getStringValueByKey("moduleName");
		_self.moduleText = _json.getStringValueByKey("moduleText");
		_self.moduleVersion = _json.getStringValueByKey("moduleVersion");
		_self.moduleEnable = uint(_json.getIntValueByKey("moduleEnable"));
		_self.moduleDescription = _json.getStringValueByKey("moduleDescription");
		_self.moduleCreateTime = uint(_json.getIntValueByKey("moduleCreateTime"));
		_self.moduleUpdateTime = uint(_json.getIntValueByKey("moduleUpdateTime"));
		_self.moduleCreator = _json.getStringValueByKey("moduleCreator").toAddress();
		_self.moduleUrl = _json.getStringValueByKey("moduleUrl");
		_self.icon = _json.getStringValueByKey("icon");
		_self.publishTime = uint(_json.getIntValueByKey("publishTime"));
		_self.moduleType = uint(_json.getIntValueByKey("moduleType"));

		// array
		_json.getArrayValueByKey("roleIds", _self.roleIds);
		string memory contractInfos = _json.getArrayValueByKey("contractInfos");
		while(true){
			string memory contractInfo;
			int braketPos = contractInfos.indexOf("}"); "{";
			if(braketPos > 0) {
				contractInfo = contractInfos.substr(0, uint(braketPos) + 1);
				contractInfos = contractInfos.substr(uint(braketPos) + 1, uint(-1));
			}else{
				contractInfo = contractInfos;
			}
			contractInfo = contractInfo.trim();
			if (bytes(contractInfo).length == 0){
				break;
			}

			LibContract.ContractInfo memory _contractInfo;
			_contractInfo.contractName = contractInfo.getStringValueByKey("contractId");
      		_contractInfo.contractName = contractInfo.getStringValueByKey("contractName");
			_contractInfo.contractVersion = contractInfo.getStringValueByKey("contractVersion");
      		_self.contractInfos.push(_contractInfo);

      		if(braketPos <= 0){
          		break;
     		}
		}

		return true;
	}

	function clear(Module storage _self) internal{
		_self.moduleId = "";
		_self.moduleName = "";
		_self.moduleText = "";
		_self.moduleVersion = "";
		_self.deleted = false;
		_self.moduleEnable = 0;
		_self.moduleDescription = "";
		_self.moduleCreateTime = 0;
		_self.moduleUpdateTime = 0;
		_self.moduleCreator = address(0);
		delete _self.contractInfos;
		_self.moduleUrl = "";
		_self.icon = "";
		_self.publishTime = 0;
		_self.moduleType = 1;
		delete _self.roleIds;
	}
}
