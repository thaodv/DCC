//
//  WeXCPCompoundCell.m
//  WeXBlockChain
//
//  Created by 张君君 on 2018/8/14.
//  Copyright © 2018年 WeX. All rights reserved.
//

#import "WeXCPCompoundCell.h"

@interface WeXCPCompoundCell () <UITextFieldDelegate>
@property (nonatomic, weak) UILabel *leftLab;
@property (nonatomic, weak) UIButton *rightButton;

@property (nonatomic, weak) UITextField *leftTextField;
@property (nonatomic, weak) UILabel *rightLab;

@end

@implementation WeXCPCompoundCell

- (void)wex_addSubViews {
    [super wex_addSubViews];
    UILabel *leftLab = CreateLeftAlignmentLabel(WexFont(16), WexDefault4ATitleColor);
    [self.contentView addSubview:leftLab];
    self.leftLab = leftLab;
    
    UIButton *rightButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [rightButton setTitleColor:ColorWithHex(0x6766CC) forState:UIControlStateNormal];
    [rightButton setContentHorizontalAlignment:UIControlContentHorizontalAlignmentRight];
    rightButton.titleLabel.font = WexFont(15.0);
    [rightButton addTarget:self action:@selector(buttonEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.contentView addSubview:rightButton];
    self.rightButton = rightButton;
    
    UITextField *textField = [UITextField new];
    [textField addTarget:self action:@selector(textFiledChanged:) forControlEvents:UIControlEventEditingChanged];
    textField.delegate = self;
    textField.keyboardType = UIKeyboardTypeNumberPad;
    textField.clearButtonMode = UITextFieldViewModeWhileEditing;
    textField.font = WexFont(16);
    textField.textColor = WexDefault4ATitleColor;
    [self.contentView addSubview:textField];
    self.leftTextField = textField;
    
    UILabel *rightLab = CreateRightAlignmentLabel(WexFont(16), ColorWithHex(0x404040));
    [self.contentView addSubview:rightLab];
    self.rightLab = rightLab;
}

- (void)buttonEvent:(UIButton *)sender {
    !self.DidClickAllButton ? : self.DidClickAllButton();
}
- (void)textFiledChanged:(UITextField *)textField {
    !self.DidInputText ? : self.DidInputText(textField.text);
}

- (void)wex_addConstraints {
    [super wex_addConstraints];
    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(55).priorityHigh();
        make.edges.mas_equalTo(0);
        make.width.equalTo(self.contentView.superview);
    }];
    
    [self.rightButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-10);
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(60, 30));
    }];
    
    [self.leftTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.centerY.mas_equalTo(0);
        make.right.equalTo(self.rightButton.mas_left).offset(-10);
    }];
    
    [self.leftLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(10);
        make.centerY.mas_equalTo(0);
        make.right.equalTo(self.rightButton.mas_left).offset(-10);
    }];
    
    [self.rightLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(-10);
        make.centerY.mas_equalTo(0);
        make.size.mas_equalTo(CGSizeMake(60, 30));
    }];
}
- (void)setLeftTitle:(NSString *)leftTitle
           rightText:(NSString *)rightText
         placeHolder:(NSString *)placeHolder
                type:(WeXCPCompoundType)cellType {
    [self.leftLab setText:leftTitle];
    [self.rightButton setTitle:rightText forState:UIControlStateNormal];
    [self.leftTextField setText:leftTitle];
    [self.leftTextField setPlaceholder:placeHolder];
    [self.rightLab setText:rightText];
    switch (cellType) {
        case WeXCPCompoundTypeTextFiledAndLabel: {
            [self.leftLab setHidden:true];
            [self.rightButton setHidden:true];
            [self.leftTextField setHidden:false];
            [self.rightLab setHidden:false];
        }
            break;
        case WeXCPCompoundTypeLabelAndButton: {
            [self.leftLab setHidden:false];
            [self.rightButton setHidden:false];
            [self.leftTextField setHidden:true];
            [self.rightLab setHidden:true];
        }
            break;
            
        default:
            break;
    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    !self.DidInputText ? : self.DidInputText(textField.text);
}
- (void)textFieldDidEndEditing:(UITextField *)textField {
    !self.DidInputText ? : self.DidInputText(textField.text);
}
- (void)setLeftTitle:(NSString *)leftTitle
    rightButtonImage:(NSString *)image {
    [self setLeftTitle:leftTitle rightText:nil placeHolder:nil type:WeXCPCompoundTypeLabelAndButton];
    if ([image length] > 0) {
        //Wex_Coin_Arrow
        [self.rightButton setHidden:false];
        [self.rightButton setImage:[UIImage imageNamed:image] forState:UIControlStateNormal];
    } else {
        [self.rightButton setHidden:true];
    }

}
- (void)setLeftTextFiled:(NSString *)text {
    [self.leftTextField setText:text];
}


- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

@end
