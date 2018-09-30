//
//  WeXBorrowFeeSlideView.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowFeeSlideView.h"

@interface WeXBorrowFeeSlideView()
{
    UILabel *_toastLabel;
    UIImageView *_toastImageView;
    UISlider *_slider;
    UILabel *_noBalanceLabel;
    UILabel *_feeLabel;
    
    NSString *_normalFee;
    NSString *_fastFee;
    NSString *_balance;
    
    NSString *_feeValue;
}

@end

@implementation WeXBorrowFeeSlideView

- (instancetype)initWithNormalFee:(NSString *)normalFee fastFee:(NSString *)fastFee balance:(NSString *)balance
{
    if (self = [super init]) {
        _normalFee = normalFee;
        _fastFee = fastFee;
        _balance = balance;
        [self commonInit];
        [self setupSubViews];
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect )frame normalFee:(NSString *)normalFee fastFee:(NSString *)fastFee balance:(NSString *)balance
{
    if (self = [super initWithFrame:frame]) {
        _normalFee = normalFee;
        _fastFee = fastFee;
        _balance = balance;
        [self commonInit];
        [self setupSubViews];
    }
    return self;
}

//-(instancetype)initWithFrame:(CGRect)frame
//{
//    self = [super initWithFrame:frame];
//    if (self) {
//        [self commonInit];
//        [self setupSubViews];
//    }
//    return self;
//
//}

- (void)commonInit{
    self.backgroundColor = [UIColor clearColor];
}


- (void)setupSubViews{
    
    UILabel *feeLabel = [[UILabel alloc] init];
    feeLabel.text = [NSString stringWithFormat:@"%@%@DCC",WeXLocalizedString(@"借币手续费"),_normalFee];
    feeLabel.textAlignment = NSTextAlignmentLeft;
    feeLabel.font = [UIFont systemFontOfSize:16];
    feeLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self addSubview:feeLabel];
    [feeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(15);
        make.top.equalTo(self).offset(15);
    }];
    _feeLabel = feeLabel;
    
    UILabel *balanceLabel = [[UILabel alloc] init];
    balanceLabel.text = [NSString stringWithFormat:@"%@%@DCC",WeXLocalizedString(@"持有量"),_balance];
    balanceLabel.textAlignment = NSTextAlignmentRight;
    balanceLabel.font = [UIFont systemFontOfSize:16];
    balanceLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self addSubview:balanceLabel];
    [balanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self).offset(15);
        make.trailing.equalTo(self).offset(-15);
    }];
//    _balanceLabel = balanceLabel;
    
//    [self layoutIfNeeded];
    UISlider *slider = [[UISlider alloc] init];
    [slider setThumbImage:[UIImage imageNamed:@"borrow_slide_normal"] forState:UIControlStateNormal];
    [slider setMaximumTrackImage:[UIImage imageNamed:@"borrow_slder_track"] forState:UIControlStateNormal];
    [slider setMinimumTrackImage:[UIImage imageNamed:@"borrow_slder_track"] forState:UIControlStateNormal];
    slider.minimumValue = [_normalFee integerValue];
    slider.maximumValue = [_fastFee integerValue];
    slider.value = [_normalFee integerValue];
    [slider addTarget:self action:@selector(sliderValueChange:) forControlEvents:UIControlEventValueChanged];
    [self addSubview:slider];
    [slider mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(feeLabel.mas_bottom).offset(60);
        make.leading.equalTo(self).offset(60);
        make.trailing.equalTo(self).offset(-60);
        make.height.equalTo(@10);
    }];
    _slider = slider;

    
    UILabel *toastLabel = [[UILabel alloc] init];
    toastLabel.text = _normalFee;
    toastLabel.textAlignment = NSTextAlignmentCenter;
    toastLabel.font = [UIFont systemFontOfSize:16];
    toastLabel.textColor = [UIColor whiteColor];
    [self addSubview:toastLabel];
    [toastLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(slider.mas_top).offset(-10);
        make.leading.equalTo(slider.mas_leading).offset(0);
    }];
    _toastLabel = toastLabel;
    
    UILabel *noBalanceLabel = [[UILabel alloc] init];
    noBalanceLabel.text = WeXLocalizedString(@"余额不足");
    noBalanceLabel.hidden = YES;
    noBalanceLabel.textAlignment = NSTextAlignmentCenter;
    noBalanceLabel.font = [UIFont systemFontOfSize:14];
    noBalanceLabel.textColor = [UIColor redColor];
    [self addSubview:noBalanceLabel];
    [noBalanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(toastLabel.mas_top).offset(-5);
        make.centerX.equalTo(toastLabel).offset(0);
    }];
    _noBalanceLabel = noBalanceLabel;
    
    
    UIImageView *toastImageView = [[UIImageView alloc] init];
    toastImageView.image = [self stretchImageWithImageName:@"borrow_slide_normal_toast"];
    [self addSubview:toastImageView];
    [toastImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(toastLabel);
        make.centerY.equalTo(toastLabel).offset(2);
        make.width.equalTo(toastLabel).multipliedBy(1.2);
        make.height.equalTo(toastLabel).multipliedBy(1.3);
    }];
    _toastImageView = toastImageView;
    [self insertSubview:toastImageView belowSubview:toastLabel];

    
    UILabel *normalTitleLabel = [[UILabel alloc] init];
    normalTitleLabel.text = WeXLocalizedString(@"标准模式");
    normalTitleLabel.textAlignment = NSTextAlignmentCenter;
    normalTitleLabel.font = [UIFont systemFontOfSize:15];
    normalTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self addSubview:normalTitleLabel];
    [normalTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(slider.mas_leading).offset(0);
        make.top.equalTo(slider.mas_bottom).offset(10);
    }];
    
    UILabel *normalLabel = [[UILabel alloc] init];
    normalLabel.text = _normalFee;
    normalLabel.textAlignment = NSTextAlignmentCenter;
    normalLabel.font = [UIFont systemFontOfSize:15];
    normalLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self addSubview:normalLabel];
    [normalLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(normalTitleLabel.mas_bottom).offset(5);
        make.centerX.equalTo(normalTitleLabel);
    }];
    
    
    UILabel *fastlTitleLabel = [[UILabel alloc] init];
    fastlTitleLabel.text = WeXLocalizedString(@"快速审核");
    fastlTitleLabel.textAlignment = NSTextAlignmentCenter;
    fastlTitleLabel.font = [UIFont systemFontOfSize:15];
    fastlTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self addSubview:fastlTitleLabel];
    [fastlTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(slider.mas_trailing).offset(0);
        make.top.equalTo(normalTitleLabel);
    }];
    
    UILabel *fastLabel = [[UILabel alloc] init];
    fastLabel.text = _fastFee;
    fastLabel.textAlignment = NSTextAlignmentCenter;
    fastLabel.font = [UIFont systemFontOfSize:15];
    fastLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self addSubview:fastLabel];
    [fastLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fastlTitleLabel.mas_bottom).offset(5);
        make.centerX.equalTo(fastlTitleLabel);
    }];
    
    
    [self layoutIfNeeded];
    CGFloat space = 13-(_toastImageView.frame.size.width-_toastLabel.frame.size.width)*0.5;
    [_toastLabel mas_updateConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(_slider.mas_leading).offset(-space);
    }];
    
    if ([_normalFee floatValue] > [_balance floatValue]) {
        _toastImageView.image = [self stretchImageWithImageName:@"borrow_slide_error_toast"];
        [_slider setThumbImage:[UIImage imageNamed:@"borrow_slide_error"] forState:UIControlStateNormal];
        _noBalanceLabel.hidden = NO;
    }
    else
    {
        _toastImageView.image = [self stretchImageWithImageName:@"borrow_slide_normal_toast"];
        [_slider setThumbImage:[UIImage imageNamed:@"borrow_slide_normal"] forState:UIControlStateNormal];
        _noBalanceLabel.hidden = YES;
    }
    
}

- (UIImage *)stretchImageWithImageName:(NSString *)imageName
{
    UIImage *toastImage = [UIImage imageNamed:imageName];
    CGFloat top = toastImage.size.height * 0.5;
    CGFloat left = toastImage.size.width * 0.8;
    CGFloat bottom = toastImage.size.height * 0.5;
    CGFloat right = toastImage.size.width * 0.2;
    UIEdgeInsets edgeInsets = UIEdgeInsetsMake(top, left, bottom, right);
    toastImage = [toastImage resizableImageWithCapInsets:edgeInsets resizingMode:UIImageResizingModeStretch];
    return toastImage;
}

- (void)sliderValueChange:(UISlider *)slider
{
    _feeLabel.text = [NSString stringWithFormat:@"%@%.0fDCC",WeXLocalizedString(@"借币手续费"),slider.value];
    _toastLabel.text = [NSString stringWithFormat:@"%.0f",slider.value];
    if (slider.value > [_balance floatValue]) {
        _toastImageView.image = [self stretchImageWithImageName:@"borrow_slide_error_toast"];
        [_slider setThumbImage:[UIImage imageNamed:@"borrow_slide_error"] forState:UIControlStateNormal];
        _noBalanceLabel.hidden = NO;
    }
    else
    {
        _toastImageView.image = [self stretchImageWithImageName:@"borrow_slide_normal_toast"];
        [_slider setThumbImage:[UIImage imageNamed:@"borrow_slide_normal"] forState:UIControlStateNormal];
        _noBalanceLabel.hidden = YES;
    }
    
    
    [self updateToastFrame:slider];
    
    if (_delegate) {
        [_delegate borrowFeeSlideViewWithValue:slider.value];
    }

    
}

- (void)updateToastFrame:(UISlider *)slider
{
    CGRect rect = [slider thumbRectForBounds:slider.bounds trackRect:[slider trackRectForBounds:slider.bounds] value:slider.value];
    CGFloat thumbX = rect.origin.x;
    CGFloat space = 13-(_toastImageView.frame.size.width-_toastLabel.frame.size.width)*0.5;
    [_toastLabel mas_updateConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(_slider.mas_leading).offset(thumbX-space);
    }];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
 
}



-(void)dealloc
{
    NSLog(@"%s",__func__);
}


@end
