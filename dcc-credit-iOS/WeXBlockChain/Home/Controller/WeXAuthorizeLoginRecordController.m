//
//  WeXAuthorizeLoginRecordController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/12/5.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXAuthorizeLoginRecordController.h"
#import "WeXAuthorizeLoginRecordRLMModel.h"
#import "WeXAuthorizeLoginRecordCell.h"

@interface WeXAuthorizeLoginRecordController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
}

@property (nonatomic,strong)NSMutableArray *datasArray;

@end

@implementation WeXAuthorizeLoginRecordController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    [self getAllLoginRecordType];
}

-(NSMutableArray *)datasArray
{
    if (_datasArray == nil) {
        _datasArray = [NSMutableArray array];
    }
    return _datasArray;
}

- (void)getAllLoginRecordType{
    
    RLMResults<WeXAuthorizeLoginRecordRLMModel *> *results = [WeXAuthorizeLoginRecordRLMModel allObjects];
    [self.datasArray removeAllObjects];
    NSLog(@"count=%lu",(unsigned long)results.count);
    for (WeXAuthorizeLoginRecordRLMModel *model in results) {
        [self.datasArray addObject:model];
    }
    
    self.datasArray = (NSMutableArray *)[[self.datasArray reverseObjectEnumerator] allObjects];

    [_tableView reloadData];
}

- (void)setupSubViews{
    _tableView = [[UITableView alloc] init];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _tableView.backgroundColor = [UIColor clearColor];
  
    [self.view addSubview:_tableView];
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.trailing.equalTo(self.view);
        make.bottom.equalTo(self.view);
    }];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.datasArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellID = @"cellID";
    WeXAuthorizeLoginRecordCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXAuthorizeLoginRecordCell" owner:self options:nil] lastObject];
    }
    cell.backgroundColor = [UIColor clearColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    WeXAuthorizeLoginRecordRLMModel *model = [self.datasArray objectAtIndex:indexPath.row];
    cell.model = model;
    return cell;
    
}




@end
