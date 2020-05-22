import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;

import static java.util.Map.entry;

public class CPU {

    class InputDevice{
        BufferedReader reader;

        InputDevice() {
             reader = new BufferedReader(new InputStreamReader(System.in));
        }

        String getInputStr(){
            try {
                return reader.readLine();
            } catch (IOException e) {
                return "";
            }
        }

        int getInputInt(){
            try{
                String line = reader.readLine();
                return Integer.parseInt(line);
            }catch(IOException e){
                return -1;
            }
        }
    }

    class OutputDevice{
        void print(int value){
            System.out.println(value);
        }

        void print(String text){
            System.out.println(text);
        }
    }

    public RMmemory memory;
    public InputDevice input;
    public OutputDevice output;
    //Registers
    private int MODE; //user - 0; supervisor - 1
    private boolean C;
    private int IC;
    private Word PLR;
    private int R, D;
    private int TI;
    private int PI;
    private int SI;
    private int CHN;
    private boolean debug;

    public final int TIME = 1000;

    public void resetTimer(){
        TI = TIME;
    }

    public void resetInterruptions(){
        TI = TIME;
        PI = 0;
        SI = 0;
    }

    public void resetVM(){
        IC = R = D = PI = SI = CHN = 0;
        C = false;
        TI = TIME;
    }

    public void printReg(){
        System.out.println("Real machine Registers:");
        System.out.println(String.format("\tMODE: %d", MODE));
        System.out.println(String.format("\tC: %d", C ? 1 : 0));
        System.out.println(String.format("\tIC: %d", IC));

        System.out.print("\tPLR: ");
        PLR.printC();
        System.out.println();

        System.out.println(String.format("\tR: %d", R));
        System.out.println(String.format("\tD: %d", D));
        System.out.println(String.format("\tTI: %d",TI));
        System.out.println(String.format("\tPI: %d",PI));
        System.out.println(String.format("\tSI: %d",SI));
        System.out.println(String.format("\tCHN: %d",CHN));
    }

    //GET&SET
    public int getMODE(){return MODE;}
    public void setMODE(int MODE){this.MODE = MODE;}

    public boolean getC(){return C;}
    public void setC(boolean C){this.C = C;}

    public int getIC(){return IC;}
    public void setIC(int IC){this.IC = IC;}

    public Word getPLR(){return PLR;}
    public void setPLR(Word PLR){this.PLR = PLR;}

    public int getR(){return R;}
    public void setR(int r){this.R = r;}

    public int getD(){return D;}
    public void setD(int d){this.D = d;}

    public int getTI(){return TI;}
    public void setTI(int TI){this.TI = TI;}

    public int getPI(){return PI;}
    public void setPI(int PI){this.PI = PI;}

    public int getSI(){return SI;}
    public void setSI(int SI){this.SI = SI;}

    public int getCHN(){return CHN;}
    public void setCHN(int CHN){this.CHN = CHN;}

    public void setDebug(boolean value){this.debug = value;}
    public boolean getDebug(){return debug;}

    //HLP

    private static Map<String, Commands> commandsDictionary = Map.ofEntries(
            entry("HALT", Commands.HALT),
            entry("DA__", Commands.DA),
            entry("SB__", Commands.SB),
            entry("AD", Commands.ADxy),
            entry("BS", Commands.BSxy),
            entry("CR__", Commands.CR),
            entry("CD__", Commands.CD),
            entry("JP", Commands.JPxy),
            entry("JC", Commands.JCxy),
            entry("LD", Commands.LDxy),
            entry("LR", Commands.LRxy),
            entry("SD", Commands.SDxy),
            entry("SR", Commands.SRxy),
            entry("GD", Commands.GDxy),
            entry("PD", Commands.PDxy),
            entry("PI", Commands.PIxy),
            entry("SI", Commands.SIxy)
    );

    private static Commands interpret(Word word){
        String s = Word.toString(word);
        if (commandsDictionary.containsKey(s)){
            return commandsDictionary.get(s);
        }
        else if (commandsDictionary.containsKey(s.substring(0,2))){
            return commandsDictionary.get(s.substring(0,2));
        }
        else{
            System.out.println("Command \""+s+"\" not implemented!");
        }
        return null;
    }

    public void writeString(String text){
        int countS = 0;
        int countB = 0;
        Word[] tarray = new Word[16];
        if (text.length() > 4*15){
            PI = 3;
        }
        else{
            for (int i = 1; i < 16; i+=1){
                countS++;
                tarray[i] = new Word();
                for (int o = 0; o < 4; o++){
                    if (countB < text.length()){
                        tarray[i].setByte(o,(byte)text.charAt(countB));
                        countB++;
                    }
                    else{
                        tarray[i].setByte(o,(byte)'$');
                    }
                }
                if (countB >= text.length())
                    break;
            }
            tarray[0] = new Word(countS);
            for (int i = 0; i <= countS; i++)
                memory.write((Word.convertChar(memory.read(IC-1).getByte(2)))*16 + (Word.convertChar(memory.read(IC-1).getByte(3)))+i, tarray[i]);
        }
    }

    public boolean update(VM.VirtualMachine vm){
        //TI PI SI
        boolean running = true;
        boolean g = false;
        MODE = 1;
        if (SI > 0){
            g = true;
            if (debug){
                IC--;
                vm.print();
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                for (int i = 0; i < 50; ++i) System.out.println(); // clear
                IC++;
            }
            if (SI == 1){//GDxyVeikia
                int countS = 0;
                int countB = 0;
                Word[] tarray = new Word[16];
                String text = input.getInputStr();
                if (text.length() > 4*15){
                    PI = 3;
                }
                else{
                    for (int i = 1; i < 16; i+=1){
                        countS++;
                        tarray[i] = new Word();
                        for (int o = 0; o < 4; o++){
                            if (countB < text.length()){
                                tarray[i].setByte(o,(byte)text.charAt(countB));
                                countB++;
                            }
                            else{
                                tarray[i].setByte(o,(byte)'$');
                            }
                        }
                        if (countB >= text.length())
                            break;
                    }
                    tarray[0] = new Word(countS);
                    for (int i = 0; i <= countS; i++)
                        memory.write((Word.convertChar(memory.read(IC-1).getByte(2)))*16 + (Word.convertChar(memory.read(IC-1).getByte(3)))+i, tarray[i]);
                }
            }
            else if (SI == 2){//PDxy VEIKIA
                int size = Word.toInt(memory.read( (Word.convertChar(memory.read(IC-1).getByte(2)))*16 + (Word.convertChar(memory.read(IC-1).getByte(3))) ));
                String ttext = "";
                for (int i = 1; i <= size; i++){
                    ttext += Word.toString(memory.read( ((Word.convertChar(memory.read(IC-1).getByte(2)))*16 + (Word.convertChar(memory.read(IC-1).getByte(3))))+i ));
                }
                output.print(ttext.replace("$",""));
            }
            else if (SI == 3){//HALT
                running = false;
            }
            else if (SI == 6){//SIxy
                memory.write( (Word.convertChar(memory.read(IC-1).getByte(2)))*16 + (Word.convertChar(memory.read(IC-1).getByte(3))) , input.getInputInt());
            }
            else if (SI == 7){//PIxy
                output.print(Word.toInt(memory.read( ((Word.convertChar(memory.read(IC-1).getByte(2)))*16 + (Word.convertChar(memory.read(IC-1).getByte(3)))))));
            }
            SI = 0;
        }
        if (PI > 0){
            g = true;
            if (PI == 1){
                System.out.println("ERROR! Wrong adress!");
            }
            else if (PI == 2){
                System.out.println("ERROR! Wrong operation code!");
            }
            else if (PI == 3){
                System.out.println("ERROR! Wrong assignment!");
            }
            else if (PI == 4){
                System.out.println("ERROR! Overflow!");
            }
            else{
                System.out.println("ERROR! Undefined programming interruption!");
            }
            running = false;
        }
        if (TI == 0){
            g = true;
            System.out.println("ERROR! Timer interruption!");
            running = false;
        }
        MODE = 0;
        if (g){
            if (debug){
                IC--;
                vm.print();
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                for (int i = 0; i < 50; ++i) System.out.println(); // clear
                IC++;
            }
        }
        return running;
    }

    public void execute(Word word){
        switch(interpret(word)){
            // STABDYMAS
            case HALT:
                SI = 3;
                break;
            // ARITMETINES
            case DA:
                R = R + D;
                if (R >= 16*16*16*16 || R < 0)
                    PI = 4;
                break;
            case SB:
                R = R - D;
                if (R >= 16*16*16*16 || R < 0)
                    PI = 4;
                break;
            case ADxy:
                R = R + Word.toInt(memory.read( (Word.convertChar(word.getByte(2)))*16 + (Word.convertChar(word.getByte(3))) ));
                if (R >= 16*16*16*16 || R < 0)
                    PI = 4;
                break;
            case BSxy:
                R = R - Word.toInt(memory.read( (Word.convertChar(word.getByte(2)))*16 + (Word.convertChar(word.getByte(3))) ));
                if (R >= 16*16*16*16 || R < 0)
                    PI = 4;
                break;
            // PALYGINIMO
            case CR:
                C = R > D;
                break;
            case CD:
                C = (R == D);
                break;
            // VALDYMO PERDAVIMO
            case JPxy:
                IC = (Word.convertChar(word.getByte(2)))*16 + (Word.convertChar(word.getByte(3)))-1;
                break;
            case JCxy:
                IC = C?(Word.convertChar(word.getByte(2)))*16 + (Word.convertChar(word.getByte(3)))-1:IC;
                break;
            // DARBAS SU ATMINTIMI
            case LRxy:
                R = Word.toInt(memory.read( (Word.convertChar(word.getByte(2)))*16 + (Word.convertChar(word.getByte(3))) ));
                break;
            case LDxy:
                D = Word.toInt(memory.read( (Word.convertChar(word.getByte(2)))*16 + (Word.convertChar(word.getByte(3))) ));
                break;
            case SRxy:
                memory.write( (Word.convertChar(word.getByte(2)))*16 + (Word.convertChar(word.getByte(3))) , R);
                break;
            case SDxy:
                memory.write( (Word.convertChar(word.getByte(2)))*16 + (Word.convertChar(word.getByte(3))) , D);
                break;
            // IVEDIMAS IR ISVEDIMAS
            case GDxy:
                SI = 1;
                break;
            case PDxy:
                SI = 2;
                break;
            case SIxy:
                SI = 6;
                break;
            case PIxy:
                SI = 7;
                break;
            default:
                System.out.println("Not implemented!"+IC);
                PI = 1;
                word.print();
                break;
        }
        IC++;
        TI--;
    }

    public RMmemory getMemory(){
        return memory;
    }

    public CPU(){
        MODE = 1;
        IC = R = D = PI = SI = CHN = 0;
        PLR = new Word(new byte[]{0,0,0,0});
        C = false;
        TI = TIME;
        memory = new RMmemory(16*16, this);
        debug = false;
        input = new InputDevice();
        output = new OutputDevice();
    }
}
