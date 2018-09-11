//
//  WeXSearchUpChainResultManger.h
//  WeXBlockChain
//
//  Created by wh on 2018/9/7.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger,WEXUpChainResultType) {
    WEXUpChainResultTypeSuccess,//成功
    WEXUpChainResultTypeFail //失败
};

typedef void(^WEXUpChainResultTypeBlock)(WEXUpChainResultType resultTyoe);

@interface WeXSearchUpChainResultManger : NSObject

+ (instancetype)shareManager;

//查询上链是否成功的方法
- (void)checkUpChainResultWithTxHash:(NSString *)txHashStr Callback:(WEXUpChainResultTypeBlock)result;

@end
