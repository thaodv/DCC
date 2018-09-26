//
//  WeXCPProductInfoCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPProductInfoCell.h"
#import "UIView+WeXCorner.h"
#import "WeXCPActivityMainResModel.h"

@interface WeXCPProductInfoCell ()
@property (nonatomic, weak) UIImageView *headImageView;
@property (nonatomic, weak) UIImageView *coinIconImageView;
@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) UILabel *titleProfitLab;

@property (nonatomic, weak) UIView *backInfoView;
@property (nonatomic, weak) UILabel *symbolTitleLab;
@property (nonatomic, weak) UILabel *symbolLab;
@property (nonatomic, weak) UILabel *profitTitleLab;
@property (nonatomic, weak) UILabel *profitLab;
@property (nonatomic, weak) UILabel *periodTitleLab;
@property (nonatomic, weak) UILabel *periodLab;
@property (nonatomic, weak) UILabel *amountTitleLab;
@property (nonatomic, weak) UILabel *amountLab;
@property (nonatomic, weak) UIImageView *statusImageview;
@property (nonatomic, weak) UIView *totastView;
@end

@implementation WeXCPProductInfoCell

static CGFloat const kImageRatio = 347.0 / 67.0;


- (void)wex_addSubViews {
    [self.contentView setBackgroundColor:WexSepratorLineColor];
    UIImageView *headImageView = [[UIImageView alloc] initWithFrame:CGRectMake(14, 0, kScreenWidth - 2 * 14,( kScreenWidth - 2 * 14) / kImageRatio)];
    headImageView.image = [UIImage imageNamed:@"Rectangle-bg"];
    [self.contentView addSubview:headImageView];
    self.headImageView = headImageView;
    
    UIImageView *coinIconImageView = [UIImageView new];
    [self.headImageView addSubview:coinIconImageView];
    self.coinIconImageView = coinIconImageView;
    
    UILabel *titleLab = CreateLeftAlignmentLabel(WexFont(14), [UIColor whiteColor]);
    titleLab.adjustsFontSizeToFitWidth = true;
    [self.headImageView addSubview:titleLab];
    self.titleLab = titleLab;
    
    UILabel *titleProfitLab = CreateLeftAlignmentLabel(WexFont(16), [UIColor whiteColor]);
    [self.headImageView addSubview:titleProfitLab];
    self.titleProfitLab = titleProfitLab;
    
    UIView *backInfoView = [[UIView alloc] initWithFrame:CGRectMake(self.headImageView.WeX_x + 14, ( kScreenWidth - 2 * 14) / kImageRatio, self.headImageView.WeX_width - 28, 170)];
    [backInfoView addBottomCornerRadius:8 fillColor:[UIColor whiteColor]];
    [self.contentView addSubview:backInfoView];
    self.backInfoView = backInfoView;
    
    
    UILabel * symbolTitleLab = CreateLeftAlignmentLabel(WexFont(15), ColorWithHex(0xBAC0C5));
    [self.backInfoView addSubview:symbolTitleLab];
    self.symbolTitleLab = symbolTitleLab;
    
    UILabel * symbolLab = CreateLeftAlignmentLabel(WexFont(16), WexDefault4ATitleColor);
    [self.backInfoView addSubview:symbolLab];
    self.symbolLab = symbolLab;
    
    UILabel * profitTitleLab = CreateLeftAlignmentLabel(WexFont(15), ColorWithHex(0xBAC0C5));
    [self.backInfoView addSubview:profitTitleLab];
    self.profitTitleLab = profitTitleLab;
    
    UILabel * profitLab = CreateLeftAlignmentLabel(WexFont(16), ColorWithHex(0xED190F));
    [self.backInfoView addSubview:profitLab];
    self.profitLab = profitLab;
    
    UILabel * periodTitleLab = CreateLeftAlignmentLabel(WexFont(15), ColorWithHex(0xBAC0C5));
    [self.backInfoView addSubview:periodTitleLab];
    self.periodTitleLab = periodTitleLab;
    
    UILabel * periodLab = CreateLeftAlignmentLabel(WexFont(16), ColorWithHex(0xED190F));
    [self.backInfoView addSubview:periodLab];
    self.periodLab = periodLab;
    
    UILabel * amountTitleLab = CreateLeftAlignmentLabel(WexFont(15), ColorWithHex(0xBAC0C5));
    [self.backInfoView addSubview:amountTitleLab];
    self.amountTitleLab = amountTitleLab;
    
    UILabel * amountLab = CreateLeftAlignmentLabel(WexFont(16), WexDefault4ATitleColor);
    [self.backInfoView addSubview:amountLab];
    self.amountLab = amountLab;
    
    UIImageView *statusImageview = [UIImageView new];
    [self.contentView addSubview:statusImageview];
    self.statusImageview = statusImageview;

}

- (void)wex_addConstraints {
    self.contentView.mas_key = @"";
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
        make.height.mas_equalTo( (kScreenWidth - 2 * 14) / kImageRatio + 180 ).priorityHigh();
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
    }];
    [self.titleProfitLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.right.mas_equalTo(-14);
    }];
    
    [self.symbolTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(12);
        make.top.mas_equalTo(18);
    }];
    
    [self.symbolLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(12);
        make.top.equalTo(self.symbolTitleLab.mas_bottom).offset(10);
    }];
    
    [self.profitTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(18);
        make.left.equalTo(self.backInfoView.mas_centerX).offset(11);
    }];
    
    [self.profitLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.profitTitleLab.mas_bottom).offset(10);
        make.left.equalTo(self.backInfoView.mas_centerX).offset(11);
    }];
    
    
    [self.periodLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(12);
        make.bottom.mas_equalTo(-26);
    }];
    
    [self.periodTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(12);
        make.bottom.equalTo(self.periodLab.mas_top).offset(-10);
    }];
    
    [self.amountLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.backInfoView.mas_centerX).offset(12);
        make.bottom.mas_equalTo(-26);
    }];
    
    [self.amountTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.backInfoView.mas_centerX).offset(12);
        make.bottom.equalTo(self.amountLab.mas_top).offset(-10);
    }];
    
    [self.statusImageview mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.profitTitleLab.mas_bottom).offset(2);
        make.left.equalTo(self.profitTitleLab.mas_right).offset(-15);
        make.size.mas_equalTo(CGSizeMake(79, 53));
    }];
}
- (void)setTitle:(NSString *)title
          profit:(NSString *)profit
          symbol:(NSString *)symbol
          period:(NSString *)period
       minAmount:(NSString *)minAmount
            type:(ProductInfoCellType)type{
    switch (type) {
        case ProductInfoCellTypeBuying: {
            [self.statusImageview setImage:[UIImage imageNamed:@"wex_buying"]];
            [self.backInfoView addBottomCornerRadius:8 fillColor:[UIColor whiteColor]];
        }
            
            break;
        case ProductInfoCellTypeIncoming: {
            [self.statusImageview setImage:[UIImage imageNamed:@"wex_incoming"]];
            [self.backInfoView addBottomCornerRadius:8 fillColor:[UIColor whiteColor]];

        }
            break;
        case ProductInfoCellTypeSellOver: {
            [self.statusImageview setImage:[UIImage imageNamed:@"wex_send_over"]];
            [self.backInfoView addBottomCornerRadius:8 fillColor:ColorWithRGBA(74, 74, 74, 0.4)];
        }
            break;
        case ProductInfoCellTypeOver: {
            [self.statusImageview setImage:[UIImage imageNamed:@"wex_over"]];
            [self.backInfoView addBottomCornerRadius:8 fillColor:ColorWithRGBA(74, 74, 74, 0.4)];
        }
            break;
            
        default: {
            [self.statusImageview setImage:[UIImage imageNamed:@"wex_buying"]];
            [self.backInfoView addBottomCornerRadius:8 fillColor:[UIColor whiteColor]];
        }
            break;
    }
    
    [self.titleLab setText:title];
    [self.titleProfitLab setText:[NSString stringWithFormat:@"+%@",profit]];
    [self.symbolTitleLab setText:@"币种"];
    [self.symbolLab setText:symbol];
    [self.profitTitleLab setText:@"预期年化收益"];
    [self.profitLab setText:[NSString stringWithFormat:@"+%@",profit]];
    [self.periodTitleLab setText:@"管理期限"];
    [self.periodLab setText:period];
    [self.amountTitleLab setText:@"起购数量"];
    [self.amountLab setText:minAmount];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}
- (void)setMarkeetProductModel:(WeXCPActivityListModel *)model {
    NSString *coinCode = model.assetCode;
    NSString *iconURL  = model.logo;
    NSString *title    = model.saleInfo.name;
    NSString *profit   = [NSString stringWithFormat:@"%@%@",model.saleInfo.annualRate,@"%"];
    NSString *period   = [NSString stringWithFormat:@"%@%@",model.saleInfo.period,@"天"];
    NSString *minBuyAmount = model.minPerHand;
    ProductInfoCellType type = ProductInfoCellTypeBuying;
    switch ([model.status integerValue]) {
        case 0 | 1:
            type = ProductInfoCellTypeBuying;
            break;
        case 2:
            type = ProductInfoCellTypeIncoming;
            break;
        case 3:
            type = ProductInfoCellTypeSellOver;
            break;
        case 4:
            type = ProductInfoCellTypeOver;
            break;
            
        default:
            break;
    }
    [self setTitle:title profit:profit symbol:coinCode period:period minAmount:minBuyAmount type:type];
    [self.coinIconImageView sd_setImageWithURL:[NSURL URLWithString:iconURL] placeholderImage:WexCoinHolderImage];
}
/*
 
 WexCPStatusLabelTypeCreate = 0,     //活动创建
 WexCPStatusLabelTypeStart  = 1,    //活动开始 (募集中)
 WexCPStatusLabelTypeClose  = 2,    //活动关闭 (收益中)
 WexCPStatusLabelTypeEnd    = 3,    //活动结束
 WexCPStatusLabelTypeComplete = 4, //结算完成 (已结束)
 
 */

@end
