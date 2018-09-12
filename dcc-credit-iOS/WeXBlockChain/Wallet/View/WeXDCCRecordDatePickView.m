//
//  WeXDCCRecordDatePickView.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/23.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDCCRecordDatePickView.h"

@interface WeXDCCRecordDatePickView()<UIPickerViewDelegate,UIPickerViewDataSource>
{
    NSInteger _yearIndex;
    
    NSInteger _monthIndex;
    
    NSInteger _dayIndex;
    
}

@end

@implementation WeXDCCRecordDatePickView

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
        make.leading.equalTo(self).offset(20);
        make.trailing.equalTo(self).offset(-20);
        make.centerY.equalTo(self);
        make.height.equalTo(@300);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"请选择";
    titleLabel.font = [UIFont systemFontOfSize:18];
    titleLabel.textColor = [UIColor blackColor];
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [contentView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(contentView).offset(0);
        make.centerX.equalTo(contentView).offset(0);
        make.height.equalTo(@40);
    }];
    
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = [UIColor lightGrayColor];
    line1.alpha = LINE_VIEW_ALPHA;
    [contentView addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
        make.top.equalTo(titleLabel.mas_bottom).offset(0);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    
    UIButton *cancelBtn = [WeXCustomButton button];
    [cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:cancelBtn];
    [cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(contentView).offset(-15);
        make.leading.equalTo(contentView).offset(20);
        make.height.equalTo(@40);
    }];
    
    UIButton *confirmBtn = [WeXCustomButton button];
    [confirmBtn setTitle:@"确定" forState:UIControlStateNormal];
    [confirmBtn addTarget:self action:@selector(confirmBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:confirmBtn];
    [confirmBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(cancelBtn);
        make.trailing.equalTo(contentView).offset(-20);
        make.leading.equalTo(cancelBtn.mas_trailing).offset(15);
        make.height.equalTo(cancelBtn);
        make.width.equalTo(cancelBtn);
    }];
    
    UIPickerView *pickView = [[UIPickerView alloc] init];
    pickView.delegate = self;
    pickView.dataSource = self;
    pickView.showsSelectionIndicator = true;
    [self addSubview:pickView];
    [pickView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(confirmBtn.mas_top).offset(-5);
        make.top.equalTo(line1.mas_bottom).offset(5);
        make.leading.equalTo(contentView).offset(10);
        make.trailing.equalTo(contentView).offset(-10);
    }];
    
    NSCalendar *calendar = [[NSCalendar alloc]
                            initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    // 定义一个时间字段的旗标，指定将会获取指定年、月、日、时、分、秒的信息
    unsigned unitFlags = NSCalendarUnitYear |
    NSCalendarUnitMonth |  NSCalendarUnitDay |
    NSCalendarUnitHour |  NSCalendarUnitMinute |
    NSCalendarUnitSecond | NSCalendarUnitWeekday;
    // 获取不同时间字段的信息
    NSDateComponents *comp = [calendar components: unitFlags fromDate:[NSDate date]];
    
    _yearIndex = [self.yearArray indexOfObject:[NSString stringWithFormat:@"%ld", comp.year]];
    _monthIndex = [self.monthArray indexOfObject:[NSString stringWithFormat:@"%02ld", comp.month]];
    _dayIndex = [self.dayArray indexOfObject:[NSString stringWithFormat:@"%02ld", comp.day]];
    
    [pickView selectRow:_yearIndex inComponent:0 animated:YES];
    [pickView selectRow:_monthIndex inComponent:1 animated:YES];
    [pickView selectRow:_dayIndex inComponent:2 animated:YES];
    
}

- (NSMutableArray *)yearArray {
    
    if (_yearArray == nil) {
        
        _yearArray = [NSMutableArray array];
        
        for (int year = 2017; year < 2020; year++) {
            
            NSString *str = [NSString stringWithFormat:@"%d", year];
            
            [_yearArray addObject:str];
        }
    }
    
    return _yearArray;
}

- (NSMutableArray *)monthArray {
    
    if (_monthArray == nil) {
        
        _monthArray = [NSMutableArray array];
        
        for (int month = 1; month <= 12; month++) {
            
            NSString *str = [NSString stringWithFormat:@"%02d", month];
            
            [_monthArray addObject:str];
        }
    }
    
    return _monthArray;
}

- (NSMutableArray *)dayArray {
    
    if (_dayArray == nil) {
        
        _dayArray = [NSMutableArray array];
        
        for (int day = 1; day <= 31; day++) {
            
            NSString *str = [NSString stringWithFormat:@"%02d", day];
            
            [_dayArray addObject:str];
        }
    }
    
    return _dayArray;
}


- (void)confirmBtnClick
{
    if (_confirmButtonBlock) {
        _confirmButtonBlock(_yearArray[_yearIndex],_monthArray[_monthIndex],_dayArray[_dayIndex]);
    }
    [self dismiss];
}

- (void)cancelBtnClick
{
    [self dismiss];
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
    NSLog(@"%s",__func__);
}

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 3;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    if (component == 0) {
        return self.yearArray.count;
        
    }else if(component == 1) {
        
        return self.monthArray.count;
        
    }else {
        
        switch (_monthIndex + 1) {
            
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12: return 31;
            
            case 4:
            case 6:
            case 9:
            case 11: return 30;
            
            default: return 28;
        }
    }
}

// returns width of column and height of row for each component.
- (CGFloat)pickerView:(UIPickerView *)pickerView widthForComponent:(NSInteger)component __TVOS_PROHIBITED
{
    return (kScreenWidth-60)/3;
}
- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component __TVOS_PROHIBITED
{
    return 35;
}

-(UIView *)pickerView:(UIPickerView *)pickerView viewForRow:(NSInteger)row forComponent:(NSInteger)component reusingView:(UIView *)view
{
    //设置文字的属性
    UILabel *genderLabel = [[UILabel alloc] init];
    genderLabel.textAlignment = NSTextAlignmentCenter;
    genderLabel.textColor = ColorWithRGB(153, 153, 153);
    genderLabel.font = [UIFont systemFontOfSize:15];
    if (component == 0) {
        
        genderLabel.text = self.yearArray[row];
        
    }else if (component == 1) {
        
        genderLabel.text = self.monthArray[row];
        
    }else {
        
        genderLabel.text = self.dayArray[row];
    }
    
    return genderLabel;
}

-(void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    if (component == 0) {
        
        _yearIndex = row;
        
    }else if (component == 1) {
        
        _monthIndex = row;
        
        [pickerView reloadComponent:2];
        
        
        if (_monthIndex + 1 == 4 || _monthIndex + 1 == 6 || _monthIndex + 1 == 9 || _monthIndex + 1 == 11) {
            
            if (_dayIndex + 1 == 31) {
                
                _dayIndex--;
            }
        }else if (_monthIndex + 1 == 2) {
            
            if (_dayIndex + 1 > 28) {
                _dayIndex = 27;
            }
        }
        [pickerView selectRow:_dayIndex inComponent:2 animated:YES];
        
    }else {
        
        _dayIndex = row;
    }
}


@end
