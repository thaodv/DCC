//
//  WeXWalletGetAcrossChainTransferRecordModal.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/25.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface  WeXWalletGetAcrossChainTransferRecordParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *number;
@property (nonatomic,copy)NSString *size;

@property (nonatomic,copy)NSString *startTime;
@property (nonatomic,copy)NSString *endTime;

@end

@protocol WeXWalletGetAcrossChainTransferRecordResponseModal_item;
@interface  WeXWalletGetAcrossChainTransferRecordResponseModal_item : WeXBaseNetModal

@property (nonatomic,copy)NSString *recordId;
@property (nonatomic,copy)NSString *createdTime;
@property (nonatomic,copy)NSString *originAssetCode;
@property (nonatomic,copy)NSString *status;
@property (nonatomic,copy)NSString *feeAmount;
@property (nonatomic,copy)NSString *toAssetCode;
@property (nonatomic,copy)NSString *originAmount;
@property (nonatomic,copy)NSString *destAmount;
@end

@interface  WeXWalletGetAcrossChainTransferRecordResponseModal : WeXBaseNetModal

@property (nonatomic,strong)NSMutableArray<WeXWalletGetAcrossChainTransferRecordResponseModal_item> *items;

@end
