package gitBackuper;

import gitBackuper.ui.ConsoleUserInterface;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ConsoleUserInterface ui = new ConsoleUserInterface();
        ui.showUI();
    }
}
