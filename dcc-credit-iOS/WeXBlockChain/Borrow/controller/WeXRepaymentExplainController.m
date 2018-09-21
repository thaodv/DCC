//
//  WeXRepaymentExplainController.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/1.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXRepaymentExplainController.h"

@interface WeXRepaymentExplainController ()<UIScrollViewDelegate>

@property (nonatomic,strong)UIScrollView *mainScrollView;

@end

@implementation WeXRepaymentExplainController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setSubviews];
    // Do any additional setup after loading the view.
}

- (void)setSubviews{
    
    self.navigationItem.title = @"还币流程";
    self.view.backgroundColor = [UIColor whiteColor];
    self.automaticallyAdjustsScrollViewInsets = NO;
    _mainScrollView = [[UIScrollView alloc]initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight)];
    _mainScrollView.backgroundColor = [UIColor whiteColor];
    // 设置内容大小
    self.mainScrollView.contentSize = CGSizeMake(kScreenWidth, kScreenWidth* (1886.0/375.0));
    // 隐藏水平滚动条
    self.mainScrollView.showsHorizontalScrollIndicator = NO;
    self.mainScrollView.showsVerticalScrollIndicator = NO;
    // 去掉弹簧效果
    self.mainScrollView.bounces = NO;
    [self.view addSubview:_mainScrollView];
    
    UIImageView *imageView = [[UIImageView alloc] init];
    imageView.image = [UIImage imageNamed:@"repaymentExplain"];
    imageView.frame = CGRectMake(0, 0,kScreenWidth, kScreenWidth* (1886.0/375.0));
    [self.mainScrollView addSubview:imageView];
  
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
