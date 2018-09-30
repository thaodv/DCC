//
//  WeXCoinProfitCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/13.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCoinProfitCell.h"
#import "WeXLeftIconRightLabelView.h"

static NSInteger const kStartTag = 10;
static CGFloat   const kCellHeight = 75;


@interface WeXCoinProfitCell ()

@property (nonatomic, strong) NSArray <NSString *> *titles;
@property (nonatomic, strong) NSArray <NSString *> *images;
@property (nonatomic, strong) NSMutableArray <NSValue *> *frames;

@property (nonatomic, weak)  WeXLeftIconRightLabelView *candyView;
@property (nonatomic, weak)  WeXLeftIconRightLabelView *creditView;


@end

@implementation WeXCoinProfitCell
- (void)wex_addSubViews {
    _titles = @[@"糖果领取",@"我的信用",@"信用借币",@"借贷报告",@"币生息"];
    _images = @[@"Wex_Coin_Candy",@"Wex_Coin_Credit",@"Wex_Coin_Loan",@"Wex_Coin_Report",@"Wex_Coin_Profit"];
    if (!_frames) {
        _frames = [[NSMutableArray alloc] init];
    }
    [self.contentView.subviews enumerateObjectsUsingBlock:^( UIView *  obj, NSUInteger idx, BOOL *  stop) {
        if ([obj isKindOfClass:[WeXCoinProfitView class]] && obj.tag >= kStartTag) {
            [obj removeFromSuperview];
            obj = nil;
        }
    }];
    
    WeXLeftIconRightLabelView *candyView = [WeXLeftIconRightLabelView new];
    candyView.tag = kStartTag + 5;
    candyView.DidClickView = ^{
        !self.DidClickCell ? : self.DidClickCell(WexCoinProfitCellTypeCandy);
    };
    [candyView setLeftImageName:@"dcc_activate_new_logo" title:WeXLocalizedString(@"糖果领取")];
    [candyView.backView setBackgroundColor:ColorWithRGB(240, 74, 148)];
    [self.contentView addSubview:candyView];
    self.candyView = candyView;
    
    WeXLeftIconRightLabelView *creditView = [WeXLeftIconRightLabelView new];
    creditView.tag = kStartTag + 6;
    creditView.DidClickView = ^{
        !self.DidClickCell ? : self.DidClickCell(WexCoinProfitCellTypeCrdit);
    };
    [creditView setLeftImageName:@"dcc_credit_new_logo" title:WeXLocalizedString(@"我的信用")];
    [creditView.backView setBackgroundColor:ColorWithRGB(82, 129, 238)];
    [self.contentView addSubview:creditView];
    self.creditView = creditView;
    
    [self.candyView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.height.mas_equalTo(60);
        make.left.mas_equalTo(0);
        make.right.equalTo(self.contentView.mas_centerX);
    }];
    
    [self.creditView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(0);
        make.height.mas_equalTo(60);
        make.left.equalTo(self.candyView.mas_right);
        make.right.mas_equalTo(0);
    }];
    
    CGFloat leftX = 10;
    CGFloat topY  = 10;
    CGFloat viewW = 55;
    CGFloat gap =  (kScreenWidth - 2 * leftX - _titles.count * viewW ) / 4.0;
    [_titles enumerateObjectsUsingBlock:^(NSString *  obj, NSUInteger idx, BOOL *  stop) {
        CGRect frame = CGRectMake(leftX + (gap + viewW) * idx, topY, viewW, viewW);
        [_frames addObject:[NSValue valueWithCGRect:frame]];
        WeXCoinProfitView *profitView = [[WeXCoinProfitView alloc] initWithFrame:frame];
        [profitView setHidden:true];
        profitView.tag = kStartTag + idx;
        [profitView setIconImage:_images[idx] title:_titles[idx]];
        UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clickViewEvent:)];
        [profitView addGestureRecognizer:tapGesture];
        [self.contentView addSubview:profitView];
    }];
}
- (void)clickViewEvent:(UITapGestureRecognizer *)gesture {
    UIEvent *event    = [[UIEvent alloc] init];
    CGPoint location  = [gesture locationInView:gesture.view];
    UIView *touchView = [gesture.view hitTest:location withEvent:event];
    if ([touchView isKindOfClass:[WeXCoinProfitView class]] ||
        [touchView isKindOfClass:[WeXLeftIconRightLabelView class]]) {
        !self.DidClickCell ? : self.DidClickCell(touchView.tag - kStartTag);
    }
}
- (void)setIsAllowAppear:(BOOL)isAppear {
    [self.contentView.subviews enumerateObjectsUsingBlock:^( UIView *  obj, NSUInteger idx, BOOL *  stop) {
        if ([obj isKindOfClass:[WeXCoinProfitView class]] && obj.tag >= kStartTag) {
            //非审核状态
            [obj setHidden:!isAppear];
        } else if ([obj isKindOfClass:[WeXLeftIconRightLabelView class]] && obj.tag >= kStartTag) {
            //审核状态
            [obj setHidden:isAppear];
        }
    }];
}

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}
+ (CGFloat)cellHeight {
    return kCellHeight;
}

@end

@interface WeXCoinProfitView ()

@property (nonatomic, weak) UIImageView *iconImage;
@property (nonatomic, weak) UILabel *titleLab;

@end

@implementation WeXCoinProfitView

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self wex_addsubviews];
        [self wex_addConstraints];
    }
    return self;
}
- (void)wex_addsubviews {
    UIImageView *iconImage = [UIImageView new];
    [self addSubview:iconImage];
    self.iconImage = iconImage;
    
    UILabel *titleLab = [UILabel new];
    titleLab.textAlignment = NSTextAlignmentCenter;
    titleLab.font = [UIFont systemFontOfSize:11.0f];
    titleLab.textColor = ColorWithHex(0x4A4A4A);
    [self addSubview:titleLab];
    self.titleLab = titleLab;
}
- (void)wex_addConstraints {
    [self.iconImage mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(0);
        make.centerX.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(36, 36));
    }];
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.iconImage.mas_bottom).offset(6);
        make.centerX.mas_equalTo(0);
    }];
}
- (void)setIconImage:(NSString *)image
               title:(NSString *)title {
    [self.iconImage setImage:[UIImage imageNamed:image]];
    [self.titleLab setText:title];
}

@end


