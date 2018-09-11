//
//  WeXMyIpfsSaveModel.h
//  WeXBlockChain
//
//  Created by wh on 2018/8/10.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Foundation/Foundation.h>
typedef NS_ENUM(NSUInteger, WeXFileIpfsType) {
    WeXFileIpfsTypeUpload,//可上传
    WeXFileIpfsTypeDownload,//可下载
    WeXFileIpfsTypeNoData,//本地云上无数据无需上传和下载
    WeXFileIpfsTypeNewest//最新信息无需上传和下载
};

typedef NS_ENUM(NSUInteger, WeXFileIpfsModeType) {
    WeXFileIpfsModeTypeIdentify,//实名认证
    WeXFileIpfsModeTypeBankCard,//银行卡认证
    WeXFileIpfsModeTypePhoneOperator// 手机运营商认证
   
};

@interface WeXMyIpfsSaveModel : NSObject

@property (nonatomic,assign)WeXFileIpfsModeType modeType;
@property (nonatomic,strong)NSString *selectStatus;
@property (nonatomic,strong)NSString *identifyTitle;
@property (nonatomic,strong)NSString *statusDescribeStr;
@property (nonatomic,strong)NSString *statusStr;
@property (nonatomic,strong)NSString *fileSizeStr;
@property (nonatomic,assign)BOOL isAllowSelect;
@property (nonatomic,assign)BOOL isSelected;
@property (nonatomic,assign)WeXFileIpfsType fileIpfsType;
@property (nonatomic,strong)NSString *hashStr;

@end
