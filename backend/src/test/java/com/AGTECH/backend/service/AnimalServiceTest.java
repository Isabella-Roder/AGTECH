package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.AnimalResponse;
import com.AGTECH.backend.dtos.CadastroAnimalRequest;
import com.AGTECH.backend.enums.SexoAnimal;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Animal;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Rebanho;
import com.AGTECH.backend.repository.AnimalRepository;
import com.AGTECH.backend.repository.RebanhoRepository;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {

    @Mock private AnimalRepository animalRepository;
    @Mock private RebanhoRepository rebanhoRepository;
    @Mock private UsuarioPropriedadeAcessoService acessoService;

    @InjectMocks private AnimalService animalService;

    private Rebanho criarRebanho(UUID rebanhoId, UUID propriedadeId) {
        PropriedadeRural propriedade = mock(PropriedadeRural.class);
        lenient().when(propriedade.getId()).thenReturn(propriedadeId);
        Rebanho rebanho = mock(Rebanho.class);
        lenient().when(rebanho.getId()).thenReturn(rebanhoId);
        lenient().when(rebanho.getPropriedade()).thenReturn(propriedade);
        return rebanho;
    }

    private CadastroAnimalRequest criarRequest() {
        return new CadastroAnimalRequest("Brinco 01", SexoAnimal.FEMEA, LocalDate.of(2023, 1, 1));
    }

    @Test
    void cadastrar_deveSalvarAnimal() {
        UUID rebanhoId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        Rebanho rebanho = criarRebanho(rebanhoId, propriedadeId);
        when(rebanhoRepository.findById(rebanhoId)).thenReturn(Optional.of(rebanho));
        when(animalRepository.save(any(Animal.class))).thenAnswer(i -> i.getArgument(0));

        AnimalResponse response = animalService.cadastrar(
                propriedadeId, rebanhoId, criarRequest(), UUID.randomUUID());

        assertEquals("Brinco 01", response.identificacao());
        assertEquals(SexoAnimal.FEMEA, response.sexo());
        assertEquals(rebanhoId, response.rebanhoId());
    }

    @Test
    void cadastrar_deveFalharQuandoRebanhoDeOutraPropriedade() {
        UUID rebanhoId = UUID.randomUUID();
        Rebanho rebanho = criarRebanho(rebanhoId, UUID.randomUUID());
        when(rebanhoRepository.findById(rebanhoId)).thenReturn(Optional.of(rebanho));

        assertThrows(RegraDeNegocioException.class, () -> animalService.cadastrar(
                UUID.randomUUID(), rebanhoId, criarRequest(), UUID.randomUUID()));
        verify(animalRepository, never()).save(any());
    }

    @Test
    void atualizar_deveAlterarCamposESalvar() {
        UUID rebanhoId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Animal existente = new Animal(
                criarRebanho(rebanhoId, propriedadeId), "Antigo", SexoAnimal.MACHO, LocalDate.of(2020, 1, 1));
        when(animalRepository.findById(id)).thenReturn(Optional.of(existente));
        when(animalRepository.save(any(Animal.class))).thenAnswer(i -> i.getArgument(0));

        AnimalResponse response = animalService.atualizar(
                propriedadeId, rebanhoId, id, criarRequest(), UUID.randomUUID());

        assertEquals("Brinco 01", response.identificacao());
        assertEquals(SexoAnimal.FEMEA, response.sexo());
        verify(animalRepository).save(existente);
    }

    @Test
    void atualizar_deveFalharQuandoAnimalDeOutroRebanho() {
        UUID id = UUID.randomUUID();
        Animal existente = new Animal(
                criarRebanho(UUID.randomUUID(), UUID.randomUUID()), "Antigo", SexoAnimal.MACHO, LocalDate.of(2020, 1, 1));
        when(animalRepository.findById(id)).thenReturn(Optional.of(existente));

        assertThrows(RegraDeNegocioException.class, () -> animalService.atualizar(
                UUID.randomUUID(), UUID.randomUUID(), id, criarRequest(), UUID.randomUUID()));
        verify(animalRepository, never()).save(any());
    }

    @Test
    void desativar_deveMarcarAnimalComoInativo() {
        UUID rebanhoId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Animal animal = new Animal(
                criarRebanho(rebanhoId, propriedadeId), "Brinco 01", SexoAnimal.FEMEA, LocalDate.of(2020, 1, 1));
        when(animalRepository.findById(id)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class))).thenAnswer(i -> i.getArgument(0));

        AnimalResponse response = animalService.desativar(propriedadeId, rebanhoId, id, UUID.randomUUID());

        assertFalse(response.ativo());
    }

    @Test
    void ativar_deveMarcarAnimalComoAtivo() {
        UUID rebanhoId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Animal animal = new Animal(
                criarRebanho(rebanhoId, propriedadeId), "Brinco 01", SexoAnimal.FEMEA, LocalDate.of(2020, 1, 1));
        animal.desativar();
        when(animalRepository.findById(id)).thenReturn(Optional.of(animal));
        when(animalRepository.save(any(Animal.class))).thenAnswer(i -> i.getArgument(0));

        AnimalResponse response = animalService.ativar(propriedadeId, rebanhoId, id, UUID.randomUUID());

        assertTrue(response.ativo());
    }

    @Test
    void desativar_deveFalharQuandoAnimalDeOutroRebanho() {
        UUID propriedadeId = UUID.randomUUID();
        UUID rebanhoId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Animal animal = new Animal(
                criarRebanho(UUID.randomUUID(), UUID.randomUUID()), "Brinco 01", SexoAnimal.FEMEA, LocalDate.of(2020, 1, 1));
        when(animalRepository.findById(id)).thenReturn(Optional.of(animal));

        assertThrows(RegraDeNegocioException.class,
                () -> animalService.desativar(propriedadeId, rebanhoId, id, UUID.randomUUID()));
        verify(animalRepository, never()).save(any());
    }

    @Test
    void listarPorRebanho_deveRetornarAnimais() {
        UUID rebanhoId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        Rebanho rebanho = criarRebanho(rebanhoId, propriedadeId);
        when(rebanhoRepository.findById(rebanhoId)).thenReturn(Optional.of(rebanho));
        when(animalRepository.findByRebanhoId(rebanhoId)).thenReturn(
                List.of(new Animal(rebanho, "Brinco 01", SexoAnimal.FEMEA, LocalDate.of(2020, 1, 1))));

        List<AnimalResponse> resultado = animalService.listarPorRebanho(propriedadeId, rebanhoId, UUID.randomUUID());

        assertEquals(1, resultado.size());
    }

    @Test
    void listarPorRebanho_deveFalharQuandoRebanhoDeOutraPropriedade() {
        UUID rebanhoId = UUID.randomUUID();
        Rebanho rebanho = criarRebanho(rebanhoId, UUID.randomUUID());
        when(rebanhoRepository.findById(rebanhoId)).thenReturn(Optional.of(rebanho));

        assertThrows(RegraDeNegocioException.class,
                () -> animalService.listarPorRebanho(UUID.randomUUID(), rebanhoId, UUID.randomUUID()));
    }

    @Test
    void buscarPorId_deveRetornarAnimal() {
        UUID rebanhoId = UUID.randomUUID();
        UUID propriedadeId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Animal animal = new Animal(
                criarRebanho(rebanhoId, propriedadeId), "Brinco 01", SexoAnimal.FEMEA, LocalDate.of(2020, 1, 1));
        when(animalRepository.findById(id)).thenReturn(Optional.of(animal));

        AnimalResponse response = animalService.buscarPorId(propriedadeId, rebanhoId, id, UUID.randomUUID());

        assertEquals("Brinco 01", response.identificacao());
    }

    @Test
    void buscarPorId_deveFalharQuandoAnimalDeOutroRebanho() {
        UUID id = UUID.randomUUID();
        Animal animal = new Animal(
                criarRebanho(UUID.randomUUID(), UUID.randomUUID()), "Brinco 01", SexoAnimal.FEMEA, LocalDate.of(2020, 1, 1));
        when(animalRepository.findById(id)).thenReturn(Optional.of(animal));

        assertThrows(RegraDeNegocioException.class, () -> animalService.buscarPorId(
                UUID.randomUUID(), UUID.randomUUID(), id, UUID.randomUUID()));
    }
}
