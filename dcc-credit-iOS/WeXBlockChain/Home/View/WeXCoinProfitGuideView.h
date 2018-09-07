//
//  WeXCoinProfitGuideView.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/13.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger,WeXCoinProfitGuideViewType) {
    WeXCoinProfitGuideViewTypeSee = 0, //去看看
    WeXCoinProfitGuideViewTypeOk  = 1, //知道了
};

@interface WeXCoinProfitGuideView : UIView
+ (instancetype)createCoinProfitGuideView:(CGRect)frame
                              circleFrame:(CGRect)circleFrame
                                clickType:(void(^)(WeXCoinProfitGuideViewType))click;

- (void)dismiss;


@end
