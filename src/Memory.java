public interface Memory {
    Word read(int index);
    void write(int index, int info);
    void write(int index, Word info);
}
