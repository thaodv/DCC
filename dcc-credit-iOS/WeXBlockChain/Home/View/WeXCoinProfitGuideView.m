//
//  WeXCoinProfitGuideView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/13.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCoinProfitGuideView.h"

@interface WeXCoinProfitGuideView ()

@property (nonatomic, weak) UIView *backView;
@property (nonatomic, weak) UIView *circleView;
@property (nonatomic, weak) UIImageView *handImageView;
@property (nonatomic, weak) UIImageView *coinProfitImage;
@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) UILabel *coinTitleLab;
@property (nonatomic, weak) UILabel *coinSubTextLab;
@property (nonatomic, weak) WeXCustomButton *seeButton;
@property (nonatomic, weak) WeXCustomButton *okButton;
@property (nonatomic,copy) void (^ClickEvent)(WeXCoinProfitGuideViewType);

@end

@implementation WeXCoinProfitGuideView

+ (instancetype)createCoinProfitGuideView:(CGRect)frame
                              circleFrame:(CGRect)circleFrame
                                clickType:(void(^)(WeXCoinProfitGuideViewType))click {
    WeXCoinProfitGuideView *guideView = [[WeXCoinProfitGuideView alloc] initWithFrame:frame];
    [guideView setCircleViewFrame:circleFrame];
    guideView.titleLab.text = @"币生息";
    guideView.coinSubTextLab.text = @"—— 极低风险的DCC增值理财产品~";
    guideView.ClickEvent = click;
    return guideView;
}
- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self wex_addsubviews];
        [self wex_addConstraints];
    }
    return self;
}
- (void)wex_addsubviews {
    UIView *backView = [UIView new];
    [backView setBackgroundColor:ColorWithRGBA(0, 0, 0, 0.4)];
    [self addSubview:backView];
    self.backView = backView;
    
    UILabel *titleLab = [UILabel new];
    titleLab.textColor = [UIColor whiteColor];
    titleLab.font = [UIFont systemFontOfSize:15.0];
    titleLab.textAlignment = NSTextAlignmentLeft;
    [self.backView addSubview:titleLab];
    self.titleLab = titleLab;
    
    UILabel *subTextLab = [UILabel new];
    subTextLab.textColor = [UIColor whiteColor];
    subTextLab.font = [UIFont systemFontOfSize:17.0];
    subTextLab.textAlignment = NSTextAlignmentLeft;
    [self.backView addSubview:subTextLab];
    self.coinSubTextLab = subTextLab;
    
    UIView *circleView = [UIView new];
    [circleView setBackgroundColor:[UIColor whiteColor]];
    [self addSubview:circleView];
    self.circleView = circleView;
    
    UIImageView *handImageView = [UIImageView new];
    handImageView.image = [UIImage imageNamed:@"Wex_Coin_Hand"];
    [self.backView addSubview:handImageView];
    self.handImageView = handImageView;
    
    UIImageView *coinProfitImage = [UIImageView new];
    coinProfitImage.image = [UIImage imageNamed:@"Wex_Coin_Profit"];
    [self.circleView addSubview:coinProfitImage];
    self.coinProfitImage = coinProfitImage;
    
    UILabel *coinTitleLab = [UILabel new];
    [coinTitleLab setText:@"币生息"];
    coinTitleLab.textAlignment = NSTextAlignmentCenter;
    coinTitleLab.font = [UIFont systemFontOfSize:11.0];
    coinTitleLab.textColor = ColorWithHex(0x4A4A4A);
    [self.circleView addSubview:coinTitleLab];
    self.coinTitleLab = coinTitleLab;
    
    
    WeXCustomButton *seeButton = [WeXCustomButton button];
    [seeButton setTitle:@"去看看" forState:UIControlStateNormal];
    [seeButton addTarget:self action:@selector(seeButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.backView addSubview:seeButton];
    self.seeButton = seeButton;
    
    WeXCustomButton *okButton = [WeXCustomButton button];
    [okButton setTitle:@"知道了" forState:UIControlStateNormal];
    [okButton addTarget:self action:@selector(okButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.backView addSubview:okButton];
    self.okButton = okButton;
}

- (void)seeButtonEvent:(WeXCustomButton *)button {
    [self dismiss];
    !self.ClickEvent ? : self.ClickEvent(WeXCoinProfitGuideViewTypeSee);
}
- (void)okButtonEvent:(WeXCustomButton *)button {
    [self dismiss];
    !self.ClickEvent ? : self.ClickEvent(WeXCoinProfitGuideViewTypeOk);
}


- (void)wex_addConstraints {
    [self.backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.right.left.mas_equalTo(0);
    }];
}
- (void)setCircleViewFrame:(CGRect)frame {
    [self.backView   layoutIfNeeded];
    [self.circleView setFrame:frame];
    self.circleView.layer.cornerRadius = frame.size.width / 2.0;
    self.circleView.clipsToBounds = YES;
    [self.circleView layoutIfNeeded];
    [self.coinProfitImage mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(5);
        make.centerX.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(36, 36));
    }];
    [self.coinTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.coinProfitImage.mas_bottom).offset(6);
        make.centerX.mas_equalTo(0);
    }];
    
    [self.handImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.circleView.mas_bottom).offset(15);
        make.right.equalTo(self.circleView.mas_left).offset(10);
        make.size.mas_equalTo(CGSizeMake(37, 36));
    }];
    
    [self.coinSubTextLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.handImageView.mas_bottom).offset(40);
        make.centerX.mas_equalTo(0);
    }];
    
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.coinSubTextLab.mas_left);
        make.bottom.equalTo(self.coinSubTextLab.mas_top).offset(-10);
    }];
    
    [self.seeButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.coinSubTextLab.mas_left);
        make.right.equalTo(self.coinSubTextLab.mas_centerX).offset(-5);
        make.top.equalTo(self.coinSubTextLab.mas_bottom).offset(20);
        make.height.mas_equalTo(40);
    }];
    
    [self.okButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.coinSubTextLab.mas_centerX).offset(5);
        make.right.equalTo(self.coinSubTextLab.mas_right);
        make.top.equalTo(self.seeButton.mas_top);
        make.height.mas_equalTo(40);
    }];
}
- (void)dismiss {
    [self removeFromSuperview];
}

@end
