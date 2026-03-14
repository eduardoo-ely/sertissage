import { useEffect, useState } from "react"
import {
  Plus, Search, ChevronRight, ChevronDown, RotateCcw,
  AlertCircle, Loader2, X
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

// ─── Tipos ──────────────────────────────────────────────────────────────────

interface PedidosPageProps {
  token: string
}

interface Pedido {
  id: string
  descricao: string | null
  tipoPedido: "FABRICACAO" | "CONSERTO"
  tipoPeca: string | null
  origem: string | null
  status: "ORCAMENTO" | "AGUARDANDO_SINAL" | "APROVADO" | "EM_PRODUCAO" | "FINALIZADO" | "CANCELADO"
  pesoGramas: number | null
  custoPorGrama: number | null
  precoCobrado: number | null
  margemBruta: number | null
  percentualMargem: number | null
  sinal: number | null
  clienteId: string
  clienteNome: string
  createdAt: string
  updatedAt: string | null
}

// ─── Constantes ──────────────────────────────────────────────────────────────

const STATUS_FLOW = [
  "ORCAMENTO",
  "AGUARDANDO_SINAL",
  "APROVADO",
  "EM_PRODUCAO",
  "FINALIZADO",
] as const

const STATUS_LABEL: Record<string, string> = {
  ORCAMENTO:        "Orçamento",
  AGUARDANDO_SINAL: "Ag. Sinal",
  APROVADO:         "Aprovado",
  EM_PRODUCAO:      "Em Produção",
  FINALIZADO:       "Finalizado",
  CANCELADO:        "Cancelado",
}

const STATUS_DOT: Record<string, string> = {
  ORCAMENTO:        "bg-muted-foreground/40",
  AGUARDANDO_SINAL: "bg-amber-400/80",
  APROVADO:         "bg-blue-400/80",
  EM_PRODUCAO:      "bg-primary/90",
  FINALIZADO:       "bg-emerald-500/80",
  CANCELADO:        "bg-destructive/60",
}

const ALL_STATUS = ["Todos", ...Object.keys(STATUS_LABEL)]

function fmt(v: number | null, decimals = 2) {
  if (v == null) return "—"
  return v.toLocaleString("pt-BR", { minimumFractionDigits: decimals, maximumFractionDigits: decimals })
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("pt-BR", { day: "2-digit", month: "short", year: "2-digit" })
}

// ─── Componente principal ────────────────────────────────────────────────────

export default function PedidosPage({ token }: PedidosPageProps) {
  const [pedidos, setPedidos] = useState<Pedido[]>([])
  const [loading, setLoading] = useState(true)
  const [erro, setErro] = useState<string | null>(null)
  const [filtroStatus, setFiltroStatus] = useState("Todos")
  const [busca, setBusca] = useState("")
  const [expandido, setExpandido] = useState<string | null>(null)
  const [acao, setAcao] = useState<string | null>(null) // pedido em ação

  const headers = { Authorization: `Bearer ${token}`, "Content-Type": "application/json" }

  const carregar = async () => {
    setLoading(true)
    setErro(null)
    try {
      const res = await fetch("/api/pedidos", { headers })
      if (!res.ok) throw new Error("Erro ao carregar pedidos")
      setPedidos(await res.json())
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : "Erro desconhecido")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { carregar() }, [token])

  const avancar = async (id: string) => {
    setAcao(id)
    try {
      const res = await fetch(`/api/pedidos/${id}/avancar`, { method: "PATCH", headers })
      if (!res.ok) {
        const err = await res.json()
        alert(err.message ?? "Erro ao avançar pedido")
        return
      }
      await carregar()
    } finally { setAcao(null) }
  }

  const cancelar = async (id: string) => {
    const motivo = window.prompt("Motivo do cancelamento:")
    if (!motivo) return
    setAcao(id)
    try {
      const res = await fetch(`/api/pedidos/${id}/cancelar`, {
        method: "PATCH", headers,
        body: JSON.stringify({ motivo }),
      })
      if (!res.ok) {
        const err = await res.json()
        alert(err.message ?? "Erro ao cancelar pedido")
        return
      }
      await carregar()
    } finally { setAcao(null) }
  }

  // Filtros
  const filtrados = pedidos.filter((p) => {
    const matchStatus = filtroStatus === "Todos" || p.status === filtroStatus
    const matchBusca = busca.trim() === "" ||
      p.clienteNome.toLowerCase().includes(busca.toLowerCase()) ||
      (p.descricao ?? "").toLowerCase().includes(busca.toLowerCase())
    return matchStatus && matchBusca
  })

  return (
    <div className="space-y-6 max-w-6xl">

      {/* Cabeçalho */}
      <div className="flex items-end justify-between">
        <div className="space-y-1">
          <h1 className="font-serif text-3xl font-light text-foreground">Pedidos</h1>
          <p className="text-sm text-muted-foreground uppercase tracking-widest">
            Gestão e rastreamento
          </p>
        </div>
        <Button className="bg-primary hover:bg-primary/90 text-primary-foreground tracking-wide gap-2 rounded-sm">
          <Plus size={14} strokeWidth={2} /> Novo Pedido
        </Button>
      </div>

      {/* Controles */}
      <div className="flex flex-wrap gap-3 items-center">
        {/* Busca */}
        <div className="relative flex-1 min-w-48">
          <Search size={13} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground/50" strokeWidth={1.5} />
          <input
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            placeholder="Buscar por cliente ou descrição..."
            className={cn(
              "w-full bg-secondary/60 border border-border/50 rounded-sm pl-8 pr-4 py-2",
              "text-sm text-foreground placeholder:text-muted-foreground/40",
              "focus:outline-none focus:border-primary/60 transition-colors",
            )}
          />
        </div>

        {/* Filtro status */}
        <div className="flex gap-1 flex-wrap">
          {["Todos", "ORCAMENTO", "EM_PRODUCAO", "FINALIZADO", "CANCELADO"].map((s) => (
            <button
              key={s}
              onClick={() => setFiltroStatus(s)}
              className={cn(
                "px-3 py-1.5 text-xs rounded-sm transition-colors border",
                filtroStatus === s
                  ? "bg-primary/15 border-primary/40 text-primary"
                  : "border-border/40 text-muted-foreground/60 hover:text-foreground hover:border-border/70"
              )}
            >
              {s === "Todos" ? "Todos" : STATUS_LABEL[s]}
            </button>
          ))}
        </div>

        {/* Reload */}
        <button
          onClick={carregar}
          disabled={loading}
          className="p-2 rounded-sm border border-border/40 text-muted-foreground/60 hover:text-foreground hover:border-border/70 transition-colors"
        >
          <RotateCcw size={13} strokeWidth={1.5} className={cn(loading && "animate-spin")} />
        </button>
      </div>

      {/* Tabela */}
      <div className="border border-border/40 rounded-sm overflow-hidden">
        {/* Header */}
        <div className="grid grid-cols-[1fr_auto_auto_auto_auto] gap-4 px-5 py-2.5 bg-secondary/40 border-b border-border/30">
          {["Cliente / Descrição", "Tipo", "Status", "Valor", ""].map((h, i) => (
            <span key={i} className="text-xs uppercase tracking-widest text-muted-foreground/60">{h}</span>
          ))}
        </div>

        {/* Estado: loading */}
        {loading && (
          <div className="flex items-center justify-center gap-2 py-16 text-muted-foreground/40">
            <Loader2 size={14} strokeWidth={1.5} className="animate-spin" />
            <span className="text-sm">Carregando pedidos...</span>
          </div>
        )}

        {/* Estado: erro */}
        {!loading && erro && (
          <div className="flex items-center justify-center gap-2 py-16 text-destructive/60">
            <AlertCircle size={14} strokeWidth={1.5} />
            <span className="text-sm">{erro}</span>
          </div>
        )}

        {/* Estado: vazio */}
        {!loading && !erro && filtrados.length === 0 && (
          <div className="flex flex-col items-center justify-center gap-2 py-16 text-muted-foreground/40">
            <AlertCircle size={18} strokeWidth={1.5} />
            <span className="text-sm">Nenhum pedido encontrado</span>
            {busca && (
              <button
                onClick={() => setBusca("")}
                className="flex items-center gap-1 text-xs hover:text-muted-foreground transition-colors mt-1"
              >
                <X size={11} /> Limpar busca
              </button>
            )}
          </div>
        )}

        {/* Linhas */}
        {!loading && !erro && filtrados.map((p, i) => (
          <div key={p.id}>
            {/* Linha principal */}
            <div
              onClick={() => setExpandido(expandido === p.id ? null : p.id)}
              className={cn(
                "grid grid-cols-[1fr_auto_auto_auto_auto] gap-4 px-5 py-3.5 items-center cursor-pointer",
                "hover:bg-secondary/30 transition-colors",
                i < filtrados.length - 1 && expandido !== p.id && "border-b border-border/20",
                expandido === p.id && "bg-secondary/20"
              )}
            >
              {/* Cliente + descrição */}
              <div className="min-w-0">
                <div className="text-sm text-foreground/85 truncate">{p.clienteNome}</div>
                {p.descricao && (
                  <div className="text-xs text-muted-foreground/50 truncate mt-0.5">{p.descricao}</div>
                )}
              </div>

              {/* Tipo */}
              <span className="text-xs text-muted-foreground/60 whitespace-nowrap">
                {p.tipoPedido === "FABRICACAO" ? "Fabricação" : "Conserto"}
              </span>

              {/* Status */}
              <div className="flex items-center gap-1.5 whitespace-nowrap">
                <div className={cn("w-1.5 h-1.5 rounded-full", STATUS_DOT[p.status])} />
                <span className="text-xs text-muted-foreground/80">
                  {STATUS_LABEL[p.status]}
                </span>
              </div>

              {/* Valor cobrado */}
              <span className="text-sm text-foreground/70 font-mono tabular-nums">
                {p.precoCobrado != null ? `R$ ${fmt(p.precoCobrado)}` : "—"}
              </span>

              {/* Chevron */}
              {expandido === p.id
                ? <ChevronDown size={13} className="text-muted-foreground/50" strokeWidth={1.5} />
                : <ChevronRight size={13} className="text-muted-foreground/30" strokeWidth={1.5} />
              }
            </div>

            {/* Painel expandido */}
            {expandido === p.id && (
              <div className={cn(
                "px-5 pb-5 pt-2 bg-secondary/10 border-b border-border/20",
                "grid grid-cols-1 md:grid-cols-[1fr_auto] gap-6"
              )}>

                {/* Detalhes financeiros */}
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                  {[
                    { label: "Peso",           value: p.pesoGramas != null ? `${fmt(p.pesoGramas, 3)} g` : "—" },
                    { label: "Custo / g",      value: p.custoPorGrama != null ? `R$ ${fmt(p.custoPorGrama)}` : "—" },
                    { label: "Preço cobrado",  value: p.precoCobrado != null ? `R$ ${fmt(p.precoCobrado)}` : "—" },
                    { label: "Margem bruta",   value: p.margemBruta != null ? `R$ ${fmt(p.margemBruta)}` : "—" },
                    { label: "Margem %",       value: p.percentualMargem != null ? `${fmt(p.percentualMargem)}%` : "—" },
                    { label: "Sinal",          value: p.sinal != null ? `R$ ${fmt(p.sinal)}` : "—" },
                    { label: "Origem",         value: p.origem ?? "—" },
                    { label: "Peça",           value: p.tipoPeca ?? "—" },
                    { label: "Criado em",      value: formatDate(p.createdAt) },
                  ].map((d) => (
                    <div key={d.label} className="space-y-0.5">
                      <span className="text-xs uppercase tracking-wider text-muted-foreground/50">
                        {d.label}
                      </span>
                      <div className="text-sm text-foreground/80 font-mono">{d.value}</div>
                    </div>
                  ))}
                </div>

                {/* Ações de status */}
                <div className="flex flex-col gap-2 items-end justify-start shrink-0">
                  {/* Barra de progresso */}
                  <div className="flex gap-1 mb-2">
                    {STATUS_FLOW.map((s, idx) => {
                      const current = STATUS_FLOW.indexOf(p.status as typeof STATUS_FLOW[number])
                      const done = idx <= current
                      return (
                        <div
                          key={s}
                          className={cn(
                            "h-1 w-8 rounded-full transition-colors",
                            done ? "bg-primary/70" : "bg-border/40"
                          )}
                        />
                      )
                    })}
                  </div>

                  {/* Botão avançar */}
                  {!["FINALIZADO", "CANCELADO"].includes(p.status) && (
                    <Button
                      size="sm"
                      onClick={(e) => { e.stopPropagation(); avancar(p.id) }}
                      disabled={acao === p.id}
                      className="bg-primary/90 hover:bg-primary text-primary-foreground rounded-sm text-xs h-8 gap-1.5"
                    >
                      {acao === p.id
                        ? <Loader2 size={12} className="animate-spin" />
                        : <ChevronRight size={12} />
                      }
                      Avançar para {STATUS_LABEL[
                        STATUS_FLOW[STATUS_FLOW.indexOf(p.status as typeof STATUS_FLOW[number]) + 1] ?? "FINALIZADO"
                      ]}
                    </Button>
                  )}

                  {/* Botão cancelar */}
                  {!["FINALIZADO", "CANCELADO"].includes(p.status) && (
                    <button
                      onClick={(e) => { e.stopPropagation(); cancelar(p.id) }}
                      disabled={acao === p.id}
                      className="text-xs text-destructive/50 hover:text-destructive/80 transition-colors"
                    >
                      Cancelar pedido
                    </button>
                  )}
                </div>

              </div>
            )}
          </div>
        ))}
      </div>

      {/* Rodapé de contagem */}
      {!loading && !erro && (
        <p className="text-xs text-muted-foreground/40">
          {filtrados.length} pedido{filtrados.length !== 1 ? "s" : ""}
          {filtroStatus !== "Todos" ? ` com status "${STATUS_LABEL[filtroStatus]}"` : " no total"}
        </p>
      )}
    </div>
  )
}