//
//  WeXCreditBorrowUSDTViewController.m
//  WeXBlockChain
//
//  Created by wcc on 2018/4/20.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCreditBorrowUSDTViewController.h"
#import "WeXCreditBorrowUSDTCell.h"
#import "WeXReceiveAddressManagerController.h"
#import "WeXMyBorrowListViewController.h"

#import "WeXBorrowProductChooseController.h"
#import "WeXAwardPlanViewController.h"

static NSString *const kCollectionIdentifier = @"collectionIdentifier";
static NSString *const kCollectionHeaderIdentifier = @"kCollectionHeaderIdentifier";
static NSString *const kCollectionFooterIdentifier = @"kCollectionFooterIdentifier";

static const CGFloat kCardHeightWidthRatio = 198.0/340.0;

@interface WeXCreditBorrowUSDTViewController ()<UICollectionViewDelegate,UICollectionViewDataSource,UICollectionViewDelegateFlowLayout>
{
    UICollectionView *_collectionView;
    UIView *_coverView;
}

@end

@implementation WeXCreditBorrowUSDTViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = WeXLocalizedString(@"信用借币");
    [self setNavigationNomalBackButtonType];
    [self setupSubViews];
    
}
//初始化滚动视图
-(void)setupSubViews{
    CGFloat space = 15.0;
    CGFloat spaceCentre = 15.0;
    CGFloat width = (kScreenWidth-space*2-spaceCentre)/2;
    CGFloat height = width;
    UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
    layout.itemSize = CGSizeMake(width, height);
    layout.minimumLineSpacing = 15;
    layout.minimumInteritemSpacing = 15;
    layout.sectionInset = UIEdgeInsetsMake(20, 15, 15, 15);
    layout.headerReferenceSize = CGSizeMake(kScreenWidth, (kScreenWidth-30)*kCardHeightWidthRatio);
    layout.footerReferenceSize = CGSizeMake(kScreenWidth, 25);
    _collectionView = [[UICollectionView alloc] initWithFrame:CGRectMake(0, kNavgationBarHeight+10, kScreenWidth, kScreenHeight-kNavgationBarHeight-10) collectionViewLayout:layout];
    _collectionView.delegate = self;
    _collectionView.dataSource = self;
    _collectionView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:_collectionView];
    [_collectionView registerNib:[UINib nibWithNibName:@"WeXCreditBorrowUSDTCell" bundle:nil] forCellWithReuseIdentifier:kCollectionIdentifier];
    [_collectionView registerClass:[UICollectionReusableView class] forSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:kCollectionHeaderIdentifier];
     [_collectionView registerClass:[UICollectionReusableView class] forSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:kCollectionFooterIdentifier];
    
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
   return  4;
}

//2018.7.24 信用借币界面全是图片
-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    WeXCreditBorrowUSDTCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:kCollectionIdentifier forIndexPath:indexPath];
    cell.backgroundColor = [UIColor clearColor];
    
    if (indexPath.item == 0) {
        cell.contentImagevIEW.image = [UIImage imageNamed:WeXLocalizedString(@"dcc_borrow_1")];
        cell.titleLabel.text = WeXLocalizedString(@"我要借款");
    }
    else if (indexPath.item == 1)
    {
        cell.contentImagevIEW.image = [UIImage imageNamed:WeXLocalizedString(@"dcc_borrow_2")];
        cell.titleLabel.text = WeXLocalizedString(@"我的借款");
    }
    else if (indexPath.item ==2)
    {
        cell.contentImagevIEW.image = [UIImage imageNamed:WeXLocalizedString(@"dcc_borrow_3")];
        cell.titleLabel.text = WeXLocalizedString(@"收款地址管理");
    }
    else if (indexPath.item == 3)
    {
        cell.contentImagevIEW.image = [UIImage imageNamed:WeXLocalizedString(@"dcc_borrow_4")];
        cell.titleLabel.text = WeXLocalizedString(@"USDT管理");
    }
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.item == 0) {
        WeXBorrowProductChooseController *ctrl = [[WeXBorrowProductChooseController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.item == 1)
    {
        WeXMyBorrowListViewController *ctrl = [[WeXMyBorrowListViewController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    
    }
    else if (indexPath.item == 2) {
        WeXReceiveAddressManagerController *ctrl = [[WeXReceiveAddressManagerController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    else if (indexPath.item == 3) {
        WeXAwardPlanViewController *ctrl = [[WeXAwardPlanViewController alloc] init];
        [self.navigationController pushViewController:ctrl animated:YES];
    }
}

// 设置headerView和footerView的
- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
{
    UICollectionReusableView *reusableView = nil;
    if (kind == UICollectionElementKindSectionHeader) {
        UICollectionReusableView *header = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:kCollectionHeaderIdentifier forIndexPath:indexPath];
        reusableView = header;
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(15, 0, kScreenWidth-30, (kScreenWidth-30)*kCardHeightWidthRatio)];
        imageView.image =[UIImage imageNamed:WeXLocalizedString(@"dcc_borrow_head_image")];
        [header addSubview:imageView];
    }
    else if (kind == UICollectionElementKindSectionFooter)
    {
        UICollectionReusableView *footer = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:kCollectionFooterIdentifier forIndexPath:indexPath];
        reusableView = footer;
        UIImageView *imageView = [[UIImageView alloc] init];
        imageView.image =[UIImage imageNamed:@"borrow_dcc_support"];
        [footer addSubview:imageView];
        [imageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.equalTo(footer);
            make.centerY.equalTo(footer);
        }];
    }
    return reusableView;
}






@end
