package com.AGTECH.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AGTECH.backend.dtos.AnimalResponse;
import com.AGTECH.backend.dtos.CadastroAnimalRequest;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.Animal;
import com.AGTECH.backend.models.Rebanho;
import com.AGTECH.backend.repository.AnimalRepository;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import com.AGTECH.backend.repository.RebanhoRepository;

@Service 
public class AnimalService {
    
    private final AnimalRepository animalRepository;
    private final RebanhoRepository rebanhoRepository;
    private final PropriedadeRuralRepository propriedadeRuralRepository;
    private final UsuarioPropriedadeAcessoService acessoService;

    public AnimalService(
        AnimalRepository animalRepository,
        RebanhoRepository rebanhoRepository,
        PropriedadeRuralRepository propriedadeRuralRepository,
        UsuarioPropriedadeAcessoService acessoService
    ) {
        this.animalRepository = animalRepository;
        this.rebanhoRepository = rebanhoRepository;
        this.propriedadeRuralRepository = propriedadeRuralRepository;
        this.acessoService = acessoService;
    }

    private Animal buscarEntidade(UUID id) {
        return animalRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Animal não encontrado com ID: " + id));
    }

    private Rebanho buscarRebanho(UUID rebanhoId){
        return rebanhoRepository.findById(rebanhoId)
            .orElseThrow(() -> new RegraDeNegocioException("Rebanho não encontrado com ID: " + rebanhoId));
    }

    private void verificarAnimalPertencente(Animal animal, UUID rebanhoId, UUID propriedadeId) {
        if (!animal.getRebanho().getId().equals(rebanhoId)) {
            throw new RegraDeNegocioException("Esse animal não pertence a esse rebanho.");
        } else if (!animal.getRebanho().getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse rebanho não pertence a essa propriedade.");
        }
    }

    private void verificarRebanhoPertencente(Rebanho rebanho, UUID propriedadeId) {
        if (!rebanho.getPropriedade().getId().equals(propriedadeId)) {
            throw new RegraDeNegocioException("Esse rebanho não pertence a essa propriedade.");
        }
    }

    @Transactional 
    public AnimalResponse cadastrar(UUID propriedadeId, UUID rebanhoId, CadastroAnimalRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Rebanho rebanho = buscarRebanho(rebanhoId);
        verificarRebanhoPertencente(rebanho, propriedadeId);

        Animal animal = new Animal(
            rebanho,
            request.identificacao(),
            request.sexo(),
            request.dataNascimento()
        );

        return AnimalResponse.from(animalRepository.save(animal));
    }

    @Transactional
    public AnimalResponse atualizar(UUID propriedadeId, UUID rebanhoId, UUID id, CadastroAnimalRequest request, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);

        Animal animal = buscarEntidade(id);
        verificarAnimalPertencente(animal, rebanhoId, propriedadeId);

        animal.setIdentificacao(request.identificacao());
        animal.setSexo(request.sexo());
        animal.setDataNascimento(request.dataNascimento());

        return AnimalResponse.from(animalRepository.save(animal));
    }

    @Transactional
    public AnimalResponse desativar(UUID propriedadeId, UUID rebanhoId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Animal animal = buscarEntidade(id);
        verificarAnimalPertencente(animal, rebanhoId, propriedadeId);
        animal.desativar();
        return AnimalResponse.from(animalRepository.save(animal));
    }

    @Transactional 
    public AnimalResponse ativar(UUID propriedadeId, UUID rebanhoId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Animal animal = buscarEntidade(id);
        verificarAnimalPertencente(animal, rebanhoId, propriedadeId);
        animal.ativar();
        return AnimalResponse.from(animalRepository.save(animal));
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> listarPorRebanho(UUID propriedadeId, UUID rebanhoId, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Rebanho rebanho = buscarRebanho(rebanhoId);
        verificarRebanhoPertencente(rebanho, propriedadeId);
        return animalRepository.findByRebanhoId(rebanhoId)
            .stream().map(AnimalResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AnimalResponse buscarPorId(UUID propriedadeId, UUID rebanhoId, UUID id, UUID usuarioId) {
        acessoService.verificarAcesso(usuarioId, propriedadeId);
        Animal animal = buscarEntidade(id);
        verificarAnimalPertencente(animal, rebanhoId, propriedadeId);
        return AnimalResponse.from(animal);
    }

}
