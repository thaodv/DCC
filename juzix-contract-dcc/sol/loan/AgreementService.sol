pragma solidity ^0.4.2;

import "../permission/OperatorPermission.sol";
import "./AgreementIntf.sol";

contract AgreementService is OperatorPermission, AgreementIntf {

    mapping(address => uint256[]) public creditIndex;

    mapping(address => uint256[]) public debitIndex;

    mapping(bytes => uint256[]) creditIdHashIndex;

    mapping(address => mapping(uint256 => uint256)) public orderIdIndex;

    uint256 public constant MAX_SIZE = 10 * 1024;

    struct Agreement {

        address orderAddress;

        uint256 orderId;

        address debit;

        address credit;

         bytes creditIdHash;

        bytes agreementHash;
    }

    Agreement[] public agreements;

    event submitted(uint256 indexed agreementId, address indexed orderAddress, uint256 indexed orderId, address
    debit, address credit,bytes creditIdHash, bytes agreementHash);

    function AgreementService() public {
	    register("AgreementServiceModule", "0.0.1.0", "AgreementService", "0.0.1.0");
        agreements.push(Agreement(address(0), 0, address(0), address(0), "",""));
    }

    function queryAgreementIdArrayByCreditIndex(address _credit) public constant  returns (uint256[]){
        if(_credit == address(0)){
           log("_credit == address(0)");
           throw;
        }
        return creditIndex[_credit];
    }

    function getAgreementArrayLengthByCreditIndex(address _credit) public constant returns (uint256){
        if(_credit == address(0)){
           log("_credit == address(0)");
           throw;
         }
        return creditIndex[_credit].length;
    }

    function queryAgreementIdArrayByDebitIndex(address _debit) public  constant  returns (uint256[]){
        if(_debit == address(0)){
           log("_debit == address(0)");
           throw;
         }
        return debitIndex[_debit];
    }

    function getAgreementArrayLengthByDebitIndex(address _debit) public constant  returns (uint256){
        if(_debit == address(0)){
           log("_debit == address(0)");
           throw;
         }
        return debitIndex[_debit].length;
    }

     function queryAgreementIdArrayByCreditIdHashIndex(bytes _creditIdHash) public constant returns (uint256[]){
            if(!(_creditIdHash.length > 0 && _creditIdHash.length <= MAX_SIZE)){
               log("!(_creditIdHash.length > 0 && _creditIdHash.length <= MAX_SIZE)");
               throw;
            }
            return creditIdHashIndex[_creditIdHash];
        }

    function getAgreementArrayLengthByCreditIdHashIndex(bytes _creditIdHash) public constant returns (uint256){
        if(!(_creditIdHash.length > 0 && _creditIdHash.length <= MAX_SIZE)){
           log("!(_creditIdHash.length > 0 && _creditIdHash.length <= MAX_SIZE)");
           throw;
        }
        return creditIdHashIndex[_creditIdHash].length;
    }

    function getAgreementByOrderId(address _orderAddress, uint256 _orderId) public constant  returns (uint256 agreementId, address orderAddress,
        uint256 orderId, address debit, address credit,bytes creditIdHash, bytes agreementHash){
        if(!(_orderAddress != address(0))){
		  log("_orderAddress != address(0");
		  throw;
		}

		if(!(_orderId > 0)){
		  log("!(_orderId > 0)");
		  throw;
		}
        uint256 _agreementId = orderIdIndex[_orderAddress][_orderId];
        return getAgreement(_agreementId);
    }

    function getAgreement(uint256 _agreementId) public constant  returns (uint256 agreementId, address orderAddress,
        uint256 orderId, address debit, address credit,bytes creditIdHash,
        bytes agreementHash){
        if(!(_agreementId > 0 && _agreementId < agreements.length)){
          log("!(_agreementId > 0 && _agreementId < agreements.length)");
          throw;
        }
        Agreement memory agreement = agreements[_agreementId];
        return (_agreementId, agreement.orderAddress, agreement.orderId, agreement.debit, agreement.credit, agreement.creditIdHash,agreement.agreementHash);
    }

    function submit(uint256 orderId, address debit, address credit,bytes creditIdHash, bytes agreementHash) public  returns (uint256
        agreementId){
		onlyOperator();
        if(!(orderId>0)){
         log("!(orderId>0)");
         throw;
       }
        if(!(debit != address(0))){
          log("debit == address(0)");
          throw;
       }
        if(!(credit != address(0))){
         log("credit == address(0)");
         throw;
        }
        if(!(debit!=credit)){
         log("debit==credit");
         throw;
       }
        if(!(creditIdHash.length > 0 && creditIdHash.length <= MAX_SIZE)){
         log("!(creditIdHash.length > 0 && creditIdHash.length <= MAX_SIZE)");
         throw;
       }

       if(!(agreementHash.length > 0 && agreementHash.length <= MAX_SIZE)){
            log("!(agreementHash.length > 0 && agreementHash.length <= MAX_SIZE)");
            throw;
       }

        address orderAddress = msg.sender;
        agreementId = agreements.push(Agreement(orderAddress, orderId, debit, credit,creditIdHash, agreementHash)) - 1;
        submitted(agreementId, orderAddress, orderId, debit, credit,creditIdHash, agreementHash);
        debitIndex[debit].push(agreementId);
        creditIndex[credit].push(agreementId);
        creditIdHashIndex[creditIdHash].push(agreementId);
        orderIdIndex[orderAddress][orderId] = agreementId;
    }

    function getAgreementLength() public constant returns (uint256 length) {
        return agreements.length;
    }

}