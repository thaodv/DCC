//
//  WeXWalletDigitalRecordDetailController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDigitalRecordDetailController.h"
#import "WeXWalletRecordHashWebController.h"
#import "WeXWalletEtherscanGetRecordDetailAdapter.h"

@interface WeXWalletDigitalRecordDetailController ()
{
    UILabel *_costLabel;
    UILabel *_statusLabel;
    UILabel *_heightLabel;
    
    NSString *_gasUsed;
    NSString *_gasPrice;
    NSString *_blockNumber;
    
    
}

@property (nonatomic,strong)WeXWalletEtherscanGetRecordDetailAdapter *getRecordDetailAdapter;

@end

@implementation WeXWalletDigitalRecordDetailController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupNavgationType];
    [self setupSubViews];
   
    if (!([self.tokenModel.symbol isEqualToString:@"ETH"]||[self.tokenModel.symbol isEqualToString:@"FTC"])) {
         [self creteRequest];
    }
    
}

- (void)creteRequest{
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(@"容器加载失败:%@",error);
            return;
        }
        
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_INFURA_SERVER responseBlock:^(id response)
         {
             [[WXPassHelper instance] queryTransactionReceipt:self.recordModel.hashStr responseBlock:^(id response) {
                 NSLog(@"%@",response);
                 NSString *status = [response objectForKey:@"status"];
                 if ([status isEqualToString:@"0x1"]) {
                     _statusLabel.text = @"成功";
                 }
                 else
                 {
                     _statusLabel.text = @"失败";
                 }
                 _gasUsed = [response objectForKey:@"gasUsed"];
                 [self configResult];
                
             }];
             [[WXPassHelper instance] queryTransactionReceiptWithPending:self.recordModel.hashStr responseBlock:^(id response) {
                 NSLog(@"%@",response);
                 NSDictionary *rep = [response objectForKey:@"rep"];
                  _gasPrice = [rep objectForKey:@"gasPrice"];
                 _blockNumber = [rep objectForKey:@"blockNumber"];
                 _heightLabel.text = [NSString stringWithFormat:@"%@",_blockNumber];
                 [self configResult];
             }];
         }];
        
    }];
}

- (void)configResult
{
    if (_gasPrice&&_gasUsed) {
        NSString *gasPrice = [NSString stringWithFormat:@"%@",_gasPrice];
        NSString *gasUsed = [NSString stringWithFormat:@"%@",_gasUsed];
        NSDecimalNumber * decimal1 = [WexCommonFunc stringWithOriginString:gasUsed multiplyString:gasPrice];
        NSDecimalNumber * decimal2 = [WexCommonFunc stringWithOriginString:[decimal1 stringValue] dividString:EIGHTEEN_ZERO];
        _costLabel.text = [decimal2 stringValue];
    }
    
}

#pragma -mark 获取交易记录请求
- (void)createGetRecordDetailRequest{
    _getRecordDetailAdapter = [[WeXWalletEtherscanGetRecordDetailAdapter alloc] init];
    _getRecordDetailAdapter.delegate = self;
    WeXWalletEtherscanGetRecordDetailParamModal* paramModal = [[WeXWalletEtherscanGetRecordDetailParamModal alloc] init];
    paramModal.module = @"transaction";
    paramModal.action = @"getstatus";
    paramModal.txhash = self.recordModel.hashStr;
    [_getRecordDetailAdapter run:paramModal];
    
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getRecordDetailAdapter) {
        
    }
    
}
    
- (void)setupNavgationType{
//    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"digital_share"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
//    self.navigationItem.rightBarButtonItem = rihgtItem;
        
}

- (void)rightItemClick{
    
}
    
    //初始化滚动视图
-(void)setupSubViews{
    UILabel *recordlabel = [[UILabel alloc] init];
    recordlabel.text = @"交易记录详情";
    recordlabel.font = [UIFont systemFontOfSize:20];
    recordlabel.textColor = ColorWithLabelDescritionBlack;
    recordlabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:recordlabel];
    [recordlabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *valueLabel = [[UILabel alloc] init];
     NSString *valueStr = [WexCommonFunc formatterStringWithContractBalance:self.recordModel.value decimals:[self.tokenModel.decimals integerValue]];
    
    if ([[WexCommonFunc getFromAddress] isEqualToString: self.recordModel.from]) {
        valueLabel.text = [NSString stringWithFormat:@"-%@%@",valueStr,self.tokenModel.symbol];
    }
    else
    {
        valueLabel.text = [NSString stringWithFormat:@"+%@%@",valueStr,self.tokenModel.symbol];
    }
    valueLabel.font = [UIFont systemFontOfSize:20];
    valueLabel.textColor = ColorWithLabelDescritionBlack;
    valueLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:valueLabel];
    [valueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(recordlabel.mas_bottom).offset(20);
        make.centerX.equalTo(self.view);
        make.height.equalTo(@20);
    }];
    
    UILabel *toTitleLabel = [[UILabel alloc] init];
    toTitleLabel.text = @"接收地址:";
    toTitleLabel.font = [UIFont systemFontOfSize:18];
    toTitleLabel.textColor = ColorWithLabelDescritionBlack;
    toTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:toTitleLabel];
    [toTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueLabel.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(@80);
    }];
    
    UILabel *toLabel = [[UILabel alloc] init];
    toLabel.text = self.recordModel.to;
    toLabel.font = [UIFont systemFontOfSize:14];
    toLabel.textColor = ColorWithLabelWeakBlack;
    toLabel.textAlignment = NSTextAlignmentLeft;
    toLabel.numberOfLines = 2;
    [self.view addSubview:toLabel];
    [toLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.height.equalTo(@40);
    }];
    
    UILabel *fromTitleLabel = [[UILabel alloc] init];
    fromTitleLabel.text = @"付款地址:";
    fromTitleLabel.font = [UIFont systemFontOfSize:18];
    fromTitleLabel.textColor = ColorWithLabelDescritionBlack;
    fromTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:fromTitleLabel];
    [fromTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel.mas_bottom).offset(30);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel);
    }];
    
    UILabel *fromLabel = [[UILabel alloc] init];
    fromLabel.text = self.recordModel.from;;
    fromLabel.font = [UIFont systemFontOfSize:14];
    fromLabel.textColor = ColorWithLabelWeakBlack;
    fromLabel.textAlignment = NSTextAlignmentLeft;
    fromLabel.numberOfLines = 2;
    [self.view addSubview:fromLabel];
    [fromLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fromTitleLabel);
        make.leading.equalTo(fromTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.height.equalTo(@40);
    }];
    
    if ([self.tokenModel.symbol isEqualToString:@"FTC"])
    {
        UILabel *transactionTitleLabel = [[UILabel alloc] init];
        transactionTitleLabel.text = @"交  易  号:";
        transactionTitleLabel.font = [UIFont systemFontOfSize:18];
        transactionTitleLabel.textColor = ColorWithLabelDescritionBlack;
        transactionTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:transactionTitleLabel];
        [transactionTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(fromLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *transactionLabel = [[UILabel alloc] init];
        transactionLabel.text = [WexCommonFunc formatterStringWithContractAddress:self.recordModel.hashStr];
        transactionLabel.font = [UIFont systemFontOfSize:14];
        transactionLabel.textColor = ColorWithLabelWeakBlack;
        transactionLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:transactionLabel];
        [transactionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(transactionTitleLabel);
            make.leading.equalTo(transactionTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        
        UILabel *heightTitleLabel = [[UILabel alloc] init];
        heightTitleLabel.text = @"区块高度:";
        heightTitleLabel.font = [UIFont systemFontOfSize:18];
        heightTitleLabel.textColor = ColorWithLabelDescritionBlack;
        heightTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:heightTitleLabel];
        [heightTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(transactionTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *heightLabel = [[UILabel alloc] init];
        if (self.recordModel.blockNumber) {
            heightLabel.text = self.recordModel.blockNumber;
        }
        
        heightLabel.font = [UIFont systemFontOfSize:14];
        heightLabel.textColor = ColorWithLabelWeakBlack;
        heightLabel.textAlignment = NSTextAlignmentLeft;
        heightLabel.numberOfLines = 2;
        [self.view addSubview:heightLabel];
        [heightLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(heightTitleLabel);
            make.leading.equalTo(heightTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        _heightLabel = heightLabel;
        
        UILabel *timeTitleLabel = [[UILabel alloc] init];
        timeTitleLabel.text = @"交易时间:";
        timeTitleLabel.font = [UIFont systemFontOfSize:18];
        timeTitleLabel.textColor = ColorWithLabelDescritionBlack;
        timeTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:timeTitleLabel];
        [timeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(heightTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *timeLabel = [[UILabel alloc] init];
        timeLabel.text = [WexCommonFunc formatterTimeStringWithTimeStamp:self.recordModel.timeStamp];
        timeLabel.font = [UIFont systemFontOfSize:14];
        timeLabel.textColor = ColorWithLabelWeakBlack;
        timeLabel.textAlignment = NSTextAlignmentLeft;
        timeLabel.numberOfLines = 2;
        [self.view addSubview:timeLabel];
        [timeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(timeTitleLabel);
            make.leading.equalTo(timeTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];

    }
    else
    {
        UILabel *costTitleLabel = [[UILabel alloc] init];
        costTitleLabel.text = @"矿  工  费:";
        costTitleLabel.font = [UIFont systemFontOfSize:18];
        costTitleLabel.textColor = ColorWithLabelDescritionBlack;
        costTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:costTitleLabel];
        [costTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(fromTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *costLabel = [[UILabel alloc] init];
        if (self.recordModel.gasUsed&&self.recordModel.gasPrice) {
            NSDecimalNumber * decimal1 = [WexCommonFunc stringWithOriginString:self.recordModel.gasUsed multiplyString:self.recordModel.gasPrice];
            NSDecimalNumber * decimal2 = [WexCommonFunc stringWithOriginString:[decimal1 stringValue] dividString:EIGHTEEN_ZERO];
            costLabel.text = [decimal2 stringValue];
        }
        costLabel.font = [UIFont systemFontOfSize:14];
        costLabel.textColor = ColorWithLabelWeakBlack;
        costLabel.textAlignment = NSTextAlignmentLeft;
        costLabel.numberOfLines = 2;
        [self.view addSubview:costLabel];
        [costLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(costTitleLabel);
            make.leading.equalTo(costTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        _costLabel = costLabel;
        
        UILabel *transactionTitleLabel = [[UILabel alloc] init];
        transactionTitleLabel.text = @"交  易  号:";
        transactionTitleLabel.font = [UIFont systemFontOfSize:18];
        transactionTitleLabel.textColor = ColorWithLabelDescritionBlack;
        transactionTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:transactionTitleLabel];
        [transactionTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(costTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *transactionLabel = [[UILabel alloc] init];
        transactionLabel.text = [WexCommonFunc formatterStringWithContractAddress:self.recordModel.hashStr];
        transactionLabel.font = [UIFont systemFontOfSize:14];
        transactionLabel.textColor = ColorWithButtonRed;
        transactionLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:transactionLabel];
        [transactionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(transactionTitleLabel);
            make.leading.equalTo(transactionTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        transactionLabel.userInteractionEnabled = YES;
        UITapGestureRecognizer *tapGes = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapGesClick)];
        [transactionLabel addGestureRecognizer:tapGes];
        
        UILabel *heightTitleLabel = [[UILabel alloc] init];
        heightTitleLabel.text = @"区块高度:";
        heightTitleLabel.font = [UIFont systemFontOfSize:18];
        heightTitleLabel.textColor = ColorWithLabelDescritionBlack;
        heightTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:heightTitleLabel];
        [heightTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(transactionTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *heightLabel = [[UILabel alloc] init];
        if (self.recordModel.blockNumber) {
            heightLabel.text = self.recordModel.blockNumber;
        }
        
        heightLabel.font = [UIFont systemFontOfSize:14];
        heightLabel.textColor = ColorWithLabelWeakBlack;
        heightLabel.textAlignment = NSTextAlignmentLeft;
        heightLabel.numberOfLines = 2;
        [self.view addSubview:heightLabel];
        [heightLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(heightTitleLabel);
            make.leading.equalTo(heightTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        _heightLabel = heightLabel;
        
        UILabel *timeTitleLabel = [[UILabel alloc] init];
        timeTitleLabel.text = @"交易时间:";
        timeTitleLabel.font = [UIFont systemFontOfSize:18];
        timeTitleLabel.textColor = ColorWithLabelDescritionBlack;
        timeTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:timeTitleLabel];
        [timeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(heightTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *timeLabel = [[UILabel alloc] init];
        timeLabel.text = [WexCommonFunc formatterTimeStringWithTimeStamp:self.recordModel.timeStamp];
        timeLabel.font = [UIFont systemFontOfSize:14];
        timeLabel.textColor = ColorWithLabelWeakBlack;
        timeLabel.textAlignment = NSTextAlignmentLeft;
        timeLabel.numberOfLines = 2;
        [self.view addSubview:timeLabel];
        [timeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(timeTitleLabel);
            make.leading.equalTo(timeTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        
        
        UILabel *statusTitleLabel = [[UILabel alloc] init];
        statusTitleLabel.text = @"交易状态:";
        statusTitleLabel.font = [UIFont systemFontOfSize:18];
        statusTitleLabel.textColor = ColorWithLabelDescritionBlack;
        statusTitleLabel.textAlignment = NSTextAlignmentLeft;
        [self.view addSubview:statusTitleLabel];
        [statusTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(timeTitleLabel.mas_bottom).offset(30);
            make.leading.equalTo(self.view).offset(10);
            make.height.equalTo(@20);
            make.width.equalTo(toTitleLabel);
        }];
        
        UILabel *statusLabel = [[UILabel alloc] init];
        if (self.recordModel.isError) {
            statusLabel.text = [self.recordModel.isError isEqualToString: @"1"]?@"失败":@"成功";
        }
        
        statusLabel.font = [UIFont systemFontOfSize:14];
        statusLabel.textColor = ColorWithLabelWeakBlack;
        statusLabel.textAlignment = NSTextAlignmentLeft;
        statusLabel.numberOfLines = 2;
        [self.view addSubview:statusLabel];
        [statusLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(statusTitleLabel);
            make.leading.equalTo(statusTitleLabel.mas_trailing).offset(10);
            make.trailing.equalTo(self.view).offset(-10);
            make.height.equalTo(@20);
        }];
        _statusLabel = statusLabel;
        
    }
    
   
    
}

- (void)tapGesClick{
    
    WeXWalletRecordHashWebController *ctrl = [[WeXWalletRecordHashWebController alloc] init];
    ctrl.txHash = self.recordModel.hashStr;
    [self.navigationController pushViewController:ctrl animated:YES];
}

@end
