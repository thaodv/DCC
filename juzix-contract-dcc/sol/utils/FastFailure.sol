pragma solidity ^0.4.2;

/**
 * 快速失败.使nonce被占用
 */
contract FastFailure {
    function fastFail() external {
        if(!(false)){
            //log("!(false)");
            throw;
        }
    }
}
