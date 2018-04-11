//
//  WeXGetTicketModal.h
//  WeXBlockChain
//
//  Created by wcc on 2017/11/15.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXBaseNetModal.h"


@interface WeXGetTicketParamModal : WeXBaseNetModal

@end

@interface  WeXGetTicketResponseModal : WeXBaseNetModal

@property (nonatomic,copy)NSString *image;

@property (nonatomic,copy)NSString *ticket;

@property (nonatomic,copy)NSString *answer;

@end
