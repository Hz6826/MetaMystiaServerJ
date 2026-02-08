package com.metamystia.server.core.gamedata;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;

// [from C#] optimize & TODO: 实现整个 GameData.Core.Collections.Sellable 即使之包含 Beverage 而不仅仅是 Food
@Data
@MemoryPackable
public class SellableFood {
    public int foodId;
    public int level;
    public int[] modifierIds;
    public int[] additiveTags;
    public int cookId;

    // TODO
}
