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

@interface WeXNewCardSettingViewController ()<UITableViewDelegate,UITableViewDataSource>

{
    UITableView *_tableView;    
}

@end

@implementation WeXNewCardSettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.title = WeXLocalizedString(@"设置");
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    
    // Do any additional setup after loading the view.
}

//初始化滚动视图
-(void)setupSubViews{
    _tableView = [[UITableView alloc] init];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = ColorWithHex(0xF8F8FF);
//    _tableView.tableFooterView = [UIView new];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self reSetTableViewFooterView];

    [self.view addSubview:_tableView];
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+5);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(self.view);
    }];
    
}

//section的高度

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 22;
}

//section底部间距
- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return CGFLOAT_MIN;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    
    UIView *contentView = [[[UIView alloc] init] initWithFrame:CGRectMake(0, 0, kScreenWidth, 20)];
    contentView.backgroundColor =  ColorWithHex(0xF8F8FF) ;
    return contentView;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
  
    return 60.f;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 0) {
        return 3;
    }else if (section == 1){
        return 2;
    }else{
        return 1;
    }
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
    
        if (indexPath.section == 0 && indexPath.row == 0)
        {
           cell.titleLabel.text = WeXLocalizedString(@"钱包地址");
        }
        else if (indexPath.section == 0 && indexPath.row == 1) {
           cell.titleLabel.text = WeXLocalizedString(@"备份钱包");
        }
        else if (indexPath.section == 0 && indexPath.row == 2)
        {
            cell.titleLabel.text = WeXLocalizedString(@"WeXCardSettingVCChangeWalletPSD");
        }
        
        else if (indexPath.section == 1 && indexPath.row == 0)
        {
            cell.titleLabel.text = WeXLocalizedString(@"关于我们");
        }
        else if (indexPath.section == 1 && indexPath.row == 1)
        {
            cell.titleLabel.text = WeXLocalizedString(@"版本更新");
            NSDictionary *infoDict = [[NSBundle mainBundle] infoDictionary];
            NSString *content = [NSString stringWithFormat:@"%@%@",WeXLocalizedString(@"当前版本"), [infoDict objectForKey:@"CFBundleShortVersionString"]];
            cell.desLabel.text = content;
        }
    
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    if (indexPath.section == 0 && indexPath.row == 0) {
        WeXPassportLocationViewController *ctrl = [[WeXPassportLocationViewController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.section == 0 && indexPath.row == 1)
    {
        WeXPassportBackupViewController *ctrl = [[WeXPassportBackupViewController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.section == 0 && indexPath.row == 2) {
        
        WeXPassportModifyPasswordController *ctrl = [[WeXPassportModifyPasswordController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.section == 1 && indexPath.row == 0)
    {
        WeXAboutUsWebViewController *ctrl = [[WeXAboutUsWebViewController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.section == 1 && indexPath.row == 1)
    {
        WeXVersionUpdateManager *manager = [WeXVersionUpdateManager shareManager];
        [manager configVersionUpdateViewOnView:self.view isUpdate:false];
    }
    
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    cell.selected = NO;
}

- (void)reSetTableViewFooterView{
    
    UIView *footerView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, 82)];
    
    UIView *backgroudView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, 22)];
    backgroudView.backgroundColor =  ColorWithHex(0xF8F8FF);
    [footerView addSubview:backgroudView];
    
    UIView *deleteView = [[UIView alloc]initWithFrame:CGRectMake(0,22, kScreenWidth, 60)];
    deleteView.backgroundColor = [UIColor whiteColor];
    [footerView addSubview:deleteView];
    UILabel *defaultLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, 60)];
    defaultLabel.textColor = COLOR_LABEL_TITLE;
    defaultLabel.font = [UIFont systemFontOfSize:18];
    defaultLabel.textAlignment =  NSTextAlignmentCenter;
    defaultLabel.text = @"删除钱包";
    [deleteView addSubview:defaultLabel];
    defaultLabel.userInteractionEnabled = YES;
    UITapGestureRecognizer *twoTapGes = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(deleteClick)];
    [defaultLabel addGestureRecognizer:twoTapGes];
    _tableView.tableFooterView = footerView;
}

- (void)deleteClick{
    [(AppDelegate *)[UIApplication sharedApplication].delegate resetRootWindowController];
//    WeXPassportDeleteViewController *ctrl = [[WeXPassportDeleteViewController alloc] init];
//    [self.navigationController pushViewController:ctrl animated:YES];
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
