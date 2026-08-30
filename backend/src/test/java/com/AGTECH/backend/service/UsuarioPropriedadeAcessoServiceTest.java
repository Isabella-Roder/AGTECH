package com.AGTECH.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.AGTECH.backend.dtos.AcessoResponse;
import com.AGTECH.backend.dtos.ConcederAcessoRequest;
import com.AGTECH.backend.enums.PapelAcesso;
import com.AGTECH.backend.exception.RegraDeNegocioException;
import com.AGTECH.backend.models.PropriedadeRural;
import com.AGTECH.backend.models.Usuario;
import com.AGTECH.backend.models.UsuarioPropriedadeAcesso;
import com.AGTECH.backend.repository.PropriedadeRuralRepository;
import com.AGTECH.backend.repository.UsuarioPropriedadeAcessoRepository;
import com.AGTECH.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioPropriedadeAcessoServiceTest {

    @Mock
    private UsuarioPropriedadeAcessoRepository acessoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PropriedadeRuralRepository propriedadeRuralRepository;

    @InjectMocks
    protected UsuarioPropriedadeAcessoService service;

    @Test
    void deveRecusarConcederAcessoDuplicado() {
        Long propriedadeId = 1L;
        ConcederAcessoRequest request = new ConcederAcessoRequest(2L, PapelAcesso.GESTOR);

        when(acessoRepository.existsByUsuarioIdAndPropriedadeId(2L, propriedadeId))
            .thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> service.conceder(propriedadeId, request));
    }

    @Test
    void deveRecusarRevogarAcessoDeOutraPropriedade() {
        PropriedadeRural propriedadeMock = mock(PropriedadeRural.class);
        when(propriedadeMock.getId()).thenReturn(5L);

        UsuarioPropriedadeAcesso acesso = new UsuarioPropriedadeAcesso(new Usuario(), propriedadeMock, PapelAcesso.GESTOR);

        when(acessoRepository.findById(10L)).thenReturn(Optional.of(acesso));

        assertThrows(RegraDeNegocioException.class, () -> service.revogar(999L, 10L));
    }

    @Test
    void deveRevogarAcessoDaPropriedadeCorreta() {
        PropriedadeRural propriedadeMock = mock(PropriedadeRural.class);
        when(propriedadeMock.getId()).thenReturn(5L);

        UsuarioPropriedadeAcesso acesso = new UsuarioPropriedadeAcesso(new Usuario(), propriedadeMock, PapelAcesso.GESTOR);

        when(acessoRepository.findById(10L)).thenReturn(Optional.of(acesso));

        service.revogar(5L, 10L);

        verify(acessoRepository).delete(acesso);
    }

    @Test
    void deveConcederAcessoPropriedade() {
        Long propriedadeId = 1L;
        ConcederAcessoRequest request = new ConcederAcessoRequest(2L, PapelAcesso.GESTOR);
        
        Usuario usuario = new Usuario();
        usuario.setNome("Isabella");

        PropriedadeRural propriedadeMock = mock(PropriedadeRural.class);

        when(acessoRepository.existsByUsuarioIdAndPropriedadeId(2L, propriedadeId)).thenReturn(false);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(propriedadeRuralRepository.findById(propriedadeId)).thenReturn(Optional.of(propriedadeMock));
        when(acessoRepository.save(any(UsuarioPropriedadeAcesso.class)))
            .thenAnswer(invocacao -> invocacao.getArgument(0));

        AcessoResponse response = service.conceder(propriedadeId, request);

        assertEquals(PapelAcesso.GESTOR, response.papel());
        assertEquals("Isabella", response.usuarioNome());
        verify(acessoRepository).save(any(UsuarioPropriedadeAcesso.class));
    }
}
