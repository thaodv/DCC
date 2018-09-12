//
//  WeXWalletGetDccAcrossChainInfoModal.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"



@interface WeXWalletGetDccAcrossChainInfoParamModal : WeXBaseNetModal
@property (nonatomic,copy)NSString *assetCode;
@end


@interface  WeXWalletGetDccAcrossChainInfoResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *fixedFeeAmount;
@property (nonatomic,copy)NSString *minAmount;
@property (nonatomic,copy)NSString *originReceiverAddress;
@end
