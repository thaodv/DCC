//
//  WeXOfficialWebViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/6/6.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXOfficialWebViewController.h"

@interface WeXOfficialWebViewController ()<UIWebViewDelegate>

@end

@implementation WeXOfficialWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"官网";
    [self setNavigationNomalBackButtonType];
    self.webView.delegate = self;
    [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"http://dcc.finance/"]]];
}

-(void)webViewDidStartLoad:(UIWebView *)webView
{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    NSLog(@"start");
}

-(void)webViewDidFinishLoad:(UIWebView *)webView
{
    [WeXPorgressHUD hideLoading];
    NSLog(@"Finish");
}


@end

