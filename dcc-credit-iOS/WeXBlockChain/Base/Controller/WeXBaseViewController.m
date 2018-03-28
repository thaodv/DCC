//
//  WeXBaseViewController.m
//  WeXIco
//
//  Created by wcc on 2017/9/7.
//  Copyright © 2017年 Wex. All rights reserved.
//

#import "WeXBaseViewController.h"

@interface WeXBaseViewController ()

@end

@implementation WeXBaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.view.backgroundColor = [UIColor whiteColor];
//    if (self.backgroundType == WeXBaseViewBackgroundTypeNormal) {
//       [self setupNormalBackgroundStyle];
//    }
    
}

- (void)setupNormalBackgroundStyle{
    UIImageView *backImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight)];
    backImageView.image = [UIImage imageNamed:@"background"];
    [self.view addSubview:backImageView];
}

//默认设置返回样式
- (void)setNavigationNomalBackButtonType
{
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"retreat2"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(backItemClick)];
    
    self.navigationItem.leftBarButtonItem = item;
    
}
//默认返回上一个页面
- (void)backItemClick
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)configNavigitonBarWithImage:(NSString *)image
{
    UIImage *oImage = [UIImage imageNamed:image];
    UIImage *newImage = [oImage imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithImage:newImage style:UIBarButtonItemStyleDone target:self action:@selector(rightButtonClick)];
    
    self.navigationItem.rightBarButtonItem = item;
}

- (void)rightButtonClick
{
    
}

-(void)showRightButtonTitleIos7Above:(NSString *)title tintColor:(UIColor*)tintColor{
    
    UIBarButtonItem* item;
    
    if(self.delegate)
        item=[[UIBarButtonItem alloc] initWithTitle:title style:UIBarButtonItemStyleDone target:self action:@selector(goRightClicked:)];
    
    item.tintColor=[UIColor whiteColor];
    
    UIBarButtonItem *flexSpacer = [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:self
                                                                               action:nil];
    
    [self.navigationItem setRightBarButtonItems:[NSArray arrayWithObjects:flexSpacer,item, nil]];
    
    //    [self.navigationItem setRightBarButtonItem:item];
}

-(IBAction)goRightClicked:(id)sender{
    
    if([((id)self.delegate) respondsToSelector:@selector(MyViewController:onRightButton:)])
    {
        [self.delegate MyViewController:self onRightButton:sender];
        return;
    }
}

-(UIStatusBarStyle)preferredStatusBarStyle
{
    return UIStatusBarStyleDefault;
}

@end
