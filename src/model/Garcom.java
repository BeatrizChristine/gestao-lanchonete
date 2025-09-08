package model;

public class Garcom {
    private int id;
    private String nome;
    private String matricula;
    private boolean ativo = true; // Por padrão, todo garçom é ativo

    public Garcom(String nome, String matricula) {
        this.nome = nome;
        this.matricula = matricula;
        this.ativo = true; // Ativo por padrão
    }

    // Construtor completo (caso preciso carregar todos os dados, inclusive ativo)
    public Garcom(int id, String nome, String matricula, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
        this.ativo = ativo;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return nome + " (Mat: " + matricula + ")";
    }
}
