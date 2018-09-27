//
//  WeXCoinProfitTopProfitCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/13.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCoinProfitTopProfitCell.h"
#import "WeXCPSaleInfoResModel.h"
#import "WeXCPActivityMainResModel.h"

@interface WeXCoinProfitTopProfitCell ()
@property (nonatomic, weak) UIImageView *topBackView;
@property (nonatomic, weak) UILabel *yearProfitLab;
@property (nonatomic, weak) UILabel *yearProfitNumLab;
@property (nonatomic, weak) UILabel *statusLab;
@property (nonatomic, weak) UIView *bottomBackView;
@property (nonatomic, weak) UILabel *buyNumTitleLab;
@property (nonatomic, weak) UILabel *buyNumLab;
@property (nonatomic, weak) UIView  *verticalLine;
@property (nonatomic, weak) UILabel *periodTitleLab;
@property (nonatomic, weak) UILabel *periodLab;


@end

@implementation WeXCoinProfitTopProfitCell

static CGFloat const kRatio = 375 / 129.0;


- (void)wex_addSubViews {
    UIImageView *topBackView = [UIImageView new];
    [topBackView setImage:[UIImage imageNamed:@"Wex_Coin_TopBg"]];
    [self.contentView addSubview:topBackView];
    self.topBackView = topBackView;
    
    UILabel *yearProfitLab = CreateCenterAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    [self.topBackView addSubview:yearProfitLab];
    self.yearProfitLab = yearProfitLab;
    
    UILabel *yearProfitNumLab = CreateCenterAlignmentLabel(WexFont(18), [UIColor whiteColor]);
    [self.topBackView addSubview:yearProfitNumLab];
    self.yearProfitNumLab = yearProfitNumLab;
    
    UILabel *statusLab = CreateCenterAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    [statusLab setHidden:true];
    statusLab.layer.borderColor = [UIColor whiteColor].CGColor;
    statusLab.layer.borderWidth = 1.0f;
    statusLab.layer.cornerRadius = 5.0;
    statusLab.clipsToBounds = YES;
    [self.topBackView addSubview:statusLab];
    self.statusLab = statusLab;
    
    UIView *bottomBackView = [UIView new];
    [bottomBackView setBackgroundColor:ColorWithHex(0x6867CD)];
    [self.contentView addSubview:bottomBackView];
    self.bottomBackView = bottomBackView;
    
    UILabel *buyNumTitleLab = CreateCenterAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    [self.bottomBackView addSubview:buyNumTitleLab];
    self.buyNumTitleLab = buyNumTitleLab;
    
    UILabel *buyNumLab = CreateCenterAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    [self.bottomBackView addSubview:buyNumLab];
    self.buyNumLab = buyNumLab;
    
    UIView *verticalLine = [UIView new];
    [verticalLine setHidden:true];
    [verticalLine setBackgroundColor:ColorWithHex(0xFFFFFF)];
    [self.bottomBackView addSubview:verticalLine];
    self.verticalLine = verticalLine;
    
    UILabel *periodTitleLab = CreateCenterAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    [self.bottomBackView addSubview:periodTitleLab];
    self.periodTitleLab = periodTitleLab;
    
    UILabel *periodLab = CreateCenterAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    [self.bottomBackView addSubview:periodLab];
    self.periodLab = periodLab;
}

- (void)wex_addConstraints {
    [self.topBackView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(0);
        make.left.right.mas_equalTo(0);
        make.height.mas_equalTo(kScreenWidth / kRatio).priorityHigh();
    }];
    
    [self.yearProfitLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo( 40 );
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
    }];
    
    [self.yearProfitNumLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.yearProfitLab.mas_bottom).offset(20 );
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
    }];
    
    [self.statusLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.yearProfitNumLab.mas_bottom).offset(10);
        make.centerX.mas_equalTo(0);
        make.width.mas_equalTo(55);
        make.height.mas_equalTo(20);
    }];
    
    [self.bottomBackView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.topBackView.mas_bottom).offset(0);
        make.left.right.mas_equalTo(0);
        make.height.mas_equalTo(64).priorityHigh();
        make.bottom.mas_equalTo(0);
    }];
    
    [self.buyNumTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.right.equalTo(self.bottomBackView.mas_centerX).offset(-10);
        make.top.mas_equalTo(15);
    }];
    [self.buyNumLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.buyNumTitleLab);
        make.right.equalTo(self.buyNumTitleLab);
        make.top.equalTo(self.buyNumTitleLab.mas_bottom).offset(5);
    }];
    
    [self.verticalLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(7);
        make.centerX.mas_equalTo(0);
        make.width.mas_equalTo(1);
        make.bottom.mas_equalTo(-7);
    }];
    
    [self.periodTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(15);
        make.left.equalTo(self.bottomBackView.mas_centerX).offset(10);
        make.right.mas_equalTo(-10);
    }];
    [self.periodLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.periodTitleLab);
        make.right.equalTo(self.periodTitleLab);
        make.top.equalTo(self.periodTitleLab.mas_bottom).offset(5);
    }];
}
- (void)setTest {
    [self.yearProfitLab setText:@"预期年化收益"];
    [self.yearProfitNumLab setText:@"6.50%"];
    [self.buyNumTitleLab setText:@"起购数量"];
    [self.buyNumLab setText:@"100 DCC"];
    [self.periodTitleLab setText:@"管理期限"];
    [self.periodLab setText:@"28天"];
}
- (void)setStatusLabelType:(WexCPStatusLabelType)type assetCode:(NSString *)assetCode{
    switch (type) {
        case WexCPStatusLabelTypeStart: {
            [self.yearProfitLab setText:[NSString stringWithFormat:@"在投资本金 (%@)",assetCode]];
            [self.statusLab setText:@"募集中"];
            [self.buyNumTitleLab setText:[NSString stringWithFormat:@"待收收益 (%@)",assetCode]];
            [self.periodTitleLab setText:[NSString stringWithFormat:@"待收本息合计 (%@)",assetCode]];
            [self.statusLab setHidden:NO];
            self.statusLab.layer.borderColor = ColorWithHex(0xFFFFFF).CGColor;
            self.statusLab.layer.borderWidth = 1.0;
            [self.statusLab setBackgroundColor:[UIColor clearColor]];
        }
            
            break;
        case WexCPStatusLabelTypeComplete: {
            [self.yearProfitLab setText:[NSString stringWithFormat:@"在投资本金 (%@)",assetCode]];
            [self.statusLab setText:@"已结束"];
            [self.periodTitleLab setText:[NSString stringWithFormat:@"待收本息合计 (%@)",assetCode]];
            [self.buyNumTitleLab setText:[NSString stringWithFormat:@"收益 (%@)",assetCode]];
            [self.statusLab setHidden:NO];
            self.statusLab.layer.borderWidth = 0;
            [self.statusLab setBackgroundColor:ColorWithRGBA(1, 1, 1, 0.1)];
        }
            break;
        case WexCPStatusLabelTypeClose: {
            [self.yearProfitLab setText:[NSString stringWithFormat:@"在投资本金(%@)",assetCode]];
            [self.statusLab setText:@"收益中"];
            [self.buyNumTitleLab setText:[NSString stringWithFormat:@"待收收益 (%@)",assetCode]];
            [self.periodTitleLab setText:[NSString stringWithFormat:@"待收本息合计 (%@)",assetCode]];
            [self.statusLab setHidden:NO];
            self.statusLab.layer.borderWidth = 0;
            [self.statusLab setBackgroundColor:ColorWithRGBA(1, 1, 1, 0.1)];
        }
            break;

        default:
            break;
    }
    if (!self.statusLab.hidden) {
        [self.yearProfitLab mas_updateConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(IS_IPHONE_6P ? 40 : 20);
            make.left.mas_equalTo(15);
            make.right.mas_equalTo(-15);
        }];
        
        [self.yearProfitNumLab mas_updateConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.yearProfitLab.mas_bottom).offset(IS_IPHONE_6P ? 20 : 10);
            make.left.mas_equalTo(15);
            make.right.mas_equalTo(-15);
        }];
        
        [self.statusLab mas_updateConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.yearProfitNumLab.mas_bottom).offset(10);
            make.centerX.mas_equalTo(0);
            make.width.mas_equalTo(55);
            make.height.mas_equalTo(20);
        }];
    }
    
    
}

- (void)setSaleInfoModel:(WeXCPSaleInfoResModel *)resModel
                    type:(WexCPStatusLabelType)type
             totalAmount:(NSString *)totalAmount
               assetCode:(NSString *)assetCode {
    [self.verticalLine setHidden:false];
    [self setStatusLabelType:type assetCode:assetCode];
    [self.yearProfitLab setText:totalAmount];
    NSString *profit = [WexCommonFunc saveTwoDigitalDecimal:[totalAmount integerValue] * 0.761];
    [self.buyNumLab setText:[NSString stringWithFormat:@"+%@",profit]];
    NSString *total = [WexCommonFunc saveTwoDigitalDecimal:[profit floatValue] + [totalAmount floatValue]];
    [self.periodLab setText:total];
}

/**
 持仓界面
 
 @param resModel model
 @param status 状态string
 @param amount 金额
 */
- (void)setSaleInfoModel:(WeXCPSaleInfoResModel *)resModel
            statusString:(NSString *)status
             totalAmount:(NSString *)amount
               assetCode:(NSString *)assetCode{
    [self.verticalLine setHidden:false];
    if ([status isEqualToString:@"1"]) { //募集
        [self setStatusLabelType:WexCPStatusLabelTypeStart assetCode:assetCode];
    } else if ([status isEqualToString:@"2"] || [status isEqualToString:@"3"]) { //收益
        [self setStatusLabelType:WexCPStatusLabelTypeClose assetCode:assetCode];
    } else { //结束
        [self setStatusLabelType:WexCPStatusLabelTypeComplete assetCode:assetCode];
    }
    [self.yearProfitNumLab setText:amount];
    NSString *profit = nil;
    if ([assetCode isEqualToString:@"DCC"]) {
        profit = [WexCommonFunc getCPProfitWithPrincipal:amount period:resModel.period annualRate:resModel.annualRate scale:2];
    } else {
        profit = [WexCommonFunc getCPProfitWithPrincipal:amount period:resModel.period annualRate:resModel.annualRate scale:5];
    }
    [self.buyNumLab setText:[NSString stringWithFormat:@"+%@",profit]];
    NSString *total = nil;
    if ([assetCode isEqualToString:@"DCC"]) {
        total = [WexCommonFunc downDoubleValue:[profit doubleValue] + [amount doubleValue] decimals:2];
    } else {
        total = [WexCommonFunc downDoubleValue:[profit doubleValue] + [amount doubleValue] decimals:5];
    }
    [self.periodLab setText:total];
}


- (void)setSaleInfoModel:(WeXCPSaleInfoResModel *)resModel {
    [self.verticalLine setHidden:false];
    [self.yearProfitLab setText:@"预期年化收益"];
    [self.yearProfitNumLab setText:[NSString stringWithFormat:@"%@%@",resModel.annualRate,@"%"]];
    [self.periodTitleLab setText:@"管理期限"];
    [self.periodLab setText:[NSString stringWithFormat:@"%@%@",resModel.period,@"天"]];
}

- (void)setMinBuyAmount:(NSString *)minAmount assetCode:(NSString *)assetCode {
    [self.buyNumTitleLab setText:@"起购数量"];
    [self.verticalLine setHidden:false];
    if ([minAmount length] > 0) {
        [self.buyNumLab setText:[NSString stringWithFormat:@"%@%@",minAmount,assetCode]];
    }
}
// MARK: - 新版币生息详情
- (void)setNewCoinProfitDetailWithProductModel:(WeXCPActivityListModel *)model {
    [self.verticalLine setHidden:false];
    [self.yearProfitLab setText:@"预期年化收益"];
    [self.yearProfitNumLab setText:[NSString stringWithFormat:@"%@%@",model.saleInfo.annualRate,@"%"]];
    [self.periodTitleLab setText:@"管理期限"];
    [self.periodLab setText:[NSString stringWithFormat:@"%@%@",model.saleInfo.period,@"天"]];
    [self.buyNumTitleLab setText:@"起购数量"];
    [self.verticalLine setHidden:false];
    NSString *minBuyAmount = [NSString stringWithFormat:@"%@%@",model.minPerHand,model.assetCode];
    [self.buyNumLab setText:minBuyAmount];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
