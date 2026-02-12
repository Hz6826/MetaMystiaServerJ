package com.metamystia.server.network.actions.storyaffect;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.network.actions.ActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class QTEAction extends AffectStoryAction{
    private ActionType type = ActionType.QTE;

    private int gridIndex;
    private float QTEScore;

    public QTEAction() {
        super();
    }

    public QTEAction(int gridIndex, float QTEScore) {
        super();
        this.gridIndex = gridIndex;
        this.QTEScore = QTEScore;
    }
}
