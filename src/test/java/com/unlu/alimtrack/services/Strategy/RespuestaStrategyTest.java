package com.unlu.alimtrack.services.Strategy;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RespuestaStrategyTest {

    private ValorNumericoStrategy numericoStrategy;
    private ValorTextoStrategy textoStrategy;
    private ValorFechaStrategy fechaStrategy;
    private ValorBooleanoStrategy booleanoStrategy;
        private ValorHoraStrategy horaStrategy;

    private RespuestaStrategyFactory factory;

    @BeforeEach
    void setUp() {
        numericoStrategy = new ValorNumericoStrategy();
        textoStrategy = new ValorTextoStrategy();
        fechaStrategy = new ValorFechaStrategy();
        booleanoStrategy = new ValorBooleanoStrategy();
        horaStrategy = new ValorHoraStrategy(); // Asumimos que existe aunque no lo leímos, si falla lo mockeamos o quitamos

        factory = new RespuestaStrategyFactory(
                textoStrategy,
                numericoStrategy,
                fechaStrategy,
                booleanoStrategy,
                horaStrategy
        );
    }

    // --- Tests para ValorNumericoStrategy ---

    @Test
    void numericoStrategy_ShouldAssignInteger() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        numericoStrategy.asignarValor(model, 123);
        assertEquals(new BigDecimal("123"), model.getValorNumerico());
        assertNull(model.getValorTexto());
    }

    @Test
    void numericoStrategy_ShouldAssignStringNumber() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        numericoStrategy.asignarValor(model, "45.67");
        assertEquals(new BigDecimal("45.67"), model.getValorNumerico());
    }

    @Test
    void numericoStrategy_ShouldThrowOnInvalidString() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        assertThrows(IllegalArgumentException.class, () -> 
            numericoStrategy.asignarValor(model, "no-numero")
        );
    }

    @Test
    void numericoStrategy_ShouldHandleNull() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        numericoStrategy.asignarValor(model, null);
        assertNull(model.getValorNumerico());
    }

    // --- Tests para ValorTextoStrategy ---

    @Test
    void textoStrategy_ShouldAssignString() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        textoStrategy.asignarValor(model, "Hola Mundo");
        assertEquals("Hola Mundo", model.getValorTexto());
        assertNull(model.getValorNumerico());
    }

    @Test
    void textoStrategy_ShouldConvertNumberToString() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        textoStrategy.asignarValor(model, 123);
        assertEquals("123", model.getValorTexto());
    }

    // --- Tests para ValorFechaStrategy ---

    @Test
    void fechaStrategy_ShouldAssignLocalDateTime() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        LocalDateTime now = LocalDateTime.now();
        fechaStrategy.asignarValor(model, now);
        assertEquals(now, model.getValorFecha());
    }

    @Test
    void fechaStrategy_ShouldParseStringDate() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        String dateStr = "2023-10-05T14:30:00";
        fechaStrategy.asignarValor(model, dateStr);
        assertEquals(LocalDateTime.parse(dateStr), model.getValorFecha());
    }

    @Test
    void fechaStrategy_ShouldThrowOnInvalidDate() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        assertThrows(IllegalArgumentException.class, () -> 
            fechaStrategy.asignarValor(model, "fecha-invalida")
        );
    }

    // --- Tests para ValorBooleanoStrategy ---

    @Test
    void booleanoStrategy_ShouldAssignTrue() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        booleanoStrategy.asignarValor(model, true);
        assertEquals(BigDecimal.ONE, model.getValorNumerico()); // 1 = true
    }

    @Test
    void booleanoStrategy_ShouldAssignFalse() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        booleanoStrategy.asignarValor(model, false);
        assertEquals(BigDecimal.ZERO, model.getValorNumerico()); // 0 = false
    }

    @Test
    void booleanoStrategy_ShouldParseStringTrue() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        booleanoStrategy.asignarValor(model, "true");
        assertEquals(BigDecimal.ONE, model.getValorNumerico());
        
        booleanoStrategy.asignarValor(model, "si");
        assertEquals(BigDecimal.ONE, model.getValorNumerico());
        
        booleanoStrategy.asignarValor(model, "1");
        assertEquals(BigDecimal.ONE, model.getValorNumerico());
    }

    @Test
    void booleanoStrategy_ShouldParseStringFalse() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        booleanoStrategy.asignarValor(model, "false");
        assertEquals(BigDecimal.ZERO, model.getValorNumerico());
        
        booleanoStrategy.asignarValor(model, "no");
        assertEquals(BigDecimal.ZERO, model.getValorNumerico());
        
        booleanoStrategy.asignarValor(model, "0");
        assertEquals(BigDecimal.ZERO, model.getValorNumerico());
    }

    @Test
    void booleanoStrategy_ShouldThrowOnInvalidString() {
        RespuestaCampoModel model = new RespuestaCampoModel();
        assertThrows(IllegalArgumentException.class, () -> 
            booleanoStrategy.asignarValor(model, "quizas")
        );
    }

    // --- Tests para RespuestaStrategyFactory ---

    @Test
    void factory_ShouldReturnCorrectStrategy() {
        assertInstanceOf(ValorTextoStrategy.class, factory.getStrategy(TipoDatoCampo.TEXTO));
        assertInstanceOf(ValorNumericoStrategy.class, factory.getStrategy(TipoDatoCampo.DECIMAL));
        assertInstanceOf(ValorNumericoStrategy.class, factory.getStrategy(TipoDatoCampo.ENTERO));
        assertInstanceOf(ValorFechaStrategy.class, factory.getStrategy(TipoDatoCampo.FECHA));
        assertInstanceOf(ValorBooleanoStrategy.class, factory.getStrategy(TipoDatoCampo.BOOLEANO));
    }

    @Test
    void factory_ShouldThrowOnNullType() {
        assertThrows(IllegalArgumentException.class, () -> 
            factory.getStrategy(null)
        );
    }
}
