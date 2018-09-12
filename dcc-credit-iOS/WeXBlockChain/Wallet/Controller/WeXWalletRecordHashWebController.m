//
//  WeXWalletRecordHashWebController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletRecordHashWebController.h"

@interface WeXWalletRecordHashWebController ()

@end

@implementation WeXWalletRecordHashWebController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];

}

- (void)setupSubViews{
    UIWebView *webView = [[UIWebView alloc] init];
    NSString *url;
    
    if (self.type == WeXWalletRecordHashTypePublic) {
        url = [NSString stringWithFormat:@"%@%@",WEX_WALLET_HASH_PUBLIC_URL,self.txHash];
    }
    else
    {
        url = [NSString stringWithFormat:@"%@%@",WEX_WALLET_HASH_PRIVATE_URL,self.txHash];
    }
    
    [webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
    
    [self.view addSubview:webView];
    
    [webView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.bottom.trailing.equalTo(self.view);
    }];
}





@end
