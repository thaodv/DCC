//
//  WeXReceiveAddressManagerController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXReceiveAddressManagerController.h"
#import "WeXReceiveAddressManagerCell.h"
#import "WeXAddReceiveAddressViewController.h"
#import "WeXBorrowReceiveAddressModal.h"
#import "WeXDeleteReceivieAddressToastView.h"

#import "WeXChooseReceiveAddressViewController.h"//回头要删


static const NSInteger defaultButtonTag = 100;

static const NSInteger editButtonTag = 200;

static const NSInteger deleteButtonTag = 300;

@interface WeXReceiveAddressManagerController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
}

@property (nonatomic,strong)NSMutableArray *datasArray;

@end

@implementation WeXReceiveAddressManagerController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"收款地址管理");
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
        model.name = WeXLocalizedString(rlmModel.name);
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
        make.bottom.equalTo(self.view).offset(-40);
        make.trailing.equalTo(self.view).offset(-20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@40);
    }];
    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, kNavgationBarHeight, kScreenWidth, kScreenHeight-kNavgationBarHeight-85) style:UITableViewStyleGrouped];;
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
    WeXReceiveAddressManagerCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXReceiveAddressManagerCell" owner:self options:nil] lastObject];
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backView.layer.cornerRadius = 12;
    cell.backView.layer.masksToBounds = YES;
    cell.backView.layer.borderWidth = 1;
    cell.backView.layer.borderColor =ColorWithHex(0xdcdcdc).CGColor;
    WeXBorrowReceiveAddressModal *model = self.datasArray[indexPath.section];
    cell.nameAddressLabel.text = model.name;
    cell.addressLabel.text = model.address;
    cell.addressLabel.adjustsFontSizeToFitWidth = YES;
    if (model.isDefault) {
        [cell.defaultButton setImage:[UIImage imageNamed:@"borrow_select_address"] forState:UIControlStateNormal];
    }
    else
    {
        [cell.defaultButton setImage:[UIImage imageNamed:@"borrow_unselect_address"] forState:UIControlStateNormal];
    }
    if (indexPath.section == 0) {
        cell.editButton.hidden = YES;
        cell.deleteButton.hidden = YES;
    }
    
    [cell.defaultButton addTarget:self action:@selector(defaultButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    cell.defaultButton.tag = defaultButtonTag+indexPath.section;
    [cell.editButton addTarget:self action:@selector(editButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    cell.editButton.tag = editButtonTag+indexPath.section;
    [cell.deleteButton addTarget:self action:@selector(deleteButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    cell.deleteButton.tag = deleteButtonTag+indexPath.section;
    return cell;
   
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
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
    return 120;
    
}

- (void)defaultButtonClick:(UIButton *)button
{
    NSInteger index = button.tag - defaultButtonTag;
    WeXBorrowReceiveAddressModal *model = self.datasArray[index];
    if (model.isDefault) {
        return;
    }
    
    [self clearCacheModelDefaultStatus];
    [self updateCacheModelDefaultStatusWithIndex:index];
    [self updateDatas];

}

- (void)clearCacheModelDefaultStatus
{
    RLMRealm *realm = [RLMRealm defaultRealm];
    RLMResults <WeXBorrowReceiveAddressModal *>*results = [WeXBorrowReceiveAddressModal allObjects];
    for (WeXBorrowReceiveAddressModal *rlmModel in results) {
        [realm beginWriteTransaction];
        rlmModel.isDefault = NO;
        [realm commitWriteTransaction];
    }
}

- (void)updateCacheModelDefaultStatusWithIndex:(NSInteger)index
{
    RLMRealm *realm = [RLMRealm defaultRealm];
    RLMResults <WeXBorrowReceiveAddressModal *>*results = [WeXBorrowReceiveAddressModal allObjects];
    WeXBorrowReceiveAddressModal *model = [results objectAtIndex:index];
    [realm beginWriteTransaction];
    model.isDefault = YES;
    [realm commitWriteTransaction];
}

- (void)editButtonClick:(UIButton *)button
{
    NSInteger index = button.tag - editButtonTag;
    WeXAddReceiveAddressViewController *ctrl = [[WeXAddReceiveAddressViewController alloc] init];
    ctrl.type = WeXReceiveAddressTypeModify;
    ctrl.index = index;
    [self.navigationController pushViewController:ctrl animated:YES];
}




- (void)deleteButtonClick:(UIButton *)button
{
    NSInteger index = button.tag - deleteButtonTag;
    WeXBorrowReceiveAddressModal *model = self.datasArray[index];
    WeXDeleteReceivieAddressToastView *deleteView = [[WeXDeleteReceivieAddressToastView alloc] initWithFrame:self.view.bounds];
    [deleteView configWithModel:model];
    [self.view addSubview:deleteView];
    deleteView.deleteButtonClickBlock = ^{
        [self deleteCacheModelWithIndex:index];
        [self updateDatas];
    };
    
}

- (void)deleteCacheModelWithIndex:(NSInteger)index
{
    
    RLMRealm *realm = [RLMRealm defaultRealm];
    RLMResults <WeXBorrowReceiveAddressModal *>*results = [WeXBorrowReceiveAddressModal allObjects];
    if (results.count == 0) {
        return;
    }
    WeXBorrowReceiveAddressModal *model = [results objectAtIndex:index];
    WeXBorrowReceiveAddressModal *firstModel = [results objectAtIndex:0];
    [realm beginWriteTransaction];
    if (model.isDefault) {
         firstModel.isDefault = YES;
    }
    [realm deleteObject:model];
    [realm commitWriteTransaction];
}

- (void)addBtnClick
{
    WeXAddReceiveAddressViewController *ctrl = [[WeXAddReceiveAddressViewController alloc] init];
    ctrl.type = WeXReceiveAddressTypeAdd;
    [self.navigationController pushViewController:ctrl animated:YES];
    
}



@end
