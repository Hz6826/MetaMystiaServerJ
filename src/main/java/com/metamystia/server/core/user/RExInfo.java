package com.metamystia.server.core.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class RExInfo {
    private Set<Integer> rExRecipes;
    private Set<Integer> rExFoods;
    private Set<Integer> rExBeverages;
    private Set<Integer> rExSpecialGuests;
}
