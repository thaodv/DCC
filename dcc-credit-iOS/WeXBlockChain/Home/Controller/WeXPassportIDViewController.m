//
//  WeXPassportIDViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDViewController.h"
#import "XWInteractiveTransition.h"
#import "WeXPassportCardIDTrasiton.h"
#import "WeXPassportIDScanController.h"

#import "WeXPassportIDStatusController.h"

#define contentLabelLineSpace 7

@interface WeXPassportIDViewController ()

@property (nonatomic, strong) XWInteractiveTransition *interactiveTransition;

@end

@implementation WeXPassportIDViewController

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
        make.top.equalTo(self.view).offset(0);
        make.leading.equalTo(self.view).offset(0);
        make.trailing.equalTo(self.view).offset(0);
        make.height.equalTo(@350);
    }];
    _cardView = cardBackView;
    
    UIImageView *cardImageView = [[UIImageView alloc] init];
    cardImageView.image = [UIImage imageNamed:@"digital_id"];
    cardImageView.layer.magnificationFilter = kCAFilterNearest;
    [cardBackView addSubview:cardImageView];
    [cardImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(cardBackView);
    }];
    
    UIImageView *headImageView = [[UIImageView alloc] init];
    headImageView.image = [UIImage imageNamed:@"digital_idName"];
    headImageView.layer.magnificationFilter = kCAFilterNearest;
    [cardBackView addSubview:headImageView];
    [headImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(cardBackView).offset(10);
        make.top.equalTo(cardBackView).offset(30);
        make.width.equalTo(@137);
        make.height.equalTo(@30);
    }];
    
    UILabel *contentLabel = [[UILabel alloc] init];
    contentLabel.font = [UIFont systemFontOfSize:17];
    contentLabel.textColor = [UIColor whiteColor];
    contentLabel.textAlignment = NSTextAlignmentLeft;
    contentLabel.numberOfLines = 0;
    [self.view addSubview:contentLabel];
    [contentLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(cardBackView.mas_bottom).offset(20);
        make.leading.equalTo(self.view).offset(10);
        make.trailing.equalTo(self.view).offset(-10);
    }];
    
    
    NSString *content1 = @"当用户需要证明自己的身份时，他们通常必须向正在检查该信息的网站平台提供实际身份证信息。一些用户处于身份隐私的考虑可能不愿意分享自己的身份证信息。例：平台要接受用户共享自己拥有身份证这一标志，而不需要了解用户的具体身份证号码即可办理相关业务。钱包只允许用户共享身份证标志信息，并将其他个人隐私信息隐藏起来并保存在认证方即可。";
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
    
    WeXCustomButton *idBtn = [WeXCustomButton button];
    [idBtn setTitle:@"身份认证" forState:UIControlStateNormal];
    idBtn.layer.cornerRadius = 5;
    idBtn.layer.masksToBounds = YES;
    [idBtn setTitleColor:ColorWithRGB(248, 31, 117) forState:UIControlStateNormal];
    [idBtn addTarget:self action:@selector(idBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:idBtn];
    [idBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@130);
        make.height.equalTo(@50);
    }];
    
}


- (void)idBtnClick{
    
    if ([WexCommonFunc getPassport].idAuthenTxHash) {
        WeXPassportIDStatusController *ctrl = [[WeXPassportIDStatusController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else
    {
        WeXPassportIDScanController *ctrl = [[WeXPassportIDScanController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}


- (void)rightItemClick{
    self.navigationController.delegate = self;
    [self.navigationController popViewControllerAnimated:YES];
}

- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController animationControllerForOperation:(UINavigationControllerOperation)operation fromViewController:(UIViewController *)fromVC toViewController:(UIViewController *)toVC{
    //分pop和push两种情况分别返回动画过渡代理相应不同的动画操作
    return [WeXPassportCardIDTrasiton transitionWithType:operation == UINavigationControllerOperationPush ? WeXPassportCardTrasitonPush : WeXPassportCardTrasitonPop];
}

- (id<UIViewControllerInteractiveTransitioning>)navigationController:(UINavigationController *)navigationController interactionControllerForAnimationController:(id<UIViewControllerAnimatedTransitioning>)animationController{
    //手势开始的时候才需要传入手势过渡代理，如果直接点击pop，应该传入空，否者无法通过点击正常pop
    return _interactiveTransition.interation ? _interactiveTransition : nil;
}




@end
