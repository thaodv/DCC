//
//  WeXPassportIDCommitController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDCommitController.h"
#import "WeXPassportIDCommitSuccessController.h"

@interface WeXPassportIDCommitController ()

@end

@implementation WeXPassportIDCommitController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"数字身份证";
    titleLabel.font = [UIFont systemFontOfSize:25];
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@25);
    }];
    
    UILabel *label1 = [[UILabel alloc] init];
    label1.text = @"①拍摄身份证-②信息确认-③采集头像-④完成";
    label1.font = [UIFont systemFontOfSize:14];
    label1.textColor = [UIColor lightGrayColor];
    label1.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(@20);
    }];
    
    //    UILabel *label2 = [[UILabel alloc] init];
    //    label2.text = @"请拍摄你本人的身份证照片";
    //    label2.font = [UIFont systemFontOfSize:14];
    //    label2.textColor = [UIColor lightGrayColor];
    //    label2.textAlignment = NSTextAlignmentLeft;
    //    [self.view addSubview:label2];
    //    [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
    //        make.top.equalTo(label1.mas_bottom).offset(10);
    //        make.leading.equalTo(self.view).offset(20);
    //        make.height.equalTo(@20);
    //    }];
    
    UIImageView *faceImageView = [[UIImageView alloc] init];
    faceImageView.layer.cornerRadius = 5;
    faceImageView.layer.masksToBounds = YES;
    faceImageView.layer.borderWidth = 1;
    faceImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    faceImageView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:faceImageView];
    [faceImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label1.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(30);
        make.trailing.equalTo(self.view).offset(-30);
        make.height.equalTo(faceImageView.mas_width).multipliedBy(1);
    }];
    
    WeXCustomButton *commitBtn = [WeXCustomButton button];
    [commitBtn setTitle:@"提交验证" forState:UIControlStateNormal];
    commitBtn.layer.cornerRadius = 5;
    commitBtn.layer.masksToBounds = YES;
    [commitBtn setTitleColor:ColorWithRGB(248, 31, 117) forState:UIControlStateNormal];
    [commitBtn addTarget:self action:@selector(commitBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:commitBtn];
    [commitBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@170);
        make.height.equalTo(@50);
    }];
    
}

- (void)commitBtnClick{
    WeXPassportIDCommitSuccessController *ctrl = [[WeXPassportIDCommitSuccessController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

@end
