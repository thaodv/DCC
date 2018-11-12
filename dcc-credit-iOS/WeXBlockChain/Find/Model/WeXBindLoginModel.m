//
//  WeXBindLoginModel.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBindLoginModel.h"

@implementation WeXBindLoginModel

@end


@implementation WeXBindLoginParmaModel

@end


@implementation WeXBindLoginResModel

//+(JSONKeyMapper*)keyMapper
//{
//    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{
//                                                                  @"idNum": @"id",
//                                                                  }];
//}

@end




@implementation WeXBindLoginResIdentityListItemModel
+(JSONKeyMapper*)keyMapper
{
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{
                                                                  @"idNum": @"id",
                                                                  }];
}

@end


@implementation WeXBindLoginResMemberModel
+(JSONKeyMapper*)keyMapper
{
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{
                                                                  @"idNum": @"id",
                                                                  }];
}

@end


@implementation WeXBindLoginResPyaerModel
+(JSONKeyMapper*)keyMapper
{
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{
                                                                  @"idNum": @"id",
                                                                  }];
}


@end
