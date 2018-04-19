//
//  WeXWalletEtherscanGetRecordModal.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXWalletEtherscanGetRecordParamModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *module;
@property (nonatomic,copy)NSString *action;
@property (nonatomic,copy)NSString *address;
@property (nonatomic,copy)NSString *tag;
@property (nonatomic,copy)NSString *sort;
@end

@protocol WeXWalletEtherscanGetRecordResponseModal_item;
@interface  WeXWalletEtherscanGetRecordResponseModal_item : WeXBaseNetModal

@property (nonatomic,copy)NSString *hashStr;
@property (nonatomic,copy)NSString *to;
@property (nonatomic,copy)NSString *from;
@property (nonatomic,copy)NSString *timeStamp;
@property (nonatomic,copy)NSString *value;
@property (nonatomic,copy)NSString *isError;
@property (nonatomic,copy)NSString *gasPrice;
@property (nonatomic,copy)NSString *gasUsed;
@property (nonatomic,copy)NSString *blockNumber;
@end

@interface  WeXWalletEtherscanGetRecordResponseModal : WeXBaseNetModal

@property (nonatomic,strong)NSMutableArray<WeXWalletEtherscanGetRecordResponseModal_item > *result;

@end
