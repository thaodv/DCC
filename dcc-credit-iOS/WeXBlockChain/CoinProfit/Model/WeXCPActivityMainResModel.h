//
//  WeXCPActivityMainResModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/25.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXCPActivityListSaleInfoModel : WeXBaseNetModal
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

@protocol WeXCPActivityListSaleInfoModel ;
@interface WeXCPActivityListModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *logo;
@property (nonatomic, copy) NSString *assetCode;
@property (nonatomic, copy) NSString *minPerHand;
@property (nonatomic, strong) WeXCPActivityListSaleInfoModel <WeXCPActivityListSaleInfoModel> *saleInfo;
@property (nonatomic, copy) NSString *status;
@property (nonatomic, copy) NSString *name; //第几期
@property (nonatomic, copy) NSString *contractAddress; //合约地址



@end


@protocol WeXCPActivityListModel;
@interface WeXCPActivityMainResModel : WeXBaseNetModal
@property (nonatomic, strong) NSArray <WeXCPActivityListModel *> <WeXCPActivityListModel>  * result;

@end


