//
//  WeXDCCSelectCrossChainPeriodView.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDCCSelectCrossChainPeriodView.h"
#import "WeXDCCRecordDatePickView.h"

@interface WeXDCCSelectCrossChainPeriodView()
{
    UIButton *_startTimeBtn;
    UIButton *_endTimeBtn;
    
    NSString *_startYearStr;
    NSString *_startMonthStr;
    NSString *_startDayStr;
    
    NSString *_endYearStr;
    NSString *_endMonthStr;
    NSString *_endDayStr;
}

@end

@implementation WeXDCCSelectCrossChainPeriodView

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
    tap.delegate = self;
    [self addGestureRecognizer:tap];
    
}



- (void)tapClick{
    [self endEditing:YES];
    [self dismiss];
}

- (void)setupSubViews{
    
    UIView *backView = [[UIView alloc] init];
    backView.backgroundColor = COLOR_ALPHA_VIEW_COVER;
    [self addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.top.equalTo(self);
        make.bottom.equalTo(self);
    }];
    
    UIView *contentView = [[UIView alloc] init];
    contentView.backgroundColor = [UIColor whiteColor];
    contentView.layer.cornerRadius = 12;
    contentView.layer.masksToBounds = YES;
    [self addSubview:contentView];
    [contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self).offset(15);
        make.trailing.equalTo(self).offset(-15);
        make.centerY.equalTo(self);
        make.height.equalTo(@230);
    }];
    
    UIView *parentView = contentView;
    NSArray *titleArray = @[@"本周",@"本月",@"近两个月"];
    for (int i = 0; i < titleArray.count; i++) {
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeSystem];
        [btn setTitle:titleArray[i] forState:UIControlStateNormal];
        btn.titleLabel.font = [UIFont systemFontOfSize:16];
        [btn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        [btn addTarget:self action:@selector(periodBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        btn.tag = 100+i;
        [self addSubview:btn];
        [btn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.equalTo(contentView).offset(15);
            i == 0?make.top.equalTo(parentView).offset(0): make.top.equalTo(parentView.mas_bottom).offset(0);
            make.trailing.equalTo(contentView).offset(-15);
            make.height.equalTo(@50);
        }];
        
        parentView = btn;
        
        UIView *line = [[UIView alloc] init];
        line.backgroundColor = [UIColor lightGrayColor];
        line.alpha = LINE_VIEW_ALPHA;
        [contentView addSubview:line];
        [line mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.equalTo(contentView).offset(10);
            make.trailing.equalTo(contentView).offset(-10);
            make.top.equalTo(btn.mas_bottom).offset(0);
            make.height.equalTo(@HEIGHT_LINE);
        }];
    }
    
    
    UIButton *confirmBtn = [WeXCustomButton button];
    [confirmBtn setTitle:@"确认" forState:UIControlStateNormal];
    confirmBtn.titleLabel.font = [UIFont systemFontOfSize:16];
    [confirmBtn addTarget:self action:@selector(confirmBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:confirmBtn];
    [confirmBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(@70);
        make.top.equalTo(parentView.mas_bottom).offset(20);
        make.trailing.equalTo(contentView).offset(-10);
        make.height.equalTo(@40);
    }];
    
    UIButton *startTimeBtn = [UIButton buttonWithType:UIButtonTypeSystem];
    startTimeBtn.layer.cornerRadius = 3;
    startTimeBtn.layer.masksToBounds = YES;
    [startTimeBtn setTitle:[self getLastWeekDate] forState:UIControlStateNormal];
    startTimeBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    startTimeBtn.backgroundColor = ColorWithRGB(202, 201, 206);
    [startTimeBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    startTimeBtn.titleLabel.adjustsFontSizeToFitWidth = true;
    [startTimeBtn addTarget:self action:@selector(startTimeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:startTimeBtn];
    [startTimeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(confirmBtn).offset(0);
        make.leading.equalTo(contentView).offset(10);
        make.height.equalTo(confirmBtn);
    }];
    _startTimeBtn = startTimeBtn;
//
    UILabel *ceterLabel = [[UILabel alloc] init];
    ceterLabel.text = @"——";
    ceterLabel.font = [UIFont systemFontOfSize:15];
    ceterLabel.textColor = [UIColor blackColor];
    ceterLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:ceterLabel];
    [ceterLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(startTimeBtn).offset(0);
        make.leading.equalTo(startTimeBtn.mas_trailing).offset(5);
        make.height.equalTo(confirmBtn);
        make.width.equalTo(@10);
    }];
    
    UIButton *endTimeBtn = [UIButton buttonWithType:UIButtonTypeSystem];
    endTimeBtn.layer.cornerRadius = 3;
    endTimeBtn.layer.masksToBounds = YES;
    [endTimeBtn setTitle:[self getTodayDate] forState:UIControlStateNormal];
    endTimeBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    endTimeBtn.backgroundColor = ColorWithRGB(202, 201, 206);
    [endTimeBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    endTimeBtn.titleLabel.adjustsFontSizeToFitWidth = true;
    [endTimeBtn addTarget:self action:@selector(endTimeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:endTimeBtn];
    [endTimeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(confirmBtn).offset(0);
        make.leading.equalTo(ceterLabel.mas_trailing).offset(5);
        make.trailing.equalTo(confirmBtn.mas_leading).offset(-5);
        make.height.equalTo(confirmBtn);
        make.width.equalTo(startTimeBtn);
    }];
    _endTimeBtn = endTimeBtn;
    
    
}

- (void)endTimeBtnClick
{
    WeXDCCRecordDatePickView *periodView = [[WeXDCCRecordDatePickView alloc] initWithFrame:self.bounds];
    periodView.confirmButtonBlock = ^(NSString *year, NSString *month, NSString *day) {
        WEXNSLOG(@"%@-%@-%@",year,month,day);
        NSString *date = [NSString stringWithFormat:@"%@/%@/%@",year,month,day];
        [_endTimeBtn setTitle:date forState:UIControlStateNormal];
        _endYearStr = year;
        _endMonthStr = month;
        _endDayStr = day;
    };
    
    [self addSubview:periodView];
}

- (void)startTimeBtnClick
{
    WeXDCCRecordDatePickView *periodView = [[WeXDCCRecordDatePickView alloc] initWithFrame:self.bounds];
    periodView.confirmButtonBlock = ^(NSString *year, NSString *month, NSString *day) {
        NSString *date = [NSString stringWithFormat:@"%@/%@/%@",year,month,day];
        [_startTimeBtn setTitle:date forState:UIControlStateNormal];
        _startYearStr = year;
        _startMonthStr = month;
        _startDayStr = day;
    };
    
    [self addSubview:periodView];
}


- (void)periodBtnClick:(UIButton *)btn
{
    NSInteger tag = btn.tag - 100;
    if (tag == 0) {
        if (_confirmButtonBlock) {
            NSDate *mondayDate = [self getMondayDate];
            NSString *startTime = [self formatterDate:mondayDate];
            NSString *endTime = [self formatterDate:[NSDate date]];
            NSString *title = @"本周";
            _confirmButtonBlock(startTime,endTime,title);
        }
    }
    else if(tag == 1)
    {
        if (_confirmButtonBlock) {
            NSDate *mondayDate = [self getFirstMonthDate];
            NSString *startTime = [self formatterDate:mondayDate];
            NSString *endTime = [self formatterDate:[NSDate date]];
            NSString *title = @"本月";
            _confirmButtonBlock(startTime,endTime,title);
        }
    }
    else if(tag == 2)
    {
        if (_confirmButtonBlock) {
            NSDate *mondayDate = [self getLastTwoMonthDate];
            NSString *startTime = [self formatterDate:mondayDate];
            NSString *endTime = [self formatterDate:[NSDate date]];
            NSString *title = @"近两个月";
            _confirmButtonBlock(startTime,endTime,title);
        }
    }
    [self dismiss];
}

- (void)confirmBtnClick
{
    if ([self judgeSelectDatePeriodInThreeMonths]) {
        if (_confirmButtonBlock) {
            NSString *startTime = [NSString stringWithFormat:@"%@%@%@",_startYearStr,_startMonthStr,_startDayStr];
            NSString *endTime = [NSString stringWithFormat:@"%@%@%@",_endYearStr,_endMonthStr,_endDayStr];
            NSString *title = [NSString stringWithFormat:@"%@.%@.%@-%@.%@.%@",_startYearStr,_startMonthStr,_startDayStr,_endYearStr,_endMonthStr,_endDayStr];
            _confirmButtonBlock(startTime,endTime,title);
        }
        [self dismiss];
    }
    
}

- (BOOL)judgeSelectDatePeriodInThreeMonths
{
    NSCalendar *calendar = [[NSCalendar alloc]
                            initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    NSDateComponents *endComponent = [[NSDateComponents alloc] init];
    endComponent.year =  [_endYearStr integerValue];
    endComponent.month =  [_endMonthStr integerValue];
    endComponent.day = [_endDayStr integerValue];
    NSDate *endDate = [calendar dateFromComponents:endComponent];
    
    NSDateComponents *lastThreeMonthComponent = [[NSDateComponents alloc] init];
    lastThreeMonthComponent.month = -3;
    NSDate *lastThreeMonthDate = [calendar dateByAddingComponents:lastThreeMonthComponent toDate:endDate options:NSCalendarMatchStrictly];
    
    NSDateComponents *startComponent = [[NSDateComponents alloc] init];
    startComponent.year =  [_startYearStr integerValue];
    startComponent.month =  [_startMonthStr integerValue];
    startComponent.day = [_startDayStr integerValue];
    NSDate *startDate = [calendar dateFromComponents:startComponent];
    WEXNSLOG(@"startDate=%@--lastThreeMonthDate=%@--endDate=%@",startDate,lastThreeMonthDate,endDate);
    
    NSComparisonResult result1 = [startDate compare:endDate];
    if (result1 == NSOrderedDescending) {
        [WeXPorgressHUD showText:@"开始日期不能大于结束日期" onView:self];
        return false;
    }
    
    
    NSComparisonResult result2 = [startDate compare:lastThreeMonthDate];
    if (result2 == NSOrderedAscending) {
        [WeXPorgressHUD showText:@"日期跨度不能超过3个月" onView:self];
        return false;
    }
    return true;
    
}

- (void)dismiss
{
    for (UIView *view in self.subviews) {
        [view removeFromSuperview];
    }
    
    [self removeFromSuperview];
}

-(void)dealloc
{
    WEXNSLOG(@"%s",__func__);
}

- (NSString *)getTodayDate
{
    NSCalendar *calendar = [[NSCalendar alloc]
                            initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    unsigned unitFlags = NSCalendarUnitYear |NSCalendarUnitMonth |  NSCalendarUnitDay | NSCalendarUnitHour | NSCalendarUnitMinute |NSCalendarUnitSecond | NSCalendarUnitWeekday;
    NSDateComponents *comp = [calendar components: unitFlags fromDate:[NSDate date]];
    NSString *todayDate = [NSString stringWithFormat:@"%ld/%02ld/%02ld",(long)comp.year,(long)comp.month,(long)comp.day];
    _endYearStr = [NSString stringWithFormat:@"%ld",(long)comp.year];
    _endMonthStr = [NSString stringWithFormat:@"%02ld",(long)comp.month];
    _endDayStr = [NSString stringWithFormat:@"%02ld",(long)comp.day];
    return todayDate;
}

- (NSString *)getLastWeekDate
{
    NSDate *todayDate = [NSDate date];
    NSCalendar *calendar = [[NSCalendar alloc]
                            initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    unsigned unitFlags = NSCalendarUnitYear |NSCalendarUnitMonth | NSCalendarUnitDay;
    NSDateComponents *component = [[NSDateComponents alloc] init];
    component.day = -7;
    NSDate *lastWeekDate = [calendar dateByAddingComponents:component toDate:todayDate options:NSCalendarMatchStrictly];
     NSDateComponents *comp = [calendar components: unitFlags fromDate:lastWeekDate];
    NSString *lastWeekStr = [NSString stringWithFormat:@"%ld/%02ld/%02ld",(long)comp.year,(long)comp.month,(long)comp.day];
    NSLog(@"lastWeekStr=%@",lastWeekStr);
    _startYearStr = [NSString stringWithFormat:@"%ld",(long)comp.year];
    _startMonthStr = [NSString stringWithFormat:@"%02ld",(long)comp.month];
    _startDayStr = [NSString stringWithFormat:@"%02ld",(long)comp.day];
    return lastWeekStr;
}

- (NSDate *)getLastTwoMonthDate
{
    NSCalendar *calendar = [[NSCalendar alloc]
                            initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    NSDate *nowDate = [NSDate date];
    NSDate *lastTwoMonthDate = [calendar dateByAddingUnit:NSCalendarUnitMonth value:-2 toDate:nowDate options:NSCalendarMatchStrictly];
    return lastTwoMonthDate;
    
}


- (NSDate *)getFirstMonthDate
{
    NSDate *nowDate = [NSDate date];
    NSCalendar *calendar = [[NSCalendar alloc]
                            initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    unsigned unitFlags = NSCalendarUnitYear |
    NSCalendarUnitMonth |  NSCalendarUnitDay |
    NSCalendarUnitHour |  NSCalendarUnitMinute |
    NSCalendarUnitSecond | NSCalendarUnitWeekday;
    NSDateComponents *comp = [calendar components: unitFlags fromDate:nowDate];;
    comp.day = 1;
    comp.hour = 0;
    comp.minute = 0;
    comp.second = 0;
    NSDate *firstDayOfWeek = [calendar dateFromComponents:comp];
    
    return firstDayOfWeek;
    
}



- (NSDate *)getMondayDate
{
    NSDate *nowDate = [NSDate date];
    NSCalendar *calendar = [[NSCalendar alloc]
                            initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    unsigned unitFlags = NSCalendarUnitYear |
    NSCalendarUnitMonth |  NSCalendarUnitDay |
    NSCalendarUnitHour |  NSCalendarUnitMinute |
    NSCalendarUnitSecond | NSCalendarUnitWeekday;
    NSDateComponents *comp = [calendar components: unitFlags fromDate:nowDate];;
    NSInteger weekDay = [comp weekday];
    NSInteger day = [comp day];
    long firstDiff,lastDiff;
    if (weekDay == 1)
    {
        firstDiff = -6;
        lastDiff = 0;
    }
    else
    {
        firstDiff = [calendar firstWeekday] - weekDay + 1;
        lastDiff = 8 - weekDay;
    }
    NSDateComponents *firstDayComp = [calendar components:unitFlags  fromDate:nowDate];
    firstDayComp.day = day + firstDiff;
    firstDayComp.hour = 0;
    firstDayComp.minute = 0;
    firstDayComp.second = 0;
    NSDate *firstDayOfWeek = [calendar dateFromComponents:firstDayComp];
    
    return firstDayOfWeek;
    
}

- (NSString *)formatterDate:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"yyyyMMdd";
    NSString *formatterStr = [formatter stringFromDate:date];
    return formatterStr;
}




    


@end
