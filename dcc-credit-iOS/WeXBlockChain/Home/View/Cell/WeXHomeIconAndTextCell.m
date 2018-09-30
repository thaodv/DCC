//
//  WeXHomeIconAndTextCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXHomeIconAndTextCell.h"
#import "WeXLoanGetOrderDetailModal.h"


@interface WeXHomeIconAndTextCell ()
@property (nonatomic, weak) UIImageView *leftIcon;
@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) UILabel *subTextLab;
@property (nonatomic, weak) UILabel *actionLabel;

@end

@implementation WeXHomeIconAndTextCell

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)wex_addSubViews {
    UIImageView *leftIcon = [UIImageView new];
    [self.contentView addSubview:leftIcon];
    self.leftIcon = leftIcon;
    
    UILabel *titleLab = CreateLeftAlignmentLabel(WexFont(14), WexDefault4ATitleColor);
    [self.contentView addSubview:titleLab];
    self.titleLab = titleLab;
    
    UILabel *subTextLab = CreateLeftAlignmentLabel(WexFont(12), ColorWithHex(0xBAC0C5));
    [self.contentView addSubview:subTextLab];
    self.subTextLab = subTextLab;
    
    UILabel *actionLab = CreateCenterAlignmentLabel(WexFont(14),[UIColor whiteColor]);
    actionLab.userInteractionEnabled = true;
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapEvent:)];
    [actionLab addGestureRecognizer:tapGesture];
    actionLab.backgroundColor = ColorWithHex(0xC009FF);
    actionLab.layer.cornerRadius = 12;
    actionLab.clipsToBounds = true;
    [self.contentView addSubview:actionLab];
    self.actionLabel = actionLab;
    
}

- (void)wex_addConstraints {
    [self.leftIcon mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.mas_equalTo(8);
        make.size.mas_equalTo(CGSizeMake(42, 42));
    }];
    
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.leftIcon);
        make.left.equalTo(self.leftIcon.mas_right).offset(14);
        make.right.lessThanOrEqualTo(self.actionLabel.mas_left);
    }];
    
    [self.subTextLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.titleLab.mas_bottom).offset(6);
        make.left.equalTo(self.leftIcon.mas_right).offset(14);
        make.right.lessThanOrEqualTo(self.actionLabel.mas_left);
    }];
    
    [self.actionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.subTextLab);
        make.right.mas_equalTo(-14);
        make.height.mas_equalTo(25);
    }];
}
- (void)setLeftIconName:(NSString *)leftIcon
                  title:(NSString *)title
               subTitle:(NSString *)subTitle
            actionTitle:(NSString *)actionTitle {
    [self.leftIcon setImage:[UIImage imageNamed:leftIcon]];
    [self.titleLab setText:title];
    [self.subTextLab setText:subTitle];
    [self.actionLabel setText:actionTitle];
    [self.actionLabel setHidden:actionTitle.length < 1];
    if (actionTitle.length > 0) {
        [self.actionLabel mas_updateConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(self.subTextLab);
            make.right.mas_equalTo(-14);
            make.height.mas_equalTo(25);
            CGSize titleSize = [actionTitle sizeWithAttributes:@{NSFontAttributeName:WexFont(14)}];
            make.width.mas_equalTo(titleSize.width + 20);
        }];
    }
}
- (void)setLeftIconName:(NSString *)leftIcon
                  title:(NSString *)title
               highText:(NSString *)highText
               subTitle:(NSString *)subTitle
            actionTitle:(NSString *)actionTitle {
    [self setLeftIconName:leftIcon title:title subTitle:subTitle actionTitle:actionTitle];
    if ([highText length] > 0) {
        NSRange range = [title rangeOfString:highText];
        NSMutableAttributedString *attributeStr = [[NSMutableAttributedString alloc] initWithString:title];
        NSDictionary *attributes = @{NSForegroundColorAttributeName:WexRedTextColor,
                                     NSFontAttributeName:WexFont(14.0),
                                     };
        [attributeStr addAttributes:attributes range:range];
        [self.titleLab setAttributedText:attributeStr];
    }
}
- (void)setRepayCoinDataModel:(WeXLoanGetOrderDetailResponseModal *)model
                      iconURL:(NSString *)iconURL {
    [self.leftIcon sd_setImageWithURL:[NSURL URLWithString:iconURL] placeholderImage:WexCoinHolderImage];
    NSString *title = [NSString stringWithFormat:@"%@%@%@",@"应还借币:",model.amount,model.currency.symbol];
    NSString *period = [NSString stringWithFormat:@"%@%@%@后",@"还币日期:",model.borrowDuration,[WexCommonFunc transferChinesePeriod:model.durationUnit]];
    [self.titleLab setText:title];
    [self.actionLabel setText:@"去还币"];
    [self.subTextLab setText:period];
    [self.actionLabel setHidden:NO];
    [self.actionLabel mas_updateConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.subTextLab);
        make.right.mas_equalTo(-14);
        make.height.mas_equalTo(25);
        CGSize titleSize = [@"去还币" sizeWithAttributes:@{NSFontAttributeName:WexFont(14)}];
        make.width.mas_equalTo(titleSize.width + 20);
    }];
}

- (void)tapEvent:(UITapGestureRecognizer *)gesture {
    !self.DidClickEvent ? : self.DidClickEvent();
}

+ (CGFloat)cellHeight {
    return 8 + 42 + 10;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
