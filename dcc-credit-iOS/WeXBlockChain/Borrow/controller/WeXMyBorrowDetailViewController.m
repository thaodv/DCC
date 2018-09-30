//
//  WeXMyBorrowDetailViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/5/4.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXMyBorrowDetailViewController.h"
#import "WeXPayCoinConfirmViewController.h"
#import "WeXLoanGetOrderDetailAdapter.h"
#import "WeXBorrowProductChooseController.h"
#import "WeXMyBorrowContractWebViewController.h"
#import "WeXRepaymentExplainController.h"

#define GRAY_COLOR ColorWithRGB(132, 132, 132)
#define PINK_COLOR ColorWithRGB(244, 73, 148)
#define BLUE_COLOR ColorWithRGB(83, 58, 135)

static const CGFloat kBackImageHeightWidthRatio = 82.0/339.0;

@interface WeXMyBorrowDetailViewController ()
{
    WeXLoanGetOrderDetailResponseModal *_orderModel;
}

@property (nonatomic,strong)WeXLoanGetOrderDetailAdapter *getOrderDetailAdapter;

@end

@implementation WeXMyBorrowDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"借币详情");
    [self setNavigationNomalBackButtonType];
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    [self createGetOrderDetailRequest];
}

#pragma -mark 查询OrderDetail请求
- (void)createGetOrderDetailRequest{
    _getOrderDetailAdapter = [[WeXLoanGetOrderDetailAdapter alloc] init];
    _getOrderDetailAdapter.delegate = self;
    WeXLoanGetOrderDetailParamModal* paramModal = [[WeXLoanGetOrderDetailParamModal alloc] init];
    paramModal.chainOrderId = _orderId;
    [_getOrderDetailAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getOrderDetailAdapter)
    {
        [WeXPorgressHUD hideLoading];
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            _orderModel = (WeXLoanGetOrderDetailResponseModal *)response;
            WEXNSLOG(@"%@",_orderModel);
            [self setupSubViews];
        }
        else
        {
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
    }

}


- (void)setupSubViews{
    
    UILabel *borrowBalanceTitleLabel = [[UILabel alloc] init];
    borrowBalanceTitleLabel.text = WeXLocalizedString(@"借币金额");
    borrowBalanceTitleLabel.font = [UIFont systemFontOfSize:20];
    borrowBalanceTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    borrowBalanceTitleLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:borrowBalanceTitleLabel];
    [borrowBalanceTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.centerX.equalTo(self.view).offset(0);
        make.height.equalTo(@20);
    }];
    
    UILabel *borrowBalanceLabel = [[UILabel alloc] init];
    borrowBalanceLabel.text = [NSString stringWithFormat:@"%.4f%@",[_orderModel.amount doubleValue],_orderModel.currency.symbol];
    borrowBalanceLabel.font = [UIFont systemFontOfSize:20];
    borrowBalanceLabel.textColor = COLOR_LABEL_DESCRIPTION;
    borrowBalanceLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:borrowBalanceLabel];
    [borrowBalanceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(borrowBalanceTitleLabel.mas_bottom).offset(20);
        make.centerX.equalTo(self.view);
        make.height.equalTo(@20);
    }];
    
    UILabel *idTitleLabel = [[UILabel alloc] init];
    idTitleLabel.text = WeXLocalizedString(@"订单号:");
    idTitleLabel.font = [UIFont systemFontOfSize:18];
    idTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    idTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:idTitleLabel];
    [idTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(borrowBalanceLabel.mas_bottom).offset(25);
        make.leading.equalTo(self.view).offset(15);
    }];
    
    UILabel *idLabel = [[UILabel alloc] init];
    idLabel.text = _orderModel.orderId;
    idLabel.font = [UIFont systemFontOfSize:18];
    idLabel.textColor = COLOR_LABEL_DESCRIPTION;
    idLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:idLabel];
    [idLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(idTitleLabel).offset(0);
        make.trailing.equalTo(self.view).offset(-15);
    }];
    
    UILabel *periodTitleLabel = [[UILabel alloc] init];
    periodTitleLabel.text = WeXLocalizedString(@"借币期限:");
    periodTitleLabel.font = [UIFont systemFontOfSize:18];
    periodTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    periodTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:periodTitleLabel];
    [periodTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(idTitleLabel.mas_bottom).offset(15);
        make.leading.equalTo(self.view).offset(15);
    }];
    
    UILabel *periodLabel = [[UILabel alloc] init];
    if ([_orderModel.status isEqualToString:WEX_LOAN_STATUS_REPAID]||[_orderModel.status isEqualToString:WEX_LOAN_STATUS_DELIVERED])
    {
        NSString *time = [NSString stringWithFormat:@"%@-%@",[WexCommonFunc formatterTimeStringWithTimeStamp:_orderModel.deliverDate formatter:@"yyyy.MM.dd"],[WexCommonFunc formatterTimeStringWithTimeStamp:_orderModel.repayDate formatter:@"yyyy.MM.dd"]];
        periodLabel.text = time;
    }
    else
    {
        periodLabel.text = [NSString stringWithFormat:@"%@%@",_orderModel.borrowDuration,[WexCommonFunc transferChinesePeriod:_orderModel.durationUnit]];
    }
    periodLabel.font = [UIFont systemFontOfSize:18];
    periodLabel.textColor = COLOR_LABEL_DESCRIPTION;
    periodLabel.textAlignment = NSTextAlignmentRight;
    [self.view addSubview:periodLabel];
    [periodLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(periodTitleLabel).offset(0);
        make.trailing.equalTo(self.view).offset(-15);
    }];
    
    UILabel *payInterestTitleLabel = [[UILabel alloc] init];
    payInterestTitleLabel.text = WeXLocalizedString(@"应还利息:");
    payInterestTitleLabel.font = [UIFont systemFontOfSize:18];
    payInterestTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    payInterestTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:payInterestTitleLabel];
    [payInterestTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(periodLabel.mas_bottom).offset(15);
        make.leading.equalTo(self.view).offset(15);
    }];
    
    UILabel *payInterestLabel = [[UILabel alloc] init];
    payInterestLabel.text = [NSString stringWithFormat:@"%.4f%@",[_orderModel.expectLoanInterest doubleValue],_orderModel.currency.symbol];
    payInterestLabel.font = [UIFont systemFontOfSize:18];
    payInterestLabel.textColor = COLOR_LABEL_DESCRIPTION;
    payInterestLabel.textAlignment = NSTextAlignmentRight;
    [self.view addSubview:payInterestLabel];
    [payInterestLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(payInterestTitleLabel).offset(0);
        make.trailing.equalTo(self.view).offset(-15);
    }];
    
    
    UILabel *feeTitleLabel = [[UILabel alloc] init];
    feeTitleLabel.text = WeXLocalizedString(@"借币手续费:");
    feeTitleLabel.font = [UIFont systemFontOfSize:18];
    feeTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    feeTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:feeTitleLabel];
    [feeTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(payInterestTitleLabel.mas_bottom).offset(15);
        make.leading.equalTo(self.view).offset(15);
    }];
    
    UILabel *feeLabel = [[UILabel alloc] init];
    feeLabel.text = [NSString stringWithFormat:@"%.4fDCC",[_orderModel.fee floatValue]];
    feeLabel.font = [UIFont systemFontOfSize:18];
    feeLabel.textColor = COLOR_LABEL_DESCRIPTION;
    feeLabel.textAlignment = NSTextAlignmentRight;
    [self.view addSubview:feeLabel];
    [feeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(feeTitleLabel).offset(0);
        make.trailing.equalTo(self.view).offset(-15);
    }];
    
    UILabel *receiveAddressTitleLabel = [[UILabel alloc] init];
    receiveAddressTitleLabel.text = WeXLocalizedString(@"收币地址:");
    receiveAddressTitleLabel.font = [UIFont systemFontOfSize:18];
    receiveAddressTitleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    receiveAddressTitleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:receiveAddressTitleLabel];
    [receiveAddressTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(feeTitleLabel.mas_bottom).offset(15);
        make.leading.equalTo(self.view).offset(15);
        make.width.equalTo(@80);
    }];
    
    UILabel *receiveAddressLabel = [[UILabel alloc] init];
    receiveAddressLabel.text = _orderModel.receiverAddress;
    receiveAddressLabel.font = [UIFont systemFontOfSize:18];
    receiveAddressLabel.textColor = COLOR_LABEL_DESCRIPTION;
    receiveAddressLabel.textAlignment = NSTextAlignmentRight;
    receiveAddressLabel.adjustsFontSizeToFitWidth = YES;
    [self.view addSubview:receiveAddressLabel];
    [receiveAddressLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(receiveAddressTitleLabel).offset(0);
        make.trailing.equalTo(self.view).offset(-15);
        make.leading.equalTo(receiveAddressTitleLabel.mas_trailing).offset(5);
    }];
    
    UIImageView *backImageView = [[UIImageView alloc] init];
    [self.view addSubview:backImageView];
    [backImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(receiveAddressTitleLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(backImageView.mas_width).multipliedBy(kBackImageHeightWidthRatio);
    }];
    
    UIImageView *statusImageView = [[UIImageView alloc] init];
    [self.view addSubview:statusImageView];
    [statusImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(backImageView).offset(0);
        make.trailing.equalTo(backImageView).offset(-15);
    }];
    
    UILabel *desLabel = [[UILabel alloc] init];
    desLabel.font = [UIFont systemFontOfSize:17];
    desLabel.textColor = [UIColor whiteColor];
    desLabel.textAlignment = NSTextAlignmentLeft;
    desLabel.adjustsFontSizeToFitWidth = YES;
    desLabel.numberOfLines = 0;
    [self.view addSubview:desLabel];
    [desLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(backImageView).offset(0);
        make.trailing.equalTo(statusImageView).offset(-5);
        make.leading.equalTo(backImageView).offset(10);
    }];
    
    UIButton *goRepaymentBtn = [[UIButton alloc]init];
    [goRepaymentBtn setTitle:@"还币流程" forState:UIControlStateNormal];
    goRepaymentBtn.titleLabel.font = [UIFont systemFontOfSize:14.0f];
    [goRepaymentBtn setTitleColor:ColorWithHex(0x5756B3) forState:UIControlStateNormal];
    [goRepaymentBtn addTarget:self action:@selector(goRepaymentBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:goRepaymentBtn];
    [goRepaymentBtn setHidden:YES];
    [goRepaymentBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backImageView.mas_bottom).offset(15);
        make.right.mas_offset(-20);
        make.width.mas_offset(60);
        make.height.mas_offset(20);
    }];
    
    UIView *repaymentLineView = [[UIView alloc]init];
    repaymentLineView.backgroundColor = ColorWithHex(0x5756B3);
    [self.view addSubview:repaymentLineView];
    [repaymentLineView setHidden:YES];
    [repaymentLineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(goRepaymentBtn.mas_bottom).offset(0);
        make.trailing.equalTo(goRepaymentBtn);
        make.leading.equalTo(goRepaymentBtn);
        make.height.mas_offset(1);
    }];
    
    UIButton *applyBtn = [WeXCustomButton button];
    applyBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:applyBtn];
    [applyBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.height.equalTo(@40);
        make.bottom.equalTo(self.view).offset(-20);
    }];
    
    UIView *protocolView = [[UIView alloc] init];
    [self.view addSubview:protocolView];
    [protocolView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(repaymentLineView.mas_bottom).offset(10);
        make.trailing.equalTo(self.view).offset(-15);
        make.leading.equalTo(self.view).offset(15);
        make.height.equalTo(@50);
    }];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(protocolClick)];
    [protocolView addGestureRecognizer:tap];
    
    UIView *line1 = [[UIView alloc] init];
    line1.backgroundColor = COLOR_ALPHA_LINE;
    [protocolView addSubview:line1];
    [line1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(protocolView).offset(0);
        make.trailing.equalTo(protocolView).offset(0);
        make.top.equalTo(protocolView).offset(0);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UIView *line2 = [[UIView alloc] init];
    line2.backgroundColor = COLOR_ALPHA_LINE;
    [protocolView addSubview:line2];
    [line2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(protocolView).offset(0);
        make.trailing.equalTo(protocolView).offset(0);
        make.bottom.equalTo(protocolView).offset(0);
        make.height.equalTo(@HEIGHT_LINE);
    }];
    
    UILabel *protocolLabel = [[UILabel alloc] init];
    protocolLabel.text = WeXLocalizedString(@"借币合同");
    protocolLabel.font = [UIFont systemFontOfSize:18];
    protocolLabel.textColor = COLOR_LABEL_DESCRIPTION;
    protocolLabel.textAlignment = NSTextAlignmentLeft;
    [protocolView addSubview:protocolLabel];
    [protocolLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(protocolView).offset(0);
        make.leading.equalTo(protocolView).offset(0);
    }];
    
    UIImageView *arrowImageView = [[UIImageView alloc] init];
    arrowImageView.image = [UIImage imageNamed:@"dcc_arrow_right_black"];
    [protocolView addSubview:arrowImageView];
    [arrowImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(protocolView).offset(0);
        make.trailing.equalTo(protocolView).offset(0);
    }];
    
   
    
    //审核中
    if ([_orderModel.status isEqualToString:WEX_LOAN_STATUS_AUDITING])
    {
        backImageView.image = [UIImage imageNamed:@"borrow_detail_card_2"];
        statusImageView.image = [UIImage imageNamed:@"borrow_detail_3"];
        //MyBorrowDetailVCOrderCheckStatus
        desLabel.text = WeXLocalizedString(@"MyBorrowDetailVCOrderCheckStatus");
        protocolView.hidden = YES;
        applyBtn.hidden = YES;
        repaymentLineView.hidden = YES;
        goRepaymentBtn.hidden = YES;
    }
    //审核失败
    else if ([_orderModel.status isEqualToString:WEX_LOAN_STATUS_REJECTED])
    {
        //MyBorrowDetailVCOrderFailStatus
        backImageView.image = [UIImage imageNamed:@"borrow_detail_card_2"];
        statusImageView.image = [UIImage imageNamed:@"borrow_detail_5"];
        desLabel.text = WeXLocalizedString(@"MyBorrowDetailVCOrderFailStatus");
        protocolView.hidden = YES;
        repaymentLineView.hidden = YES;
        goRepaymentBtn.hidden = YES;
        [applyBtn setTitle:WeXLocalizedString(@"重新申请") forState:UIControlStateNormal];
        [applyBtn addTarget:self action:@selector(applyBtnClick) forControlEvents:UIControlEventTouchUpInside];
    }
    //审核成功
    else if ([_orderModel.status isEqualToString:WEX_LOAN_STATUS_APPROVED])
    {
        backImageView.image = [UIImage imageNamed:@"borrow_detail_card_5"];
        statusImageView.image = [UIImage imageNamed:@"borrow_detail_4"];
        //MyBorrowDetailVCOrderSuccessStatus
        desLabel.text = WeXLocalizedString(@"MyBorrowDetailVCOrderSuccessStatus");
        protocolView.hidden = YES;
        applyBtn.hidden = YES;
        repaymentLineView.hidden = YES;
        goRepaymentBtn.hidden = YES;
    }
    //放币失败
    else if ([_orderModel.status isEqualToString:WEX_LOAN_STATUS_FAILURE])
    {
    
        //MyBorrowDetailVCOrderSendFailStatus
        backImageView.image = [UIImage imageNamed:@"borrow_detail_card_2"];
        statusImageView.image = [UIImage imageNamed:@"borrow_detail_1"];
        desLabel.text = WeXLocalizedString(@"MyBorrowDetailVCOrderSendFailStatus");
        protocolView.hidden = YES;
        repaymentLineView.hidden = YES;
        goRepaymentBtn.hidden = YES;
        [applyBtn setTitle:WeXLocalizedString(@"重新申请") forState:UIControlStateNormal];
        [applyBtn addTarget:self action:@selector(applyBtnClick) forControlEvents:UIControlEventTouchUpInside];
    }
    //已放款
    else if ([_orderModel.status isEqualToString:WEX_LOAN_STATUS_DELIVERED])
    {
        backImageView.image = [UIImage imageNamed:@"borrow_detail_card_4"];
        statusImageView.image = [UIImage imageNamed:@"borrow_detail_6"];
         desLabel.text = WeXLocalizedString(@"已放币");
        repaymentLineView.hidden = YES;
        goRepaymentBtn.hidden = YES;
        if (_orderModel.earlyRepayAvailable) {
            if (_orderModel.allowRepayPermit) {
                [applyBtn setTitle:WeXLocalizedString(@"提前还币") forState:UIControlStateNormal];
                [applyBtn addTarget:self action:@selector(repayCoinClick) forControlEvents:UIControlEventTouchUpInside];
            }
            else
            {
                applyBtn.hidden = YES;
            }
            
        }
        else
        {
            [applyBtn setTitle:WeXLocalizedString(@"还币") forState:UIControlStateNormal];
            [applyBtn addTarget:self action:@selector(repayCoinClick) forControlEvents:UIControlEventTouchUpInside];
        }
        
    }
    //已还币
    else if ([_orderModel.status isEqualToString:WEX_LOAN_STATUS_REPAID])
    {
        backImageView.image = [UIImage imageNamed:@"borrow_detail_card_1"];
        statusImageView.image = [UIImage imageNamed:@"borrow_detail_2"];
        repaymentLineView.hidden = YES;
        goRepaymentBtn.hidden = YES;
        desLabel.text = WeXLocalizedString(@"您的订单已处理完毕");
        [applyBtn setTitle:WeXLocalizedString(@"重新申请") forState:UIControlStateNormal];
        [applyBtn addTarget:self action:@selector(applyBtnClick) forControlEvents:UIControlEventTouchUpInside];
    }
}

- (void)protocolClick
{
    WEXNSLOG(@"%s",__func__);
    
    WeXMyBorrowContractWebViewController *ctrl = [[WeXMyBorrowContractWebViewController alloc] init];
    ctrl.orderId = _orderId;
    [self.navigationController pushViewController:ctrl animated:YES];
}

// MARK: - 提前还币
- (void)repayCoinClick{
    WeXPayCoinConfirmViewController *ctrl = [[WeXPayCoinConfirmViewController alloc] init];
    ctrl.orderId = _orderId;
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)applyBtnClick
{
    WeXBorrowProductChooseController *ctrl = [[WeXBorrowProductChooseController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
    
}

- (void)goRepaymentBtnClick{
    WeXRepaymentExplainController *vc = [[WeXRepaymentExplainController alloc]init];
    [self.navigationController pushViewController:vc animated:YES];
}



@end
