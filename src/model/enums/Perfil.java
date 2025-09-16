package model.enums;

/**
 * Perfis de acesso do sistema.
 * Usado para controle de permissões e menus.
 */
public enum Perfil {
    ADMINISTRADOR("Administrador"),
    GERENTE("Gerente"),
    COLABORADOR("Colaborador");

    private final String rotulo;

    Perfil(String rotulo) {
        this.rotulo = rotulo;
    }

    /** Rótulo amigável para exibição em telas/relatórios. */
    public String rotulo() {
        return rotulo;
    }

    // Atalhos úteis
    public boolean isAdmin()       { return this == ADMINISTRADOR; }
    public boolean isGerente()     { return this == GERENTE; }
    public boolean isColaborador() { return this == COLABORADOR; }

    /**
     * Converte texto em Perfil, aceitando variações comuns.
     * Ex.: "admin", "administrador", "adm" → ADMINISTRADOR
     */
    public static Perfil parse(String valor) {
        if (valor == null) throw new IllegalArgumentException("Perfil não pode ser nulo.");
        String v = valor.trim().toUpperCase();

        switch (v) {
            case "ADMIN": case "ADM": case "ADMINISTRADOR":
                return ADMINISTRADOR;
            case "GERENTE": case "MANAGER":
                return GERENTE;
            case "COLABORADOR": case "USUARIO": case "USUÁRIO": case "USER":
                return COLABORADOR;
            default:
                throw new IllegalArgumentException("Perfil inválido: " + valor);
        }
    }
}
