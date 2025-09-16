package model.dominio;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Liga uma Equipe a um Projeto por um período, com capacidade semanal de horas.
 * Invariantes:
 *  - projeto != null, equipe != null
 *  - dataInicio != null
 *  - se dataFim != null, então dataFim >= dataInicio
 *  - capacidadeHorasSemana >= 0
 */
public final class AlocacaoEquipeProjeto {

    private final String id; // UUID
    private Projeto projeto;
    private Equipe equipe;

    private LocalDate dataInicio;
    private LocalDate dataFim; // opcional (alocação vigente quando null)

    private int capacidadeHorasSemana; // >= 0
    private String observacoes;

    private AlocacaoEquipeProjeto(String id,
                                  Projeto projeto,
                                  Equipe equipe,
                                  LocalDate dataInicio,
                                  LocalDate dataFim,
                                  int capacidadeHorasSemana,
                                  String observacoes) {
        this.id = id;
        this.projeto = projeto;
        this.equipe = equipe;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.capacidadeHorasSemana = capacidadeHorasSemana;
        this.observacoes = observacoes;
    }

    /** Fábrica com validações de invariantes. */
    public static AlocacaoEquipeProjeto criar(Projeto projeto,
                                              Equipe equipe,
                                              LocalDate dataInicio,
                                              Integer capacidadeHorasSemana,
                                              String observacoes) {
        Objects.requireNonNull(projeto, "projeto não pode ser nulo");
        Objects.requireNonNull(equipe, "equipe não pode ser nula");
        Objects.requireNonNull(dataInicio, "dataInicio não pode ser nula");
        int cap = capacidadeHorasSemana == null ? 0 : capacidadeHorasSemana;
        validarCapacidade(cap);

        String id = UUID.randomUUID().toString();
        return new AlocacaoEquipeProjeto(
                id,
                projeto,
                equipe,
                dataInicio,
                null,
                cap,
                textoOuVazio(observacoes)
        );
    }

    // ----------------- Métodos de domínio -----------------

    /** Ajusta o período da alocação (permite dataFim nula). */
    public void ajustarPeriodo(LocalDate novoInicio, LocalDate novoFim) {
        Objects.requireNonNull(novoInicio, "novoInicio não pode ser nulo");
        validarPeriodo(novoInicio, novoFim);
        this.dataInicio = novoInicio;
        this.dataFim = novoFim;
    }

    /** Encerra a alocação definindo a data de fim (>= dataInicio). */
    public void encerrarAlocacao(LocalDate dataFim) {
        Objects.requireNonNull(dataFim, "dataFim não pode ser nula");
        validarPeriodo(this.dataInicio, dataFim);
        this.dataFim = dataFim;
    }

    /** Reabre a alocação (remove dataFim). */
    public void reabrirAlocacao() {
        this.dataFim = null;
    }

    /** Altera a capacidade semanal (>= 0). */
    public void alterarCapacidade(int horasSemana) {
        validarCapacidade(horasSemana);
        this.capacidadeHorasSemana = horasSemana;
    }

    /** Atualiza observações (aceita null -> vazio). */
    public void alterarObservacoes(String novasObs) {
        this.observacoes = textoOuVazio(novasObs);
    }

    /** Verifica se a alocação está vigente na data de referência (ou hoje). */
    public boolean isVigente(LocalDate referencia) {
        LocalDate ref = (referencia == null) ? LocalDate.now() : referencia;
        boolean iniciou = !dataInicio.isAfter(ref);
        boolean naoTerminou = (dataFim == null) || !dataFim.isBefore(ref);
        return iniciou && naoTerminou;
    }

    // ----------------- Getters -----------------

    public String getId() { return id; }
    public Projeto getProjeto() { return projeto; }
    public Equipe getEquipe() { return equipe; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public int getCapacidadeHorasSemana() { return capacidadeHorasSemana; }
    public String getObservacoes() { return observacoes; }

    // ----------------- Validações internas -----------------

    private static void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (fim != null && fim.isBefore(inicio)) {
            throw new IllegalArgumentException("dataFim deve ser maior ou igual a dataInicio.");
        }
    }

    private static void validarCapacidade(int horas) {
        if (horas < 0) {
            throw new IllegalArgumentException("capacidadeHorasSemana deve ser >= 0.");
        }
    }

    private static String textoOuVazio(String v) {
        return (v == null) ? "" : v.trim();
    }

    // ----------------- equals/hashCode/toString -----------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlocacaoEquipeProjeto)) return false;
        AlocacaoEquipeProjeto that = (AlocacaoEquipeProjeto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "AlocacaoEquipeProjeto{" +
                "id='" + id + '\'' +
                ", projeto='" + (projeto != null ? projeto.getNome() : "null") + '\'' +
                ", equipe='" + (equipe != null ? equipe.getNome() : "null") + '\'' +
                ", inicio=" + dataInicio +
                ", fim=" + (dataFim == null ? "—" : dataFim) +
                ", capacidadeHorasSemana=" + capacidadeHorasSemana +
                '}';
    }
}
