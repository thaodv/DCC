//
//  WeXP2PImageCardCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/6/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WeXBaseTableViewCell.h"

@interface WeXP2PImageCardCell : WeXBaseTableViewCell

@property (nonatomic,copy) void (^DidClickP2PLoanCard) (void);

- (void)setCardBackgroundImage:(NSString *)imageName;

+ (CGFloat)p2pImageCellHeight;

@end
