//
//  WeXPassportBackupViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXPassportBackupViewController.h"
#import "WeXPassportPasswordShowView.h"

#import "WeXBackupBigQRView.h"

#define kTitleButtonHeight 50


typedef NS_ENUM(NSInteger,WeXBackupHandleType) {
    WeXBackupHandleTypeNone,
    WeXBackupHandleTypeShowPassword,//查看口袋密码流程
    WeXBackupHandleTypePrivateKey//点击私钥明文流程
};

typedef void(^SafeVertifyResponse)(void);

@interface WeXPassportBackupViewController ()<WeXPasswordManagerDelegate>
{
    UIButton *_showBtn;
    WeXPasswordCacheModal *_model;
    UITextView *_contentTextView;
    UIImageView *_QRImageView;//二维码图片
    UIButton *_copyBtn;//复制按钮
    UILabel *_descriptionLabel;
    UILabel *_descriptionLabel2;
    
    UILabel *_exportTitle1;//导出方式一
    UILabel *_exportTitle2;//导出方式二
    
    WeXPasswordManager *_manager;
    
    UITableView *_tableView;
    
    BOOL _isNeedVerfyPsd;//点击私钥明文tab时候是否需要验证密码
    
}

@property (nonatomic,strong)UIButton *keyStroeBtn;

@property (nonatomic,strong)UIButton *privateKeyBtn;

@property (strong, nonatomic) UIView *moveLine;//tab下划线

@property (nonatomic,copy)SafeVertifyResponse response;

@property (nonatomic,assign)WeXBackupHandleType handleType;


@end

@implementation WeXPassportBackupViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"口袋备份";
    [self setNavigationNomalBackButtonType];
    [self commonInit];
    [self setupSubViews];
}

- (void)commonInit{
    self.automaticallyAdjustsScrollViewInsets = NO;
    _model = [WexCommonFunc getPassport];
    NSLog(@"keyStore=%@",_model.keyStore);
    
    self.handleType = WeXBackupHandleTypeNone;
    _isNeedVerfyPsd = YES;
    
    
}

//初始化滚动视图
-(void)setupSubViews{
    //KEYSTORE按钮
    _keyStroeBtn =[UIButton buttonWithType:UIButtonTypeCustom];
    _keyStroeBtn.frame = CGRectMake(0, kNavgationBarHeight, kScreenWidth/2, kTitleButtonHeight);
    _keyStroeBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [_keyStroeBtn setTitle:@"KEYSTORE信息" forState:UIControlStateNormal];
    [_keyStroeBtn setTitleColor:ColorWithLabelDescritionBlack forState:UIControlStateSelected];
    _keyStroeBtn.selected = YES;
    [_keyStroeBtn setTitleColor:ColorWithLabelDescritionBlack forState:UIControlStateNormal];
    _keyStroeBtn.backgroundColor = [UIColor clearColor];
    [_keyStroeBtn addTarget:self action:@selector(keyStroeBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_keyStroeBtn];
    //私钥明文按钮
    _privateKeyBtn =[UIButton buttonWithType:UIButtonTypeCustom];
    _privateKeyBtn.frame = CGRectMake(kScreenWidth/2, kNavgationBarHeight, kScreenWidth/2, kTitleButtonHeight);
    _privateKeyBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [_privateKeyBtn setTitle:@"私钥明文" forState:UIControlStateNormal];
    [_privateKeyBtn setTitleColor:ColorWithLabelDescritionBlack forState:UIControlStateSelected];
    [_privateKeyBtn setTitleColor:ColorWithLabelDescritionBlack forState:UIControlStateNormal];
    _privateKeyBtn.backgroundColor = [UIColor clearColor];
    [_privateKeyBtn addTarget:self action:@selector(privateKeyBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_privateKeyBtn];
    
    
    //创建移动下划线
    UIView *line = [[UIView alloc] initWithFrame:CGRectMake(0,CGRectGetMaxY(_keyStroeBtn.frame)-1, kScreenWidth/2*0.6, 1)];
    line.backgroundColor = ColorWithLabelDescritionBlack;
    line.WeX_centerX = _keyStroeBtn.WeX_centerX;
    [self.view addSubview:line];
    self.moveLine = line;
    
    
    _tableView = [[UITableView alloc] init];
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.tableFooterView = [UIView new];
    [self.view addSubview:_tableView];
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line.mas_bottom);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(self.view);
    }];
    
    
    _descriptionLabel = [[UILabel alloc] init];
    _descriptionLabel.text = @"二维码显示的是KEYSTORE信息，KEYSTORE是私钥的签名保护文件，通过口袋密码对KEYSTORE文件签名。单独拥有KEYSTORE文件或者口袋密码都无法使用口袋。而同时拥有KEYSTORE文件和口袋密码则可以在任何设备使用口袋。强烈建议您在使用口袋前做好备份。";
    _descriptionLabel.font = [UIFont systemFontOfSize:14];
    _descriptionLabel.textColor = ColorWithLabelDescritionBlack;
    _descriptionLabel.textAlignment = NSTextAlignmentLeft;
    _descriptionLabel.numberOfLines = 0;
    [_tableView addSubview:_descriptionLabel];
    [_descriptionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_tableView).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
    }];
    
    _showBtn = [UIButton buttonWithType:UIButtonTypeSystem];
    [_showBtn setTitle:@"点击查看口袋密码" forState:UIControlStateNormal];
    [_showBtn setTitleColor:ColorWithButtonRed forState:UIControlStateNormal];
    _showBtn.layer.borderWidth = 1;
    _showBtn.layer.borderColor = ColorWithButtonRed.CGColor;
    _showBtn.layer.cornerRadius = 5;
    [_showBtn addTarget:self action:@selector(showBtnClick) forControlEvents:UIControlEventTouchUpInside];
    _showBtn.titleLabel.font = [UIFont systemFontOfSize:12];
    [_tableView addSubview:_showBtn];
    [_showBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_descriptionLabel.mas_bottom).offset(5);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(@130);
        make.height.equalTo(@25);
    }];
    
    UILabel *title1= [[UILabel alloc] init];
    title1.text = @"导出方式一：KEYSTORE文本信息二维码";
    title1.font = [UIFont systemFontOfSize:17];
    title1.textColor = ColorWithLabelDescritionBlack;
    title1.textAlignment = NSTextAlignmentCenter;
    [_tableView addSubview:title1];
    [title1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_descriptionLabel.mas_bottom).offset(50);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(@15);
    }];
    _exportTitle1 = title1;
    
    UIImageView *QRImageView = [[UIImageView alloc] init];
    NSDictionary *keystoreDict = _model.keyStore;
    NSData *data = [NSJSONSerialization dataWithJSONObject:keystoreDict options:0 error:nil];
    NSString *address = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    QRImageView.layer.magnificationFilter = kCAFilterNearest;
    QRImageView.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:address imageViewWidth:160];
    QRImageView.layer.cornerRadius = 5;
    QRImageView.layer.masksToBounds = YES;
    [_tableView addSubview:QRImageView];
    [QRImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(title1.mas_bottom).offset(10);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@160);
        make.height.equalTo(@160);
    }];
    QRImageView.userInteractionEnabled = YES;
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick)];
    [QRImageView addGestureRecognizer:tap];
    _QRImageView = QRImageView;
    
    UILabel *label1 = [[UILabel alloc] init];
    label1.text = @"点击图片后截图保存二维码";
    label1.font = [UIFont systemFontOfSize:12];
    label1.textColor = ColorWithLabelDescritionBlack;
    label1.textAlignment = NSTextAlignmentCenter;
    [_tableView addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(QRImageView.mas_bottom).offset(5);
        make.centerX.equalTo(self.view);
        make.height.equalTo(@15);
    }];
    
    UILabel *title2= [[UILabel alloc] init];
    title2.text = @"导出方式二：KEYSTORE文本信息二维码";
    title2.font = [UIFont systemFontOfSize:17];
    title2.textColor = ColorWithLabelDescritionBlack;
    title2.textAlignment = NSTextAlignmentCenter;
    [_tableView addSubview:title2];
    [title2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label1.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(@15);
    }];
    _exportTitle2 = title2;
    
    //粘贴背景框
    UIImageView *backImageView = [[UIImageView alloc] init];
    backImageView.image = [UIImage imageNamed:@"copyFrame"];
    [_tableView addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(title2.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(30);
        make.trailing.equalTo(self.view).offset(-30);
        make.height.equalTo(@120);
    }];
    
    //粘贴框提示文字
    UILabel *label2 = [[UILabel alloc] init];
    label2.text = @"KEYSTORE文本信息如下";
    label2.font = [UIFont systemFontOfSize:15];
    label2.textColor = ColorWithLabelDescritionBlack;
    label2.textAlignment = NSTextAlignmentLeft;
    [_tableView addSubview:label2];
    [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(backImageView).offset(20);
        make.top.equalTo(backImageView).offset(20);
        make.height.equalTo(@20);
    }];
    _descriptionLabel2 = label2;
    
    //输入文本框
    _contentTextView = [[UITextView alloc] init];
    _contentTextView.backgroundColor = [UIColor clearColor];
    _contentTextView.textColor = ColorWithLabelWeakBlack;
    _contentTextView.text = address;
    _contentTextView.font = [UIFont systemFontOfSize:15];
    _contentTextView.editable = NO;
    [_tableView addSubview:_contentTextView];
    [_contentTextView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label2.mas_bottom).offset(0);
        make.leading.equalTo(backImageView).offset(20);
        make.trailing.equalTo(backImageView).offset(-20);
        make.bottom.equalTo(backImageView).offset(-10);
    }];
    
    
    UIButton *copyBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [copyBtn setTitle:@"复制KEYSTORE文本信息" forState:UIControlStateNormal];
    [copyBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    copyBtn.backgroundColor = ColorWithButtonRed;
    copyBtn.layer.cornerRadius = 3;
    copyBtn.layer.masksToBounds = YES;
    [copyBtn addTarget:self action:@selector(copyBtnClick) forControlEvents:UIControlEventTouchUpInside];
    copyBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [_tableView addSubview:copyBtn];
    [copyBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_contentTextView.mas_bottom).offset(30);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@40);
    }];
    _copyBtn = copyBtn;
}

- (void)tapClick{
    
    NSDictionary *keystoreDict = _model.keyStore;
    NSData *data = [NSJSONSerialization dataWithJSONObject:keystoreDict options:0 error:nil];
    NSString *address = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    UIImage *image;
    
    if (_keyStroeBtn.selected) {
        image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:address imageViewWidth:kScreenWidth];
    }
    else
    {
        image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:_model.walletPrivateKey imageViewWidth:kScreenWidth];
    }
    
    WeXBackupBigQRView *bigView = [[WeXBackupBigQRView alloc] initWithFrame:self.view.bounds];
    bigView.QRImageView.image = image;
    [self.view.window addSubview:bigView];
}

-(void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    [self.view layoutIfNeeded];
    _tableView.contentSize = CGSizeMake(_tableView.frame.size.width, CGRectGetMaxY(_copyBtn.frame)+20);
}




- (void)keyStroeBtnClick:(UIButton *)btn{
    btn.selected = YES;
    _privateKeyBtn.selected = NO;
    [UIView animateWithDuration:0.3 animations:^{
        self.moveLine.WeX_centerX = _keyStroeBtn.WeX_centerX;
    }];
    
    _descriptionLabel2.text = @"KEYSTORE文本信息如下";
    NSDictionary *keystoreDict = _model.keyStore;
    NSData *data = [NSJSONSerialization dataWithJSONObject:keystoreDict options:0 error:nil];
    NSString *address = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    _QRImageView.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:address imageViewWidth:160];
    _contentTextView.text = address;
    _descriptionLabel.text = @"二维码显示的是KeyStore信息，KEYSTORE是私钥的签名保护文件，通过口袋密码对KEYSTORE文件签名。单独拥有KEYSTORE文件或者口袋密码都无法使用口袋。而同时拥有KEYSTORE文件和口袋密码则可以在任何设备使用口袋。强烈建议您在使用口袋前做好备份。";
    _showBtn.hidden = NO;
    [_copyBtn setTitle:@"复制KEYSTORE文本信息" forState:UIControlStateNormal];
    
    _exportTitle1.text = @"导出方式一：KEYSTORE文本信息二维码";
    _exportTitle2.text = @"导出方式二：KEYSTORE文本信息";

}

- (void)privateKeyBtnClick:(UIButton *)btn{
    if (_isNeedVerfyPsd) {//第一次点击需要验证本地密码
        self.handleType = WeXBackupHandleTypePrivateKey;
        [self configLocalSafetyView];
    }
    else
    {
        btn.selected = YES;
        _keyStroeBtn.selected = NO;
        [UIView animateWithDuration:0.3 animations:^{
            self.moveLine.WeX_centerX = _privateKeyBtn.WeX_centerX;
        }];
        _descriptionLabel2.text = @"私钥明文如下";
        _QRImageView.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:_model.walletPrivateKey imageViewWidth:160];
        _contentTextView.text = _model.walletPrivateKey;
        _descriptionLabel.text = @"当前二维码显示的是私钥明文，私钥明文是您对口袋所有权的认证方式。通过导入私钥可以在任何设备使用口袋。并创建对通行应的KeyStore文件和口袋密码。强烈建议您在使用口袋前做好备份。";
        _showBtn.hidden = YES;
        [_copyBtn setTitle:@"复制私钥明文" forState:UIControlStateNormal];
        
        _exportTitle1.text = @"导出方式一：私钥明文二维码";
        _exportTitle2.text = @"导出方式二：私钥明文";
        
    }
    
    
}

- (void)showBtnClick
{
    self.handleType = WeXBackupHandleTypeShowPassword;
    [self configLocalSafetyView];
}

- (void)copyBtnClick{
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    pasteboard.string = _contentTextView.text;
    [WeXPorgressHUD showText:@"复制成功!" onView:self.view];
}

- (void)createPassportPasswordShowView
{
    WeXPassportPasswordShowView *passwordView = [[WeXPassportPasswordShowView alloc] initWithFrame:self.view.bounds];
    passwordView.passwordLabel.text = _model.passportPassword;
    [self.view addSubview:passwordView];
}


- (void)configLocalSafetyView{
    //没有密码
    _model = [WexCommonFunc getPassport];
    if (_model.passwordType == WeXPasswordTypeNone) {
        if (self.handleType == WeXBackupHandleTypePrivateKey) {
            _isNeedVerfyPsd = NO;
            [self privateKeyBtnClick:_privateKeyBtn];
        }
        else if(self.handleType == WeXBackupHandleTypeShowPassword)
        {
            [self createPassportPasswordShowView];
        }
    }
    else
    {
        WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeVerify parentController:self];
        manager.delegate = self;
        [manager loadPassword];
        _manager = manager;
    }
        
       
}

#pragma mark - WeXPasswordManagerDelegate
- (void)passwordManagerVerifySuccess{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        if (self.handleType == WeXBackupHandleTypePrivateKey) {
            _isNeedVerfyPsd = NO;
            [self privateKeyBtnClick:_privateKeyBtn];
        }
        else if(self.handleType == WeXBackupHandleTypeShowPassword)
        {
            [self createPassportPasswordShowView];
        }
    });
    
}



@end
