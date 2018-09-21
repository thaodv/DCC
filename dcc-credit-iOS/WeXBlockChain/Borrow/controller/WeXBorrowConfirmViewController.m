//
//  WeXBorrowConfirmViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowConfirmViewController.h"
#import "WeXBorrowConfirmCell.h"
#import "WeXBorrowConfirmToastView.h"
#import "WeXBorrowResultViewController.h"
#import "WeXBorrowConditionViewController.h"
#import "WeXBorrowProtocolWebViewController.h"
#import "WeXChooseReceiveAddressViewController.h"
#import "WeXQueryProductByLenderCodeModal.h"
#import "WeXBorrowFeeSlideView.h"
#import "WeXBorrowReceiveAddressModal.h"

#import "WeXAuthenGetTicketAdapter.h"
#import "WeXBorrowGetContractAddressAdapter.h"
#import "WeXBorrowApplyAdapter.h"
#import "WeXGetReceiptResult2Adapter.h"
#import "WeXLoanGetOrderIdAdapter.h"
#import "WeXLoanVerifyAdapter.h"
#import "WeXLoanQueryLastOrderAdapter.h"
#import "WeXLoanCancelAdapter.h"
#import "WeXLoanGetLoanInterestAdapter.h"

#import "WeXBorrowCreatedStatusToastView.h"
#import "WeXBorrowAuditingStatusToastView.h"
#import "AFNetworking.h"


static const NSInteger kMoneyButtonTag = 100;
static const NSInteger kPeriodButtonTag = 200;

static const NSInteger kSlideCellHeight = 170;

@interface WeXBorrowConfirmViewController ()<UITableViewDelegate,UITableViewDataSource,WeXBorrowConfirmToastViewDelegate,UIGestureRecognizerDelegate,WeXBorrowFeeSlideViewDelegate,UITextFieldDelegate,WeXBorrowCreatedStatusToastViewDelegate>
{
    UITableView *_tableView;
    WeXBorrowConfirmToastView *_confirmView;
    
    UIButton *_moneyOtherButton;
    UITextField *_otherMoneyTextField;
    
    UIButton *_selectedMoneyButton;
    NSString *_selectedMoney;
    
    UIButton *_selectedPeriodButton;
    NSString *_selectedPeriod;
    
    NSString *_feeStr;
    
    NSString *_interest;
    
    WeXBorrowFeeSlideView *_sliderView;
    
    NSString *_dccBalance;
    NSString *_feeValue;
    
    NSString *_addressName;
    NSString *_addressContent;
    UILabel *_addressNameLabel;
    UILabel *_addressContentLabel;
    
    UILabel *_needConditionLabel;
    
    NSString *_rawTransaction;
    
    NSString *_cancelRawTransaction;
    
    NSString *_txHash;
    
    NSString *_cancelTxHash;
    
    NSString *_orderId;
    
    NSString *_lastOrderId;
    
    NSString *_contractAddress;//合约地址
    
    NSString * _applyTimestamp;
    
    NSInteger _requestCount;//查询上链结果请求的次数
    NSInteger _cancelRequestCount;//查询上链结果请求的次数
    
    WeXGetTicketResponseModal *_getTicketModel;
    
    BOOL _isCancelProcess;//是否是取消流程
    
    BOOL _firstLoad;
}

@property (nonatomic,strong)WeXAuthenGetTicketAdapter *getTicketAdapter;
@property (nonatomic,strong)WeXBorrowGetContractAddressAdapter *getContractAddressAdapter;
@property (nonatomic,strong)WeXBorrowApplyAdapter *applyAdapter;
@property (nonatomic,strong)WeXGetReceiptResult2Adapter *getReceiptAdapter;
@property (nonatomic,strong)WeXGetReceiptResult2Adapter *getCancelReceiptAdapter;
@property (nonatomic,strong)WeXLoanGetOrderIdAdapter *getOrderIdAdapter;
@property (nonatomic,strong)WeXLoanVerifyAdapter *verifyAdapter;
@property (nonatomic,strong)WeXLoanQueryLastOrderAdapter *getLastOrderAdapter;
@property (nonatomic,strong)WeXLoanCancelAdapter *cancelAdapter;
@property (nonatomic,strong)WeXLoanGetLoanInterestAdapter *getInterestAdapter;
@end

@implementation WeXBorrowConfirmViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"借币申请");
    _firstLoad = YES;
    [self setNavigationNomalBackButtonType];
    [self getDefaultAddress];
    [self createDccBalanceRequest];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    //更新所需资料状态
    _needConditionLabel.attributedText = [self getCertString];
}

#pragma -mark 发送请求
- (void)createGetInterestRequest{
    _getInterestAdapter = [[WeXLoanGetLoanInterestAdapter alloc] init];
    _getInterestAdapter.delegate = self;
    WeXLoanGetLoanInterestParamModal* paramModal = [[WeXLoanGetLoanInterestParamModal alloc] init];
    paramModal.productId = _productModel.productId;
    paramModal.amount = _selectedMoney;
    paramModal.loanPeriodValue = _selectedPeriod;
    [_getInterestAdapter run:paramModal];
}


#pragma -mark 发送请求
- (void)createGetTicketRequest{
    _getTicketAdapter = [[WeXAuthenGetTicketAdapter alloc] init];
    _getTicketAdapter.delegate = self;
    WeXGetTicketParamModal* paramModal = [[WeXGetTicketParamModal alloc] init];
    [_getTicketAdapter run:paramModal];
}

#pragma -mark 查询上链结果请求
- (void)createReceiptResultRequest{
    _getReceiptAdapter = [[WeXGetReceiptResult2Adapter alloc] init];
    _getReceiptAdapter.delegate = self;
    WeXGetReceiptResult2ParamModal* paramModal = [[WeXGetReceiptResult2ParamModal alloc] init];
    paramModal.txHash = _txHash;
    [_getReceiptAdapter run:paramModal];
}

#pragma -mark 查询上链结果请求
- (void)createCancelReceiptResultRequest{
    _getCancelReceiptAdapter = [[WeXGetReceiptResult2Adapter alloc] init];
    _getCancelReceiptAdapter.delegate = self;
    WeXGetReceiptResult2ParamModal* paramModal = [[WeXGetReceiptResult2ParamModal alloc] init];
    paramModal.txHash = _cancelTxHash;
    [_getCancelReceiptAdapter run:paramModal];
}

#pragma -mark 发送获取orderid请求
- (void)createGetOrderIdRequest{
    _getOrderIdAdapter = [[WeXLoanGetOrderIdAdapter alloc] init];
    _getOrderIdAdapter.delegate = self;
    WeXLoanGetOrderIdParamModal* paramModal = [[WeXLoanGetOrderIdParamModal alloc] init];
    paramModal.txHash = _txHash;
    [_getOrderIdAdapter run:paramModal];
}


#pragma -mark 发送获取合约地址请求
- (void)createGetContractAddressRequest{
    _getContractAddressAdapter = [[WeXBorrowGetContractAddressAdapter alloc] init];
    _getContractAddressAdapter.delegate = self;
    WeXGetContractAddressParamModal* paramModal = [[WeXGetContractAddressParamModal alloc] init];
    [_getContractAddressAdapter run:paramModal];
}

#pragma -mark 发送apply请求
- (void)createApplyRequest{
    _applyAdapter = [[WeXBorrowApplyAdapter alloc] init];
    _applyAdapter.delegate = self;
    WeXBorrowApplyParamModal* paramModal = [[WeXBorrowApplyParamModal alloc] init];
    paramModal.ticket = _getTicketModel.ticket;
    paramModal.signMessage = _rawTransaction;
    [_applyAdapter run:paramModal];
}

#pragma -mark 发送Cancel请求
- (void)createGetLastOrderRequest{
    _getLastOrderAdapter = [[WeXLoanQueryLastOrderAdapter alloc] init];
    _getLastOrderAdapter.delegate = self;
    [_getLastOrderAdapter run:nil];
}


#pragma -mark 发送Cancel请求
- (void)createCancelRequest{
    _cancelAdapter = [[WeXLoanCancelAdapter alloc] init];
    _cancelAdapter.delegate = self;
    WeXLoanCancelParamModal* paramModal = [[WeXLoanCancelParamModal alloc] init];
    paramModal.chainOrderId = _lastOrderId;
    [_cancelAdapter run:paramModal];
}

#pragma -mark 发送Verify请求
- (void)createVerifyRequest{
    WeXPasswordCacheModal * passportModel = [WexCommonFunc getPassport];
    _verifyAdapter = [[WeXLoanVerifyAdapter alloc] init];
    _verifyAdapter.delegate = self;
    WeXLoanVerifyParamModal* paramModal = [[WeXLoanVerifyParamModal alloc] init];
    paramModal.orderId = _orderId;
    paramModal.loanProductId = _productModel.productId;
    paramModal.borrowName = passportModel.userName;
    paramModal.borrowAmount = _selectedMoney;
    NSMutableArray *periodArray = _productModel.loanPeriodList;
    if (periodArray.count > 0) {
        WeXQueryProductByLenderCodeResponseModal_period *periodModel = _productModel.loanPeriodList[0];
        paramModal.durationUnit = periodModel.unit;
    }
    paramModal.borrowDuration = _selectedPeriod;
    paramModal.certNo = passportModel.userNumber;
    paramModal.mobile = passportModel.mobileAuthenNumber;
    paramModal.bankCard = passportModel.bankAuthenCardNumber;
    paramModal.bankMobile = passportModel.bankAuthenMobile;
    paramModal.applyDate = _applyTimestamp;
    NSData *reportData = [[NSData alloc] initWithBase64EncodedString:[WexCommonFunc getContentWithFileName:WEX_MOBILE_AUTHEN_REPORT_KEY] options:0];
    NSString *content = [[NSString alloc] initWithData:reportData encoding:NSUTF8StringEncoding];
    paramModal.communicationLog = content;

    paramModal.personalPhoto = [WexCommonFunc base64StringWithImageData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_HEAD_KEY]];
    paramModal.frontPhoto = [WexCommonFunc base64StringWithImageData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_HEAD_KEY]];
    paramModal.backPhoto = [WexCommonFunc base64StringWithImageData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_HEAD_KEY]];
    paramModal.version = @"1.0.0";
//    [_verifyAdapter run:paramModal];
    NSDictionary* dict = [paramModal toDictionary];
    NSMutableDictionary *paramDict = [NSMutableDictionary dictionaryWithDictionary:dict];
    
    [paramDict removeObjectForKey:@"frontPhoto"];
    [paramDict removeObjectForKey:@"backPhoto"];
    [paramDict removeObjectForKey:@"personalPhoto"];
    
    NSData *frontImageData = [WexCommonFunc idDataWithName:WEX_ID_IMAGE_FRONT_KEY];
    NSData *backImageData  = [WexCommonFunc idDataWithName:WEX_ID_IMAGE_BACK_KEY];
    NSData *headImageData  = [WexCommonFunc idDataWithName:WEX_ID_IMAGE_HEAD_KEY];
    
    [paramDict setObject:frontImageData forKey:@"frontPhoto"];
    [paramDict setObject:backImageData forKey:@"backPhoto"];
    [paramDict setObject:headImageData forKey:@"personalPhoto"];
    [_verifyAdapter runImage:paramDict];
     

//    [_verifyAdapter runImage:(NSMutableDictionary *)]
    
}

////申请数字摘要applicationDigest：sha256(utf8_to_bytes(放款机构名称+tostring(币种代码)+tostring(借款金额)+tostring(day(借款期限+'D'))+tostring(借款期数)+tostring(借款方式(XinYong))+ tostring(mills(申请时间))))

//申请数字摘要applicationDigest：sha256(tostring(币种代码)+tostring(day(借款期限+'D'))+tostring(借款金额)+tostring(收款地址(XinYong))+ tostring(mills(借款时间))))
- (void)getRawTranstion
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
         WeXPasswordCacheModal *passportModel = [WexCommonFunc getPassport];
         //idHash
         NSString *idString = [NSString stringWithFormat:@"%@%@",passportModel.userName,passportModel.userNumber];
         NSData *idData = [idString dataUsingEncoding:NSUTF8StringEncoding];
         NSString *idHashStr = [WexCommonFunc stringSHA256WithData:idData];
         idHashStr = [NSString stringWithFormat:@"0x%@",idHashStr];
         //loanHash
         NSDate *nowDate  = [NSDate date];
         long long nowTimestamp = nowDate.timeIntervalSince1970*1000;
         _applyTimestamp = [NSString stringWithFormat:@"%lld",nowTimestamp];
         _selectedMoney = [NSString stringWithFormat:@"%.4f",[_selectedMoney floatValue]];
         NSLog(@"_selectedMoney = %@",_selectedMoney);
         NSString *idFrontStr = [WexCommonFunc base64StringWithImageData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_FRONT_KEY]];
         NSString *idBackStr = [WexCommonFunc base64StringWithImageData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_BACK_KEY]];
         NSString *idHeadStr = [WexCommonFunc base64StringWithImageData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_HEAD_KEY]];
         NSDateFormatter *formatter = [[NSDateFormatter alloc] init] ;
         [formatter setDateStyle:NSDateFormatterMediumStyle];
         [formatter setTimeStyle:NSDateFormatterShortStyle];
         [formatter setDateFormat:@"YYYY-MM-dd HH:mm:ss SSS"]; // ----------设置你想要的格式,hh与HH的区别:分别表示12小时制,24小时制
         //设置时区,这个对于时间的处理有时很重要
         NSTimeZone* timeZone = [NSTimeZone timeZoneWithName:@"Asia/Shanghai"];
         [formatter setTimeZone:timeZone];
         NSDate *datenow = [NSDate date];//现在时间,你可以输出来看下是什么格式
         NSString *timeSp = [NSString stringWithFormat:@"%ld", (long)[datenow timeIntervalSince1970]*1000];
         
        
         NSString *loanString = [NSString stringWithFormat:@"%@%@%@%@%@",_productModel.currency.symbol,_selectedPeriod,_selectedMoney,_addressContent,_applyTimestamp];
         WEXNSLOG(@"loanString = %@",loanString);
         NSData *loanData = [loanString dataUsingEncoding:NSUTF8StringEncoding];
         WEXNSLOG(@"%@",loanData);
         NSString *loanHashStr = [WexCommonFunc stringSHA256WithData:loanData];
         
         loanHashStr = [NSString stringWithFormat:@"0x%@",loanHashStr];
        
        NSString *feeFormatterStr = [[WexCommonFunc stringWithOriginString:_feeStr multiplyString:EIGHTEEN_ZERO] stringValue];
         //version
         NSString *version = @"1";
         // 合约定义说明
         NSString* abiJson= WEX_DCC_ABI_BORROW_APPLY;
         // 合约参数值 第一个参数为version 暂时为1
         NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\',\'%@\',\'%@\',\'%@\',\'%@\']",version,idHashStr,loanHashStr,feeFormatterStr,_addressContent];
         WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
         // 合约地址
         NSString* abiAddress = _contractAddress;
         WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
         [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:cacheModel.walletPrivateKey responseBlock:^(id response)
          {
              NSLog(@"rawTransaction=%@",response);
              _rawTransaction = response;

              [self createApplyRequest];
          }];
         
     }];
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
    if (adapter == _getTicketAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetTicketResponseModal *getTicketModel = (WeXGetTicketResponseModal *)response;
            _getTicketModel = getTicketModel;
            [self createGetContractAddressRequest];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
        
    }
    else if (adapter == _getContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _contractAddress = model.result;
            //合约地址请求成功
            if (_contractAddress) {
                [self getRawTranstion];
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _applyAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXBorrowApplyResponseModal *responseModel =(WeXBorrowApplyResponseModal *)response;
            _txHash = responseModel.result;
            if (_txHash) {
                [self createReceiptResultRequest];
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getReceiptAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WeXGetReceiptResult2ResponseModal *model = (WeXGetReceiptResult2ResponseModal *)response;
            //上链成功
            if (model.hasReceipt) {
                [self createGetOrderIdRequest];
                _requestCount = 0;
            }
            else
            {
                if (_requestCount > 5) {
                    _requestCount = 0;
                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:WeXLocalizedString(@"系统繁忙!") onView:self.view];
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createReceiptResultRequest];
                    _requestCount++;
                });
            }
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
                    _isCancelProcess = NO;
                    [WeXPorgressHUD showText:WeXLocalizedString(@"订单取消成功!") onView:self.view];
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
    else if (adapter == _getOrderIdAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXLoanGetOrderIdResponseModal *responseModel =(WeXLoanGetOrderIdResponseModal *)response;
            NSLog(@"%@",responseModel);
            //上链成功
            if (responseModel.resultList.count > 0) {
                WeXLoanGetOrderIdResponseModal_item *itemModel = responseModel.resultList[0];
                _orderId = itemModel.orderId;
                [self createVerifyRequest];
            }
            else
            {
                [WeXPorgressHUD hideLoading];
                [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getLastOrderAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXLoanQueryLastOrderResponseModal *responseModel =(WeXLoanQueryLastOrderResponseModal *)response;
            WEXNSLOG(@"%@",responseModel);
            //上链成功
            if (responseModel.orderId) {
                _lastOrderId = responseModel.orderId;
                if ([responseModel.status isEqualToString:WEX_LOAN_STATUS_CREATED]) {
                     [WeXPorgressHUD hideLoading];
                    WeXBorrowCreatedStatusToastView *statusView = [[WeXBorrowCreatedStatusToastView alloc] initWithFrame:self.view.frame];
                    statusView.delegate = self;
                    [self.view addSubview:statusView];
                    _isCancelProcess = YES;
                }
                else if ([responseModel.status isEqualToString:WEX_LOAN_STATUS_AUDITING])
                {
                     [WeXPorgressHUD hideLoading];
                    WeXBorrowAuditingStatusToastView *statusView = [[WeXBorrowAuditingStatusToastView alloc] initWithFrame:self.view.frame];
                    [self.view addSubview:statusView];
                    _isCancelProcess = YES;
                }
                else
                {
                     _isCancelProcess = NO;
                     [self createGetTicketRequest];
                }
            }
            else
            {
                 _isCancelProcess = NO;
                [self createGetTicketRequest];
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _cancelAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXLoanCancelResponseModal *responseModel =(WeXLoanCancelResponseModal *)response;
            WEXNSLOG(@"%@",responseModel);
            [WeXPorgressHUD hideLoading];

            [WeXPorgressHUD showText:WeXLocalizedString(@"订单取消成功!") onView:self.view];
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
    else if (adapter == _verifyAdapter){
        [WeXPorgressHUD hideLoading];
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXBorrowResultViewController *ctrl = [[WeXBorrowResultViewController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
        else if ([headModel.systemCode isEqualToString:@"SUCCESS"])
        {
            [WeXPorgressHUD showText:headModel.message onView:self.view];
        }
        else
        {
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getInterestAdapter){
        [WeXPorgressHUD hideLoading];
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXLoanGetLoanInterestResponseModal *responseModel =(WeXLoanGetLoanInterestResponseModal *)response;
            WEXNSLOG(@"%@",responseModel);
            _interest = responseModel.result;
            [self createConfirmToastView];
        }
        else
        {
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
   
    
}

// MARK: - 下一步弹出确认金额
- (void)createConfirmToastView
{
    _confirmView = [[WeXBorrowConfirmToastView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    _confirmView.delegate = self;
    _confirmView.borrowBalanceLabel.text = [NSString stringWithFormat:@"%.4f%@",[_selectedMoney floatValue],_productModel.currency.symbol];
    NSMutableArray *periodArray = _productModel.loanPeriodList;
    if (periodArray.count >= 1) {
        WeXQueryProductByLenderCodeResponseModal_period *periodModel = periodArray[0];
        _confirmView.periodLabel.text = [NSString stringWithFormat:@"%@%@",_selectedPeriod,[WexCommonFunc transferChinesePeriod:periodModel.unit]];
    }
    _confirmView.payCapitalLabel.text = _confirmView.borrowBalanceLabel.text = [NSString stringWithFormat:@"%.4f%@",[_selectedMoney floatValue],_productModel.currency.symbol];
    _confirmView.feeLabel.text = [NSString stringWithFormat:@"%@DCC",_feeStr];
    _confirmView.balanceLabel.text = [NSString stringWithFormat:@"%@DCC",_dccBalance];
    _confirmView.payInterestLabel.text = [NSString stringWithFormat:@"%.4f%@",[_interest floatValue],_productModel.currency.symbol];
    _confirmView.payTotalLabel.text = [NSString stringWithFormat:@"%.4f%@",[_interest floatValue]+[_selectedMoney floatValue],_productModel.currency.symbol];
     _confirmView.addressLabel.text = _addressContent;
    [self.view addSubview:_confirmView];
}



- (void)createDccBalanceRequest
{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    //abi方法
    NSString *abiJson = WEX_ERC20_ABI_BALANCE;
    //参数为需要查询的地址
    NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
    [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response)
     {
         //dcc私链
         [[WXPassHelper instance] call2ContractAddress:[WexCommonFunc getDCCContractAddress] data:response type:WEX_DCC_NODE_SERVER responseBlock:^(id response)
          {
              [WeXPorgressHUD hideLoading];
              NSLog(WeXLocalizedString(@"dcc私链balance=%@"),response);
              NSDictionary *responseDict = response;
              NSString * originBalance =[responseDict objectForKey:@"result"];
              NSString * ethException =[responseDict objectForKey:@"ethException"];
              if ([ethException isEqualToString:@"ethException"]) {
                  [WeXPorgressHUD showText:WeXLocalizedString(@"网络异常！") onView:self.view];
              }
              else
              {
                  _dccBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:18];
                  [self setupSubViews];
              }
              
          }];
     }];
}

- (void)getDefaultAddress{
    RLMResults <WeXBorrowReceiveAddressModal *>*results = [WeXBorrowReceiveAddressModal allObjects];
    for (WeXBorrowReceiveAddressModal *rlmModel in results) {
        if (rlmModel.isDefault) {
            _addressName = WeXLocalizedString(rlmModel.name);
            _addressContent = rlmModel.address;
        }
    }
}

- (NSMutableAttributedString *)getCertString
{
    WeXPasswordCacheModal *passportModel = [WexCommonFunc getPassport];
    NSArray *array = _productModel.requisiteCertList;
    NSMutableAttributedString *attrStr = [[NSMutableAttributedString alloc] init];
    for (int i = 0; i < array.count; i++) {
        NSString *itemStr = array[i];
        NSMutableAttributedString *attrItem;
        if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_ID]) {
            if (passportModel.idAuthenStatus == WeXCreditIDAuthenStatusTypeNone){
               attrItem = [[NSMutableAttributedString alloc] initWithString:WeXLocalizedString(@"身份证信息、") attributes:@{NSForegroundColorAttributeName:[UIColor redColor]}];
            }
            else
            {
               attrItem = [[NSMutableAttributedString alloc] initWithString:WeXLocalizedString(@"身份证信息、") attributes:@{NSForegroundColorAttributeName:COLOR_THEME_ALL}];
            }
        }
        else if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_BANK_CARD])
        {
            if (passportModel.bankAuthenStatus == WeXCreditBankAuthenStatusTypeNone){
                attrItem = [[NSMutableAttributedString alloc] initWithString:WeXLocalizedString(@"银行卡信息、") attributes:@{NSForegroundColorAttributeName:[UIColor redColor]}];
            }
            else
            {
                attrItem = [[NSMutableAttributedString alloc] initWithString:WeXLocalizedString(@"银行卡信息、") attributes:@{NSForegroundColorAttributeName:COLOR_THEME_ALL}];
            }
        }
        else if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_COMMUNICATION_LOG])
        {
            if (passportModel.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeNone||passportModel.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeAuthening||
                passportModel.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeInvalid){
                attrItem = [[NSMutableAttributedString alloc] initWithString:WeXLocalizedString(@"手机运营商信息、") attributes:@{NSForegroundColorAttributeName:[UIColor redColor]}];
            }
            else
            {
                attrItem = [[NSMutableAttributedString alloc] initWithString:WeXLocalizedString(@"手机运营商信息、") attributes:@{NSForegroundColorAttributeName:COLOR_THEME_ALL}];
            }
        }
        
        [attrStr appendAttributedString:attrItem];
    }
    if (attrStr.length > 1) {
        [attrStr deleteCharactersInRange:NSMakeRange(attrStr.length-1, 1)];
    }
    return attrStr;
}



//初始化滚动视图
-(void)setupSubViews{
    _tableView = [[UITableView alloc] init];
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.bottom.trailing.equalTo(self.view);
    }];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapClick)];
    tap.delegate = self;
    [_tableView addGestureRecognizer:tap];
    
    UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, _tableView.frame.size.width, 100)];
    _tableView.tableFooterView= footerView;
    
    UIButton *nextBtn = [WeXCustomButton button];
    [nextBtn setTitle:WeXLocalizedString(@"下一步") forState:UIControlStateNormal];
    [nextBtn addTarget:self action:@selector(nextBtnClick) forControlEvents:UIControlEventTouchUpInside];
    nextBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [footerView addSubview:nextBtn];
    [nextBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(footerView).offset(20);
        make.height.equalTo(@40);
    }];
    
    
    UIButton *protocolBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [protocolBtn addTarget:self action:@selector(protocolBtnClick) forControlEvents:UIControlEventTouchUpInside];
    protocolBtn.titleLabel.font = [UIFont systemFontOfSize:16];
    protocolBtn.titleLabel.numberOfLines = 2;
    [footerView addSubview:protocolBtn];
    [protocolBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.top.equalTo(nextBtn.mas_bottom).offset(20);
        make.height.equalTo(@50);
    }];
    
    NSString *string = [WeXLocalizedString(@"点击下一步即表示同意") stringByAppendingString:WeXLocalizedString(@"《借币协议》")];
    NSMutableAttributedString *attrStr = [[NSMutableAttributedString alloc] initWithString:string];
    [attrStr addAttribute:NSForegroundColorAttributeName value:COLOR_THEME_ALL range:NSMakeRange(attrStr.length-[WeXLocalizedString(@"《借币协议》") length], [WeXLocalizedString(@"《借币协议》") length])];
    [protocolBtn setAttributedTitle:attrStr forState:UIControlStateNormal];
}



- (void)tapClick
{
    WEXNSLOG(@"%s",__func__);
    [self.view endEditing:YES];
}

// MARK: - 借款协议
- (void)protocolBtnClick
{
    WeXBorrowProtocolWebViewController *ctrl = [[WeXBorrowProtocolWebViewController alloc] init];
    ctrl.urlString = _productModel.agreementTemplateUrl;
    [self.navigationController pushViewController:ctrl animated:YES];
}

// MARK: - 下一步

- (void)nextBtnClick
{
    
    [self.view endEditing:YES];
    
    if (![self verifyJumpCondition]) return;
    
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    
    [self createGetInterestRequest];
    
}


- (BOOL)verifyJumpCondition
{
    if ([_selectedMoney isEqualToString:@""]||!_selectedMoney) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"输入金额不能为空") onView:self.view];
        return NO;
    }
    
    NSArray *valueArray = _productModel.volumeOptionList;
    if ([_selectedMoney floatValue] > [valueArray[2] floatValue]) {
        NSString *message = WeXLocalizedString(@"输入金额不正确");
        [WeXPorgressHUD showText:message onView:self.view];
        return NO;
    }
    
    if ([_selectedMoney floatValue] < [valueArray[0] floatValue]) {
         NSString *message = WeXLocalizedString(@"输入金额不正确");
        [WeXPorgressHUD showText:message onView:self.view];
        return NO;
    }
    
    if ([_feeStr floatValue] > [_dccBalance floatValue]) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"持有量不足！") onView:self.view];
        return NO;
    }
    
    WeXPasswordCacheModal *passportModel = [WexCommonFunc getPassport];
    NSArray *authenArray = _productModel.requisiteCertList;
    for (int i = 0; i < authenArray.count; i++) {
        NSString *itemStr = authenArray[i];
        if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_ID]) {
            if (passportModel.idAuthenStatus == WeXCreditIDAuthenStatusTypeNone){
               [WeXPorgressHUD showText:WeXLocalizedString(@"没有完成实名认证！") onView:self.view];
                return NO;
            }
        }
        else if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_BANK_CARD])
        {
            if (passportModel.bankAuthenStatus == WeXCreditBankAuthenStatusTypeNone){
                [WeXPorgressHUD showText:WeXLocalizedString(@"没有完成银行卡认证！") onView:self.view];
                  return NO;
            }

        }
        else if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_COMMUNICATION_LOG])
        {
//          2018.8.21 新增运营商认证状态检测
            if (passportModel.mobileAuthenStatus == WeXCreditMobileOperatorAuthenStatusTypeInvalid) {
                [WeXPorgressHUD showText:WeXLocalizedString(@"手机运营商认证已经过期！") onView:self.view];
                return NO;
            }
            if (passportModel.mobileAuthenStatus != WeXCreditMobileOperatorAuthenStatusTypeSuccess){
                [WeXPorgressHUD showText:WeXLocalizedString(@"没有完成手机运营商认证！") onView:self.view];
                return NO;
            }
        }
    }
    return YES;
}

- (void)moneyButtonClick:(UIButton *)btn
{
    _selectedMoneyButton.selected = NO;
    btn.selected = YES;
    _selectedMoneyButton = btn;
    
    NSInteger index = btn.tag-kMoneyButtonTag;
    if (index <= 2)
    {
        [self.view endEditing:YES];
        NSArray *valueArray = _productModel.volumeOptionList;
        if (valueArray.count >= 3) {
            _selectedMoney = [valueArray[index] stringValue];
        }
    }
    else if(index == 3)
    {
        [_otherMoneyTextField becomeFirstResponder];
         _selectedMoney = _otherMoneyTextField.text;
    }
    WEXNSLOG(@"_selectedMoney=%@",_selectedMoney);

}

- (void)periodButtonClick:(UIButton *)btn
{
    _selectedPeriodButton.selected = NO;
    btn.selected = YES;
    _selectedPeriodButton = btn;
    
    NSInteger index = btn.tag-kPeriodButtonTag;
    NSMutableArray *periodArray = _productModel.loanPeriodList;
    if (periodArray.count >= 3) {
        WeXQueryProductByLenderCodeResponseModal_period *periodModel = periodArray[index];
        _selectedPeriod = periodModel.value;
    }
    WEXNSLOG(@"_selectedPeriod=%@",_selectedPeriod);
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        static NSString *cellID = @"moneyCellID";
        WeXBorrowConfirmMoneyCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
        if (cell == nil) {
            cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXBorrowConfirmCell" owner:self options:nil] objectAtIndex:1];
            cell.backgroundColor = [UIColor clearColor];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            cell.moneyFirstButton.selected = YES;
            _selectedMoneyButton = cell.moneyFirstButton;
            _moneyOtherButton = cell.otherMoneyButton;
            _otherMoneyTextField = cell.otherMoneyTextField;
            _otherMoneyTextField.delegate = self;
            [cell.moneyFirstButton addTarget:self action:@selector(moneyButtonClick:) forControlEvents:UIControlEventTouchUpInside];
            cell.moneyFirstButton.tag = kMoneyButtonTag;
            [cell.moneySecondButton addTarget:self action:@selector(moneyButtonClick:) forControlEvents:UIControlEventTouchUpInside];
            cell.moneySecondButton.tag = kMoneyButtonTag+1;
            [cell.moneyThirdButton addTarget:self action:@selector(moneyButtonClick:) forControlEvents:UIControlEventTouchUpInside];
            cell.moneyThirdButton.tag = kMoneyButtonTag+2;
            [cell.otherMoneyButton addTarget:self action:@selector(moneyButtonClick:) forControlEvents:UIControlEventTouchUpInside];
            cell.otherMoneyButton.tag = kMoneyButtonTag+3;
            
            cell.titleLabel.text = [NSString stringWithFormat:@"%@%@",WeXLocalizedString(@"借币金额"),_productModel.currency.symbol];
            NSArray *valueArray = _productModel.volumeOptionList;
            if (valueArray.count >= 3) {
                [cell.moneyFirstButton setTitle:[valueArray[0] stringValue] forState:UIControlStateNormal];
                WEXNSLOG(@"valueArray[0]=%@",valueArray[0]);
                _selectedMoney = [valueArray[0] stringValue];
                WEXNSLOG(@"_selectedMoney=%@",_selectedMoney);
                [cell.moneySecondButton setTitle:[valueArray[1] stringValue] forState:UIControlStateNormal];
                [cell.moneyThirdButton  setTitle:[valueArray[2] stringValue] forState:UIControlStateNormal];
            }
        }
        return cell;
    }
    else if (indexPath.row == 1)
    {
        static NSString *cellID = @"timeCellID";
        WeXBorrowConfirmTimeCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
        if (cell == nil) {
            cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXBorrowConfirmCell" owner:self options:nil] objectAtIndex:2];
            cell.backgroundColor = [UIColor clearColor];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            cell.periodFirstButton.selected = YES;
            _selectedPeriodButton = cell.periodFirstButton;
            [cell.periodFirstButton addTarget:self action:@selector(periodButtonClick:) forControlEvents:UIControlEventTouchUpInside];
            cell.periodFirstButton.tag = kPeriodButtonTag+0;
            [cell.periodSecondButton addTarget:self action:@selector(periodButtonClick:) forControlEvents:UIControlEventTouchUpInside];
            cell.periodSecondButton.tag = kPeriodButtonTag+1;
            [cell.periodThirdButton addTarget:self action:@selector(periodButtonClick:) forControlEvents:UIControlEventTouchUpInside];
            cell.periodThirdButton.tag = kPeriodButtonTag+2;
            
            cell.titleLabel.text = WeXLocalizedString(@"借币期限");
            NSMutableArray *periodArray = _productModel.loanPeriodList;
            if (periodArray.count >= 3) {
                WeXQueryProductByLenderCodeResponseModal_period *periodModel1 = periodArray[0];
                NSMutableString *periodStr1 = [NSMutableString stringWithFormat:@"%@%@",periodModel1.value,[WexCommonFunc transferChinesePeriod:periodModel1.unit]];
                [cell.periodFirstButton setTitle:periodStr1 forState:UIControlStateNormal];
                _selectedPeriod = periodModel1.value;
                WEXNSLOG(@"_selectedPeriod=%@",_selectedPeriod);
                
                WeXQueryProductByLenderCodeResponseModal_period *periodModel2 = periodArray[1];
                NSMutableString *periodStr2 = [NSMutableString stringWithFormat:@"%@%@",periodModel2.value,[WexCommonFunc transferChinesePeriod:periodModel2.unit]];
                [cell.periodSecondButton setTitle:periodStr2 forState:UIControlStateNormal];
                
                WeXQueryProductByLenderCodeResponseModal_period *periodModel3 = periodArray[2];
                NSMutableString *periodStr3 = [NSMutableString stringWithFormat:@"%@%@",periodModel3.value,[WexCommonFunc transferChinesePeriod:periodModel3.unit]];
                [cell.periodThirdButton setTitle:periodStr3 forState:UIControlStateNormal];
            }
        }
        return cell;
    }
    else if (indexPath.row == 2)
    {
        WeXBorrowConfirmFeeCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXBorrowConfirmCell" owner:self options:nil] objectAtIndex:3];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        NSArray *dccFeeArray = _productModel.dccFeeScope;
        if (dccFeeArray.count >= 2) {
            _sliderView = [[WeXBorrowFeeSlideView alloc] initWithFrame:CGRectMake(0, 0,_tableView.frame.size.width, kSlideCellHeight) normalFee:[dccFeeArray[0] stringValue] fastFee:[dccFeeArray[1] stringValue] balance:_dccBalance];
            _sliderView.delegate = self;
            [cell.contentView addSubview:_sliderView];
            
            _feeStr = [NSString stringWithFormat:@"%.0f",[dccFeeArray[0] floatValue]];
        }
        return cell;
    }
    
    else if (indexPath.row == 3)
    {
        WeXBorrowConfirmAddressCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXBorrowConfirmCell" owner:self options:nil] objectAtIndex:4];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.titleLabel.text = WeXLocalizedString(@"收币地址");
        cell.addressNameLabel.text = _addressName;
        cell.addressLabel.text = _addressContent;
        cell.addressLabel.adjustsFontSizeToFitWidth = YES;
        _addressNameLabel = cell.addressNameLabel;
        _addressContentLabel = cell.addressLabel;
        return cell;
    }
    else if (indexPath.row == 4)
    {
        WeXBorrowConfirmConditionCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXBorrowConfirmCell" owner:self options:nil] objectAtIndex:5];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.titleLabel.text = WeXLocalizedString(@"所需资料");
        cell.contentLabel.attributedText = [self getCertString];
        cell.contentLabel.adjustsFontSizeToFitWidth = YES;
        _needConditionLabel = cell.contentLabel;
        return cell;
    }
   
    return nil;
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        return 145;
    }
    else if (indexPath.row == 1)
    {
         return 100;
    }
    else if (indexPath.row == 2)
    {
        return kSlideCellHeight;
    }
    else if (indexPath.row == 3)
    {
         return 60;
    }
   
    return 50;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"%s",__func__);
    if (indexPath.row == 3) {
        WeXChooseReceiveAddressViewController *ctrl = [[WeXChooseReceiveAddressViewController alloc] init];
        ctrl.chooseAddressBlock = ^(NSString *addressName, NSString *addressContent) {
            _addressContentLabel.text = addressContent;
            _addressNameLabel.text = WeXLocalizedString(addressName);
            _addressName = addressName;
            _addressContent = addressContent;
        };
        //选择收款地址
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.row == 4) {
        WeXBorrowConditionViewController *ctrl = [[WeXBorrowConditionViewController alloc] init];
        ctrl.datasArray = _productModel.requisiteCertList;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}

#pragma mark -UITextFieldDelegate
- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    _selectedMoneyButton.selected = NO;
    _moneyOtherButton.selected = YES;
    _selectedMoneyButton = _moneyOtherButton;
    _selectedMoney = textField.text;
    WEXNSLOG(@"_selectedMoney=%@",_selectedMoney);
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string;
{
    NSString *content;
    if(range.length == 0)
    {
        content = [NSString stringWithFormat:@"%@%@",textField.text, string];
        
    }
    else
    {
        content = [textField.text substringToIndex:textField.text.length -range.length];
    }
    _selectedMoney = content;
    WEXNSLOG(@"_selectedMoney=%@",_selectedMoney);
    return YES;
}


#pragma mark -WeXBorrowConfirmToastViewDelegate
- (void)borrowConfirmToastViewDidClickConfirmButtoon
{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self createGetLastOrderRequest];

}

#pragma mark - WeXBorrowCreatedStatusToastViewDelegate
- (void)borrowCreatedStatusToastViewDidClickCancelOrderButtoon
{
    WEXNSLOG(@"%s",__func__);
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self createCancelRequest];
}



#pragma mark -WeXBorrowFeeSlideViewDelegate
- (void)borrowFeeSlideViewWithValue:(CGFloat)value
{
    _feeStr = [NSString stringWithFormat:@"%.0f",value];
}

#pragma mark - UIGestureRecognizerDelegate
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch
{
    // 若为UITableViewCellContentView（即点击了tableViewCell），则不截获Touch事件
    if ([NSStringFromClass([touch.view class]) isEqualToString:@"UITableViewCellContentView"]) {
        return NO;
    }
    return  YES;
}


@end
