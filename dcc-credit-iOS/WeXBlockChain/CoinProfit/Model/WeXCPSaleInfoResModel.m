//
//  WeXCPSaleInfoResModel.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPSaleInfoResModel.h"

@implementation WeXCPSaleInfoResModel

static NSString *const kSpace = @"    ";

- (void)setPresentation:(NSString *)presentation {
    _presentation = presentation;
    if ([_presentation length] > 0) {
        NSArray <NSString *>*textArray = [_presentation componentsSeparatedByString:@"。"];
        __block NSString *resultString = [NSString stringWithFormat:@"%@%@",kSpace,textArray.firstObject];
        [textArray enumerateObjectsUsingBlock:^(NSString *  obj, NSUInteger idx, BOOL *  stop) {
            if (idx > 0) {
                resultString = [NSString stringWithFormat:@"%@\%@%@",resultString,kSpace,obj];
            }
        }];
        [self setPresentationFormat:resultString];
    }
}


@end
