//
//  WeXPasswordCacheModal.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

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
//口袋密码
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

//用户相关信息
//身份认证txhash
@property (nonatomic,copy)NSString *authenTxHash;
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

+ (instancetype)sharedInstance;


@end
