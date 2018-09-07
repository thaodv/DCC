//
//  WeXLeftIconRightLabelView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXLeftIconRightLabelView.h"

@implementation WeXLeftIconRightLabelView

- (void)wex_loadViews {
    
    UIView *backView = [UIView new];
    backView.layer.cornerRadius = 29;
    backView.clipsToBounds = YES;
    [self addSubview:backView];
    self.backView = backView;
    
    UIImageView *leftIcon = [UIImageView new];
    [self.backView addSubview:leftIcon];
    self.iconImage = leftIcon;
    
    UILabel *titieLab = CreateLeftAlignmentLabel(WexFont(15.0), [UIColor whiteColor]);
    [self.backView addSubview:titieLab];
    self.titleLab = titieLab;
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clickView:)];
    [backView addGestureRecognizer:tapGesture];

}

- (void)wex_layoutConstraints {
    [self.backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(0);
        make.left.mas_equalTo(15);
        make.height.mas_equalTo(58);
        make.right.mas_equalTo(-15);
    }];
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.centerX.mas_equalTo(15);
    }];
    [self.iconImage mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.right.equalTo(self.titleLab.mas_left).offset(-10);
        make.size.mas_equalTo(CGSizeMake(20, 20));
    }];
}
- (void)clickView:(UITapGestureRecognizer *)gesture {
    !self.DidClickView ? : self.DidClickView();
}

- (void)setLeftImageName:(NSString *)imageName
                   title:(NSString *)title {
    [self.iconImage setImage:[UIImage imageNamed:imageName]];
    [self.titleLab setText:title];
}


@end
