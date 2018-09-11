// UIButton+AISAFNetworking.m
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

#import "UIButton+AISAFNetworking.h"

#import <objc/runtime.h>

#if TARGET_OS_IOS || TARGET_OS_TV

#import "UIImageView+AISAFNetworking.h"
#import "AISAFImageDownloader.h"

@interface UIButton (_AISAFNetworking)
@end

@implementation UIButton (_AISAFNetworking)

#pragma mark -

static char AISAFImageDownloadReceiptNormal;
static char AISAFImageDownloadReceiptHighlighted;
static char AISAFImageDownloadReceiptSelected;
static char AISAFImageDownloadReceiptDisabled;

static const char * AISAF_imageDownloadReceiptKeyForState(UIControlState state) {
    switch (state) {
        case UIControlStateHighlighted:
            return &AISAFImageDownloadReceiptHighlighted;
        case UIControlStateSelected:
            return &AISAFImageDownloadReceiptSelected;
        case UIControlStateDisabled:
            return &AISAFImageDownloadReceiptDisabled;
        case UIControlStateNormal:
        default:
            return &AISAFImageDownloadReceiptNormal;
    }
}

- (AISAFImageDownloadReceipt *)AISAF_imageDownloadReceiptForState:(UIControlState)state {
    return (AISAFImageDownloadReceipt *)objc_getAssociatedObject(self, AISAF_imageDownloadReceiptKeyForState(state));
}

- (void)AISAF_setImageDownloadReceipt:(AISAFImageDownloadReceipt *)imageDownloadReceipt
                           forState:(UIControlState)state
{
    objc_setAssociatedObject(self, AISAF_imageDownloadReceiptKeyForState(state), imageDownloadReceipt, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

#pragma mark -

static char AISAFBackgroundImageDownloadReceiptNormal;
static char AISAFBackgroundImageDownloadReceiptHighlighted;
static char AISAFBackgroundImageDownloadReceiptSelected;
static char AISAFBackgroundImageDownloadReceiptDisabled;

static const char * AISAF_backgroundImageDownloadReceiptKeyForState(UIControlState state) {
    switch (state) {
        case UIControlStateHighlighted:
            return &AISAFBackgroundImageDownloadReceiptHighlighted;
        case UIControlStateSelected:
            return &AISAFBackgroundImageDownloadReceiptSelected;
        case UIControlStateDisabled:
            return &AISAFBackgroundImageDownloadReceiptDisabled;
        case UIControlStateNormal:
        default:
            return &AISAFBackgroundImageDownloadReceiptNormal;
    }
}

- (AISAFImageDownloadReceipt *)AISAF_backgroundImageDownloadReceiptForState:(UIControlState)state {
    return (AISAFImageDownloadReceipt *)objc_getAssociatedObject(self, AISAF_backgroundImageDownloadReceiptKeyForState(state));
}

- (void)AISAF_setBackgroundImageDownloadReceipt:(AISAFImageDownloadReceipt *)imageDownloadReceipt
                                     forState:(UIControlState)state
{
    objc_setAssociatedObject(self, AISAF_backgroundImageDownloadReceiptKeyForState(state), imageDownloadReceipt, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

@end

#pragma mark -

@implementation UIButton (AISAFNetworking)

+ (AISAFImageDownloader *)sharedImageDownloader {

    return objc_getAssociatedObject(self, @selector(sharedImageDownloader)) ?: [AISAFImageDownloader defaultInstance];
}

+ (void)setSharedImageDownloader:(AISAFImageDownloader *)imageDownloader {
    objc_setAssociatedObject(self, @selector(sharedImageDownloader), imageDownloader, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

#pragma mark -

- (void)setImageForState:(UIControlState)state
                 withURL:(NSURL *)url
{
    [self setImageForState:state withURL:url placeholderImage:nil];
}

- (void)setImageForState:(UIControlState)state
                 withURL:(NSURL *)url
        placeholderImage:(UIImage *)placeholderImage
{
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    [request addValue:@"image/*" forHTTPHeaderField:@"Accept"];

    [self setImageForState:state withURLRequest:request placeholderImage:placeholderImage success:nil failure:nil];
}

- (void)setImageForState:(UIControlState)state
          withURLRequest:(NSURLRequest *)urlRequest
        placeholderImage:(nullable UIImage *)placeholderImage
                 success:(nullable void (^)(NSURLRequest *request, NSHTTPURLResponse * _Nullable response, UIImage *image))success
                 failure:(nullable void (^)(NSURLRequest *request, NSHTTPURLResponse * _Nullable response, NSError *error))failure
{
    if ([self isActiveTaskURLEqualToURLRequest:urlRequest forState:state]) {
        return;
    }

    [self cancelImageDownloadTaskForState:state];

    AISAFImageDownloader *downloader = [[self class] sharedImageDownloader];
    id <AISAFImageRequestCache> imageCache = downloader.imageCache;

    //Use the image from the image cache if it exists
    UIImage *cachedImage = [imageCache imageforRequest:urlRequest withAdditionalIdentifier:nil];
    if (cachedImage) {
        if (success) {
            success(urlRequest, nil, cachedImage);
        } else {
            [self setImage:cachedImage forState:state];
        }
        [self AISAF_setImageDownloadReceipt:nil forState:state];
    } else {
        if (placeholderImage) {
            [self setImage:placeholderImage forState:state];
        }

        __weak __typeof(self)weakSelf = self;
        NSUUID *downloadID = [NSUUID UUID];
        AISAFImageDownloadReceipt *receipt;
        receipt = [downloader
                   downloadImageForURLRequest:urlRequest
                   withReceiptID:downloadID
                   success:^(NSURLRequest * _Nonnull request, NSHTTPURLResponse * _Nullable response, UIImage * _Nonnull responseObject) {
                       __strong __typeof(weakSelf)strongSelf = weakSelf;
                       if ([[strongSelf AISAF_imageDownloadReceiptForState:state].receiptID isEqual:downloadID]) {
                           if (success) {
                               success(request, response, responseObject);
                           } else if(responseObject) {
                               [strongSelf setImage:responseObject forState:state];
                           }
                           [strongSelf AISAF_setImageDownloadReceipt:nil forState:state];
                       }

                   }
                   failure:^(NSURLRequest * _Nonnull request, NSHTTPURLResponse * _Nullable response, NSError * _Nonnull error) {
                       __strong __typeof(weakSelf)strongSelf = weakSelf;
                       if ([[strongSelf AISAF_imageDownloadReceiptForState:state].receiptID isEqual:downloadID]) {
                           if (failure) {
                               failure(request, response, error);
                           }
                           [strongSelf  AISAF_setImageDownloadReceipt:nil forState:state];
                       }
                   }];

        [self AISAF_setImageDownloadReceipt:receipt forState:state];
    }
}

#pragma mark -

- (void)setBackgroundImageForState:(UIControlState)state
                           withURL:(NSURL *)url
{
    [self setBackgroundImageForState:state withURL:url placeholderImage:nil];
}

- (void)setBackgroundImageForState:(UIControlState)state
                           withURL:(NSURL *)url
                  placeholderImage:(nullable UIImage *)placeholderImage
{
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    [request addValue:@"image/*" forHTTPHeaderField:@"Accept"];

    [self setBackgroundImageForState:state withURLRequest:request placeholderImage:placeholderImage success:nil failure:nil];
}

- (void)setBackgroundImageForState:(UIControlState)state
                    withURLRequest:(NSURLRequest *)urlRequest
                  placeholderImage:(nullable UIImage *)placeholderImage
                           success:(nullable void (^)(NSURLRequest *request, NSHTTPURLResponse * _Nullable response, UIImage *image))success
                           failure:(nullable void (^)(NSURLRequest *request, NSHTTPURLResponse * _Nullable response, NSError *error))failure
{
    if ([self isActiveBackgroundTaskURLEqualToURLRequest:urlRequest forState:state]) {
        return;
    }

    [self cancelBackgroundImageDownloadTaskForState:state];

    AISAFImageDownloader *downloader = [[self class] sharedImageDownloader];
    id <AISAFImageRequestCache> imageCache = downloader.imageCache;

    //Use the image from the image cache if it exists
    UIImage *cachedImage = [imageCache imageforRequest:urlRequest withAdditionalIdentifier:nil];
    if (cachedImage) {
        if (success) {
            success(urlRequest, nil, cachedImage);
        } else {
            [self setBackgroundImage:cachedImage forState:state];
        }
        [self AISAF_setBackgroundImageDownloadReceipt:nil forState:state];
    } else {
        if (placeholderImage) {
            [self setBackgroundImage:placeholderImage forState:state];
        }

        __weak __typeof(self)weakSelf = self;
        NSUUID *downloadID = [NSUUID UUID];
        AISAFImageDownloadReceipt *receipt;
        receipt = [downloader
                   downloadImageForURLRequest:urlRequest
                   withReceiptID:downloadID
                   success:^(NSURLRequest * _Nonnull request, NSHTTPURLResponse * _Nullable response, UIImage * _Nonnull responseObject) {
                       __strong __typeof(weakSelf)strongSelf = weakSelf;
                       if ([[strongSelf AISAF_backgroundImageDownloadReceiptForState:state].receiptID isEqual:downloadID]) {
                           if (success) {
                               success(request, response, responseObject);
                           } else if(responseObject) {
                               [strongSelf setBackgroundImage:responseObject forState:state];
                           }
                           [strongSelf AISAF_setBackgroundImageDownloadReceipt:nil forState:state];
                       }

                   }
                   failure:^(NSURLRequest * _Nonnull request, NSHTTPURLResponse * _Nullable response, NSError * _Nonnull error) {
                       __strong __typeof(weakSelf)strongSelf = weakSelf;
                       if ([[strongSelf AISAF_backgroundImageDownloadReceiptForState:state].receiptID isEqual:downloadID]) {
                           if (failure) {
                               failure(request, response, error);
                           }
                           [strongSelf  AISAF_setBackgroundImageDownloadReceipt:nil forState:state];
                       }
                   }];

        [self AISAF_setBackgroundImageDownloadReceipt:receipt forState:state];
    }
}

#pragma mark -

- (void)cancelImageDownloadTaskForState:(UIControlState)state {
    AISAFImageDownloadReceipt *receipt = [self AISAF_imageDownloadReceiptForState:state];
    if (receipt != nil) {
        [[self.class sharedImageDownloader] cancelTaskForImageDownloadReceipt:receipt];
        [self AISAF_setImageDownloadReceipt:nil forState:state];
    }
}

- (void)cancelBackgroundImageDownloadTaskForState:(UIControlState)state {
    AISAFImageDownloadReceipt *receipt = [self AISAF_backgroundImageDownloadReceiptForState:state];
    if (receipt != nil) {
        [[self.class sharedImageDownloader] cancelTaskForImageDownloadReceipt:receipt];
        [self AISAF_setBackgroundImageDownloadReceipt:nil forState:state];
    }
}

- (BOOL)isActiveTaskURLEqualToURLRequest:(NSURLRequest *)urlRequest forState:(UIControlState)state {
    AISAFImageDownloadReceipt *receipt = [self AISAF_imageDownloadReceiptForState:state];
    return [receipt.task.originalRequest.URL.absoluteString isEqualToString:urlRequest.URL.absoluteString];
}

- (BOOL)isActiveBackgroundTaskURLEqualToURLRequest:(NSURLRequest *)urlRequest forState:(UIControlState)state {
    AISAFImageDownloadReceipt *receipt = [self AISAF_backgroundImageDownloadReceiptForState:state];
    return [receipt.task.originalRequest.URL.absoluteString isEqualToString:urlRequest.URL.absoluteString];
}


@end

#endif
