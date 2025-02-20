package model;

public abstract class Utente {
    protected String email;
    protected String password;
    protected String nome;
    protected String telefono;

    public Utente(String email, String password, String nome, String telefono) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.telefono = telefono;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getNome() { return nome; }
    public String getTelefono() { return telefono; }

    public void setPassword(String password) { this.password = password; }
    public void setNome(String nome) { this.nome = nome; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return nome + " (" + getClass().getSimpleName() + ")";
    }
}
