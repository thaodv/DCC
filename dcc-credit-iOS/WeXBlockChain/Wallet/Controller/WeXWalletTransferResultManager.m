//
//  WeXWalletTransferResultManager.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/31.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletTransferResultManager.h"
#import "WeXWalletEtherscanGetPendingAdapter.h"
#import "WeXDigitalAssetRLMModel.h"
#import "NSString+WexTool.h"


@implementation WeXWalletTransferPendingModel


@end

#define kTransferTokenKey _isPrivateChain?[NSString stringWithFormat:@"%@_private",_symbol]:_symbol

@interface WeXWalletTransferResultManager()<WexBaseNetAdapterDelegate>
{
    NSTimer *_timer;
    NSString *_symbol;
    BOOL _isPrivateChain;
    void (^_responseBlock)(void);
}


@end

@implementation WeXWalletTransferResultManager


- (instancetype)initWithTokenSymbol:(NSString *)symbol isPrivateChain:(BOOL)isPrivate response:(void(^)(void))responseBlock
{
    self = [super init];
    if (self) {
        _symbol = symbol;
        _isPrivateChain = isPrivate;
        _responseBlock = responseBlock;
    }
    return self;
    
}

- (WeXWalletTransferPendingModel *)getPendingModelWithSymbol:(NSString *)symbol
{
    NSMutableDictionary *dataDict = [WexCommonFunc getPendingTransactionRecord];
    if (!dataDict) {
        dataDict = [NSMutableDictionary dictionary];
    }
    NSString *modelStr = [dataDict objectForKey:symbol];
    WeXWalletTransferPendingModel *pendingModel = [[WeXWalletTransferPendingModel alloc] initWithString:modelStr error:nil];
    return pendingModel;
}
- (WeXWalletTransferPendingModel *)getAllCoinPendingModel {
    RLMResults <WeXDigitalAssetRLMModel *>*results = [WeXDigitalAssetRLMModel allObjects];
    NSMutableDictionary *dataDict = [WexCommonFunc getPendingTransactionRecord];
    if (!dataDict) {
        dataDict = [NSMutableDictionary dictionary];
    }
    NSString *modelStr = nil;
    WeXWalletTransferPendingModel *pendingModel = nil;
    for (WeXDigitalAssetRLMModel *rlmModel in results) {
        modelStr = [dataDict objectForKey:rlmModel.symbol];
        if ([modelStr isVaildString]) {
            pendingModel = [[WeXWalletTransferPendingModel alloc] initWithString:modelStr error:nil];
            break;
        } else {
            pendingModel = nil;
        }
    }
    return pendingModel;
}

- (void)deletePendingModelWithSymbol:(NSString *)symbol
{
    NSMutableDictionary *dataDict = [WexCommonFunc getPendingTransactionRecord];
    if (!dataDict) {
        dataDict = [NSMutableDictionary dictionary];
    }
    [dataDict removeObjectForKey:symbol];
     [WexCommonFunc savePendingTransactionRecord:dataDict];
}

- (void)savePendingModel:(WeXWalletTransferPendingModel *)model symbol:(NSString *)symbol
{
    NSMutableDictionary *dataDict = [WexCommonFunc getPendingTransactionRecord];
    if (!dataDict) {
        dataDict = [NSMutableDictionary dictionary];
    }
    NSString *modelStr = [model toJSONString];
    [dataDict setObject:modelStr forKey:symbol];
    [WexCommonFunc savePendingTransactionRecord:dataDict];
}

- (void)beginRefresh
{
    if ([_symbol isEqualToString:@"DCC"]&&_isPrivateChain)
    {
        if (!_timer) {
            _timer = [NSTimer scheduledTimerWithTimeInterval:3 target:self selector:@selector(refreshTransferRecord) userInfo:nil repeats:YES];
            [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
        }
        [_timer fire];
    }
    else
    {
         if (!_timer) {
             _timer = [NSTimer scheduledTimerWithTimeInterval:20 target:self selector:@selector(refreshTransferRecord) userInfo:nil repeats:YES];
             [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
         }
        [_timer fire];
        
    }
    
}

- (void)endRefresh
{
    if (_timer) {
        [_timer invalidate];
        _timer = nil;
    }
}


- (void)refreshTransferRecord{
    
    NSMutableDictionary *dataDict = [WexCommonFunc getPendingTransactionRecord];
    if (!dataDict) {
        dataDict = [NSMutableDictionary dictionary];
        [WexCommonFunc savePendingTransactionRecord:dataDict];
        return;
    }
    
    NSString *modelStr = [dataDict objectForKey:kTransferTokenKey];
    WeXWalletTransferPendingModel *pendingModel = [[WeXWalletTransferPendingModel alloc] initWithString:modelStr error:nil];
    if (pendingModel) {
        if ([_symbol isEqualToString:@"DCC"]&&_isPrivateChain)
        {
            //2018.8.10 私链互转查询状态返回空指针报错
            [[WXPassHelper instance] initProvider:WEX_DCC_NODE_SERVER responseBlock:^(id response)
             {
                 [[WXPassHelper instance] queryTransactionReceipt:pendingModel.txhash responseBlock:^(id response) {
                     NSLog(@"response--%@",response);
                     if ([response isKindOfClass:[NSDictionary class]]) {
                         NSString *transactionHash = [response objectForKey:@"transactionHash"];
                         if (transactionHash)
                         {
                             [self deletePendingModelWithSymbol:kTransferTokenKey];
                             [self endRefresh];
                             if (_responseBlock) {
                                 _responseBlock();
                             }
                         }
                     }
                 }];
             }];
        }
        else
        {
            [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
             {
                 //发送上链请求
                 [[WXPassHelper instance] getETHNonceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
                     NSLog(@"Nonce=%@",response);
                     NSString *nonce = response;
                     if ([nonce integerValue] > [pendingModel.nonce integerValue]||[nonce integerValue] < [pendingModel.nonce integerValue]) {
                         [self deletePendingModelWithSymbol:kTransferTokenKey];
                         [self endRefresh];
                         if (_responseBlock) {
                             _responseBlock();
                         }
                     }
                 }];
             }];
        }
    }
    else
    {
        [self endRefresh];
    }
}

-(void)dealloc
{
    [self endRefresh];
}





@end
