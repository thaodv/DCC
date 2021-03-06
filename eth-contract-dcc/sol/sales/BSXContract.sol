pragma solidity ^0.4.2;

import "../ownership/HasToken.sol";
import "../math/SafeMath.sol";

contract BSXContract is HasToken {

    using SafeMath for uint256;

    Status public status;

    uint256 public ceilAmount;

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


    uint256 rateNumerator;

    uint256 denominator = 100;

    string saleInfo;

    enum Status {CREATED, OPENED, CLOSED, ENDED, SETTLED}

    mapping(address => uint256) public investedAmountMapping;
    mapping(address => uint256) public repaidAmountMapping;

    address[] public investorArray;

    event statusUpdated(Status indexed status);

    function BSXContract(){
        status = Status.CREATED;
    }

    function open() onlyOwner public {
        checkAndChangeStatus(status, Status.CREATED, Status.OPENED);
    }

    function close() onlyOwner public {
        checkAndChangeStatus(status, Status.OPENED, Status.CLOSED);
    }

    function end() onlyOwner public {
        repaidCeilAmount =investedTotalAmount.add(investedTotalAmount.mul(rateNumerator).div(denominator)) ;
        checkAndChangeStatus(status, Status.CLOSED, Status.ENDED);
    }


    function invest(uint256 amount) public {
        require(checkStatus(status, Status.OPENED));
        require(amount >= minAmountPerHand);
        require(investedTotalAmount.add(amount) <= ceilAmount);
        require(msg.sender == tx.origin);

        require(token.superTransfer(this, amount));
        address investor = tx.origin;
        if (investedAmountMapping[investor] == 0) {
            investorArray.push(investor);
        }
        investedAmountMapping[investor] = investedAmountMapping[investor].add(amount);
        investedTotalAmount = investedTotalAmount.add(amount);
    }


    function batchRepay(uint256 start, uint256 end) public onlyOwner {
        require(checkStatus(status, Status.ENDED));

        for (uint256 i = start; i < end; i++) {
            address investor = investorArray[i];
            uint256 investedAmount = investedAmountMapping[investor];
            uint256 profitAmount = investedAmount.mul(rateNumerator).div(denominator);
            uint256 repaidAmount = investedAmount.add(profitAmount);

            token.safeTransfer(investor, repaidAmount);
            repaidAmountMapping[investor] = repaidAmount;
            repaidTotalAmount = repaidTotalAmount.add(repaidAmount);
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

    function setCeilAmount(uint256 _ceilAmount) public onlyOwner {
        ceilAmount = _ceilAmount;
    }

    function setMinAmountPerHand(uint256 _minAmountPerHand) public onlyOwner {
        minAmountPerHand = _minAmountPerHand;
    }

    function setERC20(address _ERC20) public onlyOwner {
        require(_ERC20 != 0);
        token = ERC20(_ERC20);
    }

    function setSaleInfo(string _saleInf) public onlyOwner {
        saleInfo = _saleInf;
    }

    function setRepaidCeilAmount(uint256 _repaidCeilAmount)public onlyOwner {
        repaidCeilAmount=_repaidCeilAmount;
    }
}