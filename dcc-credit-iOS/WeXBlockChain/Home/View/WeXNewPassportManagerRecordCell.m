//
//  WeXNewPassportManagerRecordCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXNewPassportManagerRecordCell.h"
#import "WeXPassportManagerRLMModel.h"

@interface WeXNewPassportManagerRecordCell ()
@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) UILabel *subTextLab;

@end

@implementation WeXNewPassportManagerRecordCell

- (void)awakeFromNib {
    [super awakeFromNib];
}
- (void)wex_addSubViews {
    [super wex_addSubViews];
    UILabel *titleLab =CreateLeftAlignmentLabel(WexFont(14.0), COLOR_LABEL_DESCRIPTION);
    [self.contentView addSubview:titleLab];
    self.titleLab = titleLab;
    
    UILabel *subTextLab = CreateRightAlignmentLabel(WexFont(13.0), COLOR_LABEL_DESCRIPTION);
    [self.contentView addSubview:subTextLab];
    self.subTextLab = subTextLab;
    
}
- (void)wex_addConstraints {
    [super wex_addConstraints];
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.centerY.mas_equalTo(0);
    }];
    
    [self.subTextLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-10);
        make.centerY.mas_equalTo(0);
    }];
}

- (void)setManagerModel:(WeXPassportManagerRLMModel *)model {
    self.titleLab.text = WeXLocalizedString(model.type);
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
    NSString *dateStr = [formatter stringFromDate:model.date];
    self.subTextLab.text = dateStr;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

}

@end
