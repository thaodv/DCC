import {CertService3,IpfsToken} from "./abi"

let description = {
  "ID_CERT":{
    "desc":"The user submit name, ID number, and face recognition photo to confirm the identity information. After the authentication is passed, the plaintext of the real name information is stored locally on the mobile phone and the encrypted information of the real name information is stored on blockchain by the Identity Verification contract. You can query information stored in the contract on this page.",
    "time":"2018-04-10",
    "name":"Identity Verification",
    "abi":CertService3,
    "business":"id",
    "methods":{
      "cn":"Identity",
      "en":"getData",
      "index":2
    }
  },
  "BANK_CARD_CERT":{
    "desc":"The user submit bank card number, bank name, and the reserve phone number to confirm the bank card information. After the authentication is passed, the plaintext of the bank card information is stored locally on the mobile phone and the encrypted information of the the bank card information is stored on blockchain by the the Bank Card Verification contract. You can query information stored in the contract on this page.",
    "time":"2018-04-10",
    "name":"Bank Card Verification",
    "abi":CertService3,
    "business":"bankCard",
    "methods":{
      "cn":"Bankcard ",
      "en":"getData",
      "index":2
    }
  },
  "COMMUNICATION_LOG_CERT":{
    "desc":"The user submit phone number and phone number password to confirm the call records information. After the authentication is passed, the plaintext of the call records information is stored locally on the mobile phone and the encrypted information of the call records information is stored on blockchain by the  Call Records Verification contract. You can query information stored in the contract on this page.",
    "time":"2018-04-10",
    "name":"Call Records Verification",
    "abi":CertService3,
    "business":"communicationLog",
    "methods":{
      "cn":"Call Records",
      "en":"getData",
      "index":2
    }
  },
  "IPFS":{
    "desc":"After the user uses the cloud storage function, the local authentication data can be encrypted and uploaded to the IPFS. The wallet can be re-imported to synchronize the data to the mobile phone without requiring the user to re-authenticate. IPFS contract stores IPFS token and encrypted local data. You can query information stored in the contract on this page.",
    "time":"2018-09-07",
    "name":"IPFS Data Storage",
    "abi":IpfsToken,
    "methods":{
      "cn":"",
      "en":"IpfsToken",
      "index":6
    }
  },
  "DCP":{
    "desc":"Through contracts on DCR (Distributed Credit Report), there will generate a list of credit history index in the DCC system that records the individual’s whole life cycle status from application for loans, review of loans, repayment, overdue loans, collection, and bad debt. The list of indexes alongside the plain text data of the actual loan contract held by the individual constitutes a user’s credit history report in DCC system, which is also the embodiment of returning data to the individual by DCC system. For each record in the DCR, only the borrower and lender hold the plain text data, and DCR has only the index list. You can query information stored in the contract on this page.",
    "time":"2018-04-10",
    "name":"Distributed Credit Report",
    "abi":IpfsToken,
    "methods":{
      "cn":"",
      "en":"IpfsToken",
      "index":6
    }
  }
}
export {description}