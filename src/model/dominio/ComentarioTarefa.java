package model.dominio;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um comentário feito em uma Tarefa.
 * Invariantes:
 *  - tarefa != null, autor != null
 *  - mensagem obrigatória (tamanho máximo configurável)
 *  - dataHora != null
 */
public final class ComentarioTarefa {
    public static final int TAMANHO_MAX_MENSAGEM = 1000;

    private final String id; // UUID
    private final Tarefa tarefa;
    private final Usuario autor;
    private final LocalDateTime dataHora;
    private final String mensagem;

    private ComentarioTarefa(String id, Tarefa tarefa, Usuario autor,
                             LocalDateTime dataHora, String mensagem) {
        this.id = id;
        this.tarefa = tarefa;
        this.autor = autor;
        this.dataHora = dataHora;
        this.mensagem = mensagem;
    }

    /** Fábrica com validações. */
    public static ComentarioTarefa criar(Tarefa tarefa, Usuario autor,
                                         LocalDateTime dataHora, String mensagem) {
        Objects.requireNonNull(tarefa, "tarefa não pode ser nula");
        Objects.requireNonNull(autor, "autor não pode ser nulo");
        Objects.requireNonNull(dataHora, "dataHora não pode ser nula");
        validarMensagem(mensagem);
        return new ComentarioTarefa(
                UUID.randomUUID().toString(),
                tarefa, autor, dataHora,
                mensagem.trim()
        );
    }

    private static void validarMensagem(String msg) {
        if (msg == null || msg.trim().isEmpty())
            throw new IllegalArgumentException("mensagem é obrigatória.");
        if (msg.trim().length() > TAMANHO_MAX_MENSAGEM)
            throw new IllegalArgumentException("mensagem excede " + TAMANHO_MAX_MENSAGEM + " caracteres.");
    }

    // Getters (imutável)
    public String getId() { return id; }
    public Tarefa getTarefa() { return tarefa; }
    public Usuario getAutor() { return autor; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getMensagem() { return mensagem; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComentarioTarefa)) return false;
        return Objects.equals(id, ((ComentarioTarefa) o).id);
    }
    @Override public int hashCode() { return Objects.hash(id); }

    @Override public String toString() {
        String resumo = mensagem.length() > 40 ? mensagem.substring(0, 40) + "…" : mensagem;
        return "ComentarioTarefa{" +
                "id='" + id + '\'' +
                ", tarefa='" + tarefa.getTitulo() + '\'' +
                ", autor='" + autor.getLogin() + '\'' +
                ", dataHora=" + dataHora +
                ", mensagem='" + resumo + '\'' +
                '}';
    }
}
