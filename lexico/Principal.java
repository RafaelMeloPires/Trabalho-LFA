import afds.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Principal {

    // Arquivo de entrada
    public String nomeArquivo = "./lexico/arquivo.txt";

    // AFDs
    public AFD afdNumero = new AFD();
    public AFD afdId = new AFD();

    // Estados correntes de cada AFD
    public Estado estadoNumero;
    public Estado estadoId;

    public Principal() throws Exception {
        // Carrega os AFDs
        afdNumero.ler("./lexico/AFD.XML");
        afdId.ler("./lexico/AFD_ID.xml");
    }

    public static void main(String[] args) {
        try {
            Principal p = new Principal();
            p.inicio();
        } catch (Exception ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Lê o próximo símbolo do arquivo
     */
    public Simbolo proximo(BufferedReader reader) throws IOException {
        int charLido = reader.read();
        if (charLido == -1) {
            return null;
        }

        // Trata quebra de linha como símbolo explícito
        if (charLido == 10) {
            return new Simbolo('\n');
        }

        return new Simbolo((char) charLido);
    }

    /**
     * Analisador léxico
     */
    /*public Token lexico(BufferedReader r) throws IOException {
        String lexema = "";

        estadoNumero = afdNumero.getEstadoInicial();
        estadoId = afdId.getEstadoInicial();

        r.mark(1);
        Simbolo p = proximo(r);

        // Ignorar delimitadores iniciais
        while (p != null && (p.toString().equals(" ") || p.toString().equals("\n"))) {
            r.mark(1);
            p = proximo(r);
        }

        if (p == null) {
            return new Token("FIM", "");
        }

        while (p != null) {
            lexema += p.toString();

            if (estadoNumero != null) {
                estadoNumero = afdNumero.p(estadoNumero, p);
            }

            if (estadoId != null) {
                estadoId = afdId.p(estadoId, p);
            }

            // Nenhum AFD aceita continuar
            if (estadoNumero == null && estadoId == null) {
                return new Token("ERRO", lexema);
            }

            // NUMERO reconhecido
            if (estadoNumero != null &&
                    afdNumero.getEstadosFinais().pertence(estadoNumero)) {

                r.reset(); // devolve delimitador
                return new Token("NUMERO", lexema.trim());
            }

            // IDENTIFICADOR reconhecido
            if (estadoId != null &&
                    afdId.getEstadosFinais().pertence(estadoId)) {

                r.reset(); // devolve delimitador
                return new Token("IDENTIFICADOR", lexema.trim());
            }

            r.mark(1);
            p = proximo(r);
        }

        return new Token("FIM", "");
    }*/

    public Token lexico(BufferedReader r) throws IOException {
        String lexema = "";

        estadoNumero = afdNumero.getEstadoInicial();
        estadoId = afdId.getEstadoInicial();

        Simbolo p = proximo(r);

        // Ignorar delimitadores iniciais
        while (p != null && (p.toString().equals(" ") || p.toString().equals("\n"))) {
            p = proximo(r);
        }

        if (p == null) {
            return new Token("FIM", "");
        }

        while (p != null) {

            // Delimitador encerra token
            if (p.toString().equals(" ") || p.toString().equals("\n")) {

                if (estadoNumero != null) {
                    estadoNumero = afdNumero.p(estadoNumero, p);
                }
                if (estadoId != null) {
                    estadoId = afdId.p(estadoId, p);
                }

                if (estadoNumero != null &&
                        afdNumero.getEstadosFinais().pertence(estadoNumero)) {
                    return new Token("NUMERO", lexema);
                }

                if (estadoId != null &&
                        afdId.getEstadosFinais().pertence(estadoId)) {
                    return new Token("IDENTIFICADOR", lexema);
                }

                return new Token("ERRO", lexema);
            }

            // Símbolo normal
            lexema += p.toString();

            if (estadoNumero != null) {
                estadoNumero = afdNumero.p(estadoNumero, p);
            }

            if (estadoId != null) {
                estadoId = afdId.p(estadoId, p);
            }

            if (estadoNumero == null && estadoId == null) {
                return new Token("ERRO", lexema);
            }

            p = proximo(r);
        }

        // 🚨 EOF = delimitador implícito
        if (estadoNumero != null &&
                afdNumero.getEstadosFinais().pertence(estadoNumero)) {
            return new Token("NUMERO", lexema);
        }

        if (estadoId != null &&
                afdId.getEstadosFinais().pertence(estadoId)) {
            return new Token("IDENTIFICADOR", lexema);
        }

        if (!lexema.isEmpty()) {
            return new Token("ERRO", lexema);
        }

        return new Token("FIM", "");
    }



    /**
     * Loop principal do analisador léxico
     */
    public void inicio() {
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {

            Token t = lexico(reader);

            while (!t.tipo.equals("ERRO") && !t.tipo.equals("FIM")) {
                System.out.println(t);
                t = lexico(reader);
            }

            System.out.println(t);

        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}