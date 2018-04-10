//
//  WeXPassportIDAuthenSuccessController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDAuthenSuccessController.h"
#import "WeXBackupBigQRView.h"

@interface WeXPassportIDAuthenSuccessController ()

@end

@implementation WeXPassportIDAuthenSuccessController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupNavgationType];
    [self setupSubViews];
}

- (void)setupNavgationType{
    self.navigationItem.hidesBackButton = YES;
    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"digital_cha1"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
    self.navigationItem.rightBarButtonItem = rihgtItem;
    
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"实名信息";
    titleLabel.font = [UIFont systemFontOfSize:25];
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@25);
    }];
    
    UIImageView *backImageView = [[UIImageView alloc] init];
    backImageView.layer.cornerRadius = 5;
    backImageView.layer.masksToBounds = YES;
    backImageView.layer.borderWidth = 1;
    backImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    backImageView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.view).offset(-50);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(backImageView.mas_width).multipliedBy(0.5);
    }];
    
    UILabel *label1 = [[UILabel alloc] init];
    label1.text = @"张三";
    label1.font = [UIFont systemFontOfSize:14];
    label1.textColor = [UIColor whiteColor];
    label1.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView).offset(20);
        make.leading.equalTo(backImageView).offset(20);
    }];
    
    UILabel *label2 = [[UILabel alloc] init];
    label2.text = @"身份证号";
    label2.font = [UIFont systemFontOfSize:14];
    label2.textColor = [UIColor whiteColor];
    label2.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label2];
    [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label1.mas_bottom).offset(20);
        make.leading.equalTo(label1);
        make.height.equalTo(@20);
    }];
    
    UILabel *label3 = [[UILabel alloc] init];
    label3.text = @"3213123123123123123123";
    label3.font = [UIFont systemFontOfSize:14];
    label3.textColor = [UIColor whiteColor];
    label3.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label3];
    [label3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label2.mas_bottom).offset(10);
        make.leading.equalTo(label1);
        make.height.equalTo(@20);
    }];
    
    UIImageView *headImageView = [[UIImageView alloc] init];
    headImageView.layer.cornerRadius = 5;
    headImageView.layer.masksToBounds = YES;
    headImageView.backgroundColor = [UIColor greenColor];
    [self.view addSubview:headImageView];
    [headImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView).offset(30);
        make.trailing.equalTo(backImageView).offset(-30);
        make.width.equalTo(@80);
        make.height.equalTo(@100);
    }];
    
    UIImageView *stateImageView = [[UIImageView alloc] init];
    stateImageView.image = [UIImage imageNamed:@"digital_authe_success"];
    [self.view addSubview:stateImageView];
    [stateImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(backImageView);
        make.trailing.equalTo(backImageView);
    }];
    
    UIImageView *QRImageView = [[UIImageView alloc] init];
    QRImageView.layer.magnificationFilter = kCAFilterNearest;
    QRImageView.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:@"二维码内容" imageViewWidth:160];
    QRImageView.layer.cornerRadius = 5;
    QRImageView.layer.masksToBounds = YES;
    QRImageView.userInteractionEnabled = YES;
    [self.view addSubview:QRImageView];
    [QRImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView.mas_bottom).offset(30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@160);
        make.height.equalTo(@160);
    }];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick)];
    [QRImageView addGestureRecognizer:tap];
    
    
}

- (void)tapClick{
    
    UIImage *image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:@"二维码内容" imageViewWidth:kScreenWidth];
    WeXBackupBigQRView *bigView = [[WeXBackupBigQRView alloc] initWithFrame:self.view.bounds];
    bigView.QRImageView.image = image;
    [self.view.window addSubview:bigView];
}

- (void)rightItemClick{
    [self.navigationController popViewControllerAnimated:YES];
}




@end
