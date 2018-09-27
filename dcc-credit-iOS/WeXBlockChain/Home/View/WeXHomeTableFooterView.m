//
//  WeXHomeTableFooterView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXHomeTableFooterView.h"

@interface WeXHomeTableFooterView ()
@property (nonatomic, weak) UIView *leftSepratorLine;
@property (nonatomic, weak) UIView *rightSepratorLine;
@property (nonatomic, weak) UILabel *titleLab;

@end

@implementation WeXHomeTableFooterView

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self wex_addSubviews];
        [self wex_addConstraint];
    }
    return self;
}
- (void)wex_addSubviews {
    UIView *leftView = [UIView new];
    [leftView setBackgroundColor:ColorWithHex(0xBAC0C5)];
    [self addSubview:leftView];
    self.leftSepratorLine = leftView;
    
    UILabel *titleLab = CreateCenterAlignmentLabel(WexFont(12.0), ColorWithHex(0xBAC0C5));
    [self addSubview:titleLab];
    self.titleLab = titleLab;
    
    UIView *rightView = [UIView new];
    [rightView setBackgroundColor:ColorWithHex(0xBAC0C5)];
    [self addSubview:rightView];
    self.rightSepratorLine = rightView;
    
}

- (void)wex_addConstraint {
    self.titleLab.mas_key = @"TitleLab";
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(0);
        make.centerY.mas_equalTo(0);
    }];
    
    self.titleLab.mas_key = @"leftSepratorLine";
    [self.leftSepratorLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.right.equalTo(self.titleLab.mas_left).offset(-2);
        make.size.mas_equalTo(CGSizeMake(23, 3));
    }];
    
    self.titleLab.mas_key = @"rightSepratorLine";
    [self.rightSepratorLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.left.equalTo(self.titleLab.mas_right).offset(2);
        make.size.mas_equalTo(CGSizeMake(23, 3));
    }];
}

- (void)setTitle:(NSString *)title {
    [self.titleLab setText:title];
}
- (void)setLineHide {
    [self.leftSepratorLine  setHidden:true];
    [self.rightSepratorLine setHidden:true];
}



@end
