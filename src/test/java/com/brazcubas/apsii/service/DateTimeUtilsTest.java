package com.brazcubas.apsii.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.brazcubas.apsii.config.DateTimeUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilsTest {

    @Test
    @DisplayName("Deve converter string ISO UTC valida para Instant com sucesso (Caminho Feliz / Borda)")
    void parseUtc_ComFormatoIsoValido_DeveRetornarInstant() {
        // Arrange
        String dataEntrada = "2026-10-17T18:00:00.000Z";

        // Act
        Instant resultado = DateTimeUtils.parseUtc(dataEntrada);

        // Assert
        assertNotNull(resultado);
        assertEquals(Instant.parse(dataEntrada), resultado);
    }

    @Test
    @DisplayName("Deve lancar excecao quando a entrada de data for invalida (Caminho Triste)")
    void parseUtc_ComDataInvalida_DeveLancarExcecao() {
        // Arrange
        String dataInvalida = "17/10/2026";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            DateTimeUtils.parseUtc(dataInvalida);
        });
    }
}