//
//  WexBaseNetChainAdapter.h
//  WeXFin
//
//  Created by Mark on 16/5/24.
//  Copyright © 2016年 SinaPay. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WexBaseNetAdapterModal.h"

@class WexBaseNetAdapter;

// 串行Adapter
@interface WexBaseNetChainAdapter : NSObject

@property (nonatomic, weak) id<WexBaseNetAdapterDelegate> delegate;

@property (nonatomic) BOOL bDefaultRequestUIHandle; // 是否启用默认的接口请求UI回调处理（这里仅仅是标识，具体的处理由UI类去处理）
@property (nonatomic) BOOL bDefaultResponseUIHandle; // 是否启用默认的接口返回UI回调处理（这里仅仅是标识，具体的处理由UI类去处理）

- (void)addAdapter:(WexBaseNetAdapter*)adapter andParam:(JSONModel*)param;
- (void)start;

@end
