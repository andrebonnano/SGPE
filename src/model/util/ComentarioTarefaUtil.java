package model.util;

import model.dominio.ComentarioTarefa;
import model.dominio.Tarefa;
import model.dominio.Usuario;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilitários para operar coleções de ComentarioTarefa.
 * Inclui criação rápida, filtros, ordenação e exportação CSV.
 */
public final class ComentarioTarefaUtil {
    private ComentarioTarefaUtil() {}

    /** Cria um comentário com data/hora atual. */
    public static ComentarioTarefa comentarAgora(Tarefa tarefa, Usuario autor, String mensagem) {
        return ComentarioTarefa.criar(tarefa, autor, LocalDateTime.now(), mensagem);
    }

    /** Cria um comentário com data/hora informada. */
    public static ComentarioTarefa comentarEm(Tarefa tarefa, Usuario autor,
                                              LocalDateTime quando, String mensagem) {
        return ComentarioTarefa.criar(tarefa, autor, quando, mensagem);
    }

    /** Lista comentários de uma tarefa, ordenados por data/hora ascendente. */
    public static List<ComentarioTarefa> listarPorTarefa(Collection<ComentarioTarefa> comentarios, Tarefa tarefa) {
        if (comentarios == null || tarefa == null) return Collections.emptyList();
        return comentarios.stream()
                .filter(c -> tarefa.equals(c.getTarefa()))
                .sorted(Comparator.comparing(ComentarioTarefa::getDataHora))
                .collect(Collectors.toList());
    }

    /** Lista comentários de um autor, ordenados por data/hora ascendente. */
    public static List<ComentarioTarefa> listarPorAutor(Collection<ComentarioTarefa> comentarios, Usuario autor) {
        if (comentarios == null || autor == null) return Collections.emptyList();
        return comentarios.stream()
                .filter(c -> autor.equals(c.getAutor()))
                .sorted(Comparator.comparing(ComentarioTarefa::getDataHora))
                .collect(Collectors.toList());
    }

    /** Filtra comentários por período [inicio, fim] (inclusive). 'fim' pode ser null. */
    public static List<ComentarioTarefa> filtrarPeriodo(Collection<ComentarioTarefa> comentarios,
                                                        LocalDateTime inicio, LocalDateTime fim) {
        if (comentarios == null) return Collections.emptyList();
        Objects.requireNonNull(inicio, "inicio não pode ser nulo");
        return comentarios.stream()
                .filter(c -> !c.getDataHora().isBefore(inicio))
                .filter(c -> fim == null || !c.getDataHora().isAfter(fim))
                .sorted(Comparator.comparing(ComentarioTarefa::getDataHora))
                .collect(Collectors.toList());
    }

    /** Retorna os N comentários mais recentes. */
    public static List<ComentarioTarefa> ultimosN(Collection<ComentarioTarefa> comentarios, int n) {
        if (comentarios == null || n <= 0) return Collections.emptyList();
        return comentarios.stream()
                .sorted(Comparator.comparing(ComentarioTarefa::getDataHora).reversed())
                .limit(n)
                .sorted(Comparator.comparing(ComentarioTarefa::getDataHora)) // mantém ordem cronológica na saída
                .collect(Collectors.toList());
    }

    /** Exporta para CSV (header + linhas). */
    public static String toCSV(Collection<ComentarioTarefa> comentarios) {
        String header = "id;dataHora;autor;task;mensagem";
        if (comentarios == null || comentarios.isEmpty()) return header;
        return header + comentarios.stream().map(c -> String.join(";",
                c.getId(),
                c.getDataHora().toString(),
                safe(c.getAutor().getLogin()),
                safe(c.getTarefa().getTitulo()),
                safe(c.getMensagem()).replace(";", ",").replace("\n", " ")
        )).collect(Collectors.joining("\n", "\n", ""));
    }

    private static String safe(String v) { return v == null ? "" : v; }
}
