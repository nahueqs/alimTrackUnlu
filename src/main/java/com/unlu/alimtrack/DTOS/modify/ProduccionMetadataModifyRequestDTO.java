package com.unlu.alimtrack.DTOS.modify;

import org.hibernate.validator.constraints.Length;

public record ProduccionMetadataModifyRequestDTO(

        @Length(max = 100)
        String encargado,

        @Length(max = 100)
        String lote,

        @Length(max = 255)
        String observaciones
) {
}
