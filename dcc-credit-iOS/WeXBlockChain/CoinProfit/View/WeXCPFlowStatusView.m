//
//  WeXCPFlowStatusView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#define kWexHighBackColor       ColorWithHex(0x9013FE)
#define kWexHighLayerColor      ColorWithHex(0xDCB6FC)

#define kWexNormalBackColor       ColorWithHex(0xCECDD2)
#define kWexNormalLayerColor      ColorWithHex(0xF1E4FC)
#define kWexSepratorLineColor     ColorWithHex(0xEFEFEF)



#import "WeXCPFlowStatusView.h"


@interface WeXCPFlowStatusView ()

@property (nonatomic, weak) UIView *buyDotView;
@property (nonatomic, weak) UIView *leftSepratorLine;
@property (nonatomic, weak) UIView *startDotView;
@property (nonatomic, weak) UIView *rightSepratorLine;
@property (nonatomic, weak) UIView *endDotView;
@property (nonatomic, weak) UILabel *buyTitleLab;
@property (nonatomic, weak) UILabel *buyDayLab;
@property (nonatomic, weak) UILabel *startTitleLab;
@property (nonatomic, weak) UILabel *startDayLab;
@property (nonatomic, weak) UILabel *endTitleLab;
@property (nonatomic, weak) UILabel *endDayLab;

@end

@implementation WeXCPFlowStatusView

- (void)wex_loadViews {
    UIView *buyDotView = [UIView new];
    [buyDotView setBackgroundColor:kWexHighBackColor];
    buyDotView.layer.cornerRadius = 5.5;
    buyDotView.layer.borderWidth = 3.0;
    buyDotView.layer.backgroundColor = kWexHighLayerColor.CGColor;
    buyDotView.clipsToBounds = YES;
    [self addSubview:buyDotView];
    self.buyDotView = buyDotView;
    
    UIView *leftSepratorLine = [UIView new];
    [leftSepratorLine setBackgroundColor:kWexSepratorLineColor];
    [self addSubview:leftSepratorLine];
    self.leftSepratorLine = leftSepratorLine;
    
    UILabel *buyDayLab = CreateCenterAlignmentLabel(WexFont(13), WexDefault4ATitleColor);
    [self addSubview:buyDayLab];
    self.buyTitleLab = buyDayLab;
    
    UILabel *buyTimeLab = CreateCenterAlignmentLabel(WexFont(10), ColorWithHex(0x000000));
    [self addSubview:buyTimeLab];
    self.buyDayLab =buyTimeLab;
    
    UIView *startDotView = [UIView new];
    [startDotView setBackgroundColor:kWexNormalBackColor];
    startDotView.layer.cornerRadius = 5.5;
    startDotView.layer.borderWidth = 3.0;
    buyDotView.layer.backgroundColor = kWexNormalLayerColor.CGColor;
    startDotView.clipsToBounds = YES;
    [self addSubview:startDotView];
    self.startDotView = startDotView;
    
    UIView *rightSepratorLine = [UIView new];
    [rightSepratorLine setBackgroundColor:kWexSepratorLineColor];
    [self addSubview:rightSepratorLine];
    self.rightSepratorLine = rightSepratorLine;
    
    UILabel *startTitleLab = CreateCenterAlignmentLabel(WexFont(13), WexDefault4ATitleColor);
    [self addSubview:startTitleLab];
    self.startTitleLab = startTitleLab;
    
    UILabel *startDayLab = CreateCenterAlignmentLabel(WexFont(10), ColorWithHex(0x000000));
    [self addSubview:startDayLab];
    self.startDayLab =startDayLab;
    
    UIView *endDotView = [UIView new];
    [endDotView setBackgroundColor:kWexNormalBackColor];
    endDotView.layer.cornerRadius = 5.5;
    endDotView.layer.borderWidth  = 3.0;
    endDotView.layer.backgroundColor = kWexNormalLayerColor.CGColor;
    endDotView.clipsToBounds = YES;
    [self addSubview:endDotView];
    self.endDotView = endDotView;
    
    UILabel *endTitleLab = CreateCenterAlignmentLabel(WexFont(13), WexDefault4ATitleColor);
    [self addSubview:endTitleLab];
    self.endTitleLab = endTitleLab;
    
    UILabel *endDayLab = CreateCenterAlignmentLabel(WexFont(10), ColorWithHex(0x000000));
    [self addSubview:endDayLab];
    self.endDayLab =endDayLab;
    
}

- (void)wex_layoutConstraints {
    [self.startDotView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(20);
        make.centerX.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(11, 11));
    }];
    
    [self.startTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.startDotView.mas_bottom).offset(15);
        make.centerX.equalTo(self.startDotView);
    }];
    
    [self.startDayLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.startTitleLab.mas_bottom).offset(15);
        make.centerX.equalTo(self.startDotView);
    }];
    
    
    [self.buyDotView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(20);
        make.right.equalTo(self.startDotView.mas_left).offset(-100);
        make.size.mas_equalTo(CGSizeMake(11, 11));
    }];
    
    [self.buyTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.buyDotView.mas_bottom).offset(15);
        make.centerX.equalTo(self.buyDotView);
    }];
    
    [self.buyDayLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.buyTitleLab.mas_bottom).offset(15);
        make.centerX.equalTo(self.buyDotView);
    }];
    [self.leftSepratorLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.buyDotView.mas_right);
        make.centerY.equalTo(self.buyDotView);
        make.right.equalTo(self.startDotView.mas_left);
        make.height.mas_equalTo(2);
    }];
    
    [self.endDotView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(20);
        make.left.equalTo(self.startDotView.mas_left).offset(100);
        make.size.mas_equalTo(CGSizeMake(11, 11));
    }];
    
    [self.endTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.endDotView.mas_bottom).offset(15);
        make.centerX.equalTo(self.endDotView);
    }];
    
    [self.endDayLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.endTitleLab.mas_bottom).offset(15);
        make.centerX.equalTo(self.endDotView);
    }];
    [self.rightSepratorLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.startDotView.mas_right);
        make.centerY.equalTo(self.startDotView);
        make.right.equalTo(self.endDotView.mas_left);
        make.height.mas_equalTo(2);
    }];
}
- (void)setBuyTitle:(NSString *)buyTitle
             buyDay:(NSString *)buyDay
         startTitle:(NSString *)startTitle
           startDay:(NSString *)startDay
           endTitle:(NSString *)endTitle
             endDay:(NSString *)endDay {
    [self.buyTitleLab setText:buyTitle];
    [self.buyDayLab setText:buyDay];
    
    [self.startTitleLab setText:startTitle];
    [self.startDayLab setText:startDay];
    
    [self.endTitleLab setText:endTitle];
    [self.endDayLab setText:endDay];
    
}

- (void)setFlowStatusViewType:(WeXCPFlowStatusViewType)type {
    switch (type) {
        case WeXCPFlowStatusViewTypeBuy: {
            [self setViewHigh:self.buyDotView];
            [self setViewNormal:self.startDotView];
            [self setViewNormal:self.endDotView];
            [self setSepratorLineNormal:self.leftSepratorLine];
            [self setSepratorLineNormal:self.rightSepratorLine];
        }
            
            break;
        case WeXCPFlowStatusViewTypeStart: {
            [self setViewHigh:self.buyDotView];
            [self setViewHigh:self.startDotView];
            [self setViewNormal:self.endDotView];
            [self setSepratorLineNormal:self.leftSepratorLine];
            [self setSepratorLineNormal:self.rightSepratorLine];
            
        }
            break;
        case WeXCPFlowStatusViewTypeEnd: {
            [self setViewHigh:self.buyDotView];
            [self setViewHigh:self.startDotView];
            [self setViewHigh:self.endDotView];
            [self setSepratorLineNormal:self.leftSepratorLine];
            [self setSepratorLineNormal:self.rightSepratorLine];
            
        }
            break;
            
        default:
            break;
    }
}

- (void)setViewHigh:(UIView *)view {
    [view setBackgroundColor:kWexHighBackColor];
    view.layer.borderColor = kWexHighLayerColor.CGColor;
}

- (void)setViewNormal:(UIView *)view {
    [view setBackgroundColor:kWexNormalBackColor];
    view.layer.borderColor = kWexNormalLayerColor.CGColor;
}

- (void)setSepratorLineHigh:(UIView *)sepratorView {
    [sepratorView setBackgroundColor:kWexHighBackColor];
}

- (void)setSepratorLineNormal:(UIView *)sepratorView {
    [sepratorView setBackgroundColor:kWexSepratorLineColor];
}




@end
