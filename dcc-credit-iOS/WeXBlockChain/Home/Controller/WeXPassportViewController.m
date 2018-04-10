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


static NSString * const reuseWexPassIdentifier = @"reuseWexPassIdentifier";

@interface WeXPassportViewController ()<UITableViewDataSource,UITableViewDelegate,WeXCreatePassportChooseViewDelegate>
{
    NSString *_encodeData;
    UILabel *_assetlabel;//总的数字资产
    
    WeXPasswordCacheModal *_model;
    BOOL _isOpen;//数字资产开关是否打开
    
    UILabel *_nickNameLabel;
    UIImageView *_headImageView;
}

@property (nonatomic,strong)WeXWalletDigitalGetQuoteAdapter *getQuoteAdapter;

@property (nonatomic,strong)NSMutableArray *datasArray;

@end

@implementation WeXPassportViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.view.backgroundColor = ColorWithRGB(240, 240, 240);
    [self setupNavgationType];
    [self commonInit];
    [self setupSubViews];
    
    if (self.isShowDescription) {
        [self showPasswordSetDescription];
    }
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateWithCheckModel) name:WEX_CHECK_MODEL_NOTIFY object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateNickName) name:WEX_CHANGE_NICK_NAME_NOTIFY object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateHeadImage) name:WEX_CHANGE_HEAD_IMAGE_NOTIFY object:nil];

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

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    self.navigationController.delegate = nil;
    
    [self getDatas];
    [self createGetQuoteRequest];
    [self createGetBalaceRequest];
}

- (void)showPasswordSetDescription{
    if (_model) {
        if (_model.passwordType!= WeXPasswordTypeNone) {
            [WeXPorgressHUD showText:@"密码设置成功!" onView:self.view];
        }
    }
}


- (void)getDatas{
    
    [self.datasArray removeAllObjects];
    
    RLMResults <WeXDigitalAssetRLMModel *>*results = [WeXDigitalAssetRLMModel allObjects];
    for (WeXDigitalAssetRLMModel *rlmModel in results) {
        WeXWalletDigitalGetTokenResponseModal_item *model = [[WeXWalletDigitalGetTokenResponseModal_item alloc] init];
        model.symbol =rlmModel.symbol;
        model.name = rlmModel.name;
        model.iconUrl = rlmModel.iconUrl;
        model.decimals = rlmModel.decimals;
        model.contractAddress = rlmModel.contractAddress;
        [self.datasArray addObject:model];
    }
    
    
    WeXWalletDigitalGetTokenResponseModal_item *model1 = [[WeXWalletDigitalGetTokenResponseModal_item alloc] init];
    model1.symbol = @"ETH";
    model1.name = @"Ethereum Foundation";
    model1.iconUrl = @"http://www.wexpass.cn/images/Contractz_icon/ethereum@2x.png";
    model1.decimals = @"18";
    model1.contractAddress = @"";
    if (self.datasArray.count >= 1) {
        [self.datasArray insertObject:model1 atIndex:1];
    }
    
    [_tokenTabelView reloadData];
}

-(NSMutableArray *)datasArray
{
    if (_datasArray == nil) {
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
}


- (void)createGetBalaceRequest{
    
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
             WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[0];
             //abi方法
             NSString *abiJson = WEX_ERC20_ABI_BALANCE;
             //参数为需要查询的地址
             NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
             [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response) {
                 [[WXPassHelper instance] call2ContractAddress:[WexCommonFunc getFTCContractAddress] data:response type:YTF_DEVELOP_SERVER responseBlock:^(id response) {
                     NSLog(@"balance=%@",response);
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
            
         }];
         [self createETHAndERC20Banlance];
        
    }];
    
    
    
}

- (void)createETHAndERC20Banlance
{
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
//             [[WXPassHelper instance] getETHBalanceWithContractAddress:[WexCommonFunc getFromAddress] responseBlock:^(id response) {
//                 WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[1];
//                 if ([response isKindOfClass:[NSDictionary class]]) {
//                     model.balance = @"--";
//                 }
//                 else
//                 {
//                     model.balance = [WexCommonFunc formatterStringWithContractBalance:response decimals:[model.decimals integerValue]];
//                     [self configTotalDigitalAsset];
//                 }
//                 [_tokenTabelView reloadData];
//             }];
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
         }];


        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
             NSString *abiJson = WEX_ERC20_ABI_BALANCE;
             NSString *pararms = [NSString stringWithFormat:@"[\'%@\']",[WexCommonFunc getFromAddress]];
             [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:pararms responseBlock:^(id response) {
                 _encodeData = response;
                 for (int i = 2; i < self.datasArray.count; i++) {
                     WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[i];
                     [[WXPassHelper instance] call2ContractAddress:model.contractAddress data:_encodeData type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
                         NSLog(@"balance=%@",response);
                         NSDictionary *responseDict = response;
                         [self updateERC20BalaceWithResponse:responseDict];
                     }];
                 }
             }];
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
        }
    }
    
}



- (void)createGetQuoteRequest{
    _getQuoteAdapter = [[WeXWalletDigitalGetQuoteAdapter alloc]  init];
    _getQuoteAdapter.delegate = self;
    WeXWalletDigitalGetQuoteParamModal *paramModal = [[WeXWalletDigitalGetQuoteParamModal alloc] init];
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
    paramModal.varietyCodes = varietyCodesStr;
    [_getQuoteAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if(adapter == _getQuoteAdapter){
        WeXWalletDigitalGetQuoteResponseModal *model = (WeXWalletDigitalGetQuoteResponseModal *)response;
        NSLog(@"model=%@",model);
        for (WeXWalletDigitalGetQuoteResponseModal_item *quoteModel in model.data) {
            for (WeXWalletDigitalGetTokenResponseModal_item *tokenModel in self.datasArray) {
                if ([quoteModel.varietyCode isEqualToString:tokenModel.symbol]) {
                    tokenModel.price = [NSString stringWithFormat:@"%.2f",quoteModel.price];
                    break;
                }
            }
        }
        [self configTotalDigitalAsset];
        
    }
}


- (void)setupNavgationType{
    
    UIBarButtonItem *leftItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"passport_set"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(leftItemClick)];
    self.navigationItem.leftBarButtonItem = leftItem;
    
    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"scan"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rihgtItemClick)];
    self.navigationItem.rightBarButtonItem = rihgtItem;
    
}

- (void)leftItemClick{
    WeXCardSettingViewController *ctrl = [[WeXCardSettingViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)rihgtItemClick{
    WeXScanViewController *ctrl = [[WeXScanViewController alloc] init];
    ctrl.handleType = WeXScannerHandleTypeLogin;
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
    [self.view addSubview:_cardTabelView];
}

-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if (tableView == _cardTabelView) {
        if (_isOpen) {
            return 4;
        }
        else
        {
            return 2;
        }
        
    }
        return 1;
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (tableView == _cardTabelView) {
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
            NSString *address = [WexCommonFunc getFromAddress];
            if (address.length>8) {
                NSString *subAddress1 = [address substringWithRange:NSMakeRange(address.length-8, 4)];
                NSString *subAddress2 = [address substringWithRange:NSMakeRange(address.length-4, 4)];
                cell.addressLabel.text = [NSString stringWithFormat:@"0x  %@  %@",subAddress1,subAddress2];
            }
            
            UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapAddressClick)];
            cell.addressLabel.userInteractionEnabled = YES;
            [cell.addressLabel addGestureRecognizer:tap];
            
            UIImage *headImage = [IYFileManager cacheImageFileWithKey:WEX_FILE_USER_FACE];
            if (headImage == nil) {
                cell.headImageView.image = [UIImage imageNamed:@"digital_head"];
            }
            else
            {
                cell.headImageView.image = headImage;
            }
            
            cell.headImageView.layer.cornerRadius = 40;
            cell.headImageView.layer.masksToBounds = YES;
            _headImageView = cell.headImageView;
            return cell;
        }
        if (_isOpen) {
             if (indexPath.section == 1)
             {
                WeXPassportCardDigitalCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXPassportCardCell" owner:self options:nil] objectAtIndex:3];
                cell.backgroundColor = [UIColor clearColor];
                cell.selectionStyle = UITableViewCellSelectionStyleNone;
                cell.cardView.backgroundColor = [UIColor whiteColor];
                cell.cardView.layer.cornerRadius = 5;
                cell.cardView.layer.masksToBounds = YES;
                _tokenTabelView = cell.tokenTabelView;
                _tokenTabelView.delegate = self;
                _tokenTabelView.dataSource = self;
                _tokenTabelView.scrollEnabled = NO;
                _tokenTabelView.separatorStyle = UITableViewCellSeparatorStyleNone;
                _tokenTabelView.backgroundColor = [UIColor clearColor];
                _tokenTabelView.userInteractionEnabled = NO;
                _assetlabel = cell.totolAssetLabel;
                cell.backImageView.layer.cornerRadius = 5;
                cell.backImageView.layer.masksToBounds = YES;
                return cell;
                
            }
            else if (indexPath.section == 2)
            {
                
                WeXPassportCardIDCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXPassportCardCell" owner:self options:nil] objectAtIndex:2];
                cell.backgroundColor = [UIColor clearColor];
                cell.selectionStyle = UITableViewCellSelectionStyleNone;
                cell.backImageView.image = [UIImage imageNamed:@"digital_all1"];
                return cell;
            }
            else if (indexPath.section == 3)
            {
                WeXPassportCardIDCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXPassportCardCell" owner:self options:nil] objectAtIndex:2];
                cell.backgroundColor = [UIColor clearColor];
                cell.selectionStyle = UITableViewCellSelectionStyleNone;
                cell.backImageView.image = [UIImage imageNamed:@"digital_all2"];
                return cell;
            }
        }
        else
        {
            if (indexPath.section == 1)
            {
                WeXPassportCardIDCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXPassportCardCell" owner:self options:nil] objectAtIndex:2];
                cell.backgroundColor = [UIColor clearColor];
                cell.selectionStyle = UITableViewCellSelectionStyleNone;
                cell.backImageView.image = [UIImage imageNamed:@"digital_all1"];
                return cell;
            }
            
        }
        
    }
    
    WeXWalletDigitalListCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletDigitalListCell" owner:self options:nil] lastObject];
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[indexPath.row];
    cell.model =  model;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView == _cardTabelView) {
        if (_isOpen) {
            if (indexPath.section == 1) {
                return 400;
            }
            return 180;
        }
        else
        {
            return 180;
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

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView == _cardTabelView) {
        _currentIndexPath = indexPath;
        if (indexPath.section == 0) {
//            WeXCardViewController *ctrl = [[WeXCardViewController alloc] init];
//            self.navigationController.delegate = ctrl;
//            [self.navigationController pushViewController:ctrl animated:YES];
            
            WeXCreatePassportChooseView *chooseView = [[WeXCreatePassportChooseView alloc] initWithFrame:[UIScreen mainScreen].bounds];
            chooseView.delegate = self;
            [self.view addSubview:chooseView];
        }
        if (_isOpen) {
            if (indexPath.section == 1)
            {
                WeXWalletDigitalAssetListController *ctrl = [[WeXWalletDigitalAssetListController alloc] init];
                self.navigationController.delegate = ctrl;
                ctrl.datasArray = self.datasArray;
                [self.navigationController pushViewController:ctrl animated:YES];
            }
            else if (indexPath.section == 2)
            {
                WeXCreditHomeViewController *ctrl = [[WeXCreditHomeViewController alloc] init];
                [self.navigationController pushViewController:ctrl animated:YES];
            }
            else if (indexPath.section == 3)
            {
                WeXAllShow2ViewController *ctrl = [[WeXAllShow2ViewController alloc] init];
                self.navigationController.delegate = ctrl;
                [self.navigationController pushViewController:ctrl animated:YES];
            }
        }
        else
        {
            if (indexPath.section == 1)
            {
                WeXAllShow1ViewController *ctrl = [[WeXAllShow1ViewController alloc] init];
                self.navigationController.delegate = ctrl;
                [self.navigationController pushViewController:ctrl animated:YES];
            }
        }
        
    }
   
}

- (void)tapAddressClick{
    WeXPassportLocationViewController *ctrl = [[WeXPassportLocationViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

#pragma mark WeXCreatePassportChooseViewDelegate
-(void)clickCreatePassportButton
{
    WeXCreatePassportViewController *ctrl = [[WeXCreatePassportViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)clickImportPassportButton
{
    WeXImportViewController *ctrl = [[WeXImportViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}






@end
