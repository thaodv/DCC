//
//  WeXBorrowResultViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowResultViewController.h"
#import "WeXMyBorrowListViewController.h"

@interface WeXBorrowResultViewController ()

@end

@implementation WeXBorrowResultViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"借币申请结果");
    self.navigationItem.hidesBackButton = YES;
    [self setupSubViews];
}

//初始化滚动视图
-(void)setupSubViews{
    
    UIImageView *statusImageView = [[UIImageView alloc] init];
    statusImageView.image = [UIImage imageNamed:@"borrow_success"];
    [self.view addSubview:statusImageView];
    [statusImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+40);
        make.centerX.equalTo(self.view).offset(0);
    }];
    
    UILabel *locationLabel = [[UILabel alloc] init];
    locationLabel.text = WeXLocalizedString(@"申请成功");
    locationLabel.font = [UIFont systemFontOfSize:20];
    locationLabel.textColor = COLOR_LABEL_DESCRIPTION;
    locationLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:locationLabel];
    [locationLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.view).offset(0);
        make.top.equalTo(statusImageView.mas_bottom).offset(20);
    }];
       
    UIButton *recordBtn = [WeXCustomButton button];
    [recordBtn setTitle:WeXLocalizedString(@"借币记录") forState:UIControlStateNormal];
    [recordBtn addTarget:self action:@selector(recordBtnClick) forControlEvents:UIControlEventTouchUpInside];
    recordBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:recordBtn];
    [recordBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.bottom.equalTo(self.view).offset(-30);
        make.height.equalTo(@40);
    }];
    
    
}

- (void)recordBtnClick
{
    WeXMyBorrowListViewController *ctrl = [[WeXMyBorrowListViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}


@end
