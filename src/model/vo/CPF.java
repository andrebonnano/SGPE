package model.vo;

import java.util.Objects;

/**
 * Value Object para CPF (somente dígitos).
 * - Imutável
 * - Valida formato e dígitos verificadores
 * - Oferece formatos: bruto (11 dígitos), formatado (###.###.###-##) e mascarado
 */
public final class CPF {

    private final String digits; // 11 dígitos

    private CPF(String digits) {
        this.digits = digits;
    }

    /** Cria um CPF a partir de qualquer entrada (com ou sem máscara), validando DV. */
    public static CPF of(String input) {
        if (input == null) throw new IllegalArgumentException("CPF não pode ser nulo.");
        String d = input.replaceAll("\\D", "");
        if (d.length() != 11) throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        if (todosIguais(d)) throw new IllegalArgumentException("CPF inválido (sequência repetida).");
        if (!dvValido(d)) throw new IllegalArgumentException("CPF inválido (dígitos verificadores).");
        return new CPF(d);
    }

    /** Retorna os 11 dígitos (sem máscara). */
    public String value() { return digits; }

    /** Retorna o CPF formatado (###.###.###-##). */
    public String formatado() {
        return digits.substring(0,3) + "." + digits.substring(3,6) + "." +
                digits.substring(6,9) + "-" + digits.substring(9);
    }

    /** Retorna o CPF mascarado (***.***.***-##). */
    public String mascarado() {
        return "***.***.***-" + digits.substring(9);
    }

    // ---------- helpers de validação ----------

    private static boolean todosIguais(String d) {
        char c = d.charAt(0);
        for (int i = 1; i < d.length(); i++) if (d.charAt(i) != c) return false;
        return true;
    }

    private static boolean dvValido(String d) {
        // Cálculo do 1º DV
        int soma = 0, peso = 10;
        for (int i = 0; i < 9; i++) soma += (d.charAt(i) - '0') * peso--;
        int resto = soma % 11;
        int dv1 = (resto < 2) ? 0 : 11 - resto;

        // Cálculo do 2º DV
        soma = 0; peso = 11;
        for (int i = 0; i < 10; i++) soma += (d.charAt(i) - '0') * peso--;
        resto = soma % 11;
        int dv2 = (resto < 2) ? 0 : 11 - resto;

        return dv1 == (d.charAt(9) - '0') && dv2 == (d.charAt(10) - '0');
    }

    // ---------- equals/hashCode/toString ----------

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CPF)) return false;
        CPF cpf = (CPF) o;
        return Objects.equals(digits, cpf.digits);
    }

    @Override public int hashCode() { return Objects.hash(digits); }

    @Override public String toString() { return formatado(); }
}
