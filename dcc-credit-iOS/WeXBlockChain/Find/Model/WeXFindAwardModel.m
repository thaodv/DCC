//
//  WeXFindAwardModel.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXFindAwardModel.h"

@implementation WeXFindAwardModel

@end

@implementation WeXFindAwardParamModel

@end

@implementation WeXFindAwardResModel

@end

@implementation WeXFindAwardResDetailModel
+ (JSONKeyMapper *)keyMapper {
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{@"idNum":@"id"}];
}


@end
