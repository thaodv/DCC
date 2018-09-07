//
//  WeXPassportIDFaceController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDFaceController.h"
#import "WeXPassportIDCommitSuccessController.h"
#import <MGLivenessDetection/MGLivenessDetection.h>

#import <CommonCrypto/CommonDigest.h>
#import "WeXAuthenGetContractAddressAdapter.h"
#import "WeXAuthenGetTicketAdapter.h"
#import "WeXUploadAuthenticationAdapter.h"
#import "WeXAuthenGetOrderIdAdapter.h"
#import "WeXAuthenVerifyAdapter.h"

#import "WeXGraphView.h"

#import "WeXPassportIDCacheInfoModal.h"


@interface WeXPassportIDFaceController ()
{
    WeXCustomButton *_faceBtn;
    UIImageView *_faceImageView;
    UILabel *_remindLabel;
    
    NSString *_rawTransaction;
    
    NSString *_txHash;
    
    NSString *_orderId;
    
    NSString *_contractAddress;//合约地址
    
    NSString *_digest1;
    NSString *_digest2;
    
    NSInteger _requestCount;//查询上链结果请求的次数
    
    WeXGraphView *_graphView;//验证码试图
    WeXGetTicketResponseModal *_getTicketModel;
}
@property (nonatomic,strong)WeXAuthenGetTicketAdapter *getTicketAdapter;
@property (nonatomic,strong)WeXAuthenGetContractAddressAdapter *getContractAddressAdapter;
@property (nonatomic,strong)WeXUploadAuthenticationAdapter *uploadAuthenticationAdapter;
@property (nonatomic,strong)WeXAuthenGetOrderIdAdapter *getOrderIdAdapter;
@property (nonatomic,strong)WeXAuthenVerifyAdapter *getVerifyAdapter;
@end

@implementation WeXPassportIDFaceController

- (void)viewDidLoad {
    [super viewDidLoad];
    _requestCount = 1;
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    
//    [self createGetOrderIdRequest];
}


#pragma -mark 发送请求
- (void)createGetTicketRequest{
    _getTicketAdapter = [[WeXAuthenGetTicketAdapter alloc] init];
    _getTicketAdapter.delegate = self;
    WeXGetTicketParamModal* paramModal = [[WeXGetTicketParamModal alloc] init];
    [_getTicketAdapter run:paramModal];
}

#pragma -mark 发送请求
- (void)createGetVerifyRequest{
    _getVerifyAdapter = [[WeXAuthenVerifyAdapter alloc] init];
    _getVerifyAdapter.delegate = self;
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    
    WeXPassportIDCacheInfoModal *infoModel = [WeXPassportIDCacheInfoModal sharedInstance];
    
    UIImage *image = [UIImage imageNamed:@"digital_authe_success"];
    
    [dict setObject:UIImageJPEGRepresentation(image, 0.8) forKey:@"uploadImageData"];
    
    [dict setObject:_orderId forKey:@"orderId"];
    [dict setObject:infoModel.userName forKey:@"realName"];
    [dict setObject:infoModel.userNumber forKey:@"certNo"];
    NSLog(@"dict=%@",dict);
    [_getVerifyAdapter runImage:dict];
}

#pragma -mark 发送获取orderid请求
- (void)createGetOrderIdRequest{
    _getOrderIdAdapter = [[WeXAuthenGetOrderIdAdapter alloc] init];
    _getOrderIdAdapter.delegate = self;
    WeXAuthenGetOrderIdParamModal* paramModal = [[WeXAuthenGetOrderIdParamModal alloc] init];
    paramModal.txHash = _txHash;
    paramModal.business = @"ID";
    [_getOrderIdAdapter run:paramModal];
}

#pragma -mark 发送上传资料请求
- (void)createUploadAuthenticationRequest{
    _uploadAuthenticationAdapter = [[WeXUploadAuthenticationAdapter alloc] init];
    _uploadAuthenticationAdapter.delegate = self;
    WeXUploadAuthenticationParamModal* paramModal = [[WeXUploadAuthenticationParamModal alloc] init];
    paramModal.ticket = _getTicketModel.ticket;
    paramModal.signMessage = _rawTransaction;
    paramModal.business = @"ID";
//    paramModal.code = _graphView.graphTextField.text;
    [_uploadAuthenticationAdapter run:paramModal];
}


#pragma -mark 发送获取合约地址请求
- (void)createGetContractAddressRequest{
    _getContractAddressAdapter = [[WeXAuthenGetContractAddressAdapter alloc] init];
    _getContractAddressAdapter.delegate = self;
    WeXGetContractAddressParamModal* paramModal = [[WeXGetContractAddressParamModal alloc] init];
    paramModal.business = @"ID";
    [_getContractAddressAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getTicketAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
           WeXGetTicketResponseModal *getTicketModel = (WeXGetTicketResponseModal *)response;
//            [_graphView.graphBtn setImage:[WexCommonFunc imageWihtBase64String:getTicketModel.image] forState:UIControlStateNormal];
            _getTicketModel = getTicketModel;
            [self createGetContractAddressRequest];
        }
        
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
        
    }
    else if (adapter == _getContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _contractAddress = model.result;
            //合约地址请求成功
            if (_contractAddress) {
                [self getRawTranstion];
            }
            
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _uploadAuthenticationAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXUploadAuthenticationResponseModal *responseModel =(WeXUploadAuthenticationResponseModal *)response;
            _txHash = responseModel.result;
            if (_txHash) {
                [self createGetOrderIdRequest];
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getOrderIdAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXAuthenGetOrderIdResponseModal *responseModel =(WeXAuthenGetOrderIdResponseModal *)response;
            NSLog(@"%@",responseModel);
            //上链成功
            if (responseModel.orderId) {
                _orderId = responseModel.orderId;
                [self createGetVerifyRequest];
            }
            else
            {
                if (_requestCount > 8) {
                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:WeXLocalizedString(@"上传失败") onView:self.view];
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createGetOrderIdRequest];
                    _requestCount++;
                });
            }
            
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    else if (adapter == _getVerifyAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXAuthenVerifyResponseModal *responseModel =(WeXAuthenVerifyResponseModal *)response;
            NSLog(@"%@",responseModel);
            [WeXPorgressHUD hideLoading];
            [self saveInfo];
            WeXPassportIDCommitSuccessController *ctrl = [[WeXPassportIDCommitSuccessController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    
}


- (void)saveInfo
{
    WeXPassportIDCacheInfoModal *infoModel = [WeXPassportIDCacheInfoModal sharedInstance];
    
    [WexCommonFunc saveImageData:infoModel.frontIDData imageName:WEX_ID_IMAGE_FRONT_KEY];
    [WexCommonFunc saveImageData:infoModel.backIDData imageName:WEX_ID_IMAGE_BACK_KEY];
    [WexCommonFunc saveImageData:infoModel.userHeadData imageName:WEX_ID_IMAGE_HEAD_KEY];
    
    WeXPasswordCacheModal *passportModel =  [WexCommonFunc getPassport];
    
    passportModel.userAddress = infoModel.userAddress;
    passportModel.userName = infoModel.userName;
    passportModel.userSex = infoModel.userSex;
    passportModel.userNation = infoModel.userNation;
    passportModel.userNumber = infoModel.userNumber;
    passportModel.userTimeLimit = infoModel.userTimeLimit;
    passportModel.userAuthority = infoModel.userAuthority;
    passportModel.userYear = infoModel.userYear;
    passportModel.userMonth = infoModel.userMonth;
    passportModel.userDay = infoModel.userDay;
  
    passportModel.idAuthenTxHash = _txHash;
    [WexCommonFunc savePassport:passportModel];
}

- (void)createGraphFloatView{
    _graphView = [[WeXGraphView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:_graphView];
    [self createGetTicketRequest];
    
    __weak typeof(self) weakSelf = self;
    _graphView.comfirmBtnBlock = ^{
        [weakSelf createGetContractAddressRequest];
    };
    
    _graphView.graphBtnBlock = ^{
        [weakSelf createGetTicketRequest];
    };
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = WeXLocalizedString(@"数字身份证");
    titleLabel.font = [UIFont systemFontOfSize:25];
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@25);
    }];
    
    UILabel *label1 = [[UILabel alloc] init];
    label1.text = WeXLocalizedString(@"①拍摄身份证-②信息确认-③采集头像-④完成");
    label1.font = [UIFont systemFontOfSize:14];
    label1.textColor = [UIColor lightGrayColor];
    label1.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(@20);
    }];
    
    UILabel *label2 = [[UILabel alloc] init];
    label2.text = WeXLocalizedString(@"拍摄您本人人脸，请确保正对手机，光线充足");
    label2.font = [UIFont systemFontOfSize:15];
    label2.textColor = [UIColor whiteColor];
    label2.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:label2];
    [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label1.mas_bottom).offset(30);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(@20);
    }];
    _remindLabel = label2;
    
    UIImageView *faceImageView = [[UIImageView alloc] init];
    faceImageView.image = [UIImage imageNamed:@"digital_scan_face"];
    [self.view addSubview:faceImageView];
    [faceImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label1.mas_bottom).offset(65);
        make.leading.equalTo(self.view).offset(30);
        make.trailing.equalTo(self.view).offset(-30);
        make.height.equalTo(faceImageView.mas_width).multipliedBy(1);
    }];
    _faceImageView = faceImageView;
    
    WeXCustomButton *faceBtn = [WeXCustomButton button];
    [faceBtn setTitle:WeXLocalizedString(@"采集本人人脸") forState:UIControlStateNormal];
    faceBtn.layer.cornerRadius = 5;
    faceBtn.layer.masksToBounds = YES;
    [faceBtn setTitleColor:ColorWithRGB(248, 31, 117) forState:UIControlStateNormal];
    [faceBtn addTarget:self action:@selector(faceBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:faceBtn];
    [faceBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@170);
        make.height.equalTo(@50);
    }];
    _faceBtn = faceBtn;
    
}

- (void)faceBtnClick{
    if ([_faceBtn.titleLabel.text isEqualToString:WeXLocalizedString(@"采集本人人脸")]) {
        MGLiveManager *liveManager = [[MGLiveManager alloc] init];
        liveManager.actionCount = 3;
        liveManager.actionTimeOut = 10;
        liveManager.randomAction = NO;
        if (1) {
            NSMutableArray* actionMutableArray = [[NSMutableArray alloc] initWithCapacity:liveManager.actionCount];
            for (int i = 1; i <= liveManager.actionCount; i++) {
                [actionMutableArray addObject:[NSNumber numberWithInt:i]];
            }
            liveManager.actionArray = (NSArray *)actionMutableArray;
        }
        [liveManager startFaceDecetionViewController:self
                                              finish:^(FaceIDData *finishDic, UIViewController *viewController) {
                                                  [viewController dismissViewControllerAnimated:YES completion:nil];
                                                  NSData *resultData = [[finishDic images] valueForKey:@"image_best"];
                                                  UIImage *resultImage = [UIImage imageWithData:resultData];
                                                  if (resultImage) {
                                                      _faceImageView.image = resultImage;
                                                      _remindLabel.hidden = YES;
                                                      [_faceBtn setTitle:WeXLocalizedString(@"提交验证") forState:UIControlStateNormal];
                                                      WeXPassportIDCacheInfoModal *infoModel = [WeXPassportIDCacheInfoModal sharedInstance];
                                                      infoModel.userHeadData = UIImageJPEGRepresentation(resultImage, 0.8);
                                                      NSLog(@"length=%lu",(unsigned long)infoModel.userHeadData.length);
                                                  }
                                                  
                                              }
                                               error:^(MGLivenessDetectionFailedType errorType, UIViewController *viewController) {
                                                   [viewController dismissViewControllerAnimated:YES completion:nil];
                                                   NSLog(@"ERROR");
                                               }];
        
    }
    else
    {
        [WeXPorgressHUD showLoadingAddedTo:self.view];
        [self createGetTicketRequest];
        
        
//        WeXPassportIDCommitSuccessController *ctrl = [[WeXPassportIDCommitSuccessController alloc] init];
//        [self.navigationController pushViewController:ctrl animated:YES];
    }
    
   
    
}

- (void)getRawTranstion
{
    WeXPassportIDCacheInfoModal *infoModel = [WeXPassportIDCacheInfoModal sharedInstance];
    NSMutableData *data1 = [infoModel.userName dataUsingEncoding:NSUTF8StringEncoding].mutableCopy;
    NSData *data2 = [infoModel.userNumber dataUsingEncoding:NSUTF8StringEncoding];
    [data1 appendData:data2];

    NSData *data3 = [WexCommonFunc dataShaSHAWithData:data1];
    NSString *digest1 = [WexCommonFunc stringSHA256WithData:data1];
    _digest1 = [NSString stringWithFormat:@"0x%@",digest1];
    
    UIImage *image = [UIImage imageNamed:@"digital_authe_success"];
    NSData *imagedata = UIImageJPEGRepresentation(image, 0.8);
    
    NSLog(@"imagedata=%@",imagedata);
    NSData *data4 = [WexCommonFunc dataShaSHAWithData:imagedata];

    NSMutableData *data5 = [NSMutableData dataWithData:data3];
    [data5 appendData:data4];

    NSString *digest2 = [WexCommonFunc stringSHA256WithData:data5];
     _digest2 = [NSString stringWithFormat:@"0x%@",digest2];

    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response)
    {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(WeXLocalizedString(@"容器加载失败:%@"),error);
            return;
        }
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
        {
                // 合约定义说明
                NSString* abiJson= WEX_ERC20_ABI_APPLY;
                // 合约参数值
                NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\',\'%@\',\'%@\']",_digest1,_digest2,@"1519786445"];
                // 合约地址
                NSString* abiAddress=_contractAddress;
                WeXPasswordCacheModal *cacheModel = [WexCommonFunc getPassport];
                [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:cacheModel.walletPrivateKey responseBlock:^(id response)
                 {
                     NSLog(@"rawTransaction=%@",response);
                     _rawTransaction = response;
                     [self createUploadAuthenticationRequest];
                }];
            }];

        }];
}




@end
