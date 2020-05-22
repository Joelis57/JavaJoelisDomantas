import java.io.IOException;
import java.util.Scanner;

public class VM {

    static class VMmemory implements Memory{
        private RMmemory memory;

        @Override
        public Word read(int index){
            return memory.read(index);
        }

        @Override
        public void write(int index, int info){
            memory.write(index, info);
        }

        @Override
        public void write(int index, Word info){
            memory.write(index, info);
        }

        public void print(){
            memory.printMemory();
            memory.printMemoryVM();
        }

        public VMmemory(RMmemory memory){
            this.memory = memory;
        }
    }

    static class VirtualMachine{
        private boolean C;
        private int IC;
        private int R;
        private int D;
        private VMmemory memory;
        private CPU cpu;
        private boolean running;
        private boolean debug;

        public void update(){
            running = cpu.update(this);
            C = cpu.getC();
            IC = cpu.getIC();
            R = cpu.getR();
            D = cpu.getD();
        }

        public void run(){
            while (running){
                if (debug){
                    print();
                    Scanner scanner = new Scanner(System.in);
                    scanner.nextLine();
                    for (int i = 0; i < 50; ++i) System.out.println(); // clear
                }
                cpu.execute(memory.read(IC));

                update();
            }
        }

        public void print(){
            C = cpu.getC();
            IC = cpu.getIC();
            R = cpu.getR();
            D = cpu.getD();
            memory.print();
            cpu.printReg();
            System.out.println("Virtual Machine Registers:");
            System.out.println(String.format("\tC: %d", C ? 1 : 0));
            System.out.println(String.format("\tIC: %d", IC));
            System.out.println(String.format("\tR: %d", R));
            System.out.println(String.format("\tD: %d", D));
            System.out.print("Executing: ");
            memory.read(IC).printC();
            System.out.println();
        }

        public VirtualMachine(CPU cpu, boolean debug){
            memory = new VMmemory(cpu.getMemory());
            this.cpu = cpu;
            update();
            running = true;
            this.debug = debug;
        }
    }

}
