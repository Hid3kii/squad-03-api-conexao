package com.brazcubas.apsii.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import com.brazcubas.apsii.model.Forecast;
import com.brazcubas.apsii.model.PredictionModel;

class CsvForecastRepositoryTest {

    private CsvForecastRepository repository;

    @BeforeEach
    void setUp() {
        // Objeto real para o Record (sem uso de Mockito para classe final)
        PredictionModel validModel = new PredictionModel(
                "model-a",
                "Modelo Teste",
                "Descrição",
                "Grupo A",
                "RandomForest",
                "1.0",
                true,
                Instant.now()
        );

        // Dublê (Stub) manual de ModelRepository conforme pedido no exercício
        ModelRepository modelRepository = new ModelRepository() {
            @Override
            public List<PredictionModel> findAll() {
                return List.of(validModel);
            }

            @Override
            public Optional<PredictionModel> findById(String modelId) {
                return "model-a".equals(modelId) ? Optional.of(validModel) : Optional.empty();
            }
        };

        // Instancia o repositório apontando para o CSV da fixture
        repository = new CsvForecastRepository(
                new DefaultResourceLoader(),
                modelRepository,
                "classpath:predictions-test.csv"
        );
        repository.load();
    }

    @Test
    void deveCarregarApenasLinhasValidasEDescartarAsInvalidas() {
        List<Forecast> forecasts = repository.findAll();
        // O CSV de teste possui 5 linhas: 2 válidas (confidence = 0.5 e vazia) e 3 inválidas (-0.1, 1.5 e texto)
        assertEquals(2, forecasts.size(), "Deve carregar apenas as 2 linhas válidas e descartar as 3 inválidas");
    }
}