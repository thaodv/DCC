//
//  WeXBindWeChatAlertView.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseView.h"

@interface WeXBindWeChatAlertView : WeXBaseView

+ (instancetype)createAlertViewWithComplteEvent:(void(^)(BOOL isOk,BOOL isCancel))complete;

- (void)show;

- (void)dismiss;


@end


