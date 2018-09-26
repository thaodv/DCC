//
//  WeXCPLeftAndRightLabelCell.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTableViewCell.h"

@interface WeXCPLeftAndRightLabelCell : WeXBaseTableViewCell

@property (nonatomic, weak) UITextField *textField;

- (void)setLeftTitle:(NSString *)leftTitle
          rightTitle:(NSString *)rightTitle;

@end
