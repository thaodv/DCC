//
//  WeXLoanRecentOrderView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanRecentOrderView.h"

#define kTitleColor    ColorWithHex(0xD3A9FF)
#define kSubTitleColor [UIColor whiteColor]

@interface WeXLoanRecentOrderView ()
@property (nonatomic, weak) UILabel *leftTitleLab;
@property (nonatomic, weak) UILabel *leftLab;
@property (nonatomic, weak) UILabel *rightTitleLab;
@property (nonatomic, weak) UILabel *rightLab;

@end

@implementation WeXLoanRecentOrderView

- (void)wex_loadViews {
    UILabel *leftTitleLab = CreateLeftAlignmentLabel(WexFont(16.0), kTitleColor);
    [self addSubview:leftTitleLab];
    leftTitleLab.adjustsFontSizeToFitWidth = true;
    self.leftTitleLab = leftTitleLab;
    
    UILabel *leftLab = CreateLeftAlignmentLabel(WexFont(16.0), kSubTitleColor);
    [self addSubview:leftLab];
    leftLab.adjustsFontSizeToFitWidth = true;
    self.leftLab = leftLab;
    
    UILabel *rightTitleLab = CreateLeftAlignmentLabel(WexFont(16.0), kTitleColor);
    [self addSubview:rightTitleLab];
    rightTitleLab.adjustsFontSizeToFitWidth = true;
    self.rightTitleLab = rightTitleLab;
    
    UILabel *rightLab = CreateLeftAlignmentLabel(WexFont(16.0), kSubTitleColor);
    [self addSubview:rightLab];
    rightLab.adjustsFontSizeToFitWidth = true;
    self.rightLab = rightLab;
    
}
- (void)wex_layoutConstraints {
    [self.leftTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.right.equalTo(self.mas_centerX);
        make.top.mas_equalTo(0);
    }];
    
    [self.leftLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.equalTo(self.leftTitleLab.mas_bottom).offset(5);
    }];
    
    [self.rightTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.mas_centerX).offset(14);
        make.right.mas_equalTo(0);
        make.top.mas_equalTo(0);
    }];
    
    [self.rightLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.rightTitleLab.mas_bottom).offset(5);
        make.left.equalTo(self.mas_centerX).offset(14);
        make.right.mas_equalTo(0);
    }];
}


- (void)setLeftTitle:(NSString *)leftTitle
        leftSubTitle:(NSString *)leftSubTitle
          rightTitle:(NSString *)rightTitle
       rightSubTitle:(NSString *)rightSubTitle {
    [self.leftTitleLab setText:leftTitle];
    [self.leftLab setText:leftSubTitle];
    [self.rightTitleLab setText:rightTitle];
    [self.rightLab setText:rightSubTitle];
}

@end
