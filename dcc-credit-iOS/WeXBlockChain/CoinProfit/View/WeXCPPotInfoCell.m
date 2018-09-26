//
//  WeXCPPotInfoCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPPotInfoCell.h"
#import "UIView+WeXCorner.h"
#import "WeXCPPotListMainModel.h"
#import "NSString+WexTool.h"


@interface WeXCPPotInfoCell ()

@property (nonatomic, weak) UIImageView *headImageView;
@property (nonatomic, weak) UIImageView *coinIconImageView;
@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) UILabel *titleProfitLab;

@property (nonatomic, weak) UIView *backInfoView;
@property (nonatomic, weak) UILabel *principalTitleLab;
@property (nonatomic, weak) UILabel *principalLab;
@property (nonatomic, weak) UILabel *profitTitleLab;
@property (nonatomic, weak) UILabel *profitLab;
@property (nonatomic, weak) UILabel *startTimeTitleLab;
@property (nonatomic, weak) UILabel *startTimeLab;
@property (nonatomic, weak) UILabel *periodTitleLab;
@property (nonatomic, weak) UILabel *periodLab;
@property (nonatomic, weak) UILabel *endTimeTitleLab;
@property (nonatomic, weak) UILabel *endTimeLab;
@property (nonatomic, weak) UILabel *totalTitleLab;
@property (nonatomic, weak) UILabel *totalLab;
@property (nonatomic, weak) UIImageView *statusImageview;

@end

@implementation WeXCPPotInfoCell

static CGFloat const kImageRatio = 347.0 / 67.0;
static CGFloat const kInfoViewH = 237;



- (void)wex_addSubViews {
    [self.contentView setBackgroundColor:WexSepratorLineColor];
    UIImageView *headImageView = [[UIImageView alloc] initWithFrame:CGRectMake(14, 0, kScreenWidth - 2 * 14,( kScreenWidth - 2 * 14) / kImageRatio)];
    headImageView.userInteractionEnabled = true;
    headImageView.image = [UIImage imageNamed:@"Rectangle-bg"];
    [self.contentView addSubview:headImageView];
    self.headImageView = headImageView;
    
    UIImageView *coinIconImageView = [UIImageView new];
    [self.headImageView addSubview:coinIconImageView];
    self.coinIconImageView = coinIconImageView;
    
    UILabel *titleLab = CreateLeftAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    titleLab.userInteractionEnabled = true;
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clickProductNameEvent:)];
    [titleLab addGestureRecognizer:tapGesture];
    
    titleLab.adjustsFontSizeToFitWidth = true;
    [self.headImageView addSubview:titleLab];
    self.titleLab = titleLab;
    
    UILabel *titleProfitLab = CreateLeftAlignmentLabel(WexFont(16), [UIColor whiteColor]);
    [self.headImageView addSubview:titleProfitLab];
    self.titleProfitLab = titleProfitLab;
    
    UIView *backInfoView = [[UIView alloc] initWithFrame:CGRectMake(self.headImageView.WeX_x + 14, ( kScreenWidth - 2 * 14) / kImageRatio, self.headImageView.WeX_width - 28, kInfoViewH)];
    [backInfoView addBottomCornerRadius:8 fillColor:[UIColor whiteColor]];
    [self.contentView addSubview:backInfoView];
    self.backInfoView = backInfoView;
    
    
    UILabel * principalTitleLab = CreateLeftAlignmentLabel(WexFont(15), ColorWithHex(0xBAC0C5));
    [self.backInfoView addSubview:principalTitleLab];
    self.principalTitleLab = principalTitleLab;
    
    UILabel * principalLab = CreateLeftAlignmentLabel(WexFont(16), WexDefault4ATitleColor);
    [self.backInfoView addSubview:principalLab];
    self.principalLab = principalLab;
    
    UILabel * profitTitleLab = CreateLeftAlignmentLabel(WexFont(15), ColorWithHex(0xBAC0C5));
    [self.backInfoView addSubview:profitTitleLab];
    self.profitTitleLab = profitTitleLab;
    
    UILabel * profitLab = CreateLeftAlignmentLabel(WexFont(16), ColorWithHex(0xED190F));
    [self.backInfoView addSubview:profitLab];
    self.profitLab = profitLab;

    
    UILabel * startTimeTitleLab = CreateLeftAlignmentLabel(WexFont(15), ColorWithHex(0xBAC0C5));
    [self.backInfoView addSubview:startTimeTitleLab];
    self.startTimeTitleLab = startTimeTitleLab;
    
    UILabel * startTimeLab = CreateLeftAlignmentLabel(WexFont(16), WexDefault4ATitleColor);
    [self.backInfoView addSubview:startTimeLab];
    self.startTimeLab = startTimeLab;
    
    UILabel * periodTitleLab = CreateLeftAlignmentLabel(WexFont(15), ColorWithHex(0xBAC0C5));
    [self.backInfoView addSubview:periodTitleLab];
    self.periodTitleLab = periodTitleLab;
    
    UILabel * periodLab = CreateLeftAlignmentLabel(WexFont(16), ColorWithHex(0xED190F));
    [self.backInfoView addSubview:periodLab];
    self.periodLab = periodLab;
    
    UILabel * endTimeTitleLab = CreateLeftAlignmentLabel(WexFont(15), ColorWithHex(0xBAC0C5));
    [self.backInfoView addSubview:endTimeTitleLab];
    self.endTimeTitleLab = endTimeTitleLab;
    
    UILabel * endTimeLab = CreateLeftAlignmentLabel(WexFont(16), WexDefault4ATitleColor);
    [self.backInfoView addSubview:endTimeLab];
    self.endTimeLab = endTimeLab;
    
    UILabel * totalTitleLab = CreateLeftAlignmentLabel(WexFont(15), ColorWithHex(0xBAC0C5));
    [self.backInfoView addSubview:totalTitleLab];
    self.totalTitleLab = totalTitleLab;
    
    UILabel * totalLab = CreateLeftAlignmentLabel(WexFont(16), WexDefault4ATitleColor);
    [self.backInfoView addSubview:totalLab];
    self.totalLab = totalLab;
    
    UIImageView *statusImageview = [UIImageView new];
    [self.contentView addSubview:statusImageview];
    self.statusImageview = statusImageview;
    
}

- (void)wex_addConstraints {
    self.contentView.mas_key = @"";
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
        make.height.mas_equalTo( (kScreenWidth - 2 * 14) / kImageRatio + kInfoViewH + 10 ).priorityHigh();
    }];
    
    [self.coinIconImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(43, 43));
    }];
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.left.equalTo(self.coinIconImageView.mas_right).offset(11);
        make.right.lessThanOrEqualTo(self.titleProfitLab.mas_left).offset(-5);
        make.height.mas_equalTo(30);
    }];
    [self.titleProfitLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.right.mas_equalTo(-14);
    }];
    
    [self.principalTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(12);
        make.top.mas_equalTo(18);
    }];
    
    [self.principalLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(12);
        make.top.equalTo(self.principalTitleLab.mas_bottom).offset(10);
    }];
    
    [self.profitTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(18);
        make.left.equalTo(self.backInfoView.mas_centerX).offset(11);
    }];
    
    [self.profitLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.profitTitleLab.mas_bottom).offset(10);
        make.left.equalTo(self.backInfoView.mas_centerX).offset(11);
    }];
    
    
    [self.startTimeTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(12);
        make.top.mas_equalTo(96);
    }];
    
    [self.startTimeLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(12);
        make.top.equalTo(self.startTimeTitleLab.mas_bottom).offset(10);
    }];
    
    [self.periodTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.backInfoView.mas_centerX).offset(12);
        make.top.equalTo(self.startTimeTitleLab);
    }];
    
    [self.periodLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.backInfoView.mas_centerX).offset(12);
        make.top.equalTo(self.periodTitleLab.mas_bottom).offset(10);
    }];
    
    
    [self.endTimeLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(-20);
        make.left.mas_equalTo(12);
    }];
    [self.endTimeTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.endTimeLab.mas_top).offset(-10);
        make.left.mas_equalTo(12);
    }];
    
    [self.totalLab mas_makeConstraints:^(MASConstraintMaker *make) {
         make.bottom.mas_equalTo(-20);
         make.left.equalTo(self.backInfoView.mas_centerX).offset(12);
    }];
    [self.totalTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.totalLab.mas_top).offset(-10);
        make.left.equalTo(self.backInfoView.mas_centerX).offset(12);
    }];
    
    [self.statusImageview mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.profitTitleLab.mas_bottom).offset(2);
        make.left.equalTo(self.profitTitleLab.mas_right).offset(-15);
        make.size.mas_equalTo(CGSizeMake(79, 53));
    }];
}

- (void)clickProductNameEvent:(UITapGestureRecognizer *)gesture {
    !self.DidClickProductName ? : self.DidClickProductName();
}

- (void)setTitle:(NSString *)title
          profit:(NSString *)profit
       principal:(NSString *)principal
       startTime:(NSString *)startTime
          period:(NSString *)period
         endTime:(NSString *)endTime
     totalAmount:(NSString *)totalAmount
            type:(CPPotInfoCellType)type{
    [self.titleLab setText:title];
    [self.titleProfitLab setText:[NSString stringWithFormat:@"+%@",profit]];
    [self.principalTitleLab setText:@"投资本金"];
    [self.principalLab setText:principal];
    [self.profitTitleLab setText:@"预期年化收益"];
    [self.profitLab setText:[NSString stringWithFormat:@"+%@",profit]];
    [self.startTimeTitleLab setText:@"起息时间"];
    [self.startTimeLab setText:startTime];
    [self.periodTitleLab setText:@"期限"];
    [self.periodLab setText:period];
    [self.endTimeTitleLab setText:@"到期时间"];
    [self.endTimeLab setText:endTime];
    [self.totalTitleLab setText:@"应回"];
    [self.totalLab setText:totalAmount];
    [self.coinIconImageView setImage:[UIImage imageNamed:@"ethereum"]];
    switch (type) {
        case CPPotInfoCellTypeBuying: {
            [self.statusImageview setImage:[UIImage imageNamed:@"wex_buying"]];
            [self.backInfoView addBottomCornerRadius:8 fillColor:[UIColor whiteColor]];

        }
            break;
        case CPPotInfoCellTypeIncoming: {
            [self.statusImageview setImage:[UIImage imageNamed:@"wex_incoming"]];
            [self.backInfoView addBottomCornerRadius:8 fillColor:[UIColor whiteColor]];

        }
            break;
        case CPPotInfoCellTypeSellOver: {
            [self.statusImageview setImage:[UIImage imageNamed:@"wex_send_over"]];
            [self.backInfoView addBottomCornerRadius:8 fillColor:ColorWithRGBA(74, 74, 74, 0.40)];
        }
            break;
        case CPPotInfoCellTypeOver: {
            [self.statusImageview setImage:[UIImage imageNamed:@"wex_over"]];
            [self.backInfoView addBottomCornerRadius:8 fillColor:ColorWithRGBA(74, 74, 74, 0.40)];
        }
            break;
            
        default: {
            [self.statusImageview setImage:[UIImage imageNamed:@"wex_buying"]];
            [self.backInfoView addBottomCornerRadius:8 fillColor:[UIColor whiteColor]];
        }
            break;
    }
}
- (void)setPotListModel:(WeXCPPotListResultModel *)model {
    NSString *title   = model.saleInfo.name;
    NSString *profit    = [model.saleInfo.annualRate stringByAppendingString:@"%"];
    NSString *principal = [model.positionAmount stringByAppendingString:model.assetCode];
    NSString *startTime = [model.saleInfo.startTime yearToDayTimeString];
    NSString *period = [NSString stringWithFormat:@"%@%@",model.saleInfo.period,@"天"];
    NSString *endTime = [model.saleInfo.endTime yearToDayTimeString];
    NSString *totalAmount = [model.expectedRepay stringByAppendingString:[NSString stringWithFormat:@" %@",model.assetCode]];
    CPPotInfoCellType type =  CPPotInfoCellTypeBuying;
    switch ([model.status integerValue]) {
        case 0 | 1:
            type =  CPPotInfoCellTypeBuying;
            break;
        case 2:
            type =  CPPotInfoCellTypeIncoming;
            break;
        case 3:
            type =  CPPotInfoCellTypeSellOver;
            break;
        case 4:
            type =  CPPotInfoCellTypeOver;
            break;
            
        default:
            break;
    }
    [self setTitle:title profit:profit principal:principal startTime:startTime period:period endTime:endTime totalAmount:totalAmount type:type];
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
