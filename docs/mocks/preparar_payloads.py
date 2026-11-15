"""Materializa IDs e datas em arquivos locais para uma futura carga autorizada."""

import argparse
import datetime as dt
import json
from pathlib import Path

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument("--usuario", type=int, required=True)
parser.add_argument("--lojas", type=int, nargs=5, required=True)
parser.add_argument("--saida", type=Path, required=True)
args = parser.parse_args()
if args.usuario <= 0 or any(loja <= 0 for loja in args.lojas):
    parser.error("Informe IDs positivos.")
if len(set(args.lojas)) != 5:
    parser.error("Informe cinco lojas distintas.")

origem = Path(__file__).resolve().parent
if args.saida.resolve() == origem:
    parser.error("Escolha uma pasta de saída diferente da base.")
agora = dt.datetime.now(dt.timezone(dt.timedelta(hours=-3))).replace(tzinfo=None, microsecond=0)
produtos = json.loads((origem / "produtos.json").read_text())
promocoes = json.loads((origem / "promocoes.json").read_text())

payloads_produtos = []
for produto in produtos:
    payload = dict(produto["payload"], idUsuario=args.usuario)
    payloads_produtos.append(payload)

payloads_promocoes = []
for promocao in promocoes:
    indice_loja = int(promocao["estabelecimentoRef"].split("_")[-1]) - 1
    payload = dict(promocao["payload"], idUsuario=args.usuario, idEstabelecimento=args.lojas[indice_loja])
    payload["dataInicio"] = (agora + dt.timedelta(hours=promocao["inicioOffsetHoras"])).isoformat()
    if "fimOffsetDias" in promocao:
        payload["dataFim"] = (agora + dt.timedelta(days=promocao["fimOffsetDias"])).isoformat()
    payloads_promocoes.append(payload)

args.saida.mkdir(parents=True, exist_ok=True)
for nome, dados in [("produtos-payloads.json", payloads_produtos), ("promocoes-payloads.json", payloads_promocoes)]:
    (args.saida / nome).write_text(json.dumps(dados, ensure_ascii=False, indent=2) + "\n")
print(f"Arquivos locais preparados em {args.saida}. {len(payloads_produtos)} produtos e {len(payloads_promocoes)} promoções.")
