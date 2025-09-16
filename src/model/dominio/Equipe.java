package model.dominio;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Entidade de domínio que representa uma Equipe.
 * Responsável por manter membros e metadados (nome/descrição).
 */
public final class Equipe {

    private final String id;       // UUID
    private String nome;
    private String descricao;
    private final List<Usuario> membros; // mantém ordem de inserção, sem duplicatas

    private Equipe(String id, String nome, String descricao, List<Usuario> membros) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = membros;
    }

    /** Fábrica com validações básicas. */
    public static Equipe criar(String nome, String descricao) {
        validarObrigatorio(nome, "nome");
        String id = UUID.randomUUID().toString();
        return new Equipe(id, nome.trim(), textoOuVazio(descricao), new ArrayList<>());
    }

    // ----------------- Métodos de domínio -----------------

    /** Altera o nome da equipe. */
    public void alterarNome(String novoNome) {
        validarObrigatorio(novoNome, "nome");
        this.nome = novoNome.trim();
    }

    /** Altera a descrição. */
    public void alterarDescricao(String novaDescricao) {
        this.descricao = textoOuVazio(novaDescricao);
    }

    /** Adiciona um membro se ainda não estiver presente (considera equals/hash de Usuario). */
    public void adicionarMembro(Usuario usuario) {
        Objects.requireNonNull(usuario, "usuario não pode ser nulo");
        if (!membros.contains(usuario)) {
            membros.add(usuario);
        } else {
            throw new IllegalStateException("Usuário já é membro da equipe: " + usuario.getLogin());
        }
    }

    /** Remove um membro (por objeto). Retorna true se removeu. */
    public boolean removerMembro(Usuario usuario) {
        Objects.requireNonNull(usuario, "usuario não pode ser nulo");
        return membros.remove(usuario);
    }

    /** Remove um membro por ID. Retorna true se removeu. */
    public boolean removerMembroPorId(String usuarioId) {
        validarObrigatorio(usuarioId, "usuarioId");
        return membros.removeIf(u -> usuarioId.equals(u.getId()));
    }

    /** Verifica a participação de um usuário. */
    public boolean contemMembro(Usuario usuario) {
        Objects.requireNonNull(usuario, "usuario não pode ser nulo");
        return membros.contains(usuario);
    }

    /** Quantidade de membros. */
    public int quantidadeMembros() {
        return membros.size();
    }

    /** Retorna lista imutável de membros (view de leitura). */
    public List<Usuario> getMembros() {
        return Collections.unmodifiableList(membros);
    }

    /** Obtém logins dos membros (conveniência para relatórios). */
    public List<String> getLoginsDosMembros() {
        return membros.stream().map(Usuario::getLogin).collect(Collectors.toList());
    }

    // ----------------- Getters -----------------

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }

    // ----------------- Validações internas -----------------

    private static void validarObrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não informado: " + campo);
        }
    }

    private static String textoOuVazio(String v) {
        return (v == null) ? "" : v.trim();
    }

    // ----------------- equals/hashCode/toString -----------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipe)) return false;
        Equipe equipe = (Equipe) o;
        return Objects.equals(id, equipe.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Equipe{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", membros=" + membros.size() +
                '}';
    }
}
