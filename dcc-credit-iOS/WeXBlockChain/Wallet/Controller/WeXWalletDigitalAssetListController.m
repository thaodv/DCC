//
//  WeXWalletDigitalAssetListController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/17.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDigitalAssetListController.h"
#import "WeXWalletDigitalListCell.h"
#import "WeXWalletDigitalAssetAddController.h"
#import "WeXWalletDigitalAssetDetailController.h"
#import "WeXWalletInfuraGetBalanceAdapter.h"
#import "WeXWalletDigitalGetQuoteAdapter.h"
#import "WeXDigitalAssetRLMModel.h"
#import "WeXWalletDigitalGetTokenModal.h"

#import "WeXPassportCardDigitalTrasition.h"
#import "XWInteractiveTransition.h"

#import "WeXShareQRImageView.h"
#import "WeXShareManager.h"

#import "WeXTokenDccListViewController.h"
#import "WeXAgentMarketAdapter.h"
//#import "WeXNetworkCheckManager.h"
#import "WeXSelectedNodeViewController.h"
#import "WeXWalletNewCell.h"


@interface WeXWalletDigitalAssetListController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
    NSString *_encodeData;
    UILabel *_assetlabel;//总的数字资产
    
    BOOL isFirstLoad;
    
    NSString *_dccPrivateBalance;
    NSString *_dccPublicBalance;
}

@property (nonatomic,strong)WeXWalletInfuraGetBalanceAdapter *getBalanceAdapter;
//@property (nonatomic,strong)WeXWalletDigitalGetQuoteAdapter *getQuoteAdapter;
@property (nonatomic,strong)WeXAgentMarketAdapter *getAgentAdapter;
@property (nonatomic, strong) XWInteractiveTransition *interactiveTransition;
@property (nonatomic, strong) UIButton *networkDelayButton;
@property (nonatomic, strong) WeXNetworkCheckModel *checkModel;

@end

static NSString * const kNewWalletCellID = @"WeXWalletNewCellID";


@implementation WeXWalletDigitalAssetListController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title= WeXLocalizedString(@"数字资产");
    [self setNavigationNomalBackButtonType];
    NSLog(@"_datasArray = %@",_datasArray);
    [self setupSubViews];
    [self setupNodeNavgationType];
}

- (void)setupNodeNavgationType{
    _networkDelayButton = [UIButton buttonWithType:UIButtonTypeCustom];
    _networkDelayButton.bounds = CGRectMake(0, 0, 30, 21);
    [_networkDelayButton setImage:[UIImage imageNamed:@"Oval"] forState:UIControlStateNormal];
    [_networkDelayButton setImage:[UIImage imageNamed:@"Oval"] forState:UIControlStateSelected];
    [_networkDelayButton setImage:[UIImage imageNamed:@"Oval"] forState:UIControlStateHighlighted];
    [_networkDelayButton addTarget:self action:@selector(selectNodeEvent:) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:_networkDelayButton];
}

- (void)setNodeNetworkDelayModel:(WeXNetworkCheckModel *)model {
    UIImage *image = [UIImage imageNamed:@"Oval-Good"];
    switch (model.nodeCheckState) {
        case WexNetworkCheckStateGood: {
            image = [UIImage imageNamed:@"Oval-Good"];
        }
            
            break;
        case WexNetworkCheckStateCommon: {
            image = [UIImage imageNamed:@"Oval-Common"];
        }
            break;
            
        default: {
            image = [UIImage imageNamed:@"Oval-Bad"];
        }
            break;
    }
    [_networkDelayButton setImage:image forState:UIControlStateNormal];
    [_networkDelayButton setImage:image forState:UIControlStateSelected];
    [_networkDelayButton setImage:image forState:UIControlStateHighlighted];
}
// MARK: - 选择节点
- (void)selectNodeEvent:(UIButton *)sender {
    WeXSelectedNodeViewController *selectNodeVC = [WeXSelectedNodeViewController new];
    selectNodeVC.DidSelectedNode = ^(WeXNetworkCheckModel *result) {
        
    };
    [self.navigationController pushViewController:selectNodeVC animated:YES];
}


- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.delegate = nil;
    [self getDatas];
//  [self createGetQuoteRequest];
    [self createGetAgentMarketRequest];
    [self createGetBalaceRequest];
    [self getNodeNetWorkDelay];
}

// MARK: - 获取节点网络延迟
- (void)getNodeNetWorkDelay {
    [[WeXNetworkCheckManager shareManager] startDefaultNodeNetworkDelay:^(WeXNetworkCheckModel *result) {
        self.checkModel = result;
        [self setNodeNetworkDelayModel:result];
    }];
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
    [_tableView reloadData];
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
            NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
            return;
        }
        
        [self createDccBalanceRequest];
        [self createETHAndERC20Banlance];
        
    }];

}


- (void)createDccBalanceRequest
{
    if (self.datasArray.count < 2) {
        return;
    }
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[0];
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
                  _dccPrivateBalance = @"--";
              }
              else
              {
                  _dccPrivateBalance = [WexCommonFunc formatterStringWithContractBalance:originBalance decimals:[model.decimals integerValue]];
              }
              [self configDccDigitalAsset];
          }];
         //dccerc20
         [[WXPassHelper instance] call2ContractAddress:model.contractAddress data:response type:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response) {
             NSLog(WeXLocalizedString(@"dcc共连balance=%@"),response);
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
    [_tableView reloadData];
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
            [_tableView reloadData];
        }];
        
    }];
  
}

- (void)createETHAndERC20Banlance
{
    if (self.datasArray.count < 2) {
        return;
    }
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
         [_tableView reloadData];
         
     }];
    
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
    [_tableView reloadData];
}


//- (void)createGetQuoteRequest{
//
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
//           [varietyCodesStr appendString:[NSString stringWithFormat:@"%@,",model.symbol]];
//        }
//    }
//    paramModal.varietyCodes = varietyCodesStr;
//    [_getQuoteAdapter run:paramModal];
//}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
//    if(adapter == _getQuoteAdapter){
//        WeXWalletDigitalGetQuoteResponseModal *model = (WeXWalletDigitalGetQuoteResponseModal *)response;
//        NSLog(@"model=%@",model);
//        for (WeXWalletDigitalGetQuoteResponseModal_item *quoteModel in model.data) {
//            for (WeXWalletDigitalGetTokenResponseModal_item *tokenModel in self.datasArray) {
//                if ([quoteModel.varietyCode isEqualToString:tokenModel.symbol]) {
//                    tokenModel.price = [NSString stringWithFormat:@"%.2f",quoteModel.price];
//                    break;
//                }
//            }
//        }
//        [self configTotalDigitalAsset];
//    }
       if([adapter isKindOfClass:[WeXAgentMarketAdapter class]]){
//        NSLog(@"response = %@",response);
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
//            [_tableView reloadData];
              [self configTotalDigitalAsset];
        }
    }
}

- (void)configTotalDigitalAsset{
    CGFloat asset = 0.0;
    for (WeXWalletDigitalGetTokenResponseModal_item *model in self.datasArray) {
//        NSLog(@"model = %@",model);
        if (model.balance&&model.price&&![model.balance isEqualToString:@"--"]) {
           asset += [model.balance floatValue]*[model.price floatValue];
            _assetlabel.text = [NSString stringWithFormat:@"≈¥%.4f",asset];
        }
    }
}


- (void)setupNavgationType{
    
    self.navigationItem.hidesBackButton = YES;
    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"dcc_animation_cha"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
    self.navigationItem.rightBarButtonItem = rihgtItem;
    
}

//初始化滚动视图
-(void)setupSubViews{
    
    
    UIView *cardBackView = [[UIView alloc] init];
    cardBackView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:cardBackView];
    [cardBackView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.equalTo(self.view).offset(0);
        make.trailing.equalTo(self.view).offset(0);
        make.bottom.equalTo(self.view);
    }];
     _cardView = cardBackView;
    
//    UILabel *label1 = [[UILabel alloc] init];
//    label1.text = WeXLocalizedString(@"数字资产");
//    label1.font = [UIFont systemFontOfSize:15];
//    label1.textColor = ColorWithHex(0xb8b8b8);
//    label1.textAlignment = NSTextAlignmentLeft;
//    [cardBackView addSubview:label1];
//    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(self.view).offset(kNavgationBarHeight);
//        make.leading.equalTo(self.view).offset(20);
//        make.height.equalTo(@20);
//    }];
    
    UILabel *assetlabel = [[UILabel alloc] init];
    assetlabel.text = @"--";
    assetlabel.font = [UIFont systemFontOfSize:25];
    assetlabel.textColor = COLOR_LABEL_TITLE;
    assetlabel.textAlignment = NSTextAlignmentLeft;
    [cardBackView addSubview:assetlabel];
    [assetlabel mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(label1.mas_bottom).offset(10);
           make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@20);
    }];
    _assetlabel = assetlabel;
    
    UIButton *addBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [addBtn setImage:[UIImage imageNamed:@"digital_add"] forState:UIControlStateNormal];
    [addBtn addTarget:self action:@selector(addBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:addBtn];
    [addBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.trailing.equalTo(self.view).offset(-10);
        make.width.equalTo(@40);
        make.height.equalTo(@40);
    }];
    
//    WeXCustomButton *shareBtn = [WeXCustomButton button];
//    [shareBtn setTitle:WeXLocalizedString(@"分享") forState:UIControlStateNormal];
//    [shareBtn setTitleColor:ColorWithRGB(248, 31, 117) forState:UIControlStateNormal];
//    [shareBtn addTarget:self action:@selector(shareBtnClick) forControlEvents:UIControlEventTouchUpInside];
//    [self.view addSubview:shareBtn];
//    [shareBtn mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.bottom.equalTo(self.view).offset(-30);
//        make.centerX.equalTo(self.view);
//        make.width.equalTo(@170);
//        make.height.equalTo(@50);
//    }];
    
    
    _tableView = [[UITableView alloc] init];
//    UIView *footView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 150)];
//    UIButton *shareBtn = [UIButton buttonWithType:UIButtonTypeCustom];
//    shareBtn.frame = CGRectMake(0, 70, 100, 60);
//    shareBtn.WeX_centerX = footView.WeX_centerX;
//    [shareBtn setTitle:WeXLocalizedString(@"分享") forState:UIControlStateNormal];
//    [shareBtn setTitleColor:ColorWithRGB(248, 31, 117) forState:UIControlStateNormal];
//    shareBtn.backgroundColor = [UIColor whiteColor];
//    [shareBtn addTarget:self action:@selector(shareBtnClick) forControlEvents:UIControlEventTouchUpInside];
//    [footView addSubview:shareBtn];
    
//    _tableView.tableFooterView = footView;
    [cardBackView addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 70;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(assetlabel.mas_bottom).offset(20);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(self.view);
    }];

    [_tableView registerClass:[WeXWalletNewCell class] forCellReuseIdentifier:kNewWalletCellID];
}

- (void)shareBtnClick
{
    WeXShareQRImageView *shareView = [[WeXShareQRImageView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:shareView];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        WeXShareManager *shareManager = [[WeXShareManager alloc] init];
//        shareManager.shareImage = [self screenShot];
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

- (void)addBtnClick{
    WeXWalletDigitalAssetAddController *ctrl = [[WeXWalletDigitalAssetAddController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)rightItemClick
{
    self.navigationController.delegate = self;
    [self.navigationController popViewControllerAnimated:YES];
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.datasArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXWalletNewCell *cell = [tableView dequeueReusableCellWithIdentifier:kNewWalletCellID forIndexPath:indexPath];
//    static NSString *cellID = @"cellID";
//    WeXWalletDigitalListCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
//    if (cell == nil) {
//        cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletDigitalListCell" owner:self options:nil] lastObject];
//        cell.backgroundColor = [UIColor clearColor];
//        cell.selectionStyle = UITableViewCellSelectionStyleNone;
//    }
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[indexPath.row];
    [cell setModel:model];
//    cell.model =  model;
    return cell;
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[indexPath.row];
    if ([model.symbol isEqualToString:@"DCC"]) {
        WeXTokenDccListViewController *ctrl = [[WeXTokenDccListViewController alloc] init];
        ctrl.tokenModel = model;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else
    {
        WeXWalletDigitalAssetDetailController *ctrl = [[WeXWalletDigitalAssetDetailController alloc] init];
        ctrl.tokenModel = model;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}

- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController animationControllerForOperation:(UINavigationControllerOperation)operation fromViewController:(UIViewController *)fromVC toViewController:(UIViewController *)toVC{
    //分pop和push两种情况分别返回动画过渡代理相应不同的动画操作
    return [WeXPassportCardDigitalTrasition transitionWithType:operation == UINavigationControllerOperationPush ? WeXPassportCardTrasitonPush : WeXPassportCardTrasitonPop];
}

- (id<UIViewControllerInteractiveTransitioning>)navigationController:(UINavigationController *)navigationController interactionControllerForAnimationController:(id<UIViewControllerAnimatedTransitioning>)animationController{
    //手势开始的时候才需要传入手势过渡代理，如果直接点击pop，应该传入空，否者无法通过点击正常pop
    return _interactiveTransition.interation ? _interactiveTransition : nil;
}

//获取各个币种的价格请求
- (void)createGetAgentMarketRequest{
    _getAgentAdapter = [[WeXAgentMarketAdapter alloc] init];
    //    _getAgentAdapter.currentyName = self.tokenModel.symbol;
    _getAgentAdapter.delegate = self;
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
    [_getAgentAdapter run:paramModal];
}


@end
