public class Main {
    public static void main(String[] args) {
        // Creating the instance of the RingBuffer class
        RingBuffer<String> buffer = new RingBuffer<>(3);

        // Creating the writer instance
        RingBuffer.Writer<String> writer = buffer.createWriter();

        // Creating multiple reader instances
        RingBuffer.Reader<String> r1 = buffer.createReader();
        RingBuffer.Reader<String> r2 = buffer.createReader();

        // Writing values of type String to tee buffer
        writer.write("A");
        writer.write("B");
        writer.write("C");

        // Reading the written values
        System.out.println("r1 -> " + r1.read());
        System.out.println("r2 -> " + r2.read());

        // Overwriting the oldest values
        writer.write("D");
        writer.write("E");

        // r1 is faster
        System.out.println("r1 -> " + r1.read()); // B or missed jump depending on timing
        System.out.println("r1 -> " + r1.read());

        // r2 is slower; may miss items
        RingBuffer.ReadResult<String> res;
        while ((res = r2.read()) != null) {
            System.out.println("r2 -> " + res);
        }
    }
}
