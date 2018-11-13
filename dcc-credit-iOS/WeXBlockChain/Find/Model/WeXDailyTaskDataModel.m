
//
//  WeXDailyTaskDataModel.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDailyTaskDataModel.h"
#import "WeXIpfsContractHeader.h"
#import "WeXCheckRSAPublickKeyManager.h"


@interface WeXDailyTaskDataModel () <WexBaseNetAdapterDelegate>


@property (nonatomic, strong) NSMutableArray <WeXTaskListItemListModel *>*secionsModelArray;
@property (nonatomic, copy) RefreshBlock freshBlock;
@property (nonatomic,copy) RequestResult requestResult;


//阳光值回调刷新
@property (nonatomic,copy) RefreshSunValueBlock refreshSunBlock;

//查询任务列表
@property (nonatomic, strong) WeXTaskListAdapter *listAdapter;

//查询签到周期
@property (nonatomic, strong) WeXTaskSignResModel *resModel;
@property (nonatomic, strong) WeXTaskSignPeriodAdapter *signPeriodAdapter;

//查询今天签到状态
@property (nonatomic, strong) WeXTaskSignResListModel *todaySignResModel;
@property (nonatomic, strong) WeXTaskTodaySignStatusAdapter *todaySignAdapter;

//签到请求
@property (nonatomic, strong) WeXTaskSignResListModel *signRequestResModel;
@property (nonatomic, strong) WeXTaskSignRequestAdapter *signRequestAdapter;

//阳光值相关
@property (nonatomic, strong) WeXTaskGetSunBalanceResModel *balanceResModel;
@property (nonatomic, strong) WeXTaskGetSunBalanceAdapter *balanceAdapter;

//IPFS云存储前的请求
@property (nonatomic, strong) WeXIpfsKeyGetAdapter *getIpfsKeyAdapter ;


@end

static NSString * const kDefaultValue = @"0000000000000000000000000000000000000000000000000000000000000000";

@implementation WeXDailyTaskDataModel

/**
 默认初始化刷新
 
 @param refresh 回调
 @return 实例对象
 */
- (instancetype)initWithRefreshBlock:(RefreshBlock)refresh {
    if (self = [super init]) {
        self.freshBlock = refresh;
        _secionsModelArray = [NSMutableArray new];
//      查询任务列表
        [self requestTaskListAdapter];
//      查询签到周期
        [self requestSignPeriodAdapter];
//      查询今日签到状态
        [self requestTodaySignStatusAdapter];
//      查询阳光值
        [self requestBalanceAdapter];
    }
    return self;
}
/**
 下拉刷新
 
 @param refresh 回调
 */
- (void)refreshAllDataBlock:(RefreshBlock)refresh {
    self.freshBlock    = refresh;
    //      查询任务列表
    [self requestTaskListAdapter];
    //      查询签到周期
    [self requestSignPeriodAdapter];
    //      查询今日签到状态
    [self requestTodaySignStatusAdapter];
    //      查询阳光值
    [self requestBalanceAdapter];
}

/**
 只需要获得阳光值回调
 
 @param refresh 回调
 @return 实例
 */
- (instancetype)initWithRefreshSunValueBlock:(RefreshSunValueBlock)refresh {
    if (self = [super init]) {
        self.refreshSunBlock = refresh;
        [self requestBalanceAdapter];
    }
    return self;
}

/**
 请求获得阳光值信息
 
 @param refresh 回调
 */
- (void)requestSunValueWithResult:(RefreshSunValueBlock)refresh {
    self.refreshSunBlock = refresh;
    [self requestBalanceAdapter];
}

- (void)requestTaskListAdapter {
    if (!_listAdapter) {
        _listAdapter = [WeXTaskListAdapter new];
    }
    [_listAdapter setDelegate:self];
    [_listAdapter run:nil];
}

- (void)requestSignPeriodAdapter {
    if (!_signPeriodAdapter) {
        _signPeriodAdapter = [WeXTaskSignPeriodAdapter new];
    }
    [_signPeriodAdapter setDelegate:self];
    [_signPeriodAdapter run:nil];
}

- (void)requestTodaySignStatusAdapter {
    if (!_todaySignAdapter) {
        _todaySignAdapter = [WeXTaskTodaySignStatusAdapter new];
    }
    [_todaySignAdapter setDelegate:self];
    [_todaySignAdapter run:nil];
}

// MARK: - 签到
- (void)requestSignRequestAdapter {
    if (!_signRequestAdapter) {
        _signRequestAdapter = [WeXTaskSignRequestAdapter new];
    }
    [_signRequestAdapter setDelegate:self];
    [_signRequestAdapter run:nil];
}

// MARK: - 获取阳光值
- (void)requestBalanceAdapter {
    if (!_balanceAdapter) {
        _balanceAdapter = [WeXTaskGetSunBalanceAdapter new];
    }
    [_balanceAdapter setDelegate:self];
    [_balanceAdapter run:nil];
}


- (NSInteger)numberOfSections {
    return [_secionsModelArray count];
}

- (NSInteger)numOfRowsInSection:(NSInteger)section {
    return [[_secionsModelArray[section] taskList] count];
}

- ( WeXTaskListItemListModel *)sectionModelInSection:(NSInteger)section {
    return _secionsModelArray[section];
}

- (WeXTaskListItemModel *)rowModelOfIndexPath:(NSIndexPath *)indexPath {
    WeXTaskListItemListModel *sectionModels = _secionsModelArray[indexPath.section];
    return sectionModels.taskList[indexPath.row];
}

- (WeXTaskSignResModel *)sectionOneModel {
    return self.resModel;
}
- (WeXTaskSignResListModel *)todaySignStatusModel {
    return self.todaySignResModel;
}

/**
 获取阳光值信息Model
 
 @return 阳光值Model
 */
- (WeXTaskGetSunBalanceResModel *)sunBalanceModel {
    return self.balanceResModel;
}


/**
 请求签到的数据结果
 
 @return Model
 */
- (WeXTaskSignResListModel *)requestSignResultModel {
    return self.todaySignResModel;
}

/**
 开通IPFS 存储前的请求
 
 @param result 回调
 */
- (void)requestIPFSKeyDataAdapterWithResult:(RequestResult)result {
    self.requestResult = result;
    _getIpfsKeyAdapter = [[WeXIpfsKeyGetAdapter alloc] init];
    _getIpfsKeyAdapter.delegate = self;
    [_getIpfsKeyAdapter run:nil];
}

// MARK: - 实名认证之前的数据检查
- (void)requestAuthenNameDataWithController:(UIViewController *)controller result:(RequestResult)result {
    WeXCheckRSAPublickKeyManager *checkManager = [WeXCheckRSAPublickKeyManager shareManager];
    [checkManager createCheckPublickKeyRequestWithParentController:controller responseBlock:^(BOOL isResult) {
        !result ? : result (isResult);
    }];
}




/**
 对于某些认证之前需要对比链上信息的请求
 
 @param type 请求类型
 @param controller 对应的Controller
 @param result 回调
 */
- (void)requestDataAdapterWithType:(DailyTaskRequestType)type
                        controller:(UIViewController *)controller
                            result:(RequestResult)result {
    switch (type) {
        case DailyTaskRequestIPFS: {
            [self requestIPFSKeyDataAdapterWithResult:result];
        }
            break;
        case DailyTaskRequestAuthenNameID: {
            [self requestAuthenNameDataWithController:controller result:result];
        }
            break;
        case DailyTaskRequestAuthenBankID: {
            [self requestAuthenNameDataWithController:controller result:result];
        }
            break;
        case DailyTaskRequestAuthenCarrier: {
            [self requestAuthenNameDataWithController:controller result:result];
        }
            break;
        case DailyTaskRequestAuthnSameCow: {
             [self requestAuthenNameDataWithController:controller result:result];
        }
            break;
        default:
            break;
    }
}



- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter
                             head:(WexBaseNetAdapterResponseHeadModal *)headModel
                         response:(WeXBaseNetModal *)response {
    if (adapter == _listAdapter) {
        if ([headModel.systemCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
            WeXTaskListResModel *resModel =  (WeXTaskListResModel *)response;
            _secionsModelArray = [[NSMutableArray alloc] initWithArray:resModel.result];
        }
        !self.freshBlock ? : self.freshBlock(true,false);
    }
    
    else if (adapter == _signPeriodAdapter) {
        if ([headModel.systemCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
            _resModel = (WeXTaskSignResModel *)response;
        }
        !self.freshBlock ? : self.freshBlock(false,true);
    }
    else if (adapter == _todaySignAdapter) {
        if ([headModel.systemCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
            WeXTaskSignResListModel *tempModel = (WeXTaskSignResListModel *)response;
            if ([tempModel.idNum length] > 0) {
                _todaySignResModel = tempModel;
            }
        }
        !self.freshBlock ? : self.freshBlock(false,true);
    }
    else if (adapter == _signRequestAdapter) {
        if ([headModel.systemCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
            _signRequestResModel = (WeXTaskSignResListModel *)response;
//          更新今日是否签到
            _todaySignResModel = _signRequestResModel;
//          更新签到之后,已经签到的天数
            NSMutableArray <WeXTaskSignResListModel *> *periodArray = [NSMutableArray arrayWithArray:[[self sectionOneModel] result]];
            [periodArray addObject:_todaySignResModel];
            [self.resModel setResult:[periodArray copy]];
//          更新签到之后的阳光值
            NSInteger newBalance = [_balanceResModel.balance integerValue] + [_signRequestResModel.incrementValue integerValue];
            self.balanceResModel.balance = [@(newBalance) stringValue];
        }
        !self.SignResult ? : self.SignResult();
    }
    else if (adapter == _balanceAdapter) {
        if ([headModel.systemCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
            WeXTaskGetSunBalanceResModel *tempModel = (WeXTaskGetSunBalanceResModel *)response;
            self.balanceResModel = tempModel;
        }
        !self.freshBlock ? : self.freshBlock(false,true);
        !self.refreshSunBlock ? : self.refreshSunBlock();
    }
    else if (adapter == _getIpfsKeyAdapter) {
        if ([headModel.systemCode isEqualToString:WexSuccess] && [headModel.businessCode isEqualToString:WexSuccess]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            NSString *contractAddress = model.result;
            if(contractAddress.length > 0){
                [self getRawTranstion:contractAddress];
            }
        } else {
            !self.requestResult ? : self.requestResult(false);
        }
    }
}


- (void)getRawTranstion:(NSString *)contractAddress {
    // 合约定义说明
    NSString* abiJson = WEX_IPFS_ABI_GET_KEY;
    // 合约参数值 第一个参数为version 暂时为1
    NSString* abiParamsValues = @"[]";
    WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
    // 合约地址
    NSString* abiAddress = contractAddress;
    NSString *addressStr = [WexCommonFunc getFromAddress];
    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
         if(response!=nil) {
             NSError* error=response;
             NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
             return;
         }
         [[WXPassHelper instance] initIpfsProvider:WEX_IPFS_KEYHASH_URL responseBlock:^(id response) {
             [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:abiParamsValues responseBlock:^(id response) {
                 [[WXPassHelper instance] call4IpfsContractAddress:abiAddress data:response walletAddress:addressStr responseBlock:^(id response) {
                     NSString *str = response[@"result"];
                     if (str.length > 0) {
                         if([str isEqualToString:@"0x"]){
                             model.ipfsCheckKey = nil;
                             model.twoipfsCheckKey = nil;
                             [WexCommonFunc savePassport:model];
                             [self goToIpfsVc];
                             return ;
                         }
                         
                         [[WXPassHelper instance]getIpfsEncodeWithKeyHashString:str ResponseBlock:^(id response) {
                             WEXNSLOG(@"response = %@",response);
                             NSDictionary *dict = response;
                             WEXNSLOG(@"dict = %@",dict);
                             if (!dict) {
                                 return;
                             }
                             NSString *keyHashStr = dict[@"1"];
                             NSArray *array = [keyHashStr componentsSeparatedByString:@"0x"]; //字符串按照分隔成数组
                             WEXNSLOG(@"array=%@",array); //结果是
                             if (array.count>1) {
                                 NSString *pwdStr = array[1];
                                 WEXNSLOG(@"pwdStr = %@",pwdStr);
                                 if ([pwdStr isEqualToString:kDefaultValue] || [str isEqualToString:@"0x"]) {
                                     model.twoipfsCheckKey = nil;
                                     [WexCommonFunc savePassport:model];
                                     [self goToIpfsVc];
                                 }else{
                                     NSString *passWord = model.ipfsCheckKey;
                                     //处理另一个设备重置密码以后并且输入了新密码,这台设备密码也要删除重置
                                     if (passWord.length>0 && ![passWord isEqualToString:pwdStr]) {
                                         model.ipfsCheckKey = nil;
                                         [WexCommonFunc savePassport:model];
                                         return ;
                                     }
                                     model.twoipfsCheckKey = pwdStr;
                                     [WexCommonFunc savePassport:model];
                                     [self goToIpfsVc];
                                 }
                             }
                         }];
                     }
                 }];
             }];
         }];
     }];
}

- (void)goToIpfsVc{
    !self.requestResult ? : self.requestResult (true);
}













//- (void)configureStaticData {
//    for (int i =0 ; i < 4; i ++) {
//        WeXDailyTaskSectionItemModel *sectionModel = [WeXDailyTaskSectionItemModel new];
//        switch (i ) {
//            case 0: {
//                sectionModel.sectionTitle   = @"日常任务";
//                sectionModel.sectionSubTitle = @"次数无上限 奖励无上限";
//                sectionModel.rows = [self configureSectionOneData];
//            }
//                break;
//            case 1: {
//                sectionModel.sectionTitle   = @"新手任务";
//                sectionModel.sectionSubTitle = @"动动手指 轻松获得阳光";
//                sectionModel.rows = [self configureSectionTwoData];
//            }
//                break;
//            case 2: {
//                sectionModel.sectionTitle   = @"认证任务";
//                sectionModel.sectionSubTitle = @"需下载APP完成认证";
//                sectionModel.rows = [self configureSectionThreeData];
//            }
//                break;
//            case 3: {
//                sectionModel.sectionTitle   = @"备份任务";
//                sectionModel.sectionSubTitle = @"需下载APP完成备份任务";
//                sectionModel.rows = [self configureSectionFourData];
//            }
//                break;
//
//            default:
//                break;
//        }
//        [_secionsModelArray addObject:sectionModel];
//    }
//}


//- (NSArray  <WeXDailyTaskItemModel *> *)configureSectionOneData {
//    NSMutableArray *tempArray = [NSMutableArray new];
//    for (int i =0 ; i <6; i ++) {
//        WeXDailyTaskItemModel *itemModel = [WeXDailyTaskItemModel new];
//        switch (i) {
//            case 0: {
//                itemModel.imageName = @"garden_sign";
//                itemModel.title = @"每日签到";
//                itemModel.subTitle = @"+1~+7阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//            case 1: {
//                itemModel.imageName = @"garden_friend";
//                itemModel.title = @"邀请好友助力";
//                itemModel.subTitle = @"+10 阳光/人";
//                itemModel.type = WeXDailyTaskItemCellWithArrow;
//            }
//                break;
//            case 2: {
//                itemModel.imageName = @"garden_seller";
//                itemModel.title = @"商户接受订单";
//                itemModel.subTitle = @"+1 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//            case 3: {
//                itemModel.imageName = @"garden_success";
//                itemModel.title = @"放币成功";
//                itemModel.subTitle = @"+20 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//            case 4: {
//                itemModel.imageName = @"garden_repay";
//                itemModel.title = @"还币成功";
//                itemModel.subTitle = @"+40 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//
//            default: {
//                itemModel.imageName = @"garden_wallet";
//                itemModel.title = @"邀请好友创建钱包";
//                itemModel.subTitle = @"+10 阳光";
//                itemModel.type = WeXDailyTaskItemCellWithArrow;
//            }
//                break;
//        }
//        [tempArray addObject:itemModel];
//    }
//    return [tempArray copy];
//}
//
//- (NSArray  <WeXDailyTaskItemModel *> *)configureSectionTwoData {
//    NSMutableArray *tempArray = [NSMutableArray new];
//    for (int i =0 ; i <3; i ++) {
//        WeXDailyTaskItemModel *itemModel = [WeXDailyTaskItemModel new];
//        switch (i) {
//            case 0: {
//                itemModel.imageName = @"garden_wechat";
//                itemModel.title = @"绑定微信";
//                itemModel.subTitle = @"已完成";
//                itemModel.type = WeXDailyTaskItemCellWithGrren;
//            }
//                break;
//            case 1: {
//                itemModel.imageName = @"garden_create";
//                itemModel.title = @"创建钱包";
//                itemModel.subTitle = @"已完成";
//                itemModel.type = WeXDailyTaskItemCellWithGrren;
//            }
//                break;
//            case 2: {
//                itemModel.imageName = @"garden_cloud";
//                itemModel.title = @"开通云存储";
//                itemModel.subTitle = @"+1 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//        }
//        [tempArray addObject:itemModel];
//    }
//    return [tempArray copy];
//}
//
//- (NSArray  <WeXDailyTaskItemModel *> *)configureSectionThreeData {
//    NSMutableArray *tempArray = [NSMutableArray new];
//    for (int i =0 ; i <4; i ++) {
//        WeXDailyTaskItemModel *itemModel = [WeXDailyTaskItemModel new];
//        switch (i) {
//            case 0: {
//                itemModel.imageName = @"garden_name";
//                itemModel.title = @"实名认证";
//                itemModel.subTitle = @"+10 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//            case 1: {
//                itemModel.imageName = @"garden_bankID";
//                itemModel.title = @"银行卡认证";
//                itemModel.subTitle = @"+10 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//            case 2: {
//                itemModel.imageName = @"garden_operator";
//                itemModel.title = @"运营商认证";
//                itemModel.subTitle = @"+10 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//            case 3: {
//                itemModel.imageName = @"garden_samecow";
//                itemModel.title = @"同牛运营商认证";
//                itemModel.subTitle = @"+10 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//        }
//        [tempArray addObject:itemModel];
//    }
//    return [tempArray copy];
//}
//
//- (NSArray  <WeXDailyTaskItemModel *> *)configureSectionFourData {
//    NSMutableArray *tempArray = [NSMutableArray new];
//    for (int i =0 ; i <4; i ++) {
//        WeXDailyTaskItemModel *itemModel = [WeXDailyTaskItemModel new];
//        switch (i) {
//            case 0: {
//                itemModel.imageName = @"garden_name_backup";
//                itemModel.title = @"备份实名认证数据";
//                itemModel.subTitle = @"+10 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//            case 1: {
//                itemModel.imageName = @"garden_bankID_backup";
//                itemModel.title = @"备份银行卡认证数据";
//                itemModel.subTitle = @"+10 阳光/人";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//            case 2: {
//                itemModel.imageName = @"garden_operator_backup";
//                itemModel.title = @"备份运行商认证数据";
//                itemModel.subTitle = @"+10 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//            case 3: {
//                itemModel.imageName = @"garden_samecow_backup";
//                itemModel.title = @"备份同牛认证数据";
//                itemModel.subTitle = @"+10 阳光";
//                itemModel.type = WeXDailyTaskItemCellDefault;
//            }
//                break;
//        }
//        [tempArray addObject:itemModel];
//    }
//    return [tempArray copy];
//}



@end
