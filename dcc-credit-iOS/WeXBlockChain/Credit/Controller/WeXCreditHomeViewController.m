//
//  WeXCreditHomeViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/3/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCreditHomeViewController.h"
#import "WeXCreditHomeCell.h"

@interface WeXCreditHomeViewController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
}

@end

@implementation WeXCreditHomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"我的信用";
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}


//初始化滚动视图
-(void)setupSubViews{
    
    _tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStyleGrouped];;
    UILabel *desLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, _tableView.frame.size.width, 20)];
    desLabel.text = @"完成全部资料验证，即可申请数字资产借款";
    desLabel.textAlignment = NSTextAlignmentCenter;
    desLabel.font = [UIFont systemFontOfSize:10];
    desLabel.textColor = COLOR_LABEL_DESCRIPTION;
    desLabel.backgroundColor = COLOR_LABEL_DES_BACKGROUND;
    _tableView.tableHeaderView= desLabel;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 150;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 4;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellID = @"cellID";
    WeXCreditHomeCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXCreditHomeCell" owner:self options:nil] lastObject];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.backView.layer.borderWidth = 1;
        cell.backView.layer.borderColor = COLOR_LABEL_DESCRIPTION.CGColor;
    }
    
    return cell;
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
  
//    WeXWalletDigitalRecordDetailController *ctrl = [[WeXWalletDigitalRecordDetailController alloc] init];
//    ctrl.recordModel = recordModel;
//    ctrl.tokenModel = self.tokenModel;
//    [self.navigationController pushViewController:ctrl animated:YES];
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 5;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 5;
}


@end
