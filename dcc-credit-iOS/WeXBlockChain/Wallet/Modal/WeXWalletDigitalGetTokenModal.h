//
//  WeXWalletDigitalGetTokenModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXWalletDigitalGetTokenParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *search;
@property (nonatomic,copy)NSString *pageNo;
@property (nonatomic,copy)NSString *pageSize;

@end

@protocol WeXWalletDigitalGetTokenResponseModal_item;
@interface  WeXWalletDigitalGetTokenResponseModal_item : WeXBaseNetModal

@property (nonatomic,copy)NSString *name;
@property (nonatomic,copy)NSString *symbol;
@property (nonatomic,copy)NSString *contractAddress;
@property (nonatomic,copy)NSString *decimals;
@property (nonatomic,copy)NSString *iconUrl;
@property (nonatomic,copy)NSString *balance;
@property (nonatomic,copy)NSString *asset;
@property (nonatomic,copy)NSString *price;

@end

@interface  WeXWalletDigitalGetTokenResponseModal : WeXBaseNetModal

@property (nonatomic,assign)NSInteger pageNo;
@property (nonatomic,copy)NSString *pageSize;
@property (nonatomic,copy)NSString *totalPages;

@property (nonatomic, strong) NSMutableArray<WeXWalletDigitalGetTokenResponseModal_item>* items;

@end
