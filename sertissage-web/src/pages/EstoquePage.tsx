import { useEffect, useState } from "react"
import {
  Layers, TrendingUp, TrendingDown, Minus, RotateCcw,
  AlertCircle, Loader2, Plus, ChevronDown, ChevronRight
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"
import { api } from "@/lib/api" 

// ─── Tipos ──────────────────────────────────────────────────────────────────

interface EstoquePageProps {
  token: string
}

interface Movimentacao {
  id: string
  materialId: string
  materialNome: string
  tipo: "ENTRADA" | "SAIDA" | "AJUSTE"
  origem: string
  quantidadeGramas: number
  valorPorGrama: number | null
  valorTotal: number | null
  observacao: string | null
  createdAt: string
}

interface SaldoItem {
  materialId: string
  materialNome: string
  saldo: number
  unidadeMedida: string
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

const ORIGEM_LABEL: Record<string, string> = {
  COMPRA:        "Compra",
  PRODUCAO:      "Produção",
  CANCELAMENTO:  "Cancelamento",
  AJUSTE_MANUAL: "Ajuste manual",
  PERDA:         "Perda",
}

function fmt(v: number | null, d = 3) {
  if (v == null) return "—"
  return v.toLocaleString("pt-BR", { minimumFractionDigits: d, maximumFractionDigits: d })
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("pt-BR", {
    day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit",
  })
}

// ─── Componente ─────────────────────────────────────────────────────────────

export default function EstoquePage({ token }: EstoquePageProps) {
  const [movs, setMovs]       = useState<Movimentacao[]>([])
  const [loading, setLoading] = useState(true)
  const [erro, setErro]       = useState<string | null>(null)
  const [expandido, setExpandido] = useState<string | null>(null)

  // AJUSTADO: Agora usa o serviço 'api' com Axios
  const carregar = async () => {
    setLoading(true)
    setErro(null)
    try {
      // O interceptor no api.ts anexa o token automaticamente
      const res = await api.get("/estoque/historico")
      setMovs(res.data)
    } catch (e: any) {
      // Captura a mensagem de erro formatada pelo Spring Boot
      const mensagem = e.response?.data?.message || "Erro ao carregar estoque"
      setErro(mensagem)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { carregar() }, [token])

  const saldos = Object.values(
    movs.reduce<Record<string, SaldoItem>>((acc, m) => {
      if (!acc[m.materialId]) {
        acc[m.materialId] = {
          materialId: m.materialId,
          materialNome: m.materialNome,
          saldo: 0,
          unidadeMedida: "g",
        }
      }
      if (m.tipo === "ENTRADA" || m.tipo === "AJUSTE") {
        acc[m.materialId].saldo += m.quantidadeGramas
      } else {
        acc[m.materialId].saldo -= m.quantidadeGramas
      }
      return acc
    }, {})
  ).sort((a, b) => b.saldo - a.saldo)

  return (
    <div className="space-y-8 max-w-6xl">

      <div className="flex items-end justify-between">
        <div className="space-y-1">
          <h1 className="font-serif text-3xl font-light text-foreground">Estoque</h1>
          <p className="text-sm text-muted-foreground uppercase tracking-widest">
            Controle por movimentação
          </p>
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            className="border-border/50 hover:border-primary/50 text-foreground font-light rounded-sm gap-2 h-9 text-sm"
          >
            <Minus size={13} strokeWidth={2} /> Ajuste
          </Button>
          <Button className="bg-primary hover:bg-primary/90 text-primary-foreground tracking-wide gap-2 rounded-sm h-9 text-sm">
            <Plus size={13} strokeWidth={2} /> Entrada
          </Button>
          <button
            onClick={carregar}
            disabled={loading}
            className="p-2 rounded-sm border border-border/40 text-muted-foreground/60 hover:text-foreground hover:border-border/70 transition-colors"
          >
            <RotateCcw size={13} strokeWidth={1.5} className={cn(loading && "animate-spin")} />
          </button>
        </div>
      </div>

      {!loading && saldos.length > 0 && (
        <div className="space-y-3">
          <h2 className="font-serif text-lg font-light text-foreground/80">Saldos atuais</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-3">
            {saldos.slice(0, 6).map((s) => (
              <div
                key={s.materialId}
                className="bg-card border border-border/50 rounded-sm p-4 flex items-center justify-between group hover:border-border/80 transition-colors relative overflow-hidden"
              >
                <div className="absolute top-0 left-0 w-full h-px bg-gradient-to-r from-transparent via-primary/30 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
                <div>
                  <div className="text-xs uppercase tracking-wider text-muted-foreground/60 mb-1">
                    {s.materialNome}
                  </div>
                  <div className="flex items-baseline gap-1">
                    <span className="font-serif text-2xl font-light text-foreground">
                      {fmt(s.saldo)}
                    </span>
                    <span className="text-sm text-muted-foreground">{s.unidadeMedida}</span>
                  </div>
                </div>
                <Layers
                  size={18}
                  className={cn(
                    "opacity-30",
                    s.saldo > 0 ? "text-primary" : "text-destructive"
                  )}
                  strokeWidth={1.5}
                />
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="space-y-4">
        <h2 className="font-serif text-lg font-light text-foreground/80">
          Histórico de movimentações
        </h2>

        <div className="border border-border/40 rounded-sm overflow-hidden">
          <div className="grid grid-cols-[auto_1fr_auto_auto_auto] gap-4 px-5 py-2.5 bg-secondary/40 border-b border-border/30">
            {["Tipo", "Material", "Quantidade", "Valor total", "Data"].map((h, i) => (
              <span key={i} className="text-xs uppercase tracking-widest text-muted-foreground/60">{h}</span>
            ))}
          </div>

          {loading && (
            <div className="flex items-center justify-center gap-2 py-16 text-muted-foreground/40">
              <Loader2 size={14} strokeWidth={1.5} className="animate-spin" />
              <span className="text-sm">Carregando movimentações...</span>
            </div>
          )}

          {!loading && erro && (
            <div className="flex items-center justify-center gap-2 py-16 text-destructive/60">
              <AlertCircle size={14} strokeWidth={1.5} />
              <span className="text-sm">{erro}</span>
            </div>
          )}

          {!loading && !erro && movs.length === 0 && (
            <div className="flex flex-col items-center justify-center gap-2 py-16 text-muted-foreground/40">
              <AlertCircle size={18} strokeWidth={1.5} />
              <span className="text-sm">Nenhuma movimentação registrada</span>
            </div>
          )}

          {!loading && !erro && movs.map((m, i) => (
            <div key={m.id}>
              <div
                onClick={() => setExpandido(expandido === m.id ? null : m.id)}
                className={cn(
                  "grid grid-cols-[auto_1fr_auto_auto_auto] gap-4 px-5 py-3.5 items-center cursor-pointer",
                  "hover:bg-secondary/30 transition-colors",
                  i < movs.length - 1 && expandido !== m.id && "border-b border-border/20",
                  expandido === m.id && "bg-secondary/20"
                )}
              >
                <div className={cn(
                  "flex items-center gap-1.5 text-xs font-medium whitespace-nowrap",
                  m.tipo === "ENTRADA" ? "text-emerald-500/80" :
                  m.tipo === "SAIDA"   ? "text-destructive/70" :
                  "text-amber-500/80"
                )}>
                  {m.tipo === "ENTRADA"
                    ? <TrendingUp size={12} strokeWidth={2} />
                    : m.tipo === "SAIDA"
                    ? <TrendingDown size={12} strokeWidth={2} />
                    : <Minus size={12} strokeWidth={2} />
                  }
                  {m.tipo}
                </div>

                <div className="min-w-0">
                  <div className="text-sm text-foreground/80 truncate">{m.materialNome}</div>
                  <div className="text-xs text-muted-foreground/40 truncate mt-0.5">
                    {ORIGEM_LABEL[m.origem] ?? m.origem}
                  </div>
                </div>

                <span className="text-sm font-mono tabular-nums text-foreground/70 whitespace-nowrap">
                  {fmt(m.quantidadeGramas)} g
                </span>

                <span className="text-sm font-mono tabular-nums text-foreground/70 whitespace-nowrap">
                  {m.valorTotal != null ? `R$ ${fmt(m.valorTotal, 2)}` : "—"}
                </span>

                <span className="text-xs text-muted-foreground/50 whitespace-nowrap">
                  {formatDate(m.createdAt)}
                </span>
              </div>

              {expandido === m.id && (
                <div className="px-5 py-3 bg-secondary/10 border-b border-border/20 text-xs text-muted-foreground/60 space-y-1">
                  <div className="flex gap-4">
                    <span>
                      <span className="uppercase tracking-wider text-muted-foreground/40 mr-1.5">ID</span>
                      <span className="font-mono">{m.id}</span>
                    </span>
                    {m.valorPorGrama != null && (
                      <span>
                        <span className="uppercase tracking-wider text-muted-foreground/40 mr-1.5">Valor/g</span>
                        R$ {fmt(m.valorPorGrama, 2)}
                      </span>
                    )}
                  </div>
                  {m.observacao && (
                    <div>
                      <span className="uppercase tracking-wider text-muted-foreground/40 mr-1.5">Obs.</span>
                      {m.observacao}
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>

        {!loading && !erro && movs.length > 0 && (
          <p className="text-xs text-muted-foreground/40">
            {movs.length} movimentação{movs.length !== 1 ? "ões" : ""} registrada{movs.length !== 1 ? "s" : ""}
          </p>
        )}
      </div>
    </div>
  )
}