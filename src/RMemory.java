import java.util.Random;

class RMmemory implements Memory {
    public int maxBlockSize;
    private Word[] memory;
    public CPU cpu;

    public boolean isFreeBlock(int index){
        for (int i = 0; i < 16; i++)
            if (memory[index*16+i] != null){
                return false;
            }
        return true;
    }

    public void write(int index, int info){
        memory[ Word.toInt(memory[ (Word.convertChar(cpu.getPLR().getByte(2)))*16*16+(Word.convertChar(cpu.getPLR().getByte(3)))*16+index/16 ])*16 +(index%16) ] = new Word(info);
    }

    public void write(int index, Word info){
        memory[ Word.toInt(memory[ ( Word.convertChar(cpu.getPLR().getByte(2)) )*16*16+( Word.convertChar(cpu.getPLR().getByte(3)) )*16+index/16 ])*16 +(index%16) ] = new Word(info);
    }

    public void write(int program, int index, Word info){
        if (info != null)
            memory[Word.toInt(memory[program*16+index/16 ])*16 +(index%16)] = new Word(info);
    }

    public Word read(int index){
        return memory[ Word.toInt(memory[ (Word.convertChar(cpu.getPLR().getByte(2)))*16*16+(Word.convertChar(cpu.getPLR().getByte(3)))*16+index/16 ])*16 +(index%16) ];
    }

    public String readString(int index){
        int size = Word.toInt(read( (Word.convertChar(read(cpu.getIC()-1).getByte(2)))*16 + (Word.convertChar(read(cpu.getIC()-1).getByte(3))) ));
        String ttext = "";
        for (int i = 1; i <= size; i++){
            ttext += Word.toString(read( ((Word.convertChar(read(cpu.getIC()-1).getByte(2)))*16 + (Word.convertChar(read(cpu.getIC()-1).getByte(3))))+i ));
        }
        return ttext.replace("$","");
    }

    public void allocateBlock(int index){
        for (int i = 0; i < 16; i++)
            memory[index*16+i] = new Word();
    }

    public void allocateMemoryVM(int pagingIndex){
        for (int i = 0; i < 16; i++){//paging cells
            boolean found = false;
            Random rnd = new Random();
            for (int j = 0; j < 16*16*2; j++){
                int t = rnd.nextInt(16*16);
                if (isFreeBlock(t)){
                    allocateBlock(t);
                    memory[pagingIndex*16+i] = new Word(t);
                    found = true;
                    break;
                }
            }
            if (!found){
                System.out.println("OUT OF MEMORY ERROR");
                break;
            }
        }
    }

    public void freeBlock(int index){
        for (int i = 0; i < 16; i++)
            memory[index*16+i] = null;
    }

    public int newPaging(){
        int pagingIndex = -1;
        for (int i = 0; i < 16; i++){
            if (isFreeBlock(i)){
                pagingIndex = i;
                allocateMemoryVM(pagingIndex);
                break;
            }
        }
        return pagingIndex;
    }

    public int newPaging(Word[] program){
        int pagingIndex = -1;
        Random rnd = new Random();
        for (int i = 0; i < 5; i++){
            int t = rnd.nextInt(16*16/2);
            if (isFreeBlock(t)){
                allocateBlock(t);
                pagingIndex = t;
                allocateMemoryVM(pagingIndex);
                break;
            }
        }
        if (pagingIndex >= 0){
            cpu.setPLR(new Word(new byte[]{(byte)'0',(byte)'0',Word.convertInt(pagingIndex/16),Word.convertInt(pagingIndex%16)}));
            for (int i = 0; i < 16*16; i++){
                if (program[i] != null)
                    write(pagingIndex,i,program[i]);
            }
        }
        else{
            System.out.println("OUT OF MEMORY ERROR");
        }
        return pagingIndex;
    }

    public void printPaging(){
        for (int i = 0; i < 16*16; i++){
            System.out.print(String.format("%5d", i));
            if (memory[i] != null){
                memory[i].print();
            }
            else {
                System.out.print(" 0  0  0  0 | ");
            }
            if ((i+1)%10 == 0){
                System.out.println();
            }
        }
        System.out.println();
    }

    public void printMemory(){
        System.out.println("Real Machine Memory:");
        for (int i = 0; i < 16*16*16; i++){
            if (i%16 == 0)
                System.out.print(String.format("%3d: ", i/16));
            if (memory[i] != null){
                if (memory[i].isEqual(new Word())){
                    System.out.print("$$$$ ");
                }
                else{
                    memory[i].printC();
                    System.out.print(" ");
                }
            }
            else {
                System.out.print("$$$$ ");
            }
            if ((i+1)%16 == 0){
                System.out.println();
            }
        }
        System.out.println();
    }

    public void printMemoryVM(){
        System.out.println("Virtual Machine Memory:");
        for (int i = 0; i < 16*16; i++){
            if (read(i) != null){
                if(read(i).isEqual(new Word())){
                    System.out.print("$$$$ ");
                }
                else{
                    read(i).printC();
                    System.out.print(" ");
                }
            }
            else{
                System.out.print("$$$$ ");
            }
            if ((i+1)%16 == 0){
                System.out.println();
            }
        }
        System.out.println();
    }

    public RMmemory(int maxBlockSize, CPU cpu){
        this.maxBlockSize = maxBlockSize;
        this.cpu = cpu;
        memory = new Word[maxBlockSize*16];
    }
}
