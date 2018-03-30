//
//  WeXInfoHudView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/12/6.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXInfoHudView.h"

#define kInfoHudViewWith 220

@interface WeXInfoHudView()

@property (nonatomic, strong) UILabel* titleLabel;
@property (nonatomic) CGFloat delaySeconds;

@end

@implementation WeXInfoHudView

- (instancetype)initWithType:(WeXInfoHudViewType)type{
    self = [super init];
    if (self) {
        _type = type;
        [self commonInit];
        [self setupSubViews];
    }
    return self;
}


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
    if (_type == WeXInfoHudViewNormal) {
        self.backgroundColor = [UIColor clearColor];
//        self.layer.borderWidth = 0.5;
//        self.layer.borderColor = [UIColor blackColor].CGColor;
//
//        self.layer.shadowOffset = CGSizeMake(0, 0);
//        self.layer.shadowOpacity = 0.8;
//        self.layer.shadowColor = [UIColor blackColor].CGColor;
//        self.layer.cornerRadius = 5;
    }
    else
    {
        self.backgroundColor = [UIColor clearColor];
//        self.alpha = 0.3;
//        self.layer.borderWidth = 0.5;
//        self.layer.borderColor = [UIColor grayColor].CGColor;
        
//        self.layer.shadowOffset = CGSizeMake(0, 0);
//        self.layer.shadowOpacity = 0.8;
//        self.layer.shadowColor = [UIColor grayColor].CGColor;
//        self.layer.cornerRadius = 5;
    }
    
    
}

- (void)setupSubViews{
    _titleLabel = [[UILabel alloc] init];
    if (_type == WeXInfoHudViewNormal) {
        _titleLabel.textColor = [UIColor blackColor];

    }
    else
    {
        _titleLabel.textColor = ColorWithHex(0x444444);

    }

    _titleLabel.font = [UIFont systemFontOfSize:15];
    _titleLabel.numberOfLines = 0;
    _titleLabel.textAlignment = NSTextAlignmentCenter;
   
}

-(void)setText:(NSString *)text
{
    _text = text;
}

-(void)setParentView:(UIView *)parentView
{
     _parentView = parentView;
    
    _titleLabel.text = _text;
    
    CGFloat textHeight = [_text boundingRectWithSize:CGSizeMake(kInfoHudViewWith-30, CGFLOAT_MAX) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:15]} context:nil].size.height;
    
    self.frame = CGRectMake((parentView.frame.size.width-kInfoHudViewWith)*0.5, (parentView.frame.size.height-(textHeight+20))*0.5, kInfoHudViewWith, textHeight+20);

    _titleLabel.frame = CGRectMake(15, 10, self.frame.size.width-30, textHeight);
    
    UIImage *image = [UIImage imageNamed:@"frame2"];
        // 设置端盖的值
    CGFloat top = image.size.height * 0.5;
    CGFloat left = image.size.width * 0.5;
    CGFloat bottom = image.size.height * 0.5;
    CGFloat right = image.size.width * 0.5;
    // 设置端盖的值
    UIEdgeInsets edgeInsets = UIEdgeInsetsMake(top, left, bottom, right);
    // 拉伸图片
    UIImage *newImage = [image resizableImageWithCapInsets:edgeInsets resizingMode:UIImageResizingModeStretch];
    
    
    UIImageView *backImageView = [[UIImageView alloc] init];
//    backImageView.image = newImage;
    backImageView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
    backImageView.backgroundColor = ColorWithHex(0xdddddd);
    backImageView.layer.cornerRadius = 5;
    backImageView.layer.masksToBounds = YES;
    [self addSubview:backImageView];
    
    [self addSubview:_titleLabel];
    
    [parentView addSubview:self];
}



- (void)dissmiss{
 
    for (UIView *subView in self.subviews) {
        [subView removeFromSuperview];
    }
    [self removeFromSuperview];

}

-(void)dealloc
{
    NSLog(@"%s",__func__);
}



@end
