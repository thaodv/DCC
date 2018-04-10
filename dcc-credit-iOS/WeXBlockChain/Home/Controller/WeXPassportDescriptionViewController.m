//
//  WeXPassportDescriptionViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2017/11/21.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXPassportDescriptionViewController.h"
#import "WeXPassportDescriptionCell.h"
#import "WeXPassportDescriptionModal.h"

@interface WeXPassportDescriptionViewController ()<UITableViewDelegate,UITableViewDataSource>
{
    UITableView *_tableView;
}

@property (nonatomic,strong)NSMutableArray *datasArray;//记录是否展开的数组

@end

@implementation WeXPassportDescriptionViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"口袋介绍";
    [self setNavigationNomalBackButtonType];
    [self addDatas];
    [self setupSubViews];
}

- (void)addDatas{
    self.datasArray = [NSMutableArray array];
    WeXPassportDescriptionModal *model1 = [[WeXPassportDescriptionModal alloc] init];
    model1.content = @"数字社会的数字身份证\n随着信息技术的发展，数字社会正在形成，数字身份将深刻影响未来社会的方方面面。全向口袋的口袋就是您在数字社会的数字身份证，它可以有效的在区块链中认定持有人的唯一身份，黑客无法通过反向计算等方式仿冒口袋持有人的唯一身份";
    model1.isSpread = NO;
    
    WeXPassportDescriptionModal *model2 = [[WeXPassportDescriptionModal alloc] init];
    model2.content = @"自主身份信息\n全向口袋的口袋在外部认证方能够对个人身份的有效性、真实性和唯一性等进行合理验证，并且将身份控制权从其他信息服务机构重新收回到个人手中，为用户塑造完整、可信的身份认证。将哪些信息提供给其他信息消费方，所有信息均保存在本地设备中，用户拥有着最终决定权。";
    model2.isSpread = NO;
    
    WeXPassportDescriptionModal *model3 = [[WeXPassportDescriptionModal alloc] init];
    model3.content = @"无需用户名与密码的认证\n在网站和APP登录时，用户不在需要用户名和密码来认证访问，而是使用基于区块链的数字身份进行认证。用户的身份证明完全存储在自己的设备上，并通过区块链共享。利用TouchID、FaceID等不可重复使用的生物识别技术，进行更为安全的公私钥对方式进行身份认证。";
    model3.isSpread = NO;
    
    WeXPassportDescriptionModal *model4 = [[WeXPassportDescriptionModal alloc] init];
    model4.content = @"在区块链上进行最安全的信息交换\n区块链技术是目前为止信息交换中最为安全的信息交换方式。在区块链上的数据是不可更改的，因此黑客也不可能对链上数据进行攻击。而用户的个人信息都是经过加密后上传至区块链中的，区块链上的数据也不可能进行反向推导获取原始信息，只能在用户的许可下验证用户数据的原始信息。使用区块链进行的信息交换是目前最为安全可靠的信息交换方式。";
    model4.isSpread = NO;
    
    [self.datasArray addObject:model1];
    [self.datasArray addObject:model2];
    [self.datasArray addObject:model3];
    [self.datasArray addObject:model4];

}

//初始化滚动视图
-(void)setupSubViews{
    _tableView = [[UITableView alloc] init];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.tableFooterView = [UIView new];
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];
    [_tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight+20);
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
    WeXPassportDescriptionCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[[NSBundle mainBundle] loadNibNamed:@"WeXPassportDescriptionCell" owner:self options:nil] lastObject];
        cell.backgroundColor = [UIColor clearColor];
        cell.titleLabel.textColor = [UIColor whiteColor];
        cell.titleLabel.font = [UIFont systemFontOfSize:17];
        cell.accessoryType = UITableViewCellAccessoryNone;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    cell.refreshCellBlock = ^(UITableViewCell *currentCell){
        
        NSIndexPath *indexRow = [tableView indexPathForCell:(WeXPassportDescriptionCell *)currentCell];
        
        [tableView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:indexRow, nil] withRowAnimation:UITableViewRowAnimationFade];
    };
    
    WeXPassportDescriptionModal *model = [self.datasArray objectAtIndex:indexPath.row];
    [cell setModel:model];
    return cell;
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXPassportDescriptionModal *model = [self.datasArray  objectAtIndex:indexPath.row];
    return [WeXPassportDescriptionCell heightWithModel:model];
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    WeXPassportDescriptionCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    [cell arrowBtnClick];
}



@end
