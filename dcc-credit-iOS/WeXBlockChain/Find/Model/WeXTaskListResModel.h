//
//  WeXTaskListResModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/6.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

@interface WeXTaskListItemModel : WeXBaseNetModal
@property (nonatomic, copy) NSString *code;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *img;
@property (nonatomic, copy) NSString *type;
@property (nonatomic, copy) NSString *bonus;
@property (nonatomic, copy) NSString *link;
@property (nonatomic, copy) NSString *status;
@property (nonatomic, copy) NSString *statusDescription;

@end

@protocol WeXTaskListItemModel;
@interface WeXTaskListItemListModel : WeXBaseNetModal

@property (nonatomic, copy) NSString *category;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *descripe;
@property (nonatomic, strong) NSArray  <WeXTaskListItemModel *> <WeXTaskListItemModel> *taskList;

@end

@protocol WeXTaskListItemListModel;

@interface WeXTaskListResModel : WeXBaseNetModal

@property (nonatomic, strong) NSArray < WeXTaskListItemListModel * > <WeXTaskListItemListModel> *result;


@end


