//
//  WeXPassportIDStatusController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/1.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDStatusController.h"
#import "WeXBackupBigQRView.h"
#import "WeXAuthenGetOrderIdAdapter.h"
#import "WeXPassportIDScanController.h"

//APPLIED（已申请）
//PASSED（通过） 
//REJECTED（已拒绝）
//INVALID（已失效）
//REVOKED（已撤销）
//DISCARDED（已放弃

//通过
#define STATUS_PASSED @"PASSED"
//已拒绝
#define STATUS_REJECTED @"REJECTED"
//已申请
#define STATUS_APPLIED @"APPLIED"

@interface WeXPassportIDStatusController ()
{
    NSString *_status;
}

@property (nonatomic,strong)WeXAuthenGetOrderIdAdapter *getOrderIdAdapter;

@end

@implementation WeXPassportIDStatusController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupNavgationType];
    [self createGetOrderIdRequest];
}

- (void)setupNavgationType{
    self.navigationItem.hidesBackButton = YES;
    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"digital_cha1"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
    self.navigationItem.rightBarButtonItem = rihgtItem;
    
}

#pragma -mark 发送获取orderid请求
- (void)createGetOrderIdRequest{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    _getOrderIdAdapter = [[WeXAuthenGetOrderIdAdapter alloc] init];
    _getOrderIdAdapter.delegate = self;
    WeXAuthenGetOrderIdParamModal* paramModal = [[WeXAuthenGetOrderIdParamModal alloc] init];
    paramModal.txHash = [WexCommonFunc getPassport].idAuthenTxHash;
    paramModal.business = @"ID";
    [_getOrderIdAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response
{
     if (adapter == _getOrderIdAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXAuthenGetOrderIdResponseModal *responseModel =(WeXAuthenGetOrderIdResponseModal *)response;
            NSLog(@"%@",responseModel);
            _status = responseModel.status;
            [self setupSubViews];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
     }
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = WeXLocalizedString(@"实名信息");
    titleLabel.font = [UIFont systemFontOfSize:25];
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@25);
    }];
    
    UIImageView *backImageView = [[UIImageView alloc] init];
    backImageView.image = [UIImage imageNamed:@"digital_ID_front"];
    [self.view addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(40);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(backImageView.mas_width).multipliedBy(0.6);
    }];

    UILabel *nameTitleLabel = [[UILabel alloc] init];
    nameTitleLabel.text = WeXLocalizedString(@"姓名");
    nameTitleLabel.font = [UIFont systemFontOfSize:14];
    nameTitleLabel.textColor = [UIColor blueColor];
    nameTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:nameTitleLabel];
    [nameTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView).offset(20);
        make.leading.equalTo(backImageView).offset(20);
    }];
    
    UILabel *nameLabel = [[UILabel alloc] init];
    nameLabel.text = [WexCommonFunc getPassport].userName;
    nameLabel.font = [UIFont systemFontOfSize:14];
    nameLabel.textColor = [UIColor blackColor];
    nameLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:nameLabel];
    [nameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(nameTitleLabel);
        make.leading.equalTo(nameTitleLabel.mas_trailing).offset(10);
    }];
    
    UILabel *sexTitleLabel = [[UILabel alloc] init];
    sexTitleLabel.text = WeXLocalizedString(@"性别");
    sexTitleLabel.font = [UIFont systemFontOfSize:14];
    sexTitleLabel.textColor = [UIColor blueColor];
    sexTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:sexTitleLabel];
    [sexTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(nameTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(nameTitleLabel);
    }];
    
    UILabel *sexLabel = [[UILabel alloc] init];
    sexLabel.text = [WexCommonFunc getPassport].userSex;
    sexLabel.font = [UIFont systemFontOfSize:14];
    sexLabel.textColor = [UIColor blackColor];
    sexLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:sexLabel];
    [sexLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(sexTitleLabel);
        make.leading.equalTo(sexTitleLabel.mas_trailing).offset(10);
    }];
    
    
    UILabel *nationTitleLabel = [[UILabel alloc] init];
    nationTitleLabel.text = WeXLocalizedString(@"民族");
    nationTitleLabel.font = [UIFont systemFontOfSize:14];
    nationTitleLabel.textColor = [UIColor blueColor];
    nationTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:nationTitleLabel];
    [nationTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(sexTitleLabel);
        make.leading.equalTo(sexLabel.mas_trailing).offset(30);
    }];
    
    UILabel *nationLabel = [[UILabel alloc] init];
    nationLabel.text = [WexCommonFunc getPassport].userNation;
    nationLabel.font = [UIFont systemFontOfSize:14];
    nationLabel.textColor = [UIColor blackColor];
    nationLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:nationLabel];
    [nationLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(nationTitleLabel);
        make.leading.equalTo(nationTitleLabel.mas_trailing).offset(10);
    }];
    
    UILabel *birthTitleLabel = [[UILabel alloc] init];
    birthTitleLabel.text = WeXLocalizedString(@"出生");
    birthTitleLabel.font = [UIFont systemFontOfSize:14];
    birthTitleLabel.textColor = [UIColor blueColor];
    birthTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:birthTitleLabel];
    [birthTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(sexTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(nameTitleLabel);
    }];
    
    UILabel *yearLabel = [[UILabel alloc] init];
    yearLabel.text = [WexCommonFunc getPassport].userYear;
    yearLabel.font = [UIFont systemFontOfSize:14];
    yearLabel.textColor = [UIColor blackColor];
    yearLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:yearLabel];
    [yearLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(birthTitleLabel);
        make.leading.equalTo(birthTitleLabel.mas_trailing).offset(10);
    }];
    
    UILabel *yearTitleLabel = [[UILabel alloc] init];
    yearTitleLabel.text = WeXLocalizedString(@"年");
    yearTitleLabel.font = [UIFont systemFontOfSize:14];
    yearTitleLabel.textColor = [UIColor blueColor];
    yearTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:yearTitleLabel];
    [yearTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(birthTitleLabel);
        make.leading.equalTo(yearLabel.mas_trailing).offset(10);
    }];
    
    UILabel *monthLabel = [[UILabel alloc] init];
    monthLabel.text = [WexCommonFunc getPassport].userMonth;
    monthLabel.font = [UIFont systemFontOfSize:14];
    monthLabel.textColor = [UIColor blackColor];
    monthLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:monthLabel];
    [monthLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(birthTitleLabel);
        make.leading.equalTo(yearTitleLabel.mas_trailing).offset(10);
    }];
    
    UILabel *monthTitleLabel = [[UILabel alloc] init];
    monthTitleLabel.text = WeXLocalizedString(@"月");
    monthTitleLabel.font = [UIFont systemFontOfSize:14];
    monthTitleLabel.textColor = [UIColor blueColor];
    monthTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:monthTitleLabel];
    [monthTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(birthTitleLabel);
        make.leading.equalTo(monthLabel.mas_trailing).offset(10);
    }];
    
    UILabel *dayLabel = [[UILabel alloc] init];
    dayLabel.text = [WexCommonFunc getPassport].userDay;
    dayLabel.font = [UIFont systemFontOfSize:14];
    dayLabel.textColor = [UIColor blackColor];
    dayLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:dayLabel];
    [dayLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(birthTitleLabel);
        make.leading.equalTo(monthTitleLabel.mas_trailing).offset(10);
    }];
    
    UILabel *dayTitleLabel = [[UILabel alloc] init];
    dayTitleLabel.text = WeXLocalizedString(@"日");
    dayTitleLabel.font = [UIFont systemFontOfSize:14];
    dayTitleLabel.textColor = [UIColor blueColor];
    dayTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:dayTitleLabel];
    [dayTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(birthTitleLabel);
        make.leading.equalTo(dayLabel.mas_trailing).offset(10);
    }];
    
    UIImageView *headImageView = [[UIImageView alloc] init];
    headImageView.image = [WexCommonFunc imageWithName:WEX_ID_IMAGE_HEAD_KEY];
    NSLog(@"image=%@",[WexCommonFunc imageWithName:WEX_ID_IMAGE_HEAD_KEY]);
    headImageView.layer.cornerRadius = 5;
    headImageView.layer.masksToBounds = YES;
    headImageView.backgroundColor = [UIColor greenColor];
    [self.view addSubview:headImageView];
    [headImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView).offset(30);
        make.trailing.equalTo(backImageView).offset(-30);
        make.width.equalTo(@80);
        make.height.equalTo(@100);
    }];
    
    UILabel *addressTitleLabel = [[UILabel alloc] init];
    addressTitleLabel.text = WeXLocalizedString(@"住址");
    addressTitleLabel.font = [UIFont systemFontOfSize:14];
    addressTitleLabel.textColor = [UIColor blueColor];
    addressTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:addressTitleLabel];
    [addressTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(birthTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(nameTitleLabel);
    }];
    
    UILabel *addressLabel = [[UILabel alloc] init];
    addressLabel.text = [WexCommonFunc getPassport].userAddress;
    addressLabel.font = [UIFont systemFontOfSize:14];
    addressLabel.textColor = [UIColor blackColor];
    addressLabel.textAlignment = NSTextAlignmentLeft;
    addressLabel.numberOfLines = 2;
    [self.view addSubview:addressLabel];
    [addressLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(addressTitleLabel);
        make.leading.equalTo(addressTitleLabel.mas_trailing).offset(10);
        make.trailing.lessThanOrEqualTo(headImageView.mas_leading).offset(-5);
    }];
    
    UILabel *numberTitleLabel = [[UILabel alloc] init];
    numberTitleLabel.text = WeXLocalizedString(@"身份证号码");
    numberTitleLabel.font = [UIFont systemFontOfSize:14];
    numberTitleLabel.textColor = [UIColor blueColor];
    numberTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:numberTitleLabel];
    [numberTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(backImageView).offset(-40);
        make.leading.equalTo(nameTitleLabel);
    }];
    
    UILabel *numberLabel = [[UILabel alloc] init];
    numberLabel.text = [WexCommonFunc getPassport].userNumber;
    numberLabel.font = [UIFont systemFontOfSize:14];
    numberLabel.textColor = [UIColor blackColor];
    numberLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:numberLabel];
    [numberLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(numberTitleLabel);
        make.leading.equalTo(numberTitleLabel.mas_trailing).offset(10);
        make.trailing.lessThanOrEqualTo(backImageView);
    }];
    
    //成功
    if ([_status isEqualToString:STATUS_PASSED]) {
        UILabel *authTitleLabel = [[UILabel alloc] init];
        authTitleLabel.text = WeXLocalizedString(@"为认证内容");
        authTitleLabel.font = [UIFont systemFontOfSize:14];
        authTitleLabel.textColor = [UIColor redColor];
        authTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:authTitleLabel];
        [authTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(backImageView).offset(-20);
            make.leading.equalTo(nameTitleLabel);
        }];
    
        UIImageView *starImageView1 = [[UIImageView alloc] init];
        starImageView1.image = [UIImage imageNamed:@"digital_star"];
        [self.view addSubview:starImageView1];
        [starImageView1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(nameTitleLabel);
            make.trailing.equalTo(nameTitleLabel.mas_leading).offset(-3);
            
        }];
        
        UIImageView *starImageView2 = [[UIImageView alloc] init];
        starImageView2.image = [UIImage imageNamed:@"digital_star"];
        [self.view addSubview:starImageView2];
        [starImageView2 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(numberTitleLabel);
            make.trailing.equalTo(numberTitleLabel.mas_leading).offset(-3);
            
        }];
        
        UIImageView *starImageView3 = [[UIImageView alloc] init];
        starImageView3.image = [UIImage imageNamed:@"digital_star"];
        [self.view addSubview:starImageView3];
        [starImageView3 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(authTitleLabel);
            make.trailing.equalTo(authTitleLabel.mas_leading).offset(-3);
            
        }];
        
        UIImageView *starImageView4 = [[UIImageView alloc] init];
        starImageView4.image = [UIImage imageNamed:@"digital_star"];
        [self.view addSubview:starImageView4];
        [starImageView4 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(headImageView);
            make.trailing.equalTo(headImageView.mas_leading).offset(-3);
            
        }];
        
        UIImageView *stateImageView = [[UIImageView alloc] init];
        stateImageView.image = [UIImage imageNamed:@"digital_authe_success"];
        [self.view addSubview:stateImageView];
        [stateImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(backImageView);
            make.trailing.equalTo(backImageView);
        }];
        
        UIImageView *QRImageView = [[UIImageView alloc] init];
        QRImageView.layer.magnificationFilter = kCAFilterNearest;
        QRImageView.image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:WeXLocalizedString(@"二维码内容") imageViewWidth:160];
        QRImageView.layer.cornerRadius = 5;
        QRImageView.layer.masksToBounds = YES;
        QRImageView.userInteractionEnabled = YES;
        [self.view addSubview:QRImageView];
        [QRImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(backImageView.mas_bottom).offset(30);
            make.centerX.equalTo(self.view);
            make.width.equalTo(@160);
            make.height.equalTo(@160);
        }];
        
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick)];
        [QRImageView addGestureRecognizer:tap];
    }
    //申请中
    else if ([_status isEqualToString:STATUS_APPLIED])
    {
        UIImageView *coverImageView = [[UIImageView alloc] init];
        coverImageView.backgroundColor = [UIColor blackColor];
        coverImageView.alpha = COVER_VIEW_ALPHA;
        coverImageView.layer.cornerRadius = 15;
        coverImageView.layer.masksToBounds = YES;
        [self.view addSubview:coverImageView];
        [coverImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(backImageView);
        }];
        
        UIImageView *stateImageView = [[UIImageView alloc] init];
        stateImageView.image = [UIImage imageNamed:@"digital_process"];
        [self.view addSubview:stateImageView];
        [stateImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(backImageView);
            make.trailing.equalTo(backImageView);
        }];
    }
    //已拒绝
    else
    {
        UIImageView *coverImageView = [[UIImageView alloc] init];
        coverImageView.backgroundColor = [UIColor blackColor];
        coverImageView.alpha = COVER_VIEW_ALPHA;
        coverImageView.layer.cornerRadius = 15;
        coverImageView.layer.masksToBounds = YES;
        [self.view addSubview:coverImageView];
        [coverImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(backImageView);
        }];
        
        UIImageView *stateImageView = [[UIImageView alloc] init];
        stateImageView.image = [UIImage imageNamed:@"digital_authe_fail"];
        [self.view addSubview:stateImageView];
        [stateImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(backImageView);
            make.trailing.equalTo(backImageView);
        }];
       
        WeXCustomButton *applyBtn = [WeXCustomButton button];
        [applyBtn setTitle:WeXLocalizedString(@"重新申请认证") forState:UIControlStateNormal];
        [applyBtn addTarget:self action:@selector(applyBtnClick) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:applyBtn];
        [applyBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(backImageView.mas_bottom).offset(50);
            make.centerX.equalTo(self.view);
            make.width.equalTo(@180);
            make.height.equalTo(@50);
        }];
        
    }
    
}

- (void)applyBtnClick
{
    WeXPassportIDScanController *ctrl = [[WeXPassportIDScanController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)tapClick{
    
    UIImage *image = [SGQRCodeGenerateManager generateWithDefaultQRCodeData:WeXLocalizedString(@"二维码内容") imageViewWidth:kScreenWidth];
    WeXBackupBigQRView *bigView = [[WeXBackupBigQRView alloc] initWithFrame:self.view.bounds];
    bigView.QRImageView.image = image;
    [self.view.window addSubview:bigView];
}

- (void)rightItemClick{
    [self.navigationController popViewControllerAnimated:YES];
}


@end
