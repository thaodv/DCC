pragma solidity ^0.4.2;

contract IRegisterApplyManager {

    //用户注册申请
    function insert(string _json);

    //更新注册申请
    function update(string _json);

    //用户注册审核
    function audit(string _json);

    //根据ID查询申请信息
    function findById(string _applyId) constant returns (string);

    //根据uuid查询申请信息
    function findByUuid(string _uuid) constant returns (string);

    //分页查询申请列表
    function listByCondition(string _name, string _mobile, uint _certType, uint _pageSize, uint _pageNo, string _auditStatus, uint _accountStatus) constant returns (string);
}
