package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
@NoArgsConstructor
public class SelectAction extends AbstractNetAction{
    public ActionType type = ActionType.SELECT;

    public String mapLabel = "";
    public int mapLevel = 0;

    public SelectAction(String mapLabel, int mapLevel) {
        super();
        this.mapLabel = mapLabel;
        this.mapLevel = mapLevel;
    }

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
