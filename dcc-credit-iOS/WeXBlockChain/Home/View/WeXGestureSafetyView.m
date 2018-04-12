//
//  WeXGestureSafetyView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/16.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXGestureSafetyView.h"

@interface WeXGestureSafetyView()
{
    int _pswErrorCount;     // 密码错误次数统计
}

@property (nonatomic, strong) NSMutableArray *buttonArray;      // 初始生成button array
@property (nonatomic, strong) NSMutableArray *selectedButtons;  // 被选中的button array

@property (nonatomic, assign) CGPoint lineStartPoint;
@property (nonatomic, assign) CGPoint lineEndPoint;
@property (nonatomic,assign) CGPoint currentPoint;

@property (nonatomic, strong) UIImage *normalImage;
@property (nonatomic, strong) UIImage *selectedImage;
@property (nonatomic, strong) UIImage *errorImage;

@property (nonatomic, strong) NSMutableString *savedPswString;  // 存储密码序列
@property (nonatomic, strong) NSMutableString *lastPswString;  // 上一次存储的密码

@property (nonatomic,strong)UILabel *descriptionLabel;//描述文字
@property (nonatomic,strong)UILabel *descriptionDetailLabel;//描述文字
@property (nonatomic,strong)UILabel *titleLabel;//头部文字


@end


@implementation WeXGestureSafetyView

-(instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        [self setupSubViews];
    }
    return self;
    
}

- (void)commonInit{
    
    self.backgroundColor = [UIColor whiteColor];
    // 正常图片效果
    self.normalImage = [UIImage imageNamed:@"get_normal"];
    // 选中的图片效果
    self.selectedImage = [UIImage imageNamed:@"ges_select"];
    // 选中后异常的图片效果
    self.errorImage = [UIImage imageNamed:@"ges_error"];
    
    _selectedButtons = [NSMutableArray array];
    
    _buttonArray = [NSMutableArray array];
 
}

-(void)setType:(WeXGesturePswType)type
{
    _type = type;
    if (self.type == WeXGesturePswTypeCreate) {
        _titleLabel.text = @"启用手势密码";
    }
    else if (self.type == WeXGesturePswTypeVerify){
        _titleLabel.text = @"验证手势密码";
    }
}

- (void)setupSubViews{
    
    _titleLabel = [[UILabel alloc] init];
    _titleLabel.font = [UIFont systemFontOfSize:20];
    _titleLabel.textColor = ColorWithRGB(249, 31, 117);
    _titleLabel.textAlignment = NSTextAlignmentCenter;
    [self addSubview:_titleLabel];
    [_titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self);
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.height.equalTo(@45);
    }];

    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = [UIColor lightGrayColor];
    line1.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.trailing.equalTo(self);
        make.top.equalTo(_titleLabel.mas_bottom);
        make.height.equalTo(@LINE_VIEW_Width);
    }];
    
    _descriptionLabel = [[UILabel alloc] init];
    _descriptionLabel.text = @"请绘制解图";
    _descriptionLabel.font = [UIFont systemFontOfSize:15];
    _descriptionLabel.textColor = [UIColor lightGrayColor];
    _descriptionLabel.textAlignment = NSTextAlignmentCenter;
    [self addSubview:_descriptionLabel];
    [_descriptionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line1.mas_bottom).offset(5);
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.height.equalTo(@20);
    }];
    
    _descriptionDetailLabel = [[UILabel alloc] init];
    _descriptionDetailLabel.font = [UIFont systemFontOfSize:12];
    _descriptionDetailLabel.textColor = ColorWithRGB(249, 31, 117);
    _descriptionDetailLabel.textAlignment = NSTextAlignmentCenter;
    [self addSubview:_descriptionDetailLabel];
    [_descriptionDetailLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_descriptionLabel.mas_bottom).offset(0);
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.height.equalTo(@20);
    }];
    
  
    for (int i = 0; i < 9; ++i)
    {
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        [btn setBackgroundImage:self.normalImage forState:UIControlStateNormal];
        [btn setBackgroundImage:self.selectedImage forState:UIControlStateSelected];
        [self addSubview:btn];
        btn.userInteractionEnabled = NO;
        btn.tag = i;
        [self.buttonArray addObject:btn];
    }
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
    [cancelBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    cancelBtn.titleLabel.font = [UIFont systemFontOfSize:17];
    [self addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self);
        make.leading.trailing.equalTo(self);
        make.height.mas_equalTo(60);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = [UIColor lightGrayColor];
    line2.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(cancelBtn.mas_top);
        make.leading.trailing.equalTo(self);
        make.height.mas_equalTo(LINE_VIEW_Width);
    }];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    
    CGFloat btnW = 45;
    CGFloat btnH = 45;
    CGFloat space = (self.frame.size.height-CGRectGetMaxY(_descriptionLabel.frame)-60-3*btnW)/3;
    CGFloat spaceX = (self.frame.size.width-3*btnW-2*space)/2;
    CGFloat spaceY = CGRectGetMaxY(_descriptionLabel.frame)+space *0.5;
    
    for (int i = 0; i < 9; i++) {
        
        CGFloat btnX = spaceX+i%3*(space+btnW);
        CGFloat btnY =i/3*(space+btnH)+spaceY;
        
        UIButton *btn = [_buttonArray objectAtIndex:i];
        btn.frame = CGRectMake(btnX, btnY, btnW, btnH);
    }
  
}
#pragma mark - 点击取消按钮
- (void)cancelBtnClick{
    
    if ([_delegate respondsToSelector:@selector(gestureSafetyViewCancel)]) {
        [self.delegate gestureSafetyViewCancel];
    }
    
    
}

#pragma mark - 手势密码相关
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    self.savedPswString = nil;
    self.currentPoint = CGPointZero;
    
    UITouch *touch = [touches anyObject];
    CGPoint pos = [touch locationInView:touch.view];
    
    for (UIButton *btn in self.buttonArray)
    {
        if (CGRectContainsPoint(btn.frame, pos)) {
            btn.selected = YES;
            [self.selectedButtons addObject:btn];
        }
    }
    
    [self setNeedsDisplay];
    
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    
    UITouch *touch = [touches anyObject];
    CGPoint pos = [touch locationInView:touch.view];
    
    for (UIButton *btn in self.buttonArray)
    {
        if (CGRectContainsPoint(btn.frame, pos)&&btn.isSelected==NO) {
            btn.selected = YES;
            [self.selectedButtons addObject:btn];
            
        }
        else
        {
            self.currentPoint = pos;
        }
    }
    
    [self setNeedsDisplay];
    
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    for (UIButton *btn in self.selectedButtons) {
        //获取路径
        [self.savedPswString appendFormat:@"%ld",(long)btn.tag];
    }
    
    [self configDrawGestureEnd];
    
}

- (void)configDrawGestureEnd{
    
    if (self.type == WeXGesturePswTypeCreate) {
        // 是否已经设了密码，等待验证
        if (self.lastPswString && self.lastPswString.length > 1)
        {
            if ([self.savedPswString isEqualToString:self.lastPswString])
            {
                
                [self resetGesturePswDrawZoom];
                // 验证通过，将密码保存至本地
                NSLog(@"保存成功,密码=%@",self.savedPswString);
                WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
                if (model) {
                    model.gesturePassword = self.savedPswString;
                    model.passwordType = WeXPasswordTypeGesture;
                }
                [WexCommonFunc savePassport:model];
                if ([_delegate respondsToSelector:@selector(gestureSafetyViewDidSetGesturePassword)]) {
                    [self.delegate gestureSafetyViewDidSetGesturePassword];
                }
            }
            else
            {
                [self tintLabelWithExceptString:DrawPsw_Inconsistent_Lasttime];
                
                [self shaketimes:Shake_Times direction:1 currentTimes:0];
                
                [self drawGestureExceptionEffect];
            }
        }
        else
        {
            // 设置密码，重复密码
            if (self.savedPswString.length < 4)
            {
                [self tintLabelWithExceptString:DrawPsw_Less_4points_Warning];
                
                [self shaketimes:Shake_Times direction:1 currentTimes:0];
                
                [self drawGestureExceptionEffect];
            }
            else
            {
                _descriptionLabel.text = DrawPsw_Draw_Unlock_Pattern_Again;
                // 将初始密码保存
                self.lastPswString = self.savedPswString;
                
                [self resetGesturePswDrawZoom];
            }
        }
    }
    else if (self.type == WeXGesturePswTypeVerify){
        // 手势解密
        WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
        NSString *password = model.gesturePassword;
        if ([password isEqualToString:self.savedPswString])
        {
            // 解锁成功
            NSLog(@"解锁成功");
            if ([_delegate respondsToSelector:@selector(gestureSafetyViewVerifySuccess)]) {
                [self.delegate gestureSafetyViewVerifySuccess];
            }
            [self resetGesturePswDrawZoom];
        }
        else
        {
            if (self.savedPswString.length > 0)
            {
            
                [self tintLabelWithExceptString:@"密码错误!"];
                
                [self shaketimes:Shake_Times direction:1 currentTimes:0];
                
                [self drawGestureExceptionEffect];
              
            }
        }
    }
    
}

#pragma mark -  异常提示label的效果
- (void) tintLabelWithExceptString:(NSString *)tintString
{
    _descriptionDetailLabel.text = tintString;
    // 红色
    _descriptionDetailLabel.textColor = ColorWithRGB(255.0f, 60.0f, 48.0f);
}

#pragma mark -  正确的情况正常提示
- (void) tintLabelWitnNomalString:(NSString *)tintString
{
    _descriptionDetailLabel.text = tintString;
    
    // 蓝色
    _descriptionDetailLabel.textColor = ColorWithRGB(255.0f, 102.0f, 0.0f);
    
    [self resetGesturePswDrawZoom];
    
    
    
}


#pragma mark - 绘制手势密码异常时的效果
- (void) drawGestureExceptionEffect
{
    for (UIButton *btn in self.selectedButtons)
    {
        [btn setBackgroundImage:self.errorImage forState:UIControlStateSelected];
    }
  
    [self performSelector:@selector(resetGesturePswDrawZoom) withObject:nil afterDelay:0.2f];
}

#pragma mark - 还原手势密码绘制区域
- (void) resetGesturePswDrawZoom
{
    for (UIButton *btn in self.selectedButtons)
    {
        btn.selected = NO;
        [btn setBackgroundImage:self.selectedImage forState:UIControlStateSelected];
    }
    [self.selectedButtons removeAllObjects];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
         self.descriptionDetailLabel.text = @"";
    });
    [self setNeedsDisplay];
}

/** 提示框
 *  晃动提示框
 *  @param times     晃动的次数
 *  @param direction 左右晃动，选左还是右
 *  @param current   当前的次数
 */
- (void)shaketimes:(int)times direction:(int)direction currentTimes:(int)current
{
    [UIView animateWithDuration:0.10 animations:^{
        _descriptionDetailLabel.transform = CGAffineTransformMakeTranslation(5 * direction, 0);
    } completion:^(BOOL finished) {
        if(current >= times) {
            _descriptionDetailLabel.transform = CGAffineTransformIdentity;
            return;
        }
        [self shaketimes:(times - 1) direction:direction * -1 currentTimes:current + 1];
    }];
}

#pragma mark - 解锁密码错误5次的alert提示
- (void) pswErrorExceedTint
{
    _pswErrorCount = 0;
    
    // 错误次数超过5次，请重新登录
//    [WpCommonFunction messageBoxOneButtonWithMessage:DrawPsw_PswError_5Times_Warning andTag:kGestures_Psw_Error5times andDelegate:self];
}


- (void)drawRect:(CGRect)rect {
    CGContextRef ctx = UIGraphicsGetCurrentContext();
    CGContextSetLineWidth(ctx, 2);
    CGContextSetLineJoin(ctx, kCGLineJoinBevel);
    CGContextSetStrokeColorWithColor(ctx, ColorWithRGB(254, 216, 232).CGColor) ;
    for (int i = 0;i < self.selectedButtons.count;i++) {
        UIButton *btn = self.selectedButtons[i];
        if (i == 0) {
            CGContextMoveToPoint(ctx, btn.center.x, btn.center.y);
        }
        else
        {
            CGContextAddLineToPoint(ctx, btn.center.x, btn.center.y);
        }
    }
    if (self.selectedButtons.count &&!CGPointEqualToPoint(self.currentPoint, CGPointZero)) {
        CGContextAddLineToPoint(ctx, self.currentPoint.x, self.currentPoint.y);
        
    }
    
    
    CGContextStrokePath(ctx);
    
}

-(NSMutableString *)savedPswString
{
    if (!_savedPswString) {
        _savedPswString = [NSMutableString string];
    }
    return _savedPswString;
}

- (void)dismiss{
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        for (UIView *view in self.subviews) {
            [view removeFromSuperview];
        }
        
        [self removeFromSuperview];
    });
    
}

-(void)dealloc{
    NSLog(@"%s",__func__);
}




@end
