//
//  WeXHomeTopCardCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXHomeTopCardCell.h"

@interface WeXHomeTopCardCell ()
@property (nonatomic, weak) UILabel *titleLab;
//347 * 203
@property (nonatomic, weak) UIImageView *cardImageView;
@property (nonatomic, weak) UILabel *addressLab;

@end

static CGFloat const kImageRatio =  347.0 / 204.0;


@implementation WeXHomeTopCardCell

- (void)wex_addSubViews {
    UILabel *titleLab = CreateLeftAlignmentLabel(WexFont(17), 0x000000);
    [self.contentView addSubview:titleLab];
    self.titleLab = titleLab;

    UIImageView *cardImageView = [UIImageView new];
    [cardImageView setImage:[UIImage imageNamed:@"digital_card"]];
    [self.contentView addSubview:cardImageView];
    self.cardImageView = cardImageView;
    
    UILabel *addressLab = CreateLeftAlignmentLabel(WexFont(16), ColorWithHex(0xFFFFFF));
    [self.cardImageView addSubview:addressLab];
    self.addressLab = addressLab;
}

- (void)wex_addConstraints {
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.top.mas_equalTo(11);
        make.height.mas_equalTo(20);
    }];
    
    [self.cardImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.right.mas_equalTo(-14);
        make.top.equalTo(self.titleLab.mas_bottom).offset(5);
        make.height.mas_equalTo((kScreenWidth - 2 * 14) / kImageRatio) ;
    }];
    
    [self.addressLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.bottom.mas_equalTo(-15);
    }];
}

- (void)setTitleText:(NSString *)titleText
         addressText:(NSString *)addressText {
    [self.titleLab setText:titleText];
    [self.addressLab setText:addressText];
}

+ (CGFloat)cellHeight {
    return 14 + 20 + 5 + (kScreenWidth - 2 * 14) / kImageRatio + 15;
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
