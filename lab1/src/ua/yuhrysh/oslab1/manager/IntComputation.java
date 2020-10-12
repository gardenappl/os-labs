package ua.yuhrysh.oslab1.manager;

public class IntComputation {
    
    public enum Type {
        F("f"),
        G("g");

        private final String internalName;

        Type(String internalName) {
            this.internalName = internalName;
        }

        public String getInternalName() {
            return internalName;
        }
    }
    
    private final Type type;
    private final int argument;

    public IntComputation(int argument, Type type) {
        this.type = type;
        this.argument = argument;
    }

    public int getArgument() {
        return argument;
    }

    public Type getType() {
        return type;
    }
}
