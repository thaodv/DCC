//
//  WeXBaseFDTableViewController.h
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

@interface WeXBaseFDTableViewController : WeXBaseViewController

@property (nonatomic, strong) UITableView* bodyTableView;

- (void)insertCell:(UITableViewCell*)cell inSection:(NSInteger)section;
- (void)insertCell:(UITableViewCell*)cell inSection:(NSInteger)section withIndex:(NSInteger)index;
// 移除所有的Cells
- (void)removeAllCells;
- (void)removeExceptCells:(NSArray*)exceptCells inSection:(NSInteger)section;
// 移除某个Cell
- (void)removeCell:(UITableViewCell*)cell inSection:(NSInteger)section;
// 获得某个CELL
- (UITableViewCell*)getTableViewCellFromRow:(NSInteger)row andSection:(NSInteger)section;
// 获得某个Section最后一个Cell
- (UITableViewCell*)getLastCellInSection:(NSInteger)section;
- (NSInteger)getTableViewCellCountFromSection:(NSInteger)section;


// 可覆写
- (void)createBodyTableViewRelatedControl;
- (void)initBodyTableViewLayoutConstraints;


// TableView点击事件，子类如果需要可以覆写，需要先调用父类方法
- (void)wexTableViewTap:(id)sender NS_REQUIRES_SUPER;




@end
