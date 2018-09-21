//
//  WeXRepaymentStatusView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/8.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXRepaymentStatusView.h"

@interface WeXRepaymentStatusView ()

@property (nonatomic, weak) UIImageView *logoImag;
@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, assign) WeXRepaymentStatus status;


@end

@implementation WeXRepaymentStatusView

+ (instancetype)createRepaymentStatusView:(CGRect)frame
                                   status:(WeXRepaymentStatus)status {
    WeXRepaymentStatusView *statusView = [[WeXRepaymentStatusView alloc] initWithFrame:frame status:status];
    return statusView;
}
- (id)initWithFrame:(CGRect)frame status:(WeXRepaymentStatus)status {
    if (self = [super initWithFrame:frame]) {
        self.status = status;
        [self wex_addsubviews];
        [self wex_addConstraints];
    }
    return self;
}

- (void)wex_addsubviews {
    
    UIImageView *logoIcon = [UIImageView new];
    [self addSubview:logoIcon];
    self.logoImag = logoIcon;
    
    UILabel *titleLab = [UILabel new];
    titleLab.font = [UIFont systemFontOfSize:15.0];
    titleLab.textAlignment = NSTextAlignmentCenter;
    titleLab.textColor = ColorWithHex(0x4A4A4A);
    [self addSubview:titleLab];
    self.titleLab = titleLab;
    
    if (self.status == WeXRepaymentStatusRunning) {
        [self.titleLab setText:@"还币转账进行中，请在转账成功后，回来确认还款。"];
        [self.logoImag setImage:[UIImage imageNamed:@"Repay_Running"]];
    } else {
        [self.titleLab setText:@"还币失败,请稍后重新发起"];
        [self.logoImag setImage:[UIImage imageNamed:@"Repay_Fail"]];
    }
    
}

- (void)wex_addConstraints {
    CGFloat kImageRatio = self.logoImag.image.size.width / self.logoImag.image.size.height;
    CGFloat kImageH = 126;
    [self.logoImag mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(0);
        make.centerY.mas_equalTo(-30);
        make.height.mas_equalTo(kImageH);
        make.width.mas_equalTo(kImageH * kImageRatio);
    }];
    
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.logoImag.mas_bottom).offset(16);
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
    }];    
}



@end
