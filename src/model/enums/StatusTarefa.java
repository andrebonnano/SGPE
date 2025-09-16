package model.enums;

/**
 * Estados do ciclo de vida de uma Tarefa.
 * Usado para validações de fluxo, filtros e apresentação.
 */
public enum StatusTarefa {
    NOVA("Nova"),
    EM_ANDAMENTO("Em andamento"),
    BLOQUEADA("Bloqueada"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String rotulo;

    StatusTarefa(String rotulo) {
        this.rotulo = rotulo;
    }

    /** Rótulo amigável para exibição. */
    public String rotulo() { return rotulo; }

    /** Indica se a tarefa está finalizada (não sofre mais alterações). */
    public boolean isFinalizada() {
        return this == CONCLUIDA || this == CANCELADA;
    }

    /** Indica se a tarefa está em algum estado operacional. */
    public boolean isAtiva() {
        return this == NOVA || this == EM_ANDAMENTO || this == BLOQUEADA;
    }

    /**
     * Regras de transição:
     *  - NOVA → EM_ANDAMENTO | BLOQUEADA | CANCELADA
     *  - EM_ANDAMENTO → BLOQUEADA | CONCLUIDA | CANCELADA
     *  - BLOQUEADA → EM_ANDAMENTO | CONCLUIDA | CANCELADA
     *  - CONCLUIDA/CANCELADA → (sem transições)
     */
    public boolean podeTransicionarPara(StatusTarefa destino) {
        if (destino == null) return false;
        switch (this) {
            case NOVA:
                return destino == EM_ANDAMENTO || destino == BLOQUEADA || destino == CANCELADA;
            case EM_ANDAMENTO:
                return destino == BLOQUEADA || destino == CONCLUIDA || destino == CANCELADA;
            case BLOQUEADA:
                return destino == EM_ANDAMENTO || destino == CONCLUIDA || destino == CANCELADA;
            default:
                return false;
        }
    }

    /** Converte texto em StatusTarefa, aceitando variações comuns/PT-EN. */
    public static StatusTarefa parse(String valor) {
        if (valor == null) throw new IllegalArgumentException("Status não pode ser nulo.");
        String v = valor.trim().toUpperCase();
        switch (v) {
            case "NOVA": case "NOVO": case "OPEN":
                return NOVA;
            case "EM ANDAMENTO": case "ANDAMENTO": case "IN PROGRESS":
            case "EM_ANDAMENTO":
                return EM_ANDAMENTO;
            case "BLOQUEADA": case "BLOQUEADO": case "BLOCKED":
                return BLOQUEADA;
            case "CONCLUIDA": case "CONCLUÍDA": case "FINALIZADA":
            case "COMPLETED": case "DONE":
                return CONCLUIDA;
            case "CANCELADA": case "CANCELADO": case "CANCELED":
                return CANCELADA;
            default:
                throw new IllegalArgumentException("Status inválido: " + valor);
        }
    }
}
