pragma solidity ^0.4.2;

import '../ownership/Ownable.sol';

contract BSXContractList is Ownable {

    struct BSX {
        string chainCode;
        address contractAddress;
        string assetCode;
    }

    uint256 constant MAXSIZE = 100;

    BSX[] public contractList;

    function addContract(string chainCode, address contractAddress,string assetCode) public onlyOwner {
        require(bytes(chainCode).length > 0 && bytes(chainCode).length <= MAXSIZE);
        require(contractAddress != 0);
        require(bytes(assetCode).length > 0 && bytes(assetCode).length <= MAXSIZE);
        contractList.push(BSX(chainCode, contractAddress,assetCode));
    }

    function removeContract(uint256 index)public onlyOwner{
        for(uint256 i=index;i<contractList.length-1;i++){
            contractList[i]=contractList[i+1];
        }
        contractList.length--;
    }

    function updateContract(uint256 index,string chainCode,address contractAddress,string assetCode) public onlyOwner{
        require(index<contractList.length);
        require(bytes(chainCode).length > 0 && bytes(chainCode).length <= MAXSIZE);
        require(contractAddress != 0);
        require(bytes(assetCode).length > 0 && bytes(assetCode).length <= MAXSIZE);

        BSX storage bsx=contractList[index];
        bsx.chainCode=chainCode;
        bsx.contractAddress=contractAddress;
        bsx.assetCode=assetCode;
    }

    function getContractListLength() public view returns(uint256){
        return contractList.length;
    }
}