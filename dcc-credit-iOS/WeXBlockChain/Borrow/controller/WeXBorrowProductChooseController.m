//
//  WeXBorrowProductChooseController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBorrowProductChooseController.h"
#import "WeXBorrowProductDetailController.h"
#import "WeXBorrowProductChooseCell.h"
#import "WeXQueryProductByLenderCodeAdapter.h"
#import "WeXGetDefaultLoanerAdapter.h"
#import "WeXCreditBorrowUSDTViewController.h"

static const CGFloat kHeaderHeightWidthRatio = 159.0/341.0;

@interface WeXBorrowProductChooseController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
}

@property (nonatomic,strong)NSMutableArray *datasArray;
@property (nonatomic,strong)WeXGetDefaultLoanerAdapter *queryLoaderAdapter;
@property (nonatomic,strong)WeXQueryProductByLenderCodeAdapter *queryProductsAdapter;

@end

@implementation WeXBorrowProductChooseController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"我要借币");
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    [self createQueryProductsRequest];
}


- (void)backItemClick
{
    for (UIViewController *ctrl in self.navigationController.viewControllers) {
        if ([ctrl isKindOfClass:[WeXCreditBorrowUSDTViewController class]]) {
            [self.navigationController popToViewController:ctrl animated:YES];
        }
    }
}


#pragma -mark 发送QueryProducts请求
- (void)createQueryProductsRequest{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    _queryProductsAdapter = [[WeXQueryProductByLenderCodeAdapter alloc] init];
    _queryProductsAdapter.delegate = self;
    WeXQueryProductByLenderCodeParamModal* paramModal = [[WeXQueryProductByLenderCodeParamModal alloc] init];
    [_queryProductsAdapter run:paramModal];
}


#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _queryProductsAdapter){
        [WeXPorgressHUD hideLoading];
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            WeXQueryProductByLenderCodeResponseModal *model = (WeXQueryProductByLenderCodeResponseModal *)response;
            NSLog(@"%@",model);
            self.datasArray = model.resultList;
        }
        else {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:WeXLocalizedString(@"系统错误，请稍后再试!") onView:self.view];
        }
        [_tableView reloadData];
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

-(NSMutableArray *)datasArray {
    if(!_datasArray) {
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
}

//初始化滚动视图
-(void)setupSubViews{
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight) style:UITableViewStyleGrouped];;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 120;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    if (@available(iOS 11.0, *)) {
        _tableView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    } else {
        // Fallback on earlier versions
    }
    
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, _tableView.frame.size.width, (kScreenWidth-30)*kHeaderHeightWidthRatio)];
    _tableView.tableHeaderView= headerView;

    UIImageView *headImageView = [[UIImageView alloc] init];
    headImageView.image = [UIImage imageNamed:@"borrow_loader_logo"];
    [headerView addSubview:headImageView];
    [headImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(headerView);
        make.leading.equalTo(headerView).offset(15);
        make.trailing.equalTo(headerView).offset(-15);
        make.height.equalTo(headImageView.mas_width).multipliedBy(kHeaderHeightWidthRatio);
    }];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.datasArray.count+1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.section == self.datasArray.count) {
        WeXBorrowProductChooseEmailCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXBorrowProductChooseCell" owner:self options:nil] lastObject];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.emailLabel.text = @"yimtoken1@163.com";
        cell.titleLabel.text = WeXLocalizedString(@"BorrowProduct-Contact");
        return cell;
    }
    else {
        WeXBorrowProductChooseCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXBorrowProductChooseCell" owner:self options:nil] firstObject];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        WeXQueryProductByLenderCodeResponseModal_item *model = self.datasArray[indexPath.section];
        cell.model = model;
        return cell;
    }
    return nil;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
//    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
//    NSLog(@"isHighlighted=%d",cell.isHighlighted);
    
    if (indexPath.section == self.datasArray.count) {
        NSString *mailStr = [NSString stringWithFormat:@"mailto://%@",@"yimtoken1@163.com"];
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:mailStr] options:nil completionHandler:^(BOOL success) {
            NSLog(@"success");
        }];
    }
    else {
        WeXQueryProductByLenderCodeResponseModal_item *model = self.datasArray[indexPath.section];
        WeXBorrowProductDetailController *ctrl = [[WeXBorrowProductDetailController alloc] init];
        ctrl.productModel = model;
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 10;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 10;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    return nil;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return nil;
}


@end
