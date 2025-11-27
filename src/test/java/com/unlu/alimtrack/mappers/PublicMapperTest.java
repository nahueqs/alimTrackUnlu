package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.ProduccionMetadataPublicaResponseDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicMapperTest {

    private final PublicMapper publicMapper = Mappers.getMapper(PublicMapper.class);

    @Test
    void shouldMapProduccionResponseDTOMetadataProduccionToPublicDTOAndOmitSensitiveFields() {
        // Arrange
        ProduccionMetadataResponseDTO fullDto = new ProduccionMetadataResponseDTO(
                "PROD-001",
                "V1",
                "test_encargado",
                "test_email@example.com",
                "LOTE-A",
                "EN_PROCESO",
                LocalDateTime.now(),
                null,
                "Observaciones"
        );

        // Act
        ProduccionMetadataPublicaResponseDTO publicDto = publicMapper.metadataProduccionToPublicDTO(fullDto);

        // Assert
        assertThat(publicDto.codigoProduccion()).isEqualTo(fullDto.codigoProduccion());
        assertThat(publicDto.lote()).isEqualTo(fullDto.lote());
        assertThat(publicDto.estado()).isEqualTo(fullDto.estado());
        assertThat(publicDto.fechaInicio()).isEqualTo(fullDto.fechaInicio());

        // Verificar que los campos sensibles no están en el DTO público
        // Esto se comprueba por la ausencia de los getters en el record.
        // Este test sirve como documentación de esa expectativa.
        assertThat(publicDto.getClass().getDeclaredFields()).allSatisfy(field ->
                assertThat(field.getName()).isNotIn("encargado", "emailCreador", "codigoVersion", "observaciones")
        );
    }


}
