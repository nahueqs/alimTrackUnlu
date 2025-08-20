package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "fila_tabla",
uniqueConstraints = @UniqueConstraint(columnNames = {"id_tabla", "orden"}))

public class FilaTablaModel {
    @Id
    @Column(name = "id_fila", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_tabla", nullable = false)
    @JsonIgnoreProperties("filas")
    private TablaModel tabla;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @ColumnDefault("0")
    @Column(name = "orden")
    private Integer orden;

}