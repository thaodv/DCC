//
//  WeXDccAcrossChainRecordDetailView.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDccAcrossChainRecordDetailView.h"
#import "WeXWalletGetDccAcrossChainRecordDetailModal.h"


@interface WeXDccAcrossChainRecordDetailView ()
{
    WeXWalletGetDccAcrossChainRecordDetailResponseModal *_model;
}

@end

@implementation WeXDccAcrossChainRecordDetailView


+ (instancetype)recordDetailViewWithModal:(WeXWalletGetDccAcrossChainRecordDetailResponseModal *)model{
    return [[self alloc] initWithModel:model];
}

-(instancetype)initWithModel:(WeXWalletGetDccAcrossChainRecordDetailResponseModal *)model
{
    self = [super init];
    if (self) {
        _model = model;
        [self commonInit];
        [self setupSubViews];
    }
    return self;
    
}

- (void)commonInit{
    
    
}

- (void)setupSubViews{
 
    UIScrollView *mainScrollView = [[UIScrollView alloc] init];
    mainScrollView.showsVerticalScrollIndicator = false;
    [self addSubview:mainScrollView];
    [mainScrollView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self).offset(0);
        make.leading.trailing.bottom.equalTo(self);
    }];
    
    UIView *contentView = [[UIView alloc] init];
    [mainScrollView addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(mainScrollView);
        make.width.equalTo(mainScrollView);
    }];
    
    UILabel *leftSymbolLabel = [[UILabel alloc] init];
    leftSymbolLabel.font = [UIFont systemFontOfSize:16];
    leftSymbolLabel.textColor = COLOR_LABEL_DESCRIPTION;
    leftSymbolLabel.textAlignment = NSTextAlignmentLeft;
    leftSymbolLabel.numberOfLines = 0;
    leftSymbolLabel.text = @"DCC";
    [contentView addSubview:leftSymbolLabel];
    [leftSymbolLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(contentView).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *leftNameLabel = [[UILabel alloc] init];
    leftNameLabel.font = [UIFont systemFontOfSize:16];
    leftNameLabel.textColor = COLOR_LABEL_DESCRIPTION;
    leftNameLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:leftNameLabel];
    [leftNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(leftSymbolLabel.mas_bottom).offset(5);
        make.leading.equalTo(leftSymbolLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UIImageView *arrowImageView = [[UIImageView alloc] init];
    arrowImageView.image = [UIImage imageNamed:@"across_chain_right_arrow"];
    [contentView addSubview:arrowImageView];
    [arrowImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(leftNameLabel).offset(0);
        make.leading.equalTo(leftNameLabel.mas_trailing).offset(5);
        make.width.equalTo(@14);
        make.height.equalTo(@12);
    }];
    
    UILabel *rightSymbolLabel = [[UILabel alloc] init];
    rightSymbolLabel.text = @"DCC";
    rightSymbolLabel.font = [UIFont systemFontOfSize:16];
    rightSymbolLabel.textColor = COLOR_LABEL_DESCRIPTION;
    rightSymbolLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:rightSymbolLabel];
    [rightSymbolLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(leftSymbolLabel).offset(0);
        make.leading.equalTo(arrowImageView.mas_trailing).offset(5);
        make.height.equalTo(@20);
    }];
    
    UILabel *rightNameLabel = [[UILabel alloc] init];
    rightNameLabel.font = [UIFont systemFontOfSize:16];
    rightNameLabel.textColor = COLOR_LABEL_DESCRIPTION;
    rightNameLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:rightNameLabel];
    [rightNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(rightSymbolLabel.mas_bottom).offset(5);
        make.leading.equalTo(rightSymbolLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UILabel *topTimeLabel = [[UILabel alloc] init];
    topTimeLabel.font = [UIFont systemFontOfSize:16];
    topTimeLabel.textColor = COLOR_LABEL_DESCRIPTION;
    topTimeLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:topTimeLabel];
    [topTimeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(leftNameLabel.mas_bottom).offset(5);
        make.leading.equalTo(leftNameLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UILabel *topValueLabel = [[UILabel alloc] init];
    topValueLabel.font = [UIFont systemFontOfSize:16];
    topValueLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [contentView addSubview:topValueLabel];
    [topValueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(rightSymbolLabel).offset(0);
        make.trailing.equalTo(contentView).offset(-20);
        make.height.equalTo(@20);
    }];
    
    UILabel *statusLabel = [[UILabel alloc] init];
    statusLabel.font = [UIFont systemFontOfSize:16];
    statusLabel.textColor = COLOR_LABEL_DESCRIPTION;
    statusLabel.textAlignment = NSTextAlignmentRight;
    statusLabel.text = @"转移中";
    [contentView addSubview:statusLabel];
    [statusLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(topTimeLabel).offset(0);
        make.trailing.equalTo(topValueLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    
    
    UILabel *feelabel = [[UILabel alloc] init];
    feelabel.font = [UIFont systemFontOfSize:16];
    feelabel.textColor = COLOR_LABEL_DESCRIPTION;
    feelabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:feelabel];
    [feelabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(topTimeLabel.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
    }];
    
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = [UIColor lightGrayColor];
    line.alpha = LINE_VIEW_ALPHA;
    [contentView addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.top.equalTo(feelabel.mas_bottom).offset(10);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.font = [UIFont systemFontOfSize:16];
    titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
    }];
    //
    UILabel *valueLabel = [[UILabel alloc] init];
    valueLabel.font = [UIFont systemFontOfSize:20];
    valueLabel.textColor = COLOR_LABEL_DESCRIPTION;
    valueLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:valueLabel];
    [valueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(20);
        make.centerX.equalTo(contentView);
        make.height.equalTo(@20);
    }];
    
    UILabel *toTitleLabel = [[UILabel alloc] init];
    toTitleLabel.text = @"接收地址:";
    toTitleLabel.font = [UIFont systemFontOfSize:18];
    toTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    toTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:toTitleLabel];
    [toTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueLabel.mas_bottom).offset(20);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(@80);
    }];
    
    UILabel *toLabel = [[UILabel alloc] init];
    toLabel.font = [UIFont systemFontOfSize:14];
    toLabel.textColor = COLOR_LABEL_DESCRIPTION;
    toLabel.textAlignment = NSTextAlignmentLeft;
    toLabel.adjustsFontSizeToFitWidth = true;
    [contentView addSubview:toLabel];
    [toLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel);
        make.leading.equalTo(toTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    UILabel *fromTitleLabel = [[UILabel alloc] init];
    fromTitleLabel.text = @"付款地址:";
    fromTitleLabel.font = [UIFont systemFontOfSize:18];
    fromTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    fromTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:fromTitleLabel];
    [fromTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel);
    }];
    
    UILabel *fromLabel = [[UILabel alloc] init];
    fromLabel.font = [UIFont systemFontOfSize:14];
    fromLabel.textColor = COLOR_LABEL_DESCRIPTION;
    fromLabel.textAlignment = NSTextAlignmentLeft;
    fromLabel.adjustsFontSizeToFitWidth = true;
    [contentView addSubview:fromLabel];
    [fromLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fromTitleLabel);
        make.leading.equalTo(fromTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    UILabel *transactionTitleLabel = [[UILabel alloc] init];
    transactionTitleLabel.text = @"交  易  号:";
    transactionTitleLabel.font = [UIFont systemFontOfSize:18];
    transactionTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    transactionTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:transactionTitleLabel];
    [transactionTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fromLabel.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel);
    }];
    
    UILabel *transactionLabel = [[UILabel alloc] init];
    transactionLabel.font = [UIFont systemFontOfSize:14];
    transactionLabel.textColor = COLOR_THEME_ALL;
    transactionLabel.textAlignment = NSTextAlignmentLeft;
    transactionLabel.adjustsFontSizeToFitWidth = true;
    [contentView addSubview:transactionLabel];
    [transactionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(transactionTitleLabel);
        make.leading.equalTo(transactionTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    transactionLabel.userInteractionEnabled = YES;
    UITapGestureRecognizer *tapGes = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapGesClick)];
    [transactionLabel addGestureRecognizer:tapGes];
    
    UILabel *heightTitleLabel = [[UILabel alloc] init];
    heightTitleLabel.text = @"区块高度:";
    heightTitleLabel.font = [UIFont systemFontOfSize:18];
    heightTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    heightTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:heightTitleLabel];
    [heightTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(transactionTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel);
    }];
    
    UILabel *heightLabel = [[UILabel alloc] init];
    heightLabel.font = [UIFont systemFontOfSize:14];
    heightLabel.textColor = COLOR_LABEL_DESCRIPTION;
    heightLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:heightLabel];
    [heightLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(heightTitleLabel);
        make.leading.equalTo(heightTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    UILabel *timeTitleLabel = [[UILabel alloc] init];
    timeTitleLabel.text = @"交易时间:";
    timeTitleLabel.font = [UIFont systemFontOfSize:18];
    timeTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    timeTitleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:timeTitleLabel];
    [timeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(heightTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel);
    }];
    
    UILabel *timeLabel = [[UILabel alloc] init];
    timeLabel.font = [UIFont systemFontOfSize:14];
    timeLabel.textColor = COLOR_LABEL_DESCRIPTION;
    timeLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:timeLabel];
    [timeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(timeTitleLabel);
        make.leading.equalTo(timeTitleLabel.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = [UIColor lightGrayColor];
    line1.alpha = LINE_VIEW_ALPHA;
    [contentView addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.top.equalTo(timeLabel.mas_bottom).offset(10);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *titleLabel1 = [[UILabel alloc] init];
    titleLabel1.font = [UIFont systemFontOfSize:16];
    titleLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel1.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:titleLabel1];
    [titleLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
    }];
    //
    UILabel *valueLabel1 = [[UILabel alloc] init];
    valueLabel1.font = [UIFont systemFontOfSize:20];
    valueLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    valueLabel1.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:valueLabel1];
    [valueLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel1.mas_bottom).offset(20);
        make.centerX.equalTo(contentView);
        make.height.equalTo(@20);
    }];
    
    UILabel *toTitleLabel1 = [[UILabel alloc] init];
    toTitleLabel1.text = @"接收地址:";
    toTitleLabel1.font = [UIFont systemFontOfSize:18];
    toTitleLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    toTitleLabel1.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:toTitleLabel1];
    [toTitleLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(valueLabel1.mas_bottom).offset(20);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(@80);
    }];
    
    UILabel *toLabel1 = [[UILabel alloc] init];
    toLabel1.font = [UIFont systemFontOfSize:14];
    toLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    toLabel1.textAlignment = NSTextAlignmentLeft;
    toLabel1.adjustsFontSizeToFitWidth = true;
    [contentView addSubview:toLabel1];
    [toLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel1);
        make.leading.equalTo(toTitleLabel1.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    UILabel *fromTitleLabel1 = [[UILabel alloc] init];
    fromTitleLabel1.text = @"付款地址:";
    fromTitleLabel1.font = [UIFont systemFontOfSize:18];
    fromTitleLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    fromTitleLabel1.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:fromTitleLabel1];
    [fromTitleLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toTitleLabel1.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel1);
    }];
    
    UILabel *fromLabel1 = [[UILabel alloc] init];
    fromLabel1.font = [UIFont systemFontOfSize:14];
    fromLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    fromLabel1.textAlignment = NSTextAlignmentLeft;
    fromLabel1.adjustsFontSizeToFitWidth = true;
    [contentView addSubview:fromLabel1];
    [fromLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fromTitleLabel1);
        make.leading.equalTo(fromTitleLabel1.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    UILabel *transactionTitleLabel1 = [[UILabel alloc] init];
    transactionTitleLabel1.text = @"交  易  号:";
    transactionTitleLabel1.font = [UIFont systemFontOfSize:18];
    transactionTitleLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    transactionTitleLabel1.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:transactionTitleLabel1];
    [transactionTitleLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fromLabel1.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel1);
    }];
    
    UILabel *transactionLabel1 = [[UILabel alloc] init];
    transactionLabel1.font = [UIFont systemFontOfSize:14];
    transactionLabel1.textColor = COLOR_THEME_ALL;
    transactionLabel1.textAlignment = NSTextAlignmentLeft;
    transactionLabel1.adjustsFontSizeToFitWidth = true;
    [contentView addSubview:transactionLabel1];
    [transactionLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(transactionTitleLabel1);
        make.leading.equalTo(transactionTitleLabel1.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    transactionLabel1.userInteractionEnabled = YES;
    UITapGestureRecognizer *tapGes1 = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapGesClick1)];
    [transactionLabel1 addGestureRecognizer:tapGes1];
    
    UILabel *heightTitleLabel1 = [[UILabel alloc] init];
    heightTitleLabel1.text = @"区块高度:";
    heightTitleLabel1.font = [UIFont systemFontOfSize:18];
    heightTitleLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    heightTitleLabel1.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:heightTitleLabel1];
    [heightTitleLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(transactionTitleLabel1.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel1);
    }];
    
    UILabel *heightLabel1 = [[UILabel alloc] init];
    heightLabel1.font = [UIFont systemFontOfSize:14];
    heightLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    heightLabel1.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:heightLabel1];
    [heightLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(heightTitleLabel1);
        make.leading.equalTo(heightTitleLabel1.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    UILabel *timeTitleLabel1 = [[UILabel alloc] init];
    timeTitleLabel1.text = @"交易时间:";
    timeTitleLabel1.font = [UIFont systemFontOfSize:18];
    timeTitleLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    timeTitleLabel1.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:timeTitleLabel1];
    [timeTitleLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(heightTitleLabel1.mas_bottom).offset(10);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(@20);
        make.width.equalTo(toTitleLabel1);
    }];
    
    UILabel *timeLabel1 = [[UILabel alloc] init];
    timeLabel1.font = [UIFont systemFontOfSize:14];
    timeLabel1.textColor = COLOR_LABEL_DESCRIPTION;
    timeLabel1.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:timeLabel1];
    [timeLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(timeTitleLabel1);
        make.leading.equalTo(timeTitleLabel1.mas_trailing).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(timeLabel1).offset(10);
    }];
    
 
    
    if ([_model.originAssetCode isEqualToString:@"DCC_JUZIX"]) {
        leftSymbolLabel.textColor = ColorWithRGB(236, 128, 52);
        leftNameLabel.textColor = ColorWithRGB(236, 128, 52);
        leftNameLabel.text = @"@Distributed Credit Chain";
        
        rightSymbolLabel.textColor = ColorWithRGB(86, 88, 173);
        rightNameLabel.textColor = ColorWithRGB(86, 88, 173);
        rightNameLabel.text = @"@Ethereum";
        
        titleLabel.text = @"私链转出交易详情";
        titleLabel1.text = @"转入公链交易详情";
      
    }
    else
    {
        leftSymbolLabel.textColor = ColorWithRGB(86, 88, 173);;
        leftNameLabel.textColor = ColorWithRGB(86, 88, 173);
        leftNameLabel.text = @"@Ethereum";
        
        rightSymbolLabel.textColor = ColorWithRGB(236, 128, 52);
        rightNameLabel.textColor = ColorWithRGB(236, 128, 52);
        rightNameLabel.text = @"@Distributed Credit Chain";
        
        titleLabel.text = @"公链转出交易详情";
        titleLabel1.text = @"转入私链交易详情";
     
    }
    NSString *topAmount = [WexCommonFunc formatterStringWithContractBalance:_model.originAmount decimals:18];
    topValueLabel.text = topAmount;
   
    
    //    ACCEPTED -> App显示 "转移中"
    //    DELIVERED -> App显示 "已完成"
    if ([_model.status isEqualToString:@"ACCEPTED"]) {
        statusLabel.text = @"转移中";
        topTimeLabel.hidden = true;
        
        titleLabel1.hidden = true;
        valueLabel1.hidden = true;
        fromTitleLabel1.hidden = true;
        fromLabel1.hidden = true;
        toTitleLabel1.hidden = true;
        toLabel1.hidden = true;
        transactionTitleLabel1.hidden = true;
        transactionLabel1.hidden = true;
        heightTitleLabel1.hidden = true;
        heightLabel1.hidden = true;
        timeTitleLabel1.hidden = true;
        timeLabel1.hidden = true;
        
    }
    else
    {
        statusLabel.text =@"已完成";
        topTimeLabel.text = [WexCommonFunc formatterTimeStringWithTimeStamp:_model.lastUpdatedTime formatter:@"完成时间: yyyy-MM-dd HH:mm:ss"];
        topTimeLabel.hidden = false;
        
        titleLabel1.hidden = false;
        valueLabel1.hidden = false;
        fromTitleLabel1.hidden = false;
        fromLabel1.hidden = false;
        toTitleLabel1.hidden = false;
        toLabel1.hidden = false;
        transactionTitleLabel1.hidden = false;
        transactionLabel1.hidden = false;
        heightTitleLabel1.hidden = false;
        heightLabel1.hidden = false;
        timeTitleLabel1.hidden = false;
        timeLabel1.hidden = false;
    }
    NSString *feeAmount = [WexCommonFunc formatterStringWithContractBalance:_model.feeAmount decimals:18];
    feelabel.text = [NSString stringWithFormat:@"手续费: %@DCC",feeAmount];
    
    NSString *originAmount = [WexCommonFunc formatterStringWithContractBalance:_model.originAmount decimals:18];
    valueLabel.text = [NSString stringWithFormat:@"-%@DCC",originAmount];
    toLabel.text = _model.originReceiverAddress;
    fromLabel.text = _model.beneficiaryAddress;
    transactionLabel.text = [WexCommonFunc formatterStringWithContractAddress:_model.originTxHash];
    heightLabel.text = _model.originBlockNumber;
    timeLabel.text =  [WexCommonFunc formatterTimeStringWithTimeStamp:_model.originTradeTime formatter:@"yyyy-MM-dd HH:mm:ss"];
    
    NSString *destAmount = [WexCommonFunc formatterStringWithContractBalance:_model.destAmount decimals:18];
    valueLabel1.text = [NSString stringWithFormat:@"+%@DCC",destAmount];
    toLabel1.text = _model.beneficiaryAddress;
    fromLabel1.text = _model.destPayerAddress;
    transactionLabel1.text = [WexCommonFunc formatterStringWithContractAddress:_model.destTxHash];
    heightLabel1.text = _model.destBlockNumber;
    timeLabel1.text = [WexCommonFunc formatterTimeStringWithTimeStamp:_model.destTradeTime formatter:@"yyyy-MM-dd HH:mm:ss"];
    
}

//
- (void)tapGesClick{
    
    if (_HashClickBlock) {
        _HashClickBlock(0);
    }

}

- (void)tapGesClick1{
    if (_HashClickBlock) {
        _HashClickBlock(1);
    }
}


@end
