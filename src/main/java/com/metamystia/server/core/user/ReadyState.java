package com.metamystia.server.core.user;

import com.metamystia.server.network.actions.ReadyAction;
import lombok.Data;

@Data
public class ReadyState {
    private boolean dayOver = false;
    private boolean prepOver = false;

    public boolean getReadyFor(ReadyAction.ReadyType readyType) {
        return switch (readyType) {
            case DayOver -> dayOver;
            case PrepOver -> prepOver;
        };
    }

    public void setReadyFor(ReadyAction.ReadyType readyType, boolean ready) {
        switch (readyType) {
            case DayOver -> dayOver = ready;
            case PrepOver -> prepOver = ready;
        }
    }
}
