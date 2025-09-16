package model.enums;

/**
 * Estados do ciclo de vida de um Projeto.
 * Útil para validações, filtros e apresentação.
 */
public enum StatusProjeto {
    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String rotulo;

    StatusProjeto(String rotulo) {
        this.rotulo = rotulo;
    }

    /** Rótulo amigável para exibição. */
    public String rotulo() {
        return rotulo;
    }

    /** Indica se o projeto está em execução. */
    public boolean isAtivo() {
        return this == EM_ANDAMENTO;
    }

    /** Indica se o projeto não sofrerá mais alterações de execução. */
    public boolean isFinalizado() {
        return this == CONCLUIDO || this == CANCELADO;
    }

    /**
     * Regras simples de transição:
     *  - PLANEJADO → EM_ANDAMENTO ou CANCELADO
     *  - EM_ANDAMENTO → CONCLUIDO ou CANCELADO
     *  - CONCLUIDO/CANCELADO → (sem transição)
     */
    public boolean podeTransicionarPara(StatusProjeto destino) {
        if (destino == null) return false;
        switch (this) {
            case PLANEJADO:
                return destino == EM_ANDAMENTO || destino == CANCELADO;
            case EM_ANDAMENTO:
                return destino == CONCLUIDO || destino == CANCELADO;
            default:
                return false;
        }
    }

    /** Converte texto em StatusProjeto, aceitando variações comuns. */
    public static StatusProjeto parse(String valor) {
        if (valor == null) throw new IllegalArgumentException("Status não pode ser nulo.");
        String v = valor.trim().toUpperCase();
        switch (v) {
            case "PLANEJADO":
                return PLANEJADO;
            case "EM ANDAMENTO":
            case "ANDAMENTO":
            case "EM_ANDAMENTO":
                return EM_ANDAMENTO;
            case "CONCLUIDO":
            case "CONCLUÍDO":
            case "CONCLUIDO(A)":
                return CONCLUIDO;
            case "CANCELADO":
                return CANCELADO;
            default:
                throw new IllegalArgumentException("Status inválido: " + valor);
        }
    }
}
