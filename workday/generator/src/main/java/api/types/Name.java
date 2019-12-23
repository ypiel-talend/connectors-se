package api.types;

public class Name {

    private final String value;

    public Name(String value) {
        this.value = build(value);
    }

    public String getValue() {
        return value;
    }

    private String build(String base) {
        StringBuilder name = new StringBuilder(base);

        int pos = name.length() - 1;
        while (pos >= 0) {
            if (!this.isOk(name.charAt(pos))) {
                name.deleteCharAt(pos);
                this.toUpper(name, pos);
            }
            pos--;
        }
        //this.toUpper(name, 0);

        return name.toString();
    }

    private void toUpper(StringBuilder name, int pos) {
        if (pos < name.length()) {
            char nextChar = name.charAt(pos);
            if (nextChar > 'a' && nextChar < 'z') {
                name.setCharAt(pos, Character.toUpperCase(nextChar));
            }
        }
    }

    private boolean isOk(char x) {
        return (x >= 'a' && x <='z')
                || (x >= 'A' && x <= 'Z')
                || (x >= '0' && x <= '9')
                || x == '_' ;
    }
}
