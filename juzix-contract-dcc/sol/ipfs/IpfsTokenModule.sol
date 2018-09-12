pragma solidity ^0.4.2;

import "../sysbase/BaseModule.sol";
import "../library/LibModule.sol";

contract IpfsTokenModule is BaseModule {
    using LibModule for *;

    LibModule.Module tmpModule;

    string moduleName;
    string moduleVersion;
    string moduleName_moduleVersion;

    //模块构造函数
    function IpfsTokenModule() {
        //定义模块合约名称
        string memory moduleName = "IpfsTokenModule";

        //定义模块合约名称
        string memory moduleDesc = "IpfsTokenModule";

        //定义模块合约版本号
        string memory moduleVersion = "0.0.1.0";

        //把合约注册到JUICE链上
        LibLog.log("register IpfsTokenModule");
        register(moduleName, moduleVersion);

        tmpModule.moduleName = moduleName;
        tmpModule.moduleVersion = moduleVersion;
        tmpModule.moduleEnable = 0;
        tmpModule.moduleDescription = moduleDesc;
        tmpModule.moduleText = moduleDesc;

        uint nowTime = now * 1000;
        tmpModule.moduleCreateTime = nowTime;
        tmpModule.moduleUpdateTime = nowTime;

        tmpModule.moduleCreator = msg.sender;

        tmpModule.icon = "";
        tmpModule.publishTime = nowTime;

        //把模块合约本身添加到系统的模块管理合约中。这一步是必须的，只有这样，用户的dapp才能调用添加到此模块合约的相关合约。
        LibLog.log("add IpfsTokenModule to SysModule");
        uint ret = addModule(tmpModule.toJson());

        if (ret != 0) {
            LibLog.log("add IpfsTokenModule to SysModule failed");
            return;
        }
    }

}
