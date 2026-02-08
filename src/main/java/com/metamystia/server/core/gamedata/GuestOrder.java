package com.metamystia.server.core.gamedata;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;

@MemoryPackable
@Data
public class GuestOrder {
    public int requestFoodIdOrTag;
    public int requestBevIdOrTag;
    public int deskCode;
    public boolean notShowInUI;
    public boolean isFree;
}
