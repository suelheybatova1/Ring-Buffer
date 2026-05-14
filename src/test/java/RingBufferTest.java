import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RingBufferTest {

    @Test
    void shouldReturnCorrectCapacity() {
        RingBuffer<String> buffer =
                new RingBuffer<>(3);
        assertEquals(3, buffer.capacity());
    }
    @Test
    void testInvalidCapacity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RingBuffer<String>(0)
        );
    }
    @Test
    void testEmptyBuffer() {
        RingBuffer<String> buffer =
                new RingBuffer<>(3);
        RingBuffer.Reader<String> reader =
                buffer.createReader();
        assertNull(reader.read());
    }

    public RingBufferTest() {
        super();
    }

    @Test
    void testWriteAndRead() {
        RingBuffer<String> buffer =
                new RingBuffer<>(3);
        RingBuffer.Writer<String> writer =
                buffer.createWriter();
        RingBuffer.Reader<String> reader =
                buffer.createReader();
        writer.write("A");
        RingBuffer.ReadResult<String> result =
                reader.read();
        assertNotNull(result);
        assertEquals("A", result.getItem());
    }
}
