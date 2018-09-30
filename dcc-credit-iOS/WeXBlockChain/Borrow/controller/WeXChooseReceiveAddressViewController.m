//
//  WeXChooseReceiveAddressViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXChooseReceiveAddressViewController.h"
#import "WeXChooseReceiveAddressCell.h"
#import "WeXBorrowReceiveAddressModal.h"
#import "WeXAddReceiveAddressViewController.h"

@interface WeXChooseReceiveAddressViewController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
}

@property (nonatomic,strong)NSMutableArray *datasArray;

@end

@implementation WeXChooseReceiveAddressViewController


- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"选择收款地址");
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self updateDatas];
}

-(NSMutableArray *)datasArray
{
    if(!_datasArray)
    {
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
    
}


- (void)updateDatas{
    
    [self.datasArray removeAllObjects];
    RLMResults <WeXBorrowReceiveAddressModal *>*results = [WeXBorrowReceiveAddressModal allObjects];
    for (WeXBorrowReceiveAddressModal *rlmModel in results) {
        WeXBorrowReceiveAddressModal *model = [[WeXBorrowReceiveAddressModal alloc] init];
        model.address =rlmModel.address;
        model.name = rlmModel.name;
        model.isDefault = rlmModel.isDefault;
        [self.datasArray addObject:model];
    }
    [_tableView reloadData];
}

//初始化滚动视图
-(void)setupSubViews{
    
    UIButton *addBtn = [WeXCustomButton button];
    [addBtn setTitle:WeXLocalizedString(@"添加收款地址") forState:UIControlStateNormal];
    [addBtn addTarget:self action:@selector(addBtnClick) forControlEvents:UIControlEventTouchUpInside];
    addBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [self.view addSubview:addBtn];
    [addBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@40);
    }];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight-75) style:UITableViewStyleGrouped];;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    if (@available(iOS 11.0, *)) {
        _tableView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    } else {
        // Fallback on earlier versions
    }
    
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return self.datasArray.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXChooseReceiveAddressCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXChooseReceiveAddressCell" owner:self options:nil] lastObject];
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backView.layer.cornerRadius = 12;
    cell.backView.layer.masksToBounds = YES;
    cell.backView.layer.borderWidth = 1;
    cell.backView.layer.borderColor =ColorWithHex(0xdcdcdc).CGColor;
    WeXBorrowReceiveAddressModal *model = self.datasArray[indexPath.section];
    cell.nameLabel.text = WeXLocalizedString(model.name);
    cell.addressLabel.text = model.address;
    cell.addressLabel.adjustsFontSizeToFitWidth = YES;
    return cell;
    
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXBorrowReceiveAddressModal *model = self.datasArray[indexPath.section];
    if (_chooseAddressBlock) {
        _chooseAddressBlock(model.name,model.address);
    }
    [self.navigationController popViewControllerAnimated:YES];
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

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
    
}

// MARK: - 添加收款地址
- (void)addBtnClick
{
    WeXAddReceiveAddressViewController *ctrl = [[WeXAddReceiveAddressViewController alloc] init];
    ctrl.type = WeXReceiveAddressTypeAdd;
    [self.navigationController pushViewController:ctrl animated:YES];
}

@end
