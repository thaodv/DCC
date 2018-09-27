//
//  WeXLoginManagerTopCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@interface WeXLoginManagerTopCell : WeXBaseTableViewCell

@property (nonatomic,copy) void (^DidClickScan)(void);


// 0 不可用, 1可用
- (void)setStatus:(BOOL)status;

+ (CGFloat)cellHeight;


@end
