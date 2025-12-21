public class Token {
    public String tipo;
    public String lexema;

    public Token(String tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        if (tipo.equals("FIM")) {
            return "<FIM>";
        }
        return "<" + tipo + ", " + lexema + ">";
    }
}
