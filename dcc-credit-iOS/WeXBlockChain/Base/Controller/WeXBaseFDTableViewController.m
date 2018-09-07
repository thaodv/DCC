//
//  WeXBaseFDTableViewController.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseFDTableViewController.h"
#import "UITableView+FDTemplateLayoutCell.h"

@interface WeXBaseFDTableViewController ()<UITableViewDelegate, UITableViewDataSource>


@property (nonatomic, strong) NSMutableDictionary* cellDict;

@end

@implementation WeXBaseFDTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // 创建TableView
    [self initBodyTableView];
    [self initTableViewDataSource];
    
    [self createBodyTableViewRelatedControl];
    [self initBodyTableViewLayoutConstraints];
}

- (void)insertCell:(UITableViewCell*)cell inSection:(NSInteger)section
{
    NSMutableArray* cellSectionArray = [_cellDict objectForKey:[NSNumber numberWithInteger:section]];
    
    if (cellSectionArray == nil)
    {
        cellSectionArray = [NSMutableArray array];
        [_cellDict setObject:cellSectionArray forKey:[NSNumber numberWithInteger:section]];
    }
    
    [cellSectionArray addObject:cell];
}

- (void)insertCell:(UITableViewCell*)cell inSection:(NSInteger)section withIndex:(NSInteger)index
{
    NSMutableArray* cellSectionArray = [_cellDict objectForKey:[NSNumber numberWithInteger:section]];
    
    if (cellSectionArray == nil)
    {
        cellSectionArray = [NSMutableArray array];
        [_cellDict setObject:cellSectionArray forKey:[NSNumber numberWithInteger:section]];
    }
    
    if (index < cellSectionArray.count)
    {
        [cellSectionArray insertObject:cell atIndex:index];
    }
}

// 移除所有的Cells
- (void)removeAllCells
{
    if (_cellDict)
    {
        [_cellDict removeAllObjects];
    }
}

- (void)removeExceptCells:(NSArray*)exceptCells inSection:(NSInteger)section
{
    NSMutableArray* cellSectionArray = [_cellDict objectForKey:[NSNumber numberWithInteger:section]];
    
    if (cellSectionArray == nil)
    {
        return;
    }
    
    NSMutableArray* replaceCells = [NSMutableArray array];
    
    for (UITableViewCell* cell in cellSectionArray)
    {
        if ([exceptCells indexOfObject:cell] != NSNotFound)
        {
            [replaceCells addObject:cell];
        }
    }
    
    [_cellDict setObject:replaceCells forKey:[NSNumber numberWithInteger:section]];
}

// 移除某个Cell
- (void)removeCell:(UITableViewCell*)cell inSection:(NSInteger)section
{
    NSMutableArray* cellSectionArray = [_cellDict objectForKey:[NSNumber numberWithInteger:section]];
    
    if (cellSectionArray)
    {
        [cellSectionArray removeObject:cell];
    }
}

// 获得某个CELL
- (UITableViewCell*)getTableViewCellFromRow:(NSInteger)row andSection:(NSInteger)section
{
    NSMutableArray* cellSectionArray = [_cellDict objectForKey:[NSNumber numberWithInteger:section]];
    
    if (cellSectionArray)
    {
        if (row < [cellSectionArray count])
        {
            return [cellSectionArray objectAtIndex:row];
        }
    }
    
    return nil;
}

// 获得某个Section最后一个Cell
- (UITableViewCell*)getLastCellInSection:(NSInteger)section
{
    NSMutableArray* cellSectionArray = [_cellDict objectForKey:[NSNumber numberWithInteger:section]];
    
    if (cellSectionArray == nil)
    {
        return nil;
    }
    else
    {
        return [cellSectionArray lastObject];
    }
}

- (NSInteger)getTableViewCellCountFromSection:(NSInteger)section
{
    NSMutableArray* cellSectionArray = [_cellDict objectForKey:[NSNumber numberWithInteger:section]];
    
    if (cellSectionArray == nil)
    {
        return 0;
    }
    else
    {
        return [cellSectionArray count];
    }
}

#pragma mark - 设置BodyTableView

- (void)initBodyTableView
{
    _bodyTableView = [[UITableView alloc] init];
    [self.view addSubview:_bodyTableView];
    
    _bodyTableView.delegate = self;
    _bodyTableView.dataSource = self;
    
    _bodyTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    _bodyTableView.showsVerticalScrollIndicator = NO;
    
    // 兼容iOS11
    // http://www.jianshu.com/p/1535b0b8d3d6
    _bodyTableView.estimatedRowHeight = 0;
    _bodyTableView.estimatedSectionHeaderHeight = 0;
    _bodyTableView.estimatedSectionFooterHeight = 0;
}

- (void)initTableViewDataSource
{
    _cellDict = [NSMutableDictionary dictionary];
}

#pragma mark - UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSArray* cellSectionArray = [_cellDict objectForKey:[NSNumber numberWithInteger:section]];
    
    return [cellSectionArray count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [[_cellDict allKeys] count];
}

// CELL高度的处理参考：
// http://blog.csdn.net/xiaoyuertongxue/article/details/43447311
// 例子：TableViewCellWithAutoLayout
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSArray* cellSectionArray = [_cellDict objectForKey:[NSNumber numberWithInteger:indexPath.section]];
    UITableViewCell* cell = [cellSectionArray objectAtIndex:indexPath.row];
    

    return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSArray* cellSectionArray = [_cellDict objectForKey:[NSNumber numberWithInteger:indexPath.section]];
    
    return cellSectionArray[indexPath.row];
}



@end




