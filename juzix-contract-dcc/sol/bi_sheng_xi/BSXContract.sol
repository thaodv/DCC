pragma solidity ^0.4.2;

import "../permission/HasToken.sol";
import "../math/SafeMath.sol";
//币生息
contract BSXContract is HasToken {

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

    uint256 public denominator = 100;

    string public saleInfo;

    enum Status {CREATED, OPENED, CLOSED, ENDED, SETTLED}

    mapping(address => uint256) public investedAmountMapping;
    mapping(address => uint256) public repaidAmountMapping;

    address[] public investorArray;

    event statusUpdated(Status indexed status);
    event invested(address investor, uint256 investedAmount);
    event repaid(address investor, uint256 repaidAmount);

    function BSXContract(){
        register("BSXContractModule", "0.0.1.0", "BSXContract", "0.0.1.0");
        status = Status.CREATED;
    }

    function open()  public {
        onlyOwner();
        checkAndChangeStatus(status, Status.CREATED, Status.OPENED);
    }

    function close()  public {
        onlyOwner();
        checkAndChangeStatus(status, Status.OPENED, Status.CLOSED);
    }

    function end()  public {
        onlyOwner();
        repaidCeilAmount = investedTotalAmount.add(investedTotalAmount.mul(rateNumerator).div(denominator));
        checkAndChangeStatus(status, Status.CLOSED, Status.ENDED);
    }


    function invest(uint256 amount) public {
        if(!(checkStatus(status, Status.OPENED))){
            log("!(checkStatus(status, Status.OPENED))");
            throw;
        }
        if(!(amount >= minAmountPerHand)){
            log("!(amount >= minAmountPerHand)");
            throw;
        }
        if(!(investedTotalAmount.add(amount) <= investCeilAmount)){
            log("!(investedTotalAmount.add(amount) <= investCeilAmount)");
            throw;
        }
        if(!(msg.sender == tx.origin)){
            log("!(msg.sender == tx.origin)");
            throw;
        }

        if(!(token.superTransfer(this, amount))){
            log("!(token.superTransfer(this, amount))");
            throw;
        }
        address investor = tx.origin;
        if (investedAmountMapping[investor] == 0) {
            investorArray.push(investor);
        }
        investedAmountMapping[investor] = investedAmountMapping[investor].add(amount);
        investedTotalAmount = investedTotalAmount.add(amount);
        invested(msg.sender, amount);
    }


    function batchRepay(uint256 start, uint256 end) public  {
        onlyOwner();
        if(!(checkStatus(status, Status.ENDED))){
            log("!(checkStatus(status, Status.ENDED))");
            throw;
        }

        for (uint256 i = start; i < end; i++) {
            address investor = investorArray[i];
            if (repaidAmountMapping[investor] == 0) {
                uint256 investedAmount = investedAmountMapping[investor];
                uint256 profitAmount = investedAmount.mul(rateNumerator).div(denominator);
                uint256 repaidAmount = investedAmount.add(profitAmount);

                token.safeTransfer(investor, repaidAmount);
                repaidAmountMapping[investor] = repaidAmount;
                repaidTotalAmount = repaidTotalAmount.add(repaidAmount);
                repaid(investor, repaidAmount);
            }
        }
    }

    function settle() public {
        onlyOwner();
        if(!(checkStatus(status, Status.ENDED))){
            log("!(checkStatus(status, Status.ENDED))");
            throw;
        }
        if(!(repaidTotalAmount == repaidCeilAmount)){
            log("!(repaidTotalAmount == repaidCeilAmount)");
            throw;
        }
        justChangeStatus(Status.SETTLED);
    }

    function checkAndChangeStatus(Status inputStatus, Status expectedStatus, Status nextStatus) internal {
        if(!((checkStatus(inputStatus, expectedStatus)))){
            log("!((checkStatus(inputStatus, expectedStatus)))");
            throw;
        }
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

    function setRateNumerator(uint256 _rateNumerator) public {
        onlyOwner();
        rateNumerator = _rateNumerator;
    }

    function setDenominator(uint256 _denominator) public {
        onlyOwner();
        denominator = _denominator;
    }

    function setInvestCeilAmount(uint256 _investCeilAmount) public {
        onlyOwner();
        investCeilAmount = _investCeilAmount;
    }

    function setMinAmountPerHand(uint256 _minAmountPerHand) public {
        onlyOwner();
        minAmountPerHand = _minAmountPerHand;
    }

    function setERC20(address _ERC20) public {
        onlyOwner();
        if(!(_ERC20 != 0)){
            log("!(_ERC20 != 0)");
            throw;
        }
        token = ERC20(_ERC20);
    }

    function setSaleInfo(string _saleInf) public {
        onlyOwner();
        saleInfo = _saleInf;
    }

    function getInvestorArrayLength()public constant returns(uint256 length){
        return investorArray.length;
    }

}