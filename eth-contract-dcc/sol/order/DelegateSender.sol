pragma solidity ^0.4.2;

import "../ownership/OperatorPermission.sol";
import '../token/SafeERC20.sol';
import '../token/ERC20.sol';

contract DelegateSender is OperatorPermission {
    using SafeERC20 for ERC20;

    mapping(uint256 => string) public requestNoMap;

    function deliverToken(uint256 requestNo, address beneficiary, address tokenAddress, uint256 tokenAmount)
    public onlyOperator returns (bool) {
        require(requestNo > 0);
        require(bytes(requestNoMap[requestNo]).length == 0);
        require(tokenAddress != address(0));
        ERC20 ERC20Contract = ERC20(tokenAddress);

        requestNoMap[requestNo] = "true";
        ERC20Contract.safeTransferFrom(msg.sender, beneficiary, tokenAmount);
        return true;
    }

    function deliverEth(uint256 requestNo, address beneficiary)
    public onlyOperator payable returns (bool) {
        require(requestNo > 0);
        require(bytes(requestNoMap[requestNo]).length == 0);
        require(msg.value > 0);

        requestNoMap[requestNo] = "true";
        beneficiary.transfer(msg.value);
        return true;
    }

    function fastFailure(uint256 requestNo)
    public onlyOperator returns (bool) {
        require(requestNo > 0);
        require(bytes(requestNoMap[requestNo]).length == 0);
     
        requestNoMap[requestNo] = "false";
        return true;
    }
}