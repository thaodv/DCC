//
//  WeXFindIListItemCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXFindIListItemCell.h"
#import "WeXLastPKResModel.h"
#import "NSString+WexTool.h"
#import "WeXFindAwardModel.h"

#define KHighTextColor ColorWithHex(0x7B40FF )

@interface WeXFindIListItemCell  ()

@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UIImageView *iconImageView;
@property (nonatomic, strong) UILabel *subTitleLab;
@property (nonatomic, strong) UILabel *subDescribeLab;
@property (nonatomic, strong) UIImageView *notiIcon;
@property (nonatomic, strong) UILabel *notiTitleLab;
@property (nonatomic, strong) UILabel *notiTimeLab;

@end

@implementation WeXFindIListItemCell

- (void)wex_addSubViews {
    _titleLab = CreateLeftAlignmentLabel(WeXPFFont(17), [UIColor blackColor]);
    [self.contentView addSubview:_titleLab];
    
    _iconImageView = [UIImageView new];
    [self.contentView addSubview:_iconImageView];
    
    _subTitleLab = CreateLeftAlignmentLabel(WeXPFFont(14), WexDefault4ATitleColor);
    [self.contentView addSubview:_subTitleLab];
    
    _subDescribeLab = CreateLeftAlignmentLabel(WeXPFFont(12), ColorWithHex(0xBAC0C5));
    [self.contentView addSubview:_subDescribeLab];
    
    _notiIcon = [UIImageView new];
    [_notiIcon setHidden:true];
    _notiIcon.image = [UIImage imageNamed:@"garden_volume"];
    [self.contentView addSubview:_notiIcon];
    
    _notiTitleLab = CreateLeftAlignmentLabel(WeXPFFont(11), WexDefault4ATitleColor);
    [self.contentView addSubview:_notiTitleLab];
    
    _notiTimeLab = CreateLeftAlignmentLabel(WeXPFFont(11), ColorWithHex(0xBAC0C5));
    [self.contentView addSubview:_notiTimeLab];
    
}

- (void)wex_addConstraints {
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.mas_equalTo(14).priorityHigh();
    }];
    [self.iconImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.titleLab);
        make.top.equalTo(self.titleLab.mas_bottom).offset(20);
        make.size.mas_equalTo(CGSizeMake(42, 42));
    }];
    [self.subTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.iconImageView.mas_right).offset(14);
        make.top.equalTo(self.iconImageView);
    }];
    [self.subDescribeLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.subTitleLab);
        make.top.equalTo(self.subTitleLab.mas_bottom).offset(5);
    }];
    [self.notiIcon mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.equalTo(self.iconImageView.mas_bottom).offset(20);
        make.bottom.mas_equalTo(-14);
        make.size.mas_equalTo(CGSizeMake(12, 13));
    }];
    [self.notiTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.notiIcon.mas_right).offset(5);
        make.centerY.equalTo(self.notiIcon);
    }];
    [self.notiTimeLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.notiTitleLab.mas_right).offset(10);
        make.centerY.equalTo(self.notiTitleLab);
    }];
    
}

- (void)setTitle:(NSString *)title
       imageName:(NSString *)imageName
        subTitle:(NSString *)subTitle
          subDes:(NSString *)subDesc {
    [self.titleLab setText:title];
    [self.subTitleLab setText:subTitle];
    [self.subDescribeLab setText:subDesc];
    [self.iconImageView setImage:[UIImage imageNamed:imageName]];

}

- (void)setNotiTitle:(NSString *)notiTitle
        notiHighText:(NSString *)highText
            notiTime:(NSString *)notiTime {
    if ([highText length] > 0) {
        NSRange range = [notiTitle rangeOfString:highText];
        NSMutableAttributedString *attributeStr = [[NSMutableAttributedString alloc] initWithString:notiTitle];
        NSDictionary *attributes = @{NSForegroundColorAttributeName:KHighTextColor};
        [attributeStr addAttributes:attributes range:range];
        [self.notiTitleLab setAttributedText:attributeStr];
    }
    else {
        [self.notiTitleLab setText:notiTitle];
    }
    [self.notiTimeLab setText:notiTime];
}

- (void)setLastPkResModel:(WeXLastPKResModel *)model {
    if (model) {
        [_notiIcon setHidden:false];
        NSString *highText  = [NSString stringWithFormat:@"获得+%@阳光",model.amount];
        NSString *notiTitle = [NSString stringWithFormat:@"%@ %@",model.nickName,highText];
        NSString *time = [model.lastUpdatedTime transferToNotiTime];
        [self setNotiTitle:notiTitle notiHighText:highText notiTime:time];
    } else {
        [_notiIcon setHidden:true];
        [self setNotiTitle:nil notiHighText:nil notiTime:nil];
    }
}
- (void)setSunValueModel:(WeXFindAwardResDetailModel *)model {
    if (model) {
        [_notiIcon setHidden:false];
        [self setNotiTitle:@"有奖励未领取哦" notiHighText:nil notiTime:[model.createdTime  transferToNotiTime]];
    } else {
        [_notiIcon setHidden:false];
        [self setNotiTitle:@"您的奖励将于3小时后发放" notiHighText:nil notiTime:nil];
    }
}


- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
