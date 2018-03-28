//
//  WexBaseNetAdapterModal.h
//  WeXFin
//
//  Created by Mark on 15/7/28.
//  Copyright (c) 2015年 SinaPay. All rights reserved.
//

#import "JSONModel.h"


#pragma mark - WexBaseNetAdapterResponseHeadModal
#pragma mark

typedef NS_ENUM(NSInteger, WexBaseNetAdapterResponseStatus){
    WexBaseNetAdapterResponseStatusUnknown,
    WexBaseNetAdapterResponseStatusSuccess,
    WexBaseNetAdapterResponseStatusFail,
    WexBaseNetAdapterResponseStatusSessionTimeOut,
    WexBaseNetAdapterResponseStatusChangeDevice,
};

@interface WexBaseNetAdapterResponseHeadModal : JSONModel

@property (nonatomic, strong) NSString<Optional>* systemCode;
@property (nonatomic, strong) NSString<Optional>* businessCode;
@property (nonatomic, strong) NSString<Optional>* message;

//- (WexBaseNetAdapterResponseStatus)status;

@end


@interface WexBaseNetAdapterWordPressGETResponseHeadModal : JSONModel


@property (nonatomic, strong) NSString<Optional>* totalNumber;
@property (nonatomic, strong) NSString<Optional>* totalPages;


@end


#pragma mark - WexBaseNetAdapterDelegate
#pragma mark

@class WexBaseNetAdapter;
@class WexBaseNetChainAdapter;

@protocol WexBaseNetAdapterDelegate<NSObject>

@optional
// 调用前消息
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter*)adapter
                        beforeRun:(JSONModel*)param;

// wordPresss GET回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter*)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal*)response;
// 回调
- (void)wexBaseNetAdapterWordPressGETDelegate:(WexBaseNetAdapter*)adapter
                                         head:(WexBaseNetAdapterWordPressGETResponseHeadModal*)head
                                     response:(JSONModel*)response;

// ChainAdapter每次调用完一个adpter后，会调用这个delegate，我们可以在这里改变currentBaseAdapterParam
- (void)wexBaseNetChainAdapterDelegate:(WexBaseNetChainAdapter*)chainAdapter
                    andPrevBaseAdapter:(WexBaseNetAdapter*)prevBaseAdapter
               andPrevBaseAdapterParam:(JSONModel*)prevBaseAdapterParam
            andPrevBaseAdapterResponse:(JSONModel*)prevBaseAdapterResponse
                 andCurrentBaseAdapter:(WexBaseNetAdapter*)currentBaseAdapter
            andCurrentBaseAdapterParam:(JSONModel*)currentBaseAdapterParam;

@end



