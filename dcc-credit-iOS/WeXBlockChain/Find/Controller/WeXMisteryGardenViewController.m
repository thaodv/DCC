//
//  WeXMisteryGardenViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/5.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMisteryGardenViewController.h"

@import JavaScriptCore;
@interface WeXMisteryGardenViewController () <UIWebViewDelegate>

@end

@implementation WeXMisteryGardenViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"神秘花园");
    [self configureWEBContent];
    [self setNavigationNomalBackButtonType];
}
- (void)configureWEBContent {
    if ([self.webURL length] < 1) {
        self.webURL = @"https://open.dcc.finance/dapp/mysticalGarden/index.html#/Mygarden";
    }
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:self.webURL]];
    [self.webView setDelegate:self];
    [self.webView loadRequest:request];
    [WeXPorgressHUD showLoadingAddedTo:self.view];
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [WeXPorgressHUD hideLoading];
    [self observerJSContext:webView];
    
}
- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    [WeXPorgressHUD hideLoading];
}

- (void)observerJSContext:(UIWebView *)webView {
    JSContext *context = [webView valueForKeyPath:@"documentView.webView.mainFrame.javaScriptContext"];
    context[@"shareEvent_iOS"] = ^ () {
        WEXNSLOG(@"分享事件");
    };
}

- (void)dealloc {
    [self.webView setDelegate:nil];
    [self.webView stopLoading];
    WEXNSLOG(@"dealloc---%@",self);
}

@end
