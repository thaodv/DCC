//
//  WeXMyBorrowContractViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/15.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyBorrowContractViewController.h"
#import "WeXLoanDownloadContractAdapter.h"

@interface WeXMyBorrowContractViewController ()<UIWebViewDelegate>

@end

@implementation WeXMyBorrowContractViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"合同";
    [self setNavigationNomalBackButtonType];
    UIWebView *webView = [[UIWebView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:webView];
    webView.delegate = self;
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *urlStr = [NSString stringWithFormat:@"%@secure/loan/download/agreement?x-auth-token=%@&chainOrderId=%@",WEX_BASE_DCC_ACTIVITY_URL,[userDefaults objectForKey:WEX_NET_TOKEN_KEY],_orderId];
    WEXNSLOG(@"urlStr=%@",urlStr);
    [webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:urlStr]]];
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
