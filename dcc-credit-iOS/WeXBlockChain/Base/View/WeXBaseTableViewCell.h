//
//  WeXBaseTableViewCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/6/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WeXBaseTableViewCell : UITableViewCell

@property (nonatomic, assign) BOOL bottomLineHidden;
@property (nonatomic, weak) UIImageView *bottomLine;


- (void)wex_addSubViews;

- (void)wex_addConstraints;

- (void)setTest;

- (void)setBottomLineLeft:(CGFloat)left
                    right:(CGFloat)right;


@end
