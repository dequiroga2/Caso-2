import java.io.*;
import java.util.ArrayList;
import java.util.List;

class GeneradorReferencias {

    public static void generarReferencias(String nombreArchivo, int tamPagina) throws IOException {
        File file = new File(nombreArchivo);
        if (!file.exists()) {
            System.out.println("El archivo no existe.");
            return;
        }

        // Leer dimensiones de la imagen
        Imagen imagen = new Imagen(nombreArchivo);
        int alto = imagen.alto;
        int ancho = imagen.ancho;

        // Calcular tamaños de las matrices (en bytes)
        int imagenSize = alto * ancho * 3;      // BGR (3 bytes por píxel)
        int filtroXSize = 3 * 3 * 4;            // 3x3 enteros (4 bytes cada uno)
        int filtroYSize = 3 * 3 * 4;
        int respuestaSize = alto * ancho * 3;

        // Direcciones base en memoria virtual
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
                        int col = j + kj;

                        // Accesos a B, G, R de la imagen de entrada (3 referencias)
                        for (int canal = 0; canal < 3; canal++) {
                            long offsetImagen = (fila * ancho + col) * 3 + canal;
                            long direccionImagen = baseImagen + offsetImagen;
                            int pagina = (int) (direccionImagen / tamPagina);
                            int desplazamiento = (int) (direccionImagen % tamPagina);
                            String componente = (canal == 0) ? "r" : (canal == 1) ? "g" : "b";
                            referencias.add(String.format(
                                "Imagen[%d][%d].%s,%d,%d,R",
                                fila, col, componente, pagina, desplazamiento
                            ));
                        }

                        // Accesos a SOBEL_X (3 referencias, una por canal R/G/B)
                        int x = ki + 1;
                        int y = kj + 1;
                        for (int canal = 0; canal < 3; canal++) {
                            long offsetFX = (x * 3 + y) * 4;
                            long direccionFX = baseFiltroX + offsetFX;
                            int paginaFX = (int) (direccionFX / tamPagina);
                            int desplFX = (int) (direccionFX % tamPagina);
                            referencias.add(String.format(
                                "SOBEL_X[%d][%d],%d,%d,R",
                                x, y, paginaFX, desplFX
                            ));
                        }

                        // Accesos a SOBEL_Y (3 referencias, una por canal R/G/B)
                        for (int canal = 0; canal < 3; canal++) {
                            long offsetFY = (x * 3 + y) * 4;
                            long direccionFY = baseFiltroY + offsetFY;
                            int paginaFY = (int) (direccionFY / tamPagina);
                            int desplFY = (int) (direccionFY % tamPagina);
                            referencias.add(String.format(
                                "SOBEL_Y[%d][%d],%d,%d,R",
                                x, y, paginaFY, desplFY
                            ));
                        }
                    }
                }

                // Escritura en la respuesta (3 referencias: B, G, R)
                for (int canal = 0; canal < 3; canal++) {
                    long offsetRta = (i * ancho + j) * 3 + canal;
                    long direccionRta = baseRespuesta + offsetRta;
                    int paginaRta = (int) (direccionRta / tamPagina);
                    int desplRta = (int) (direccionRta % tamPagina);
                    String componente = (canal == 0) ? "r" : (canal == 1) ? "g" : "b";
                    referencias.add(String.format(
                        "Rta[%d][%d].%s,%d,%d,W",
                        i, j, componente, paginaRta, desplRta
                    ));
                }
            }
        }

        // Calcular NP (número de páginas virtuales)
        int totalSize = imagenSize + filtroXSize + filtroYSize + respuestaSize;
        int np = (totalSize + tamPagina - 1) / tamPagina;

        // Escribir archivo de referencias
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("referencias.txt"))) {
            writer.write("TP=" + tamPagina + "\n");
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