package ac.id.ubpkarawang.sigeoo.Preprocessor;

import java.util.HashMap;

public final class CommandFactory {
    private final HashMap<String, Command> commands;

    public CommandFactory() {
        commands = new HashMap<>();
    }

    public void addCommand(String name, Command command) {
        commands.put(name, command);
    }

    public PreProcessor executeCommand(String name, PreProcessor preProcessor) {
        if (commands.containsKey(name)) {
            return commands.get(name).preprocessImage(preProcessor);
        } else {
            return null;
        }
    }
}
