//
//  WeXDccAcrossChainRecordController.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDccAcrossChainRecordController.h"
#import "WeXDccAcrossChainRecordCell.h"
#import "WeXDccAcrossChainRecordDetailController.h"
#import "WeXDCCSelectCrossChainPeriodView.h"
#import "WeXWalletGetAcrossChainTransferRecordAdapter.h"
#import "WeXWalletGetAcrossChainTransferTotalAdapter.h"
#import "MJRefresh.h"

static NSString *const kPageSize = @"20";

@interface WeXDccAcrossChainRecordController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
    MJRefreshNormalHeader *_refreshHeader;
    MJRefreshBackFooter *_refreshFooter;
    UIImageView *_noAwardImageView;
    UILabel *_noAwardLabel;
    
    NSString *_startTime;
    NSString *_endTime;
    
    UILabel *_periodLabel;
    UILabel *_toPublicTotalLabel;
    UILabel *_toPrivateTotalLabel;
}

@property (nonatomic,strong)NSMutableArray *datasArray;
@property (nonatomic,strong)WeXWalletGetAcrossChainTransferRecordAdapter *getRecordAdapter;
@property (nonatomic,strong)WeXWalletGetAcrossChainTransferTotalAdapter *getTotalAdapter;
@end

@implementation WeXDccAcrossChainRecordController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"跨链交易记录";
    [self setNavigationNomalBackButtonType];
    [self initData];
    [self setupSubViews];
    [self createQueryTransferTotalRequest];
}

- (void)initData
{
    NSDate *mondayDate = [self getMondayDate];
    _startTime = [self formatterDate:mondayDate];
    _endTime = [self formatterDate:[NSDate date]];
}

- (NSString *)formatterDate:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"yyyyMMdd";
    NSString *formatterStr = [formatter stringFromDate:date];
    return formatterStr;
}


- (NSDate *)getMondayDate
{
    NSDate *nowDate = [NSDate date];
    NSCalendar *calendar = [[NSCalendar alloc]
                            initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    unsigned unitFlags = NSCalendarUnitYear |
    NSCalendarUnitMonth |  NSCalendarUnitDay |
    NSCalendarUnitHour |  NSCalendarUnitMinute |
    NSCalendarUnitSecond | NSCalendarUnitWeekday;
    NSDateComponents *comp = [calendar components: unitFlags fromDate:nowDate];;
    NSInteger weekDay = [comp weekday];
    NSInteger day = [comp day];
    long firstDiff,lastDiff;
    if (weekDay == 1)
    {
        firstDiff = -6;
        lastDiff = 0;
    }
    else
    {
        firstDiff = [calendar firstWeekday] - weekDay + 1;
        lastDiff = 8 - weekDay;
    }
    NSDateComponents *firstDayComp = [calendar components:unitFlags  fromDate:nowDate];
    firstDayComp.day = day + firstDiff;
    firstDayComp.hour = 0;
    firstDayComp.minute = 0;
    firstDayComp.second = 0;
    NSDate *firstDayOfWeek = [calendar dateFromComponents:firstDayComp];
    
    return firstDayOfWeek;
    
}


- (void)createNoRecrodView
{
    if(_noAwardImageView ||_noAwardLabel) return;
    _noAwardImageView = [[UIImageView alloc] init];
    _noAwardImageView.image = [UIImage imageNamed:@"activity_zoolog_no_award"];
    [self.view addSubview:_noAwardImageView];
    [_noAwardImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.view);
        make.centerY.equalTo(self.view);
    }];
    UILabel *noAwardLabel = [[UILabel alloc] init];
    noAwardLabel.text = @"暂无记录";
    noAwardLabel.textAlignment = NSTextAlignmentCenter;
    noAwardLabel.font = [UIFont systemFontOfSize:19];
    noAwardLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self.view addSubview:noAwardLabel];
    [noAwardLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_noAwardImageView.mas_bottom).offset(10);
        make.centerX.equalTo(self.view).offset(0);
    }];
    _noAwardLabel = noAwardLabel;
    
}

- (void)removeNoRecrodView
{
    [_noAwardImageView removeFromSuperview];
    _noAwardImageView = nil;
    [_noAwardLabel removeFromSuperview];
    _noAwardLabel = nil;
}

- (void)calendarBtnClick
{
    WeXDCCSelectCrossChainPeriodView *periodView = [[WeXDCCSelectCrossChainPeriodView alloc] initWithFrame:self.view.bounds];
    periodView.confirmButtonBlock = ^(NSString *startTime, NSString *endTime,NSString *title) {
        _startTime = startTime;
        _endTime = endTime;
        _periodLabel.text = title;
        [self createQueryTransferTotalRequest];
    };
    [self.view addSubview:periodView];
}

- (void)createGetRecordRequest{
    _getRecordAdapter = [[WeXWalletGetAcrossChainTransferRecordAdapter alloc] init];
    _getRecordAdapter.delegate = self;
    WeXWalletGetAcrossChainTransferRecordParamModal *paramModel = [[WeXWalletGetAcrossChainTransferRecordParamModal alloc] init];
    paramModel.number = @"0";
    paramModel.size = kPageSize;
    paramModel.startTime = _startTime;
    paramModel.endTime = _endTime;
    [_getRecordAdapter run:paramModel];
    
   
    
}

- (void)loadMoreRecordRequest
{
    _getRecordAdapter.number += 1;
    WeXWalletGetAcrossChainTransferRecordParamModal *paramModel = [[WeXWalletGetAcrossChainTransferRecordParamModal alloc] init];
    paramModel.number = [NSString stringWithFormat:@"%ld",(long)_getRecordAdapter.number];
    paramModel.size = kPageSize;
    paramModel.startTime = _startTime;
    paramModel.endTime = _endTime;
    [_getRecordAdapter run:paramModel];
}

- (void)createQueryTransferTotalRequest{
    _getTotalAdapter = [[WeXWalletGetAcrossChainTransferTotalAdapter alloc] init];
    _getTotalAdapter.delegate = self;
    WeXWalletGetAcrossChainTransferTotalParamModal *paramModel = [[WeXWalletGetAcrossChainTransferTotalParamModal alloc] init];
    paramModel.startTime = _startTime;
    paramModel.endTime = _endTime;
    paramModel.originAssetCodeList = @"DCC,DCC_JUZIX";
    [_getTotalAdapter run:paramModel];

}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getRecordAdapter){
        [_refreshHeader endRefreshing];
        [_refreshFooter endRefreshing];
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXWalletGetAcrossChainTransferRecordResponseModal *model = (WeXWalletGetAcrossChainTransferRecordResponseModal *)response;
            if (_getRecordAdapter.number == 0 && model.items.count == 0)
            {
                [self createNoRecrodView];
            }
            else
            {
                [self removeNoRecrodView];
            }
            
            if (_getRecordAdapter.number == 0 && model.items.count > 0) {
                _refreshFooter = [MJRefreshBackNormalFooter footerWithRefreshingTarget:self refreshingAction:@selector(loadMoreRecordRequest)];
                _tableView.mj_footer = _refreshFooter;
            }
            if (_getRecordAdapter.number == 0) {
                self.datasArray = model.items;
            }
            else
            {
                [self.datasArray addObjectsFromArray:model.items];
            }
            
            if (model.items.count == 0) {
                [_refreshFooter endRefreshingWithNoMoreData];
            }
            [_tableView reloadData];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:[UIApplication sharedApplication].keyWindow];
        }
    }
    else if (adapter == _getTotalAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXWalletGetAcrossChainTransferTotalResponseModal *model = (WeXWalletGetAcrossChainTransferTotalResponseModal *)response;
            if (model.result.count == 0) {
                _toPublicTotalLabel.text = @"转到公链0.00";
                _toPrivateTotalLabel.text = @"转到私链0.00";
            }
            else if (model.result.count == 1) {
                WeXWalletGetAcrossChainTransferTotalResponseModal_item *itemModel = model.result[0];
                if ([itemModel.originAssetCode isEqualToString:@"DCC_JUZIX"]) {
                    if (itemModel.totalAmount) {
                        NSString *toPublicAmount = [WexCommonFunc formatterStringWithContractBalance:itemModel.totalAmount decimals:18];
                        _toPublicTotalLabel.text = [NSString stringWithFormat:@"转到公链%@",[self formatterTotalTransferValue:toPublicAmount]];
                        _toPrivateTotalLabel.text = @"转到私链0.00";
                    }
                    else
                    {
                        _toPublicTotalLabel.text = @"转到公链0.00";
                        _toPrivateTotalLabel.text = @"转到私链0.00";
                        
                    }
                    
                }
                else {
                    if (itemModel.totalAmount) {
                        NSString *toPrivateAmount = [WexCommonFunc formatterStringWithContractBalance:itemModel.totalAmount decimals:18];
                         _toPrivateTotalLabel.text = [NSString stringWithFormat:@"转到私链%@",[self formatterTotalTransferValue:toPrivateAmount]];
                         _toPublicTotalLabel.text = @"转到公链0.00";
                    }
                    else
                    {
                        _toPublicTotalLabel.text = @"转到公链0.00";
                        _toPrivateTotalLabel.text = @"转到私链0.00";
                    }
                }
            }
            else
            {
                for (WeXWalletGetAcrossChainTransferTotalResponseModal_item *itemModel in model.result) {
                    if ([itemModel.originAssetCode isEqualToString:@"DCC_JUZIX"]) {
                        if (itemModel.totalAmount) {
                            NSString *toPublicAmount = [WexCommonFunc formatterStringWithContractBalance:itemModel.totalAmount decimals:18];
                            _toPublicTotalLabel.text = [NSString stringWithFormat:@"转到公链%@",[self formatterTotalTransferValue:toPublicAmount]];
                        }
                        else
                        {
                            _toPublicTotalLabel.text = @"转到公链0.00";
                        }
                        
                    }
                    if ([itemModel.originAssetCode isEqualToString:@"DCC"]) {
                        if (itemModel.totalAmount) {
                            NSString *toPrivateAmount = [WexCommonFunc formatterStringWithContractBalance:itemModel.totalAmount decimals:18];
                            _toPrivateTotalLabel.text = [NSString stringWithFormat:@"转到私链%@",[self formatterTotalTransferValue:toPrivateAmount]];
                        }
                        else
                        {
                            _toPrivateTotalLabel.text = @"转到私链0.00";
                        }
                    }
                    
                }
            }
           
            [_refreshHeader beginRefreshing];
            
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

- (NSString *)formatterTotalTransferValue:(NSString *)originStr
{
    NSString *formatterStr;
    if ([originStr floatValue] >= 100000000) {
        formatterStr = [NSString stringWithFormat:@"%.2f亿",[originStr floatValue]/100000000];
    }
    else if([originStr floatValue] < 100000000 &&[originStr floatValue] > 10000)
    {
        formatterStr = [NSString stringWithFormat:@"%.2f万",[originStr floatValue]/10000];
    }
    else
    {
       formatterStr = [NSString stringWithFormat:@"%.2f",[originStr floatValue]];
    }
        
    
    return formatterStr;
}




//初始化滚动视图
-(void)setupSubViews{
    
    UIView *backView = [[UIView alloc] init];
    backView.backgroundColor = ColorWithRGB(248, 248, 254);
    [self.view addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.trailing.equalTo(self.view);
        make.height.equalTo(@60);
    }];
    
    UIButton *calendarBtn = [[UIButton alloc] init];
    [calendarBtn setImage:[UIImage imageNamed:@"across_chain_calendar"] forState:UIControlStateNormal];
    [calendarBtn addTarget:self action:@selector(calendarBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [backView addSubview:calendarBtn];
    [calendarBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(backView).offset(0);
        make.trailing.equalTo(backView).offset(-15);
    }];
    
    UILabel *periodLabel = [[UILabel alloc] init];
    periodLabel.font = [UIFont systemFontOfSize:16];
    periodLabel.textColor = COLOR_LABEL_DESCRIPTION;
    periodLabel.textAlignment = NSTextAlignmentLeft;
    periodLabel.text = @"本周";
    [backView addSubview:periodLabel];
    [periodLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backView).offset(5);
        make.leading.equalTo(backView).offset(15);
    }];
    _periodLabel = periodLabel;
    
    UILabel *totalPublicLabel = [[UILabel alloc] init];
    totalPublicLabel.font = [UIFont systemFontOfSize:15];
    totalPublicLabel.textColor = COLOR_LABEL_DESCRIPTION;
    totalPublicLabel.textAlignment = NSTextAlignmentLeft;
    [backView addSubview:totalPublicLabel];
    [totalPublicLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(periodLabel.mas_bottom).offset(8);
        make.leading.equalTo(periodLabel).offset(0);
    }];
    _toPublicTotalLabel = totalPublicLabel;
    
    UILabel *totalPrivateLabel = [[UILabel alloc] init];
    totalPrivateLabel.font = [UIFont systemFontOfSize:15];
    totalPrivateLabel.textColor = COLOR_LABEL_DESCRIPTION;
    totalPrivateLabel.textAlignment = NSTextAlignmentLeft;
    [backView addSubview:totalPrivateLabel];
    [totalPrivateLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(totalPublicLabel).offset(0);
        make.leading.equalTo(totalPublicLabel.mas_trailing).offset(10);
    }];
    _toPrivateTotalLabel = totalPrivateLabel;
    
    _tableView = [[UITableView alloc] init];
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 90;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backView.mas_bottom).offset(0);
        make.leading.bottom.trailing.equalTo(self.view);
    }];
    
    _refreshHeader = [MJRefreshNormalHeader headerWithRefreshingTarget:self refreshingAction:@selector(createGetRecordRequest)];
    _tableView.mj_header = _refreshHeader;
    _refreshHeader.lastUpdatedTimeLabel.hidden = YES;
    
    if (@available(iOS 11.0, *)) {
        _tableView.contentInsetAdjustmentBehavior= UIScrollViewContentInsetAdjustmentNever;
    }
    else {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.datasArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellID = @"cellID";
    WeXDccAcrossChainRecordCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[WeXDccAcrossChainRecordCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellID];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    WeXWalletGetAcrossChainTransferRecordResponseModal_item *itemModel = self.datasArray[indexPath.row];
    cell.model = itemModel;
    return cell;
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXWalletGetAcrossChainTransferRecordResponseModal_item *itemModel = self.datasArray[indexPath.row];
    WeXDccAcrossChainRecordDetailController *ctrl = [[WeXDccAcrossChainRecordDetailController alloc] init];
    ctrl.recordId = itemModel.recordId;
    [self.navigationController pushViewController:ctrl animated:YES];
    
}



@end
