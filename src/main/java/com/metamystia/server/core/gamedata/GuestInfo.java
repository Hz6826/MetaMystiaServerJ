package com.metamystia.server.core.gamedata;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MemoryPackable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestInfo
{
    public int id;
    public Integer visualId;
    public Integer id2;
    public Integer visualId2;
    public boolean isSpecial = false;

    public Vector3 OverrideSpawnPosition = null;
    public GuestGroupController.LeaveType LeaveType = GuestGroupController.LeaveType.MOVE;
}
