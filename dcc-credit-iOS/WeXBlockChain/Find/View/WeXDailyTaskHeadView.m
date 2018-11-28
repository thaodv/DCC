//
//  WeXDailyTaskHeadView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDailyTaskHeadView.h"

@interface WeXDailyTaskHeadView ()
@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UILabel *subTitleLab;

@end

@implementation WeXDailyTaskHeadView

- (id)initWithReuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithReuseIdentifier:reuseIdentifier]) {
        [self initUI];
    }
    return self;
}

- (void)initUI {
    _titleLab = CreateLeftAlignmentLabel(WeXPFFont(17), [UIColor blackColor]);
    [self.contentView addSubview:_titleLab];
    
    _subTitleLab = CreateLeftAlignmentLabel(WeXPFFont(14),WexDefault4ATitleColor);
    [self.contentView addSubview:_subTitleLab];
    
    [_titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.centerY.mas_equalTo(0);
        make.width.mas_equalTo(75);
    }];
    
    [_subTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(_titleLab.mas_right).offset(30);
        make.centerY.equalTo(_titleLab);
    }];
}

- (void)setTitle:(NSString *)title subTitle:(NSString *)subTitle {
    [self.titleLab setText:title];
    [self.subTitleLab setText:subTitle];
}

@end
