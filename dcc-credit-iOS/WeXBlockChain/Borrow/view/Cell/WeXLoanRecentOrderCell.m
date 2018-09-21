//
//  WeXLoanRecentOrderCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanRecentOrderCell.h"
#import "WeXLoanRecentOrderView.h"
#import "WeXLoanQueryOrdersModal.h"
#import "WeXLoanGetOrderDetailModal.h"

@interface WeXLoanRecentOrderCell ()
@property (nonatomic, strong) UIImageView *backImageView;
@property (nonatomic, strong) UILabel *recentOrderLab;
@property (nonatomic, strong) UIButton *moreOrderButton;
@property (nonatomic, strong) UIView *sepratorLine;

@property (nonatomic, weak) WeXLoanRecentOrderView *firstLineView;
@property (nonatomic, weak) WeXLoanRecentOrderView *secondLineView;
@property (nonatomic, weak) WeXLoanRecentOrderView *thirdLineView;

#define kTitleColor    ColorWithHex(0xD3A9FF)
#define kSubTitleColor [UIColor whiteColor]

@end

static CGFloat const kImageRatio = 347.0 / 289.0;

@implementation WeXLoanRecentOrderCell

- (void)wex_addSubViews {
    [self.contentView setBackgroundColor:WexSepratorLineColor];

    _backImageView = [UIImageView new];
    _backImageView.userInteractionEnabled = true;
    _backImageView.image = [UIImage imageNamed:@"LoanOrderBg"];
    [self.contentView addSubview:_backImageView];
    
    _recentOrderLab = CreateLeftAlignmentLabel(WexFont(17), [UIColor whiteColor]);
    [self.backImageView addSubview:_recentOrderLab];

    _moreOrderButton = [UIButton new];
    [_moreOrderButton setTitle:@"更多订单" forState:UIControlStateNormal];
    _moreOrderButton.layer.cornerRadius = 12;
    [_moreOrderButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    _moreOrderButton.titleLabel.font = WexFont(15.0);
    _moreOrderButton.layer.borderWidth = 1.0;
    _moreOrderButton.layer.borderColor = [UIColor whiteColor].CGColor;
    [_moreOrderButton addTarget:self action:@selector(moreOrderEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.backImageView addSubview:_moreOrderButton];
    
    _sepratorLine = [UIView new];
    _sepratorLine.backgroundColor = ColorWithHex(0XD3A9FF);
    [self.backImageView addSubview:_sepratorLine];
    
    WeXLoanRecentOrderView *firstLineView = [WeXLoanRecentOrderView new];
    [self.backImageView addSubview:firstLineView];
    self.firstLineView = firstLineView;
    
    WeXLoanRecentOrderView *secondLineView = [WeXLoanRecentOrderView new];
    [self.backImageView addSubview:secondLineView];
    self.secondLineView = secondLineView;
    
    WeXLoanRecentOrderView *thirdLineView = [WeXLoanRecentOrderView new];
    [self.backImageView addSubview:thirdLineView];
    self.thirdLineView = thirdLineView;
    
}

//WeXLoanRecentOrderCell
- (void)wex_addConstraints {
    UIView *tempViewOne = [UIView new];
    [self.backImageView addSubview:tempViewOne];
    
    UIView *tempViewTwo = [UIView new];
    [self.backImageView addSubview:tempViewTwo];
    
    [self.backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.mas_equalTo(10);
        make.right.mas_equalTo(-14);
        make.height.mas_equalTo(self.backImageView.mas_width).dividedBy(kImageRatio).priorityMedium();
        make.bottom.mas_equalTo(-10).priorityHigh();
    }];
    
    [self.recentOrderLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.mas_equalTo(13);
    }];
    
    [self.moreOrderButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-14);
        make.centerY.equalTo(self.recentOrderLab);
        make.size.mas_equalTo(CGSizeMake(70, 25));
    }];
    
    [self.sepratorLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.right.mas_equalTo(-14);
        make.top.equalTo(self.recentOrderLab.mas_bottom).offset(10);
        make.height.mas_equalTo(3);
    }];
    
    [self.firstLineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.mas_equalTo(0);
        make.top.equalTo(self.sepratorLine.mas_bottom).offset(15);
        make.height.mas_equalTo(50);
        make.bottom.equalTo(tempViewOne.mas_top);
    }];
    
    [self.secondLineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.mas_equalTo(0);
        make.top.equalTo(tempViewOne.mas_bottom);
        make.height.mas_equalTo(50);
        make.bottom.equalTo(tempViewTwo.mas_top);
    }];
    [self.thirdLineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.mas_equalTo(0);
        make.bottom.mas_equalTo(-15);
        make.height.mas_equalTo(50);
        make.top.equalTo(tempViewTwo.mas_bottom);
    }];

    [tempViewOne mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.backImageView);
        make.height.equalTo(tempViewTwo.mas_height);
    }];

    [tempViewTwo mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.backImageView);
    }];
    
}
- (void)setOrderNum:(NSString *)orderNum
             symbol:(NSString *)symbol
         loanAmount:(NSString *)loanAmount
        repayAmount:(NSString *)repayAmount
             period:(NSString *)period
             status:(NSString *)status {
    [self.recentOrderLab setText:@"最近订单"];
    [self.firstLineView  setLeftTitle:@"订单号"   leftSubTitle:orderNum rightTitle:@"币种" rightSubTitle:symbol];
    [self.secondLineView setLeftTitle:@"借款金额" leftSubTitle:loanAmount rightTitle:@"应还金额" rightSubTitle:repayAmount];
    [self.thirdLineView  setLeftTitle:@"借款周期" leftSubTitle:period rightTitle:@"状态" rightSubTitle:status];
}
- (void)setRecentOrderData:(WeXLoanGetOrderDetailResponseModal *)data {
    [self.recentOrderLab setText:@"最近订单"];
    [self.firstLineView  setLeftTitle:@"订单号" leftSubTitle:data.orderId rightTitle:@"币种" rightSubTitle:data.currency.symbol];
    //expectLoanInterest
    NSString *repayAmount = [NSString stringWithFormat:@"%.4f",[data.amount floatValue] + [data.expectLoanInterest floatValue]];
    NSString *loanAmount = [NSString stringWithFormat:@"%@%@",data.amount,data.currency.symbol];
    [self.secondLineView setLeftTitle:@"借款金额" leftSubTitle:loanAmount rightTitle:@"应还金额" rightSubTitle:repayAmount];
    NSString *period = nil;
    if ([data.status isEqualToString:WEX_LOAN_STATUS_REPAID]||[data.status isEqualToString:WEX_LOAN_STATUS_DELIVERED]) {
        NSString *time = [NSString stringWithFormat:@"%@-%@",[WexCommonFunc formatterTimeStringWithTimeStamp:data.deliverDate formatter:@"yyyy.MM.dd"],[WexCommonFunc formatterTimeStringWithTimeStamp:data.repayDate formatter:@"yyyy.MM.dd"]];
        period = time;
    }
    else {
        period = [NSString stringWithFormat:@"%@%@",data.borrowDuration,[WexCommonFunc transferChinesePeriod:data.durationUnit]];
    }
    NSString *status = nil;
    //审核中
    if ([data.status isEqualToString:WEX_LOAN_STATUS_AUDITING]) {
        status = @"审核中";
    }
    //审核失败
    else if ([data.status isEqualToString:WEX_LOAN_STATUS_REJECTED]) {
        status = @"审核失败";
    }
    //审核成功
    else if ([data.status isEqualToString:WEX_LOAN_STATUS_APPROVED]){
        status = @"审核成功";
    }
    //放币失败
    else if ([data.status isEqualToString:WEX_LOAN_STATUS_FAILURE]) {
        status = @"放币失败";
    }
    //已放款
    else if ([data.status isEqualToString:WEX_LOAN_STATUS_DELIVERED]) {
        status = @"已放款";
    }
    //已还币
    else if ([data.status isEqualToString:WEX_LOAN_STATUS_REPAID]) {
       status = @"已还币";
    }
    [self.thirdLineView  setLeftTitle:@"借款周期" leftSubTitle:period rightTitle:@"状态" rightSubTitle:status];

}

- (void)moreOrderEvent:(UIButton *)sender {
    !self.DidClickMoreOrder ? : self.DidClickMoreOrder();
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

}

@end
