//
//  WeXCPBuyBottomView.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseView.h"

typedef NS_ENUM(NSInteger,WeXCPBuyBottomViewType) {
    WeXCPBuyBottomViewTypeLink  = 0, //公链转到私链
    WeXCPBuyBottomViewTypeBuyIn = 1, //立即认购
};

typedef NS_ENUM(NSInteger,WexCPBuyButtonType) {
    WexCPBuyButtonTypeNormal  = 0, //购买Button可点击
    WexCPBuyButtonTypeDisable = 1, //认购Button不可点击
};

typedef NS_ENUM(NSInteger,WexCPBuyButtonTipsType) {
    WexCPBuyButtonTipsTypePrivate = 0, //购买私链上的(DCC)
    WexCPBuyButtonTipsTypePublic  = 1, //购买公链上的(ETH)
};

@interface WeXCPBuyBottomView : WeXBaseView

+ (instancetype)createBuyBottomView:(CGRect)frame
                         clickEvent:(void(^)(WeXCPBuyBottomViewType))click;

+ (instancetype)createBuyBottomView:(CGRect)frame
                           tipsType:(WexCPBuyButtonTipsType)type
                         clickEvent:(void(^)(WeXCPBuyBottomViewType))click;

- (void)setBuyInButtonType:(WexCPBuyButtonType)type
                     title:(NSString *)title;


@end
