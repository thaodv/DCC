//
//  WeXWalletTransferResultManager.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/31.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletTransferResultManager.h"
#import "WeXWalletEtherscanGetPendingAdapter.h"


@implementation WeXWalletTransferResultModel

@end

@interface WeXWalletTransferResultManager()<WexBaseNetAdapterDelegate>

@property (nonatomic,strong)WeXWalletEtherscanGetPendingAdapter *getPendingAdapter;

@end

@implementation WeXWalletTransferResultManager
+ (instancetype)manager{
   static WeXWalletTransferResultManager *manager;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[WeXWalletTransferResultManager alloc] init];
        manager.dataDict = [NSMutableDictionary dictionary];
    });
    return manager;
}


- (void)createGetPendingRequest{
    _getPendingAdapter = [[WeXWalletEtherscanGetPendingAdapter alloc]  init];
    _getPendingAdapter.delegate = self;
    WeXWalletEtherscanGetPendingParamModal *paramModal = [[WeXWalletEtherscanGetPendingParamModal alloc] init];
    paramModal.module = @"proxy";
    paramModal.action = @"eth_getTransactionReceipt";
    paramModal.txhash = @"0x1e2910a262b1008d0616a0beb24c1a491d78771baa54a33e66065e03b1f46bc1";
    [_getPendingAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getPendingAdapter) {
        
    }
 
}


@end
