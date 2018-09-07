//
//  WeXPassportIDCommitSuccessController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDCommitSuccessController.h"
#import "WeXPassportIDViewController.h"

@interface WeXPassportIDCommitSuccessController ()

@end

@implementation WeXPassportIDCommitSuccessController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.hidesBackButton = YES;
    [self setupSubViews];
}




//初始化滚动视图
-(void)setupSubViews{
    
    UIImageView *faceImageView = [[UIImageView alloc] init];
    faceImageView.image = [UIImage imageNamed:@"digital_success"];
    [self.view addSubview:faceImageView];
    [faceImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.view).offset(-70);
        make.centerX.equalTo(self.view);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"实名认证提交成功";
    titleLabel.font = [UIFont systemFontOfSize:16];
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(faceImageView.mas_bottom).offset(10);
        make.leading.trailing.equalTo(self.view);
        make.height.equalTo(@25);
    }];
    
    WeXCustomButton *backBtn = [WeXCustomButton button];
    [backBtn setTitle:@"返回BitExpress钱包" forState:UIControlStateNormal];
    backBtn.layer.cornerRadius = 5;
    backBtn.layer.masksToBounds = YES;
    [backBtn setTitleColor:ColorWithRGB(248, 31, 117) forState:UIControlStateNormal];
    [backBtn addTarget:self action:@selector(backBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:backBtn];
    [backBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@170);
        make.height.equalTo(@50);
    }];
    
}

- (void)backBtnClick{
    for (UIViewController *ctrl in self.navigationController.viewControllers) {
        if ([ctrl isKindOfClass:[WeXPassportIDViewController class]]) {
            [self.navigationController popToViewController:ctrl animated:YES];
        }
    }
}

@end
