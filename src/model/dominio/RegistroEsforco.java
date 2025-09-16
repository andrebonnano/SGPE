package model.dominio;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um lançamento de horas em uma Tarefa.
 * Invariantes:
 *  - tarefa != null, usuario != null, data != null
 *  - horas > 0
 *  - data >= tarefa.getDataInicio()
 */
public final class RegistroEsforco {
    private final String id; // UUID
    private final Tarefa tarefa;
    private final Usuario usuario;
    private final LocalDate data;
    private final int horas;
    private final String observacao;

    private RegistroEsforco(String id, Tarefa tarefa, Usuario usuario,
                            LocalDate data, int horas, String observacao) {
        this.id = id;
        this.tarefa = tarefa;
        this.usuario = usuario;
        this.data = data;
        this.horas = horas;
        this.observacao = observacao == null ? "" : observacao.trim();
    }

    public static RegistroEsforco criar(Tarefa tarefa, Usuario usuario,
                                        LocalDate data, int horas, String observacao) {
        Objects.requireNonNull(tarefa, "tarefa não pode ser nula");
        Objects.requireNonNull(usuario, "usuario não pode ser nulo");
        Objects.requireNonNull(data, "data não pode ser nula");
        if (horas <= 0) throw new IllegalArgumentException("horas deve ser > 0");
        if (data.isBefore(tarefa.getDataInicio())) {
            throw new IllegalArgumentException("data do esforço não pode ser anterior ao início da tarefa.");
        }
        return new RegistroEsforco(UUID.randomUUID().toString(), tarefa, usuario, data, horas, observacao);
    }

    // Getters
    public String getId() { return id; }
    public Tarefa getTarefa() { return tarefa; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getData() { return data; }
    public int getHoras() { return horas; }
    public String getObservacao() { return observacao; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistroEsforco)) return false;
        return Objects.equals(id, ((RegistroEsforco) o).id);
    }
    @Override public int hashCode() { return Objects.hash(id); }

    @Override public String toString() {
        return "RegistroEsforco{" +
                "id='" + id + '\'' +
                ", tarefa='" + tarefa.getTitulo() + '\'' +
                ", usuario='" + usuario.getLogin() + '\'' +
                ", data=" + data +
                ", horas=" + horas +
                (observacao.isEmpty() ? "" : ", obs='" + observacao + '\'') +
                '}';
    }
}
