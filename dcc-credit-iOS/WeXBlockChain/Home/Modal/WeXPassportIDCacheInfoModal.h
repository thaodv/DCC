//
//  WeXPassportIDCacheInfoModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/2/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXPassportIDCacheInfoModal : WeXBaseNetModal

//身份证正面
@property (nonatomic,strong)NSData *frontIDData;
//身份证反面
@property (nonatomic,strong)NSData *backIDData;
//人体头像
@property (nonatomic,strong)NSData *userHeadData;

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
