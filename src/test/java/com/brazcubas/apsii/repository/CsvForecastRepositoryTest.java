package com.brazcubas.apsii.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.core.io.DefaultResourceLoader;

import com.brazcubas.apsii.model.Forecast;
import com.brazcubas.apsii.model.PredictionModel;

class CsvForecastRepositoryTest {

    private CsvForecastRepository repository;

    @BeforeEach
    void setUp() {
        // 1. Dublê de ModelRepository com Mockito devolvendo model-a e model-b
        ModelRepository modelRepository = mock(ModelRepository.class);

        PredictionModel modelA = new PredictionModel(
                "model-a", "Modelo A", "Descrição A", "Grupo", "Teste", "1.0", true, null);
        PredictionModel modelB = new PredictionModel(
                "model-b", "Modelo B", "Descrição B", "Grupo", "Teste", "1.0", true, null);

        when(modelRepository.findAll()).thenReturn(List.of(modelA, modelB));

        // 2. Criação do repositório apontando para o caminho exigido
        repository = new CsvForecastRepository(
                new DefaultResourceLoader(),
                modelRepository,
                "classpath:data-test/predictions-11-2.csv"
        );

        // 3. Chamada de load()
        repository.load();
    }

    @Test
    void deveProtegerCampoOpcionalDeConfiancaEFiltrarLinhasInvalidas() {
        // Asserts sobre findAll():
        // O CSV tem 9 linhas:
        // - 4 válidas do model-a (mock-a-1 a mock-a-4)
        // - 3 inválidas descartadas do model-a (1.5, abc, -0.1)
        // - 1 válida do model-b (mock-b-1)
        // - 1 descartada por modelo não cadastrado (model-zz)
        // Total esperado no repositório = 5
        List<Forecast> todos = repository.findAll();
        assertEquals(5, todos.size(), "Deve conter apenas as 5 linhas válidas no repositório");

        // Asserts sobre findByModelAndProbe("model-a", 900001)
        List<Forecast> timeline = repository.findByModelAndProbe("model-a", 900001);
        assertEquals(4, timeline.size(), "A timeline deve conter apenas os 4 registros válidos do model-a");

        // Cubram as decisões do campo:
        // mock-a-1: valor 0.82
        assertEquals(0.82, timeline.get(0).modelConfidence(), 0.0001);

        // mock-a-2: em branco carrega null
        assertNull(timeline.get(1).modelConfidence(), "Confiança em branco deve ser carregada como null");

        // mock-a-3: borda 0 aceita
        assertEquals(0.0, timeline.get(2).modelConfidence(), 0.0001);

        // mock-a-4: borda 1 aceita
        assertEquals(1.0, timeline.get(3).modelConfidence(), 0.0001);
    }
}