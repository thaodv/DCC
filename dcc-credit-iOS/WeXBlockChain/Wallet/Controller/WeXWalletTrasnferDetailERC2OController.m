//
//  WeXWalletTrasnferDetailERC2OController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletTrasnferDetailERC2OController.h"

@interface WeXWalletTrasnferDetailERC2OController ()

@end

@implementation WeXWalletTrasnferDetailERC2OController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}


//初始化滚动视图
-(void)setupSubViews{
    UILabel *titlelabel = [[UILabel alloc] init];
    titlelabel.text = @"支付详情";
    titlelabel.font = [UIFont systemFontOfSize:20];
    titlelabel.textColor = [UIColor whiteColor];
    titlelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:titlelabel];
    [titlelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
    }];
    
    
    UILabel *orderTitleLabel = [[UILabel alloc] init];
    orderTitleLabel.text = @"订单信息:";
    orderTitleLabel.font = [UIFont systemFontOfSize:18];
    orderTitleLabel.textColor = [UIColor whiteColor];
    orderTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:orderTitleLabel];
    [orderTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titlelabel.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(@80);
    }];
    
    UILabel *orderLabel = [[UILabel alloc] init];
    orderLabel.text = @"支付";
    orderLabel.font = [UIFont systemFontOfSize:14];
    orderLabel.textColor = [UIColor whiteColor];
    orderLabel.textAlignment = NSTextAlignmentLeft;
    orderLabel.numberOfLines = 2;
    [self.view addSubview:orderLabel];
    [orderLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(orderTitleLabel);
        make.leading.equalTo(orderTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.height.equalTo(@40);
    }];
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = [UIColor lightGrayColor];
    line1.alpha = LINE_VIEW_ALPHA;
    [self.view addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(10);
        make.trailing.equalTo(self).offset(-10);
        make.top.equalTo(orderLabel.mas_bottom).offset(10);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    UILabel *fromTitleLabel = [[UILabel alloc] init];
    fromTitleLabel.text = @"付款地址:";
    fromTitleLabel.font = [UIFont systemFontOfSize:18];
    fromTitleLabel.textColor = [UIColor whiteColor];
    fromTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:fromTitleLabel];
    [fromTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(orderTitleLabel);
    }];
    
    UILabel *fromLabel = [[UILabel alloc] init];
//    fromLabel.text = self.recordModel.from;;
    fromLabel.font = [UIFont systemFontOfSize:14];
    fromLabel.textColor = [UIColor whiteColor];
    fromLabel.textAlignment = NSTextAlignmentLeft;
    fromLabel.numberOfLines = 2;
    [self.view addSubview:fromLabel];
    [fromLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fromTitleLabel);
        make.leading.equalTo(fromTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.height.equalTo(@40);
    }];
    
    UILabel *costTitleLabel = [[UILabel alloc] init];
    costTitleLabel.text = @"矿  工  费:";
    costTitleLabel.font = [UIFont systemFontOfSize:18];
    costTitleLabel.textColor = [UIColor whiteColor];
    costTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:costTitleLabel];
    [costTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fromTitleLabel.mas_bottom).offset(30);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(orderTitleLabel);
    }];
    
    UILabel *costLabel = [[UILabel alloc] init];
    //    costLabel.text = @"0x131231";
    costLabel.font = [UIFont systemFontOfSize:14];
    costLabel.textColor = [UIColor whiteColor];
    costLabel.textAlignment = NSTextAlignmentLeft;
    costLabel.numberOfLines = 2;
    [self.view addSubview:costLabel];
    [costLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(costTitleLabel);
        make.leading.equalTo(costTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.height.equalTo(@20);
    }];
    
}

@end
