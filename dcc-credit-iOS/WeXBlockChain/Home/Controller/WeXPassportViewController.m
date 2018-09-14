//
//  WeXPassportViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportViewController.h"
#import "WeXPassportCardCell.h"
#import "WeXCardViewController.h"
#import "WeXCardSettingViewController.h"
#import "WeXScanViewController.h"
#import "WeXPassportIDViewController.h"
#import "WeXWalletDigitalAssetListController.h"
#import "WeXPassportLocationViewController.h"

#import "WeXWalletDigitalGetQuoteAdapter.h"

#import "WeXWalletDigitalListCell.h"
#import "WeXDigitalAssetRLMModel.h"
#import "WeXWalletDigitalGetTokenModal.h"


#import "WeXAllShow1ViewController.h"
#import "WeXAllShow2ViewController.h"

#import "WeXCreditHomeViewController.h"


#import "WeXCreatePassportChooseView.h"

#import "WeXCreatePassportViewController.h"

#import "WeXImportViewController.h"

#import "WeXActivityHomeViewController.h"
#import "WeXCreditBorrowUSDTViewController.h"
#import "WeXLoanReportHomeViewController.h"

#import "WeXGetRedPacketView.h"

#import "WeXGetMemberIdAdapter.h"
#import "WeXBorrowGetNonceAdapter.h"
#import "WeXRegisterMemberAdapter.h"

#import "WeXActvityApplyBonusAdapter.h"
#import "WeXActivityQueryBonusAdapter.h"

#import "WeXAddInviteCodeView.h"

#import "YYKeyboardManager.h"

#import "WeXP2PImageCardCell.h"

#import "WeXP2PLoanCoinWebViewController.h"
#import "UIWebViewController.h"
#import "NSString+WexTool.h"
#import "WeXAddressWebViewController.h"

#import "WeXActivityZoologyAwardController.h"
#import "WeXInviteFriendViewController.h"
#import "WeXVersionUpdateManager.h"
#import "WeXAgentMarketAdapter.h"
#import "WeXAgentMarketModel.h"
//#import "WeXNetworkCheckManager.h"
#import "WeXSelectedNodeViewController.h"
#import "WeXCoinProfitCell.h"
#import "WeXGuideViewManager.h"
#import "WeXCoinProfitGuideView.h"
#import "WeXCoinProfitDetailViewController.h"
#import "WeXWalletNewCell.h"
#import "WeXDisPlayAdapter.h"
#import "WeXOpenParamModel.h"
#import "WeXLocalCacheManager.h"
//ipfs处理
#import "WeXIpfsKeyGetAdapter.h"
#import "WeXIpfsKeyGetModel.h"
#import "WeXIpfsContractHeader.h"

static NSString * const reuseWexPassIdentifier = @"reuseWexPassIdentifier";
static NSString * const kP2PCardCellIdentifier = @"WeXP2PImageCardCellIdentifier";
static NSString * const kCoinProfitCellIdentifier = @"WeXCoinProfitCellID";
static NSString * const kNewWalletCellID = @"WeXWalletNewCellID";

static const CGFloat kCardHeightWidthRatio = 179.0/350.0;
static const CGFloat kInviteFriendCardHeightWidthRatio = 135.0/345.0;

@interface WeXPassportViewController ()<UITableViewDataSource,UITableViewDelegate,WeXCreatePassportChooseViewDelegate,YYKeyboardObserver,WeXGetRedPacketViewDelegate>
{
    NSString *_encodeData;
    UILabel *_assetlabel;//总的数字资产
    
    WeXPasswordCacheModal *_model;
    BOOL _isOpen;//数字资产开关是否打开
    
    UILabel *_nickNameLabel;
    UILabel *_addressLabel;
    UIImageView *_headImageView;
    
    NSString *_dccPrivateBalance;
    NSString *_dccPublicBalance;
    
    NSString *_nonce;
    
    WeXAddInviteCodeView *_inviteCodeView;
    
    WeXGetRedPacketView *_redPacketView;
    
    BOOL _isRegister;
    
    NSString *_inviteCode;
    NSString *_assetStr;
    NSString *_bonusId;
    UIView *_backView;
}

@property (nonatomic,strong)WeXWalletDigitalGetQuoteAdapter *getQuoteAdapter;

@property (nonatomic,strong)WeXBorrowGetNonceAdapter *getNonceAdapter;

@property (nonatomic,strong)WeXGetMemberIdAdapter *getMemberAdapter;

@property (nonatomic,strong)WeXRegisterMemberAdapter *registerAdapter;

@property (nonatomic,strong)WeXActivityQueryBonusAdapter *queryBonusAdapter;

@property (nonatomic,strong)WeXActvityApplyBonusAdapter *applyBonusAdapter;

@property (nonatomic,strong)NSMutableArray *datasArray;

@property (nonatomic, strong) WeXNetworkCheckModel *nodeDelayModel;

@property (nonatomic, strong) WeXCoinProfitGuideView *profitGuideView;

@property (nonatomic,strong)WeXDisPlayAdapter *playAdapter;

@property (nonatomic,assign)BOOL isAllowDisPlay;
//ipfs
@property (nonatomic,copy)NSString *contractAddress;
@property (nonatomic,strong)WeXIpfsKeyGetAdapter *getIpfsKeyAdapter;

@end

@implementation WeXPassportViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.automaticallyAdjustsScrollViewInsets = NO;
    [[YYKeyboardManager defaultManager] addObserver:self];
    [self setupNavgationType];
    [self commonInit];
    [self setupSubViews];
    
    WeXVersionUpdateManager *manager = [WeXVersionUpdateManager shareManager];
    [manager configVersionUpdateViewOnView:self.navigationController.view isUpdate:true];
    
    [self configExceptionRegisterQuestion];
    
    //如果本地没有缓存则是隐藏状态
    if (![WeXLocalCacheManager getAppearWithBundleID:[WexCommonFunc getVersion]]) {
        [self createPlayRequest];
    } else {
        _isAllowDisPlay = [WeXLocalCacheManager getAppearWithBundleID:[WexCommonFunc getVersion]];
    }
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateWithCheckModel) name:WEX_CHECK_MODEL_NOTIFY object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateNickName) name:WEX_CHANGE_NICK_NAME_NOTIFY object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateHeadImage) name:WEX_CHANGE_HEAD_IMAGE_NOTIFY object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateAddressChange) name:WEX_CHANGE_ADDRESS_NOTIFY object:nil];
    
    NSString *documentPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
    WEXNSLOG(@"documentPath=%@",documentPath);
}

- (void)configExceptionRegisterQuestion
{
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    NSString *address = [model.keyStore objectForKey:@"address"];
    if (address&&!model.hasMemberId) {
        [self createGetNonceRequest];
    }
}

- (void)createPlayRequest{
    if (!_playAdapter) {
        _playAdapter = [[WeXDisPlayAdapter alloc] init];
    }
    _playAdapter.delegate = self;
    WeXOpenParamModel *paramModel = [WeXOpenParamModel new];
    paramModel.platform = @"IOS";
    paramModel.version  = [WexCommonFunc getVersion];
    [_playAdapter run:paramModel];
}

#pragma -mark 发送请求
- (void)createGetNonceRequest{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    _getNonceAdapter = [[WeXBorrowGetNonceAdapter alloc] init];
    _getNonceAdapter.delegate = self;
    WeXBorrowGetNonceParamModal* paramModal = [[WeXBorrowGetNonceParamModal alloc] init];
    [_getNonceAdapter run:paramModal];
}

#pragma -mark 获取数据发送请求
- (void)createGetMemberRequest{
    _getMemberAdapter = [[WeXGetMemberIdAdapter alloc] init];
    _getMemberAdapter.delegate = self;
    WeXGetMemberIdParamModal *modal = [[WeXGetMemberIdParamModal alloc] init];
    modal.nonce = _nonce;
    modal.address = [WexCommonFunc getFromAddress];
    [_getMemberAdapter run:modal];
    
}

#pragma -mark 发送请求
- (void)createQuerytBonusRequest{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    _queryBonusAdapter = [[WeXActivityQueryBonusAdapter alloc] init];
    _queryBonusAdapter.delegate = self;
    [_queryBonusAdapter run:nil];
}

- (void)createApplyBonusRequest{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    _applyBonusAdapter = [[WeXActvityApplyBonusAdapter alloc] init];
    _applyBonusAdapter.delegate = self;
    WeXActvityApplyBonusParamModal *model = [[WeXActvityApplyBonusParamModal alloc] init];
    model.redeemTokenId = _bonusId;
    [_applyBonusAdapter run:model];
    
}

#pragma -mark 获取数据发送请求
- (void)createRegisterRequest{
    _registerAdapter = [[WeXRegisterMemberAdapter alloc] init];
    _registerAdapter.delegate = self;
    WeXRegisterMemberParamModal *model = [[WeXRegisterMemberParamModal alloc] init];
    model.nonce = _nonce;
    model.address = [WexCommonFunc getFromAddress];
    model.loginName = [WexCommonFunc getFromAddress];
    model.inviteCode = _inviteCode;
    [_registerAdapter run:model];
    
}

- (void)updateNickName
{
    _nickNameLabel.text = [WexDefaultConfig instance].nickName;
}

- (void)updateHeadImage
{
    UIImage *headImage = [IYFileManager cacheImageFileWithKey:WEX_FILE_USER_FACE];
    if (headImage == nil) {
        _headImageView.image = [UIImage imageNamed:@"digital_head"];
    }
    else
    {
        _headImageView.image = headImage;
    }
}

- (void)updateAddressChange
{
    NSString *address = [WexCommonFunc getFromAddress];
    if (address.length>8) {
        NSString *subAddress1 = [address substringWithRange:NSMakeRange(address.length-8, 4)];
        NSString *subAddress2 = [address substringWithRange:NSMakeRange(address.length-4, 4)];
        _addressLabel.text = [NSString stringWithFormat:@"0x  %@  %@",subAddress1,subAddress2];
    }
}

- (void)updateWithCheckModel
{
    if (WEX_DIGITAL_CHECK_MODEL) {
        _isOpen = YES;
    }
    else
    {
        _isOpen = YES;
    }
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [_cardTabelView reloadData];
    });
}

- (void)commonInit{
    
    _model = [WexCommonFunc getPassport];
    
    if (WEX_DIGITAL_CHECK_MODEL) {
        _isOpen = YES;
    }
    else
    {
        _isOpen = YES;
    }
    
    NSLog(@"_isOpen=%d",_isOpen);
    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.delegate = nil;
    [self getDatas];
    [self getDefaultNodeDelay];
    
    if ([self hasPassport]) {
//        [self judgeIsDisplaySearchChainView];
        [self addCoinProfitGuideView];
//        [self createGetQuoteRequest];
        [self createGetAgentMarketRequest];
        [self createGetBalaceRequest];
    }
}

- (void)showPasswordSetDescription{
    if (_model) {
        if (_model.passwordType!= WeXPasswordTypeNone) {
            [WeXPorgressHUD showText:@"密码设置成功!" onView:self.view];
        }
    }
}

#pragma mark - 判断是否有钱包
- (BOOL)hasPassport
{
    
    NSString *address = [WexCommonFunc getFromAddress];
    if (!address) {
        return NO;
    }
    return YES;
}

#pragma mark - 创建钱包入口页面
- (void)createPassportChooseView
{
    WeXCreatePassportChooseView *chooseView = [[WeXCreatePassportChooseView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    chooseView.delegate = self;
    [self.view addSubview:chooseView];
}

- (void)getDatas{
    [self.datasArray removeAllObjects];
    RLMResults <WeXDigitalAssetRLMModel *>*results = [WeXDigitalAssetRLMModel allObjects];
    for (WeXDigitalAssetRLMModel *rlmModel in results) {
        WeXWalletDigitalGetTokenResponseModal_item *model =  [[WeXWalletDigitalGetTokenResponseModal_item alloc] init];
        model.symbol =rlmModel.symbol;
        model.name = rlmModel.name;
        model.iconUrl = rlmModel.iconUrl;
        model.decimals = rlmModel.decimals;
        model.contractAddress = rlmModel.contractAddress;
        [self.datasArray addObject:model];
    }
    NSLog(@"count=%ld",self.datasArray.count);
    [_tokenTabelView reloadData];
}
//2018.8.7 获取默认节点的延迟
// MARK: - 获取节点延迟
- (void)getDefaultNodeDelay {
    [[WeXNetworkCheckManager shareManager] startDefaultNodeNetworkDelay:^(WeXNetworkCheckModel *result) {
        self.nodeDelayModel = result;
        [self.cardTabelView reloadData];
    }];
}


-(NSMutableArray *)datasArray
{
    if (_datasArray == nil) {
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
}


- (void)createGetBalaceRequest{
    
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(@"容器加载失败:%@",error);
            return;
        }
        
        [self createDccBalanceRequest];
        
        [self createETHAndERC20Banlance];
        
    }];
    
}

- (void)createDccBalanceRequest
{
    if (self.datasArray.count == 0) {
        return;
    }
    
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[0];
    //abi方法
    NSString *abiJson = WEX_ERC20_ABI_BALANCE;
    //参数为需要查询的地址
    NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
    [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response)
     {
         //私链
         [[WXPassHelper instance] call2ContractAddress:[WexCommonFunc getDCCContractAddress] data:response type:WEX_DCC_NODE_SERVER responseBlock:^(id response)
          {
              NSLog(@"dcc私链balance=%@",response);
              NSDictionary *responseDict = response;
              NSString * originBalance =[responseDict objectForKey:@"result"];
              NSString * ethException =[responseDict objectForKey:@"ethException"];
              if ([ethException isEqualToString:@"ethException"]) {
                  _dccPrivateBalance = @"--";
              }
              else
              {
                  _dccPrivateBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[model.decimals integerValue]];
              }
              [self configDccDigitalAsset];
          }];
         //erc20
         NSLog(@"contractAddress=%@",model.contractAddress);
         NSLog(@"model=%@",model);
         [[WXPassHelper instance] call2ContractAddress:model.contractAddress data:response type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
             NSLog(@"dcc共连balance=%@",response);
             NSDictionary *responseDict = response;
             NSString * originBalance =[responseDict objectForKey:@"result"];
             NSString * ethException =[responseDict objectForKey:@"ethException"];
             if ([ethException isEqualToString:@"ethException"]) {
                 _dccPublicBalance = @"--";
             }
             else
             {
                 _dccPublicBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[model.decimals integerValue]];
                 
             }
             [self configDccDigitalAsset];
             
         }];
     }];
}

- (void)configDccDigitalAsset
{
    if (self.datasArray.count < 2) {
        return;
    }
    
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[0];
    if (_dccPublicBalance&&![_dccPublicBalance isEqualToString:@"--"]&&_dccPrivateBalance&&![_dccPrivateBalance isEqualToString:@"--"])
    {
        CGFloat dccBalance = [_dccPublicBalance floatValue]+[_dccPrivateBalance floatValue];
        model.balance = [NSString stringWithFormat:@"%.4f",dccBalance];
    }
    else
    {
        model.balance = @"--";
    }
    [self configTotalDigitalAsset];
    [_tokenTabelView reloadData];
}


- (void)createFtcBalanceRequest
{
    if (self.datasArray.count < 2) {
        return;
    }
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[2];
    //abi方法
    NSString *abiJson = WEX_ERC20_ABI_BALANCE;
    //参数为需要查询的地址
    NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
    [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response) {
        [[WXPassHelper instance] call2ContractAddress:[WexCommonFunc getFTCContractAddress] data:response type:YTF_DEVELOP_SERVER responseBlock:^(id response) {
            NSDictionary *responseDict = response;
            NSString * originBalance =[responseDict objectForKey:@"result"];
            NSString * ethException =[responseDict objectForKey:@"ethException"];
            if ([ethException isEqualToString:@"ethException"]) {
                model.balance = @"--";
            }
            else
            {
                model.balance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[model.decimals integerValue]];
                [self configTotalDigitalAsset];
            }
            [_tokenTabelView reloadData];
        }];
        
    }];
    
    
}

- (void)createETHAndERC20Banlance
{
    if (self.datasArray.count < 2) {
        return;
    }
    //eth
    [[WXPassHelper instance] getETHBalance2WithContractAddress:[WexCommonFunc getFromAddress] type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
        WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[1];
        if ([response isKindOfClass:[NSDictionary class]]) {
            model.balance = @"--";
        }
        else
        {
            model.balance = [WexCommonFunc formatterStringWithContractBalance:response decimals:[model.decimals integerValue]];
            [self configTotalDigitalAsset];
        }
        [_tokenTabelView reloadData];
    }];
    
    //erc20
    NSString *abiJson = WEX_ERC20_ABI_BALANCE;
    NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
    [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response) {
        _encodeData = response;
        for (int i = 2; i < self.datasArray.count; i++) {
            WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[i];
            [[WXPassHelper instance] call2ContractAddress:model.contractAddress data:_encodeData type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
                NSDictionary *responseDict = response;
                [self updateERC20BalaceWithResponse:responseDict];
            }];
        }
    }];
    
}


- (void)updateERC20BalaceWithResponse:(NSDictionary *)responseDict{
    
    for (WeXWalletDigitalGetTokenResponseModal_item *model in self.datasArray) {
        NSString * contractAddress =[responseDict objectForKey:@"to"];
        NSString * originBalance =[responseDict objectForKey:@"result"];
        NSString * ethException =[responseDict objectForKey:@"ethException"];
        if ([model.contractAddress isEqualToString:contractAddress]) {
            if ([ethException isEqualToString:@"ethException"]) {
                model.balance = @"--";
            }
            else
            {
                model.balance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[model.decimals integerValue]];
            }
            
            break;
        }
    }
    [self configTotalDigitalAsset];
    [_tokenTabelView reloadData];
}

- (void)configTotalDigitalAsset{
    CGFloat asset = 0.0;
    for (WeXWalletDigitalGetTokenResponseModal_item *model in self.datasArray) {
        if (model.balance&&![model.balance isEqualToString:@"--"]&&model.price) {
            asset += [model.balance floatValue]*[model.price floatValue];
            _assetlabel.text = [NSString stringWithFormat:@"≈¥%.4f",asset];
            _assetStr = _assetlabel.text;
        }
    }
    
}



//- (void)createGetQuoteRequest{
//    _getQuoteAdapter = [[WeXWalletDigitalGetQuoteAdapter alloc]  init];
//    _getQuoteAdapter.delegate = self;
//    WeXWalletDigitalGetQuoteParamModal *paramModal = [[WeXWalletDigitalGetQuoteParamModal alloc] init];
//    NSMutableString *varietyCodesStr = [NSMutableString string];
//    for (int i = 0; i < self.datasArray.count; i++) {
//        WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[i];
//        if (i == self.datasArray.count-1) {
//            [varietyCodesStr appendString:[NSString stringWithFormat:@"%@",model.symbol]];
//        }
//        else
//        {
//            [varietyCodesStr appendString:[NSString stringWithFormat:@"%@,",model.symbol]];
//        }
//
//    }
//    paramModal.varietyCodes = varietyCodesStr;
//    [_getQuoteAdapter run:paramModal];
//}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getNonceAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXBorrowGetNonceResponseModal *model = (WeXBorrowGetNonceResponseModal *)response;
            NSLog(@"model=%@",model);
            _nonce = model.result;
            if (_nonce) {
                if (_isRegister) {
                    [self createRegisterRequest];
                }
                else
                {
                    [self createGetMemberRequest];
                    
                }
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:[UIApplication sharedApplication].keyWindow];
        }
    }
    else if (adapter == _getMemberAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXGetMemberIdResponseModal *model = (WeXGetMemberIdResponseModal *)response;
            WEXNSLOG(@"%@",model);
            if (!model.memberId) {
                _inviteCodeView = [[WeXAddInviteCodeView alloc] initWithFrame:self.view.bounds];
                __weak typeof(self) weakSelf = self;
                _inviteCodeView.inviteConfirmBtnBlock = ^(NSString *code) {
                    _isRegister = YES;
                    if ([code isEqualToString:@""]) {
                        _inviteCode = nil;
                    }
                    else
                    {
                        _inviteCode = code;
                    }
                    
                    [weakSelf createGetNonceRequest];
                };
                [self.view addSubview:_inviteCodeView];
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
    }
    else if (adapter == _registerAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            
            WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
            model.hasMemberId = YES;
            [WexCommonFunc savePassport:model];
            
            [_inviteCodeView dismiss];
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
    else if (adapter == _queryBonusAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXActivityQueryBonusResponseModal *model = (WeXActivityQueryBonusResponseModal *)response;
            WEXNSLOG(@"%@",model);
            if (model.resultList.count > 0) {
                WeXActivityQueryBonusResponseModal_item *itemModel = [model.resultList objectAtIndex:0];
                _bonusId = itemModel.redeemTokenId;
                _redPacketView = [[WeXGetRedPacketView alloc] initWithFrame:self.view.bounds];
                _redPacketView.delegate = self;
                __weak typeof(self) weakSelf = self;
                _redPacketView.confirmBtnBlock = ^{
                    [weakSelf createApplyBonusRequest];
                };
                _redPacketView.jumpBtnBlock = ^{
                    WeXActivityHomeViewController *ctrl = [[WeXActivityHomeViewController alloc] init];
                    [weakSelf.navigationController pushViewController:ctrl animated:YES];
                };
                [self.view addSubview:_redPacketView];
            }
            // MARK: - 糖果领取
            else
            {
                WeXActivityHomeViewController *ctrl = [[WeXActivityHomeViewController alloc] init];
                [self.navigationController pushViewController:ctrl animated:YES];
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
    }
    else if (adapter == _applyBonusAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXActvityApplyBonusResponseModal *model = (WeXActvityApplyBonusResponseModal *)response;
            NSString *amount = [WexCommonFunc formatterStringWithContractBalance:model.amount decimals:18];
            _redPacketView.amountLabel.text = [NSString stringWithFormat:@"您获得了%@DCC",amount];
            [_redPacketView removeFrontRedPacketView];
        }
        else if ([headModel.systemCode isEqualToString:@"SUCCESS"])
        {
            [_redPacketView dismiss];
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:headModel.message onView:self.view];
        }
        else
        {
            [_redPacketView dismiss];
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if([adapter isKindOfClass:[WeXAgentMarketAdapter class]]){
        //        NSLog(@"response = %@",response);
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            if(response){
                WeXAgentMarketResponseModel *model= (WeXAgentMarketResponseModel *)response;
                
                for (WeXAgentMarketResponseModel_item *quoteModel in model.data) {
                    for (WeXWalletDigitalGetTokenResponseModal_item *tokenModel in self.datasArray) {
                        if ([quoteModel.symbol isEqualToString:tokenModel.symbol]) {
                            tokenModel.price = [NSString stringWithFormat:@"%f",[quoteModel.price floatValue]];
                            break;
                        }
                    }
                }
                //            [self.cardTabelView reloadData];
                [self configTotalDigitalAsset];
            }
        }
    }
    else if (adapter == _playAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            if(response){
                WeXOpenResResultModel *model= (WeXOpenResResultModel *)response;
                WEXNSLOG(@"model = %@",model);
                WEXNSLOG(@"model.result = %@",model.memo);
                if ([model.memo isEqualToString:@"1"]) {
                    _isAllowDisPlay = NO;
                    [WeXLocalCacheManager saveIsAppear:NO bundleID:[WexCommonFunc getVersion]];
                } else if ([model.memo isEqualToString:@"2"]){
                    _isAllowDisPlay = YES;
                    [WeXLocalCacheManager saveIsAppear:YES bundleID:[WexCommonFunc getVersion]];
                } else {
                    _isAllowDisPlay = [WeXLocalCacheManager getAppearWithBundleID:[WexCommonFunc getVersion]];
                }
            }
        } else {
            _isAllowDisPlay = [WeXLocalCacheManager getAppearWithBundleID:[WexCommonFunc getVersion]];
        }
        [_cardTabelView reloadData];
    } 
}


- (void)setupNavgationType{
    
    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"passport_set"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rihgtItemClick)];
    self.navigationItem.rightBarButtonItem = rihgtItem;
    
}

- (void)leftItemClick{
    WeXCardSettingViewController *ctrl = [[WeXCardSettingViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)rihgtItemClick{
    
    if (![self hasPassport])
    {
        [self createPassportChooseView];
        return;
    }
    
    WeXCardSettingViewController *ctrl = [[WeXCardSettingViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}



- (void)setupSubViews;
{
    _cardTabelView = [[UITableView alloc]  initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight) style:UITableViewStyleGrouped];
    _cardTabelView.backgroundColor = [UIColor clearColor];
    _cardTabelView.dataSource = self;
    _cardTabelView.delegate = self;
    _cardTabelView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _cardTabelView.sectionFooterHeight = 5;
    _cardTabelView.sectionHeaderHeight = 5;
    _cardTabelView.showsVerticalScrollIndicator = NO;
    [self.view addSubview:_cardTabelView];
    
    UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, _cardTabelView.frame.size.width, 25)];
    _cardTabelView.tableFooterView= footerView;
    
    UIImageView *imageView = [[UIImageView alloc] init];
    imageView.image =[UIImage imageNamed:@"borrow_dcc_support"];
    [footerView addSubview:imageView];
    [imageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(footerView);
        make.centerX.equalTo(footerView);
    }];
    _assetStr = @"--";
    [self registerTableViewCell];
}

// MARK: - 注册Cell

- (void)registerTableViewCell {
    [_cardTabelView registerClass:[WeXP2PImageCardCell class] forCellReuseIdentifier:kP2PCardCellIdentifier];
    [_cardTabelView registerClass:[WeXCoinProfitCell   class] forCellReuseIdentifier:kCoinProfitCellIdentifier];
}
-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if (tableView == _cardTabelView) {
        return 5;
    }
    return 1;
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (tableView == _cardTabelView) {
        if (section == 3) {
            return _isAllowDisPlay ? 1 : 0;
        }
        return 1;
    }
    
    return self.datasArray.count >4?4:self.datasArray.count;
    
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView == _cardTabelView) {
        if (indexPath.section == 0)
        {
            WeXPassportCardPassCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXPassportCardCell" owner:self options:nil] objectAtIndex:1];
            cell.backgroundColor = [UIColor clearColor];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            cell.nickLabel.text = [WexDefaultConfig instance].nickName;
            _nickNameLabel = cell.nickLabel;
            _nickNameLabel.font = [UIFont systemFontOfSize:15];
            NSString *address = [WexCommonFunc getFromAddress];
            if (address.length>8) {
                NSString *subAddress1 = [address substringWithRange:NSMakeRange(address.length-8, 4)];
                NSString *subAddress2 = [address substringWithRange:NSMakeRange(address.length-4, 4)];
                cell.addressLabel.text = [NSString stringWithFormat:@"0x  %@  %@",subAddress1,subAddress2];
            }
            
            UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(goToWebViewClick)];
            cell.addressLabel.userInteractionEnabled = YES;
            [cell.addressLabel addGestureRecognizer:tap];
            _addressLabel = cell.addressLabel;
            _addressLabel.font = [UIFont systemFontOfSize:15];
            
            UIImage *headImage = [IYFileManager cacheImageFileWithKey:WEX_FILE_USER_FACE];
            if (headImage == nil) {
                cell.headImageView.image = [UIImage imageNamed:@"digital_head"];
            }
            else{
                cell.headImageView.image = headImage;
            }
            cell.headImageView.layer.cornerRadius = 40;
            cell.headImageView.layer.masksToBounds = YES;
            _headImageView = cell.headImageView;
            return cell;
        }
        else if (indexPath.section == 1) {
            __weak typeof(self) weakSelf  = self;
            WeXCoinProfitCell *cell = [tableView dequeueReusableCellWithIdentifier:kCoinProfitCellIdentifier forIndexPath:indexPath];
            cell.DidClickCell = ^(WexCoinProfitCellType type) {
                __strong typeof(weakSelf) strongSelf = weakSelf;
                if (![strongSelf hasPassport]) {
                    [strongSelf createPassportChooseView];
                    return;
                }
                [strongSelf handleProfitCellCLickWithType:type];
            };
            [cell setIsAllowAppear:_isAllowDisPlay];
            return cell;
        }
        else if (indexPath.section == 2) {
            WeXPassportCardIDCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXPassportCardCell" owner:self options:nil] objectAtIndex:2];
            cell.backgroundColor = [UIColor clearColor];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            cell.cardView.backgroundColor = [UIColor whiteColor];
            cell.backImageView.image = [UIImage imageNamed:WeXLocalizedString(@"home_invite_friend_card")];
            return cell;
        }
        else if (indexPath.section == 3) {
            WeXP2PImageCardCell *cardCell = [tableView dequeueReusableCellWithIdentifier:kP2PCardCellIdentifier forIndexPath:indexPath];
            [cardCell setCardBackgroundImage:WeXLocalizedString(@"banner")];
            __weak typeof(self) weakSelf  = self;
            cardCell.DidClickP2PLoanCard = ^{
                if (![self hasPassport]) {
                    [self createPassportChooseView];
                    return;
                }
                [weakSelf p2pLoanCoinEvent];
            };
            return cardCell;
        }
        else if (indexPath.section == 4)
        {
            WeXPassportCardDigitalCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXPassportCardCell" owner:self options:nil] objectAtIndex:3];
            cell.backgroundColor = [UIColor clearColor];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            cell.cardView.backgroundColor = [UIColor whiteColor];
            cell.cardView.layer.cornerRadius = 5;
            _tokenTabelView = cell.tokenTabelView;
            _tokenTabelView.delegate = self;
            _tokenTabelView.dataSource = self;
            _tokenTabelView.scrollEnabled = NO;
            _tokenTabelView.separatorStyle = UITableViewCellSeparatorStyleNone;
            _tokenTabelView.backgroundColor = [UIColor clearColor];
            _tokenTabelView.userInteractionEnabled = NO;
            cell.backImageView.backgroundColor = [UIColor clearColor];
            cell.detailLabel.textColor = COLOR_THEME_ALL;
            cell.titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
            cell.totalAssetLabel.textColor =COLOR_LABEL_DESCRIPTION;
            _assetlabel = cell.totalAssetLabel;
            _assetlabel.text = _assetStr;
            _assetlabel.font = [UIFont systemFontOfSize:25];
            UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(changeNodeEvent:)];
            [cell.netbackView addGestureRecognizer:tapGesture];
            if (self.nodeDelayModel) {
                [cell setNodeNetworkDelayModel:self.nodeDelayModel];
            }
            [_tokenTabelView registerClass:[WeXWalletNewCell class] forCellReuseIdentifier:kNewWalletCellID];
            return cell;
        }
    }
//    WeXWalletDigitalListCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletDigitalListCell" owner:self options:nil] lastObject];
//    cell.backgroundColor = [UIColor clearColor];
//    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[indexPath.row];
    WeXWalletNewCell *cell = [tableView dequeueReusableCellWithIdentifier:kNewWalletCellID forIndexPath:indexPath];
    [cell setModel:model];
//    cell.model =  model;
    return cell;
}

// MARK: - 小信用借币Event
- (void)p2pLoanCoinEvent {
    NSString *userAddress = [WexCommonFunc getFromAddress];
    if (![userAddress containsString:@"0x"]) {
        userAddress = [@"0x" stringByAppendingString:userAddress];
    }
    NSAssert([userAddress isVaildString], @"钱包地址为空");
    //@"0xb3dccc63177baca756f4fe4c99fd24f28077e26b"
    UIWebViewController *coinWebVC = [UIWebViewController new];
    coinWebVC.urlStr = [[WeXLocalizedManager shareManager] isChinese] ? WexP2PLoadCoinURL : WexP2PLoadCoinURL_EN;
    coinWebVC.accountAddress = userAddress;
    [self.navigationController pushViewController:coinWebVC animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView == _cardTabelView) {
        if (indexPath.section == 0) {
            return (kScreenWidth-30) * kCardHeightWidthRatio;
        }
        else if (indexPath.section == 1) {
            return [WeXCoinProfitCell cellHeight];
        }
        else if (indexPath.section == 2) {
            return (kScreenWidth - 30) * kInviteFriendCardHeightWidthRatio;;
        }
        else if (indexPath.section == 3) {
            return _isAllowDisPlay ? [WeXP2PImageCardCell p2pImageCellHeight] : 0.01;
        }
        else if (indexPath.section == 4) {
            return 390;
        }
    }
    return 70;
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    if (tableView == _cardTabelView) {
        return 5;
    }
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (tableView == _cardTabelView) {
        return 5;
    }
    return 0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    return nil;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return nil;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (![self hasPassport])
    {
        [self createPassportChooseView];
        return;
    }
    
    if (tableView == _cardTabelView) {
        _currentIndexPath = indexPath;
        //顶部Card(带头像的)
        if (indexPath.section == 0) {
            WeXCardViewController *ctrl = [[WeXCardViewController alloc] init];
            self.navigationController.delegate = ctrl;
            [self.navigationController pushViewController:ctrl animated:YES];
        }
        // MARK: - 邀请好友
        else if (indexPath.section == 2)
        {
            WeXInviteFriendViewController *ctrl = [[WeXInviteFriendViewController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
        else if (indexPath.section == 4)
        {
            WeXWalletDigitalAssetListController *ctrl = [[WeXWalletDigitalAssetListController alloc] init];
            ctrl.datasArray = self.datasArray;
            [self.navigationController pushViewController:ctrl animated:YES];
        }
    }
}
// MARK: - 五个按钮点击事件
//  2018.8.13

- (void)handleProfitCellCLickWithType:(WexCoinProfitCellType)type {
    switch (type) {
        case WexCoinProfitCellTypeCandy: {
            [self activityButtonClick];
        }
            break;
        case WexCoinProfitCellTypeCrdit: {
            [self creditButtonClick];
        }
            break;
        case WexCoinProfitCellTypeLoan: {
            [self borrowButtonClick];
        }
            break;
        case WexCoinProfitCellTypeReport: {
            [self reportButtonClick];
        }
            break;
        case WexCoinProfitCellTypeProfit: {
            [self pushToCoinProfitDetailVC];
            WEXNSLOG(@"币生息正在研发中...");
        }
            break;
        default:
            break;
    }
}

// MARK: - 切换节点选择
- (void)changeNodeEvent:(UITapGestureRecognizer *)gesture {
    WeXSelectedNodeViewController *selectedNodeVC =  [WeXSelectedNodeViewController new];
    selectedNodeVC.DidSelectedNode = ^(WeXNetworkCheckModel *model){
        
    };
    [self.navigationController pushViewController:selectedNodeVC animated:YES];
    WEXNSLOG(@"网络节点选择");
}

- (void)tapAddressClick{
    WeXPassportLocationViewController *ctrl = [[WeXPassportLocationViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

// MARK: - 糖果领取
- (void)activityButtonClick
{
    [[WeXNetworkCheckManager shareManager] startAllNodeNetworkDelay:^(NSArray<WeXNetworkCheckModel *> *result) {
        WEXNSLOG(@"result:%@",result);
    }];
    
    if (![self hasPassport])
    {
        [self createPassportChooseView];
        return;
    }
    
    [self createQuerytBonusRequest];
    
}
// MARK: - 我的信用
- (void)creditButtonClick
{
    if (![self hasPassport])
    {
        [self createPassportChooseView];
        return;
    }
    WeXCreditHomeViewController *ctrl = [[WeXCreditHomeViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}
// MARK: - 信用借币
- (void)borrowButtonClick
{
    if (![self hasPassport])
    {
        [self createPassportChooseView];
        return;
    }
    WeXCreditBorrowUSDTViewController *ctrl = [[WeXCreditBorrowUSDTViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
    
}
// MARK: - 借贷报告
- (void)reportButtonClick
{
    if (![self hasPassport])
    {
        [self createPassportChooseView];
        return;
    }
    WeXLoanReportHomeViewController *ctrl = [[WeXLoanReportHomeViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

#pragma mark WeXCreatePassportChooseViewDelegate
// MARK: - 创建

-(void)clickCreatePassportButton
{
    WeXCreatePassportViewController *ctrl = [[WeXCreatePassportViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}
// MARK: - 导入
- (void)clickImportPassportButton
{
    WeXImportViewController *ctrl = [[WeXImportViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

#pragma mark WeXGetRedPacketViewDelegate
- (void)redPacketViewClickJumpButton
{
    
}

- (void)keyboardChangedWithTransition:(YYKeyboardTransition)transition {
    [UIView animateWithDuration:transition.animationDuration delay:0 options:transition.animationOption animations:^{
        if (transition.fromVisible) {
            _inviteCodeView.frame = self.view.bounds;
        }
        else
        {
            ///用此方法获取键盘的rect
            CGRect kbFrame = [[YYKeyboardManager defaultManager] convertRect:transition.toFrame toView:self.view];
            ///从新计算view的位置并赋值
            CGRect frame = _inviteCodeView.frame;
            frame.origin.y = kbFrame.origin.y - (frame.size.height*0.5+110+20);
            _inviteCodeView.frame = frame;
        }
    } completion:^(BOOL finished) {
        
    }];
}

-(void)dealloc
{
    [[YYKeyboardManager defaultManager] removeObserver:self];
}


//获取各个币种的价格请求
- (void)createGetAgentMarketRequest{
    WeXAgentMarketAdapter *getAgentAdapter = [[WeXAgentMarketAdapter alloc] init];
    //    _getAgentAdapter.currentyName = self.tokenModel.symbol;
    getAgentAdapter.delegate = self;
    WeXAgentMarketModel *paramModal = [[WeXAgentMarketModel alloc] init];
    NSMutableString *varietyCodesStr = [NSMutableString string];
    for (int i = 0; i < self.datasArray.count; i++) {
        WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[i];
        if (i == self.datasArray.count-1) {
             [varietyCodesStr appendString:[NSString stringWithFormat:@"%@",model.symbol]];
        }
        else
        {
              [varietyCodesStr appendString:[NSString stringWithFormat:@"%@,",model.symbol]];
        }
    
    }
   
        paramModal.coinTypes = varietyCodesStr;
        [getAgentAdapter run:paramModal];
}

//用户第一次安装新版本时的新功能提示(searchchain)数据分析功能
- (void)judgeIsDisplaySearchChainView{
    
    // 获取软件自身的版本号
    NSDictionary *infoDict = [NSBundle mainBundle].infoDictionary;
    NSString *currentVersion = infoDict[@"CFBundleShortVersionString"];
    NSLog(@"currentVersion = %@", currentVersion);

    // 获取沙盒中存储的软件版本号
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *sandboxVersion = [defaults objectForKey:@"wexVersion"];
    NSLog(@"sandboxVersion = %@", sandboxVersion);
    [defaults setObject:currentVersion forKey:@"wexVersion"];
    [defaults synchronize];

    if ([currentVersion compare:sandboxVersion] == NSOrderedDescending) {
        [self addSearchChainView];
    }else{
    }
}

- (void)addSearchChainView{
    
    UIView *blackView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight)];
    blackView .backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
    [self.view addSubview:blackView ];
    _backView = blackView;
    
    UIView *headerView = [[UIView alloc]initWithFrame:CGRectMake(15, 70, kScreenWidth-30, (kScreenWidth-30)*kCardHeightWidthRatio)];
    [_backView addSubview:headerView];
    UIImageView *oneBackImg = [[UIImageView alloc]init];
    oneBackImg.image = [UIImage imageNamed:@"digital_card"];
    [headerView addSubview:oneBackImg];
    [oneBackImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.bottom.equalTo(headerView);
    }];
    UIImageView *twoBackImg = [[UIImageView alloc]init];
    twoBackImg.image = [UIImage imageNamed:@"digital_head"];
    [headerView addSubview:twoBackImg];
    [twoBackImg mas_makeConstraints:^(MASConstraintMaker *make) {
      make.centerX.equalTo(headerView);
      make.centerY.equalTo(headerView);
      make.width.mas_equalTo(80);
      make.height.mas_equalTo(80);
    }];
    
    UILabel *addressLabel = [[UILabel alloc]init];
    addressLabel.textColor = [UIColor whiteColor];
    NSString *address = [WexCommonFunc getFromAddress];
    if (address.length>8) {
        NSString *subAddress1 = [address substringWithRange:NSMakeRange(address.length-8, 4)];
        NSString *subAddress2 = [address substringWithRange:NSMakeRange(address.length-4, 4)];
        addressLabel.text = [NSString stringWithFormat:@"0x  %@  %@",subAddress1,subAddress2];
        //设置文本属性
        NSMutableParagraphStyle *paraStyle = [[NSMutableParagraphStyle alloc]init];
        //设置行间距
        [paraStyle setLineSpacing:5];
        [paraStyle setLineBreakMode:NSLineBreakByWordWrapping];
        //字体大小
        UIFont *textFont = [UIFont systemFontOfSize:15];
        //字体颜色
        UIColor *textColor = [UIColor lightGrayColor];
        NSDictionary *textDic = @{NSFontAttributeName : textFont,NSForegroundColorAttributeName : textColor,NSParagraphStyleAttributeName : paraStyle};
        CGSize textSize = [addressLabel.text boundingRectWithSize:CGSizeMake([UIScreen mainScreen].bounds.size.width *0.5, MAXFLOAT) options:NSStringDrawingUsesLineFragmentOrigin attributes:textDic context:nil].size;
        //设置虚线边框
        CAShapeLayer *borderLayer = [CAShapeLayer layer];
        borderLayer.bounds = CGRectMake(0, 0, textSize.width + 10, textSize.height + 10);
        //中心点位置
        borderLayer.position = CGPointMake(textSize.width *0.5, textSize.height *0.5);
        
        borderLayer.path = [UIBezierPath bezierPathWithRect:CGRectMake(0, 0, textSize.width + 10, textSize.height + 10)].CGPath;
        //边框的宽度
        borderLayer.lineWidth = 2;
        //边框虚线线段的宽度
        borderLayer.lineDashPattern = @[@5,@5];
        borderLayer.fillColor = [UIColor clearColor].CGColor;
        borderLayer.strokeColor = [UIColor whiteColor].CGColor;
        [addressLabel.layer addSublayer:borderLayer];
        
    }
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(goToWebViewClick)];
    addressLabel.userInteractionEnabled = YES;
    [addressLabel addGestureRecognizer:tap];
    addressLabel.font = [UIFont systemFontOfSize:15];
    [headerView addSubview:addressLabel];
    [addressLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(headerView.mas_bottom).offset(-10);
        make.leading.equalTo(headerView).offset(25);
        make.height.mas_equalTo(20);
    }];
   
    
    
    
    UILabel *nameLabel = [[UILabel alloc]init];
    nameLabel.textColor = [UIColor whiteColor];
    nameLabel.text = [WexDefaultConfig instance].nickName;
    nameLabel.font = [UIFont systemFontOfSize:15];
    [headerView addSubview:nameLabel];
    [nameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(addressLabel.mas_top).offset(-5);
        make.leading.equalTo(addressLabel);
        make.height.mas_equalTo(20);
    }];
    
    UIImageView *fingerImg = [[UIImageView alloc]init];
    fingerImg.image = [UIImage imageNamed:@"finger"];
    [headerView addSubview:fingerImg];
    [fingerImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(30);
        make.width.mas_equalTo(24);
        make.top.equalTo(headerView.mas_bottom).offset(-2);
        make.leading.equalTo(_backView).offset(35);
    }];
    
    UILabel *defultLabel = [[UILabel alloc]init];
    defultLabel.text = @"新增了钱包地址的Searchain数据分析服务";
    defultLabel.font = [UIFont systemFontOfSize:15];
    defultLabel.textColor = [UIColor whiteColor];
    defultLabel.textAlignment = NSTextAlignmentLeft;
    [_backView addSubview:defultLabel];
    [defultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(headerView.mas_bottom).offset(10);
        make.leading.equalTo(fingerImg.mas_trailing).offset(10);
        make.trailing.mas_equalTo(-15);
        make.height.mas_offset(30);
    }];
    
    UIButton *deleteBtn = [[UIButton alloc]initWithFrame:CGRectMake(kScreenWidth/2-124, (kScreenWidth-30)*kCardHeightWidthRatio +120, 114, 40)];
    [deleteBtn setTitle:@"去看看" forState:UIControlStateNormal];
    deleteBtn.titleLabel.font = [UIFont systemFontOfSize:15.0];
    [deleteBtn setBackgroundColor:ColorWithHex(0x6766CC)];
    deleteBtn.titleLabel.textAlignment = NSTextAlignmentCenter;
    deleteBtn.layer.cornerRadius = 6;
    deleteBtn.clipsToBounds = YES;
    [deleteBtn addTarget:self action:@selector(goToWebViewClick) forControlEvents:UIControlEventTouchUpInside];
    [_backView addSubview:deleteBtn];
    
    UIButton *sureBtn = [[UIButton alloc]initWithFrame:CGRectMake(kScreenWidth/2+10, (kScreenWidth-30)*kCardHeightWidthRatio +120, 114, 40)];
    [sureBtn setTitle:@"知道了" forState:UIControlStateNormal];
    sureBtn.titleLabel.font = [UIFont systemFontOfSize:15.0];
    sureBtn.titleLabel.textAlignment = NSTextAlignmentCenter;
    [sureBtn setBackgroundColor:ColorWithHex(0x6766CC)];
    sureBtn.layer.cornerRadius = 6;
    sureBtn.clipsToBounds = YES;
    [sureBtn addTarget:self action:@selector(removeSearchChainView) forControlEvents:UIControlEventTouchUpInside];
    [_backView addSubview:sureBtn];
   
}

- (void)goToWebViewClick{
    [self removeSearchChainView];
    WeXAddressWebViewController *vc = [[WeXAddressWebViewController alloc]init];
    vc.addressStr = [WexCommonFunc getFromAddress];
    [self.navigationController pushViewController:vc animated:YES];
}

// MARK: - 币生息引导页
- (void)addCoinProfitGuideView {
//    [[WeXGuideViewManager shareManager] deleteAllLocalGuideViewType];
    if ([[WeXGuideViewManager shareManager] checkGuideViewWithType:WexGuideViewTypeCoinProfit] || !_isAllowDisPlay) {
        return;
    }
    [_cardTabelView setContentOffset:CGPointZero];
    CGFloat circleTopY = kNavgationBarHeight + (kScreenWidth-30) * kCardHeightWidthRatio + 20;
    CGRect circleFrame = CGRectMake(kScreenWidth - 72, circleTopY , 70, 70);
    __weak typeof(self) weakSelf = self;
    _profitGuideView = [WeXCoinProfitGuideView createCoinProfitGuideView:self.view.bounds circleFrame:circleFrame clickType:^(WeXCoinProfitGuideViewType type) {
        [[WeXGuideViewManager shareManager] setGuideViewValue:YES type:WexGuideViewTypeCoinProfit];
        if (type == WeXCoinProfitGuideViewTypeSee) { //看一看
            [weakSelf pushToCoinProfitDetailVC];
        }
        WEXNSLOG(@"profitGuideView---%@",weakSelf.profitGuideView);
    }];
    [self.view addSubview:_profitGuideView];
}
// MARK: - 币生息详情
- (void)pushToCoinProfitDetailVC {
    [_profitGuideView removeFromSuperview];
    _profitGuideView = nil;
    WeXCoinProfitDetailViewController *coinProfitVC = [WeXCoinProfitDetailViewController new];
    [self.navigationController pushViewController:coinProfitVC animated:YES];
}

- (void)removeSearchChainView{
    [_backView removeFromSuperview];
    _backView = nil;
}

@end
