//
//  WeXWalletGetAcrossChainTransferTotalModal.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXWalletGetAcrossChainTransferTotalParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *originAssetCodeList;
@property (nonatomic,copy)NSString *startTime;
@property (nonatomic,copy)NSString *endTime;

@end

@protocol WeXWalletGetAcrossChainTransferTotalResponseModal_item;
@interface  WeXWalletGetAcrossChainTransferTotalResponseModal_item : WeXBaseNetModal

@property (nonatomic,copy)NSString *originAssetCode;
@property (nonatomic,copy)NSString *destAssetCode;
@property (nonatomic,copy)NSString *totalAmount;
@end



@interface  WeXWalletGetAcrossChainTransferTotalResponseModal : WeXBaseNetModal

@property (nonatomic,strong)NSMutableArray<WeXWalletGetAcrossChainTransferTotalResponseModal_item> *result;


@end

