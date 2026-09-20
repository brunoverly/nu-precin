package br.com.anima.nuPrecin.voto;

import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.promocao.PromocaoRepository;
import br.com.anima.nuPrecin.security.CurrentUserService;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import br.com.anima.nuPrecin.voto.dto.VotoPromocaoRankingResponseDto;
import br.com.anima.nuPrecin.voto.dto.VotoRequestDto;
import br.com.anima.nuPrecin.voto.dto.VotoResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VotoService {
    public record VotoOperationResult(VotoResponseDto response, boolean created) {
    }

    @Autowired
    private VotoRepository votoRepository;
    @Autowired
    private VotoMapper votoMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PromocaoRepository promocaoRepository;
    @Autowired
    private CurrentUserService currentUserService;

    @Transactional
    public VotoOperationResult createOrUpdate(@Valid VotoRequestDto dto) {
        validarId("idUsuario", dto.idUsuario());
        validarId("idPromocao", dto.idPromocao());
        currentUserService.ensureCanUseUserId(dto.idUsuario());
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(dto.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + dto.idUsuario() + "} não localizado no banco"));
        Promocao promocao = promocaoRepository.findByIdAndAtivoTrue(dto.idPromocao())
                .orElseThrow(() -> new EntityNotFoundException("promoção com id {" + dto.idPromocao() + "} não localizada no banco"));

        if (promocao.getIdUsuario() != null && promocao.getIdUsuario().equals(usuario.getId())) {
            throw new IllegalArgumentException("usuário não pode votar na própria promoção.");
        }

        Optional<Voto> votoExistente = votoRepository.findByUsuarioIdAndPromocaoIdAndAtivoTrue(dto.idUsuario(), dto.idPromocao());
        Voto voto = votoExistente
                .orElseGet(() -> {
                    Voto novoVoto = new Voto();
                    novoVoto.setUsuario(usuario);
                    novoVoto.setPromocao(promocao);
                    return novoVoto;
                });

        voto.setVoto(dto.voto());
        voto.setDataVoto(LocalDateTime.now());
        voto.setAtivo(true);
        votoRepository.save(voto);

        return new VotoOperationResult(votoMapper.toResponse(voto), votoExistente.isEmpty());
    }

    @Transactional(readOnly = true)
    public VotoResponseDto findById(Long id) {
        Voto voto = votoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("voto com id {" + id + "} não localizado no banco"));
        currentUserService.ensureCanManageUser(voto.getUsuario().getId());
        return votoMapper.toResponse(voto);
    }

    @Transactional(readOnly = true)
    public Page<VotoResponseDto> findAll(Long idPromocao, Long idUsuario, LocalDateTime dataInicio, LocalDateTime dataFim, VotoEnum voto, Pageable pageable) {
        validarIdOpcional("idPromocao", idPromocao);
        validarIdOpcional("idUsuario", idUsuario);
        validarPeriodo(dataInicio, dataFim);

        if (pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("size não pode ser maior que 100.");
        }

        return votoRepository.findAtivosComFiltros(idPromocao, idUsuario, dataInicio, dataFim, voto, pageable)
                .map(votoMapper::toResponse);
    }

    private void validarId(String nome, Long valor) {
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException(nome + " deve ser maior que zero.");
        }
    }

    private void validarIdOpcional(String nome, Long valor) {
        if (valor != null && valor <= 0) {
            throw new IllegalArgumentException(nome + " deve ser maior que zero.");
        }
    }

    private void validarPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if ((dataInicio == null) != (dataFim == null)) {
            throw new IllegalArgumentException("dataInicio e dataFim devem ser informados juntos.");
        }

        if (dataInicio != null && dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("dataFim deve ser maior ou igual à dataInicio.");
        }
    }

    @Transactional(readOnly = true)
    public List<VotoPromocaoRankingResponseDto> buscarRankingPromocoes(LocalDateTime dataInicio, LocalDateTime dataFim, VotoEnum voto, String ordenacao) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("dataInicio e dataFim são obrigatórios para ranking.");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("dataFim deve ser maior ou igual à dataInicio.");
        }

        VotoEnum tipoVoto = voto == null ? VotoEnum.POSITIVO : voto;
        String ordem = ordenacao == null ? "desc" : ordenacao.trim().toLowerCase();

        if (!"asc".equals(ordem) && !"desc".equals(ordem)) {
            throw new IllegalArgumentException("ordenacao deve ser asc ou desc.");
        }

        List<VotoRepository.VotoPromocaoRankingProjection> ranking = "asc".equals(ordem)
                ? votoRepository.buscarRankingPromocoesAsc(tipoVoto, dataInicio, dataFim)
                : votoRepository.buscarRankingPromocoesDesc(tipoVoto, dataInicio, dataFim);

        return ranking.stream()
                .map(item -> new VotoPromocaoRankingResponseDto(item.getIdPromocao(), item.getTotalVotos()))
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Voto voto = votoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("voto com id {" + id + "} não localizado no banco"));
        currentUserService.ensureCanManageUser(voto.getUsuario().getId());

        voto.setAtivo(false);
        votoRepository.save(voto);
    }
}
