package com.metamystia.server.core.gamedata;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;

@Data
@MemoryPackable
public class CookerSlot {
    public static final int slotsLength = 16; // [from c#] TODO: 根据实际情况调整最大值，重构 PrepSceneManager 相关代码

    public int id = -1;
    public long timestamp = 0;

    // clone method implemented as default

    public static CookerSlot[] createDefaultArray() {
        var slots = new CookerSlot[slotsLength];
        for (int i = 0; i < slotsLength; i++) {
            slots[i] = new CookerSlot();
        }
        return slots;
    }
}
