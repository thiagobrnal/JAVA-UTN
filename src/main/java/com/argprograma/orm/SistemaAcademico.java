/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.argprograma.orm;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import static java.lang.Math.random;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 * @author mcasatti
 */
public class SistemaAcademico {
    ArrayList<Curso> Cursos;
    ArrayList<Alumno> Alumnos;
    ArrayList<Materia> Materias;
    ArrayList<Nota> Notas;
    ArrayList<Inscripcion> Inscripciones;
    
    Dao<Alumno,Long> alumnosDao;
    Dao<Curso,Long> cursosDao;
    Dao<Inscripcion,Long> inscripcionesDao;
    Dao<Materia,Long> materiasDao;
    Dao<Nota,Long> notasDao;  
    
    JdbcPooledConnectionSource connectionSource;
    int RANDOM_SEED;
    
    public SistemaAcademico() throws SQLException {
        this.RANDOM_SEED = 117;
        connectionSource = new JdbcPooledConnectionSource("jdbc:sqlite:academico.sqlite");
        ConfigurarTablas();
        ConfigurarDao();
        CargarAlumnos();
        CargarCursos();
        CargarMaterias();
        CargarNotas();
        CargarInscripciones();
    }    
    
    private void ConfigurarTablas () throws SQLException {
        TableUtils.dropTable(connectionSource, Alumno.class, true);
        TableUtils.createTableIfNotExists(connectionSource, Alumno.class);
        TableUtils.dropTable(connectionSource, Curso.class, true);
        TableUtils.createTableIfNotExists(connectionSource, Curso.class);
        TableUtils.dropTable(connectionSource, Inscripcion.class, true);
        TableUtils.createTableIfNotExists(connectionSource, Inscripcion.class);
        TableUtils.dropTable(connectionSource, Materia.class, true);
        TableUtils.createTableIfNotExists(connectionSource, Materia.class);
        TableUtils.dropTable(connectionSource, Nota.class, true);
        TableUtils.createTableIfNotExists(connectionSource, Nota.class);
    }
    
    private void ConfigurarDao() throws SQLException {
        alumnosDao = DaoManager.createDao(connectionSource,Alumno.class);
        cursosDao = DaoManager.createDao(connectionSource,Curso.class);
        inscripcionesDao = DaoManager.createDao(connectionSource,Inscripcion.class);
        materiasDao = DaoManager.createDao(connectionSource,Materia.class);
        notasDao = DaoManager.createDao(connectionSource,Nota.class);       
    }
    
    private void CargarAlumnos() throws SQLException {
        ArrayList<Alumno> tmpAlumnos = new ArrayList<Alumno>();
        tmpAlumnos.add(new Alumno("Martin Casatti","26207"));
        tmpAlumnos.add(new Alumno("Ivan Casatti","26307"));
        tmpAlumnos.add(new Alumno("Analia Guzman","85678"));
        tmpAlumnos.add(new Alumno("Julio Sosa","98645"));
        alumnosDao.create(tmpAlumnos);
    }
    
    private void CargarCursos() throws SQLException {
        ArrayList<Curso> tmpCursos = new ArrayList<Curso>();
        tmpCursos.add(new Curso("1K1",3));
        tmpCursos.add(new Curso("1K2",3));
        tmpCursos.add(new Curso("2K1",1));
        cursosDao.create(tmpCursos);
    }
    
    private void CargarMaterias() throws SQLException {
        ArrayList<Materia> tmpMaterias = new ArrayList<Materia>();
        tmpMaterias.add(new Materia("Programación 1","PRG",6));
        tmpMaterias.add(new Materia("Testing","TST",6));
        tmpMaterias.add(new Materia("Paradigmas","PPR",6));
        materiasDao.create(tmpMaterias);
    }
    
    private void CargarNotas() throws SQLException {
        ArrayList<Alumno> alumnos = (ArrayList<Alumno>) alumnosDao.queryForAll();
        ArrayList<Materia> materias = (ArrayList<Materia>) materiasDao.queryForAll();
        ArrayList<Nota> notas = new ArrayList<Nota>();
        
        Random rn = new Random(this.RANDOM_SEED);
        for (Alumno alumno : alumnos) {
            for (Materia materia : materias) {
                int nota1 = rn.nextInt(7)+3;
                int nota2 = rn.nextInt(7)+3;
                Nota n1 = new Nota(alumno,materia,"PARC1",nota1);
                Nota n2 = new Nota(alumno,materia,"PARC2",nota2);
                notas.add(n1);
                notas.add(n2);
            }
        }
        notasDao.create(notas);
    }
    
    private void CargarInscripciones() throws SQLException {
        
        Curso c1 = new Curso("1K1",3);
        Curso c3 = new Curso("2K1",1);
        
        Alumno a1 = new Alumno("Martin Casatti","26207");
        Alumno a2 = new Alumno("Ivan Casatti","26307");
        Alumno a3 = new Alumno("Analia Guzman","85678");
        
        Inscripcion ins1 = new Inscripcion(c1,a1);
        Inscripcion ins2 = new Inscripcion(c1,a2);
        Inscripcion ins3 = new Inscripcion(c3,a3);
        
        inscripcionesDao.create(ins1);
        inscripcionesDao.create(ins2);
        inscripcionesDao.create(ins3);   
    }
    
    private void MostrarDatosEjemplo() throws SQLException {
        // Ahora vamos a hacer algunas consultas antes de irnos
        // Buscar todos los alumnos
        List<Alumno> alumnos = alumnosDao.queryForAll();
        System.out.println("--- Listado de alumnos ---");
        alumnos.forEach(
                (a) -> System.out.println(a)
        );
        System.out.println("\n");
        
        // Buscar los cursos que terminen con K1
        List<Curso> cursos = cursosDao.queryBuilder()
                .where()
                .like("Codigo", "2%")
                .query();
        System.out.println("--- Cursos ?K1 ---");
        cursos.forEach(
                (c) -> System.out.println(c)
        );
        System.out.println("\n");
        
        cursos = cursosDao.queryForAll();
        for (Curso curso : cursos) {
            System.out.println (
            String.format("El curso %s tiene %d inscriptos y %d plazas disponibles de un total de %d", 
                    curso.getCodigo(),curso.inscriptos(),curso.plazas_disponibles(), curso.getCapacidad()));
        }
        System.out.println("\n");

        ArrayList<Materia> materias = (ArrayList<Materia>) materiasDao.queryForAll();
        for (Materia m : materias) {
            System.out.println (m.getNombre());
        }
        System.out.println("\n");
        
        Alumno a1 = alumnosDao.queryForFirst();
        a1.materias();
        Materia ppr = new Materia();
        ppr.setCodigo("PPR");
        System.out.println(String.format("El promedio general de %s para PPR es %.2f",a1.getNombre(),a1.promedio_materia(ppr)));
        System.out.println(String.format("El promedio general de %s es %.2f\n",a1.getNombre(),a1.promedio_general()));
        
        materias = (ArrayList<Materia>) materiasDao.queryForAll();
        for (Materia m : materias) {
            int cantidad_alumnos = m.cantidad_alumnos();
            System.out.println(String.format("La materia %s tiene %d alumnos con notas",m.getNombre(),cantidad_alumnos));
            System.out.println(String.format("La materia %s tiene %d alumnos promocionados",m.getNombre(),m.cantidad_promocionados()));
            System.out.println(String.format("La materia %s tiene una proporción de %.2f%% alumnos promocionados\n",m.getNombre(),m.proporcion_promocionados()*100));
        }
        System.out.println("\n");
    }
    
    public void cursos_con_vacantes() throws SQLException {
        ArrayList<Curso> cursos = (ArrayList<Curso>) cursosDao.queryForAll();
        System.out.println("### CURSOS ###");
        for (Curso curso : cursos) {
            System.out.println(curso);
        }
        
        // Usamos una funcion lambda sobre la colección para generar otra colección
        // solamente con los cursos cuya cantidad de vacantes sea > 0
        ArrayList<Curso> conVacantes = (ArrayList<Curso>) cursos.stream().filter(
                (c) -> c.plazas_disponibles() > 0).collect(Collectors.toList());
        System.out.println("### CURSOS CON VACANTES ###");
        for (Curso curso : conVacantes) {
            System.out.println(curso);
        }
    }
    
    public void cursos_mas_50_porciento() throws SQLException {
        ArrayList<Curso> cursos = (ArrayList<Curso>) cursosDao.queryForAll();
        System.out.println("### CURSOS ###");
        for (Curso curso : cursos) {
            System.out.println(curso);
        }
        
        // Usamos una funcion lambda sobre la colección para generar otra colección
        // solamente con los cursos cuya cantidad de vacantes sea > 0
        ArrayList<Curso> conVacantes = (ArrayList<Curso>) cursos.stream().filter(
                (c) -> c.plazas_disponibles()/c.getCapacidad() > 0.5).collect(Collectors.toList());
        System.out.println("### CURSOS CON +50% ###");
        for (Curso curso : conVacantes) {
            System.out.println(curso);
        }
    }
    
    public void promedio_general_alumnos() throws SQLException {
        float promedioGeneralAl = 0;
        int c1 = 0;
        ArrayList<Alumno> alumnos = (ArrayList<Alumno>) alumnosDao.queryForAll();
        System.out.println("###Promedio General Alumnos###");
        for(Alumno alumno : alumnos){
            promedioGeneralAl += alumno.promedio_general();
            c1++;
        }
        System.out.printf("El promeedio general de los Alumonos es: %.2f\n", promedioGeneralAl/c1);
    }
    
    public void proporcion_promocionados_materia() throws SQLException{
        ArrayList<Materia> materias = (ArrayList<Materia>) materiasDao.queryForAll();
        System.out.println("###Proporcion de Promocionados por Materia###");
        for(Materia materia : materias){
            System.out.println(String.format("%s: %.0f%%", materia.getNombre(), materia.proporcion_promocionados()*100));
        }
    }
    
    public void alumno_mejor_promedio() throws SQLException{
        ArrayList<Alumno> mayorPromedios = new ArrayList<Alumno>();
        ArrayList<Alumno> alumnos = (ArrayList<Alumno>) alumnosDao.queryForAll();
        
        mayorPromedios.add(alumnos.get(0));
        System.out.println("###Alumno con Mejor Promedio General###");
        
        for(Alumno alumno : alumnos){
            if(alumno.promedio_general()>mayorPromedios.get(0).promedio_general()){
                mayorPromedios.clear();
                mayorPromedios.add(alumno);
            }else if (alumno.promedio_general() == mayorPromedios.get(0).promedio_general() && !mayorPromedios.contains(alumno)){
                mayorPromedios.add(alumno);
            }
        }
        
        if (mayorPromedios.size()>1) {
            System.out.println("Hay varios alumnos con el mismo promedio general:");

            for (Alumno alumno : mayorPromedios) {
                System.out.println(String.format("%s con un promedio de %.2f", alumno.getNombre(), alumno.promedio_general()));
            }
        }else{
            System.out.println(String.format("El alumno %s tiene el mejor promedio general con un %.2f\n", mayorPromedios.get(0).getNombre(), mayorPromedios.get(0).promedio_general()));
        }
        
    }
    
    public void Simular () throws SQLException {
        cursos_con_vacantes();
        System.out.println("\n");
        cursos_mas_50_porciento();  
        System.out.println("\n");
        promedio_general_alumnos();
        System.out.println("\n");
        proporcion_promocionados_materia();
        System.out.println("\n");
        alumno_mejor_promedio();
        System.out.println("\n");
        //MostrarDatosEjemplo();
    }
}
