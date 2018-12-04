//
//  WexCashLoanRealInfoFormModel.m
//  NealTestDemo
//
//  Created by Kikpop on 2018/10/18.
//  Copyright © 2018 weihuahu. All rights reserved.
//

#import "WexCashLoanRealInfoFormModel.h"

@implementation WexCashLoanRealInfoFormModel

- (void)encodeWithCoder:(NSCoder *)aCoder{
    [aCoder encodeInteger:self.marriageStatusIndex forKey:WeXMarriageStatusIndex];
    [aCoder encodeObject:self.marriageStatus forKey:WexMarriageStatus];

    [aCoder encodeInteger:self.homeProviceIndex forKey:WeXHomeProviceIndex];
    [aCoder encodeInteger:self.homeCityIndex forKey:WeXHomeCityIndex];
    [aCoder encodeInteger:self.homeDistrictIndex forKey:WeXHomeDistrictIndex];
    [aCoder encodeObject:self.homeProvice forKey:WeXHomeProvice];
    [aCoder encodeObject:self.homeCity forKey:WeXHomeCity];
    [aCoder encodeObject:self.homeDistrict forKey:WeXHomeDistrict];
    [aCoder encodeObject:self.homeLoacation forKey:WeXHomeLoacation];

    [aCoder encodeInteger:self.loanUseIndex forKey:WeXLoanUseIndex];
    [aCoder encodeObject:self.loanUse forKey:WeXLoanUse];

    [aCoder encodeInteger:self.workTypeIndex forKey:WeXWorkTypeIndex];
    [aCoder encodeInteger:self.workIndustryIndex forKey:WeXWorkIndustryIndex];
    [aCoder encodeObject:self.workType forKey:WeXWorkType];
    [aCoder encodeObject:self.workIndustry forKey:WeXWorkIndustry];
    [aCoder encodeObject:self.workAge forKey:WeXWorkAge];

    [aCoder encodeInteger:self.workProviceIndex forKey:WeXWorkProviceIndex];
    [aCoder encodeInteger:self.workCityIndex forKey:WeXWorkCityIndex];
    [aCoder encodeInteger:self.workDistrictIndex forKey:WeXWorkDistrictIndex];
    [aCoder encodeObject:self.workProvice forKey:WeXWorkProvice];
    [aCoder encodeObject:self.workCity forKey:WeXWorkCity];
    [aCoder encodeObject:self.workDistrict forKey:WeXWorkDistrict];
    [aCoder encodeObject:self.workLocation forKey:WeXWorkLocation];
    [aCoder encodeObject:self.workName forKey:WeXWorkName];
    [aCoder encodeObject:self.workPhoneNumber forKey:WeXWorkPhoneNumber];

    [aCoder encodeInteger:self.linkmanOneIndex forKey:WeXLinkmanOneIndex];
    [aCoder encodeInteger:self.linkmanTwoIndex forKey:WeXLinkmanTwoIndex];
    [aCoder encodeInteger:self.linkmanThreeIndex forKey:WeXLinkmanThreeIndex];
    [aCoder encodeObject:self.linkmanOne forKey:WeXLinkmanOne];
    [aCoder encodeObject:self.linkmanTwo forKey:WeXLinkmanTwo];
    [aCoder encodeObject:self.linkmanThree forKey:WeXLinkmanThree];
    [aCoder encodeObject:self.linkmanOnePhone forKey:WeXLinkmanOnePhone];
    [aCoder encodeObject:self.linkmanOneName forKey:WeXLinkmanOneName];
    [aCoder encodeObject:self.linkmanTwoPhone forKey:WeXLinkmanTwoPhone];
    [aCoder encodeObject:self.linkmanTwoName forKey:WeXLinkmanTwoName];
    [aCoder encodeObject:self.linkmanThreePhone forKey:WeXLinkmanThreePhone];
    [aCoder encodeObject:self.linkmanThreeName forKey:WeXLinkmanThreeName];
}

- (nullable instancetype)initWithCoder:(NSCoder *)aDecoder{
    self = [super init];
    if (self) {
        self.marriageStatusIndex = [aDecoder decodeIntegerForKey:WeXMarriageStatusIndex];
        self.marriageStatus      = [aDecoder decodeObjectForKey:WexMarriageStatus];
        
        self.homeProviceIndex    = [aDecoder decodeIntegerForKey:WeXHomeProviceIndex];
        self.homeCityIndex       = [aDecoder decodeIntegerForKey:WeXHomeCityIndex];
        self.homeDistrictIndex   = [aDecoder decodeIntegerForKey:WeXHomeDistrictIndex];
        self.homeProvice         = [aDecoder decodeObjectForKey:WeXHomeProvice];
        self.homeCity            = [aDecoder decodeObjectForKey:WeXHomeCity];
        self.homeDistrict        = [aDecoder decodeObjectForKey:WeXHomeDistrict];
        self.homeLoacation       = [aDecoder decodeObjectForKey:WeXHomeLoacation];
        
        self.loanUseIndex        = [aDecoder decodeIntegerForKey:WeXLoanUseIndex];
        self.loanUse             = [aDecoder decodeObjectForKey:WeXLoanUse];
        
        self.workTypeIndex       = [aDecoder decodeIntegerForKey:WeXWorkTypeIndex];
        self.workIndustryIndex   = [aDecoder decodeIntegerForKey:WeXWorkIndustryIndex];
        self.workType            = [aDecoder decodeObjectForKey:WeXWorkType];
        self.workIndustry        = [aDecoder decodeObjectForKey:WeXWorkIndustry];
        self.workAge             = [aDecoder decodeObjectForKey:WeXWorkAge];
        
        self.workProviceIndex    = [aDecoder decodeIntegerForKey:WeXWorkProviceIndex];
        self.workCityIndex       = [aDecoder decodeIntegerForKey:WeXWorkCityIndex];
        self.workDistrictIndex   = [aDecoder decodeIntegerForKey:WeXWorkDistrictIndex];
        self.workProvice         = [aDecoder decodeObjectForKey:WeXWorkProvice];
        self.workCity            = [aDecoder decodeObjectForKey:WeXWorkCity];
        self.workDistrict        = [aDecoder decodeObjectForKey:WeXWorkDistrict];
        self.workLocation        = [aDecoder decodeObjectForKey:WeXWorkLocation];
        self.workName            = [aDecoder decodeObjectForKey:WeXWorkName];
        self.workPhoneNumber     = [aDecoder decodeObjectForKey:WeXWorkPhoneNumber];
        
        self.linkmanOneIndex     = [aDecoder decodeIntegerForKey:WeXLinkmanOneIndex];
        self.linkmanTwoIndex     = [aDecoder decodeIntegerForKey:WeXLinkmanTwoIndex];
        self.linkmanThreeIndex   = [aDecoder decodeIntegerForKey:WeXLinkmanThreeIndex];
        self.linkmanOne          = [aDecoder decodeObjectForKey:WeXLinkmanOne];
        self.linkmanTwo          = [aDecoder decodeObjectForKey:WeXLinkmanTwo];
        self.linkmanThree        = [aDecoder decodeObjectForKey:WeXLinkmanThree];
        self.linkmanOnePhone     = [aDecoder decodeObjectForKey:WeXLinkmanOnePhone];
        self.linkmanOneName      = [aDecoder decodeObjectForKey:WeXLinkmanOneName];
        self.linkmanTwoPhone     = [aDecoder decodeObjectForKey:WeXLinkmanTwoPhone];
        self.linkmanTwoName      = [aDecoder decodeObjectForKey:WeXLinkmanTwoName];
        self.linkmanThreePhone   = [aDecoder decodeObjectForKey:WeXLinkmanThreePhone];
        self.linkmanThreeName    = [aDecoder decodeObjectForKey:WeXLinkmanThreeName];
    }
    return self;
}

@end
