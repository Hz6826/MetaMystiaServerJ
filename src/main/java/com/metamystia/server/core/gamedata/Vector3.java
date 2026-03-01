package com.metamystia.server.core.gamedata;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@MemoryPackable(noHeader = true)
@NoArgsConstructor
@AllArgsConstructor
public class Vector3 {
    public float x;
    public float y;
    public float z;
}
