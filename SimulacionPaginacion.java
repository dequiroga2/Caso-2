import java.util.*;
import java.io.*;
class SimulacionPaginacion {
    private PaginadorNRU paginador;
    private List<Integer> referencias;
    
    public SimulacionPaginacion(int numMarcos, String archivoReferencias) throws IOException {
        this.paginador = new PaginadorNRU(numMarcos);
        this.referencias = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(archivoReferencias));
        String linea;
        while ((linea = reader.readLine()) != null) {
            if (linea.matches("\\d+")) {
                referencias.add(Integer.parseInt(linea));
            }
        }
        reader.close();
    }
    
    public void ejecutar() {
        Thread procesadorReferencias = new Thread(() -> {
            try {
                for (int i = 0; i < referencias.size(); i++) {
                    paginador.procesarReferencia(referencias.get(i));
                    if (i % 10000 == 0) {
                        Thread.sleep(1);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        Thread actualizadorNRU = new Thread(() -> {
            try {
                while (procesadorReferencias.isAlive()) {
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        procesadorReferencias.start();
        actualizadorNRU.start();
        
        try {
            procesadorReferencias.join();
            actualizadorNRU.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Simulación finalizada");
        System.out.println("Total de hits: " + paginador.getHits());
        System.out.println("Total de fallas de página: " + paginador.getFallas());
    }
}