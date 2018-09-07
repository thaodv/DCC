//
//  WeXAboutUsWebViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/6/12.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAboutUsWebViewController.h"

@interface WeXAboutUsWebViewController ()<UIWebViewDelegate>

@end

@implementation WeXAboutUsWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"关于我们");
    [self setNavigationNomalBackButtonType];
    self.webView.delegate = self;
    NSString *webURL;
    if ([[WeXLocalizedManager shareManager] isChinese]) {
        webURL = [WEX_WEB_BASE_URL stringByAppendingString:@"invite/about.html"];
    } else {
        webURL = [WEX_WEB_BASE_URL stringByAppendingString:@"invite/about_en.html"];
    }
    [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:webURL]]];
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

