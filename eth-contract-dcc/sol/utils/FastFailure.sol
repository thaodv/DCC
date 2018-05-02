pragma solidity ^0.4.0;

/**
 * 快速失败.使nonce被占用
 */
contract FastFailure {
    function fastFail() external {
        require(false);
    }
}
