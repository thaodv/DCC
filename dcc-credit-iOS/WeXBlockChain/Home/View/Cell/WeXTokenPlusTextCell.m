//
//  WeXTokenPlusTextCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXTokenPlusTextCell.h"

@interface WeXTokenPlusTextCell ()

@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) UILabel *subTextLab;

@end

@implementation WeXTokenPlusTextCell

- (void)wex_addSubViews {
    UILabel *titleLab = CreateLeftAlignmentLabel(WexFont(16),ColorWithHex(0x000000));
    titleLab.numberOfLines = 0;
    [self.contentView addSubview:titleLab];
    self.titleLab = titleLab;
    
    UILabel *subTextLab = CreateLeftAlignmentLabel(WexFont(15.0),WexDefault4ATitleColor);
    subTextLab.numberOfLines = 0;
    [self.contentView addSubview:subTextLab];
    self.subTextLab = subTextLab;
}
- (void)wex_addConstraints {
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.mas_equalTo(15);
        make.right.mas_equalTo(-14);
        make.height.mas_equalTo(20);
    }];
    
    [self.subTextLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.titleLab.mas_bottom).offset(10);
        make.left.mas_equalTo(14);
        make.right.mas_equalTo(-14);
        make.bottom.mas_equalTo(-15);
    }];
}
- (void)setTitle:(NSString *)title
         subText:(NSString *)subText {
    [self.titleLab setText:title];
    [self.subTextLab setText:subText];
    [self.subTextLab sizeToFit];
    [self layoutIfNeeded];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
