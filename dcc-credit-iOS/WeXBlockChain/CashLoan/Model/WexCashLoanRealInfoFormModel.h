//
//  WexCashLoanRealInfoFormModel.h
//  NealTestDemo
//
//  Created by Kikpop on 2018/10/18.
//  Copyright © 2018 weihuahu. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface WexCashLoanRealInfoFormModel : NSObject<NSCoding>

//婚姻状况
@property (nonatomic, assign) NSInteger marriageStatusIndex;
@property (nonatomic, copy)   NSString *marriageStatus;
//居住地址
@property (nonatomic, assign) NSInteger homeProviceIndex;
@property (nonatomic, assign) NSInteger homeCityIndex;
@property (nonatomic, assign) NSInteger homeDistrictIndex;
@property (nonatomic, copy)   NSString *homeProvice;
@property (nonatomic, copy)   NSString *homeCity;
@property (nonatomic, copy)   NSString *homeDistrict;
@property (nonatomic, copy)   NSString *homeLoacation;
//借款用途
@property (nonatomic, assign) NSInteger loanUseIndex;
@property (nonatomic, copy)   NSString *loanUse;
//工作信息
@property (nonatomic, assign) NSInteger workTypeIndex;
@property (nonatomic, assign) NSInteger workIndustryIndex;
@property (nonatomic, copy)   NSString *workType;
@property (nonatomic, copy)   NSString *workIndustry;
@property (nonatomic, copy)   NSString *workAge;
//企业相关
@property (nonatomic, assign) NSInteger workProviceIndex;
@property (nonatomic, assign) NSInteger workCityIndex;
@property (nonatomic, assign) NSInteger workDistrictIndex;
@property (nonatomic, copy)   NSString *workProvice;
@property (nonatomic, copy)   NSString *workCity;
@property (nonatomic, copy)   NSString *workDistrict;
@property (nonatomic, copy)   NSString *workLocation;
@property (nonatomic, copy)   NSString *workName;
@property (nonatomic, copy)   NSString *workPhoneNumber;
//联系人
@property (nonatomic, assign) NSInteger linkmanOneIndex;
@property (nonatomic, assign) NSInteger linkmanTwoIndex;
@property (nonatomic, assign) NSInteger linkmanThreeIndex;
@property (nonatomic, copy)   NSString *linkmanOne;
@property (nonatomic, copy)   NSString *linkmanTwo;
@property (nonatomic, copy)   NSString *linkmanThree;
@property (nonatomic, copy)   NSString *linkmanOnePhone;
@property (nonatomic, copy)   NSString *linkmanOneName;
@property (nonatomic, copy)   NSString *linkmanTwoPhone;
@property (nonatomic, copy)   NSString *linkmanTwoName;
@property (nonatomic, copy)   NSString *linkmanThreePhone;
@property (nonatomic, copy)   NSString *linkmanThreeName;

@end

NS_ASSUME_NONNULL_END
