pragma solidity ^0.4.18;

import "../ownership/OperatorPermission.sol";
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
    debit, address credit, bytes creditIdHash, bytes agreementHash);

    function AgreementService() public {
        agreements.push(Agreement(address(0), 0, address(0), address(0), "", ""));
    }

    function queryAgreementIdArrayByCreditIndex(address _credit) public view returns (uint256[]){
        require(_credit != address(0));
        return creditIndex[_credit];
    }

    function getAgreementArrayLengthByCreditIndex(address _credit) public view returns (uint256){
        require(_credit != address(0));
        return creditIndex[_credit].length;
    }

    function queryAgreementIdArrayByDebitIndex(address _debit) public view returns (uint256[]){
        require(_debit != address(0));
        return debitIndex[_debit];
    }

    function getAgreementArrayLengthByDebitIndex(address _debit) public view returns (uint256){
        require(_debit != address(0));
        return debitIndex[_debit].length;
    }

    function queryAgreementIdArrayByCreditIdHashIndex(bytes _creditIdHash) public view returns (uint256[]){
        require(_creditIdHash.length > 0 && _creditIdHash.length <= MAX_SIZE);
        return creditIdHashIndex[_creditIdHash];
    }

    function getAgreementArrayLengthByCreditIdHashIndex(bytes _creditIdHash) public view returns (uint256){
        require(_creditIdHash.length > 0 && _creditIdHash.length <= MAX_SIZE);
        return creditIdHashIndex[_creditIdHash].length;
    }

    function getAgreementByOrderId(address _orderAddress, uint256 _orderId) public view returns (uint256 agreementId, address orderAddress,
        uint256 orderId, address debit, address credit, bytes creditIdHash, bytes agreementHash){
        require(_orderAddress != address(0));
        require(_orderId > 0);
        uint256 _agreementId = orderIdIndex[_orderAddress][_orderId];
        return getAgreement(_agreementId);
    }

    function getAgreement(uint256 _agreementId) public view returns (uint256 agreementId, address orderAddress,
        uint256 orderId, address debit, address credit, bytes creditIdHash, bytes agreementHash){
        require(_agreementId > 0 && _agreementId < agreements.length);
        Agreement memory agreement = agreements[_agreementId];
        return (_agreementId, agreement.orderAddress, agreement.orderId, agreement.debit, agreement.credit,
        agreement.creditIdHash, agreement.agreementHash);
    }

    function submit(uint256 orderId, address debit, address credit, bytes creditIdHash, bytes agreementHash) public
    onlyOperator returns (uint256 agreementId){
        require(orderId > 0);
        require(debit != address(0));
        require(credit != address(0));
        require(debit != credit);
        require(agreementHash.length > 0 && agreementHash.length <= MAX_SIZE);
        require(creditIdHash.length > 0 && creditIdHash.length <= MAX_SIZE);

        address orderAddress = msg.sender;
        agreementId = agreements.push(Agreement(orderAddress, orderId, debit, credit, creditIdHash, agreementHash)) - 1;
        submitted(agreementId, orderAddress, orderId, debit, credit, creditIdHash, agreementHash);
        debitIndex[debit].push(agreementId);
        creditIndex[credit].push(agreementId);
        creditIdHashIndex[creditIdHash].push(agreementId);
        orderIdIndex[orderAddress][orderId] = agreementId;

    }

    function getAgreementLength() public view returns (uint256 length) {
        return agreements.length;
    }

}