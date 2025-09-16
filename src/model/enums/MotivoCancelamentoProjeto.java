// src/model/enums/MotivoCancelamentoProjeto.java
package model.enums;

/**
 * Motivos padronizados para cancelamento de projetos.
 * Útil para relatórios e métricas de portfólio.
 */
public enum MotivoCancelamentoProjeto {
    ORCAMENTO_ESTOURADO("Orçamento estourado"),
    FALTA_RECURSOS("Falta de recursos (pessoas/tempo)"),
    PRIORIDADE_ALTERADA("Prioridade alterada pelo negócio"),
    DEPENDENCIAS_EXTERNAS("Dependências externas inviabilizadas"),
    VIABILIDADE_TECNICA("Inviabilidade técnica"),
    RISCOS_INACEITAVEIS("Riscos inaceitáveis"),
    CLIENTE_CANCELOU("Cancelamento pelo cliente"),
    COMPLIANCE_LEGAL("Compliance / Legal"),
    DUPLICIDADE("Duplicidade / Projeto substituído"),
    DECISAO_ESTRATEGICA("Decisão estratégica");

    private final String rotulo;

    MotivoCancelamentoProjeto(String rotulo) {
        this.rotulo = rotulo;
    }

    /** Rótulo amigável para exibição em telas/relatórios. */
    public String rotulo() { return rotulo; }

    /** Converte texto em enum, aceitando variações comuns. */
    public static MotivoCancelamentoProjeto parse(String valor) {
        if (valor == null) throw new IllegalArgumentException("Motivo não pode ser nulo.");
        String v = valor.trim().toUpperCase();

        switch (v) {
            case "ORCAMENTO ESTOURADO":
            case "ORÇAMENTO ESTOURADO":
            case "BUDGET":
            case "BUDGET ESTOURADO":
                return ORCAMENTO_ESTOURADO;

            case "FALTA RECURSOS":
            case "FALTA DE RECURSOS":
            case "RECURSOS":
            case "TIME INSUFICIENTE":
                return FALTA_RECURSOS;

            case "PRIORIDADE ALTERADA":
            case "ALTERACAO DE PRIORIDADE":
            case "ALTERAÇÃO DE PRIORIDADE":
            case "REPRIORIZACAO":
            case "REPRIORIZAÇÃO":
                return PRIORIDADE_ALTERADA;

            case "DEPENDENCIAS EXTERNAS":
            case "DEPENDÊNCIAS EXTERNAS":
            case "DEPENDENCIAS":
            case "FORNECEDOR":
                return DEPENDENCIAS_EXTERNAS;

            case "VIABILIDADE TECNICA":
            case "VIABILIDADE TÉCNICA":
            case "INVIABILIDADE TECNICA":
            case "INVIABILIDADE TÉCNICA":
                return VIABILIDADE_TECNICA;

            case "RISCOS INACEITAVEIS":
            case "RISCOS INACEITÁVEIS":
            case "RISCO":
                return RISCOS_INACEITAVEIS;

            case "CLIENTE CANCELOU":
            case "CANCELAMENTO PELO CLIENTE":
            case "CLIENT CANCELED":
                return CLIENTE_CANCELOU;

            case "COMPLIANCE":
            case "LEGAL":
            case "COMPLIANCE LEGAL":
                return COMPLIANCE_LEGAL;

            case "DUPLICIDADE":
            case "PROJETO DUPLICADO":
            case "SUBSTITUIDO":
            case "SUBSTITUÍDO":
                return DUPLICIDADE;

            case "DECISAO ESTRATEGICA":
            case "DECISÃO ESTRATÉGICA":
            case "ESTRATEGIA":
            case "ESTRATÉGIA":
                return DECISAO_ESTRATEGICA;

            default:
                throw new IllegalArgumentException("Motivo de cancelamento inválido: " + valor);
        }
    }
}
