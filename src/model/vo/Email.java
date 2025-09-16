package model.vo;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object para Email.
 * - Imutável
 * - Normaliza (trim + lower-case)
 * - Valida formato com regex simples
 */
public final class Email {

    // Regex básica e pragmática para este projeto (case-insensitive)
    private static final Pattern EMAIL_RX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    private final String address; // normalizado (lower-case, trimmed)

    private Email(String address) {
        this.address = address;
    }

    /** Cria um Email validando formato e normalizando para lower-case. */
    public static Email of(String input) {
        if (input == null) throw new IllegalArgumentException("E-mail não pode ser nulo.");
        String v = input.trim().toLowerCase();
        if (v.isEmpty()) throw new IllegalArgumentException("E-mail não pode ser vazio.");
        if (!EMAIL_RX.matcher(v).matches()) throw new IllegalArgumentException("E-mail inválido.");
        return new Email(v);
    }

    /** Endereço completo normalizado. */
    public String value() { return address; }

    /** Parte local (antes do @). */
    public String usuario() {
        int at = address.lastIndexOf('@');
        return address.substring(0, at);
    }

    /** Domínio (após o @). */
    public String dominio() {
        int at = address.lastIndexOf('@');
        return address.substring(at + 1);
    }

    // ---------- equals/hashCode/toString ----------

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return Objects.equals(address, email.address);
    }

    @Override public int hashCode() { return Objects.hash(address); }

    @Override public String toString() { return address; }
}
