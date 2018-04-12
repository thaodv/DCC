//
//  WeXNumberSafetyView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/16.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXNumberSafetyView.h"

@interface WeXNumberSafetyView()
{
    CGRect _tableRect;
    UIButton *_deleteBtn;
}

@property (assign, nonatomic) NSUInteger passWordNum;//密码的位数
@property (assign, nonatomic) CGFloat squareWidth;//正方形的大小
@property (assign, nonatomic) CGFloat pointRadius;//黑点的半径
@property (strong, nonatomic) UIColor *pointColor;//黑点的颜色
@property (strong, nonatomic) UIColor *rectColor;//边框的颜色

@property (strong, nonatomic) NSMutableString *numberPasswordStr;//保存密码的字符串
@property (strong, nonatomic) NSMutableString *lastNumberPasswordStr;//上一次保存密码的字符串

@property (nonatomic,strong)UILabel *descriptionLabel;//描述文字
@property (nonatomic,strong)UILabel *titleLabel;//头部文字

@property (nonatomic, strong) NSMutableArray *buttonArray;      // 初始生成button array

@end

@implementation WeXNumberSafetyView

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
    
    _tableRect = CGRectMake(20, 50, self.frame.size.width-40, 50);
    self.buttonArray = [NSMutableArray array];
    self.numberPasswordStr = [NSMutableString string];
    self.pointRadius = 6;
    self.rectColor = [UIColor lightGrayColor];
    self.pointColor = [UIColor blackColor];
}

-(void)setType:(WeXNumberPasswordType)type
{
    _type = type;
    if (self.type == WeXNumberPasswordTypeCreate) {
        _titleLabel.text = @"启用数字密码";
    }
    else if (self.type == WeXNumbePasswordTypeVerify){
        _titleLabel.text = @"验证数字密码";
    }
}


- (void)setupSubViews{
    
    _titleLabel = [[UILabel alloc] init];
    _titleLabel.text = @"启用数字密码";
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
    
    _descriptionLabel = [[UILabel alloc] init];
    _descriptionLabel.font = [UIFont systemFontOfSize:14];
    _descriptionLabel.textColor = ColorWithRGB(249, 31, 117);
    _descriptionLabel.textAlignment = NSTextAlignmentCenter;
    [self addSubview:_descriptionLabel];
    [_descriptionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_titleLabel.mas_bottom).offset(70);
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.height.equalTo(@20);
    }];
    
    NSArray *titleArray = @[@"1",@"2",@"3",@"4",@"5",@"6",@"7",@"8",@"9",@"0"];
    for (int i = 0; i < 10; ++i)
    {
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeSystem];
        [btn setTitle:titleArray[i] forState:UIControlStateNormal];
        [btn setTitleColor:ColorWithRGB(249, 31, 117) forState:UIControlStateNormal];
        [btn addTarget:self action:@selector(numberButtonClick:) forControlEvents:UIControlEventTouchUpInside];
        btn.titleLabel.font = [UIFont systemFontOfSize:22];
        [self addSubview:btn];
        if (i == 9) {
            btn.tag = 0;
        }
        else
        {
            btn.tag = i+1;
        }
        
        [self.buttonArray addObject:btn];
    }
    
    _deleteBtn = [UIButton buttonWithType:UIButtonTypeSystem];
    [_deleteBtn setTitle:@"<删除" forState:UIControlStateNormal];
    [_deleteBtn setTitleColor:ColorWithRGB(249, 31, 117) forState:UIControlStateNormal];
    [_deleteBtn addTarget:self action:@selector(deleteBtnClick) forControlEvents:UIControlEventTouchUpInside];
    _deleteBtn.titleLabel.font = [UIFont systemFontOfSize:17];
    [self addSubview:_deleteBtn];
    
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
    
    CGFloat btnW = 100;
    CGFloat btnH = 60;
    CGFloat spaceY = (self.frame.size.height-CGRectGetMaxY(_descriptionLabel.frame)-60-4*btnH)/4;
    CGFloat spaceX = (self.frame.size.width-3*btnW)/3;
    for (int i = 0; i < 10; i++) {
        CGFloat btnX = 0;
        CGFloat btnY = 0;
        if (i == 9) {
             btnX = spaceX*0.5+(spaceX+btnW);
             btnY = 3*(spaceY+btnH)+spaceY*0.5+CGRectGetMaxY(_descriptionLabel.frame);
        }
        else
        {
             btnX = spaceX*0.5+i%3*(spaceX+btnW);
             btnY =i/3*(spaceY+btnH)+spaceY*0.5+CGRectGetMaxY(_descriptionLabel.frame);
     
        }
        
        UIButton *btn = [_buttonArray objectAtIndex:i];
        btn.frame = CGRectMake(btnX, btnY, btnW, btnH);
   
    }
    
    _deleteBtn.frame = CGRectMake(self.frame.size.width-spaceX*0.5-btnW*0.5-30, 3*(spaceY+btnH)+spaceY*0.5+CGRectGetMaxY(_descriptionLabel.frame), 60, btnH);
    
}

- (void)numberButtonClick:(UIButton *)btn{
    if (self.numberPasswordStr.length == 6) {
        return;
    }
    
    [self.numberPasswordStr appendString:[NSString stringWithFormat:@"%ld",btn.tag]];
        
    if (self.numberPasswordStr.length == 6) {
        if (self.type == WeXNumberPasswordTypeCreate) {
            // 是否已经设了密码，等待验证
            if (self.lastNumberPasswordStr && self.lastNumberPasswordStr.length > 1)
            {
                if ([self.numberPasswordStr isEqualToString:self.lastNumberPasswordStr])
                {
                    // 验证通过，将密码保存至本地
                    NSLog(@"保存成功,密码=%@",self.numberPasswordStr);
                    WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
                    if (model) {
                        model.numberPassword = self.numberPasswordStr;
                        model.passwordType = WeXPasswordTypeNumber;
                    }
                    [WexCommonFunc savePassport:model];
                    if ([_delegate respondsToSelector:@selector(numberSafetyViewDidSetNumberPassword)]) {
                        [self.delegate numberSafetyViewDidSetNumberPassword];
                    }
            
                }
                else
                {
                    _descriptionLabel.text = @"数字密码错误，请重试";
                }
            }
            else
            {
                _titleLabel.text = @"再次输入数字密码";
                // 将初始密码保存
                self.lastNumberPasswordStr = self.numberPasswordStr;
            }
      
        }
        else if (self.type == WeXNumbePasswordTypeVerify)
        {
            WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
            NSString *password = model.numberPassword;
            if ([password isEqualToString:self.numberPasswordStr])
            {
                // 解锁成功
                NSLog(@"解锁成功");
                if ([_delegate respondsToSelector:@selector(numberSafetyViewVerifySuccess)]) {
                    [self.delegate numberSafetyViewVerifySuccess];
                }
            }
            else
            {
                _descriptionLabel.text = @"数字密码错误，请重试";
            }
            
            
        }
        
        self.numberPasswordStr = [NSMutableString string];
      
    }
 
    [self setNeedsDisplay];
    
   
    
}

- (void)deleteBtnClick
{
    if (self.numberPasswordStr.length == 0) {
        return;
    }
    [self.numberPasswordStr deleteCharactersInRange:NSMakeRange(self.numberPasswordStr.length-1, 1)];
    [self setNeedsDisplay];
    NSLog(@"textStore=%@",self.numberPasswordStr);
    
}

- (void)cancelBtnClick{
    
    if ([_delegate respondsToSelector:@selector(numberSafetyViewCancel)]) {
        [self.delegate numberSafetyViewCancel];
    }
    
}


- (void)drawRect:(CGRect)rect {
    
    CGFloat height = _tableRect.size.height;
    CGFloat width = _tableRect.size.width;
    CGContextRef context = UIGraphicsGetCurrentContext();
    //画外框
    CGContextAddRect(context, _tableRect);
    CGContextSetLineWidth(context, 1);
    CGContextSetStrokeColorWithColor(context, self.rectColor.CGColor);
    CGContextSetFillColorWithColor(context, [UIColor whiteColor].CGColor);
    
    CGContextSaveGState(context);
    //画竖条
    for (int i = 1; i < 6; i++) {
        CGContextMoveToPoint(context,20+i*width/6, _tableRect.origin.y);
        CGContextAddLineToPoint(context,20+i*width/6,CGRectGetMaxY(_tableRect));
        CGContextStrokePath(context);
    }
    [[UIColor lightGrayColor] set];
    CGContextRestoreGState(context);
    //画黑点
    CGContextSaveGState(context);
    [[UIColor blackColor] set];
    for (int i = 1; i <= self.numberPasswordStr.length; i++) {
        CGContextAddArc(context,  20+i*width/6 - width/6/2, _tableRect.origin.y+height*0.5, self.pointRadius, 0, M_PI*2, YES);
        CGContextFillPath(context);
    }
    CGContextRestoreGState(context);
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
