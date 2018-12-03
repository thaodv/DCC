//
//  WeXSunRecordDetailCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/30.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXSunRecordDetailCell.h"
#import "WeXSunBlanceModel.h"
#import "NSString+WexTool.h"


@interface WeXSunRecordDetailCell ()

@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UILabel *timeLab;
@property (nonatomic, strong) UILabel *valueLab;

@end

@implementation WeXSunRecordDetailCell

- (void)wex_addSubViews {
    [super wex_addSubViews];
    _titleLab = CreateLeftAlignmentLabel(WeXPFFont(16), WexDefault4ATitleColor);
    [self.contentView addSubview:_titleLab];
    
    _timeLab = CreateLeftAlignmentLabel(WeXPFFont(13), ColorWithHex(0xBAC0C5));
    [self.contentView addSubview:_timeLab];
    
    _valueLab = CreateRightAlignmentLabel(WeXPFFont(14), ColorWithHex(0x7B40FF));
    [self.contentView addSubview:_valueLab];
}

- (void)wex_addConstraints {
    [super wex_addConstraints];
//    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.edges.mas_equalTo(0);
//        make.height.mas_equalTo(66).priorityHigh();
//        make.width.equalTo(self.contentView.superview);
//    }];
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.mas_equalTo(14);
    }];
    [self.timeLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.equalTo(self.titleLab.mas_bottom).offset(6);
        make.bottom.mas_equalTo(-14);
    }];
    [self.valueLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-14);
        make.centerY.mas_equalTo(0);
    }];
}


- (void)setTitle:(NSString *)title
            time:(NSString *)time
           value:(NSString *)value {
    [self.titleLab setText:title];
    [self.timeLab setText:time];
    [self.valueLab setText:value];
}
- (void)setSunBalanceListModel:(WeXSunBlanceModel *)model {
    NSString *symbol = [model.direction isEqualToString:@"PLUS"] ? @"+" : @"-";
    NSString *value = [NSString stringWithFormat:@"%@%@%@",symbol,model.amount,WeXLocalizedString(@"阳光")];
    NSString *time = [model.lastUpdatedTime yearToSecondTimeString];
    time = time ? time : @" ";
    [self setTitle:model.memo time:time value:value];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
