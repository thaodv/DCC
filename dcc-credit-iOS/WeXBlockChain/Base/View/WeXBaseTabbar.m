//
//  WeXBaseTabbar.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/9/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseTabbar.h"

@implementation WeXBaseTabbar

- (void)setFrame:(CGRect)frame {
    if (self.superview &&CGRectGetMaxY(self.superview.bounds) != CGRectGetMaxY(frame)) {
        frame.origin.y = CGRectGetHeight(self.superview.bounds) - CGRectGetHeight(frame);
    }
    [super setFrame:frame];
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.translucent =false;
        self.backgroundColor = [UIColor whiteColor];
    }
    return self;
    
}



@end
