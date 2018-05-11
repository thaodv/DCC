//
//  WXPassHelper.m
//  testWebView
//
//  Created by zhuojian on 2017/11/13.
//  Copyright © 2017年 zhuojian. All rights reserved.
//
#import "AppDelegate.h"
#import "WXPassHelper.h"
#import "WebViewJavascriptBridge.h"
#import "WXPassHelperConstant.h"
#define WEX_WEB_INT_SUCC 2 // 容器初始化成功
#define WEX_Provider_INT_SUCC 2 // 连接器初始化成功

#define WEX_EVN_PRODUCTION NO  // 生产环境YES 读内部资源XXXConstant.h,开发环境NO 读外部资源.html

@interface WXPassHelper()
@property(nonatomic,strong)WKWebView* webView;
    
@property WebViewJavascriptBridge* bridge;
//@property(nonatomic,assign)WXResultBlock initBlock;

@property(nonatomic,strong)WKWebView* thirdWebView; // 第三方以太坊容器
    @property(nonatomic,strong)WKWebView* nThirdWebView;

@property WebViewJavascriptBridge* thirdBridge; // 第三方以太坊桥接器
@property WebViewJavascriptBridge* nThirdBridge;
    
    


@property(nonatomic,assign)NSInteger isInitSucced;
@property(nonatomic,assign)NSInteger isConnectSucced;



@end

@implementation WXPassHelper

static WXPassHelper* instanceShare;
static WXPassHelper* instanceFTCShare;
static WXResultBlock initProviderBlock;
static WXResultBlock initBlock;
+(id)instance{
    if(!instanceShare)
    {
        instanceShare=[[WXPassHelper alloc] init];
        instanceShare.isInitSucced=0;
        
        
//        [instanceShare initPassHelperBlock];
    }
    
    return instanceShare;
}

+(id)createFTCInstance{
    if(!instanceFTCShare)
    {
        instanceFTCShare=[[WXPassHelper alloc] init];
        instanceFTCShare.isInitSucced=0;
    }
    return instanceFTCShare;
}

-(id)init{
    self=[super init];
    
    return self;
}

-(void)initPassHelperBlock:(WXResultBlock)block{

    // other
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.001 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        initBlock=block;

        if(!self.webView)
        {
            self.webView = [[WKWebView alloc] initWithFrame:CGRectZero];
            [[[UIApplication sharedApplication] keyWindow] addSubview:self.webView];
            if (_bridge) { return; }
            
            _bridge = [WebViewJavascriptBridge bridgeForWebView:self.webView];
            [_bridge setWebViewDelegate:self];
            [self loadPage:self.webView fileName:@"wex_main"];
            
            
            // 第三方以太坊库
            self.thirdWebView = [[WKWebView alloc] initWithFrame:CGRectZero];
            [[[UIApplication sharedApplication] keyWindow] addSubview:self.thirdWebView];
            if (_thirdBridge) { return; }
            //
            _thirdBridge = [WebViewJavascriptBridge bridgeForWebView:self.thirdWebView];
            [_thirdBridge setWebViewDelegate:self];
            [self loadPage:self.thirdWebView fileName:@"wex_chain_third"];
            
            // nthird
            
            
            [WebViewJavascriptBridge enableLogging];
        }
        else{
            if(self.isInitSucced==WEX_WEB_INT_SUCC)
            initBlock(nil);
        }
    });
 
   

//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.01 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//        self.nThirdWebView = [[WKWebView alloc] initWithFrame:CGRectZero];
//        [[[UIApplication sharedApplication] keyWindow] addSubview:self.nThirdWebView];
//        //
//        self.nThirdBridge = [WebViewJavascriptBridge bridgeForWebView:self.nThirdWebView];
//
//        [self.nThirdBridge setWebViewDelegate:self];
//        [self loadPage:self.nThirdWebView fileName:@"wex_chain_third"];
//
////        NSString *strResourcesBundle = [[NSBundle mainBundle] pathForResource:@"web3js" ofType:@"bundle"];
////        // 找到对应images夹下的图片
////        NSString* htmlPath = [[NSBundle bundleWithPath:strResourcesBundle] pathForResource:@"wex_chain_third" ofType:@"html" inDirectory:nil];
////        //    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"test" ofType:@"html"];
////        NSString* appHtml = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:nil];
////        NSURL *baseURL = [NSURL fileURLWithPath:htmlPath];
////        [self.nThirdWebView loadHTMLString:appHtml baseURL:baseURL];
//    });
}

- (void)loadPage:(WKWebView*)webView fileName:(NSString*)fileName{
    NSString* htmlPath=@"";
    if([fileName isEqualToString:@"wex_chain_third"])
    {
        // 思路:从mainbundle中获取resources.bundle
        NSString *strResourcesBundle = [[NSBundle mainBundle] pathForResource:@"web3js" ofType:@"bundle"];
        // 找到对应images夹下的图片
        htmlPath = [[NSBundle bundleWithPath:strResourcesBundle] pathForResource:fileName ofType:@"html" inDirectory:nil];
        
        // 线上部署
        if(WEX_EVN_PRODUCTION)
        {
        NSURL *baseURL = [NSURL fileURLWithPath:htmlPath];
        NSString* content=WX_THIRD_HTML;
        [webView loadHTMLString:content baseURL:baseURL];
        return;
        }
    }
    else
    {
        NSString *strResourcesBundle = [[NSBundle mainBundle] pathForResource:@"web3js" ofType:@"bundle"];
        htmlPath = [[NSBundle bundleWithPath:strResourcesBundle] pathForResource:fileName ofType:@"html" inDirectory:nil];
        
        // 线上部署
        if(WEX_EVN_PRODUCTION)
        {
        NSURL *baseURL = [NSURL fileURLWithPath:htmlPath];
        NSString* content=WX_NORMAL_HTML;
        [webView loadHTMLString:content baseURL:baseURL];
        return;
        }
    }
    
    NSString* appHtml = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:nil];
    NSURL *baseURL = [NSURL fileURLWithPath:htmlPath];
    [webView loadHTMLString:appHtml baseURL:baseURL];
}

#pragma mark - WebView
- (void)webViewDidStartLoad:(UIWebView *)webView {
    NSLog(@"webViewDidStartLoad");
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    NSLog(@"webViewDidFinishLoad");
    self.isInitSucced++;
    if(self.isInitSucced==WEX_WEB_INT_SUCC)
        initBlock(nil);
    
  
}

-(void)connectUrl:(NSString*)url responseBlock:(WXResultBlock)block
{
    [self initPassHelperBlock:^(id response) {
        
        if(response!=nil)
        {
            NSError* error=response;
            NSLog(@"容器加载失败:%@",error);
            block([NSError errorWithDomain:@"WXPassHelperException" code:-1 userInfo:nil]);
            return;
        }
        
        
        [_thirdBridge callHandler:@"ethInitProvider" data:url responseCallback:^(id response) {
            [_bridge callHandler:@"ethInitProvider" data:url responseCallback:^(id response) {
                block(nil);
            }];
        }];
        
    }];
    
}
- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error{
    initBlock(error);
}
    
#pragma mark -WKWebView
- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(WKNavigation *)navigation {
        NSLog(@"webViewDidStartLoad");
    
    }
    
- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    NSLog(@"webViewDidFinishLoad");
    self.isInitSucced++;
    if(self.isInitSucced==WEX_WEB_INT_SUCC)
    initBlock(nil);
    
}

#pragma mark - passMethod


-(void)initProvider:(NSString*)url  responseBlock:(WXResultBlock)block
{
    [_bridge callHandler:@"ethInitProvider" data:url responseCallback:^(id response) {
        [_thirdBridge callHandler:@"ethInitProvider" data:url responseCallback:^(id response) {
            block(response);
        }];
    }];
}

-(void)createAccountBlock:(WXResultBlock)block{
    [_bridge callHandler:@"accountsCreate" data:nil responseCallback:^(id response) {
        block(response);
    }];
}

-(void)accountFromPrivateKey:(NSString*)privateKey responseBlock:(WXResultBlock)block{
    [_bridge callHandler:@"accountFromPrivateKey" data:privateKey responseCallback:^(id response) {
//        NSLog(@"accountsCreate responded: %@", response); // 返回address,privateKey
        block(response);
    }];
}

-(void)sha3:(NSString*)stringValue responseBlock:(WXResultBlock)block{
    [_bridge callHandler:@"ethSha3" data:stringValue responseCallback:^(id response) {
//        NSLog(@"accountsCreate responded: %@", response); // 返回address,privateKey
        block(response);
        
    }];
}
    
-(void)bigValue:(id)stringValue responseBlock:(WXResultBlock)block{
    [_bridge callHandler:@"bigValue" data:stringValue responseCallback:^(id response) {
        block(response);
    }];
}

- (void)encodeFunCallAbiInterface:(NSString*)abiJson params:(NSString*)params responseBlock:(WXResultBlock)block
{
    NSDictionary* dictParams=@{@"abiInterface":abiJson};
    if(params)
    {
        dictParams=@{@"abiInterface":abiJson,@"params":params};
    }
    
    [_bridge callHandler:@"ethEncodeFunCall" data:dictParams responseCallback:^(id response) {
        block(response);
    }];
    
}

- (void)callContractAddress:(NSString*)contractAddress data:(NSString *)data responseBlock:(WXResultBlock)block{
    NSDictionary* dict=@{
                         @"to":contractAddress,
                         @"data":data
                         };
    [_thirdBridge callHandler:@"ethCallBack" data:dict responseCallback:^(id response) {
        block(response);
    }];
}

- (void)call2ContractAddress:(NSString*)contractAddress data:(NSString *)data type:(NSString *)urlStr responseBlock:(WXResultBlock)block{
    NSDictionary* dict=@{
                         @"urlStr":urlStr,
                         @"to":contractAddress,
                         @"data":data
                         };
    [_thirdBridge callHandler:@"ethCallBack2" data:dict responseCallback:^(id response) {
        block(response);
    }];
}
    
-(void)signTransactionWithContractAddress:(NSString*)contractAddress abiInterface:(NSString*)abiJson params:(NSString*)params privateKey:(NSString*)privateKey responseBlock:(WXResultBlock)block{
    
    NSDictionary* dictParams=@{@"abiInterface":abiJson};
    if(params)
        dictParams=@{@"abiInterface":abiJson,@"params":params};
    
    [_bridge callHandler:@"ethEncodeFunCall" data:dictParams responseCallback:^(id response) {
        
        NSDictionary* dict=@{
                             @"to":contractAddress,
                             @"pk":privateKey,
//                             @"value":@"0",
//                             @"gas":@"200000000",
//                             @"gasPrice":@"21000000000",
                             @"data":response
                             };
        [_thirdBridge callHandler:@"ethSignTransaction" data:dict responseCallback:^(id response) {
//            NSLog(@"testJavascriptHandler responded: %@", response);
            block(response);
        }];
    }];
}

//新增方法 2018-1-22 wcc ETH
- (void)getETHNonceWithContractAddress:(NSString*)contractAddress responseBlock:(WXResultBlock)block
{
     NSDictionary* dictParams=@{@"address":contractAddress};
    [_bridge callHandler:@"ethGetNonce" data:dictParams responseCallback:^(id response) {
        block(response);
    }];
}

- (void)getETHBalanceWithContractAddress:(NSString*)contractAddress responseBlock:(WXResultBlock)block
{
    NSDictionary* dictParams=@{
                               @"address":contractAddress
                               
                               };
    [_thirdBridge callHandler:@"ethGetBalance" data:dictParams responseCallback:^(id response) {
        block(response);
    }];
}

- (void)getETHBalance2WithContractAddress:(NSString*)contractAddress type:(NSString *)urlStr responseBlock:(WXResultBlock)block
{
    NSDictionary* dictParams=@{
                               @"address":contractAddress,
                               @"urlStr":urlStr
                               };
    [_thirdBridge callHandler:@"ethGetBalance" data:dictParams responseCallback:^(id response) {
        block(response);
    }];
}

- (void)getGasPriceResponseBlock:(WXResultBlock)block
{
    [_thirdBridge callHandler:@"ethGetGasPrice" data:nil responseCallback:^(id response) {
        block(response);
    }];
}

- (void)getGasLimitWithToAddress:(NSString*)toAddress fromAddress:(NSString*)fromAddress data:(NSString *)data responseBlock:(WXResultBlock)block
{
    NSDictionary* dict=@{
                         @"to":toAddress,
                         @"from":fromAddress,
                         @"data":data
                         };
    [_bridge callHandler:@"ethGetGasLimit" data:dict responseCallback:^(id response) {
        block(response);
    }];
}

//新增方法 2018-1-22 wcc ETH
-(void)sendETHTransactionWithContractToAddress:(NSString*)contractToAddress  privateKey:(NSString*)privateKey value:(NSString *)value gasPrice:(NSString *)gasPrice gasLimit:(NSString *)gasLimit nonce:(NSNumber *)nonce remark:(NSString *)remark responseBlock:(WXResultBlock)block{
    
    NSMutableString *dataStr;
    if (remark) {
        dataStr = [WexCommonFunc hexStringWithString:remark];
    }
    
    NSDictionary* dict=@{
                         @"to":contractToAddress,
                         @"pk":privateKey,
                         @"value":value,
                         @"gasLimit":gasLimit,
                         @"gasPrice":gasPrice,
                         @"nonce":nonce,
                         };
    NSLog(@"dict=%@",dict);
    [_thirdBridge callHandler:@"ethETHSignTransaction" data:dict responseCallback:^(id response) {
        block(response);
    }];
}

//新增方法 2018-1-22 wcc ERC20
-(void)sendERC20TransactionWithContractToAddress:(NSString*)contractToAddress abiJson:(NSString *)abiJson  privateKey:(NSString*)privateKey params:(NSString*)params gasPrice:(NSString *)gasPrice gasLimit:(NSString *)gasLimit nonce:(NSString *)nonce responseBlock:(WXResultBlock)block{
    
    NSDictionary* dictParams=@{@"abiInterface":abiJson};
    if(params)
        dictParams=@{@"abiInterface":abiJson,@"params":params};
    
    [_bridge callHandler:@"ethEncodeFunCall" data:dictParams responseCallback:^(id response) {
        
        NSDictionary* dict=@{
                             @"to":contractToAddress,
                             @"pk":privateKey,
                             @"value":@"0",
                             @"gasLimit":gasLimit,
                             @"gasPrice":gasPrice,
                             @"nonce":nonce,
                             @"data":response
                             };
        [_thirdBridge callHandler:@"ethETHSignTransaction" data:dict responseCallback:^(id response) {
            block(response);
        }];
    }];
}

//私链上链方法
-(void)sendPrivateTransactionWithContractToAddress:(NSString*)contractToAddress abiJson:(NSString *)abiJson  privateKey:(NSString*)privateKey params:(NSString*)params responseBlock:(WXResultBlock)block{
    
    NSDictionary* dictParams=@{@"abiInterface":abiJson};
    if(params)
        dictParams=@{@"abiInterface":abiJson,@"params":params};
    
    [_bridge callHandler:@"ethEncodeFunCall" data:dictParams responseCallback:^(id response) {
        
        NSDictionary* dict=@{
                             @"to":contractToAddress,
                             @"pk":privateKey,
                             @"value":@"0",
                             @"gasLimit":@"200000000",
                             @"gasPrice":@"21000000000",
                             @"data":response
                             };
        [_thirdBridge callHandler:@"ethSendPrivateTransaction" data:dict responseCallback:^(id response) {
            block(response);
        }];
    }];
}
//返回带txhash
-(void)queryTransactionWithResponseParamReceipt:(NSString*)hashTx  responseBlock:(WXResultBlock)block{
    [_thirdBridge callHandler:@"ethQueryTransactionWithResponseParam" data:hashTx responseCallback:^(id response) {
        block(response);
    }];
}


-(void)queryTransactionReceipt:(NSString*)hashTx  responseBlock:(WXResultBlock)block{
    [_thirdBridge callHandler:@"ethQueryTransaction" data:hashTx responseCallback:^(id response) {
        block(response);
    }];
}

-(void)queryTransactionReceiptWithPending:(NSString *)hashTx responseBlock:(WXResultBlock)block{
    [_thirdBridge callHandler:@"ethQueryTransactionAll" data:hashTx responseCallback:^(id response) {
        block(@{@"rep":response,@"hash":hashTx});
    }];
}

-(void)sendRawTransaction:(NSString*)rawTransaction  responseBlock:(WXResultBlock)block{
    [_thirdBridge callHandler:@"ethSendRawTransaction" data:rawTransaction responseCallback:^(id response) {
       block(response);
    }];
}

-(void)nonceWithAccountAddress:(NSString*)accountAddress  responseBlock:(WXResultBlock)block{
    
//    Byte bytes[]={0, 0, 1, 95, -62, -85, 12, 3};
//    NSData* data=[NSData dataWithBytes:bytes length:8];
//    long ll=[self NSDataToUInt:data];
   
    __weak WXPassHelper* weakSelf=self;
    
//    NSData* data2=[self longToNSData:ll];
//    NSString* string= [self byteStringFromData:data2];
//    NSLog(@"%@",string);

    
    NSString* stringVal=[NSString stringWithFormat:@"%@%@",[self uuid],accountAddress];
    [self sha3:stringVal responseBlock:^(id response) {
        
        NSString* shaVal=response;
        [weakSelf bigValue:shaVal responseBlock:^(id response) {
            
            NSString* lastString=[response substringToIndex:24];
            NSData *lastData = [lastString dataUsingEncoding: NSASCIIStringEncoding];
            Byte *lastBytes = (Byte *)[lastData bytes];
            lastData = [[NSData alloc] initWithBytes:lastBytes length:24];
            
            //
            NSTimeInterval timestamp=[[NSDate date] timeIntervalSince1970];
            
            long longTime=timestamp*1000;
//            long longTime=1510799510531;
            NSData* timeData=[weakSelf longToNSData:longTime];
            Byte *byteTime = (Byte *)[timeData bytes];

            
            NSMutableData* list=[NSMutableData data];
            [list appendBytes:byteTime length:8];
            [list appendBytes:lastBytes length:24];
            
            NSString* string= [self byteStringFromData:list];
            [_bridge callHandler:@"crunchValue" data:string responseCallback:^(id response) {
                block(response);

            }];

        }];
        
    }];

}




-(void)hexStringFromBytes:(NSData*)data responseBlock:(WXResultBlock)block{
    NSString* strval=[self byteStringFromData:data];
    [_bridge callHandler:@"ethHexFromBytes" data:strval responseCallback:^(id response) {
        block(response);
    }];
}
    
#pragma mark - v0.2 钱包API
-(void)createPrivateKeyBlock:(WXResultBlock)block{
    [_thirdBridge callHandler:@"ethCreatePK" data:nil responseCallback:^(id response) {
        block(response);
    }];
}
-(void)keystoreCreateWithPrivateKey:(NSString *)privateKey password:(NSString *)password responseBlock:(WXResultBlock)block{
    
    [_thirdBridge callHandler:@"ethCreateKeyStore" data:@{@"pk":privateKey,@"pwd":password} responseCallback:^(id response) {
        block(response);
    }];
}
    
-(void)restoreAccountFromKeyStore:(NSString*)keyStoreJson  password:(NSString *)password responseBlock:(WXResultBlock)block{

    
        [_thirdBridge callHandler:@"ethRecoverPrivateKey" data:@{@"keyVal":keyStoreJson,@"pwd":password} responseCallback:^(id response) {
            block(response);
        }];
}

-(void)callFuncWithABIJson:(NSString*)abiJson params:(NSString*)params abiAddress:(NSString*)abiAddress responseBlock:(WXResultBlock)block
{
    
    NSDictionary* dictParams=@{@"abiInterface":abiJson};
    if(params)
    dictParams=@{@"abiInterface":abiJson,@"params":params};
    
    [_bridge callHandler:@"ethEncodeFunCall" data:dictParams responseCallback:^(id responseData) {
        [_bridge callHandler:@"ethFunCall" data:@{@"data":responseData,@"to":abiAddress} responseCallback:^(id response) {
            block(response);
        }];
    }];
   
}

#pragma mark - utils
#pragma mark - long long转NSData



/** 输出字节数组格式 */
-(NSString*)byteStringFromData:(NSData*)data{
    NSMutableString* result=[NSMutableString string];
    Byte *testByte = (Byte *)[data bytes];
    for(int i=0;i<[data length];i++)
    {
//        printf("testByte = %d\n",testByte[i]);
        if(i==data.length-1) // last byte for remove ','
        {
            NSString* val=[NSString stringWithFormat:@"%d",testByte[i]];
            [result appendString:val];
        }
        else{
        NSString* val=[NSString stringWithFormat:@"%d,",testByte[i]];
        [result appendString:val];
        }
    }
    
    [result insertString:@"[" atIndex:0];
    [result appendString:@"]"];
    
    return result;
}

-(void)hexStringFromBytes:(NSString*)bytes{
    
}

- (NSData *)longToNSData:(long long)data
{
    
    long long datatemplength = CFSwapInt64BigToHost(data);  //大小端不一样，需要转化
    
    NSData *temdata = [NSData dataWithBytes: &datatemplength length: sizeof(datatemplength)];
    
    return temdata;
}

- (long long) NSDataToUInt:(NSData *)data
{
    long long datatemplength;
    
    [data getBytes:&datatemplength length:sizeof(datatemplength)];
    
    long long result = CFSwapInt64BigToHost(datatemplength);//大小端不一样，需要转化
    
    return result;
}

-(NSString*)uuid{
    CFUUIDRef uuidRef = CFUUIDCreate(kCFAllocatorDefault);
    NSString *uuid= (NSString *)CFBridgingRelease(CFUUIDCreateString (kCFAllocatorDefault,uuidRef));
    return uuid;
}



@end
