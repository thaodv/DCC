//
//  WeXWalletEtherscanGetRecordModal.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletEtherscanGetRecordModal.h"


@implementation WeXWalletEtherscanGetRecordParamModal

@end

@implementation WeXWalletEtherscanGetRecordResponseModal_item

+ (JSONKeyMapper *)keyMapper
{
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:
            @{
              @"hashStr": @"hash",
              }];
}

@end

@implementation WeXWalletEtherscanGetRecordResponseModal

@end
