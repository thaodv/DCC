//
//  WeXWalletTransferResultManager.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/31.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface  WeXWalletTransferResultModel:NSObject

@property (nonatomic,copy)NSString *value;

@property (nonatomic,copy)NSString *txhash;

@end


@interface WeXWalletTransferResultManager : NSObject

@property (nonatomic,strong)NSMutableDictionary *dataDict;

+ (instancetype)manager;

- (void)createGetPendingRequest;

@end
