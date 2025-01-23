package quickbyte;

public class Utente {
    private String email;
    private String nome;
    private String telefono;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public void registrazione() {}
    public void login() {}
    public void logout() {}
    public void modificaProfilo() {}
}