//
//  WeXWalletDigitalRecordController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDigitalRecordController.h"
#import "WeXWalletTransctionRecordCell.h"
#import "WeXWalletDigitalRecordDetailController.h"

#import "WeXWalletEtherscanGetRecordAdapter.h"
#import "WeXWalletEthplorerGetRecordAdapter.h"
#import "WeXWalletEtherscanGetPendingAdapter.h"

#import "WeXWalletAllGetRecordAdapter.h"

#import "WeXWalletTransferResultManager.h"

#import "WeXShareQRImageView.h"
#import "WeXShareManager.h"

@interface WeXWalletDigitalRecordController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
    NSTimer *_timer;
    
    UILabel *_noMoreLabel;
}

@property (nonatomic,strong)WeXWalletEtherscanGetRecordAdapter *getRecordAdapter;
@property (nonatomic,strong)WeXWalletEthplorerGetRecordAdapter *getEthplorerRecordAdapter;
@property (nonatomic,strong)WeXWalletEtherscanGetPendingAdapter *getPendingAdapter;
@property (nonatomic,strong)WeXWalletAllGetRecordAdapter *getAllRecordAdapter;
@property (nonatomic,strong)NSMutableArray *datasArray;
@end

@implementation WeXWalletDigitalRecordController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}

- (void)setupNavgationType{
    
    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"digital_share"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
    self.navigationItem.rightBarButtonItem = rihgtItem;
    
}

- (void)rightItemClick
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


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    if ([self.tokenModel.symbol isEqualToString:@"ETH"]) {
        [self createGetRecordRequest];
    }
    else if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        [self createGetAllRecordRequest:[WexCommonFunc getFTCContractAddress]];
    }
    else if ([self.tokenModel.symbol isEqualToString:@"DCC"]&&self.isPrivateChain)
    {
        [self createGetAllRecordRequest:[WexCommonFunc getDCCContractAddress]];
    }
    else
    {
        [self createGetEthplorerQuoteRequest];
    }
    
    
}


- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [_timer invalidate];
}

- (void)createGetEthplorerQuoteRequest{
    _getEthplorerRecordAdapter = [[WeXWalletEthplorerGetRecordAdapter alloc]  init];
    _getEthplorerRecordAdapter.delegate = self;
    WeXWalletEthplorerGetRecordParamModal *paramModal = [[WeXWalletEthplorerGetRecordParamModal alloc] init];
    paramModal.limit = @"10";
    paramModal.token = self.tokenModel.contractAddress;
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

#pragma -mark 获取BitExpress交易记录请求
- (void)createGetAllRecordRequest:(NSString *)privateContractAddress{
    _getAllRecordAdapter = [[WeXWalletAllGetRecordAdapter alloc] init];
    _getAllRecordAdapter.delegate = self;
    WeXWalletAllGetRecordParamModal* paramModal = [[WeXWalletAllGetRecordParamModal alloc] init];
    paramModal.contractAddress = privateContractAddress;
    paramModal.address = [WexCommonFunc getFromAddress];
    paramModal.page = @"1";
    paramModal.pageSize = @"1000";
    [_getAllRecordAdapter run:paramModal];
    
}


#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getRecordAdapter) {
        WeXWalletEtherscanGetRecordResponseModal *responseModel = (WeXWalletEtherscanGetRecordResponseModal *)response;
        _datasArray = responseModel.result;
        if (_datasArray.count > 0) {
            _noMoreLabel.hidden = NO;
        }
        [_tableView reloadData];
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
    
        if (_datasArray.count > 0) {
            _noMoreLabel.hidden = NO;
        }
        [_tableView reloadData];
        
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
        if (_datasArray.count > 0) {
            _noMoreLabel.hidden = NO;
        }
        [_tableView reloadData];
        
    }
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *recordlabel = [[UILabel alloc] init];
    recordlabel.text = WeXLocalizedString(@"交易记录");
    recordlabel.font = [UIFont systemFontOfSize:20];
    recordlabel.textColor = COLOR_LABEL_DESCRIPTION;
    recordlabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:recordlabel];
    [recordlabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
    }];
    
    _tableView = [[UITableView alloc] init];
    UIView *footView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 20)];
    UILabel *noMoreLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, footView.frame.size.width, 20)];
    noMoreLabel.hidden = YES;
    noMoreLabel.text = WeXLocalizedString(@"没有更多了");
    noMoreLabel.textAlignment = NSTextAlignmentCenter;
    noMoreLabel.font = [UIFont systemFontOfSize:10];
    noMoreLabel.textColor = COLOR_LABEL_WEAK;
    [footView addSubview:noMoreLabel];
    _noMoreLabel = noMoreLabel;
    
    _tableView.tableFooterView = footView;
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 70;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(recordlabel.mas_bottom).offset(10);
        make.leading.bottom.trailing.equalTo(self.view);
    }];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.datasArray.count;
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
   
    WeXWalletDigitalRecordDetailController *ctrl = [[WeXWalletDigitalRecordDetailController alloc] init];
    ctrl.recordModel = recordModel;
    ctrl.tokenModel = self.tokenModel;
    ctrl.isPrivateChain = self.isPrivateChain;
    [self.navigationController pushViewController:ctrl animated:YES];
    
}



@end
