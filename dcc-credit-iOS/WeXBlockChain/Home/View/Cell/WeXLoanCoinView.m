//
//  WeXLoanCoinView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanCoinView.h"

@interface WeXLoanCoinView ()

@property (nonatomic, weak) UIImageView *leftIcon;
@property (nonatomic, weak) UILabel *leftTitleLab;
@property (nonatomic, weak) UILabel *leftSubtitleLab;

@end

@implementation WeXLoanCoinView

- (void)wex_loadViews {
    
    UIImageView *leftIcon = [UIImageView new];
    leftIcon.layer.cornerRadius = 6;
    leftIcon.clipsToBounds = true;
    [self addSubview:leftIcon];
    self.leftIcon = leftIcon;
    
    UILabel *leftTitleLab = CreateLeftAlignmentLabel(WexFont(13), ColorWithHex(0xBAC0C5));
    [self addSubview:leftTitleLab];
    self.leftTitleLab = leftTitleLab;
    
    UILabel *leftSubtitleLab = CreateLeftAlignmentLabel(WexFont(13), ColorWithHex(0xBAC0C5));
    [self addSubview:leftSubtitleLab];
    self.leftSubtitleLab = leftSubtitleLab;
}
- (void)wex_layoutConstraints {
    [self.leftIcon mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.mas_equalTo(10);
        make.size.mas_equalTo(CGSizeMake(43, 43));
    }];
    
    [self.leftTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.leftIcon);
        make.left.equalTo(self.leftIcon.mas_right).offset(13);
        make.right.mas_equalTo(-5);
    }];
    
    [self.leftSubtitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.leftTitleLab.mas_bottom).offset(10);
        make.left.equalTo(self.leftTitleLab);
        make.right.mas_equalTo(-5);
    }];
}

- (void)setIconURL:(NSString *)URL
            amount:(NSString *)amount
            period:(NSString *)period {
    [self.leftIcon sd_setImageWithURL:[NSURL URLWithString:URL] placeholderImage:[UIImage imageNamed:@"ethereum"]];
    [self.leftTitleLab    setText:amount];
    [self.leftSubtitleLab setText:period];
}


@end
