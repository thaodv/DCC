//
//  WeXHomeLoanCoinCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXHomeLoanCoinCell.h"
#import "WeXQueryProductByLenderCodeModal.h"

@interface WeXHomeLoanCoinCell ()
@property (nonatomic, weak) UIImageView *leftIcon;
@property (nonatomic, weak) UILabel *leftTitleLab;
@property (nonatomic, weak) UILabel *leftSubtitleLab;

@property (nonatomic, weak) UIImageView *rightIcon;
@property (nonatomic, weak) UILabel *rightTitleLab;
@property (nonatomic, weak) UILabel *rightSubtitleLab;


@end

@implementation WeXHomeLoanCoinCell

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)wex_addSubViews {
    UIImageView *leftIcon = [UIImageView new];
    leftIcon.layer.cornerRadius = 6;
    leftIcon.clipsToBounds = true;
    [self.contentView addSubview:leftIcon];
    self.leftIcon = leftIcon;
    
    UILabel *leftTitleLab = CreateLeftAlignmentLabel(WexFont(13), ColorWithHex(0xBAC0C5));
    [self.contentView addSubview:leftTitleLab];
    self.leftTitleLab = leftTitleLab;
    
    UILabel *leftSubtitleLab = CreateLeftAlignmentLabel(WexFont(13), ColorWithHex(0xBAC0C5));
    [self.contentView addSubview:leftSubtitleLab];
    self.leftSubtitleLab = leftSubtitleLab;
    
    UIImageView *rightIcon = [UIImageView new];
    rightIcon.layer.cornerRadius = 6;
    rightIcon.clipsToBounds = true;
    [self.contentView addSubview:rightIcon];
    self.rightIcon = rightIcon;
    
    UILabel *rightTitleLab = CreateLeftAlignmentLabel(WexFont(13), ColorWithHex(0xBAC0C5));
    [self.contentView addSubview:rightTitleLab];
    self.rightTitleLab = rightTitleLab;
    
    UILabel *rightSubtitleLab = CreateLeftAlignmentLabel(WexFont(13), ColorWithHex(0xBAC0C5));
    [self.contentView addSubview:rightSubtitleLab];
    self.rightSubtitleLab = rightSubtitleLab;
    
}

- (void)wex_addConstraints {
    [self.leftIcon mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.mas_equalTo(10);
        make.size.mas_equalTo(CGSizeMake(43, 43));
    }];
    
    [self.leftTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.leftIcon);
        make.left.equalTo(self.leftIcon.mas_right).offset(13);
        make.right.lessThanOrEqualTo(self.contentView.mas_centerX);
    }];
    [self.leftSubtitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.leftTitleLab.mas_bottom).offset(10);
        make.left.equalTo(self.leftIcon.mas_right).offset(13);
        make.right.lessThanOrEqualTo(self.contentView.mas_centerX);
    }];
    
    [self.rightIcon mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.contentView.mas_centerX).offset(14);
        make.top.mas_equalTo(10);
        make.size.mas_equalTo(CGSizeMake(43, 43));
    }];
    
    [self.rightTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.rightIcon);
        make.left.equalTo(self.rightIcon.mas_right).offset(13);
        make.right.mas_lessThanOrEqualTo(0);
    }];
    [self.rightSubtitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.rightTitleLab.mas_bottom).offset(10);
        make.left.equalTo(self.rightIcon.mas_right).offset(13);
        make.right.mas_lessThanOrEqualTo(0);
    }];
}

+ (CGFloat)cellHeight {
    return 10 + 43 + 10;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}
- (void)setLeftModel:(WeXQueryProductByLenderCodeResponseModal_item *)leftModel
          rightModel:(WeXQueryProductByLenderCodeResponseModal_item *)rightModel {
    
    NSMutableArray *leftPeriodArray = leftModel.loanPeriodList;
    if (leftPeriodArray.count >= 3) {
        WeXQueryProductByLenderCodeResponseModal_period *periodModel1 = leftPeriodArray[0];
        WeXQueryProductByLenderCodeResponseModal_period *periodModel3 = leftPeriodArray[2];
        NSMutableString *periodStr = [NSMutableString stringWithFormat:@"%@-%@%@",periodModel1.value,periodModel3.value,[WexCommonFunc transferChinesePeriod:periodModel3.unit]];
        _leftSubtitleLab.text = [NSString stringWithFormat:@"%@:%@",WeXLocalizedString(@"周期"),periodStr];
    }
    NSArray *leftValueArray = leftModel.volumeOptionList;
    if (leftValueArray.count >= 3) {
        _leftTitleLab.text = [NSString stringWithFormat:@"%@:%@-%@%@",WeXLocalizedString(@"额度"),leftValueArray[0],leftValueArray[2],leftModel.currency.symbol];
    }
    [self.leftIcon sd_setImageWithURL: [NSURL URLWithString:leftModel.logoUrl] placeholderImage:[UIImage imageNamed:@"ethereum"]];
    
    NSMutableArray *rightPeriodArray = rightModel.loanPeriodList;
    if (rightPeriodArray.count >= 3) {
        WeXQueryProductByLenderCodeResponseModal_period *periodModel1 = rightPeriodArray[0];
        WeXQueryProductByLenderCodeResponseModal_period *periodModel3 = rightPeriodArray[2];
        NSMutableString *periodStr = [NSMutableString stringWithFormat:@"%@-%@%@",periodModel1.value,periodModel3.value,[WexCommonFunc transferChinesePeriod:periodModel3.unit]];
        _rightSubtitleLab.text = [NSString stringWithFormat:@"%@:%@",WeXLocalizedString(@"周期"),periodStr];
    }
    NSArray *rightValueArray = rightModel.volumeOptionList;
    if (rightValueArray.count >= 3) {
        _rightTitleLab.text = [NSString stringWithFormat:@"%@:%@-%@%@",WeXLocalizedString(@"额度"),rightValueArray[0],rightValueArray[2],rightModel.currency.symbol];
    }
    [self.rightIcon sd_setImageWithURL: [NSURL URLWithString:rightModel.logoUrl] placeholderImage:[UIImage imageNamed:@"ethereum"]];
    
    [self.rightIcon setHidden:!rightModel];
    [self.rightTitleLab setHidden:!rightModel];
    [self.rightSubtitleLab setHidden:!rightModel];
}

@end
