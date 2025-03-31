import java.util.*;
import java.io.*;
class GeneradorReferencias {
    public static void generarReferencias(String nombreArchivo, int tamPagina) throws IOException {
        File file = new File(nombreArchivo);
        if (!file.exists()) {
            System.out.println("El archivo no existe.");
            return;
        }
        
        int nf = 300; // Número de filas (se puede leer del BMP)
        int nc = 500; // Número de columnas (se puede leer del BMP)
        int np = (nf * nc + 9 * 4 + nf * nc) / tamPagina; // Cálculo simplificado
        
        List<Integer> referencias = new ArrayList<>();
        
        for (int i = 0; i < nf; i++) {
            for (int j = 0; j < nc; j++) {
                int dirImagen = (i * nc + j) / tamPagina;
                int dirFiltroX = 1;
                int dirFiltroY = 2;
                int dirRespuesta = (nf * nc + 9 * 4) / tamPagina;
                
                referencias.add(dirImagen);
                referencias.add(dirFiltroX);
                referencias.add(dirFiltroY);
                referencias.add(dirRespuesta);
            }
        }
        
        BufferedWriter writer = new BufferedWriter(new FileWriter("referencias.txt"));
        writer.write("TP " + tamPagina + "\n");
        writer.write("NF " + nf + "\n");
        writer.write("NC " + nc + "\n");
        writer.write("NR " + referencias.size() + "\n");
        writer.write("NP " + np + "\n");
        for (int ref : referencias) {
            writer.write(ref + "\n");
        }
        writer.close();
        System.out.println("Referencias generadas en referencias.txt");
    }
}