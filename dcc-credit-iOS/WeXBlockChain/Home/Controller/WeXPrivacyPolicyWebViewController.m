//
//  WeXPrivacyPolicyWebViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/6/6.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPrivacyPolicyWebViewController.h"

@interface WeXPrivacyPolicyWebViewController ()<UIWebViewDelegate>

@end

@implementation WeXPrivacyPolicyWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"隐私协议";
    [self setNavigationNomalBackButtonType];
    self.webView.delegate = self;
    [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"http://open.dcc.finance/dapp/privacy_policy.html"]]];
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

