# Base mock para testes visuais

Esta base contém **212 produtos e 1.060 promoções simuladas**, em **18 categorias de supermercado**. Cada produto tem cinco ofertas para comparar preços entre lojas. Todos os produtos possuem uma URL de imagem.

## Arquivos

| Arquivo | Conteúdo |
|---|---|
| `produtos.json` | Payload de produto, referência local e fonte dos dados |
| `promocoes.json` | Payload de promoção, vínculo com produto e referência de loja |
| `fontes.json` | URLs de consulta ao catálogo público e horário da pesquisa |
| `manifesto.json` | Totais, cenários, categorias e IDs a mapear |
| `preparar_payloads.py` | Preparação local de payloads com IDs e datas atualizados |

Nomes, marcas, embalagens, imagens e preços de referência foram consultados no catálogo público do [Supernosso](https://www.supernosso.com/). Cada produto registra sua própria URL de origem e preço consultado. As imagens permanecem hospedadas na origem e podem mudar ou ficar indisponíveis.

Os descontos de 5% a 25% e a distribuição entre cinco lojas foram simulados. Os preços de referência são preços consultados online e podem diferir dos preços de lojas físicas. Produtos vendidos por peso estão representados pela quantidade de referência do anúncio. A quantidade da embalagem permanece ausente quando a fonte não permite identificá-la.

## Cenários

- Ofertas já iniciadas enquanto durar o estoque, sem data final, com janela de confirmação de 24 horas.
- Ofertas de estoque com uma data final anunciada.
- Ofertas futuras com início em uma hora e duração de 2 a 13 dias.
- Cinco preços diferentes por produto, para exercitar a ordenação e o comparativo.
- Itens baratos, caros, embalagens pequenas e grandes, e nomes de tamanhos diferentes.

O cadastro futuro pela API gera a primeira entrada de histórico de cada promoção. Uma série histórica de dias anteriores exige observações adicionais e não faz parte desta base.

## Preparar uma carga futura

Preencha o ID do usuário e escolha cinco estabelecimentos existentes. Os arquivos originais deixam esses IDs como `null` e usam referências como `loja_01`. As associações não indicam ofertas reais dessas lojas.

Para gerar os corpos JSON com datas válidas no momento da preparação, execute o comando abaixo, substituindo os IDs de exemplo pelos IDs reais.

```bash
python3 docs/mocks/preparar_payloads.py --usuario 1 --lojas 101 102 103 104 105 --saida /tmp/nuprecin-mock-preparado
```

O comando somente grava arquivos locais. Para uma futura importação autorizada, cadastre ou reutilize os produtos pelos códigos `MOCK-NUPRECIN-...` e depois cadastre as promoções. Reexecutar uma importação precisa considerar as promoções já criadas para evitar duplicações.

Os códigos são identificadores sintéticos aceitos pelo catálogo. O EAN informado pelo varejista fica nos metadados de referência. As datas dos arquivos originais são exemplos da data de preparação. Gere novamente os payloads imediatamente antes da futura carga e considere que ofertas comuns entram no comparativo após uma hora. As ofertas sem data final ficam disponíveis imediatamente após o cadastro.

A preparação desta base fez apenas consultas de leitura a fontes públicas e gravações locais.
