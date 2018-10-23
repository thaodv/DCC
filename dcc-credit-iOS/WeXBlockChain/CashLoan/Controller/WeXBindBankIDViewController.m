//
//  WeXBindBankIDViewController.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/10/17.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBindBankIDViewController.h"
#import "WeXCashBindBankIDCell.h"
#import "WeXCreditChooseOpenBankView.h"
#import "WeXBindBankIDBottomView.h"

@interface WeXBindBankIDViewController () <WeXCreditChooseOpenBankViewDelegate>
@property (nonatomic, strong) NSArray <NSString *> *titles;
@property (nonatomic, strong) WeXPasswordCacheModal *cacheModel;
@property (nonatomic, strong) UIButton *phoneCodeButton;
@property (nonatomic, copy) NSString *bankCode;
@property (nonatomic, copy) NSString *bankName;
@property (nonatomic, copy) NSString *bankIDNumber;
@property (nonatomic, copy) NSString *phoneCode;
@property (nonatomic, strong) WeXBindBankIDBottomView *bottomView;
@property (nonatomic, strong) NSTimer *timer;
@property (nonatomic, assign) NSInteger maxTime;


@end

static NSString * const kBindBackCellID = @"WeXCashBindBankIDCellID";
static NSString * const kNormalTitle = @"点击获取";



@implementation WeXBindBankIDViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = WeXLocalizedString(@"绑定银行卡");
    [self p_configureUI];
}

- (void)p_configureUI {
    _titles = @[@"持卡人",@"开户行",@"银行卡号",@"银行预留手机号",@"短信验证码"];
    _cacheModel = [WexCommonFunc getPassport];
    [self setNavigationNomalBackButtonType];
    [self wex_addTopSepratorLine:40 text:@"请提交本人的银行卡"];
    [self wex_unistallRefreshHeader];
    self.maxTime = 30;
    __weak typeof(self) weakSelf     = self;
    self.bottomView = [WeXBindBankIDBottomView createViewButtonTitle:@"确认" buttonEvent:^{
        [weakSelf commitEvent];
    }];
    CGFloat height = kScreenHeight - 66 * 5 - kNavgationBarHeight - 40 ;
    self.bottomView.frame = CGRectMake(0, 0, kScreenWidth, height > 100 ? height : 100);
    self.tableView.tableFooterView = self.bottomView;
    
}

- (void)wexRegisterTableViewCell:(UITableView *)tableView {
    [tableView registerClass:[WeXCashBindBankIDCell class] forCellReuseIdentifier:kBindBackCellID];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _titles.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [self wexTableview:tableView cellForRowWithIdentifier:kBindBackCellID indexPath:indexPath];
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return [self wexTableView:tableView cellIdentifier:kBindBackCellID indexPath:indexPath];
}
- (void)wexTableView:(UITableView *)tableView configureCell:(UITableViewCell *)currentCell indexPath:(NSIndexPath *)indexPath {
    WeXCashBindBankIDCell *cell = (WeXCashBindBankIDCell *)currentCell;
    NSString *title = _titles[indexPath.row];
    switch (indexPath.row) {
        case 0: {
            [cell setTitle:title subText:_cacheModel.userName cellType:WeXCashBindBankIDCellDefault];
        }
            break;
        case 1: {
            NSString *subTitle = self.bankName ? : _cacheModel.bankAuthenCardName;
            [cell setTitle:title subText:subTitle cellType:WeXCashBindBankIDCellWithArrow];
        }
            break;
        case 2: {
            NSString *subTitle = self.bankIDNumber ? : _cacheModel.bankAuthenCardNumber;
            [cell setTitle:title subText:subTitle cellType:WeXCashBindBankIDCellWithTextField];
            cell.DidInputCode = ^(NSString *cardNum) {
                self.bankIDNumber = cardNum;
                WEXNSLOG(@"bankIDNumber is : %@",self.bankIDNumber);
            };
        }
            break;
        case 3: {
            [cell setTitle:title subText:_cacheModel.mobileAuthenNumber cellType:WeXCashBindBankIDCellDefault];
        }
            break;
        case 4: {
            [cell setTitle:title subText:nil cellType:WeXCashBindBankIDCellWithButton];
            cell.maxCount = 6;
            self.phoneCodeButton = cell.codeButton;
            [cell setBottomLineHidden:true];
            [self.phoneCodeButton setTitle:@"点击获取" forState:UIControlStateNormal];
            [self.phoneCodeButton addTarget:self action:@selector(getPhoneCode:) forControlEvents:UIControlEventTouchUpInside];
            cell.DidInputCode = ^(NSString *phoneCode) {
                self.phoneCode = phoneCode;
                WEXNSLOG(@"phoneCode is : %@",self.phoneCode);
            };
        }
            break;
            
        default: {
            [cell setTitle:title subText:_cacheModel.userName cellType:WeXCashBindBankIDCellDefault];
        }
            break;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 1) {
        [self initChooseBankView];
    }
}

- (void)getPhoneCode:(UIButton *)sender {
    [self beginTimeDown];
    WEXNSLOG(@"获取验证码");
}

// MARK: - 确认
- (void)commitEvent {
    WEXNSLOG(@"确认");
}

- (void)initChooseBankView {
    WeXCreditChooseOpenBankView *openBankView = [[WeXCreditChooseOpenBankView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    openBankView.delegate = self;
    [self.view.window addSubview:openBankView];
}

// MARK: - WeXCreditChooseOpenBankViewDelegate
- (void)creditChooseOpenBankViewDidSelectBankName:(NSString *)bankName bankCode:(NSString *)bankCode {
    self.bankName = bankName;
    self.bankCode = bankCode;
    [self.tableView reloadData];
}

- (void)beginTimeDown {
    [self invalidateTimer];
    _timer = [NSTimer timerWithTimeInterval:1.0 target:self selector:@selector(timeCountDown:) userInfo:nil repeats:true];
    [[NSRunLoop currentRunLoop] addTimer:_timer forMode:NSRunLoopCommonModes];
    [_timer fire];
}
- (void)timeCountDown:(NSTimer *)timer {
    self.maxTime --;
    if (self.maxTime <= 0) {
        [self.phoneCodeButton setEnabled:true];
        [self.phoneCodeButton setTitle:kNormalTitle forState:UIControlStateNormal];
        [self invalidateTimer];
    } else {
        NSString *title = [NSString stringWithFormat:@"%lds",self.maxTime];
        [self.phoneCodeButton setTitle:title forState:UIControlStateNormal];
    }
}

- (void)invalidateTimer {
    [self.timer invalidate];
    self.timer = nil;
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    if (self.timer) {
        [self invalidateTimer];
    }
}

- (void)dealloc {
    WEXNSLOG(@"funnction:%s",__func__);
}




@end
