//
//  WeXMyBorrowContractWebViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/15.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyBorrowContractWebViewController.h"

@interface WeXMyBorrowContractWebViewController ()<UIWebViewDelegate>

@end

@implementation WeXMyBorrowContractWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"合同";
    [self setNavigationNomalBackButtonType];
    self.webView.delegate = self;
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *urlStr = [NSString stringWithFormat:@"%@secure/loan/download/agreement?x-auth-token=%@&chainOrderId=%@",WEX_BASE_DCC_ACTIVITY_URL,[userDefaults objectForKey:WEX_NET_TOKEN_KEY],_orderId];
    WEXNSLOG(@"urlStr=%@",urlStr);
    [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:urlStr]]];
}

-(void)webViewDidStartLoad:(UIWebView *)webView
{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    NSLog(@"start");
}

-(void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    [WeXPorgressHUD hideLoading];
    [WeXPorgressHUD showText:@"服务器繁忙!" onView:self.view];
}

-(void)webViewDidFinishLoad:(UIWebView *)webView
{
    [WeXPorgressHUD hideLoading];
    NSLog(@"Finish");
}

@end
