// src/model/dominio/Usuario.java
package model.dominio;

import model.enums.Perfil;
import model.vo.CPF;
import model.vo.Email;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio que representa um usuário do sistema.
 * Agora utilizando Value Objects: CPF e Email (imutáveis e validados).
 */
public final class Usuario {

    private final String id;              // UUID
    private String nomeCompleto;
    private CPF cpf;                      // VO (11 dígitos, com DV)
    private Email email;                  // VO (normalizado)
    private String cargo;
    private String login;                 // único (validar unicidade no repositório)
    private String senhaHash;             // SHA-256 simples (didático)
    private Perfil perfil;

    private Usuario(String id,
                    String nomeCompleto,
                    CPF cpf,
                    Email email,
                    String cargo,
                    String login,
                    String senhaHash,
                    Perfil perfil) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.cargo = cargo;
        this.login = login;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
    }

    /** Fábrica com parâmetros String para CPF/Email (converte para VO). */
    public static Usuario criar(String nomeCompleto,
                                String cpfStr,
                                String emailStr,
                                String cargo,
                                String login,
                                String senhaClara,
                                Perfil perfil) {
        validarObrigatorio(nomeCompleto, "nomeCompleto");
        validarObrigatorio(cargo, "cargo");
        validarLogin(login);
        Objects.requireNonNull(perfil, "perfil não pode ser nulo");
        validarSenhaClara(senhaClara);

        CPF cpf = CPF.of(cpfStr);
        Email email = Email.of(emailStr);
        String id = UUID.randomUUID().toString();
        String senhaHash = hashSenha(login, senhaClara);

        return new Usuario(id, nomeCompleto.trim(), cpf, email,
                cargo.trim(), login.trim(), senhaHash, perfil);
    }

    /** Fábrica alternativa recebendo VOs. */
    public static Usuario criar(String nomeCompleto,
                                CPF cpf,
                                Email email,
                                String cargo,
                                String login,
                                String senhaClara,
                                Perfil perfil) {
        validarObrigatorio(nomeCompleto, "nomeCompleto");
        Objects.requireNonNull(cpf, "cpf não pode ser nulo");
        Objects.requireNonNull(email, "email não pode ser nulo");
        validarObrigatorio(cargo, "cargo");
        validarLogin(login);
        Objects.requireNonNull(perfil, "perfil não pode ser nulo");
        validarSenhaClara(senhaClara);

        String id = UUID.randomUUID().toString();
        String senhaHash = hashSenha(login, senhaClara);

        return new Usuario(id, nomeCompleto.trim(), cpf, email,
                cargo.trim(), login.trim(), senhaHash, perfil);
    }

    // ---------- Métodos de domínio (intenção) ----------

    public void alterarEmail(Email novoEmail) {
        this.email = Objects.requireNonNull(novoEmail, "email não pode ser nulo");
    }

    /** Overload que aceita String e converte para VO. */
    public void alterarEmail(String novoEmail) {
        this.alterarEmail(Email.of(novoEmail));
    }

    public void alterarCargo(String novoCargo) {
        validarObrigatorio(novoCargo, "cargo");
        this.cargo = novoCargo.trim();
    }

    public void alterarNome(String novoNome) {
        validarObrigatorio(novoNome, "nomeCompleto");
        this.nomeCompleto = novoNome.trim();
    }

    public void alterarPerfil(Perfil novoPerfil) {
        this.perfil = Objects.requireNonNull(novoPerfil, "perfil não pode ser nulo");
    }

    /** Troca a senha, verificando a senha atual. */
    public void trocarSenha(String senhaAtualClara, String novaSenhaClara) {
        if (!verificarSenha(senhaAtualClara)) {
            throw new IllegalArgumentException("Senha atual inválida.");
        }
        validarSenhaClara(novaSenhaClara);
        this.senhaHash = hashSenha(this.login, novaSenhaClara);
    }

    /** Verifica a senha informada comparando com o hash armazenado. */
    public boolean verificarSenha(String senhaClara) {
        return Objects.equals(this.senhaHash, hashSenha(this.login, senhaClara));
    }

    // ---------- Getters ----------

    public String getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public CPF getCpf() { return cpf; }
    public Email getEmail() { return email; }
    public String getCargo() { return cargo; }
    public String getLogin() { return login; }
    public Perfil getPerfil() { return perfil; }

    // Conveniências de exibição
    public String getCpfFormatado() { return cpf.formatado(); }
    public String getCpfMascarado() { return cpf.mascarado(); }
    public String getEmailValue() { return email.value(); }

    // ---------- Validações & utilidades ----------

    private static void validarObrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não informado: " + campo);
        }
    }

    private static void validarLogin(String login) {
        validarObrigatorio(login, "login");
        if (login.trim().length() < 3) {
            throw new IllegalArgumentException("Login deve ter ao menos 3 caracteres.");
        }
    }

    private static void validarSenhaClara(String senhaClara) {
        validarObrigatorio(senhaClara, "senha");
        if (senhaClara.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter ao menos 6 caracteres.");
        }
    }

    private static String hashSenha(String login, String senhaClara) {
        // Didático: usa login como "sal" simples. Não use em produção.
        String input = login + ":" + senhaClara;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao calcular hash de senha.", e);
        }
    }

    // ---------- equals/hashCode/toString ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        // id e login identificam unicamente o usuário
        return Objects.equals(id, usuario.id) &&
                Objects.equals(login, usuario.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", cpf='" + cpf.mascarado() + '\'' +
                ", email='" + email.value() + '\'' +
                ", cargo='" + cargo + '\'' +
                ", login='" + login + '\'' +
                ", perfil=" + perfil +
                '}';
    }
}
