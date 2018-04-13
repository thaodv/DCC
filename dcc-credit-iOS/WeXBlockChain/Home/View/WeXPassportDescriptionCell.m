//
//  WeXPassportDescriptionCell.m
//  WeXBlockChain
//
//  Created by wcc on 2017/12/8.
//  Copyright © 2017年 WeX. All rights reserved.
//

#import "WeXPassportDescriptionCell.h"
#import "WeXPassportDescriptionModal.h"

#define contentLabelFont 17

#define contentLabelLineSpace 6

@implementation WeXPassportDescriptionCell


- (void)setModel:(WeXPassportDescriptionModal *)model{
    
    _model = model;
  
    //文字内容
    NSString *content = model.content;
    
    //设置间距
    NSMutableAttributedString *attrStr = [[NSMutableAttributedString alloc] initWithString:content];
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineSpacing = contentLabelLineSpace;
    paragraphStyle.alignment = NSTextAlignmentLeft;
    paragraphStyle.hyphenationFactor = 1.0;
    paragraphStyle.firstLineHeadIndent = 0.0;
    paragraphStyle.paragraphSpacingBefore = 0.0;
    paragraphStyle.headIndent = 0;
    paragraphStyle.tailIndent = 0;
    
    [attrStr addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, content.length)];
    
    self.titleLabel.attributedText = attrStr;
    
    self.titleLabel.lineBreakMode = NSLineBreakByTruncatingTail;
    
    //4行文字高度
    CGFloat contentHeight = [content boundingRectWithSize:CGSizeMake(kScreenWidth-20, CGFLOAT_MAX) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:contentLabelFont],NSParagraphStyleAttributeName:paragraphStyle} context:nil].size.height;
    
    [self.arrowBtn addTarget:self action:@selector(arrowBtnClick) forControlEvents:UIControlEventTouchUpInside];
    
 
    if (model.isSpread) {
        self.titleLabel.numberOfLines = 0;
        self.titleLabelHeightConst.constant = contentHeight+1;
        
         [self.arrowBtn setImage:[UIImage imageNamed:@"upArrow"] forState:UIControlStateNormal];
    }
    else
    {
         self.titleLabel.numberOfLines = 1;
        [self.arrowBtn setImage:[UIImage imageNamed:@"downArrow"] forState:UIControlStateNormal];
        
    }
 
}

- (void)arrowBtnClick{
    
    self.model.isSpread = !self.model.isSpread;
    
    if (self.refreshCellBlock) {
        self.refreshCellBlock(self);
        
    }
    
}

+ (CGFloat)heightWithModel:(WeXPassportDescriptionModal *)model;
{
    NSString *content = model.content;
    
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineSpacing = contentLabelLineSpace;
    paragraphStyle.alignment = NSTextAlignmentLeft;
    paragraphStyle.hyphenationFactor = 1.0;
    paragraphStyle.firstLineHeadIndent = 0.0;
    paragraphStyle.paragraphSpacingBefore = 0.0;
    paragraphStyle.headIndent = 0;
    paragraphStyle.tailIndent = 0;
    
    CGFloat contentHeight = [content boundingRectWithSize:CGSizeMake(kScreenWidth-20, CGFLOAT_MAX) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:contentLabelFont],NSParagraphStyleAttributeName:paragraphStyle} context:nil].size.height;

    if (model.isSpread) {//展开了
        return contentHeight+24;
    }
    else//没有展开
    {
        return 44;
    }

}

@end
