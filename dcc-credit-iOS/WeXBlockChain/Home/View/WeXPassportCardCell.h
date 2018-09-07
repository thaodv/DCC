//
//  WeXPassportCardCell.h
//  WeXBlockChain
//
//  Created by wcc on 2018/1/29.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WeXNetworkCheckModel.h"

@interface WeXPassportCardCell : UITableViewCell

@end

@interface WeXPassportCardPassCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *cardImageView;
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
{
    
}
@property (weak, nonatomic) IBOutlet UIView *cardView;
@property (weak, nonatomic) IBOutlet UITableView *tokenTabelView;
@property (weak, nonatomic) IBOutlet UIImageView *backImageView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *detailLabel;
@property (weak, nonatomic) IBOutlet UILabel *totalAssetLabel;

@property (weak, nonatomic) IBOutlet UIView *netbackView;

@property (weak, nonatomic) IBOutlet UIImageView *networkImageView;

- (void)setNodeNetworkDelayModel:(WeXNetworkCheckModel *)model;


@end

@interface WeXPassportCardChooseCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIButton *activityButton;
@property (weak, nonatomic) IBOutlet UILabel *activityLabel;
@property (weak, nonatomic) IBOutlet UILabel *creditLabel;
@property (weak, nonatomic) IBOutlet UIButton *creditButton;
@property (weak, nonatomic) IBOutlet UIButton *borrowButton;
@property (weak, nonatomic) IBOutlet UILabel *loanLabel;
@property (weak, nonatomic) IBOutlet UIButton *inviteButton;
@property (weak, nonatomic) IBOutlet UILabel *reportLabel;

@end

@interface WeXPassportCardChoose2Cell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIButton *activityButton;
@property (weak, nonatomic) IBOutlet UIButton *creditButton;
@property (weak, nonatomic) IBOutlet UIButton *borrowButton;
@property (weak, nonatomic) IBOutlet UIButton *inviteButton;

@end
