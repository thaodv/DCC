//
//  WeXAllShow2ViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/17.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAllShow2ViewController.h"


#import "XWInteractiveTransition.h"
#import "WeXAllShow2Trasiton.h"
#import "AFNetworking.h"


#define contentLabelLineSpace 6

@interface WeXAllShow2ViewController ()

@property (nonatomic, strong) XWInteractiveTransition *interactiveTransition;

@end

@implementation WeXAllShow2ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.automaticallyAdjustsScrollViewInsets = NO;
    [self setupNavgationType];
    [self setupSubViews];
    
    //    //初始化手势过渡的代理
    //    self.interactiveTransition = [XWInteractiveTransition interactiveTransitionWithTransitionType:XWInteractiveTransitionTypePop GestureDirection:XWInteractiveTransitionGestureDirectionRight];
    //    //给当前控制器的视图添加手势
    //    [_interactiveTransition addPanGestureForViewController:self];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    self.navigationController.delegate = nil;
    
}

- (void)setupNavgationType{
    self.navigationItem.hidesBackButton = YES;
    UIBarButtonItem *rihgtItem = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"digital_cha1"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
    self.navigationItem.rightBarButtonItem = rihgtItem;
    
}

//初始化滚动视图
-(void)setupSubViews{
    
    UIView *cardBackView = [[UIView alloc] init];
    cardBackView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:cardBackView];
    [cardBackView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.equalTo(self.view).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
        make.height.equalTo(@180);
    }];
    _cardView = cardBackView;
    
    UIImageView *cardImageView = [[UIImageView alloc] init];
    cardImageView.image = [UIImage imageNamed:@"digital_all2"];
    cardImageView.layer.magnificationFilter = kCAFilterNearest;
    [cardBackView addSubview:cardImageView];
    [cardImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(cardBackView);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"全向体育";
    titleLabel.font = [UIFont systemFontOfSize:18];
    titleLabel.textColor = ColorWithLabelDescritionBlack;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(cardBackView.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(10);
        make.height.equalTo(@20);
    }];
    
    
    UILabel *contentLabel = [[UILabel alloc] init];
    contentLabel.font = [UIFont systemFontOfSize:15];
    contentLabel.textColor = ColorWithLabelDescritionBlack;
    contentLabel.textAlignment = NSTextAlignmentLeft;
    contentLabel.numberOfLines = 0;
    [self.view addSubview:contentLabel];
    [contentLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
    }];
    
    WeXCustomButton *downBtn = [WeXCustomButton button];
    [downBtn setTitle:@"下载全项体育" forState:UIControlStateNormal];
    [downBtn addTarget:self action:@selector(downBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:downBtn];
    [downBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-20);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@130);
        make.height.equalTo(@44);
    }];
    
    //输入文本框
//    UITextView *contentTextView = [[UITextView alloc] init];
//    contentTextView.backgroundColor = [UIColor clearColor];
//    contentTextView.textColor = ColorWithLabelDescritionBlack;
//    contentTextView.font = [UIFont systemFontOfSize:20];
//    contentTextView.editable = NO;
//    [self.view addSubview:contentTextView];
//    [contentTextView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(titleLabel.mas_bottom).offset(10);
//        make.leading.equalTo(self.view).offset(10);
//        make.trailing.equalTo(self.view).offset(-10);
//        make.bottom.equalTo(downBtn.mas_top).offset(-5);
//    }];
//    contentTextView.text =
    
    NSString *content1 = @"全向是专注于体育行业的科技公司，全向体育APP是奔跑中国马拉松赛事官方指定报名APP，为广大运动爱好者提供一键报名、状态查询等便捷赛事管理功能。独立的运动体系算法，实时定位，支持户外运动轨迹追踪、卡路里消耗查询。";
//    contentTextView.text = content1;
    //设置间距
    NSMutableAttributedString *attrStr1 = [[NSMutableAttributedString alloc] initWithString:content1];
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineSpacing = contentLabelLineSpace;
    paragraphStyle.alignment = NSTextAlignmentLeft;
    paragraphStyle.hyphenationFactor = 1.0;
    paragraphStyle.firstLineHeadIndent = 0.0;
    paragraphStyle.paragraphSpacingBefore = 0.0;
    paragraphStyle.headIndent = 0;
    paragraphStyle.tailIndent = 0;
    [attrStr1 addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, content1.length)];
    contentLabel.attributedText = attrStr1;
    
  
    
}


- (void)downBtnClick{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    AFHTTPSessionManager * manager = [AFHTTPSessionManager manager];
    
    [manager  GET:@"https://api.allxsports.com.cn/api/open/url" parameters:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        [WeXPorgressHUD hideLoading];
        NSLog(@"%@",responseObject);
        NSDictionary *dataDick = [responseObject objectForKey:@"data"];
        NSString *urlStr = [dataDick objectForKey:@"ios_download_url"];
        if (urlStr) {
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:urlStr]];
        }
        else
        {
            NSString *appStr = @"itms-apps://itunes.apple.com/cn/app/id1329340872?mt=8";
            // 跳转
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:appStr]];
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        [WeXPorgressHUD hideLoading];
        [WeXPorgressHUD showText:@"服务器繁忙，请稍后再试!" onView:self.view];
    }];
    
   
   
}


- (void)rightItemClick{
    self.navigationController.delegate = self;
    [self.navigationController popViewControllerAnimated:YES];
}

- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController animationControllerForOperation:(UINavigationControllerOperation)operation fromViewController:(UIViewController *)fromVC toViewController:(UIViewController *)toVC{
    //分pop和push两种情况分别返回动画过渡代理相应不同的动画操作
    return [WeXAllShow2Trasiton transitionWithType:operation == UINavigationControllerOperationPush ? WeXPassportCardTrasitonPush : WeXPassportCardTrasitonPop];
}

- (id<UIViewControllerInteractiveTransitioning>)navigationController:(UINavigationController *)navigationController interactionControllerForAnimationController:(id<UIViewControllerAnimatedTransitioning>)animationController{
    //手势开始的时候才需要传入手势过渡代理，如果直接点击pop，应该传入空，否者无法通过点击正常pop
    return _interactiveTransition.interation ? _interactiveTransition : nil;
}




@end
