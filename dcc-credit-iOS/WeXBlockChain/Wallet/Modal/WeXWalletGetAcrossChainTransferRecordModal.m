//
//  WeXWalletGetAcrossChainTransferRecordModal.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/25.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletGetAcrossChainTransferRecordModal.h"

@implementation WeXWalletGetAcrossChainTransferRecordParamModal

@end
@implementation WeXWalletGetAcrossChainTransferRecordResponseModal_item

+ (JSONKeyMapper *)keyMapper
{
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{@"recordId":@"id"}];
}

@end
@implementation WeXWalletGetAcrossChainTransferRecordResponseModal

@end
