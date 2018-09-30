//
//  WeXHomeLoanCoinCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXHomeLoanCoinCell.h"
#import "WeXQueryProductByLenderCodeModal.h"
#import "WeXLoanCoinView.h"

@interface WeXHomeLoanCoinCell ()

@property (nonatomic, weak) WeXLoanCoinView *leftCoinView;
@property (nonatomic, weak) WeXLoanCoinView *rightCoinView;
//@property (nonatomic, weak) UIImageView *leftIcon;
//@property (nonatomic, weak) UILabel *leftTitleLab;
//@property (nonatomic, weak) UILabel *leftSubtitleLab;
//
//@property (nonatomic, weak) UIImageView *rightIcon;
//@property (nonatomic, weak) UILabel *rightTitleLab;
//@property (nonatomic, weak) UILabel *rightSubtitleLab;


@end
static NSInteger const kStartTag = 10;


@implementation WeXHomeLoanCoinCell

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)wex_addSubViews {
    WeXLoanCoinView *leftCoinView = [WeXLoanCoinView new];
    leftCoinView.tag = kStartTag;
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(leftCoinViewClick:)];
    [leftCoinView addGestureRecognizer:tapGesture];
    [self.contentView addSubview:leftCoinView];
    self.leftCoinView = leftCoinView;
    
    WeXLoanCoinView *rightCoinView = [WeXLoanCoinView new];
    rightCoinView.tag = kStartTag + 1;
    UITapGestureRecognizer *rightTapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(rightCoinViewClick:)];
    [rightCoinView addGestureRecognizer:rightTapGesture];
    
    [self.contentView addSubview:rightCoinView];
    self.rightCoinView = rightCoinView;
    
}

- (void)wex_addConstraints {
    
    [self.leftCoinView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(0);
        make.height.mas_equalTo(43);
        make.right.equalTo(self.contentView.mas_centerX);
    }];
    
    [self.rightCoinView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.contentView.mas_centerX).offset(14);
        make.height.mas_equalTo(43);
        make.right.mas_equalTo(0);
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
    NSString *leftPeriod = nil;
    if (leftPeriodArray.count >= 3) {
        WeXQueryProductByLenderCodeResponseModal_period *periodModel1 = leftPeriodArray[0];
        WeXQueryProductByLenderCodeResponseModal_period *periodModel3 = leftPeriodArray[2];
        NSMutableString *periodStr = [NSMutableString stringWithFormat:@"%@-%@%@",periodModel1.value,periodModel3.value,[WexCommonFunc transferChinesePeriod:periodModel3.unit]];
        leftPeriod = [NSString stringWithFormat:@"%@:%@",WeXLocalizedString(@"周期"),periodStr];
    }
    NSArray *leftValueArray = leftModel.volumeOptionList;
    NSString *leftAmount = nil;
    if (leftValueArray.count >= 3) {
       leftAmount = [NSString stringWithFormat:@"%@:%@-%@%@",WeXLocalizedString(@"额度"),leftValueArray[0],leftValueArray[2],leftModel.currency.symbol];
    }
    [self.leftCoinView setIconURL:leftModel.logoUrl amount:leftAmount period:leftPeriod];
    
    NSMutableArray *rightPeriodArray = rightModel.loanPeriodList;
    NSString *rightPeriod = nil;
    if (rightPeriodArray.count >= 3) {
        WeXQueryProductByLenderCodeResponseModal_period *periodModel1 = rightPeriodArray[0];
        WeXQueryProductByLenderCodeResponseModal_period *periodModel3 = rightPeriodArray[2];
        NSMutableString *periodStr = [NSMutableString stringWithFormat:@"%@-%@%@",periodModel1.value,periodModel3.value,[WexCommonFunc transferChinesePeriod:periodModel3.unit]];
        rightPeriod = [NSString stringWithFormat:@"%@:%@",WeXLocalizedString(@"周期"),periodStr];
    }
    NSArray *rightValueArray = rightModel.volumeOptionList;
    NSString *rightAmount = nil;
    if (rightValueArray.count >= 3) {
        rightAmount = [NSString stringWithFormat:@"%@:%@-%@%@",WeXLocalizedString(@"额度"),rightValueArray[0],rightValueArray[2],rightModel.currency.symbol];
    }
    [self.rightCoinView setIconURL:rightModel.logoUrl amount:rightAmount period:rightPeriod];
    [self.rightCoinView setHidden:!rightModel];
}
- (void)rightCoinViewClick:(UITapGestureRecognizer *)gesture {
    !self.DidClickCell ? : self.DidClickCell(WexLoanCoinCellClickTypeRight);
}
- (void)leftCoinViewClick:(UITapGestureRecognizer *)gesture {
    !self.DidClickCell ? : self.DidClickCell(WexLoanCoinCellClickTypeLeft);
}


@end
