//
//  WeXWalletDigitalAssetAddController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/1/18.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXWalletDigitalAssetAddController.h"
#import "WeXWalletDigitalAddCell.h"
#import "WeXWalletDigitalGetTokenAdapter.h"
#import "MJRefresh.h"
#import "WeXWalletDigitalAssetDetailController.h"
#import "WeXNewTokenViewController.h"
#import "YYKeyboardManager.h"

@interface WeXWalletDigitalAssetAddController ()<UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate,YYKeyboardObserver>
{
    UITableView *_tableView;
    UITextField *_searchTextField;
    MJRefreshBackNormalFooter* _footer;
    UIButton *_commitBtn;
    
    NSString *_search;
}

@property (nonatomic,strong)WeXWalletDigitalGetTokenAdapter *getTokenAdapter;

@property (nonatomic,strong)NSMutableArray *datasArray;

@end

@implementation WeXWalletDigitalAssetAddController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
//    [self setupNavgationType];
    [self setupSubViews];
    
    [[YYKeyboardManager defaultManager] addObserver:self];

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [_tableView reloadData];
}


- (void)createGetTokenRequest{
    _getTokenAdapter = [[WeXWalletDigitalGetTokenAdapter alloc]  init];
    _getTokenAdapter.delegate = self;
    WeXWalletDigitalGetTokenParamModal *paramModal = [[WeXWalletDigitalGetTokenParamModal alloc] init];
    if(_search == nil||[_search isEqualToString:@""])
    {
        return;
    }
    paramModal.search = _search;
    NSLog(@"%@",_searchTextField.text);
    paramModal.pageNo = @"1";
    paramModal.pageSize = @"1000";
    [_getTokenAdapter run:paramModal];
}

- (void)loadMoreDataForRefreshFooter
{
    WeXWalletDigitalGetTokenParamModal *paramModal = [[WeXWalletDigitalGetTokenParamModal alloc] init];
    paramModal.search = _searchTextField.text;
    paramModal.pageNo = [NSString stringWithFormat:@"%ld",_getTokenAdapter.pageNo+1];
    [_getTokenAdapter run:paramModal];
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getTokenAdapter) {
//        [_footer endRefreshing];
        WeXWalletDigitalGetTokenResponseModal *model = (WeXWalletDigitalGetTokenResponseModal *)response;
        _getTokenAdapter.pageNo = model.pageNo;
//        if (model.pageNo==1&&model.items.count > 0) {
//            _footer = [MJRefreshBackNormalFooter footerWithRefreshingTarget:self refreshingAction:@selector(loadMoreDataForRefreshFooter)];
//            _tableView.mj_footer = _footer;
//        }
        
        if (model.items.count == 0) {
           
            [_footer setTitle:@"没有更多数据了" forState:MJRefreshStateNoMoreData];
            
            [_footer endRefreshingWithNoMoreData];
        }
        [self.datasArray removeAllObjects];
        for (WeXWalletDigitalGetTokenResponseModal_item *itemModel in model.items) {
            [self.datasArray addObject:itemModel];
        }
        
        [_tableView reloadData];
    }
}

-(NSMutableArray *)datasArray
{
    if (_datasArray == nil) {
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
}

- (void)setupNavgationType{
    
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    cancelBtn.frame = CGRectMake(0, 0, 50, 20);
    [cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
    [cancelBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelBtnClick) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *leftItem = [[UIBarButtonItem alloc] initWithCustomView:cancelBtn];
    self.navigationItem.leftBarButtonItem = leftItem;
    
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"添加数字资产";
    titleLabel.font = [UIFont systemFontOfSize:24];
    titleLabel.textColor = ColorWithLabelDescritionBlack;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+20);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@25);
    }];
    
    UITextField *searchTextField = [[UITextField alloc] init];
    [searchTextField  becomeFirstResponder];
    UIView *leftView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 30, 40)];
    UIImageView *leftImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 14, 14)];
    leftImageView.image = [UIImage imageNamed:@"digital_search"];
    leftImageView.WeX_centerX = leftView.WeX_centerX;
    leftImageView.WeX_centerY = leftView.WeX_centerY;
    [leftView addSubview:leftImageView];
    
    UIView *rightView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 30, 40)];
    UIButton *chaBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    chaBtn.frame = CGRectMake(0, 0, 14, 14);
    [chaBtn setImage:[UIImage imageNamed:@"digital_cha1"] forState:UIControlStateNormal];
    [chaBtn addTarget:self action:@selector(chaBtnClick) forControlEvents:UIControlEventTouchUpInside];
    chaBtn.WeX_centerX = rightView.WeX_centerX;
    chaBtn.WeX_centerY = rightView.WeX_centerY;
    [rightView addSubview:chaBtn];
    
    searchTextField.delegate = self;
    searchTextField.leftView = leftView;
    searchTextField.rightView = rightView;
    searchTextField.rightViewMode = UITextFieldViewModeWhileEditing;
    searchTextField.leftViewMode = UITextFieldViewModeAlways;
    searchTextField.textColor = ColorWithLabelDescritionBlack;
    searchTextField.font = [UIFont systemFontOfSize:17];
    searchTextField.borderStyle = UITextBorderStyleNone;
    searchTextField.layer.borderWidth = 1;
    searchTextField.layer.borderColor = ColorWithButtonRed.CGColor;
    searchTextField.layer.cornerRadius = 4;
    searchTextField.layer.masksToBounds = YES;
    searchTextField.placeholder = @"搜索Token名称、简写或合约地址";
    searchTextField.backgroundColor = [UIColor clearColor];
    searchTextField.returnKeyType = UIReturnKeySearch;
    
    [self.view addSubview:searchTextField];
    [searchTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(15);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(@40);
    }];
    _searchTextField = searchTextField;
    
    WeXCustomButton *commitBtn = [WeXCustomButton button];
    [commitBtn setTitle:@"提交新token" forState:UIControlStateNormal];
    [commitBtn addTarget:self action:@selector(commitBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:commitBtn];
    [commitBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@170);
        make.height.equalTo(@50);
    }];
    _commitBtn = commitBtn;

    
    _tableView = [[UITableView alloc] init];
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.rowHeight = 70;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(searchTextField.mas_bottom).offset(20);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(commitBtn.mas_top);
    }];
    
    
}

- (void)commitBtnClick
{
    WeXNewTokenViewController *ctrl = [[WeXNewTokenViewController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}


- (void)chaBtnClick{
    _searchTextField.text = @"";
}

- (void)cancelBtnClick
{
    [self.navigationController popViewControllerAnimated:YES];
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.datasArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellID = @"cellID";
    WeXWalletDigitalAddCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXWalletDigitalAddCell" owner:self options:nil] lastObject];
        cell.backgroundColor = [UIColor clearColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[indexPath.row];
    cell.model = model;
    return cell;
    
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXWalletDigitalGetTokenResponseModal_item *model = self.datasArray[indexPath.row];
    WeXWalletDigitalAssetDetailController *ctrl = [[WeXWalletDigitalAssetDetailController alloc] init];
    ctrl.tokenModel = model;
    [self.navigationController pushViewController:ctrl animated:YES];
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

#pragma mark -

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField.text.length>0&&![textField.text isEqual:@""]) {
         _search = textField.text;
        [self createGetTokenRequest];
    }
    [self.view endEditing:YES];
    return YES;
}

-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    
    NSString *comment;
    if(range.length == 0)
    {
        comment = [NSString stringWithFormat:@"%@%@",textField.text, string];
    }
    else
    {
        comment = [textField.text substringToIndex:textField.text.length -range.length];
    }
    
    _search = comment;
    
    [self createGetTokenRequest];
    
    return YES;
}

- (void)keyboardChangedWithTransition:(YYKeyboardTransition)transition {
    [UIView animateWithDuration:transition.animationDuration delay:0 options:transition.animationOption animations:^{
        ///用此方法获取键盘的rect
        CGRect kbFrame = [[YYKeyboardManager defaultManager] convertRect:transition.toFrame toView:self.view];
        ///从新计算view的位置并赋值
        CGFloat y = kbFrame.origin.y;
        [_commitBtn mas_updateConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(self.view).offset(-(kScreenHeight-y)-30);
            
        }];
        [self.view layoutIfNeeded];
        
    } completion:^(BOOL finished) {
        
    }];
    
  
    
}



-(void)dealloc
{
    [[YYKeyboardManager defaultManager] removeObserver:self];
}




@end
