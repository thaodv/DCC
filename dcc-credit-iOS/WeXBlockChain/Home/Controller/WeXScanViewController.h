//
//  WeXScanViewController.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/15.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseViewController.h"

typedef NS_ENUM(NSInteger, ScannerType) {
    ScannerTypeQR = 1,        // 扫一扫 - 二维码
    ScannerTypeCover,         // 扫一扫 - 封面
    ScannerTypeStreet,        // 扫一扫 - 街景
    ScannerTypeTranslate,     // 扫一扫 - 翻译
};


typedef NS_ENUM(NSInteger, WeXScannerHandleType) {
    WeXScannerHandleTypeImport,  //返回导入页面扫描的内容
    WeXScannerHandleTypeLogin,//第三方授权登录
     WeXScannerHandleTypeToAddress//扫描地址页面
};

typedef void(^ScannerResponseBlock)(NSString *content);

@interface WeXScanViewController : WeXBaseViewController

@property (nonatomic,assign) ScannerType scannerType;

@property (nonatomic,assign) WeXScannerHandleType handleType;

@property (nonatomic,copy) ScannerResponseBlock responseBlock;

@end
