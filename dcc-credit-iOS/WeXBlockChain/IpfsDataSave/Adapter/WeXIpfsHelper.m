//
//  WeXIpfsHelper.m
//  WeXBlockChain
//
//  Created by wh on 2018/8/24.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXIpfsHelper.h"
#import <CommonCrypto/CommonDigest.h>

@implementation WeXIpfsHelper


+ (BOOL) isBlankString:(NSString *)string {
    
    if (string ==nil || string ==NULL) {
        return YES;
    }
    if ([string isKindOfClass:[NSNull class]]) {
        return YES;
    }
    if ([[string stringByTrimmingCharactersInSet: [NSCharacterSet whitespaceCharacterSet]]length]==0) {//特殊字符判断
        return YES;
    }
    return NO;

}

//图片转字符串
+(NSString *)UIImageToBase64Str:(UIImage *) image
{
    NSData *data = UIImagePNGRepresentation(image);
    NSString *encodedImageStr = [data base64EncodedStringWithOptions:NSDataBase64EncodingEndLineWithLineFeed];
    return encodedImageStr;
}

// 将Base64字符串转成图片（图片下载）
+(UIImage *)imageWihtBase64String:(NSString *)base64Str
{
    if (base64Str == nil) {
        return  nil;
    }
    // Base64字符串转UIImage图片：
    NSData *imageData = [[NSData alloc] initWithBase64EncodedString:base64Str options:NSDataBase64DecodingIgnoreUnknownCharacters];
    UIImage *image =[UIImage imageWithData:imageData];
    return image;
}

//16位MD5加密方式
+ (NSString *)getMd5_16Bit_String:(NSString *)srcString{
    
    if (srcString == nil) {
        return nil;
    }
    const char *cStr = [srcString UTF8String];
    unsigned char digest[CC_MD5_DIGEST_LENGTH];
    
    CC_MD5(cStr, (CC_LONG)strlen(cStr), digest);
    
    NSMutableString *result = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
    for (int i = 0; i < CC_MD5_DIGEST_LENGTH; i++) {
        [result appendFormat:@"%02X", digest[i]];
    }
    NSString *resultStr = [NSString stringWithFormat:@"%@",result];
    //提取32位MD5散列的中间16位
  
    resultStr = [[resultStr substringToIndex:24] substringFromIndex:8];//即9～25位
    return resultStr;
}

//单个文件的大小
+ (long long) fileSizeAtPath:(NSString*) filePath{
    NSFileManager* manager = [NSFileManager defaultManager];
    if ([manager fileExistsAtPath:filePath]){
        return [[manager attributesOfItemAtPath:filePath error:nil] fileSize];
    }
    
    return 0;
}
//遍历文件夹获得文件夹大小，返回多少M
+ (float ) folderSizeAtPath:(NSString*) folderPath{
    
    NSFileManager* manager = [NSFileManager defaultManager];
    if (![manager fileExistsAtPath:folderPath]) return 0;
    NSEnumerator *childFilesEnumerator = [[manager subpathsAtPath:folderPath] objectEnumerator];
    NSString* fileName;
    long long folderSize = 0;
    while ((fileName = [childFilesEnumerator nextObject]) != nil){
        NSString* fileAbsolutePath = [folderPath stringByAppendingPathComponent:fileName];
        folderSize += [self fileSizeAtPath:fileAbsolutePath];
    }
    
    return folderSize/(1024.0*1024.0);
}

//将NSData写入沙盒文件
+ (void)writeToFileSavePath:(NSString *)dataFilePath WithData:(NSData *)fromData{
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isSuccess = [fileManager createFileAtPath:dataFilePath contents:fromData attributes:nil];
    if (isSuccess) {
        NSLog(@"存储成功");
    }else{
        NSLog(@"error");
    }
}

+ (NSString *)getIdentifyFileDataSize:(AIIpfsFileDataSize)ipfsFileSize WithDict:(NSDictionary *)dataDict{
    [self createIpfsUploadFile];
    NSString *fileSize;
    if (ipfsFileSize == AIIpfsFileDataSizeID) {
        
        NSString *docsdir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        NSString *bankCardPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIPhotoOperator/phoneOperator.txt"];
        NSData *identifyData= [NSJSONSerialization dataWithJSONObject:dataDict options:NSJSONWritingPrettyPrinted error:nil];
        //    NSData *encryptionData = [identifyData AIAES256EncryptWithKey:password];
        [self writeToFileSavePath:bankCardPath WithData:identifyData];
        
    }else if (ipfsFileSize == AIIpfsFileDataSizeBankCard){
        
    }else if (ipfsFileSize == AIIpfsFileDataSizePhotoOprator){
    
    }
    return fileSize;
}

//沙盒建立上传文件夹
+ (void)createIpfsUploadFile{
    
    NSString *docsdir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *dataFilePath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE"]; // 在Document目录下创建 "archiver" 文件夹
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isDir = NO;
    // fileExistsAtPath 判断一个文件或目录是否有效，isDirectory判断是否一个目录
    BOOL existed = [fileManager fileExistsAtPath:dataFilePath isDirectory:&isDir];
    
    if (!(isDir && existed)) {
        // 在Document目录下创建一个archiver目录
        [fileManager createDirectoryAtPath:dataFilePath withIntermediateDirectories:YES attributes:nil error:nil];
    }
  
    NSString *cachesPath = [dataFilePath stringByAppendingString:@"/AICaches"];
    
    if (![fileManager fileExistsAtPath:cachesPath]) {
        [fileManager createDirectoryAtPath:cachesPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
}

////遍历文件夹获得文件夹大小，返回多少M
//- (float ) getFolderSizeAtPath:(NSString*) folderPath{
//
//    NSFileManager* manager = [NSFileManager defaultManager];
//    if (![manager fileExistsAtPath:folderPath]) return 0;
//    NSEnumerator *childFilesEnumerator = [[manager subpathsAtPath:folderPath] objectEnumerator];
//    NSString* fileName;
//    long long folderSize = 0;
//    while ((fileName = [childFilesEnumerator nextObject]) != nil){
//        NSString* fileAbsolutePath = [folderPath stringByAppendingPathComponent:fileName];
//        folderSize += [self getFileSizeAtPath:fileAbsolutePath];
//    }
//    return folderSize/(1024.0*1024.0);
//}
//
////单个文件的大小
//- (long long) getFileSizeAtPath:(NSString*) filePath{
//    NSFileManager* manager = [NSFileManager defaultManager];
//    if ([manager fileExistsAtPath:filePath]){
//        return [[manager attributesOfItemAtPath:filePath error:nil] fileSize];
//    }
//    return 0;
//}

+ (NSString *)toLower:(NSString *)str{
    for (NSInteger i=0; i<str.length; i++) {
        if ([str characterAtIndex:i]>='A'&[str characterAtIndex:i]<='Z') {
            //A  65  a  97
            char  temp=[str characterAtIndex:i]+32;
            NSRange range=NSMakeRange(i, 1);
            str=[str stringByReplacingCharactersInRange:range withString:[NSString stringWithFormat:@"%c",temp]];
        }
    }
    return str;
}

+ (NSString *)toUpper:(NSString *)str{
    for (NSInteger i=0; i<str.length; i++) {
        if ([str characterAtIndex:i]>='a'&[str characterAtIndex:i]<='z') {
            //A  65  a  97
            char  temp=[str characterAtIndex:i]-32;
            NSRange range=NSMakeRange(i, 1);
            str=[str stringByReplacingCharactersInRange:range withString:[NSString stringWithFormat:@"%c",temp]];
        }
    }
    return str;
}



@end
