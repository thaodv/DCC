//
//  WeXMyBorrowListViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyBorrowListViewController.h"
#import "WeXMyBorrowListCell.h"
#import "WeXMyBorrowDetailViewController.h"
#import "WeXLoanQueryOrdersAdapter.h"
#import "MJRefresh.h"
#import "WeXBorrowCreatedStatusToastView.h"

#import "WeXBorrowGetContractAddressAdapter.h"
#import "WeXGetReceiptResult2Adapter.h"
#import "WeXLoanCancelAdapter.h"

#import "WeXCreditBorrowUSDTViewController.h"

static NSString *const pageSize = @"8";

static const CGFloat kBackImageHeightWidthRatio = 82.0/339.0;


@interface WeXMyBorrowListViewController ()<UITableViewDelegate,UITableViewDataSource,WeXBorrowCreatedStatusToastViewDelegate>
{
    UITableView *_tableView;
    MJRefreshNormalHeader *_refreshHeader;
    MJRefreshBackFooter *_refreshFooter;
    
    UILabel *_noRecordLabel;
    
    NSString *_cancelRawTransaction;
    
    NSString *_cancelTxHash;
    
    NSString *_lastOrderId;
    
    NSString *_contractAddress;//合约地址

    NSInteger _cancelRequestCount;//查询上链结果请求的次数
}

@property (nonatomic,strong)NSMutableArray *datasArray;
@property (nonatomic,strong)WeXLoanQueryOrdersAdapter *getOrdersAdapter;
@property (nonatomic,strong)WeXBorrowGetContractAddressAdapter *getContractAddressAdapter;
@property (nonatomic,strong)WeXGetReceiptResult2Adapter *getCancelReceiptAdapter;
@property (nonatomic,strong)WeXLoanCancelAdapter *cancelAdapter;
@end

@implementation WeXMyBorrowListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"MyBorrowListVCTitle");
    [self setupSubViews];
    [self setNavigationNomalBackButtonType];
    
}

- (void)backItemClick {
    if ([self.navigationController.viewControllers containsObject:[WeXCreditBorrowUSDTViewController new]]) {
        for (UIViewController *ctrl in self.navigationController.viewControllers) {
            if ([ctrl isKindOfClass:[WeXCreditBorrowUSDTViewController class]]) {
                [self.navigationController popToViewController:ctrl animated:YES];
            }
        }
    } else {
        [self.navigationController popViewControllerAnimated:true];
    }
}

- (void)createGetOrdersRequest{
    _getOrdersAdapter = [[WeXLoanQueryOrdersAdapter alloc] init];
    _getOrdersAdapter.delegate = self;
    _getOrdersAdapter.number = 0;
    WeXLoanQueryOrdersParamModal *paramModel = [[WeXLoanQueryOrdersParamModal alloc] init];
    paramModel.number = @"0";
    paramModel.size = pageSize;
    [_getOrdersAdapter run:paramModel];
}

- (void)loadMoreOrdersRequest
{
    _getOrdersAdapter.number += 1;
    WeXLoanQueryOrdersParamModal *paramModel = [[WeXLoanQueryOrdersParamModal alloc] init];
    paramModel.number = [NSString stringWithFormat:@"%ld",_getOrdersAdapter.number];
    paramModel.size = pageSize;
    [_getOrdersAdapter run:paramModel];
}


#pragma -mark 发送获取合约地址请求
- (void)createGetContractAddressRequest{
    _getContractAddressAdapter = [[WeXBorrowGetContractAddressAdapter alloc] init];
    _getContractAddressAdapter.delegate = self;
    WeXGetContractAddressParamModal* paramModal = [[WeXGetContractAddressParamModal alloc] init];
    [_getContractAddressAdapter run:paramModal];
}

#pragma -mark 查询上链结果请求
- (void)createCancelReceiptResultRequest{
    _getCancelReceiptAdapter = [[WeXGetReceiptResult2Adapter alloc] init];
    _getCancelReceiptAdapter.delegate = self;
    WeXGetReceiptResult2ParamModal* paramModal = [[WeXGetReceiptResult2ParamModal alloc] init];
    paramModal.txHash = _cancelTxHash;
    [_getCancelReceiptAdapter run:paramModal];
}

#pragma -mark 发送Cancel请求
- (void)createCancelRequest{
    _cancelAdapter = [[WeXLoanCancelAdapter alloc] init];
    _cancelAdapter.delegate = self;
    WeXLoanCancelParamModal* paramModal = [[WeXLoanCancelParamModal alloc] init];
    paramModal.chainOrderId = _lastOrderId;
    [_cancelAdapter run:paramModal];
}

- (void)getCancelRawTranstion
{
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response)
     {
         if(response!=nil)
         {
             NSError* error=response;
             NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
             return;
         }
         // 合约定义说明
         NSString* abiJson= WEX_DCC_ABI_BORROW_CANCEL;
         // 合约参数值
         NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\']",_lastOrderId];
         WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
         // 合约地址
         NSString* abiAddress = _contractAddress;
         WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
         [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:cacheModel.walletPrivateKey responseBlock:^(id response)
          {
              NSLog(@"rawTransaction=%@",response);
              _cancelRawTransaction = response;
              [self createCancelRequest];
          }];
         
     }];
}


#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getOrdersAdapter)
    {
        [_refreshHeader endRefreshing];
        [_refreshFooter endRefreshing];
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXLoanQueryOrdersResponseModal *model = (WeXLoanQueryOrdersResponseModal *)response;
            WEXNSLOG(@"model=%@",model);
            
            if (_getOrdersAdapter.number == 0 && model.items.count == 0) {
                _noRecordLabel.hidden = NO;
            }
            else
            {
                _noRecordLabel.hidden = YES;
            }
        
            if (_getOrdersAdapter.number == 0 && model.items.count > 0) {
                _refreshFooter = [MJRefreshBackNormalFooter footerWithRefreshingTarget:self refreshingAction:@selector(loadMoreOrdersRequest)];
                _tableView.mj_footer = _refreshFooter;
            }
            if (_getOrdersAdapter.number == 0) {
                self.datasArray = model.items;
            }
            else
            {
                [self.datasArray addObjectsFromArray:model.items];
            }
            
            if (model.items.count == 0) {
                 [_refreshFooter endRefreshingWithNoMoreData];
            }
        }
        else if ([headModel.systemCode isEqualToString:@"SUCCESS"])
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:headModel.message onView:self.view];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
        [_tableView reloadData];

    }
    else if (adapter == _getContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _contractAddress = model.result;
            //合约地址请求成功
            if (_contractAddress) {
                [self getCancelRawTranstion];
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getCancelReceiptAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WeXGetReceiptResult2ResponseModal *model = (WeXGetReceiptResult2ResponseModal *)response;
            //上链成功
            if (model.hasReceipt) {
                [WeXPorgressHUD hideLoading];
                if (model.approximatelySuccess) {
                    [WeXPorgressHUD showText:WeXLocalizedString(@"订单取消成功!") onView:self.view];
                    [_refreshHeader beginRefreshing];
                }
                else{
                    [WeXPorgressHUD showText:WeXLocalizedString(@"订单取消失败!") onView:self.view];
                }
                _cancelRequestCount = 0;
            }
            else
            {
                if (_cancelRequestCount > 5) {
                    _cancelRequestCount = 0;
                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:WeXLocalizedString(@"认证失败") onView:self.view];
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createCancelReceiptResultRequest];
                    _cancelRequestCount++;
                });
            }
        }
    }
    else if (adapter == _cancelAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXLoanCancelResponseModal *responseModel =(WeXLoanCancelResponseModal *)response;
            WEXNSLOG(@"%@",responseModel);
            [WeXPorgressHUD hideLoading];
           [WeXPorgressHUD showText:WeXLocalizedString(@"订单取消成功!") onView:self.view];
            [_refreshHeader beginRefreshing];
        }
        else if ([headModel.systemCode isEqualToString:@"SUCCESS"])
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:headModel.message onView:self.view];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
}


- (void)setupSubViews
{

    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight) style:UITableViewStyleGrouped];;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    if (@available(iOS 11.0, *)) {
        _tableView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    } else {
        // Fallback on earlier versions
    }
    
    UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, _tableView.frame.size.width, 30)];
    _tableView.tableFooterView= footerView;
    
    UILabel *bottomLabel = [[UILabel alloc] init];
    bottomLabel.hidden = YES;
    bottomLabel.text = WeXLocalizedString(@"暂无借币记录");
    bottomLabel.textAlignment = NSTextAlignmentCenter;
    bottomLabel.font = [UIFont systemFontOfSize:15];
    bottomLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [footerView addSubview:bottomLabel];
    [bottomLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(footerView).offset(0);
        make.leading.equalTo(footerView).offset(15);
        make.trailing.equalTo(footerView).offset(-15);
    }];
    _noRecordLabel = bottomLabel;
    
    _refreshHeader = [MJRefreshNormalHeader headerWithRefreshingTarget:self refreshingAction:@selector(createGetOrdersRequest)];
    _tableView.mj_header = _refreshHeader;
    _refreshHeader.lastUpdatedTimeLabel.hidden = YES;
    [_refreshHeader beginRefreshing];
    
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
    WeXMyBorrowListCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXMyBorrowListCell" owner:self options:nil] lastObject];
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    WeXLoanQueryOrdersResponseModal_item *model = self.datasArray[indexPath.section];
    cell.model = model;
    return cell;
    

}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXLoanQueryOrdersResponseModal_item *model = self.datasArray[indexPath.section];
    if ([model.status isEqualToString:WEX_LOAN_STATUS_CREATED])
    {
        WeXBorrowCreatedStatusToastView *statusView = [[WeXBorrowCreatedStatusToastView alloc] initWithFrame:self.view.frame];
        statusView.delegate = self;
        [self.view addSubview:statusView];
        
        _lastOrderId = model.orderId;
    }
    // MARK: - 借币详情
    else
    {
        WeXMyBorrowDetailViewController *ctrl = [[WeXMyBorrowDetailViewController alloc] init];
        ctrl.orderId = model.orderId;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 5;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 5;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    return nil;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return nil;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return (kScreenWidth-30)*kBackImageHeightWidthRatio;
    
}
#pragma mark -WeXBorrowCreatedStatusToastViewDelegate
- (void)borrowCreatedStatusToastViewDidClickCancelOrderButtoon
{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self createCancelRequest];
}



@end
