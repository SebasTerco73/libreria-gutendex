package com.desafio.libreria.principal;

import com.desafio.libreria.model.Autor;
import com.desafio.libreria.model.DatosLibro;
import com.desafio.libreria.model.Libro;
import com.desafio.libreria.model.Resultado;
import com.desafio.libreria.repository.AutorRepository;
import com.desafio.libreria.repository.LibroRepository;
import com.desafio.libreria.service.ConsumoAPI;
import com.desafio.libreria.service.ConvierteDatos;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class Principal {
    private final Scanner leer = new Scanner(System.in);
    private final ConsumoAPI consumoApi = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final LibroRepository repositoryLibro;
    private final AutorRepository repositoryAutor;
    private List<Libro> libros;
    private List<Autor> autor;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.repositoryLibro = libroRepository;
        this.repositoryAutor = autorRepository;
    }

    public void menuPrincipal() {
        int opcion;
        do {
            System.out.println("* * * * * *  L I B R E R I A  * * * * *");
            System.out.println("""
                    1) Agregar un nuevo libro
                    2) Ver todos los libros de la libreria
                    3) Buscar por titulo
                    4) Buscar por idioma
                    5) Autores registrados
                    6) Buscar autor por nombre
                    7) Autores vivos por año
                    8) Libros mas descargados
                    0) SALIR""");
            try {
                opcion = leer.nextInt();
            } catch (InputMismatchException e) {
                opcion = 9;
            } finally {
                leer.nextLine();
            }

            switch (opcion) {
                case 1:
                    buscarYguardarLibro();
                    break;

                case 2:
                    verLibreria();
                    break;

                case 3:
                    buscarPorTitulo();
                    break;

                case 4:
                    buscarPorIdioma();
                    break;

                case 5:
                    autoresRegistrados();
                    break;

                case 6:
                    buscarPorAutor();
                    break;

                case 7:
                    autoresVivosEnDeterminadoAnio();
                    break;

                case 8:
                    top5Descargas();
                    break;

                case 0:
                    System.out.println("Finalizando........");
                    break;

                default:
                    System.out.println("Opcion incorrecta");
            }

        } while (opcion != 0);
    }

    @Transactional
    private void buscarYguardarLibro() {
        Libro libro1;
        DatosLibro dl = buscarLibroAPI()
                .orElse(null);
        if (dl != null) {
            Optional<Autor> autor = dl.autor().stream()
                    .findFirst()
                    .map(e -> new Autor(e.nombre(),e.fechaNac(),e.fechaMuerte()));
            Optional<Autor> autor1 = repositoryAutor.findByAutor(autor.get().getNombre());
            libro1 = autor1.map(value -> new Libro(dl.titulo(), value, dl.idioma().get(0), dl.descargas())).orElseGet(() -> new Libro(dl));
            repositoryLibro.save(libro1);
            System.out.println("***** Guardado *****");
            System.out.println(libro1.getTitulo());
        } else {
            System.out.println("No se encontró ningún libro con ese nombre");
        }
    }

    private Optional<DatosLibro> buscarLibroAPI() {
        String URL_BASE = "https://gutendex.com/books/?search=";
        System.out.println("Ingrese el nombre del libro que desea agregar");
        String nombre = leer.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombre.replace(" ","%20"));
        Resultado resultado = conversor.obtenerDatos(json, Resultado.class);
        return resultado.resultado().stream()
                .filter(l -> l.titulo().toLowerCase().contains(nombre.toLowerCase()))
                .findFirst();
    }

    private void verLibreria() {
        libros = repositoryLibro.findAll();
        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(l -> System.out.println(l.getTitulo()));
    }

    private void buscarPorTitulo() {
        System.out.println("Ingrese el titulo del libro");
        String titulo = leer.nextLine();
        libros = repositoryLibro.findByTituloContainingIgnoreCase(titulo);
        if (!libros.isEmpty()) {
            System.out.println("***** Titulo Encontrado *****");
            libros.forEach(System.out::println);
        } else System.out.println("El libro " + titulo + " no esta en la libreria");
    }

    private void buscarPorIdioma() {
        System.out.println("Seleccione el idioma");
        System.out.println("es- Español");
        System.out.println("en- Ingles");
        System.out.println("pt- Portugues");
        String idioma = leer.nextLine().toLowerCase();
        switch (idioma) {
            case "es", "español":
                libros = repositoryLibro.findByIdiomaContainingIgnoreCase("es");
                if (!libros.isEmpty()) {
                    System.out.println("*** LIBROS EN ESPAÑOL ***");
                    libros.forEach(libro -> System.out.println(libro.getTitulo()));
                } else System.out.println("No hay libros en Español");
                break;

            case "en", "ingles":
                libros = repositoryLibro.findByIdiomaContainingIgnoreCase("en");
                if (!libros.isEmpty()) {
                    System.out.println("*** LIBROS EN INGLES ***");
                    libros.forEach(libro -> System.out.println(libro.getTitulo()));
                } else System.out.println("No hay libros en Ingles");
                break;

            case "pt", "portugues":
                libros = repositoryLibro.findByIdiomaContainingIgnoreCase("pt");
                if (!libros.isEmpty()) {
                    libros.forEach(libro -> System.out.println(libro.getTitulo()));
                } else System.out.println("No hay libros en Portugues");
                break;

            default:
                System.out.println("Idioma no identificado");
        }
    }

    private void autoresRegistrados() {
        autor = repositoryAutor.findAll();
        autor.forEach(autor -> System.out.println(autor.getNombre()));
    }

    private void buscarPorAutor() {
        System.out.println("Ingrese el nombre del autor");
        String nombre = leer.nextLine();
        autor = repositoryAutor.findByNombreContainingIgnoreCase(nombre);
        if (!autor.isEmpty()) {
            System.out.println("***** Autor Encontrado *****");
            autor.forEach(System.out::println);
        } else {
            System.out.println("El autor " + nombre + " no tiene libros cargados");
        }
    }

    private void autoresVivosEnDeterminadoAnio() {
        System.out.println("Ingrese el año!!");
        try {
            int anio = leer.nextInt();
            leer.nextLine();
            autor = repositoryAutor.buscarPorAnio(anio);
            if (!autor.isEmpty()) {
                System.out.println("Autores vivos en el año: " + anio);
                autor.forEach(a -> System.out.println(a.getNombre()));
            }else {
                System.out.println("No hay autores registrados en el año: "+anio);
            }
        } catch (InputMismatchException e) {
            System.out.println("Año ingresado incorrectamente");
            leer.nextLine();
            this.autoresVivosEnDeterminadoAnio();
        }
    }

    private void top5Descargas() {
        libros = repositoryLibro.findTop5ByOrderByDescargasDesc();
        libros.forEach(libro -> System.out.println("Libro: "+libro.getTitulo()+"\nDescargas: "+ libro.getDescargas()));
    }


}


