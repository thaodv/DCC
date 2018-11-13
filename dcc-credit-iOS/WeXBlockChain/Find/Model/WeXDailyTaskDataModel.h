//
//  WeXDailyTaskDataModel.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WeXTaskListAdapter.h"
#import "WeXTaskSignPeriodAdapter.h"
#import "WeXTaskTodaySignStatusAdapter.h"
#import "WeXTaskSignRequestAdapter.h"
#import "WeXTaskGetSunBalanceAdapter.h"
#import "WeXIpfsKeyGetAdapter.h"

@class WeXDailyTaskItemModel;
@class WeXDailyTaskSectionItemModel;


typedef NS_ENUM(NSInteger,DailyTaskRequestType) {
    DailyTaskRequestIPFS = 0, // IPFS请求 (开通云存储)
    DailyTaskRequestAuthenNameID = 1, // 实名认证
    DailyTaskRequestAuthenBankID = 2, //银行卡认证
    DailyTaskRequestAuthenCarrier= 3, //运营商认证
    DailyTaskRequestAuthnSameCow = 4, //同牛认证
    DailyTaskRequestBackUpNameID = 5, //备份实名
    DailyTaskRequestBackUpBankID = 6 , //备份银行卡
    DailyTaskRequestBackUpCarrier= 7 , //备份运营商
    DailyTaskRequestBackUpSameCow= 8 , //备份同牛
    DailyTaskRequestBackUpWallet = 9 , //备份钱包
};

/**
 刷新TableView回调

 @param reloadAll 刷新整个TableView
 @param reloadSectionOne 刷新TableView SectionOne
 */
typedef void (^RefreshBlock)(BOOL reloadAll,BOOL reloadSectionOne);


/**
 刷新阳光值相关
 */
typedef void (^RefreshSunValueBlock)(void);


/**
 签到结果回调
 */
typedef void (^SignResultBlock)(void);


/**
 请求某个URL 是否成功

 @param isSucc 是否成功
 */
typedef void (^RequestResult)(BOOL isSucc);


@interface WeXDailyTaskDataModel : NSObject

@property (nonatomic,copy) SignResultBlock SignResult;


/**
 默认初始化刷新

 @param refresh 回调
 @return 实例对象
 */
- (instancetype)initWithRefreshBlock:(RefreshBlock)refresh;


/**
 下拉刷新

 @param refresh 回调
 */
- (void)refreshAllDataBlock:(RefreshBlock)refresh;


/**
 只需要获得阳光值回调

 @param refresh 回调
 @return 实例
 */
- (instancetype)initWithRefreshSunValueBlock:(RefreshSunValueBlock)refresh;

- (NSInteger)numberOfSections;

- (NSInteger)numOfRowsInSection:(NSInteger)section;

- (WeXTaskListItemListModel *)sectionModelInSection:(NSInteger)section;

- (WeXTaskListItemModel *)rowModelOfIndexPath:(NSIndexPath *)indexPath;

/**
 请求获得阳光值信息

 @param refresh 回调
 */
- (void)requestSunValueWithResult:(RefreshSunValueBlock)refresh;


/**
 SectionOne 的数据

 @return model
 */
- (WeXTaskSignResModel *)sectionOneModel;


/**
 今天签到的数据Model

 @return Model
 */
- (WeXTaskSignResListModel *)todaySignStatusModel;


/**
 获取阳光值信息Model

 @return 阳光值Model
 */
- (WeXTaskGetSunBalanceResModel *)sunBalanceModel;




/**
 请求签到的数据结果

 @return Model
 */
- (WeXTaskSignResListModel *)requestSignResultModel;


/**
 签到接口
 */
- (void)requestSignRequestAdapter;




/**
 对于某些认证之前需要对比链上信息的请求

 @param type 请求类型
 @param controller 对应的Controller
 @param result 回调
 */
- (void)requestDataAdapterWithType:(DailyTaskRequestType)type
                        controller:(UIViewController *)controller
                            result:(RequestResult)result;



@end


