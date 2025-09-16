package model.dominio;

import model.enums.StatusProjeto;
import model.enums.Perfil;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio que representa um Projeto.
 * Regras principais:
 *  - dataTerminoPrevista >= dataInicio
 *  - gerenteResponsavel deve ter perfil GERENTE ou ADMINISTRADOR
 */
public final class Projeto {

    private final String id; // UUID
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataTerminoPrevista;
    private StatusProjeto status;
    private Usuario gerenteResponsavel;

    private Projeto(String id,
                    String nome,
                    String descricao,
                    LocalDate dataInicio,
                    LocalDate dataTerminoPrevista,
                    StatusProjeto status,
                    Usuario gerenteResponsavel) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataTerminoPrevista = dataTerminoPrevista;
        this.status = status;
        this.gerenteResponsavel = gerenteResponsavel;
    }

    /** Fábrica com validações de invariantes. */
    public static Projeto criar(String nome,
                                String descricao,
                                LocalDate dataInicio,
                                LocalDate dataTerminoPrevista,
                                Usuario gerenteResponsavel,
                                StatusProjeto status) {
        validarObrigatorio(nome, "nome");
        validarObrigatorio(descricao, "descricao");
        Objects.requireNonNull(dataInicio, "dataInicio não pode ser nula");
        Objects.requireNonNull(dataTerminoPrevista, "dataTerminoPrevista não pode ser nula");
        validarDatas(dataInicio, dataTerminoPrevista);
        definirGerenteValido(gerenteResponsavel);

        StatusProjeto statusFinal = (status == null) ? StatusProjeto.PLANEJADO : status;
        String id = UUID.randomUUID().toString();

        return new Projeto(id, nome.trim(), descricao.trim(),
                dataInicio, dataTerminoPrevista, statusFinal, gerenteResponsavel);
    }

    // ----------------- Métodos de domínio -----------------

    /** Replaneja datas garantindo coerência (término >= início). */
    public void replanejar(LocalDate novaDataInicio, LocalDate novaDataTerminoPrevista) {
        Objects.requireNonNull(novaDataInicio, "novaDataInicio não pode ser nula");
        Objects.requireNonNull(novaDataTerminoPrevista, "novaDataTerminoPrevista não pode ser nula");
        validarDatas(novaDataInicio, novaDataTerminoPrevista);
        this.dataInicio = novaDataInicio;
        this.dataTerminoPrevista = novaDataTerminoPrevista;
    }

    /** Altera status do projeto (ex.: PLANEJADO → EM_ANDAMENTO → CONCLUIDO ou CANCELADO). */
    public void alterarStatus(StatusProjeto novoStatus) {
        this.status = Objects.requireNonNull(novoStatus, "status não pode ser nulo");
    }

    /** Atualiza descrição com validação. */
    public void alterarDescricao(String novaDescricao) {
        validarObrigatorio(novaDescricao, "descricao");
        this.descricao = novaDescricao.trim();
    }

    /** Define/atualiza o gerente responsável (precisa ser GERENTE ou ADMINISTRADOR). */
    public void definirGerenteResponsavel(Usuario novoGerente) {
        definirGerenteValido(novoGerente);
        this.gerenteResponsavel = novoGerente;
    }

    /** Indica se, na data informada (ou hoje), o projeto está atrasado. */
    public boolean estaAtrasado(LocalDate referencia) {
        LocalDate ref = (referencia == null) ? LocalDate.now() : referencia;
        return (status != StatusProjeto.CONCLUIDO && status != StatusProjeto.CANCELADO)
                && dataTerminoPrevista.isBefore(ref);
    }

    // ----------------- Getters -----------------

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataTerminoPrevista() { return dataTerminoPrevista; }
    public StatusProjeto getStatus() { return status; }
    public Usuario getGerenteResponsavel() { return gerenteResponsavel; }

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

    private static void definirGerenteValido(Usuario gerente) {
        if (gerente == null) throw new IllegalArgumentException("gerenteResponsavel não pode ser nulo.");
        Perfil p = gerente.getPerfil();
        if (!(p == Perfil.GERENTE || p == Perfil.ADMINISTRADOR)) {
            throw new IllegalArgumentException("gerenteResponsavel deve ter perfil GERENTE ou ADMINISTRADOR.");
        }
    }

    // ----------------- equals/hashCode/toString -----------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Projeto)) return false;
        Projeto projeto = (Projeto) o;
        return Objects.equals(id, projeto.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Projeto{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", status=" + status +
                ", dataInicio=" + dataInicio +
                ", dataTerminoPrevista=" + dataTerminoPrevista +
                ", gerenteResponsavel=" + (gerenteResponsavel != null ? gerenteResponsavel.getLogin() : "null") +
                '}';
    }
}
