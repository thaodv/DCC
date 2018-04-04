//
//  WeXGetTicketAdapter.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/15.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXGetTicketAdapter.h"

@implementation WeXGetTicketAdapter

- (NSString*)getRequestUrl
{
    return @"getTicket";
}

- (Class)getResponseClass
{
    return [WeXGetTicketResponseModal class];
}

@end
