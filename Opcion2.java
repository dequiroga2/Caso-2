import java.util.*;
import java.util.concurrent.locks.*;

public class Opcion2 {
    private final int marcos;
    private final List<String> referencias;
    private final Map<Integer, Integer> tablaPaginas;
    private final Map<Integer, Boolean> bitsR;
    private final Map<Integer, Boolean> bitsM;
    private final Lock lock = new ReentrantLock();
    
    private final Queue<Integer> libres;
    
    private int hits = 0;
    private int misses = 0;
    private long tiempoTotal = 0;
    
    private volatile boolean finished = false;
    
    public Opcion2(int marcos, List<String> referencias) {
        this.marcos = marcos;
        this.referencias = referencias;
        this.tablaPaginas = new LinkedHashMap<>();
        this.bitsR = new HashMap<>();
        this.bitsM = new HashMap<>();
        this.libres = new LinkedList<>();
        for (int i = 0; i < marcos; i++) {
            libres.offer(i);
        }
    }
    
    public void simular() {
        Thread tReferencias = new Thread(this::procesarReferencias);
        Thread tActualizar = new Thread(this::actualizarBitsR);
        
        tReferencias.start();
        tActualizar.start();
        
        try {
            tReferencias.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        finished = true;
        try {
            tActualizar.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void procesarReferencias() {
        int contador = 0;
        for (String ref : referencias) {
            lock.lock();
            try {
                String[] partes = ref.split(",");
                if (partes.length < 4) continue;
                int pagina;

                try {
                    pagina = Integer.parseInt(partes[1].trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                
                String accion = partes[3].trim();
                boolean esEscritura = accion.equalsIgnoreCase("W");
                
                if (tablaPaginas.containsKey(pagina)) {
                    hits++;
                    tiempoTotal += 50;
                    bitsR.put(pagina, true);
                    if (esEscritura) {
                        bitsM.put(pagina, true);
                    }
                } else {
                    misses++;
                    tiempoTotal += 10_000_000; // 10 ms = 10,000,000 ns
                    cargarPagina(pagina, esEscritura);
                }
            } finally {
                lock.unlock();
            }
            contador++;
            
            if (contador % 10000 == 0) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            }
        }
    }
    
    private void actualizarBitsR() {
        while (!finished) {
            try {
                Thread.sleep(1); // Ejecuta cada 1 ms
            } catch (InterruptedException e) {
            }
            
            lock.lock();
            try {
                // Resetea todos los bits R a false
                for (Integer pagina : bitsR.keySet()) {
                    bitsR.put(pagina, false);
                }
            } finally {
                lock.unlock();
            }
        }
    }
    
    private void cargarPagina(int pagina, boolean esEscritura) {
        
        if (libres.isEmpty()) {
            int eliminar = eliminarMarco();
            int marcoEliminar = tablaPaginas.remove(eliminar);
            bitsR.remove(eliminar);
            bitsM.remove(eliminar);
            libres.offer(marcoEliminar);
        }
        
        int marco = libres.poll();
        tablaPaginas.put(pagina, marco);
        bitsR.put(pagina, true);
        bitsM.put(pagina, esEscritura);
    }
    
    private int eliminarMarco() {
        List<Integer> clase0 = new ArrayList<>();
        List<Integer> clase1 = new ArrayList<>();
        List<Integer> clase2 = new ArrayList<>();
        List<Integer> clase3 = new ArrayList<>();
        
        for (Integer p : tablaPaginas.keySet()) {
            boolean r = bitsR.getOrDefault(p, false);
            boolean m = bitsM.getOrDefault(p, false);
            if (!r && !m) {
                clase0.add(p);
            } else if (!r && m) {
                clase1.add(p);
            } else if (r && !m) {
                clase2.add(p);
            } else {
                clase3.add(p);
            }
        }
        
        if (!clase0.isEmpty()) return clase0.get(0);
        if (!clase1.isEmpty()) return clase1.get(0);
        if (!clase2.isEmpty()) return clase2.get(0);
        return clase3.get(0);
    }
    
    public int getHits() {
        return hits;
    }
    
    public int getMisses() {
        return misses;
    }
    
    public long getTiempoTotal() {
        return tiempoTotal;
    }
}