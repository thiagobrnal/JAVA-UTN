/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.argprograma.orm;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

 /*
 * @author mcasatti
 */
@DatabaseTable(tableName="alumnos")
public class Alumno {
    @DatabaseField(canBeNull=false)
    @Getter @Setter private String Nombre;
    
    @DatabaseField(canBeNull=false,id=true,unique=true)
    @Getter @Setter private String Legajo;
    
    JdbcPooledConnectionSource connSrc;
    Dao<Alumno, Long> alumnosDao;
    
    public Alumno () throws SQLException {
        // Crear el connectionSource y los Dao que necesite
        this.connSrc = new JdbcPooledConnectionSource("jdbc:sqlite:academico.sqlite");
        this.alumnosDao = DaoManager.createDao(connSrc, Alumno.class);
    }
    
    public Alumno(String nombre, String legajo) throws SQLException {
        this.Nombre = nombre;
        this.Legajo = legajo;
        // Crear el connectionSource y los Dao que necesite
        this.connSrc = new JdbcPooledConnectionSource("jdbc:sqlite:academico.sqlite");
        this.alumnosDao = DaoManager.createDao(connSrc, Alumno.class);
    }
    
    public ArrayList<Materia> materias() throws SQLException {
        // Crear un Dao de notas
        Dao<Nota,Long> notasDao = DaoManager.createDao(connSrc, Nota.class);
        Dao<Materia,Long> materiasDao = DaoManager.createDao(connSrc, Materia.class);

        ArrayList<Nota> notas = (ArrayList<Nota>) notasDao.queryBuilder()
                .where()
                .eq("Alumno_id",this.Legajo)
                .query();        
        
        HashSet<String> codigos_unicos = new HashSet<String>();        
        for (Nota n : notas) {
            codigos_unicos.add(n.getMateria().getCodigo());
        }
        ArrayList<Materia> materias_unicas = new ArrayList<Materia> ();
        for (String codigo : codigos_unicos) {
            Materia m = new Materia();
            m.setCodigo(codigo);
            materiasDao.refresh(m);
            materias_unicas.add(m);
        }
        return materias_unicas;
    }
    
    public float promedio_materia (Materia materia) throws SQLException {
        Dao<Nota,Long> notasDao = DaoManager.createDao(connSrc, Nota.class);
        ArrayList<Nota> notas = (ArrayList<Nota>) notasDao.queryBuilder()
                .where()
                .eq("Materia_id",materia.getCodigo())
                .and()
                .eq("Alumno_id", this.getLegajo())
                .query();        
        int acumulado = 0;
        int cantidad = notas.size();
        for (Nota n : notas) {
            acumulado += n.getNota();
        }
        
        return acumulado/cantidad;
    }
    
    public float promedio_general () throws SQLException {
        ArrayList<Materia> materias = this.materias();
        float acumulado = 0;
        int cantidad = materias.size();
        for (Materia m : materias) {
            acumulado += this.promedio_materia(m);
        }
        return acumulado/cantidad;
    }
        
    @Override
    public String toString() {
        return String.format("N:%s - L:[%s]",this.Nombre, this.Legajo);
    }
}
