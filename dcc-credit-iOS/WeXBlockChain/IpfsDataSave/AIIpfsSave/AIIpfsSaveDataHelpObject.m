//
//  AIIpfsSaveDataHelpObject.m
//  云存储demo
//
//  Created by wh on 2018/7/31.
//  Copyright © 2018年 wh. All rights reserved.
//

#import "AIIpfsSaveDataHelpObject.h"
#import "SSZipArchive.h"
#import "AIIpfsSaveDataManger.h"

@interface AIIpfsSaveDataHelpObject()<SSZipArchiveDelegate>

@end

@implementation AIIpfsSaveDataHelpObject

/*
 *  上传数据到ipfs上
 */

+ (void)uploadIdnetifyData:(NSMutableDictionary *)identifyData WithBaseUrl:(NSString *)baseUrl WithUserPassword:(NSString *)userPassword WithNonceStr:(NSString *)userNonceStr WithType:(AIIpfsSaveDataType)ipfsSaveDataType Callback:(AIIpfsSaveDataHash)hashStr{
    
    if(ipfsSaveDataType == AIIpfsSaveDataTypeID){
        
        [[AIIpfsSaveDataManger shareManager]uploadIdentifyData:identifyData  WithBaseUrl:baseUrl WithPassword:userPassword WithNonceStr:userNonceStr     Callback:^(NSString *hashData) {
            if (hashData) {
                hashStr(hashData);
            }
        }];
        
    }else if (ipfsSaveDataType ==  AIIpfsSaveDataTypeBankCard){
        
        [[AIIpfsSaveDataManger shareManager]uploadBankCardData:identifyData WithBaseUrl:baseUrl WithPassword:userPassword WithNonceStr:userNonceStr  Callback:^(NSString *hashData) {
            if (hashData) {
                hashStr(hashData);
            }
        }];
        
    }else if (ipfsSaveDataType ==  AIIpfsSaveDataTypePhotoOprator){
       
        [[AIIpfsSaveDataManger shareManager]uploadPhoneOperotarData:identifyData WithBaseUrl:baseUrl WithPassword:userPassword WithNonceStr:userNonceStr  Callback:^(NSString *hashData) {
            if (hashData) {
                hashStr(hashData);
            }
        }];
        
    }else if (ipfsSaveDataType ==  AIIpfsSaveDataTypeOther){
    
    }else{
        
    }
    
}


/*
 *  从ipfs上获取数据
 */
+ (void)getDataFromIpfs:(NSString *)hashStr WithBaseUrl:(NSString *)baseUrl WithUserPassword:(NSString *)userPassword WithNonceStr:(NSString *)userNonceStr WithType:(AIIpfsSaveDataType)ipfsSaveDataType Callback:(AIIpfsSaveDataDict)saveDict{
    
    if(ipfsSaveDataType == AIIpfsSaveDataTypeID){
        
        [[AIIpfsSaveDataManger shareManager]getIdentifyDataWithHash:hashStr WithBaseUrl:baseUrl WithPassword:userPassword WithNonceStr:userNonceStr  WithData:^(id obj) {
            NSMutableDictionary *dict = obj;
            saveDict(dict);
        }];
        
    }else if (ipfsSaveDataType ==  AIIpfsSaveDataTypeBankCard){
        
        [[AIIpfsSaveDataManger shareManager]getBankCardDataWithHash:hashStr  WithBaseUrl:baseUrl WithPassword:userPassword WithNonceStr:userNonceStr  WithData:^(id obj) {
            NSMutableDictionary *dict = obj;
            saveDict(dict);
        }];
        
    }else if (ipfsSaveDataType ==  AIIpfsSaveDataTypePhotoOprator){
        
        [[AIIpfsSaveDataManger shareManager]getPhoneOperotarDataWithHash:hashStr WithBaseUrl:baseUrl  WithPassword:userPassword WithNonceStr:userNonceStr  WithData:^(id obj) {
            NSMutableDictionary *dict = obj;
            saveDict(dict);
        }];
        
    }else if (ipfsSaveDataType ==  AIIpfsSaveDataTypeOther){
        
    }else{
        
    }
    
}

//获取实名认证的上传进度
+ (void)getUploadIdentifyProgress:(AIIpfsSaveDataProgress)uploadProgress{
    [AIIpfsSaveDataManger shareManager].progressBlock = ^(id obj) {
        NSProgress *proObject = obj;
        uploadProgress(proObject);
    } ;
}
//获取银行卡认证的上传进度
+ (void)getUploadBankCardProgress:(AIIpfsSaveDataProgress)uploadProgress{
    [AIIpfsSaveDataManger shareManager].progressBlock = ^(id obj) {
        NSProgress *proObject = obj;
        uploadProgress(proObject);
    } ;
}
//获取手机运营商的上传进度
+ (void)getUploadPhoneOperatorProgress:(AIIpfsSaveDataProgress)uploadProgress{
    [AIIpfsSaveDataManger shareManager].progressBlock = ^(id obj) {
        NSProgress *proObject = obj;
        uploadProgress(proObject);
    };
}

@end
