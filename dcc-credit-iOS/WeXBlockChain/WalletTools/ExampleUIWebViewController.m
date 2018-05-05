//
//  ExampleUIWebViewController.m
//  ExampleApp-iOS
//
//  Created by Marcus Westin on 1/13/14.
//  Copyright (c) 2014 Marcus Westin. All rights reserved.
//

#import "ExampleUIWebViewController.h"
#import "WebViewJavascriptBridge.h"
#import "WXPassHelper.h"
@interface ExampleUIWebViewController ()
@property WebViewJavascriptBridge* bridge;
@end

@implementation ExampleUIWebViewController

- (void)viewWillAppear:(BOOL)animated {
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(@"容器加载失败:%@",error);
            return;
        }
        
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:@"http://10.65.209.49:6789" responseBlock:^(id response) {
            NSLog(@"%@",response); // 返回address,privateKey
            [[WXPassHelper instance] createAccountBlock:^(id response) {
                NSLog(@"%@",response); // 返回address,privateKey
            }];
        }];
    }];
    /*
    if (_bridge) { return; }
    
    UIWebView* webView = [[UIWebView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:webView];
    
    [WebViewJavascriptBridge enableLogging];
    
    _bridge = [WebViewJavascriptBridge bridgeForWebView:webView];
    [_bridge setWebViewDelegate:self];
    
    [_bridge registerHandler:@"testObjcCallback" handler:^(id data, WVJBResponseCallback responseCallback) {
        NSLog(@"testObjcCallback called: %@", data);
        responseCallback(@"Response from testObjcCallback");
    }];
    
    [_bridge callHandler:@"testJavascriptHandler" data:@{ @"foo":@"before ready" }];
    
    [self renderButtons:webView];
    [self loadExamplePage:webView];
     */
    
    NSString *homeDir = NSHomeDirectory();
    [self renderButtons:self.view];
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    NSLog(@"webViewDidStartLoad");
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    NSLog(@"webViewDidFinishLoad");
}

- (void)renderButtons:(UIWebView*)webView {
    UIFont* font = [UIFont fontWithName:@"HelveticaNeue" size:11.0];
    
    UIButton *callbackButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [callbackButton setTitle:@"Call handler" forState:UIControlStateNormal];
    [callbackButton addTarget:self action:@selector(callHandler:) forControlEvents:UIControlEventTouchUpInside];
    [self.view insertSubview:callbackButton aboveSubview:webView];
    callbackButton.frame = CGRectMake(0, 400, 100, 35);
    callbackButton.titleLabel.font = font;
    
    UIButton* reloadButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [reloadButton setTitle:@"Reload webview" forState:UIControlStateNormal];
    [reloadButton addTarget:webView action:@selector(reload) forControlEvents:UIControlEventTouchUpInside];
    [self.view insertSubview:reloadButton aboveSubview:webView];
    reloadButton.frame = CGRectMake(90, 400, 100, 35);
    reloadButton.titleLabel.font = font;
    
    UIButton* safetyTimeoutButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [safetyTimeoutButton setTitle:@"Disable safety timeout" forState:UIControlStateNormal];
    [safetyTimeoutButton addTarget:self action:@selector(disableSafetyTimeout) forControlEvents:UIControlEventTouchUpInside];
    [self.view insertSubview:safetyTimeoutButton aboveSubview:webView];
    safetyTimeoutButton.frame = CGRectMake(190, 400, 120, 35);
    safetyTimeoutButton.titleLabel.font = font;
}

- (void)disableSafetyTimeout {
    [self.bridge disableJavscriptAlertBoxSafetyTimeout];
}

- (void)callHandler:(id)sender {
    
    /** 创建钱包address,privateKey */
    [[WXPassHelper instance] createAccountBlock:^(id response) {
        NSLog(@"%@",response); // 返回address,privateKey
    }];
    
    /** 根据私钥返回钱包地址 */
    [[WXPassHelper instance] accountFromPrivateKey:@"0x66fcd52854d3f9d99f69eed4c295eee163edf37499510bc70fd0c6c39f793b47" responseBlock:^(id response) {
        NSLog(@"%@",response); // 返回address,privateKey
    }];
    
    /** 生成keystore json */
    [[WXPassHelper instance] keystoreCreateWithPrivateKey:@"0x4c0883a69102937d6231471b5dbb6204fe5129617082792ae468d01a3f362318" password:@"test!" responseBlock:^(id response) {
        NSLog(@"%@",response); // 返回keystore json
        
        /** 恢复钱包地址和私钥 */
        [[WXPassHelper instance] restoreAccountFromKeyStore:response password:@"test!" responseBlock:^(id response) {
            NSLog(@"%@",response); // 返回钱包地址和privateKey
        }];

    }];
    
    
    /** 事物签名返回rawTransaction  上传RSA公钥用到 http://10.65.178.18:9511/passport-gateway/restful/ca/1/uploadPubKey */
    
    // 合约定义说明
//    NSString* abiJson=@"{'constant':false,'inputs':[{'name':'_publickey','type':'bytes'}],'name':'putKey','outputs':[{'name':'_result','type':'bool'}],'payable':false,'stateMutability':'nonpayable','type':'function'}";
    
    NSString* abiJson=@"{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}";
    
    NSData* dataPubKey= [NSData dataWithContentsOfFile:[[NSBundle mainBundle]pathForResource:@"pub" ofType:@"key"]];
    
    // 合约参数值
//    NSString* abiParamsValues=@"['0x0cc9684af605bae10de801218321c1336bb62946']"; // 这里代表存放是自己的RSA公钥
    NSString* abiParamsValues=@"['0xB935E63A83BD0e8C65E599c8fE1C1DF54228837D','10000000000000000']"; // 这里代表存放是自己的RSA公钥

    
    // 合约地址(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境)
    NSString* abiAddress=@"0xFB1c9F77b2D2d0B3a5cee28615d3Be22d3DFC217"; // dev
//    NSString* abiAddress=@"0xca2b061e4cb4f90f7bf227bd4116692cd0093a19";
    
    // 钱包地址或账户地址
    NSString* accountAddress=@"0x4339F7d07691A6C05098aBc0663e582590F0642E"; // dev
//    NSString* accountAddress=@"0x6EA7eb2C8d9ff708F0AaE485d596a00c9763C234";
    
    // 以太坊私钥地址
    NSString* privateKey=@"3ae3a79bacb84a63dbc48e14f093d649218dc096df606db366c06661a94ccab5"; // dev
//    NSString* privateKey=@"0x7bea866aea49e617b3ee8f116359085a83565bd4913b5ca54e732e3c94609b0c";
    
    
    // 第一层先获取nonc
//    [[WXPassHelper instance] hexStringFromBytes:dataPubKey responseBlock:^(id response) {
//
//        NSString* abiParamsValues=[NSString stringWithFormat:@"['%@']",response];
//        [[WXPassHelper instance] nonceWithAccountAddress:accountAddress responseBlock:^(id response) {
//
//            // 签名方法返回rawTransaction(签名方法必须有网络，否则会调用失败)，调用 http://10.65.178.18:9511/passport-gateway/restful/ca/1/uploadPubKey
//            [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:privateKey nonce:response responseBlock:^(id response) {
//                NSLog(@"%@",response); // rawTransaction
//            }];
//
//        }];
//
//    } ];

    // 签名方法返回rawTransaction(签名方法必须有网络，否则会调用失败)，调用 http://10.65.178.18:9511/passport-gateway/restful/ca/1/uploadPubKey
    [[WXPassHelper instance] signTransactionWithContractAddress:abiAddress abiInterface:abiJson params:abiParamsValues privateKey:privateKey responseBlock:^(id response) {
        NSString* rawTransaction=response;
        NSLog(@"%@",response); // rawTransaction

//        // 提交事务(仅用于开发调试)
//        [[WXPassHelper instance] sendRawTransaction:rawTransaction responseBlock:^(id response) {
//            NSLog(@"%@",response); // hashTX
//        }];
    }];
    
    // 查询事务上链收据(仅用于开发调试)
    [[WXPassHelper instance] queryTransactionReceipt:@"0x645611236a0f267c855e054e4bbf919fd2efa1931017c8dce80dd600b0fc3cc7" responseBlock:^(id response) {
        NSLog(@"%@",response); // rawTransaction
    }];
    
    
}

- (void)testGetKey{
    // 初始化以太坊容器
    [[WXPassHelper instance] initPassHelperBlock:^(id response) {
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(@"容器加载失败:%@",error);
            return;
        }
        /** 连接以太坊(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境) */
        [[WXPassHelper instance] initProvider:YTF_DEVELOP_SERVER responseBlock:^(id response) {
            NSString* abiJsonGetKey=@"{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"}],\"name\":\"getKey\",\"outputs\":[{\"name\":\"_data\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}";
            //        // 合约参数值
            NSString* abiParamsValues=[NSString stringWithFormat:@"[\'%@\']",@"address"];
            // 合约地址(开发，测试，生产环境地址值不同，建议用宏区分不同开发环境)
            NSString* abiAddress=@"";
            [[WXPassHelper instance] callFuncWithABIJson:abiJsonGetKey params:abiParamsValues abiAddress:abiAddress responseBlock:^(id response) {
                NSLog(@"callFunc=%@",response);
            }];
        }];
    }];
    
}


- (void)loadExamplePage:(UIWebView*)webView {
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"index" ofType:@"html"];
    NSString* appHtml = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:nil];
    NSURL *baseURL = [NSURL fileURLWithPath:htmlPath];
    [webView loadHTMLString:appHtml baseURL:baseURL];
}

#pragma mark - Utils

@end
