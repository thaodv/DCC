//
//  WeXUploadPubKeyAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/17.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXUploadPubKeyAdapter.h"

@implementation WeXUploadPubKeyAdapter

- (NSString*)getRequestUrl
{
    return @"uploadPubKey";
}

- (Class)getResponseClass
{
    return [WeXUploadPubKeyResponseModal class];
}

@end
