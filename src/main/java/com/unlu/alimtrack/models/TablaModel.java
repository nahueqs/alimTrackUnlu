package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tabla")
public class TablaModel {

    @Id
    @Column(name = "id_tabla", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_seccion", nullable = false)
    @JsonIgnoreProperties("tablas")
    private SeccionModel seccion;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @ColumnDefault("0")
    @Column(name = "orden")
    private Integer orden;

    @OneToMany(mappedBy = "tabla", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("tabla")
    private List<ColumnaTablaModel> columnas = new ArrayList<>();

    @OneToMany(mappedBy = "tabla", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("tabla")
    private List<FilaTablaModel> filas = new ArrayList<>();


    public void addColumna(ColumnaTablaModel columna) {
        columnas.add(columna);
        columna.setTabla(this);
    }

    public void addFila(FilaTablaModel fila) {
        filas.add(fila);
        fila.setTabla(this);
    }
}
