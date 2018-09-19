pragma solidity ^0.4.2;

import '../permission/OwnerPermission.sol';

contract BSXContractList is OwnerPermission {

    struct BSX {
        string chainCode;
        address contractAddress;
        string assetCode;
    }

    uint256 constant MAXSIZE = 100;

    BSX[] public contractList;

    function BSXContractList()public{
        register("BSXContractListModule", "0.0.1.0", "BSXContractList", "0.0.1.0");
    }

    function addContract(string chainCode, address contractAddress,string assetCode) public {
        onlyOwner();
        if(!(bytes(chainCode).length > 0 && bytes(chainCode).length <= MAXSIZE)){
            log("!(bytes(chainCode).length > 0 && bytes(chainCode).length <= MAXSIZE)");
            throw;
        }
        if(!(contractAddress != 0)){
            log("!(contractAddress != 0)");
            throw;
        }
        if(!(bytes(assetCode).length > 0 && bytes(assetCode).length <= MAXSIZE)){
            log("!(bytes(assetCode).length > 0 && bytes(assetCode).length <= MAXSIZE)");
            throw;
        }
        contractList.push(BSX(chainCode, contractAddress,assetCode));
    }

    function removeContract(uint256 index)public{
        onlyOwner();
        for(uint256 i=index;i<contractList.length-1;i++){
            contractList[i]=contractList[i+1];
        }
        contractList.length--;
    }

    function updateContract(uint256 index,string chainCode,address contractAddress,string assetCode) public {
        onlyOwner();
        if(!(index<contractList.length)){
            log("!(index<contractList.length)");
            throw;
        }
        if(!(bytes(chainCode).length > 0 && bytes(chainCode).length <= MAXSIZE)){
            log("!(bytes(chainCode).length > 0 && bytes(chainCode).length <= MAXSIZE)");
            throw;
        }
        if(!(contractAddress != 0)){
            log("!(contractAddress != 0)");
            throw;
        }
        if(!(bytes(assetCode).length > 0 && bytes(assetCode).length <= MAXSIZE)){
            log("!(bytes(assetCode).length > 0 && bytes(assetCode).length <= MAXSIZE)");
            throw;
        }

        BSX storage bsx=contractList[index];
        bsx.chainCode=chainCode;
        bsx.contractAddress=contractAddress;
        bsx.assetCode=assetCode;
    }

    function getContractListLength() public constant returns(uint256){
        return contractList.length;
    }
}