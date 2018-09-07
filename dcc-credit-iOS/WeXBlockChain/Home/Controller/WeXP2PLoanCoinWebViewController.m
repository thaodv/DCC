//
//  WeXP2PLoanCoinWebViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/6/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXP2PLoanCoinWebViewController.h"

@interface WeXP2PLoanCoinWebViewController () <UIWebViewDelegate>

@end

@implementation WeXP2PLoanCoinWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self initWebViewRequest];
}

- (void)initWebViewRequest {
//    self.webURL = @"http://www.baidu.com";
    [self.webView setDelegate:self];
    [self.webView loadRequest:[[NSURLRequest alloc]initWithURL:[NSURL URLWithString:self.webURL]]];
}
- (void)backItemClick {
    if ([self.webView canGoBack]) {
        [self.webView goBack];
    } else {
        [self.navigationController popViewControllerAnimated:YES];
    }
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    NSLog(@"webViewDidStartLoad:%s ",__func__);
}
- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [WeXPorgressHUD hideLoading];
    NSLog(@"webViewDidFinishLoad:%s",__func__);
}
- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    [WeXPorgressHUD hideLoading];
    NSLog(@"error is : %@---%s",error,__func__);
}
- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    NSURL* url = [request URL];
    NSString* urlString = [url.absoluteString stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSLog(@"urlString: %@", urlString);
    return YES;
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


@end
