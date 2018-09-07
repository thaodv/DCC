//
//  WeXBaseWebViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/11.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseWebViewController.h"

@interface WeXBaseWebViewController ()

@end

@implementation WeXBaseWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initWebView];
}

- (void)initWebView
{
    _webView = [[UIWebView alloc] init];
    _webView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:_webView];
    [_webView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.trailing.bottom.equalTo(self.view);
    }];
}



#pragma mark - UIWebViewDelegate

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    //#close
    NSURL* url = [request URL];
    
    NSString* urlString = [url.absoluteString stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    WEXNSLOG(@"urlString: %@", urlString);
    
    return YES;
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    WEXNSLOG(@"webView error: %@", error);
}

@end
