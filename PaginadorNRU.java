import java.util.*;
class PaginadorNRU {
    private int numMarcos;
    private Map<Integer, Pagina> tablaPaginas;
    private Queue<Integer> marcosEnRAM;
    private int hits;
    private int fallas;
    
    public PaginadorNRU(int numMarcos) {
        this.numMarcos = numMarcos;
        this.tablaPaginas = new HashMap<>();
        this.marcosEnRAM = new LinkedList<>();
        this.hits = 0;
        this.fallas = 0;
    }
    
    public synchronized void procesarReferencia(int pagina) {
        if (tablaPaginas.containsKey(pagina)) {
            hits++;
            tablaPaginas.get(pagina).referenciada = true;
        } else {
            fallas++;
            manejarFalloDePagina(pagina);
        }
    }
    
    private void manejarFalloDePagina(int pagina) {
        if (marcosEnRAM.size() >= numMarcos) {
            reemplazarPagina();
        }
        Pagina nuevaPagina = new Pagina(pagina);
        tablaPaginas.put(pagina, nuevaPagina);
        marcosEnRAM.offer(pagina);
    }
    
    private void reemplazarPagina() {
        for (Integer p : marcosEnRAM) {
            Pagina pag = tablaPaginas.get(p);
            if (!pag.referenciada && !pag.modificada) {
                tablaPaginas.remove(p);
                marcosEnRAM.remove(p);
                break;
            }
        }
    }
    
    public synchronized int getHits() {
        return hits;
    }
    
    public synchronized int getFallas() {
        return fallas;
    }
}
