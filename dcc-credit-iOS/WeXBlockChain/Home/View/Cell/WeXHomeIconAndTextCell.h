//
//  WeXHomeIconAndTextCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@class WeXLoanGetOrderDetailResponseModal;

@interface WeXHomeIconAndTextCell : WeXBaseTableViewCell
@property (nonatomic,copy) void (^DidClickEvent)(void);


- (void)setLeftIconName:(NSString *)leftIcon
                  title:(NSString *)title
               subTitle:(NSString *)subTitle
            actionTitle:(NSString *)actionTitle;

- (void)setLeftIconName:(NSString *)leftIcon
                  title:(NSString *)title
               highText:(NSString *)highText
               subTitle:(NSString *)subTitle
            actionTitle:(NSString *)actionTitle;

- (void)setRepayCoinDataModel:(WeXLoanGetOrderDetailResponseModal *)model
                      iconURL:(NSString *)iconURL;

+ (CGFloat)cellHeight;

@end
