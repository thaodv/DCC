//
//  WeXAuthenGetTicketAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAuthenGetTicketAdapter.h"

@implementation WeXAuthenGetTicketAdapter

- (NSString*)getRequestUrl
{
    return @"ticket/1/getTicket";
}

- (WexBaseUrlType)getBasetUrlType
{
    return WexBaseUrlTypeAuthen;
}


- (Class)getResponseClass
{
    return [WeXGetTicketResponseModal class];
}

@end
