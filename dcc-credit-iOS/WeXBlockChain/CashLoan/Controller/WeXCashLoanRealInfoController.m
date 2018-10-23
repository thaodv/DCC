//
//  WeXCashLoanRealInfoController.m
//  NealTestDemo
//
//  Created by Kikpop on 2018/10/12.
//  Copyright © 2018 weihuahu. All rights reserved.
//

#import "WeXCashLoanRealInfoController.h"
#import "WeXCashLoanPerfectInfoCell.h"
#import <IQKeyboardManager/IQKeyboardManager.h>
#import "WexCashLoanRealInfoPCTModel.h"
#import "WexCashLoanRealInfoFormModel.h"
#import "NSString+WexTool.h"
#import "WeXPasswordCacheModal.h"
#import "UIAlertController+HCAdd.h"
#import "UIScrollView+WeXScroll.h"

static NSString *const kPerfectInfoLeftKey     = @"kCashLoanPerfectInfoLeftKey"; // 标记左侧内容
static NSString *const kPerfectInfoMidKey      = @"kCashLoanPerfectInfoMidKey";  // 标记中间内容
static NSString *const kPerfectInfoRightKey    = @"kCashLoanPerfectInfoRightKey"; // 标记右侧内容
static NSString *const WeXCashLoanPerfectInfoLabelCellID = @"WeXCashLoanPerfectInfoLabelCellIdentifier";

@interface WeXCashLoanRealInfoController ()

@property (nonatomic, strong) NSMutableArray *sectionTitleArray;
@property (nonatomic, strong) UIButton *completeBtn;

@property (nonatomic, assign) NSInteger scrollToRowIdx;
@property (nonatomic, assign) NSInteger scrollToSectionIdx;

@property (nonatomic, assign) BOOL isNeedBecomeFirstResponder;

@property (nonatomic, copy) NSString *linkmanOneStr;
@property (nonatomic, copy) NSString *linkmanTwoStr;
@property (nonatomic, copy) NSString *linkmanThreeStr;

@end

@implementation WeXCashLoanRealInfoController
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [[IQKeyboardManager sharedManager] setEnable:YES];
    [[IQKeyboardManager sharedManager] setEnableAutoToolbar:YES];
    [IQKeyboardManager sharedManager].keyboardDistanceFromTextField = 50.f;
}

- (void)viewWillDisappear:(BOOL)animated{
    [[IQKeyboardManager sharedManager] setEnable:NO];
    [[IQKeyboardManager sharedManager] setEnableAutoToolbar:NO];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initGsRows];
    [self initData];
    [self.completeBtn addTarget:self action:@selector(completeClick) forControlEvents:UIControlEventTouchUpInside];
#ifdef DEBUG
    UIBarButtonItem *item = [[UIBarButtonItem alloc]initWithTitle:@"清空数据" style:UIBarButtonItemStylePlain target:self action:@selector(removeAllData)];
    self.navigationItem.rightBarButtonItem = item;
#endif
}

- (void)removeAllData{
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:WeXCashLoanRealInfoKey];
    [[NSUserDefaults standardUserDefaults] synchronize];
    WeXSinger.model = nil;
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)initGsRows{
    [self.form addSection:[self HomeInfoSection]];
    [self.form addSection:[self locationSection]];
    [self.form addSection:[self loanUseSection]];
    [self.form addSection:[self workInfoSection]];
    [self.form addSection:[self companyLocationSection]];
    [self.form addSection:[self linkmanInfoSection]];
}

- (void)initData{
    NSUserDefaults * defaults = [NSUserDefaults standardUserDefaults];
    NSData * encodeObject = [defaults objectForKey:WeXCashLoanRealInfoKey];
    if (encodeObject == nil) {
        if (!WeXSinger.model) {
            WeXSinger.model = [[WexCashLoanRealInfoFormModel alloc]init];
        }else{
            [self.form reformRespRet:WeXSinger.model];
            if ([NSThread isMainThread]) {
                [self.tableView reloadData];
            }else{
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    [self.tableView reloadData];
                });
            }
        }
        return;
    }else{
        id obj = [NSKeyedUnarchiver unarchiveObjectWithData:encodeObject];
        if ([obj isKindOfClass:[WexCashLoanRealInfoFormModel class]]) {
            WeXSinger.model = obj;
            [self.form reformRespRet:WeXSinger.model];
            if ([NSThread isMainThread]) {
                [self.tableView reloadData];
            }else{
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    [self.tableView reloadData];
                });
            }
        }
    }
}

#pragma mark --
#pragma mark -- 提交 --
- (void)completeClick{
    NSDictionary *dic = [self.form validateRows];
    __weak typeof(self)weakSelf = self;
    if (![dic[kValidateRetKey] boolValue]) {
        if (self.isNeedBecomeFirstResponder) {
            self.isNeedBecomeFirstResponder = NO;
            NSIndexPath *indexPath = [NSIndexPath indexPathForRow:weakSelf.scrollToRowIdx inSection:weakSelf.scrollToSectionIdx];
            if (weakSelf.scrollToSectionIdx == 5) {
                [weakSelf.tableView scrollToBottom];
            }else{
                [weakSelf.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionNone animated:YES];
            }
            __block WeXCashLoanPerfectInfoTextViewCell *cell = [weakSelf.tableView cellForRowAtIndexPath:indexPath];
            if (cell.textField) {
                [cell.textField becomeFirstResponder];
            }
        }else{
            __weak typeof(self)ws = self;
            [UIAlertController showAlertViewWithTitle:nil Message:dic[kValidateMsgKey] BtnTitles:@[@"知道了"] ClickBtn:^(NSInteger index) {
                NSIndexPath *indexPath = [NSIndexPath indexPathForRow:ws.scrollToRowIdx inSection:ws.scrollToSectionIdx];
                [ws.tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionNone animated:YES];
            }];
        }
    } else {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            if (@available(iOS 11.0, *)) {
                NSError *error;
                NSData * data = [NSKeyedArchiver archivedDataWithRootObject:WeXSinger.model requiringSecureCoding:NO error:&error];
                if (error) {
                    WEXNSLOG(@"%@",error.localizedDescription);
                    return ;
                }
                if (data) {
                    [[NSUserDefaults standardUserDefaults] setObject:data forKey:WeXCashLoanRealInfoKey];
                    WEXNSLOG(@"保存成功");
                }else{
                    [UIAlertController showAlertViewWithTitle:@"提示" Message:@"资料保存失败，请重试" BtnTitles:@[@"再试一次"] ClickBtn:^(NSInteger index) {
                        [weakSelf completeClick];
                    }];
                    return;
                }
                [[NSUserDefaults standardUserDefaults] setObject:data forKey:WeXCashLoanRealInfoKey];
                [[NSUserDefaults standardUserDefaults] synchronize];
            }else{
                NSData * data = [NSKeyedArchiver archivedDataWithRootObject:WeXSinger.model];
                if (data) {
                    [[NSUserDefaults standardUserDefaults] setObject:data forKey:WeXCashLoanRealInfoKey];
                    WEXNSLOG(@"保存成功");
                }else{
                    [UIAlertController showAlertViewWithTitle:@"提示" Message:@"资料保存失败，请重试" BtnTitles:@[@"再试一次"] ClickBtn:^(NSInteger index) {
                        [weakSelf completeClick];
                    }];
                    return;
                }
            }
        });
        dispatch_async(dispatch_get_main_queue(), ^{
            __weak typeof(self)weakSelf = self;
            [UIAlertController showAlertViewWithTitle:@"提示" Message:@"保存资料成功" BtnTitles:@[@"知道了"] ClickBtn:^(NSInteger index) {
                [weakSelf.navigationController popViewControllerAnimated:YES];
            }];
        });
    }
}

#pragma mark --
#pragma mark -- 创建GSSection --
- (GSSection *)HomeInfoSection{
    GSSection *section = [GSSection new];
    GSRow *row = nil;
    row = [self perfectInfoChooseRow:@{kPerfectInfoLeftKey:@"婚姻状况"} cType:0];
    [section addRow:row];
    [self.sectionTitleArray addObject:@"家庭信息"];
    return section;
}

- (GSSection *)locationSection{
    GSSection *section = [GSSection new];
    GSRow *row = nil;
    row = [self perfectInfoThreeBtnRow:@{} isHome:YES];
    [section addRow:row];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"居住地址"} placeholder:@"请输入详细的街道地址" tType:0]];
    [self.sectionTitleArray addObject:@"居住地址"];
    return section;
}

- (GSSection *)loanUseSection{
    GSSection *section = [GSSection new];
    GSRow *row = [self perfectInfoChooseRow:@{kPerfectInfoLeftKey:@"借款用途"} cType:1];
    [section addRow:row];
    [self.sectionTitleArray addObject:@"借款用途"];
    return section;
}

- (GSSection *)workInfoSection{
    GSSection *section = [GSSection new];
    [section addRow:[self perfectInfoChooseRow:@{kPerfectInfoLeftKey:@"工作类别"} cType:2]];
    [section addRow:[self perfectInfoChooseRow:@{kPerfectInfoLeftKey:@"工作行业"} cType:3]];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"工作年限"} placeholder:@"请输入工作年限" tType:1]];
    [self.sectionTitleArray addObject:@"工作信息"];
    return section;
}

- (GSSection *)companyLocationSection{
    GSSection *section = [GSSection new];
    section.footerHeight = GSSectionFooterHeight;
    [section addRow:[self perfectInfoThreeBtnRow:@{} isHome:NO]];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"办公地址"} placeholder:@"请输入详细的街道地址" tType:2]];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"单位全称"} placeholder:@"请输入单位全称" tType:3]];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"办公电话"} placeholder:@"请输入办公电话(选填)" tType:4]];
    [self.sectionTitleArray addObject:@"企业所在地址"];
    return section;
}

- (GSSection *)linkmanInfoSection{
    GSSection *section = [GSSection new];
    [section addRow:[self perfectInfoLabelRow:@"联系人1"]];
    [section addRow:[self perfectInfoChooseRow:@{kPerfectInfoLeftKey:@"与你关系"} cType:4]];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"手机号"} placeholder:@"请输入手机号" tType:5]];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"真实姓名"} placeholder:@"请输入真实姓名" tType:6]];

    [section addRow:[self perfectInfoLabelRow:@"联系人2"]];
    [section addRow:[self perfectInfoChooseRow:@{kPerfectInfoLeftKey:@"与你关系"} cType:5]];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"手机号"} placeholder:@"请输入手机号" tType:7]];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"真实姓名"} placeholder:@"请输入真实姓名" tType:8]];
    
    [section addRow:[self perfectInfoLabelRow:@"联系人3"]];
    [section addRow:[self perfectInfoChooseRow:@{kPerfectInfoLeftKey:@"与你关系"} cType:6]];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"手机号"} placeholder:@"请输入手机号" tType:9]];
    [section addRow:[self perfectInfoTextViewRow:@{kPerfectInfoLeftKey:@"真实姓名"} placeholder:@"请输入真实姓名" tType:10]];
    [self.sectionTitleArray addObject:@"联系人信息"];
    return section;
}

#pragma mark --
#pragma mark -- 创建GSRows --
- (GSRow *)perfectInfoChooseRow:(NSDictionary *)dic cType:(NSInteger)ctype{
    GSRow *row = [[GSRow alloc]initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
    row.rowHeight = 40.f;
    row.cellClass = [WeXCashLoanPerfectInfoCell class];
    row.value = dic.mutableCopy;
    __weak typeof(self) weakSelf = self;
    row.rowConfigBlock = ^(WeXCashLoanPerfectInfoCell *cell, id value, NSIndexPath *indexPath) {
        cell.type = ctype;
        cell.leftLabel.text = value[kPerfectInfoLeftKey];
       !value[kPerfectInfoRightKey] ?:[cell.rightButton setTitle:value[kPerfectInfoRightKey] forState:UIControlStateNormal];
        [cell setClickBtnBlock:^(NSString *str) {
            value[kPerfectInfoRightKey] = str;
            if (ctype == 4) {
                weakSelf.linkmanOneStr = str;
            }else if (ctype == 5){
                weakSelf.linkmanTwoStr = str;
            }else if (ctype == 6){
                weakSelf.linkmanThreeStr = str;
            }
            [weakSelf.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
        }];
    };
    row.valueValidateBlock = ^NSDictionary *(id value) {
        if (![NSString isNilOrEmptyString:value[kPerfectInfoRightKey]]) {
            if (ctype == 4 || ctype == 5 || ctype == 6) {
            }else{
                return rowOK();
            }
        }else{
            return rowError([weakSelf getTipsMessageWithType:ctype isTextFiled:NO]);
        }
        if ([weakSelf checkoutWithValue:weakSelf.linkmanOneStr value2:weakSelf.linkmanTwoStr value3:weakSelf.linkmanThreeStr]) {
            return rowOK();
        }else{
            return rowError(@"联系人：父亲、母亲、配偶 不可复选");
        }
    };
    row.reformRespRetBlock = ^(WexCashLoanRealInfoFormModel *ret, id value) {
        [weakSelf changeValueWithModel:ret value:value type:ctype isChooseRow:YES];
    };
    return row;
}

- (GSRow *)perfectInfoThreeBtnRow:(NSDictionary *)dict isHome:(BOOL)isHome{
    GSRow *row = [[GSRow alloc]initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
    row.rowHeight = 40;
    row.cellClass = [WeXCashLoanPerfectInfoThreeBtnCell class];
    row.value = dict.mutableCopy;
    __weak typeof(self) weakSelf = self;
    row.rowConfigBlock = ^(WeXCashLoanPerfectInfoThreeBtnCell *cell, id value, NSIndexPath *indexPath) {
        cell.type = isHome ? 7 : 8;
        !value[kPerfectInfoLeftKey]?:[cell.leftButton setTitle:value[kPerfectInfoLeftKey] forState:UIControlStateNormal];
        !value[kPerfectInfoMidKey]?:[cell.midButton setTitle:value[kPerfectInfoMidKey] forState:UIControlStateNormal];
        !value[kPerfectInfoRightKey]?:[cell.rightButton setTitle:value[kPerfectInfoRightKey] forState:UIControlStateNormal];
        [cell setClickBtnBlock:^(WexCashLoanRealInfoProvinceModel *provice, WexCashLoanRealInfoAreaModel *city, WexCashLoanRealInfoAreaModel *district) {
            value[kPerfectInfoLeftKey] = provice.name;
            value[kPerfectInfoMidKey] = city.name;
            value[kPerfectInfoRightKey] = district.name;
            [weakSelf.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
        }];
    };
    row.valueValidateBlock = ^NSDictionary *(id value) {
        if (![NSString isNilOrEmptyString:value[kPerfectInfoLeftKey]]) {
            return rowOK();
        }else{
            NSString *str;
            str = isHome ? @"请选择 居住地址" : @"请选择 企业所在地";
            return rowError(str);
        }
    };
    row.reformRespRetBlock = ^(WexCashLoanRealInfoFormModel *ret, id value) {
        if (isHome) {
            if (![NSString isNilOrEmptyString:ret.homeProvice]) {
                value[kPerfectInfoLeftKey] = ret.homeProvice;
            }
            if (![NSString isNilOrEmptyString:ret.homeCity]) {
                value[kPerfectInfoMidKey] = ret.homeCity;
            }
            if (![NSString isNilOrEmptyString:ret.homeDistrict]) {
                value[kPerfectInfoRightKey] = ret.homeDistrict;
            }
        }else{
            if (![NSString isNilOrEmptyString:ret.workProvice]) {
                value[kPerfectInfoLeftKey] = ret.workProvice;
            }
            if (![NSString isNilOrEmptyString:ret.workCity]) {
                value[kPerfectInfoMidKey] = ret.workCity;
            }
            if (![NSString isNilOrEmptyString:ret.workDistrict]) {
                value[kPerfectInfoRightKey] = ret.workDistrict;
            }
        }
    };
    return row;
}

- (GSRow *)perfectInfoTextViewRow:(NSDictionary *)dict placeholder:(NSString *)placeholder tType:(NSInteger)tType{
    GSRow *row = [[GSRow alloc]initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:nil];
    row.rowHeight = 40;
    row.cellClass = [WeXCashLoanPerfectInfoTextViewCell class];
    row.value = dict.mutableCopy;
    row.rowConfigBlock = ^(WeXCashLoanPerfectInfoTextViewCell *cell, id value, NSIndexPath *indexPath) {
        cell.type = tType;
        if (tType == 1) {
            cell.limitNum = 2;
        }else if (tType == 5 || tType == 7 || tType == 9){
            cell.limitNum = 11;
        }else{
            cell.limitNum = 30;
        }
        cell.leftLabel.text = value[kPerfectInfoLeftKey];
        cell.textField.text = value[kPerfectInfoMidKey];
        cell.textField.placeholder = placeholder;
        [cell setTextChangeBlock:^(NSString *text) {
            value[kPerfectInfoMidKey] = text;
        }];
    };
    __weak __typeof(&*self)weakSelf = self;
    if (!(tType == 4)) {
        row.valueValidateBlock = ^NSDictionary *(id value) {
            if (![NSString isNilOrEmptyString:value[kPerfectInfoMidKey]]) {
                return rowOK();
            }else{
                return rowError([weakSelf getTipsMessageWithType:tType isTextFiled:YES]);
            }
        };
    }
    row.reformRespRetBlock = ^(WexCashLoanRealInfoFormModel *ret, id value) {
        [weakSelf changeValueWithModel:ret value:value type:tType isChooseRow:NO];
    };
    return row;
}

- (GSRow *)perfectInfoLabelRow:(NSString *)text{
    GSRow *row = [[GSRow alloc]initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:WeXCashLoanPerfectInfoLabelCellID];
    row.rowHeight = 30;
    row.cellClass = [WeXCashLoanPerfectInfoLabelCell class];
    row.rowConfigBlock = ^(WeXCashLoanPerfectInfoLabelCell *cell, id value, NSIndexPath *indexPath) {
        cell.leftLabel.text = text;
    };
    return row;
}

#pragma mark --
#pragma mark -- UITableViewDelegate --
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    if (section < self.sectionTitleArray.count) {
        UIView *view = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, GSSectionHeaderHeight)];
        view.backgroundColor = [UIColor whiteColor];
        UILabel *label = [UILabel new];
        label.textColor = ColorWithRGB(74, 74, 74);;
        label.font = WeXPFFont(16);
        label.text = self.sectionTitleArray[section];
        [view addSubview:label];
        [label mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(view.mas_left).offset(15);
            make.bottom.equalTo(view.mas_bottom);
        }];
        return view;
    }
    return [UIView new];
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    if (section < self.sectionTitleArray.count) {
        if ([self.form.sectionArray[section] footerHeight] > 10) {
            UIView *view = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kScreenWidth, 45)];
            view.backgroundColor = [UIColor whiteColor];
            UILabel *label = [UILabel new];
            label.textColor = ColorWithRGBA(237, 25, 15, 1);
            label.font = WeXPFFont(12);
            label.numberOfLines = 2;
            label.text = @"填写格式：区号－电话号码－分机号，021-12345678-121，分机号没有可不填";
            [view addSubview:label];
            [label mas_makeConstraints:^(MASConstraintMaker *make) {
                make.left.equalTo(view.mas_left).offset(15);
                make.right.equalTo(view.mas_right).offset(-15);
                make.bottom.equalTo(view.mas_bottom);
            }];
            return view;
        }else{
            UIView *view = [UIView new];
            view.backgroundColor = [[UIColor whiteColor] colorWithAlphaComponent:0];
            return view;
        }
    }
    return [UIView new];
}

#pragma mark --
#pragma mark -- Private Methods --
- (NSString *)getTipsMessageWithType:(NSInteger)type isTextFiled:(BOOL)isTextFiled{
    self.isNeedBecomeFirstResponder = isTextFiled;
    NSString *str = nil;
    switch (type) {
        case 0:
            self.scrollToRowIdx = isTextFiled ? 1 : 0;
            self.scrollToSectionIdx = isTextFiled ? 1 : 0;
            str = isTextFiled ? @"请输入 居住地址" : @"请选择 婚姻状况";
            break;
        case 1:
            self.scrollToRowIdx = isTextFiled ? 2 : 0;
            self.scrollToSectionIdx = isTextFiled ? 3 : 2;
            str = isTextFiled ? @"请输入 工作年限" : @"请选择 借款用途";
            break;
        case 2:
            self.scrollToRowIdx = isTextFiled ? 1 : 0;
            self.scrollToSectionIdx = isTextFiled ? 4 : 3;
            str = isTextFiled ? @"请输入 办公地址" : @"请选择 工作类别";
            break;
        case 3:
            self.scrollToRowIdx = isTextFiled ? 2 : 1;
            self.scrollToSectionIdx = isTextFiled ? 4 : 3;
            str = isTextFiled ? @"请输入 单位全称" : @"请选择 工作行业";
            break;
        case 4:
            if (!isTextFiled) {
                self.scrollToRowIdx = 1;
                self.scrollToSectionIdx = 5;
                str = @"请补全联系人信息";
            }
            break;
        case 5:
            if (!isTextFiled) {
                self.scrollToRowIdx = 5;
                self.scrollToSectionIdx = 5;
                str = @"请补全联系人信息";
            }else{
                self.scrollToRowIdx = 2;
                self.scrollToSectionIdx = 5;
                str = @"请输入 联系人1手机号";
            }
            break;
        case 6:
            if (!isTextFiled) {
                self.scrollToRowIdx = 9;
                self.scrollToSectionIdx = 5;
                str = @"请补全联系人信息";
            }else{
                self.scrollToRowIdx = 3;
                self.scrollToSectionIdx = 5;
                str = @"请输入 联系人1真实姓名";
            }
            break;
        case 7:
            self.scrollToRowIdx = 6;
            self.scrollToSectionIdx = 5;
            str = @"请输入 联系人2手机号";
            break;
        case 8:
            self.scrollToRowIdx = 7;
            self.scrollToSectionIdx = 5;
            str = @"请输入 联系人2真实姓名";
            break;
        case 9:
            self.scrollToRowIdx = 10;
            self.scrollToSectionIdx = 5;
            str = @"请输入 联系人3手机号";
            break;
        case 10:
            self.scrollToRowIdx = 11;
            self.scrollToSectionIdx = 5;
            str = @"请输入 联系人3真实姓名";
            break;
        default:
            break;
    }
    return str;
}

#pragma mark --
#pragma mark -- 设置初始值 --
- (void)changeValueWithModel:(WexCashLoanRealInfoFormModel *)model value:(id)value type:(NSInteger)type isChooseRow:(BOOL)isChooseRow{
    NSString *str = nil;
    if (type == 0) {
        if (isChooseRow) {
            if (![NSString isNilOrEmptyString:model.marriageStatus]) {
                str = model.marriageStatus;
            }
        }else{
            if (![NSString isNilOrEmptyString:model.homeLoacation]) {
                str = model.homeLoacation;
            }
        }
    }else if (type == 1){
        if (isChooseRow) {
            if (![NSString isNilOrEmptyString:model.loanUse]) {
                str = model.loanUse;
            }
        }else{
            if (![NSString isNilOrEmptyString:model.workAge]) {
                str = model.workAge;
            }
        }
    }else if (type == 2){
        if (isChooseRow) {
            if (![NSString isNilOrEmptyString:model.workType]) {
                str = model.workType;
            }
        }else{
            if (![NSString isNilOrEmptyString:model.workLocation]) {
                str = model.workLocation;
            }
        }
    }else if (type == 3){
        if (isChooseRow) {
            if (![NSString isNilOrEmptyString:model.workIndustry]) {
                str = model.workIndustry;
            }
        }else{
            if (![NSString isNilOrEmptyString:model.workName]) {
                str = model.workName;
            }
        }
    }else if (type == 4){
        if (isChooseRow) {
            if (![NSString isNilOrEmptyString:model.linkmanOne]) {
                str = model.linkmanOne;
            }
        }else{
            if (![NSString isNilOrEmptyString:model.workPhoneNumber]) {
                str = model.workPhoneNumber;
            }
        }
    }else if (type == 5) {
        if (isChooseRow) {
            if (![NSString isNilOrEmptyString:model.linkmanTwo]) {
                str = model.linkmanTwo;
            }
        }else{
            if (![NSString isNilOrEmptyString:model.linkmanOnePhone]) {
                str = model.linkmanOnePhone;
            }
        }
    }else if (type == 6){
        if (isChooseRow) {
            if (![NSString isNilOrEmptyString:model.linkmanThree]) {
                str = model.linkmanThree;
            }
        }else{
            if (![NSString isNilOrEmptyString:model.linkmanOneName]) {
                str = model.linkmanOneName;
            }
        }
    }else if (type == 7){
        if (![NSString isNilOrEmptyString:model.linkmanTwoPhone]) {
            str = model.linkmanTwoPhone;
        }
    }else if (type == 8){
        if (![NSString isNilOrEmptyString:model.linkmanTwoName]) {
            str = model.linkmanTwoName;
        }
    }else if (type == 9){
        if (![NSString isNilOrEmptyString:model.linkmanThreePhone]) {
            str = model.linkmanThreePhone;
        }
    }else if (type == 10){
        if (![NSString isNilOrEmptyString:model.linkmanThreeName]) {
            str = model.linkmanThreeName;
        }
    }
    if (![NSString isNilOrEmptyString:str]) {
        if (isChooseRow) {
            value[kPerfectInfoRightKey] = str;
        }else{
            value[kPerfectInfoMidKey] = str;
        }
    }
}

- (BOOL)checkoutWithValue:(NSString *)value1 value2:(NSString *)value2 value3:(NSString *)value3{
    NSString *temp;
    if ([value1 isEqualToString:value2]) {
        temp = value1;
    }else if ([value1 isEqualToString:value3]){
        temp = value1;
    }else if ([value2 isEqualToString:value3]){
        temp = value2;
    }else{
        return YES;
    }
    if ([temp isEqualToString:@"父亲"] || [temp isEqualToString:@"母亲"] || [temp isEqualToString:@"配偶"]) {
        self.scrollToSectionIdx = 5;
        return NO;
    }
    return YES;
}

#pragma mark --
#pragma mark -- 懒加载 --
- (NSMutableArray *)sectionTitleArray{
    if (!_sectionTitleArray) {
        _sectionTitleArray = [NSMutableArray array];
    }
    return _sectionTitleArray;
}

- (UIButton *)completeBtn{
    if (!_completeBtn) {
        _completeBtn = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [_completeBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_completeBtn setTitle:@"确定" forState:UIControlStateNormal];
        _completeBtn.backgroundColor = ColorWithRGBA(186, 192, 197, 1);
        _completeBtn.layer.cornerRadius = 4;
        _completeBtn.layer.masksToBounds = YES;
        [self.view insertSubview:_completeBtn aboveSubview:self.tableView];
        [_completeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.view.mas_left).offset(15);
            make.right.equalTo(self.view.mas_right).offset(-15);
            make.bottom.equalTo(self.view.mas_bottom).offset(-19);
            make.height.mas_equalTo(40.f);
        }];
    }
    return _completeBtn;
}

- (void)dealloc{
    WEXNSLOG(@"%s",__func__);
}
@end
