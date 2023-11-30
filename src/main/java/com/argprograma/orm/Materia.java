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
import lombok.Getter;
import lombok.Setter;
/**
 *
 * @author mcasatti
 */

@DatabaseTable(tableName="materias")
public class Materia {
    @DatabaseField(canBeNull=false)
    @Getter @Setter private String Nombre;
    @DatabaseField(id=true)
    @Getter @Setter private String Codigo;
    @DatabaseField(columnName="promedio_promocion")
    @Getter @Setter private int PromedioPromocion;
    
    JdbcPooledConnectionSource connSrc;
    Dao<Materia, Long> materiasDao;
    
    public Materia() throws SQLException{
        // Crear el connectionSource y los Dao que necesite
        this.connSrc = new JdbcPooledConnectionSource("jdbc:sqlite:academico.sqlite");
        this.materiasDao = DaoManager.createDao(connSrc, Materia.class);
    };
    
    public Materia (String nombre, String codigo, int promedioPromocion) throws SQLException {
        this.Nombre = nombre;
        this.Codigo = codigo;
        this.PromedioPromocion = promedioPromocion;
        // Crear el connectionSource y los Dao que necesite
        this.connSrc = new JdbcPooledConnectionSource("jdbc:sqlite:academico.sqlite");
        this.materiasDao = DaoManager.createDao(connSrc, Materia.class);
    }
    
    public int cantidad_alumnos() throws SQLException {
        // Crear un Dao de notas
        Dao<Nota,Long> notasDao = DaoManager.createDao(connSrc, Nota.class);

        ArrayList<Nota> notas = (ArrayList<Nota>) notasDao.queryBuilder()
                .where()
                .eq("Materia_id",this.Codigo)
                .query(); 
        
        HashSet<String> legajos_unicos = new HashSet<String>();        
        for (Nota n : notas) {
            legajos_unicos.add(n.getAlumno().getLegajo());
        }
        return legajos_unicos.size();
    }
    
    public int cantidad_promocionados() throws SQLException {
        // Crear un Dao de notas
        Dao<Nota,Long> notasDao = DaoManager.createDao(connSrc, Nota.class);
        // Crear un Dao de 
        ArrayList<Nota> notas = (ArrayList<Nota>) notasDao.queryBuilder()
                .where()
                .eq("Materia_id",this.Codigo)
                .query(); 
        HashSet<String> legajos_unicos = new HashSet<String>();        
        for (Nota n : notas) {
            legajos_unicos.add(n.getAlumno().getLegajo());
        }
        
        int cantidad = 0;
        
        for (String legajo : legajos_unicos) {
            Alumno a = new Alumno();
            a.setLegajo(legajo);
            if (a.promedio_materia(this) >= this.getPromedioPromocion())
                cantidad++;
        }
        
        return cantidad;
    }
    
    public float proporcion_promocionados() throws SQLException {
        return ((float)cantidad_promocionados()/(float)cantidad_alumnos());
    }
    
    @Override
    public String toString() {
        return String.format("Materia: %s [%s] ",
                this.getNombre(), 
                this.getCodigo()
        );
    }
}
