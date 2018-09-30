//
//  WeXDCCPrivateToPublicChainController.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDCCPrivateToPublicChainController.h"
#import "WeXDCCPrivateToPublicChainCell.h"
#import "WeXDCCPrivateToPublicConfirmView.h"
#import "WeXWalletTransferResultManager.h"
#import "WeXTokenDccListViewController.h"
#import "WeXWalletGetDccAcrossChainInfoAdapter.h"
#import "WeXBaseTableView.h"


@interface WeXDCCPrivateToPublicChainController ()<UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate,WeXPasswordManagerDelegate,WeXBaseTableViewDelegate>
{
    WeXBaseTableView *_tableView;
    
    UILabel *_privateBalanceLabel1;
    UILabel *_privateBalanceLabel2;
    UILabel *_publicBalanceLabel;
    
    NSString *_privateBalance;
    NSString *_publicBalance;
    
    UILabel *_feeLabel;
    UILabel *_receiveValueLabel;
    
    NSString *_minValueStr;
    NSString *_toAddressStr;
    NSString *_feeStr;
    
    UITextField *_valueTextField;
    
    WeXPasswordManager *_manager;
}
@property (nonatomic,strong)WeXWalletGetDccAcrossChainInfoAdapter *getInfoAdapter;
@property (nonatomic,strong)NSMutableArray *datasArray;

@end

@implementation WeXDCCPrivateToPublicChainController

static NSString *const kWeXDCCPrivateToPublicChainSecondCellID = @"WeXDCCPrivateToPublicChainSecondCellID";
static NSString *const kWeXDCCPrivateToPublicChainFirstCellID = @"WeXDCCPrivateToPublicChainFirstCellID";


- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"跨链交易";
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    [self createGetBalaceRequest];
    [self createQueryInfoRequest];
}


- (void)createQueryInfoRequest{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    _getInfoAdapter = [[WeXWalletGetDccAcrossChainInfoAdapter alloc] init];
    _getInfoAdapter.delegate = self;
    WeXWalletGetDccAcrossChainInfoParamModal *paramModel = [[WeXWalletGetDccAcrossChainInfoParamModal alloc] init];
    paramModel.assetCode = @"DCC_JUZIX";
    [_getInfoAdapter run:paramModel];
    
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
   if (adapter == _getInfoAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXWalletGetDccAcrossChainInfoResponseModal *model = (WeXWalletGetDccAcrossChainInfoResponseModal *)response;
            NSString *fixedFeeAmount = [WexCommonFunc formatterStringWithContractBalance:model.fixedFeeAmount decimals:18];
            _feeStr = fixedFeeAmount;
            NSString *minAmount = [WexCommonFunc formatterStringWithContractBalance:model.minAmount decimals:18];
            _minValueStr = minAmount;
            _toAddressStr = model.originReceiverAddress;
            _feeLabel.text = _feeStr;
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
                  _privateBalanceLabel1.text = _privateBalance;
                  _privateBalanceLabel2.text = [NSString stringWithFormat:@"私链数量: %@DCC",_privateBalance];
              }
              else
              {
                  _privateBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                  _privateBalanceLabel1.text = _privateBalance;
                  _privateBalanceLabel2.text = [NSString stringWithFormat:@"私链数量: %@DCC",_privateBalance];
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
                 _publicBalanceLabel.text = _publicBalance;
                 
             }
             else
             {
                 _publicBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[self.tokenModel.decimals integerValue]];
                 _publicBalanceLabel.text = _publicBalance;
             }
         }];
     }];
}



//初始化滚动视图
-(void)setupSubViews{
    
    _tableView = [[WeXBaseTableView alloc] init];
    [self.view addSubview:_tableView];
    _tableView.WexDelegate = self;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.bottom.trailing.equalTo(self.view);
    }];
    [_tableView registerClass:[WeXDCCPrivateToPublicChainSecondCell class] forCellReuseIdentifier:kWeXDCCPrivateToPublicChainSecondCellID];
    [_tableView registerClass:[WeXDCCPrivateToPublicChainFirstCell class] forCellReuseIdentifier:kWeXDCCPrivateToPublicChainFirstCellID];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick)];
    [_tableView addGestureRecognizer:tap];
    
}

- (void)tapClick{
    [self.view endEditing:YES];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        WeXDCCPrivateToPublicChainFirstCell *cell = [tableView dequeueReusableCellWithIdentifier:kWeXDCCPrivateToPublicChainFirstCellID forIndexPath:indexPath];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        _privateBalanceLabel1 = cell.privateBalanceLabel;
        _publicBalanceLabel = cell.publicBalanceLabel;
        return cell;
    }
   else if (indexPath.row == 1) {
        WeXDCCPrivateToPublicChainSecondCell *cell =[tableView dequeueReusableCellWithIdentifier:kWeXDCCPrivateToPublicChainSecondCellID forIndexPath:indexPath];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.allButtonBlock = ^{
           if ([_privateBalance integerValue] > 0) {
               _valueTextField.text = _privateBalance;
               [self configReceiveValue:_valueTextField.text];
           }
           
       };
        _privateBalanceLabel2 = cell.privateBalanceLabel;
       _valueTextField = cell.valueTextField;
       _valueTextField.delegate = self;
        return cell;
    }
   else if (indexPath.row == 2) {
       WeXDCCPrivateToPublicChainThirdCell *cell =[[WeXDCCPrivateToPublicChainThirdCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
       cell.selectionStyle = UITableViewCellSelectionStyleNone;
       cell.titleLabel.text = @"手续费";
       _feeLabel = cell.contentLabel;
       return cell;
   }
   else if (indexPath.row == 3) {
       WeXDCCPrivateToPublicChainThirdCell *cell =[[WeXDCCPrivateToPublicChainThirdCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
       cell.selectionStyle = UITableViewCellSelectionStyleNone;
       cell.titleLabel.text = @"到账数量";
       _receiveValueLabel = cell.contentLabel;
       return cell;
   }
    else if (indexPath.row == 4) {
        WeXDCCPrivateToPublicChainFourthCell *cell =[[WeXDCCPrivateToPublicChainFourthCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.nextButtonBlock = ^{
            [self nextBtnClick];
        };
        return cell;
    }
   
    return nil;
    
}

- (void)nextBtnClick
{
    [self.view endEditing:YES];
    
    if ([_privateBalance floatValue] == 0||[_privateBalance isEqualToString:@"--"]) {
        [WeXPorgressHUD showText:@"账户余额不足!" onView:self.view];
        return;
    }
    
    if (_valueTextField.text.length == 0||[_valueTextField.text isEqualToString:@""]) {
        [WeXPorgressHUD showText:@"转账金额不能为空!" onView:self.view];
        return;
    }
    if ([_valueTextField.text floatValue] < [_minValueStr floatValue]) {
        NSString *message = [NSString stringWithFormat:@"最低交易%.4fDCC",[_minValueStr floatValue]];
        [WeXPorgressHUD showText:message onView:self.view];
        return;
    }
    
    [self createConfirmView];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        return [WeXDCCPrivateToPublicChainFirstCell height];
    }
    else if (indexPath.row == 1) {
         return [WeXDCCPrivateToPublicChainSecondCell height];
    }
    else if (indexPath.row == 2) {
        return [WeXDCCPrivateToPublicChainThirdCell height];
    }
    else if (indexPath.row == 3) {
        return [WeXDCCPrivateToPublicChainThirdCell height];
    }
    else if (indexPath.row == 4) {
        return [WeXDCCPrivateToPublicChainFourthCell height];
    }
    return 0;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WEXNSLOG(@"%s",__func__);
}

- (void)createConfirmView
{
    WeXDCCPrivateToPublicConfirmView *confirmView = [[WeXDCCPrivateToPublicConfirmView alloc] initWithFrame:self.view.bounds];
    confirmView.valueLabel.text = [NSString stringWithFormat:@"%@DCC",_valueTextField.text];
    confirmView.feeLabel.text = [NSString stringWithFormat:@"%@DCC",_feeStr];
    confirmView.receiveLabel.text = [NSString stringWithFormat:@"%.4fDCC",[_valueTextField.text floatValue]-[_feeStr floatValue]];
    confirmView.balanceLabel.text = [NSString stringWithFormat:@"%@DCC",_privateBalance];
    confirmView.confirmBtnBlock = ^{
        if ([_valueTextField.text floatValue]  > [_privateBalance floatValue]) {
            [WeXPorgressHUD showText:@"持有量不足,请核对后重新提交!" onView:self.view];
            return;
        }
        
        [self configLocalSafetyView];
        
    };
    [self.view addSubview:confirmView];
}

- (void)createTransferRequest{
    
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    [[WXPassHelper instance] initProvider:WEX_DCC_NODE_SERVER responseBlock:^(id response)
     {
         //转账金额
         NSString *value = [[WexCommonFunc stringWithOriginString:_valueTextField.text multiplyString:EIGHTEEN_ZERO] stringValue];
         //abi方法
         NSString *abiJson = WEX_ERC20_ABI_TRANSFER;
         //参数为接收方地址和金额
         NSString *pararms = [NSString stringWithFormat:@"[\'%@\',\'%@\']",_toAddressStr,value];
         //私钥
         NSString *privateKey = model.walletPrivateKey;
         [[WXPassHelper instance] sendPrivateTransactionWithContractToAddress:[WexCommonFunc getDCCContractAddress] abiJson:abiJson privateKey:privateKey params:pararms responseBlock:^(id response) {
             NSLog(@"txhash=%@",response);
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
    WeXWalletTransferResultManager *manager = [[WeXWalletTransferResultManager alloc] init];
    [manager savePendingModel:model symbol:[NSString stringWithFormat:@"%@_private",self.tokenModel.symbol]];
}


- (void)configReceiveValue:(NSString *)valueString
{
    _receiveValueLabel.text = [NSString stringWithFormat:@"%.4f",[valueString floatValue]-[_feeStr floatValue]>0?[valueString floatValue]-[_feeStr floatValue]:0.0];
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

    [self configReceiveValue:comment];
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

// MARK: - WeXBaseTableViewDelegate
- (void)wex_tableviewTouchBegan:(WeXBaseTableView *)tableView {
    if (tableView == _tableView) {
        [self.view endEditing:YES];
    }
}






@end
