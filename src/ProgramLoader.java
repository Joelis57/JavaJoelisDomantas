import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ProgramLoader {

    private Word[] memory;
    private int maxSize;

    public boolean success;

    private void load(String fileName) {
        List<String> lines = new ArrayList<String>();
        File file = new File(fileName);
        if (!file.exists()) {
            success = false;
            return;
        } else {
            try {
                lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        int index = 0;
        boolean reading = false;
        boolean foundEnd = false;
        for (String line : lines){
            String[] t = line.split(" ");
            if (t[0].equals("DATA") && !reading){
                reading = true;
            }
            else if (t[0].equals("CODE") && reading){
                memory[0] = new Word(new byte[]{(byte)'J',(byte)'P', Word.convertInt((index/16)), Word.convertInt((index-(index/16)*16))});
                continue;
            }
            else if (t[0].equals("HALT") && reading){
                memory[index] = new Word("HALT");
                foundEnd = true;
                break;
            }
            else if (t[0].equals("DB") && reading){
                memory[index] = new Word(t[1]);
            }
            else if (t[0].equals("DW") && reading){
                memory[index] = new Word(Integer.parseInt(t[1]));
            }
            else if (reading){
                memory[index] = new Word(new byte[]{(byte)t[0].charAt(0),(byte)t[0].charAt(1),(byte)t[0].charAt(2),(byte)t[0].charAt(3)});
            }

            if (reading) index++;
        }
        if (!foundEnd || !lines.get(lines.size() - 1).equals("HALT")) {
            success = false;
            return;
        }
        success = true;
    }

    public Word[] getMemory(){
        return memory;
    }

    public void print(){
        for (int i = 0; i < maxSize; i++){
            if (memory[i]!=null)
                memory[i].print();
            else
                new Word(0).print();
        }
    }

    public ProgramLoader(String fileName){
        maxSize = 16*16;
        memory = new Word[maxSize];
        load(fileName);
    }
}
