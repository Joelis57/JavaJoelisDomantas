import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RealMachine {

    private CPU cpu;
    private boolean exit;

    public void run(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Initializing opOS...");
        while(!exit){
            System.out.print("opOS> ");
            String[] t = new String[0];
            try {
                t = reader.readLine().split(" ");
            } catch (IOException e) {
                e.printStackTrace();
            }
            File file = new File(t[0]);
            if (file.exists()){
                if (t.length > 1)
                    if (t[1].equals("d")){

                        cpu.setDebug(true);
                    }
                    else{
                        cpu.setDebug(false);
                    }
                else{
                    cpu.setDebug(false);
                }
                cpu.resetVM();
                ProgramLoader tmp = new ProgramLoader(t[0]);
                cpu.getMemory().newPaging(tmp.getMemory());
                VM.VirtualMachine vm = new VM.VirtualMachine(cpu,cpu.getDebug());
                vm.run();
            }
            else if (t[0].equals("exit")){
                exit = true;
            }
            else if (t[0].equals("cls") || t[0].equals("clear")){
                for (int i = 0; i < 50; ++i) System.out.println(); // clear
            }
            else if (t[0].equals("pm")){
                cpu.memory.printMemory();
            }
            else if (t[0].equals("pr")){
                cpu.printReg();
            }
            else if (t[0].equals("alloc") && t.length > 1) {
                cpu.memory.allocateBlock(Integer.parseInt(t[1]));
            }
            else{
                System.out.println("File/Command \"" + t[0] + "\" not found!");
            }
        }
    }

    public RealMachine(){
        cpu = new CPU();
        exit = false;
    }

}
