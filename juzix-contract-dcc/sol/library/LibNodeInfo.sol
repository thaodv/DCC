/**
* @file LibNodeInfo.sol
* @author Jungle
* @time 2017-5-20 09:42:28
* @desc the defination of Department library
*/
pragma solidity ^0.4.2;
 
import "../utillib/LibInt.sol";
import "../utillib/LibString.sol";
import "../utillib/LibJson.sol";

library LibNodeInfo {
    
    using LibInt for *;
    using LibString for *;
    using LibNodeInfo for *;
    using LibJson for *;

    /* struct NodeIP {
        string      ip;                 // ip地址
        int         uintIP;             // IP的整形表示
        uint        P2PPort;            // p2p端口
        uint        RPCPort;            // RPC端口
        uint        TPort;              // tomcat端口
        uint        _type;              // 内外网标识  0内网、1外网
    } */

    // 入口信息
    struct NodeNAT {
        string      ip;                     // 入口IP地址（外网）
        string      pubkey;
        address     nodeAddress;
        uint        activated;              // 0 禁用 1 启用                 
        uint        p2pPort;                // p2p端口
        uint        tPort;
        uint        rpcPort;
    }

    // 出口信息
    struct NodeLAN {
        string      maskIP;                 // 掩码IP
        int         mastInt;                // 掩码IP对应整数
        string      goByIP;                 // 掩码基础IP
        int         goByInt;                // 基础IP对应整数
        string      startIP;                // IP段的起始IP
        int         startInt;               
        string      endIP;                  // IP段对应结束IP
        int         endInt;
    }

    struct NodeInfo {
        string          nodeId;             // 节点ID
        string          nodeName;           // 节点名称
        string          nodeShortName;      // 节点简称             *
        address         nodeAdmin;          // 节点管理员地址      
        //address       createAddr;         // 节点创建者地址       *
        string          nodeDescription;    // 节点描述
        bool            deleted;            // 删除标记 true 已删除 false 未删除
        uint            state;              // 节点状态：0 无效 1 有效
        uint            _type;              // 1 记账节点 0 非记账节点
        string          deptId;             // 归属机构ID
        uint            deptLevel;          // 机构级别             - 
        string          deptCN;             // 归属机构证书CN 
        //string        pubkey;             // 节点公钥
        //string        ip;                 // 节点P2P通信IP      *
        //uint          port;               // 节点P2P通信端口    * 
        //uint          activated;          // 节点是否激活       * 
        uint            createTime; 
        uint            updateTime;
        //NodeIP[]      nodeIPList;         // 节点IP信息集合     *
        NodeLAN         nodeLAN;              // 局域网信息
        NodeNAT         nodeNAT;              // 公网信息
    }

    /**
    * @dev Convert self to json string
    * @param _self Self object
    * @return Return the converted json string
    */
    function toJson(NodeInfo storage _self) internal constant returns(string _json) {
        _json = _json.concat("{");
        _json = _json.concat( _self.nodeId.toKeyValue("nodeId"), "," );
        _json = _json.concat( _self.nodeName.toKeyValue("nodeName"), "," );
        _json = _json.concat( _self.nodeShortName.toKeyValue("nodeShortName"), "," );
        _json = _json.concat( uint(_self.nodeAdmin).toAddrString().toKeyValue("nodeAdmin"), "," );
        //_json = _json.concat( uint(_self.createAddr).toAddrString().toKeyValue("createAddr"), "," );
        _json = _json.concat( _self.nodeDescription.toKeyValue("nodeDescription"), "," );
        _json = _json.concat( uint(_self.state).toKeyValue("state"), "," );
        _json = _json.concat( uint(_self._type).toKeyValue("type"), "," );
        _json = _json.concat( _self.deptId.toKeyValue("deptId"), "," );
        _json = _json.concat( uint(_self.deptLevel).toKeyValue("deptLevel"), "," );
        _json = _json.concat( _self.deptCN.toKeyValue("deptCN"), "," );
        //_json = _json.concat( _self.pubkey.toKeyValue("pubkey"), "," );
        //_json = _json.concat( _self.ip.toKeyValue("ip"), "," );
        //_json = _json.concat( uint(_self.port).toKeyValue("port"), "," );
        //_json = _json.concat( uint(_self.activated).toKeyValue("activated"), "," );
        
        _json = _json.concat( uint(_self.createTime).toKeyValue("createTime"), "," );
        _json = _json.concat( uint(_self.updateTime).toKeyValue("updateTime"), "," );
        /* _json = _json.concat("\"nodeIPList\":[");
        for (uint i=0; i<_self.nodeIPList.length; ++i) {
            if (i > 0) {
                _json = _json.concat(",");
            }
            _json = _json.concat("{");
            _json = _json.concat( _self.nodeIPList[i].ip.toKeyValue("ip"), "," );
            _json = _json.concat( int(_self.nodeIPList[i].uintIP).toKeyValue("uintIP"),",");
            _json = _json.concat( uint(_self.nodeIPList[i].P2PPort).toKeyValue("P2PPort"), "," );
            _json = _json.concat( uint(_self.nodeIPList[i].RPCPort).toKeyValue("RPCPort"), "," );
            _json = _json.concat( uint(_self.nodeIPList[i].TPort).toKeyValue("TPort"), "," );
            _json = _json.concat( uint(_self.nodeIPList[i]._type).toKeyValue("type") );
            _json = _json.concat("}");
        }
        _json = _json.concat("]"); */

        // concat nodeLAN 
        _json = _json.concat("\"nodeLAN\":{");
        _json = _json.concat( _self.nodeLAN.maskIP.toKeyValue("maskIP"),"," );
        _json = _json.concat( int(_self.nodeLAN.mastInt).toKeyValue("mastInt"),"," );
        _json = _json.concat( _self.nodeLAN.goByIP.toKeyValue("goByIP"),"," );
        _json = _json.concat( int(_self.nodeLAN.goByInt).toKeyValue("goByInt"),"," );
        _json = _json.concat( _self.nodeLAN.startIP.toKeyValue("startIP"),"," );
        _json = _json.concat( int(_self.nodeLAN.startInt).toKeyValue("startInt"),"," );
        _json = _json.concat( _self.nodeLAN.endIP.toKeyValue("endIP"),"," );
        _json = _json.concat( int(_self.nodeLAN.endInt).toKeyValue("endInt") );
	   _json = _json.concat("},");

        // concat nodeNAT 
        _json = _json.concat("\"nodeNAT\":{");
        _json = _json.concat( _self.nodeNAT.ip.toKeyValue("ip"),"," );
        _json = _json.concat( _self.nodeNAT.pubkey.toKeyValue("pubkey"),"," );
        _json = _json.concat( uint(_self.nodeNAT.nodeAddress).toAddrString().toKeyValue("nodeAddress"), "," );
        _json = _json.concat( uint(_self.nodeNAT.activated).toKeyValue("activated"),"," );
        _json = _json.concat( uint(_self.nodeNAT.p2pPort).toKeyValue("p2pPort"),"," );
        _json = _json.concat( uint(_self.nodeNAT.tPort).toKeyValue("tPort"),"," );
        _json = _json.concat( uint(_self.nodeNAT.rpcPort).toKeyValue("rpcPort"),"" );

        _json = _json.concat("}}");
    }

    /**
    * @dev Construct self from json string
    * @param _self Self object
    * @param _json The json string
    * @return succ The result of Construction, true or false
    */
    function fromJson(NodeInfo storage _self, string _json) internal constant returns(bool succ) {
        _self.clear();

        _self.nodeId = _json.getStringValueByKey("nodeId");
        _self.nodeName = _json.getStringValueByKey("nodeName");
        _self.nodeShortName = _json.getStringValueByKey("nodeShortName");
        _self.nodeAdmin = _json.getStringValueByKey("nodeAdmin").toAddress();
        //_self.createAddr = _json.getStringValueByKey("createAddr").toAddress(); 
        _self.nodeDescription = _json.getStringValueByKey("nodeDescription");
        _self.state = uint(_json.getIntValueByKey("state"));
        _self._type = uint(_json.getIntValueByKey("type"));
        _self.deptId = _json.getStringValueByKey("deptId");
        _self.deptLevel = uint(_json.getIntValueByKey("deptLevel"));
        _self.deptCN = _json.getStringValueByKey("deptCN");

        // parse NodeLAN
        string memory nodeLANStr = _json.getObjectValueByKey("nodeLAN");
        nodeLANStr = nodeLANStr.trim();
        NodeLAN memory _nodeLAN;
        _nodeLAN.maskIP = nodeLANStr.getStringValueByKey("maskIP");
        _nodeLAN.mastInt = int(nodeLANStr.getIntValueByKey("mastInt"));
        _nodeLAN.goByIP = nodeLANStr.getStringValueByKey("goByIP");
        _nodeLAN.goByInt = int(nodeLANStr.getIntValueByKey("goByInt"));
        _nodeLAN.startIP = nodeLANStr.getStringValueByKey("startIP");
        _nodeLAN.startInt = int(nodeLANStr.getIntValueByKey("startInt"));
        _nodeLAN.endIP = nodeLANStr.getStringValueByKey("endIP");
        _nodeLAN.endInt = int(nodeLANStr.getIntValueByKey("endInt"));
        _self.nodeLAN = _nodeLAN;

        // parse NodeNAT
        string memory nodeNATStr = _json.getObjectValueByKey("nodeNAT");
        nodeNATStr = nodeNATStr.trim();
        NodeNAT memory _nodeNAT;
        _nodeNAT.pubkey = nodeNATStr.getStringValueByKey("pubkey");
        _nodeNAT.nodeAddress = nodeNATStr.getStringValueByKey("nodeAddress").toAddress();
        _nodeNAT.ip = nodeNATStr.getStringValueByKey("ip");
        _nodeNAT.p2pPort = uint(nodeNATStr.getIntValueByKey("p2pPort"));
        _nodeNAT.tPort = uint(nodeNATStr.getIntValueByKey("tPort"));
        _nodeNAT.rpcPort = uint(nodeNATStr.getIntValueByKey("rpcPort"));
        _nodeNAT.activated = uint(nodeNATStr.getIntValueByKey("activated"));      
        _self.nodeNAT = _nodeNAT;

        //_self.pubkey = _json.getStringValueByKey("pubkey");
        //_self.ip = _json.getStringValueByKey("ip");
        //_self.port = uint(_json.getIntValueByKey("port"));
        //_self.activated = uint( _json.getIntValueByKey("activated") );


        /* string memory nodeIPListStr = _json.getArrayValueByKey("nodeIPList");
        while (true) {
            string memory enodeStr;
            int braketPos = nodeIPListStr.indexOf("}"); "{"; //"{" compiler bug
            if (braketPos > 0) {
                enodeStr = nodeIPListStr.substr(0, uint(braketPos)+1);
                nodeIPListStr = nodeIPListStr.substr(uint(braketPos)+1, uint(-1));
            } else {
                enodeStr = nodeIPListStr;
            }

            enodeStr = enodeStr.trim();
            if (bytes(enodeStr).length == 0) {
                break;
            }

            NodeIP memory nodeIP;
            nodeIP.ip = enodeStr.getStringValueByKey("ip");
            nodeIP.uintIP = int(enodeStr.getIntValueByKey("uintIP"));
            nodeIP.P2PPort = uint(enodeStr.getIntValueByKey("P2PPort"));
            nodeIP.RPCPort = uint(enodeStr.getIntValueByKey("RPCPort"));
            nodeIP.TPort = uint(enodeStr.getIntValueByKey("TPort"));
            nodeIP._type = uint(enodeStr.getIntValueByKey("type"));

            _self.nodeIPList.push(nodeIP);

            if (braketPos <= 0) {
                break;
            }
        } */
        
        if (bytes(_self.nodeId).length == 0) {
            return false;
        }

        return true;
    }

    /**
    * @dev Clear self data
    * @param _self Self object
    * @return No return
    */
    function clear(NodeInfo storage _self) internal {
        _self.nodeId = "";
        _self.nodeName = "";       
        _self.nodeShortName = "";  
        _self.nodeAdmin = 0;      
        //_self.createAddr = 0;     
        _self.nodeDescription = "";
        _self.deleted = false;        
        _self.state = 1;          
        _self._type = 0;          
        _self.deptId = "";
        _self.deptLevel = 0;    
        _self.deptCN = "";     
        //_self.pubkey = "";         
        //_self.ip = "";             
        //_self.port = 0;           
        //_self.activated = 1;   
        //delete _self.nodeIPList;
        delete _self.nodeLAN;
        delete _self.nodeNAT;      
    }
}
