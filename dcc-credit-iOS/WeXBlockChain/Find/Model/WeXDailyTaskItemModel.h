//
//  WeXDailyTaskItemModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"

typedef NS_ENUM  (NSInteger,WeXDailyTaskItemCellType) {
    WeXDailyTaskItemCellDefault   = 0, //紫色副标题 无箭头
    WeXDailyTaskItemCellWithArrow = 1, //紫色副标题 有箭头
    WeXDailyTaskItemCellWithGrren = 2, //绿色副标题 无箭头
};


@interface WeXDailyTaskItemModel : WeXBaseNetModal

@property (nonatomic, copy) NSString *imageName;
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *subTitle;
@property (nonatomic, assign) WeXDailyTaskItemCellType type;

@end

@interface WeXDailyTaskSectionItemModel : WeXBaseNetModal

@property (nonatomic, strong) NSArray <WeXDailyTaskItemModel *>  * rows;
@property (nonatomic, copy) NSString *sectionTitle;
@property (nonatomic, copy) NSString *sectionSubTitle;



@end
