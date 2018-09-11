//
//  WeXMyIpfsAddressController.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyIpfsAddressController.h"
#import "WeXIpfsContractHeader.h"
#import "AIIpfsHeader.h"

@interface WeXMyIpfsAddressController ()

@property (nonatomic,strong)UIView *topView;
@property (nonatomic,strong)UILabel *oneDefaultLabel;
@property (nonatomic,strong)UIView *centralView;
@property (nonatomic,strong)UIView *bottomView;
@property (nonatomic,strong)UIButton *hashBtn;
@property (nonatomic,strong)UIButton *urlBtn;
@property (nonatomic,strong)UILabel *hashLabel;
@property (nonatomic,strong)UILabel *urlLabel;

@end

@implementation WeXMyIpfsAddressController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    // Do any additional setup after loading the view.
}

#pragma mark -初始化控件布局
-(void)setupSubViews{
    
    self.view.backgroundColor = [UIColor whiteColor];
    //头部布局
    _topView = [[UIView alloc]init];
    _topView.backgroundColor = ColorWithHex(0xF8F8FF);
    [self.view addSubview:_topView];
    [_topView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.left.right.mas_equalTo(0);
        make.height.mas_offset(64);
    }];
    self.oneDefaultLabel = [[UILabel alloc]init];
    self.oneDefaultLabel.font = [UIFont systemFontOfSize:14];
    self.oneDefaultLabel.textColor = ColorWithHex(0x4A4A4A);
    self.oneDefaultLabel.text = @"数据加密存储在IPFS后会获得唯一Hash,可以根据Hash去访问及下载您存储的数据。";
    self.oneDefaultLabel.numberOfLines = 2;//多行显示
    self.oneDefaultLabel.lineBreakMode = NSLineBreakByWordWrapping;
    [self.topView addSubview:self.oneDefaultLabel];
    [_oneDefaultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.topView).offset(15);
        make.left.equalTo(self.topView).offset(15);
        make.right.equalTo(self.topView).offset(-15);
    }];
    
    _centralView = [[UIView alloc]init];
    [self.view addSubview:_centralView];
    [_centralView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.topView.mas_bottom).offset(0);
        make.left.right.mas_equalTo(0);
        make.height.mas_offset(80);
    }];
 
    UILabel *twoDefaultLabel = [[UILabel alloc]init];
    twoDefaultLabel.font = [UIFont systemFontOfSize:14];
    twoDefaultLabel.textColor = ColorWithHex(0x4A4A4A);
    twoDefaultLabel.text = @"IPFS Hash:";
 
    [self.centralView addSubview:twoDefaultLabel];
    [twoDefaultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.centralView).offset(10);
        make.left.equalTo(self.centralView).offset(15);
        make.width.mas_equalTo(120);
        make.height.mas_equalTo(20);
    }];
    
    self.hashBtn = [[UIButton alloc]init];
    [self.hashBtn setTitle:@"复制Hash" forState:UIControlStateNormal];
    [self.hashBtn setTitleColor:ColorWithHex(0x6766CC) forState:UIControlStateNormal];
    //    [self.cloudAddressBtn setBackgroundColor:ColorWithHex(0x6766CC)];
    [self.hashBtn addTarget:self action:@selector(copyHashClick) forControlEvents:UIControlEventTouchUpInside];
    //边框宽度
    self.hashBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    self.hashBtn.layer.borderColor = ColorWithHex(0x6766CC).CGColor;//设置边框颜色
    self.hashBtn.layer.borderWidth = 1.0f;//设置边框颜色
    self.hashBtn.layer.cornerRadius = 6;
    self.hashBtn.clipsToBounds = YES;
    [self.centralView addSubview:self.hashBtn];
    [self.hashBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.centralView).offset(10);
        make.right.mas_equalTo(-15);
        make.width.mas_equalTo(70);
        make.height.mas_equalTo(20);
    }];
    
    self.hashLabel = [[UILabel alloc]init];
    self.hashLabel.font = [UIFont systemFontOfSize:14];
    self.hashLabel.textColor = ColorWithHex(0x9B9B9B);
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    
    if (_idnetifyType == WeXFileIpfsModeTypeIdentify) {
        self.hashLabel.text = [user objectForKey:WEX_IPFS_IDENTIFY_HASH];
    }
    else if (_idnetifyType == WeXFileIpfsModeTypeBankCard){
        self.hashLabel.text = [user objectForKey:WEX_IPFS_BankCard_HASH];
    }
    else if (_idnetifyType == WeXFileIpfsModeTypePhoneOperator){
        self.hashLabel.text = [user objectForKey:WEX_IPFS_PhoneOperator_HASH];
    }

    self.hashLabel.numberOfLines = 2;//多行显示
    self.hashLabel.lineBreakMode = NSLineBreakByWordWrapping;
    [self.centralView addSubview:self.hashLabel];
    [self.hashLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(twoDefaultLabel.mas_bottom).offset(0);
        make.left.equalTo(self.centralView).offset(15);
        make.right.equalTo(self.centralView).offset(-15);
    }];
    
    UIView *oneLineView = [[UIView alloc]init];
    oneLineView.backgroundColor = ColorWithHex(0xEFEFEF);
    [self.centralView addSubview: oneLineView];
    [oneLineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.hashLabel.mas_bottom).offset(5);
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
        make.height.mas_equalTo(1);
    }];
    
    _bottomView = [[UIView alloc]init];
    [self.view addSubview:_bottomView];
    [_bottomView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.centralView.mas_bottom).offset(0);
        make.left.right.mas_equalTo(0);
        make.height.mas_offset(80);
    }];
    
    UILabel *threeDefaultLabel = [[UILabel alloc]init];
    threeDefaultLabel.font = [UIFont systemFontOfSize:14];
    threeDefaultLabel.textColor = ColorWithHex(0x4A4A4A);
    threeDefaultLabel.text = @"网关地址:";
    [self.bottomView addSubview:threeDefaultLabel];
    [threeDefaultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.bottomView).offset(10);
        make.left.equalTo(self.bottomView).offset(15);
        make.width.mas_equalTo(120);
        make.height.mas_equalTo(20);
    }];
    
    self.urlBtn = [[UIButton alloc]init];
    [self.urlBtn setTitle:@"复制地址" forState:UIControlStateNormal];
    [self.urlBtn setTitleColor:ColorWithHex(0x6766CC) forState:UIControlStateNormal];
//    self.urlBtn.hidden = YES;
    //    [self.cloudAddressBtn setBackgroundColor:ColorWithHex(0x6766CC)];
    [self.urlBtn addTarget:self action:@selector(copyUrlClick) forControlEvents:UIControlEventTouchUpInside];
    //边框宽度
    self.urlBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    self.urlBtn.layer.borderColor = ColorWithHex(0x6766CC).CGColor;//设置边框颜色
    self.urlBtn.layer.borderWidth = 1.0f;//设置边框颜色
    self.urlBtn.layer.cornerRadius = 6;
    self.urlBtn.clipsToBounds = YES;
    [self.bottomView addSubview:self.urlBtn];
    [self.urlBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.bottomView).offset(5);
        make.right.mas_equalTo(-15);
        make.width.mas_equalTo(70);
        make.height.mas_equalTo(20);
    }];
    
    self.urlLabel = [[UILabel alloc]init];
    self.urlLabel.font = [UIFont systemFontOfSize:14];
    self.urlLabel.textColor = ColorWithHex(0x9B9B9B);
    if (_idnetifyType == WeXFileIpfsModeTypeIdentify) {
        
        NSString *urlStr = [user objectForKey:WEX_IPFS_IDENTIFY_HASH];
        if (urlStr.length>0) {
            self.urlLabel.text =  [WEX_IPFS_MAIN_URL stringByAppendingString:[user objectForKey:WEX_IPFS_IDENTIFY_HASH]];
        }else{
            self.urlLabel.text =  WEX_IPFS_MAIN_URL;
        }
            
    }
    else if (_idnetifyType == WeXFileIpfsModeTypeBankCard){
        NSString *urlStr = [user objectForKey:WEX_IPFS_BankCard_HASH];
        if (urlStr.length>0) {
           self.urlLabel.text =  [WEX_IPFS_MAIN_URL stringByAppendingString:[user objectForKey:WEX_IPFS_BankCard_HASH]];
        }else{
           self.urlLabel.text =  WEX_IPFS_MAIN_URL;
        }
    }
    else if (_idnetifyType == WeXFileIpfsModeTypePhoneOperator){
        NSString *urlStr = [user objectForKey:WEX_IPFS_PhoneOperator_HASH];
        if (urlStr.length>0) {
            self.urlLabel.text =  [WEX_IPFS_MAIN_URL stringByAppendingString:[user objectForKey:WEX_IPFS_PhoneOperator_HASH]];
        }else{
            self.urlLabel.text =  WEX_IPFS_MAIN_URL;
        }
    }
    self.urlLabel.numberOfLines = 0;//多行显示
    self.urlLabel.lineBreakMode = NSLineBreakByWordWrapping;
    [self.bottomView addSubview:self.urlLabel];
    [self.urlLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(threeDefaultLabel.mas_bottom).offset(2);
        make.left.equalTo(self.bottomView).offset(15);
        make.right.equalTo(self.bottomView).offset(-15);
    }];
    
    UIView *twoLineView = [[UIView alloc]init];
    twoLineView.backgroundColor = ColorWithHex(0xEFEFEF);
    [self.bottomView addSubview: twoLineView];
    [twoLineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.urlLabel.mas_bottom).offset(5);
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
        make.height.mas_equalTo(1);
    }];
    
}

- (void)copyHashClick{
    
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    pasteboard.string = self.hashLabel.text;
    [WeXPorgressHUD showText:WeXLocalizedString(@"复制成功!") onView:self.view];
}

- (void)copyUrlClick{
    
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    pasteboard.string = self.urlLabel.text;
    [WeXPorgressHUD showText:WeXLocalizedString(@"复制成功!") onView:self.view];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
