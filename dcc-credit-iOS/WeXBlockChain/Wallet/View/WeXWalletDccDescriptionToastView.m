//
//  WeXWalletDccDescriptionToastView.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/6/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDccDescriptionToastView.h"

@implementation WeXWalletDccDescriptionToastView

-(instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        [self setupSubViews];
    }
    return self;
    
}

- (void)commonInit{
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick)];
    [self addGestureRecognizer:tap];
    
}

- (void)tapClick{
    [self endEditing:YES];
    [self dismiss];
}

- (void)setupSubViews{
    
    UIView *backView = [[UIView alloc] init];
    backView.backgroundColor = COLOR_ALPHA_VIEW_COVER;
    [self addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.top.equalTo(self);
        make.bottom.equalTo(self);
    }];

    
    UIImageView *firstImageView = [[UIImageView alloc] init];
    UIImage *image1 = [UIImage imageNamed:WeXLocalizedString(@"dcc_toast_des1")];
    firstImageView.image = image1;
    firstImageView.frame = CGRectMake(0, 0, image1.size.width, image1.size.height);
    [self addSubview:firstImageView];
    self.firstImageView = firstImageView;
    
    UIImageView *secondImageView = [[UIImageView alloc] init];
    UIImage *image2 = [UIImage imageNamed:WeXLocalizedString(@"dcc_toast_des2")];
    secondImageView.image = image2;
    secondImageView.frame = CGRectMake(0, 0, image2.size.width, image2.size.height);
    [self addSubview:secondImageView];
    self.secondImageView = secondImageView;
    
    UIImageView *thirdImageView = [[UIImageView alloc] init];
    UIImage *image3 = [UIImage imageNamed:WeXLocalizedString(@"dcc_toast_des3")];
    thirdImageView.image = image3;
    thirdImageView.frame = CGRectMake(0, 0, image3.size.width, image3.size.height);
    [self addSubview:thirdImageView];
    self.thirdImageView = thirdImageView;
    
    
    WeXCustomButton *confirmBtn = [WeXCustomButton button];
    [confirmBtn setTitle:WeXLocalizedString(@"我知道了") forState:UIControlStateNormal];
    confirmBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [confirmBtn addTarget:self action:@selector(confirmBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:confirmBtn];
    [confirmBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.mas_bottom).offset(-15);
        make.centerX.equalTo(self);
        make.height.equalTo(@45);
        make.width.equalTo(@150);
    }];
    
    
    if (IS_IPHONE_5) return;
    
    YYLabel *desLabel = [YYLabel new];
    desLabel.layer.cornerRadius = 12;
    desLabel.layer.masksToBounds = YES;
    desLabel.backgroundColor = [UIColor whiteColor];
    desLabel.textAlignment = NSTextAlignmentLeft;
    desLabel.numberOfLines = 0;
    desLabel.preferredMaxLayoutWidth = kScreenWidth-30;
    desLabel.textContainerInset = UIEdgeInsetsMake(10, 10, 10, 10);
    [self addSubview:desLabel];
    
    NSMutableAttributedString *text = [[NSMutableAttributedString alloc] initWithString:WeXLocalizedString(@"WalletDccDescriptionToastBottomText")];
    text.yy_font = [UIFont systemFontOfSize:18];
    text.yy_color = COLOR_LABEL_DESCRIPTION;
    text.yy_lineSpacing = 5;
    desLabel.attributedText = text;
    
    [desLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(confirmBtn.mas_top).offset(-10);
        make.leading.equalTo(self).offset(15);
        make.trailing.equalTo(self).offset(-15);
    }];
    
    
}

- (void)confirmBtnClick
{
    [self dismiss];
}

- (void)dismiss
{
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    
    [self removeFromSuperview];
}

-(void)dealloc
{
    NSLog(@"%s",__func__);
}

@end
