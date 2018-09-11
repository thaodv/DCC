//
//  WeXIpfsNodeModel.h
//  WeXBlockChain
//
//  Created by wh on 2018/8/27.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, WeXIpfsNodeModelType) {
    WeXIpfsNodeModelTypeDefault,//默认节点
    WeXIpfsNodeModelTypeCustom//自定义节点
};

@interface WeXIpfsNodeModel : NSObject

@property (nonatomic,copy)NSString *nameTitle;
@property (nonatomic,copy)NSString *describeStr;
@property (nonatomic,assign)BOOL isSelected;
@property (nonatomic,assign)BOOL isDefault;
@property (nonatomic,assign)WeXIpfsNodeModelType modeType;

@end
