//
//  WeXRegisterSuccessViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/14.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXRegisterSuccessViewController.h"
#import "WeXHomeSafetyView.h"
#import "WeXCardViewController.h"
#import "WeXAuthorizeLoginViewController.h"
#import "WeXAuthorizeTwoStepLoginController.h"

#import "WeXPassportViewController.h"

#import "WeXDigitalAssetRLMModel.h"


#define kSafeViewHeight 400

@interface WeXRegisterSuccessViewController ()<WeXHomeSafetyViewDelegate>
{
    UIView *_coverView;//蒙版
    WeXPasswordCacheModal *_model;
}

@property (nonatomic,strong)UILabel *resultLabel;

@end

@implementation WeXRegisterSuccessViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.hidesBackButton = YES;
    [self initDigitalAsset];
    [self commonInit];
    [self setupSubViews];
    
}


- (void)initDigitalAsset{
    
    RLMRealm *realm = [RLMRealm defaultRealm];
    
    WeXDigitalAssetRLMModel *model7 = [[WeXDigitalAssetRLMModel alloc] init];
    model7.symbol = @"FTC";
    model7.name = @"Fit Coin";
    model7.iconUrl = @"http://www.wexpass.cn/images/Contractz_icon/Fitcoin@2x.png";
    model7.decimals = @"18";
    model7.contractAddress = [WexCommonFunc getFTCContractAddress];
    [realm beginWriteTransaction];
    [realm addObject:model7];
    [realm commitWriteTransaction];
    
    WeXDigitalAssetRLMModel *model1 = [[WeXDigitalAssetRLMModel alloc] init];
    model1.symbol = @"EOS";
    model1.name = @"EOS";
    model1.iconUrl = @"http://www.wexpass.cn/images/Contractz_icon/eos@2x.png";
    model1.decimals = @"18";
    model1.contractAddress = @"0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0";
    [realm beginWriteTransaction];
    [realm addObject:model1];
    [realm commitWriteTransaction];
    
    WeXDigitalAssetRLMModel *model2 = [[WeXDigitalAssetRLMModel alloc] init];
    model2.symbol = @"TRX";
    model2.name = @"TRON";
    model2.iconUrl = @"http://www.wexpass.cn/images/Contractz_icon/tron@2x.png";
    model2.decimals = @"6";
    model2.contractAddress = @"0xf230b790e05390fc8295f4d3f60332c93bed42e2";
    [realm beginWriteTransaction];
    [realm addObject:model2];
    [realm commitWriteTransaction];
   
    
    WeXDigitalAssetRLMModel *model3 = [[WeXDigitalAssetRLMModel alloc] init];
    model3.symbol = @"QTUM";
    model3.name = @"Qtum";
    model3.iconUrl = @"http://www.wexpass.cn/images/Contractz_icon/qtum@2x.png";
    model3.decimals = @"18";
    model3.contractAddress = @"0x9a642d6b3368ddc662CA244bAdf32cDA716005BC";
    [realm beginWriteTransaction];
    [realm addObject:model3];
    [realm commitWriteTransaction];
    
    WeXDigitalAssetRLMModel *model4 = [[WeXDigitalAssetRLMModel alloc] init];
    model4.symbol = @"RED";
    model4.name = @"Red Community";
    model4.iconUrl = @"http://www.wexpass.cn/images/Contractz_icon/red@2x.png";
    model4.decimals = @"18";
    model4.contractAddress = @"0x76960Dccd5a1fe799F7c29bE9F19ceB4627aEb2f";
    [realm beginWriteTransaction];
    [realm addObject:model4];
    [realm commitWriteTransaction];
    
    WeXDigitalAssetRLMModel *model5 = [[WeXDigitalAssetRLMModel alloc] init];
    model5.symbol = @"RUFF";
    model5.name = @"RUFF";
    model5.iconUrl = @"http://www.wexpass.cn/images/Contractz_icon/ruff@2x.png";
    model5.decimals = @"18";
    model5.contractAddress = @"0xf278c1ca969095ffddded020290cf8b5c424ace2";
    [realm beginWriteTransaction];
    [realm addObject:model5];
    [realm commitWriteTransaction];
    
    WeXDigitalAssetRLMModel *model6 = [[WeXDigitalAssetRLMModel alloc] init];
    model6.symbol = @"AIDOC";
    model6.name = @"AI Doctor";
    model6.iconUrl = @"http://www.wexpass.cn/images/Contractz_icon/ai_doctor@2x.png";
    model6.decimals = @"18";
    model6.contractAddress = @"0x584b44853680ee34a0f337b712a8f66d816df151";
    [realm beginWriteTransaction];
    [realm addObject:model6];
    [realm commitWriteTransaction];
    

    
}


- (void)commonInit{
    _model = [WexCommonFunc getPassport];
    NSLog(@"keyStore=%@",_model.keyStore);
}

//初始化滚动视图
-(void)setupSubViews{
    
    NSString *address = [_model.keyStore objectForKey:@"address"];
    
    UIImageView *backImageView = [[UIImageView alloc] init];
    backImageView.image = [UIImage imageNamed:@"copyFrame"];
    [self.view addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(100);
        make.leading.equalTo(self.view).offset(30);
        make.trailing.equalTo(self.view).offset(-30);
        make.height.mas_equalTo(backImageView.mas_width).multipliedBy(1/2.5);
        
    }];
    
    UIImageView *QRImageView = [[UIImageView alloc] init];
    QRImageView.layer.cornerRadius = 4;
    QRImageView.layer.masksToBounds = YES;
    QRImageView.layer.magnificationFilter = kCAFilterNearest;
    QRImageView.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:address imageViewWidth:70];
    [self.view addSubview:QRImageView];
    [QRImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.trailing.equalTo(backImageView.mas_centerX).offset(-20);
        make.top.equalTo(backImageView).offset(20);
        make.bottom.equalTo(backImageView).offset(-20);
        make.width.mas_equalTo(QRImageView.mas_height);
    }];
    
    
    UILabel *label1 = [[UILabel alloc] init];
    label1.text = @"口袋地址:";
    label1.font = [UIFont systemFontOfSize:16];
    label1.textColor = ColorWithLabelDescritionBlack;
    label1.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(QRImageView);
        make.leading.equalTo(backImageView.mas_centerX).offset(30);
        make.height.equalTo(@15);
    }];
    
    UILabel *label2 = [[UILabel alloc] init];
    label2.text = [NSString stringWithFormat:@"***%@",[address substringWithRange:NSMakeRange(address.length-6, 6)]];
    label2.font = [UIFont systemFontOfSize:16];
    label2.textColor = ColorWithLabelDescritionBlack;
    label2.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label2];
    [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(QRImageView);
        make.leading.equalTo(backImageView.mas_centerX).offset(30);
        make.height.equalTo(@15);
    }];
    
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = [UIColor lightGrayColor];
    line.alpha = LINE_VIEW_ALPHA;
    [self.view addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(label2);
        make.trailing.equalTo(label2);
        make.top.equalTo(label2.mas_bottom);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    UILabel *label3= [[UILabel alloc] init];
    label3.text = @"全向口袋";
    label3.font = [UIFont systemFontOfSize:16];
    label3.textColor = ColorWithLabelDescritionBlack;
    label3.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label3];
    [label3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(QRImageView);
        make.leading.equalTo(backImageView.mas_centerX).offset(30);
        make.height.equalTo(@15);
    }];
    
    UIImageView *successImageView1 = [[UIImageView alloc] init];
    successImageView1.image = [UIImage imageNamed:@"success1"];
    [self.view addSubview:successImageView1];
    [successImageView1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label3);
        make.leading.equalTo(label3.mas_trailing);
        make.height.equalTo(@15);
        make.width.equalTo(@15);
    }];
    
    UIImageView *successImageView2 = [[UIImageView alloc] init];
    successImageView2.image = [UIImage imageNamed:@"success3"];
    [self.view addSubview:successImageView2];
    [successImageView2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(backImageView);
        make.centerY.equalTo(backImageView.mas_bottom).offset(-5);
        make.height.equalTo(@30);
        make.width.equalTo(@30);
    }];
    
    _resultLabel= [[UILabel alloc] init];
    if (self.type == WeXRegisterSuccessTypeCreate) {
        _resultLabel.text = @"创建成功";
    }
    else if (self.type == WeXRegisterSuccessTypeImport)
    {
        _resultLabel.text = @"导入成功";
    }
    
    _resultLabel.font = [UIFont boldSystemFontOfSize:20];
    _resultLabel.textColor = [UIColor whiteColor];
    _resultLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:_resultLabel];
    [_resultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(successImageView2.mas_bottom).offset(20);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@100);
        make.height.equalTo(@20);
    }];
    
    UIImageView *backImageView2 = [[UIImageView alloc] init];
    backImageView2.image = [UIImage imageNamed:@"frame1"];
    [self.view addSubview:backImageView2];
    [backImageView2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.view).offset(50);
        make.leading.equalTo(self.view);
        make.trailing.equalTo(self.view);
        make.height.equalTo(@70);
        
    }];
    
    UIImageView *successImageView3 = [[UIImageView alloc] init];
    successImageView3.image = [UIImage imageNamed:@"success2"];
    [self.view addSubview:successImageView3];
    [successImageView3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(20);
        make.centerY.equalTo(backImageView2);
        make.height.equalTo(@30);
        make.width.equalTo(@25);
    }];
    
    UILabel *label4= [[UILabel alloc] init];
    label4.text = @"为了您的口袋安全,建议您先开启本地安全保护";
    label4.adjustsFontSizeToFitWidth = YES;
    label4.font = [UIFont systemFontOfSize:17];
    label4.textColor = ColorWithLabelDescritionBlack;
    label4.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:label4];
    [label4 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(backImageView2);
        make.leading.equalTo(successImageView3.mas_trailing).offset(10);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(@20);
    }];
    
    UIButton *openSafeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [openSafeBtn setTitle:@"开启本地安全保护" forState:UIControlStateNormal];
    [openSafeBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    openSafeBtn.backgroundColor = ColorWithButtonRed;
    openSafeBtn.layer.cornerRadius = 3;
    openSafeBtn.layer.masksToBounds = YES;
    [openSafeBtn addTarget:self action:@selector(openSafeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    openSafeBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:openSafeBtn];
    [openSafeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-80);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@50);
    }];
    
    UIButton *skipBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [skipBtn setTitle:@"跳过" forState:UIControlStateNormal];
    [skipBtn setTitleColor:ColorWithButtonRed forState:UIControlStateNormal];
    skipBtn.backgroundColor = [UIColor clearColor];
    skipBtn.layer.cornerRadius = 3;
    skipBtn.layer.masksToBounds = YES;
    skipBtn.layer.borderWidth = 1;
    skipBtn.layer.borderColor = ColorWithButtonRed.CGColor;
    [skipBtn addTarget:self action:@selector(skipBtnClick) forControlEvents:UIControlEventTouchUpInside];
    skipBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [self.view addSubview:skipBtn];
    [skipBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-25);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(@60);
        make.height.equalTo(@30);
    }];
    
}

- (void)openSafeBtnClick{
    
    //创建蒙版
    _coverView = [[UIView alloc] initWithFrame:self.view.bounds];
    _coverView.backgroundColor = [UIColor blackColor];
    _coverView.alpha = COVER_VIEW_ALPHA;
    [self.view addSubview:_coverView];
    
    WeXHomeSafetyView *safeView = [[WeXHomeSafetyView alloc] initWithFrame:CGRectMake(0, kScreenHeight, kScreenWidth, kSafeViewHeight)];
    safeView.skipName = @"跳过";
    safeView.backgroundColor = [UIColor whiteColor];
    safeView.delegate = self;
    [self.view addSubview:safeView];
//    __weak typeof(safeView) weakSafeView = safeView;
    safeView.skipBtnBlock = ^{
       [self configJumpController];
    };
    
    [UIView animateWithDuration:0.5 animations:^{
        safeView.frame = CGRectMake(0, kScreenHeight-kSafeViewHeight, kScreenWidth, kSafeViewHeight);
    }];
}

- (void)skipBtnClick{
    [self configJumpController];
   
}

- (void)safetyViewDidSetPassword{
    [self configJumpController];
 
}

- (void)configJumpController
{
    if (self.isFromAuthorize) {
        NSString *urlStr = [self.url absoluteString];
        //微财富两步走流程
        if ([urlStr hasPrefix:@"wexchain://auth2fa"]) {
            WeXAuthorizeTwoStepLoginController *ctrl = [[WeXAuthorizeTwoStepLoginController alloc] init];
            ctrl.url = self.url;
            [self.navigationController pushViewController:ctrl animated:YES];
        }
        else
        {
            WeXAuthorizeLoginViewController *ctrl = [[WeXAuthorizeLoginViewController alloc] init];
            ctrl.url = self.url;
            [self.navigationController pushViewController:ctrl animated:YES];
        }
    }
    else
    {
        WeXPassportViewController *ctrl = [[WeXPassportViewController alloc] init];
        ctrl.isShowDescription = YES;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}




@end
