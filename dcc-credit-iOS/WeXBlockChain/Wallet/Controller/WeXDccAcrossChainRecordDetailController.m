//
//  WeXDccAcrossChainRecordDetailController.m
//  WeXBlockChain
//
//  Created by 王创创 on 2018/7/16.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXDccAcrossChainRecordDetailController.h"
#import "WeXWalletGetDccAcrossChainRecordDetailAdapter.h"
#import "WeXDccAcrossChainRecordDetailView.h"
#import "WeXWalletRecordHashWebController.h"

@interface WeXDccAcrossChainRecordDetailController ()
{
    WeXWalletGetDccAcrossChainRecordDetailResponseModal *_model;
}
@property (nonatomic,strong)UILabel *leftSymbolLabel;
@property (nonatomic,strong)UILabel *leftNameLabel;
@property (nonatomic,strong)UILabel *rightSymbolLabel;
@property (nonatomic,strong)UILabel *rightNameLabel;

@property (nonatomic,strong)UILabel *valueLabel;

@property (nonatomic,strong)UILabel *statusLabel;
@property (nonatomic,strong)UILabel *timeLabel;

@property (nonatomic,strong)UIScrollView *mainScrollView;
@property (nonatomic,strong)WeXWalletGetDccAcrossChainRecordDetailAdapter *getRecordDetailAdapter;
@end

@implementation WeXDccAcrossChainRecordDetailController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = @"跨链交易详情";
    [self setNavigationNomalBackButtonType];
    [self createQueryRecordDetailRequest];
}

- (void)createQueryRecordDetailRequest{
    [WeXPorgressHUD showLoadingAddedTo:self.view];
    _getRecordDetailAdapter = [[WeXWalletGetDccAcrossChainRecordDetailAdapter alloc] init];
    _getRecordDetailAdapter.delegate = self;
    WeXWalletGetDccAcrossChainRecordDetailParamModal *model = [[WeXWalletGetDccAcrossChainRecordDetailParamModal alloc] init];
    model.orderId = self.recordId;
    [_getRecordDetailAdapter run:model];
    
}

#pragma mark - 请求回调
- (void)wexBaseNetAdapterDelegate:(WexBaseNetAdapter *)adapter head:(WexBaseNetAdapterResponseHeadModal *)headModel response:(WeXBaseNetModal *)response{
    if (adapter == _getRecordDetailAdapter){
        if ([headModel.systemCode isEqualToString:@"SUCCESS"]&&[headModel.businessCode isEqualToString:@"SUCCESS"]) {
            [WeXPorgressHUD hideLoading];
            _model = (WeXWalletGetDccAcrossChainRecordDetailResponseModal *)response;
            WEXNSLOG(@"%@",_model);
            if (_model) {
                [self createRecordDetailViewWithModel:_model];
            }
        }
        else if ([headModel.systemCode isEqualToString:@"SUCCESS"])
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:headModel.message onView:self.view];
        }
        else
        {
            [WeXPorgressHUD hideLoading];
            [WeXPorgressHUD showText:@"系统错误，请稍后再试!" onView:self.view];
        }
    }
    
    
}

- (void)createRecordDetailViewWithModel:(WeXWalletGetDccAcrossChainRecordDetailResponseModal *)model
{
    WeXDccAcrossChainRecordDetailView *detailView = [WeXDccAcrossChainRecordDetailView recordDetailViewWithModal:model];
    detailView.HashClickBlock = ^(NSInteger index) {
        WeXWalletRecordHashWebController *ctrl = [[WeXWalletRecordHashWebController alloc] init];
        if (index == 0) {
           ctrl.txHash = _model.originTxHash;
            if ([_model.originAssetCode isEqualToString:@"DCC_JUZIX"])  {
                ctrl.type = WeXWalletRecordHashTypePrivate;
            }
            else
            {
                ctrl.type = WeXWalletRecordHashTypePublic;
            }
           
        }
        else if (index == 1)
        {
            ctrl.txHash = _model.destTxHash;
            if ([_model.originAssetCode isEqualToString:@"DCC_JUZIX"])  {
                ctrl.type = WeXWalletRecordHashTypePublic;
            }
            else
            {
                ctrl.type = WeXWalletRecordHashTypePrivate;
            }
        }
        [self.navigationController pushViewController:ctrl animated:YES];
    };
    [self.view addSubview:detailView];
    [detailView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(kNavgationBarHeight);
        make.leading.trailing.bottom.equalTo(self.view);
    }];
}




@end
