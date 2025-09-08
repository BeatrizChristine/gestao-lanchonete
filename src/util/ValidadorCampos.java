package util;

public class ValidadorCampos {

    // Nome: apenas letras (com ou sem acento) e espaços
    public static boolean nomeValido(String nome) {
        return nome.matches("^[A-Za-zÀ-ÿ\\s]+$");
    }

    // Matrícula: exatamente 4 números
    public static boolean matriculaValida(String matricula) {
        return matricula.matches("^\\d{4}$");
    }
}
