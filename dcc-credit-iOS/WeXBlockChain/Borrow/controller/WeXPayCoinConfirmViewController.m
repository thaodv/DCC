//
//  WeXPayCoinConfirmViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/4.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPayCoinConfirmViewController.h"
#import "WeXPayCoinViewController.h"
#import "WeXLoanGetRepaymentBillAdapter.h"


@interface WeXPayCoinConfirmViewController ()
{
    WeXLoanGetRepaymentBillResponseModal *_billModel;
    
    UIView *_line;
}

@property (nonatomic,strong)WeXLoanGetRepaymentBillAdapter *getBillAdapter;

@end

@implementation WeXPayCoinConfirmViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"还币信息确认");
    [self setNavigationNomalBackButtonType];

    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self createGetBillRequest];
}

#pragma -mark 查询OrderDetail请求
- (void)createGetBillRequest{
    _getBillAdapter = [[WeXLoanGetRepaymentBillAdapter alloc] init];
    _getBillAdapter.delegate = self;
    WeXLoanGetRepaymentBillParamModal* paramModal = [[WeXLoanGetRepaymentBillParamModal alloc] init];
    paramModal.chainOrderId = _orderId;
    [_getBillAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getBillAdapter)
    {
        [WeXPorgressHUD hideLoading];
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            _billModel = (WeXLoanGetRepaymentBillResponseModal *)response;
            WEXNSLOG(@"%@",_billModel);
            [self setupSubViews];
        }
        else
        {
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }
    
}



- (void)setupSubViews{
  
    UILabel *payCapitalTitleLabel = [[UILabel alloc] init];
    payCapitalTitleLabel.text = WeXLocalizedString(@"应还本金");
    payCapitalTitleLabel.textAlignment = NSTextAlignmentLeft;
    payCapitalTitleLabel.font = [UIFont systemFontOfSize:18];
    payCapitalTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self.view addSubview:payCapitalTitleLabel];
    [payCapitalTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.height.equalTo(@20);
    }];
    
    UILabel *payCapitalLabel = [[UILabel alloc] init];
    payCapitalLabel.text = [NSString stringWithFormat:@"%.4f%@",_billModel.repaymentPrincipal,_billModel.assetCode];
    payCapitalLabel.textAlignment = NSTextAlignmentRight;
    payCapitalLabel.font = [UIFont systemFontOfSize:18];
    payCapitalLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self.view addSubview:payCapitalLabel];
    [payCapitalLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(payCapitalTitleLabel).offset(0);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(@20);
    }];
    
    UILabel *payInterestTitleLabel = [[UILabel alloc] init];
    payInterestTitleLabel.text = WeXLocalizedString(@"应还利息");
    payInterestTitleLabel.textAlignment = NSTextAlignmentCenter;
    payInterestTitleLabel.font = [UIFont systemFontOfSize:18];
    payInterestTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self.view addSubview:payInterestTitleLabel];
    [payInterestTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(payCapitalTitleLabel).offset(0);
        make.top.equalTo(payCapitalLabel.mas_bottom).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *payInterestLabel = [[UILabel alloc] init];
    payInterestLabel.text = [NSString stringWithFormat:@"%.4f%@",_billModel.repaymentInterest,_billModel.assetCode];
    payInterestLabel.textAlignment = NSTextAlignmentCenter;
    payInterestLabel.font = [UIFont systemFontOfSize:18];
    payInterestLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self.view addSubview:payInterestLabel];
    [payInterestLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(payInterestTitleLabel).offset(0);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(@20);
    }];
    /**提前还款罚金*/
    if (_billModel.penaltyAmount != 0) {
        UILabel *beforeRepayFeeTitleLabel = [[UILabel alloc] init];
        beforeRepayFeeTitleLabel.text = WeXLocalizedString(@"提前还款手续费:");
        beforeRepayFeeTitleLabel.textAlignment = NSTextAlignmentCenter;
        beforeRepayFeeTitleLabel.font = [UIFont systemFontOfSize:18];
        beforeRepayFeeTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        [self.view addSubview:beforeRepayFeeTitleLabel];
        [beforeRepayFeeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.equalTo(payCapitalTitleLabel).offset(0);
            make.top.equalTo(payInterestTitleLabel.mas_bottom).offset(10);
            make.height.equalTo(@20);
        }];
        
        UILabel *beforeRepayFeeLabel = [[UILabel alloc] init];
        beforeRepayFeeLabel.text = [NSString stringWithFormat:@"%.4f%@",_billModel.penaltyAmount,_billModel.assetCode];
        beforeRepayFeeLabel.textAlignment = NSTextAlignmentCenter;
        beforeRepayFeeLabel.font = [UIFont systemFontOfSize:18];
        beforeRepayFeeLabel.textColor = COLOR_LABEL_DESCRIPTION;
        [self.view addSubview:beforeRepayFeeLabel];
        [beforeRepayFeeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(beforeRepayFeeTitleLabel).offset(0);
            make.trailing.equalTo(self.view).offset(-15);
            make.height.equalTo(@20);
        }];
        
        UIView *line = [[UIView alloc] init];
        line.backgroundColor = COLOR_ALPHA_LINE;
        [self.view addSubview:line];
        [line mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.equalTo(self.view).offset(15);
            make.trailing.equalTo(self.view).offset(-15);
            make.top.equalTo(beforeRepayFeeTitleLabel.mas_bottom).offset(15);
            make.height.equalTo(@HEIGHT_LINE);
        }];
        _line = line;
    }
    /**滞纳金*/
    else if(_billModel.overdueFine != 0)
    {
        UILabel *afterRepayFeeTitleLabel = [[UILabel alloc] init];
        afterRepayFeeTitleLabel.text = WeXLocalizedString(@"逾期滞纳金:");
        afterRepayFeeTitleLabel.textAlignment = NSTextAlignmentCenter;
        afterRepayFeeTitleLabel.font = [UIFont systemFontOfSize:18];
        afterRepayFeeTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
        [self.view addSubview:afterRepayFeeTitleLabel];
        [afterRepayFeeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.equalTo(payCapitalTitleLabel).offset(0);
            make.top.equalTo(payInterestTitleLabel.mas_bottom).offset(10);
            make.height.equalTo(@20);
        }];
        
        UILabel *afterRepayFeeLabel = [[UILabel alloc] init];
        afterRepayFeeLabel.text = [NSString stringWithFormat:@"%.4f%@",_billModel.overdueFine,_billModel.assetCode];
        afterRepayFeeLabel.textAlignment = NSTextAlignmentCenter;
        afterRepayFeeLabel.font = [UIFont systemFontOfSize:18];
        afterRepayFeeLabel.textColor = COLOR_LABEL_DESCRIPTION;
        [self.view addSubview:afterRepayFeeLabel];
        [afterRepayFeeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(afterRepayFeeTitleLabel).offset(0);
            make.trailing.equalTo(self.view).offset(-15);
            make.height.equalTo(@20);
        }];
        
        UIView *line = [[UIView alloc] init];
        line.backgroundColor = COLOR_ALPHA_LINE;
        [self.view addSubview:line];
        [line mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.equalTo(self.view).offset(15);
            make.trailing.equalTo(self.view).offset(-15);
            make.top.equalTo(afterRepayFeeTitleLabel.mas_bottom).offset(15);
            make.height.equalTo(@HEIGHT_LINE);
        }];
        _line = line;
    }
    else //正常
    {
        UIView *line = [[UIView alloc] init];
        line.backgroundColor = COLOR_ALPHA_LINE;
        [self.view addSubview:line];
        [line mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.equalTo(self.view).offset(15);
            make.trailing.equalTo(self.view).offset(-15);
            make.top.equalTo(payInterestTitleLabel.mas_bottom).offset(15);
            make.height.equalTo(@HEIGHT_LINE);
        }];
        _line = line;
    }
    
    UILabel *payTotalTitleLabel = [[UILabel alloc] init];
    payTotalTitleLabel.text = WeXLocalizedString(@"还币总额:");
    payTotalTitleLabel.textAlignment = NSTextAlignmentCenter;
    payTotalTitleLabel.font = [UIFont systemFontOfSize:18];
    payTotalTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self.view addSubview:payTotalTitleLabel];
    [payTotalTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(payCapitalTitleLabel).offset(0);
        make.top.equalTo(_line.mas_bottom).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *payTotalLabel = [[UILabel alloc] init];
    payTotalLabel.text = [NSString stringWithFormat:@"%.4f%@",_billModel.amount,_billModel.assetCode];
    payTotalLabel.textAlignment = NSTextAlignmentCenter;
    payTotalLabel.font = [UIFont systemFontOfSize:18];
    payTotalLabel.textColor = COLOR_LABEL_DESCRIPTION;
    [self.view addSubview:payTotalLabel];
    [payTotalLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(payTotalTitleLabel).offset(0);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(@20);
    }];
  
    
    UIButton *applyBtn = [WeXCustomButton button];
    [applyBtn setTitle:WeXLocalizedString(@"确认") forState:UIControlStateNormal];
    [applyBtn addTarget:self action:@selector(applyBtnClick) forControlEvents:UIControlEventTouchUpInside];
    applyBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:applyBtn];
    [applyBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(@40);
        make.bottom.equalTo(self.view).offset(-20);
    }];
    
    
    
}

// MARK: - 还币
- (void)applyBtnClick
{
    WeXPayCoinViewController *ctrl = [[WeXPayCoinViewController alloc] init];
    ctrl.orderId = _billModel.chainOrderId;
    [self.navigationController pushViewController:ctrl animated:YES];
}

@end
