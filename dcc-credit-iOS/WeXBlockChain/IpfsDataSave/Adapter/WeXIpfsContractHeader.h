//
//  WeXIpfsContractHeader.h
//  WeXBlockChain
//
//  Created by wh on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#ifndef WeXIpfsContractHeader_h
#define WeXIpfsContractHeader_h

//ipfs合约查询私钥
#define WEX_IPFS_ABI_GET_KEY  @"{\"constant\": true,\"inputs\": [],\"name\": \"getIpfsKeyHash\",\"outputs\": [{\"name\": \"version\",\"type\": \"uint256\"},{\"name\": \"value\",\"type\": \"bytes32\"}],\"payable\": false,\"type\": \"function\"}"

//ipfs合约删除私钥
#define WEX_IPFS_ABI_DELETE_KEY  @"{\"constant\": false,\"inputs\": [],\"name\": \"deleteIpfsKeyHash\",\"outputs\": [],\"payable\": false,\"type\": \"function\"}"

//ipfs合约上传私钥
#define WEX_IPFS_ABI_POST_KEY  @"{\"constant\": false,\"inputs\": [{\"name\": \"value\",\"type\": \"bytes32\"}],\"name\": \"addIpfsKeyHash\",\"outputs\": [],\"payable\": false,\"type\": \"function\"}"

//ipfs合约Hash查询私钥
#define WEX_IPFS_ABI_GET_TOKEN  @"{\"constant\": true,\"inputs\": [{\"name\": \"contractAddress\",\"type\": \"address\"}],\"name\": \"getIpfsToken\",\"outputs\": [{\"name\": \"_userAddress\",\"type\": \"address\"},{\"name\": \"_contractAddress\",\"type\": \"address\"},{\"name\": \"_version\",\"type\": \"uint256\"},{\"name\": \"_cipher\",\"type\": \"string\"},{\"name\": \"_token\",\"type\": \"string\"},{\"name\": \"_iv\",\"type\": \"bytes\"},{\"name\": \"_digest1\",\"type\": \"bytes\"},{\"name\": \"_digest2\",\"type\": \"bytes\"},{\"name\": \"_keyHashVersion\",\"type\": \"uint256\"}],\"payable\": false,\"type\": \"function\"}"

//ipfs合约Hash上传私钥
#define WEX_IPFS_ABI_POST_TOKEN @"{\"constant\": false,\"inputs\": [{\"name\": \"contractAddress\",\"type\": \"address\"},{\"name\": \"version\",\"type\": \"uint256\"},{\"name\": \"cipher\",\"type\": \"string\"},{\"name\": \"token\",\"type\": \"string\"},{\"name\": \"iv\",\"type\": \"bytes\"},{\"name\": \"digest1\",\"type\": \"bytes\"},{\"name\": \"digest2\",\"type\": \"bytes\"},{\"name\": \"keyHash\",\"type\": \"bytes32\"}],\"name\": \"putIpfsToken\",\"outputs\": [],\"payable\": false,\"type\": \"function\"}"

//ipfs合约Hash删除
#define WEX_IPFS_ABI_DELETE_TOKEN @"{\"constant\": false,\"inputs\": [{\"name\": \"contractAddress\",\"type\": \"address\"}],\"name\": \"deleteIpfsToken\",\"outputs\": [],\"payable\": false,\"type\": \"function\"}"

//用户加密私钥
#define WEX_IPFS_MY_KEY @"userIpfsPwd"
//用户校验密码私钥
#define WEX_IPFS_MY_CHECKKEY @"checkUserIpfsPwd"
//重置钱包后的临时私钥
#define WEX_IPFS_MY_TWOCHECKKEY @"twoCheckUserIpfsPwd"
//本地展示的Hash
#define WEX_IPFS_IDENTIFY_HASH @"ipfsIdentifyHash"
#define WEX_IPFS_BankCard_HASH @"ipfsBankCardHash"
#define WEX_IPFS_PhoneOperator_HASH @"ipfsPhoneOperatorHash"
#define WEX_IPFS_ENCRYPTION_VERSION @"ipfsEncryptionVersion"

#define WEX_IPFS_WEAK_BLACK  __weak __typeof(self)weakSelf = self

#define WEX_IPFS_MAIN_URL @"http://ipfs.dcc.finance:8000/ipfs/"

//ipfs自定义节点
#define WEX_IPFS_MAIN_NONEURL @"ipfsMainNoneUrl"
#define WEX_IPFS_DEFAULT_NONEURL @"ipfsDefaultNoneUrl"
#define WEX_IPFS_CUSTOM_NONEURL @"ipfsCustomNoneUrl"
#define WEX_IPFS_CUSTOM_PUBLICADDRESS @"ipfsCustomPublicAddress"
#define WEX_IPFS_CUSTOM_PORTNUM @"ipfsCustomPortNum"

#endif /* WeXIpfsContractHeader_h */
