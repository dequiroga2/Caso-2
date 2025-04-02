import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Caso 2");
            System.out.println("1. Opcion 1");
            System.out.println("2. Opcion 2");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            
            int opcion = scanner.nextInt();
            scanner.nextLine();
            
            switch (opcion) {
                case 1:
                    generarReferencias(scanner);
                    break;
                case 2:
                    simularPaginacion(scanner);
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void generarReferencias(Scanner scanner) {
        System.out.print("Ingrese el nombre del archivo BMP: ");
        String nombreArchivo = scanner.nextLine();
        System.out.print("Ingrese el tamaño de página (bytes): ");
        int tamPagina = scanner.nextInt();
        scanner.nextLine();

        try {
            Opcion1.opcion1Referencias(nombreArchivo, tamPagina);
            System.out.println("Archivo 'referencias.txt' generado exitosamente.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void simularPaginacion(Scanner scanner) {
        System.out.print("Ingrese el número de marcos de página: ");
        int marcos = scanner.nextInt();
        scanner.nextLine();

        List<String> referencias = leerReferencias("referencias.txt");
        if (referencias.isEmpty()) {
            System.out.println("El archivo no contiene referencias válidas.");
            return;
        }

        Opcion2 opcion2 = new Opcion2(marcos, referencias);
        opcion2.simular();

        // Mostrar resultados
        System.out.println("Resultado");
        System.out.println("Hits: " + opcion2.getHits());
        System.out.println("Misses: " + opcion2.getMisses());
        System.out.printf("Porcentaje de hits: %.2f%%\n", (opcion2.getHits() * 100.0 / (opcion2.getHits() + opcion2.getMisses())));
        System.out.println("Tiempo total: " + opcion2.getTiempoTotal() + " ns");
    }

    // Leer referencias desde archivo
    private static List<String> leerReferencias(String archivo) {
        List<String> referencias = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("Imagen") || linea.startsWith("SOBEL") || linea.startsWith("Rta")) {
                    referencias.add(linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo: " + e.getMessage());
        }
        return referencias;
    }
}