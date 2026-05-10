package br.com.anima.nuPrecin.voto;

import br.com.anima.nuPrecin.promocao.Promocao;
import br.com.anima.nuPrecin.promocao.PromocaoRepository;
import br.com.anima.nuPrecin.usuario.Usuario;
import br.com.anima.nuPrecin.usuario.UsuarioRepository;
import br.com.anima.nuPrecin.voto.dto.VotoPromocaoRankingResponseDto;
import br.com.anima.nuPrecin.voto.dto.VotoRequestDto;
import br.com.anima.nuPrecin.voto.dto.VotoResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VotoService {
    @Autowired
    private VotoRepository votoRepository;
    @Autowired
    private VotoMapper votoMapper;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PromocaoRepository promocaoRepository;

    public VotoResponseDto createOrUpdate(@Valid VotoRequestDto dto) {
        Usuario usuario = usuarioRepository.findByIdAndAtivoTrue(dto.idUsuario())
                .orElseThrow(() -> new EntityNotFoundException("usuário com id {" + dto.idUsuario() + "} não localizado no banco"));
        Promocao promocao = promocaoRepository.findByIdAndAtivoTrue(dto.idPromocao())
                .orElseThrow(() -> new EntityNotFoundException("promoção com id {" + dto.idPromocao() + "} não localizada no banco"));

        if (promocao.getUsuario() != null && promocao.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("usuário não pode votar na própria promoção.");
        }

        Voto voto = votoRepository.findByUsuarioIdAndPromocaoIdAndAtivoTrue(dto.idUsuario(), dto.idPromocao())
                .orElseGet(() -> {
                    Voto novoVoto = new Voto();
                    novoVoto.setUsuario(usuario);
                    novoVoto.setPromocao(promocao);
                    return novoVoto;
                });

        voto.setVoto(dto.voto());
        voto.setAtivo(true);
        votoRepository.save(voto);

        return votoMapper.toResponse(voto);
    }

    public VotoResponseDto findById(Long id) {
        Voto voto = votoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("voto com id {" + id + "} não localizado no banco"));
        return votoMapper.toResponse(voto);
    }

    public List<VotoResponseDto> findAll(Long idPromocao, Long idUsuario, LocalDateTime dataInicio, LocalDateTime dataFim, VotoEnum voto) {
        if (dataInicio != null && dataFim != null && voto != null) {
            if (dataFim.isBefore(dataInicio)) {
                throw new IllegalArgumentException("dataFim deve ser maior ou igual à dataInicio.");
            }
            return votoMapper.toResponseList(
                    votoRepository.findByAtivoTrueAndVotoAndDataVotoBetweenOrderByDataVotoDesc(voto, dataInicio, dataFim)
            );
        }

        if (idPromocao != null) {
            return votoMapper.toResponseList(votoRepository.findByPromocaoIdAndAtivoTrue(idPromocao));
        }
        if (idUsuario != null) {
            return votoMapper.toResponseList(votoRepository.findByUsuarioIdAndAtivoTrue(idUsuario));
        }
        List<Voto> votos = votoRepository.findAll().stream().filter(Voto::isAtivo).toList();
        return votoMapper.toResponseList(votos);
    }

    public List<VotoPromocaoRankingResponseDto> buscarRankingPromocoes(LocalDateTime dataInicio, LocalDateTime dataFim, VotoEnum voto, String ordenacao) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("dataInicio e dataFim são obrigatórios para ranking.");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("dataFim deve ser maior ou igual à dataInicio.");
        }

        VotoEnum tipoVoto = voto == null ? VotoEnum.POSITIVO : voto;
        String ordem = ordenacao == null ? "desc" : ordenacao.trim().toLowerCase();

        List<VotoRepository.VotoPromocaoRankingProjection> ranking = "asc".equals(ordem)
                ? votoRepository.buscarRankingPromocoesAsc(tipoVoto, dataInicio, dataFim)
                : votoRepository.buscarRankingPromocoesDesc(tipoVoto, dataInicio, dataFim);

        return ranking.stream()
                .map(item -> new VotoPromocaoRankingResponseDto(item.getIdPromocao(), item.getTotalVotos()))
                .toList();
    }

    public void delete(Long id) {
        Voto voto = votoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("voto com id {" + id + "} não localizado no banco"));

        voto.setAtivo(false);
        votoRepository.save(voto);
    }
}
