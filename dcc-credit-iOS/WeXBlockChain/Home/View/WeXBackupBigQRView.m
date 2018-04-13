//
//  WeXBackupBigQRView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/29.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBackupBigQRView.h"

@implementation WeXBackupBigQRView

-(instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
        [self setupSubViews];
    }
    return self;
    
}

- (void)commonInit{
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapClick)];
    [self addGestureRecognizer:tap];
}

- (void)tapClick{
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    
    [self removeFromSuperview];
}

- (void)setupSubViews{
    
    UIView *backView = [[UIView alloc] init];
    backView.backgroundColor = [UIColor blackColor];
//    backView.alpha = 0.4;
    [self addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.top.equalTo(self);
        make.bottom.equalTo(self);
    }];
    
    UIImageView *QRImageView = [[UIImageView alloc] init];
    QRImageView.layer.magnificationFilter = kCAFilterNearest;
    QRImageView.backgroundColor = [UIColor greenColor];
    [self addSubview:QRImageView];
    [QRImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self);
        make.centerX.equalTo(self);
        make.width.equalTo(self);
        make.height.equalTo(self.mas_width);
    }];
    QRImageView.userInteractionEnabled = YES;
    _QRImageView = QRImageView;
 
    
}



-(void)dealloc
{
    NSLog(@"%s",__func__);
}



@end
