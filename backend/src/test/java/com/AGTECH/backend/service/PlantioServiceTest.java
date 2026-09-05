package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.CadastroPlantioRequest;
import com.AGTECH.backend.dtos.PlantioResponse;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Plantio;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Safra;
import com.AGTECH.backend.models.Talhao;
import com.AGTECH.backend.repository.PlantioRepository;
import com.AGTECH.backend.repository.SafraRepository;

@ExtendWith(MockitoExtension.class)
class PlantioServiceTest {

    @Mock
    private PlantioRepository plantioRepository;

    @Mock
    private SafraRepository safraRepository;

    @Mock
    private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks
    private PlantioService plantioService;

    private Safra criarSafraDoTalhao(UUID propriedadeId, UUID talhaoId, UUID safraId) {
        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        lenient().when(propriedade.getId()).thenReturn(propriedadeId);

        Talhao talhao = mock(Talhao.class);
        lenient().when(talhao.getId()).thenReturn(talhaoId);
        lenient().when(talhao.getPropriedade()).thenReturn(propriedade);

        Safra safra = mock(Safra.class);
        lenient().when(safra.getId()).thenReturn(safraId);
        lenient().when(safra.getTalhao()).thenReturn(talhao);
        return safra;
    }

    @Test
    void deveCadastrarPlantioQuandoSafraPertenceAoTalhao() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Safra safra = criarSafraDoTalhao(propriedadeId, talhaoId, safraId);

        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));
        when(plantioRepository.save(any(Plantio.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        CadastroPlantioRequest request = new CadastroPlantioRequest(LocalDate.now(), 5.0, "Plantio de teste");

        PlantioResponse response = plantioService.cadastrar(propriedadeId, talhaoId, safraId, request, usuarioId);

        assertEquals(5.0, response.areaPlantadaHectares());
        assertEquals("Plantio de teste", response.observacoes());
    }

    @Test
    void deveRecusarCadastroQuandoSafraDeOutroTalhao() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Safra safra = criarSafraDoTalhao(propriedadeId, UUID.randomUUID(), safraId);

        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        CadastroPlantioRequest request = new CadastroPlantioRequest(LocalDate.now(), 5.0, null);

        assertThrows(RegraDeNegocioException.class,
            () -> plantioService.cadastrar(propriedadeId, talhaoId, safraId, request, usuarioId));
    }

    @Test
    void deveRecusarCadastroQuandoTalhaoDeOutraPropriedade() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Safra safra = criarSafraDoTalhao(UUID.randomUUID(), talhaoId, safraId);

        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));

        CadastroPlantioRequest request = new CadastroPlantioRequest(LocalDate.now(), 5.0, null);

        assertThrows(RegraDeNegocioException.class,
            () -> plantioService.cadastrar(propriedadeId, talhaoId, safraId, request, usuarioId));
    }

    @Test
    void deveAtualizarPlantioQuandoPertenceASafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();
        UUID plantioId = UUID.randomUUID();

        Safra safra = criarSafraDoTalhao(propriedadeId, talhaoId, safraId);
        Plantio plantio = new Plantio(safra, LocalDate.now().minusDays(5), 5.0, "Original");

        when(plantioRepository.findById(plantioId)).thenReturn(Optional.of(plantio));

        LocalDate novaData = LocalDate.now();
        CadastroPlantioRequest request = new CadastroPlantioRequest(novaData, 5.0, "Atualizado");

        PlantioResponse response = plantioService.atualizar(propriedadeId, talhaoId, safraId, plantioId, request, usuarioId);

        assertEquals(novaData, response.dataPlantio());
        assertEquals("Atualizado", response.observacoes());
    }

    @Test
    void deveRecusarAtualizarPlantioDeOutraSafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();
        UUID plantioId = UUID.randomUUID();

        Safra outraSafra = criarSafraDoTalhao(propriedadeId, talhaoId, UUID.randomUUID());
        Plantio plantio = new Plantio(outraSafra, LocalDate.now(), 5.0, null);

        when(plantioRepository.findById(plantioId)).thenReturn(Optional.of(plantio));

        CadastroPlantioRequest request = new CadastroPlantioRequest(LocalDate.now(), 5.0, null);

        assertThrows(RegraDeNegocioException.class,
            () -> plantioService.atualizar(propriedadeId, talhaoId, safraId, plantioId, request, usuarioId));
    }

    @Test
    void deveListarPlantiosDaSafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();

        Safra safra = criarSafraDoTalhao(propriedadeId, talhaoId, safraId);
        Plantio plantio1 = new Plantio(safra, LocalDate.now(), 5.0, null);
        Plantio plantio2 = new Plantio(safra, LocalDate.now(), 3.0, "Replantio");

        when(safraRepository.findById(safraId)).thenReturn(Optional.of(safra));
        when(plantioRepository.findBySafraId(safraId)).thenReturn(List.of(plantio1, plantio2));

        List<PlantioResponse> resultado = plantioService.listar(propriedadeId, safraId, talhaoId, usuarioId);

        assertEquals(2, resultado.size());
    }

    @Test
    void deveBuscarPlantioPorIdQuandoPertenceASafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();
        UUID plantioId = UUID.randomUUID();

        Safra safra = criarSafraDoTalhao(propriedadeId, talhaoId, safraId);
        Plantio plantio = new Plantio(safra, LocalDate.now(), 5.0, "Observação");

        when(plantioRepository.findById(plantioId)).thenReturn(Optional.of(plantio));

        PlantioResponse response = plantioService.buscarPorId(propriedadeId, safraId, talhaoId, plantioId, usuarioId);

        assertEquals("Observação", response.observacoes());
    }

    @Test
    void deveRecusarBuscarPlantioDeOutraSafra() {
        UUID usuarioId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID talhaoId = UUID.randomUUID();
        UUID safraId = UUID.randomUUID();
        UUID plantioId = UUID.randomUUID();

        Safra outraSafra = criarSafraDoTalhao(propriedadeId, talhaoId, UUID.randomUUID());
        Plantio plantio = new Plantio(outraSafra, LocalDate.now(), 5.0, null);

        when(plantioRepository.findById(plantioId)).thenReturn(Optional.of(plantio));

        assertThrows(RegraDeNegocioException.class,
            () -> plantioService.buscarPorId(propriedadeId, safraId, talhaoId, plantioId, usuarioId));
    }
}
