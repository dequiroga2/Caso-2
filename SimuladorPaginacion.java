import java.util.*;
import java.io.*;
public class SimuladorPaginacion {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Seleccione una opción:");
            System.out.println("1. Generar referencias");
            System.out.println("2. Simular paginación");
            System.out.println("3. Salir");
            int opcion = scanner.nextInt();
            
            switch (opcion) {
                case 1:
                    System.out.println("Ingrese el nombre del archivo BMP:");
                    String nombreArchivo = scanner.next();
                    System.out.println("Ingrese el tamaño de página en bytes:");
                    int tamPagina = scanner.nextInt();
                    GeneradorReferencias.generarReferencias(nombreArchivo, tamPagina);
                    break;
                case 2:
                    System.out.println("Ingrese el número de marcos de página:");
                    int numMarcos = scanner.nextInt();
                    SimulacionPaginacion simulacion = new SimulacionPaginacion(numMarcos, "referencias.txt");
                    simulacion.ejecutar();
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }
}