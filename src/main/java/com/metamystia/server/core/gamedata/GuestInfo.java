package com.metamystia.server.core.gamedata;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;

@MemoryPackable
@Data
public class GuestInfo
{
    public int id;
    public int visualId;
    public int id2;
    public int visualId2;
    public boolean isSpecial = false;


    public Vector3 OverrideSpawnPosition  = null;
    public GuestGroupController.LeaveType LeaveType = GuestGroupController.LeaveType.MOVE;
}
