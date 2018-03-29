//
//  WexBaseNetAdapterModal.m
//  WeXFin
//
//  Created by Mark on 15/7/28.
//  Copyright (c) 2015年 SinaPay. All rights reserved.
//

#import "WexBaseNetAdapterModal.h"

@implementation WexBaseNetAdapterResponseHeadModal

//- (WexBaseNetAdapterResponseStatus)status
//{
//    if (_code == nil)
//    {
//        return WexBaseNetAdapterResponseStatusUnknown;
//    }
//    
//    if ([_code isEqualToString:@"200"])
//    {
//        return WexBaseNetAdapterResponseStatusSuccess;
//    }
//    
//    if ([_code isEqualToString:@"401"])
//    {
//        return WexBaseNetAdapterResponseStatusSessionTimeOut;
//    }
//    
//    if ([_code isEqualToString:@"412"])
//    {
//        return WexBaseNetAdapterResponseStatusChangeDevice;
//    }
//    
//    return WexBaseNetAdapterResponseStatusFail;
//}

@end

@implementation WexBaseNetAdapterWordPressGETResponseHeadModal

+ (JSONKeyMapper *)keyMapper
{
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:
            @{
              @"totalNumber": @"X-WP-Total",
              @"totalPages": @"X-WP-TotalPages",
              }];
}

@end

