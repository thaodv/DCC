//
//  WeXAddReceiveAddressViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXAddReceiveAddressViewController.h"
#import "WeXAddReceiveAddressCell.h"
#import "WeXScanViewController.h"
#import "WeXBorrowReceiveAddressModal.h"

@interface WeXAddReceiveAddressViewController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
    UITextField *_nameTextField;
    UITextField *_addressTextField;
    UIButton *_defaultButton;
}

@end

@implementation WeXAddReceiveAddressViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    if (self.type == WeXReceiveAddressTypeAdd) {
        self.navigationItem.title = WeXLocalizedString(@"添加收款地址");
    }
    else
    {
        self.navigationItem.title = WeXLocalizedString(@"修改收款地址");
    }
    
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
}

//初始化滚动视图
-(void)setupSubViews{
    
    _tableView = [[UITableView alloc] init];
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 50;
    _tableView.scrollEnabled = NO;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.bottom.trailing.equalTo(self.view);
    }];
    
    
    UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0,0,_tableView.frame.size.width, 100)];
    _tableView.tableFooterView= footerView;
    
    UIButton *addBtn = [WeXCustomButton button];
    if (self.type == WeXReceiveAddressTypeAdd) {
        [addBtn setTitle:WeXLocalizedString(@"添加") forState:UIControlStateNormal];
    }
    else
    {
        [addBtn setTitle:WeXLocalizedString(@"修改") forState:UIControlStateNormal];
    }
    [addBtn addTarget:self action:@selector(addBtnClick) forControlEvents:UIControlEventTouchUpInside];
    addBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [footerView addSubview:addBtn];
    [addBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.leading.equalTo(self.view).offset(15);
        make.trailing.equalTo(self.view).offset(-15);
        make.bottom.equalTo(footerView).offset(0);
        make.height.equalTo(@40);
    }];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapClick)];
    [_tableView addGestureRecognizer:tap];
    
    
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
   
    return 3;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
   
    if (indexPath.row == 0) {
        WeXAddReceiveAddressTextFiedCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXAddReceiveAddressCell" owner:self options:nil] objectAtIndex:1];
        cell.nameTextField.placeholder = WeXLocalizedString(@"请设置备注");
        UILabel *leftLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,0,80,50)];
        leftLabel.text = WeXLocalizedString(@"备注");
        leftLabel.font = [UIFont systemFontOfSize:17];
        leftLabel.textColor = COLOR_LABEL_DESCRIPTION;
        cell.nameTextField.leftViewMode = UITextFieldViewModeAlways;
        cell.nameTextField.leftView = leftLabel;
        cell.nameTextField.font = [UIFont systemFontOfSize:15];
        _nameTextField = cell.nameTextField;
        if (self.type == WeXReceiveAddressTypeModify) {
            RLMResults <WeXBorrowReceiveAddressModal *>*results = [WeXBorrowReceiveAddressModal allObjects];
            WeXBorrowReceiveAddressModal *model = [results objectAtIndex:_index];
            _nameTextField.text = model.name;
        }
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        [_nameTextField setValue:COLOR_LABEL_DESCRIPTION forKeyPath:@"_placeholderLabel.textColor"];
        _nameTextField.textColor = COLOR_LABEL_DESCRIPTION;
        return cell;
    }
    else if (indexPath.row == 1)
    {
        WeXAddReceiveAddressTextFiedAndButtonCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXAddReceiveAddressCell" owner:self options:nil] objectAtIndex:2];
        cell.addressTextField.placeholder = WeXLocalizedString(@"请输入收款地址");
        UILabel *leftLabel = [[UILabel alloc] initWithFrame:CGRectMake(0,0,80,50)];
        leftLabel.text = WeXLocalizedString(@"收款地址");
        leftLabel.font = [UIFont systemFontOfSize:17];
        leftLabel.textColor = COLOR_LABEL_DESCRIPTION;
        cell.addressTextField.leftViewMode = UITextFieldViewModeAlways;
        cell.addressTextField.leftView = leftLabel;
        [cell.addressTextField setValue:COLOR_LABEL_DESCRIPTION forKeyPath:@"_placeholderLabel.textColor"];
        cell.addressTextField.textColor = COLOR_LABEL_DESCRIPTION;
        cell.addressTextField.font = [UIFont systemFontOfSize:15];
        cell.backgroundColor = [UIColor clearColor];
        _addressTextField = cell.addressTextField;
        _addressTextField.adjustsFontSizeToFitWidth = YES;
        if (self.type == WeXReceiveAddressTypeModify) {
            RLMResults <WeXBorrowReceiveAddressModal *>*results = [WeXBorrowReceiveAddressModal allObjects];
            WeXBorrowReceiveAddressModal *model = [results objectAtIndex:_index];
            _addressTextField.text = model.address;
        }
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        [cell.scanButton addTarget:self action:@selector(scanButtonClick) forControlEvents:UIControlEventTouchUpInside];
        return cell;
    }
    else if (indexPath.row == 2)
    {
        WeXAddReceiveAddressLabelAndButtonCell *cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXAddReceiveAddressCell" owner:self options:nil] objectAtIndex:3];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        [cell.defaultButton addTarget:self action:@selector(defaultButtonClick:) forControlEvents:UIControlEventTouchUpInside];
        cell.defaultButton.selected = NO;
        _defaultButton = cell.defaultButton;
        if (self.type == WeXReceiveAddressTypeModify) {
            RLMResults <WeXBorrowReceiveAddressModal *>*results = [WeXBorrowReceiveAddressModal allObjects];
            WeXBorrowReceiveAddressModal *model = [results objectAtIndex:self.index];
            if (model.isDefault) {
                 cell.defaultButton.selected = YES;
            }
        }
        [_defaultButton setImage:_defaultButton.selected?[UIImage imageNamed:@"borrow_normal_address"]:[UIImage imageNamed:@"borrow_unnormal_address"] forState:UIControlStateNormal];
        cell.defaultLabel.textColor = COLOR_LABEL_DESCRIPTION;
        return cell;
    }
    return nil;
    
}

- (void)scanButtonClick{
    WeXScanViewController *ctrl = [[WeXScanViewController alloc]  init];
    ctrl.handleType = WeXScannerHandleTypeManagerReceiveAddress;
    ctrl.responseBlock = ^(NSString *content) {
        if (content) {
            _addressTextField.text = content;
        }
    };
    [self.navigationController pushViewController:ctrl animated:YES];
}


- (void)defaultButtonClick:(UIButton *)button
{
    button.selected = !button.selected;
    [button setImage:button.selected?[UIImage imageNamed:@"borrow_normal_address"]:[UIImage imageNamed:@"borrow_unnormal_address"] forState:UIControlStateNormal];
}

- (void)addBtnClick
{
    [self.view endEditing:YES];
    
    if (![self verifyJumpCondition]) return;
    
    if (self.type == WeXReceiveAddressTypeAdd) {
        [self addReceiveAddress];
        [WeXPorgressHUD showText:WeXLocalizedString(@"添加成功") onView:self.view];
    }
    else
    {
        [self modifyReceiveAddress];
        [WeXPorgressHUD showText:WeXLocalizedString(@"修改成功") onView:self.view];
    }
    
   
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self.navigationController popViewControllerAnimated:YES];
    });
    
}

- (void)modifyReceiveAddress
{
    RLMRealm *realm = [RLMRealm defaultRealm];
    RLMResults <WeXBorrowReceiveAddressModal *>*results = [WeXBorrowReceiveAddressModal allObjects];
    WeXBorrowReceiveAddressModal *model = [results objectAtIndex:_index];
    if (_defaultButton.isSelected) {
        
        [self clearCacheModelDefaultStatus];
        
        [realm beginWriteTransaction];
        model.name = _nameTextField.text;
        model.address = _addressTextField.text;
        model.isDefault = YES;
        [realm commitWriteTransaction];
    }
    else
    {
        if (model.isDefault) {
            WeXBorrowReceiveAddressModal *firstModel = [results objectAtIndex:0];
            [realm beginWriteTransaction];
            firstModel.isDefault = YES;
            model.name = _nameTextField.text;
            model.address = _addressTextField.text;
            model.isDefault = NO;
            [realm commitWriteTransaction];
        }
        else
        {
            [realm beginWriteTransaction];
            model.name = _nameTextField.text;
            model.address = _addressTextField.text;
            model.isDefault = NO;
            [realm commitWriteTransaction];
        }
        
    }
   
}

- (void)addReceiveAddress
{
    RLMRealm *realm = [RLMRealm defaultRealm];
    WeXBorrowReceiveAddressModal *model = [[WeXBorrowReceiveAddressModal alloc] init];
    model.name = _nameTextField.text;
    model.address = _addressTextField.text;
    if (_defaultButton.isSelected) {
        model.isDefault = YES;
        [self clearCacheModelDefaultStatus];
    }
    else
    {
        model.isDefault = NO;
    }
    
    [realm beginWriteTransaction];
    [realm addObject:model];
    [realm commitWriteTransaction];
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

- (BOOL)verifyJumpCondition
{
    if (!_nameTextField.text||_nameTextField.text.length == 0) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"姓名不能为空!") onView:self.view];
        return NO;
    }
    
    if (_nameTextField.text.length > 10) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"姓名的长度不能大于10位!") onView:self.view];
        return NO;
    }
    
    if (!_addressTextField.text||_addressTextField.text.length == 0) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"地址不能为空!") onView:self.view];
        return NO;
    }
    
    if (![WexCommonFunc checkoutString:_addressTextField.text withRegularExpression:Regex_ETHPassportAddress]) {
        [WeXPorgressHUD showText:WeXLocalizedString(@"地址格式不对!") onView:self.view];
        return NO;
    }
    
    return YES;
}

- (void)tapClick
{
    [self.view endEditing:YES];
}





@end
