package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
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

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @ColumnDefault("0")
    @Column(name = "orden", nullable = false)
    private Integer orden;

    @OneToMany(mappedBy = "tabla", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("tabla")
    @BatchSize(size = 100)
    private Set<ColumnaTablaModel> columnas = new HashSet<>();

    @OneToMany(mappedBy = "tabla", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("tabla")
    @BatchSize(size = 100)
    private Set<FilaTablaModel> filas = new HashSet<>();


    public void addColumna(ColumnaTablaModel columna) {
        columnas.add(columna);
        columna.setTabla(this);
    }

    public void addFila(FilaTablaModel fila) {
        filas.add(fila);
        fila.setTabla(this);
    }
}
