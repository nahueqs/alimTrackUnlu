package com.unlu.alimtrack.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "columnatabla")
public class ColumnaTablaModel {
    @Id
    @Column(name = "id_columna", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_tabla", nullable = false)
    private TablaModel idTabla;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Lob
    @Column(name = "tipo_dato", nullable = false)
    private String tipoDato;

    @ColumnDefault("0")
    @Column(name = "orden")
    private Integer orden;

}