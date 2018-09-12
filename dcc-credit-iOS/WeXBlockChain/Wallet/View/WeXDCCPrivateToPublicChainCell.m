//
//  WeXDCCPrivateToPublicChainCell.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/17.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDCCPrivateToPublicChainCell.h"

@implementation WeXDCCPrivateToPublicChainCell

@end
@implementation WeXDCCPrivateToPublicChainFirstCell
{
    UIImageView *_backImageView1;
    UIImageView *_backImageView2;
    CAGradientLayer *_gradientLayer1;
    CAGradientLayer *_gradientLayer2;
}

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
    
    UIImageView *arrowImageView = [[UIImageView alloc] init];
    arrowImageView.image = [UIImage imageNamed:@"across_chain_right_yellow"];
    [self.contentView addSubview:arrowImageView];
    [arrowImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.contentView).offset(0);
        make.centerX.equalTo(self.contentView).offset(0);
        make.height.equalTo(@25);
        make.width.equalTo(@25);
    }];
    
    UIImageView *backImageView1 = [[UIImageView alloc] init];
    backImageView1.layer.cornerRadius = 6;
    backImageView1.layer.masksToBounds = YES;
    [self.contentView addSubview:backImageView1];
    [backImageView1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.contentView).offset(0);
        make.leading.equalTo(self.contentView).offset(15);
        make.trailing.equalTo(arrowImageView.mas_leading).offset(-5);
        make.height.equalTo(@85);
    }];
    _backImageView1 = backImageView1;
    
    UILabel *symbolLabel1 = [[UILabel alloc] init];
    symbolLabel1.text = @"DCC";
    symbolLabel1.font = [UIFont systemFontOfSize:16];
    symbolLabel1.textColor = [UIColor whiteColor];
    symbolLabel1.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:symbolLabel1];
    [symbolLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView1).offset(10);
        make.leading.equalTo(backImageView1).offset(10);
    }];
    
    UILabel *nameLabel1 = [[UILabel alloc] init];
    nameLabel1.text = @"@Distributed Credit Chain";
    nameLabel1.font = [UIFont systemFontOfSize:14];
    nameLabel1.textColor = [UIColor whiteColor];
    nameLabel1.textAlignment = NSTextAlignmentLeft;
    nameLabel1.adjustsFontSizeToFitWidth = true;
    [self.contentView addSubview:nameLabel1];
    [nameLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(symbolLabel1.mas_bottom).offset(0);
        make.leading.equalTo(symbolLabel1);
        make.trailing.equalTo(backImageView1).offset(-5);
    }];
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = [UIColor whiteColor];
    line1.alpha = 0.5;
    [self.contentView addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(backImageView1).offset(10);
        make.trailing.equalTo(backImageView1).offset(-10);
        make.top.equalTo(nameLabel1.mas_bottom).offset(5);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    
    UILabel *balanceLabel1 = [[UILabel alloc] init];
    balanceLabel1.text = @"--";
    balanceLabel1.font = [UIFont systemFontOfSize:18];
    balanceLabel1.textColor = [UIColor whiteColor];
    balanceLabel1.textAlignment = NSTextAlignmentCenter;
    [self.contentView addSubview:balanceLabel1];
    [balanceLabel1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(0);
        make.centerX.equalTo(backImageView1).offset(0);
        make.bottom.equalTo(backImageView1).offset(0);
    }];
    _privateBalanceLabel = balanceLabel1;
    
    UIImageView *backImageView2 = [[UIImageView alloc] init];
    backImageView2.layer.cornerRadius = 6;
    backImageView2.layer.masksToBounds = YES;
    [self.contentView addSubview:backImageView2];
    [backImageView2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.contentView).offset(0);
        make.leading.equalTo(arrowImageView.mas_trailing).offset(5);
        make.trailing.equalTo(self.contentView).offset(-15);
        make.height.equalTo(@85);
    }];
    _backImageView2 = backImageView2;
    
    UILabel *symbolLabel2 = [[UILabel alloc] init];
    symbolLabel2.text = @"DCC";
    symbolLabel2.font = [UIFont systemFontOfSize:16];
    symbolLabel2.textColor = [UIColor whiteColor];
    symbolLabel2.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:symbolLabel2];
    [symbolLabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView2).offset(10);
        make.leading.equalTo(backImageView2).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *nameLabel2 = [[UILabel alloc] init];
    nameLabel2.text = @"@Ethereum";
    nameLabel2.font = [UIFont systemFontOfSize:14];
    nameLabel2.textColor = [UIColor whiteColor];
    nameLabel2.textAlignment = NSTextAlignmentLeft;
    nameLabel2.adjustsFontSizeToFitWidth = true;
    [self.contentView addSubview:nameLabel2];
    [nameLabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(symbolLabel2.mas_bottom).offset(0);
        make.leading.equalTo(symbolLabel2);
        make.trailing.equalTo(backImageView2).offset(-5);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = [UIColor whiteColor];
    line2.alpha = 0.5;
    [self.contentView addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(backImageView2).offset(10);
        make.trailing.equalTo(backImageView2).offset(-10);
        make.top.equalTo(nameLabel2.mas_bottom).offset(5);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    
    UILabel *balanceLabel2 = [[UILabel alloc] init];
    balanceLabel2.text = @"--";
    balanceLabel2.font = [UIFont systemFontOfSize:18];
    balanceLabel2.textColor = [UIColor whiteColor];
    balanceLabel2.textAlignment = NSTextAlignmentCenter;
    [self.contentView addSubview:balanceLabel2];
    [balanceLabel2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line2.mas_bottom).offset(0);
        make.centerX.equalTo(backImageView2).offset(0);
        make.bottom.equalTo(backImageView2).offset(0);
    }];
    _publicBalanceLabel = balanceLabel2;
    
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
-(void)layoutSubviews
{
    
    if (!_gradientLayer1) {
        [_backImageView1 layoutIfNeeded];
        _gradientLayer1 = [CAGradientLayer layer];
        _gradientLayer1.frame = _backImageView1.bounds;
        _gradientLayer1.colors = @[(__bridge id)ColorWithHex(0xfc7c03).CGColor,(__bridge id)ColorWithHex(0xfeae6c).CGColor];
        _gradientLayer1.locations = @[@0.0,@1.0];
        _gradientLayer1.startPoint = CGPointMake(0, 0);
        _gradientLayer1.endPoint = CGPointMake(1, 0);
        [_backImageView1.layer addSublayer:_gradientLayer1];
    }
    
    if (!_gradientLayer2) {
        [_backImageView2 layoutIfNeeded];
        _gradientLayer2 = [CAGradientLayer layer];
        _gradientLayer2.frame = _backImageView2.bounds;
        _gradientLayer2.colors = @[(__bridge id)ColorWithHex(0x5944c3).CGColor,(__bridge id)ColorWithHex(0x8280de).CGColor];
        _gradientLayer2.locations = @[@0.0,@1.0];
        _gradientLayer2.startPoint = CGPointMake(0, 0);
        _gradientLayer2.endPoint = CGPointMake(1, 0);
        [_backImageView2.layer addSublayer:_gradientLayer2];
    }
    
   
}

+ (CGFloat)height
{
    return 105;
}


@end
@implementation WeXDCCPrivateToPublicChainSecondCell

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
    
    UILabel *titelLabel = [[UILabel alloc] init];
    titelLabel.text = @"转移数量";
    titelLabel.font = [UIFont systemFontOfSize:16];
    titelLabel.textColor = [UIColor blackColor];
    titelLabel.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:titelLabel];
    [titelLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(10);
        make.leading.equalTo(self.contentView).offset(15);
        make.height.equalTo(@20);
    }];
    
    UILabel *symbolLabel = [[UILabel alloc] init];
    symbolLabel.text = @"DCC";
    symbolLabel.font = [UIFont systemFontOfSize:16];
    symbolLabel.textColor = [UIColor blackColor];
    symbolLabel.textAlignment = NSTextAlignmentRight;
    [self.contentView addSubview:symbolLabel];
    [symbolLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titelLabel.mas_bottom).offset(15);
        make.trailing.equalTo(self.contentView).offset(-10);
        make.height.equalTo(@50);
        make.width.equalTo(@40);
    }];
    
    _valueTextField = [[UITextField alloc] init];
    _valueTextField.borderStyle = UITextBorderStyleNone;
    _valueTextField.backgroundColor = [UIColor clearColor];
    _valueTextField.textColor = COLOR_LABEL_DESCRIPTION;
    _valueTextField.font = [UIFont systemFontOfSize:17];
    _valueTextField.keyboardType = UIKeyboardTypeDecimalPad;
    [self.contentView addSubview:_valueTextField];
    [_valueTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(symbolLabel).offset(0);
        make.leading.equalTo(self.contentView).offset(15);
        make.trailing.equalTo(symbolLabel.mas_leading).offset(-5);
        make.height.equalTo(@50);
    }];
    
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = [UIColor lightGrayColor];
    line.alpha = LINE_VIEW_ALPHA;
    [self.contentView addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.contentView).offset(10);
        make.trailing.equalTo(self.contentView).offset(-10);
        make.top.equalTo(_valueTextField.mas_bottom).offset(0);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *privateBalanceLabel = [[UILabel alloc] init];
    privateBalanceLabel.font = [UIFont systemFontOfSize:16];
    privateBalanceLabel.textColor = [UIColor blackColor];
    privateBalanceLabel.textAlignment = NSTextAlignmentRight;
    [self.contentView addSubview:privateBalanceLabel];
    [privateBalanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line.mas_bottom).offset(0);
        make.leading.equalTo(self.contentView).offset(15);
        make.height.equalTo(@60);
    }];
    _privateBalanceLabel = privateBalanceLabel;
    
    UIButton *allBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [allBtn setTitleColor:COLOR_THEME_ALL forState:UIControlStateNormal];
    [allBtn setTitle:@"全部" forState:UIControlStateNormal];
    [allBtn addTarget:self action:@selector(allBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.contentView addSubview:allBtn];
    [allBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(privateBalanceLabel).offset(0);
        make.trailing.equalTo(self.contentView).offset(-15);
        make.height.equalTo(@60);
//        make.width.equalTo(@30);
    }];
    UIView *backView = [[UIView alloc] init];
    backView.backgroundColor = ColorWithRGB(248, 248, 254);
    [self.contentView addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(privateBalanceLabel.mas_bottom).offset(0);
        make.leading.trailing.equalTo(self.contentView);
        make.height.equalTo(@10);
    }];
    
    
    
}

- (void)allBtnClick
{
    WEXNSLOG(@"%s",__func__);
    if (_allButtonBlock) {
        _allButtonBlock();
    }
}


+ (CGFloat)height
{
    return 165;
}

@end
@implementation WeXDCCPrivateToPublicChainThirdCell

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
    
    UILabel *titelLabel = [[UILabel alloc] init];
    titelLabel.text = @"转移数量";
    titelLabel.font = [UIFont systemFontOfSize:16];
    titelLabel.textColor = [UIColor blackColor];
    titelLabel.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:titelLabel];
    [titelLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.contentView).offset(0);
        make.leading.equalTo(self.contentView).offset(15);
        make.height.equalTo(@20);
    }];
    _titleLabel = titelLabel;
    
    UILabel *symbolLabel = [[UILabel alloc] init];
    symbolLabel.text = @"DCC";
    symbolLabel.font = [UIFont systemFontOfSize:16];
    symbolLabel.textColor = [UIColor blackColor];
    symbolLabel.textAlignment = NSTextAlignmentRight;
    [self.contentView addSubview:symbolLabel];
    [symbolLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.contentView).offset(0);
        make.trailing.equalTo(self.contentView).offset(-15);
        make.height.equalTo(@20);
    }];
   [symbolLabel setContentHuggingPriority:UILayoutPriorityDefaultHigh forAxis:UILayoutConstraintAxisHorizontal];
    
    UILabel *contentLabel = [[UILabel alloc] init];
    contentLabel.font = [UIFont systemFontOfSize:16];
    contentLabel.textColor = [UIColor blackColor];
    contentLabel.textAlignment = NSTextAlignmentRight;
    [self.contentView addSubview:contentLabel];
    [contentLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.contentView).offset(0);
        make.leading.equalTo(titelLabel.mas_trailing).offset(15);
        make.trailing.equalTo(symbolLabel.mas_leading).offset(-10);
        make.height.equalTo(@20);
    }];
    _contentLabel = contentLabel;
    
    
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


+ (CGFloat)height
{
    return 60;
}

@end
@implementation WeXDCCPrivateToPublicChainFourthCell


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
    
    UIImageView *coinImageView = [[UIImageView alloc] init];
    coinImageView.image = [UIImage imageNamed:@"ic_warning_black_24dp_2x"];
    [self.contentView addSubview:coinImageView];
    [coinImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(50);
        make.leading.equalTo(self.contentView).offset(15);
        make.width.equalTo(@17);
        make.height.equalTo(@14);
    }];
    
    UILabel *label1 = [[UILabel alloc] init];
    label1.text = @"温馨提示:";
    label1.font = [UIFont systemFontOfSize:16];
    label1.textColor = [UIColor blackColor];
    label1.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(coinImageView).offset(0);
        make.leading.equalTo(coinImageView.mas_trailing).offset(5);
        make.height.equalTo(@20);
    }];
//
    UILabel *label3 = [[UILabel alloc] init];
    label3.text = @"结果返回前，请不要重复转移DCC";
    label3.font = [UIFont systemFontOfSize:15];
    label3.textColor = [UIColor redColor];
    label3.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:label3];
    [label3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label1.mas_bottom).offset(5);
        make.leading.equalTo(coinImageView).offset(0);
        make.height.equalTo(@20);
    }];
    
    
    UILabel *label2 = [[UILabel alloc] init];
    label2.text = @"交易记录需要6个区块的生成时间进行确认";
    label2.font = [UIFont systemFontOfSize:15];
    label2.textColor = [UIColor blackColor];
    label2.textAlignment = NSTextAlignmentLeft;
    label2.adjustsFontSizeToFitWidth = true;
    [self.contentView addSubview:label2];
    [label2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label3.mas_bottom).offset(5);
        make.leading.equalTo(coinImageView).offset(0);
        make.height.equalTo(@20);
        make.trailing.equalTo(self).offset(-5);
    }];
//
    UIButton *nextBtn = [WeXCustomButton button];
    [nextBtn setTitle:@"下一步" forState:UIControlStateNormal];
    [nextBtn addTarget:self action:@selector(nextBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.contentView addSubview:nextBtn];
    [nextBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label2.mas_bottom).offset(10);
        make.trailing.equalTo(self.contentView).offset(-15);
        make.leading.equalTo(self.contentView).offset(15);
        make.height.equalTo(@45);
    }];
}

- (void)nextBtnClick
{
  
    if (_nextButtonBlock) {
        _nextButtonBlock();
    }


}


+ (CGFloat)height
{
    return 210;
}

@end

