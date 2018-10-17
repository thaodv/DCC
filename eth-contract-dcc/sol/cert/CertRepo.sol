pragma solidity ^0.4.11;

import "../ownership/OperatorPermission.sol";

contract CertRepo is OperatorPermission {

    mapping(address => Checkpoint[]) checkpoints;

    enum DigestIntegrity {EMPTY, APPLICANT, VERIFIER}

    DigestIntegrity public digest1Integrity;
    DigestIntegrity public digest2Integrity;
    DigestIntegrity public expiredIntegrity;


    struct Checkpoint {

        // `fromBlock` is the block number that the value was generated from
        uint256 fromBlock;

        uint256 dataVersion;

        // `value`
        Content content;

    }

    struct Content {

        bytes digest1;

        bytes digest2;

        uint256 expired;

    }

    bytes public name;


    function setDigest1Integrity(DigestIntegrity _digest1Integrity) public onlyOwner {
        digest1Integrity = _digest1Integrity;
    }

    function setDigest2Integrity(DigestIntegrity _digest2Integrity) public onlyOwner {
        digest2Integrity = _digest2Integrity;
    }

    function setExpiredIntegrity(DigestIntegrity _expiredIntegrity) public onlyOwner {
        expiredIntegrity = _expiredIntegrity;
    }


    function CertRepo(bytes _name, DigestIntegrity _digest1Integrity, DigestIntegrity _digest2Integrity, DigestIntegrity _expiredIntegrity) public {
        name = _name;
        digest1Integrity = _digest1Integrity;
        digest2Integrity = _digest2Integrity;
        expiredIntegrity = _expiredIntegrity;
    }

    function revoke(address applicant) external onlyOperator {
        require(applicant != address(0));

        Checkpoint memory cp = getCheckpointAt(applicant);

        //表示有有效的验证信息
        require(cp.content.digest1.length > 0 || cp.content.digest2.length>0 || cp.content.expired>0);

        uint256 len=checkpoints[applicant].length;
        Checkpoint storage checkpoint=checkpoints[applicant][len-1];
        checkpoint.content.digest1="";
        checkpoint.content.digest2="";
        checkpoint.content.expired=0;

    }

    function appendElement(Checkpoint[] storage checkpointList, uint256 dataVersion, Content memory icd) internal returns (uint256) {
        uint256 length = checkpointList.push(Checkpoint(block.number, dataVersion, icd));
        return length - 1;
    }



    /// @dev `getCheckpointAt` retrieves the number of tokens at a given block number
    /// @param checkpointList The history of values being queried
    /// @param _block The block number to retrieve the value at
    /// @return The number of tokens being queried
    function getCheckpointAt(Checkpoint[] storage checkpointList, uint _block) internal view returns (Checkpoint) {
        if (checkpointList.length == 0) {
            return Checkpoint(0, 0, Content("", "", 0));
        }

        // Shortcut for the actual value
        if (_block >= checkpointList[checkpointList.length - 1].fromBlock)
            return checkpointList[checkpointList.length - 1];
        if (_block < checkpointList[0].fromBlock) {
            return Checkpoint(0, 0, Content("", "", 0));
        }

        // Binary search of the value in the array
        uint min = 0;
        uint max = checkpointList.length - 1;
        while (max > min) {
            uint mid = (max + min + 1) / 2;
            if (checkpointList[mid].fromBlock <= _block) {
                min = mid;
            } else {
                max = mid - 1;
            }
        }
        return checkpointList[min];
    }

    function getCheckpointAt(address _owner) internal view returns (Checkpoint){
        return getCheckpointAt(checkpoints[_owner], block.number);
    }

    function getDataAt(address _owner, uint256 _atBlock) view public returns (bytes digest1, bytes digest2, uint256 expired, uint256 dataVersion) {
        Checkpoint memory cp = getCheckpointAt(checkpoints[_owner], _atBlock);
        return (cp.content.digest1, cp.content.digest2, cp.content.expired, cp.dataVersion);
    }

    function getData(address _owner) view public returns (bytes digest1, bytes digest2, uint256 expired, uint256 dataVersion) {
        return getDataAt(_owner, block.number);
    }

    function getData() view public returns (bytes digest1, bytes digest2, uint256 expired, uint256 dataVersion) {
        return getData(msg.sender);
    }
}