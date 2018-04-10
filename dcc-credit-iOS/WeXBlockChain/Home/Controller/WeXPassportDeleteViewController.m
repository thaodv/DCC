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



@interface WeXPassportDeleteViewController ()<WeXDeletePassportViewDelegate,WeXPasswordManagerDelegate>
{
    WeXPasswordManager *_manager;
    
    WeXDeletePassportView *_deleteView;
}

@end

@implementation WeXPassportDeleteViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"删除口袋";
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"删除口袋后，您只能通过导入口袋恢复当前口袋的使用，如果您未备份口袋，则无法继续使用当前口袋。强烈建议您在删除口袋之前备份口袋。";
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
    
    UIButton *backupBtn = [UIButton buttonWithType:UIButtonTypeSystem];
    [backupBtn setTitle:@"备份口袋" forState:UIControlStateNormal];
    [backupBtn setTitleColor:ColorWithButtonRed forState:UIControlStateNormal];
    backupBtn.layer.borderWidth = 1;
    backupBtn.layer.borderColor = ColorWithButtonRed.CGColor;
    backupBtn.layer.cornerRadius = 5;
    [backupBtn addTarget:self action:@selector(backupBtnClick) forControlEvents:UIControlEventTouchUpInside];
    backupBtn.titleLabel.font = [UIFont systemFontOfSize:17];
    [self.view addSubview:backupBtn];
    [backupBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(10);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(@110);
        make.height.equalTo(@30);
    }];
    
    UIButton *deleteBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [deleteBtn setTitle:@"删除口袋" forState:UIControlStateNormal];
    [deleteBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    deleteBtn.backgroundColor = ColorWithButtonRed;
    deleteBtn.layer.cornerRadius = 3;
    deleteBtn.layer.masksToBounds = YES;
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


//点击删除口袋
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
    
    [WeXPorgressHUD showText:@"删除成功" onView:self.view];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        WeXHomeViewController *ctrl = [[WeXHomeViewController alloc] init];
        WeXBaseNavigationController *navCtrl = [[WeXBaseNavigationController alloc] initWithRootViewController:ctrl];
        AppDelegate* appDelagete = (AppDelegate*)[UIApplication sharedApplication].delegate;
        appDelagete.window.rootViewController = navCtrl;
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



@end
