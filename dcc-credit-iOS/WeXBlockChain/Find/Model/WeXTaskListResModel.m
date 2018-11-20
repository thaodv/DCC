//
//  WeXTaskListResModel.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/6.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXTaskListResModel.h"

@implementation WeXTaskListItemModel

@end


@implementation WeXTaskListItemListModel

+ (JSONKeyMapper *)keyMapper {
    return [[JSONKeyMapper alloc] initWithModelToJSONDictionary:@{@"descripe":@"description"}];
}

@end


@implementation WeXTaskListResModel

@end
