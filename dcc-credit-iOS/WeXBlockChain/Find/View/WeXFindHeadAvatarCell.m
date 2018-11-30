//
//  WeXFindHeadAvatarCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXFindHeadAvatarCell.h"

@interface WeXFindHeadAvatarCell ()

@property (nonatomic, strong) UIImageView *headImageView;
@property (nonatomic, strong) UILabel *nickNameLab;

@end


@implementation WeXFindHeadAvatarCell

- (void)wex_addSubViews {
    _headImageView = [UIImageView new];
    _headImageView.layer.cornerRadius = 24;
    _headImageView.clipsToBounds = true;
    [self.contentView addSubview:_headImageView];
    
    _nickNameLab = CreateLeftAlignmentLabel(WeXPFFont(18), WexDefault4ATitleColor);
    [self.contentView addSubview:_nickNameLab];

}

- (void)wex_addConstraints {
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
        make.height.mas_equalTo(82).priorityHigh();
    }];
    
    [self.headImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.mas_equalTo(24);
        make.size.mas_equalTo(CGSizeMake(48, 48));
    }];
    [self.nickNameLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.headImageView);
        make.left.equalTo(self.headImageView.mas_right).offset(15);
    }];
}


- (void)setAvatarImage:(UIImage *)avatarImage
              nickName:(NSString *)nickName {
    [self.headImageView setImage:avatarImage];
    [self.nickNameLab setText:nickName];
}
- (void)setCacheModel:(WeXPasswordCacheModal *)cacheModel {
    [self.headImageView sd_setImageWithURL:[NSURL URLWithString:cacheModel.portrait] placeholderImage:[UIImage imageNamed:@"Fill 9"]];
    [self.nickNameLab setText:cacheModel.nickName];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
