class Pagina {
    int numero;
    boolean referenciada;
    boolean modificada;
    
    public Pagina(int numero) {
        this.numero = numero;
        this.referenciada = false;
        this.modificada = false;
    }
}