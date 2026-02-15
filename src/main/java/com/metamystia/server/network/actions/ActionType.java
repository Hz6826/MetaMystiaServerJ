package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackEnumActualType;
import com.hz6826.memorypack.util.UnsignedShort;
import com.metamystia.server.network.actions.storyaffect.*;
import com.metamystia.server.network.actions.storyaffect.guest.*;
import lombok.Getter;

@Getter
@MemoryPackEnumActualType(UnsignedShort.class)
public enum ActionType {
    PING(PingAction.class),
    PONG(PongAction.class),
    HELLO(HelloAction.class),
    SCENE_TRANSIT(SceneTransitAction.class),
    SYNC(SyncAction.class),
    READY(ReadyAction.class),
    MESSAGE(MessageAction.class),
    SELECT(SelectAction.class),
    MAP_DECIDED(MapDecidedAction.class),
    PREP(PrepAction.class),
    NIGHTSYNC(NightSyncAction.class),
    COOK(CookAction.class),
    EXTRACT(ExtractAction.class),
    QTE(QTEAction.class),
    STORE_FOOD(StoreFoodAction.class),        // 这是往保温箱中存储，仅可以存储 food
    STORE_SELLABLE(StoreSellableAction.class),    // 这是往空位存储，可以存储 sellable（food / beverage）
    EXTRACT_FOOD(ExtractAction.class),
    GUEST_INVITE(GuestInviteAction.class),
    GUEST_SPAWN(GuestSpawnAction.class),
    GUEST_SEATED(GuestSeatedAction.class),
    GUEST_GEN_NORMAL_ORDER(GuestGenNormalOrderAction.class),
    GUEST_GEN_SPECIAL_ORDER(GuestGenSPOrderAction.class),
    GUEST_SERVE(GuestServeAction.class),
    GUEST_PAY(GuestPayAction.class),
    GUEST_LEAVE(GuestLeaveAction.class),
    BUFF(BuffAction.class),
    IZAKAYA_CLOSE(IzakayaCloseAction.class),
    GET_COLLECTABLE(GetCollectableAction.class),

    CHANGE_HOST_ROLE(ChangeHostRoleAction.class);

    ActionType(Class<?> relatedActionClass) {
        this.relatedActionClass = relatedActionClass;
    }

    private final Class<?> relatedActionClass;

}
