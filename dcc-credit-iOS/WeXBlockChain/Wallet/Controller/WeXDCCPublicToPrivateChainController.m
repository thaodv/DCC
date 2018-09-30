//
//  WeXDCCPublicToPrivateChainController.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDCCPublicToPrivateChainController.h"
#import "WeXDCCPublicToPrivateChainCell.h"
#import "UITableView+FDTemplateLayoutCell.h"
#import "WeXDCCPublicToPrivateConfirmView.h"
#import "WeXTokenDccListViewController.h"
#import "WeXWalletTransferResultManager.h"
#import "WeXWalletGetDccAcrossChainInfoAdapter.h"
#import "WeXBaseTableView.h"

static NSString *const kFirstReuseIdentifier = @"kFirstReuseIdentifier";
static NSString *const kSecondReuseIdentifier = @"kSecondReuseIdentifier";
static NSString *const kThirdReuseIdentifier = @"kThirdReuseIdentifier";
static NSString *const kFourthReuseIdentifier = @"kFourthReuseIdentifier";
static NSString *const kFifthReuseIdentifier = @"kFifthReuseIdentifier";

@interface WeXDCCPublicToPrivateChainController ()<UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate,WeXPasswordManagerDelegate,WeXBaseTableViewDelegate>
{
    WeXBaseTableView *_tableView;
    
    UILabel *_privateBalanceLabel;
    UILabel *_publicBalanceLabel1;
    UILabel *_publicBalanceLabel2;
    
    NSString *_privateBalance;
    NSString *_publicBalance;
    
    NSString *_minValueStr;
    
    NSString *_toAddressStr;
    
    UITextField *_valueTextField;
    UITextField *_gasPriceTextField;
    UITextField *_gasLimitTextField;
    
    UILabel *_costLabel;
    CGFloat _cost;//最高矿工费
    
    NSNumber *_nonce;
    NSString *_gasPrice;
    
    NSString *_ethBalance;
    
    WeXPasswordManager *_manager;
    
    WeXDCCPublicToPrivateConfirmView *_confirmView;
}
@property (nonatomic,strong)WeXWalletGetDccAcrossChainInfoAdapter *getInfoAdapter;
@property (nonatomic,strong)NSMutableArray *datasArray;
@end



@implementation WeXDCCPublicToPrivateChainController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"跨链交易";
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    [self createGetBalaceRequest];
    [self createQueryInfoRequest];
    [self getGasPrice];
}
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [self addNotification];
}
- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [self removeNotification];
}

- (void)getGasPrice{
    
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(@"容器加载失败:%@",error);
            return;
        }
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
            [[WXPassHelper instance] getGasPriceResponseBlock:^(id response) {
                NSLog(@"gasprice:=%@",response);
                NSDecimalNumber *gasPrice = [WexCommonFunc stringWithOriginString:response dividString:NINE_ZERO];
                _gasPriceTextField.text = [NSString stringWithFormat:@"%.0f",[gasPrice floatValue] < 1.0?1.0:[gasPrice floatValue]];
            }];
            
        }];
    }];
}

- (void)getGasLimitRequest
{
  
    [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
        NSString *abiJson1 = WEX_ERC20_ABI_TRANSFER;
        NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
        NSString *pararms1 = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressStr,value];
        
        [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson1 params:pararms1 responseBlock:^(id response){
            [[WXPassHelper instance] getGasLimitWithToAddress:self.tokenModel.contractAddress fromAddress:[WexCommonFunc getFromAddress] data:response responseBlock:^(id response) {
                NSLog(@"response=%@",response);
                if ([response isKindOfClass:[NSDictionary class]]) {
                    
                }
                else
                {
                    if (_gasLimitTextField.text.length ==0 ) {
                        _gasLimitTextField.text = [NSString stringWithFormat:@"%@",response];
                        [self configCostWithGasLimit:_gasLimitTextField.text];
                    }
                }
            }];
            
        }];
    }];
    
    
}


- (void)createQueryInfoRequest{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    _getInfoAdapter = [[WeXWalletGetDccAcrossChainInfoAdapter alloc] init];
    _getInfoAdapter.delegate = self;
    WeXWalletGetDccAcrossChainInfoParamModal *paramModel = [[WeXWalletGetDccAcrossChainInfoParamModal alloc] init];
    paramModel.assetCode = @"DCC";
    [_getInfoAdapter run:paramModel];
    
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getInfoAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXWalletGetDccAcrossChainInfoResponseModal *model = (WeXWalletGetDccAcrossChainInfoResponseModal *)response;
            NSString *minAmount = [WexCommonFunc formatterStringWithContractBalance:model.minAmount decimals:18];
            _minValueStr = minAmount;
            _toAddressStr = model.originReceiverAddress;
            _valueTextField.placeholder = [NSString stringWithFormat:@"最小交易数量%@",_minValueStr];
        }
        else if ([headModel.systemCode isEqualToString:@"SUCCESS"])
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:headModel.message onView:self.view];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }
    }
    
    
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
              NSLog(@"dcc私链balance=%@",response);
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
          }];
         //dccerc20
         [[WXPassHelper instance] call2ContractAddress:self.tokenModel.contractAddress data:response type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
             NSLog(@"dcc共连balance=%@",response);
             NSDictionary *responseDict = response;
             NSString * originBalance =[responseDict objectForKey:@"result"];
             NSString * ethException =[responseDict objectForKey:@"ethException"];
             if ([ethException isEqualToString:@"ethException"]) {
                 _publicBalance = @"--";
                 _publicBalanceLabel1.text = _publicBalance;
                 _publicBalanceLabel2.text = [NSString stringWithFormat:@"公链数量:%@DCC",_publicBalance];
             }
             else
             {
                 _publicBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                 _publicBalanceLabel1.text = _publicBalance;
                 _publicBalanceLabel2.text = [NSString stringWithFormat:@"公链数量:%@DCC",_publicBalance];
             }
         }];
     }];
}


//初始化滚动视图
-(void)setupSubViews{
    
    _tableView = [[WeXBaseTableView alloc] init];
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.WexDelegate = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.bottom.trailing.equalTo(self.view);
    }];
    
    [_tableView registerClass:[WeXDCCPublicToPrivateChainFirstCell  class] forCellReuseIdentifier:kFirstReuseIdentifier];
    [_tableView registerClass:[WeXDCCPublicToPrivateChainSecondCell class] forCellReuseIdentifier:kSecondReuseIdentifier];
    [_tableView registerClass:[WeXDCCPublicToPrivateChainThirdCell  class] forCellReuseIdentifier:kThirdReuseIdentifier];
    [_tableView registerClass:[WeXDCCPublicToPrivateChainFourthCell class] forCellReuseIdentifier:kFourthReuseIdentifier];
    [_tableView registerClass:[WeXDCCPublicToPrivateChainFifthCell  class] forCellReuseIdentifier:kFifthReuseIdentifier];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick)];
    [_tableView addGestureRecognizer:tap];
    
}

- (void)tapClick{
    [self.view endEditing:YES];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 6;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        WeXDCCPublicToPrivateChainFirstCell *cell =[tableView dequeueReusableCellWithIdentifier:kFirstReuseIdentifier forIndexPath:indexPath];
        _privateBalanceLabel = cell.privateBalanceLabel;
        _publicBalanceLabel1 = cell.publicBalanceLabel;
        return cell;
    }
    else if (indexPath.row == 1) {
        WeXDCCPublicToPrivateChainSecondCell *cell =[tableView dequeueReusableCellWithIdentifier:kSecondReuseIdentifier forIndexPath:indexPath];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.allButtonBlock = ^{
            if ([_publicBalance integerValue] > 0) {
                _valueTextField.text = _publicBalance;
            }
        };
        _publicBalanceLabel2 = cell.publicBalanceLabel;
        _valueTextField = cell.valueTextField;
        _valueTextField.delegate = self;
        return cell;
    }
    else if (indexPath.row == 2) {
        WeXDCCPublicToPrivateChainThirdCell *cell =[tableView dequeueReusableCellWithIdentifier:kThirdReuseIdentifier forIndexPath:indexPath];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.titleLabel.text  = @"Gas Price";
        cell.symbolLabel.text = @"Gwei";
        _gasPriceTextField = cell.valueTextField;
        _gasPriceTextField.delegate = self;
        return cell;
    }
    else if (indexPath.row == 3) {
        WeXDCCPublicToPrivateChainThirdCell *cell =[tableView dequeueReusableCellWithIdentifier:kThirdReuseIdentifier forIndexPath:indexPath];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.titleLabel.text = @"Gas Limit";
        _gasLimitTextField = cell.valueTextField;
        _gasLimitTextField.placeholder = @"自定义Gas Limit";
        _gasLimitTextField.delegate = self;
        return cell;
    }
    else if (indexPath.row == 4) {
        WeXDCCPublicToPrivateChainFourthCell *cell =[[WeXDCCPublicToPrivateChainFourthCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        _costLabel = cell.feeLabel;
        return cell;
    }
    else if (indexPath.row == 5) {
        WeXDCCPublicToPrivateChainFifthCell *cell =[[WeXDCCPublicToPrivateChainFifthCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.nextButtonBlock = ^{
            [self nextBtnClick];
        };
        return cell;
    }
    
    return nil;
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{

    if (indexPath.row == 0) {
        return [WeXDCCPublicToPrivateChainFirstCell height];
    }
    else if (indexPath.row == 1) {
       return [WeXDCCPublicToPrivateChainSecondCell height];;
    }
    else if (indexPath.row == 2) {
        return [WeXDCCPublicToPrivateChainThirdCell height];
    }
    else if (indexPath.row == 3) {
       return [WeXDCCPublicToPrivateChainThirdCell height];
    }
    else if (indexPath.row == 4) {
       return [WeXDCCPublicToPrivateChainFourthCell height];
    }
    else if (indexPath.row == 5) {
      return [WeXDCCPublicToPrivateChainFifthCell height];
    }
    return 0;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WEXNSLOG(@"%s",__func__);
}

- (void)nextBtnClick
{
    [self.view endEditing:YES];
    
    if ([_publicBalance floatValue] == 0||[_publicBalance isEqualToString:@"--"]) {
        [WeXPorgressHUD showText:@"账户余额不足!" onView:self.view];
        return;
    }
    
    
    if (_valueTextField.text.length == 0||[_valueTextField.text isEqualToString:@""]) {
        [WeXPorgressHUD showText:@"转账金额不能为空!" onView:self.view];
        return;
    }    
    if (_gasPriceTextField.text.length == 0||[_gasPriceTextField.text isEqualToString:@""]) {
        [WeXPorgressHUD showText:@"GasPrice不能为空!" onView:self.view];
        return;
    }
    if (_gasLimitTextField.text.length == 0||[_gasLimitTextField.text isEqualToString:@""]) {
        [WeXPorgressHUD showText:@"GasLimit不能为空!" onView:self.view];
        return;
    }
    
    if ([_valueTextField.text floatValue] < [_minValueStr floatValue]) {
        NSString *message = [NSString stringWithFormat:@"最低交易%.4fDCC",[_minValueStr floatValue]];
        [WeXPorgressHUD showText:message onView:self.view];
        return;
    }
    
    [self createConfirmView];
}

- (void)createConfirmView
{
    WeXDCCPublicToPrivateConfirmView *confirmView = [[WeXDCCPublicToPrivateConfirmView alloc] initWithFrame:self.view.bounds];
    confirmView.valueLabel.text = [NSString stringWithFormat:@"%@DCC",_valueTextField.text];
    confirmView.coastLabel.text = [NSString stringWithFormat:@"%.8fDCC",_cost];
    confirmView.balanceLabel.text = [NSString stringWithFormat:@"%@DCC",_publicBalance];
    confirmView.confirmBtnBlock = ^{
        if ([_valueTextField.text floatValue] > [_publicBalance floatValue]|| _cost > [_ethBalance floatValue]) {
            [WeXPorgressHUD showText:@"持有量不足,请核对后重新提交!" onView:self.view];
            return;
        }
     
        [self configLocalSafetyView];
        
    };
    [self.view addSubview:confirmView];
    _confirmView = confirmView;
    [self createGetETHBalaceRequest];
}

- (void)createGetETHBalaceRequest{
  
    [[WXPassHelper instance] getETHBalance2WithContractAddress:[WexCommonFunc getFromAddress] type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
        NSString *balace = [WexCommonFunc formatterStringWithContractBalance:response decimals:18];
        _confirmView.ethBalanceLabel.text = [NSString stringWithFormat:@"%@ETH",balace];
        _ethBalance = balace;
    }];
    
}

- (void)createTransferRequest{
    
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
     {
         [[WXPassHelper instance] getETHNonceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
             WEXNSLOG(@"%@",response);
             _nonce = response;
             NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
             NSString *gasprice = [[WexCommonFunc stringWithOriginString:_gasPriceTextField.text multiplyString:NINE_ZERO] stringValue];
             _gasPrice = gasprice;
             NSString *abiJson = WEX_ERC20_ABI_TRANSFER;
             NSString *pararms = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressStr,value];
             [[WXPassHelper instance] sendERC20TransactionWithContractToAddress:self.tokenModel.contractAddress abiJson:abiJson privateKey:model.walletPrivateKey params:pararms gasPrice:gasprice gasLimit:_gasLimitTextField.text nonce:_nonce responseBlock:^(id response) {
                 NSLog(@"response=%@",response);
                 [WeXPorgressHUD hideLoading];
                 //转账失败
                 if([response isKindOfClass:[NSDictionary class]])
                 {
                     [WeXPorgressHUD hideLoading];
                     NSString *message = [response objectForKey:@"message"];
                     [WeXPorgressHUD showText:message onView:self.view];
                     return;
                 };
                 if (response) {
                     NSString *txHash = [NSString stringWithFormat:@"%@",response];
                     [self savePendingTransferModelWithTxhash:txHash];
                 }
                 [WeXPorgressHUD showText:@"转账申请已提交，请耐心等待转账结果。" onView:self.view];
                 dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                     for (UIViewController *ctrl in self.navigationController.viewControllers) {
                         if ([ctrl isKindOfClass:[WeXTokenDccListViewController class]]) {
                             [self.navigationController popToViewController:ctrl animated:YES];
                         }
                     }
                 });
                 
             }];
             
         }];
     }];
    
}

- (void)savePendingTransferModelWithTxhash:(NSString *)txhash
{
    WeXWalletTransferPendingModel *model = [[WeXWalletTransferPendingModel alloc] init];
    model.from = [WexCommonFunc getFromAddress];
    model.to = _toAddressStr;
    NSDate *nowTime = [NSDate date];
    NSTimeInterval timeStamp = nowTime.timeIntervalSince1970;
    model.timeStamp = [NSString stringWithFormat:@"%f",timeStamp];
    model.value =_valueTextField.text?[[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue]:nil;
    model.txhash = txhash;
    model.nonce = [_nonce stringValue];
    model.gasPrice = _gasPriceTextField.text?[[WexCommonFunc stringWithOriginString:_gasPriceTextField.text multiplyString:NINE_ZERO] stringValue]:nil;
    model.gasLimit = _gasLimitTextField.text;
    WeXWalletTransferResultManager *manager = [[WeXWalletTransferResultManager alloc] init];
    [manager savePendingModel:model symbol:self.tokenModel.symbol];
}


- (void)configCostWithGasLimit:(NSString *)gasLimitString {
    _cost = [gasLimitString floatValue]*[_gasPriceTextField.text floatValue]*powf(10, -9);
    _costLabel.text = [NSString stringWithFormat:@"%.8f",[gasLimitString floatValue]*[_gasPriceTextField.text floatValue]*powf(10, -9)];
}

-(void)textFieldDidBeginEditing:(UITextField *)textField
{
    if (textField == _gasLimitTextField) {
        if (_toAddressStr.length>0 &&_valueTextField.text.length>0) {
            [self getGasLimitRequest];
        }
    }
}

-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *comment;
    if(range.length == 0)
    {
        comment = [NSString stringWithFormat:@"%@%@",textField.text, string];
    }
    else
    {
        comment = [textField.text substringToIndex:textField.text.length -range.length];
    }
    
    return YES;
}

#pragma mark -密码
- (void)configLocalSafetyView{
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    if (model.passwordType == WeXPasswordTypeNone) {
        [self createTransferRequest];
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
        [self createTransferRequest];
    });
    
}
// MARK: - WexBaseTableViewDelegate
- (void)wex_tableviewTouchBegan:(WeXBaseTableView *)tableView {
    if (tableView == _tableView) {
        [self.view endEditing:true];
    }
}

- (void)addNotification {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}

- (void)removeNotification {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (void)keyboardWillHide:(NSNotification *)noti {
    if (_valueTextField.isFirstResponder) {
        if ([_valueTextField.text floatValue] >[_publicBalance floatValue]) {
            WEXNSLOG(@"当前输入的数量大于钱包余额了");
        }
    }
    WEXNSLOG(@"---acd%@--%@",noti.object,@(_valueTextField.isFirstResponder));
}
- (void)dealloc {
    WEXNSLOG(@"%@,%s",self,__func__);
}


@end
