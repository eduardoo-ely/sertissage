import { useEffect, useState } from "react"
import {
  Package, Layers, Users, TrendingUp, TrendingDown,
  ArrowRight, Clock, CheckCircle2, AlertCircle
} from "lucide-react"
import { cn } from "@/lib/utils"

// ─── Tipos ──────────────────────────────────────────────────────────────────

interface DashboardProps {
  token: string
  empresaId: string
  onNavigate: (page: "pedidos" | "estoque" | "clientes") => void
}

interface MetricCard {
  label: string
  value: string | number
  unit?: string
  trend?: { label: string; positive: boolean }
  icon: typeof Package
  action?: { label: string; onClick: () => void }
}

interface PedidoRecente {
  id: string
  clienteNome: string
  status: string
  tipoPedido: string
  createdAt: string
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

const STATUS_LABEL: Record<string, string> = {
  ORCAMENTO:        "Orçamento",
  AGUARDANDO_SINAL: "Aguard. sinal",
  APROVADO:         "Aprovado",
  EM_PRODUCAO:      "Em produção",
  FINALIZADO:       "Finalizado",
  CANCELADO:        "Cancelado",
}

const STATUS_COLOR: Record<string, string> = {
  ORCAMENTO:        "text-muted-foreground",
  AGUARDANDO_SINAL: "text-amber-500/80",
  APROVADO:         "text-blue-400/80",
  EM_PRODUCAO:      "text-primary/90",
  FINALIZADO:       "text-emerald-500/80",
  CANCELADO:        "text-destructive/70",
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("pt-BR", {
    day: "2-digit", month: "short",
  })
}

// ─── Componente ─────────────────────────────────────────────────────────────

export default function DashboardPage({ token, onNavigate }: DashboardProps) {
  const [pedidos, setPedidos] = useState<PedidoRecente[]>([])
  const [loading, setLoading] = useState(true)

  const headers = { Authorization: `Bearer ${token}`, "Content-Type": "application/json" }

  useEffect(() => {
    const load = async () => {
      try {
        const res = await fetch("/api/pedidos", { headers })
        if (res.ok) {
          const data = await res.json()
          setPedidos(data.slice(0, 5))
        }
      } catch { /* silently fail — mostra dados placeholder */ }
      finally { setLoading(false) }
    }
    load()
  }, [token])

  // Métricas derivadas dos pedidos carregados
  const ativos = pedidos.filter(
    (p) => !["FINALIZADO", "CANCELADO"].includes(p.status)
  ).length
  const emProducao = pedidos.filter((p) => p.status === "EM_PRODUCAO").length
  const finalizados = pedidos.filter((p) => p.status === "FINALIZADO").length

  const metrics: MetricCard[] = [
    {
      label: "Pedidos ativos",
      value: loading ? "—" : ativos,
      icon: Package,
      trend: emProducao > 0 ? { label: `${emProducao} em produção`, positive: true } : undefined,
      action: { label: "Ver pedidos", onClick: () => onNavigate("pedidos") },
    },
    {
      label: "Finalizados",
      value: loading ? "—" : finalizados,
      icon: CheckCircle2,
      trend: { label: "neste período", positive: true },
      action: { label: "Ver histórico", onClick: () => onNavigate("pedidos") },
    },
    {
      label: "Estoque",
      value: "—",
      unit: "g",
      icon: Layers,
      trend: { label: "consultar saldos", positive: true },
      action: { label: "Ver estoque", onClick: () => onNavigate("estoque") },
    },
    {
      label: "Clientes",
      value: "—",
      icon: Users,
      action: { label: "Ver carteira", onClick: () => onNavigate("clientes") },
    },
  ]

  return (
    <div className="space-y-8 max-w-6xl">

      {/* Cabeçalho da página */}
      <div className="space-y-1">
        <h1 className="font-serif text-3xl font-light text-foreground">
          Visão Geral
        </h1>
        <p className="text-sm text-muted-foreground uppercase tracking-widest">
          Dashboard Operacional
        </p>
      </div>

      {/* ── Grid de Métricas ─────────────────────────────────────────────── */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        {metrics.map((m) => {
          const Icon = m.icon
          return (
            <div
              key={m.label}
              className="bg-card border border-border/50 rounded-sm p-5 flex flex-col gap-3 relative overflow-hidden group hover:border-border/80 transition-colors"
            >
              {/* Linha de topo animada */}
              <div className="absolute top-0 left-0 w-full h-px bg-gradient-to-r from-transparent via-primary/40 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500" />

              <div className="flex justify-between items-start">
                <span className="text-xs uppercase tracking-widest text-muted-foreground/70">
                  {m.label}
                </span>
                <Icon size={14} className="text-primary/60" strokeWidth={1.5} />
              </div>

              <div className="flex items-baseline gap-1">
                <span className="font-serif text-3xl font-light text-foreground">
                  {m.value}
                </span>
                {m.unit && (
                  <span className="text-base text-muted-foreground">{m.unit}</span>
                )}
              </div>

              {m.trend && (
                <div className={cn("flex items-center gap-1 text-xs mt-auto",
                  m.trend.positive ? "text-emerald-500/80" : "text-destructive/70"
                )}>
                  {m.trend.positive
                    ? <TrendingUp size={11} />
                    : <TrendingDown size={11} />
                  }
                  {m.trend.label}
                </div>
              )}

              {m.action && (
                <button
                  onClick={m.action.onClick}
                  className="flex items-center gap-1 text-xs text-muted-foreground/50 hover:text-primary transition-colors mt-auto w-fit"
                >
                  {m.action.label}
                  <ArrowRight size={11} />
                </button>
              )}
            </div>
          )
        })}
      </div>

      {/* ── Pedidos Recentes ─────────────────────────────────────────────── */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="font-serif text-xl font-light text-foreground/90">
            Pedidos recentes
          </h2>
          <button
            onClick={() => onNavigate("pedidos")}
            className="flex items-center gap-1 text-xs text-muted-foreground/60 hover:text-primary transition-colors"
          >
            Ver todos <ArrowRight size={11} />
          </button>
        </div>

        <div className="border border-border/40 rounded-sm overflow-hidden">
          {/* Cabeçalho da tabela */}
          <div className="grid grid-cols-[1fr_1fr_auto_auto] gap-4 px-5 py-2.5 bg-secondary/40 border-b border-border/30">
            {["Cliente", "Tipo", "Status", "Data"].map((h) => (
              <span key={h} className="text-xs uppercase tracking-widest text-muted-foreground/60">
                {h}
              </span>
            ))}
          </div>

          {/* Linhas */}
          {loading ? (
            <div className="flex items-center justify-center gap-2 py-10 text-muted-foreground/40">
              <Clock size={14} strokeWidth={1.5} className="animate-pulse" />
              <span className="text-sm">Carregando...</span>
            </div>
          ) : pedidos.length === 0 ? (
            <div className="flex items-center justify-center gap-2 py-10 text-muted-foreground/40">
              <AlertCircle size={14} strokeWidth={1.5} />
              <span className="text-sm">Nenhum pedido encontrado</span>
            </div>
          ) : (
            pedidos.map((p, i) => (
              <div
                key={p.id}
                className={cn(
                  "grid grid-cols-[1fr_1fr_auto_auto] gap-4 px-5 py-3.5 items-center",
                  "hover:bg-secondary/30 transition-colors cursor-pointer",
                  i < pedidos.length - 1 && "border-b border-border/20"
                )}
              >
                <span className="text-sm text-foreground/80 truncate">{p.clienteNome}</span>
                <span className="text-sm text-muted-foreground/70 truncate">
                  {p.tipoPedido === "FABRICACAO" ? "Fabricação" : "Conserto"}
                </span>
                <span className={cn("text-xs font-medium", STATUS_COLOR[p.status] ?? "text-muted-foreground")}>
                  {STATUS_LABEL[p.status] ?? p.status}
                </span>
                <span className="text-xs text-muted-foreground/50 text-right">
                  {formatDate(p.createdAt)}
                </span>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  )
}