//
//  WeXConfirmRepaymentView.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/8.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WeXConfirmRepaymentView : UIView

+ (instancetype)createConfirmRepayView:(CGRect)frame
                                amount:(NSString *)amount
                                symbol:(NSString *)symbol
                               okEvent:(void(^)(void))okEvent;

@end
