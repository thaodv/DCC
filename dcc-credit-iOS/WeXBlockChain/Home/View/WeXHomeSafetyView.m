//
//  WeXHomeSafetyView.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/16.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXHomeSafetyView.h"
#import "WeXGestureSafetyView.h"
#import "WeXNumberSafetyView.h"
#import <LocalAuthentication/LocalAuthentication.h>
#import "WeXHomeSafetyCell.h"

@interface WeXHomeSafetyView()<UITableViewDelegate,UITableViewDataSource,WeXGestureSafetyViewDelegate,WeXNumberSafetyViewDelegate>
{
    UITableView *_tableView;
    LAContext *_context;
    
    WeXGestureSafetyView *_gestureView;
    WeXNumberSafetyView *_numberView;
    
    UIButton *_skipBtn;
}

@end

@implementation WeXHomeSafetyView

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
    self.backgroundColor = [UIColor whiteColor];
    //创建LAContext
    _context = [[LAContext alloc] init];
    //是否支持指纹支付
    if ([_context canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:nil]){
        if (@available(iOS 11.0, *))
        {
            if (_context.biometryType == LABiometryTypeFaceID)
            {
                _dataArray = @[WeXLocalizedString(@"手势密码"),WeXLocalizedString(@"数字密码"),WeXLocalizedString(@"人像识别")];
            }
            else
            {
                _dataArray = @[WeXLocalizedString(@"手势密码"),WeXLocalizedString(@"数字密码"),WeXLocalizedString(@"指纹识别")];
            }
        }
        else
        {
           _dataArray = @[WeXLocalizedString(@"手势密码"),WeXLocalizedString(@"数字密码"),WeXLocalizedString(@"指纹识别")];
        }
    }
    else
    {
        _dataArray = @[WeXLocalizedString(@"手势密码"),WeXLocalizedString(@"数字密码")];
    }
}



- (void)setupSubViews{
    
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = WeXLocalizedString(@"选择验证方式");
    titleLabel.font = [UIFont systemFontOfSize:20];
    titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    titleLabel.textAlignment = NSTextAlignmentCenter;
    [self addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self);
        make.leading.equalTo(self);
        make.trailing.equalTo(self);
        make.height.equalTo(@50);
    }];
    
    UIButton *skipBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [skipBtn setTitle:WeXLocalizedString(@"跳过") forState:UIControlStateNormal];
    [skipBtn setTitleColor:COLOR_THEME_ALL forState:UIControlStateNormal];
    [skipBtn addTarget:self action:@selector(skipBtnClick) forControlEvents:UIControlEventTouchUpInside];
    skipBtn.titleLabel.font = [UIFont systemFontOfSize:16];
    [self addSubview:skipBtn];
    [skipBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(titleLabel);
        make.trailing.equalTo(self).offset(-10);
        make.size.mas_equalTo(CGSizeMake(50, 50));
    }];
    _skipBtn = skipBtn;
    
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = [UIColor lightGrayColor];
    line.alpha = LINE_VIEW_ALPHA;
    [self addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.trailing.equalTo(self);
        make.top.equalTo(titleLabel.mas_bottom);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    _tableView = [[UITableView alloc] init];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.scrollEnabled = NO;
    _tableView.tableFooterView = [UIView new];
    _tableView.rowHeight = 50;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self addSubview:_tableView];
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(line.mas_bottom);
        make.leading.trailing.equalTo(self);
        make.bottom.equalTo(self);
    }];
    
}

-(void)setSkipName:(NSString *)skipName
{
    [_skipBtn setTitle:skipName forState:UIControlStateNormal];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _dataArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellID = @"cellID";
    WeXHomeSafetyCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXHomeSafetyCell" owner:self options:nil] lastObject];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    cell.textLabel.text = [_dataArray objectAtIndex:indexPath.row];
    cell.textLabel.textAlignment = NSTextAlignmentCenter;
    cell.textLabel.font = [UIFont systemFontOfSize:18];
    cell.textLabel.textColor = COLOR_THEME_ALL;
//    UIView *backView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(cell.frame), CGRectGetHeight(cell.frame))];
//    backView.backgroundColor = ColorWithRGB(250, 31, 118);
//    cell.selectedBackgroundView = backView;
    cell.textLabel.highlightedTextColor = COLOR_THEME_ALL;
    return cell;
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        _gestureView = [[WeXGestureSafetyView alloc] initWithFrame:CGRectMake(self.frame.size.width, 0, self.frame.size.width, self.frame.size.height)];
        _gestureView.type = WeXGesturePswTypeCreate;
        _gestureView.delegate = self;
        [self addSubview:_gestureView];
        
        [UIView animateWithDuration:0.5 animations:^{
            _gestureView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
        }];
    }
    else if(indexPath.row == 1)
    {
        _numberView = [[WeXNumberSafetyView alloc] initWithFrame:CGRectMake(self.frame.size.width, 0, self.frame.size.width, self.frame.size.height)];
        _numberView.delegate = self;
        _numberView.type = WeXNumberPasswordTypeCreate;
        [self addSubview:_numberView];
        
        [UIView animateWithDuration:0.5 animations:^{
            _numberView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
        }];
    }
    else if (indexPath.row == 2)
    {
        //创建LAContext
        _context = [[LAContext alloc] init];
        //这个属性是设置指纹输入失败之后的弹出框的选项
        _context.localizedFallbackTitle = @"";
        NSError *error = nil;
        NSString *localizedReason = WeXLocalizedString(@"指纹解锁");
        if (@available(iOS 11.0, *))
        {
            if (_context.biometryType == LABiometryTypeFaceID)
            {
                localizedReason = WeXLocalizedString(@"FaceID登录");
            }
        }
        if ([_context canEvaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics error:&error]) {
            NSLog(@"%@",WeXLocalizedString(@"支持指纹识别"));
            [_context evaluatePolicy:LAPolicyDeviceOwnerAuthenticationWithBiometrics localizedReason:localizedReason reply:^(BOOL success, NSError * _Nullable error) {
                if (success) {
                    NSLog(@"%@",WeXLocalizedString(@"验证成功 刷新主界面"));
                    dispatch_async(dispatch_get_main_queue(), ^{
                        //保存手势进passport
                        WeXPasswordCacheModal *model = [WexCommonFunc getPassport];
                        if (model) {
                            model.passwordType = WeXPasswordTypeTouchID;
                        }
                        [WexCommonFunc savePassport:model];
                        if ([self.delegate respondsToSelector:@selector(safetyViewDidSetPassword)]) {
                            [self.delegate safetyViewDidSetPassword];
                        }
                    });
                 
                }else{
                    NSLog(@"%@",error.localizedDescription);
                    switch (error.code) {
                        case LAErrorSystemCancel:
                        {
                            NSLog(@"%@",WeXLocalizedString(@"系统取消授权，如其他APP切入"));
                            break;
                        }
                        case LAErrorUserCancel:
                        {
                            NSLog(@"%@",WeXLocalizedString(@"用户取消验证Touch ID"));
                            break;
                        }
                        case LAErrorAuthenticationFailed:
                        {
                            NSLog(@"%@",WeXLocalizedString(@"授权失败"));
                            break;
                        }
                        case LAErrorPasscodeNotSet:
                        {
                            NSLog(@"%@",WeXLocalizedString(@"系统未设置密码"));
                            break;
                        }
                        case LAErrorTouchIDNotAvailable:
                        {
                            NSLog(@"%@",WeXLocalizedString(@"设备Touch ID不可用，例如未打开"));
                            break;
                        }
                        case LAErrorTouchIDNotEnrolled:
                        {
                            NSLog(@"%@",WeXLocalizedString(@"设备Touch ID不可用，用户未录入"));
                            break;
                        }
                        case LAErrorUserFallback:
                        {
                            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                                NSLog(@"%@",WeXLocalizedString(@"用户选择输入密码，切换主线程处理"));
                            }];
                            break;
                        }
                        default:
                        {
                            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                                NSLog(@"%@",WeXLocalizedString(@"其他情况，切换主线程处理"));
                            }];
                            break;
                        }
                    }
                }
            }];
        }else{
            NSLog(@"%@",WeXLocalizedString(@"不支持指纹识别"));
            switch (error.code) {
                case LAErrorTouchIDNotEnrolled:
                {
                    NSLog(@"TouchID is not enrolled");
                    break;
                }
                case LAErrorPasscodeNotSet:
                {
                    NSLog(@"A passcode has not been set");
                    break;
                }
                default:
                {
                    NSLog(@"TouchID not available");
                    break;
                }
            }
            
            NSLog(@"%@",error.localizedDescription);
        }
        
    }
  
}


- (void)skipBtnClick{
    if (self.skipBtnBlock) {
        self.skipBtnBlock();
    }
}

- (void)dismiss{
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        for (UIView *view in self.subviews) {
            [view removeFromSuperview];
        }
        
        [self removeFromSuperview];
    });
    
}

#pragma mark - WeXGestureSafetyViewDelegate
- (void)gestureSafetyViewDidSetGesturePassword{
    if ([self.delegate respondsToSelector:@selector(safetyViewDidSetPassword)]) {
        [self.delegate safetyViewDidSetPassword];
    }
}

- (void)gestureSafetyViewCancel
{
    [UIView animateWithDuration:0.5 animations:^{
        _gestureView.frame = CGRectMake(self.frame.size.width, 0, self.frame.size.width, self.frame.size.height);
    }];
    [_gestureView dismiss];
    
}

#pragma mark - WeXGestureSafetyViewDelegate
- (void)numberSafetyViewDidSetNumberPassword{
    if ([self.delegate respondsToSelector:@selector(safetyViewDidSetPassword)]) {
        [self.delegate safetyViewDidSetPassword];
    }
    
}

-(void)numberSafetyViewCancel
{
    [UIView animateWithDuration:0.5 animations:^{
        _numberView.frame = CGRectMake(self.frame.size.width, 0, self.frame.size.width, self.frame.size.height);
    }];
    
    [_numberView dismiss];
}

-(void)dealloc{
    NSLog(@"%s",__func__);
}


@end
