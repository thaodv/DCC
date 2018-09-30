//
//  WeXBorrowProductDetailController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowProductDetailController.h"
#import "WeXBorrowProductDetailCell.h"
#import "WeXBorrowConfirmViewController.h"
#import "WeXQueryProductByLenderCodeModal.h"

@interface WeXBorrowProductDetailController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
}

@end

@implementation WeXBorrowProductDetailController

- (void)viewDidLoad {
    [super viewDidLoad];
    NSString *title = nil;
    if ([[WeXLocalizedManager shareManager] isChinese]) {
        title = [NSString stringWithFormat:@"%@%@",WeXLocalizedString(@"BorrowProductTitle"),_productModel.currency.symbol];
    } else {
        title = [NSString stringWithFormat:@"%@%@",_productModel.currency.symbol,WeXLocalizedString(@"BorrowProductTitle")];
    }
    self.navigationItem.title = title;
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}

//初始化滚动视图
-(void)setupSubViews{
    
    UIButton *borrowBtn = [WeXCustomButton button];
    [borrowBtn setTitle:WeXLocalizedString(@"申请借币") forState:UIControlStateNormal];
    [borrowBtn addTarget:self action:@selector(borrowBtnClick) forControlEvents:UIControlEventTouchUpInside];
    borrowBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:borrowBtn];
    [borrowBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.bottom.equalTo(self.view).offset(-30);
        make.height.equalTo(@40);
    }];
    
    _tableView = [[UITableView alloc] init];
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 40;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(borrowBtn.mas_top).offset(-5);
    }];
    
    if (self.productModel.lender.logoUrl) {
        CGFloat headViewHeight = 0;
        NSString *imageUrl = self.productModel.lender.logoUrl;
        NSRange range1 = [imageUrl rangeOfString:@"." options:NSBackwardsSearch];
        NSRange range2 = [imageUrl rangeOfString:@"@" options:NSBackwardsSearch];
        NSRange range3 = NSMakeRange(range2.location+1, range1.location-range2.location-1);
        NSString *sizeStr = [imageUrl substringWithRange:range3];
        NSArray *sizeArray = [sizeStr componentsSeparatedByString:@"_"];
        if (sizeArray.count == 2) {
            CGFloat width  = [sizeArray[0] floatValue];
            CGFloat height = [sizeArray[1] floatValue];
            headViewHeight = (kScreenWidth-160)*height/width;
        }
        UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, headViewHeight)];
        _tableView.tableHeaderView= headerView;
        
        UIImageView *headImageView = [[UIImageView alloc] init];
        [headImageView sd_setImageWithURL:[NSURL URLWithString:self.productModel.lender.logoUrl]];
        [headerView addSubview:headImageView];
        [headImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(headerView).offset(0);
            make.leading.equalTo(self.view).offset(80);
            make.trailing.equalTo(self.view).offset(-80);
            make.bottom.equalTo(headerView);
        }];
    }
    
    
    NSArray *array = _productModel.requisiteCertList;
    UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 40+array.count*30)];
    _tableView.tableFooterView = footerView;

    UIView *backView = [[UIView alloc] init];
    backView.backgroundColor = ColorWithRGB(227, 228, 242);
    [footerView addSubview:backView];
    [backView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.equalTo(footerView);
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
    }];
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = WeXLocalizedString(@"审核资料:");
    titleLabel.textColor = [UIColor blackColor];
    titleLabel.font = [UIFont boldSystemFontOfSize:18];
    [footerView addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(backView).offset(10);
        make.leading.equalTo(backView).offset(10);
        make.height.equalTo(@20);
    }];
    
    UILabel *preLabel = titleLabel;
    for (int i = 0; i < array.count; i++)
    {
        UILabel *contentLabel = [[UILabel alloc] init];
        contentLabel.textColor = COLOR_LABEL_DESCRIPTION;
        contentLabel.font = [UIFont systemFontOfSize:15];
        [footerView addSubview:contentLabel];
        [contentLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(preLabel.mas_bottom).offset(10);
            make.leading.equalTo(backView).offset(10);
            make.height.equalTo(@20);
        }];
        preLabel = contentLabel;
        NSString *itemStr = array[i];
        if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_ID]) {
            contentLabel.text = [NSString stringWithFormat:@"%d.%@",i+1,WeXLocalizedString(@"实名认证;")];
        }
        else if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_BANK_CARD])
        {
            contentLabel.text = [NSString stringWithFormat:@"%d.%@",i+1,WeXLocalizedString(@"银行卡认证;")];
        }
        else if ([itemStr isEqualToString:WEX_DCC_AUTHEN_BUSINESS_COMMUNICATION_LOG])
        {
            contentLabel.text = [NSString stringWithFormat:@"%d.%@",i+1,WeXLocalizedString(@"运营商认证;")];
        }
    }
}

// MARK: - 借币申请
- (void)borrowBtnClick
{
    WeXBorrowConfirmViewController *ctrl = [[WeXBorrowConfirmViewController alloc] init];
    ctrl.productModel = self.productModel;
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
   
    return self.productModel.repayPermit?5:3;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXBorrowProductDetailCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXBorrowProductDetailCell" owner:self options:nil] objectAtIndex:0];
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    cell.titleLabel.textColor = COLOR_LABEL_DESCRIPTION;
    cell.titleLabel.font = [UIFont systemFontOfSize:18];
    
    cell.contentLabel.textColor = COLOR_LABEL_DESCRIPTION;
    cell.contentLabel.font = [UIFont systemFontOfSize:18];
   
    if (indexPath.row == 0) {
        cell.titleLabel.text = WeXLocalizedString(@"额度");
        NSArray *valueArray = _productModel.volumeOptionList;
        if (valueArray.count >= 3) {
            cell.contentLabel.text = [NSString stringWithFormat:@"%@-%@%@",valueArray[0],valueArray[2],_productModel.currency.symbol];
            
        }
    }
    else if (indexPath.row == 1)
    {
        cell.titleLabel.text = WeXLocalizedString(@"周期");
        NSMutableArray *periodArray = _productModel.loanPeriodList;
        if (periodArray.count >= 3) {
            WeXQueryProductByLenderCodeResponseModal_period *periodModel1 = periodArray[0];
            WeXQueryProductByLenderCodeResponseModal_period *periodModel3 = periodArray[2];
            NSMutableString *periodStr = [NSMutableString stringWithFormat:@"%@-%@%@",periodModel1.value,periodModel3.value,[WexCommonFunc transferChinesePeriod:periodModel3.unit]];
            cell.contentLabel.text = [NSString stringWithFormat:@"%@",periodStr];
        }

    }
    else if (indexPath.row == 2)
    {
        cell.titleLabel.text = WeXLocalizedString(@"利率");
        NSString *text;
        if ([[WeXLocalizedManager shareManager] isChinese]) {
            text = [NSString stringWithFormat:@"%@%.2f%@",WeXLocalizedString(@"日利率"),[_productModel.loanRate floatValue]*100/365.0,@"%"];
        } else {
            text = [NSString stringWithFormat:@"%@%.2f%@",@"%",[_productModel.loanRate floatValue]*100/365.0,WeXLocalizedString(@"日利率")];
        }
        cell.contentLabel.text = text ;
    }
    else if (indexPath.row == 3)
    {
        cell.titleLabel.text = WeXLocalizedString(@"提前还币");
        cell.contentLabel.text = WeXLocalizedString(@"允许");
    }
    else if (indexPath.row == 4)
    {
        cell.titleLabel.text = WeXLocalizedString(@"提前还币手续费");
        cell.contentLabel.text = [NSString stringWithFormat:@"%@%.2f%@",WeXLocalizedString(@"本金"),[_productModel.repayAheadRate floatValue]*100,@"%"];
    }
    return cell;
    
}

@end
