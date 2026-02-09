package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "columna_tabla",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_tabla", "orden"}))
public class ColumnaTablaModel {

    @Id
    @Column(name = "id_columna", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_tabla", nullable = false)
    @JsonIgnoreProperties("columnas")
    private TablaModel tabla;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dato", nullable = false, length = 50)
    private TipoDatoCampo tipoDato;

    @ColumnDefault("0")
    @Column(name = "orden", nullable = false)
    private Integer orden;

}
