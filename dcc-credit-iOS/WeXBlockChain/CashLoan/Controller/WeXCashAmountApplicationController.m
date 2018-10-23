//
//  WeXCashAmountApplicationController.m
//  WeXBlockChain
//
//  Created by wh on 2018/10/12.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCashAmountApplicationController.h"
#import "WeXNewCreditMessageCell.h"
#import "WeXCreditIDAuthenViewController.h"
#import "WeXCreditRealInformationAuthenController.h"
#import "WeXCreditBankCardAuthenController.h"
#import "WeXCreditMobileOperatorAuthenController.h"

#import "WeXCreditIDSuccessViewController.h"
#import "WeXCreditMobileOperatorSuccessController.h"
#import "WeXCreditBankSuccessController.h"
#import "WeXCheckRSAPublickKeyManager.h"


static NSString * const kNewCreditMessageCellID = @"WeXNewCreditMessageCellID";
#define COLOR_CREDITED_ALL  ColorWithHex(0x7ED321)
#define COLOR_NEVERCREDIT_ALL  ColorWithHex(0xC009FF)

@interface WeXCashAmountApplicationController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic,strong)UITableView *tableView;

@property (nonatomic,strong)UIButton *commitBtn;

@property (nonatomic,strong)UILabel *identifyFeeLabel;
@property (nonatomic,strong)UILabel *bankFeeLabel;
@property (nonatomic,strong)UILabel *sameCowFeeLabel;
@property (nonatomic,strong)UILabel *myDccNumLabel;

@end

@implementation WeXCashAmountApplicationController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    
    // Do any additional setup after loading the view.
}

//初始化滚动视图
-(void)setupSubViews{
    
    self.navigationItem.title = @"额度申请";
//    self.view.backgroundColor = ColorWithHex(0xF5F5FD);
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight-70) style:UITableViewStyleGrouped];;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 100;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
    if (@available(iOS 11.0, *)) {
        UITableView.appearance.estimatedRowHeight = 0;
        UITableView.appearance.estimatedSectionFooterHeight = 0;
        UITableView.appearance.estimatedSectionHeaderHeight = 0;
    }
    
    [_tableView registerClass:[WeXNewCreditMessageCell class] forCellReuseIdentifier:kNewCreditMessageCellID];
//    //headerView
//    UIView *headerView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, 40)];
//    headerView.backgroundColor = [UIColor clearColor];
//    _tableView.tableHeaderView = headerView;
//    UILabel *defaultLabel = [[UILabel alloc]init];
//    defaultLabel.textColor = ColorWithHex(0x000000);
//    defaultLabel.font = [UIFont systemFontOfSize:17.0f];
//    defaultLabel.text = @"基本信息认证";
//    [headerView addSubview:defaultLabel];
//    [defaultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.mas_equalTo(13);
//        make.left.mas_equalTo(10);
//        make.width.mas_equalTo(200);
//        make.height.mas_equalTo(30);
//    }];
    //footerView
    UIView *footerView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, 80)];
    footerView.backgroundColor = ColorWithHex(0xF5F5FD);
    _tableView.tableFooterView = footerView;
    _myDccNumLabel = [[UILabel alloc]init];
    _myDccNumLabel.textColor = ColorWithHex(0x9B9B9B);
    _myDccNumLabel.font = [UIFont systemFontOfSize:14.0f];
    _myDccNumLabel.text = @"当前DCC持有量:4500DCC";
    [footerView addSubview:_myDccNumLabel];
    [_myDccNumLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(30);
        make.left.mas_equalTo(14);
        make.width.mas_equalTo(220);
        make.height.mas_equalTo(30);
    }];
    
    UILabel *noDccLabel = [[UILabel alloc]init];
    noDccLabel.textColor = ColorWithHex(0x7B40FF);
    noDccLabel.font = [UIFont systemFontOfSize:14.0f];
//    noDccLabel.text = @"DCC不足?";
    NSDictionary *attribtDic = @{NSUnderlineStyleAttributeName: [NSNumber numberWithInteger:NSUnderlineStyleSingle]};
    NSMutableAttributedString *attribtStr = [[NSMutableAttributedString alloc]initWithString:@"DCC不足?" attributes:attribtDic];
    noDccLabel.attributedText = attribtStr;
    [footerView addSubview:noDccLabel];
    [noDccLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(30);
        make.right.mas_equalTo(-14);
        make.width.mas_equalTo(80);
        make.height.mas_equalTo(30);
    }];
    
    noDccLabel.userInteractionEnabled = YES;
    UITapGestureRecognizer *twoTapGes = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(goNoDccClick)];
    [noDccLabel addGestureRecognizer:twoTapGes];
    
    UIView *bottomView = [[UIView alloc]init];
    bottomView.backgroundColor = ColorWithHex(0xF5F5FD);
    [self.view addSubview:bottomView];
    [bottomView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(0);
        make.trailing.leading.mas_equalTo(0);
        make.height.mas_equalTo(70);
    }];
    
    _commitBtn = [WeXCustomButton button];
    [_commitBtn setTitle:WeXLocalizedString(@"提交") forState:UIControlStateNormal];
//    [_commitBtn setBackgroundColor:ColorWithHex(0x7B40FF)];
    [_commitBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [_commitBtn addTarget:self action:@selector(goCommitClick) forControlEvents:UIControlEventTouchUpInside];
    _commitBtn.layer.cornerRadius = 6;
    _commitBtn.clipsToBounds = YES;
    [bottomView addSubview:_commitBtn];
    [_commitBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(-20);
        make.left.mas_equalTo(15);
        make.right.mas_equalTo(-15);
        make.height.mas_equalTo(40);
    }];
    
    
//    UILabel *cloudLabel = [[UILabel alloc]init];
//    cloudLabel.font = [UIFont systemFontOfSize:14];
//    cloudLabel.textColor = ColorWithHex(0x5756B3);
//    cloudLabel.backgroundColor = [UIColor whiteColor];
//    //创建富文本
//    NSMutableAttributedString *attri = [[NSMutableAttributedString alloc] initWithString:@"认证数据云存储"];
//    //NSTextAttachment可以将要插入的图片作为特殊字符处理
//    NSTextAttachment *attch = [[NSTextAttachment alloc] init];
//    //定义图片内容及位置和大小
//    attch.image = [UIImage imageNamed:@"8G"];
//    attch.bounds = CGRectMake(-2, -2, 21, 15);
//    //NSTextAttachment可以将要插入的图片作为特殊字符处理
//    //创建带有图片的富文本
//    NSAttributedString *string = [NSAttributedString attributedStringWithAttachment:attch];
//    //将图片放在第一位
//    [attri insertAttributedString:string atIndex:0];
//    NSTextAttachment *twoAttch = [[NSTextAttachment alloc] init];
//    //定义图片内容及位置和大小
//    twoAttch.image = [UIImage imageNamed:@"dcc_arrow_right"];
//    twoAttch.bounds = CGRectMake(2, 0, 8, 8);
//    //NSTextAttachment可以将要插入的图片作为特殊字符处理
//    //创建带有图片的富文本
//    NSAttributedString *twoString = [NSAttributedString attributedStringWithAttachment:twoAttch];
//    // 将图片放在最后一位
//    [attri appendAttributedString:twoString];
//    //用label的attributedText属性来使用富文本
//    cloudLabel.attributedText = attri;
//    [self.view addSubview:cloudLabel];
//
//    [cloudLabel mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.centerX.equalTo(self.view).offset(0);
//        make.bottom.mas_equalTo(-10);
//        make.height.mas_equalTo(30);
//    }];
//
//    cloudLabel.userInteractionEnabled = YES;
//    UITapGestureRecognizer *twoTapGes = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(twoTapGesClick)];
//    [cloudLabel addGestureRecognizer:twoTapGes];
    
}

- (void)goCommitClick{
    
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 4;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return 74;
    
}

//section的高度
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 50;
}

//section底部间距
- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return CGFLOAT_MIN;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    
    UIView *contentView = [[[UIView alloc] init] initWithFrame:CGRectMake(0, 0, kScreenWidth, 50)];
    contentView.backgroundColor =  ColorWithHex(0xF5F5FD) ;
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(10, 8, 120, 40)];
    label.backgroundColor = [UIColor clearColor];
    label.font = [UIFont systemFontOfSize:17.0f];
    label.textColor = ColorWithHex(0x000000);
    label.text = @"基本信息认证";
   
    [contentView addSubview:label];
    return contentView;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    
        WeXNewCreditMessageCell *cell = [tableView dequeueReusableCellWithIdentifier:kNewCreditMessageCellID];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        if (indexPath.row == 0) {
            cell.defaultImg.image = [UIImage imageNamed:@"identifyes"];
            cell.nameTitleLabel.text = @"实名认证";
            cell.statusDescribeLabel.text = @"认证费用:xxDcc";
            if (model.idAuthenStatus == WeXCreditIDAuthenStatusTypeNone) {
                [cell.defaultStatusBtn setTitle:@"去认证" forState:UIControlStateNormal];
                [cell.defaultStatusBtn setBackgroundColor:COLOR_CREDITED_ALL];
            }else if (model.idAuthenStatus == WeXCreditIDAuthenStatusTypeSuccess){
                [cell.defaultStatusBtn setTitle:@"已认证" forState:UIControlStateNormal];
                [cell.defaultStatusBtn setBackgroundColor:COLOR_NEVERCREDIT_ALL];
            }else if (model.idAuthenStatus == WeXCreditIDAuthenStatusTypeInvalid){
                [cell.defaultStatusBtn setTitle:@"已过期" forState:UIControlStateNormal];
                [cell.defaultStatusBtn setBackgroundColor:COLOR_CREDITED_ALL];
            }
            
            
        }else if (indexPath.row == 1) {
            cell.defaultImg.image = [UIImage imageNamed:@"bankCards"];
            cell.nameTitleLabel.text = @"银行卡认证";
            cell.statusDescribeLabel.text = @"认证费用:xxDcc";
            if (model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeNone) {
                [cell.defaultStatusBtn setTitle:@"去认证" forState:UIControlStateNormal];
                [cell.defaultStatusBtn setBackgroundColor:COLOR_CREDITED_ALL];
            }else if (model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeSuccess){
                [cell.defaultStatusBtn setTitle:@"已认证" forState:UIControlStateNormal];
                [cell.defaultStatusBtn setBackgroundColor:COLOR_NEVERCREDIT_ALL];
            }else if (model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeInvalid){
                [cell.defaultStatusBtn setTitle:@"已过期" forState:UIControlStateNormal];
                [cell.defaultStatusBtn setBackgroundColor:COLOR_CREDITED_ALL];
            }
  
        }else if (indexPath.row == 2) {
            cell.defaultImg.image = [UIImage imageNamed:@"phoneOperator"];
            cell.nameTitleLabel.text = @"同牛运营商认证";
            cell.statusDescribeLabel.text = @"认证费用:xxDcc";
            if (model.sameCowMobileAuthenStatus == WeXSameCowMobileOperatorAuthenStatusTypeNone) {
                [cell.defaultStatusBtn setTitle:@"去认证" forState:UIControlStateNormal];
                [cell.defaultStatusBtn setBackgroundColor:COLOR_CREDITED_ALL];
                //                [cell refreshData];
            }else if (model.sameCowMobileAuthenStatus == WeXSameCowMobileOperatorAuthenStatusTypeAuthening){
                [cell.defaultStatusBtn setTitle:@"认证中" forState:UIControlStateNormal];
                //                [cell refreshData];
            }else if (model.sameCowMobileAuthenStatus == WeXSameCowMobileOperatorAuthenStatusTypeSuccess){
                [cell.defaultStatusBtn setTitle:@"已认证" forState:UIControlStateNormal];
                [cell.defaultStatusBtn setBackgroundColor:COLOR_NEVERCREDIT_ALL];
                //                [cell refreshData];
            }else if (model.sameCowMobileAuthenStatus == WeXSameCowMobileOperatorAuthenStatusTypeInvalid){
                [cell.defaultStatusBtn setTitle:@"已过期" forState:UIControlStateNormal];
                [cell.defaultStatusBtn setBackgroundColor:COLOR_CREDITED_ALL];
                //                [cell refreshData];
            }
            
        }else if (indexPath.row == 3) {
            cell.defaultImg.image = [UIImage imageNamed:@"loanReports"];
            cell.nameTitleLabel.text = @"信息完善";
//            cell.statusDescribeLabel.text = @"借贷记录全整合";
            [cell.defaultStatusBtn setTitle:@"去完善" forState:UIControlStateNormal];
            [cell.defaultStatusBtn setBackgroundColor:ColorWithHex(0xC009FF)];
        }
        [cell refreshData];
        return cell;

}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    WeXCreditAuthenProcessManager *processManager = [WeXCreditAuthenProcessManager shareManager];
    
    if ( indexPath.row == 0) {
        if (model.idAuthenStatus == WeXCreditIDAuthenStatusTypeNone){
            
            
            WeXCheckRSAPublickKeyManager *checkManager = [WeXCheckRSAPublickKeyManager shareManager];
            [checkManager createCheckPublickKeyRequestWithParentController:self responseBlock:^(BOOL result) {
                if (result) {
                    processManager.idAuthenProcessType = WeXIDAuthenProcessTypeMyCredit;
                
                    WeXCreditIDAuthenViewController *ctrl = [[WeXCreditIDAuthenViewController alloc] init];
                    [self.navigationController pushViewController:ctrl animated:YES];
                }
            }];
            
        }
        // MARK: - 身份证认证
        else if (model.idAuthenStatus == WeXCreditIDAuthenStatusTypeSuccess || model.idAuthenStatus == WeXCreditIDAuthenStatusTypeInvalid)
        {
            WeXCreditIDSuccessViewController *ctrl = [[WeXCreditIDSuccessViewController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
    }
    
    else if (indexPath.row == 1)
    {
        //      先判断是否完成实名认证
        if (model.idAuthenStatus != WeXCreditIDAuthenStatusTypeSuccess && model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeNone) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"请先完成实名认证") onView:self.view];
            return;
        }
        
        if (model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeNone){
            WeXCheckRSAPublickKeyManager *checkManager = [WeXCheckRSAPublickKeyManager shareManager];
            [checkManager createCheckPublickKeyRequestWithParentController:self responseBlock:^(BOOL result) {
                if (result) {
                    // MARK: - 银行卡认证
                    processManager.bankAuthenProcessType = WeXBankAuthenProcessTypeMyCredit;
                    WeXCreditBankCardAuthenController *ctrl = [[WeXCreditBankCardAuthenController alloc] init];
                    [self.navigationController pushViewController:ctrl animated:YES];
                }
            }];
        }
        else if (model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeSuccess || model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeInvalid)
        {
            WeXCreditBankSuccessController *ctrl = [[WeXCreditBankSuccessController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
    }

    else if (indexPath.row == 2)
    {
        //    先判断是否完成实名认证
        if (model.idAuthenStatus != WeXCreditIDAuthenStatusTypeSuccess && model.sameCowMobileAuthenStatus != WeXSameCowMobileOperatorAuthenStatusTypeSuccess) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"请先完成实名认证") onView:self.view];
            return;
        }
        
        if (model.sameCowMobileAuthenStatus == WeXSameCowMobileOperatorAuthenStatusTypeNone){
            WeXCheckRSAPublickKeyManager *checkManager = [WeXCheckRSAPublickKeyManager shareManager];
            [checkManager createCheckPublickKeyRequestWithParentController:self responseBlock:^(BOOL result) {
                // MARK: - 手机运营商认证
                if (result) {
                    processManager.mobileAuthenProcessType = WeXMobileAuthenProcessTypeMyCredit;
                    
                    WeXCreditMobileOperatorAuthenController *ctrl = [[WeXCreditMobileOperatorAuthenController alloc] init];
                    ctrl.creditMobileType = WeXCreditMobileOperatorTypeSameCow;
                    [self.navigationController pushViewController:ctrl animated:YES];
                    
                }
            }];
            
        }
        // MARK: - 手机运营商认证结果页 (成功或者过期)
        else if (model.sameCowMobileAuthenStatus == WeXSameCowMobileOperatorAuthenStatusTypeSuccess || model.sameCowMobileAuthenStatus == WeXSameCowMobileOperatorAuthenStatusTypeInvalid)
        {
            WeXCreditMobileOperatorSuccessController *ctrl = [[WeXCreditMobileOperatorSuccessController alloc] init];
            ctrl.creditMobileType = WeXCreditMobileOperatorSuccessTypeSameCow;
            [self.navigationController pushViewController:ctrl animated:YES];
        }
    }
    else if ( indexPath.row == 3)//借贷报告
    {
        //前往信息完善界面
//        [WeXHomePushService pushFromVC:self toVC:[WeXLoanReportHomeViewController new]];
    }
}

- (void)goNoDccClick{
    
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
