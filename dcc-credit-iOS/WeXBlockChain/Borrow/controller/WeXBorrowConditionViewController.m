//
//  WeXBorrowConditionViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//
#import "WeXBorrowConditionViewController.h"

#import "WeXCreditIDAuthenViewController.h"
#import "WeXCreditRealInformationAuthenController.h"
#import "WeXCreditBankCardAuthenController.h"
#import "WeXCreditMobileOperatorAuthenController.h"

#import "WeXCreditIDSuccessViewController.h"
#import "WeXCreditMobileOperatorSuccessController.h"
#import "WeXCreditBankSuccessController.h"

#import "WeXBorrowConditionCell.h"

#import "WeXCreditDccGetOrderDetailAdapter.h"
#import "WeXCreditDccMobileGetReportAdapter.h"
#import "WeXCreditDccGetOrderDataAdapter.h"

#import "WeXCreditHomeTransition.h"

#import "WeXCheckRSAPublickKeyManager.h"

#define  kCardHeight 180

@interface WeXBorrowConditionViewController ()<UITableViewDelegate,UITableViewDataSource,UIScrollViewDelegate>
{
    UITableView *_tableView;
    NSString *_reportData;
}

@property (nonatomic,strong)WeXCreditDccGetOrderDetailAdapter *getOrderAdapter;
@property (nonatomic,strong)WeXCreditDccMobileGetReportAdapter *getMobileReportAdapter;
@property (nonatomic,strong)WeXCreditDccGetOrderDataAdapter *getOrderDataAdapter;
@end

@implementation WeXBorrowConditionViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.automaticallyAdjustsScrollViewInsets = NO;
    [self setNavigationNomalBackButtonType];
    self.navigationItem.title = WeXLocalizedString(@"所需资料");
    
    [self setupSubViews];
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeAuthening)
    {
        [WeXPorgressHUD showLoadingAddedTo:self.view];
        [self createGetMobileReportRequest];
    }
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [_tableView reloadData];

}


#pragma -mark 发送获取orderdata请求
- (void)createGetOrderDataRequest{
    _getOrderDataAdapter = [[WeXCreditDccGetOrderDataAdapter alloc] init];
    _getOrderDataAdapter.delegate = self;
    WeXCreditDccGetOrderDataParamModal* paramModal = [[WeXCreditDccGetOrderDataParamModal alloc] init];
    paramModal.address = [WexCommonFunc getFromAddress];
    paramModal.business = WEX_DCC_AUTHEN_BUSINESS_COMMUNICATION_LOG;
    [_getOrderDataAdapter run:paramModal];
}

#pragma -mark 查询订单详情
- (void)createGetMobileReportRequest
{
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    _getMobileReportAdapter = [[WeXCreditDccMobileGetReportAdapter alloc] init];
    _getMobileReportAdapter.delegate = self;
    WeXCreditDccMobileGetReportParamModal* paramModal = [[WeXCreditDccMobileGetReportParamModal alloc] init];
    paramModal.orderId = model.mobileAuthenOrderId;
    paramModal.address = [WexCommonFunc getFromAddress];
    paramModal.userName = model.userName;
    paramModal.certNo = model.userNumber;
    paramModal.phoneNo = model.mobileAuthenNumber;
    [_getMobileReportAdapter run:paramModal];
    
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getMobileReportAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXCreditDccMobileGetReportResponseModal *model = (WeXCreditDccMobileGetReportResponseModal *)response;
            NSLog(@"model=%@",model);
            if ([model.fail isEqualToString:@"1"]) {
                [WeXPorgressHUD hideLoading];
                [WeXPorgressHUD showText:WeXLocalizedString(@"手机运行商认证失败!") onView:self.view];
                [self clearPassportCacheMobileAuthenData];
            }
            else
            {
                if ([model.isComplete isEqualToString:@"Y"]) {
                    _reportData = model.reportData;
                    [self createGetOrderDataRequest];
                    [WexCommonFunc saveToFile:WEX_MOBILE_AUTHEN_REPORT_KEY content:_reportData];
                }
                else
                {
                    [WeXPorgressHUD hideLoading];
                }
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getOrderDataAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXCreditDccGetOrderDataResponseModal *responseModel =(WeXCreditDccGetOrderDataResponseModal *)response;
            NSLog(@"%@",responseModel);
            NSData *digest = [[NSData alloc] initWithBase64EncodedString:responseModel.content.digest1 options:0];
            NSData *reportData = [[NSData alloc] initWithBase64EncodedString:_reportData options:0];
            NSData *reportShaData = [WexCommonFunc dataShaSHAWithData:reportData];
            if ([digest isEqualToData:reportShaData]) {
                [WeXPorgressHUD showText:WeXLocalizedString(@"手机运行商认证成功!") onView:self.view];
                [self updatePassportCacheMobileAuthenData];
                
            }
            else
            {
                [WeXPorgressHUD showText:WeXLocalizedString(@"手机运行商认证失败!") onView:self.view];
                [self clearPassportCacheMobileAuthenData];
            }
            [_tableView reloadData];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
}

- (void)updatePassportCacheMobileAuthenData
{
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    model.mobileAuthenStatus = WeXCreditMobileOperatorAuthenStatusTypeSuccess;
    [WexCommonFunc savePassport:model];
}

- (void)clearPassportCacheMobileAuthenData
{
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    model.mobileAuthenStatus = WeXCreditMobileOperatorAuthenStatusTypeNone;
    model.mobileAuthenNumber = nil;
    [WexCommonFunc savePassport:model];
}


//初始化滚动视图
-(void)setupSubViews{
    UIButton *borrowBtn = [WeXCustomButton button];
    [borrowBtn setTitle:WeXLocalizedString(@"继续借币") forState:UIControlStateNormal];
    [borrowBtn addTarget:self action:@selector(borrowBtnClick) forControlEvents:UIControlEventTouchUpInside];
    borrowBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:borrowBtn];
    [borrowBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.bottom.equalTo(self.view).offset(-20);
        make.height.equalTo(@40);
    }];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight-65) style:UITableViewStyleGrouped];;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 100;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    
}

- (void)borrowBtnClick
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return self.datasArray.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    WeXBorrowConditionCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXBorrowConditionCell" owner:self options:nil] firstObject];
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backView.layer.cornerRadius = 12;
    cell.backView.layer.masksToBounds = YES;
    cell.backView.layer.borderWidth = 1;
    cell.backView.layer.borderColor =ColorWithHex(0xdcdcdc).CGColor;
    cell.backView.backgroundColor = [UIColor whiteColor];
    cell.titleLabel.textColor = COLOR_LABEL_DESCRIPTION;

    NSString *itemStr = self.datasArray[indexPath.section];
    if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_ID]) {
        cell.titleLabel.text = WeXLocalizedString(@"身份证认证");
        cell.refreshImageView.hidden = YES;
        cell.statusLabel.hidden = NO;
        if (model.idAuthenStatus == WeXCreditIDAuthenStatusTypeNone) {
            cell.statusLabel.text = WeXLocalizedString(@"去认证");
            cell.statusLabel.textColor = [UIColor whiteColor];
            cell.statusLabel.backgroundColor = COLOR_THEME_ALL;
        }
        else if (model.idAuthenStatus == WeXCreditIDAuthenStatusTypeSuccess)
        {
            cell.statusLabel.text = WeXLocalizedString(@"已认证");
            cell.statusLabel.textColor = COLOR_LABEL_DESCRIPTION;
            cell.statusLabel.backgroundColor = [UIColor whiteColor];
        }
    }
    else if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_BANK_CARD])
    {
        cell.titleLabel.text = WeXLocalizedString(@"银行卡认证");
        cell.refreshImageView.hidden = YES;
        cell.statusLabel.hidden = NO;
        if (model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeNone) {
            cell.statusLabel.text = WeXLocalizedString(@"去认证");
            cell.statusLabel.textColor = [UIColor whiteColor];
            cell.statusLabel.backgroundColor = COLOR_THEME_ALL;
        }
        else if (model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeSuccess)
        {
            cell.statusLabel.text = WeXLocalizedString(@"已认证");
            cell.statusLabel.textColor = COLOR_LABEL_DESCRIPTION;
            cell.statusLabel.backgroundColor = [UIColor whiteColor];
        }
    
    }
    else if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_COMMUNICATION_LOG])
    {
        cell.titleLabel.text = WeXLocalizedString(@"运营商认证");
        cell.refreshImageView.hidden = YES;
        cell.statusLabel.hidden = NO;
        if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeNone) {
            cell.statusLabel.text = WeXLocalizedString(@"去认证");
            cell.statusLabel.textColor = [UIColor whiteColor];
            cell.statusLabel.backgroundColor = COLOR_THEME_ALL;
        }
        else if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeAuthening)
        {
            cell.statusLabel.hidden = YES;
            cell.refreshImageView.hidden = NO;
            [self benginRefreshWithImageView:cell.refreshImageView];
        }
        else if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeSuccess)
        {
            cell.statusLabel.text = WeXLocalizedString(@"已认证");
            cell.statusLabel.textColor = COLOR_LABEL_DESCRIPTION;
            cell.statusLabel.backgroundColor = [UIColor whiteColor];
        }//2018.8.23 所需资料修改
        else if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeInvalid) {
            cell.statusLabel.text = WeXLocalizedString(@"去认证");
            cell.statusLabel.textColor = [UIColor whiteColor];
            cell.statusLabel.backgroundColor = COLOR_THEME_ALL;
        }
    }
  
    return cell;

}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXCreditAuthenProcessManager *processManager = [WeXCreditAuthenProcessManager shareManager];
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    NSString *itemStr = self.datasArray[indexPath.section];
    if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_ID]) {
        if (model.idAuthenStatus == WeXCreditIDAuthenStatusTypeNone) {
            processManager.idAuthenProcessType = WeXIDAuthenProcessTypeNeedConditon;
            WeXCreditIDAuthenViewController *ctrl = [[WeXCreditIDAuthenViewController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
    }
    else if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_BANK_CARD])
    {
        if (model.bankAuthenStatus == WeXCreditBankAuthenStatusTypeNone) {
            //        先判断是否完成实名认证
            if (model.idAuthenStatus != WeXCreditIDAuthenStatusTypeSuccess) {
                [WeXPorgressHUD showText:WeXLocalizedString(@"请先完成实名认证") onView:self.view];
                return;
            }
            
            processManager.bankAuthenProcessType = WeXBankAuthenProcessTypeNeedConditon;
            WeXCreditBankCardAuthenController *ctrl = [[WeXCreditBankCardAuthenController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
    }
    else if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_COMMUNICATION_LOG])
    {
        if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeNone) {
            //先判断是否完成实名认证
            if (model.idAuthenStatus != WeXCreditIDAuthenStatusTypeSuccess) {
                [WeXPorgressHUD showText:WeXLocalizedString(@"请先完成实名认证") onView:self.view];
                return;
            }
            processManager.mobileAuthenProcessType = WeXMobileAuthenProcessTypeNeedConditon;
            WeXCreditMobileOperatorAuthenController *ctrl = [[WeXCreditMobileOperatorAuthenController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
        //2018.8.23 所需资料修改
        if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeNone
            || model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeInvalid) {
            processManager.mobileAuthenProcessType = WeXMobileAuthenProcessTypeNeedConditon;
            if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeNone) {
                processManager.mobileAuthenProcessType = WeXMobileAuthenProcessTypeNeedConditon;
            } else if (model.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeInvalid) {
                processManager.mobileAuthenProcessType = WeXMobileAuthenProcessTypeMyCredit;
            }
            WeXCreditMobileOperatorAuthenController *ctrl = [[WeXCreditMobileOperatorAuthenController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
     
    }
 
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 10;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 10;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    return nil;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return nil;
}


- (void)benginRefreshWithImageView:(UIImageView *)iamgeView{
    
    CABasicAnimation *animtion = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    animtion.toValue = [NSNumber numberWithFloat:M_PI *2];
    animtion.duration = 3;
    animtion.repeatCount = CGFLOAT_MAX;
    animtion.removedOnCompletion = NO;
    animtion.fillMode = kCAFillModeForwards;
    [iamgeView.layer addAnimation:animtion forKey:nil];
    
    
}


@end
