//
//  WeXSelectNodeTableViewCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXSelectNodeTableViewCell.h"

@interface WeXSelectNodeTableViewCell ()

@property (nonatomic, weak) UIImageView *leftMarkImage;
@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) UILabel *subTextLab;
@property (nonatomic, weak) UIImageView *rightDotImage;


@end

@implementation WeXSelectNodeTableViewCell

- (void)wex_addSubViews {
    [super wex_addSubViews];
    UIImageView *leftMarkImage = [UIImageView new];
    [leftMarkImage setHidden:true];
    [self.contentView addSubview:leftMarkImage];
    self.leftMarkImage = leftMarkImage;
    
    UILabel *titleLab = [UILabel new];
    titleLab.textColor = ColorWithHex(0x333333);
    titleLab.font = [UIFont systemFontOfSize:16.0];
    titleLab.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:titleLab];
    self.titleLab = titleLab;
    
    UILabel *subtextLab = [UILabel new];
    subtextLab.textColor = ColorWithHex(0x9B9B9B);
    subtextLab.font = [UIFont systemFontOfSize:16.0];
    subtextLab.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:subtextLab];
    self.subTextLab = subtextLab;
    
    UIImageView *rightDotImage = [UIImageView new];
    rightDotImage.layer.cornerRadius = 7.5;
    rightDotImage.clipsToBounds = true;
    [self.contentView addSubview:rightDotImage];
    self.rightDotImage = rightDotImage;

}
- (void)wex_addConstraints {
    [super wex_addConstraints];
    [self.leftMarkImage mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(20);
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(17, 11));
    }];
    
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(15);
        make.left.equalTo(self.leftMarkImage.mas_right).offset(15);
        make.right.mas_equalTo(-35);
    }];
    
    [self.subTextLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.titleLab.mas_bottom).offset(10);
        make.left.equalTo(self.titleLab.mas_left);
        make.right.equalTo(self.subTextLab.mas_right);
        make.bottom.mas_equalTo(-15);
    }];
    
    [self.rightDotImage mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-15);
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(15, 15));
    }];
}

- (void)setDelayModel:(WeXNetworkCheckModel *)model isSelected:(BOOL)isSelected {
    [self.leftMarkImage setHidden:!isSelected];
    [self.leftMarkImage setImage:[UIImage imageNamed:@"Shape-Selected"]];
    [self.titleLab setText:model.nodeName];
    NSString *subText = model.nodesStatus;
    if (isSelected) {
        subText = [@"已连接" stringByAppendingString:subText];
    }
    [self.subTextLab setText:subText];
    UIColor *notColor;
    switch (model.nodeCheckState) {
        case WexNetworkCheckStateChecking: {
            [self.rightDotImage setHidden:YES];
        }
            break;
        case WexNetworkCheckStateGood: {
            [self.rightDotImage setHidden:NO];
            notColor = ColorWithHex(0x7ED321);
        }
            break;
        case WexNetworkCheckStateCommon: {
            [self.rightDotImage setHidden:NO];
            notColor = ColorWithHex(0xF8E71C);
        }
            break;
        default: {
            [self.rightDotImage setHidden:NO];
            notColor = ColorWithHex(0xED190F);
        }
            break;
    }
    [self.rightDotImage setBackgroundColor:notColor];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
