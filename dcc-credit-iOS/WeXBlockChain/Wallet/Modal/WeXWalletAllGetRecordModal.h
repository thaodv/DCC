//
//  WeXWalletAllGetRecordModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/3/17.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXWalletAllGetRecordParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *contractAddress;
@property (nonatomic,copy)NSString *address;
@property (nonatomic,copy)NSString *page;
@property (nonatomic,copy)NSString *pageSize;
@end

@protocol WeXWalletAllGetRecordResponseModal_item;
@interface  WeXWalletAllGetRecordResponseModal_item : WeXBaseNetModal

@property (nonatomic,copy)NSString *transactionHash;
@property (nonatomic,copy)NSString *toAddress;
@property (nonatomic,copy)NSString *fromAddress;
@property (nonatomic,copy)NSString *blockTimestamp;
@property (nonatomic,copy)NSString *value;
@property (nonatomic,copy)NSString *isError;
@property (nonatomic,copy)NSString *gasPrice;
@property (nonatomic,copy)NSString *gasUsed;
@property (nonatomic,copy)NSString *blockNumber;
@end

@interface  WeXWalletAllGetRecordResponseModal : WeXBaseNetModal

@property (nonatomic,strong)NSMutableArray<WeXWalletAllGetRecordResponseModal_item > *items;

//@property (nonatomic,copy)NSString *result;

@end
