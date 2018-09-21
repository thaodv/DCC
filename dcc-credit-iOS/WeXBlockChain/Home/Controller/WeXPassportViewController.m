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
#import "WeXHomePushService.h"

//测试新版我的模块
#import "WeXNewMyController.h"
#import "WeXVersionUpdateAdapter.h"
#import "WeXHomeTopCardCell.h"
#import "WeXHomeSectionHeaderView.h"
#import "WeXHomeIndicatorCell.h"
#import "WeXHomeIconAndTextCell.h"
#import "WeXHomeLoanCoinCell.h"
#import "WeXHomeTableFooterView.h"
#import "WeXQueryProductByLenderCodeAdapter.h"
#import "WeXLoginManagerViewController.h"
#import "WeXTokenPlusViewController.h"
#import "WeXCPMarketViewController.h"
#import "WeXBorrowProductDetailController.h"

static NSString * const reuseWexPassIdentifier = @"reuseWexPassIdentifier";
static NSString * const kP2PCardCellIdentifier = @"WeXP2PImageCardCellIdentifier";
static NSString * const kCoinProfitCellIdentifier = @"WeXCoinProfitCellID";
static NSString * const kNewWalletCellID = @"WeXWalletNewCellID";
static NSString * const kTopCardCellID = @"WeXHomeTopCardCellID";
static NSString * const kSectionHeaderID = @"WeXHomeSectionHeaderViewID";
static NSString * const kIndicatorCellID = @"WeXHomeIndicatorCellID";
static NSString * const kIconAndTextCellID = @"WeXHomeIconAndTextCellID";
static NSString * const kLoanCoinCellID = @"WeXHomeLoanCoinCellID";

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
//信用借币
@property (nonatomic, strong) WeXQueryProductByLenderCodeAdapter *queryProductsAdapter;
@property (nonatomic, strong) NSMutableArray <WeXQueryProductByLenderCodeResponseModal_item *> *loanDataArray;


@end

@implementation WeXPassportViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"服务";
    self.automaticallyAdjustsScrollViewInsets = NO;
    [[YYKeyboardManager defaultManager] addObserver:self];
//    [self setupNavgationType];
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
// MARK: - 信用借币相关
- (void)createQueryProductsRequest{
    _queryProductsAdapter = [[WeXQueryProductByLenderCodeAdapter alloc] init];
    _queryProductsAdapter.delegate = self;
    WeXQueryProductByLenderCodeParamModal* paramModal = [[WeXQueryProductByLenderCodeParamModal alloc] init];
    [_queryProductsAdapter run:paramModal];
}

- (void)updateNickName {
    _nickNameLabel.text = [WexDefaultConfig instance].nickName;
}

- (void)updateHeadImage {
    UIImage *headImage = [IYFileManager cacheImageFileWithKey:WEX_FILE_USER_FACE];
    if (headImage == nil) {
        _headImageView.image = [UIImage imageNamed:@"digital_head"];
    }
    else {
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

- (void)updateWithCheckModel {
    if (WEX_DIGITAL_CHECK_MODEL) {
        _isOpen = YES;
    }
    else {
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
    else {
        _isOpen = YES;
    }
    NSLog(@"_isOpen=%d",_isOpen);
    _loanDataArray = [NSMutableArray new];
    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.delegate = nil;
    [self getDatas];
    [self getDefaultNodeDelay];
    [self createQueryProductsRequest];
    if ([self hasPassport]) {
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
- (BOOL)hasPassport {
    NSString *address = [WexCommonFunc getFromAddress];
    if (!address) {
        return NO;
    }
    return YES;
}

#pragma mark - 创建钱包入口页面
- (void)createPassportChooseView {
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


-(NSMutableArray *)datasArray {
    if (_datasArray == nil) {
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
}


- (void)createGetBalaceRequest{
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil) {
            NSError* error=response;
            NSLog(@"容器加载失败:%@",error);
            return;
        }
        [self createDccBalanceRequest];
        [self createETHAndERC20Banlance];
        
    }];
}

- (void)createDccBalanceRequest {
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
              else {
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
             else {
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
                else {
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
                    else {
                        _inviteCode = code;
                    }
                    [weakSelf createGetNonceRequest];
                };
                [self.view addSubview:_inviteCodeView];
            }
        }
        else if ([headModel.systemCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:headModel.message onView:self.view];
        }
        else {
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
                    [WeXHomePushService pushFromVC:weakSelf toVC:[WeXActivityHomeViewController new]];
                };
                [self.view addSubview:_redPacketView];
            }
            // MARK: - 糖果领取
            else {
                [WeXHomePushService pushFromVC:self toVC:[WeXActivityHomeViewController new]];
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
    else if (adapter == _queryProductsAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXQueryProductByLenderCodeResponseModal *model = (WeXQueryProductByLenderCodeResponseModal *)response;
            WEXNSLOG(@"%@",model);
            [self.loanDataArray removeAllObjects];
            [self.loanDataArray addObjectsFromArray:model.resultList];
            [_cardTabelView reloadData];
        }
        else {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
}
- (void)leftItemClick{
    [WeXHomePushService pushFromVC:self toVC:[WeXCardSettingViewController new]];
}

- (void)rihgtItemClick{
    if (![self hasPassport]) {
        [self createPassportChooseView];
        return;
    }
    [WeXHomePushService pushFromVC:self toVC:[WeXCardSettingViewController new]];
    //测试
    WeXNewMyController *vc = [[WeXNewMyController alloc]init];
    [self.navigationController pushViewController:vc animated:YES];
}


- (void)setupSubViews;
{
    _cardTabelView = [[UITableView alloc]  initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight - kTabBarHeight ) style:UITableViewStyleGrouped];
    _cardTabelView.backgroundColor = [UIColor clearColor];
    _cardTabelView.dataSource = self;
    _cardTabelView.delegate = self;
    _cardTabelView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _cardTabelView.sectionFooterHeight = 5;
    _cardTabelView.sectionHeaderHeight = 5;
    _cardTabelView.showsVerticalScrollIndicator = NO;
    [self.view addSubview:_cardTabelView];
    
    WeXHomeTableFooterView *footerView = [[WeXHomeTableFooterView alloc] initWithFrame:CGRectMake(0, 0, _cardTabelView.frame.size.width, 45)];
    [footerView setBackgroundColor:WexSepratorLineColor];
    [footerView setTitle:@"更多服务，敬请期待"];
    _cardTabelView.tableFooterView= footerView;
    _assetStr = @"--";
    [self registerTableViewCell];
}

// MARK: - 注册Cell

- (void)registerTableViewCell {
    [_cardTabelView registerClass:[WeXP2PImageCardCell class] forCellReuseIdentifier:kP2PCardCellIdentifier];
    [_cardTabelView registerClass:[WeXCoinProfitCell   class] forCellReuseIdentifier:kCoinProfitCellIdentifier];
    [_cardTabelView registerClass:[WeXHomeTopCardCell class] forCellReuseIdentifier:kTopCardCellID];
    [_cardTabelView registerClass:[WeXHomeSectionHeaderView class] forHeaderFooterViewReuseIdentifier:kSectionHeaderID];
    [_cardTabelView registerClass:[WeXHomeIndicatorCell class] forCellReuseIdentifier:kIndicatorCellID];
    [_cardTabelView registerClass:[WeXHomeIconAndTextCell class] forCellReuseIdentifier:kIconAndTextCellID];
    [_cardTabelView registerClass:[WeXHomeLoanCoinCell class] forCellReuseIdentifier:kLoanCoinCellID];
}
-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if (tableView == _cardTabelView) {
        return 5;
    }
    return 1;
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (tableView == _cardTabelView) {
        if (section == 0) {
            return 1;
        } else if (section == 1) {
            return _loanDataArray.count > 0 ? 2 + (_loanDataArray.count %2 == 0 ? _loanDataArray.count / 2 : _loanDataArray.count/2 + 1) : 0 ;
        } else if (section == 2) {
            return 2;
        } else if (section == 3) {
            return 2;
        } else if (section == 4) {
            return 2;
        }
//        if (section == 3) {
//            return _isAllowDisPlay ? 1 : 0;
//        }
        return 1;
    }
    
    return self.datasArray.count >4?4:self.datasArray.count;
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 10;
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    WeXHomeSectionHeaderView *headerView = [tableView dequeueReusableHeaderFooterViewWithIdentifier:kSectionHeaderID];
    [headerView.contentView setBackgroundColor:WexSepratorLineColor];
    return headerView;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (tableView == _cardTabelView) {
        if (indexPath.section == 0) {
            WeXHomeTopCardCell *cell = [tableView dequeueReusableCellWithIdentifier:kTopCardCellID forIndexPath:indexPath];
            NSString *address = [WexCommonFunc getFromAddress];
            NSString *shortAddress = nil;
            if (address.length>8) {
                NSString *subAddress1 = [address substringWithRange:NSMakeRange(address.length-8, 4)];
                NSString *subAddress2 = [address substringWithRange:NSMakeRange(address.length-4, 4)];
                shortAddress = [NSString stringWithFormat:@"0x  %@  %@",subAddress1,subAddress2];
            }
            [cell setTitleText:@"我的征信" addressText:shortAddress];
            return cell;
        }
        else if (indexPath.section == 1) {
            if (indexPath.row == 0) {
                WeXHomeIndicatorCell *indicatorCell = [tableView dequeueReusableCellWithIdentifier:kIndicatorCellID forIndexPath:indexPath];
                indicatorCell.DidClickRight = ^{
                    [self borrowButtonClick];
                };
                [indicatorCell setLeftTitle:@"信用借币" rightTitle:@"更多币种"];
                return indicatorCell;
            } else if (indexPath.row == 1) {
                WeXHomeIconAndTextCell *cell = [tableView dequeueReusableCellWithIdentifier:kIconAndTextCellID forIndexPath:indexPath];
                [cell setLeftIconName:@"xinyongjiebi" title:@"丰富币种等你来借" subTitle:@"做空、抄底给你再来一把的机会" actionTitle:@"去借币"];
                cell.DidClickEvent = ^{
                    [self borrowButtonClick];
                    WEXNSLOG(@"去借币");
                };
                return cell;
            } else {
                WeXHomeLoanCoinCell *cell = [tableView dequeueReusableCellWithIdentifier:kLoanCoinCellID forIndexPath:indexPath];
                if (_loanDataArray.count /2 == 0) {
                    NSInteger start = indexPath.row - 2;
                    NSInteger index = start * 2;
                    [cell setLeftModel:self.loanDataArray[index] rightModel:self.loanDataArray[index + 1 ]];
                    cell.DidClickCell = ^(WexLoanCoinCellClickType type) {
                        if (type == WexLoanCoinCellClickTypeLeft) {
                            [self pushToBorrowCoinControllerWithCoinModel:_loanDataArray[index]];
                        } else {
                            [self pushToBorrowCoinControllerWithCoinModel:_loanDataArray[index + 1]];
                        }
                    };
                } else {
                    NSInteger start = indexPath.row - 2;
                    NSInteger index = start * 2;
                    NSInteger indexPlus = index + 1 ;
                    [cell setLeftModel:self.loanDataArray[index] rightModel:indexPlus >= _loanDataArray.count ? nil : _loanDataArray[indexPlus]];
                    cell.DidClickCell = ^(WexLoanCoinCellClickType type) {
                        if (type == WexLoanCoinCellClickTypeLeft) {
                            [self pushToBorrowCoinControllerWithCoinModel:_loanDataArray[index]];
                        } else {
                            [self pushToBorrowCoinControllerWithCoinModel:_loanDataArray[index + 1]];
                        }
                    };
                }
                return cell;
            }
        }
        else if (indexPath.section == 2) {
            if (indexPath.row == 0) {
                WeXHomeIndicatorCell *cell = [tableView dequeueReusableCellWithIdentifier:kIndicatorCellID forIndexPath:indexPath];
                [cell setLeftTitle:@"币生息" rightTitle:nil];
                return cell;
            } else {
                WeXHomeIconAndTextCell *cell = [tableView dequeueReusableCellWithIdentifier:kIconAndTextCellID forIndexPath:indexPath];
                [cell setLeftIconName:@"bishengxi" title:@"多币种预计年化 10%-40%" highText:@"10%-40%" subTitle:@"极低风险跨市套利让你持币有红利" actionTitle:@"去认购"];
                cell.DidClickEvent = ^{
                    [self pushToCoinProfitDetailVC];
                    WEXNSLOG(@"去认购");
                };
                return cell;
            }
        }
        else if (indexPath.section == 3) {
            if (indexPath.row == 0) {
                WeXHomeIndicatorCell *cell = [tableView dequeueReusableCellWithIdentifier:kIndicatorCellID forIndexPath:indexPath];
                [cell setLeftTitle:@"资产套利" rightTitle:nil];
                return cell;
            } else {
                WeXHomeIconAndTextCell *cell = [tableView dequeueReusableCellWithIdentifier:kIconAndTextCellID forIndexPath:indexPath];
                [cell setLeftIconName:@"zichan" title:@"TokenPlus套利工具" subTitle:@"专为资产大户持有者提供服务的高回报神器！" actionTitle:nil];
                return cell;
            }
        }
        else if (indexPath.section == 4)
        {
            if (indexPath.row == 0) {
                WeXHomeIndicatorCell *cell = [tableView dequeueReusableCellWithIdentifier:kIndicatorCellID forIndexPath:indexPath];
                [cell setLeftTitle:@"统一登录管理" rightTitle:nil];
                return cell;
            } else {
                WeXHomeIconAndTextCell *cell = [tableView dequeueReusableCellWithIdentifier:kIconAndTextCellID forIndexPath:indexPath];
                [cell setLeftIconName:@"saoma" title:@"扫码登录" subTitle:@"使用个人数字认证信息实现联合登录！" actionTitle:nil];
                return cell;
            }
        }
    }
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[indexPath.row];
    WeXWalletNewCell *cell = [tableView dequeueReusableCellWithIdentifier:kNewWalletCellID forIndexPath:indexPath];
    [cell setModel:model];
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
    [WeXHomePushService pushFromVC:self toVC:coinWebVC];
}

// MARK: - 信用借币
- (void)pushToBorrowCoinControllerWithCoinModel:(WeXQueryProductByLenderCodeResponseModal_item *)model {
    WeXBorrowProductDetailController *borrowCoinVC = [WeXBorrowProductDetailController new];
    borrowCoinVC.productModel = model;
    [WeXHomePushService pushFromVC:self toVC:borrowCoinVC];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView == _cardTabelView) {
        if (indexPath.section == 0) {
            // (kScreenWidth-30) * kCardHeightWidthRatio;
            return [WeXHomeTopCardCell cellHeight];
        }
        else if (indexPath.section == 1) {
            if (indexPath.row == 0) {
                return [WeXHomeIndicatorCell cellHeight];
            } else if (indexPath.row == 1) {
                return [WeXHomeIconAndTextCell cellHeight];
            } else {
                return [WeXHomeLoanCoinCell cellHeight];
            }
//            return [WeXCoinProfitCell cellHeight];
        }
        else if (indexPath.section == 2) {
            return indexPath.row == 0 ? [WeXHomeIndicatorCell cellHeight] : [WeXHomeIconAndTextCell cellHeight];
//            if (indexPath.row == 0) {
//                 return [WeXHomeIndicatorCell cellHeight];
//            }
//            return [WeXHomeIconAndTextCell cellHeight];
//            return (kScreenWidth - 30) * kInviteFriendCardHeightWidthRatio;;
        }
        else if (indexPath.section == 3) {
            return indexPath.row == 0 ? [WeXHomeIndicatorCell cellHeight] : [WeXHomeIconAndTextCell cellHeight];
//            return _isAllowDisPlay ? [WeXP2PImageCardCell p2pImageCellHeight] : 0.01;
//            return [WeXP2PImageCardCell p2pImageCellHeight];
        }
        else if (indexPath.section == 4) {
            return indexPath.row == 0 ? [WeXHomeIndicatorCell cellHeight] : [WeXHomeIconAndTextCell cellHeight];
//            return 390;
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

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return nil;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (![self hasPassport]) {
        [self createPassportChooseView];
        return;
    }
    
    if (tableView == _cardTabelView) {
        _currentIndexPath = indexPath;
        //信用认证
        if (indexPath.section == 0) {
            [self creditButtonClick];
        }
        // MARK: - 邀请好友
        else if (indexPath.section == 2) {
            [self pushToCoinProfitDetailVC];
        }
        // MARK: - 资产套利
        else if (indexPath.section == 3) {
            [WeXHomePushService pushFromVC:self toVC:[WeXTokenPlusViewController new]];
        }
        //统一登录管理
        else if (indexPath.section == 4) {
            [WeXHomePushService pushFromVC:self toVC:[WeXLoginManagerViewController new]];
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
    [WeXHomePushService pushFromVC:self toVC:selectedNodeVC];
    WEXNSLOG(@"网络节点选择");
}

- (void)tapAddressClick{
    [WeXHomePushService pushFromVC:self toVC:[WeXPassportLocationViewController new]];
}

// MARK: - 糖果领取
- (void)activityButtonClick {
    [[WeXNetworkCheckManager shareManager] startAllNodeNetworkDelay:^(NSArray<WeXNetworkCheckModel *> *result) {
        WEXNSLOG(@"result:%@",result);
    }];
    if (![self hasPassport]) {
        [self createPassportChooseView];
        return;
    }
    
    [self createQuerytBonusRequest];
    
}
// MARK: - 我的信用
- (void)creditButtonClick {
    if (![self hasPassport]) {
        [self createPassportChooseView];
        return;
    }
    [WeXHomePushService pushFromVC:self toVC:[WeXCreditHomeViewController new]];

}
// MARK: - 信用借币
- (void)borrowButtonClick {
    if (![self hasPassport]) {
        [self createPassportChooseView];
        return;
    }
    [WeXHomePushService pushFromVC:self toVC:[WeXCreditBorrowUSDTViewController new]];
}
// MARK: - 借贷报告
- (void)reportButtonClick {
    if (![self hasPassport]) {
        [self createPassportChooseView];
        return;
    }
    [WeXHomePushService pushFromVC:self toVC:[WeXLoanReportHomeViewController new]];
}

#pragma mark WeXCreatePassportChooseViewDelegate
// MARK: - 创建
-(void)clickCreatePassportButton {
    [WeXHomePushService pushFromVC:self toVC:[WeXCreatePassportViewController new]];
}
// MARK: - 导入
- (void)clickImportPassportButton {
    [WeXHomePushService pushFromVC:self toVC:[WeXImportViewController new]];
}

#pragma mark WeXGetRedPacketViewDelegate
- (void)keyboardChangedWithTransition:(YYKeyboardTransition)transition {
    [UIView animateWithDuration:transition.animationDuration delay:0 options:transition.animationOption animations:^{
        if (transition.fromVisible) {
            _inviteCodeView.frame = self.view.bounds;
        }
        else {
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

-(void)dealloc {
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
        } else {
              [varietyCodesStr appendString:[NSString stringWithFormat:@"%@,",model.symbol]];
        }
    }
        paramModal.coinTypes = varietyCodesStr;
        [getAgentAdapter run:paramModal];
}
- (void)goToWebViewClick{
    [self removeSearchChainView];
    WeXAddressWebViewController *vc = [[WeXAddressWebViewController alloc]init];
    vc.addressStr = [WexCommonFunc getFromAddress];
    [WeXHomePushService pushFromVC:self toVC:vc];
}

// MARK: - 币生息详情
- (void)pushToCoinProfitDetailVC {
    if (![self hasPassport]) {
        [self createPassportChooseView];
        return;
    }
    [WeXHomePushService pushFromVC:self toVC:[WeXCPMarketViewController new]];
}

- (void)removeSearchChainView{
    [_backView removeFromSuperview];
    _backView = nil;
}

@end
