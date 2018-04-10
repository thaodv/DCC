//
//  WeXAllShow1WebViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/19.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAllShow1WebViewController.h"

@interface WeXAllShow1WebViewController ()<UIWebViewDelegate>

@end

@implementation WeXAllShow1WebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    UIWebView *webView = [[UIWebView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:webView];

    webView.delegate = self;
    [webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:self.urlStr]]];
}

-(void)webViewDidStartLoad:(UIWebView *)webView
{
    NSLog(@"start");
}

-(void)webViewDidFinishLoad:(UIWebView *)webView
{
    NSLog(@"Finish");
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
