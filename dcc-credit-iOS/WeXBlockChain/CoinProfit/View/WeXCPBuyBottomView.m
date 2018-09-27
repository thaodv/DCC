//
//  WeXCPBuyBottomView.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPBuyBottomView.h"
#import "NSString+WexTool.h"

@interface WeXCPBuyBottomView ()

@property(nonatomic, copy) void (^WeXCPBuyBottomClick)(WeXCPBuyBottomViewType);
@property (nonatomic, weak) UILabel *tipsLab;
@property (nonatomic, weak) YYLabel *contentLab;
@property (nonatomic, weak) UIButton *buyInButton;
@property (nonatomic, assign) WexCPBuyButtonTipsType tipsType;


@end

@implementation WeXCPBuyBottomView

static NSString * const kPrivateText = @"• 币生息只能使用私链上的DCC；\n• 通过跨链交易将公链上的DCC转移到私链上，即可购买币生息产品。公链转到私链 >";
static NSString * const kPrivateHighText = @"公链转到私链 >";
static NSString * const kPublicText = @"• 认购成功后不可撤销。";


+ (instancetype)createBuyBottomView:(CGRect)frame
                         clickEvent:(void(^)(WeXCPBuyBottomViewType))click {
    WeXCPBuyBottomView *bottomView = [[WeXCPBuyBottomView alloc] initWithFrame:frame tipsType:WexCPBuyButtonTipsTypePrivate];
    [bottomView setBackgroundColor:[UIColor whiteColor]];
    bottomView.WeXCPBuyBottomClick = click;
    return bottomView;
}

+ (instancetype)createBuyBottomView:(CGRect)frame
                           tipsType:(WexCPBuyButtonTipsType)type
                         clickEvent:(void(^)(WeXCPBuyBottomViewType))click {
    WeXCPBuyBottomView *bottomView = [[WeXCPBuyBottomView alloc] initWithFrame:frame tipsType:type];
    [bottomView setBackgroundColor:[UIColor whiteColor]];
    bottomView.WeXCPBuyBottomClick = click;
    return bottomView;
}

- (id)initWithFrame:(CGRect)frame tipsType:(WexCPBuyButtonTipsType)type {
    if (self = [super initWithFrame:frame]) {
        self.tipsType = type;
        [self p_wex_loadViews];
        [self wex_layoutConstraints];
    }
    return self;
}
- (void)p_wex_loadViews {
    UILabel *tipsLab = CreateLeftAlignmentLabel(WexFont(14), WexDefault4ATitleColor);
    NSMutableAttributedString *attri = [[NSMutableAttributedString alloc] initWithString:@"温馨提示："];
    //NSTextAttachment可以将要插入的图片作为特殊字符处理
    NSTextAttachment *attch = [[NSTextAttachment alloc] init];
    //定义图片内容及位置和大小
    attch.image = [UIImage imageNamed:@"ic_warning_black_24dp_2x"];
    attch.bounds = CGRectMake(0, -2, 17, 14);
    NSAttributedString *string = [NSAttributedString attributedStringWithAttachment:attch];
    [attri insertAttributedString:string atIndex:0];
    tipsLab.attributedText = attri;
    [self addSubview:tipsLab];
    self.tipsLab = tipsLab;
    
//    NSString *text = @"• 币生息只能使用私链上的DCC；\n• 通过跨链交易将公链上的DCC转移到私链上，即可购买币生息产品。公链转到私链 >";
//    NSString *highText = @"公链转到私链 >";
    YYLabel *contentLab = [YYLabel new];
    contentLab.textColor = WexDefault4ATitleColor;
    contentLab.font = WexFont(14.0f);
    contentLab.textAlignment = NSTextAlignmentLeft;
    contentLab.numberOfLines = 0;
    contentLab.preferredMaxLayoutWidth = kScreenWidth - 20;
    switch (self.tipsType) {
        case WexCPBuyButtonTipsTypePrivate: {
            contentLab.attributedText = [self configureTipsLabelText:kPrivateText highText:kPrivateHighText];
        }
            break;
        case WexCPBuyButtonTipsTypePublic: {
            contentLab.attributedText = [self configureTipsLabelText:kPublicText highText:nil];
        }
            break;
            
        default:
            break;
    }
//    NSMutableAttributedString *attribute = [[NSMutableAttributedString alloc] initWithString:text];
//    attribute.yy_lineSpacing = 10;
//    attribute.yy_color = WexDefault4ATitleColor;
//    attribute.yy_font = WexFont(14.0);
//    NSRange range = [text rangeOfString:highText];
//    __weak typeof(self) weakSelf     = self;
//    [attribute yy_setTextHighlightRange:range color:ColorWithHex(0x6766CC) backgroundColor:nil tapAction:^(UIView *  containerView, NSAttributedString *  text, NSRange range, CGRect rect) {
//        !weakSelf.WeXCPBuyBottomClick ? : weakSelf.WeXCPBuyBottomClick(WeXCPBuyBottomViewTypeLink);
//    }];
//    contentLab.attributedText = [self configureTipsLabelText:kPrivateText highText:kPrivateHighText];
    contentLab.numberOfLines = 0;
    [self addSubview:contentLab];
    self.contentLab = contentLab;
    
    WeXCustomButton *buyButton = [WeXCustomButton button];
    [buyButton setBackgroundImage:[WexCommonFunc imageWithColor:ColorWithHex(0x7B40FF)] forState:UIControlStateNormal];
    [buyButton setTitle:@"立即认购" forState:UIControlStateNormal];
    buyButton.layer.borderWidth = 0;
    [buyButton addTarget:self action:@selector(buyInEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:buyButton];
    self.buyInButton = buyButton;
}

- (NSMutableAttributedString *)configureTipsLabelText:(NSString *)tipsText
                                      highText:(NSString *)highText {
    NSMutableAttributedString *attribute = [[NSMutableAttributedString alloc] initWithString:tipsText];
    attribute.yy_lineSpacing = 10;
    attribute.yy_color = WexDefault4ATitleColor;
    attribute.yy_font = WexFont(14.0);
    if ([highText isVaildString]) {
        NSRange range = [tipsText rangeOfString:highText];
        __weak typeof(self) weakSelf     = self;
        [attribute yy_setTextHighlightRange:range color:ColorWithHex(0x6766CC) backgroundColor:nil tapAction:^(UIView *  containerView, NSAttributedString *  text, NSRange range, CGRect rect) {
            !weakSelf.WeXCPBuyBottomClick ? : weakSelf.WeXCPBuyBottomClick(WeXCPBuyBottomViewTypeLink);
        }];
    }
    return attribute;
}

- (void)buyInEvent:(WeXCustomButton *)button {
    !self.WeXCPBuyBottomClick ? : self.WeXCPBuyBottomClick(WeXCPBuyBottomViewTypeBuyIn);
}
- (void)wex_layoutConstraints {
    [self.buyInButton mas_updateConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
        make.height.mas_equalTo(40);
        make.bottom.mas_equalTo(-30);
    }];
    
    [self.contentLab mas_updateConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
        make.bottom.equalTo(self.buyInButton.mas_top).offset(-30);
    }];
    
    [self.tipsLab mas_updateConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.right.mas_equalTo(-10);
        make.bottom.equalTo(self.contentLab.mas_top).offset(-10);
    }];
}
- (void)setBuyInButtonType:(WexCPBuyButtonType)type
                     title:(NSString *)title {
    switch (type) {
        case WexCPBuyButtonTypeNormal: {
            [self.buyInButton setEnabled:true];
            [self.buyInButton setTitle:title forState:UIControlStateNormal];
        }
            break;
        case WexCPBuyButtonTypeDisable: {
            [self.buyInButton setEnabled:false];
            [self.buyInButton setTitle:title forState:UIControlStateDisabled];
        }
            break;
        default:
            break;
    }
}


@end
