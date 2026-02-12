package com.metamystia.server.core.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class DLCInfo {
    private List<String> activeDLCLabel;
    @ToString.Exclude private Set<Integer> DLCRecipes;
    @ToString.Exclude private Set<Integer> DLCCookers;
    @ToString.Exclude private Set<Integer> DLCFoods;
    @ToString.Exclude private Set<Integer> DLCBeverages;
    @ToString.Exclude private Set<Integer> DLCNormalGuests;
    @ToString.Exclude private Set<Integer> DLCSpecialGuests;
}
