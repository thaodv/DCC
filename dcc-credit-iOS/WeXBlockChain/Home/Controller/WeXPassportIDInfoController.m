//
//  WeXPassportIDInfoController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/2/26.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXPassportIDInfoController.h"
#import "WeXPassportIDInfoCell.h"
#import "WeXPassportIDFaceController.h"

static NSString *cellID = @"cellID";

@interface WeXPassportIDInfoController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
    
    UITextView *_nameTextView;
    UITextView *_numberTextView;
    UITextView *_deadTimeTextView;
}

@property (nonatomic,strong)NSMutableArray *datasArray;

@end

@implementation WeXPassportIDInfoController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    [self getDatas];
}



- (void)getDatas
{
    _datasArray = [NSMutableArray array];
    NSArray *titleArray = @[@"(*)姓名:",@"性别:",@"民族:",@"出生:",@"住址:",@"(*)身份证号:",@"(*)有效期限:",@"签发机关:"];
   WeXPassportIDCacheInfoModal  *infoModel = [WeXPassportIDCacheInfoModal sharedInstance];
    for (int i = 0; i < 8; i++) {
        WeXPassportIDInfoModel *model = [[WeXPassportIDInfoModel alloc] init];
        model.title = titleArray[i];
        if (i == 0) {
            model.content = infoModel.userName;
        }
        else if (i == 1)
        {
             model.content = infoModel.userSex;
        }
        else if (i == 2)
        {
            model.content = infoModel.userNation;
        }
        else if (i == 3)
        {
            NSString *birthday = [NSString stringWithFormat:@"%@年%@月%@日",infoModel.userYear,infoModel.userMonth,infoModel.userDay];
            model.content = birthday;
            
        }
        else if (i == 4)
        {
            model.content = infoModel.userAddress;
        }
        else if (i == 5)
        {
            model.content = infoModel.userNumber;
        }
        else if (i == 6)
        {
            model.content = infoModel.userTimeLimit;
        }
        else if (i == 7)
        {
            model.content = infoModel.userAuthority;
        }
        
        [_datasArray addObject:model];
    }
    [_tableView reloadData];
}

//初始化滚动视图
-(void)setupSubViews{
    
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text = @"数字身份证";
    titleLabel.font = [UIFont systemFontOfSize:25];
    titleLabel.textColor = [UIColor whiteColor];
    titleLabel.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+10);
        make.leading.equalTo(self.view).offset(20);
        make.height.equalTo(@25);
    }];
    
    UILabel *label1 = [[UILabel alloc] init];
    label1.text = @"①拍摄身份证-②信息确认-③采集头像-④完成";
    label1.font = [UIFont systemFontOfSize:14];
    label1.textColor = [UIColor lightGrayColor];
    label1.textAlignment = NSTextAlignmentLeft;
    [self.view addSubview:label1];
    [label1 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.mas_bottom).offset(10);
        make.leading.equalTo(self.view).offset(20);
        make.trailing.equalTo(self.view).offset(-20);
        make.height.equalTo(@20);
    }];
 
    
    WeXCustomButton *nextBtn = [WeXCustomButton button];
    [nextBtn setTitle:@"下一步" forState:UIControlStateNormal];
    nextBtn.layer.cornerRadius = 5;
    nextBtn.layer.masksToBounds = YES;
    [nextBtn setTitleColor:ColorWithRGB(248, 31, 117) forState:UIControlStateNormal];
    [nextBtn addTarget:self action:@selector(nextBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:nextBtn];
    [nextBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(-30);
        make.centerX.equalTo(self.view);
        make.width.equalTo(@170);
        make.height.equalTo(@50);
    }];
    
    _tableView = [[UITableView alloc] init];
    [self.view addSubview:_tableView];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(label1.mas_bottom).offset(10);
        make.bottom.equalTo(nextBtn.mas_top).offset(-10);
        make.leading.trailing.equalTo(self.view);
    }];
    
    UILabel *bottomLabel = [[UILabel alloc]  initWithFrame:CGRectMake(0, 0, kScreenWidth, 30)];
    bottomLabel.text = @"  (*)必填";
    bottomLabel.textColor = [UIColor whiteColor];
    bottomLabel.font = [UIFont systemFontOfSize:15];
    _tableView.tableFooterView = bottomLabel;
    
    
    [_tableView registerNib:[UINib nibWithNibName:@"WeXPassportIDInfoCell" bundle:[NSBundle mainBundle]] forCellReuseIdentifier:cellID];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]  initWithTarget:self action:@selector(tapClick)];
    [_tableView addGestureRecognizer:tap];
    
}

- (void)nextBtnClick{
    
    if (_nameTextView.text.length == 0||[_nameTextView.text isEqualToString:@""]) {
        [WeXPorgressHUD showText:@"姓名不能为空!" onView:self.view];
        return;
    }
    
    if (_numberTextView.text.length == 0||[_numberTextView.text isEqualToString:@""]) {
        [WeXPorgressHUD showText:@"身份证号码不能为空!" onView:self.view];
        return;
    }
    
    
    WeXPassportIDFaceController *ctrl = [[WeXPassportIDFaceController alloc] init];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)tapClick{
    [self.view endEditing:YES];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.datasArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXPassportIDInfoCell * cell = [tableView dequeueReusableCellWithIdentifier:cellID forIndexPath:indexPath];
    if (indexPath.row == 0) {
        _nameTextView = cell.contentTextView;
    }
    else if (indexPath.row == 5)
    {
        _numberTextView = cell.contentTextView;
    }
    else if (indexPath.row == 6)
    {
        
    }
    cell.backgroundColor = [UIColor clearColor];
    WeXPassportIDInfoModel *model = [self.datasArray objectAtIndex:indexPath.row];
    cell.refreshBlock = ^(NSString *content, NSIndexPath *indexPath) {
        WeXPassportIDInfoModel *model = [self.datasArray objectAtIndex:indexPath.row];
        model.content = content;
        [tableView beginUpdates];
        [tableView endUpdates];
    };
    [cell configWithModel:model indexPath:indexPath];
    return cell;
  
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 4) {
        WeXPassportIDInfoModel *model = [self.datasArray objectAtIndex:indexPath.row];
        return [WeXPassportIDInfoCell heightWithModel:model indexPath:indexPath];
    }
    return 44;
    
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}



@end
