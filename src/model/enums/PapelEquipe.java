package model.enums;

/**
 * Papéis desempenhados pelos membros de uma equipe.
 * Útil para compor times e gerar relatórios por função.
 */
public enum PapelEquipe {
    DEV("Desenvolvedor"),
    ANALISTA("Analista de Sistemas"),
    DESIGNER("Designer de Interface"),
    QA("Analista de Qualidade (QA)");

    private final String rotulo;

    PapelEquipe(String rotulo) {
        this.rotulo = rotulo;
    }

    /** Rótulo amigável para exibição. */
    public String rotulo() {
        return rotulo;
    }

    /** Converte texto em PapelEquipe, aceitando variações comuns/PT-EN. */
    public static PapelEquipe parse(String valor) {
        if (valor == null) throw new IllegalArgumentException("Papel não pode ser nulo.");
        String v = valor.trim().toUpperCase();

        switch (v) {
            case "DEV":
            case "DESENVOLVEDOR":
            case "DEVELOPER":
                return DEV;

            case "ANALISTA":
            case "ANALISTA DE SISTEMAS":
            case "SYSTEM ANALYST":
            case "ANALYST":
                return ANALISTA;

            case "DESIGNER":
            case "UI":
            case "UX":
            case "UI/UX":
            case "PRODUCT DESIGNER":
                return DESIGNER;

            case "QA":
            case "QUALIDADE":
            case "TESTER":
            case "QUALITY ASSURANCE":
                return QA;

            default:
                throw new IllegalArgumentException("Papel de equipe inválido: " + valor);
        }
    }
}
