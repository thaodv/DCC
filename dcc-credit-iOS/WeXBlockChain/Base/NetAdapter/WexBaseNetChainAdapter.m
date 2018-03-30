//
//  WexBaseNetChainAdapter.m
//  WeXFin
//
//  Created by Mark on 16/5/24.
//  Copyright © 2016年 SinaPay. All rights reserved.
//

#import "WexBaseNetChainAdapter.h"
#import "WexBaseNetAdapter.h"

@interface WexBaseNetChainAdapter () <WexBaseNetAdapterDelegate>

@property (nonatomic, strong) NSMutableArray* adapterArray;
@property (nonatomic, strong) NSMutableArray* paramArray;
@property (nonatomic) NSUInteger nextRequestIndex;

@end

@implementation WexBaseNetChainAdapter

- (id)init
{
    self = [super init];
    if (self)
    {
        _nextRequestIndex = 0;
        _adapterArray = [NSMutableArray array];
        _paramArray = [NSMutableArray array];
    }
    return self;
}

- (void)addAdapter:(WexBaseNetAdapter*)adapter andParam:(JSONModel*)param
{
    adapter.delegate = self;
    
    [_adapterArray addObject:adapter];
    
    if (param)
    {
        [_paramArray addObject:param];
    }
    else
    {
        [_paramArray addObject:[NSNull null]];
    }
}

- (void)start
{
    if (_nextRequestIndex > 0)
    {
        return;
    }
    
    if ([_adapterArray count] > 0)
    {
        [self runAdapter];
    }
}

#pragma mark - Private functions

- (void)runAdapter
{
    WexBaseNetAdapter* adapter = _adapterArray[_nextRequestIndex];
    JSONModel* param = _paramArray[_nextRequestIndex];
    
    // 只有第一个adapter才会走wexBaseNetAdapterDelegate:beforeRun:
    if (_nextRequestIndex == 0 &&
        _delegate &&
        [_delegate respondsToSelector:@selector(wexBaseNetAdapterDelegate:beforeRun:)])
    {
        adapter.bDefaultRequestUIHandle = _bDefaultRequestUIHandle;
        
        [_delegate wexBaseNetAdapterDelegate:adapter beforeRun:param];
    }
    
    if ([param isKindOfClass:[NSNull class]])
    {
        [adapter run:nil];
    }
    else
    {
        [adapter run:param];
    }
}

- (void)adapterFinish:(WexBaseNetAdapter*)adapter
                 head:(WexBaseNetAdapterResponseHeadModal*)head
             response:(WeXBaseNetModal*)response
{
    if (_delegate &&
        [_delegate respondsToSelector:@selector(wexBaseNetAdapterDelegate:head:response:)])
    {
        adapter.bDefaultResponseUIHandle = _bDefaultResponseUIHandle;
        [_delegate wexBaseNetAdapterDelegate:adapter head:head response:response];
    }
}

#pragma mark - WexBaseNetAdapterDelegate

- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter*)adapter
                             head:(WexBaseNetAdapterResponseHeadModal*)head
                         response:(JSONModel*)response
{
//    if ([head status] == WexBaseNetAdapterResponseStatusSuccess)
//    {
//        // 调用下一个接口
//        if (_nextRequestIndex < ([_adapterArray count] - 1))
//        {
//            WexBaseNetAdapter* prevAdapter = _adapterArray[_nextRequestIndex];
//            JSONModel* prevParam = _paramArray[_nextRequestIndex];
//            
//            ++_nextRequestIndex;
//            
//            WexBaseNetAdapter* currentAdapter = _adapterArray[_nextRequestIndex];
//            JSONModel* currentParam = _paramArray[_nextRequestIndex];
//
//            if (_delegate &&
//                [_delegate respondsToSelector:@selector(wexBaseNetChainAdapterDelegate:andPrevBaseAdapter:andPrevBaseAdapterParam:andPrevBaseAdapterResponse:andCurrentBaseAdapter:andCurrentBaseAdapterParam:)])
//            {
//                [_delegate wexBaseNetChainAdapterDelegate:self
//                                       andPrevBaseAdapter:prevAdapter
//                                  andPrevBaseAdapterParam:prevParam
//                               andPrevBaseAdapterResponse:response
//                                    andCurrentBaseAdapter:currentAdapter
//                               andCurrentBaseAdapterParam:currentParam];
//            }
//            
//            [self runAdapter];
//        }
//        // 调用完成
//        else
//        {
//            [self adapterFinish:adapter head:head response:response];
//        }
//    }
//    // 中断接口调用链
//    else
//    {
//        [self adapterFinish:adapter head:head response:response];
//    }
}

@end
