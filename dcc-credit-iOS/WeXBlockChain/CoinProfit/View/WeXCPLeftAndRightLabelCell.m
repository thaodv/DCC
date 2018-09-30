//
//  WeXCPLeftAndRightLabelCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPLeftAndRightLabelCell.h"

@interface WeXCPLeftAndRightLabelCell ()
@property (nonatomic, weak) UILabel *leftLab;
@property (nonatomic, weak) UILabel *rightLab;
@end

@implementation WeXCPLeftAndRightLabelCell

- (void)wex_addSubViews {
    [super wex_addSubViews];
    UILabel *leftLab = CreateLeftAlignmentLabel(WexFont(16), WexDefault4ATitleColor);
    [self.contentView addSubview:leftLab];
    self.leftLab = leftLab;
    
    UITextField *textFiled = [[UITextField alloc] init];
    textFiled.keyboardType = UIKeyboardTypeNumberPad;
    [self.contentView addSubview:textFiled];
    self.textField = textFiled;
    
    UILabel *rightLab = CreateRightAlignmentLabel(WexFont(16), WexDefault4ATitleColor);
    rightLab.adjustsFontSizeToFitWidth = true;
    [self.contentView addSubview:rightLab];
    self.rightLab = rightLab;

}

- (void)wex_addConstraints {
    [super wex_addConstraints];
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(55).priorityHigh();
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
    }];
    [self.leftLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.centerY.mas_equalTo(0);
    }];
    
    [self.rightLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.right.mas_equalTo(-10);
        make.width.mas_equalTo(40);
    }];
    
    [self.textField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.leftLab.mas_right).offset(10);
        make.right.equalTo(self.rightLab.mas_left).offset(-10);
        make.height.mas_equalTo(30);
        make.centerY.mas_equalTo(0);
    }];

}
- (void)setLeftTitle:(NSString *)leftTitle
          rightTitle:(NSString *)rightTitle {
    [self.leftLab setText:leftTitle];
    [self.rightLab setText:rightTitle];
}


- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
