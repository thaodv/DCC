//
//  WeXLoginManagerMoreDataCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLoginManagerMoreDataCell.h"

@interface WeXLoginManagerMoreDataCell ()

@property (nonatomic, weak) UILabel *titleLab;

@end

@implementation WeXLoginManagerMoreDataCell

- (void)awakeFromNib {
    [super awakeFromNib];
}
- (void)wex_addSubViews {
    UILabel *titleLab = CreateCenterAlignmentLabel(WexFont(12), ColorWithHex(0xBAC0C5));
    [self.contentView addSubview:titleLab];
    self.titleLab = titleLab;
}

- (void)wex_addConstraints {
    
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(15);
        make.centerY.mas_equalTo(0);
        make.right.mas_equalTo(-15);
    }];
}
- (void)setTitle:(NSString *)title {
    [self.titleLab setText:title];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
