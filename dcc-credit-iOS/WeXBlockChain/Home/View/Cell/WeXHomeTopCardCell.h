//
//  WeXHomeTopCardCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@interface WeXHomeTopCardCell : WeXBaseTableViewCell

- (void)setTitleText:(NSString *)titleText
         addressText:(NSString *)addressText;

+ (CGFloat)cellHeight;

@end
