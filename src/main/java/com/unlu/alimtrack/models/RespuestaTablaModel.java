package com.unlu.alimtrack.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "respuesta_tabla")
public class RespuestaTablaModel {

    @Id
    @Column(name = "id_respuesta", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_produccion", nullable = false)
    private ProduccionModel produccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_tabla", nullable = false)
    private TablaModel idTabla;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_fila", nullable = false)
    private FilaTablaModel fila;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_columna", nullable = false)
    private ColumnaTablaModel columna;

    @ManyToOne(fetch = FetchType.LAZY, optional = true) // Assuming optional = true based on schema
    @OnDelete(action = OnDeleteAction.RESTRICT) // Consistent with other creado_por FKs
    @JoinColumn(name = "creado_por", referencedColumnName = "id_usuario")
    private UsuarioModel creadoPor;

    @Lob
    @Column(name = "valor", columnDefinition = "LONGTEXT")
    private String valor;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    // ✅ Método de validación para garantizar consistencia
    @PrePersist
    @PreUpdate
    private void validarConsistencia() {
        if (fila != null && columna != null) {
            Long tablaFila = fila.getTabla().getId();
            Long tablaColumna = columna.getTabla().getId();

            if (!tablaFila.equals(tablaColumna)) {
                throw new IllegalStateException(
                        "La fila y la columna deben pertenecer a la misma tabla. " +
                                "Fila.Tabla: " + tablaFila + ", Columna.Tabla: " + tablaColumna
                );
            }
        }
    }
}
