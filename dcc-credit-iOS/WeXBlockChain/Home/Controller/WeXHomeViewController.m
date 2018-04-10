//
//  WeXHomeViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/13.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXHomeViewController.h"
#import "WeXImportViewController.h"
#import "WeXRegisterView.h"
#import "WeXRegisterSuccessViewController.h"
#import "YYKeyboardManager.h"
#import "WeXGetTicketAdapter.h"
#import "WeXGetContractAddressAdapter.h"
#import "WeXUploadPubKeyAdapter.h"
#import "WeXGetReceiptResultAdapter.h"
#import "WeXGetPubKeyAdapter.h"

#import "WeXInfoHudView.h"


#define kRegisterViewHeight 210

@interface WeXHomeViewController ()<YYKeyboardObserver,CAAnimationDelegate>
{
    WeXRegisterView *_registerView;
    UIView *_coverView;//蒙版
    
    RSA *_publicKey;
    RSA *_privateKey;
    
    NSString *_rsaPublicKey;
    NSString *_rsaPrivateKey;
    NSString *_walletAddress;//钱包地址
    NSString *_walletPrivateKey;//钱包私钥
    NSDictionary *_keyStroe;//口袋
    
    NSString *_rawTransaction;
    
    NSString *_txHash;
    
    NSString *_contractAddress;//合约地址
    
    NSInteger _requestCount;//查询上链结果请求的次数
    
    CABasicAnimation *_animation7;
    
    
    UILabel *_label1;
    UIImageView *_imageView1;
    UIImageView *_imageView2;
    UIImageView *_imageView3;
    UIImageView *_imageView4;
    UILabel *_label2;
    UIImageView *_imageView5;
    UIImageView *_imageView6;
    UILabel *_label3;
    UIImageView *_imageView7;
    UIImageView *_imageView8;
}
@property (nonatomic,strong)WeXGetTicketAdapter *getTicketAdapter;
@property (nonatomic,strong)WeXGetContractAddressAdapter *getContractAddressAdapter;
@property (nonatomic,strong)WeXUploadPubKeyAdapter *uoloadPubKeyAdapter;
@property (nonatomic,strong)WeXGetReceiptResultAdapter *getReceiptAdapter;
@property (nonatomic,strong)WeXGetTicketResponseModal *getTicketModel;
@property (nonatomic,strong)WeXGetPubKeyAdapter *getPubKeyAdapter;

@end

@implementation WeXHomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [[YYKeyboardManager defaultManager] addObserver:self];
    _requestCount = 1;
    [self setupSubViews];
    
    
//    UIImage *image = [YYImage imageNamed:@"ani.gif"];
//    UIImageView *imageView = [[YYAnimatedImageView alloc] initWithImage:image];
//    [self.view addSubview:imageView];

 
}

- (void)configAnimation1
{
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(kScreenWidth*0.5-80, 50*scrRatioH, 80, 20)];
    label1.text = @"创造身份";
    label1.textColor = [UIColor whiteColor];
    label1.textAlignment = NSTextAlignmentRight;
    label1.font = [UIFont systemFontOfSize:14];
    [self.view addSubview:label1];
    _label1 = label1;
    
    UIImageView *imageView1 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth*0.5, -70, 70, 70)];
    imageView1.image = [UIImage imageNamed:@"globe"];
    imageView1.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView1];
    _imageView1 = imageView1;
    
    CABasicAnimation *animation1 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation1.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5, label1.frame.origin.y+10)];
    animation1.duration = 0.5;
    animation1.beginTime = CACurrentMediaTime()+0;
    animation1.fillMode=kCAFillModeForwards;
    animation1.removedOnCompletion = NO;
    [imageView1.layer addAnimation:animation1 forKey:nil];
    
    
    UIImageView *imageView2 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth, 70, 45, 30)];
    imageView2.image = [UIImage imageNamed:@"vcard"];
    imageView2.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView2];
    _imageView2 = imageView2;
    
    
    CABasicAnimation *animation2 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation2.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5+60, label1.frame.origin.y+45)];
    animation2.duration = 0.5;
    animation2.beginTime = CACurrentMediaTime()+0.5;
    animation2.fillMode=kCAFillModeForwards;
    animation2.removedOnCompletion = NO;
    [imageView2.layer addAnimation:animation2 forKey:nil];
    
    
    UIImageView *imageView3 = [[UIImageView alloc] initWithFrame:CGRectMake(-100, 50, 50, 100)];
    imageView3.image = [UIImage imageNamed:@"iPhone"];
    imageView3.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView3];
    _imageView3 = imageView3;
    
    CABasicAnimation *animation3 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation3.toValue = [NSValue valueWithCGPoint:CGPointMake(40, 120*scrRatioH)];
    animation3.duration = 0.5;
    animation3.beginTime = CACurrentMediaTime()+1;
    animation3.fillMode=kCAFillModeForwards;
    animation3.removedOnCompletion = NO;
    [imageView3.layer addAnimation:animation3 forKey:nil];
    
    UIImageView *imageView4 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth, 155, 50, 30)];
    imageView4.image = [UIImage imageNamed:@"key"];
    imageView4.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView4];
    _imageView4 = imageView4;
    
    CABasicAnimation *animation4 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation4.toValue = [NSValue valueWithCGPoint:CGPointMake(65, 120*scrRatioH+35)];
    animation4.duration = 0.5;
    animation4.beginTime = CACurrentMediaTime()+1.5;
    animation4.fillMode=kCAFillModeForwards;
    animation4.removedOnCompletion = NO;
    [imageView4.layer addAnimation:animation4 forKey:nil];
    
    
    UILabel *label2= [[UILabel alloc] initWithFrame:CGRectMake(kScreenWidth*0.5-40, 180*scrRatioH, 80, 20)];
    label2.text = @"创造秘钥";
    label2.textColor = [UIColor whiteColor];
    label2.textAlignment = NSTextAlignmentRight;
    label2.font = [UIFont systemFontOfSize:14];
    [self.view addSubview:label2];
    _label2 = label2;
    
    
    UIImageView *imageView5 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth, 200, 40, 40)];
    imageView5.image = [UIImage imageNamed:@"fingerprint"];
    imageView5.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView5];
    _imageView5 = imageView5;
    
    CABasicAnimation *animation5 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation5.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5-40, CGRectGetMaxY(label2.frame)+20)];
    animation5.duration = 0.5;
    animation5.beginTime = CACurrentMediaTime()+2;
    animation5.fillMode=kCAFillModeForwards;
    animation5.removedOnCompletion = NO;
    [imageView5.layer addAnimation:animation5 forKey:nil];
    
    UIImageView *imageView6 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth, 190, 60, 60)];
    imageView6.image = [UIImage imageNamed:@"fingerprintkey"];
    imageView6.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView6];
    _imageView6 = imageView6;
    
    CABasicAnimation *animation6 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation6.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5, CGRectGetMaxY(label2.frame)+10)];
    animation6.duration = 0.5;
    animation6.beginTime = CACurrentMediaTime()+2.5;
    animation6.fillMode=kCAFillModeForwards;
    animation6.removedOnCompletion = NO;
    [imageView6.layer addAnimation:animation6 forKey:nil];
    
    
    UILabel *label3= [[UILabel alloc] initWithFrame:CGRectMake(kScreenWidth*0.5-35-90, 335*scrRatioH, 80, 20)];
    label3.text = @"创造成功";
    label3.textColor = [UIColor whiteColor];
    label3.textAlignment = NSTextAlignmentRight;
    label3.font = [UIFont systemFontOfSize:14];
    [self.view addSubview:label3];
    _label3 = label3;
    
    
    UIImageView *imageView7 = [[UIImageView alloc] initWithFrame:CGRectMake(-140, 250, 70, 70)];
    imageView7.image = [UIImage imageNamed:@"succeed"];
    imageView7.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView7];
    _imageView7 = imageView7;
    
    CABasicAnimation *animation7 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation7.delegate = self;
    animation7.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5-35, CGRectGetMidY(label3.frame)-35)];
    animation7.duration = 0.5;
    animation7.beginTime = CACurrentMediaTime()+3;
    animation7.fillMode=kCAFillModeForwards;
    animation7.removedOnCompletion = NO;
    [imageView7.layer addAnimation:animation7 forKey:@"first"];
}

- (void)configAnimation2
{
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(kScreenWidth*0.5+45+10, 30*scrRatioH+50-10, 80, 20)];
    label1.text = @"功能注入";
    label1.textColor = [UIColor whiteColor];
    label1.textAlignment = NSTextAlignmentLeft;
    label1.font = [UIFont systemFontOfSize:14];
    [self.view addSubview:label1];
    _label1 = label1;
    
    UIImageView *imageView1 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth*0.5-25, -100, 50, 100)];
    imageView1.image = [UIImage imageNamed:@"iPhone"];
    imageView1.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView1];
    _imageView1 = imageView1;
    
    CABasicAnimation *animation1 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation1.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5-25, 30*scrRatioH)];
    animation1.duration = 0.5;
    animation1.beginTime = CACurrentMediaTime()+0;
    animation1.fillMode=kCAFillModeForwards;
    animation1.removedOnCompletion = NO;
    [imageView1.layer addAnimation:animation1 forKey:nil];
    
    
    UIImageView *imageView2 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth, 70, 45, 30)];
    imageView2.image = [UIImage imageNamed:@"vcard"];
    imageView2.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView2];
    _imageView2 = imageView2;
    
    
    CABasicAnimation *animation2 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation2.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5, 30*scrRatioH+50-15)];
    animation2.duration = 0.5;
    animation2.beginTime = CACurrentMediaTime()+0.5;
    animation2.fillMode=kCAFillModeForwards;
    animation2.removedOnCompletion = NO;
    [imageView2.layer addAnimation:animation2 forKey:nil];
    
    
    UIImageView *imageView3 = [[UIImageView alloc] initWithFrame:CGRectMake(60, -100, 30, 50)];
    imageView3.image = [UIImage imageNamed:@"letter"];
//    imageView3.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView3];
    _imageView3 = imageView3;
    
    CABasicAnimation *animation3 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation3.toValue = [NSValue valueWithCGPoint:CGPointMake(80, 150*scrRatioH)];
    animation3.duration = 0.5;
    animation3.beginTime = CACurrentMediaTime()+1;
    animation3.fillMode=kCAFillModeForwards;
    animation3.removedOnCompletion = NO;
    [imageView3.layer addAnimation:animation3 forKey:nil];
    
    UIImageView *imageView4 = [[UIImageView alloc] initWithFrame:CGRectMake(35, -140, 70, 70)];
    imageView4.image = [UIImage imageNamed:@"mail"];
//    imageView4.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView4];
    _imageView4 = imageView4;
    
    CABasicAnimation *animation4 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation4.toValue = [NSValue valueWithCGPoint:CGPointMake(80, 150*scrRatioH+20)];
    animation4.duration = 0.5;
    animation4.beginTime = CACurrentMediaTime()+1.5;
    animation4.fillMode=kCAFillModeForwards;
    animation4.removedOnCompletion = NO;
    [imageView4.layer addAnimation:animation4 forKey:nil];
    
    UIImageView *imageView5 = [[UIImageView alloc] initWithFrame:CGRectMake(64, -60, 25, 30)];
    imageView5.image = [UIImage imageNamed:@"locked"];
//    imageView5.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView5];
    _imageView5 = imageView5;
    
    CABasicAnimation *animation5 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation5.toValue = [NSValue valueWithCGPoint:CGPointMake(80, 150*scrRatioH+25)];
    animation5.duration = 0.5;
    animation5.beginTime = CACurrentMediaTime()+2;
    animation5.fillMode=kCAFillModeForwards;
    animation5.removedOnCompletion = NO;
    [imageView5.layer addAnimation:animation5 forKey:nil];
    
    
    UILabel *label2= [[UILabel alloc] initWithFrame:CGRectMake(kScreenWidth*0.5-40, 180*scrRatioH, 80, 20)];
    label2.text = @"上链";
    label2.textColor = [UIColor whiteColor];
    label2.textAlignment = NSTextAlignmentLeft;
    label2.font = [UIFont systemFontOfSize:14];
    [self.view addSubview:label2];
    _label2 = label2;
    
    
    UIImageView *imageView6 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth, CGRectGetMaxY(label2.frame)+10, 66, 60)];
    imageView6.image = [UIImage imageNamed:@"chain"];
    imageView6.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView6];
    _imageView6 = imageView6;
    
    CABasicAnimation *animation6 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation6.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5+10, CGRectGetMaxY(label2.frame)+10)];
    animation6.duration = 0.5;
    animation6.beginTime = CACurrentMediaTime()+2.5;
    animation6.fillMode=kCAFillModeForwards;
    animation6.removedOnCompletion = NO;
    [imageView6.layer addAnimation:animation6 forKey:nil];
    
    UIImageView *imageView7 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth, CGRectGetMaxY(label2.frame)+20, 25, 30)];
    imageView7.image = [UIImage imageNamed:@"locked"];
//    imageView7.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView7];
    _imageView7 = imageView7;
    
    CABasicAnimation *animation7 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation7.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5+10+33, CGRectGetMaxY(label2.frame)+10+30)];
    animation7.duration = 0.5;
    animation7.beginTime = CACurrentMediaTime()+3;
    animation7.fillMode=kCAFillModeForwards;
    animation7.removedOnCompletion = NO;
    [imageView7.layer addAnimation:animation7 forKey:@"second"];
    
    
    UILabel *label3= [[UILabel alloc] initWithFrame:CGRectMake(kScreenWidth*0.5+30+10, 300*scrRatioH+40, 80, 20)];
    label3.text = @"信息加密";
    label3.textColor = [UIColor whiteColor];
    label3.textAlignment = NSTextAlignmentRight;
    label3.font = [UIFont systemFontOfSize:14];
    [self.view addSubview:label3];
    _label3 = label3;
    
    UIImageView *imageView8 = [[UIImageView alloc] initWithFrame:CGRectMake(-140, 200, 70, 60)];
    imageView8.image = [UIImage imageNamed:@"box"];
    imageView8.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView8];
    _imageView8 = imageView8;
    
    CABasicAnimation *animation8 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation8.delegate = self;
    animation8.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5-35, 300*scrRatioH)];
    animation8.duration = 0.5;
    animation8.beginTime = CACurrentMediaTime()+3.5;
    animation8.fillMode=kCAFillModeForwards;
    animation8.removedOnCompletion = NO;
    [imageView8.layer addAnimation:animation8 forKey:@"second"];
    
    
   
}

- (void)configAnimation3
{
    
    UIBezierPath *circlePath7 = [UIBezierPath bezierPathWithArcCenter:CGPointMake(kScreenWidth*0.5, 220*scrRatioH) radius:145 startAngle:-M_PI_2 endAngle:M_PI_2 clockwise:NO];
    // 创建shapeLayer
    CAShapeLayer * shapeLayer7 = [CAShapeLayer layer];
    shapeLayer7.frame = self.view.bounds;// 设置图层大小
    shapeLayer7.path = circlePath7.CGPath;// 设置shapeLayer的cgPath
    shapeLayer7.opacity = 1.0f;  //设置透明度0~1之间
    shapeLayer7.lineCap = kCALineCapRound;//制定线的边缘是圆形
    shapeLayer7.lineWidth = 2.0f; // 设置线宽
    shapeLayer7.strokeColor = [UIColor whiteColor].CGColor;// 设置线条颜色
    [shapeLayer7 setFillColor:[UIColor clearColor].CGColor]; // 清楚填充颜色
    [self.view.layer addSublayer:shapeLayer7];
    shapeLayer7.strokeStart = 0.0;
    shapeLayer7.strokeEnd = 0.0;
    
    CABasicAnimation *animation7 = [CABasicAnimation animationWithKeyPath:@"strokeEnd"];
    animation7.toValue = [NSNumber numberWithFloat:1.0];
    animation7.duration = 3;
    animation7.beginTime = CACurrentMediaTime()+1;
    animation7.fillMode=kCAFillModeForwards;
    animation7.removedOnCompletion = NO;
    [shapeLayer7 addAnimation:animation7 forKey:nil];
    
    UIImageView *imageView7 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth*0.5-3, 220*scrRatioH+140, 7, 13)];
    imageView7.image = [UIImage imageNamed:@"left"];
    [self.view addSubview:imageView7];
    
    CAKeyframeAnimation *keyAnimation7 = [CAKeyframeAnimation animationWithKeyPath:@"position"];
    keyAnimation7.path = circlePath7.CGPath;
    keyAnimation7.duration = 3;
    //动画重复次数
    keyAnimation7.repeatCount = 1;
    //动画是否逆转
    keyAnimation7.autoreverses = false;
    //动画速度为匀速
    keyAnimation7.calculationMode = kCAAnimationCubicPaced;
    //动画角度是否调整
    keyAnimation7.rotationMode = kCAAnimationRotateAuto;
    
    keyAnimation7.beginTime = CACurrentMediaTime()+1;
    keyAnimation7.fillMode=kCAFillModeForwards;
    keyAnimation7.removedOnCompletion = NO;
    [imageView7.layer addAnimation:keyAnimation7 forKey:nil];
    
    
    UIBezierPath *circlePath8 = [UIBezierPath bezierPathWithArcCenter:CGPointMake(kScreenWidth*0.5, 220*scrRatioH) radius:145 startAngle:M_PI_2*0.9 endAngle:-M_PI_2*0.88 clockwise:NO];
    // 创建shapeLayer
    CAShapeLayer * shapeLayer8 = [CAShapeLayer layer];
    shapeLayer8.frame = self.view.bounds;// 设置图层大小
    shapeLayer8.path = circlePath8.CGPath;// 设置shapeLayer的cgPath
    shapeLayer8.opacity = 1.0f;  //设置透明度0~1之间
    shapeLayer8.lineCap = kCALineCapRound;//制定线的边缘是圆形
    shapeLayer8.lineWidth = 2.0f; // 设置线宽
    shapeLayer8.strokeColor = [UIColor whiteColor].CGColor;// 设置线条颜色
    [shapeLayer8 setFillColor:[UIColor clearColor].CGColor]; // 清楚填充颜色
    [self.view.layer addSublayer:shapeLayer8];
    shapeLayer8.strokeStart = 0.0;
    shapeLayer8.strokeEnd = 0.0;
    
    CABasicAnimation *animation8 = [CABasicAnimation animationWithKeyPath:@"strokeEnd"];
    animation8.toValue = [NSNumber numberWithFloat:1.0];
    animation8.duration = 3;
    animation8.beginTime = CACurrentMediaTime()+1;
    animation8.fillMode=kCAFillModeForwards;
    animation8.removedOnCompletion = NO;
    [shapeLayer8 addAnimation:animation8 forKey:nil];
    
    UIImageView *imageView8 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth*0.5-3, 220*scrRatioH+140, 7, 13)];
    imageView8.image = [UIImage imageNamed:@"left"];
    [self.view addSubview:imageView8];
    _imageView8 = imageView8;
    
    
    CAKeyframeAnimation *keyAnimation8 = [CAKeyframeAnimation animationWithKeyPath:@"position"];
    keyAnimation8.path = circlePath8.CGPath;
    keyAnimation8.duration = 3;
    //动画重复次数
    keyAnimation8.repeatCount = 1;
    //动画是否逆转
    keyAnimation8.autoreverses = false;
    //动画速度为匀速
    keyAnimation8.calculationMode = kCAAnimationCubicPaced;
    //动画角度是否调整
    keyAnimation8.rotationMode = kCAAnimationRotateAuto;
    
    keyAnimation8.beginTime = CACurrentMediaTime()+1;
    keyAnimation8.fillMode=kCAFillModeForwards;
    keyAnimation8.removedOnCompletion = NO;
    [imageView8.layer addAnimation:keyAnimation8 forKey:nil];
    
    
    UIImageView *imageView1 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth*0.5-25, -100, 50, 100)];
    imageView1.image = [UIImage imageNamed:@"iphonelogo"];
    imageView1.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView1];
    _imageView1 = imageView1;
    
    CABasicAnimation *animation1 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation1.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5-25, 220*scrRatioH-70-100)];
    animation1.duration = 0.5;
    animation1.beginTime = CACurrentMediaTime()+0;
    animation1.fillMode=kCAFillModeForwards;
    animation1.removedOnCompletion = NO;
    [imageView1.layer addAnimation:animation1 forKey:nil];
    
    
    UIImageView *imageView2 = [[UIImageView alloc] initWithFrame:CGRectMake(-120, 20, 75, 60)];
    imageView2.image = [UIImage imageNamed:@"web"];
    imageView2.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView2];
    _imageView2 = imageView2;
    
    
    CABasicAnimation *animation2 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation2.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5-20-75, 220*scrRatioH-60)];
    animation2.duration = 0.5;
    animation2.beginTime = CACurrentMediaTime()+0;
    animation2.fillMode=kCAFillModeForwards;
    animation2.removedOnCompletion = NO;
    [imageView2.layer addAnimation:animation2 forKey:nil];
    
    
    UIImageView *imageView3 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth, 20, 60, 55)];
    imageView3.image = [UIImage imageNamed:@"shop"];
    imageView3.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView3];
    _imageView3 = imageView3;
    
    CABasicAnimation *animation3 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation3.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5+40, 220*scrRatioH-50)];
    animation3.duration = 0.5;
    animation3.beginTime = CACurrentMediaTime()+0;
    animation3.fillMode=kCAFillModeForwards;
    animation3.removedOnCompletion = NO;
    [imageView3.layer addAnimation:animation3 forKey:nil];
    
    UIImageView *imageView4 = [[UIImageView alloc] initWithFrame:CGRectMake(-120, 240*scrRatioH, 60, 60)];
    imageView4.image = [UIImage imageNamed:@"subway"];
        imageView4.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView4];
    _imageView4 = imageView4;
    
    CABasicAnimation *animation4 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation4.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5-75-60, 220*scrRatioH)];
    animation4.duration = 0.5;
    animation4.beginTime = CACurrentMediaTime()+0;
    animation4.fillMode=kCAFillModeForwards;
    animation4.removedOnCompletion = NO;
    [imageView4.layer addAnimation:animation4 forKey:nil];
    
    UIImageView *imageView5 = [[UIImageView alloc] initWithFrame:CGRectMake(-120, 285*scrRatioH, 80, 60)];
    imageView5.image = [UIImage imageNamed:@"ccard"];
        imageView5.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView5];
    _imageView5 = imageView5;
    
    CABasicAnimation *animation5 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation5.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5-30, 220*scrRatioH+20)];
    animation5.duration = 0.5;
    animation5.beginTime = CACurrentMediaTime()+0;
    animation5.fillMode=kCAFillModeForwards;
    animation5.removedOnCompletion = NO;
    [imageView5.layer addAnimation:animation5 forKey:nil];
    
    
    UIImageView *imageView6 = [[UIImageView alloc] initWithFrame:CGRectMake(kScreenWidth, 300*scrRatioH, 60, 60)];
    imageView6.image = [UIImage imageNamed:@"air"];
    imageView6.layer.anchorPoint = CGPointZero;
    [self.view addSubview:imageView6];
    _imageView6 = imageView6;
    
    CABasicAnimation *animation6 = [CABasicAnimation animationWithKeyPath:@"position"];
    animation6.toValue = [NSValue valueWithCGPoint:CGPointMake(kScreenWidth*0.5+50, 220*scrRatioH+75)];
    animation6.duration = 0.5;
    animation6.beginTime = CACurrentMediaTime()+0;
    animation6.fillMode=kCAFillModeForwards;
    animation6.removedOnCompletion = NO;
    [imageView6.layer addAnimation:animation6 forKey:nil];
    
    
    
    
}


-(void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag
{
    if ([_imageView7.layer animationForKey:@"first"] == anim) {
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self removeAnimationView];
        });
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self configAnimation2];
        });
        
    }
    
    if ([_imageView8.layer animationForKey:@"second"] == anim) {
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self removeAnimationView];
        });
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self configAnimation3];
        });
        
    }
    
}

- (void)removeAnimationView{
    [_label1 removeFromSuperview];
    [_label2 removeFromSuperview];
    [_label3 removeFromSuperview];
    
    [_imageView1.layer removeAllAnimations];
    [_imageView1 removeFromSuperview];
    
    [_imageView2.layer removeAllAnimations];
    [_imageView2 removeFromSuperview];
    
    [_imageView3.layer removeAllAnimations];
    [_imageView3 removeFromSuperview];
    
    [_imageView4.layer removeAllAnimations];
    [_imageView4 removeFromSuperview];
    
    [_imageView5.layer removeAllAnimations];
    [_imageView5 removeFromSuperview];
    
    [_imageView6.layer removeAllAnimations];
    [_imageView6 removeFromSuperview];
    
    [_imageView7.layer removeAllAnimations];
    [_imageView7 removeFromSuperview];
    
    [_imageView8.layer removeAllAnimations];
    [_imageView8 removeFromSuperview];
}

- (void)createRSA{
    if ([DDRSAWrapper generateRSAKeyPairWithKeySize:2048 publicKey:&_publicKey privateKey:&_privateKey]) {
        NSString * publicKeyBase64 = [DDRSAWrapper base64EncodedStringPublicKey:_publicKey];
        publicKeyBase64 = [WexCommonFunc stringRemoveSpaceWithString:publicKeyBase64];
        NSData *publicKeyData =  [[NSData alloc] initWithBase64EncodedString:publicKeyBase64 options:0];
        _rsaPublicKey = [WexCommonFunc hexStringWithData:publicKeyData];
        
        NSString * privateKeyBase64 = [DDRSAWrapper base64EncodedStringPrivateKey:_privateKey];
        privateKeyBase64 = [WexCommonFunc stringRemoveSpaceWithString:privateKeyBase64];
        NSData *privateKeyData =  [[NSData alloc] initWithBase64EncodedString:privateKeyBase64 options:0];
        _rsaPrivateKey = [WexCommonFunc hexStringWithData:privateKeyData];
        
    }
}


- (void)initPassHelper{
    
    [self createRSA];
    
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(@"容器加载失败:%@",error);
            return;
        }
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response) {
            [self cretePass];
        }];
    }];
   
}

- (void)cretePass{
    /** 创建钱包privateKey */
    [[WXPassHelper instance] createPrivateKeyBlock:^(id response) {
        if (response) {
            _walletPrivateKey = response;
            
            if ([_walletPrivateKey hasPrefix:@"0x"]) {
                _walletPrivateKey=[_walletPrivateKey substringFromIndex:2];
            }
        }
        // 合约定义说明
        NSString* abiJson=@"{'constant':false,'inputs':[{'name':'_publickey','type':'bytes'}],'name':'putKey','outputs':[{'name':'_result','type':'bool'}],'payable':false,'stateMutability':'nonpayable','type':'function'}";
//        // 合约参数值
        NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\']",_rsaPublicKey]; // 这里代表存放是自己的RSA公钥
        // 合约地址(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境)
        NSString* abiAddress=_contractAddress;
        // 以太坊私钥地址
        NSString* privateKey=_walletPrivateKey;
        [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:privateKey responseBlock:^(id response) {
            _rawTransaction = response;
            //上传本地pubkey
            [self createUploadPubKeyRequest];

        }];
  
    }];
}

#pragma -mark 发送请求
- (void)createGetTicketRequest{
    _getTicketAdapter = [[WeXGetTicketAdapter alloc] init];
    _getTicketAdapter.delegate = self;
    WeXGetTicketParamModal* paramModal = [[WeXGetTicketParamModal alloc] init];
    [_getTicketAdapter run:paramModal];
}

#pragma -mark 发送获取合约地址请求
- (void)createGetContractAddressRequest{
    _getContractAddressAdapter = [[WeXGetContractAddressAdapter alloc] init];
    _getContractAddressAdapter.delegate = self;
    WeXGetContractAddressParamModal* paramModal = [[WeXGetContractAddressParamModal alloc] init];
    [_getContractAddressAdapter run:paramModal];
}

#pragma -mark 获取公钥请求
- (void)createGetPubKyeRequest{
    _getPubKeyAdapter = [[WeXGetPubKeyAdapter alloc] init];
    _getPubKeyAdapter.delegate = self;
    WeXGetPubKeyParamModal* paramModal = [[WeXGetPubKeyParamModal alloc] init];
    paramModal.address = _walletAddress;
    [_getPubKeyAdapter run:paramModal];
}

#pragma -mark 上传公钥请求
- (void)createUploadPubKeyRequest{
    _uoloadPubKeyAdapter = [[WeXUploadPubKeyAdapter alloc] init];
    _uoloadPubKeyAdapter.delegate = self;
    WeXUploadPubKeyParamModal* paramModal = [[WeXUploadPubKeyParamModal alloc] init];
    paramModal.ticket = _getTicketModel.ticket;
    paramModal.signMessage = _rawTransaction;
    paramModal.code = _registerView.graphTextField.text;
    [_uoloadPubKeyAdapter run:paramModal];
    
}

#pragma -mark 查询上链结果请求
- (void)createReceiptResultRequest{
    _getReceiptAdapter = [[WeXGetReceiptResultAdapter alloc] init];
    _getReceiptAdapter.delegate = self;
    WeXGetReceiptResultParamModal* paramModal = [[WeXGetReceiptResultParamModal alloc] init];
    paramModal.txHash = _txHash;
    [_getReceiptAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getTicketAdapter) {
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            _getTicketModel = (WeXGetTicketResponseModal *)response;
            [_registerView.graphBtn setImage:[WexCommonFunc imageWihtBase64String:_getTicketModel.image] forState:UIControlStateNormal];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }
       
    }
    else if (adapter == _getContractAddressAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetContractAddressResponseModal *model = (WeXGetContractAddressResponseModal *)response;
            _contractAddress = model.result;
            //合约地址请求成功 然后开始初始化passhelper
            [self initPassHelper];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }
    }
    else if (adapter == _uoloadPubKeyAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXUploadPubKeyResponseModal *model = (WeXUploadPubKeyResponseModal *)response;
            _txHash = model.result;
            [self createReceiptResultRequest];
        }
        else if([headModel.businessCode isEqualToString:@"CHALLENGE_FAILURE"])
        {
            [WeXPorgressHUD hideLoading];

            [WeXPorgressHUD showText:@"验证码校验失败!" onView:self.view];
        }
        else if([headModel.businessCode isEqualToString:@"TICKET_INVALID"])
        {
            [WeXPorgressHUD hideLoading];
            
            [WeXPorgressHUD showText:@"验证码超时!" onView:self.view];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            
            [WeXPorgressHUD showText:@"系统错误，请稍后再试" onView:self.view];
        }
    }
    else if (adapter == _getReceiptAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXGetReceiptResultResponseModal *model = (WeXGetReceiptResultResponseModal *)response;
            //上链成功
            if ([model.result isEqualToString:@"1"]) {
                /** 生成keystore json */
                [[WXPassHelper instance] keystoreCreateWithPrivateKey:_walletPrivateKey password:_registerView.passwordTextField.text responseBlock:^(id response) {
                    NSLog(@"keystore json=%@",response); // 返回keystore json
                    _keyStroe = response;
                    _walletAddress = [_keyStroe objectForKey:@"address"];
                    [self createGetPubKyeRequest];
                    
                }];
            }
            else
            {
                if (_requestCount > 4) {
                    [WeXPorgressHUD hideLoading];
                    [WeXPorgressHUD showText:@"创建口袋失败" onView:self.view];
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createReceiptResultRequest];
                    _requestCount++;
                });
            }
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }
       
        
    }
    else if (adapter == _getPubKeyAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            WeXGetPubKeyResponseModal *model = (WeXGetPubKeyResponseModal *)response;
            NSData *publicKeyData =  [[NSData alloc] initWithBase64EncodedString:model.result options:0];
            NSString *resultPublickKey  = [WexCommonFunc hexStringWithData:publicKeyData];
            //相等表示口袋创建成功
            if ([resultPublickKey isEqualToString:_rsaPublicKey]) {
                [self savePassport];
                [UIView animateWithDuration:0.2 animations:^{
                    _registerView.frame = CGRectMake(0, kScreenHeight, kScreenWidth, kRegisterViewHeight);
                    //删除蒙版
                    [_coverView removeFromSuperview];
                    [_registerView dismiss];
                } completion:^(BOOL finished) {
                    //保存统一操作记录
                     [WexCommonFunc saveManagerRecordWithTypeString:@"启用统一登录"];
                    
                    WeXRegisterSuccessViewController *ctrl = [[WeXRegisterSuccessViewController alloc] init];
                    ctrl.type = WeXRegisterSuccessTypeCreate;
                    ctrl.isFromAuthorize = self.isFromAuthorize;
                    ctrl.url = self.url;
                    [self.navigationController pushViewController:ctrl animated:YES];
                }];
            }
            else
            {
                [WeXPorgressHUD showText:@"创建口袋失败" onView:self.view];
            }
            
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }

    }
}



#pragma mark -保存口袋信息
- (void)savePassport
{
    WeXPasswordCacheModal *model = [WeXPasswordCacheModal sharedInstance];
    model.rsaPublicKey = _rsaPublicKey;
    model.rasPrivateKey = _rsaPrivateKey;
    model.passportPassword = _registerView.passwordTextField.text;
    model.walletPrivateKey = _walletPrivateKey;
    model.keyStore = _keyStroe;
    model.isAllow = YES;
    [WexCommonFunc savePassport:model];
}

- (void)setupSubViews{
    UIImageView *backImageView1 = [[UIImageView alloc] init];
    backImageView1.image = [UIImage imageNamed:@"slogan"];
    [self.view addSubview:backImageView1];
    [backImageView1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.view);
        make.centerY.equalTo(self.view);
    }];
    
//    UIImageView *backImageView2 = [[UIImageView alloc] init];
//    backImageView2.image = [UIImage imageNamed:@"whiteLogo"];
//    [self.view addSubview:backImageView2];
//    [backImageView2 mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.centerX.equalTo(self.view);
//        make.bottom.equalTo(backImageView1.mas_top).offset(-30);
//    }];
    
    //创建按钮
    UIButton *createBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [createBtn setTitle:@"创建" forState:UIControlStateNormal];
    [createBtn setTitleColor:ColorWithButtonRed forState:UIControlStateNormal];
    createBtn.backgroundColor = [UIColor clearColor];
    createBtn.layer.borderWidth = 2;
    createBtn.layer.borderColor = [ColorWithButtonRed CGColor];
    createBtn.layer.cornerRadius = 40;
    createBtn.layer.masksToBounds = YES;
    [createBtn addTarget:self action:@selector(createBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:createBtn];
    [createBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view.mas_bottom).offset(-80);
        make.centerX.equalTo(self.view);
        make.height.equalTo(@(80));
        make.width.equalTo(@80);
    }];
    
    //导入按钮
    UIButton *importBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [importBtn setTitle:@"导入" forState:UIControlStateNormal];
    [importBtn setTitleColor:ColorWithButtonRed forState:UIControlStateNormal];
    importBtn.backgroundColor = [UIColor clearColor];
    [importBtn addTarget:self action:@selector(importBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:importBtn];
    [importBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view.mas_bottom).offset(-30);
        make.trailing.equalTo(self.view).offset(-10);
        make.height.equalTo(@(30));
        make.width.equalTo(@50);
    }];
}

- (void)createBtnClick:(UIButton *)btn{
    
    
    //创建蒙版
    _coverView = [[UIView alloc] initWithFrame:self.view.bounds];
    _coverView.backgroundColor = [UIColor blackColor];
    _coverView.alpha = COVER_VIEW_ALPHA;
    [self.view addSubview:_coverView];
    //创建注册试图
    WeXRegisterView *registerView = [[WeXRegisterView alloc] initWithFrame:CGRectMake(0, kScreenHeight, kScreenWidth, kRegisterViewHeight)];
    registerView.backgroundColor = [UIColor whiteColor];
    [self.view.window addSubview:registerView];
    _registerView  = registerView;
    //动画
    [UIView animateWithDuration:0.5 animations:^{
        registerView.frame = CGRectMake(0, kScreenHeight-kRegisterViewHeight, kScreenWidth, kRegisterViewHeight);
    }];
    //发送获取验证码请求
    [self createGetTicketRequest];
    
    __weak typeof(registerView) weakRegisterView = registerView;
    //点击取消按钮
    registerView.cancelBtnBlock = ^{
        [UIView animateWithDuration:0.5 animations:^{
            weakRegisterView.frame = CGRectMake(0, kScreenHeight, kScreenWidth, kRegisterViewHeight);
        }];
        //删除蒙版
        [_coverView removeFromSuperview];
        [weakRegisterView dismiss];
    };
    //点击创建按钮
    registerView.createBtnBlock = ^{
        [WeXPorgressHUD showLoadingAddedTo:self.view.window];
        [self createGetContractAddressRequest];

    };
    registerView.graphBtnBlock = ^{
        [self createGetTicketRequest];
    };
  
    
}

- (void)importBtnClick:(UIButton *)btn{
    WeXImportViewController *ctrl = [[WeXImportViewController alloc] init];
    ctrl.isFromAuthorize = self.isFromAuthorize;
    ctrl.url = self.url;
    [self.navigationController pushViewController:ctrl animated:YES];
  
    
}

- (void)keyboardChangedWithTransition:(YYKeyboardTransition)transition {
    [UIView animateWithDuration:transition.animationDuration delay:0 options:transition.animationOption animations:^{
        ///用此方法获取键盘的rect
        CGRect kbFrame = [[YYKeyboardManager defaultManager] convertRect:transition.toFrame toView:self.view];
        ///从新计算view的位置并赋值
        CGRect frame = _registerView.frame;
        frame.origin.y = kbFrame.origin.y - frame.size.height;
        _registerView.frame = frame;
    } completion:^(BOOL finished) {
        
    }];
}

-(void)dealloc
{
    [[YYKeyboardManager defaultManager] removeObserver:self];
}


@end
