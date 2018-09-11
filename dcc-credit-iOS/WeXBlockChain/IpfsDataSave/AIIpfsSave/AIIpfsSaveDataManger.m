//
//  AIIpfsSaveDataManger.m
//  云存储demo
//
//  Created by wh on 2018/7/26.
//  Copyright © 2018年 wh. All rights reserved.
//

#import "AIIpfsSaveDataManger.h"
#import "AISAFNetworking.h"
#import "AIIpfsHeader.h"
#import "SSZipArchive.h"

#import "NSData+AISAES256.h"

@interface AIIpfsSaveDataManger()<SSZipArchiveDelegate>

@end

@implementation AIIpfsSaveDataManger

+ (instancetype)shareManager{
    static AIIpfsSaveDataManger *manager;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[AIIpfsSaveDataManger alloc] init];
    });
    return manager;
}

//上传实名认证数据
- (void)uploadIdentifyData:(NSMutableDictionary *)identifyDict WithBaseUrl:(NSString *)baseUrl WithPassword:(NSString *)password WithNonceStr:(NSString *)userNonceStr Callback:(AIIpfsReturnHashDataBlock)hashString{
    [self createIpfsUploadFile];
    
    NSString *docsdir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
//    NSString *postivePhotoPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIIdentify/postivePhoto.jpg"];
//    NSString *backPhotoPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIIdentify/backPhoto.jpg"];
//    NSString *facePhotoPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIIdentify/facePhoto.jpg"];
    NSString *identifyDataPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIIdentify/identifyData.txt"];
//    UIImage *postiveImg = identifyDict[@"positivePhoto"];
//    UIImage *backImg = identifyDict[@"backPhoto"];
//    UIImage *faceImg = identifyDict[@"facePhoto"];
//    NSData *postiveData = [UIImageJPEGRepresentation(postiveImg, 1.0) AES256EncryptWithKey:password iv:password];
//    NSData *backData = [UIImageJPEGRepresentation(backImg, 1.0) AES256EncryptWithKey:password iv:password];
//    NSData *faceData = [UIImageJPEGRepresentation(faceImg, 1.0) AES256EncryptWithKey:password iv:password];
//    [self writeToFileSavePath:postivePhotoPath WithData:postiveData];
//    [self writeToFileSavePath:backPhotoPath WithData:backData];
//    [self writeToFileSavePath:facePhotoPath WithData:faceData];
//    [identifyDict removeObjectForKey:@"positivePhoto"];
//    [identifyDict removeObjectForKey:@"backPhoto"];
//    [identifyDict removeObjectForKey:@"facePhoto"];
    NSLog(@"identifyDict = %@",identifyDict);
    NSData *identifyData= [NSJSONSerialization dataWithJSONObject:identifyDict options:NSJSONWritingPrettyPrinted error:nil];
//    NSData *encryptionData = [identifyData AIAES256EncryptWithKey:password];
    NSData *encryptionData = [identifyData AES256EncryptWithKey:password iv:userNonceStr];
    [self writeToFileSavePath:identifyDataPath WithData:encryptionData];
    NSString *allIdentifyDataPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIIdentify/identify.zip"];
//    [self createZipFileWithDestinationPath:allIdentifyDataPath WithSourceFileArray:@[postivePhotoPath,backPhotoPath,facePhotoPath,identifyDataPath]];
    [self createZipFileWithDestinationPath:allIdentifyDataPath WithSourceFilePath:identifyDataPath];
    [self upload:kAddUrl WithBaseUrl:baseUrl WithFilePath:allIdentifyDataPath  WithFileName:@"identify" WithFileType:@"zip" Success:^(NSString *hashData) {
        hashString (hashData);
    }];
    
}
//上传银行卡认证数据
- (void)uploadBankCardData:(NSMutableDictionary *)identifyDict WithBaseUrl:(NSString *)baseUrl WithPassword:(NSString *)password WithNonceStr:(NSString *)userNonceStr Callback:(AIIpfsReturnHashDataBlock)hashString{
    
    [self createIpfsUploadFile];
    NSString *docsdir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *bankCardPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIBankCard/bankCardData.txt"];
    NSData *identifyData= [NSJSONSerialization dataWithJSONObject:identifyDict options:NSJSONWritingPrettyPrinted error:nil];
//    NSData *encryptionData = [identifyData AIAES256EncryptWithKey:password];
    NSData *encryptionData = [identifyData AES256EncryptWithKey:password iv:userNonceStr];
    [self writeToFileSavePath:bankCardPath WithData:encryptionData];
    NSString *allIdentifyDataPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIBankCard/bankCardData.zip"];
    [self createZipFileWithDestinationPath:allIdentifyDataPath WithSourceFilePath:bankCardPath];
    [self upload:kAddUrl WithBaseUrl:baseUrl WithFilePath:allIdentifyDataPath  WithFileName:@"bankCard" WithFileType:@"zip" Success:^(NSString *hashData) {
        hashString (hashData);
    }];
    
}
//上传手机运营商认证数据
- (void)uploadPhoneOperotarData:(NSMutableDictionary *)identifyDict WithBaseUrl:(NSString *)baseUrl WithPassword:(NSString *)password WithNonceStr:(NSString *)userNonceStr Callback:(AIIpfsReturnHashDataBlock)hashString{
    
    [self createIpfsUploadFile];
    NSString *docsdir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *bankCardPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIPhotoOperator/phoneOperator.txt"];
    NSData *identifyData= [NSJSONSerialization dataWithJSONObject:identifyDict options:NSJSONWritingPrettyPrinted error:nil];
//    NSData *encryptionData = [identifyData AIAES256EncryptWithKey:password];
    NSData *encryptionData = [identifyData AES256EncryptWithKey:password iv:userNonceStr];
    [self writeToFileSavePath:bankCardPath WithData:encryptionData];
    NSString *allIdentifyDataPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIPhotoOperator/phoneOperator.zip"];
    [self createZipFileWithDestinationPath:allIdentifyDataPath WithSourceFilePath:bankCardPath];
    [self upload:kAddUrl WithBaseUrl:baseUrl WithFilePath:allIdentifyDataPath WithFileName:@"phoneOperator" WithFileType:@"zip" Success:^(NSString *hashData) {
        hashString (hashData);
    }];
}

//下载实名认证数据
- (void)getIdentifyDataWithBaseUrl:(NSString *)baseUrl WithHash:(NSString *)hashStr WithPassword:(NSString *)password WithNonceStr:(NSString *)userNonceStr WithData:(AIIpfsGetDownLoadBlock)dataBlock{
    [self createIpfsUploadFile];
    NSString *docsdir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *allIdentifyPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AICaches/identifys.zip"];
    __weak __typeof(self)weakSelf = self;
    [self getIpfsDataWithBaseUrl:baseUrl WithHash:hashStr SaveFilePath:allIdentifyPath WithDataType:AIIpfsReturnDataTypeFile WithData:^(id obj) {
        NSString *allPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AICaches/identifys"];;
        [weakSelf unzipFileWithDestinationPath:allPath WithSourceFilePath:allIdentifyPath];
//        NSString *postivePhotoPath = [allPath stringByAppendingPathComponent:@"/postivePhoto.jpg"];
//        NSString *backPhotoPath = [allPath stringByAppendingPathComponent:@"/backPhoto.jpg"];
//        NSString *facePhotoPath = [allPath stringByAppendingPathComponent:@"/facePhoto.jpg"];
        NSString *identifyDataPath = [allPath stringByAppendingPathComponent:@"/identifyData.txt"];

//      NSData *postiveData = [[NSData dataWithContentsOfFile:postivePhotoPath] AES2568DecryptWithKey :password iv:password];
//      NSData *backData = [[NSData dataWithContentsOfFile:backPhotoPath] AES2568DecryptWithKey:password iv:password];
//      NSData *faceData = [[NSData dataWithContentsOfFile:facePhotoPath] AES2568DecryptWithKey:password iv:password];
        NSData *identifyData= [[NSData dataWithContentsOfFile:identifyDataPath] AES2568DecryptWithKey:password iv:userNonceStr];
        
//        UIImage *postiveImg = [UIImage imageWithData:postiveData];
//        UIImage *backImg = [UIImage imageWithData:backData];
//        UIImage *faceImg = [UIImage imageWithData:faceData];
//        NSMutableDictionary *dataDict = [[NSMutableDictionary alloc]init];
//        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:identifyData options:NSJSONReadingMutableLeaves error:nil];
//        NSLog(@"dataDict= %@",dataDict);
//        dataDict = [NSMutableDictionary dictionaryWithDictionary:dict];
//        dataDict[@"positivePhoto"] = postiveImg;   //身份证正面照
//        dataDict[@"backPhoto"] = backImg;  //身份证反面
//        dataDict[@"facePhoto"] = faceImg;  //头像
        
        NSMutableDictionary *dataDict = [NSJSONSerialization JSONObjectWithData:identifyData options:NSJSONReadingMutableLeaves error:nil];
        NSLog(@"dataDict= %@",dataDict);
//        NSString *twoIdentifyDataPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AIIdentify/identifyDatas.txt"];
//        [self writeToFileSavePath:twoIdentifyDataPath WithData:identifyData];
        
        dataBlock(dataDict);
        
    }];
    
}
//下载银行卡认证数据
- (void)getBankCardDataWithBaseUrl:(NSString *)baseUrl WithHash:(NSString *)hashStr WithPassword:(NSString *)password WithNonceStr:(NSString *)userNonceStr WithData:(AIIpfsGetDownLoadBlock)dataBlock{
    [self createIpfsUploadFile];
    NSString *docsdir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *allIdentifyPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AICaches/bankCardData.zip"];
//     NSString *allIdentifyPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AICaches/bankCardData.txt"];
     __weak __typeof(self)weakSelf = self;
    [self getIpfsDataWithBaseUrl:baseUrl WithHash:hashStr SaveFilePath:allIdentifyPath WithDataType:AIIpfsReturnDataTypeFile WithData:^(id obj) {
        NSString *allPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AICaches/bankCardData"];
        [weakSelf unzipFileWithDestinationPath:allPath WithSourceFilePath:allIdentifyPath];
        NSString *identifyDataPath = [allPath stringByAppendingPathComponent:@"/bankCardData.txt"];

        NSData *identifyData= [[NSData dataWithContentsOfFile:identifyDataPath] AES2568DecryptWithKey:password iv:userNonceStr];
        NSLog(@"identifyData = %@",identifyData);
        NSMutableDictionary *dataDict = [NSJSONSerialization JSONObjectWithData:identifyData options:NSJSONReadingMutableLeaves error:nil];
        NSLog(@"dataDict= %@",dataDict);
        dataBlock(dataDict);
    }];
    
}
//下载手机运营商认证数据
- (void)getPhoneOperotarDataWithBaseUrl:(NSString *)baseUrl WithHash:(NSString *)hashStr WithPassword:(NSString *)password  WithNonceStr:(NSString *)userNonceStr WithData:(AIIpfsGetDownLoadBlock)dataBlock{
    [self createIpfsUploadFile];
    NSString *docsdir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    NSString *allIdentifyPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AICaches/phoneOperator.zip"];
    __weak __typeof(self)weakSelf = self;
    [self getIpfsDataWithBaseUrl:baseUrl WithHash:hashStr SaveFilePath:allIdentifyPath WithDataType:AIIpfsReturnDataTypeFile WithData:^(id obj) {
        NSString *allPath = [docsdir stringByAppendingPathComponent:@"AIIPFSUPLOADFILE/AICaches/phoneOperator"];
        [weakSelf unzipFileWithDestinationPath:allPath WithSourceFilePath:allIdentifyPath];
        NSString *identifyDataPath = [allPath stringByAppendingPathComponent:@"/phoneOperator.txt"];
//        NSData *identifyData= [[NSData dataWithContentsOfFile:identifyDataPath] AIAES256DecryptWithKey:password];
        NSData *identifyData= [[NSData dataWithContentsOfFile:identifyDataPath] AES2568DecryptWithKey:password iv:userNonceStr];
        NSMutableDictionary *dataDict = [NSJSONSerialization JSONObjectWithData:identifyData options:NSJSONReadingMutableLeaves error:nil];
        NSLog(@"dataDict= %@",dataDict);
        dataBlock(dataDict);
    }];
}

//上传文件
- (void)upload:(NSString *)url WithBaseUrl:(NSString *)baseUrl WithFilePath:(NSString *)filePath WithFileName:(NSString *)fileName WithFileType:(NSString *)fileType Success:(AIIpfsReturnHashDataBlock)hashStr{
    
    NSString *requestUrl = [NSString stringWithFormat:@"%@%@",baseUrl,url];
    AISAFHTTPSessionManager *manager = [AISAFHTTPSessionManager manager];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/html",@"application/zip",@"text/plain",nil];
    [manager.requestSerializer setValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
//    NSString *path = [[NSBundle mainBundle] pathForResource:fileName ofType:fileType];
    NSData *data = [NSData dataWithContentsOfFile:filePath];
    NSLog(@"data=%@",data);
    
    [manager POST:requestUrl parameters:nil constructingBodyWithBlock:^(id<AISAFMultipartFormData>  _Nonnull formData) {
        // 上传图片，以文件流的格式，这里注意：name是指服务器端的文件夹名字
        [formData appendPartWithFileData:data name:fileName fileName:fileName mimeType:fileType];
        //[formData appendPartWithFileData:data1 name:@"file" fileName:@"file2" mimeType:@"text/plain"];
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        NSLog(@"uploadProgress = %@",uploadProgress);
        if (self.progressBlock) {
            self.progressBlock(uploadProgress);
        }
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"Content: %@",[responseObject description]);
        NSDictionary *dict  = responseObject;
        NSLog(@"dict = %@",dict);
        NSString *hash = dict[@"Hash"];
        hashStr(hash);
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"Error: %@", error);
    }];
}

//上传图片
- (void)upload:(NSString *)url WithPhotoName:(NSString *)photoName WithPhotoType:(NSString *)photoType Success:(AIIpfsReturnHashDataBlock)hashStr{
    
    NSString *requestUrl = [NSString stringWithFormat:@"%@%@",kBaseUrl,url];
    AISAFHTTPSessionManager *manager = [AISAFHTTPSessionManager manager];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/html",nil];
    [manager.requestSerializer setValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    
    NSString *path = [[NSBundle mainBundle] pathForResource:photoName ofType:photoType];
    NSData *data = [NSData dataWithContentsOfFile:path];
    NSLog(@"data1=%@",data);
    
    [manager POST:requestUrl parameters:nil constructingBodyWithBlock:^(id<AISAFMultipartFormData>  _Nonnull formData) {
        // 上传图片，以文件流的格式，这里注意：name是指服务器端的文件夹名字
        [formData appendPartWithFileData:data name:photoType fileName:photoType mimeType:photoType];
        //[formData appendPartWithFileData:data1 name:@"file" fileName:@"file2" mimeType:@"text/plain"];
        
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"Content: %@",[responseObject description]);
        NSDictionary *dict  = responseObject;
        NSLog(@"dict = %@",dict);
        NSString *hash = dict[@"Hash"];
        hashStr(hash);
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"Error: %@", error);
    }];
    
}

//上传字典
- (void)upload:(NSString *)url WithDict:(NSDictionary *)dictData WithDictName:(NSString *)dictName Success:(AIIpfsReturnHashDataBlock)hashStr{
    
    NSString *requestUrl = [NSString stringWithFormat:@"%@%@",kBaseUrl,url];
    AISAFHTTPSessionManager *manager = [AISAFHTTPSessionManager manager];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/html",nil];
    [manager.requestSerializer setValue:@"application/x-www-form-urlencoded; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    
//    NSData *data= [NSJSONSerialization dataWithJSONObject:dictData options:NSJSONWritingPrettyPrinted error:nil];
    NSData *identifyData= [NSJSONSerialization dataWithJSONObject:dictData options:NSJSONWritingPrettyPrinted error:nil];
    NSData *encryptionData = [identifyData AES256EncryptWithKey:@"aiIdentify" iv:@"aiIdentify"];
    
//    NSLog(@"encryptionData = %@",encryptionData);
    [manager POST:requestUrl parameters:nil constructingBodyWithBlock:^(id<AISAFMultipartFormData>  _Nonnull formData) {
        // 上传图片，以文件流的格式，这里注意：name是指服务器端的文件夹名字
        [formData appendPartWithFileData:encryptionData name:dictName fileName:dictName mimeType:@""];
        //[formData appendPartWithFileData:data1 name:@"file" fileName:@"file2" mimeType:@"text/plain"];
    } progress:^(NSProgress * _Nonnull uploadProgress) {
        NSLog(@"uploadProgress = %@",uploadProgress);
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"Content: %@",[responseObject description]);
        NSDictionary *dict  = responseObject;
        NSLog(@"dict = %@",dict);
        NSString *hash = dict[@"Hash"];
        hashStr(hash);
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"Error: %@", error);
    }];
}

//通过hash获取ipfs上的数据
- (void)getIpfsDataWithBaseUrl:(NSString *)baseUrl WithHash:(NSString *)hashStr SaveFilePath:(NSString *)saveFilePath WithDataType:(AIIpfsReturnDataType)dataType WithData:(AIIpfsGetDownLoadBlock)dataBlock{
    
    NSString *requestUrl = [NSString stringWithFormat:@"%@/cat?arg=%@",baseUrl,hashStr];
    AISAFHTTPSessionManager *manager = [AISAFHTTPSessionManager manager];
    manager.responseSerializer = [AISAFHTTPResponseSerializer serializer];
    manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json",@"text/html",@"text/plain",@"image/jpeg",@"image/png",@"application/zip",@"application/octet-stream",nil];
    //    [manager.requestSerializer setValue:@"text/plain; charset=utf-8" forHTTPHeaderField:@"Content-Type"];
    [manager GET:requestUrl parameters:nil progress:^(NSProgress * _Nonnull downloadProgress) {
    } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        NSLog(@"Content: %@",[responseObject description]);
        NSString *str = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        NSLog(@"url=%@\n,content=%@",requestUrl,str);
   
//        NSLog(@"responseObject = %@",responseObject);
//        NSData *data = [responseObject AES256DecryptWithKey:[@"123456" dataUsingEncoding:NSUTF8StringEncoding]];
//        NSString *oneStr = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
//        NSLog(@"oneStr = %@",oneStr);
        
        if (dataType == AIIpfsReturnDataTypePhoto) {
            UIImage *img = [UIImage imageWithData:responseObject];
            dataBlock(img);
        }
        if (dataType == AIIpfsReturnDataTypeDict) {
            NSDictionary *dictionary =[NSJSONSerialization JSONObjectWithData:responseObject options:NSJSONReadingMutableLeaves error:nil];
            NSLog(@"dictionary = %@",dictionary);
            NSData *data = [responseObject AES2568DecryptWithKey:@"aiIdentify" iv:@"aiIdentify"];
             NSDictionary *twoDict =[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableLeaves error:nil];
            NSLog(@"twoDict = %@",twoDict);
            
            dataBlock(dictionary);
        }
        if (dataType == AIIpfsReturnDataTypeFile) {
            NSData *data = responseObject;
         
            NSFileManager *fileManager = [NSFileManager defaultManager];
            BOOL isSuccess = [fileManager createFileAtPath:saveFilePath contents:data attributes:nil];
            if (isSuccess) {
                dataBlock(saveFilePath);
            }else{
                NSLog(@"从ipfs获取数据失败");
            }
        }
        if (dataType == AIIpfsReturnDataTypeNSString) {
            NSData *data = responseObject;
            
            NSFileManager *fileManager = [NSFileManager defaultManager];
            BOOL isSuccess = [fileManager createFileAtPath:saveFilePath contents:data attributes:nil];
            if (isSuccess) {
                dataBlock(saveFilePath);
            }else{
                NSLog(@"从ipfs获取数据失败");
            }
        }
       
        //        UIImageView *imageView = [[UIImageView alloc] initWithImage:iamge];
        //        [self.view addSubview:imageView];
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"Error: %@", error);
    }];
}



//压缩单个文件
- (void)createZipFileWithDestinationPath:(NSString *)destinationPath WithSourceFilePath:(NSString *)sourceFilePath{
    //    //目的路径
    //    NSString *destinationPath = @"/Users/Administrator/Desktop/wzg.zip";//注意是这个是 zip 格式的后缀
    //    //源文件路径
    //    NSString *sourceFilePath = @"/Users/Administrator/Desktop/wzg.txt";
    //    //数组里可以放多个源文件，这些文件会被同一打包成压缩包，到 destinationPath 这个路径下。
    if ([SSZipArchive createZipFileAtPath:destinationPath withFilesAtPaths:@[sourceFilePath]]) {
        NSLog(@"压缩成功");
    }
    else {
        NSLog(@"压缩失败");
    }
}
//压缩多个文件
- (void)createZipFileWithDestinationPath:(NSString *)destinationPath WithSourceFileArray:(NSArray *)sourceFileArray{
    
    if ([SSZipArchive createZipFileAtPath:destinationPath withFilesAtPaths:sourceFileArray]) {
        NSLog(@"压缩成功");
    }
    else {
        NSLog(@"压缩失败");
    }
    
}


//解压文件
- (void)unzipFileWithDestinationPath:(NSString *)destinationPath WithSourceFilePath:(NSString *)sourceFilePath {

    if ([SSZipArchive unzipFileAtPath:sourceFilePath toDestination:destinationPath delegate:self]) {
        NSLog(@"解压成功");
    } else {
        NSLog(@"解压失败");
    }
}

//沙盒建立上传文件夹
- (void)createIpfsUploadFile{
    
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
    NSString *idnetifyPath = [dataFilePath stringByAppendingString:@"/AIIdentify"];
    if (![fileManager fileExistsAtPath:idnetifyPath]) {
        [fileManager createDirectoryAtPath:idnetifyPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *bankCardPath = [dataFilePath stringByAppendingString:@"/AIBankCard"];
    if (![fileManager fileExistsAtPath:bankCardPath]) {
        [fileManager createDirectoryAtPath:bankCardPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *phoneOperatorPath = [dataFilePath stringByAppendingString:@"/AIPhotoOperator"];
    if (![fileManager fileExistsAtPath:phoneOperatorPath]) {
        [fileManager createDirectoryAtPath:phoneOperatorPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *otherPath = [dataFilePath stringByAppendingString:@"/AIOtherData"];
    if (![fileManager fileExistsAtPath:phoneOperatorPath]) {
        [fileManager createDirectoryAtPath:otherPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *cachesPath = [dataFilePath stringByAppendingString:@"/AICaches"];
    if (![fileManager fileExistsAtPath:cachesPath]) {
        [fileManager createDirectoryAtPath:cachesPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
}

- (void)writeToFileSavePath:(NSString *)dataFilePath WithData:(NSData *)fromData{
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isSuccess = [fileManager createFileAtPath:dataFilePath contents:fromData attributes:nil];
    if (isSuccess) {
        NSLog(@"存储成功");
    }else{
        NSLog(@"error");
    }
}

@end
