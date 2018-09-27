//
//  WeXLoginManagerViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXLoginManagerViewController.h"
#import "WeXQuestionRemindView.h"
#import "WeXGraphView.h"
#import "WeXDeletePubKeyAdapter.h"
#import "WeXGetTicketAdapter.h"
#import "WeXGetContractAddressAdapter.h"
#import "WeXUploadPubKeyAdapter.h"
#import "WeXGetReceiptResultAdapter.h"
#import "WeXGetPubKeyAdapter.h"


#import "WeXPassportManagerRLMModel.h"

#import "WeXPassportManagerRecordCell.h"
#import "WeXNewPassportManagerRecordCell.h"
#import "WeXLoginManagerTopCell.h"
#import "WeXLoginManagerMoreDataCell.h"
#import "WeXLoginRecoredViewController.h"
#import "WeXHomePushService.h"

#define kAutoLayoutHeight1 65+kNavgationBarHeight
#define kAutoLayoutHeight2 105+kNavgationBarHeight


typedef NS_ENUM(NSInteger,WeXPassportManagerType) {
    WeXPassportManagerTypeNone,
    WeXPassportManagerTypeForbidden,
    WeXPassportManagerTypeAllow,
    WeXPassportManagerTypeUpdatePubKey
};


@interface WeXLoginManagerViewController ()<UITableViewDelegate,UITableViewDataSource,WeXPasswordManagerDelegate>
{
    UITableView *_tableView;
    WeXPasswordCacheModal *_model;
    
    UILabel *_loginStateLabel;
    UIButton *_forbiddenBtn;
    UIButton *_questionBtn;
    UILabel *_priviteKeyLabel;
    UIButton *_updateBtn;
    UIView *_backView;
    
    RSA *_publicKey;
    RSA *_privateKey;
    
    NSString *_rsaPublicKey;
    NSString *_rsaPrivateKey;
    NSString *_walletAddress;//钱包地址
    NSString *_walletPrivateKey;//钱包私钥
    NSDictionary *_keyStroe;//钱包
    
    NSString *_rawTransaction;
    
    NSString *_txHash;
    
    NSString *_contractAddress;//合约地址
    
    NSInteger _requestCount;//查询上链结果请求的次数

    WeXGraphView *_graphView;//验证码试图
    
     WeXPasswordManager *_manager;
}

@property (nonatomic,strong)WeXGetTicketAdapter *getTicketAdapter;
@property (nonatomic,strong)WeXGetContractAddressAdapter *getContractAddressAdapter;
@property (nonatomic,strong)WeXUploadPubKeyAdapter *uoloadPubKeyAdapter;
@property (nonatomic,strong)WeXGetReceiptResultAdapter *getReceiptAdapter;
@property (nonatomic,strong)WeXDeletePubKeyAdapter *deletePubKeyAdapter;
@property (nonatomic,strong)WeXGetPubKeyAdapter *getPubKeyAdapter;
@property (nonatomic,strong)WeXGetTicketResponseModal *getTicketModel;
@property (nonatomic,assign)WeXPassportManagerType managerType;
@property (nonatomic,strong)NSMutableArray  *datasArray;
//更新记录
@property (nonatomic, strong) NSMutableArray  *updateRecordArrays;
//变更记录
@property (nonatomic, strong) NSMutableArray  *changeRecordArrays;
@end

static NSString * const kCellID = @"WeXPassportManagerRecordCellID";
static NSString * const kTopCellID = @"WeXLoginManagerTopCellID";
static NSString * const kMoreDataCellID = @"WeXLoginManagerMoreDataCellID";

@implementation WeXLoginManagerViewController

static NSString * const kEnablePrefix = @"启用";
static NSString * const kDisablePrefix = @"禁用";

static NSInteger const kMaxCount = 3;

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"统一登录管理");
    [self setupSubViews];
    [self commonInit];
    [self configureNav];
    [self getAllManagerRecordType];
}

- (void)configureNav {
    [self setNavigationNomalBackButtonType];
    UIBarButtonItem *rightItem = [[UIBarButtonItem alloc] initWithTitle:@"变更记录" style:UIBarButtonItemStyleDone target:self action:@selector(changeRecordEvent:)];
    [rightItem setTitleTextAttributes:@{NSFontAttributeName:WexFont(16),NSForegroundColorAttributeName:ColorWithHex(0xC009FF)} forState:UIControlStateNormal];
    self.navigationItem.rightBarButtonItem = rightItem;
    
    _updateRecordArrays = [NSMutableArray new];
    _changeRecordArrays = [NSMutableArray new];
}


- (void)commonInit{
    _model = [WexCommonFunc getPassport];
}

- (void)changeRecordEvent:(UIBarButtonItem *)item {
    WeXLoginRecoredViewController *recoredVC = [WeXLoginRecoredViewController new];
    recoredVC.type = WeXLoginRecoredTypeChange;
    recoredVC.dataArray = self.changeRecordArrays;
    [WeXHomePushService pushFromVC:self toVC:recoredVC];
    WEXNSLOG(@"变更记录");
}


- (void)forbiddenPassport{
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
            return;
        }
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response) {
            NSLog(@"nitProvide=%@",response);
            // 删除key定义说明
            NSString* abiJson=@"{\"constant\":false,\"inputs\":[],\"name\":\"deleteKey\",\"outputs\":[{\"name\":\"_result\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}";
            // 合约地址(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境)
            NSString* abiAddress = _contractAddress;
            // 以太坊私钥地址
            NSString* privateKey=_model.walletPrivateKey;
            [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:nil privateKey:privateKey responseBlock:^(id response) {
                _rawTransaction = response;
                //删除本地pubkey
                [self createDeletePubKyeRequest];
                
            }];
        }];
    }];
}

- (void)updatePubKeyPassport{
    [self createRSA];
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
            return;
        }
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response) {
            NSLog(@"nitProvide=%@",response);
            // 合约定义说明
            NSString* abiJson=@"{'constant':false,'inputs':[{'name':'_publickey','type':'bytes'}],'name':'putKey','outputs':[{'name':'_result','type':'bool'}],'payable':false,'stateMutability':'nonpayable','type':'function'}";
            //            // 合约参数值
            NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\']",_rsaPublicKey]; //存放是自己的RSA公钥
            // 合约地址(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境)
            NSString* abiAddress = _contractAddress;
            
            NSString* privateKey=_model.walletPrivateKey;
            [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:privateKey responseBlock:^(id response) {
                NSLog(@"_rawTransaction=%@",response);
                _rawTransaction = response;
                //上传本地pubkey
                [self createUploadPubKeyRequest];
                
            }];
        }];
    }];
 
}

#pragma -mark 获取公钥请求
- (void)createGetPubKyeRequest{
    _getPubKeyAdapter = [[WeXGetPubKeyAdapter alloc] init];
    _getPubKeyAdapter.delegate = self;
    WeXGetPubKeyParamModal* paramModal = [[WeXGetPubKeyParamModal alloc] init];
    paramModal.address = [_model.keyStore objectForKey:@"address"];
    [_getPubKeyAdapter run:paramModal];
}

#pragma -mark 发送获取合约地址请求
- (void)createGetContractAddressRequest{
    _getContractAddressAdapter = [[WeXGetContractAddressAdapter alloc] init];
    _getContractAddressAdapter.delegate = self;
    WeXGetContractAddressParamModal* paramModal = [[WeXGetContractAddressParamModal alloc] init];
    [_getContractAddressAdapter run:paramModal];
}

#pragma -mark 删除公钥请求
- (void)createDeletePubKyeRequest{
    _deletePubKeyAdapter = [[WeXDeletePubKeyAdapter alloc] init];
    _deletePubKeyAdapter.delegate = self;
    WeXDeletePubKeyParamModal* paramModal = [[WeXDeletePubKeyParamModal alloc] init];
    paramModal.ticket = _getTicketModel.ticket;
    paramModal.signMessage = _rawTransaction;
    paramModal.code = _graphView.graphTextField.text;
    [_deletePubKeyAdapter run:paramModal];
}

#pragma -mark 发送请求
- (void)createGetTicketRequest{
    _getTicketAdapter = [[WeXGetTicketAdapter alloc] init];
    _getTicketAdapter.delegate = self;
    WeXGetTicketParamModal* paramModal = [[WeXGetTicketParamModal alloc] init];
    [_getTicketAdapter run:paramModal];
}

#pragma -mark 上传公钥请求
- (void)createUploadPubKeyRequest{
    _uoloadPubKeyAdapter = [[WeXUploadPubKeyAdapter alloc] init];
    _uoloadPubKeyAdapter.delegate = self;
    WeXUploadPubKeyParamModal* paramModal = [[WeXUploadPubKeyParamModal alloc] init];
    paramModal.ticket = _getTicketModel.ticket;
    paramModal.signMessage = _rawTransaction;
    paramModal.code = _graphView.graphTextField.text;
    [_uoloadPubKeyAdapter run:paramModal];
    
}

#pragma -mark 查询上链结果请求
- (void)createReceiptResultRequest{
    _getReceiptAdapter = [[WeXGetReceiptResultAdapter alloc] init];
    _getReceiptAdapter.delegate = self;
    WeXGetReceiptResultParamModal* paramModal = [[WeXGetReceiptResultParamModal alloc] init];
    paramModal.txHash = _txHash;
    [_getReceiptAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getTicketAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            _getTicketModel = (WeXGetTicketResponseModal *)response;
            [self createGetContractAddressRequest];
        } else {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _contractAddress = model.result;
            //合约地址请求成功 然后开始初始化passhelper
            //当前流程为启用
            if (self.managerType == WeXPassportManagerTypeAllow) {
                    [self forbiddenPassport];
            }
            //当前流程为禁用
            else if(self.managerType == WeXPassportManagerTypeForbidden||self.managerType == WeXPassportManagerTypeUpdatePubKey)
            {
                    [self updatePubKeyPassport];
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _uoloadPubKeyAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXUploadPubKeyResponseModal *model = (WeXUploadPubKeyResponseModal *)response;
            _txHash = model.result;
            [self createReceiptResultRequest];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统繁忙!") onView:self.view];
        }
    }
    else if (adapter == _deletePubKeyAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXDeletePubKeyResponseModal *model = (WeXDeletePubKeyResponseModal *)response;
            _txHash = model.result;
            [self createReceiptResultRequest];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getReceiptAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetReceiptResultResponseModal *model = (WeXGetReceiptResultResponseModal *)response;
            //上链成功
            if ([model.result isEqualToString:@"1"]) {
                //当前流程为允许时候点击，返回状态为禁用
                if (self.managerType == WeXPassportManagerTypeAllow)
                {
                    [WeXPorgressHUD hideLoading];
                    //更新秘钥
                    _model.rsaPublicKey = nil;
                    _model.rasPrivateKey = nil;
                    _model.isAllow = NO;
                    [WexCommonFunc savePassport:_model];
                    [self updateSubviews:NO];
                    NSLog(@"%@", WeXLocalizedString(@"禁用成功"));
                    
                    [self saveManagerRecordWithTypeString:WeXLocalizedString(@"禁用统一登录")];
                }
                else
                {
                    _walletAddress = [_keyStroe objectForKey:@"address"];
                    [self createGetPubKyeRequest];
                }
            }
            else
            {
                if (_requestCount > 4) {
                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:WeXLocalizedString(@"系统繁忙,请稍后再试!") onView:self.view];
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createReceiptResultRequest];
                    _requestCount++;
                });
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getPubKeyAdapter)
    {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXGetPubKeyResponseModal *model = (WeXGetPubKeyResponseModal *)response;
            NSData *publicKeyData =  [[NSData alloc] initWithBase64EncodedString:model.result options:0];
            NSString *resultPublickKey  = [WexCommonFunc hexStringWithData:publicKeyData];
            //相等表示钱包创建成功
            if ([resultPublickKey isEqualToString:_rsaPublicKey]) {
                
                if (self.managerType == WeXPassportManagerTypeUpdatePubKey) {
                    NSLog(@"%@", WeXLocalizedString(@"更新秘钥成功"));
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        [WeXPorgressHUD showText:WeXLocalizedString(@"更新秘钥成功") onView:self.view];
                    });
                    // 更新秘钥
                    _model.rsaPublicKey = _rsaPublicKey;
                    _model.rasPrivateKey = _rsaPrivateKey;
                    [WexCommonFunc savePassport:_model];
                    
                    NSString *md5 = [WexCommonFunc md5:_rsaPublicKey];
                    if (md5.length >= 6) {
                        _priviteKeyLabel.text = [NSString stringWithFormat:WeXLocalizedString(@"统一登录秘钥:***%@"),[md5 substringWithRange:NSMakeRange(md5.length-6, 6)]];
                    }
                    [self saveManagerRecordWithTypeString:WeXLocalizedString(@"更新统一登录秘钥")];
                }
                //当前流程为禁用时候点击，返回状态为启用
                else if (self.managerType == WeXPassportManagerTypeForbidden)
                {
                    //更新秘钥
                    _model.rsaPublicKey = _rsaPublicKey;
                    _model.rasPrivateKey = _rsaPrivateKey;
                    _model.isAllow = YES;
                    [WexCommonFunc savePassport:_model];
                    [self updateSubviews:YES];
                    NSLog(@"%@", WeXLocalizedString(@"启用成功"));
                    [self saveManagerRecordWithTypeString:WeXLocalizedString(@"启用统一登录")];
                }
            }
            else {
                [WeXPorgressHUD showText:WeXLocalizedString(@"系统繁忙,请稍后再试!") onView:self.view];
            }
        }
        else {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    [_tableView reloadData];
}

-(NSMutableArray *)datasArray {
    if (_datasArray == nil) {
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
}

- (void)getAllManagerRecordType{
    RLMResults<WeXPassportManagerRLMModel *> *results = [WeXPassportManagerRLMModel allObjects];
    [self.datasArray removeAllObjects];
    [self.changeRecordArrays removeAllObjects];
    [self.updateRecordArrays removeAllObjects];
    NSLog(@"count=%lu",(unsigned long)results.count);
    for (WeXPassportManagerRLMModel *model in results) {
        [self.datasArray addObject:model];
    }
    self.datasArray = (NSMutableArray *)[[self.datasArray reverseObjectEnumerator] allObjects];
    for (WeXPassportManagerRLMModel *model in _datasArray) {
        [self.changeRecordArrays addObject:model];
    }
    [_tableView reloadData];
}

- (void)saveManagerRecordWithTypeString:(NSString *)string{
    WeXPassportManagerRLMModel *model = [[WeXPassportManagerRLMModel alloc] init];
    model.type = string;
    
    model.date = [NSDate date];
    RLMRealm *realm = [RLMRealm defaultRealm];
    [realm beginWriteTransaction];
    [realm addObject:model];
    [realm commitWriteTransaction];
    
    [self getAllManagerRecordType];
}


#pragma mark - 创建RSA
- (void)createRSA{
    if ([DDRSAWrapper generateRSAKeyPairWithKeySize:2048 publicKey:&_publicKey privateKey:&_privateKey]) {
       NSString * publicKeyBase64 = [DDRSAWrapper base64EncodedStringPublicKey:_publicKey];
        publicKeyBase64 = [WexCommonFunc stringRemoveSpaceWithString:publicKeyBase64];
        NSData *publicKeyData =  [[NSData alloc] initWithBase64EncodedString:publicKeyBase64 options:0];
        _rsaPublicKey = [WexCommonFunc hexStringWithData:publicKeyData];
       
       NSString * privateKeyBase64 = [DDRSAWrapper base64EncodedStringPrivateKey:_privateKey];
        privateKeyBase64 = [WexCommonFunc stringRemoveSpaceWithString:privateKeyBase64];
        NSData *privateKeyData =  [[NSData alloc] initWithBase64EncodedString:privateKeyBase64 options:0];
        _rsaPrivateKey = [WexCommonFunc hexStringWithData:privateKeyData];
       
    }
}

-(void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    if (_model.isAllow) {
        [self updateSubviews:YES];
    }
    else {
        [self updateSubviews:NO];
    }
}

//初始化滚动视图
-(void)setupSubViews{
    
    WeXCustomButton *forbiddenBtn = [WeXCustomButton button];
    if (_model.isAllow) {
        [forbiddenBtn setTitle:WeXLocalizedString(@"禁用统一登录") forState:UIControlStateNormal];
    }
    else
    {
        [forbiddenBtn setTitle:WeXLocalizedString(@"启用统一登录") forState:UIControlStateNormal];
    }
    [forbiddenBtn addTarget:self action:@selector(forbiddenBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    forbiddenBtn.titleLabel.font = [UIFont systemFontOfSize:17];
    [self.view addSubview:forbiddenBtn];
    [forbiddenBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@50);
    }];
    _forbiddenBtn = forbiddenBtn;
    
    
    WeXCustomButton *updateBtn = [WeXCustomButton button];
    [updateBtn setTitle:WeXLocalizedString(@"更新秘钥") forState:UIControlStateNormal];
    [updateBtn addTarget:self action:@selector(updateBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    updateBtn.titleLabel.font = [UIFont systemFontOfSize:17];
    [self.view addSubview:updateBtn];
    [updateBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(forbiddenBtn);
        make.trailing.equalTo(self.view).offset(-20);
        make.width.equalTo(forbiddenBtn);
        make.leading.equalTo(forbiddenBtn.mas_trailing).offset(10);
        make.height.equalTo(forbiddenBtn);
    }];
    _updateBtn = updateBtn;
    
    UIView *backView = [[UIView alloc] init];
    backView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(forbiddenBtn.mas_top);
    }];
    
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = WexSepratorLineColor;
    [backView addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.mas_equalTo(kScreenWidth);
        make.height.mas_equalTo(10);
        make.top.mas_equalTo(0);
    }];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 300) style:UITableViewStyleGrouped];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 50;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;

    [backView addSubview:_tableView];
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line.mas_bottom);
        make.leading.trailing.equalTo(backView);
        make.bottom.equalTo(forbiddenBtn.mas_top);
    }];
    [_tableView registerClass:[WeXNewPassportManagerRecordCell class] forCellReuseIdentifier:kCellID];
    [_tableView registerClass:[WeXLoginManagerTopCell class] forCellReuseIdentifier:kTopCellID];
    [_tableView registerClass:[WeXLoginManagerMoreDataCell class] forCellReuseIdentifier:kMoreDataCellID];
    [_tableView layoutIfNeeded];
    NSLog(@"%@",NSStringFromCGRect(_tableView.frame));
   
}

- (void)questionBtnClick{
    WeXQuestionRemindView *questionView = [[WeXQuestionRemindView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:questionView];
}
#pragma mark - 禁用按钮点击
- (void)forbiddenBtnClick:(UIButton *)btn{
    
    _requestCount = 0;
    //当前流程为启用
    if (_model.isAllow) {
        self.managerType = WeXPassportManagerTypeAllow;
    }
    //当前流程为禁用
    else {
        self.managerType = WeXPassportManagerTypeForbidden;
    }
    
    [self configLocalSafetyView];
}
#pragma mark - 更新按钮点击
- (void)updateBtnClick:(UIButton *)btn{
    _requestCount = 0;
    //记录操作类型
    self.managerType = WeXPassportManagerTypeUpdatePubKey;
    [self configLocalSafetyView];
}

- (void)createGraphFloatView{
    _graphView = [[WeXGraphView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:_graphView];
    [self createGetTicketRequest];
    
    __weak typeof(self) weakSelf = self;
    _graphView.comfirmBtnBlock = ^{
        [WeXPorgressHUD showLoadingAddedTo:weakSelf.view];
        [weakSelf createGetContractAddressRequest];
    };
 
    _graphView.graphBtnBlock = ^{
        [weakSelf createGetTicketRequest];
    };
}

- (void)updateSubviews:(BOOL)isAllow{
    //启用
    if (isAllow) {
        [_forbiddenBtn setTitle:WeXLocalizedString(@"禁用统一登录") forState:UIControlStateNormal];
        _loginStateLabel.text = WeXLocalizedString(@"统一登录状态:已启用");

        _updateBtn.enabled = YES;
        _updateBtn.layer.borderWidth = 1;
        _priviteKeyLabel.hidden = NO;
        
        _model = [WexCommonFunc getPassport];
        NSString *md5 = [WexCommonFunc md5:_model.rsaPublicKey];
        if (md5.length >= 6) {
            _priviteKeyLabel.text = [NSString stringWithFormat:WeXLocalizedString(@"统一登录秘钥:***%@"),[md5 substringWithRange:NSMakeRange(md5.length-6, 6)]];
        }
        
        [_backView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.view).offset(kAutoLayoutHeight2);
        }];
    }
    else {
        [_forbiddenBtn setTitle:WeXLocalizedString(@"启用统一登录") forState:UIControlStateNormal];
        _loginStateLabel.text = WeXLocalizedString(@"统一登录状态:已禁用");
        _updateBtn.enabled = NO;
        _updateBtn.layer.borderWidth = 0;
        
        _priviteKeyLabel.hidden = YES;
        [_backView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.view).offset(kAutoLayoutHeight1);
        }];
    }
}


#pragma mark - tableViewDelegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 40;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    UIView *headerView = [UIView new];
    UILabel *titleLab =CreateLeftAlignmentLabel(WexFont(17), [UIColor blackColor]);
    titleLab.frame = CGRectMake(10, 0, kScreenWidth - 10 * 2, 40);
    [headerView addSubview:titleLab];
    if (section == 0) {
        [titleLab setText:@"统一登录"];
    } else {
        [titleLab setText:@"统一登录记录"];
    }
    return headerView;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {
        return 1;
    } else {
        if (_updateRecordArrays.count >= kMaxCount) {
            return kMaxCount + 1;
        }
        return _updateRecordArrays.count + 1;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        return [WeXLoginManagerTopCell cellHeight];
    } else {
        if (_updateRecordArrays.count >= kMaxCount) {
            return indexPath.row < kMaxCount ? 50 : 120;
        } else {
            return indexPath.row < _updateRecordArrays.count ? 50 : 120;
        }
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == 0) {
        WeXLoginManagerTopCell *cell = [tableView dequeueReusableCellWithIdentifier:kTopCellID forIndexPath:indexPath];
        WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
        [cell setStatus:model.isAllow];
        cell.DidClickScan = ^{
            WEXNSLOG(@"这是扫一扫");
        };
        return cell;
    }
    if (_updateRecordArrays.count > kMaxCount) {
        if (indexPath.row < kMaxCount) {
            WeXNewPassportManagerRecordCell *cell = [tableView dequeueReusableCellWithIdentifier:kCellID forIndexPath:indexPath];
            WeXPassportManagerRLMModel *model = _updateRecordArrays[indexPath.row];
            [cell setManagerModel:model];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        } else {
            WeXLoginManagerMoreDataCell *cell = [tableView dequeueReusableCellWithIdentifier:kMoreDataCellID forIndexPath:indexPath];
            [cell setTitle:@"加载更多~~"];
            return cell;
        }
    } else {
        if (indexPath.row < _updateRecordArrays.count) {
            WeXNewPassportManagerRecordCell *cell = [tableView dequeueReusableCellWithIdentifier:kCellID forIndexPath:indexPath];
            WeXPassportManagerRLMModel *model = _updateRecordArrays[indexPath.row];
            [cell setManagerModel:model];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
            return cell;
        } else {
            WeXLoginManagerMoreDataCell *cell = [tableView dequeueReusableCellWithIdentifier:kMoreDataCellID forIndexPath:indexPath];
            [cell setTitle:@"没有更多记录了"];
            return cell;
        }
    }
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (_updateRecordArrays.count > kMaxCount) {
        if (indexPath.row >= kMaxCount) {
            WeXLoginRecoredViewController *recoredVC = [WeXLoginRecoredViewController new];
            recoredVC.type = WeXLoginRecoredTypeLogin;
            recoredVC.dataArray = self.updateRecordArrays;
            [WeXHomePushService pushFromVC:self toVC:recoredVC];
        }
    }
}

- (void)configLocalSafetyView{
    _model = [WexCommonFunc getPassport];
    if (_model.passwordType == WeXPasswordTypeNone) {
        [WeXPorgressHUD showLoadingAddedTo:self.view];
        [self createGetTicketRequest];
    }
    else
    {
        WeXPasswordManager *manager = [WeXPasswordManager managerWithType:WeXPasswordManagerTypeVerify parentController:self];
        manager.delegate = self;
        [manager loadPassword];
        _manager = manager;
    }
    
}
#pragma mark - WeXPasswordManagerDelegate
- (void)passwordManagerVerifySuccess{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [WeXPorgressHUD showLoadingAddedTo:self.view];
        [self createGetTicketRequest];
    });
   
}





@end
