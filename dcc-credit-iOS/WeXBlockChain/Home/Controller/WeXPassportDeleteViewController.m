//
//  WeXPassportDeleteViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXPassportDeleteViewController.h"
#import "WeXPassportBackupViewController.h"
#import "WeXHomeViewController.h"
#import "AppDelegate.h"
#import "WeXBaseNavigationController.h"
#import "WeXDeletePassportView.h"

#import "WeXPassportViewController.h"
#import "WeXDefaultTokenManager.h"

#import "WeXCreditDccMobileCacheModal.h"
#import "WeXCreditDccBankCacheModal.h"
#import "WeXCreditDccIDCacheModal.h"
#import "WeXIpfsContractHeader.h"
#import "WeXMyIpfsSaveController.h"
#import "WeXIpfsSavePasswordController.h"
#import "AppDelegate.h"

@interface WeXPassportDeleteViewController ()<WeXDeletePassportViewDelegate,WeXPasswordManagerDelegate>
{
    WeXPasswordManager *_manager;
    
    WeXDeletePassportView *_deleteView;
}

@end

@implementation WeXPassportDeleteViewController


- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"删除钱包");
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    //PassportDeleteViewControllerTipsMessage
    titleLabel.text = WeXLocalizedString(@"PassportDeleteViewControllerTipsMessage");
    titleLabel.font = [UIFont systemFontOfSize:17];
    titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    titleLabel.numberOfLines = 0;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+20);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
    }];
    
    UIButton *backupBtn = [WeXCustomButton button];
    [backupBtn setTitle:WeXLocalizedString(@"备份钱包") forState:UIControlStateNormal];
    [backupBtn addTarget:self action:@selector(backupBtnClick) forControlEvents:UIControlEventTouchUpInside];
    backupBtn.titleLabel.font = [UIFont systemFontOfSize:17];
    [self.view addSubview:backupBtn];
    [backupBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        //        make.trailing.equalTo(@110);
        make.height.equalTo(@40);
    }];
    
    UIButton *copyDataBtn = [WeXCustomButton button];
    [copyDataBtn setTitle:WeXLocalizedString(@"备份数据") forState:UIControlStateNormal];
    [copyDataBtn addTarget:self action:@selector(copyDataBtnClick) forControlEvents:UIControlEventTouchUpInside];
    copyDataBtn.titleLabel.font = [UIFont systemFontOfSize:17];
    [self.view addSubview:copyDataBtn];
    [copyDataBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backupBtn);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(backupBtn);
        make.leading.equalTo(backupBtn.mas_trailing).offset(20);
        make.height.equalTo(backupBtn);
    }];
    
    UIButton *deleteBtn = [WeXCustomButton button];
    [deleteBtn setTitle:WeXLocalizedString(@"删除钱包") forState:UIControlStateNormal];
    [deleteBtn addTarget:self action:@selector(deleteBtnClick) forControlEvents:UIControlEventTouchUpInside];
    deleteBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:deleteBtn];
    [deleteBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-40);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@40);
    }];
    
}

- (void)backupBtnClick
{
    WeXPassportBackupViewController *ctrl = [[WeXPassportBackupViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
    
}

- (void)deleteBtnClick
{
    
    [self createDeleteView];
    
    
}


//点击删除钱包
- (void)deletePassportViewDidDeletePassport{
    
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    
    if (model.passwordType == WeXPasswordTypeNone)
    {
        [self deleteCacheInfos];
    }
    else
    {
        [self configLocalSafetyView];
    }
    
    
   
}

- (void)deleteCacheInfos
{
    [_deleteView dismiss];
    
    [WexCommonFunc deletePassport];
    
    [[RLMRealm defaultRealm] beginWriteTransaction];
    [[RLMRealm defaultRealm] deleteAllObjects];
    [[RLMRealm defaultRealm] commitWriteTransaction];
    [IYFileManager removeCacheFileWithKey:WEX_FILE_USER_FACE];
    [WexDefaultConfig clearAll];
    
    [WexCommonFunc deleteImageWithName:WEX_ID_IMAGE_FRONT_KEY];
    [WexCommonFunc deleteImageWithName:WEX_ID_IMAGE_BACK_KEY];
    [WexCommonFunc deleteImageWithName:WEX_ID_IMAGE_HEAD_KEY];
    
    WeXCreditDccIDCacheModal *idCacheModel = [WeXCreditDccIDCacheModal sharedInstance];
    [idCacheModel clearCacheData];
    
    WeXCreditDccBankCacheModal *bankCacheModel = [WeXCreditDccBankCacheModal sharedInstance];
    [bankCacheModel clearCacheData];
    
    WeXCreditDccMobileCacheModal *mobileCacheModel = [WeXCreditDccMobileCacheModal sharedInstance];
    [mobileCacheModel clearCacheData];
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults removeObjectForKey:WEX_NET_TOKEN_KEY];
    [userDefaults removeObjectForKey:WEX_LOAN_REPORT_TIME_KEY];
    [userDefaults removeObjectForKey:WEX_LOAN_REPORT_CONTENT_KEY];
    //ipfs数据删除
    [userDefaults removeObjectForKey:WEX_IPFS_MY_KEY];
    [userDefaults removeObjectForKey:WEX_IPFS_MY_CHECKKEY];
    [userDefaults removeObjectForKey:WEX_IPFS_MY_TWOCHECKKEY];

    [userDefaults removeObjectForKey:WEX_IPFS_IDENTIFY_HASH];
    [userDefaults removeObjectForKey:WEX_IPFS_BankCard_HASH];
    [userDefaults removeObjectForKey:WEX_IPFS_PhoneOperator_HASH];
    
    //IPFS新增节点
    [userDefaults removeObjectForKey:WEX_IPFS_DEFAULT_NONEURL];
    [userDefaults removeObjectForKey:WEX_IPFS_CUSTOM_NONEURL];
    [userDefaults removeObjectForKey:WEX_IPFS_MAIN_NONEURL];
    [userDefaults removeObjectForKey:WEX_IPFS_CUSTOM_PUBLICADDRESS];
    [userDefaults removeObjectForKey:WEX_IPFS_CUSTOM_PORTNUM];
    
    [userDefaults synchronize];
 
    [WeXPorgressHUD showText:WeXLocalizedString(@"删除成功") onView:self.view];
    
    WeXDefaultTokenManager *tokenManager = [WeXDefaultTokenManager shareManager];
    [tokenManager initDefaultToken];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [(AppDelegate *)[UIApplication sharedApplication].delegate resetRootWindowController];
//        WeXPassportViewController *ctrl = [[WeXPassportViewController alloc] init];
//        WeXBaseNavigationController *navCtrl = [[WeXBaseNavigationController alloc] initWithRootViewController:ctrl];
//        AppDelegate* appDelagete = (AppDelegate*)[UIApplication sharedApplication].delegate;
//        appDelagete.window.rootViewController = navCtrl;
    });
}
//点击备份
- (void)deletePassportViewDidBackup{
    WeXPassportBackupViewController *ctrl = [[WeXPassportBackupViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)createDeleteView{
    _deleteView = [[WeXDeletePassportView alloc] initWithFrame:self.view.bounds];
    _deleteView.delegate = self;
    [self.view addSubview:_deleteView];
}


- (void)configLocalSafetyView{

    WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeVerify parentController:self];
    manager.delegate = self;
    [manager loadPassword];
    _manager = manager;
   
}

#pragma mark - WeXPasswordManagerDelegate
- (void)passwordManagerVerifySuccess{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self deleteCacheInfos];
    });
}

- (void)copyDataBtnClick{
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *passWord =  [user objectForKey:WEX_IPFS_MY_CHECKKEY];
    NSString *twoPassWord = [user objectForKey:WEX_IPFS_MY_TWOCHECKKEY];
    if (passWord) {
        WeXMyIpfsSaveController *ctrl = [[WeXMyIpfsSaveController alloc] init];
        ctrl.fromVc = self;
        [self.navigationController pushViewController:ctrl animated:YES];
    }else if (twoPassWord) {
        WeXIpfsSavePasswordController *ctrl = [[WeXIpfsSavePasswordController alloc] init];
        ctrl.fromVc = self;
        [self.navigationController pushViewController:ctrl animated:YES];
    } else{
        WeXIpfsSavePasswordController *ctrl = [[WeXIpfsSavePasswordController alloc] init];
        ctrl.fromVc = self;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}


@end
