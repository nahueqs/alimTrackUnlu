package com.unlu.alimtrack.models;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "respuesta_tabla")
public class RespuestaTablaModel extends RespuestaBaseModel {

    @Id
    @Column(name = "id_respuesta", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_produccion", nullable = false)
    private ProduccionModel produccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_tabla", nullable = false)
    private TablaModel tabla;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_fila", nullable = false)
    private FilaTablaModel fila;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_columna", nullable = false)
    private ColumnaTablaModel columna;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "creado_por", referencedColumnName = "id_usuario")
    private UsuarioModel creadoPor;

    public boolean esRespuestaValida() {
        // Verificar si la respuesta tiene algún valor
        boolean tieneValorTexto = getValorTexto() != null &&
                !getValorTexto().trim().isEmpty();
        boolean tieneValorNumerico = getValorNumerico() != null;
        boolean tieneValorFecha = getValorFecha() != null;

        // La respuesta es válida si tiene al menos un valor
        return tieneValorTexto || tieneValorNumerico || tieneValorFecha;
    }

    // O más específica según tipo de columna
    public boolean esRespuestaValidaParaColumna() {
        if (esRespuestaVacia()) {
            return false;
        }

        if (columna == null || columna.getTipoDato() == null) {
            return false;
        }

        TipoDatoCampo tipo = columna.getTipoDato();
        Object valor = getValor(tipo);

        if (valor == null) {
            return false;
        }

        // Validaciones específicas por tipo
        switch (tipo) {
            case TEXTO:
                return !((String) valor).trim().isEmpty();
            case DECIMAL:
            case ENTERO:
                return ((java.math.BigDecimal) valor) != null;
            case FECHA:
            case HORA:
                return ((java.time.LocalDateTime) valor) != null;
            case BOOLEANO:
                return ((Boolean) valor) != null;
            default:
                return false;
        }
    }
}