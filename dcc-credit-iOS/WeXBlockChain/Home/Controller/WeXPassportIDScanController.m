//
//  WeXPassportIDScanController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDScanController.h"
#import "WeXPassportIDInfoController.h"
#import <MGIDCard/MGIDCard.h>

#import "WeXGetOcrInfoAdapter.h"
#import "WeXPassportIDCacheInfoModal.h"

@interface WeXPassportIDScanController ()
{
    UIImageView *_frontImageView;
    UIImageView *_backImageView;
    
    UIImageView *_sacnImageView1;
    UILabel *_scanLabel1;
    
    UIImageView *_sacnImageView2;
    UILabel *_scanLabel2;
    
    NSInteger _responseCount;
    WeXPassportIDCacheInfoModal *_infoModel;
}

@property (nonatomic,strong)WeXGetOcrInfoAdapter *getOcrInfoAdapter;

@end

@implementation WeXPassportIDScanController

- (void)viewDidLoad {
    [super viewDidLoad];
    _infoModel = [WeXPassportIDCacheInfoModal sharedInstance];
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    
    [MGLicenseManager licenseForNetWokrFinish:^(bool License) {
        NSLog(@"%@", [NSString stringWithFormat:@"授权%@", License ? @"成功" : @"失败"]);
    }];
    
    //    __unsafe_unretained ViewController *weakSelf = self;
//    BOOL idcard = [MGIDCardManager getLicense];
//    if (!idcard) {
//
//        return;
//    }
    
    
    
}

#pragma -mark 查询ocr信息请求
- (void)createGetOcrInfoRequest:(UIImage *)image{
   WeXGetOcrInfoAdapter *getOcrInfoAdapter = [[WeXGetOcrInfoAdapter alloc] init];
    getOcrInfoAdapter.delegate = self;
    
    NSData *imageData = UIImageJPEGRepresentation(image, 0.8f);
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:imageData forKey:@"uploadImageData"];
    [getOcrInfoAdapter runImage:dict];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if ([adapter isKindOfClass:[WeXGetOcrInfoAdapter class]]) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            NSLog(@"response=%@",response);
            WeXGetOcrInfoResponseModal *responseModel = (WeXGetOcrInfoResponseModal *)response;
            WeXGetOcrInfoResponseModal_item *infoModel = responseModel.idCardInfo;
            if (infoModel.address) {
                _infoModel.userAddress = infoModel.address;
            }
            if (infoModel.authority) {
                _infoModel.userAuthority = infoModel.authority;
            }
            if (infoModel.month) {
                _infoModel.userMonth = infoModel.month;
            }
            if (infoModel.day) {
                _infoModel.userDay = infoModel.day;
            }
            if (infoModel.name) {
                _infoModel.userName = infoModel.name;
            }
            if (infoModel.nation) {
                _infoModel.userNation = infoModel.nation;
            }
            if (infoModel.number) {
                _infoModel.userNumber = infoModel.number;
            }
            if (infoModel.sex) {
                _infoModel.userSex = infoModel.sex;
            }
            if (infoModel.timelimit) {
                _infoModel.userTimeLimit = infoModel.timelimit;
            }
            if (infoModel.year) {
                _infoModel.userYear = infoModel.year;
            }
            
        }
        else
        {
            
        }
        _responseCount++;
        if (_responseCount == 2) {
            WeXPassportIDInfoController *ctrl = [[WeXPassportIDInfoController alloc] init];
            [self.navigationController pushViewController:ctrl animated:YES];
        }
    }
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"数字身份证";
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
    label1.text = @"①拍摄身份证-②信息确认-③采集头像-④完成";
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
    label2.text = @"请拍摄你本人的身份证照片";
    label2.font = [UIFont systemFontOfSize:14];
    label2.textColor = [UIColor lightGrayColor];
    label2.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label2];
    [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label1.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@20);
    }];
    
    UIImageView *frontImageView = [[UIImageView alloc] init];
    frontImageView.layer.cornerRadius = 5;
    frontImageView.layer.masksToBounds = YES;
    frontImageView.layer.borderWidth = 1;
    frontImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    frontImageView.backgroundColor = [UIColor clearColor];
    frontImageView.userInteractionEnabled = YES;
    [self.view addSubview:frontImageView];
    [frontImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label2.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(frontImageView.mas_width).multipliedBy(0.5);
    }];
    _frontImageView = frontImageView;
    
    UITapGestureRecognizer *frontTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(frontTapClick)];
    [frontImageView addGestureRecognizer:frontTap];
    
    
    UIImageView *sacnImageView1 = [[UIImageView alloc] init];
    sacnImageView1.image = [UIImage imageNamed:@"digital_camera"];

    [self.view addSubview:sacnImageView1];
    [sacnImageView1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(frontImageView);
        make.centerY.equalTo(frontImageView).offset(-15);
    }];
    _sacnImageView1 = sacnImageView1;
    
    UILabel *scanLabel1 = [[UILabel alloc] init];
    scanLabel1.text = @"身份证正面扫描";
    scanLabel1.font = [UIFont systemFontOfSize:14];
    scanLabel1.textColor = [UIColor whiteColor];
    scanLabel1.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:scanLabel1];
    [scanLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(frontImageView);
        make.centerY.equalTo(frontImageView).offset(15);
    }];
    _scanLabel1 = scanLabel1;
    
    WeXPassportIDCacheInfoModal *infoModel = [WeXPassportIDCacheInfoModal sharedInstance];
    if (infoModel.frontIDData) {
        frontImageView.image = [UIImage imageWithData:infoModel.frontIDData scale:0.8];
        sacnImageView1.hidden = YES;
        scanLabel1.hidden = YES;
    }
    
    UIImageView *backImageView = [[UIImageView alloc] init];
    backImageView.layer.cornerRadius = 5;
    backImageView.layer.masksToBounds = YES;
    backImageView.layer.borderWidth = 1;
    backImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    backImageView.backgroundColor = [UIColor clearColor];
    backImageView.userInteractionEnabled = YES;
    [self.view addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(frontImageView.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(frontImageView.mas_width).multipliedBy(0.5);
    }];
    _backImageView = backImageView;
    
    UITapGestureRecognizer *backTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(backTapClick)];
    [backImageView addGestureRecognizer:backTap];
    
    UIImageView *sacnImageView2 = [[UIImageView alloc] init];
    sacnImageView2.image = [UIImage imageNamed:@"digital_camera"];

    [self.view addSubview:sacnImageView2];
    [sacnImageView2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(backImageView);
        make.centerY.equalTo(backImageView).offset(-15);
    }];
    _sacnImageView2 = sacnImageView2;
    
    UILabel *scanLabel2 = [[UILabel alloc] init];
    scanLabel2.text = @"身份证反面扫描";
    scanLabel2.font = [UIFont systemFontOfSize:14];
    scanLabel2.textColor = [UIColor whiteColor];
    scanLabel2.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:scanLabel2];
    [scanLabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(backImageView);
        make.centerY.equalTo(backImageView).offset(15);
    }];
    _scanLabel2 = scanLabel2;
    
    if (infoModel.backIDData) {
        frontImageView.image = [UIImage imageWithData:infoModel.backIDData scale:0.8];
        sacnImageView2.hidden = YES;
        scanLabel2.hidden = YES;
    }
    
    WeXCustomButton *nextBtn = [WeXCustomButton button];
    [nextBtn setTitle:@"下一步" forState:UIControlStateNormal];
    nextBtn.layer.cornerRadius = 5;
    nextBtn.layer.masksToBounds = YES;
    [nextBtn setTitleColor:ColorWithRGB(248, 31, 117) forState:UIControlStateNormal];
    [nextBtn addTarget:self action:@selector(nextBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:nextBtn];
    [nextBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@170);
        make.height.equalTo(@50);
    }];
    
}

- (void)frontTapClick
{
    MGIDCardManager *cardManager = [[MGIDCardManager alloc] init];
    [cardManager setScreenOrientation:MGIDCardScreenOrientationLandscapeLeft];
    [cardManager IDCardStartDetection:self
                           IdCardSide: IDCARD_SIDE_FRONT
                               finish:^(MGIDCardModel *model) {
                                   UIImage *image = [model croppedImageOfIDCard];
                                   if (image) {
                                       _frontImageView.image = image;
                                       _scanLabel1.hidden = YES;
                                       _sacnImageView1.hidden = YES;
                                       
                                       WeXPassportIDCacheInfoModal *infoModel = [WeXPassportIDCacheInfoModal sharedInstance];
                                       infoModel.frontIDData = UIImageJPEGRepresentation(image, 0.8f);
                                         NSLog(@"frontIDData=%lu",(unsigned long)infoModel.frontIDData.length);
                                   }
                                   
                               }
                                 errr:^(MGIDCardError errorType) {
                                     //                                     _imageView.image = nil;
                                 }];
}

- (void)backTapClick
{
    MGIDCardManager *cardManager = [[MGIDCardManager alloc] init];
    [cardManager setScreenOrientation:MGIDCardScreenOrientationLandscapeLeft];
    [cardManager IDCardStartDetection:self
                           IdCardSide: IDCARD_SIDE_BACK
                               finish:^(MGIDCardModel *model) {
                                   UIImage *image = [model croppedImageOfIDCard];
                                   if (image) {
                                       _backImageView.image = image;
                                       _scanLabel2.hidden = YES;
                                       _sacnImageView2.hidden = YES;
                                       
                                       WeXPassportIDCacheInfoModal *infoModel = [WeXPassportIDCacheInfoModal sharedInstance];
                                       infoModel.backIDData = UIImageJPEGRepresentation(image, 0.8f);
                                   }
                               }
                                 errr:^(MGIDCardError errorType) {
                                     // _imageView.image = nil;
                                 }];
}

- (void)nextBtnClick{
//    if (!_frontImageView.image||!_backImageView.image) {
//        [WeXPorgressHUD showText:@"信息不完整" onView:self.view];
//        return;
//    }
    _responseCount = 0;
    [self createGetOcrInfoRequest:[UIImage imageNamed:@"digital_authe_success"]];
    [self createGetOcrInfoRequest:[UIImage imageNamed:@"digital_authe_success"]];
    
//    [self createGetOcrInfoRequest:_frontImageView.image];
//    [self createGetOcrInfoRequest:_backImageView.image];
    
   
    
}

- (void)btn1Click{
    
    
}





@end
