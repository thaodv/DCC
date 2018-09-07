//
//  WeXLeftIconRightLabelView.h
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/28.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseView.h"

@interface WeXLeftIconRightLabelView : WeXBaseView

@property (nonatomic, weak) UIImageView *iconImage;
@property (nonatomic, weak) UILabel *titleLab;
@property (nonatomic, weak) UIView *backView;
@property (nonatomic,copy) void (^DidClickView)(void);


- (void)setLeftImageName:(NSString *)imageName
                   title:(NSString *)title;


@end
