pragma solidity ^0.4.2;

import "../ownership/HasETH.sol";
import "../math/SafeMath.sol";

contract BSXETHContract is HasETH {

    using SafeMath for uint256;

    Status public status;

    uint256 public investCeilAmount;

    uint256 public investedTotalAmount;

    uint256 public minAmountPerHand;

    /**
     *已发放累计
     */
    uint256 public repaidTotalAmount;
    /**
     *应发放硬顶
     */
    uint256 public repaidCeilAmount;


    uint256 public rateNumerator;

    uint256 public denominator;

    string public saleInfo;

    enum Status {CREATED, OPENED, CLOSED, ENDED, SETTLED}

    mapping(address => uint256) public investedAmountMapping;
    mapping(address => uint256) public repaidAmountMapping;

    address[] public investorArray;

    event statusUpdated(Status indexed status);
    event invested(address investor, uint256 investedAmount);
    event repaid(address investor, uint256 repaidAmount);

    function BSXETHContract(){
        status = Status.CREATED;
    }

    function open() onlyOwner public {
        checkAndChangeStatus(status, Status.CREATED, Status.OPENED);
    }

    function close() onlyOwner public {
        checkAndChangeStatus(status, Status.OPENED, Status.CLOSED);
    }

    function end() onlyOwner public {
        repaidCeilAmount = investedTotalAmount.add(investedTotalAmount.mul(rateNumerator).div(denominator));
        checkAndChangeStatus(status, Status.CLOSED, Status.ENDED);
    }


    function invest() public payable {
        require(checkStatus(status, Status.OPENED));
        require(msg.value >= minAmountPerHand);
        require(investedTotalAmount.add(msg.value) <= investCeilAmount);
        require(msg.sender == tx.origin);

        address investor = tx.origin;
        if (investedAmountMapping[investor] == 0) {
            investorArray.push(investor);
        }
        investedAmountMapping[investor] = investedAmountMapping[investor].add(msg.value);
        investedTotalAmount = investedTotalAmount.add(msg.value);
        invested(msg.sender, msg.value);
    }


    function batchRepay(uint256 start, uint256 end) public onlyOwner {
        require(checkStatus(status, Status.ENDED));

        for (uint256 i = start; i < end; i++) {
            address investor = investorArray[i];
            if (repaidAmountMapping[investor] == 0) {
                uint256 investedAmount = investedAmountMapping[investor];
                uint256 profitAmount = investedAmount.mul(rateNumerator).div(denominator);
                uint256 repaidAmount = investedAmount.add(profitAmount);

                investor.transfer(repaidAmount);
                repaidAmountMapping[investor] = repaidAmount;
                repaidTotalAmount = repaidTotalAmount.add(repaidAmount);
                repaid(investor, repaidAmount);
            }
        }
    }

    function settle() public onlyOwner {
        require(checkStatus(status, Status.ENDED));
        require(repaidTotalAmount == repaidCeilAmount);
        justChangeStatus(Status.SETTLED);
    }

    function checkAndChangeStatus(Status inputStatus, Status expectedStatus, Status nextStatus) internal {
        require((checkStatus(inputStatus, expectedStatus)));
        justChangeStatus(nextStatus);
    }

    function checkStatus(Status inputStatus, Status expectedStatus) internal returns (bool){
        if (inputStatus == expectedStatus) {
            return true;
        }
        return false;
    }

    function justChangeStatus(Status nextStatus) internal {
        //状态机跃迁
        status = nextStatus;
        statusUpdated(nextStatus);
    }

    function setRateNumerator(uint256 _rateNumerator) public onlyOwner {
        rateNumerator = _rateNumerator;
    }

    function setDenominator(uint256 _denominator) public onlyOwner {
        denominator = _denominator;
    }

    function setInvestCeilAmount(uint256 _investCeilAmount) public onlyOwner {
        investCeilAmount = _investCeilAmount;
    }

    function setMinAmountPerHand(uint256 _minAmountPerHand) public onlyOwner {
        minAmountPerHand = _minAmountPerHand;
    }

    function setSaleInfo(string _saleInf) public onlyOwner {
        saleInfo = _saleInf;
    }

    function getInvestorArrayLength() public view returns (uint256 length){
        return investorArray.length;
    }

}