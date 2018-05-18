pragma solidity ^0.4.2;

import '../token/SafeERC20.sol';
import '../token/ERC20.sol';

contract PublicDelegateSender {
    using SafeERC20 for ERC20;

    mapping(address => mapping(uint256 => string)) public requestNoMap;

    function deliverToken(uint256 requestNo, address beneficiary, address tokenAddress, uint256 tokenAmount)
    public returns (bool) {
        require(requestNo > 0);
        require(bytes(requestNoMap[msg.sender][requestNo]).length == 0);
        require(tokenAddress != address(0));
        ERC20 ERC20Contract = ERC20(tokenAddress);

        requestNoMap[msg.sender][requestNo] = "true";
        ERC20Contract.safeTransferFrom(msg.sender, beneficiary, tokenAmount);
        return true;
    }

    function deliverEth(uint256 requestNo, address beneficiary)
    public payable returns (bool) {
        require(requestNo > 0);
        require(bytes(requestNoMap[msg.sender][requestNo]).length == 0);
        require(msg.value > 0);

        requestNoMap[msg.sender][requestNo] = "true";
        beneficiary.transfer(msg.value);
        return true;
    }

    function fastFailure(uint256 requestNo)
    public returns (bool) {
        require(requestNo > 0);
        require(bytes(requestNoMap[msg.sender][requestNo]).length == 0);

        requestNoMap[msg.sender][requestNo] = "false";
        return true;
    }
}