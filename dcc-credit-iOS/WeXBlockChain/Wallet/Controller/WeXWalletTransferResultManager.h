//
//  WeXWalletTransferResultManager.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/31.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface  WeXWalletTransferPendingModel: WeXBaseNetModal

@property (nonatomic,copy)NSString *value;

@property (nonatomic,copy)NSString *txhash;

@property (nonatomic,copy)NSString *from;

@property (nonatomic,copy)NSString *to;

@property (nonatomic,copy)NSString *timeStamp;

@property (nonatomic,copy)NSString *nonce;

@property (nonatomic,copy)NSString *gasPrice;

@property (nonatomic,copy)NSString *gasLimit;

@end


@interface WeXWalletTransferResultManager : NSObject


- (instancetype)initWithTokenSymbol:(NSString *)symbol isPrivateChain:(BOOL)isPrivate response:(void(^)(void))responseBlock;
- (WeXWalletTransferPendingModel *)getPendingModelWithSymbol:(NSString *)symbol;
- (WeXWalletTransferPendingModel *)getAllCoinPendingModel;
- (void)savePendingModel:(WeXWalletTransferPendingModel *)model symbol:(NSString *)symbol;
- (void)deletePendingModelWithSymbol:(NSString *)symbol;
- (void)beginRefresh;
- (void)endRefresh;

@end
