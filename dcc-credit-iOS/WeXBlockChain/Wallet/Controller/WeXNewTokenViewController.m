//
//  WeXNewTokenViewController.m
//  WeXBlockChain
//
//  Created by zhuojian on 2018/1/30.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXNewTokenViewController.h"


@interface WeXNewTokenViewController ()
@property(nonatomic,weak)IBOutlet UITextView* txtView;
@end

@implementation WeXNewTokenViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.title = @"提交新Token";

    // Do any additional setup after loading the view from its nib.
    
    [self.view bringSubviewToFront:_txtView];
    
    CGRect rectOfStatusbar = [[UIApplication sharedApplication] statusBarFrame];
    CGRect frame= _txtView.frame;
    frame.origin.y+=rectOfStatusbar.size.height;
    _txtView.frame=frame;
    
    [self setNavigationNomalBackButtonType];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setNavigationNomalBackButtonType
{
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithImage:[[UIImage imageNamed:@"retreat2"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] style:UIBarButtonItemStylePlain target:self action:@selector(backItemClick)];
    
    self.navigationItem.leftBarButtonItem = item;
    
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
