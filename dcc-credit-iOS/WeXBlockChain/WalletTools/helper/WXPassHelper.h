//
//  WXPassHelper.h
//  testWebView
//
//  Created by zhuojian on 2017/11/13.
//  Copyright © 2017年 zhuojian. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger,WexContractConncetType) {
    WexContractConncetTypeINFULA,
    WexContractConncetTypeFTC
};

typedef void(^WXResultBlock)(id response);

@interface WXPassHelper : NSObject
+(id)instance;

+(id)createFTCInstance;

/**初始化以太坊容器*/
-(void)initPassHelperBlock:(WXResultBlock)block;

-(void)connectUrl:(NSString*)url responseBlock:(WXResultBlock)block;

/**
返回钱包address,privateKey
 */
-(void)createAccountBlock:(WXResultBlock)block;

/**
 根据privateKey返回钱包地址
 */
-(void)accountFromPrivateKey:(NSString*)privateKey responseBlock:(WXResultBlock)block;

/** 事物签名
@param contractAddress 合约地址 0xe6af6f27f32efd9eb6dc015d9c08f745029e82be
@param abiInterface 合约接口JSON定义
 eg:
 {"constant":false,"inputs":[{"name":"_publickey","type":"bytes"}],"name":"putKey","outputs":[{"name":"_result","type":"bool"}],"payable":false,"stateMutability":"nonpayable","type":"function"}
@param params 合约参数值定义
@param privateKey 私钥地址
 */
-(void)signTransactionWithContractAddress:(NSString*)contractAddress abiInterface:(NSString*)abiJson params:(NSString*)params privateKey:(NSString*)privateKey responseBlock:(WXResultBlock)block;
//调用以太坊官方方法

- (void)encodeFunCallAbiInterface:(NSString*)abiJson params:(NSString*)params responseBlock:(WXResultBlock)block;

- (void)callContractAddress:(NSString*)contractAddress data:(NSString *)data responseBlock:(WXResultBlock)block;

- (void)call2ContractAddress:(NSString*)contractAddress data:(NSString *)data type:(NSString *)urlStr responseBlock:(WXResultBlock)block;

- (void)getETHNonceWithContractAddress:(NSString*)contractAddress responseBlock:(WXResultBlock)block;

- (void)getETHBalanceWithContractAddress:(NSString*)contractAddress responseBlock:(WXResultBlock)block;
- (void)getETHBalance2WithContractAddress:(NSString*)contractAddress type:(NSString *)urlStr responseBlock:(WXResultBlock)block;
//获取gasprice
- (void)getGasPriceResponseBlock:(WXResultBlock)block;

- (void)getGasLimitWithToAddress:(NSString*)toAddress fromAddress:(NSString*)fromAddress data:(NSString *)data responseBlock:(WXResultBlock)block;

-(void)sendETHTransactionWithContractToAddress:(NSString*)contractToAddress  privateKey:(NSString*)privateKey value:(NSString *)value gasPrice:(NSString *)gasPrice gasLimit:(NSString *)gasLimit nonce:(NSString *)nonce remark:(NSString *)remark responseBlock:(WXResultBlock)block;

-(void)sendERC20TransactionWithContractToAddress:(NSString*)contractToAddress abiJson:(NSString *)abiJson  privateKey:(NSString*)privateKey params:(NSString*)params gasPrice:(NSString *)gasPrice gasLimit:(NSString *)gasLimit nonce:(NSString *)nonce responseBlock:(WXResultBlock)block;

-(void)sendPrivateTransactionWithContractToAddress:(NSString*)contractToAddress abiJson:(NSString *)abiJson  privateKey:(NSString*)privateKey params:(NSString*)params responseBlock:(WXResultBlock)block;
/** nonce值获取
@param accountAddress 钱包地址
*/
-(void)nonceWithAccountAddress:(NSString*)accountAddress responseBlock:(WXResultBlock)block;

/**
 根据私钥和密码来创建keystore
 */
-(void)keystoreCreateWithPrivateKey:(NSString*)privateKey password:(NSString*)password responseBlock:(WXResultBlock)block;

/**
 传入keystore和密码，返回私钥和钱包地址
 */
-(void)restoreAccountFromKeyStore:(NSString*)keyStoreJson  password:(NSString *)password responseBlock:(WXResultBlock)block;

/**
 初始化以太坊连接器
 */
-(void)initProvider:(NSString*)url  responseBlock:(WXResultBlock)block;

/**
 查询事务明细
 */

//返回带txhash
-(void)queryTransactionWithResponseParamReceipt:(NSString*)hashTx  responseBlock:(WXResultBlock)block;
-(void)queryTransactionReceipt:(NSString*)hashTx  responseBlock:(WXResultBlock)block;

-(void)queryTransactionReceiptWithPending:(NSString *)hashTx responseBlock:(WXResultBlock)block;

/** 创建ETH私钥 */
-(void)createPrivateKeyBlock:(WXResultBlock)block;
/**
 发送事务
 */
-(void)sendRawTransaction:(NSString*)rawTransaction  responseBlock:(WXResultBlock)block;

/**
 调用方法（开发用途）
 */
-(void)callFuncWithABIJson:(NSString*)abiJson params:(NSString*)params abiAddress:(NSString*)abiAddress responseBlock:(WXResultBlock)block;

-(void)hexStringFromBytes:(NSData*)data responseBlock:(WXResultBlock)block;
@end
