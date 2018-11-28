//
//  WeXDailyTaskHeadSignCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/11/1.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDailyTaskHeadSignCell.h"
#import "WeXLocalCacheManager.h"
#import "WeXTaskSignResModel.h"
#import "WeXTaskGetSunBalanceResModel.h"

@interface WeXDailyTaskHeadSignCell ()

@property (nonatomic, strong) UIImageView *subImageView;
@property (nonatomic, strong) UILabel *subLab;
@property (nonatomic, strong) UILabel *subValueLab;
@property (nonatomic, strong) UILabel *signTitleLab;
@property (nonatomic, strong) UILabel *haveSignedLab;
@property (nonatomic, strong) WeXCustomButton *signButton;
@property (nonatomic, strong) UIImageView *signBackImageView;
@property (nonatomic, strong) NSMutableArray <UILabel *> *daysArray;
@property (nonatomic, strong) NSMutableArray <UILabel *> *timesArray;

@end

static CGFloat const kCellHeight = 157;
static CGFloat const kLeftGap  = 7;
static CGFloat const kRightGap = 7;
static CGFloat const kWidth = 40;
static CGFloat const kHeight = 18;
static CGFloat const kCircleW = 22;




@implementation WeXDailyTaskHeadSignCell

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)wex_addSubViews {
    _daysArray  = [NSMutableArray new];
    _timesArray = [NSMutableArray new];
    
    _subImageView = [UIImageView new];
    _subImageView.userInteractionEnabled = true;
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(clickSunImageEvent:)];
    [_subImageView addGestureRecognizer:tapGesture];
    
    _subImageView.image = [UIImage imageNamed:@"Sun"];
    [self.contentView addSubview:_subImageView];
    
    _subLab = CreateLeftAlignmentLabel(WeXPFFont(16), WexDefault4ATitleColor);
    [self.contentView addSubview:_subLab];
    
    _subValueLab = CreateLeftAlignmentLabel(WeXPFFont(16), WexDefault4ATitleColor);
    [self.contentView addSubview:_subValueLab];
    
    _signTitleLab = CreateRightAlignmentLabel(WeXPFFont(13), ColorWithHex(0x9B9B9B));
    [self.contentView addSubview:_signTitleLab];
    
    _haveSignedLab = CreateLeftAlignmentLabel(WeXPFFont(14), ColorWithHex(0xD3A9FF));
    [self.contentView addSubview:_haveSignedLab];
    
    _signButton = [WeXCustomButton createWithType:WeXCustomButtonPurple];
    _signButton.titleLabel.font    = WeXPFFont(14);
    _signButton.layer.cornerRadius = 12.5;
    _signButton.layer.borderWidth  = 0;
    [_signButton addTarget:self action:@selector(signEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.contentView addSubview:_signButton];
    
    _signBackImageView = [UIImageView new];
    _signBackImageView.image = [WeXLocalCacheManager getFindLocalImageWithType:WexFindTaskTypeSignBackground];
    [self.contentView addSubview:_signBackImageView];
    
    [self initForLoopUI];
}

- (void)wex_addConstraints {
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
        make.height.mas_equalTo(kCellHeight).priorityHigh();
    }];
    
    [self.subImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.mas_equalTo(28);
        make.size.mas_equalTo(CGSizeMake(39, 39));
    }];
    
    [self.subLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.subImageView).offset(-5);
        make.left.equalTo(self.subImageView.mas_right).offset(16);
    }];
    [self.subValueLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.subImageView).offset(5);
        make.left.equalTo(self.subLab);
    }];
    [self.signButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(43);
        make.right.mas_equalTo(-14);
        make.size.mas_equalTo(CGSizeMake(65, 25));
    }];
    
    [self.signTitleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.subLab).offset(2);
        make.right.equalTo(self.signButton.mas_left).offset(-20);
    }];
    [self.haveSignedLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.signTitleLab.mas_bottom).offset(5);
        make.left.equalTo(self.signTitleLab);
    }];
    [self.signBackImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(14);
        make.right.mas_equalTo(-14);
        make.bottom.mas_equalTo(-14);
        make.height.mas_equalTo(61);
    }];
}

- (void)clickSunImageEvent:(UITapGestureRecognizer *)gesture {
    !self.ClickSunBalance ? : self.ClickSunBalance();
}

- (void)initForLoopUI {
    CGFloat gap = (kScreenWidth - 14 * 2 - kLeftGap - kRightGap - [LoopCounts() count] * kWidth  ) / ([LoopCounts() count] - 1);
    for (int i =0 ; i < [LoopCounts() count]; i ++) {
        UILabel *dayLab = CreateLeftAlignmentLabel(WeXPFFont(13), [UIColor whiteColor]);
        if (i == [LoopCounts() count] - 1) {
            dayLab.textAlignment = NSTextAlignmentRight;
        }
        dayLab.frame = CGRectMake((gap + kWidth ) * i + kLeftGap, kLeftGap, kWidth, kHeight);
        [dayLab setText:LoopCounts()[i]];
        [self.signBackImageView addSubview:dayLab];
        [_daysArray addObject:dayLab];
        
        UILabel *signLab = CreateCenterAlignmentLabel(WeXPFFont(10), [UIColor whiteColor]);
        signLab.bounds   = CGRectMake(0, 0, kCircleW, kCircleW);
        if (i < [LoopCounts() count] - 1 ) {
            signLab.center   = CGPointMake(dayLab.center.x - 2, dayLab.center.y + 28);
        } else {
            signLab.center   = CGPointMake(dayLab.center.x + 2, dayLab.center.y + 28);
        }
        signLab.layer.cornerRadius = kCircleW /2;
        signLab.clipsToBounds = true;
        [_timesArray addObject:signLab];
    }
    UILabel *firstSignLab = _timesArray.firstObject;
    UILabel *lastSignLab  = _timesArray.lastObject;
    UIView  *sepratorLine = [UIView new];
    [sepratorLine setBackgroundColor:ColorWithRGBA(0, 0, 0, 0.5)];
    CGFloat width = CGRectGetMinX(lastSignLab.frame) - CGRectGetMaxX(firstSignLab.frame);
    sepratorLine.frame = CGRectMake(CGRectGetMaxX(firstSignLab.frame), firstSignLab.center.y - 1, width, 2);
    [self.signBackImageView addSubview:sepratorLine];
    
}

- (void)signEvent:(UIButton *)sender {
    !self.SignEvent ? : self.SignEvent();
}

- (void)setTaskSignResModel:(WeXTaskSignResModel *)resModel
               balanceModel:(WeXTaskGetSunBalanceResModel *)balancemodel
                todayStatus:(NSInteger)stauts {
    [self.subLab       setText:@"阳光"];
    [self.subValueLab  setText:balancemodel.balance];
    NSInteger signNumber = [[resModel result] count];
    [self.signTitleLab setText:@"七天累计签到"];
    NSString *prefixString = @"已签到";
    NSString *dayString    = [NSString stringWithFormat:@"%@%@",@(signNumber),@"天"];
    NSString *totalString  = [NSString stringWithFormat:@"%@%@",prefixString,dayString];
    NSMutableAttributedString *attributeString = [[NSMutableAttributedString alloc] initWithString:totalString];
    NSRange range = [totalString rangeOfString:dayString];
    NSDictionary *attributes = @{NSForegroundColorAttributeName:ColorWithHex(0x7B40FF)};
    [attributeString addAttributes:attributes range:range];
    [self.haveSignedLab setAttributedText:attributeString];
    NSString *signTitle = @"签到";
    UIControlState state = UIControlStateNormal;
    [self.signButton setEnabled:true];
    if (stauts == 1) {
        signTitle = @"已签到";
        state = UIControlStateDisabled;
        [self.signButton setEnabled:false];
    }
    [self.signButton setTitle:signTitle forState:state];
    [_timesArray enumerateObjectsUsingBlock:^(UILabel * obj, NSUInteger idx, BOOL *  stop) {
        if (idx + 1 <= signNumber) {
            [obj setBackgroundColor:[UIColor whiteColor]];
            [obj setTextColor:ColorWithHex(0xFC318C)];
        } else {
            [obj setBackgroundColor:ColorWithRGB(201, 85, 188)];
            [obj setTextColor:ColorWithRGBA(255, 255, 255, 0.4)];
        }
        [obj setText:[NSString stringWithFormat:@"+%@",ScoreArray ()[idx]]];
        [self.signBackImageView addSubview:obj];
    }];
}

- (void)setHaveSignDays:(NSInteger)signNumber
            todayStatus:(NSInteger)stauts {
    [self.subLab       setText:@"阳光"];
    [self.subValueLab  setText:@"1234"];
    [self.signTitleLab setText:@"七天累计签到"];
    NSString *prefixString = @"已签到";
    NSString *dayString    = [NSString stringWithFormat:@"%@%@",@(signNumber),@"天"];
    NSString *totalString  = [NSString stringWithFormat:@"%@%@",prefixString,dayString];
    NSMutableAttributedString *attributeString = [[NSMutableAttributedString alloc] initWithString:totalString];
    NSRange range = [totalString rangeOfString:dayString];
    NSDictionary *attributes = @{NSForegroundColorAttributeName:ColorWithHex(0x7B40FF)};
    [attributeString addAttributes:attributes range:range];
    [self.haveSignedLab setAttributedText:attributeString];
    NSString *signTitle = @"签到";
    UIControlState state = UIControlStateNormal;
    [self.signButton setEnabled:true];
    if (stauts == 1) {
        signTitle = @"已签到";
        state = UIControlStateDisabled;
        [self.signButton setEnabled:false];
    }
    [self.signButton setTitle:signTitle forState:state];
    [_timesArray enumerateObjectsUsingBlock:^(UILabel * obj, NSUInteger idx, BOOL *  stop) {
        if (idx + 1 <= signNumber) {
            [obj setBackgroundColor:[UIColor whiteColor]];
            [obj setTextColor:ColorWithHex(0xFC318C)];
        } else {
            [obj setBackgroundColor:ColorWithRGB(201, 85, 188)];
            [obj setTextColor:ColorWithRGBA(255, 255, 255, 0.4)];
        }
        [obj setText:[NSString stringWithFormat:@"+%ld",idx + 1]];
        [self.signBackImageView addSubview:obj];
    }];
}

static NSArray <NSString *> * LoopCounts () {
    return @[@"第1天",@"第2天",@"第3天",@"第4天",@"第5天",@"第6天",@"第7天"];
}

static NSArray <NSString *> * ScoreArray () { 
    return @[@"10",@"15",@"20",@"50",@"60",@"70",@"90"];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
