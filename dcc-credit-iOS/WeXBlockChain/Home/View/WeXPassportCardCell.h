//
//  WeXPassportCardCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WeXPassportCardCell : UITableViewCell

@end

@interface WeXPassportCardPassCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIView *cardView;
@property (weak, nonatomic) IBOutlet UILabel *nickLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UIImageView *headImageView;

@end

@interface WeXPassportCardIDCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIView *cardView;
@property (weak, nonatomic) IBOutlet UIImageView *backImageView;

@end

@interface WeXPassportCardDigitalCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIView *cardView;
@property (weak, nonatomic) IBOutlet UILabel *totolAssetLabel;
@property (weak, nonatomic) IBOutlet UITableView *tokenTabelView;
@property (weak, nonatomic) IBOutlet UIImageView *backImageView;

@end
