package model.dominio;

import model.enums.Perfil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Entidade de domínio que representa um usuário do sistema.
 * Invariantes e validações são aplicadas nos métodos de criação e atualização.
 */
public final class Usuario {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");

    // aceita 11 dígitos ou formato ###.###.###-##
    private static final Pattern CPF_PATTERN =
            Pattern.compile("\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");

    private final String id;              // UUID
    private String nomeCompleto;
    private String cpf;                   // armazenado normalizado (apenas dígitos)
    private String email;
    private String cargo;
    private String login;                 // único
    private String senhaHash;             // SHA-256 simples (didático)
    private Perfil perfil;

    private Usuario(String id,
                    String nomeCompleto,
                    String cpf,
                    String email,
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

    /** Fábrica para criação de usuário com validações e hashing de senha. */
    public static Usuario criar(String nomeCompleto,
                                String cpf,
                                String email,
                                String cargo,
                                String login,
                                String senhaClara,
                                Perfil perfil) {
        validarObrigatorio(nomeCompleto, "nomeCompleto");
        validarCPF(cpf);
        validarEmail(email);
        validarObrigatorio(cargo, "cargo");
        validarLogin(login);
        Objects.requireNonNull(perfil, "perfil não pode ser nulo");
        validarSenhaClara(senhaClara);

        String id = UUID.randomUUID().toString();
        String cpfNormalizado = normalizarCpf(cpf);
        String senhaHash = hashSenha(login, senhaClara);
        return new Usuario(id, nomeCompleto.trim(), cpfNormalizado, email.trim(),
                cargo.trim(), login.trim(), senhaHash, perfil);
    }

    // ---------- Métodos de domínio (intenção) ----------

    public void alterarEmail(String novoEmail) {
        validarEmail(novoEmail);
        this.email = novoEmail.trim();
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
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getCargo() { return cargo; }
    public String getLogin() { return login; }
    public Perfil getPerfil() { return perfil; }

    // ---------- Validações & utilidades ----------

    private static void validarObrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não informado: " + campo);
        }
    }

    private static void validarEmail(String email) {
        validarObrigatorio(email, "email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
    }

    private static void validarCPF(String cpf) {
        validarObrigatorio(cpf, "cpf");
        if (!CPF_PATTERN.matcher(cpf.trim()).matches()) {
            throw new IllegalArgumentException("CPF inválido. Use 11 dígitos ou ###.###.###-##");
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

    private static String normalizarCpf(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    private static String hashSenha(String login, String senhaClara) {
        // Didático: usa login como "sal" simples. Não usar em produção.
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
                ", cpf='***'" +
                ", email='" + email + '\'' +
                ", cargo='" + cargo + '\'' +
                ", login='" + login + '\'' +
                ", perfil=" + perfil +
                '}';
    }
}