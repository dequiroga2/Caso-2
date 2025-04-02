import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Opcion1 {

    public static void opcion1Referencias(String archivo, int paginaSize) throws IOException {
        File file = new File(archivo);
        if (!file.exists()) {
            System.out.println("El archivo no existe.");
            return;
        }

        Imagen imagen = new Imagen(archivo);
        int alto = imagen.alto;
        int ancho = imagen.ancho;

        int imagenSize = alto * ancho * 3;
        int filtroXSize = 3 * 3 * 4;
        int filtroYSize = 3 * 3 * 4;
        int imagenSalidaSize = alto * ancho * 3;


        long baseImagen = 0;
        long baseFiltroX = baseImagen + imagenSize;
        long baseFiltroY = baseFiltroX + filtroXSize;
        long baseRespuesta = baseFiltroY + filtroYSize;

        List<String> referencias = new ArrayList<>();

        // Simular accesos del applySobel
        for (int i = 1; i < alto - 1; i++) {
            for (int j = 1; j < ancho - 1; j++) {
                // Vecindario 3x3 alrededor de (i,j)
                for (int ki = -1; ki <= 1; ki++) {
                    for (int kj = -1; kj <= 1; kj++) {
                        int fila = i + ki;
                        int columna = j + kj;

                        for (int canal = 0; canal < 3; canal++) {
                            long offsetImagen = (fila * ancho + columna) * 3 + canal;
                            long direccionImagen = baseImagen + offsetImagen;
                            int pagina = (int) (direccionImagen / paginaSize);
                            int desplazamiento = (int) (direccionImagen % paginaSize);
                            String componente = (canal == 0) ? "r" : (canal == 1) ? "g" : "b";
                            referencias.add(String.format("Imagen[%d][%d].%s,%d,%d,R", fila, columna, componente, pagina, desplazamiento));
                        }

                        int x = ki + 1;
                        int y = kj + 1;
                        for (int canal = 0; canal < 3; canal++) {
                            long offsetFiltroX = (x * 3 + y) * 4;
                            long direccionFiltroX = baseFiltroX + offsetFiltroX;
                            int paginaFiltroX = (int) (direccionFiltroX / paginaSize);
                            int desplazamientoFiltroX = (int) (direccionFiltroX % paginaSize);
                            referencias.add(String.format("SOBEL_X[%d][%d],%d,%d,R", x, y, paginaFiltroX, desplazamientoFiltroX));
                        }

                        for (int canal = 0; canal < 3; canal++) {
                            long offsetFiltroY = (x * 3 + y) * 4;
                            long direccionFiltroY = baseFiltroY + offsetFiltroY;
                            int paginaFY = (int) (direccionFiltroY / paginaSize);
                            int desplazamientoFiltroY = (int) (direccionFiltroY % paginaSize);
                            referencias.add(String.format("SOBEL_Y[%d][%d],%d,%d,R", x, y, paginaFY, desplazamientoFiltroY));
                        }
                    }
                }

                for (int canal = 0; canal < 3; canal++) {
                    long offsetRta = (i * ancho + j) * 3 + canal;
                    long direccionRta = baseRespuesta + offsetRta;
                    int paginaRta = (int) (direccionRta / paginaSize);
                    int desplazamientoRta = (int) (direccionRta % paginaSize);
                    String componente = (canal == 0) ? "r" : (canal == 1) ? "g" : "b";
                    referencias.add(String.format("Rta[%d][%d].%s,%d,%d,W", i, j, componente, paginaRta, desplazamientoRta));
                }
            }
        }

        int totalSize = imagenSize + filtroXSize + filtroYSize + imagenSalidaSize;
        int np = (totalSize + paginaSize - 1) / paginaSize;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("referencias.txt"))) {
            writer.write("TP=" + paginaSize + "\n");
            writer.write("NF=" + alto + "\n");
            writer.write("NC=" + ancho + "\n");
            writer.write("NR=" + referencias.size() + "\n");
            writer.write("NP=" + np + "\n");
            for (String ref : referencias) {
                writer.write(ref + "\n");
            }
        }

        System.out.println("Archivo de referencias generado correctamente.");
    }
}