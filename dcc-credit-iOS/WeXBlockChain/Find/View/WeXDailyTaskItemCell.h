//
//  WeXDailyTaskItemCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WeXDailyTaskItemModel.h"
#import "WeXBaseTableViewCell.h"
#import "WeXGradenTaskManager.h"


@class WeXTaskListItemModel;
@class WeXTaskListItemListModel;

@interface WeXDailyTaskItemCell : WeXBaseTableViewCell

- (void)setItemModel:(WeXTaskListItemModel *)itemModel
            cellType:(WeXDailyTaskItemCellType)type;

- (void)setItemSectionModel:(WeXTaskListItemListModel*)sectionModel
                   rowModel:(WeXTaskListItemModel *)itemModel;



@end

