package model.dominio;

import model.enums.PrioridadeTarefa;
import model.enums.StatusTarefa;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio que representa uma Tarefa de um Projeto.
 * Regras principais:
 *  - dataTerminoPrevista >= dataInicio
 *  - Transições de status controladas (NOVA → EM_ANDAMENTO/BLOQUEADA/CANCELADA;
 *    EM_ANDAMENTO → BLOQUEADA/CONCLUIDA/CANCELADA; BLOQUEADA → EM_ANDAMENTO/CANCELADA)
 *  - CONCLUIDA exige dataConclusao e esforçoReal informado
 */
public final class Tarefa {

    private final String id; // UUID
    private Projeto projeto;
    private String titulo;
    private String descricao;

    private Usuario responsavel;                // opcional no cadastro
    private PrioridadeTarefa prioridade;        // BAIXA/MEDIA/ALTA/CRITICA
    private StatusTarefa status;                // NOVA (default), ...

    private LocalDate dataInicio;
    private LocalDate dataTerminoPrevista;
    private LocalDate dataConclusao;            // somente quando CONCLUIDA

    private int esforcoEstimadoHoras;           // >= 0
    private int esforcoRealHoras;               // >= 0 (soma de registros)

    private Tarefa(String id,
                   Projeto projeto,
                   String titulo,
                   String descricao,
                   Usuario responsavel,
                   PrioridadeTarefa prioridade,
                   StatusTarefa status,
                   LocalDate dataInicio,
                   LocalDate dataTerminoPrevista,
                   int esforcoEstimadoHoras,
                   int esforcoRealHoras,
                   LocalDate dataConclusao) {
        this.id = id;
        this.projeto = projeto;
        this.titulo = titulo;
        this.descricao = descricao;
        this.responsavel = responsavel;
        this.prioridade = prioridade;
        this.status = status;
        this.dataInicio = dataInicio;
        this.dataTerminoPrevista = dataTerminoPrevista;
        this.esforcoEstimadoHoras = esforcoEstimadoHoras;
        this.esforcoRealHoras = esforcoRealHoras;
        this.dataConclusao = dataConclusao;
    }

    /** Fábrica com validações de invariantes. */
    public static Tarefa criar(Projeto projeto,
                               String titulo,
                               String descricao,
                               Usuario responsavel,            // pode ser null
                               PrioridadeTarefa prioridade,    // se null, assume MEDIA
                               LocalDate dataInicio,
                               LocalDate dataTerminoPrevista,
                               Integer esforcoEstimadoHoras) { // se null, assume 0
        Objects.requireNonNull(projeto, "projeto não pode ser nulo");
        validarObrigatorio(titulo, "titulo");
        Objects.requireNonNull(dataInicio, "dataInicio não pode ser nula");
        Objects.requireNonNull(dataTerminoPrevista, "dataTerminoPrevista não pode ser nula");
        validarDatas(dataInicio, dataTerminoPrevista);

        PrioridadeTarefa prio = (prioridade == null) ? PrioridadeTarefa.MEDIA : prioridade;
        int estimado = (esforcoEstimadoHoras == null) ? 0 : Math.max(0, esforcoEstimadoHoras);
        String id = UUID.randomUUID().toString();

        return new Tarefa(
                id,
                projeto,
                titulo.trim(),
                descricao == null ? "" : descricao.trim(),
                responsavel,
                prio,
                StatusTarefa.NOVA,
                dataInicio,
                dataTerminoPrevista,
                estimado,
                0,
                null
        );
    }

    // ----------------- Métodos de domínio -----------------

    /** Replaneja datas garantindo coerência (término >= início). */
    public void replanejar(LocalDate novaDataInicio, LocalDate novaDataTerminoPrevista) {
        garantirNaoFinalizada();
        Objects.requireNonNull(novaDataInicio, "novaDataInicio não pode ser nula");
        Objects.requireNonNull(novaDataTerminoPrevista, "novaDataTerminoPrevista não pode ser nula");
        validarDatas(novaDataInicio, novaDataTerminoPrevista);
        this.dataInicio = novaDataInicio;
        this.dataTerminoPrevista = novaDataTerminoPrevista;
    }

    /** Atribui (ou troca) o responsável pela tarefa. Aceita null para desatribuir. */
    public void atribuirResponsavel(Usuario novoResponsavel) {
        garantirNaoFinalizada();
        this.responsavel = novoResponsavel;
    }

    /** Atualiza prioridade. */
    public void alterarPrioridade(PrioridadeTarefa novaPrioridade) {
        garantirNaoFinalizada();
        this.prioridade = Objects.requireNonNull(novaPrioridade, "prioridade não pode ser nula");
    }

    /** Altera o título. */
    public void alterarTitulo(String novoTitulo) {
        garantirNaoFinalizada();
        validarObrigatorio(novoTitulo, "titulo");
        this.titulo = novoTitulo.trim();
    }

    /** Altera a descrição. */
    public void alterarDescricao(String novaDescricao) {
        garantirNaoFinalizada();
        this.descricao = (novaDescricao == null) ? "" : novaDescricao.trim();
    }

    /** Registra/acrescenta esforço real em horas (>=1). */
    public void registrarEsforco(int horas) {
        garantirNaoFinalizada();
        if (horas <= 0) throw new IllegalArgumentException("Horas devem ser positivas.");
        this.esforcoRealHoras += horas;
    }

    /** Define esforço estimado (>=0). */
    public void definirEsforcoEstimado(int horas) {
        garantirNaoFinalizada();
        if (horas < 0) throw new IllegalArgumentException("Esforço estimado deve ser >= 0.");
        this.esforcoEstimadoHoras = horas;
    }

    /** Transição de status com regras básicas de fluxo. */
    public void alterarStatus(StatusTarefa destino) {
        Objects.requireNonNull(destino, "status destino não pode ser nulo");
        if (!podeTransicionarPara(destino)) {
            throw new IllegalStateException("Transição de status inválida: " + status + " → " + destino);
        }
        // Se for concluir sem dados, orientar uso de concluir()
        if (destino == StatusTarefa.CONCLUIDA) {
            throw new IllegalStateException("Use concluir(horas, data) para encerrar a tarefa.");
        }
        this.status = destino;
    }

    /** Inicia a tarefa (atalho para status EM_ANDAMENTO). */
    public void iniciar() { alterarStatus(StatusTarefa.EM_ANDAMENTO); }

    /** Bloqueia a tarefa (atalho para status BLOQUEADA). */
    public void bloquear() {
        if (status == StatusTarefa.CONCLUIDA || status == StatusTarefa.CANCELADA) {
            throw new IllegalStateException("Não é possível bloquear tarefa finalizada.");
        }
        if (status == StatusTarefa.NOVA || status == StatusTarefa.EM_ANDAMENTO || status == StatusTarefa.BLOQUEADA) {
            this.status = StatusTarefa.BLOQUEADA;
        } else {
            throw new IllegalStateException("Transição para BLOQUEADA inválida a partir de " + status);
        }
    }

    /** Cancela a tarefa. */
    public void cancelar() {
        if (status == StatusTarefa.CONCLUIDA) {
            throw new IllegalStateException("Não é possível cancelar tarefa concluída.");
        }
        this.status = StatusTarefa.CANCELADA;
    }

    /** Conclui a tarefa exigindo esforço real e data de conclusão válidos. */
    public void concluir(int horasRealGastas, LocalDate dataConclusao) {
        if (!podeTransicionarPara(StatusTarefa.CONCLUIDA)) {
            throw new IllegalStateException("Transição para CONCLUIDA inválida a partir de " + status);
        }
        if (horasRealGastas < 0) throw new IllegalArgumentException("Esforço real deve ser >= 0.");
        Objects.requireNonNull(dataConclusao, "dataConclusao não pode ser nula");
        if (dataConclusao.isBefore(this.dataInicio)) {
            throw new IllegalArgumentException("dataConclusao não pode ser anterior à data de início.");
        }
        this.esforcoRealHoras += horasRealGastas;
        this.dataConclusao = dataConclusao;
        this.status = StatusTarefa.CONCLUIDA;
    }

    /** Indica se, na data informada (ou hoje), a tarefa está atrasada. */
    public boolean estaAtrasada(LocalDate referencia) {
        LocalDate ref = (referencia == null) ? LocalDate.now() : referencia;
        return (status != StatusTarefa.CONCLUIDA && status != StatusTarefa.CANCELADA)
                && dataTerminoPrevista.isBefore(ref);
    }

    // ----------------- Getters -----------------

    public String getId() { return id; }
    public Projeto getProjeto() { return projeto; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public Usuario getResponsavel() { return responsavel; }
    public PrioridadeTarefa getPrioridade() { return prioridade; }
    public StatusTarefa getStatus() { return status; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataTerminoPrevista() { return dataTerminoPrevista; }
    public LocalDate getDataConclusao() { return dataConclusao; }
    public int getEsforcoEstimadoHoras() { return esforcoEstimadoHoras; }
    public int getEsforcoRealHoras() { return esforcoRealHoras; }

    // ----------------- Validações internas -----------------

    private static void validarObrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não informado: " + campo);
        }
    }

    private static void validarDatas(LocalDate inicio, LocalDate terminoPrevisto) {
        if (terminoPrevisto.isBefore(inicio)) {
            throw new IllegalArgumentException("dataTerminoPrevista deve ser maior ou igual a dataInicio.");
        }
    }

    private void garantirNaoFinalizada() {
        if (status == StatusTarefa.CONCLUIDA || status == StatusTarefa.CANCELADA) {
            throw new IllegalStateException("Tarefa finalizada não pode ser alterada.");
        }
    }

    private boolean podeTransicionarPara(StatusTarefa destino) {
        if (destino == null) return false;
        switch (this.status) {
            case NOVA:
                return destino == StatusTarefa.EM_ANDAMENTO
                        || destino == StatusTarefa.BLOQUEADA
                        || destino == StatusTarefa.CANCELADA;
            case EM_ANDAMENTO:
                return destino == StatusTarefa.BLOQUEADA
                        || destino == StatusTarefa.CONCLUIDA
                        || destino == StatusTarefa.CANCELADA;
            case BLOQUEADA:
                return destino == StatusTarefa.EM_ANDAMENTO
                        || destino == StatusTarefa.CANCELADA
                        || destino == StatusTarefa.CONCLUIDA; // permitir concluir bloqueada se houve trabalho?
            default:
                return false; // CONCLUIDA e CANCELADA não saem
        }
    }

    // ----------------- equals/hashCode/toString -----------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tarefa)) return false;
        Tarefa tarefa = (Tarefa) o;
        return Objects.equals(id, tarefa.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Tarefa{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", projeto='" + (projeto != null ? projeto.getNome() : "null") + '\'' +
                ", prioridade=" + prioridade +
                ", status=" + status +
                ", terminoPrevisto=" + dataTerminoPrevista +
                ", responsavel=" + (responsavel != null ? responsavel.getLogin() : "—") +
                '}';
    }
}