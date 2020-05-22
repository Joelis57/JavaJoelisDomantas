public class Word {
    static private final int SIZE = 4;

    private byte[] data;

    public boolean isEqual(Word word){
        int countEqual = 0;
        for (int i = 0; i < SIZE; i++) {
            if (this.data[i] == word.getByte(i)){
                countEqual++;
            }
        }
        return countEqual == SIZE;
    }

    public byte getByte(int i){
        return data[i];
    }

    public String getString(){
        return String.format("%c%c%c%c", (char)data[0], (char)data[1], (char)data[2], (char)data[3]);
    }

    public void setByte(int i, byte b){
        this.data[i] = b;
    }

    public static int convertChar(byte t){
        if(t-65 >=0){
            return 10+(t-65);
        }
        return t-48;
    }

    public static byte convertInt(int t){
        if(t>=10){
            return (byte)(65+t%10);
        }
        return (byte)(48+t);
    }

    public void setInt(int value){
        data = new byte[SIZE];
        data[0] = convertInt((int)(value/Math.pow(16,3)));
        value -= (int)(convertChar(data[0])*Math.pow(16,3));
        data[1] = convertInt((int)(value/Math.pow(16,2)));
        value -= (int)(convertChar(data[1])*Math.pow(16,2));
        data[2] = convertInt((int)(value/Math.pow(16,1)));
        value -= (int)(convertChar(data[2])*Math.pow(16,1));
        data[3] = convertInt(value);
    }

    public void setString(String value){
        data = new byte[SIZE];
        data[0] = (byte)value.charAt(0);
        data[1] = (byte)value.charAt(1);
        data[2] = (byte)value.charAt(2);
        data[3] = (byte)value.charAt(3);
    }

    public void setBytes(byte[] value){
        data = new byte[]{value[0],value[1],value[2],value[3]};
    }

    public static int toInt(Word word){
        return (int)(convertChar(word.data[0])*Math.pow(16,3)+convertChar(word.data[1])*Math.pow(16,2)+convertChar(word.data[2])*Math.pow(16,1)+convertChar(word.data[3]));
    }

    public static String toString(Word word){
        return new String(new char[]{(char)word.getByte(0),(char)word.getByte(1),(char)word.getByte(2),(char)word.getByte(3)});
    }

    public static Word toWord(int value){
        Word word = new Word(value);
        return word;
    }

    public void print(){
        System.out.println(String.format("%2X%2X%2X%2X ", data[0], data[1], data[2], data[3]));
    }

    public void printC(){
        System.out.print(getString());
    }

    public Word(){
        data = new byte[SIZE];
    }

    public Word(Word word){
        this.data = word.data;
    }

    public Word(int value){
        setInt(value);
    }

    public Word(String value){
        data = new byte[SIZE];
        for (int i = 0; i < ((value.length() > 3) ? 4: value.length()); i++)
            data[i] = (byte)value.charAt(i);
    }

    public Word(byte[] value){
        setBytes(value);
    }

}
