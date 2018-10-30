//
//  WeXTokenDccListViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/3.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXTokenDccListViewController.h"
#import "WeXWalletDigitalGetQuoteAdapter.h"

#import "WeXWalletDigitalAssetDetailController.h"
#import "WeXWalletDccDescriptionToastView.h"
#import "WeXDCCPublicToPrivateChainController.h"
#import "WeXDCCPrivateToPublicChainController.h"
#import "WeXDccAcrossChainRecordController.h"
#import "WeXWalletAlertWithCancelButtonView.h"
#import "WeXWalletTransferResultManager.h"

#import "WeXAgentMarketAdapter.h"
#import "WeXAgentMarketModel.h"

#define kTransferManagerKey self.isPrivateChain?[NSString stringWithFormat:@"%@_private",self.tokenModel.symbol]:self.tokenModel.symbol


@interface WeXTokenDccListViewController ()<UIScrollViewDelegate>
{
    UILabel *_privateBalanceLabel;
    UILabel *_privateValueLabel;
    UILabel *_publicBalanceLabel;
    UILabel *_publicValueLabel;
    
    NSString *_privateBalance;
    NSString *_publicBalance;
    
    CGFloat _price;
    
    UIImageView *_firstBackImageView;
    UIImageView *_secondBackImageView;
    UIButton *_centerTransferButton;
    
//    WeXWalletDccDescriptionToastView *_dccToastView;
    
    WeXWalletTransferResultManager *_privateRefreshManager;
    WeXWalletTransferResultManager *_publickRefreshManager;
}

@property (nonatomic,weak)WeXWalletDccDescriptionToastView *dccToastView;

//@property (nonatomic,strong)WeXWalletDigitalGetQuoteAdapter *getQuoteAdapter;
@property (nonatomic,strong)WeXAgentMarketAdapter *getAgentAdapter;
@property (nonatomic,strong)UIScrollView *mainScrollView;

@end

@implementation WeXTokenDccListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"DCC";
    [self setNavigationNomalBackButtonType];
    [self setupNavgationType];
    [self setupSubViews];
    [self createGetBalaceRequest];
    [self createGetAgentMarketRequest];
//    [self createGetQuoteRequest];

    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    BOOL showToast = [defaults boolForKey:@"dcc_toast_show_key"];
    if (!showToast) {
        [self createDccDescriptionToastView];
        [defaults setBool:YES forKey:@"dcc_toast_show_key"];
        [defaults synchronize];
    }
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self initTransferPendingManager];

}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [_publickRefreshManager endRefresh];
    [_privateRefreshManager endRefresh];
}


- (void)setupNavgationType{
    
    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"across_chain_question"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rihgtItemClick)];
    
      UIBarButtonItem *rihgtItem2 = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"across_chain_record"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rihgtItem2Click)];
    self.navigationItem.rightBarButtonItems = @[rihgtItem2,rihgtItem];
    
}

- (void)rihgtItemClick
{
    [self createDccDescriptionToastView];
}

- (void)rihgtItem2Click
{
    WeXDccAcrossChainRecordController *ctrl = [[WeXDccAcrossChainRecordController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

//获取各个币种的价格请求
- (void)createGetAgentMarketRequest{
    _getAgentAdapter = [[WeXAgentMarketAdapter alloc] init];
    //    _getAgentAdapter.currentyName = self.tokenModel.symbol;
    _getAgentAdapter.delegate = self;
    WeXAgentMarketModel *paramModal = [[WeXAgentMarketModel alloc] init];
    paramModal.coinTypes = self.tokenModel.symbol;
    [_getAgentAdapter run:paramModal];
}

- (void)createDccDescriptionToastView
{
    [self.view layoutIfNeeded];
    if (!_dccToastView) {
        WeXWalletDccDescriptionToastView *dccToastView = [[WeXWalletDccDescriptionToastView alloc] initWithFrame:self.view.bounds];
        
        CGSize size1 = dccToastView.firstImageView.frame.size;
        dccToastView.firstImageView.frame = CGRectMake(kScreenWidth*0.5-size1.width*0.5, _firstBackImageView.frame.origin.y-10, size1.width, size1.height);
        
        CGSize size2 = dccToastView.secondImageView.frame.size;
        dccToastView.secondImageView.frame = CGRectMake(CGRectGetMidX(_centerTransferButton.frame)-15, CGRectGetMidY(_centerTransferButton.frame)-size2.height, size2.width, size2.height);
        
        CGSize size3 = dccToastView.thirdImageView.frame.size;
        dccToastView.thirdImageView.frame = CGRectMake(kScreenWidth-size3.width-15, _secondBackImageView.frame.origin.y-30, size3.width, size3.height);
        
        [self.view addSubview:dccToastView];
        _dccToastView = dccToastView;
        }
 
    
}


//- (void)createGetQuoteRequest{
//    _getQuoteAdapter = [[WeXWalletDigitalGetQuoteAdapter alloc]  init];
//    _getQuoteAdapter.delegate = self;
//    WeXWalletDigitalGetQuoteParamModal *paramModal = [[WeXWalletDigitalGetQuoteParamModal alloc] init];
//    paramModal.varietyCodes = self.tokenModel.symbol;
//    [_getQuoteAdapter run:paramModal];
//}


- (void)createGetBalaceRequest{
    
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
            return;
        }
        
        [self createDccBalanceRequest];
        
    }];
    
}


- (void)createDccBalanceRequest
{
    //abi方法
    NSString *abiJson = WEX_ERC20_ABI_BALANCE;
    //参数为需要查询的地址
    NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
    [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response)
     {
         //dcc私链
         [[WXPassHelper instance] call2ContractAddress:[WexCommonFunc getDCCContractAddress] data:response type:WEX_DCC_NODE_SERVER responseBlock:^(id response)
          {
              NSLog(WeXLocalizedString(@"dcc私链balance=%@"),response);
              NSDictionary *responseDict = response;
              NSString * originBalance =[responseDict objectForKey:@"result"];
              NSString * ethException =[responseDict objectForKey:@"ethException"];
              if ([ethException isEqualToString:@"ethException"]) {
                  _privateBalance = @"--";
                  _privateBalanceLabel.text = _privateBalance;
              }
              else
              {
                  _privateBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                  _privateBalanceLabel.text = _privateBalance;
              }
              [self configDccTotalAsset];
          }];
         //dccerc20
         [[WXPassHelper instance] call2ContractAddress:self.tokenModel.contractAddress data:response type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
             NSLog(WeXLocalizedString(@"dcc共连balance=%@"),response);
             NSDictionary *responseDict = response;
             NSString * originBalance =[responseDict objectForKey:@"result"];
             NSString * ethException =[responseDict objectForKey:@"ethException"];
             if ([ethException isEqualToString:@"ethException"]) {
                 _publicBalance = @"--";
                 _publicBalanceLabel.text = _publicBalance;
             }
             else
             {
                 _publicBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                 _publicBalanceLabel.text = _publicBalance;
             }
             
             [self configDccTotalAsset];
         }];
     }];
}


#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    
   if([adapter isKindOfClass:[WeXAgentMarketAdapter class]]){
        NSLog(@"response = %@",response);
        if(response){
            WeXAgentMarketResponseModel *model= (WeXAgentMarketResponseModel *)response;
            NSLog(@"model = %@",model);
          
            if(model.data.count >0)
             {
                WeXAgentMarketResponseModel_item *agentMarketModel = model.data[0];
                _price = [agentMarketModel.price floatValue];
                [self configDccTotalAsset];
             }
             else
             {
                 _price = 0;
            }
        }
    }
//    if(adapter == _getQuoteAdapter)
//    {
//        WeXWalletDigitalGetQuoteResponseModal *model = (WeXWalletDigitalGetQuoteResponseModal *)response;
//        //有数据
//        if(model.data.count >0)
//        {
//            WeXWalletDigitalGetQuoteResponseModal_item *itemModel = [model.data objectAtIndex:0];
//            _price = itemModel.price;
//            [self configDccTotalAsset];
//        }
//        else
//        {
//            _price = 0;
//        }
//
//
//    }
}
    
    
-  (void)configDccTotalAsset{
    
    if(_privateBalance&&![_privateBalance isEqualToString:@"--"]&&(_price-0.00001)>0)
    {
        _privateValueLabel.text = [NSString stringWithFormat:@"≈¥%.2f",_price *[_privateBalance floatValue]];
    }
    
    if(_publicBalance&&![_publicBalance isEqualToString:@"--"]&&(_price-0.00001)>0)
    {
        _publicValueLabel.text = [NSString stringWithFormat:@"≈¥%.2f",_price *[_publicBalance floatValue]];
    }
    
    
}

-(void)viewDidLayoutSubviews
{
    CAGradientLayer *gradientLayer = [CAGradientLayer layer];
    gradientLayer.frame = _firstBackImageView.bounds;
    gradientLayer.colors = @[(__bridge id)ColorWithHex(0xfc7c03).CGColor,(__bridge id)ColorWithHex(0xfeae6c).CGColor];
    gradientLayer.locations = @[@0.0,@1.0];
    gradientLayer.startPoint = CGPointMake(0, 0);
    gradientLayer.endPoint = CGPointMake(1, 0);
    [_firstBackImageView.layer addSublayer:gradientLayer];
    
}

//初始化滚动视图
-(void)setupSubViews{
    
    _mainScrollView = [[UIScrollView alloc] init];
    _mainScrollView.delegate = self;
    // 隐藏水平滚动条
    _mainScrollView.showsHorizontalScrollIndicator = NO;
    _mainScrollView.showsVerticalScrollIndicator = NO;
    // 去掉弹簧效果
    _mainScrollView.bounces = NO;
    [self.view addSubview:_mainScrollView];
    
    UILabel *desLabel2 = [[UILabel alloc] init];
    desLabel2.text = @"表示DCC从公链转移到私链;";
    desLabel2.font = [UIFont systemFontOfSize:16];
    desLabel2.textColor = COLOR_LABEL_DESCRIPTION;
    desLabel2.textAlignment = NSTextAlignmentLeft;
    [self.mainScrollView addSubview:desLabel2];
    
    [_mainScrollView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsMake(kNavgationBarHeight, 0, 0, 0));
        make.bottom.equalTo(desLabel2).offset(20);
    }];
    
    UIImageView *backImageView = [[UIImageView alloc] init];
    backImageView.layer.cornerRadius = 12;
    backImageView.layer.masksToBounds = YES;
    backImageView.userInteractionEnabled = YES;
    [self.mainScrollView addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_mainScrollView).offset(10);
        make.left.equalTo(self.view).offset(15);
        make.right.equalTo(self.view).offset(-15);
        make.height.equalTo(@180);
    }];
    _firstBackImageView = backImageView;
    UITapGestureRecognizer *privateTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(privateTapClick)];
    [backImageView addGestureRecognizer:privateTap];
    
    
    UIImageView *assetImageView = [[UIImageView alloc] init];
    [assetImageView sd_setImageWithURL:[NSURL URLWithString:self.tokenModel.iconUrl] placeholderImage:[UIImage imageNamed:@"ethereum"]];
    [self.mainScrollView addSubview:assetImageView];
    [assetImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView).offset(15);
        make.leading.equalTo(backImageView).offset(10);
        make.height.equalTo(@45);
        make.width.equalTo(@45);
    }];
    
    UILabel *assetNamelabel = [[UILabel alloc] init];
    assetNamelabel.text = @"Distributed Credit Coin";
    assetNamelabel.font = [UIFont systemFontOfSize:20];
    assetNamelabel.textColor = [UIColor whiteColor];
    assetNamelabel.textAlignment = NSTextAlignmentLeft;
    [self.mainScrollView addSubview:assetNamelabel];
    [assetNamelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(assetImageView);
        make.leading.equalTo(assetImageView.mas_trailing).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *assetSymbolLabel = [[UILabel alloc] init];
    assetSymbolLabel.text = @"@Distributed Credit Chain";
    assetSymbolLabel.font = [UIFont systemFontOfSize:18];
    assetSymbolLabel.textColor = [UIColor whiteColor];
    assetSymbolLabel.textAlignment = NSTextAlignmentLeft;
    [self.mainScrollView addSubview:assetSymbolLabel];
    [assetSymbolLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(assetNamelabel.mas_bottom).offset(10);
        make.leading.equalTo(assetNamelabel);
        make.height.equalTo(@20);
    }];
    
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = [UIColor whiteColor];
    line1.alpha = 0.5;
    [self.mainScrollView addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(backImageView).offset(10);
        make.trailing.equalTo(backImageView).offset(-10);
        make.top.equalTo(assetSymbolLabel.mas_bottom).offset(20);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    
    UILabel *balanceLabel = [[UILabel alloc] init];
    balanceLabel.text = @"--";
    balanceLabel.font = [UIFont systemFontOfSize:25];
    balanceLabel.textColor = [UIColor whiteColor];
    balanceLabel.textAlignment = NSTextAlignmentCenter;
    [self.mainScrollView addSubview:balanceLabel];
    [balanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(15);
        make.leading.equalTo(backImageView).offset(10);
        make.trailing.equalTo(backImageView).offset(-10);
        make.height.equalTo(@25);
    }];
    _privateBalanceLabel = balanceLabel;
 
    UILabel *valuelabel = [[UILabel alloc] init];
    valuelabel.text = @"--";
    valuelabel.font = [UIFont systemFontOfSize:16];
    valuelabel.textColor = [UIColor whiteColor];
    valuelabel.textAlignment = NSTextAlignmentLeft;
    [self.mainScrollView addSubview:valuelabel];
    [valuelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(balanceLabel.mas_bottom).offset(10);
        make.centerX.equalTo(backImageView).offset(0);
        make.height.equalTo(@20);
    }];
    _privateValueLabel = valuelabel;
    
    
    UIButton *downBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [downBtn setBackgroundImage:[UIImage imageNamed:@"across_chain_down"] forState:UIControlStateNormal];
    [downBtn addTarget:self action:@selector(downBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.mainScrollView addSubview:downBtn];
    [downBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView.mas_bottom).offset(20);
        make.centerX.equalTo(self.view.mas_trailing).multipliedBy(1.0/4);
        make.width.equalTo(@50);
        make.height.equalTo(@50);
    }];
    _centerTransferButton = downBtn;
    
    UIButton *upBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [upBtn setBackgroundImage:[UIImage imageNamed:@"across_chain_up"] forState:UIControlStateNormal];
    [upBtn addTarget:self action:@selector(upBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.mainScrollView addSubview:upBtn];
    [upBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(downBtn);
        make.centerX.equalTo(self.view.mas_trailing).multipliedBy(3.0/4);
        make.width.equalTo(@50);
        make.height.equalTo(@50);
    }];
    
    UILabel *centerLabel = [[UILabel alloc] init];
    centerLabel.text = WeXLocalizedString(@"跨链转移");
    centerLabel.font = [UIFont systemFontOfSize:16];
    centerLabel.textColor = COLOR_THEME_ALL;
    centerLabel.textAlignment = NSTextAlignmentCenter;
    centerLabel.adjustsFontSizeToFitWidth = YES;
    [self.mainScrollView addSubview:centerLabel];
    [centerLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(downBtn);
        make.leading.equalTo(downBtn.mas_trailing).offset(5);
        make.trailing.equalTo(upBtn.mas_leading).offset(-5);
        make.height.equalTo(@20);
    }];
    
    UIImageView *backImageView2 = [[UIImageView alloc] init];
    backImageView2.image = [UIImage imageNamed:@"dcc_token_background_card"];
    backImageView2.userInteractionEnabled = YES;
    [self.mainScrollView addSubview:backImageView2];
    [backImageView2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(upBtn.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(backImageView);
    }];
    _secondBackImageView = backImageView2;
    
    UITapGestureRecognizer *publicTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(publicTapClick)];
    [backImageView2 addGestureRecognizer:publicTap];
    
    
    UIImageView *assetImageView2 = [[UIImageView alloc] init];
    [assetImageView2 sd_setImageWithURL:[NSURL URLWithString:self.tokenModel.iconUrl] placeholderImage:[UIImage imageNamed:@"ethereum"]];
    [self.mainScrollView addSubview:assetImageView2];
    [assetImageView2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView2).offset(15);
        make.leading.equalTo(backImageView2).offset(10);
        make.height.equalTo(@45);
        make.width.equalTo(@45);
    }];
    
    UILabel *assetNamelabel2 = [[UILabel alloc] init];
    assetNamelabel2.text = @"Distributed Credit Coin";
    assetNamelabel2.font = [UIFont systemFontOfSize:20];
    assetNamelabel2.textColor = [UIColor whiteColor];
    assetNamelabel2.textAlignment = NSTextAlignmentLeft;
    [self.mainScrollView addSubview:assetNamelabel2];
    [assetNamelabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(assetImageView2);
        make.leading.equalTo(assetImageView2.mas_trailing).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *assetSymbolLabel2 = [[UILabel alloc] init];
    assetSymbolLabel2.text = @"@Ethereum";
    assetSymbolLabel2.font = [UIFont systemFontOfSize:18];
    assetSymbolLabel2.textColor = [UIColor whiteColor];
    assetSymbolLabel2.textAlignment = NSTextAlignmentLeft;
    [self.mainScrollView addSubview:assetSymbolLabel2];
    [assetSymbolLabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(assetNamelabel2.mas_bottom).offset(10);
        make.leading.equalTo(assetNamelabel2);
        make.height.equalTo(@20);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = [UIColor whiteColor];
    line2.alpha = 0.5;
    [self.mainScrollView addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(backImageView).offset(10);
        make.trailing.equalTo(backImageView).offset(-10);
        make.top.equalTo(assetSymbolLabel2.mas_bottom).offset(20);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    
    UILabel *balanceLabel2 = [[UILabel alloc] init];
    balanceLabel2.text = @"--";
    balanceLabel2.font = [UIFont systemFontOfSize:25];
    balanceLabel2.textColor = [UIColor whiteColor];
    balanceLabel2.textAlignment = NSTextAlignmentCenter;
    [self.mainScrollView addSubview:balanceLabel2];
    [balanceLabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line2.mas_bottom).offset(15);
        make.leading.equalTo(backImageView).offset(10);
        make.trailing.equalTo(backImageView).offset(-10);
        make.height.equalTo(@25);
    }];
    _publicBalanceLabel = balanceLabel2;
    
    UILabel *valuelabel2 = [[UILabel alloc] init];
    valuelabel2.text = @"--";
    valuelabel2.font = [UIFont systemFontOfSize:16];
    valuelabel2.textColor = [UIColor whiteColor];
    valuelabel2.textAlignment = NSTextAlignmentLeft;
    [self.mainScrollView addSubview:valuelabel2];
    [valuelabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(balanceLabel2.mas_bottom).offset(10);
        make.centerX.equalTo(backImageView2).offset(0);
        make.height.equalTo(@20);
    }];
    _publicValueLabel = valuelabel2;
    
    UILabel *bottomTitleLabel = [[UILabel alloc] init];
    bottomTitleLabel.text = @"跨链交易说明:";
    bottomTitleLabel.font = [UIFont systemFontOfSize:16];
    bottomTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    bottomTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.mainScrollView addSubview:bottomTitleLabel];
    [bottomTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView2.mas_bottom).offset(15);
        make.leading.equalTo(backImageView2).offset(0);
        make.height.equalTo(@20);
    }];
    
    UIImageView *bottomDownImageView = [[UIImageView alloc] init];
    bottomDownImageView.image = [UIImage imageNamed:@"across_chain_small_down"];
    [self.mainScrollView addSubview:bottomDownImageView];
    [bottomDownImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(bottomTitleLabel.mas_bottom).offset(10);
        make.left.equalTo(bottomTitleLabel).offset(0);
    }];
    
    UILabel *desLabel1 = [[UILabel alloc] init];
    desLabel1.text = @"表示DCC从私链转移到公链;";
    desLabel1.font = [UIFont systemFontOfSize:16];
    desLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    desLabel1.textAlignment = NSTextAlignmentLeft;
    [self.mainScrollView addSubview:desLabel1];
    [desLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(bottomDownImageView).offset(0);
        make.leading.equalTo(bottomDownImageView.mas_trailing).offset(5);
        make.height.equalTo(@20);
    }];
    
    UIImageView *bottomUpImageView = [[UIImageView alloc] init];
    bottomUpImageView.image = [UIImage imageNamed:@"across_chain_small_up"];
    [self.mainScrollView addSubview:bottomUpImageView];
    [bottomUpImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(bottomDownImageView.mas_bottom).offset(5);
        make.leading.equalTo(bottomTitleLabel).offset(0);
    }];
    
    [desLabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(bottomUpImageView).offset(0);
        make.leading.equalTo(bottomUpImageView.mas_trailing).offset(5);
        make.height.equalTo(@20);
    }];

}

- (void)createTransferAlertView
{
    WeXWalletAlertWithCancelButtonView *alertView = [[WeXWalletAlertWithCancelButtonView alloc] initWithFrame:self.view.bounds];
    alertView.contentLabel.text = @"请待【待上链】交易变为【已上链】后再提交新的交易。";
    [self.view addSubview:alertView];
}


- (void)initTransferPendingManager
{
    _privateRefreshManager = [[WeXWalletTransferResultManager alloc] initWithTokenSymbol:self.tokenModel.symbol isPrivateChain:true response:^{
    }];
    [_privateRefreshManager beginRefresh];
    
    _publickRefreshManager = [[WeXWalletTransferResultManager alloc] initWithTokenSymbol:self.tokenModel.symbol isPrivateChain:false response:^{
    }];
    [_publickRefreshManager beginRefresh];
}


- (void)upBtnClick
{
//  2018.8.9 对于用币种区分是否有交易是否上链的修改
//    WeXWalletTransferPendingModel *pendingModel = [_publickRefreshManager getPendingModelWithSymbol:self.tokenModel.symbol];
    WeXWalletTransferPendingModel *pendingModel = [_publickRefreshManager getAllCoinPendingModel];
    if (pendingModel) {
        [self createTransferAlertView];
    }
    else
    {
        WeXDCCPublicToPrivateChainController *ctrl = [[WeXDCCPublicToPrivateChainController alloc] init];
        ctrl.tokenModel = self.tokenModel;
        [self.navigationController pushViewController:ctrl animated:YES];
    
    }
    
    
}

- (void)downBtnClick
{
    WeXWalletTransferPendingModel *pendingModel = [_privateRefreshManager getPendingModelWithSymbol:[NSString stringWithFormat:@"%@_private",self.tokenModel.symbol]];
    //  2018.8.9 对于用币种区分是否有交易是否上链的修改
//    WeXWalletTransferPendingModel *pendingModel = [_privateRefreshManager getAllCoinPendingModel];
    if (pendingModel) {
        [self createTransferAlertView];
    }
    else
    {
        WeXDCCPrivateToPublicChainController *ctrl = [[WeXDCCPrivateToPublicChainController alloc] init];
        ctrl.tokenModel = self.tokenModel;
        [self.navigationController pushViewController:ctrl animated:YES];
        
    }
}

//2018.8.10 点击私链
- (void)privateTapClick
{
    WeXWalletDigitalAssetDetailController *ctrl = [[WeXWalletDigitalAssetDetailController alloc] init];
    ctrl.tokenModel = self.tokenModel;
    ctrl.isPrivateChain = YES;
    [self.navigationController pushViewController:ctrl animated:YES];
}

//2018.8.10 点击去公链
- (void)publicTapClick
{
    WeXWalletDigitalAssetDetailController *ctrl = [[WeXWalletDigitalAssetDetailController alloc] init];
    ctrl.tokenModel = self.tokenModel;
    ctrl.isPrivateChain = NO;
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)dealloc {
    WEXNSLOG(@"%@,%s",self,__func__);
}




@end
