//
//  WeXSunRecordTopCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/30.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXSunRecordTopCell.h"

@interface WeXSunRecordTopCell ()

@property (nonatomic, strong) UIImageView *sunImageView;
@property (nonatomic, strong) UILabel *sunLab;
@property (nonatomic, strong) UILabel *valueLab;


@end

@implementation WeXSunRecordTopCell

- (void)wex_addSubViews {
    _sunImageView = [UIImageView new];
    _sunImageView.image = [UIImage imageNamed:@"Sun"];
    [self.contentView addSubview:_sunImageView];
    
    _sunLab = CreateLeftAlignmentLabel(WeXPFFont(16), [UIColor whiteColor]);
    [self.contentView addSubview:_sunLab];
    
    _valueLab = CreateLeftAlignmentLabel(WeXPFFont(16), [UIColor whiteColor]);
    [self.contentView addSubview:_valueLab];
    
    [self.contentView setBackgroundColor:ColorWithHex(0xFC790C)];
}

- (void)wex_addConstraints {
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
        make.height.mas_equalTo(105).priorityHigh();
    }];
    self.sunImageView.mas_key = @"sunImageView";
    [self.sunImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.centerX.mas_equalTo(-24);
        make.size.mas_equalTo(CGSizeMake(38, 38));
    }];
    self.sunLab.mas_key = @"sunLab";
    [self.sunLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.sunImageView).offset(-5);
        make.left.equalTo(self.sunImageView.mas_right).offset(10);
    }];
    self.valueLab.mas_key = @"valueLab";
    [self.valueLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.sunLab);
        make.bottom.equalTo(self.sunImageView).offset(8);
    }];
}

- (void)setSunValue:(NSString *)value {
    [self.sunLab setText:@"阳光"];
    [self.valueLab setText:value];
}


- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

}

@end
