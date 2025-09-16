package model.util;

import model.dominio.RegistroEsforco;
import model.dominio.Tarefa;
import model.dominio.Usuario;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utilitários para operar coleções de RegistroEsforco.
 * Funções de soma, filtro, agregação e exportação simples (CSV).
 */
public final class RegistroEsforcoUtil {
    private RegistroEsforcoUtil() {}

    /** Cria e aplica um registro na tarefa (incrementa esforço real). */
    public static RegistroEsforco registrar(Tarefa tarefa, Usuario usuario,
                                            LocalDate data, int horas, String obs) {
        RegistroEsforco reg = RegistroEsforco.criar(tarefa, usuario, data, horas, obs);
        tarefa.registrarEsforco(horas);
        return reg;
    }

    /** Soma as horas de todos os registros. */
    public static int somarHoras(Collection<RegistroEsforco> regs) {
        if (regs == null) return 0;
        return regs.stream().mapToInt(RegistroEsforco::getHoras).sum();
    }

    /** Soma horas por usuário. */
    public static Map<Usuario, Integer> horasPorUsuario(Collection<RegistroEsforco> regs) {
        if (regs == null) return Collections.emptyMap();
        return regs.stream().collect(Collectors.groupingBy(
                RegistroEsforco::getUsuario,
                Collectors.summingInt(RegistroEsforco::getHoras)
        ));
    }

    /** Soma horas por tarefa. */
    public static Map<Tarefa, Integer> horasPorTarefa(Collection<RegistroEsforco> regs) {
        if (regs == null) return Collections.emptyMap();
        return regs.stream().collect(Collectors.groupingBy(
                RegistroEsforco::getTarefa,
                Collectors.summingInt(RegistroEsforco::getHoras)
        ));
    }

    /** Filtra registros por período [inicio, fim] (inclusive). 'fim' pode ser null. */
    public static List<RegistroEsforco> filtrarPeriodo(Collection<RegistroEsforco> regs,
                                                       LocalDate inicio, LocalDate fim) {
        if (regs == null) return Collections.emptyList();
        Objects.requireNonNull(inicio, "inicio não pode ser nulo");
        return regs.stream()
                .filter(r -> !r.getData().isBefore(inicio))
                .filter(r -> fim == null || !r.getData().isAfter(fim))
                .sorted(Comparator.comparing(RegistroEsforco::getData))
                .collect(Collectors.toList());
    }

    /** Exporta para CSV (header + linhas). */
    public static String toCSV(Collection<RegistroEsforco> regs) {
        String header = "id;data;horas;usuario;task;obs";
        if (regs == null || regs.isEmpty()) return header;
        return header + regs.stream().map(r -> String.join(";",
                r.getId(),
                r.getData().toString(),
                String.valueOf(r.getHoras()),
                safe(r.getUsuario().getLogin()),
                safe(r.getTarefa().getTitulo()),
                safe(r.getObservacao()).replace(";", ",")
        )).collect(Collectors.joining("\n", "\n", ""));
    }

    private static String safe(String v) { return v == null ? "" : v; }
}
