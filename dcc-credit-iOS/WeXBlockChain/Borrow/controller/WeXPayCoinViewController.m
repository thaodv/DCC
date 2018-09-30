//
//  WeXPayCoinViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/4.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPayCoinViewController.h"
#import "WeXPayCoinResultViewController.h"
#import "WeXLoanConfirmRepayAdapter.h"
#import "WeXLoanGetRepaymentBillAdapter.h"
#import "WeXWalletTransferViewController.h"
#import "NSString+WexTool.h"
#import "WeXRepaymentStatusView.h"
#import "WeXWalletTransferResultManager.h"
#import "WeXWalletAlertWithCancelButtonView.h"
#import "WeXConfirmRepaymentView.h"
#import "WeXDigitalAssetRLMModel.h"
@interface WeXPayCoinViewController ()
{
    WeXLoanGetRepaymentBillResponseModal *_billModel;
}

@property (nonatomic,strong)WeXLoanConfirmRepayAdapter *repayAdapter;
@property (nonatomic,strong)WeXLoanGetRepaymentBillAdapter *getBillAdapter;
//再次查询账单
@property (nonatomic,strong)WeXLoanGetRepaymentBillAdapter *getBillAdapterTwo;

//获取合约地址
@property (nonatomic, copy) NSString *contractAddress;
@property (nonatomic,strong) WeXRepaymentStatusView  * statusView;
//转账记录,本地存储的Model
@property (nonatomic,strong) WeXWalletTransferResultManager *refreshManager;
//本地是否已经有(未上链的)
@property (nonatomic,strong) WeXWalletTransferPendingModel *pendingModel;

@property (nonatomic, strong) WeXConfirmRepaymentView *confirmView;


@end

@implementation WeXPayCoinViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"还币");
    [self setNavigationNomalBackButtonType];
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self createGetBillRequest];
}

//获取合约地址
- (void)getCoinContractAddress:(void(^)(NSString *contractAddress))result {
    //ETH 不需要合约地址
    if ([_billModel.assetCode isEqualToString:@"ETH"]) {
        !result ? : result(nil);
    }
    
    RLMResults <WeXDigitalAssetRLMModel *>*results = [WeXDigitalAssetRLMModel allObjects];
    for (WeXDigitalAssetRLMModel *rlmModel in results) {
        if ([rlmModel.symbol isEqualToString:_billModel.assetCode]) {
            !result ? : result(rlmModel.contractAddress);
        }
    }
//    NSString * URL = [NSString stringWithFormat:@"%@token/2/getContractAddress/%@",WEX_BASE_AUTHEN_URL,_billModel.assetCode];
//    [[WeXNetworkCheckManager shareManager] getWithURL:URL complete:^(NSDictionary *dic, NSError *error) {
//        if (dic) {
//            WEXNSLOG(@"%@",dic);
//            self.contractAddress = [NSString stringWithFormat:@"%@",dic[@"result"]];
//            !result ? : result(self.contractAddress);
//        } else {
//            !result ? : result(nil);
//            WEXNSLOG(@"getCoinContractAddress:%@",error);
//        }
//    }];
}

- (void)configPendingTransferData {
//  如果有pendingModel 说明本地已经有一笔交易在上链中,判断收币地址如果相同则还币进行中,否则就是点击本钱包还币
    _refreshManager = [[WeXWalletTransferResultManager alloc] initWithTokenSymbol:_billModel.assetCode isPrivateChain:NO response:^{
        //查询到上链信息了(查询失败,或者成功)
        [self searchTxHash];
    }];
    [_refreshManager beginRefresh];
    _pendingModel = [_refreshManager getPendingModelWithSymbol:_billModel.assetCode];
    if (!_pendingModel) { //未有交易记录(没有还币也没交易)
        [WeXPorgressHUD hideLoading];
        [self setupSubViews];
    } else {
        [WeXPorgressHUD hideLoading];
        if ([_pendingModel.to isEqualToString:_billModel.repaymentAddress]) {
            //有还币订单 (还币中...)
            [self createStatusViewIsRunning:true];
        } else {
            [self setupSubViews];
            //可能不是还币,是其他转账功能
            //点击本钱包还币(提醒当前有笔交易还在进行中...)
        }
    }
}

//查询上链是否成功
- (void)searchTxHash {
    [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
        [[WXPassHelper instance] queryTransactionReceipt:_pendingModel.txhash responseBlock:^(id response) {
            NSLog(@"%@",response);
            if ([response isKindOfClass:[NSDictionary class]]) {
                // 上链成功
                [_refreshManager deletePendingModelWithSymbol:_billModel.assetCode];
                NSString *status = [response objectForKey:@"status"];
                //弹出还币成功页面
                if ([status isEqualToString:@"0x1"]) { //成功
                    [self removeStatusView];
                    [self createGetBillTwoRequest];
//                    [self createConfirmView];
                }
                else { //失败
                    [WeXPorgressHUD hideLoading];
                    [self removeStatusView];
                    [self createStatusViewIsRunning:false];
                }
            } else {
                [WeXPorgressHUD hideLoading];
                //查询交易状态未返回信息
                [self setupSubViews];
            }
        }];
    }];
}

- (void)removeStatusView {
    if (_statusView) {
        [_statusView removeFromSuperview];
        _statusView = nil;
    }
}

// MARK: - 创建还币状态图
- (void)createStatusViewIsRunning:(BOOL)isRunning {
    _statusView = [WeXRepaymentStatusView createRepaymentStatusView:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight - kNavgationBarHeight) status:WeXRepaymentStatusRunning];
    [_statusView setBackgroundColor:[UIColor whiteColor]];
    [_statusView bringSubviewToFront:self.view];
    [self.view addSubview:_statusView];
}
// MARK: - 初始化确认还币
- (void)createConfirmView {
    if (_confirmView) {
        [_confirmView removeFromSuperview];
        _confirmView = nil;
    }
    NSString *amount = [NSString stringWithFormat:@"%.4f",_billModel.amount];
    _confirmView = [WeXConfirmRepaymentView createConfirmRepayView:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight - kNavgationBarHeight) amount:amount symbol:_billModel.assetCode okEvent:^{
        [WeXPorgressHUD showLoadingAddedTo:self.view];
        [self createGetRepayRequest];
    }];
    [_confirmView setBackgroundColor:[UIColor whiteColor]];
    [_confirmView bringSubviewToFront:self.view];
    [self.view addSubview:_confirmView];
}
     

#pragma -mark 查询OrderDetail请求
- (void)createGetBillRequest{
    if (!_getBillAdapter) {
        _getBillAdapter = [[WeXLoanGetRepaymentBillAdapter alloc] init];
    }
    _getBillAdapter.delegate = self;
    WeXLoanGetRepaymentBillParamModal* paramModal = [[WeXLoanGetRepaymentBillParamModal alloc] init];
    paramModal.chainOrderId = _orderId;
    [_getBillAdapter run:paramModal];
}
// MARK: - 再次查询OrderDetail
- (void)createGetBillTwoRequest{
    if (!_getBillAdapterTwo) {
        _getBillAdapterTwo = [[WeXLoanGetRepaymentBillAdapter alloc] init];
    }
    _getBillAdapterTwo.delegate = self;
    WeXLoanGetRepaymentBillParamModal* paramModal = [[WeXLoanGetRepaymentBillParamModal alloc] init];
    paramModal.chainOrderId = _orderId;
    [_getBillAdapterTwo run:paramModal];
}

#pragma -mark 查询OrderDetail请求
- (void)createGetRepayRequest{
    if (!_repayAdapter) {
        _repayAdapter = [[WeXLoanConfirmRepayAdapter alloc] init];
    }
    _repayAdapter.delegate = self;
    WeXLoanConfirmRepayParamModal* paramModal = [[WeXLoanConfirmRepayParamModal alloc] init];
    paramModal.chainOrderId = _billModel.chainOrderId;
    [_repayAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _repayAdapter)
    {
        [WeXPorgressHUD hideLoading];
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXPayCoinResultViewController *ctrl = [[WeXPayCoinResultViewController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
        else if ([headModel.systemCode isEqualToString:@"SUCCESS"])
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:headModel.message onView:self.view];
        }
        else
        {
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else  if (adapter == _getBillAdapter)
    {
//        [WeXPorgressHUD hideLoading];
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            _billModel = (WeXLoanGetRepaymentBillResponseModal *)response;
            WEXNSLOG(@"%@",_billModel);
//            [self setupSubViews];
//          查询本地是否有相关交易记录
            //待还金额
            if ((NSInteger)(_billModel.noPayAmount * 100000) == 0) {
                [WeXPorgressHUD hideLoading];
                [self createConfirmView];
            } else {
                [self configPendingTransferData];
                [self getCoinContractAddress:nil];
            }
        }
        else
        {
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getBillAdapterTwo) {
         [WeXPorgressHUD hideLoading];
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            _billModel = (WeXLoanGetRepaymentBillResponseModal *)response;
            //待还金额
            if (_billModel.noPayAmount * 100000 <= 1) {
                [self createConfirmView];
            } else {
                [self setupSubViews];
            }
        } else {
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    
}



//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *desLabel1 = [[UILabel alloc] init];
    desLabel1.text = [NSString stringWithFormat:@"%@%.4f%@%@", WeXLocalizedString(@"请将待还币金额"),_billModel.noPayAmount,_billModel.assetCode, WeXLocalizedString(@"转入到下面的还币地址中")];
    desLabel1.font = [UIFont systemFontOfSize:17];
    desLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    desLabel1.textAlignment = NSTextAlignmentLeft;
    desLabel1.numberOfLines = 0;
    [self.view addSubview:desLabel1];
    [desLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+20);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
    }];
    
    UIButton *copyBtn = [WeXCustomButton button];
    [copyBtn setTitle:WeXLocalizedString(@"复制") forState:UIControlStateNormal];
    [copyBtn addTarget:self action:@selector(copyBtnClick) forControlEvents:UIControlEventTouchUpInside];
    copyBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [self.view addSubview:copyBtn];
    [copyBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(desLabel1.mas_bottom).offset(10);
        make.trailing.equalTo(self.view).offset(-15);
        make.width.equalTo(@60);
        make.height.equalTo(@25);
    }];
    
    UILabel *addressLabel = [[UILabel alloc] init];
    addressLabel.text = _billModel.repaymentAddress;
    addressLabel.layer.cornerRadius = 3;
    addressLabel.layer.masksToBounds = YES;
    addressLabel.layer.borderWidth = 1;
    addressLabel.layer.borderColor =ColorWithHex(0xdcdcdc).CGColor;
    addressLabel.font = [UIFont systemFontOfSize:17];
    addressLabel.textColor = COLOR_LABEL_DESCRIPTION;
    addressLabel.textAlignment = NSTextAlignmentCenter;
    addressLabel.adjustsFontSizeToFitWidth = YES;
    [self.view addSubview:addressLabel];
    [addressLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(copyBtn).offset(0);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(copyBtn.mas_leading).offset(-5);
        make.height.equalTo(@25);
    }];
    
    UIImageView *QRImageView = [[UIImageView alloc] init];
    QRImageView.layer.magnificationFilter = kCAFilterNearest;
    QRImageView.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:_billModel.repaymentAddress imageViewWidth:160];
    QRImageView.layer.cornerRadius = 8;
    QRImageView.layer.masksToBounds = YES;
    QRImageView.backgroundColor = [UIColor greenColor];
    [self.view addSubview:QRImageView];
    [QRImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(copyBtn.mas_bottom).offset(20);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@160);
        make.height.equalTo(@160);
    }];
    
    UILabel *desLabel2 = [[UILabel alloc] init];
    desLabel2.text = WeXLocalizedString(@"扫描二维码即可获得还币地址");
    desLabel2.font = [UIFont systemFontOfSize:13];
    [desLabel2 setHidden:true];
    desLabel2.textColor = COLOR_LABEL_DESCRIPTION;
    desLabel2.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:desLabel2];
    [desLabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(QRImageView.mas_bottom).offset(5);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    [line2 setHidden:true];
    line2.backgroundColor = COLOR_ALPHA_LINE;
    [self.view addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(desLabel2.mas_bottom).offset(15);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *desLabel3 = [[UILabel alloc] init];
    desLabel3.text = WeXLocalizedString(@"or");
    desLabel3.font = [UIFont systemFontOfSize:20];
    desLabel3.textColor = COLOR_LABEL_DESCRIPTION;
    desLabel3.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:desLabel3];
    [desLabel3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line2.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
    }];
    
    
    UIButton *payBtn = [WeXCustomButton button];
    [payBtn setTitle:WeXLocalizedString(@"本钱包还币") forState:UIControlStateNormal];
    [payBtn addTarget:self action:@selector(payBtnClick) forControlEvents:UIControlEventTouchUpInside];
    payBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:payBtn];
    [payBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(desLabel3.mas_bottom).offset(40);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@40);
    }];
    
    UILabel *desLabel4 = [[UILabel alloc] init];
    [desLabel4 setHidden:true];
    desLabel4.text = WeXLocalizedString(@"因转账确认需要时间，请在3分钟后确认");
    desLabel4.font = [UIFont systemFontOfSize:15];
    desLabel4.textColor = COLOR_LABEL_DESCRIPTION;
    desLabel4.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:desLabel4];
    [desLabel4 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(payBtn.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
    }];
}

- (void)copyBtnClick
{
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    pasteboard.string = _billModel.repaymentAddress;
    [WeXPorgressHUD showText:WeXLocalizedString(@"复制成功!") onView:self.view];
}

// MARK: - 本钱包还币
// 2018.8.7 本钱包还币功能
- (void)payBtnClick{
    WeXWalletTransferPendingModel *pendingModel = [_refreshManager getAllCoinPendingModel];
    if (pendingModel) {
        [self createTransferAlertView];
        return;
    }
    [self checkIsVaildContract];
//    [WeXPorgressHUD showLoadingAddedTo:self.view];
//    [self createGetRepayRequest];
}

//2018.8.7 检测是否获取合约地址
- (void)checkIsVaildContract {
    if ([self.contractAddress isVaildString] || [_billModel.assetCode isEqualToString:@"ETH"]) {
        [self pushToTransferViewControllerWithContractAddress:self.contractAddress];
    } else {
        [WeXPorgressHUD showLoadingAddedTo:self.view];
        [self getCoinContractAddress:^(NSString *contractAddress) {
            [WeXPorgressHUD hideLoading];
            if ([contractAddress isVaildString]) {
                [self pushToTransferViewControllerWithContractAddress:contractAddress];
            } else {
                [WeXPorgressHUD showText:@"获取合约地址失败,请稍后再试" onView:self.view];
            }
        }];
    }
}

- (void)createTransferAlertView {
    WeXWalletAlertWithCancelButtonView *alertView = [[WeXWalletAlertWithCancelButtonView alloc] initWithFrame:self.view.bounds];
    alertView.contentLabel.text = WeXLocalizedString(@"还币或转账正在进行中，请稍后...");
    [self.view addSubview:alertView];
}



// MARK: - 跳转到转账页面
- (void)pushToTransferViewControllerWithContractAddress:(NSString *)contractAddress {
    WeXWalletTransferViewController *transferVC = [WeXWalletTransferViewController new];
    WeXWalletDigitalGetTokenResponseModal_item *tokenModel = [WeXWalletDigitalGetTokenResponseModal_item new];
    tokenModel.symbol   = _billModel.assetCode;
    //差一个合约地址
    tokenModel.contractAddress = contractAddress;
    tokenModel.decimals = @"18";
    
    
    WeXWalletEtherscanGetRecordResponseModal_item *recordModel = [WeXWalletEtherscanGetRecordResponseModal_item new];
    recordModel.value = [WexCommonFunc upFourDigitalDecimal:_billModel.noPayAmount];
    transferVC.recordModel = recordModel;
    
    transferVC.addressStr = _billModel.repaymentAddress;
    transferVC.isPrivateChain = NO;
    transferVC.useOriginalAmount = YES;
    transferVC.tokenModel = tokenModel;
    transferVC.DidTransferAmount = ^{
        [WeXPorgressHUD showLoadingAddedTo:self.view];
        [self createGetBillRequest];
    };
    [self.navigationController pushViewController:transferVC animated:YES];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [_refreshManager endRefresh];
}




@end
