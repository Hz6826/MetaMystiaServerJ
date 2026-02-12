package com.metamystia.server.network.actions.storyaffect;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.network.actions.ActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class BuffAction extends AffectStoryAction{
    @Getter
    public enum QTEBuff {
        InstantEvaluation(0), // 立即完食
        PatientFreeze(1), // 耐心不减
        ThrowDeliver(2),   // 投掷上菜

        Fever(3),      // 热火朝天
        Fever_Infinite(-1);  // 永续热火朝天

        QTEBuff(int value) {
            this.value = value;
        }

        private final int value;
    }
    private final ActionType type = ActionType.BUFF;

    private QTEBuff buff;

    public BuffAction() {
        super();
    }

    public BuffAction(QTEBuff buff) {
        super();
        this.buff = buff;
    }
}
