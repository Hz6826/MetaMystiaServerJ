import com.hz6826.memorypack.serializer.MemoryPackSerializer;
import com.hz6826.memorypack.serializer.SerializerRegistry;
import com.metamystia.server.core.gamedata.Scene;
import com.metamystia.server.network.actions.HelloAction;
import com.metamystia.server.network.actions.generated.HelloActionMemoryPackSerializer;
import com.metamystia.server.util.DebugUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.io.InputStream;
import java.util.Set;

@Testable
public class MemoryPackTest {
    // @Test
    public void test() {
        SerializerRegistry.getInstance().register(HelloAction.class, new HelloActionMemoryPackSerializer());

        HelloAction helloAction = new HelloAction();
        helloAction.setCurrentGameScene(Scene.DayScene);
        helloAction.setVersion("1.0.0");
        helloAction.setPeerDLCCookers(Set.of(1, 2, 3));
        System.out.println(helloAction);

        MemoryPackSerializer<HelloAction> serializer = SerializerRegistry.getInstance().getSerializer(HelloAction.class);
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        serializer.serialize(helloAction, buffer);
        System.out.println(buffer);
        HelloAction deserializedHelloAction = serializer.deserialize(buffer);
        System.out.println(deserializedHelloAction);
        Assertions.assertEquals(helloAction, deserializedHelloAction);
    }

    @Test
    public void test2() {
        SerializerRegistry.getInstance().register(HelloAction.class, new HelloActionMemoryPackSerializer());
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        try (InputStream in = HelloAction.class.getClassLoader().getResourceAsStream("test.dat")){
            Assertions.assertNotNull(in);
            buffer.writeBytes(in.readAllBytes());
            buffer.skipBytes(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HelloAction deserializedHelloAction = SerializerRegistry.getInstance().getSerializer(HelloAction.class).deserialize(buffer);
        System.out.println(deserializedHelloAction);
    }
}
