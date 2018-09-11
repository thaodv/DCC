//
//  AIIpfsSaveDataHelpObject.h
//  云存储demo
//
//  Created by wh on 2018/7/31.
//  Copyright © 2018年 wh. All rights reserved.
//

#import <Foundation/Foundation.h>

//存储数据的类型
typedef NS_ENUM(NSInteger,AIIpfsSaveDataType) {
    AIIpfsSaveDataTypeID,//实名认证数据存取
    AIIpfsSaveDataTypeBankCard,//银行卡认证数据存取
    AIIpfsSaveDataTypePhotoOprator,//手机运营商认证数据存取
    AIIpfsSaveDataTypeOther//其他类型
};

typedef void(^AIIpfsSaveDataHash) (NSString *saveHash);
typedef void(^AIIpfsSaveDataDict) (NSMutableDictionary *saveDict);
typedef void(^AIIpfsSaveDataProgress) (NSProgress *progress);

@interface AIIpfsSaveDataHelpObject : NSObject

/*
 *  上传数据到ipfs上
 */
+ (void)uploadIdnetifyData:(NSMutableDictionary *)identifyData WithBaseUrl:(NSString *)baseUrl WithUserPassword:(NSString *)userPassword WithNonceStr:(NSString *)userNonceStr WithType:(AIIpfsSaveDataType)ipfsSaveDataType Callback:(AIIpfsSaveDataHash)hashStr;

/*
 *  从ipfs上获取数据
 */
+ (void)getDataFromIpfs:(NSString *)hashStr WithBaseUrl:(NSString *)baseUrl WithUserPassword:(NSString *)userPassword WithNonceStr:(NSString *)userNonceStr WithType:(AIIpfsSaveDataType)ipfsSaveDataType Callback:(AIIpfsSaveDataDict)saveDict;


//获取实名认证的上传进度
+ (void)getUploadIdentifyProgress:(AIIpfsSaveDataProgress)uploadProgress;
//获取银行卡认证的上传进度
+ (void)getUploadBankCardProgress:(AIIpfsSaveDataProgress)uploadProgress;
//获取手机运营商的上传进度
+ (void)getUploadPhoneOperatorProgress:(AIIpfsSaveDataProgress)uploadProgress;


@end
