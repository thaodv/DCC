/**
 *
 * @file LibNodeApply.sol
 * @author Jungle
 * @time 2017-4-13 22:16:20
 * @desc Lib for node apply
 *
 */
pragma solidity ^0.4.2;

import "../utillib/LibInt.sol";
import "../utillib/LibString.sol";
import "../utillib/LibJson.sol";

library LibNodeApply {

	using LibInt for *;
	using LibString for *;
    using LibJson for *;
	using LibNodeApply for *;

    struct ApplyNodeIP {
        string      ip;                 // ip地址
        int         uintIP;             // IP转为int的值
        uint        P2PPort;            // p2p端口
        uint        RPCPort;            // RPC端口
        uint        TPort;              // tomcat端口
        uint        _type;              // 内外网标识  0内网、1外网
    }

    struct ApplyNodeInfo {
        string          nodeId;             // 节点ID
        string          nodeName;           // 节点名称
        string          nodeShortName;      // 节点简称
        address         nodeAdmin;          // 节点管理员地址
        string          nodeDescription;    // 节点描述
        uint            state;              // 节点状态：0 无效 1 有效
        uint            _type;              // 1 记账节点 0 非记账节点
        string          deptId;             // 归属机构ID
        uint            deptLevel;          // 机构级别
        string          deptCN;             // 归属机构证书CN 
        string          pubkey;             // 节点公钥
        address         nodeAddress;        // 节点地址
        string          ip;                 // 节点P2P通信IP
        uint            port;               // 节点P2P通信端口
    }

    // 审核信息
    struct AuditData {
        string      applyId;             // node apply id
        string      parentId;            // department parent id
        uint        departmentLevel;     // departmentLevel
        uint        state;               // audit state 0 init ,1 wait ,2 success ,3 fail
        address     admin;               // the admin address of department 
        string      reason;              // the reason of autiting result
        string[]    roleIdList;          // the roleid
        string      cipherGroupKey;      // 用户群私钥
    }

	// 节点信息之 - 用户信息
	struct ApplyUser {
        address    	userAddr;			// 用户钱包文件地址address
        string     	name;				// 用户名称
        string     	account;			// 用户账户名
        string     	email;				// 用户邮箱
        uint        passwordStatus;     // 密码状态
        uint        accountStatus;      // 账户状态
        uint        deleteStatus;       // 
        string     	mobile;				// 用户手机号
        string     	departmentId;		// 归属部门ID（uuid）
        string     	uuid;				// 唯一标识
        string     	publicKey;		    // 用户公钥  " " 
        string     	cipherGroupKey;	    // 密码组key " "
    } 

	// 节点申请之 - 部门信息
	struct ApplyDepartment {
		string id;					// 机构Id [UUID由业务随机生成（保持唯一）]				
        string name;				// 机构名称
        uint departmentLevel;		// 机构层级（树形结构Level，0 开始）
        string parentId;			// 父机构Id
        string description;			// 机构描述
        string commonName;			// 证书CN
        string stateName;			// 省名称
        string countryName;			// 国家名称
        address admin;				// 部门管理员地址
        string orgaShortName;       // 组织简称
	}

    enum ApplyState {
        APPLY_INIT,
        APPLY_WAIT,
        APPLY_SUCCESS,
        APPLY_FAIL
    }

	// 申请信息记录
	struct NodeApply {
		string 		    id;						// 申请ID（uuid）
		uint		    applyTime; 				// 申请时间戳
        uint            createTime;             // 创建时间
        uint            updateTime;             // 修改时间
		ApplyDepartment applyDepartment;	    // 申请的部门信息
		ApplyUser	    applyUser;				// 申请的用户信息
        bool            deleted;                // 标识当前对象是否有效
        uint            state;                  // 申请状态：0 初始化 1 等待审核 2 申请 3 申请失败
        string          reason;                 // 审核备注（通过或不通过原因）
        address         creator;                // 申请者地址
        address         auditUAddr;             // 审核人地址
        ApplyNodeInfo   applyNodeInfo;          // 申请节点信息
        ApplyNodeIP[]   applyNodeIPList;        // 申请节点的IP地址信息
	}

	function toJson(NodeApply storage _self) internal constant returns(string _json) {
        _json = _json.concat("{");
        _json = _json.concat( _self.id.toKeyValue("id"),"," );
        _json = _json.concat( uint(_self.createTime).toKeyValue("createTime"),"," );
        _json = _json.concat( uint(_self.updateTime).toKeyValue("updateTime"),"," );
        _json = _json.concat( uint(_self.applyTime).toKeyValue("applyTime"),"," );
        _json = _json.concat( uint(_self.state).toKeyValue("state"),"," );
        _json = _json.concat( _self.reason.toKeyValue("reason"),"," );
        _json = _json.concat( uint(_self.auditUAddr).toAddrString().toKeyValue("auditUAddr"), "," );

        // parse department
        _json = _json.concat("\"applyDepartment\":{");
        _json = _json.concat( _self.applyDepartment.id.toKeyValue("id"),"," );
        _json = _json.concat( _self.applyDepartment.name.toKeyValue("name"),"," );
        _json = _json.concat( uint(_self.applyDepartment.departmentLevel).toKeyValue("departmentLevel"),"," );
        _json = _json.concat( _self.applyDepartment.parentId.toKeyValue("parentId"),"," );
        _json = _json.concat( _self.applyDepartment.description.toKeyValue("description"),"," );
        _json = _json.concat( _self.applyDepartment.commonName.toKeyValue("commonName"),"," );
        _json = _json.concat( _self.applyDepartment.stateName.toKeyValue("stateName"),"," );
        _json = _json.concat( _self.applyDepartment.countryName.toKeyValue("countryName"),"," );
        _json = _json.concat( _self.applyDepartment.orgaShortName.toKeyValue("orgaShortName"),"," );
        _json = _json.concat( uint(_self.applyDepartment.admin).toAddrString().toKeyValue("admin"));
        _json = _json.concat("},");

        // parse nodeinfo 
        _json = _json.concat("\"applyNodeInfo\":{");
        _json = _json.concat( _self.applyNodeInfo.nodeId.toKeyValue("nodeId"),"," );
        _json = _json.concat( _self.applyNodeInfo.nodeName.toKeyValue("nodeName"),"," );
        _json = _json.concat( _self.applyNodeInfo.nodeShortName.toKeyValue("nodeShortName"),"," );
        _json = _json.concat( uint(_self.applyNodeInfo.nodeAdmin).toAddrString().toKeyValue("nodeAdmin"),"," );
        _json = _json.concat( _self.applyNodeInfo.nodeDescription.toKeyValue("nodeDescription"),"," );
        _json = _json.concat( uint(_self.applyNodeInfo._type).toKeyValue("type"),"," );
        _json = _json.concat( _self.applyNodeInfo.deptId.toKeyValue("deptId"),"," );
        _json = _json.concat( uint(_self.applyNodeInfo.deptLevel).toKeyValue("deptLevel"),"," );
        _json = _json.concat( _self.applyNodeInfo.deptCN.toKeyValue("deptCN"),"," );
        _json = _json.concat( _self.applyNodeInfo.pubkey.toKeyValue("pubkey"),"," );
        _json = _json.concat( uint(_self.applyNodeInfo.nodeAddress).toAddrString().toKeyValue("nodeAddress"),"," );
        _json = _json.concat( _self.applyNodeInfo.ip.toKeyValue("ip"),"," );
        _json = _json.concat( uint(_self.applyNodeInfo.port).toKeyValue("port"));
        _json = _json.concat("},");

        // parse nodeIP list
        _json = _json.concat("\"applyNodeIPList\":[");
        for (uint i=0; i<_self.applyNodeIPList.length; ++i) {
            if (i > 0) {
                _json = _json.concat(",");
            }
            _json = _json.concat("{");
            _json = _json.concat( _self.applyNodeIPList[i].ip.toKeyValue("ip"), "," );
            _json = _json.concat( int(_self.applyNodeIPList[i].uintIP).toKeyValue("uintIP"),"," );
            _json = _json.concat( uint(_self.applyNodeIPList[i].P2PPort).toKeyValue("P2PPort"),"," );
            _json = _json.concat( uint(_self.applyNodeIPList[i].RPCPort).toKeyValue("RPCPort"),"," );
            _json = _json.concat( uint(_self.applyNodeIPList[i].TPort).toKeyValue("TPort"),"," );
            _json = _json.concat( uint(_self.applyNodeIPList[i]._type).toKeyValue("type"),"" );
            _json = _json.concat("}");
        }
        _json = _json.concat("],");

        // parse user
        _json = _json.concat("\"applyUser\":{");
        _json = _json.concat( _self.applyUser.account.toKeyValue("account"),"," );
        _json = _json.concat( uint(_self.applyUser.accountStatus).toKeyValue("accountStatus"),",");
        _json = _json.concat( _self.applyUser.cipherGroupKey.toKeyValue("cipherGroupKey"),"," );
        _json = _json.concat( _self.applyUser.publicKey.toKeyValue("publicKey"),"," );
        _json = _json.concat( uint(_self.applyUser.deleteStatus).toKeyValue("deleteStatus"),",");
        _json = _json.concat( _self.applyUser.departmentId.toKeyValue("departmentId"),",");
        _json = _json.concat( _self.applyUser.email.toKeyValue("email"),",");
        _json = _json.concat( _self.applyUser.mobile.toKeyValue("mobile"),",");
        _json = _json.concat( _self.applyUser.name.toKeyValue("name"),",");
        _json = _json.concat( uint(_self.applyUser.userAddr).toAddrString().toKeyValue("userAddr"),"" );
        _json = _json.concat("}}");
    }

    function fromJson(NodeApply storage _self, string _json) internal constant returns(bool succ) {
        // internal : Internal visible only
        // constant : Diasllows modification of state
        _self.clear();
        _self.id = _json.getStringValueByKey("id");
        _self.applyTime = uint(_json.getIntValueByKey("applyTime"));
        _self.state = uint(_json.getIntValueByKey("state"));
        _self.reason = _json.getStringValueByKey("reason");

        // parse ApplyDepartment
        string memory applyDepartmentStr = _json.getObjectValueByKey("applyDepartment");

        applyDepartmentStr = applyDepartmentStr.trim();

        ApplyDepartment memory department;
        department.id = applyDepartmentStr.getStringValueByKey("id");
        department.name = applyDepartmentStr.getStringValueByKey("name");
        department.departmentLevel = uint(applyDepartmentStr.getIntValueByKey("departmentLevel"));
        department.parentId = applyDepartmentStr.getStringValueByKey("parentId");
        department.description = applyDepartmentStr.getStringValueByKey("description");
        department.commonName = applyDepartmentStr.getStringValueByKey("commonName");
        department.stateName = applyDepartmentStr.getStringValueByKey("stateName");
        department.countryName = applyDepartmentStr.getStringValueByKey("countryName");
        department.orgaShortName = applyDepartmentStr.getStringValueByKey("orgaShortName");
        // Note: convert to address 
        department.admin = applyDepartmentStr.getStringValueByKey("admin").toAddress();

        _self.applyDepartment = department;

        // parse NodeInfo
        string memory _nodeInfoStr = _json.getObjectValueByKey("applyNodeInfo");
        _nodeInfoStr = _nodeInfoStr.trim();
        ApplyNodeInfo memory nodeInfo;
        nodeInfo.nodeId = _nodeInfoStr.getStringValueByKey("nodeId");
        nodeInfo.nodeName = _nodeInfoStr.getStringValueByKey("nodeName");
        nodeInfo.nodeShortName = _nodeInfoStr.getStringValueByKey("nodeShortName");
        nodeInfo.nodeAdmin = _nodeInfoStr.getStringValueByKey("nodeAdmin").toAddress();
        nodeInfo.nodeDescription = _nodeInfoStr.getStringValueByKey("nodeDescription");
        nodeInfo.state = uint(_nodeInfoStr.getIntValueByKey("state"));
        nodeInfo._type = uint(_nodeInfoStr.getIntValueByKey("type"));
        nodeInfo.deptId = _nodeInfoStr.getStringValueByKey("deptId");
        nodeInfo.deptLevel = uint(_nodeInfoStr.getIntValueByKey("deptLevel"));
        nodeInfo.deptCN = _nodeInfoStr.getStringValueByKey("deptCN");
        nodeInfo.pubkey = _nodeInfoStr.getStringValueByKey("pubkey");
        nodeInfo.nodeAddress = _nodeInfoStr.getStringValueByKey("nodeAddress").toAddress();
        nodeInfo.ip = _nodeInfoStr.getStringValueByKey("ip");
        nodeInfo.port = uint(_nodeInfoStr.getIntValueByKey("port"));
        _self.applyNodeInfo = nodeInfo;

        // parse nodeIP 
        _nodeInfoStr = _json.getArrayValueByKey("applyNodeIPList");
        while(true){
            string memory nodeIPStr;
            int braketPos = _nodeInfoStr.indexOf("}"); "{";  //
            if(braketPos > 0){
                nodeIPStr = _nodeInfoStr.substr(0,uint(braketPos) + 1);
                _nodeInfoStr = _nodeInfoStr.substr(uint(braketPos) + 1, uint(-1));
            }else{
                nodeIPStr = _nodeInfoStr;
            }
            nodeIPStr = nodeIPStr.trim();
            if(bytes(nodeIPStr).length == 0){
                break;
            }
            ApplyNodeIP memory nodeIP;
            nodeIP.ip = nodeIPStr.getStringValueByKey("ip");
            nodeIP.uintIP = int(nodeIPStr.getIntValueByKey("uintIP"));
            nodeIP.P2PPort = uint(nodeIPStr.getIntValueByKey("P2PPort"));
            nodeIP.RPCPort = uint(nodeIPStr.getIntValueByKey("RPCPort"));
            nodeIP.TPort = uint(nodeIPStr.getIntValueByKey("TPort"));
            nodeIP._type = uint(nodeIPStr.getIntValueByKey("type"));

            _self.applyNodeIPList.push(nodeIP);

            if(braketPos <= 0){
                break;
            }
        } 

        // parse ApplyUser
        string memory _applyUser = _json.getObjectValueByKey("applyUser");
        ApplyUser memory user;
        user.account = _applyUser.getStringValueByKey("account");
        user.accountStatus = uint(_applyUser.getIntValueByKey("accountStatus"));
        //user.birthday = uint(_applyUser.getIntValueByKey("birthday"));
        user.cipherGroupKey = _applyUser.getStringValueByKey("cipherGroupKey");
        user.departmentId = _applyUser.getStringValueByKey("departmentId");
        user.email = _applyUser.getStringValueByKey("email");
        user.mobile = _applyUser.getStringValueByKey("mobile");
        user.name = _applyUser.getStringValueByKey("name");
        user.passwordStatus = uint(_applyUser.getIntValueByKey("passwordStatus"));
        user.publicKey = _applyUser.getStringValueByKey("publicKey");
        user.userAddr = _applyUser.getStringValueByKey("userAddr").toAddress();

        _self.applyUser = user;

        if(bytes(_self.id).length == 0 ){
            return false;
        }

        return true;
    }

    function clear(NodeApply storage _self) internal {
        _self.id = "";
        _self.applyTime = 0;
        delete _self.applyDepartment;
        delete _self.applyUser;
        _self.deleted = false;
        _self.creator = 0;
        _self.auditUAddr = 0;
        _self.state = 0;
        delete _self.applyNodeIPList;
        delete _self.applyNodeInfo;
    }

    function toJsonForAuditData(AuditData storage _self) internal constant returns(string _json) {
        _json = _json.concat("{");
        _json = _json.concat( _self.applyId.toKeyValue("applyId"),"," );
        _json = _json.concat( _self.parentId.toKeyValue("parentId"),"," );
        _json = _json.concat( uint(_self.state).toKeyValue("state"),"," );
        _json = _json.concat( uint(_self.admin).toAddrString().toKeyValue("admin"),"," );
        _json = _json.concat( _self.reason.toKeyValue("reason"),"," );
        _json = _json.concat( _self.roleIdList.toKeyValue("roleIdList"),"," );
        _json = _json.concat( _self.cipherGroupKey.toKeyValue("cipherGroupKey"),"," );
        _json = _json.concat( uint(_self.departmentLevel).toKeyValue("departmentLevel"),"" );
        _json = _json.concat("}");
    }
    
    function fromJsonForAuditData(AuditData storage _self, string _json) internal constant returns(bool succ) {
        _self.clearForAuditData();
        _self.applyId = _json.getStringValueByKey("applyId");
        _self.parentId = _json.getStringValueByKey("parentId");
        _self.state = uint(_json.getIntValueByKey("state"));
        _self.admin = _json.getStringValueByKey("admin").toAddress();
        _self.reason = _json.getStringValueByKey("reason");

        // array
        _json.getArrayValueByKey("roleIdList", _self.roleIdList);
        _self.departmentLevel = uint(_json.getIntValueByKey("departmentLevel"));
        _self.cipherGroupKey = _json.getStringValueByKey("cipherGroupKey");
        return true;
    }

    function clearForAuditData(AuditData storage _self) internal {
        _self.applyId = "";
        _self.parentId = "";
        _self.departmentLevel = 0;
        _self.state = 0;
        _self.admin = 0;
        delete _self.roleIdList;
        _self.cipherGroupKey = "";
    }

}
