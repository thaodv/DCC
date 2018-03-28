/**
*@file      LibUser.sol
*@author    kelvin
*@time      2016-11-29
*@desc      the defination of LibUser
*/
pragma solidity ^0.4.2;

import "../utillib/LibInt.sol";
import "../utillib/LibString.sol";

library LibUser {

    using LibInt for *;
    using LibString for *;
    using LibUser for *;
    
    enum UserState { USER_INVALID, USER_VALID, USER_LOGIN }

    enum AccountState { INVALID, VALID, LOCKED }

    struct User {
         address    userAddr;
         string     name;
         string     account;                
         string     email;              
         string     mobile;             
         string     departmentId;       
         uint       accountStatus;       
         uint       passwordStatus;          // -  
         uint       deleteStatus;            // -         
         string     uuid;                   
         string     publicKey;              
         string     cipherGroupKey;
         uint       createTime;
         uint       updateTime;
         uint       loginTime;                // -
         string[]   roleIdList;
         UserState  state;                  // 标记用户数据是否有效
         uint       status;                 // 是否禁用用户：0 禁用 1 激活
         address    ownerAddr;              // 用户真实地址
         uint       certType;               // 证书类型 1 文件证书 2 U-KEY证书
         string     remark;
         string     icon;                   // icon base64编码

    }
    
    struct ModuleRoles {                   
    	string moduleId;
	string moduleName;
      	string moduleVersion;
    	string roleIds;
    }

    function toJson(User storage _self) internal returns(string _strjson){
        _strjson = "{";

        string memory strAddr = "0x";
        strAddr = strAddr.concat(_self.userAddr.addrToAsciiString());
        _strjson = _strjson.concat(strAddr.toKeyValue("userAddr"), ",");

        //_strjson = _strjson.concat( uint(_self.ownerAddr).toAddrString().toKeyValue("ownerAddr"), "," );
        
        _strjson = _strjson.concat(_self.name.toKeyValue("name"), ",");
        _strjson = _strjson.concat(_self.account.toKeyValue("account"), ",");
        _strjson = _strjson.concat(_self.email.toKeyValue("email"), ",");
        _strjson = _strjson.concat(_self.mobile.toKeyValue("mobile"), ",");
        _strjson = _strjson.concat(_self.departmentId.toKeyValue("departmentId"), ",");
        _strjson = _strjson.concat(uint(_self.accountStatus).toKeyValue("accountStatus"), ",");
        _strjson = _strjson.concat(uint(_self.passwordStatus).toKeyValue("passwordStatus"), ",");
        _strjson = _strjson.concat(uint(_self.state).toKeyValue("state"), ",");
        _strjson = _strjson.concat(uint(_self.status).toKeyValue("status"),",");
        _strjson = _strjson.concat(uint(_self.deleteStatus).toKeyValue("deleteStatus"), ",");
        _strjson = _strjson.concat(_self.uuid.toKeyValue("uuid"), ",");
        _strjson = _strjson.concat(_self.publicKey.toKeyValue("publicKey"), ",");
        _strjson = _strjson.concat(_self.cipherGroupKey.toKeyValue("cipherGroupKey"), ",");
        _strjson = _strjson.concat(uint(_self.createTime).toKeyValue("createTime"), ",");
        _strjson = _strjson.concat(uint(_self.updateTime).toKeyValue("updateTime"), ",");
        _strjson = _strjson.concat(uint(_self.loginTime).toKeyValue("loginTime"), ",");
        _strjson = _strjson.concat(uint(_self.certType).toKeyValue("certType"), ",");
        _strjson = _strjson.concat(_self.remark.toKeyValue("remark"), ",");
        _strjson = _strjson.concat(_self.icon.toKeyValue("icon"), ",");
        _strjson = _strjson.concat( uint(_self.ownerAddr).toAddrString().toKeyValue("ownerAddr"),"," );

        string memory strRoleIdList = "\"roleIdList\": [";
        for (uint index = 0; index < _self.roleIdList.length; ++index) {
            strRoleIdList = strRoleIdList.concat("\"");
            if (index != _self.roleIdList.length-1) {
                strRoleIdList = strRoleIdList.concat(_self.roleIdList[index], "\",");
            }
            else {
                strRoleIdList = strRoleIdList.concat(_self.roleIdList[index], "\"");
            }
        }
        
        strRoleIdList = strRoleIdList.concat("]}");
        _strjson = _strjson.concat(strRoleIdList);
    }

    function jsonParse(User storage _self, string _userJson) internal returns(bool) {
        _self.reset();

        string memory strAddr = _userJson.getStringValueByKey("userAddr");
        strAddr = _userJson.getStringValueByKey("userAddr");
        _self.userAddr = strAddr.toAddress();

        _self.name = _userJson.getStringValueByKey("name");
        _self.mobile = _userJson.getStringValueByKey("mobile");
        _self.account = _userJson.getStringValueByKey("account");
        _self.email = _userJson.getStringValueByKey("email");
        _self.departmentId = _userJson.getStringValueByKey("departmentId");
        _self.passwordStatus = uint(_userJson.getIntValueByKey("passwordStatus"));
        _self.accountStatus  = uint(_userJson.getIntValueByKey("accountStatus"));
        _self.deleteStatus = uint(_userJson.getIntValueByKey("deleteStatus"));
        _self.status = uint(_userJson.getIntValueByKey("status"));
        _self.uuid = _userJson.getStringValueByKey("uuid");
        _self.publicKey = _userJson.getStringValueByKey("publicKey");
        _self.cipherGroupKey = _userJson.getStringValueByKey("cipherGroupKey");
        _self.icon = _userJson.getStringValueByKey("icon");
        _self.remark = _userJson.getStringValueByKey("remark");

        _userJson.getArrayValueByKey("roleIdList", _self.roleIdList);
        _self.state = UserState.USER_INVALID;

        return true;
    }

    function reset(User storage _self) internal {
        _self.userAddr = address(0);
        _self.name = "";
        _self.account = "";
        _self.email = "";
        _self.mobile = "";
        _self.departmentId = "";
        _self.accountStatus = 0;
        _self.passwordStatus = 0;
        _self.deleteStatus = 0;
        _self.uuid = "";
        _self.publicKey = "";
        _self.cipherGroupKey = "";
        _self.createTime = 0;
        _self.updateTime = 0;
        _self.loginTime = 0;
        delete _self.roleIdList;
        _self.state = UserState.USER_INVALID;
        _self.status = 1;
        _self.ownerAddr = address(0);
        _self.remark = "";
        _self.icon = "";
    }
}