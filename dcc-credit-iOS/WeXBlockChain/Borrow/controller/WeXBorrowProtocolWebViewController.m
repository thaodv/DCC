//
//  WeXBorrowProtocolWebViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/2.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowProtocolWebViewController.h"


@interface WeXBorrowProtocolWebViewController ()<UIWebViewDelegate>

@end

@implementation WeXBorrowProtocolWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"借款协议";
    [self setNavigationNomalBackButtonType];
    
    self.webView.delegate = self;
    [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:self.urlString]]];
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
