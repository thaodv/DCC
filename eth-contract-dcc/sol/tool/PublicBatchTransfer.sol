pragma solidity ^0.4.18;

import '../ownership/WalletUsage.sol';
import '../token/SafeERC20.sol';
import '../token/ERC20.sol';

contract PublicBatchTransfer is WalletUsage {
    using SafeERC20 for ERC20;

    uint256 public fee;

    function PublicBatchTransfer(address walletAddress,uint256 _fee){
        require(walletAddress != address(0));
        setWallet(walletAddress);
        setFee(_fee);
    }

    function batchTransfer(address tokenAddress, address[] beneficiaries, uint256[] tokenAmount) payable public returns (bool) {
        require(msg.value >= fee);
        require(tokenAddress != address(0));
        require(beneficiaries.length > 0 && beneficiaries.length == tokenAmount.length);
        ERC20 ERC20Contract = ERC20(tokenAddress);
        for (uint256 i = 0; i < beneficiaries.length; i++) {
            ERC20Contract.safeTransferFrom(msg.sender, beneficiaries[i], tokenAmount[i]);
        }
        if (!keepEth) {
            wallet.transfer(msg.value);
        }

        return true;
    }

    function setFee(uint256 _fee) onlyOwner public {
        fee = _fee;
    }
}
