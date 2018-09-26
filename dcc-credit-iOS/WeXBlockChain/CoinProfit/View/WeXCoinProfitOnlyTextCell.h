//
//  WeXCoinProfitOnlyTextCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

typedef NS_ENUM(NSInteger,WeXCoinProfitOnlyTextCellType) {
    WeXCoinProfitOnlyTextCellTypeOnlyTitle = 0, //只有标题
    WeXCoinProfitOnlyTextCellTypeDefaultSubtextColor = 1, //黑色副标题
    WeXCoinProfitOnlyTextCellTypeRedSubTextColor = 2, //红色副标题
    WeXCoinProfitOnlyTextCellTypeOnlyCenterTitle = 3, //标题居中
    WeXCoinProfitOnlyTextCellTypeOnlyBoldTitle = 4, //只有加粗标题
};

@interface WeXCoinProfitOnlyTextCell : WeXBaseTableViewCell
- (void)setTitle:(NSString *)title
         subText:(NSString *)subText
        cellType:(WeXCoinProfitOnlyTextCellType)type;

- (void)setTitle:(NSString *)title
        highText:(NSString *)highText;




@end

@class WeXCPSaleInfoResModel;

@interface WeXCoinProfitAutoHeightTextCell : WeXBaseTableViewCell

- (void)setTextContent:(NSString *)text;

- (void)setSaleInfoResModel:(WeXCPSaleInfoResModel *)resModel;
@end
