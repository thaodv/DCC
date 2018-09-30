//
//  WeXPasswordCacheModal.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"
//身份认证状态
typedef NS_ENUM(NSInteger,WeXCreditIDAuthenStatusType) {
    WeXCreditIDAuthenStatusTypeNone,//未认证
    WeXCreditIDAuthenStatusTypeAuthening,//进行中
    WeXCreditIDAuthenStatusTypeSuccess,//已完成
    WeXCreditIDAuthenStatusTypeInvalid//已过期
};

//银行卡认证状态
typedef NS_ENUM(NSInteger,WeXCreditBankAuthenStatusType) {
    WeXCreditBankAuthenStatusTypeNone,//未认证
    WeXCreditBankAuthenStatusTypeAuthening,//进行中
    WeXCreditBankAuthenStatusTypeSuccess,//已完成
    WeXCreditBankAuthenStatusTypeInvalid //已过期
};

//手机运营商证状态
typedef NS_ENUM(NSInteger,WeXCreditMobileOperatorAuthenStatusType) {
    WeXCreditMobileOperatorAuthenStatusTypeNone,//未认证
    WeXCreditMobileOperatorAuthenStatusTypeAuthening,//进行中
    WeXCreditMobileOperatorAuthenStatusTypeSuccess,//已完成
    WeXCreditMobileOperatorAuthenStatusTypeInvalid //已过期
};

//同牛手机运营商证状态
typedef NS_ENUM(NSInteger,WeXSameCowMobileOperatorAuthenStatusType) {
    WeXSameCowMobileOperatorAuthenStatusTypeNone,//未认证
    WeXSameCowMobileOperatorAuthenStatusTypeAuthening,//进行中
    WeXSameCowMobileOperatorAuthenStatusTypeSuccess,//已完成
    WeXSameCowMobileOperatorAuthenStatusTypeInvalid //已过期
};

typedef NS_ENUM(NSInteger,WeXPasswordType) {
    WeXPasswordTypeNone,
    WeXPasswordTypeGesture,
    WeXPasswordTypeNumber,
    WeXPasswordTypeTouchID
};

@interface WeXPasswordCacheModal : WeXBaseNetModal

//RSA公钥
@property (nonatomic,copy)NSString *rsaPublicKey;
//RSA私钥
@property (nonatomic,copy)NSString *rasPrivateKey;
//钱包密码
@property (nonatomic,copy)NSString *passportPassword;
//钱包私钥
@property (nonatomic,copy)NSString *walletPrivateKey;
//keyStore
@property (nonatomic,copy)NSDictionary *keyStore;
//手势密码
@property (nonatomic,copy)NSString *gesturePassword;
//数字密码
@property (nonatomic,copy)NSString *numberPassword;
//密码类型
@property (nonatomic,assign)WeXPasswordType passwordType;
//统一登录状态
@property (nonatomic,assign)BOOL isAllow;
//是否注册
@property (nonatomic,assign)BOOL hasMemberId;


#pragma mark - 用户认证相关 --身份证认证
//身份认证状态
@property (nonatomic,assign)WeXCreditIDAuthenStatusType idAuthenStatus;
//身份认证Nonce
@property (nonatomic,copy)NSString *idAuthenNonce;
//身份认证OrderId
@property (nonatomic,copy)NSString *idAuthenOrderId;
//身份认证txhash
@property (nonatomic,copy)NSString *idAuthenTxHash;

@property (nonatomic,copy)NSString *similarity;

//用户相关信息
//地址
@property (nonatomic,copy)NSString *userAddress;
//姓名
@property (nonatomic,copy)NSString *userName;
//身份证号码
@property (nonatomic,copy)NSString *userNumber;
//民族
@property (nonatomic,copy)NSString *userNation;
//性别
@property (nonatomic,copy)NSString *userSex;
//过期时间
@property (nonatomic,copy)NSString *userTimeLimit;
//签名机构
@property (nonatomic,copy)NSString *userAuthority;

@property (nonatomic,copy)NSString *userYear;

@property (nonatomic,copy)NSString *userMonth;

@property (nonatomic,copy)NSString *userDay;

#pragma mark - 用户认证相关 --银行卡认证

//银行卡认证状态
@property (nonatomic,assign)WeXCreditBankAuthenStatusType bankAuthenStatus;
//银行卡认证Nonce
@property (nonatomic,copy)NSString *bankAuthenNonce;
//银行卡认证OrderId
@property (nonatomic,copy)NSString *bankAuthenOrderId;
//银行卡认证txhash
@property (nonatomic,copy)NSString *bankAuthenTxHash;

@property (nonatomic,copy)NSString *bankAuthenTicket;

@property (nonatomic,copy)NSString *bankAuthenCode;

@property (nonatomic,copy)NSString *bankAuthenCardName;

@property (nonatomic,copy)NSString *bankAuthenCardNumber;

@property (nonatomic,copy)NSString *bankAuthenMobile;

//过期时间
@property (nonatomic,copy)NSString *bankTimeLimit;

#pragma mark - 用户认证相关 --手机运营商认证

//手机运营商认证状态
@property (nonatomic,assign)WeXCreditMobileOperatorAuthenStatusType mobileAuthenStatus;
//手机运营商认证Nonce
@property (nonatomic,copy)NSString *mobileAuthenNonce;
//手机运营商认证OrderId
@property (nonatomic,copy)NSString *mobileAuthenOrderId;
//手机运营商认证txhash
@property (nonatomic,copy)NSString *mobileAuthenTxHash;

//手机号码
@property (nonatomic,copy)NSString *mobileAuthenNumber;
//过期时间
@property (nonatomic,copy)NSString *phoneTimeLimit;

#pragma mark - 用户认证相关 --同牛手机运营商认证

//同牛手机运营商认证状态
@property (nonatomic,assign)WeXSameCowMobileOperatorAuthenStatusType sameCowMobileAuthenStatus;
//同牛手机运营商认证Nonce
@property (nonatomic,copy)NSString *sameCowMobileAuthenNonce;
//同牛手机运营商认证OrderId
@property (nonatomic,copy)NSString *sameCowMobileAuthenOrderId;
//同牛手机运营商认证txhash
@property (nonatomic,copy)NSString *sameCowMobileAuthenTxHash;

//同牛手机号码
@property (nonatomic,copy)NSString *sameCowMobileAuthenNumber;
//同牛过期时间
@property (nonatomic,copy)NSString *sameCowMobileAuthenLimit;

+ (instancetype)sharedInstance;


@end
