package com.metamystia.server.core.gamedata;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@MemoryPackable
public class Table {
    public Map<Integer, Long> RecipeAdditions = new HashMap<>();
    public Map<Integer, Long> RecipeDeletions = new HashMap<>();

    public Map<Integer, Long> BeverageAdditions = new HashMap<>();
    public Map<Integer, Long> BeverageDeletions = new HashMap<>();

    public CookerSlot[] Cookers  = CookerSlot.createDefaultArray();
}
