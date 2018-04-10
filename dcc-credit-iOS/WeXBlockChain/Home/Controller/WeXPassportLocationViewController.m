//
//  WeXPassportLocationViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXPassportLocationViewController.h"

@interface WeXPassportLocationViewController ()
{
    WeXPasswordCacheModal *_model;
    UITextView *_contentTextView;
}

@end

@implementation WeXPassportLocationViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"口袋地址";
    [self setNavigationNomalBackButtonType];
    [self commonInit];
    [self setupSubViews];
}

- (void)commonInit{
    _model = [WexCommonFunc getPassport];
    NSLog(@"keyStore=%@",_model.keyStore);
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"二维码显示的是口袋地址，它可以作为以太坊及FTC代币的接收地址。";
    titleLabel.font = [UIFont systemFontOfSize:17];
    titleLabel.textColor = ColorWithLabelDescritionBlack;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    titleLabel.numberOfLines = 0;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+20);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
    }];
    
    UIImageView *QRImageView = [[UIImageView alloc] init];
    NSString *address = [_model.keyStore objectForKey:@"address"];
    QRImageView.layer.magnificationFilter = kCAFilterNearest;
    QRImageView.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:address imageViewWidth:160];
    QRImageView.layer.cornerRadius = 8;
    QRImageView.layer.masksToBounds = YES;
    QRImageView.backgroundColor = [UIColor greenColor];
    [self.view addSubview:QRImageView];
    [QRImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(20);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@160);
        make.height.equalTo(@160);
    }];
    
    //粘贴背景框
    UIImageView *backImageView = [[UIImageView alloc] init];
    backImageView.image = [UIImage imageNamed:@"copyFrame"];
    [self.view addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(QRImageView.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(30);
        make.trailing.equalTo(self.view).offset(-30);
        make.height.equalTo(@120);
    }];
    
    //粘贴框提示文字
    UILabel *locationLabel = [[UILabel alloc] init];
    locationLabel.text = @"口袋地址";
    locationLabel.font = [UIFont systemFontOfSize:17];
    locationLabel.textColor = ColorWithLabelDescritionBlack;
    locationLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:locationLabel];
    [locationLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(backImageView).offset(20);
        make.top.equalTo(backImageView).offset(30);
        make.width.equalTo(@100);
        make.height.equalTo(@20);
    }];
    
    //输入文本框
    _contentTextView = [[UITextView alloc] init];
    _contentTextView.backgroundColor = [UIColor clearColor];
    _contentTextView.textColor = ColorWithLabelWeakBlack;
    _contentTextView.text = address;
    _contentTextView.font = [UIFont systemFontOfSize:17];
    _contentTextView.editable = NO;
    [self.view addSubview:_contentTextView];
    [_contentTextView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(locationLabel.mas_bottom).offset(0);
        make.leading.equalTo(backImageView).offset(20);
        make.trailing.equalTo(backImageView).offset(-20);
        make.bottom.equalTo(backImageView).offset(-10);
    }];
  
    
    
    UIButton *copyBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [copyBtn setTitle:@"复制口袋地址" forState:UIControlStateNormal];
    [copyBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    copyBtn.backgroundColor = ColorWithButtonRed;
    copyBtn.layer.cornerRadius = 3;
    copyBtn.layer.masksToBounds = YES;
    [copyBtn addTarget:self action:@selector(copyBtnClick) forControlEvents:UIControlEventTouchUpInside];
    copyBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:copyBtn];
    [copyBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-40);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@40);
    }];
}

- (void)copyBtnClick{
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    pasteboard.string = _contentTextView.text;
    [WeXPorgressHUD showText:@"复制成功!" onView:self.view];
}

@end
