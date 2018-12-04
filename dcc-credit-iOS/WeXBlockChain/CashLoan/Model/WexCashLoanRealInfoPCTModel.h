//
//  WexCashLoanRealInfoPCTModel.h
//  NealTestDemo
//
//  Created by Kikpop on 2018/10/18.
//  Copyright © 2018 weihuahu. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol WexCashLoanRealInfoAreaModel;
@protocol WexCashLoanRealInfoProvinceModel;
@interface WexCashLoanRealInfoPCTModel : WeXBaseNetModal

@property (nonatomic, strong) NSArray <WexCashLoanRealInfoProvinceModel>*province;
@property (nonatomic, strong) NSArray <WexCashLoanRealInfoAreaModel>*area;

@end

@interface WexCashLoanRealInfoProvinceModel : WeXBaseNetModal

@property (nonatomic, strong) NSArray <WexCashLoanRealInfoAreaModel>*city;
@property (nonatomic, copy) NSString *id;
@property (nonatomic, copy) NSString *name;

@end

@interface WexCashLoanRealInfoAreaModel : WeXBaseNetModal

@property (nonatomic, copy) NSString *id;
@property (nonatomic, copy) NSString *pid;
@property (nonatomic, copy) NSString *name;

@end


NS_ASSUME_NONNULL_END
