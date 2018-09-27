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
#import "WeXReceiveAddressManager.h"
#import "AppDelegate.h"




#define kSafeViewHeight 400

static const CGFloat kCardHeightWidthRatio = 203.0/347.0;


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
    [self commonInit];
    [self setupSubViews];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:WEX_CHANGE_ADDRESS_NOTIFY object:nil userInfo:nil];
    WeXReceiveAddressManager *manager = [WeXReceiveAddressManager shareManager];
    [manager initDefaultAddress];
}

- (void)commonInit{
    _model = [WexCommonFunc getPassport];
    NSLog(@"keyStore=%@",_model.keyStore);
}

//初始化滚动视图
-(void)setupSubViews{
    
    UIView *cardBackView = [[UIView alloc] init];
    cardBackView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:cardBackView];
    [cardBackView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(cardBackView.mas_width).multipliedBy(kCardHeightWidthRatio);
    }];
//    digital_card
    UIImageView *cardImageView = [[UIImageView alloc] init];
    cardImageView.image = [UIImage imageNamed:@"digital_card"];
    cardImageView.layer.magnificationFilter = kCAFilterNearest;
    [cardBackView addSubview:cardImageView];
    [cardImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(cardBackView);
    }];
    
//    UIImageView *headImageView = [[UIImageView alloc] init];
//    UIImage *headImage = [IYFileManager cacheImageFileWithKey:WEX_FILE_USER_FACE];
//    if (headImage == nil) {
//        headImageView.image = [UIImage imageNamed:@"digital_head"];
//    }
//    else
//    {
//        headImageView.image = headImage;
//    }
//    headImageView.layer.cornerRadius = 40;
//    headImageView.layer.masksToBounds = YES;
//    [cardBackView addSubview:headImageView];
//    [headImageView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.centerX.equalTo(cardBackView);
//        make.centerY.equalTo(cardBackView);
//        make.width.equalTo(@80);
//        make.height.equalTo(@80);
//    }];
    
    UILabel *addressLabel = [[UILabel alloc] init];
    NSString *address = [WexCommonFunc getFromAddress];
    if (address.length>8) {
        NSString *subAddress1 = [address substringWithRange:NSMakeRange(address.length-8, 4)];
        NSString *subAddress2 = [address substringWithRange:NSMakeRange(address.length-4, 4)];
        addressLabel.text = [NSString stringWithFormat:@"0x  %@  %@",subAddress1,subAddress2];
    }
    addressLabel.font = [UIFont systemFontOfSize:15];
    addressLabel.textColor = [UIColor whiteColor];
    addressLabel.textAlignment = NSTextAlignmentLeft;
    [cardBackView addSubview:addressLabel];
    [addressLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(cardBackView.mas_bottom).offset(-10);
        make.leading.equalTo(cardBackView).offset(25);
        make.height.equalTo(@20);
    }];
    
    UILabel *nickLabel = [[UILabel alloc] init];
    nickLabel.text = [WexDefaultConfig instance].nickName;
    nickLabel.font = [UIFont systemFontOfSize:15];
    nickLabel.textColor = [UIColor whiteColor];
    nickLabel.textAlignment = NSTextAlignmentLeft;
    [cardBackView addSubview:nickLabel];
    [nickLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(addressLabel.mas_top).offset(-10);
        make.leading.equalTo(addressLabel);
        make.height.equalTo(@20);
        make.trailing.equalTo(addressLabel.mas_trailing);
    }];
    
    
    _resultLabel = [[UILabel alloc] init];
    if (self.type == WeXRegisterSuccessTypeCreate) {
        _resultLabel.text = WeXLocalizedString(@"创建成功");
    }
    else if (self.type == WeXRegisterSuccessTypeImport)
    {
        _resultLabel.text = WeXLocalizedString(@"导入成功");
    }
    _resultLabel.font = [UIFont boldSystemFontOfSize:20];
    _resultLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _resultLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:_resultLabel];
    [_resultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(cardBackView.mas_bottom).offset(20);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@100);
        make.height.equalTo(@20);
    }];
    
    UIView *tableBackView = [[UIImageView alloc] init];
    tableBackView.backgroundColor = [UIColor clearColor];
    tableBackView.layer.cornerRadius = 12;
    tableBackView.layer.masksToBounds = YES;
    tableBackView.layer.borderWidth = 1;
    tableBackView.layer.borderColor =ColorWithHex(0xdcdcdc).CGColor;
    [self.view addSubview:tableBackView];
    [tableBackView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_resultLabel.mas_bottom).offset(30);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(@150);
    }];
//
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = COLOR_ALPHA_LINE;
    [self.view addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(tableBackView).offset(0);
        make.trailing.equalTo(tableBackView).offset(0);
        make.centerY.equalTo(tableBackView).offset(0);
        make.height.equalTo(@HEIGHT_LINE);
    }];
//
    UIImageView *successImageView3 = [[UIImageView alloc] init];
    successImageView3.image = [UIImage imageNamed:@"success2"];
    [tableBackView addSubview:successImageView3];
    [successImageView3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(tableBackView).offset(15);
        make.centerY.equalTo(tableBackView.mas_bottom).multipliedBy(0.25);
        make.width.equalTo(@29);
        make.height.equalTo(@32);
    }];
//
    UILabel *label4= [[UILabel alloc] init];
    label4.text = WeXLocalizedString(@"为了您的钱包安全,建议您先开启本地安全保护");
    label4.adjustsFontSizeToFitWidth = YES;
    label4.font = [UIFont systemFontOfSize:17];
    label4.textColor = COLOR_LABEL_DESCRIPTION;
    label4.textAlignment = NSTextAlignmentLeft;
    label4.numberOfLines = 2;
    [self.view addSubview:label4];
    [label4 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(successImageView3);
        make.leading.equalTo(successImageView3.mas_trailing).offset(10);
        make.trailing.equalTo(tableBackView).offset(-15);
    }];
//
//
    UIImageView *warringImageView = [[UIImageView alloc] init];
    warringImageView.image = [UIImage imageNamed:@"register_warring"];
    [tableBackView addSubview:warringImageView];
    [warringImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(tableBackView).offset(15);
        make.centerY.equalTo(tableBackView.mas_bottom).multipliedBy(0.75);
        make.width.equalTo(@29);
        make.height.equalTo(@26);
    }];
//
    UILabel *label5= [[UILabel alloc] init];
    label5.text = WeXLocalizedString(@"请优先备份钱包到其他设备，以免客户端删除、秘钥丢失等造成资产损失。");
    label5.adjustsFontSizeToFitWidth = YES;
    label5.font = [UIFont boldSystemFontOfSize:17];
    label5.textColor = ColorWithHex(0xed190f);
    label5.textAlignment = NSTextAlignmentLeft;
    label5.numberOfLines = 2;
    [self.view addSubview:label5];
    [label5 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(warringImageView);
        make.leading.equalTo(warringImageView.mas_trailing).offset(10);
        make.trailing.equalTo(tableBackView).offset(-15);
    }];
 
    WeXCustomButton *skipBtn = [WeXCustomButton button];
    [skipBtn setTitle:WeXLocalizedString(@"跳过") forState:UIControlStateNormal];
    [skipBtn addTarget:self action:@selector(skipBtnClick) forControlEvents:UIControlEventTouchUpInside];
    skipBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [self.view addSubview:skipBtn];
    [skipBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-20);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(@60);
        make.height.equalTo(@30);
    }];
    
    WeXCustomButton *openSafeBtn = [WeXCustomButton button];
    [openSafeBtn setTitle:WeXLocalizedString(@"开启本地安全保护") forState:UIControlStateNormal];
    [openSafeBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [openSafeBtn addTarget:self action:@selector(openSafeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    openSafeBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:openSafeBtn];
    [openSafeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(skipBtn.mas_top).offset(-10);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@50);
    }];
    
}

- (void)openSafeBtnClick{
    
    //创建蒙版
    _coverView = [[UIView alloc] initWithFrame:self.view.bounds];
    _coverView.backgroundColor = [UIColor blackColor];
    _coverView.alpha = COVER_VIEW_ALPHA;
    [self.view addSubview:_coverView];
    
    WeXHomeSafetyView *safeView = [[WeXHomeSafetyView alloc] initWithFrame:CGRectMake(0, kScreenHeight, kScreenWidth, kSafeViewHeight)];
    safeView.skipName = WeXLocalizedString(@"跳过");
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

- (void)configJumpController {
//    for (UIViewController *ctrl in self.navigationController.viewControllers) {
//        if ([ctrl isKindOfClass:[WeXPassportViewController class]]) {
//            [self.navigationController popToViewController:ctrl animated:YES];
//        }
//    }
    [(AppDelegate *)[UIApplication sharedApplication].delegate resetRootWindowController];
    
    
//    if (self.isFromAuthorize) {
//        NSString *urlStr = [self.url absoluteString];
//        //微财富两步走流程
//        if ([urlStr hasPrefix:@"wexchain://auth2fa"]) {
//            WeXAuthorizeTwoStepLoginController *ctrl = [[WeXAuthorizeTwoStepLoginController alloc] init];
//            ctrl.url = self.url;
//            [self.navigationController pushViewController:ctrl animated:YES];
//        }
//        else
//        {
//            WeXAuthorizeLoginViewController *ctrl = [[WeXAuthorizeLoginViewController alloc] init];
//            ctrl.url = self.url;
//            [self.navigationController pushViewController:ctrl animated:YES];
//        }
//    }
//    else
//    {
//        WeXPassportViewController *ctrl = [[WeXPassportViewController alloc] init];
//        ctrl.isShowDescription = YES;
//        [self.navigationController pushViewController:ctrl animated:YES];
//    }
}




@end
