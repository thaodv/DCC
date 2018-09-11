//
//  WeXSearchUpChainResultManger.m
//  WeXBlockChain
//
//  Created by wh on 2018/9/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXSearchUpChainResultManger.h"
#import "WeXGetReceiptResult2Adapter.h"
#import "WeXGetReceiptResult2Modal.h"

@interface WeXSearchUpChainResultManger()<WexBaseNetAdapterDelegate>

@property (nonatomic,strong)WeXGetReceiptResult2Adapter *getReceiptAdapter;
@property (nonatomic,assign)NSInteger requestCount;
@property (nonatomic,copy)NSString *txHash;
@property (nonatomic,copy)WEXUpChainResultTypeBlock resultBlock;

@end

@implementation WeXSearchUpChainResultManger

+ (instancetype)shareManager{
    static WeXSearchUpChainResultManger *manager;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[WeXSearchUpChainResultManger alloc] init];
    });
    return manager;
}

//查询上链是否成功的方法
- (void)checkUpChainResultWithTxHash:(NSString *)txHashStr Callback:(WEXUpChainResultTypeBlock)result{
    
    [self createReceiptResultRequest:txHashStr];
    
    self.resultBlock = ^(WEXUpChainResultType resultTyoe) {
        result(resultTyoe);
    };
    
}

#pragma -mark 查询上链结果请求
- (void)createReceiptResultRequest:(NSString *)txHash{
    _getReceiptAdapter = [[WeXGetReceiptResult2Adapter alloc] init];
    _getReceiptAdapter.delegate = self;
    WeXGetReceiptResult2ParamModal* paramModal = [[WeXGetReceiptResult2ParamModal alloc] init];
    paramModal.txHash = txHash;
    [_getReceiptAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    
  if (adapter == _getReceiptAdapter){
        
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"])
        {
            WeXGetReceiptResult2ResponseModal *model = (WeXGetReceiptResult2ResponseModal *)response;
            //上链成功
            WEXNSLOG(@"%@",model);
            
            if (model.hasReceipt && model.approximatelySuccess) {
                //上链成功
              
                WEXNSLOG(@"上链成功");
                WEXNSLOG(@"0.0");
                if (self.resultBlock) {
                    self.resultBlock(WEXUpChainResultTypeSuccess);
                }
                
            } else
            {
                if (_requestCount > 5) {
                    _requestCount = 0;
                    if (self.resultBlock) {
                        self.resultBlock(WEXUpChainResultTypeFail);
                    }
                    WEXNSLOG(@"查询结果失败");
                    return;
                }
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self createReceiptResultRequest:_txHash];
                    _requestCount++;
                });
              
            }
            
        }else{
            if (self.resultBlock) {
                self.resultBlock(WEXUpChainResultTypeFail);
            }
             WEXNSLOG(@"请求失败");
        }
    }
}


@end
