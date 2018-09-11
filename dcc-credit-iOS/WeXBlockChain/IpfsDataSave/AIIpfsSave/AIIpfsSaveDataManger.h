//
//  AIIpfsSaveDataManger.h
//  云存储demo
//
//  Created by wh on 2018/7/26.
//  Copyright © 2018年 wh. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger,AIIpfsReturnDataType) {
    AIIpfsReturnDataTypeFile,//文件
    AIIpfsReturnDataTypePhoto,//图片
    AIIpfsReturnDataTypeDict,//字典
    AIIpfsReturnDataTypeNSString//字符串
};

typedef void(^AIIpfsReturnHashDataBlock)(NSString *hashData);
typedef void(^AIIpfsGetDownLoadBlock)(id obj);

@interface AIIpfsSaveDataManger : NSObject

@property (nonatomic,copy)AIIpfsReturnHashDataBlock ipfsBlock;
@property (nonatomic,copy)AIIpfsGetDownLoadBlock progressBlock;

+ (instancetype)shareManager;

//上传实名认证数据
- (void)uploadIdentifyData:(NSMutableDictionary *)identifyDict WithBaseUrl:(NSString *)baseUrl WithPassword:(NSString *)password WithNonceStr:(NSString *)userNonceStr Callback:(AIIpfsReturnHashDataBlock)hashString;
//上传银行卡认证数据
- (void)uploadBankCardData:(NSMutableDictionary *)identifyDict WithBaseUrl:(NSString *)baseUrl WithPassword:(NSString *)password WithNonceStr:(NSString *)userNonceStr Callback:(AIIpfsReturnHashDataBlock)hashString;
//上传手机运营商认证数据
- (void)uploadPhoneOperotarData:(NSMutableDictionary *)identifyDict WithBaseUrl:(NSString *)baseUrl WithPassword:(NSString *)password WithNonceStr:(NSString *)userNonceStr Callback:(AIIpfsReturnHashDataBlock)hashString;
//下载实名认证数据
- (void)getIdentifyDataWithHash:(NSString *)hashStr WithBaseUrl:(NSString *)baseUrl WithPassword:(NSString *)password  WithNonceStr:(NSString *)userNonceStr WithData:(AIIpfsGetDownLoadBlock)dataBlock;
//下载银行卡认证数据
- (void)getBankCardDataWithHash:(NSString *)hashStr WithBaseUrl:(NSString *)baseUrl WithPassword:(NSString *)password WithNonceStr:(NSString *)userNonceStr WithData:(AIIpfsGetDownLoadBlock)dataBlock;
//下载手机运营商认证数据
- (void)getPhoneOperotarDataWithHash:(NSString *)hashStr WithBaseUrl:(NSString *)baseUrl WithPassword:(NSString *)password WithNonceStr:(NSString *)userNonceStr WithData:(AIIpfsGetDownLoadBlock)dataBlock;

//上传文件
- (void)upload:(NSString *)url WithBaseUrl:(NSString *)baseUrl WithFilePath:(NSString *)filePath WithFileName:(NSString *)fileName WithFileType:(NSString *)fileType Success:(AIIpfsReturnHashDataBlock)hashStr;
//上传图片
- (void)upload:(NSString *)url WithPhotoName:(NSString *)photoName WithPhotoType:(NSString *)photoType Success:(AIIpfsReturnHashDataBlock)hashStr;

//上传字典
- (void)upload:(NSString *)url WithDict:(NSDictionary *)dictData WithDictName:(NSString *)dictName Success:(AIIpfsReturnHashDataBlock)hashStr;

//通过hash获取ipfs上的数据
- (void)getIpfsDataWithBaseUrl:(NSString *)baseUrl WithHash:(NSString *)hashStr SaveFilePath:(NSString *)saveFilePath WithDataType:(AIIpfsReturnDataType) dataType WithData:(AIIpfsGetDownLoadBlock)dataBlock;

@end
