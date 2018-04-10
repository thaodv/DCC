//
//  WeXAllShow1ViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/17.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAllShow1ViewController.h"
#import "WeXAllShow1WebViewController.h"

#import "XWInteractiveTransition.h"
#import "WeXAllShow1Trasiton.h"
#import "AFNetworking.h"


#define contentLabelLineSpace 7

@interface WeXAllShow1ViewController ()

@property (nonatomic, strong) XWInteractiveTransition *interactiveTransition;

@end

@implementation WeXAllShow1ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
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
    cardImageView.image = [UIImage imageNamed:@"digital_all1"];
    cardImageView.layer.magnificationFilter = kCAFilterNearest;
    [cardBackView addSubview:cardImageView];
    [cardImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(cardBackView);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"手环介绍";
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
    
    
    NSString *content1 = @"全向手环是一款智能运动手环，内置专业算法及科学跑步指导，超长续航能力，7*24小时心率监测、运动步数、配速等数据，抬腕即看；基于共识算法，搭建运动激励计划体系，让每位运动爱好者在跑步的同时获得运动激励。";
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
    
    WeXCustomButton *downBtn = [WeXCustomButton button];
    [downBtn setTitle:@"购买全向手环" forState:UIControlStateNormal];
    [downBtn addTarget:self action:@selector(downBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:downBtn];
    [downBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@130);
        make.height.equalTo(@44);
    }];
    
}


- (void)downBtnClick{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    AFHTTPSessionManager * manager = [AFHTTPSessionManager manager];
   
    [manager  GET:@"https://api.allxsports.com.cn/api/open/url" parameters:nil progress:^(NSProgress * _Nonnull downloadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        [WeXPorgressHUD hideLoading];
        NSLog(@"%@",responseObject);
        NSDictionary *dataDick = [responseObject objectForKey:@"data"];
        NSString *urlStr = [dataDick objectForKey:@"bracelet_url"];
        if (urlStr) {
            WeXAllShow1WebViewController *ctrl = [[WeXAllShow1WebViewController alloc] init];
            ctrl.urlStr = urlStr;
            [self.navigationController pushViewController:ctrl animated:YES];
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
    return [WeXAllShow1Trasiton transitionWithType:operation == UINavigationControllerOperationPush ? WeXPassportCardTrasitonPush : WeXPassportCardTrasitonPop];
}

- (id<UIViewControllerInteractiveTransitioning>)navigationController:(UINavigationController *)navigationController interactionControllerForAnimationController:(id<UIViewControllerAnimatedTransitioning>)animationController{
    //手势开始的时候才需要传入手势过渡代理，如果直接点击pop，应该传入空，否者无法通过点击正常pop
    return _interactiveTransition.interation ? _interactiveTransition : nil;
}




@end
