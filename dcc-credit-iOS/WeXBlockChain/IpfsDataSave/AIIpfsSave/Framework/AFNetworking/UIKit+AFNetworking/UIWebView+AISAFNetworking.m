// UIWebView+AISAFNetworking.m
// Copyright (c) 2011–2016 Alamofire Software Foundation ( http://alamofire.org/ )
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

#import "UIWebView+AISAFNetworking.h"

#import <objc/runtime.h>

#if TARGET_OS_IOS

#import "AISAFHTTPSessionManager.h"
#import "AISAFURLResponseSerialization.h"
#import "AISAFURLRequestSerialization.h"

@interface UIWebView (_AISAFNetworking)
@property (readwrite, nonatomic, strong, setter = AISAF_setURLSessionTask:) NSURLSessionDataTask *AISAF_URLSessionTask;
@end

@implementation UIWebView (_AISAFNetworking)

- (NSURLSessionDataTask *)AISAF_URLSessionTask {
    return (NSURLSessionDataTask *)objc_getAssociatedObject(self, @selector(AISAF_URLSessionTask));
}

- (void)AISAF_setURLSessionTask:(NSURLSessionDataTask *)AISAF_URLSessionTask {
    objc_setAssociatedObject(self, @selector(AISAF_URLSessionTask), AISAF_URLSessionTask, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

@end

#pragma mark -

@implementation UIWebView (AISAFNetworking)

- (AISAFHTTPSessionManager  *)sessionManager {
    static AISAFHTTPSessionManager *_AISAF_defaultHTTPSessionManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _AISAF_defaultHTTPSessionManager = [[AISAFHTTPSessionManager alloc] initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
        _AISAF_defaultHTTPSessionManager.requestSerializer = [AISAFHTTPRequestSerializer serializer];
        _AISAF_defaultHTTPSessionManager.responseSerializer = [AISAFHTTPResponseSerializer serializer];
    });

    return objc_getAssociatedObject(self, @selector(sessionManager)) ?: _AISAF_defaultHTTPSessionManager;
}

- (void)setSessionManager:(AISAFHTTPSessionManager *)sessionManager {
    objc_setAssociatedObject(self, @selector(sessionManager), sessionManager, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (AISAFHTTPResponseSerializer <AISAFURLResponseSerialization> *)responseSerializer {
    static AISAFHTTPResponseSerializer <AISAFURLResponseSerialization> *_AISAF_defaultResponseSerializer = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _AISAF_defaultResponseSerializer = [AISAFHTTPResponseSerializer serializer];
    });

    return objc_getAssociatedObject(self, @selector(responseSerializer)) ?: _AISAF_defaultResponseSerializer;
}

- (void)setResponseSerializer:(AISAFHTTPResponseSerializer<AISAFURLResponseSerialization> *)responseSerializer {
    objc_setAssociatedObject(self, @selector(responseSerializer), responseSerializer, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

#pragma mark -

- (void)loadRequest:(NSURLRequest *)request
           progress:(NSProgress * _Nullable __autoreleasing * _Nullable)progress
            success:(NSString * (^)(NSHTTPURLResponse *response, NSString *HTML))success
            failure:(void (^)(NSError *error))failure
{
    [self loadRequest:request MIMEType:nil textEncodingName:nil progress:progress success:^NSData *(NSHTTPURLResponse *response, NSData *data) {
        NSStringEncoding stringEncoding = NSUTF8StringEncoding;
        if (response.textEncodingName) {
            CFStringEncoding encoding = CFStringConvertIANACharSetNameToEncoding((CFStringRef)response.textEncodingName);
            if (encoding != kCFStringEncodingInvalidId) {
                stringEncoding = CFStringConvertEncodingToNSStringEncoding(encoding);
            }
        }

        NSString *string = [[NSString alloc] initWithData:data encoding:stringEncoding];
        if (success) {
            string = success(response, string);
        }

        return [string dataUsingEncoding:stringEncoding];
    } failure:failure];
}

- (void)loadRequest:(NSURLRequest *)request
           MIMEType:(NSString *)MIMEType
   textEncodingName:(NSString *)textEncodingName
           progress:(NSProgress * _Nullable __autoreleasing * _Nullable)progress
            success:(NSData * (^)(NSHTTPURLResponse *response, NSData *data))success
            failure:(void (^)(NSError *error))failure
{
    NSParameterAssert(request);

    if (self.AISAF_URLSessionTask.state == NSURLSessionTaskStateRunning || self.AISAF_URLSessionTask.state == NSURLSessionTaskStateSuspended) {
        [self.AISAF_URLSessionTask cancel];
    }
    self.AISAF_URLSessionTask = nil;

    __weak __typeof(self)weakSelf = self;
    __block NSURLSessionDataTask *dataTask;
    dataTask = [self.sessionManager
                dataTaskWithRequest:request
                uploadProgress:nil
                downloadProgress:nil
                completionHandler:^(NSURLResponse * _Nonnull response, id  _Nonnull responseObject, NSError * _Nullable error) {
                    __strong __typeof(weakSelf) strongSelf = weakSelf;
                    if (error) {
                        if (failure) {
                            failure(error);
                        }
                    } else {
                        if (success) {
                            success((NSHTTPURLResponse *)response, responseObject);
                        }
                        [strongSelf loadData:responseObject MIMEType:MIMEType textEncodingName:textEncodingName baseURL:[dataTask.currentRequest URL]];

                        if ([strongSelf.delegate respondsToSelector:@selector(webViewDidFinishLoad:)]) {
                            [strongSelf.delegate webViewDidFinishLoad:strongSelf];
                        }
                    }
                }];
    self.AISAF_URLSessionTask = dataTask;
    if (progress != nil) {
        *progress = [self.sessionManager downloadProgressForTask:dataTask];
    }
    [self.AISAF_URLSessionTask resume];

    if ([self.delegate respondsToSelector:@selector(webViewDidStartLoad:)]) {
        [self.delegate webViewDidStartLoad:self];
    }
}

@end

#endif
