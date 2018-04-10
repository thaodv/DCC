//
//  WeXCardViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/15.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXCardViewController.h"
#import "WeXCardSettingViewController.h"
#import "WeXScanViewController.h"
#import "WeXLoginManagerViewController.h"

#import "WeXAuthorizeLoginViewController.h"
#import "WeXAuthorizeLoginRecordRLMModel.h"

#import "WeXAuthorizeLoginRecordCell.h"
#import "WeXAuthorizeLoginRecordController.h"
#import "WeXPassportLocationViewController.h"

#import "ExampleUIWebViewController.h"

#import "XWInteractiveTransition.h"
#import "WeXPassportCardTrasiton.h"



#import "TestViewController.h"//删
#import "WeXWalletDigitalAssetListController.h"
//#import "WeXRegisterSuccessViewController.h"//删
#import "WeXDigitalAssetRLMModel.h"



@interface WeXCardViewController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
    WeXPasswordCacheModal *_model;
    UIImageView *_QRImageView;
    
    UILabel *_loginStatusLabel;
    UIImageView *_statusImageView;
    
    
    RSA *_publicKey;
    RSA *_privateKey;
    
    NSString *_rsaPublicKey;
    NSString *_rsaPrivateKey;
    
    BOOL _isFirstLoad;
    
    UILabel *_footerLabel;
    UIButton *_moreBtn;
}

@property (nonatomic,strong)NSMutableArray  *datasArray;

@property (nonatomic,strong)XWInteractiveTransition *interactiveTransition;

@end

@implementation WeXCardViewController

- (void)viewDidLoad {
    [super viewDidLoad];
//    self.navigationItem.title = @"全向口袋";
    _isFirstLoad = YES;
    [self setupNavgationType];
    [self commonInit];
    [self setupSubViews];
   
    
//    //初始化手势过渡的代理
//    self.interactiveTransition = [XWInteractiveTransition interactiveTransitionWithTransitionType:XWInteractiveTransitionTypePop GestureDirection:XWInteractiveTransitionGestureDirectionRight];
//    //给当前控制器的视图添加手势
//    [_interactiveTransition addPanGestureForViewController:self];

}




-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if (!_isFirstLoad) {
        [self commonInit];
        
    }
    _isFirstLoad = NO;
    [self getAllLoginRecordType];
    
    self.navigationController.delegate = nil;
   
}

- (void)commonInit{
    
    _model = [WexCommonFunc getPassport];
    
    if (_model.isAllow) {
        _loginStatusLabel.text = @"统一登录状态:可用";
        _statusImageView.image = [UIImage imageNamed:@"passportStatusYes"];
        
    }
    else
    {
        _loginStatusLabel.text = @"统一登录状态:不可用";
        _statusImageView.image = [UIImage imageNamed:@"passportStatusNo"];
        
    }
//
  
    
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
        
        
//        NSData *data = [WexCommonFunc rsaSHA256SignPlainSring:@"123" withHexPrivateKey:_model.rasPrivateKey];
//        
//        BOOL res = [WexCommonFunc rsaSHA256VerifyPlainSring:@"123" withSignature:data withHexPublicKey:_model.rsaPublicKey];
//        NSLog(@"res=%d",res);
        
    }
}






//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"全向口袋";
    titleLabel.font = [UIFont systemFontOfSize:20];
    titleLabel.textColor = ColorWithLabelTitleBlack;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(30);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@20);
    }];
    
    UIView *cardBackView = [[UIView alloc] init];
    cardBackView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:cardBackView];
    [cardBackView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.equalTo(self.view).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.height.equalTo(@180);
    }];
//    [cardBackView layoutIfNeeded];
    _cardView = cardBackView;
    
    UIImageView *cardImageView = [[UIImageView alloc] init];
    cardImageView.image = [UIImage imageNamed:@"digital_card"];
    cardImageView.layer.magnificationFilter = kCAFilterNearest;
    [cardBackView addSubview:cardImageView];
    [cardImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(cardBackView);
    }];
    
    UIImageView *headImageView = [[UIImageView alloc] init];
    
    UIImage *headImage = [IYFileManager cacheImageFileWithKey:WEX_FILE_USER_FACE];
    if (headImage == nil) {
        headImageView.image = [UIImage imageNamed:@"digital_head"];
    }
    else
    {
        headImageView.image = headImage;
    }
    
    headImageView.layer.cornerRadius = 40;
    headImageView.layer.masksToBounds = YES;
    [cardBackView addSubview:headImageView];
    [headImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(cardBackView);
        make.centerY.equalTo(cardBackView);
        make.width.equalTo(@80);
        make.height.equalTo(@80);
    }];
    
    UILabel *addressLabel = [[UILabel alloc] init];
    NSString *address = [WexCommonFunc getFromAddress];
    if (address.length>8) {
        NSString *subAddress1 = [address substringWithRange:NSMakeRange(address.length-8, 4)];
        NSString *subAddress2 = [address substringWithRange:NSMakeRange(address.length-4, 4)];
        addressLabel.text = [NSString stringWithFormat:@"0x  %@  %@",subAddress1,subAddress2];
    }
    addressLabel.font = [UIFont systemFontOfSize:16];
    addressLabel.textColor = [UIColor whiteColor];
    addressLabel.textAlignment = NSTextAlignmentLeft;
    [cardBackView addSubview:addressLabel];
    [addressLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(cardBackView.mas_bottom).offset(-10);
        make.leading.equalTo(cardBackView).offset(30);
        make.height.equalTo(@20);
    }];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapAddressClick)];
    addressLabel.userInteractionEnabled = YES;
    [addressLabel addGestureRecognizer:tap];
    
    
    UILabel *nickLabel = [[UILabel alloc] init];
    nickLabel.text = [WexDefaultConfig instance].nickName;
    nickLabel.font = [UIFont systemFontOfSize:16];
    nickLabel.textColor = [UIColor whiteColor];
    nickLabel.textAlignment = NSTextAlignmentLeft;
    [cardBackView addSubview:nickLabel];
    [nickLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(addressLabel.mas_top).offset(-10);
        make.leading.equalTo(addressLabel);
        make.height.equalTo(@20);
        make.trailing.equalTo(headImageView.mas_leading);
    }];
    
   
    
 
    
//    UIView *backView = [[UIView alloc] init];
//    backView.backgroundColor = [UIColor whiteColor];
//    [self.view addSubview:backView];
//    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(topBackView.mas_bottom);
//        make.leading.trailing.bottom.equalTo(self.view);
//    }];
//
    
    UILabel *loginStatusLabel = [[UILabel alloc] init];
    loginStatusLabel.text = @"统一登录状态:可用";
    loginStatusLabel.font = [UIFont systemFontOfSize:16];
    loginStatusLabel.textColor = ColorWithLabelDescritionBlack;
    loginStatusLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:loginStatusLabel];
    [loginStatusLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(cardBackView.mas_bottom).offset(20);
        make.centerX.equalTo(self.view);
        make.height.equalTo(@15);
    }];
    _loginStatusLabel = loginStatusLabel;
    
    UIImageView *statusImageView = [[UIImageView alloc] init];
    if (_model.isAllow) {
        loginStatusLabel.text = @"统一登录状态:可用";
        statusImageView.image = [UIImage imageNamed:@"passportStatusYes"];

    }
    else
    {
        loginStatusLabel.text = @"统一登录状态:不可用";
        statusImageView.image = [UIImage imageNamed:@"passportStatusNo"];

    }
    statusImageView.layer.magnificationFilter = kCAFilterNearest;
    [self.view addSubview:statusImageView];
    [statusImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(loginStatusLabel.mas_trailing).offset(5);
        make.top.equalTo(loginStatusLabel);
        make.width.equalTo(@15);
        make.height.equalTo(@15);
    }];
    _statusImageView = statusImageView;
    
    
    
//    UIButton *loginStateBtn = [UIButton buttonWithType:UIButtonTypeCustom];
//    if (_model.isAllow) {
//        [loginStateBtn setTitle:@"统一登录状态:可用" forState:UIControlStateNormal];
//        loginStateBtn.backgroundColor = ColorWithRGB(179, 225, 134);
//    }
//    else
//    {
//         [loginStateBtn setTitle:@"统一登录状态:不可用" forState:UIControlStateNormal];
//        loginStateBtn.backgroundColor = ColorWithRGB(191, 192,193);
//
//    }
//    [loginStateBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
//    loginStateBtn.layer.cornerRadius = 20;
//    [loginStateBtn addTarget:self action:@selector(loginStateBtnClick) forControlEvents:UIControlEventTouchUpInside];
//    loginStateBtn.titleLabel.font = [UIFont systemFontOfSize:17];
//    [self.view addSubview:loginStateBtn];
//    [loginStateBtn mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.centerX.equalTo(self.view);
//        make.centerY.equalTo(backView.mas_top);
//        make.width.equalTo(@220);
//        make.height.equalTo(@40);
//    }];
//    _loginStateBtn = loginStateBtn;
    
    UILabel *recordLabel= [[UILabel alloc] init];
    recordLabel.text = @"统一登录使用记录";
    recordLabel.font = [UIFont systemFontOfSize:16];
    recordLabel.textColor = ColorWithLabelDescritionBlack;
    recordLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:recordLabel];
    [recordLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(loginStatusLabel.mas_bottom).offset(30);
        make.leading.equalTo(self.view).offset(10);
        make.width.equalTo(@150);
        make.height.equalTo(@15);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = ColorWithLine;
    [self.view addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.top.equalTo(recordLabel.mas_bottom).offset(10);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    WeXCustomButton *loginManagerBtn = [WeXCustomButton button];
    [loginManagerBtn setTitle:@"统一登录管理" forState:UIControlStateNormal];
    [loginManagerBtn addTarget:self action:@selector(loginManagerBtnClick) forControlEvents:UIControlEventTouchUpInside];
    loginManagerBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [self.view addSubview:loginManagerBtn];
    [loginManagerBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@130);
        make.height.equalTo(@40);
    }];
    
    _tableView = [[UITableView alloc] init];
    _tableView.delegate = self;
    _tableView.dataSource = self;
//    _tableView.scrollEnabled = NO;
    _tableView.rowHeight = 50;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self getAllLoginRecordType];
    
    UIView *footView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 30)];
    UILabel *footerLabel = [[UILabel alloc] initWithFrame:footView.bounds];
    footerLabel.text = @"没有更多记录了";
    footerLabel.font = [UIFont systemFontOfSize:13];
    footerLabel.textColor = [UIColor lightGrayColor];
    footerLabel.textAlignment = NSTextAlignmentCenter;
    [footView addSubview:footerLabel];
    _footerLabel = footerLabel;
    
    UIButton *moreBtn = [UIButton buttonWithType:UIButtonTypeSystem];
    moreBtn.frame = CGRectMake(0, 0, 20, 20);
    [moreBtn setImage:[UIImage imageNamed:@"more"] forState:UIControlStateNormal];
    [moreBtn addTarget:self action:@selector(moreBtnClick) forControlEvents:UIControlEventTouchUpInside];
    moreBtn.WeX_centerX = footView.WeX_centerX;
    moreBtn.WeX_centerY = footView.WeX_centerY;
    [footView addSubview:moreBtn];
    _moreBtn = moreBtn;

    
    _tableView.tableFooterView = footView;
    [self.view addSubview:_tableView];
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line2.mas_bottom);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(loginManagerBtn.mas_top).offset(-10);
    }];
    
    
    
    
}

- (void)setupNavgationType{
    self.navigationItem.hidesBackButton = YES;
    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"digital_cha1"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
    self.navigationItem.rightBarButtonItem = rihgtItem;

}

- (void)tapAddressClick{
    WeXPassportLocationViewController *ctrl = [[WeXPassportLocationViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}


- (void)moreBtnClick{
    WeXAuthorizeLoginRecordController *ctrl = [[WeXAuthorizeLoginRecordController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)loginStateBtnClick{
//    WeXLoginManagerViewController *ctrl = [[WeXLoginManagerViewController alloc] init];
//    [self.navigationController pushViewController:ctrl animated:YES];
    

    
    
//    ExampleUIWebViewController *ctrl = [[ExampleUIWebViewController alloc] init];
//    [self.navigationController pushViewController:ctrl animated:YES];
    
    WeXWalletDigitalAssetListController *ctrl = [[WeXWalletDigitalAssetListController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)loginManagerBtnClick{
    
    WeXLoginManagerViewController *ctrl = [[WeXLoginManagerViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}


- (void)rightItemClick{
    self.navigationController.delegate = self;
    [self.navigationController popViewControllerAnimated:YES];
}


-(NSMutableArray *)datasArray
{
    if (_datasArray == nil) {
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
}

- (void)getAllLoginRecordType{
    
    RLMResults<WeXAuthorizeLoginRecordRLMModel *> *results = [WeXAuthorizeLoginRecordRLMModel allObjects];
    [self.datasArray removeAllObjects];
    NSLog(@"count=%lu",(unsigned long)results.count);
    for (WeXAuthorizeLoginRecordRLMModel *model in results) {
        [self.datasArray addObject:model];
    }
    
    self.datasArray = (NSMutableArray *)[[self.datasArray reverseObjectEnumerator] allObjects];
    
    if (self.datasArray.count <= 6) {
        _footerLabel.hidden = NO;
        _moreBtn.hidden = YES;
    }
    else
    {
        _footerLabel.hidden = YES;
        _moreBtn.hidden = NO;
    }
    
    [_tableView reloadData];
}



- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.datasArray.count>= 6?6:self.datasArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellID = @"cellID";
    WeXAuthorizeLoginRecordCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXAuthorizeLoginRecordCell" owner:self options:nil] lastObject];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    WeXAuthorizeLoginRecordRLMModel *model = [self.datasArray objectAtIndex:indexPath.row];
    cell.model = model;
    return cell;
   
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.datasArray.count> 6) {
        WeXAuthorizeLoginRecordController *ctrl = [[WeXAuthorizeLoginRecordController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}

- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController animationControllerForOperation:(UINavigationControllerOperation)operation fromViewController:(UIViewController *)fromVC toViewController:(UIViewController *)toVC{
    //分pop和push两种情况分别返回动画过渡代理相应不同的动画操作
    return [WeXPassportCardTrasiton transitionWithType:operation == UINavigationControllerOperationPush ? WeXPassportCardTrasitonPush : WeXPassportCardTrasitonPop];
}

- (id<UIViewControllerInteractiveTransitioning>)navigationController:(UINavigationController *)navigationController interactionControllerForAnimationController:(id<UIViewControllerAnimatedTransitioning>)animationController{
    //手势开始的时候才需要传入手势过渡代理，如果直接点击pop，应该传入空，否者无法通过点击正常pop
    return _interactiveTransition.interation ? _interactiveTransition : nil;
}




@end
