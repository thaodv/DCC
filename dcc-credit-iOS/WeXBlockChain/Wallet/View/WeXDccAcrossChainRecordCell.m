//
//  WeXDccAcrossChainRecordCell.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDccAcrossChainRecordCell.h"
#import "WeXWalletGetAcrossChainTransferRecordModal.h"


@implementation WeXDccAcrossChainRecordCell

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self setupSubViews];
    }
    return  self;
}

- (void)setupSubViews
{
    
    _leftSymbolLabel = [[UILabel alloc] init];
    _leftSymbolLabel.font = [UIFont systemFontOfSize:16];
    _leftSymbolLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _leftSymbolLabel.textAlignment = NSTextAlignmentLeft;
    _leftSymbolLabel.text = @"DCC";
    [self.contentView addSubview:_leftSymbolLabel];
    [_leftSymbolLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(10);
        make.leading.equalTo(self.contentView).offset(10);
        make.height.equalTo(@20);
    }];
    
    _leftNameLabel = [[UILabel alloc] init];
    _leftNameLabel.font = [UIFont systemFontOfSize:14];
    _leftNameLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _leftNameLabel.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:_leftNameLabel];
    [_leftNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_leftSymbolLabel.mas_bottom).offset(5);
        make.leading.equalTo(_leftSymbolLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UIImageView *arrowImageView = [[UIImageView alloc] init];
    arrowImageView.image = [UIImage imageNamed:@"across_chain_right_arrow"];
    [self.contentView addSubview:arrowImageView];
    [arrowImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(_leftNameLabel).offset(0);
        make.leading.equalTo(_leftNameLabel.mas_trailing).offset(5);
        make.width.equalTo(@14);
        make.height.equalTo(@12);
    }];
    
    _rightSymbolLabel = [[UILabel alloc] init];
    _rightSymbolLabel.font = [UIFont systemFontOfSize:16];
    _rightSymbolLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _rightSymbolLabel.textAlignment = NSTextAlignmentLeft;
    _rightSymbolLabel.text = @"DCC";
    [self.contentView addSubview:_rightSymbolLabel];
    [_rightSymbolLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_leftSymbolLabel).offset(0);
        make.leading.equalTo(arrowImageView.mas_trailing).offset(5);
        make.height.equalTo(@20);
    }];
    
    _rightNameLabel = [[UILabel alloc] init];
    _rightNameLabel.font = [UIFont systemFontOfSize:14];
    _rightNameLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _rightNameLabel.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:_rightNameLabel];
    [_rightNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_rightSymbolLabel.mas_bottom).offset(5);
        make.leading.equalTo(_rightSymbolLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    _timeLabel = [[UILabel alloc] init];
    _timeLabel.font = [UIFont systemFontOfSize:16];
    _timeLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _timeLabel.textAlignment = NSTextAlignmentLeft;
    _timeLabel.numberOfLines = 0;
    [self.contentView addSubview:_timeLabel];
    [_timeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_leftNameLabel.mas_bottom).offset(5);
        make.leading.equalTo(_leftNameLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    _valueLabel = [[UILabel alloc] init];
    _valueLabel.font = [UIFont systemFontOfSize:16];
    _valueLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _valueLabel.textAlignment = NSTextAlignmentRight;
    _valueLabel.numberOfLines = 0;
    [self.contentView addSubview:_valueLabel];
    [_valueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_rightSymbolLabel).offset(0);
        make.trailing.equalTo(self.contentView).offset(-10);
        make.height.equalTo(@20);
    }];
    
    _statusLabel = [[UILabel alloc] init];
    _statusLabel.font = [UIFont systemFontOfSize:16];
    _statusLabel.textColor = COLOR_LABEL_DESCRIPTION;
    _statusLabel.textAlignment = NSTextAlignmentRight;
    _statusLabel.numberOfLines = 0;
    [self.contentView addSubview:_statusLabel];
    [_statusLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_timeLabel).offset(0);
        make.trailing.equalTo(_valueLabel).offset(0);
        make.height.equalTo(@20);
    }];
    
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = [UIColor lightGrayColor];
    line.alpha = LINE_VIEW_ALPHA;
    [self.contentView addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.contentView).offset(10);
        make.trailing.equalTo(self.contentView).offset(-10);
        make.bottom.equalTo(self.contentView).offset(0);
        make.height.equalTo(@HEIGHT_LINE);
    }];
}

//fromAssetCode=DCC && toChain=DCC_JUZIX：公链转私链
//fromAssetCode=DCC_JUZIX && toChain=DCC：私链转公链

-(void)setModel:(WeXWalletGetAcrossChainTransferRecordResponseModal_item *)model
{
    _model = model;
    
    if ([model.originAssetCode isEqualToString:@"DCC_JUZIX"]) {
        _leftSymbolLabel.textColor = ColorWithRGB(236, 128, 52);
        _leftNameLabel.textColor = ColorWithRGB(236, 128, 52);
        _leftNameLabel.text = @"@Distributed Credit Chain";
        
        _rightSymbolLabel.textColor = ColorWithRGB(86, 88, 173);
        _rightNameLabel.textColor = ColorWithRGB(86, 88, 173);
        _rightNameLabel.text = @"@Ethereum";
    }
    else
    {
        _leftSymbolLabel.textColor = ColorWithRGB(86, 88, 173);;
        _leftNameLabel.textColor = ColorWithRGB(86, 88, 173);
        _leftNameLabel.text = @"@Ethereum";
        
        _rightSymbolLabel.textColor = ColorWithRGB(236, 128, 52);
        _rightNameLabel.textColor = ColorWithRGB(236, 128, 52);
        _rightNameLabel.text = @"@Distributed Credit Chain";
    }
    NSString *destAmount = [WexCommonFunc formatterStringWithContractBalance:model.originAmount decimals:18];
    _valueLabel.text = destAmount;
    _timeLabel.text = [WexCommonFunc formatterTimeStringWithTimeStamp:model.createdTime formatter:@"yyyy-MM-dd hh:mm:ss"];
    
//    ACCEPTED -> App显示 "转移中"
//    DELIVERED -> App显示 "已完成"
    if ([model.status isEqualToString:@"ACCEPTED"]) {
        _statusLabel.text = @"转移中";
    }
    else
    {
        _statusLabel.text =@"已完成";
    }
   
    
    
    
}


@end
