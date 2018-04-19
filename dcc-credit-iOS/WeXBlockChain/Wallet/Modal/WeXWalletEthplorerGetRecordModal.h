//
//  WeXWalletEthplorerGetRecordModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/30.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXWalletEthplorerGetRecordParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *limit;
@property (nonatomic,copy)NSString *token;
@property (nonatomic,copy)NSString *address;
@property (nonatomic,copy)NSString *type;
@property (nonatomic,copy)NSString *apiKey;
@end

@protocol WeXWalletEthplorerGetRecordResponseModal_item;
@interface  WeXWalletEthplorerGetRecordResponseModal_item : WeXBaseNetModal

@property (nonatomic,copy)NSString *transactionHash;
@property (nonatomic,copy)NSString *to;
@property (nonatomic,copy)NSString *from;
@property (nonatomic,copy)NSString *timestamp;
@property (nonatomic,copy)NSString *value;
@property (nonatomic,copy)NSString *isError;
@property (nonatomic,copy)NSString *gasPrice;
@property (nonatomic,copy)NSString *gasUsed;
@property (nonatomic,copy)NSString *blockNumber;
@end

@interface  WeXWalletEthplorerGetRecordResponseModal : WeXBaseNetModal

@property (nonatomic,strong)NSArray<WeXWalletEthplorerGetRecordResponseModal_item > *operations;

@end

