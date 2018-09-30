//
//  WeXTokenPlusCardCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXTokenPlusCardCell.h"

@interface WeXTokenPlusCardCell ()

@property (nonatomic, weak) UIImageView *cardImageView;

@end

@implementation WeXTokenPlusCardCell

static CGFloat const kImageRatio = 345.0 / 377.0;


- (void)wex_addSubViews {
    UIImageView *cardImageView = [UIImageView new];
    cardImageView.image = [UIImage imageNamed:@"tokenPlus_bannert"];
    [self.contentView addSubview:cardImageView];
    self.cardImageView = cardImageView;
}
- (void)wex_addConstraints {
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
        make.height.mas_equalTo( (kScreenWidth - 14 * 2) / kImageRatio);
    }];
    [self.cardImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.mas_equalTo(0);
        make.left.mas_equalTo(14);
        make.right.mas_equalTo(-14);
    }];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

}

@end
