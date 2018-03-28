pragma solidity ^0.4.18;

import '../ownership/Ownable.sol';

contract Kyc is Ownable {

    mapping(address => bool) public customers;

    event AddedToCustomers(address indexed customer);

    event DeletedFromCustomers(address indexed customer);

    function addCustomer(address customer) public onlyOwner {
        customers[customer] = true;
        AddedToCustomers(customer);
    }

    function deleteCustomer(address customer) public onlyOwner {
        delete customers[customer];
        DeletedFromCustomers(customer);
    }

}