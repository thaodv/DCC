//
//  WeXCPNoRecordCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPNoRecordCell.h"

@interface WeXCPNoRecordCell ()
@property (nonatomic, weak) UIImageView *contentImage;
@property (nonatomic, weak) UILabel *subTextLab;

@end

@implementation WeXCPNoRecordCell

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}
- (void)wex_addSubViews {
    
    UIImageView *imageView = [UIImageView new];
    imageView.image = [UIImage imageNamed:@"Wex_Coin_NoRecord"];
    [self.contentView addSubview:imageView];
    self.contentImage = imageView;
    
    UILabel *subTextLab = CreateCenterAlignmentLabel(WexFont(16), WexDefault4ATitleColor);
    [self.contentView addSubview:subTextLab];
    self.subTextLab = subTextLab;
}

- (void)wex_addConstraints {
    [self.contentImage mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(0);
        make.centerY.equalTo(self.contentView.mas_centerY).offset(-40);
        make.size.mas_equalTo(CGSizeMake(114, 120));
    }];
    
    [self.subTextLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentImage.mas_bottom).offset(30);
        make.centerX.mas_equalTo(0);
    }];
}


- (void)setImageName:(NSString *)imageName
             subText:(NSString *)subText {
    UIImage *image = [UIImage imageNamed:imageName];
    CGFloat kRatio = image.size.width / image.size.height;
    CGFloat kImageW = 114;
    CGFloat kImageH = kImageW / kRatio;
    [self.contentImage mas_updateConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(0);
        make.centerY.equalTo(self.contentView.mas_centerY).offset(-40);
        make.size.mas_equalTo(CGSizeMake(kImageW, kImageH));
    }];
    
    [self.subTextLab setText:subText];
    [self.subTextLab mas_updateConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentImage.mas_bottom).offset(30);
        make.centerX.mas_equalTo(0);
    }];
    
}

@end
