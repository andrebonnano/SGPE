package model.enums;

/**
 * Níveis de prioridade de uma tarefa.
 */
public enum PrioridadeTarefa {
    BAIXA("Baixa", 1),
    MEDIA("Média", 2),
    ALTA("Alta", 3),
    CRITICA("Crítica", 4);

    private final String rotulo;
    private final int peso; // útil para ordenar/priorizar (maior = mais urgente)

    PrioridadeTarefa(String rotulo, int peso) {
        this.rotulo = rotulo;
        this.peso = peso;
    }

    /** Rótulo amigável para exibição. */
    public String rotulo() { return rotulo; }

    /** Peso de severidade (1..4). */
    public int peso() { return peso; }

    /** Atalho para verificações de urgência. */
    public boolean isAltaOuCritica() {
        return this == ALTA || this == CRITICA;
    }

    /** Converte texto em PrioridadeTarefa, aceitando variações comuns. */
    public static PrioridadeTarefa parse(String valor) {
        if (valor == null) throw new IllegalArgumentException("Prioridade não pode ser nula.");
        String v = valor.trim().toUpperCase();
        switch (v) {
            case "BAIXA":
            case "LOW":
                return BAIXA;
            case "MEDIA":
            case "MÉDIA":
            case "MED":
            case "MEDIUM":
                return MEDIA;
            case "ALTA":
            case "HIGH":
                return ALTA;
            case "CRITICA":
            case "CRÍTICA":
            case "CRITICO":
            case "CRÍTICO":
            case "CRITICAL":
            case "URGENTE":
            case "URGENT":
                return CRITICA;
            default:
                throw new IllegalArgumentException("Prioridade inválida: " + valor);
        }
    }
}
