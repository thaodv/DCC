//
//  WeXHomeIndicatorCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXHomeIndicatorCell.h"
@interface WeXHomeIndicatorCell ()
@property (nonatomic, weak) UILabel *leftLab;
@property (nonatomic, weak) UILabel *rightLab;

@end

@implementation WeXHomeIndicatorCell

- (void)wex_addSubViews {
    UILabel *leftLab = CreateLeftAlignmentLabel(WexFont(17.0), ColorWithHex(0x000000));
    [self.contentView addSubview:leftLab];
    self.leftLab = leftLab;
    
    UILabel *rightLab = CreateRightAlignmentLabel(WexFont(15.0), ColorWithHex(0xC009FF));
    [self.contentView addSubview:rightLab];
    self.rightLab = rightLab;
}

- (void)wex_addConstraints {
    
    [self.leftLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(11);
        make.left.mas_equalTo(14);
        make.right.lessThanOrEqualTo(self.rightLab.mas_left).offset(10);
        make.height.mas_equalTo(20);
    }];
    
    [self.rightLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.self.leftLab);
        make.right.mas_equalTo(-10);
    }];
}

- (void)setLeftTitle:(NSString *)leftTitle rightTitle:(NSString *)rightTitle {
    [self.leftLab  setText:leftTitle];
    [self.rightLab setText:rightTitle];
}

+ (CGFloat)cellHeight {
    return 42;
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
