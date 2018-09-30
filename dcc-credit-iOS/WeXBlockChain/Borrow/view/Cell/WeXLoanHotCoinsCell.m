//
//  WeXLoanHotCoinsCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/21.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoanHotCoinsCell.h"
#import "WeXQueryProductByLenderCodeModal.h"
#import "UIView+WeXCorner.h"

@interface WeXLoanHotCoinsCell ()

@property (nonatomic, weak) UIView *backView;
@property (nonatomic, weak) UIImageView *coinIcon;
@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) UILabel *amountLab;
@property (nonatomic, weak) UILabel *rateLab;
@property (nonatomic, weak) UILabel *periodLab;

@end

static CGFloat const kBackViewH = 68;

@implementation WeXLoanHotCoinsCell

- (void)wex_addSubViews {
    [self.contentView setBackgroundColor:WexSepratorLineColor];
    UIView *backView = [[UIView alloc] initWithFrame:CGRectMake(14, 0, kScreenWidth - 14 * 2, kBackViewH)];
    [backView addCornerRadius:8.0f];
    [self.contentView addSubview:backView];
    self.backView = backView;
    
    UIImageView *coinIcon = [UIImageView new];
    coinIcon.layer.cornerRadius = 6;
    coinIcon.clipsToBounds = true;
    [self.backView addSubview:coinIcon];
    self.coinIcon = coinIcon;
    
    UILabel *titleLab = CreateLeftAlignmentLabel(WexFont(14), WexDefault4ATitleColor);
    [self.backView addSubview:titleLab];
    self.titleLab = titleLab;
    
    UILabel *amountLab = CreateLeftAlignmentLabel(WexFont(14), ColorWithHex(0xBAC0C5));
    [self.backView addSubview:amountLab];
    self.amountLab = amountLab;
    
    UILabel *rateLab = CreateLeftAlignmentLabel(WexFont(12), ColorWithHex(0x9B9B9B));
    [self.backView addSubview:rateLab];
    self.rateLab = rateLab;
    
    UILabel *periodLab = CreateLeftAlignmentLabel(WexFont(12), ColorWithHex(0x9B9B9B));
    [self.backView addSubview:periodLab];
    self.periodLab = periodLab;
}

- (void)wex_addConstraints {
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
        make.height.mas_equalTo(kBackViewH + 10).priorityHigh();
    }];
    
    [self.coinIcon mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.left.mas_equalTo(14);
        make.size.mas_equalTo(CGSizeMake(43, 43));
    }];
    
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.coinIcon.mas_top).offset(3);
        make.left.equalTo(self.coinIcon.mas_right).offset(8);
    }];
    
    [self.amountLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.titleLab.mas_bottom).offset(12);
        make.left.equalTo(self.coinIcon.mas_right).offset(8);
    }];
    [self.rateLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-14);
        make.centerY.equalTo(self.titleLab);
    }];
    
    [self.periodLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-14);
        make.centerY.equalTo(self.amountLab);
    }];
    
}

- (void)setLoanHotCoinsModel:(WeXQueryProductByLenderCodeResponseModal_item *)model {
    NSMutableArray *leftPeriodArray = model.loanPeriodList;
    NSString *leftPeriod = nil;
    if (leftPeriodArray.count >= 3) {
        WeXQueryProductByLenderCodeResponseModal_period *periodModel1 = leftPeriodArray[0];
        WeXQueryProductByLenderCodeResponseModal_period *periodModel3 = leftPeriodArray[2];
        NSMutableString *periodStr = [NSMutableString stringWithFormat:@"%@-%@%@",periodModel1.value,periodModel3.value,[WexCommonFunc transferChinesePeriod:periodModel3.unit]];
        leftPeriod = periodStr;
    }
    NSArray *leftValueArray = model.volumeOptionList;
    NSString *maxAmount  = nil;
    NSString *highAmount = nil;
    if (leftValueArray.count >= 3) {
        maxAmount = [NSString stringWithFormat:@"%@ %@%@",WeXLocalizedString(@"最高"),leftValueArray[2],model.currency.symbol];
        highAmount = [NSString stringWithFormat:@"%@%@",leftValueArray[2],model.currency.symbol];
    }
    [self.titleLab setText:model.currency.symbol];
    [self.amountLab setText:maxAmount];
    if ([highAmount length] > 0) {
        NSRange range = [maxAmount rangeOfString:highAmount];
        NSMutableAttributedString *attributeStr = [[NSMutableAttributedString alloc] initWithString:maxAmount];
        NSDictionary *attributes = @{NSFontAttributeName:WexFont(12.0),NSForegroundColorAttributeName:COLOR_NAV_TITLE};
        [attributeStr addAttributes:attributes range:range];
        self.amountLab.attributedText = attributeStr;
    }
    NSString *rate = [NSString stringWithFormat:@"%@:  %.2f%@",@"日利率",[model.loanRate floatValue]*100/365.0,@"%"];
    [self.rateLab setText:rate];
    [self.periodLab setText:leftPeriod];
    [self.coinIcon sd_setImageWithURL:[NSURL URLWithString:model.logoUrl] placeholderImage:WexCoinHolderImage];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
