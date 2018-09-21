//
//  WeXHomeIndicatorCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@interface WeXHomeIndicatorCell : WeXBaseTableViewCell

@property (nonatomic,copy) void (^DidClickRight)(void);


- (void)setLeftTitle:(NSString *)leftTitle
          rightTitle:(NSString *)rightTitle;

+ (CGFloat)cellHeight;

@end
