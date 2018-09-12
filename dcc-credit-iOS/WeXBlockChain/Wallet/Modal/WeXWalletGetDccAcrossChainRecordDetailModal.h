//
//  WeXWalletGetDccAcrossChainRecordDetailModal.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXWalletGetDccAcrossChainRecordDetailParamModal : WeXBaseNetModal
@property (nonatomic,copy)NSString *orderId;
@end


@interface  WeXWalletGetDccAcrossChainRecordDetailResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *originAssetCode;
@property (nonatomic,copy)NSString *originAmount;
@property (nonatomic,copy)NSString *destAssetCode;
@property (nonatomic,copy)NSString *destAmount;
@property (nonatomic,copy)NSString *feeAmount;
@property (nonatomic,copy)NSString *toAssetCode;
@property (nonatomic,copy)NSString *status;
@property (nonatomic,copy)NSString *createdTime;

@property (nonatomic,copy)NSString *lastUpdatedTime;
@property (nonatomic,copy)NSString *originReceiverAddress;
@property (nonatomic,copy)NSString *beneficiaryAddress;
@property (nonatomic,copy)NSString *destPayerAddress;
@property (nonatomic,copy)NSString *originTxHash;
@property (nonatomic,copy)NSString *destTxHash;
@property (nonatomic,copy)NSString *originBlockNumber;
@property (nonatomic,copy)NSString *destBlockNumber;
@property (nonatomic,copy)NSString *originTradeTime;
@property (nonatomic,copy)NSString *destTradeTime;
@end


