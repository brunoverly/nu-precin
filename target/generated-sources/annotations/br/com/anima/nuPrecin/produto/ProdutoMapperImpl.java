package br.com.anima.nuPrecin.produto;

import br.com.anima.nuPrecin.produto.dto.ProdutoRequestDto;
import br.com.anima.nuPrecin.produto.dto.ProdutoResponseDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-22T19:54:46-0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ProdutoMapperImpl implements ProdutoMapper {

    @Override
    public Produto toEntity(ProdutoRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Produto.ProdutoBuilder produto = Produto.builder();

        produto.nome( dto.nome() );
        produto.marca( dto.marca() );
        produto.categoria( dto.categoria() );

        return produto.build();
    }

    @Override
    public ProdutoResponseDto toResponse(Produto entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String nome = null;
        String descricao = null;
        String marca = null;
        String codigoDeBarras = null;
        String qrCode = null;
        String imagem = null;
        ProdutoEnum categoria = null;
        boolean ativo = false;

        id = entity.getId();
        nome = entity.getNome();
        descricao = entity.getDescricao();
        marca = entity.getMarca();
        codigoDeBarras = entity.getCodigoDeBarras();
        qrCode = entity.getQrCode();
        imagem = entity.getImagem();
        categoria = entity.getCategoria();
        ativo = entity.isAtivo();

        ProdutoResponseDto produtoResponseDto = new ProdutoResponseDto( id, nome, descricao, marca, codigoDeBarras, qrCode, imagem, categoria, ativo );

        return produtoResponseDto;
    }

    @Override
    public List<ProdutoResponseDto> toResponseList(List<Produto> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ProdutoResponseDto> list = new ArrayList<ProdutoResponseDto>( entities.size() );
        for ( Produto produto : entities ) {
            list.add( toResponse( produto ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(ProdutoRequestDto dto, Produto entity) {
        if ( dto == null ) {
            return;
        }

        entity.setNome( dto.nome() );
        entity.setMarca( dto.marca() );
        entity.setCategoria( dto.categoria() );
    }
}
