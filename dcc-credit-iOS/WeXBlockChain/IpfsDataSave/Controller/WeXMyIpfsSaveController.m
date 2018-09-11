//
//  WeXMyIpfsSaveController.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/9.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyIpfsSaveController.h"
#import "WeXMyIpfsSaveCell.h"
#import "WeXMyIpfsSaveModel.h"
#import "WeXReSetIpfsPwdController.h"
#import "AIIpfsSaveDataHelpObject.h"
#import "WeXIpfsKeyGetAdapter.h"
#import "WeXIpfsContractHeader.h"
#import "WeXIpfsHashGetAdapter.h"
#import "WeXIpfsKeyGetModel.h"
#import "WeXCreditDccIDCacheModal.h"
#import "WeXCreditDccGetContractAddressAdapter.h"
#import "WeXBorrowGetNonceAdapter.h"
#import "WeXGetReceiptResult2Adapter.h"
#import "WeXGetReceiptResult2Modal.h"
#import "WeXCreditDccGetOrderDataAdapter.h"
#import "WeXIpfsTokenModel.h"
#import "WeXIpfsHelper.h"
#import "WeXMyIpfsAddressController.h"
#import "WeXChooseIpfsNodeController.h"
#import "WeXCardSettingViewController.h"
#import "AIIpfsHeader.h"

static NSString *const kMyIpfsSaveIdentifier = @"WeXMyIpfsSaveCellID";

@interface WeXMyIpfsSaveController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) UIButton *synchBtn;
@property (nonatomic,strong) UIButton *changePwdBtn;
@property (nonatomic,strong) NSMutableArray *dataArray;
@property (nonatomic,strong) NSMutableArray *selectDataArray;

@property (nonatomic,strong) UIView *backView;
@property (nonatomic,strong) UIView *tipView;
@property (nonatomic,strong)dispatch_group_t group;

@property (nonatomic,copy)NSString *identifyHash;
@property (nonatomic,copy)NSString *bankCardHash;
@property (nonatomic,copy)NSString *phoneOperatorHash;
//需要用到的4个合约地址
@property (nonatomic,copy)NSString *contractAddress;
@property (nonatomic,copy)NSString *identifyContractAddress;
@property (nonatomic,copy)NSString *bankCardContractAddress;
@property (nonatomic,copy)NSString *phoneContractAddress;

//@property (nonatomic,copy)NSString *rawTransaction;
@property (nonatomic,strong) WeXIpfsHashGetAdapter *getIpfsHashGetAdapter;
@property (nonatomic,strong) WeXCreditDccGetContractAddressAdapter *getIDContractAddressAdapter;
@property (nonatomic,strong) WeXCreditDccGetContractAddressAdapter *getBankContractAddressAdapter;
@property (nonatomic,strong) WeXCreditDccGetContractAddressAdapter *getPhoneContractAddressAdapter;
@property (nonatomic,strong) WeXBorrowGetNonceAdapter *getNonceAdapter;
//ipfs上传参数
@property (nonatomic,copy)NSString *identifyTokenDigest1;
@property (nonatomic,copy)NSString *identifyTokenDigest2;
@property (nonatomic,copy)NSString *bankCardTokenDigest1;
@property (nonatomic,copy)NSString *bankCardTokenDigest2;
@property (nonatomic,copy)NSString *phoneOperatorTokenDigest1;
@property (nonatomic,copy)NSString *phoneOperatorTokenDigest2;

//链上查询参数
@property (nonatomic,copy)NSString *identifyDigest1;
@property (nonatomic,copy)NSString *identifyDigest2;
//@property (nonatomic,copy)NSString *identifyExpired;
@property (nonatomic,copy)NSString *bankCardDigest1;
@property (nonatomic,copy)NSString *bankCardDigest2;
@property (nonatomic,copy)NSString *bankCardExpired;
@property (nonatomic,copy)NSString *phoneOperatorDigest1;
@property (nonatomic,copy)NSString *phoneOperatorDigest2;
@property (nonatomic,copy)NSString *phoneOperatorExpired;

@property (nonatomic,copy)NSString *idendifyNonce;
@property (nonatomic,copy)NSString *bankCardNonce;
@property (nonatomic,copy)NSString *phoneNonce;

@property (nonatomic, assign) NSInteger identifyRequestCount;
@property (nonatomic, assign) NSInteger bankRequestCount;
@property (nonatomic, assign) NSInteger phoneRequestCount;
@property (nonatomic,copy)NSString *identifyTxHash;
@property (nonatomic,copy)NSString *bankTxHash;
@property (nonatomic,copy)NSString *phoneTxHash;

//查询链上结果是否上传成功
@property (nonatomic,strong)WeXGetReceiptResult2Adapter *getIdnetifyReceiptAdapter;
@property (nonatomic,strong)WeXGetReceiptResult2Adapter *getBankReceiptAdapter;
@property (nonatomic,strong)WeXGetReceiptResult2Adapter *getPhoneReceiptAdapter;
@property (nonatomic,copy)NSString *txHash;
//查询token合约Adapter
@property (nonatomic,strong)WeXCreditDccGetOrderDataAdapter *getIdentifyOrderDataAdapter;
@property (nonatomic,strong)WeXCreditDccGetOrderDataAdapter *getBankCardOrderDataAdapter;
@property (nonatomic,strong)WeXCreditDccGetOrderDataAdapter *getPhoneOrderDataAdapter;
//查询token合约Model
@property (nonatomic,strong)WeXIpfsTokenModel *identifyTokenModel;
@property (nonatomic,strong)WeXIpfsTokenModel *bankCardTokenModel;
@property (nonatomic,strong)WeXIpfsTokenModel *phoneTokenModel;
//合约的读写BOOL
@property (nonatomic,assign)BOOL ipfsTokenWriteIdentify;
@property (nonatomic,assign)BOOL ipfsTokenWriteBank;
@property (nonatomic,assign)BOOL ipfsTokenWritePhone;
//异步请求队列
@property (nonatomic,strong)dispatch_group_t getAllContractGroup;
@property (nonatomic,strong)dispatch_group_t getAllCreditGroup;
@property (nonatomic,strong)dispatch_group_t identifyCompareLocalGroup;
@property (nonatomic,strong)dispatch_group_t identifyCompareIpfsGroup;
@property (nonatomic,strong)dispatch_group_t bankCardCompareLocalGroup;
@property (nonatomic,strong)dispatch_group_t bankCardCompareIpfsGroup;
@property (nonatomic,strong)dispatch_group_t phoneCompareLocalGroup;
@property (nonatomic,strong)dispatch_group_t phoneCompareIpfsGroup;

@property (nonatomic,strong)dispatch_group_t identifyDownLoadGroup;
@property (nonatomic,strong)dispatch_group_t bankCardDownLoadGroup;
@property (nonatomic,strong)dispatch_group_t phoneDownLoadGroup;
@property (nonatomic,strong)dispatch_group_t surnAllGroup;

//加密版本号(用于加密算法升级时给用户提示)
@property (nonatomic,copy)NSString *versionStr;

@end

@implementation WeXMyIpfsSaveController

- (void)viewDidLoad {
    [super viewDidLoad];

    [self setNavitem];
    [self setDefalutMainNone];
    [self setupSubViews];
    [self reSetDisplayData];
    
   // Do any additional setup after loading the view.
}

-(void)setNavitem{

    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"retreat2"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(backItemClick)];
    
    self.navigationItem.leftBarButtonItem = item;
    //导航右边的按钮
    UIButton *addClickBtn = [[UIButton alloc]initWithFrame:CGRectMake(40, 5, 35, 30)];
    [addClickBtn setImage:[UIImage imageNamed:@"checklist"] forState:UIControlStateNormal];
    [addClickBtn setImage:[UIImage imageNamed:@"checklist"] forState:UIControlStateSelected];
    [addClickBtn addTarget:self action:@selector(goChooseNodeVc) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithCustomView:addClickBtn];
    self.navigationItem.rightBarButtonItem = rightButton;
    
}

-(void)goChooseNodeVc{
    WeXChooseIpfsNodeController *vc = [[WeXChooseIpfsNodeController alloc]init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)backItemClick{
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *passWord = [user objectForKey:WEX_IPFS_MY_KEY];

    //处理多个入口返回不同界面的逻辑
    if (passWord && _iSFromSettingVc) {
        for (UIViewController *controller in self.navigationController.viewControllers) {
            if ([controller isKindOfClass:[self.fromVc class]]) {
                [self.navigationController popToViewController:self.fromVc animated:YES];
            }
        }
    }else{
        [self.navigationController popViewControllerAnimated:YES];
    }
}

//20180909新增选择ipfs节点的功能,需要设置默认节点
- (void)setDefalutMainNone{
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *mainUrlStr = [user objectForKey:WEX_IPFS_MAIN_NONEURL];
    NSString *defaultUrlStr = [user objectForKey:WEX_IPFS_DEFAULT_NONEURL];
    //判断本地已经有主节点则不存值
    if ([WeXIpfsHelper isBlankString:mainUrlStr]) {
      [user setObject:kBaseUrl forKey:WEX_IPFS_MAIN_NONEURL];
    }else{}
    if ([WeXIpfsHelper isBlankString:defaultUrlStr]) {
        [user setObject:kBaseUrl forKey:WEX_IPFS_DEFAULT_NONEURL];
    }else{}
    
}

#pragma mark -- 数据初始化
- (void)reSetDisplayData{
    //获取4个合约地址
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    //声明版本号
    _versionStr = @"1";
    
    _getAllContractGroup = dispatch_group_create();
    dispatch_group_enter(_getAllContractGroup);
    [self createGetIpfsHashRequest];
    dispatch_group_enter(_getAllContractGroup);
    [self createGetIdentifyContractAddressRequest];
    dispatch_group_enter(_getAllContractGroup);
    [self createGetBankCardContractAddressRequest];
    dispatch_group_enter(_getAllContractGroup);
    [self createGetPhoneOptaContractAddressRequest];
    dispatch_group_notify(_getAllContractGroup, dispatch_get_main_queue(), ^{
        WEXNSLOG(@"刷新界面");
        WEXNSLOG(@"_contractAddress = %@",_contractAddress);
        WEXNSLOG(@"_identifyContractAddress = %@",_identifyContractAddress);
        WEXNSLOG(@"_phoneContractAddress = %@",_phoneContractAddress);
        WEXNSLOG(@"_bankCardContractAddress = %@",_bankCardContractAddress);
        _getAllContractGroup = nil;
        if ([WeXIpfsHelper isBlankString:_contractAddress] || [WeXIpfsHelper isBlankString:_identifyContractAddress] || [WeXIpfsHelper isBlankString:_bankCardContractAddress] || [WeXIpfsHelper isBlankString:_phoneContractAddress]) {
            [WeXPorgressHUD hideLoading];
            return ;
        }
        
//      [self testSetData];
        _getAllCreditGroup =  dispatch_group_create();
        dispatch_group_enter(_getAllCreditGroup);
        [self identifyDataInitialization];
        dispatch_group_enter(_getAllCreditGroup);
        [self bankCardDataInitialization];
        dispatch_group_enter(_getAllCreditGroup);
        [self phoneOperatorDataInitialization];
        dispatch_group_notify(_getAllCreditGroup, dispatch_get_main_queue(), ^{
             WEXNSLOG(@"刷新界面");
             WEXNSLOG(@"_dataArray = %@",_dataArray);
             [self reSortData];
//           [self.tableView reloadData];
//             [self refreshSynchBtnStatus];
//             _getAllCreditGroup = nil;
//             [WeXPorgressHUD hideLoading];
         });
    });
}
//处理并发初始化排序混乱的问题
- (void)reSortData{
    
    NSMutableArray *transitionArray = [[NSMutableArray alloc]init];

    for (WeXMyIpfsSaveModel *model in _dataArray) {
        WEXNSLOG(@"model.modeType = %ld",model.modeType);
        if (model.modeType == WeXFileIpfsModeTypeIdentify) {
            [transitionArray addObject:model];
            break;
        }
    }
    for (WeXMyIpfsSaveModel *model in _dataArray) {
        if (model.modeType == WeXFileIpfsModeTypeBankCard) {
            [transitionArray addObject:model];
            break;
        }
    }
    for (WeXMyIpfsSaveModel *model in _dataArray) {
        if (model.modeType == WeXFileIpfsModeTypePhoneOperator) {
            [transitionArray addObject:model];
            break;
        }
    }
   
    [self.dataArray removeAllObjects];
    WEXNSLOG(@"_dataArray = %@",_dataArray);
    self.dataArray = transitionArray;
    WEXNSLOG(@"_dataArray = %@",_dataArray);
    WEXNSLOG(@"transitionArray = %@",transitionArray);
    [self.tableView reloadData];
    [self refreshSynchBtnStatus];
    _getAllCreditGroup = nil;
    [WeXPorgressHUD hideLoading];
    
}

#pragma mark -- 同步按钮处理
- (void)surnBtnClick{
    
    if (_selectDataArray.count>0) {
       [WeXPorgressHUD showLoadingAddedTo:self.view];
        _surnAllGroup = dispatch_group_create();
        dispatch_group_enter(_surnAllGroup);
        [self judgeIdentifyDataClick];
        dispatch_group_enter(_surnAllGroup);
        [self judgeBankCardfyDataClick];
        dispatch_group_enter(_surnAllGroup);
        [self judgePhoneOperatorDataClick];
        
        dispatch_group_notify(_surnAllGroup, dispatch_get_main_queue(), ^{
            WEXNSLOG(@"刷新界面");
            WEXNSLOG(@"_dataArray = %@",_dataArray);
        
                _surnAllGroup = nil;
                [self updateData];
            });
            
//            [WeXPorgressHUD hideLoading];
//            _surnAllGroup = nil;
//            [self updateData];
    }
}

#pragma mark -- 同步完成后的自动刷新数据
- (void)updateData{
    
    [self.dataArray removeAllObjects];
    self.dataArray = nil;
    [self.selectDataArray removeAllObjects];
    self.selectDataArray = nil;
    _identifyTokenModel = nil;
    _bankCardTokenModel = nil;
    _phoneTokenModel = nil;
    //处理链上数据没有及时更新导致的bug,本地延时处理
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC));
    dispatch_after(time, dispatch_get_main_queue(), ^{
        NSDate *dateAfter = [NSDate date];
        NSLog(@"dateAfter---%@",dateAfter);
    [WeXPorgressHUD hideLoading];
        
    [self reSetDisplayData];
        
    });
    
}

//实名认证数据初始化
- (void)identifyDataInitialization{
    
    WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
    if (cacheModel.idAuthenStatus == WeXCreditIDAuthenStatusTypeSuccess) {
        //本地有实名认证数据
        _identifyCompareLocalGroup = dispatch_group_create();
        dispatch_group_enter(_identifyCompareLocalGroup);
        [self createGetIdentifyOrderDataRequest];//查询链上合约摘要
        dispatch_group_notify(_identifyCompareLocalGroup, dispatch_get_main_queue(), ^{
            WEXNSLOG(@"刷新界面");
            WEXNSLOG(@"_identifyDigest1 = %@",_identifyDigest1);
            WEXNSLOG(@"_identifyDigest2 = %@",_identifyDigest2);
            _identifyCompareLocalGroup  = nil;
            
            NSMutableData *data1 = [cacheModel.userName dataUsingEncoding:NSUTF8StringEncoding].mutableCopy;
            NSData *data2 = [cacheModel.userNumber dataUsingEncoding:NSUTF8StringEncoding];
            [data1 appendData:data2];
            NSData *data3 = [WexCommonFunc dataShaSHAWithData:data1];
            NSString *digest1 = [WexCommonFunc stringSHA256WithData:data1];
            NSString *digestStr1 = [NSString stringWithFormat:@"0x%@",digest1];

            NSData *frontData = [WexCommonFunc dataShaSHAWithData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_FRONT_KEY]];
            NSData *backData = [WexCommonFunc dataShaSHAWithData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_BACK_KEY]];
            NSData *headData = [WexCommonFunc dataShaSHAWithData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_HEAD_KEY]];

            NSMutableData *data5 = [NSMutableData dataWithData:data3];
            [data5 appendData:frontData];
            [data5 appendData:backData];
            [data5 appendData:headData];
            
            NSData *data4 = [WexCommonFunc dataShaSHAWithData:data5];
            NSString *digest2 = [WexCommonFunc stringSHA256WithData:data5];
            NSString *digestStr2 = [NSString stringWithFormat:@"0x%@",digest2];
            WEXNSLOG(@"digestStr1 = %@",digestStr1);
            WEXNSLOG(@"digestStr2 = %@",digestStr2);
            NSData *oneDigestData = [[NSData alloc] initWithBase64EncodedString:_identifyDigest1 options:0];
            WEXNSLOG(@"onlineDigestData=%@",oneDigestData);
            WEXNSLOG(@"data3 =%@",data3);
            NSData *twoDigestData = [[NSData alloc] initWithBase64EncodedString:_identifyDigest2 options:0];
            WEXNSLOG(@"twoDigestData=%@",twoDigestData);
            WEXNSLOG(@"data4 =%@",data4);
            
            if ([oneDigestData isEqualToData:data3] && [twoDigestData isEqualToData:data4]) {
                
                _identifyCompareIpfsGroup = dispatch_group_create();
                 dispatch_group_enter(_identifyCompareIpfsGroup);
                 [self getIdentifyRawTranstion];
           
                dispatch_group_notify(_identifyCompareIpfsGroup, dispatch_get_main_queue(), ^{
                    WEXNSLOG(@"刷新界面");
                    WEXNSLOG(@"_identifyTokenModel.digest1 = %@",_identifyTokenModel.digest1);
                    WEXNSLOG(@"_identifyTokenModel.digest2 = %@",_identifyTokenModel.digest2);
                    WEXNSLOG(@"digestStr1 = %@",digestStr1);
                    WEXNSLOG(@"digestStr2 = %@",digestStr2);
                    _identifyCompareIpfsGroup = nil;
                    
                    if ([WeXIpfsHelper isBlankString:_identifyTokenModel.digest1] &&[WeXIpfsHelper isBlankString:_identifyTokenModel.digest2] ) {
                        [self addIdentifyUploadModel:WeXFileIpfsModeTypeIdentify];
                        dispatch_group_leave(_getAllCreditGroup);
                        
                    }else if ([_identifyTokenModel.digest1 isEqualToString:digestStr1] && [_identifyTokenModel.digest2 isEqualToString:digestStr2] ) {
                        [self addIdentifyNewestModel:WeXFileIpfsModeTypeIdentify];
                        dispatch_group_leave(_getAllCreditGroup);
                        
                    }else{
                        [self addIdentifyUploadModel:WeXFileIpfsModeTypeIdentify];
                        dispatch_group_leave(_getAllCreditGroup);
                        
                    }
                });
            }else{
                
                _identifyCompareIpfsGroup = dispatch_group_create();
                dispatch_group_enter(_identifyCompareIpfsGroup);
                [self getIdentifyRawTranstion];
                
                dispatch_group_notify(_identifyCompareIpfsGroup, dispatch_get_main_queue(), ^{
                    WEXNSLOG(@"刷新界面");
                    WEXNSLOG(@"_identifyTokenModel.digest1 = %@",_identifyTokenModel.digest1);
                    WEXNSLOG(@"_identifyTokenModel.digest2 = %@",_identifyTokenModel.digest2);
                    _identifyCompareIpfsGroup = nil;
                    
                    NSData *oneDigestData = [[NSData alloc] initWithBase64EncodedString:_identifyDigest1 options:0];
                    NSLog(@"onlineDigestData=%@",oneDigestData);
                    NSString *oneStr = [oneDigestData description];
                    oneStr = [oneStr stringByReplacingOccurrencesOfString:@" " withString:@""];
                    oneStr = [oneStr stringByReplacingOccurrencesOfString:@"<" withString:@""];
                    oneStr = [oneStr stringByReplacingOccurrencesOfString:@">" withString:@""];
                    oneStr = [NSString stringWithFormat:@"0x%@",oneStr];
                    NSLog(@"oneStr = %@",oneStr);
                    NSData *twoDigestData = [[NSData alloc] initWithBase64EncodedString:_identifyDigest2 options:0];
                    NSLog(@"twoDigestData=%@",twoDigestData);
                    NSString *twoStr = [twoDigestData description];
                    twoStr = [twoStr stringByReplacingOccurrencesOfString:@" " withString:@""];
                    twoStr = [twoStr stringByReplacingOccurrencesOfString:@"<" withString:@""];
                    twoStr = [twoStr stringByReplacingOccurrencesOfString:@">" withString:@""];
                    twoStr = [NSString stringWithFormat:@"0x%@",twoStr];
                    NSLog(@"twoStr = %@",twoStr);
                    
                    if ([WeXIpfsHelper isBlankString:_identifyTokenModel.digest1] &&[WeXIpfsHelper isBlankString:_identifyTokenModel.digest2] ) {
                        
                        [self addIdentifyNoDataModel:WeXFileIpfsModeTypeIdentify];
                        dispatch_group_leave(_getAllCreditGroup);
                    }
                    
                  else  if ([oneStr isEqualToString:_identifyTokenModel.digest1] && [twoStr isEqualToString:_identifyTokenModel.digest2] ) {
                        [self addIdentifyDownloadModel:WeXFileIpfsModeTypeIdentify];
                        dispatch_group_leave(_getAllCreditGroup);
                        
                    }else{
                        [self addIdentifyNoDataModel:WeXFileIpfsModeTypeIdentify];
                         dispatch_group_leave(_getAllCreditGroup);
                    }
                });
            }
        });
        
    }else{
        //本地无实名认证数据
        _identifyCompareIpfsGroup = dispatch_group_create();
        dispatch_group_enter(_identifyCompareIpfsGroup);
        [self getIdentifyRawTranstion];
        dispatch_group_enter(_identifyCompareIpfsGroup);
        [self createGetIdentifyOrderDataRequest];
        dispatch_group_notify(_identifyCompareIpfsGroup, dispatch_get_main_queue(), ^{
            WEXNSLOG(@"刷新界面");
            WEXNSLOG(@"_identifyTokenModel.digest1 = %@",_identifyTokenModel.digest1);
            WEXNSLOG(@"_identifyTokenModel.digest2 = %@",_identifyTokenModel.digest2);
            WEXNSLOG(@"_identifyDigest1 = %@",_identifyDigest1);
            WEXNSLOG(@"_identifyDigest2 = %@",_identifyDigest2);
            _identifyCompareIpfsGroup = nil;
            
            NSData *oneDigestData = [[NSData alloc] initWithBase64EncodedString:_identifyDigest1 options:0];
            NSLog(@"onlineDigestData=%@",oneDigestData);
            NSString *oneStr = [oneDigestData description];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@" " withString:@""];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@"<" withString:@""];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@">" withString:@""];
            oneStr = [NSString stringWithFormat:@"0x%@",oneStr];
            NSLog(@"oneStr = %@",oneStr);
            NSData *twoDigestData = [[NSData alloc] initWithBase64EncodedString:_identifyDigest2 options:0];
            NSLog(@"twoDigestData=%@",twoDigestData);
            NSString *twoStr = [twoDigestData description];
            twoStr = [twoStr stringByReplacingOccurrencesOfString:@" " withString:@""];
            twoStr = [twoStr stringByReplacingOccurrencesOfString:@"<" withString:@""];
            twoStr = [twoStr stringByReplacingOccurrencesOfString:@">" withString:@""];
            twoStr = [NSString stringWithFormat:@"0x%@",twoStr];
            NSLog(@"twoStr = %@",twoStr);

            if ([WeXIpfsHelper isBlankString:_identifyTokenModel.digest1]  &&  [WeXIpfsHelper isBlankString: _identifyTokenModel.digest2] ) {
                
                [self addIdentifyNoDataModel:WeXFileIpfsModeTypeIdentify];
                dispatch_group_leave(_getAllCreditGroup);
                return ;
            }
            else if ([oneStr isEqualToString:_identifyTokenModel.digest1] && [twoStr isEqualToString:_identifyTokenModel.digest2] ) {
                [self addIdentifyDownloadModel:WeXFileIpfsModeTypeIdentify];
                dispatch_group_leave(_getAllCreditGroup);
                
            }else{
                [self addIdentifyNoDataModel:WeXFileIpfsModeTypeIdentify];
                dispatch_group_leave(_getAllCreditGroup);
            }
        });
    }
    
}

//银行卡认证数据初始化
- (void)bankCardDataInitialization{
    
    WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
    if (cacheModel.bankAuthenStatus == WeXCreditBankAuthenStatusTypeSuccess) {
        //本地有银行卡认证数据
        _bankCardCompareLocalGroup = dispatch_group_create();
        dispatch_group_enter(_bankCardCompareLocalGroup);
        [self createGetBankOrderDataRequest];//查询链上合约摘要
        dispatch_group_notify(_bankCardCompareLocalGroup, dispatch_get_main_queue(), ^{
            WEXNSLOG(@"刷新界面");
            WEXNSLOG(@"_bankCardDigest1 = %@",_bankCardDigest1);
            WEXNSLOG(@"_bankCardDigest2 = %@",_bankCardDigest2);
            _bankCardCompareLocalGroup = nil;
            
            WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
            
            NSMutableData *data1 = [cacheModel.bankAuthenCardNumber dataUsingEncoding:NSUTF8StringEncoding].mutableCopy;
            NSData *data2 = [cacheModel.bankAuthenMobile dataUsingEncoding:NSUTF8StringEncoding];
            [data1 appendData:data2];
            
            NSData *data3 = [WexCommonFunc dataShaSHAWithData:data1];
            NSString *digest1 = [WexCommonFunc stringSHA256WithData:data1];
            NSString *digestStr1  = [NSString stringWithFormat:@"0x%@",digest1];
//            NSString *digestStr2  = @"";
            NSData *oneDigestData = [[NSData alloc] initWithBase64EncodedString:_bankCardDigest1 options:0];
            WEXNSLOG(@"onlineDigestData=%@",oneDigestData);
            WEXNSLOG(@"data3 =%@",data3);
            WEXNSLOG(@"digestStr1 =%@",digestStr1);
            if ([oneDigestData isEqualToData:data3]) {
                
                _bankCardCompareIpfsGroup = dispatch_group_create();
                dispatch_group_enter(_bankCardCompareIpfsGroup);
                [self getBankRawTranstion];
                
                dispatch_group_notify(_bankCardCompareIpfsGroup, dispatch_get_main_queue(), ^{
                    WEXNSLOG(@"刷新界面");
                    WEXNSLOG(@"_bankCardTokenModel.digest1 = %@",_bankCardTokenModel.digest1);
                    WEXNSLOG(@"_bankCardTokenModel.digest2 = %@",_bankCardTokenModel.digest2);
                    _bankCardCompareIpfsGroup = nil;
                    
                    if ([WeXIpfsHelper isBlankString:_bankCardTokenModel.digest1] &&[WeXIpfsHelper isBlankString:_bankCardTokenModel.digest2] ) {
                        [self addIdentifyUploadModel:WeXFileIpfsModeTypeBankCard];
                        dispatch_group_leave(_getAllCreditGroup);
                    }
                    
                   else if ([_bankCardTokenModel.digest1 isEqualToString:digestStr1] ) {
                       
                       [self addIdentifyNewestModel:WeXFileIpfsModeTypeBankCard];
                        dispatch_group_leave(_getAllCreditGroup);
                        
                  }else{
                        [self addIdentifyUploadModel:WeXFileIpfsModeTypeBankCard];
                        dispatch_group_leave(_getAllCreditGroup);
                    }
                });
            }else{
                
                _bankCardCompareIpfsGroup = dispatch_group_create();
                dispatch_group_enter(_bankCardCompareIpfsGroup);
                [self getBankRawTranstion];
                
                dispatch_group_notify(_bankCardCompareIpfsGroup, dispatch_get_main_queue(), ^{
                    WEXNSLOG(@"刷新界面");
                    WEXNSLOG(@"_bankCardTokenModel.digest1 = %@",_bankCardTokenModel.digest1);
                    WEXNSLOG(@"_bankCardTokenModel.digest2 = %@",_bankCardTokenModel.digest2);
                    _bankCardCompareIpfsGroup = nil;
                    NSData *oneDigestData = [[NSData alloc] initWithBase64EncodedString:_bankCardDigest1 options:0];
                    NSLog(@"onlineDigestData=%@",oneDigestData);
                    NSString *oneStr = [oneDigestData description];
                    oneStr = [oneStr stringByReplacingOccurrencesOfString:@" " withString:@""];
                    oneStr = [oneStr stringByReplacingOccurrencesOfString:@"<" withString:@""];
                    oneStr = [oneStr stringByReplacingOccurrencesOfString:@">" withString:@""];
                    oneStr = [NSString stringWithFormat:@"0x%@",oneStr];
                    NSLog(@"oneStr = %@",oneStr);
                    
                    if ([WeXIpfsHelper isBlankString:_bankCardTokenModel.digest1] &&[WeXIpfsHelper isBlankString:_bankCardTokenModel.digest2] ) {
                        [self addIdentifyNoDataModel:WeXFileIpfsModeTypeBankCard];
                        dispatch_group_leave(_getAllCreditGroup);
                    }
                   else if ([_bankCardTokenModel.digest1 isEqualToString:oneStr]) {
                       [self addIdentifyDownloadModel:WeXFileIpfsModeTypeBankCard];
                        dispatch_group_leave(_getAllCreditGroup);
                        
                    }else{
                        [self addIdentifyNoDataModel:WeXFileIpfsModeTypeBankCard];
                        dispatch_group_leave(_getAllCreditGroup);
                    }
                });
            }
        });
        
    }else{
        //本地无银行卡认证数据
        _bankCardCompareIpfsGroup = dispatch_group_create();
        dispatch_group_enter(_bankCardCompareIpfsGroup);
        [self getBankRawTranstion];
        dispatch_group_enter(_bankCardCompareIpfsGroup);
        [self createGetBankOrderDataRequest];
        dispatch_group_notify(_bankCardCompareIpfsGroup, dispatch_get_main_queue(), ^{
            WEXNSLOG(@"刷新界面");

            WEXNSLOG(@"_bankCardTokenModel.digest1 = %@",_bankCardTokenModel.digest1);
            WEXNSLOG(@"_bankCardTokenModel.digest2 = %@",_bankCardTokenModel.digest2);
            WEXNSLOG(@"_bankCardDigest1 = %@",_bankCardDigest1);
            WEXNSLOG(@"_bankCardDigest2 = %@",_bankCardDigest2);
            _bankCardCompareIpfsGroup = nil;
            NSData *oneDigestData = [[NSData alloc] initWithBase64EncodedString:_bankCardDigest1 options:0];
            NSLog(@"onlineDigestData=%@",oneDigestData);
            NSString *oneStr = [oneDigestData description];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@" " withString:@""];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@"<" withString:@""];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@">" withString:@""];
            oneStr = [NSString stringWithFormat:@"0x%@",oneStr];
            NSLog(@"oneStr = %@",oneStr);
            if ([WeXIpfsHelper isBlankString:_bankCardTokenModel.digest1]  &&  [WeXIpfsHelper isBlankString: _bankCardTokenModel.digest2] ) {
                [self addIdentifyNoDataModel:WeXFileIpfsModeTypeBankCard];
                dispatch_group_leave(_getAllCreditGroup);
                return ;
            }
           else if ([_bankCardTokenModel.digest1 isEqualToString:oneStr]) {
                [self addIdentifyDownloadModel:WeXFileIpfsModeTypeBankCard];
                dispatch_group_leave(_getAllCreditGroup);
            }else{
                [self addIdentifyNoDataModel:WeXFileIpfsModeTypeBankCard];
                dispatch_group_leave(_getAllCreditGroup);
            }
        });
    }
}

//运营商认证数据初始化
- (void)phoneOperatorDataInitialization{
    
    WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
    if (cacheModel.mobileAuthenStatus ==  WeXCreditMobileOperatorAuthenStatusTypeSuccess) {
        //本地有运营商认证数据
        _phoneCompareLocalGroup = dispatch_group_create();
        dispatch_group_enter( _phoneCompareLocalGroup);
        [self createGetPhoneOrderDataRequest];//查询链上合约摘要
        dispatch_group_notify( _phoneCompareLocalGroup , dispatch_get_main_queue(), ^{
     
        WEXNSLOG(@"_phoneOperatorDigest1 = %@",_phoneOperatorDigest1);
        WEXNSLOG(@"_phoneOperatorDigest2 = %@",_phoneOperatorDigest2);
        _phoneCompareLocalGroup = nil;
//         dispatch_group_leave(_getAllCreditGroup);
        NSString *digestStr =  [WexCommonFunc getContentWithFileName:WEX_MOBILE_AUTHEN_REPORT_KEY];
        WEXNSLOG(@"digestStr = %@",digestStr);
        NSData *digest = [[NSData alloc] initWithBase64EncodedString:_phoneOperatorDigest1 options:0];
        WEXNSLOG(@"digest = %@",digest);
        NSData *reportData = [[NSData alloc] initWithBase64EncodedString:digestStr options:0];
        WEXNSLOG(@"digest = %@",digest);
        NSData *reportShaData = [WexCommonFunc dataShaSHAWithData:reportData];
        WEXNSLOG(@"reportShaData = %@",reportShaData);
        WEXNSLOG(@"0.0");
            
        if ([digest isEqualToData:reportShaData]) {
       
            _phoneCompareIpfsGroup = dispatch_group_create();
            dispatch_group_enter(_phoneCompareIpfsGroup);
            [self getPhoneRawTranstion];
            dispatch_group_notify(_phoneCompareIpfsGroup, dispatch_get_main_queue(), ^{
            WEXNSLOG(@"刷新界面");
            _phoneCompareIpfsGroup = nil;
                
            WEXNSLOG(@"_phoneTokenModel.digest1 = %@",_phoneTokenModel.digest1);
            WEXNSLOG(@"_phoneTokenModel.digest2 = %@",_phoneTokenModel.digest2);
            NSData *oneDigestData = [[NSData alloc] initWithBase64EncodedString:_phoneOperatorDigest1 options:0];
            NSLog(@"onlineDigestData=%@",oneDigestData);
            NSString *oneStr = [oneDigestData description];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@" " withString:@""];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@"<" withString:@""];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@">" withString:@""];
            oneStr = [NSString stringWithFormat:@"0x%@",oneStr];
            NSLog(@"oneStr = %@",oneStr);
                
            if ([WeXIpfsHelper isBlankString:_phoneTokenModel.digest1] &&[WeXIpfsHelper isBlankString:_phoneTokenModel.digest2] ) {
                [self addIdentifyUploadModel:WeXFileIpfsModeTypePhoneOperator];
                dispatch_group_leave(_getAllCreditGroup);
            }
                
            else if ([_phoneTokenModel.digest1 isEqualToString:oneStr]) {
                [self addIdentifyNewestModel:WeXFileIpfsModeTypePhoneOperator];
                dispatch_group_leave(_getAllCreditGroup);
    
            }else{
                [self addIdentifyUploadModel:WeXFileIpfsModeTypePhoneOperator];
                dispatch_group_leave(_getAllCreditGroup);
                
            }
});
            
        }else{
            
            _phoneCompareIpfsGroup = dispatch_group_create();
            dispatch_group_enter(_phoneCompareIpfsGroup);
            [self getPhoneRawTranstion];
            dispatch_group_notify(_phoneCompareIpfsGroup, dispatch_get_main_queue(), ^{
                WEXNSLOG(@"刷新界面");
                WEXNSLOG(@"_phoneTokenModel.digest1 = %@",_phoneTokenModel.digest1);
                WEXNSLOG(@"_phoneTokenModel.digest2 = %@",_phoneTokenModel.digest2);
                _phoneCompareIpfsGroup = nil;
                NSData *oneDigestData = [[NSData alloc] initWithBase64EncodedString:_phoneOperatorDigest1 options:0];
                NSLog(@"onlineDigestData=%@",oneDigestData);
                NSString *oneStr = [oneDigestData description];
                oneStr = [oneStr stringByReplacingOccurrencesOfString:@" " withString:@""];
                oneStr = [oneStr stringByReplacingOccurrencesOfString:@"<" withString:@""];
                oneStr = [oneStr stringByReplacingOccurrencesOfString:@">" withString:@""];
                oneStr = [NSString stringWithFormat:@"0x%@",oneStr];
                NSLog(@"oneStr = %@",oneStr);
                if ([WeXIpfsHelper isBlankString:_phoneTokenModel.digest1] &&[WeXIpfsHelper isBlankString:_phoneTokenModel.digest2] ) {
                    [self addIdentifyNoDataModel:WeXFileIpfsModeTypePhoneOperator];
                    dispatch_group_leave(_getAllCreditGroup);
                }
                
                else if ([_phoneTokenModel.digest1 isEqualToString:oneStr]) {
                    [self addIdentifyDownloadModel:WeXFileIpfsModeTypePhoneOperator];
                    dispatch_group_leave(_getAllCreditGroup);
                    
                }else{
                    [self addIdentifyNoDataModel:WeXFileIpfsModeTypePhoneOperator];
                    dispatch_group_leave(_getAllCreditGroup);
                    
                }
                
            });
        }

        });
    }else{
        
        //本地无认证数据
        _phoneCompareIpfsGroup = dispatch_group_create();
        dispatch_group_enter(_phoneCompareIpfsGroup);
        [self getPhoneRawTranstion];
        dispatch_group_enter(_phoneCompareIpfsGroup);
        [self createGetPhoneOrderDataRequest];
        dispatch_group_notify(_phoneCompareIpfsGroup, dispatch_get_main_queue(), ^{
            WEXNSLOG(@"刷新界面");
       
            WEXNSLOG(@"_phoneTokenModel.digest1 = %@",_phoneTokenModel.digest1);
            WEXNSLOG(@"_phoneTokenModel.digest2 = %@",_phoneTokenModel.digest2);
            WEXNSLOG(@"_phoneOperatorDigest1 = %@",_phoneOperatorDigest1);
            WEXNSLOG(@"_phoneOperatorDigest2 = %@",_phoneOperatorDigest2);
            _phoneCompareIpfsGroup = nil;
            
            NSData *oneDigestData = [[NSData alloc] initWithBase64EncodedString:_phoneOperatorDigest1 options:0];
            NSLog(@"onlineDigestData=%@",oneDigestData);
            NSString *oneStr = [oneDigestData description];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@" " withString:@""];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@"<" withString:@""];
            oneStr = [oneStr stringByReplacingOccurrencesOfString:@">" withString:@""];
            oneStr = [NSString stringWithFormat:@"0x%@",oneStr];
            NSLog(@"oneStr = %@",oneStr);
            if ([WeXIpfsHelper isBlankString:_phoneTokenModel.digest1]  &&  [WeXIpfsHelper isBlankString: _phoneTokenModel.digest2] ) {
                [self addIdentifyNoDataModel:WeXFileIpfsModeTypePhoneOperator];
                dispatch_group_leave(_getAllCreditGroup);
                return ;
            }
            
            else if ([_phoneTokenModel.digest1 isEqualToString:oneStr]) {
                [self addIdentifyDownloadModel:WeXFileIpfsModeTypePhoneOperator];
                dispatch_group_leave(_getAllCreditGroup);
                
            }else{
                
                [self addIdentifyNoDataModel:WeXFileIpfsModeTypePhoneOperator];
                dispatch_group_leave(_getAllCreditGroup);
            }
        });
    }
}

#pragma mark -初始化的4种状态
//可上传的Model
- (void)addIdentifyUploadModel:(WeXFileIpfsModeType)ipfsModeType{
    WeXMyIpfsSaveModel *identifyModel = [[WeXMyIpfsSaveModel alloc]init];
    if(ipfsModeType == WeXFileIpfsModeTypeIdentify){
        identifyModel.identifyTitle = @"实名认证数据";
    }else if(ipfsModeType == WeXFileIpfsModeTypeBankCard){
        identifyModel.identifyTitle = @"银行卡认证数据";
    }else if(ipfsModeType == WeXFileIpfsModeTypePhoneOperator){
        identifyModel.identifyTitle = @"运营商认证数据";
    }
    identifyModel.statusDescribeStr = @"可上传";
    identifyModel.fileIpfsType = WeXFileIpfsTypeUpload;
    identifyModel.isSelected = YES;
    identifyModel.isAllowSelect = YES;
    if(ipfsModeType == WeXFileIpfsModeTypeIdentify){
        identifyModel.modeType = WeXFileIpfsModeTypeIdentify;
    }else if(ipfsModeType == WeXFileIpfsModeTypeBankCard){
        identifyModel.modeType = WeXFileIpfsModeTypeBankCard;
    }else if(ipfsModeType == WeXFileIpfsModeTypePhoneOperator){
        identifyModel.modeType = WeXFileIpfsModeTypePhoneOperator;
    }
    [self.dataArray addObject:identifyModel];
    [self.selectDataArray addObject:identifyModel];
}

//可下载的Model
- (void)addIdentifyDownloadModel:(WeXFileIpfsModeType)ipfsModeType{
    WeXMyIpfsSaveModel *identifyModel = [[WeXMyIpfsSaveModel alloc]init];
    if(ipfsModeType == WeXFileIpfsModeTypeIdentify){
        identifyModel.identifyTitle = @"实名认证数据";
    }else if(ipfsModeType == WeXFileIpfsModeTypeBankCard){
        identifyModel.identifyTitle = @"银行卡认证数据";
    }else if(ipfsModeType == WeXFileIpfsModeTypePhoneOperator){
        identifyModel.identifyTitle = @"运营商认证数据";
    }
    identifyModel.statusDescribeStr = @"可下载";
    identifyModel.fileIpfsType = WeXFileIpfsTypeDownload;
    identifyModel.isSelected = YES;
    identifyModel.isAllowSelect = YES;
    if(ipfsModeType == WeXFileIpfsModeTypeIdentify){
        identifyModel.modeType = WeXFileIpfsModeTypeIdentify;
    }else if(ipfsModeType == WeXFileIpfsModeTypeBankCard){
        identifyModel.modeType = WeXFileIpfsModeTypeBankCard;
    }else if(ipfsModeType == WeXFileIpfsModeTypePhoneOperator){
        identifyModel.modeType = WeXFileIpfsModeTypePhoneOperator;
    }
    [self.dataArray addObject:identifyModel];
    [self.selectDataArray addObject:identifyModel];
}

//本地和云端均为最新数据的Model
- (void)addIdentifyNewestModel:(WeXFileIpfsModeType)ipfsModeType{
    WeXMyIpfsSaveModel *identifyModel = [[WeXMyIpfsSaveModel alloc]init];
    if(ipfsModeType == WeXFileIpfsModeTypeIdentify){
        identifyModel.identifyTitle = @"实名认证数据";
    }else if(ipfsModeType == WeXFileIpfsModeTypeBankCard){
        identifyModel.identifyTitle = @"银行卡认证数据";
    }else if(ipfsModeType == WeXFileIpfsModeTypePhoneOperator){
        identifyModel.identifyTitle = @"运营商认证数据";
    }
    identifyModel.statusDescribeStr = @"本地和云端均为最新数据";
    identifyModel.fileIpfsType =  WeXFileIpfsTypeNewest;
    identifyModel.isSelected = NO;
    identifyModel.isAllowSelect = NO;
    if(ipfsModeType == WeXFileIpfsModeTypeIdentify){
        identifyModel.modeType = WeXFileIpfsModeTypeIdentify;
    }else if(ipfsModeType == WeXFileIpfsModeTypeBankCard){
        identifyModel.modeType = WeXFileIpfsModeTypeBankCard;
    }else if(ipfsModeType == WeXFileIpfsModeTypePhoneOperator){
        identifyModel.modeType = WeXFileIpfsModeTypePhoneOperator;
    }
    [self.dataArray addObject:identifyModel];
}

//本地云上无数据无需上传和下载的Model
- (void)addIdentifyNoDataModel:(WeXFileIpfsModeType)ipfsModeType{
    WeXMyIpfsSaveModel *identifyModel = [[WeXMyIpfsSaveModel alloc]init];
    if(ipfsModeType == WeXFileIpfsModeTypeIdentify){
        identifyModel.identifyTitle = @"实名认证数据";
    }else if(ipfsModeType == WeXFileIpfsModeTypeBankCard){
        identifyModel.identifyTitle = @"银行卡认证数据";
    }else if(ipfsModeType == WeXFileIpfsModeTypePhoneOperator){
        identifyModel.identifyTitle = @"运营商认证数据";
    }
    identifyModel.statusDescribeStr = @"无可上传下载数据,建议重新认证";
    identifyModel.fileIpfsType =  WeXFileIpfsTypeNoData;
    identifyModel.isSelected = NO;
    identifyModel.isAllowSelect = NO;
    if(ipfsModeType == WeXFileIpfsModeTypeIdentify){
        identifyModel.modeType = WeXFileIpfsModeTypeIdentify;
    }else if(ipfsModeType == WeXFileIpfsModeTypeBankCard){
        identifyModel.modeType = WeXFileIpfsModeTypeBankCard;
    }else if(ipfsModeType == WeXFileIpfsModeTypePhoneOperator){
        identifyModel.modeType = WeXFileIpfsModeTypePhoneOperator;
    }
    [self.dataArray addObject:identifyModel];
}


#pragma mark -初始化控件布局
-(void)setupSubViews{
    self.navigationItem.title = @"我的云存储";
    self.view.backgroundColor = [UIColor whiteColor];
    
    UIView *topView = [[UIView alloc]initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, 40)];
    topView.backgroundColor = ColorWithHex(0xF8F8FF);
    [self.view addSubview:topView];
 
    UILabel *oneDefaultLabel = [[UILabel alloc]init];
    oneDefaultLabel.font = [UIFont systemFontOfSize:14];
    //    self.oneDefaultLabel.backgroundColor = [UIColor greenColor];
    oneDefaultLabel.textColor = ColorWithHex(0x4A4A4A);
    oneDefaultLabel.text = @"可同步数据";
    [topView addSubview:oneDefaultLabel];
    [oneDefaultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(10);
        make.left.mas_equalTo(15);
//        make.width.mas_equalTo(10);
        make.height.mas_equalTo(20);
    }];
    
    UIButton *oneDefultImgBtn = [[UIButton alloc]init];
    [oneDefultImgBtn setImage:[UIImage imageNamed:@"1B"] forState:UIControlStateNormal];
    [oneDefultImgBtn addTarget:self action:@selector(addTipsView) forControlEvents:UIControlEventTouchUpInside];
    [topView addSubview:oneDefultImgBtn];
    [oneDefultImgBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(oneDefaultLabel);
        make.width.mas_equalTo(16);
        make.height.mas_equalTo(16);
        make.leading.equalTo(oneDefaultLabel.mas_trailing).offset(10);
    }];
    
    UIView *fotterView = [[UIView alloc]init];
    [self.view addSubview:fotterView];
    [fotterView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(0);
        make.leading.trailing.equalTo(self.view).offset(0);
        make.height.mas_equalTo(160);
    }];
    
    _synchBtn = [[UIButton alloc]init];
    [_synchBtn setTitle:@"开始同步" forState:UIControlStateNormal];
    [_synchBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [_synchBtn setBackgroundColor:ColorWithHex(0x6766CC)];
    _synchBtn.layer.cornerRadius = 6;
    _synchBtn.clipsToBounds = YES;
    [_synchBtn addTarget:self action:@selector(surnBtnClick) forControlEvents: UIControlEventTouchUpInside];
    [self.view addSubview:_synchBtn];
    [_synchBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fotterView).offset(20);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.mas_equalTo(40);
    }];
    
    _changePwdBtn = [[UIButton alloc]init];
    [_changePwdBtn setTitle:@"更换云存储密码" forState:UIControlStateNormal];
    [_changePwdBtn setTitleColor:ColorWithHex(0x6766CC) forState:UIControlStateNormal];
    [_changePwdBtn setBackgroundColor:[UIColor whiteColor]];
    [_changePwdBtn addTarget:self action:@selector(changePwdBtnClick) forControlEvents: UIControlEventTouchUpInside];
    [self.view addSubview:_changePwdBtn];
    [_changePwdBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.synchBtn.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.mas_equalTo(40);
    }];
    
    _tableView = [[UITableView alloc] init];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.sectionIndexBackgroundColor=[UIColor clearColor];
    _tableView.sectionIndexColor = ColorWithHex(0x5756B3);
    [self.view addSubview:_tableView];
    _tableView.tableFooterView = [UIView new];//当cell较少时影藏多余的cell
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(topView.mas_bottom).offset(10);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(fotterView.mas_top);
    }];
    [_tableView registerClass:[WeXMyIpfsSaveCell class] forCellReuseIdentifier:kMyIpfsSaveIdentifier];
    [_tableView reloadData];
    
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return _dataArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXMyIpfsSaveCell *cell = [tableView dequeueReusableCellWithIdentifier:kMyIpfsSaveIdentifier];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    WeXMyIpfsSaveModel *dataModel = _dataArray[indexPath.row];
    cell.model = dataModel;
    cell.ipfsModelBlock = ^(WeXMyIpfsSaveModel *ipfsModel) {
        WEXNSLOG(@"ipfsModel = %@",ipfsModel);
        WeXMyIpfsAddressController *vc = [[WeXMyIpfsAddressController alloc]init];
        vc.idnetifyType = ipfsModel.modeType;
        [self.navigationController pushViewController:vc animated:YES];
        
    };
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 70;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    WeXMyIpfsSaveModel *dataModel = _dataArray[indexPath.row];
    if (dataModel.isAllowSelect) {
        dataModel.isSelected = !dataModel.isSelected;
        if ([self.selectDataArray containsObject:dataModel]) {
            [self.selectDataArray removeObject:dataModel];
        }else{
            [self.selectDataArray addObject:dataModel];
        }
    }else{
    }
    [self.tableView reloadData];
    [self refreshSynchBtnStatus];
}


#pragma mark -- 上传认证数据动画处理
- (void)judgeIdentifyDataClick{
    BOOL isIdentifyExistence  = NO;
    for (WeXMyIpfsSaveModel *model in _selectDataArray) {
        if (model.modeType == WeXFileIpfsModeTypeIdentify) {
             if (model.fileIpfsType == WeXFileIpfsTypeUpload) {
                 [self upLoadIdentifyData];
                 isIdentifyExistence =YES;
                 return;
             }
            if (model.fileIpfsType == WeXFileIpfsTypeDownload) {
                 [self downLoadIdentifyData];
                 isIdentifyExistence =YES;
                 return;
            }
             dispatch_group_leave(_surnAllGroup);
        }
    }
    if (!isIdentifyExistence) {
          dispatch_group_leave(_surnAllGroup);
    }
    
}

- (void)judgeBankCardfyDataClick{
    BOOL isBankExistence  = NO;
    WEXNSLOG(@"_selectDataArray = %@",_selectDataArray);
    for (WeXMyIpfsSaveModel *model in _selectDataArray) {
        if (model.modeType == WeXFileIpfsModeTypeBankCard) {
            if (model.fileIpfsType == WeXFileIpfsTypeUpload) {
                [self upLoadBankCardData];
                isBankExistence = YES;
                 return;
            }
            if (model.fileIpfsType == WeXFileIpfsTypeDownload) {
                [self downLoadBankCardData];
                isBankExistence = YES;
                 return;
            }
            dispatch_group_leave(_surnAllGroup);
        }
    }
    if (!isBankExistence) {
        dispatch_group_leave(_surnAllGroup);
    }
}

- (void)judgePhoneOperatorDataClick{
    BOOL isPhoneExistence  = NO;
    WEXNSLOG(@"_selectDataArray = %@",_selectDataArray);
    for (WeXMyIpfsSaveModel *model in _selectDataArray) {
        if (model.modeType == WeXFileIpfsModeTypePhoneOperator) {
            if (model.fileIpfsType == WeXFileIpfsTypeUpload) {
                [self upLoadPhoneOperatorData];
                isPhoneExistence = YES;
                 return;
            }
            if (model.fileIpfsType == WeXFileIpfsTypeDownload) {
                [self downLoadPhoneOperatorData];
                 isPhoneExistence = YES;
                 return;
            }
            dispatch_group_leave(_surnAllGroup);
        }
    }
    if (!isPhoneExistence ) {
        dispatch_group_leave(_surnAllGroup);
    }
    
}

- (void)upLoadIdentifyData{
    
    for (WeXMyIpfsSaveModel *model in _selectDataArray) {
        if (model.modeType == WeXFileIpfsModeTypeIdentify) {
            if (model.fileIpfsType == WeXFileIpfsTypeUpload) {
                WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
            
                NSString *postiveImgStr = [WexCommonFunc base64StringWithImageData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_FRONT_KEY]];
                NSString *backImgStr = [WexCommonFunc base64StringWithImageData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_BACK_KEY]];
                NSString *faceImgStr = [WexCommonFunc base64StringWithImageData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_HEAD_KEY]];
                
                NSMutableDictionary *saveDict = [[NSMutableDictionary alloc]init];
                saveDict[@"positivePhoto"] = postiveImgStr;   //身份证正面照
                saveDict[@"backPhoto"] = backImgStr;  //身份证反面
                saveDict[@"facePhoto"] = faceImgStr;  //头像
                if (cacheModel.idAuthenStatus ==  WeXCreditIDAuthenStatusTypeNone) {
                    saveDict[@"idAuthenStatus"] = @"NO";  //实名认证状态
                }
                if (cacheModel.idAuthenStatus ==  WeXCreditIDAuthenStatusTypeSuccess) {
                    saveDict[@"idAuthenStatus"] = @"PASSED";  //实名认证状态
                }
//                saveDict[@"idAuthenStatus"] = @"none";  //实名认证状态
                saveDict[@"orderId"] = cacheModel.idAuthenOrderId;  //订单id
                saveDict[@"similarity"] = cacheModel.similarity;  //相似度
                saveDict[@"userAddress"] = cacheModel.userAddress; //地址
                saveDict[@"userName"] = cacheModel.userName; //姓名
                saveDict[@"userNumber"] = cacheModel.userNumber; //身份证号码
                saveDict[@"userNation"] = cacheModel.userNation; //民族
                saveDict[@"userSex"] = cacheModel.userSex;  //性别
                saveDict[@"userTimeLimit"] = cacheModel.userTimeLimit; //过期时间
                saveDict[@"userAuthority"] = cacheModel.userAuthority; //签名机构
                saveDict[@"userYear"] = cacheModel.userYear;  //出生年
                saveDict[@"userMonth"] = cacheModel.userMonth;  //出生月
                saveDict[@"userDay"] = cacheModel.userDay;   // 出生日
//                saveDict[@"idAuthenTxHash"] = cacheModel.idAuthenTxHash;  //出生月
//                saveDict[@"idAuthenNonce"] = cacheModel.idAuthenNonce;   // 出生日
                
                WEXNSLOG(@"saveDict = %@",saveDict);
                __weak __typeof(self)weakSelf = self;
                NSIndexPath *indexPath = [NSIndexPath indexPathForRow:0 inSection:0];

                WeXMyIpfsSaveCell *cell = [_tableView cellForRowAtIndexPath:indexPath];
                cell.statusStr.hidden = YES;
                cell.statusProgressView.hidden = NO;
                cell.fileUploadStatusLabel.hidden = NO;
                cell.fileUploadStatusLabel.text = @"正在上链";
                NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                NSString *passWord = [user objectForKey:WEX_IPFS_MY_KEY];
                
                [[WXPassHelper instance]getIpfsNonceWithContractAddressResponseBlock:^(id response) {
                    WEXNSLOG(@"response= %@",response);
                    NSDictionary *dicts = response;
                    NSArray *nonceArray = dicts[@"data"];
                 
                    WEXNSLOG(@"_idendifyNonce = %@", _idendifyNonce);
                    NSData *data = [NSKeyedArchiver archivedDataWithRootObject:nonceArray];
                    _idendifyNonce = [NSString stringWithFormat:@"0x%@", [WexCommonFunc stringSHA256WithData:data]];
                    //适配安卓的要求,aes加密密码用md5(32位)处理,iv用md5(16位)处理
                    NSString *nonceStr = [WeXIpfsHelper getMd5_16Bit_String:_idendifyNonce];
                    WEXNSLOG(@"nonceStr = %@",nonceStr);
                    NSString *passwardStr = [WexCommonFunc md5: passWord];;
                    WEXNSLOG(@"passwardStr = %@", passwardStr);
                    
                    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                    NSString *mainUrlStr = [user objectForKey:WEX_IPFS_MAIN_NONEURL];
                    
                    [AIIpfsSaveDataHelpObject uploadIdnetifyData:saveDict WithBaseUrl:mainUrlStr   WithUserPassword:passwardStr WithNonceStr:nonceStr  WithType: AIIpfsSaveDataTypeID Callback:^(NSString *saveHash) {
                        WEXNSLOG(@"saveHash = %@",saveHash);
                        _identifyHash = saveHash;
                        NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                        [user setObject:saveHash forKey:WEX_IPFS_IDENTIFY_HASH];
                        cell.statusStr.hidden = YES;
                        cell.fileUploadStatusLabel.hidden = YES;
                        cell.statusProgressView.hidden = YES;
                        cell.cloudAddressBtn.hidden = NO;
                        [self postIdentifyRawTranstion];

                    
                     }];
                     [AIIpfsSaveDataHelpObject getUploadIdentifyProgress:^(NSProgress *progress) {
                        dispatch_sync(dispatch_get_main_queue(), ^{
                         cell.statusStr.hidden = YES;
                         cell.statusProgressView.progress = progress.fractionCompleted;
         
                      });
                    
                      }];
                }];
            }
        }
    }
}

-(void)downLoadIdentifyData{
    
    _identifyDownLoadGroup = dispatch_group_create();
    dispatch_group_enter( _identifyDownLoadGroup);
    [self getIdentifyRawTranstion];//查询链上合约摘要
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *passWord = [user objectForKey:WEX_IPFS_MY_KEY];
    NSString *twoPassWord = [user objectForKey:WEX_IPFS_MY_CHECKKEY];
    
    WEXNSLOG(@"passWord= %@",passWord);
    WEXNSLOG(@"twoPassWord= %@",twoPassWord);
    
    dispatch_group_notify(_identifyDownLoadGroup, dispatch_get_main_queue(), ^{
         WEXNSLOG(@"_identifyTokenModel.token = %@",_identifyTokenModel.token);
        _identifyDownLoadGroup = nil;

        if ([_identifyTokenModel.version integerValue]>[_versionStr integerValue]) {
            [WeXPorgressHUD hideLoading];
            [self ipfsEncopyTips];
            return ;
        }
        //适配安卓的要求,aes加密密码用md5(32位)处理,iv用md5(16位)处理
        NSString *nonceStr = [WeXIpfsHelper getMd5_16Bit_String:_identifyTokenModel.nonce];
        WEXNSLOG(@"nonceStr = %@",nonceStr);
        NSString *passwardStr = [WexCommonFunc md5: passWord];;
        WEXNSLOG(@"passwardStr = %@", passwardStr);
        NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
        NSString *mainUrlStr = [user objectForKey:WEX_IPFS_MAIN_NONEURL];
        
        [AIIpfsSaveDataHelpObject getDataFromIpfs:_identifyTokenModel.token WithBaseUrl:mainUrlStr WithUserPassword: passwardStr  WithNonceStr:nonceStr WithType:AIIpfsSaveDataTypeID Callback:^(NSMutableDictionary *saveDict) {
            WEXNSLOG(@"saveDict = %@",saveDict);
            if (!saveDict) {
                if (_surnAllGroup) {
                    dispatch_group_leave(_surnAllGroup);
                }
                return ;
            }
            WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
            NSString *statusStr = saveDict[@"idAuthenStatus"];
            if ([statusStr isEqualToString:@"NO"] ) {
                model.idAuthenStatus =  WeXCreditIDAuthenStatusTypeNone;
            }
            if ([statusStr isEqualToString:@"PASSED"] ) {
                model.idAuthenStatus =  WeXCreditIDAuthenStatusTypeSuccess;
            }
           
            NSString *postiveImgStr = saveDict[@"positivePhoto"];
            NSString *backImgStr = saveDict[@"backPhoto"];
            NSString *faceImgStr = saveDict[@"facePhoto"];
            
            NSData *postiveData = [[NSData alloc] initWithBase64EncodedString:postiveImgStr options:NSDataBase64DecodingIgnoreUnknownCharacters];
            NSData *backData = [[NSData alloc] initWithBase64EncodedString:backImgStr options:NSDataBase64DecodingIgnoreUnknownCharacters];
            NSData *faceData = [[NSData alloc] initWithBase64EncodedString:faceImgStr options:NSDataBase64DecodingIgnoreUnknownCharacters];
            
            [WexCommonFunc saveImageData:postiveData imageName:WEX_ID_IMAGE_FRONT_KEY];
            [WexCommonFunc saveImageData:backData imageName:WEX_ID_IMAGE_BACK_KEY];
            [WexCommonFunc saveImageData:faceData imageName:WEX_ID_IMAGE_HEAD_KEY];
            
            model.idAuthenOrderId = saveDict[@"orderId"];  //订单id
            model.similarity = saveDict[@"similarity"] ;  //相似度
            model.userAddress = saveDict[@"userAddress"] ; //地址
            model.userName = saveDict[@"userName"] ; //姓名
            model.userNumber = saveDict[@"userNumber"]; //身份证号码
            model.userNation = saveDict[@"userNation"] ; //民族
            model.userSex =  saveDict[@"userSex"] ;  //性别
            model.userTimeLimit = saveDict[@"userTimeLimit"] ; //过期时间
            model.userAuthority = saveDict[@"userAuthority"] ; //签名机构
            model.userYear = saveDict[@"userYear"] ;  //出生年
            model.userMonth = saveDict[@"userMonth"] ;  //出生月
            model.userDay = saveDict[@"userDay"] ;   // 出生日
            [WexCommonFunc savePassport:model];
            
            if (_surnAllGroup) {
                dispatch_group_leave(_surnAllGroup);
            }
        }];
    });
}

- (void)upLoadBankCardData{
    
    for (WeXMyIpfsSaveModel *model in _selectDataArray) {
        if (model.modeType == WeXFileIpfsModeTypeBankCard) {
            if (model.fileIpfsType == WeXFileIpfsTypeUpload) {
            NSMutableDictionary *saveDict = [[NSMutableDictionary alloc]init];
            WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
            if (cacheModel.bankAuthenStatus ==  WeXCreditBankAuthenStatusTypeNone) {
                saveDict[@"bankAuthenStatus"] = @"NO";  //认证状态
            }
            if (cacheModel.bankAuthenStatus ==  WeXCreditBankAuthenStatusTypeSuccess) {
                saveDict[@"bankAuthenStatus"] = @"PASSED";  //认证状态
            }

            saveDict[@"bankAuthenOrderid"] = cacheModel.idAuthenOrderId;  //银行订单号
//            saveDict[@"bankAuthenCodeName"] = cacheModel.userName;  //收款人姓名
            saveDict[@"bankAuthenCodeNumber"] = cacheModel.bankAuthenCardNumber;  //卡号
            saveDict[@"bankAuthenMobile"] = cacheModel.bankAuthenMobile;  //预留手机号
            saveDict[@"bankAuthenName"] = cacheModel.bankAuthenCardName; //银行名字
            if(![WeXIpfsHelper isBlankString:_bankCardExpired]){
                saveDict[@"bankAuthenExpired"] = _bankCardExpired; //过期时间
            }else{
                saveDict[@"bankAuthenExpired"] = @"0"; //过期时间
            }
//            saveDict[@"bankAuthenExpired"] = cacheModel.userTimeLimit; //过期时间
            saveDict[@"bankAuthenCode"] = cacheModel.bankAuthenCode; //银行内部代码
                   
            NSLog(@"saveDict = %@",saveDict);
            __weak __typeof(self)weakSelf = self;
            NSIndexPath *indexPath = [NSIndexPath indexPathForRow:1 inSection:0];
            WeXMyIpfsSaveCell *cell = [_tableView cellForRowAtIndexPath:indexPath];
            cell.statusStr.hidden = YES;
            cell.statusProgressView.hidden = NO;
            cell.fileUploadStatusLabel.hidden = NO;
            cell.fileUploadStatusLabel.text = @"正在上链";
            NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
            NSString *passWord = [user objectForKey:WEX_IPFS_MY_KEY];
             
            [[WXPassHelper instance]getIpfsNonceWithContractAddressResponseBlock:^(id response) {
                WEXNSLOG(@"response= %@",response);
                NSDictionary *dicts = response;
                NSArray *nonceArray = dicts[@"data"];
                WEXNSLOG(@"0.0");
                
                NSData *data = [NSKeyedArchiver archivedDataWithRootObject:nonceArray];
                _bankCardNonce = [NSString stringWithFormat:@"0x%@", [WexCommonFunc stringSHA256WithData:data]];
                WEXNSLOG(@"_bankCardNonce = %@",  _bankCardNonce);
                //适配安卓的要求,aes加密密码用md5(32位)处理,iv用md5(16位)处理
                NSString *nonceStr = [WeXIpfsHelper getMd5_16Bit_String:_bankCardNonce];
                WEXNSLOG(@"nonceStr = %@",nonceStr);
                NSString *passwardStr = [WexCommonFunc md5: passWord];;
                WEXNSLOG(@"passwardStr = %@", passwardStr);
                
                NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                NSString *mainUrlStr = [user objectForKey:WEX_IPFS_MAIN_NONEURL];
                
               [AIIpfsSaveDataHelpObject uploadIdnetifyData:saveDict WithBaseUrl:mainUrlStr WithUserPassword: passwardStr WithNonceStr:nonceStr WithType:AIIpfsSaveDataTypeBankCard Callback:^(NSString *saveHash) {
                NSLog(@"saveHash = %@",saveHash);
//                dispatch_sync(dispatch_get_main_queue(), ^{
                cell.statusStr.hidden = YES;
                _bankCardHash = saveHash;
                NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                [user setObject:saveHash forKey:WEX_IPFS_BankCard_HASH];

                cell.fileUploadStatusLabel.hidden = YES;
                cell.statusProgressView.hidden = YES;
                cell.cloudAddressBtn.hidden = NO;
                [self postBankRawTranstion];
//              });
            }];
            [AIIpfsSaveDataHelpObject getUploadIdentifyProgress:^(NSProgress *progress) {
                dispatch_sync(dispatch_get_main_queue(), ^{
                cell.statusStr.hidden = YES;
                cell.statusProgressView.progress = progress.fractionCompleted;
               });
            }];
          }];
         }
       }
    }
}

- (void)downLoadBankCardData{

    _bankCardDownLoadGroup = dispatch_group_create();
    dispatch_group_enter( _bankCardDownLoadGroup);
    [self getBankRawTranstion];//查询链上合约摘要
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *passWord = [user objectForKey:WEX_IPFS_MY_KEY];
    
    dispatch_group_notify(_bankCardDownLoadGroup, dispatch_get_main_queue(), ^{
        _bankCardDownLoadGroup = nil;
//        WEXNSLOG(@"_bankCardTokenModel.token = %@",_bankCardTokenModel.token);
        WEXNSLOG(@"passWord = %@",passWord);
        WEXNSLOG(@"_bankCardTokenModel.nonce = %@",_bankCardTokenModel.nonce);
        WEXNSLOG(@"_bankCardTokenModel.token = %@",_bankCardTokenModel.token);
        if ([_bankCardTokenModel.version integerValue]>[_versionStr integerValue]) {
            [WeXPorgressHUD hideLoading];
            [self ipfsEncopyTips];
            return ;
        }
        //适配安卓的要求,aes加密密码用md5(32位)处理,iv用md5(16位)处理
        NSString *nonceStr = [WeXIpfsHelper getMd5_16Bit_String:_bankCardTokenModel.nonce];
        WEXNSLOG(@"nonceStr = %@",nonceStr);
        NSString *passwardStr = [WexCommonFunc md5: passWord];
        WEXNSLOG(@"passwardStr = %@", passwardStr);
        NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
        NSString *mainUrlStr = [user objectForKey:WEX_IPFS_MAIN_NONEURL];
        
        [AIIpfsSaveDataHelpObject getDataFromIpfs:_bankCardTokenModel.token WithBaseUrl:mainUrlStr WithUserPassword:passwardStr  WithNonceStr:nonceStr WithType:AIIpfsSaveDataTypeBankCard Callback:^(NSMutableDictionary *saveDict) {
            WEXNSLOG(@"saveDict = %@",saveDict);
            if (!saveDict) {
                if (_surnAllGroup) {
                    dispatch_group_leave(_surnAllGroup);
                }
                return ;
            }
            
            WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
            NSString *statusStr = saveDict[@"bankAuthenStatus"];
            if ([statusStr isEqualToString:@"PASSED"] ) {
                model.bankAuthenStatus =  WeXCreditBankAuthenStatusTypeSuccess;
            }
            
            model.bankAuthenOrderId = saveDict[@"bankAuthenOrderid"] ;  //银行内部代码
//            model.userName = saveDict[@"bankAuthenCodeName"] ;  //收款人姓名
            model.bankAuthenCardNumber = saveDict[@"bankAuthenCodeNumber"] ;  //卡号
            model.bankAuthenMobile = saveDict[@"bankAuthenMobile"] ;  //预留手机号
            model.bankAuthenCardName = saveDict[@"bankAuthenName"];//银行名字
            model.bankTimeLimit = saveDict[@"bankAuthenExpired"] ; //过期时间
            model.bankAuthenCode = saveDict[@"bankAuthenCode"]; //过期时间
            
            [WexCommonFunc savePassport:model];
            if (_surnAllGroup) {
                dispatch_group_leave(_surnAllGroup);
            }
//            [self updateData];
        }];
    });
}

- (void)upLoadPhoneOperatorData{
    
    for (WeXMyIpfsSaveModel *model in _selectDataArray) {
        if (model.modeType == WeXFileIpfsModeTypePhoneOperator) {
            if (model.fileIpfsType == WeXFileIpfsTypeUpload) {
                NSMutableDictionary *saveDict = [[NSMutableDictionary alloc]init];
                WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
                if (cacheModel.bankAuthenStatus ==  WeXCreditMobileOperatorAuthenStatusTypeNone) {
                    saveDict[@"mobileAuthenStatus"] = @"NO";  //认证状态
                }
                if (cacheModel.bankAuthenStatus ==  WeXCreditMobileOperatorAuthenStatusTypeAuthening) {
                    saveDict[@"mobileAuthenStatus"] = @"NO";  //认证状态
                }
                if (cacheModel.bankAuthenStatus ==  WeXCreditMobileOperatorAuthenStatusTypeSuccess) {
                    saveDict[@"mobileAuthenStatus"] = @"DONE";  //认证状态
                }
                
                saveDict[@"mobileAuthenOrderid"] = cacheModel.mobileAuthenOrderId;  //订单ID
                saveDict[@"mobileAuthenNumber"] = cacheModel.mobileAuthenNumber;  //收款人姓名
                
                NSString *content = [WexCommonFunc getContentWithFileName:WEX_MOBILE_AUTHEN_REPORT_KEY];
                saveDict[@"mobileAuthenCmData"] = content;  //卡号
           
                if(![WeXIpfsHelper isBlankString:_bankCardExpired]){
                    saveDict[@"mobileAuthenExpired"] = _phoneOperatorExpired; //过期时间
                }else{
                    saveDict[@"mobileAuthenExpired"] = @"0"; //过期时间
                }
//                saveDict[@"mobileAuthenName"] = cacheModel.userName;
                
                WEXNSLOG(@"saveDict = %@",saveDict);
//                __weak __typeof(self)weakSelf = self;
                NSIndexPath *indexPath = [NSIndexPath indexPathForRow:2 inSection:0];
                WeXMyIpfsSaveCell *cell = [_tableView cellForRowAtIndexPath:indexPath];
                cell.statusStr.hidden = YES;
                cell.statusProgressView.hidden = NO;
                cell.fileUploadStatusLabel.hidden = NO;
                cell.fileUploadStatusLabel.text = @"正在上链";
                NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                NSString *passWord = [user objectForKey:WEX_IPFS_MY_KEY];
                
                [[WXPassHelper instance]getIpfsNonceWithContractAddressResponseBlock:^(id response) {
                    WEXNSLOG(@"response= %@",response);
                    NSDictionary *dicts = response;
                    NSArray *nonceArray = dicts[@"data"];
                    WEXNSLOG(@"0.0");
                  
                    NSData *data = [NSKeyedArchiver archivedDataWithRootObject:nonceArray];
                    _phoneNonce = [NSString stringWithFormat:@"0x%@", [WexCommonFunc stringSHA256WithData:data]];
                    WEXNSLOG(@"_phoneNonce = %@", _phoneNonce);
                    //适配安卓的要求,aes加密密码用md5(32位)处理,iv用md5(16位)处理
                    NSString *nonceStr = [WeXIpfsHelper getMd5_16Bit_String:_phoneNonce];
                    WEXNSLOG(@"nonceStr = %@",nonceStr);
                    NSString *passwardStr = [WexCommonFunc md5: passWord];;
                    WEXNSLOG(@"passwardStr = %@", passwardStr);
                    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                    NSString *mainUrlStr = [user objectForKey:WEX_IPFS_MAIN_NONEURL];
                    
                    [AIIpfsSaveDataHelpObject uploadIdnetifyData:saveDict WithBaseUrl:mainUrlStr WithUserPassword:passwardStr WithNonceStr:nonceStr WithType:AIIpfsSaveDataTypePhotoOprator Callback:^(NSString *saveHash) {
                        NSLog(@"saveHash = %@",saveHash);
                        // dispatch_sync(dispatch_get_main_queue(), ^{
                        cell.statusStr.hidden = YES;
                        _phoneOperatorHash = saveHash;
                        NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                        [user setObject:saveHash forKey:WEX_IPFS_PhoneOperator_HASH];
                        
                        cell.fileUploadStatusLabel.hidden = YES;
                        cell.statusProgressView.hidden = YES;
                        cell.cloudAddressBtn.hidden = NO;
                        [self postPhoneRawTranstion];
                        //              });
                    }];
                    [AIIpfsSaveDataHelpObject getUploadIdentifyProgress:^(NSProgress *progress) {
                        dispatch_sync(dispatch_get_main_queue(), ^{
                            cell.statusStr.hidden = YES;
                            cell.statusProgressView.progress = progress.fractionCompleted;
                        });
                    }];
                }];
            }
        }
    }
}

- (void)downLoadPhoneOperatorData{
    
    _phoneDownLoadGroup = dispatch_group_create();
    dispatch_group_enter( _phoneDownLoadGroup);
    [self getPhoneRawTranstion];//查询链上合约摘要
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    NSString *passWord = [user objectForKey:WEX_IPFS_MY_KEY];
    
    dispatch_group_notify(_phoneDownLoadGroup, dispatch_get_main_queue(), ^{
        WEXNSLOG(@"_bankCardTokenModel.token = %@",_phoneTokenModel.token);
        WEXNSLOG(@"passWord = %@",passWord);
        WEXNSLOG(@"_bankCardTokenModel.nonce = %@",_phoneTokenModel.nonce);
        WEXNSLOG(@"_bankCardTokenModel.token = %@",_phoneTokenModel.token);
        _phoneDownLoadGroup = nil;

        if ([_phoneTokenModel.version integerValue]>[_versionStr integerValue]) {
            [WeXPorgressHUD hideLoading];
            [self ipfsEncopyTips];
            return ;
        }
        //适配安卓的要求,aes加密密码用md5(32位)处理,iv用md5(16位)处理
        NSString *nonceStr = [WeXIpfsHelper getMd5_16Bit_String:_phoneTokenModel.nonce];
        WEXNSLOG(@"nonceStr = %@",nonceStr);
        NSString *passwardStr = [WexCommonFunc md5: passWord];;
        WEXNSLOG(@"passwardStr = %@", passwardStr);
        NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
        NSString *mainUrlStr = [user objectForKey:WEX_IPFS_MAIN_NONEURL];
        
        [AIIpfsSaveDataHelpObject getDataFromIpfs:_phoneTokenModel.token WithBaseUrl:mainUrlStr WithUserPassword:passwardStr  WithNonceStr:nonceStr WithType:AIIpfsSaveDataTypePhotoOprator Callback:^(NSMutableDictionary *saveDict) {
            WEXNSLOG(@"saveDict = %@",saveDict);
            if (!saveDict) {
                if (_surnAllGroup) {
                    dispatch_group_leave(_surnAllGroup);
                }
                return ;
            }
            WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
            NSString *statusStr = saveDict[@"mobileAuthenStatus"];
            if ([statusStr isEqualToString:@"DONE"] ) {
                model.mobileAuthenStatus =   WeXCreditMobileOperatorAuthenStatusTypeSuccess;
            }
            model.phoneTimeLimit  = saveDict[@"mobileAuthenExpired"]; //过期时间
            model.mobileAuthenOrderId = saveDict[@"mobileAuthenOrderid"];  //订单ID
            model.mobileAuthenNumber = saveDict[@"mobileAuthenNumber"] ;  //收款人姓名
//            model.userName = saveDict[@"mobileAuthenName"];
            
            NSString *contentStr = saveDict[@"mobileAuthenCmData"];

            WEXNSLOG(@"contentStr = %@",contentStr);
            WEXNSLOG(@"0.0");
           [WexCommonFunc saveToFile:WEX_MOBILE_AUTHEN_REPORT_KEY content:contentStr];
//
            [WexCommonFunc savePassport:model];
            if (_surnAllGroup) {
                dispatch_group_leave(_surnAllGroup);
            }
        }];
        
    });
    
}

- (void)changePwdBtnClick{
    WeXReSetIpfsPwdController *vc = [[WeXReSetIpfsPwdController alloc]init];
    vc.fromVc = self.fromVc;
    [self.navigationController pushViewController:vc animated:YES];
}



#pragma -mark 发送请求
- (void)createGetNonceRequest{
    _getNonceAdapter = [[WeXBorrowGetNonceAdapter alloc] init];
    _getNonceAdapter.delegate = self;
    WeXBorrowGetNonceParamModal* paramModal = [[WeXBorrowGetNonceParamModal alloc] init];
    [_getNonceAdapter run:paramModal];
}

#pragma -mark 发送获取orderdata请求
- (void)createGetIdentifyOrderDataRequest{
    _getIdentifyOrderDataAdapter = [[WeXCreditDccGetOrderDataAdapter alloc] init];
    _getIdentifyOrderDataAdapter.delegate = self;
    WeXCreditDccGetOrderDataParamModal* paramModal = [[WeXCreditDccGetOrderDataParamModal alloc] init];
    paramModal.address = [WexCommonFunc getFromAddress];
    paramModal.business = WEX_DCC_AUTHEN_BUSINESS_ID;
    [_getIdentifyOrderDataAdapter run:paramModal];
}

- (void)createGetBankOrderDataRequest{
    _getBankCardOrderDataAdapter = [[WeXCreditDccGetOrderDataAdapter alloc] init];
    _getBankCardOrderDataAdapter.delegate = self;
    WeXCreditDccGetOrderDataParamModal* paramModal = [[WeXCreditDccGetOrderDataParamModal alloc] init];
    paramModal.address = [WexCommonFunc getFromAddress];
    paramModal.business = WEX_DCC_AUTHEN_BUSINESS_BANK_CARD;
    [_getBankCardOrderDataAdapter run:paramModal];
    
}

- (void)createGetPhoneOrderDataRequest{
    _getPhoneOrderDataAdapter = [[WeXCreditDccGetOrderDataAdapter alloc] init];
    _getPhoneOrderDataAdapter.delegate = self;
    WeXCreditDccGetOrderDataParamModal* paramModal = [[WeXCreditDccGetOrderDataParamModal alloc] init];
    paramModal.address = [WexCommonFunc getFromAddress];
    paramModal.business = WEX_DCC_AUTHEN_BUSINESS_COMMUNICATION_LOG;
    WEXNSLOG(@"paramModal.business = %@",paramModal.business);
    [_getPhoneOrderDataAdapter run:paramModal];
    
}

#pragma -mark 发送获取合约地址请求
- (void)createGetIdentifyContractAddressRequest{
    _getIDContractAddressAdapter = [[WeXCreditDccGetContractAddressAdapter alloc] init];
    _getIDContractAddressAdapter.delegate = self;
    WeXGetContractAddressParamModal* paramModal = [[WeXGetContractAddressParamModal alloc] init];
    paramModal.business = WEX_DCC_AUTHEN_BUSINESS_ID;
    [_getIDContractAddressAdapter run:paramModal];
}

- (void)createGetBankCardContractAddressRequest{
    _getBankContractAddressAdapter = [[WeXCreditDccGetContractAddressAdapter alloc] init];
    _getBankContractAddressAdapter.delegate = self;
    WeXGetContractAddressParamModal* paramModal = [[WeXGetContractAddressParamModal alloc] init];
    paramModal.business = WEX_DCC_AUTHEN_BUSINESS_BANK_CARD;
    [_getBankContractAddressAdapter run:paramModal];
}

- (void)createGetPhoneOptaContractAddressRequest{
    _getPhoneContractAddressAdapter = [[WeXCreditDccGetContractAddressAdapter alloc] init];
    _getPhoneContractAddressAdapter.delegate = self;
    WeXGetContractAddressParamModal* paramModal = [[WeXGetContractAddressParamModal alloc] init];
    paramModal.business = WEX_DCC_AUTHEN_BUSINESS_COMMUNICATION_LOG;
    [_getPhoneContractAddressAdapter run:paramModal];
}

#pragma -mark 查询上链结果请求
- (void)createIdentifyReceiptResultRequest:(NSString *)txHash{
    
    _getIdnetifyReceiptAdapter = [[WeXGetReceiptResult2Adapter alloc] init];
    _getIdnetifyReceiptAdapter.delegate = self;
    WeXGetReceiptResult2ParamModal* paramModal = [[WeXGetReceiptResult2ParamModal alloc] init];
    paramModal.txHash = txHash;
    [ _getIdnetifyReceiptAdapter run:paramModal];
}

- (void)createBankReceiptResultRequest:(NSString *)txHash{
    
    _getBankReceiptAdapter = [[WeXGetReceiptResult2Adapter alloc] init];
    _getBankReceiptAdapter.delegate = self;
    WeXGetReceiptResult2ParamModal* paramModal = [[WeXGetReceiptResult2ParamModal alloc] init];
    paramModal.txHash = txHash;
    [_getBankReceiptAdapter run:paramModal];
}

- (void)createPhoneReceiptResultRequest:(NSString *)txHash{
    
    _getPhoneReceiptAdapter = [[WeXGetReceiptResult2Adapter alloc] init];
    _getPhoneReceiptAdapter .delegate = self;
    WeXGetReceiptResult2ParamModal* paramModal = [[WeXGetReceiptResult2ParamModal alloc] init];
    paramModal.txHash = txHash;
    [_getPhoneReceiptAdapter  run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    
    if (adapter == _getIpfsHashGetAdapter) {
        NSLog(@"response = %@",response);
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
           WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
           NSLog(@"model.result = %@",model.result);
           _contractAddress = model.result;
           dispatch_group_leave(_getAllContractGroup);
        }else{
           [WeXPorgressHUD hideLoading];
           [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
        
    }
    else if (adapter == _getIDContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _identifyContractAddress = model.result;
            if (_getAllContractGroup) {
               dispatch_group_leave(_getAllContractGroup);
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getBankContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _bankCardContractAddress = model.result;
            //合约地址请求成功
            if (_bankCardContractAddress) {
                 dispatch_group_leave(_getAllContractGroup);
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getPhoneContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _phoneContractAddress = model.result;
            //合约地址请求成功
            if (_phoneContractAddress) {
             dispatch_group_leave(_getAllContractGroup);
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getIdentifyOrderDataAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WEXNSLOG(@"response =%@",response);
            WeXCreditDccGetOrderDataResponseModal *model =  (WeXCreditDccGetOrderDataResponseModal *)response;
            _identifyDigest1 = model.content.digest1;
            _identifyDigest2 = model.content.digest2;
            if (_identifyCompareLocalGroup) {
                dispatch_group_leave(_identifyCompareLocalGroup);
            }
            if (_identifyCompareIpfsGroup) {
                dispatch_group_leave(_identifyCompareIpfsGroup);
            }
            
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getBankCardOrderDataAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WEXNSLOG(@"response =%@",response);
            WeXCreditDccGetOrderDataResponseModal *model =  (WeXCreditDccGetOrderDataResponseModal *)response;
            _bankCardDigest1 = model.content.digest1;
            _bankCardDigest2 = model.content.digest2;
            _bankCardExpired = model.content.expired;
            if (_bankCardCompareLocalGroup) {
                dispatch_group_leave(_bankCardCompareLocalGroup);
            }
            if (_bankCardCompareIpfsGroup) {
                dispatch_group_leave(_bankCardCompareIpfsGroup);
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getPhoneOrderDataAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WEXNSLOG(@"response =%@",response);
            WeXCreditDccGetOrderDataResponseModal *model =  (WeXCreditDccGetOrderDataResponseModal *)response;
            _phoneOperatorDigest1 = model.content.digest1;
            _phoneOperatorDigest2 = model.content.digest2;
            _phoneOperatorExpired = model.content.expired;
            if (_phoneCompareLocalGroup) {
                dispatch_group_leave(_phoneCompareLocalGroup);
            }
            if (_phoneCompareIpfsGroup) {
                dispatch_group_leave(_phoneCompareIpfsGroup);

            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getIdnetifyReceiptAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WeXGetReceiptResult2ResponseModal *model = (WeXGetReceiptResult2ResponseModal *)response;
            //上链成功
            WEXNSLOG(@"0.0");
            if (model.hasReceipt && model.approximatelySuccess) {
                //上链成功
//                [WeXPorgressHUD hideLoading];
                WEXNSLOG(@"上链成功");
                WEXNSLOG(@"0.0");
                if (_surnAllGroup) {
                    dispatch_group_leave(_surnAllGroup);
                }
            } else{
                if (_identifyRequestCount > 10) {
                    _identifyRequestCount = 0;
//                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:WeXLocalizedString(@"实名认证上传失败") onView:self.view];
                    if (_surnAllGroup) {
                        dispatch_group_leave(_surnAllGroup);
                    }
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createIdentifyReceiptResultRequest:_identifyTxHash];
                     _identifyRequestCount++;
                });
            }
        }
        else
        {
//            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"实名认证上传失败") onView:self.view];
            if (_surnAllGroup) {
                dispatch_group_leave(_surnAllGroup);
            }
        }
    }
    else if (adapter == _getBankReceiptAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WeXGetReceiptResult2ResponseModal *model = (WeXGetReceiptResult2ResponseModal *)response;
            //上链成功
            WEXNSLOG(@"0.0");
            if (model.hasReceipt && model.approximatelySuccess) {
                //上链成功
//                [WeXPorgressHUD hideLoading];
                WEXNSLOG(@"上链成功");
                WEXNSLOG(@"0.0");
                if (_surnAllGroup) {
                    dispatch_group_leave(_surnAllGroup);
                }
            } else{
                if (_bankRequestCount > 10) {
                    _bankRequestCount = 0;
//                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:WeXLocalizedString(@"银行卡认证上传失败") onView:self.view];
                    if (_surnAllGroup) {
                        dispatch_group_leave(_surnAllGroup);
                    }
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createBankReceiptResultRequest:_bankTxHash];
                    _bankRequestCount++;
                });
            }
        }
        else
        {
//            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"银行卡认证上传失败") onView:self.view];
            if (_surnAllGroup) {
                dispatch_group_leave(_surnAllGroup);
            }
        }
    }
    else if (adapter == _getPhoneReceiptAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WeXGetReceiptResult2ResponseModal *model = (WeXGetReceiptResult2ResponseModal *)response;
            //上链成功
            WEXNSLOG(@"0.0");
            if (model.hasReceipt && model.approximatelySuccess) {
                //上链成功
//                [WeXPorgressHUD hideLoading];
                WEXNSLOG(@"上链成功");
                WEXNSLOG(@"0.0");
                if (_surnAllGroup) {
                    dispatch_group_leave(_surnAllGroup);
                }
            } else{
                if (_phoneRequestCount > 10) {
                    _phoneRequestCount = 0;
//                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:WeXLocalizedString(@"运营商认证上传失败") onView:self.view];
                    if (_surnAllGroup) {
                        dispatch_group_leave(_surnAllGroup);
                    }
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createPhoneReceiptResultRequest:_phoneTxHash];
                    _phoneRequestCount++;
                });
            }
        }
        else
        {
            if (_surnAllGroup) {
                dispatch_group_leave(_surnAllGroup);
            }
//            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"运营商认证上传失败") onView:self.view];
        }
    }
}

#pragma -mark 实名数据的ipfs合约读写
- (void)getIdentifyRawTranstion{
    
    // 合约定义说明
    NSString* abiJson = WEX_IPFS_ABI_GET_TOKEN;
    // 合约参数值 第一个参数为version 暂时为1
    NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\']",_identifyContractAddress];
    WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
    // 合约地址
    NSString *abiAddress = _contractAddress;
//    WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
    WEXNSLOG(@"abiAddress=%@",abiAddress);
    NSString *addressStr = [WexCommonFunc getFromAddress];
    WEXNSLOG(@"addressStr =%@",addressStr);
    [[WXPassHelper instance] initPassHelperBlock:^(id response)
     {
         if(response!=nil)
         {
             NSError* error=response;
             NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
             return;
         }
         [[WXPassHelper instance] initProvider:WEX_IPFS_HASHTOKEN_URL responseBlock:^(id response) {
             [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:abiParamsValues responseBlock:^(id response) {
                 [[WXPassHelper instance] call4ContractAddress:abiAddress data:response walletAddress:addressStr responseBlock:^(id response) {
                     NSDictionary *responseDict = response;
                     NSString *orginStr = responseDict[@"result"];
                     WEXNSLOG(@"orginStr = %@",orginStr);
                     if ([WeXIpfsHelper isBlankString:orginStr]) {
                         return ;
                     }
                     [[WXPassHelper instance]getIpfsEncodeWithOriginalString:orginStr ResponseBlock:^(id response) {
                         WEXNSLOG(@"response = %@",response);
                         NSDictionary *dict = response;
                         WEXNSLOG(@"dict = %@",dict);
                         if (!dict) {
                             return;
                         }
                         
                         _identifyTokenModel = [[WeXIpfsTokenModel alloc]init];
                         _identifyTokenModel.walletAddress = dict[@"0"];
                         _identifyTokenModel.contractAddress = dict[@"1"];
                         _identifyTokenModel.version = dict[@"2"];
                         _identifyTokenModel.cipher = dict[@"3"];
                         _identifyTokenModel.token = dict[@"4"];
                         _identifyTokenModel.nonce = dict[@"5"];
                         _identifyTokenModel.digest1 = dict[@"6"];
                         _identifyTokenModel.digest2 = dict[@"7"];
                         WEXNSLOG(@"_identifyTokenModel = %@",_identifyTokenModel);
                         WEXNSLOG(@"0.0");
                         if (_identifyTokenModel.token.length > 0 ) {
                             NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                             [user setObject:_identifyTokenModel.token forKey:WEX_IPFS_IDENTIFY_HASH];
                         }
                         if ( _identifyDownLoadGroup) {
                             dispatch_group_leave(_identifyDownLoadGroup);
//                             _identifyDownLoadGroup = nil;
                         }
                         if (_identifyCompareIpfsGroup) {
                              dispatch_group_leave(_identifyCompareIpfsGroup);
//                             _identifyCompareIpfsGroup = nil;
                         }
                        
                     }];
                 }];
             }];
         }];
     }];
}

- (void)postIdentifyRawTranstion
{
    
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response)
     {
         if(response!=nil)
         {
             NSError* error=response;
             NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
             return;
         }
//         NSString *address = [WexCommonFunc getFromAddress];
         
         WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
         NSMutableData *data1 = [cacheModel.userName dataUsingEncoding:NSUTF8StringEncoding].mutableCopy;
         NSData *data2 = [cacheModel.userNumber dataUsingEncoding:NSUTF8StringEncoding];
         [data1 appendData:data2];
         
         NSData *data3 = [WexCommonFunc dataShaSHAWithData:data1];
         NSString *digest1 = [WexCommonFunc stringSHA256WithData:data1];
         _identifyTokenDigest1 = [NSString stringWithFormat:@"0x%@",digest1];
         
         NSData *frontData = [WexCommonFunc dataShaSHAWithData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_FRONT_KEY]];
         NSData *backData = [WexCommonFunc dataShaSHAWithData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_BACK_KEY]];
         NSData *headData = [WexCommonFunc dataShaSHAWithData:[WexCommonFunc idDataWithName:WEX_ID_IMAGE_HEAD_KEY]];
         
         NSMutableData *data5 = [NSMutableData dataWithData:data3];
         [data5 appendData:frontData];
         [data5 appendData:backData];
         [data5 appendData:headData];
         NSString *digest2 = [WexCommonFunc stringSHA256WithData:data5];
         _identifyTokenDigest2 = [NSString stringWithFormat:@"0x%@",digest2];

         NSString *version = @"1" ;
         // 合约定义说明
         NSString *abiJson = WEX_IPFS_ABI_POST_TOKEN;
         NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
         NSString *passWord = [user objectForKey:WEX_IPFS_MY_CHECKKEY];
         passWord = [NSString stringWithFormat:@"0x%@",passWord];
         // 合约参数值
         NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\',\'%@\',\'%@\',\'%@\',\'%@\',\'%@\',\'%@\',\'%@\']",_identifyContractAddress,version,@"AES256",[NSString stringWithFormat:@"%@", _identifyHash],_idendifyNonce,_identifyTokenDigest1,_identifyTokenDigest2,passWord];
         WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
         // 合约地址
         NSString* abiAddress = _contractAddress;
//         WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
         [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:cacheModel.walletPrivateKey responseBlock:^(id response)
          {
              NSLog(@"rawTransaction=%@",response);
              NSString *rawTransaction = response;
              [[WXPassHelper instance] initProvider:WEX_IPFS_HASHTOKEN_URL responseBlock:^(id response) {
                  [[WXPassHelper instance] sendRawTransaction:rawTransaction responseBlock:^(id response) {
                      NSLog(@ "reponse = %@",response);
                      _identifyTxHash = response;
                      
                      [self createIdentifyReceiptResultRequest:_identifyTxHash];
                      
//                      if (_surnAllGroup) {
//                          dispatch_group_leave(_surnAllGroup);
//                      }
                  }];
              }];
          }];
      }];
//  }];
}

#pragma -mark 银行卡的ipfs合约读写
- (void)getBankRawTranstion{
    // 合约定义说明
    NSString* abiJson = WEX_IPFS_ABI_GET_TOKEN;
    // 合约参数值 第一个参数为version 暂时为1
    NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\']",_bankCardContractAddress];
    WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
    // 合约地址
    NSString* abiAddress = _contractAddress;
       
    WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
    NSString *addressStr = [WexCommonFunc getFromAddress];
    
    [[WXPassHelper instance] initPassHelperBlock:^(id response)
     {
         if(response!=nil)
         {
             NSError* error=response;
             NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
             return;
         }
         [[WXPassHelper instance] initProvider:WEX_IPFS_HASHTOKEN_URL responseBlock:^(id response) {
             [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:abiParamsValues responseBlock:^(id response) {
                 [[WXPassHelper instance] call4ContractAddress:abiAddress data:response walletAddress:addressStr responseBlock:^(id response) {
                     NSDictionary *responseDict = response;
                     NSString *orginStr = responseDict[@"result"];
                     WEXNSLOG(@"orginStr = %@",orginStr);
                     if ([WeXIpfsHelper isBlankString:orginStr]) {
                         return ;
                     }
                     [[WXPassHelper instance]getIpfsEncodeWithOriginalString:orginStr ResponseBlock:^(id response) {
                         WEXNSLOG(@"response = %@",response);
                         NSDictionary *dict = response;
                         WEXNSLOG(@"dict = %@",dict);
                         if(!dict){
                             return ;
                         }
                          _bankCardTokenModel = [[WeXIpfsTokenModel alloc]init];
                          _bankCardTokenModel.walletAddress = dict[@"0"];
                          _bankCardTokenModel.contractAddress = dict[@"1"];
                          _bankCardTokenModel.version = dict[@"2"];
                          _bankCardTokenModel.cipher = dict[@"3"];
                          _bankCardTokenModel.token = dict[@"4"];
                          _bankCardTokenModel.nonce = dict[@"5"];
                          _bankCardTokenModel.digest1 = dict[@"6"];
                          _bankCardTokenModel.digest2 = dict[@"7"];
                          WEXNSLOG(@"_bankCardTokenModel = %@",_bankCardTokenModel);
                          WEXNSLOG(@"0.0");
                         //多设备同步时需要存储hash
                         if (_bankCardTokenModel.token.length > 0 ) {
                             NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                             [user setObject:_bankCardTokenModel.token forKey:WEX_IPFS_BankCard_HASH];
                         }
                         if (_bankCardDownLoadGroup) {
                             dispatch_group_leave(_bankCardDownLoadGroup);
//                             _bankCardDownLoadGroup = nil;
                         }
                          if (_bankCardCompareIpfsGroup) {
                              dispatch_group_leave(_bankCardCompareIpfsGroup);
//                              _bankCardCompareIpfsGroup = nil;
                          }
                      
                     }];
                 }];
             }];
         }];
     }];
}

- (void)postBankRawTranstion{
    
    [[WXPassHelper instance] initPassHelperBlock:^(id response)
     {
         if(response!=nil)
         {
             NSError* error=response;
             NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
             return;
         }
         
        WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
    
        NSMutableData *data1 = [cacheModel.bankAuthenCardNumber dataUsingEncoding:NSUTF8StringEncoding].mutableCopy;
        NSData *data2 = [cacheModel.bankAuthenMobile dataUsingEncoding:NSUTF8StringEncoding];
        [data1 appendData:data2];
             
        NSString *digest1 = [WexCommonFunc stringSHA256WithData:data1];
        _bankCardTokenDigest1  = [NSString stringWithFormat:@"0x%@",digest1];
    
        NSString *version = @"1" ;
        // 合约定义说明
        NSString *abiJson = WEX_IPFS_ABI_POST_TOKEN;
         NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
         NSString *passWord = [user objectForKey:WEX_IPFS_MY_CHECKKEY];
         passWord = [NSString stringWithFormat:@"0x%@",passWord];
        _bankCardTokenDigest2 = @"0x";
        // 合约参数值
        NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\',\'%@\',\'%@\',\'%@\',\'%@\',\'%@\',\'%@\',\'%@\']",_bankCardContractAddress,version,@"AES256",[NSString stringWithFormat:@"%@", _bankCardHash],_bankCardNonce,_bankCardTokenDigest1,_bankCardTokenDigest2,passWord];
        WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
        // 合约地址
        NSString* abiAddress = _contractAddress;
//      WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
             [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:cacheModel.walletPrivateKey responseBlock:^(id response)
              {
                  NSLog(@"rawTransaction=%@",response);
                  NSString *rawTransaction = response;
                  [[WXPassHelper instance] initProvider:WEX_IPFS_HASHTOKEN_URL responseBlock:^(id response) {
                      [[WXPassHelper instance] sendRawTransaction:rawTransaction responseBlock:^(id response) {
                          NSLog(@ "reponse = %@",response);
                          _bankTxHash = response;
                          [self createBankReceiptResultRequest:_bankTxHash];
//                          if (_surnAllGroup) {
//                              dispatch_group_leave(_surnAllGroup);
//                          }
                        
                      }];
                  }];
              }];
     }];
    
}

#pragma -mark 手机运营商的ipfs合约读写
- (void)getPhoneRawTranstion{
    
    // 合约定义说明
    NSString* abiJson = WEX_IPFS_ABI_GET_TOKEN;
    // 合约参数值 第一个参数为version 暂时为1
    NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\']",_phoneContractAddress];
    WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
    // 合约地址
    NSString* abiAddress = _contractAddress;
    
    WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
    NSString *addressStr = [WexCommonFunc getFromAddress];
    
    [[WXPassHelper instance] initPassHelperBlock:^(id response)
     {
         if(response!=nil)
         {
             NSError* error=response;
             NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
             return;
         }
         [[WXPassHelper instance] initProvider:WEX_IPFS_HASHTOKEN_URL responseBlock:^(id response) {
             [[WXPassHelper instance] encodeFunCallAbiInterface:abiJson params:abiParamsValues responseBlock:^(id response) {
                 [[WXPassHelper instance] call4ContractAddress:abiAddress data:response walletAddress:addressStr responseBlock:^(id response) {
                     NSDictionary *responseDict = response;
                     NSString *orginStr = responseDict[@"result"];
                     WEXNSLOG(@"orginStr = %@",orginStr);
                     if ([WeXIpfsHelper isBlankString:orginStr]) {
                         return ;
                     }
                     [[WXPassHelper instance]getIpfsEncodeWithOriginalString:orginStr ResponseBlock:^(id response) {
                         WEXNSLOG(@"response = %@",response);
                         NSDictionary *dict = response;
                         WEXNSLOG(@"dict = %@",dict);
                         if (!dict) {
                             return;
                         }
                         _phoneTokenModel = [[WeXIpfsTokenModel alloc]init];
                         _phoneTokenModel.walletAddress = dict[@"0"];
                         _phoneTokenModel.contractAddress = dict[@"1"];
                         _phoneTokenModel.version = dict[@"2"];
                         _phoneTokenModel.cipher = dict[@"3"];
                         _phoneTokenModel.token = dict[@"4"];
                         _phoneTokenModel.nonce = dict[@"5"];
                         _phoneTokenModel.digest1 = dict[@"6"];
                         _phoneTokenModel.digest2 = dict[@"7"];
                         
                         //多设备同步时需要存储hash
                         if (_phoneTokenModel.token.length > 0 ) {
                             NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
                             [user setObject:_phoneTokenModel.token forKey:WEX_IPFS_PhoneOperator_HASH];
                         }
                         WEXNSLOG(@"_phoneTokenModel = %@",_phoneTokenModel);
                         WEXNSLOG(@"0.0");
                         if (_phoneDownLoadGroup) {
                             dispatch_group_leave(_phoneDownLoadGroup);

                         }
                         if (_phoneCompareIpfsGroup) {
                             dispatch_group_leave(_phoneCompareIpfsGroup);

                         }
                         
                     }];
                 }];
             }];
         }];
     }];
}
- (void)postPhoneRawTranstion{
    
    [[WXPassHelper instance] initPassHelperBlock:^(id response)
     {
         if(response!=nil)
         {
             NSError* error=response;
             NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
             return;
         }
         
         WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
         NSString *digestStr =  [WexCommonFunc getContentWithFileName:WEX_MOBILE_AUTHEN_REPORT_KEY];
         NSData *reportData = [[NSData alloc] initWithBase64EncodedString:digestStr options:0];
         
         NSString *digest1 = [WexCommonFunc stringSHA256WithData:reportData];
         _phoneOperatorTokenDigest1  = [NSString stringWithFormat:@"0x%@",digest1];
         _phoneOperatorTokenDigest2  = @"0x";
         NSString *version = @"1" ;
         // 合约定义说明
         NSString *abiJson = WEX_IPFS_ABI_POST_TOKEN;
         NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
         NSString *passWord = [user objectForKey:WEX_IPFS_MY_CHECKKEY];
         passWord = [NSString stringWithFormat:@"0x%@",passWord];
         
        // 合约参数值
        NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\',\'%@\',\'%@\',\'%@\',\'%@\',\'%@\',\'%@\',\'%@\']",_phoneContractAddress,version,@"AES256",[NSString stringWithFormat:@"%@", _phoneOperatorHash],_phoneNonce,_phoneOperatorTokenDigest1,_phoneOperatorTokenDigest2,passWord];
             WEXNSLOG(@"abiParamsValues=%@",abiParamsValues);
             // 合约地址
             NSString* abiAddress = _contractAddress;
       
             [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:cacheModel.walletPrivateKey responseBlock:^(id response)
              {
                  NSLog(@"rawTransaction=%@",response);
                  NSString *rawTransaction = response;
                  [[WXPassHelper instance] initProvider:WEX_IPFS_HASHTOKEN_URL responseBlock:^(id response) {
                      [[WXPassHelper instance] sendRawTransaction:rawTransaction responseBlock:^(id response) {
                          NSLog(@ "reponse = %@",response);
                          _phoneTxHash = response;
                          [self createPhoneReceiptResultRequest:_phoneTxHash];

                      }];
                  }];
              }];
         }];
    
}


#pragma mark -- 懒加载
- (NSMutableArray *)dataArray{
    if (_dataArray == nil) {
        _dataArray = [NSMutableArray array];
    }
    return _dataArray;
}

- (NSMutableArray *)selectDataArray{
    if (_selectDataArray == nil) {
        _selectDataArray = [NSMutableArray array];
    }
    return _selectDataArray;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark -- 刷新同步按钮的状态
- (void)refreshSynchBtnStatus{
    if (_selectDataArray.count>0) {
        [_synchBtn setBackgroundColor:ColorWithHex(0x6766CC)];
        _synchBtn.enabled = YES;
    }else{
        [_synchBtn setBackgroundColor:ColorWithHex(0x9B9B9B)];
        _synchBtn.enabled = NO;
    }
}

#pragma mark -- 问号提示框
- (void)addTipsView{
    
    if (_backView) {
        return;
    }
    _backView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight)];
    _backView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
    [self.view addSubview:_backView];
    if (_tipView) {
        return;
    }
    _tipView = [[UIView alloc]initWithFrame:CGRectMake(kScreenWidth*0.15, kScreenHeight*0.3, kScreenWidth*0.7, kScreenHeight*0.4)];
    _tipView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:_tipView];
    _tipView.layer.cornerRadius = 6;
    _tipView.clipsToBounds = YES;
    
    UILabel *defultLabel = [[UILabel alloc]init];
    defultLabel.text = @"可同步数据";
    defultLabel.font = [UIFont systemFontOfSize:16.0];
    defultLabel.textColor = ColorWithHex(0x4A4A4A);
    defultLabel.textAlignment = NSTextAlignmentCenter;
    [self.tipView addSubview:defultLabel];
    [defultLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.tipView).offset(10);
        make.centerX.equalTo(self.tipView);
        make.width.mas_offset(100);
        make.height.mas_offset(40);
    }];
    
    UITextView *displayView = [[UITextView alloc]init];
    displayView.textColor = ColorWithHex(0x4A4A4A);
    displayView.font = [UIFont systemFontOfSize:14.0];
    displayView.editable = NO;
    displayView.text = @"目前仅支持认证数据同步到云端。若认证数据和链上最新记录一致,即可上传或下载,如不一致,即便本地或云端有数据,也无法上传或下载,建议重新认证后再上传。";
    [self.tipView addSubview:displayView];
    [displayView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(defultLabel.mas_bottom).offset(1);
        make.right.equalTo(self.tipView).offset(-10);
        make.left.equalTo(self.tipView).offset(10);
        make.height.mas_equalTo(120);
    }];
    
    UIButton *deleteBtn = [[UIButton alloc]init];
    [deleteBtn setTitle:@"我知道了" forState:UIControlStateNormal];
    [deleteBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [deleteBtn setBackgroundColor:ColorWithHex(0x6766CC)];
    deleteBtn.layer.cornerRadius = 6;
    deleteBtn.clipsToBounds = YES;
    [deleteBtn addTarget:self action:@selector(removeBackViewClick) forControlEvents:UIControlEventTouchUpInside];
    [self.tipView addSubview:deleteBtn];
    [deleteBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(displayView.mas_bottom).offset(2);
        make.centerX.equalTo(self.tipView);
        make.width.mas_offset(120);
        make.height.mas_offset(40);
    }];
}

- (void)removeBackViewClick{
    [self.backView removeFromSuperview];
    self.backView = nil;
    [self.tipView removeFromSuperview];
    self.tipView = nil;
}

- (void)createGetIpfsHashRequest{
    _getIpfsHashGetAdapter = [[WeXIpfsHashGetAdapter alloc] init];
    _getIpfsHashGetAdapter.delegate = self;
    WeXIpfsKeyGetModel *paramModal = [[WeXIpfsKeyGetModel alloc] init];
    [ _getIpfsHashGetAdapter run:paramModal];
}

- (void)ipfsEncopyTips{
    
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"消息提示" message:@"云存储数据加密算法已经升级，为确保该功能可以继续使用,请更新APP到最新版本。" preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *delete = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
    }];
    [alert addAction:delete];
    [self presentViewController:alert animated:YES completion:nil];
}



/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
