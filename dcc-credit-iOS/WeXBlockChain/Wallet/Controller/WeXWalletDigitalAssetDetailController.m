 //
//  WeXWalletDigitalAssetDetailController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDigitalAssetDetailController.h"
#import "WeXWalletTransferViewController.h"
#import "WeXWalletTransctionRecordCell.h"
#import "WeXWalletDigitalRecordController.h"
#import "WeXWalletDigitalRecordDetailController.h"
#import "WeXPassportLocationViewController.h"

#import "WeXWalletEtherscanGetRecordAdapter.h"
#import "WeXWalletEthplorerGetRecordAdapter.h"
//#import "WeXWalletDigitalGetQuoteAdapter.h"

#import "WeXDigitalAssetRLMModel.h"

#import "WeXWalletTransferResultManager.h"

#import "WeXWalletEtherscanGetPendingAdapter.h"
#import "WeXWalletAllGetRecordAdapter.h"
#import "WeXWalletAllGetFeeRateAdapter.h"
#import "WeXTransferAccountsDetailsController.h"
#import "WeXShareQRImageView.h"
#import "WeXShareManager.h"
#import "WeXAssetAuthorizationController.h"

#import "WeXWalletAlertWithCancelButtonView.h"
#import "WeXAgentMarketAdapter.h"
#import "WeXAgentMarketModel.h"


#define kTransferManagerKey self.isPrivateChain?[NSString stringWithFormat:@"%@_private",self.tokenModel.symbol]:self.tokenModel.symbol

@interface WeXWalletDigitalAssetDetailController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
    UILabel *_balancelabel;
    NSString *_balance;
    CGFloat _price;
    CGFloat _close;
    //市值
    UILabel *_valuelabel;
    UILabel *_quotelabel;
    UILabel *_marginlabel;
    UILabel *_percentlabel;
    UILabel *_highTitleLabel;
    UILabel *_highLabel;
    UILabel *_lowTitleLabel;
    UILabel *_lowLabel;
    UILabel *_volTitlelabel;
    UILabel *_volLabel;
    UILabel *_sourceLabel;
    UILabel *_dayTransDataLabel;
    
    UILabel *_noResLabel;
    
    UILabel *_noDataLabel;
    UIButton *_moreBtn;
    
    NSTimer *_timer;
    
    WeXWalletTransferResultManager *_refreshManager;
    
}

@property (nonatomic,strong)WeXWalletEtherscanGetRecordAdapter *getRecordAdapter;
@property (nonatomic,strong)WeXWalletEthplorerGetRecordAdapter *getEthplorerRecordAdapter;
//@property (nonatomic,strong)WeXWalletDigitalGetQuoteAdapter *getQuoteAdapter;
@property (nonatomic,strong)WeXWalletEtherscanGetPendingAdapter *getPendingAdapter;
@property (nonatomic,strong)WeXWalletAllGetRecordAdapter *getAllRecordAdapter;
@property (nonatomic,strong)WeXWalletAllGetFeeRateAdapter *getRateAdapter;
@property (nonatomic,strong)NSMutableArray *datasArray;
@property (nonatomic,strong)WeXAgentMarketAdapter *getAgentAdapter;
@property (nonatomic,strong)WeXAgentMarketResponseModel_item *agentMarketModel;

@end

@implementation WeXWalletDigitalAssetDetailController


- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setNavigationNomalBackButtonType];
    [self setupNavgationType];
    [self setupSubViews];
    [self createGetAgentMarketRequest];
    [self createGetBalaceRequest];
//    [self createGetQuoteRequest];
   
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self initTransferPendingManager];

    [self createGetRecordRequest];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [_refreshManager endRefresh];
    
}

- (void)initTransferPendingManager
{
    _refreshManager = [[WeXWalletTransferResultManager alloc] initWithTokenSymbol:self.tokenModel.symbol isPrivateChain:self.isPrivateChain response:^{
        [self createGetRecordRequest];
        [self createGetBalaceRequest];
    }];
    [_refreshManager beginRefresh];
}

- (void)createGetAgentMarketRequest{
    _getAgentAdapter = [[WeXAgentMarketAdapter alloc] init];
//    _getAgentAdapter.currentyName = self.tokenModel.symbol;
    _getAgentAdapter.delegate = self;
    WeXAgentMarketModel *paramModal = [[WeXAgentMarketModel alloc] init];
    paramModal.coinTypes = self.tokenModel.symbol;
    [_getAgentAdapter run:paramModal];
}

- (void)createGetRecordRequest
{
    if ([self.tokenModel.symbol isEqualToString:@"ETH"]) {
        [self createGetETHRecordRequest];
    }
    else if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
    {
        [self createGetAllRecordRequest:[WexCommonFunc getDCCContractAddress]];
    }
    else
    {
        [self createGetEthplorerQuoteRequest];
    }
}

- (void)createGetBalaceRequest{
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
            return;
        }
        if ([self.tokenModel.symbol isEqualToString:@"DCC"])
        {
            [self createDccBalanceRequest];
        }
        else if ([self.tokenModel.symbol isEqualToString:@"ETH"])
        {
            [self createETHBanlanceRequest];
        }
        else
        {
            [self createERC20BanlanceRequest];
        }
    }];
    
}


- (void)createDccBalanceRequest
{
    
    NSString *abiJson = WEX_ERC20_ABI_BALANCE;
    NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
    [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response)
     {
         if (self.isPrivateChain) {
             //dcc私链
             [[WXPassHelper instance] call2ContractAddress:[WexCommonFunc getDCCContractAddress] data:response type:WEX_DCC_NODE_SERVER responseBlock:^(id response)
              {
                  NSLog(WeXLocalizedString(@"dcc私链balance=%@"),response);
                  NSDictionary *responseDict = response;
                  NSString * originBalance =[responseDict objectForKey:@"result"];
                  NSString * ethException =[responseDict objectForKey:@"ethException"];
                  if (![ethException isEqualToString:@"ethException"]) {
                      originBalance = [NSString stringWithFormat:@"%@",originBalance];
                      _balance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                      self.tokenModel.balance = _balance;
                      _balancelabel.text= _balance;
                      [self configTotalAsset];
                  }
              }];
         }
         else
         {
             //dccerc20
             [[WXPassHelper instance] call2ContractAddress:self.tokenModel.contractAddress data:response type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
                 NSLog(WeXLocalizedString(@"dcc共连balance=%@"),response);
                 NSDictionary *responseDict = response;
                 NSString * originBalance =[responseDict objectForKey:@"result"];
                 NSString * ethException =[responseDict objectForKey:@"ethException"];
                 if (![ethException isEqualToString:@"ethException"]) {
                     _balance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                 }
                 else
                 {
                     _balance = @"--";
                 }
                 
                 _balancelabel.text= _balance;
                 [self configTotalAsset];
             }];
         }
   
     }];
}

- (void)createETHBanlanceRequest
{
    [[WXPassHelper instance] getETHBalance2WithContractAddress:[WexCommonFunc getFromAddress] type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
        if ([response isKindOfClass:[NSDictionary class]]) {
            _balance = @"--";
        }
        else
        {
            _balance = [WexCommonFunc formatterStringWithContractBalance:response decimals:[self.tokenModel.decimals integerValue]];
        }
        
        self.tokenModel.balance = _balance;
        _balancelabel.text= _balance;
        [self configTotalAsset];
    }];
    
}


- (void)createERC20BanlanceRequest
{
    
    NSString *abiJson = WEX_ERC20_ABI_BALANCE;
    NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
    [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response)
     {
         [[WXPassHelper instance] call2ContractAddress:self.tokenModel.contractAddress data:response type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
             NSDictionary *responseDict = response;
             NSString * originBalance =[responseDict objectForKey:@"result"];
             NSString * ethException =[responseDict objectForKey:@"ethException"];
             if (![ethException isEqualToString:@"ethException"]) {
                 _balance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
             }
             else
             {
                 _balance = @"--";
             }
             
             _balancelabel.text= _balance;
             [self configTotalAsset];
         }];
     }];
}

//- (void)createGetQuoteRequest{
//    _getQuoteAdapter = [[WeXWalletDigitalGetQuoteAdapter alloc] init];
//    _getQuoteAdapter.delegate = self;
//    WeXWalletDigitalGetQuoteParamModal *paramModal = [[WeXWalletDigitalGetQuoteParamModal alloc] init];
//    paramModal.varietyCodes = self.tokenModel.symbol;
//    [_getQuoteAdapter run:paramModal];
//}

- (void)createGetEthplorerQuoteRequest{
    _getEthplorerRecordAdapter = [[WeXWalletEthplorerGetRecordAdapter alloc]  init];
    _getEthplorerRecordAdapter.delegate = self;
    WeXWalletEthplorerGetRecordParamModal *paramModal = [[WeXWalletEthplorerGetRecordParamModal alloc] init];
    paramModal.limit = @"10";
    paramModal.token = self.tokenModel.contractAddress;
    paramModal.type = @"transfer";
    paramModal.apiKey = @"freekey";
    [_getEthplorerRecordAdapter run:paramModal];
}


#pragma -mark 获取交易记录请求
- (void)createGetETHRecordRequest{
    _getRecordAdapter = [[WeXWalletEtherscanGetRecordAdapter alloc] init];
    _getRecordAdapter.delegate = self;
    WeXWalletEtherscanGetRecordParamModal* paramModal = [[WeXWalletEtherscanGetRecordParamModal alloc] init];
    paramModal.module = @"account";
    
    paramModal.action = @"txlist";
    paramModal.tag = @"latest";
    paramModal.address = [WexCommonFunc getFromAddress];
    paramModal.sort = @"desc";
    [_getRecordAdapter run:paramModal];
    
}

#pragma -mark 获取私链交易记录请求
- (void)createGetAllRecordRequest:(NSString *)privateContractAddress{
    _getAllRecordAdapter = [[WeXWalletAllGetRecordAdapter alloc] init];
    _getAllRecordAdapter.delegate = self;
    WeXWalletAllGetRecordParamModal* paramModal = [[WeXWalletAllGetRecordParamModal alloc] init];
    paramModal.contractAddress = privateContractAddress;
    paramModal.address = [WexCommonFunc getFromAddress];
    paramModal.page = @"1";
    paramModal.pageSize = @"1000";
    [_getAllRecordAdapter run:paramModal];
    
}

-(NSMutableArray *)datasArray
{
    if(_datasArray == nil){
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
}

- (BOOL)hasSuccessTxhashWithModel:(WeXWalletTransferPendingModel *)pendingModel
{
    for (WeXWalletEtherscanGetRecordResponseModal_item *model in _datasArray) {
        if ([model.hashStr isEqualToString:pendingModel.txhash]) {
            [_refreshManager deletePendingModelWithSymbol:kTransferManagerKey];
             return YES;
        }
    }
    return NO;
    
}

- (void)configPendingTransferData
{
    //  2018.8.9 对于用币种区分是否有交易是否上链的修改
//    WeXWalletTransferPendingModel *pendingModel = [_refreshManager getAllCoinPendingModel];
    WeXWalletTransferPendingModel *pendingModel = [_refreshManager getPendingModelWithSymbol:kTransferManagerKey];
    if (pendingModel) {
        if (![self hasSuccessTxhashWithModel:pendingModel]) {
            WeXWalletEtherscanGetRecordResponseModal_item *model = [[WeXWalletEtherscanGetRecordResponseModal_item alloc] init];
            model.from = pendingModel.from;
            model.to = pendingModel.to;
            model.hashStr = pendingModel.txhash;
            model.timeStamp = pendingModel.timeStamp;
            model.value = pendingModel.value;
            model.nonce = pendingModel.nonce;
            model.gasPrice = pendingModel.gasPrice;
            model.gasLimit = pendingModel.gasLimit;
            model.isPending = true;
            [_datasArray insertObject:model atIndex:0];
        }
    }
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getRecordAdapter) {
        WeXWalletEtherscanGetRecordResponseModal *responseModel = (WeXWalletEtherscanGetRecordResponseModal *)response;
        _datasArray = responseModel.result;
        [self configPendingTransferData];
       
        if (_datasArray.count > 3) {
            _moreBtn.hidden = NO;
        }
        
        if (_datasArray.count == 0) {
            _noDataLabel.hidden = NO;
        }
        else
        {
           _noDataLabel.hidden = YES;
        }
        
        [_tableView reloadData];
    }

    else if(adapter == _getEthplorerRecordAdapter){
        WeXWalletEthplorerGetRecordResponseModal *responseModel = (WeXWalletEthplorerGetRecordResponseModal *)response;
        _datasArray = [NSMutableArray array];
        for (WeXWalletEthplorerGetRecordResponseModal_item *itemModel in responseModel.operations) {
             WeXWalletEtherscanGetRecordResponseModal_item *model = [[WeXWalletEtherscanGetRecordResponseModal_item alloc] init];
            model.from = itemModel.from;
            model.to = itemModel.to;
            model.hashStr = itemModel.transactionHash;
            model.timeStamp = itemModel.timestamp;
            model.value = itemModel.value;
            [_datasArray  addObject:model];
        }
        
       [self configPendingTransferData];
        
        if (_datasArray.count > 3) {
            _moreBtn.hidden = NO;
        }
        if (_datasArray.count == 0) {
            _noDataLabel.hidden = NO;
        }
        else
        {
            _noDataLabel.hidden = YES;
        }
        [_tableView reloadData];
       
    }
    else if([adapter isKindOfClass:[WeXWalletAllGetRecordAdapter class]]){
        WeXWalletAllGetRecordResponseModal *responseModel = (WeXWalletAllGetRecordResponseModal *)response;
        _datasArray = [NSMutableArray array];
        for (WeXWalletAllGetRecordResponseModal_item *itemModel in responseModel.items) {
            WeXWalletEtherscanGetRecordResponseModal_item *model = [[WeXWalletEtherscanGetRecordResponseModal_item alloc] init];
            model.from = itemModel.fromAddress;
            model.to = itemModel.toAddress;
            model.hashStr = itemModel.transactionHash;
            model.timeStamp = [NSString stringWithFormat:@"%f",[itemModel.blockTimestamp doubleValue]/1000];
            model.value = itemModel.value;
            model.blockNumber = itemModel.blockNumber;
            [_datasArray  addObject:model];
        }
        
        [self configPendingTransferData];
        
        if (_datasArray.count > 3) {
            _moreBtn.hidden = NO;
        }
        if (_datasArray.count == 0) {
            _noDataLabel.hidden = NO;
        }
        else
        {
            _noDataLabel.hidden = YES;
        }
        [_tableView reloadData];
        
    }
    else if([adapter isKindOfClass:[WeXAgentMarketAdapter class]]){
//        NSLog(@"response = %@",response);
        if(response){
       WeXAgentMarketResponseModel *model= (WeXAgentMarketResponseModel *)response;
        NSLog(@"model = %@",model);
            _agentMarketModel = model.data[0];
            _price = [_agentMarketModel.price floatValue];
            [self configTotalAsset];
            [self setAgentMarketData];
        }
    }
}

-  (void)configTotalAsset{
    NSLog(@"_balance=%@---%.2f",_balance,_price);
    if(_balance&&![_balance isEqualToString:@"--"]&&(_price-0.00001)>0)
    {
        _valuelabel.text = [NSString stringWithFormat:@"≈¥%.2f",_price *[_balance floatValue]];
    }

}

- (void)setupNavgationType{
 
//    UIButton *assetClickBtn = [[UIButton alloc]initWithFrame:CGRectMake(0, 5, 60, 21)];
//    [assetClickBtn setTitle:WeXLocalizedString(@"资产授权") forState:UIControlStateNormal];
//    [assetClickBtn setTitleColor:ColorWithHex(0x5756B3) forState:UIControlStateNormal];
//    [assetClickBtn addTarget:self action:@selector(assetClickBtnClick) forControlEvents:UIControlEventTouchUpInside];
//    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithCustomView:assetClickBtn];
//    self.navigationItem.rightBarButtonItem = rightButton;

}

//跳转资产授权VC
- (void)assetClickBtnClick{
    WeXAssetAuthorizationController *vc = [[WeXAssetAuthorizationController alloc]init];
    [self.navigationController pushViewController:vc animated:YES];
}

//初始化滚动视图
-(void)setupSubViews{
    
    if (!([self.tokenModel.symbol isEqualToString:@"ETH"]||[self.tokenModel.symbol isEqualToString:@"DCC"])) {
        UIButton *addBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        if ([self hasCacheSymbol]) {
            [addBtn setImage:[UIImage imageNamed:@"digital_subtract"] forState:UIControlStateNormal];
        }
        else
        {
            [addBtn setImage:[UIImage imageNamed:@"digital_add"] forState:UIControlStateNormal];
            
        }
        [addBtn addTarget:self action:@selector(addBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:addBtn];
        [addBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.view).offset(kNavgationBarHeight);
            make.trailing.equalTo(self.view).offset(-10);
            make.width.equalTo(@40);
            make.height.equalTo(@40);
        }];
    }
    
    UIImageView *backImageView = [[UIImageView alloc] init];
    [self.view addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(@180);
    }];

    UIImageView *assetImageView = [[UIImageView alloc] init];
    [assetImageView sd_setImageWithURL:[NSURL URLWithString:self.tokenModel.iconUrl] placeholderImage:[UIImage imageNamed:@"ethereum"]];
    [self.view addSubview:assetImageView];
    [assetImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView).offset(10);
        make.leading.equalTo(backImageView).offset(10);
        make.height.equalTo(@45);
        make.width.equalTo(@45);
    }];
    
    UILabel *assetNamelabel = [[UILabel alloc] init];
    assetNamelabel.text = self.tokenModel.symbol;
    assetNamelabel.font = [UIFont systemFontOfSize:20];
    assetNamelabel.textColor = COLOR_LABEL_DESCRIPTION;
    assetNamelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:assetNamelabel];
    [assetNamelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(assetImageView);
        make.leading.equalTo(assetImageView.mas_trailing).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *assetDeslabel = [[UILabel alloc] init];
    if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
    {
        assetDeslabel.text = @"@Distributed Credit Chain";
    }
    else
    {
        assetDeslabel.text = @"@Ethereum";

    }
    assetDeslabel.font = [UIFont systemFontOfSize:15];
    assetDeslabel.textColor = COLOR_LABEL_WEAK;
    assetDeslabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:assetDeslabel];
    [assetDeslabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(assetNamelabel.mas_bottom).offset(10);
        make.leading.equalTo(assetNamelabel);
        make.height.equalTo(@20);
    }];
    
//    UILabel *authorizationLabel = [[UILabel alloc] init];
//    authorizationLabel.text = WeXLocalizedString(@"已授权地址");
//    authorizationLabel.font = [UIFont systemFontOfSize:15];
//    authorizationLabel.textColor = COLOR_LABEL_WEAK;
//    authorizationLabel.textAlignment = NSTextAlignmentLeft;
//    [self.view addSubview:authorizationLabel];
//    [authorizationLabel mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(assetImageView.mas_bottom).offset(10);
//        make.leading.equalTo(assetImageView);
//        make.height.equalTo(@20);
//    }];
//
    
    UILabel *balanceLabel = [[UILabel alloc] init];
    balanceLabel.text = @"--";
    balanceLabel.font = [UIFont systemFontOfSize:25];
    balanceLabel.textColor = COLOR_THEME_ALL;
    balanceLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:balanceLabel];
    [balanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(backImageView).offset(20);
        make.leading.equalTo(backImageView).offset(10);
        make.trailing.equalTo(backImageView).offset(-10);
        make.height.equalTo(@25);
    }];
    _balancelabel = balanceLabel;
    
    UILabel *valuelabel = [[UILabel alloc] init];
    valuelabel.text = @"--";
    valuelabel.font = [UIFont systemFontOfSize:16];
    valuelabel.textColor = COLOR_LABEL_DESCRIPTION;
    valuelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:valuelabel];
    [valuelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(balanceLabel.mas_bottom).offset(10);
        make.centerX.equalTo(backImageView).offset(0);
        make.height.equalTo(@20);
    }];
    _valuelabel = valuelabel;
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = COLOR_LABEL_DESCRIPTION;
    line1.alpha = LINE_VIEW_ALPHA;
    [self.view addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.top.equalTo(backImageView.mas_bottom).offset(0);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
  
    UILabel *marketTitlelabel = [[UILabel alloc] init];
    marketTitlelabel.text = WeXLocalizedString(@"市场价格");
    marketTitlelabel.font = [UIFont systemFontOfSize:17];
    marketTitlelabel.textColor = ColorWithHex(0x333333);
    marketTitlelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:marketTitlelabel];
    [marketTitlelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView.mas_bottom).offset(18);
        make.leading.equalTo(self.view).offset(15);
        make.height.equalTo(@20);
    }];
    //市场价格具体值
    UILabel *quotelabel = [[UILabel alloc] init];
//    quotelabel.backgroundColor = [UIColor blueColor];
    quotelabel.text = @"--";
    quotelabel.font = [UIFont systemFontOfSize:20];
    quotelabel.textColor = COLOR_LABEL_DESCRIPTION;
    quotelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:quotelabel];
    [quotelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(marketTitlelabel.mas_bottom).offset(12);
        make.leading.equalTo(self.view).offset(15);
        make.height.equalTo(@20);
    }];
    _quotelabel = quotelabel;
    
    //差额涨跌值
    UILabel *marginlabel = [[UILabel alloc] init];
//    marginlabel.backgroundColor = [UIColor greenColor];
    marginlabel.text = @"--";
    marginlabel.font = [UIFont systemFontOfSize:13];
    marginlabel.textColor = ColorWithHex(0xED190F);
    marginlabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:marginlabel];
    [marginlabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(quotelabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(16);
        make.height.equalTo(@20);
    }];
    _marginlabel =marginlabel;
    
    //涨跌百分比
    UILabel *percentlabel = [[UILabel alloc] init];
//    percentlabel.backgroundColor = [UIColor redColor];
    percentlabel.text = @"--";
    percentlabel.font = [UIFont systemFontOfSize:13];
    percentlabel.textColor = ColorWithHex(0xED190F);
    percentlabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:percentlabel];
    [percentlabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(quotelabel.mas_bottom).offset(10);
        make.leading.equalTo(marginlabel.mas_trailing).offset(10);
        make.height.equalTo(@20);
    }];
    _percentlabel = percentlabel;
    
    //24h交易量默认文本
    UILabel *dayTransLabel = [[UILabel alloc]init];
//    dayTransLabel.backgroundColor = [UIColor yellowColor];
    dayTransLabel.font = [UIFont systemFontOfSize:13];
    dayTransLabel.textColor = ColorWithHex(0x9B9B9B);
    dayTransLabel.text = @"24h交易量";
    [self.view addSubview:dayTransLabel];
    [dayTransLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(marginlabel .mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(16);
        make.height.equalTo(@20);
    }];
    //24h交易量
    UILabel *dayTransDataLabel = [[UILabel alloc]init];
//    dayTransDataLabel.backgroundColor = [UIColor orangeColor];
    dayTransDataLabel.font = [UIFont systemFontOfSize:13];
    dayTransDataLabel.textColor = ColorWithHex(0x000000);
//    dayTransDataLabel.text = @"100亿";
    [self.view addSubview:dayTransDataLabel];
    [dayTransDataLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(marginlabel .mas_bottom).offset(10);
        make.leading.equalTo(dayTransLabel.mas_trailing).offset(10);
        make.height.equalTo(@20);
    }];
    _dayTransDataLabel = dayTransDataLabel;
   
    
    //1h涨跌幅
    UILabel *lowTitlelabel = [[UILabel alloc] init];
    //    lowTitlelabel.backgroundColor = [UIColor blackColor];
        lowTitlelabel.text = @"--";
    lowTitlelabel.font = [UIFont systemFontOfSize:13];
    lowTitlelabel.textColor = ColorWithHex(0x000000);
    lowTitlelabel.textAlignment = NSTextAlignmentRight;
    [self.view addSubview:lowTitlelabel];
    [lowTitlelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(quotelabel);
        make.trailing.equalTo(self.view).offset(-23);
        make.height.equalTo(@20);
        make.width.equalTo(@100);
//        make.top.equalTo(highTitlelabel);
//        make.leading.equalTo(highTitlelabel.mas_trailing).offset(30);
//        make.height.equalTo(@20);
    }];
    _lowTitleLabel = lowTitlelabel;
    
    //1h涨跌幅默认文本
    UILabel *highTitlelabel = [[UILabel alloc] init];
//    highTitlelabel.backgroundColor = [UIColor yellowColor];
    highTitlelabel.text = @"1h涨跌幅";
    highTitlelabel.font = [UIFont systemFontOfSize:13];
    highTitlelabel.textColor = ColorWithHex(0x9B9B9B);
    highTitlelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:highTitlelabel];
    [highTitlelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(lowTitlelabel);
        make.trailing.equalTo(lowTitlelabel).offset(-40);
        make.height.equalTo(@20);
        make.width.equalTo(@80);
    }];
    _highTitleLabel = highTitlelabel;
    
   
    //7d涨跌幅
    UILabel *lowLabel = [[UILabel alloc] init];
    //    lowLabel.backgroundColor = [UIColor redColor];
    lowLabel.text = @"--";
    lowLabel.font = [UIFont systemFontOfSize:13];
    lowLabel.textColor = ColorWithHex(0x000000);
    lowLabel.textAlignment = NSTextAlignmentRight;
    [self.view addSubview:lowLabel];
    [lowLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(highTitlelabel.mas_bottom).offset(9);
        make.trailing.equalTo(self.view).offset(-23);
        make.height.equalTo(@20);
        make.width.equalTo(@100);
    }];
    _lowLabel = lowLabel;
    
    //7d涨跌幅默认文本
    UILabel *highLabel = [[UILabel alloc] init];
//    highLabel.backgroundColor = [UIColor orangeColor];
    highLabel.text = @"7d涨跌幅";
    highLabel.font = [UIFont systemFontOfSize:13];
    highLabel.textColor = ColorWithHex(0x9B9B9B);
    highLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:highLabel];
    [highLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(highTitlelabel.mas_bottom).offset(9);
        make.leading.equalTo(highTitlelabel);
        make.height.equalTo(@20);
    }];
    _highLabel= highLabel;
    
    //最新更新时间
    UILabel *volLabel = [[UILabel alloc] init];
    //    volLabel.backgroundColor = [UIColor brownColor];
    volLabel.text = @"--";
    volLabel.font = [UIFont systemFontOfSize:13];
    volLabel.textColor = ColorWithHex(0x000000);
    volLabel.textAlignment = NSTextAlignmentRight;
    [self.view addSubview:volLabel];
    [volLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(highLabel.mas_bottom).offset(11);
        make.trailing.equalTo(self.view).offset(-23);
        make.height.equalTo(@20);
        make.width.equalTo(@100);
    }];
    _volLabel = volLabel;
    
    //最新更新时间文本
    UILabel *volTitlelabel = [[UILabel alloc] init];
//    volTitlelabel .backgroundColor = [UIColor blueColor];
    volTitlelabel.text = @"最新更新";
    volTitlelabel.font = [UIFont systemFontOfSize:13];
    volTitlelabel.textColor = ColorWithHex(0x9B9B9B);
    volTitlelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:volTitlelabel];
    [volTitlelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(highLabel.mas_bottom).offset(11);
        make.leading.equalTo(highTitlelabel);
        make.height.equalTo(@20);
    }];
    _volTitlelabel = volTitlelabel;
    
//    UILabel *noResLabel = [[UILabel alloc] init];
//    noResLabel.backgroundColor = [UIColor yellowColor];
//    noResLabel.hidden = YES;
//    noResLabel.text = @"未能查询到该币种市场价格";
//    noResLabel.font = [UIFont systemFontOfSize:16];
//    noResLabel.textColor = COLOR_LABEL_DESCRIPTION;
//    noResLabel.textAlignment = NSTextAlignmentCenter;
//    [self.view addSubview:noResLabel];
//    [noResLabel mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(volTitlelabel).offset(-10);
//        make.leading.trailing.equalTo(self.view);
//        make.height.equalTo(@20);
//    }];
//    _noResLabel = noResLabel;
    
//    UILabel *sourceLabel = [[UILabel alloc] init];
//    sourceLabel.backgroundColor = [UIColor redColor];
//    sourceLabel.text = @"--";
//    sourceLabel.font = [UIFont systemFontOfSize:12];
//    sourceLabel.textColor = COLOR_LABEL_DESCRIPTION;
//    sourceLabel.textAlignment = NSTextAlignmentRight;
//    [self.view addSubview:sourceLabel];
//    [sourceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(volLabel.mas_bottom).offset(10);
//        make.trailing.equalTo(self.view).offset(-20);
//        make.height.equalTo(@10);
//    }];
//    _sourceLabel = sourceLabel;
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = COLOR_LABEL_DESCRIPTION;
    line2.alpha = LINE_VIEW_ALPHA;
    [self.view addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.top.equalTo(dayTransLabel.mas_bottom).offset(20);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *recordTitleLabel = [[UILabel alloc] init];
    recordTitleLabel.text = WeXLocalizedString(@"交易记录");
    recordTitleLabel.font = [UIFont systemFontOfSize:16];
    recordTitleLabel.textColor = ColorWithHex(0x333333);
    recordTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:recordTitleLabel];
    [recordTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line2.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(15);
        make.height.equalTo(@20);
    }];
    
    WeXCustomButton *transferBtn = [WeXCustomButton button];
    UIImage *transferImage = [UIImage imageNamed:@"digital_transfer_red"];
    transferImage = [transferImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    [transferBtn setImage:transferImage forState:UIControlStateHighlighted];
    UIImage *transferImageHigh = [UIImage imageNamed:@"digital_transfer"];
    transferImageHigh = [transferImageHigh imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    [transferBtn setImage:transferImageHigh forState:UIControlStateNormal];
    [transferBtn setTitle:WeXLocalizedString(@"转账") forState:UIControlStateNormal];
    [transferBtn setImageEdgeInsets:UIEdgeInsetsMake(0, 0, 0, 8)];
    [transferBtn setTitleEdgeInsets:UIEdgeInsetsMake(0, 8, 0, 0)];
    [transferBtn addTarget:self action:@selector(transferBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:transferBtn];
    [transferBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@50);
    }];
    
    WeXCustomButton *receiveBtn = [WeXCustomButton button];
    [receiveBtn setTitle:WeXLocalizedString(@"收款") forState:UIControlStateNormal];
    UIImage *receiveImage = [UIImage imageNamed:@"digital_receive_red"];
    receiveImage = [receiveImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    [receiveBtn setImage:receiveImage forState:UIControlStateHighlighted];
    
    UIImage *receiveImageHigh = [UIImage imageNamed:@"digital_receive"];
    receiveImageHigh = [receiveImageHigh imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    [receiveBtn setImage:receiveImageHigh forState:UIControlStateNormal];
    [receiveBtn setImageEdgeInsets:UIEdgeInsetsMake(0, 0, 0, 8)];
    [receiveBtn setTitleEdgeInsets:UIEdgeInsetsMake(0, 8, 0, 0)];
    [receiveBtn addTarget:self action:@selector(receiveBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:receiveBtn];
    [receiveBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(transferBtn);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(transferBtn);
        make.leading.equalTo(transferBtn.mas_trailing).offset(10);
        make.height.equalTo(transferBtn);
    }];
    
    
    _tableView = [[UITableView alloc] init];
    UIView *footView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 30)];
    
    UIButton *moreBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    moreBtn.hidden = YES;
    [moreBtn setTitle:WeXLocalizedString(@"加载更多") forState:UIControlStateNormal];
    [moreBtn setTitleColor:[UIColor lightGrayColor] forState:UIControlStateNormal];
    moreBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [moreBtn addTarget:self action:@selector(moreBtnClick) forControlEvents:UIControlEventTouchUpInside];
    moreBtn.frame = CGRectMake(0, 0, 100, 30);
    moreBtn.WeX_centerX = footView.WeX_centerX;
    moreBtn.WeX_centerY = footView.WeX_centerY;
   
    [footView addSubview:moreBtn];
    _moreBtn = moreBtn;
    
    UILabel *noMoreLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, footView.frame.size.width, 20)];
    noMoreLabel.hidden = YES;
    noMoreLabel.text = WeXLocalizedString(@"未查询到该币种交易记录");
    noMoreLabel.textAlignment = NSTextAlignmentCenter;
    noMoreLabel.font = [UIFont systemFontOfSize:16];
    noMoreLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [footView addSubview:noMoreLabel];
    _noDataLabel = noMoreLabel;

    _tableView.tableFooterView = footView;
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 70;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(recordTitleLabel.mas_bottom).offset(10);
        make.bottom.equalTo(receiveBtn.mas_top).offset(-10);
        make.leading.trailing.equalTo(self.view);
    }];
    
    
}

- (void)moreBtnClick{
    WeXWalletDigitalRecordController *ctrl = [[WeXWalletDigitalRecordController alloc] init];
    ctrl.tokenModel = self.tokenModel;
    ctrl.isPrivateChain = self.isPrivateChain;
    [self.navigationController pushViewController:ctrl animated:YES];
    
}

- (void)addBtnClick:(UIButton *)addBtn{
    
    if ([self hasCacheSymbol]) {
        UIAlertAction *alert1 = [UIAlertAction actionWithTitle:WeXLocalizedString(@"确定") style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [WeXPorgressHUD showText:WeXLocalizedString(@"删除成功") onView:self.view];
            [self deleteCache];
            [addBtn setImage:[UIImage imageNamed:@"digital_add"] forState:UIControlStateNormal];
        }];
        UIAlertAction *alert2 = [UIAlertAction actionWithTitle:WeXLocalizedString(@"取消") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        }];
        
        NSString *content = [NSString stringWithFormat:WeXLocalizedString(@"确认删除%@?"),self.tokenModel.symbol];
        UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:WeXLocalizedString(@"提示") message:content preferredStyle:UIAlertControllerStyleAlert];
        [alertCtrl addAction:alert1];
        [alertCtrl addAction:alert2];
        [self presentViewController:alertCtrl animated:YES completion:nil];
        
    }
    else
    {
        [WeXPorgressHUD showText:WeXLocalizedString(@"添加成功") onView:self.view];
        [self addCache];
        [addBtn setImage:[UIImage imageNamed:@"digital_subtract"] forState:UIControlStateNormal];
        
    }
}

- (void)deleteCache
{
    RLMResults <WeXDigitalAssetRLMModel *>*results = [WeXDigitalAssetRLMModel allObjects];
    for (WeXDigitalAssetRLMModel *rlmModel in results) {
        if([rlmModel.symbol isEqualToString:self.tokenModel.symbol])
        {
            [[RLMRealm defaultRealm] beginWriteTransaction];
            [[RLMRealm defaultRealm] deleteObject:rlmModel];
            [[RLMRealm defaultRealm] commitWriteTransaction];
        }
    }
}

- (void)addCache
{
    WeXDigitalAssetRLMModel *rlmModel = [[WeXDigitalAssetRLMModel alloc] init];
    rlmModel.name = self.tokenModel.name;
    rlmModel.symbol = self.tokenModel.symbol;
    rlmModel.iconUrl = self.tokenModel.iconUrl;
    rlmModel.decimals = self.tokenModel.decimals;
    rlmModel.contractAddress = self.tokenModel.contractAddress;
    
    RLMRealm *realm = [RLMRealm defaultRealm];
    [realm beginWriteTransaction];
    [realm addObject:rlmModel];
    [realm commitWriteTransaction];
 
}

- (BOOL)hasCacheSymbol{
    RLMResults <WeXDigitalAssetRLMModel *>*results = [WeXDigitalAssetRLMModel allObjects];
    NSMutableArray *cacheSymbolArray = [NSMutableArray array];
    for (WeXDigitalAssetRLMModel *rlmModel in results) {
        NSLog(@"symbol=%@",rlmModel.symbol);
        [cacheSymbolArray addObject:rlmModel.symbol];
    }
    
    if ([cacheSymbolArray containsObject:self.tokenModel.symbol]) {
        return YES;
    }
    
    return NO;
}


- (void)createTransferAlertView
{
    WeXWalletAlertWithCancelButtonView *alertView = [[WeXWalletAlertWithCancelButtonView alloc] initWithFrame:self.view.bounds];
    alertView.contentLabel.text = WeXLocalizedString(@"请待【待上链】交易变为【已上链】后再提交新的交易。");
    [self.view addSubview:alertView];
}

// MARK: - 转账功能
//2018.7.24 转账按钮
- (void)transferBtnClick{
    WeXWalletTransferPendingModel *pendingModel = nil;
    //2018.8.10 公私链互不影响
    if (self.isPrivateChain) { //私链转账
       pendingModel = [_refreshManager getPendingModelWithSymbol:kTransferManagerKey];
    } else {
        //公链转账
        pendingModel = [_refreshManager getAllCoinPendingModel];
    }
    if (pendingModel) {
        [self createTransferAlertView];
    }
    else
    {
        //新增新的转账界面
        WeXTransferAccountsDetailsController *ctrl = [[WeXTransferAccountsDetailsController alloc]init];
        ctrl.tokenModel = self.tokenModel;
        ctrl.isPrivateChain = self.isPrivateChain;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    
}

// MARK: - 收款功能
//2018.7.24 收款按钮
- (void)receiveBtnClick{
    WeXPassportLocationViewController *ctrl = [[WeXPassportLocationViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.datasArray.count>= 3?3:self.datasArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellID = @"cellID";
    WeXWalletTransctionRecordCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletTransctionRecordCell" owner:self options:nil] lastObject];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;

    }
    WeXWalletEtherscanGetRecordResponseModal_item *model = self.datasArray[indexPath.row];
    [cell configWithRecodModel:model tokenModel:self.tokenModel];
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXWalletEtherscanGetRecordResponseModal_item *recordModel = self.datasArray[indexPath.row];
    if (recordModel.isPending&&self.isPrivateChain) {
        return;
    }
    WeXWalletDigitalRecordDetailController *ctrl = [[WeXWalletDigitalRecordDetailController alloc] init];
    ctrl.recordModel = recordModel;
    ctrl.tokenModel = self.tokenModel;
    ctrl.isPrivateChain = self.isPrivateChain;
    [self.navigationController pushViewController:ctrl animated:YES];
}

//设置代币详情数据
- (void)setAgentMarketData{
//    NSLog(@"self.agentMarketModel = %@",self.agentMarketModel);
    _quotelabel.text = [NSString stringWithFormat:@"￥%.4f",[self.agentMarketModel.price floatValue]];
    if ([self.agentMarketModel.price_change_24 floatValue]>0) {
        _marginlabel.text = [NSString stringWithFormat:@"+%.4f",[self.agentMarketModel.price_change_24 floatValue]];
//        _marginlabel.textColor = ColorWithHex(0xED190F);
         _marginlabel.textColor = ColorWithHex(0x7ED321);
    }else{
        _marginlabel.text = [NSString stringWithFormat:@"%.4f",[self.agentMarketModel.price_change_24 floatValue]];
//         _marginlabel.textColor = ColorWithHex(0x7ED321);
        _marginlabel.textColor = ColorWithHex(0xED190F);
        
    }
    if ([self.agentMarketModel.price_change_24 floatValue]>0) {
        _percentlabel.text = [[NSString stringWithFormat:@"%.2f",[self.agentMarketModel.percent_change_24h floatValue]]stringByAppendingString:@"%"];
         _percentlabel.textColor = ColorWithHex(0x7ED321);
//        _percentlabel.textColor = ColorWithHex(0xED190F);
    }else{
        _percentlabel.text = [[NSString stringWithFormat:@"%.2f",[self.agentMarketModel.percent_change_24h floatValue]]stringByAppendingString:@"%"];
//        _percentlabel.textColor = ColorWithHex(0x7ED321);
         _percentlabel.textColor = ColorWithHex(0xED190F);
    }
    
    if ([self.agentMarketModel.volume_24 floatValue]>100000000) {
        _dayTransDataLabel.text = [NSString stringWithFormat:@"%.2f亿",([self.agentMarketModel.volume_24  floatValue]/100000000)];
    } else if ([self.agentMarketModel.volume_24 floatValue]>10000){
         _dayTransDataLabel.text = [NSString stringWithFormat:@"%.2f万",([self.agentMarketModel.volume_24  floatValue]/10000)];
    } else {
        _dayTransDataLabel.text = [NSString stringWithFormat:@"%.2f",[self.agentMarketModel.volume_24 floatValue]];
    }
    if ([self.agentMarketModel.percent_change_1h floatValue]>0) {
        _lowTitleLabel.text = [[NSString stringWithFormat:@"+%.2f",[self.agentMarketModel.percent_change_1h floatValue]]stringByAppendingString:@"%"];
    }else{
        _lowTitleLabel.text = [[NSString stringWithFormat:@"%.2f",[self.agentMarketModel.percent_change_1h floatValue]]stringByAppendingString:@"%"];
    }
    if ([self.agentMarketModel.percent_change_7d floatValue]>0) {
        _lowLabel.text = [[NSString stringWithFormat:@"+%.2f",[self.agentMarketModel.percent_change_7d floatValue]]stringByAppendingString:@"%"];
    }else{
        _lowLabel.text = [[NSString stringWithFormat:@"%.2f",[self.agentMarketModel.percent_change_7d floatValue]]stringByAppendingString:@"%"];
    }
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:@"HH:mm:ss"];
    NSDate *confromTimesp = [NSDate dateWithTimeIntervalSince1970:[self.agentMarketModel.timeStamp doubleValue]];
//    NSLog(@"1532487514 = %@",confromTimesp);
    NSString *confromTimespStr = [formatter stringFromDate:confromTimesp];
//    NSLog(@"confromTimespStr =  %@",confromTimespStr);
    _volLabel.text = confromTimespStr;
}

@end
