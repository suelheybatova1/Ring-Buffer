import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RingBufferTest {

    @Test
    void shouldReturnCorrectCapacity() {

        RingBuffer<String> buffer =
                new RingBuffer<>(3);

        assertEquals(3, buffer.capacity());
    }
}
