//
//  WeXCPMarketTopAmountCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPMarketTopAmountCell.h"
#import "UIView+WeXCorner.h"


@interface WeXCPMarketTopAmountCell ()

@property (nonatomic, weak) UIView *backView;
@property (nonatomic, weak) UILabel *principalTitleLab;
@property (nonatomic, weak) UILabel *principalAmountLab;
@property (nonatomic, weak) UILabel *profitTitleLab;
@property (nonatomic, weak) UILabel *profitNumLab;
@property (nonatomic, weak) UIImageView *arrowImage;

@end

static CGFloat const kBackViewH = 105;


@implementation WeXCPMarketTopAmountCell

- (void)wex_addSubViews {
    [self.contentView setBackgroundColor:WexSepratorLineColor];
    UIView *backView = [[UIView alloc] initWithFrame:CGRectMake(14, 14, kScreenWidth - 2 * 14, kBackViewH)];
    [backView addCornerRadius:8.0];
    [self.contentView addSubview:backView];
    self.backView = backView;
    
    UILabel *principalTitleLab = CreateLeftAlignmentLabel(WexFont(16), [UIColor blackColor]);
    [self.backView addSubview:principalTitleLab];
    self.principalTitleLab = principalTitleLab;
    
    UILabel *principalAmountLab = CreateLeftAlignmentLabel(WexFont(18), WexPurpleTextColor);
    [self.backView addSubview:principalAmountLab];
    self.principalAmountLab = principalAmountLab;
    
    UILabel *profitTitleLab = CreateLeftAlignmentLabel(WexFont(16), [UIColor blackColor]);
    [self.backView addSubview:profitTitleLab];
    self.profitTitleLab = profitTitleLab;
    
    UILabel *profitNumLab = CreateLeftAlignmentLabel(WexFont(18), WexPurpleTextColor);
    [self.backView addSubview:profitNumLab];
    self.profitNumLab = profitNumLab;
    
    UIImageView *arrowIamge = [UIImageView new];
    arrowIamge.image = [UIImage imageNamed:@"wex_arrow"];
    [self.backView addSubview:arrowIamge];
    self.arrowImage = arrowIamge;
}

- (void)setPrincipal:(NSString *)principal
              profit:(NSString *)profit {
    [self.principalTitleLab setText:@"在投本金（￥）"];
    [self.principalAmountLab setText:principal];
    [self.profitTitleLab setText:@"预期收益（￥）"];
    [self.profitNumLab setText:profit];
    
}

- (void)wex_addConstraints {
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
        make.height.mas_equalTo(kBackViewH + 2 * 14).priorityHigh();
    }];
    
    [self.principalTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.mas_equalTo(16);
        make.right.equalTo(self.backView.mas_centerX).offset(-10);
    }];
    
    [self.principalAmountLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.equalTo(self.principalTitleLab.mas_bottom).offset(5);
        make.right.equalTo(self.backView.mas_centerX).offset(-10);
    }];
    
    [self.profitTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.backView.mas_centerX).offset(10);
        make.top.mas_equalTo(16);
        make.right.mas_equalTo(-10);
    }];
    
    [self.profitNumLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.backView.mas_centerX).offset(10);
        make.top.equalTo(self.profitTitleLab.mas_bottom).offset(5);
        make.right.mas_equalTo(-10);
    }];
    
    [self.arrowImage mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.right.mas_equalTo(-10);
        make.size.mas_equalTo(CGSizeMake(10, 14));
    }];
}

- (void)setRightArrowHide:(BOOL)isHidden {
    [self.arrowImage setHidden:isHidden];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
