//
//  WeXNewCardSettingViewController.m
//  WeXBlockChain
//
//  Created by wh on 2018/9/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXNewCardSettingViewController.h"
#import "WeXCardSettingCell.h"
#import "WeXAboutUsWebViewController.h"
#import "WeXPassportDescriptionViewController.h"
#import "WeXPassportDeleteViewController.h"
#import "WeXPassportModifyPasswordController.h"
#import "WeXPassportBackupViewController.h"
#import "WeXPassportLocationViewController.h"

#import "WeXPrivacyPolicyWebViewController.h"
#import "WeXAddressBookController.h"

#import "WeXCardSettingCell.h"
#import "UIImage-Extensions.h"
#import "IYFileManager.h"
#import "WexNickNameViewController.h"
#import "SettingFaceViewCell.h"
#import "WeXVersionUpdateManager.h"
#import "WeXSelectedNodeViewController.h"
#import "AppDelegate.h"
#import "WeXHomePushService.h"


@interface WeXNewCardSettingViewController ()<UITableViewDelegate,UITableViewDataSource,WeXPasswordManagerDelegate>

{
    UITableView *_tableView;
    UILabel *_safetyDescriptionLabel;
    WeXPasswordCacheModal *_model;
    
    WeXPasswordManager *_manager;
    UISwitch *_safeSwitch;
}

@end

@implementation WeXNewCardSettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.title = WeXLocalizedString(@"更多");
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    [self commonInit];
    
    // Do any additional setup after loading the view.
}

- (void)commonInit{
    
    _model = [WexCommonFunc getPassport];
    NSLog(@"_model=%@",_model);
    
}
//初始化滚动视图
-(void)setupSubViews{
    _tableView = [[UITableView alloc] init];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = ColorWithHex(0xF8F8FF);
    _tableView.tableFooterView = [UIView new];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
//    [self reSetTableViewFooterView];

    [self.view addSubview:_tableView];
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+5);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(self.view).offset(-60);
    }];
    [self reSetFooterView];
}

//section的高度

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 10;
}

//section底部间距
- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return CGFLOAT_MIN;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    
    UIView *contentView = [[[UIView alloc] init] initWithFrame:CGRectMake(0, 0, kScreenWidth, 10)];
    contentView.backgroundColor =  ColorWithHex(0xF5F5FD) ;
    return contentView;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
  
    return 60.f;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 4;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellID = @"cellID";
    
    WeXCardSettingCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXCardSettingCell" owner:self options:nil] firstObject];
        cell.backgroundColor = [UIColor whiteColor];
        cell.titleLabel.textColor = COLOR_LABEL_TITLE;
        cell.titleLabel.font = [UIFont systemFontOfSize:18];
        cell.desLabel.textColor = COLOR_LABEL_TITLE;
        cell.desLabel.font = [UIFont systemFontOfSize:15];
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    
//        if ( indexPath.row == 0)
//        {
//           cell.titleLabel.text = WeXLocalizedString(@"钱包地址");
//        }
        if ( indexPath.row == 0) {
            cell.titleLabel.text = WeXLocalizedString(@"选择以太坊节点");
        }
        else if ( indexPath.row == 1)
        {
            cell.titleLabel.text = WeXLocalizedString(@"本地安全保护");
            _model = [WexCommonFunc getPassport];
            cell.safeSwitch.hidden = NO;
            cell.arrowImg.hidden = YES;
            
            if (_model.passwordType == WeXPasswordTypeNone) {
//                cell.desLabel.text = WeXLocalizedString(@"已关闭");
                cell.safeSwitch.on = NO;
            }
            else
            {
                cell.safeSwitch.on = YES;
//                cell.desLabel.text = WeXLocalizedString(@"已开启");
            }
            [cell.safeSwitch addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventTouchUpInside];
            _safeSwitch = cell.safeSwitch;
        }
        
        else if ( indexPath.row == 2)
        {
            cell.titleLabel.text = WeXLocalizedString(@"版本更新");
            NSDictionary *infoDict = [[NSBundle mainBundle] infoDictionary];
            NSString *content = [NSString stringWithFormat:@"%@%@",WeXLocalizedString(@"当前版本"), [infoDict objectForKey:@"CFBundleShortVersionString"]];
            cell.desLabel.text = content;
           
        }
        else if (indexPath.row == 3)
        {
            cell.titleLabel.text = WeXLocalizedString(@"关于我们");
        }
    
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
//    if (indexPath.row == 0) {
//        WeXPassportLocationViewController *ctrl = [[WeXPassportLocationViewController alloc] init];
//        [WeXHomePushService pushFromVC:self toVC:ctrl];
//    }
//    else
    if (indexPath.row == 0)
    {
        WeXSelectedNodeViewController *ctrl = [[WeXSelectedNodeViewController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.row == 1) {
        
        _model = [WexCommonFunc getPassport];
        if (_model.passwordType == WeXPasswordTypeNone) {
            WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeCreate parentController:self];
            manager.delegate = self;
            [manager loadPassword];
            _manager = manager;
        }
        else {
            WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeVerify parentController:self];
            manager.delegate = self;
            [manager loadPassword];
            _manager = manager;
        }
    }
    else if (indexPath.row == 2)
    {
        WeXVersionUpdateManager *manager = [WeXVersionUpdateManager shareManager];
        [manager configVersionUpdateViewOnView:self.view isUpdate:false];
      
    }
    else if (indexPath.row == 3)
    {
        WeXAboutUsWebViewController *ctrl = [[WeXAboutUsWebViewController alloc] init];
        [WeXHomePushService pushFromVC:self toVC:ctrl];
    }
    
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    cell.selected = NO;
}

- (void)reSetFooterView{
    
    UIView *footerView = [[UIView alloc]initWithFrame:CGRectMake(0, kScreenHeight-52, kScreenWidth,52)];
    
    UIView *deleteView = [[UIView alloc]initWithFrame:CGRectMake(0,0, kScreenWidth, 52)];
    deleteView.backgroundColor = [UIColor whiteColor];
    [footerView addSubview:deleteView];
    UILabel *defaultLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, 52)];
    defaultLabel.textColor = [UIColor whiteColor];
    defaultLabel.backgroundColor = ColorWithHex(0x7B40FF);
    defaultLabel.font = [UIFont systemFontOfSize:18];
    defaultLabel.textAlignment =  NSTextAlignmentCenter;
    defaultLabel.text = @"删除钱包";
    [deleteView addSubview:defaultLabel];
    defaultLabel.userInteractionEnabled = YES;
    UITapGestureRecognizer *twoTapGes = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(deleteClick)];
    [defaultLabel addGestureRecognizer:twoTapGes];
    
    [self.view addSubview:footerView];
}

- (void)deleteClick{
//    [(AppDelegate *)[UIApplication sharedApplication].delegate resetRootWindowController];
    [WeXHomePushService pushFromVC:self toVC:[WeXPassportDeleteViewController new]];
//    WeXPassportDeleteViewController *ctrl = [[WeXPassportDeleteViewController alloc] init];
//    [self.navigationController pushViewController:ctrl animated:YES];
}


#pragma mark - WeXPasswordManagerDelegate
- (void)passwordManagerDidSetPasswordSuccess
{
//    _safetyDescriptionLabel.text = WeXLocalizedString(@"已开启");
    _safeSwitch.on = YES;
}

- (void)passwordManagerVerifySuccess{
//    _safetyDescriptionLabel.text = WeXLocalizedString(@"已关闭");
     _safeSwitch.on = NO;
    
    [self deletePassword];
    //关闭秘密后，显示提示
    [self showToastInfo];
}

- (void)passwordManagerVerifyException
{
//    _safetyDescriptionLabel.text = WeXLocalizedString(@"已关闭");
     _safeSwitch.on = NO;
}

- (void)deletePassword{
    //删除passport中密码
    _model = [WexCommonFunc getPassport];
    _model.numberPassword = nil;
    _model.gesturePassword = nil;
    _model.passwordType = WeXPasswordTypeNone;
    [WexCommonFunc savePassport:_model];
}

- (void)showToastInfo{
    
    [WeXPorgressHUD showText:WeXLocalizedString(@"关闭本地安全保护成功，您可以开启其他安全保护方式。") onView:self.view];
}

- (void)switchAction:(UISwitch *)sender{
    
    if (sender.on == YES) {
        sender.on = NO;
    }else{
        sender.on = YES;
    }
    
    _model = [WexCommonFunc getPassport];
    if (_model.passwordType == WeXPasswordTypeNone) {
        WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeCreate parentController:self];
        manager.delegate = self;
        [manager loadPassword];
        _manager = manager;
    }
    else {
        WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeVerify parentController:self];
        manager.delegate = self;
        [manager loadPassword];
        _manager = manager;
    }
    
 
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
