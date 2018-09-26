//
//  WeXCPPotListMainModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/25.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXCPPotListParamModel : WeXBaseNetModal

@property (nonatomic, copy) NSString *userAddress;


@end

@interface WeXCPPotListSaleInfoModel : WeXBaseNetModal

@property (nonatomic, copy) NSString * _Nullable presentation;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *startTime;
@property (nonatomic, copy) NSString *closeTime;
@property (nonatomic, copy) NSString *incomeTime;
@property (nonatomic, copy) NSString *endTime;
@property (nonatomic, copy) NSString *period;
@property (nonatomic, copy) NSString *annualRate;
@property (nonatomic, copy) NSString *profitMethod;

@end


@protocol WeXCPPotListSaleInfoModel;
@interface WeXCPPotListResultModel : WeXBaseNetModal
//投资本金
@property (nonatomic, copy) NSString *positionAmount;
//应回金额(本金 + 利息)
@property (nonatomic, copy) NSString *expectedRepay;
@property (nonatomic, copy) NSString *status;
@property (nonatomic, copy) NSString *assetCode;
@property (nonatomic, strong) WeXCPPotListSaleInfoModel <WeXCPPotListSaleInfoModel> *saleInfo;
//期数
@property (nonatomic, copy) NSString *name;
// 合约地址
@property (nonatomic, copy) NSString *contractAddress;



@end

@protocol WeXCPPotListResultModel;

@interface WeXCPPotListMainModel : WeXBaseNetModal
@property (nonatomic, strong) NSArray  <WeXCPPotListResultModel *> <WeXCPPotListResultModel> *result;

@end

