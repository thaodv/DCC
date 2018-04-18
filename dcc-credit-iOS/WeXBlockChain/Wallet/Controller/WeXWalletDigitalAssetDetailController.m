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
#import "WeXWalletDigitalGetQuoteAdapter.h"

#import  "UIImageView+WebCache.h"
#import "WeXDigitalAssetRLMModel.h"

#import "WeXWalletTransferResultManager.h"

#import "WeXWalletEtherscanGetPendingAdapter.h"
#import "WeXWalletAllGetRecordAdapter.h"
#import "WeXWalletAllGetFeeRateAdapter.h"

#import "WeXShareQRImageView.h"
#import "WeXShareManager.h"

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
    
    UILabel *_noResLabel;
    
    UILabel *_noMoreLabel;
    UIButton *_moreBtn;
    
    NSTimer *_timer;
    
}

@property (nonatomic,strong)WeXWalletEtherscanGetRecordAdapter *getRecordAdapter;
@property (nonatomic,strong)WeXWalletEthplorerGetRecordAdapter *getEthplorerRecordAdapter;
@property (nonatomic,strong)WeXWalletDigitalGetQuoteAdapter *getQuoteAdapter;
@property (nonatomic,strong)WeXWalletEtherscanGetPendingAdapter *getPendingAdapter;
@property (nonatomic,strong)WeXWalletAllGetRecordAdapter *getAllRecordAdapter;
@property (nonatomic,strong)WeXWalletAllGetFeeRateAdapter *getRateAdapter;
@property (nonatomic,strong)NSMutableArray *datasArray;
@end

@implementation WeXWalletDigitalAssetDetailController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
//    [self setupNavgationType];
    [self setupSubViews];
    [self createGetBalaceRequest];
    [self createGetQuoteRequest];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    if ([self.tokenModel.symbol isEqualToString:@"ETH"]) {
        [self createGetRecordRequest];
    }
    else if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        [self createGetAllRecordRequest];
    }
    else
    {
        [self createGetEthplorerQuoteRequest];
    }
    
    WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
    NSMutableArray *hashAarry = [manager.dataDict objectForKey:self.tokenModel.symbol];
    if (hashAarry.count > 0) {
        [self getPendingAction];
    }
    
    
    
}


- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [_timer invalidate];
}


- (void)createGetBalaceRequest{
    
    if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        // 初始化以太坊容器
        [[WXPassHelper instance] initPassHelperBlock:^(id response) {
            if(response!=nil)
            {
                NSError* error=response;
                NSLog(@"容器加载失败:%@",error);
                return;
            }
            [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response)
             {
                 //abi方法
                 NSString *abiJson = WEX_ERC20_ABI_BALANCE;
                 //参数为需要查询的地址
                 NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
                 [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response) {
//                     [[WXPassHelper instance] callContractAddress:[WexCommonFunc getFTCContractAddress] data:response responseBlock:^(id response) {
//                         NSDictionary *responseDict = response;
//                         NSString * originBalance =[responseDict objectForKey:@"result"];
//                         NSString * ethException =[responseDict objectForKey:@"ethException"];
//                         if (![ethException isEqualToString:@"ethException"]) {
//                             NSLog(@"FTCbalance=%@",originBalance);
//                             originBalance = [NSString stringWithFormat:@"%@",originBalance];
//                             _balance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
//
//                             self.tokenModel.balance = _balance;
//                             _balancelabel.text= _balance;
//                             [self configTotalAsset];
//                         }
//                     }];
                     [[WXPassHelper instance] call2ContractAddress:[WexCommonFunc getFTCContractAddress] data:response type:YTF_DEVELOP_SERVER responseBlock:^(id response) {
                         NSDictionary *responseDict = response;
                         NSString * originBalance =[responseDict objectForKey:@"result"];
                         NSString * ethException =[responseDict objectForKey:@"ethException"];
                         if (![ethException isEqualToString:@"ethException"]) {
                             NSLog(@"FTCbalance=%@",originBalance);
                             originBalance = [NSString stringWithFormat:@"%@",originBalance];
                             _balance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                             
                             self.tokenModel.balance = _balance;
                             _balancelabel.text= _balance;
                             [self configTotalAsset];
                         }
                     }];
                     
                 }];
             }];
        }];
    }
    else
    {
        // 初始化以太坊容器
        [[WXPassHelper instance] initPassHelperBlock:^(id response) {
            if(response!=nil)
            {
                NSError* error=response;
                NSLog(@"容器加载失败:%@",error);
                return;
            }
            if ([self.tokenModel.symbol isEqualToString:@"ETH"]) {
                [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
                 {
                     [[WXPassHelper instance] getETHBalanceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
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
                 }];
            }
            else
            {
                [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
                 {
                     NSString *abiJson = WEX_ERC20_ABI_BALANCE;
                     NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
                     [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response) {
                         [[WXPassHelper instance] callContractAddress:self.tokenModel.contractAddress data:response responseBlock:^(id response) {
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
                 }];
            }
        }];
        
        
    }
    
    
    
   
    
}

- (void)createGetQuoteRequest{
    _getQuoteAdapter = [[WeXWalletDigitalGetQuoteAdapter alloc]  init];
    _getQuoteAdapter.delegate = self;
    WeXWalletDigitalGetQuoteParamModal *paramModal = [[WeXWalletDigitalGetQuoteParamModal alloc] init];
    paramModal.varietyCodes = self.tokenModel.symbol;
    [_getQuoteAdapter run:paramModal];
}

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
- (void)createGetRecordRequest{
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

#pragma -mark 获取全向交易记录请求
- (void)createGetAllRecordRequest{
    _getAllRecordAdapter = [[WeXWalletAllGetRecordAdapter alloc] init];
    _getAllRecordAdapter.delegate = self;
    WeXWalletAllGetRecordParamModal* paramModal = [[WeXWalletAllGetRecordParamModal alloc] init];
    paramModal.contractAddress = [WexCommonFunc getFTCContractAddress];
    paramModal.address = [WexCommonFunc getFromAddress];
    paramModal.page = @"1";
    paramModal.pageSize = @"1000";
    [_getAllRecordAdapter run:paramModal];
    
}

#pragma -mark 获取全向交易费率请求
- (void)createGetRateRequest{
    _getRateAdapter = [[WeXWalletAllGetFeeRateAdapter alloc] init];
    _getRateAdapter.delegate = self;
    WeXWalletAllGetFeeRateParamModal* paramModal = [[WeXWalletAllGetFeeRateParamModal alloc] init];

    [_getRateAdapter run:paramModal];
    
}


- (void)getPendingAction{
    if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        _timer = [NSTimer scheduledTimerWithTimeInterval:3 target:self selector:@selector(refreshTransferRecord) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }
    else
    {
        _timer = [NSTimer scheduledTimerWithTimeInterval:30 target:self selector:@selector(refreshTransferRecord) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    }
    
}

- (void)refreshTransferRecord{
//    WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
//    NSMutableArray *hashAarry = [manager.dataDict objectForKey:self.tokenModel.symbol];
//    if (hashAarry.count == 0) {
//        [_timer invalidate];
//        return;
//    }
    
    if ([self.tokenModel.symbol isEqualToString:@"FTC"]) {
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response)
         {
             WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
             NSMutableArray *hashAarry = [manager.dataDict objectForKey:self.tokenModel.symbol];
                 for (int i = 0; i < hashAarry.count; i++)
                 {
                     WeXWalletTransferResultModel *resultModel = hashAarry[i];
                     [[WXPassHelper instance] queryTransactionReceipt:resultModel.txhash responseBlock:^(id response) {
                         NSLog(@"1--%@",response);
                         NSString *transactionHash = [response objectForKey:@"transactionHash"];
                         if (transactionHash)
                         {
                             for (WeXWalletTransferResultModel *resultModel in hashAarry) {
                                 if ([resultModel.txhash isEqualToString:transactionHash]) {
                                     [hashAarry removeObject:resultModel];
                                     [manager.dataDict setObject:hashAarry forKey:self.tokenModel.symbol];
                                     break;
                                 }
                             }
                         }
                     }];
                 }
             [self createGetAllRecordRequest];
             [self createGetBalaceRequest];
         }];
    }
    else
    {
        WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
        NSMutableArray *hashAarry = [manager.dataDict objectForKey:self.tokenModel.symbol];
        for (int i = 0; i < hashAarry.count; i++) {
            WeXWalletTransferResultModel *resultModel = hashAarry[i];
            WeXWalletEtherscanGetPendingAdapter *getPendingAdapter = [[WeXWalletEtherscanGetPendingAdapter alloc] init];
            getPendingAdapter.delegate = self;
            WeXWalletEtherscanGetPendingParamModal *paramModal = [[WeXWalletEtherscanGetPendingParamModal alloc] init];
            paramModal.module = @"proxy";
            paramModal.action = @"eth_getTransactionReceipt";
            paramModal.txhash = resultModel.txhash;
            getPendingAdapter.txhash = resultModel.txhash;
            [getPendingAdapter run:paramModal];
        }
    }
    
    
    
  
}

//-(NSMutableArray *)datasArray
//{
//    if(_datasArray == nil){
//        _datasArray = [NSMutableArray array];
//    }
//    return _datasArray;
//}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getRecordAdapter) {
        WeXWalletEtherscanGetRecordResponseModal *responseModel = (WeXWalletEtherscanGetRecordResponseModal *)response;
        _datasArray = responseModel.result;
    
        WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
        NSMutableArray *dataArray = [manager.dataDict objectForKey:self.tokenModel.symbol];
       
        if (dataArray.count) {
            for (int i= 0 ;i < dataArray.count; i++) {
                WeXWalletTransferResultModel *resultModel = dataArray[i];
                WeXWalletEtherscanGetRecordResponseModal_item *model = [[WeXWalletEtherscanGetRecordResponseModal_item alloc] init];
                model.hashStr = resultModel.txhash;
                model.timeStamp = @"待上链";
                model.value = resultModel.value;
                [_datasArray insertObject:model atIndex:0];
            }
        }
       
        if (_datasArray.count > 3) {
            _moreBtn.hidden = NO;
        }
        [_tableView reloadData];
    }
    else if(adapter == _getQuoteAdapter){
        WeXWalletDigitalGetQuoteResponseModal *model = (WeXWalletDigitalGetQuoteResponseModal *)response;
        //有数据
        if(model.data.count >0){
            WeXWalletDigitalGetQuoteResponseModal_item *itemModel = [model.data objectAtIndex:0];
            _quotelabel.text = [NSString stringWithFormat:@"%.2f",itemModel.price];
            _highLabel.text = [NSString stringWithFormat:@"%.2f",itemModel.high];
            _lowLabel.text = [NSString stringWithFormat:@"%.2f",itemModel.low];
            _volLabel.text = [NSString stringWithFormat:@"%.2f",itemModel.volume];
            _sourceLabel.text = [NSString stringWithFormat:@"来源:%@",itemModel.sourceName];
            _price = itemModel.price;
            
            if (itemModel.close == nil) {
                _marginlabel.text = @"--";
                _percentlabel.text = @"--";
            }
            else
            {
                _marginlabel.text = [NSString stringWithFormat:@"%.2f",_price-[itemModel.close floatValue]];
                if(fabs(_price -0.000001)>0)
                {
                    _percentlabel.text = [NSString stringWithFormat:@"%.2f%%",(_price-[itemModel.close floatValue])/_price*100];
                    
                }
            }
            
            
           
            [self configTotalAsset];
            
            _noResLabel.hidden = YES;
        }
        else
        {
            _quotelabel.hidden = YES;
            _highTitleLabel.hidden = YES;
            _highLabel.hidden = YES;
            _lowTitleLabel.hidden = YES;
            _lowLabel.hidden = YES;
            _volTitlelabel.hidden = YES;
            _volLabel.hidden = YES;
            _sourceLabel.hidden = YES;
            _marginlabel.hidden = YES;
            _percentlabel.hidden = YES;
            
            _noResLabel.hidden = NO;
            
        }
       
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
        
        WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
        NSMutableArray *dataArray = [manager.dataDict objectForKey:self.tokenModel.symbol];
        if (dataArray.count) {
            for (int i= 0 ;i < dataArray.count; i++) {
                WeXWalletTransferResultModel *resultModel = dataArray[i];
                WeXWalletEtherscanGetRecordResponseModal_item *model = [[WeXWalletEtherscanGetRecordResponseModal_item alloc] init];
                model.hashStr = resultModel.txhash;
                model.timeStamp = @"待上链";
                model.value = resultModel.value;
                [_datasArray insertObject:model atIndex:0];
            }
        }
        
        if (_datasArray.count > 3) {
            _moreBtn.hidden = NO;
        }
        
        [_tableView reloadData];
       
    }
    else if([adapter isKindOfClass:[WeXWalletEtherscanGetPendingAdapter class]]){
        WeXWalletEtherscanGetPendingAdapter *responseAdapter = (WeXWalletEtherscanGetPendingAdapter *)adapter;
        WeXWalletEtherscanGetPendingResponseModal *responseModel = (WeXWalletEtherscanGetPendingResponseModal *)response;
        NSLog(@"result=%@",responseModel.result);
        if (responseModel.result == nil) {
            //删除不为pinding的key
             WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
            NSMutableArray *hashAarry = [manager.dataDict objectForKey:self.tokenModel.symbol];
            if (hashAarry.count) {
                for (int i = 0; i < hashAarry.count; i++) {
                    WeXWalletTransferResultModel *resultModel = hashAarry[i];
                    if ([resultModel.txhash isEqualToString:responseAdapter.txhash]) {
                        [hashAarry removeObject:resultModel];
                        
                        if ([self.tokenModel.symbol isEqualToString:@"ETH"]) {
                            [self createGetRecordRequest];
                        }
                        else
                        {
                            [self createGetEthplorerQuoteRequest];
                        }
                        [self createGetBalaceRequest];
                        break;
                    }
                }
            }
         
        }
        
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
        
        WeXWalletTransferResultManager *manager = [WeXWalletTransferResultManager manager];
        NSMutableArray *dataArray = [manager.dataDict objectForKey:self.tokenModel.symbol];
        NSLog(@"dataArray=%ld",(unsigned long)dataArray.count);
        if (dataArray.count) {
            for (int i= 0 ;i < dataArray.count; i++) {
                WeXWalletTransferResultModel *resultModel = dataArray[i];
                WeXWalletEtherscanGetRecordResponseModal_item *model = [[WeXWalletEtherscanGetRecordResponseModal_item alloc] init];
                model.hashStr = resultModel.txhash;
                model.timeStamp = @"待上链";
                model.value = resultModel.value;
                [_datasArray insertObject:model atIndex:0];
            }
        }
        
        if (_datasArray.count > 3) {
            _moreBtn.hidden = NO;
        }
        
        [_tableView reloadData];
        
    }
     else if([adapter isKindOfClass:[WeXWalletAllGetFeeRateAdapter class]]){
         if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
             WeXWalletAllGetFeeRateResponseModal *responseModel = (WeXWalletAllGetFeeRateResponseModal *)response;
             NSLog(@"%@",responseModel.result);
             
             WeXWalletTransferViewController *ctrl = [[WeXWalletTransferViewController alloc] init];
             ctrl.feeRate = responseModel.result;
             ctrl.tokenModel = self.tokenModel;
             [self.navigationController pushViewController:ctrl animated:YES];
         }
        
     }
}

-  (void)configTotalAsset{
    NSLog(@"_balance=%@---%.2f",_balance,_price);
    if(_balance&&![_balance isEqualToString:@"--"]&&(_price-0.00001)>0)
    {
        _valuelabel.text = [NSString stringWithFormat:@"%.2f",_price *[_balance floatValue]];
    }
    
  
}

- (void)setupNavgationType{

    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"digital_share"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
    self.navigationItem.rightBarButtonItem = rihgtItem;

}

//初始化滚动视图
-(void)setupSubViews{
    
    UIImageView *assetImageView = [[UIImageView alloc] init];
    [assetImageView sd_setImageWithURL:[NSURL URLWithString:self.tokenModel.iconUrl] placeholderImage:[UIImage imageNamed:@"ethereum"]];
    [self.view addSubview:assetImageView];
    [assetImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@45);
        make.width.equalTo(@45);
    }];
    
    UILabel *assetNamelabel = [[UILabel alloc] init];
    assetNamelabel.text = self.tokenModel.symbol;
    assetNamelabel.font = [UIFont systemFontOfSize:20];
    assetNamelabel.textColor = ColorWithLabelDescritionBlack;
    assetNamelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:assetNamelabel];
    [assetNamelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(assetImageView);
        make.leading.equalTo(assetImageView.mas_trailing).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *assetDeslabel = [[UILabel alloc] init];
    assetDeslabel.text = self.tokenModel.name;
    assetDeslabel.font = [UIFont systemFontOfSize:15];
    assetDeslabel.textColor = ColorWithLabelWeakBlack;
    assetDeslabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:assetDeslabel];
    [assetDeslabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(assetImageView.mas_bottom);
        make.leading.equalTo(assetNamelabel);
        make.height.equalTo(@20);
    }];
    
    if (!([self.tokenModel.symbol isEqualToString:@"ETH"]||[self.tokenModel.symbol isEqualToString:@"FTC"])) {
        UIButton *addBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        if ([self hasCacheSymbol]) {
            [addBtn setImage:[UIImage imageNamed:@"digital_subtract"] forState:UIControlStateNormal];
        }
        else
        {
            [addBtn setImage:[UIImage imageNamed:@"digital_add"] forState:UIControlStateNormal];

        }
        [addBtn setTitleColor:ColorWithRGB(248, 31, 117) forState:UIControlStateNormal];
        [addBtn addTarget:self action:@selector(addBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:addBtn];
        [addBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.view).offset(kNavgationBarHeight);
            make.trailing.equalTo(self.view).offset(-20);
            make.width.equalTo(@60);
            make.height.equalTo(@40);
        }];
        
    }
  
    
    UILabel *balancelabel = [[UILabel alloc] init];
    balancelabel.text = @"--";
    balancelabel.font = [UIFont systemFontOfSize:20];
    balancelabel.textColor = ColorWithLabelDescritionBlack;
    balancelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:balancelabel];
    [balancelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(assetImageView.mas_bottom).offset(10);
        make.leading.equalTo(assetImageView);
//        make.height.equalTo(@20);
    }];
    _balancelabel = balancelabel;
    
    UILabel *balanceNamelabel = [[UILabel alloc] init];
    balanceNamelabel.text = @"持有量";
    balanceNamelabel.font = [UIFont systemFontOfSize:15];
    balanceNamelabel.textColor = ColorWithLabelWeakBlack;
    balanceNamelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:balanceNamelabel];
    [balanceNamelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(balancelabel.mas_bottom).offset(5);
        make.leading.equalTo(balancelabel);
        make.height.equalTo(@20);
    }];
    //市值
    UILabel *valuelabel = [[UILabel alloc] init];
    valuelabel.text = @"--";
    valuelabel.font = [UIFont systemFontOfSize:20];
    valuelabel.textColor = ColorWithLabelDescritionBlack;
    valuelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:valuelabel];
    [valuelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(assetImageView.mas_bottom).offset(10);
        make.leading.equalTo(self.view.mas_centerX).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
//        make.height.equalTo(@20);
    }];
    _valuelabel = valuelabel;
    //市值标题
    UILabel *valueNamelabel = [[UILabel alloc] init];
    valueNamelabel.text = @"市值";
    valueNamelabel.font = [UIFont systemFontOfSize:15];
    valueNamelabel.textColor = ColorWithLabelWeakBlack;
    valueNamelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:valueNamelabel];
    [valueNamelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valuelabel.mas_bottom).offset(5);
        make.leading.equalTo(valuelabel);
        make.height.equalTo(@20);
    }];
   
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = ColorWithLabelDescritionBlack;
    line1.alpha = LINE_VIEW_ALPHA;
    [self.view addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.top.equalTo(valueNamelabel.mas_bottom).offset(10);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    UILabel *marketTitlelabel = [[UILabel alloc] init];
    marketTitlelabel.text = @"市场价格";
    marketTitlelabel.font = [UIFont systemFontOfSize:20];
    marketTitlelabel.textColor = ColorWithLabelDescritionBlack;
    marketTitlelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:marketTitlelabel];
    [marketTitlelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *quotelabel = [[UILabel alloc] init];
    quotelabel.text = @"--";
    quotelabel.font = [UIFont systemFontOfSize:20];
    quotelabel.textColor = ColorWithLabelDescritionBlack;
    quotelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:quotelabel];
    [quotelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(marketTitlelabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
    }];
    _quotelabel = quotelabel;
    
    //差额
    UILabel *marginlabel = [[UILabel alloc] init];
    marginlabel.text = @"--";
    marginlabel.font = [UIFont systemFontOfSize:15];
    marginlabel.textColor = ColorWithLabelWeakBlack;
    marginlabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:marginlabel];
    [marginlabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(quotelabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
    }];
    _marginlabel =marginlabel;
    
    //百分比
    UILabel *percentlabel = [[UILabel alloc] init];
    percentlabel.text = @"--";
    percentlabel.font = [UIFont systemFontOfSize:15];
    percentlabel.textColor = ColorWithLabelWeakBlack;
    percentlabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:percentlabel];
    [percentlabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(quotelabel.mas_bottom).offset(10);
        make.leading.equalTo(marginlabel.mas_trailing).offset(10);
        make.height.equalTo(@20);
    }];
    _percentlabel = percentlabel;
    
    
    //24 high
    UILabel *highTitlelabel = [[UILabel alloc] init];
    highTitlelabel.text = @"24H High";
    highTitlelabel.font = [UIFont systemFontOfSize:12];
    highTitlelabel.textColor = ColorWithLabelWeakBlack;
    highTitlelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:highTitlelabel];
    [highTitlelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(quotelabel);
        make.leading.equalTo(self.view.mas_centerX);
        make.height.equalTo(@10);
    }];
    _highTitleLabel = highTitlelabel;
    
    //24 high
    UILabel *highLabel = [[UILabel alloc] init];
    highLabel.text = @"--";
    highLabel.font = [UIFont systemFontOfSize:10];
    highLabel.textColor = ColorWithLabelDescritionBlack;
    highLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:highLabel];
    [highLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(highTitlelabel.mas_bottom).offset(5);
        make.leading.equalTo(highTitlelabel);
        make.height.equalTo(@10);
    }];
    _highLabel= highLabel;
    
    UILabel *lowTitlelabel = [[UILabel alloc] init];
    lowTitlelabel.text = @"24H Low";
    lowTitlelabel.font = [UIFont systemFontOfSize:12];
    lowTitlelabel.textColor = ColorWithLabelWeakBlack;
    lowTitlelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:lowTitlelabel];
    [lowTitlelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(highTitlelabel);
        make.leading.equalTo(highTitlelabel.mas_trailing).offset(30);
        make.height.equalTo(@10);
    }];
    _lowTitleLabel = lowTitlelabel;
    
    UILabel *lowLabel = [[UILabel alloc] init];
    lowLabel.text = @"--";
    lowLabel.font = [UIFont systemFontOfSize:10];
    lowLabel.textColor = ColorWithLabelDescritionBlack;
    lowLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:lowLabel];
    [lowLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(highTitlelabel.mas_bottom).offset(5);
        make.leading.equalTo(lowTitlelabel);
        make.height.equalTo(@10);
    }];
    _lowLabel = lowLabel;
    
    UILabel *volTitlelabel = [[UILabel alloc] init];
    volTitlelabel.text = @"24H Vol";
    volTitlelabel.font = [UIFont systemFontOfSize:12];
    volTitlelabel.textColor = ColorWithLabelWeakBlack;
    volTitlelabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:volTitlelabel];
    [volTitlelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(highLabel.mas_bottom).offset(10);
        make.leading.equalTo(highTitlelabel);
        make.height.equalTo(@10);
    }];
    _volTitlelabel = volTitlelabel;
    
    
    UILabel *volLabel = [[UILabel alloc] init];
    volLabel.text = @"--";
    volLabel.font = [UIFont systemFontOfSize:10];
    volLabel.textColor = ColorWithLabelDescritionBlack;
    volLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:volLabel];
    [volLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(volTitlelabel.mas_bottom).offset(5);
        make.leading.equalTo(highTitlelabel);
        make.height.equalTo(@10);
    }];
    _volLabel = volLabel;
    
    UILabel *noResLabel = [[UILabel alloc] init];
    noResLabel.hidden = YES;
    noResLabel.text = @"未能查询到该币种市场价格";
    noResLabel.font = [UIFont systemFontOfSize:16];
    noResLabel.textColor = ColorWithLabelDescritionBlack;
    noResLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:noResLabel];
    [noResLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(volTitlelabel).offset(-10);
        make.leading.trailing.equalTo(self.view);
        make.height.equalTo(@20);
    }];
    _noResLabel = noResLabel;
    
    UILabel *sourceLabel = [[UILabel alloc] init];
    sourceLabel.text = @"--";
    sourceLabel.font = [UIFont systemFontOfSize:12];
    sourceLabel.textColor = ColorWithLabelDescritionBlack;
    sourceLabel.textAlignment = NSTextAlignmentRight;
    [self.view addSubview:sourceLabel];
    [sourceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(volLabel.mas_bottom).offset(10);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(@10);
    }];
    _sourceLabel = sourceLabel;
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = ColorWithLabelDescritionBlack;
    line2.alpha = LINE_VIEW_ALPHA;
    [self.view addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.top.equalTo(sourceLabel.mas_bottom).offset(10);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    UILabel *recordTitleLabel = [[UILabel alloc] init];
    recordTitleLabel.text = @"交易记录";
    recordTitleLabel.font = [UIFont systemFontOfSize:20];
    recordTitleLabel.textColor = ColorWithLabelDescritionBlack;
    recordTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:recordTitleLabel];
    [recordTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line2.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
    }];
    
   
    
    WeXCustomButton *transferBtn = [WeXCustomButton button];
    UIImage *transferImage = [UIImage imageNamed:@"digital_transfer_red"];
    transferImage = [transferImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    [transferBtn setImage:transferImage forState:UIControlStateHighlighted];
    UIImage *transferImageHigh = [UIImage imageNamed:@"digital_transfer"];
    transferImageHigh = [transferImageHigh imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    [transferBtn setImage:transferImageHigh forState:UIControlStateNormal];
    [transferBtn setTitle:@"转账" forState:UIControlStateNormal];
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
    [receiveBtn setTitle:@"收款" forState:UIControlStateNormal];
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
    [moreBtn setTitle:@"加载更多" forState:UIControlStateNormal];
    [moreBtn setTitleColor:[UIColor lightGrayColor] forState:UIControlStateNormal];
    moreBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [moreBtn addTarget:self action:@selector(moreBtnClick) forControlEvents:UIControlEventTouchUpInside];
    moreBtn.frame = CGRectMake(0, 0, 100, 30);
    moreBtn.WeX_centerX = footView.WeX_centerX;
    moreBtn.WeX_centerY = footView.WeX_centerY;
   
    [footView addSubview:moreBtn];
    _moreBtn = moreBtn;
    
//    UILabel *noMoreLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, footView.frame.size.width, 20)];
//    noMoreLabel.text = @"没有更多了";
//    noMoreLabel.textAlignment = NSTextAlignmentCenter;
//    noMoreLabel.font = [UIFont systemFontOfSize:10];
//    noMoreLabel.textColor = [UIColor lightGrayColor];
//    [footView addSubview:noMoreLabel];
//    _noMoreLabel = noMoreLabel;

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
    [self.navigationController pushViewController:ctrl animated:YES];
    
}

- (void)addBtnClick:(UIButton *)addBtn{
    
    if ([self hasCacheSymbol]) {
        UIAlertAction *alert1 = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [WeXPorgressHUD showText:@"删除成功" onView:self.view];
            [self deleteCache];
            [addBtn setImage:[UIImage imageNamed:@"digital_add"] forState:UIControlStateNormal];
        }];
        UIAlertAction *alert2 = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        }];
        
        NSString *content = [NSString stringWithFormat:@"确认删除%@?",self.tokenModel.symbol];
        UIAlertController *alertCtrl = [UIAlertController alertControllerWithTitle:@"提示" message:content preferredStyle:UIAlertControllerStyleAlert];
        [alertCtrl addAction:alert1];
        [alertCtrl addAction:alert2];
        [self presentViewController:alertCtrl animated:YES completion:nil];
        
    }
    else
    {
        [WeXPorgressHUD showText:@"添加成功" onView:self.view];
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


- (void)transferBtnClick{
   
    
    if ([self.tokenModel.symbol isEqualToString:@"FTC"]) {
        [self createGetRateRequest];
    }
    else
    {
        WeXWalletTransferViewController *ctrl = [[WeXWalletTransferViewController alloc] init];
        ctrl.tokenModel = self.tokenModel;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}

- (void)receiveBtnClick{
    WeXPassportLocationViewController *ctrl = [[WeXPassportLocationViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}



- (void)rightItemClick
{
    WeXShareQRImageView *shareView = [[WeXShareQRImageView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:shareView];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        WeXShareManager *shareManager = [[WeXShareManager alloc] init];
        shareManager.shareImage = [self screenShot];
        [shareManager shareWithParentController:self];
    });
 
}

- (UIImage *)screenShot {
    UIImage* image = nil;
    UIGraphicsBeginImageContextWithOptions(self.view.frame.size, YES, 0.0);
    [self.view.layer renderInContext: UIGraphicsGetCurrentContext()];
    //截取当前上下文生成Image
    image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    if (image != nil) {
        return image;
    }else {
        return nil;
    }
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
    WeXWalletEtherscanGetRecordResponseModal_item *recordModel  = self.datasArray[indexPath.row];
    if ([recordModel.timeStamp isEqualToString:@"待上链"]) {
        return;
    }
    WeXWalletDigitalRecordDetailController *ctrl = [[WeXWalletDigitalRecordDetailController alloc] init];
    ctrl.recordModel = recordModel;
    ctrl.tokenModel = self.tokenModel;
    [self.navigationController pushViewController:ctrl animated:YES];
}

@end
