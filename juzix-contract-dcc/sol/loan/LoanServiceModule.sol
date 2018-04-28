pragma solidity ^0.4.2;

import "../sysbase/BaseModule.sol";
import "../library/LibModule.sol";

contract LoanServiceModule is BaseModule {
    using LibModule for *;

    LibModule.Module tmpModule;

    string moduleName;
    string moduleVersion;
    string moduleName_moduleVersion;

    //模块构造函数
    function LoanServiceModule() {
        //定义模块合约名称
        string memory moduleName = "LoanServiceModule";

        //定义模块合约名称
        string memory moduleDesc = "LoanServiceModule";

        //定义模块合约版本号
        string memory moduleVersion = "0.0.1.0";

        //把合约注册到JUICE链上
        LibLog.log("register LoanServiceModule");
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
        LibLog.log("add LoanServiceModule to SysModule");
        uint ret = addModule(tmpModule.toJson());

        if (ret != 0) {
            LibLog.log("add LoanServiceModule to SysModule failed");
            return;
        }
        //添加模块
        //tmpModule.clear();
        //tmpModule.moduleName = "CertServiceModule";//设置模块名称【需要修改】
        //tmpModule.moduleVersion = "0.0.1.0";//默认模块版本号，不建议修改
        //tmpModule.moduleEnable = 0;//模块开关：0-放权；1-控权【需要修改】
        //tmpModule.moduleDescription = "";
        //tmpModule.moduleText = "身份验证服务模块";//中文模块名【需要修改】

        //addModule(tmpModule.toJson());//添加模块
    }


}
