package com.metamystia.server.core.gamedata;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;

@Data
@MemoryPackable(noHeader = true)
public class Vector3 {
    public float x;
    public float y;
    public float z;
}
