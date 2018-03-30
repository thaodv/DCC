//
//  WeXBaseLoadingView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/22.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseLoadingView.h"

@interface WeXBaseLoadingView(){
    CAShapeLayer *_shapeLayer;
    BOOL _clockwise;//顺时针
    UIBezierPath *_circlePath;
    NSInteger _count;
    UIImageView *_gearImageView;//齿轮图
}

@property (nonatomic,strong)CADisplayLink *link;
@end

@implementation WeXBaseLoadingView

-(instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        [self setupSubViews];
        [self beginAnimation];
    }
    return self;
    
}

- (void)commonInit{
    _clockwise = YES;
    _count =1;
}



- (void)setupSubViews{
    
    UIView *backView = [[UIView alloc] init];
    backView.backgroundColor = [UIColor blackColor];
    backView.alpha = COVER_VIEW_ALPHA;
    [self addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.top.equalTo(self);
        make.bottom.equalTo(self);
    }];
    
    UIView *contentView = [[UIView alloc] init];
    contentView.backgroundColor = [UIColor clearColor];
    [self addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self);
        make.centerY.equalTo(self);
        make.height.equalTo(@110);
        make.width.equalTo(@110);
    }];
    
    UIImageView *logoImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"loading"]];
    [contentView addSubview:logoImageView];
    [logoImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(contentView);
        make.centerX.equalTo(contentView);

    }];
    
    UIImageView *gearImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"gear"]];
    [contentView addSubview:gearImageView];
    [gearImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(logoImageView);
        make.centerX.equalTo(logoImageView);
    }];
    _gearImageView = gearImageView;
    
   
    
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"loading...";
    titleLabel.font = [UIFont systemFontOfSize:12];
    titleLabel.textColor = [UIColor blackColor];
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.numberOfLines = 0;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(contentView).offset(0);
        make.leading.equalTo(contentView).offset(0);
        make.trailing.equalTo(contentView).offset(0);
        make.height.equalTo(@20);
    }];
    
    
//    // 创建曲线,绘制圆形path
//    _circlePath = [UIBezierPath bezierPathWithArcCenter:CGPointMake(self.center.x, self.center.y) radius:30 startAngle:-M_PI_2 endAngle:M_PI*3/2 clockwise:YES];
//    // 创建shapeLayer
//    CAShapeLayer *bottomShapeLayer = [CAShapeLayer layer];
//    bottomShapeLayer.frame = self.bounds;// 设置图层大小
//    bottomShapeLayer.path = _circlePath.CGPath;// 设置shapeLayer的cgPath
//    bottomShapeLayer.opacity = 1.0f;  //设置透明度0~1之间
//    bottomShapeLayer.lineCap = kCALineCapRound;//制定线的边缘是圆形
//    bottomShapeLayer.lineWidth = 2.0f; // 设置线宽
//    bottomShapeLayer.strokeColor = [UIColor lightGrayColor].CGColor;// 设置线条颜色
//    [bottomShapeLayer setFillColor:[UIColor clearColor].CGColor]; // 清楚填充颜色
//    [self.layer addSublayer:bottomShapeLayer];
//    bottomShapeLayer.strokeStart = 0.0;
//    bottomShapeLayer.strokeEnd = 1.0;
    
    
//    // 创建shapeLayer
//    CAShapeLayer *shapeLayer = [CAShapeLayer layer];
//    shapeLayer.masksToBounds = YES;
//    shapeLayer.frame = self.bounds;// 设置图层大小
//    shapeLayer.path = _circlePath.CGPath;// 设置shapeLayer的cgPath
//    shapeLayer.opacity = 1.0f;  //设置透明度0~1之间
//    shapeLayer.lineCap = kCALineCapRound;//制定线的边缘是圆形
//    shapeLayer.lineWidth = 2.0f; // 设置线宽
//    shapeLayer.strokeColor = ColorWithRGB(252, 30, 111).CGColor;// 设置线条颜色
//    [shapeLayer setFillColor:[UIColor clearColor].CGColor]; // 清楚填充颜色
//    [self.layer addSublayer:shapeLayer];
//    _shapeLayer = shapeLayer;
//    _shapeLayer.strokeStart = 0.0;
//    _shapeLayer.strokeEnd = 0.0;
    
//    [NSTimer scheduledTimerWithTimeInterval:0.1 target:self selector:@selector(update) userInfo:nil repeats:YES];
    
//    _link = [CADisplayLink displayLinkWithTarget:self selector:@selector(update)];
//    [_link addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    
    

    
}

- (void)beginAnimation
{
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    animation.fromValue = [NSNumber numberWithFloat:0.f];
    animation.toValue = [NSNumber numberWithFloat:M_PI*2];
    animation.duration = 5;
    animation.autoreverses = NO;
    animation.fillMode = kCAFillModeForwards;
    animation.repeatCount = MAXFLOAT;
    [_gearImageView.layer addAnimation:animation forKey:@"gear"];
    
}


- (void)update{
    
    
//    if (_shapeLayer.strokeEnd < 1.0) {
//        _shapeLayer.strokeStart = 0.0;
//        _shapeLayer.strokeEnd += 0.01;
//    }
//    else
//    {
//        if (_shapeLayer.strokeStart < 1.0) {
//            _shapeLayer.strokeEnd = 1.0;
//            _shapeLayer.strokeStart +=0.01 ;
//        }
//        else
//        {
//            _circlePath = [UIBezierPath bezierPathWithArcCenter:CGPointMake(self.center.x, self.center.y) radius:30 startAngle:-M_PI_2 endAngle:M_PI*3/2 clockwise:YES];
//            // 创建shapeLayer
//            _shapeLayer = [CAShapeLayer layer];
//            _shapeLayer.frame = self.bounds;// 设置图层大小
//            _shapeLayer.path = _circlePath.CGPath;// 设置shapeLayer的cgPath
//            _shapeLayer.opacity = 1.0f;  //设置透明度0~1之间
//            _shapeLayer.lineCap = kCALineCapRound;//制定线的边缘是圆形
//            _shapeLayer.lineWidth = 2.0f; // 设置线宽
//            _shapeLayer.strokeColor = ColorWithRGB(252, 30, 111).CGColor;// 设置线条颜色
//            [_shapeLayer setFillColor:[UIColor clearColor].CGColor]; // 清楚填充颜色
//            [self.layer addSublayer:_shapeLayer];
//            _shapeLayer.strokeStart = 0.0;
//            _shapeLayer.strokeEnd = 0.0;
//        }
//
//    }
 

}

- (void)dissMiss{
//    [_link invalidate];
    [_gearImageView.layer removeAnimationForKey:@"gear"];
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    [self removeFromSuperview];
}

-(void)dealloc
{
    NSLog(@"%s",__func__);
}



@end
