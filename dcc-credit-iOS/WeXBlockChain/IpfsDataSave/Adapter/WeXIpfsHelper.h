//
//  WeXIpfsHelper.h
//  WeXBlockChain
//
//  Created by wh on 2018/8/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Foundation/Foundation.h>

//存储数据的类型
typedef NS_ENUM(NSInteger,AIIpfsFileDataSize) {
    AIIpfsFileDataSizeID,//实名认证数据存取
    AIIpfsFileDataSizeBankCard,//银行卡认证数据存取
    AIIpfsFileDataSizePhotoOprator,//手机运营商认证数据存取
    AIIpfsFileDataSizeOther//其他类型
};

@interface WeXIpfsHelper : NSObject

//字符串的判空处理
+ (BOOL) isBlankString:(NSString *)string ;

//图片转字符串
+(NSString *)UIImageToBase64Str:(UIImage *) image;

// 将Base64字符串转成图片（图片下载）
+(UIImage *)imageWihtBase64String:(NSString *)base64Str;

//16位MD5加密方式
+ (NSString *)getMd5_16Bit_String:(NSString *)srcString;

//单个文件的大小
+ (long long) fileSizeAtPath:(NSString *) filePath;

//遍历文件夹获得文件夹大小，返回多少M
+ (float ) folderSizeAtPath:(NSString *) folderPath;

//将NSData写入沙盒文件
+ (void)writeToFileSavePath:(NSString *)dataFilePath WithData:(NSData *)fromData;

//获取各个上传文件的大小
+ (NSString *)getIdentifyFileDataSize:(AIIpfsFileDataSize)ipfsFileSize WithDict:(NSDictionary *)dataDict;

//沙盒建立上传文件夹
+ (void)createIpfsUploadFile;
//过滤字符串把字母大写转成小写
+ (NSString *)toLower:(NSString *)str;
//过滤字符串把字母小写转成大写
+ (NSString *)toUpper:(NSString *)str;

@end
