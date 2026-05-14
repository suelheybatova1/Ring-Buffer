/*
Name: Asgar Huseynli
ID: 17683
Course: Object Oriented Analysis & Design
CRN: 20964
Assignment 2: Ring Buffer
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// RingBuffer class containing Reader, Writer, ReadResult, Slot classes to separate responsibilities
public final class RingBuffer<T> {
    private final int capacity;
    private final List<Slot<T>> slots;

    // Next writing sequence
    private long writeSeq = 0;

    public RingBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("The capacity should be greater than 0!");
        this.capacity = capacity;

        slots = new ArrayList<>(capacity);

        // Initialize the buffer by default
        for (int i = 0; i < capacity; i++) {
            this.slots.add(null);
        }
    }

    // The method returns the total size of the ring buffer
    public int capacity() {
        return this.capacity;
    }

    // The method creates and returns a new writer
    public Writer<T> createWriter() {
        return new Writer<>(this);
    }

    // The method creates each reader in the oldest available sequence
    public Reader<T> createReader() {
        long oldest = oldestAvailableSequence(writeSeq);
        return new Reader<>(this, oldest);
    }

    // Writer class responsible for creating a writer
    public static final class Writer<T> {
        private final RingBuffer<T> buffer;

        public Writer(RingBuffer<T> buffer) {
            this.buffer = buffer;
        }

        public void write(T item) {
            // Prevents writing null values
            Objects.requireNonNull(item, "item");
            buffer.writeInternal(item);
        }
    }

    // Reader class responsible for creating readers
    public static final class Reader<T> {
        private final RingBuffer<T> buffer;
        private long nextSeq;

        public Reader(RingBuffer<T> buffer, long startSeq) {
            this.buffer = buffer;
            this.nextSeq = startSeq;
        }

        public ReadResult<T> read() {
            return this.buffer.readInternal(this);
        }

        public long position() {
            return this.nextSeq;
        }
    }

    // ReadResult class responsible for reading the item and getting the number of missed counts due to overrides
    public static final class ReadResult<T> {
        private final T item;
        private final long missedCount;

        public ReadResult(T item, long missedCount) {
            this.item = item;
            this.missedCount = missedCount;
        }

        // Returns the item read
        public T getItem() {
            return this.item;
        }

        // Returns the total number of missed counts
        public long getMissedCount() {
            return this.missedCount;
        }

        @Override
        public String toString() {
            return "ReadResult{item=" + item + ", missedCount=" + missedCount + "}";
        }
    }

    // Slot class responsible for recording internal storage
    private static final class Slot<T> {
        private final long seq;
        private final T value;

        Slot(long seq, T value) {
            this.seq = seq;
            this.value = value;
        }
    }

    // Method responsible for overwriting items based on the sequence number
    private void writeInternal(T item) {
        long seq = writeSeq;
        int index = indexOf(seq);

        // Overwrite the item in a specific index
        slots.set(index, new Slot<>(seq, item));

        // Update the writing sequence by 1 after writing
        writeSeq = seq + 1;
    }

    // Method responsible for reading the oldest items available based on the sequence number
    private ReadResult<T> readInternal(Reader<T> reader) {
        long missed = 0;

        // Execute all the time
        while (true) {
            long currentWrite = this.writeSeq;
            long oldest = oldestAvailableSequence(currentWrite);

            // If the reader sequence is behind the oldest available item, then there are missed counts
            if (reader.nextSeq < oldest) {
                missed += (oldest - reader.nextSeq);
                reader.nextSeq = oldest;
            }

            if (reader.nextSeq >= currentWrite) {
                return null;
            }

            long wanted = reader.nextSeq;
            int index = indexOf(wanted);
            Slot<T> slot = slots.get(index);

            // If slot null or mismatched, then keep retrying
            if (slot == null || slot.seq != wanted) {
                continue;
            }

            // In case of success, the particular reader sequence is advanced
            reader.nextSeq = wanted + 1;
            return new ReadResult<>(slot.value, missed);
        }
    }

    // Custom method for getting the index of each item (considering returning to the start after reaching the lat item)
    private int indexOf(long seq) {
        return (int) (seq % this.capacity);
    }

    // Used to return the oldest item existing in the ring buffer
    private long oldestAvailableSequence(long currentWriteSequence) {
        long oldest = currentWriteSequence - this.capacity;
        // Considering the case when the number of writes is less than the overall capacity
        return Math.max(0, oldest);
    }
}
