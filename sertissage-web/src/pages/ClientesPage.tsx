import { useEffect, useState } from "react"
import {
  Users, Plus, Search, RotateCcw, AlertCircle,
  Loader2, X, Phone, Mail, FileText, ChevronRight
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

// ─── Tipos ──────────────────────────────────────────────────────────────────

interface ClientesPageProps {
  token: string
}

interface Cliente {
  id: string
  nome: string
  telefone: string | null
  email: string | null
  observacao: string | null
  createdAt: string
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString("pt-BR", {
    day: "2-digit", month: "short", year: "2-digit"
  })
}

// Iniciais para avatar
function iniciais(nome: string) {
  return nome.split(" ").slice(0, 2).map((w) => w[0]).join("").toUpperCase()
}

// Cor determinística baseada no nome
const AVATAR_COLORS = [
  "bg-primary/20 text-primary",
  "bg-blue-500/15 text-blue-400",
  "bg-emerald-500/15 text-emerald-400",
  "bg-amber-500/15 text-amber-400",
  "bg-purple-500/15 text-purple-400",
]
function avatarColor(nome: string) {
  const idx = nome.charCodeAt(0) % AVATAR_COLORS.length
  return AVATAR_COLORS[idx]
}

// ─── Componente ─────────────────────────────────────────────────────────────

export default function ClientesPage({ token }: ClientesPageProps) {
  const [clientes, setClientes]   = useState<Cliente[]>([])
  const [loading, setLoading]     = useState(true)
  const [erro, setErro]           = useState<string | null>(null)
  const [busca, setBusca]         = useState("")
  const [selecionado, setSelecionado] = useState<Cliente | null>(null)

  const headers = { Authorization: `Bearer ${token}`, "Content-Type": "application/json" }

  const carregar = async (query?: string) => {
    setLoading(true)
    setErro(null)
    try {
      const url = query
        ? `/api/clientes/buscar?nome=${encodeURIComponent(query)}`
        : "/api/clientes"
      const res = await fetch(url, { headers })
      if (!res.ok) throw new Error("Erro ao carregar clientes")
      setClientes(await res.json())
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : "Erro desconhecido")
    } finally {
      setLoading(false) }
  }

  useEffect(() => { carregar() }, [token])

  // Busca ao digitar (debounce simples)
  useEffect(() => {
    const t = setTimeout(() => {
      carregar(busca.trim() || undefined)
    }, 400)
    return () => clearTimeout(t)
  }, [busca])

  return (
    <div className="space-y-6 max-w-6xl">

      {/* Cabeçalho */}
      <div className="flex items-end justify-between">
        <div className="space-y-1">
          <h1 className="font-serif text-3xl font-light text-foreground">Clientes</h1>
          <p className="text-sm text-muted-foreground uppercase tracking-widest">
            Carteira de clientes
          </p>
        </div>
        <Button className="bg-primary hover:bg-primary/90 text-primary-foreground tracking-wide gap-2 rounded-sm">
          <Plus size={14} strokeWidth={2} /> Novo Cliente
        </Button>
      </div>

      {/* Busca + reload */}
      <div className="flex gap-3 items-center">
        <div className="relative flex-1 max-w-md">
          <Search size={13} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground/50" strokeWidth={1.5} />
          <input
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            placeholder="Buscar por nome..."
            className={cn(
              "w-full bg-secondary/60 border border-border/50 rounded-sm pl-8 pr-4 py-2",
              "text-sm text-foreground placeholder:text-muted-foreground/40",
              "focus:outline-none focus:border-primary/60 transition-colors",
            )}
          />
          {busca && (
            <button
              onClick={() => setBusca("")}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground/40 hover:text-muted-foreground transition-colors"
            >
              <X size={12} />
            </button>
          )}
        </div>
        <button
          onClick={() => carregar(busca.trim() || undefined)}
          disabled={loading}
          className="p-2 rounded-sm border border-border/40 text-muted-foreground/60 hover:text-foreground hover:border-border/70 transition-colors"
        >
          <RotateCcw size={13} strokeWidth={1.5} className={cn(loading && "animate-spin")} />
        </button>
      </div>

      {/* Layout dividido: lista + detalhe */}
      <div className="flex gap-5 h-full">

        {/* ── Lista ────────────────────────────────────────────────────── */}
        <div className={cn(
          "border border-border/40 rounded-sm overflow-hidden flex-1 min-w-0",
          selecionado && "hidden md:flex md:flex-col"
        )}>

          {/* Header */}
          <div className="grid grid-cols-[auto_1fr_auto] gap-4 px-5 py-2.5 bg-secondary/40 border-b border-border/30">
            {["", "Nome / Email", "Desde"].map((h, i) => (
              <span key={i} className="text-xs uppercase tracking-widest text-muted-foreground/60">{h}</span>
            ))}
          </div>

          {/* Loading */}
          {loading && (
            <div className="flex items-center justify-center gap-2 py-16 text-muted-foreground/40">
              <Loader2 size={14} strokeWidth={1.5} className="animate-spin" />
              <span className="text-sm">Carregando clientes...</span>
            </div>
          )}

          {/* Erro */}
          {!loading && erro && (
            <div className="flex items-center justify-center gap-2 py-16 text-destructive/60">
              <AlertCircle size={14} strokeWidth={1.5} />
              <span className="text-sm">{erro}</span>
            </div>
          )}

          {/* Vazio */}
          {!loading && !erro && clientes.length === 0 && (
            <div className="flex flex-col items-center justify-center gap-2 py-16 text-muted-foreground/40">
              <Users size={20} strokeWidth={1.5} />
              <span className="text-sm">
                {busca ? "Nenhum cliente encontrado" : "Nenhum cliente cadastrado"}
              </span>
            </div>
          )}

          {/* Linhas */}
          {!loading && !erro && clientes.map((c, i) => (
            <div
              key={c.id}
              onClick={() => setSelecionado(selecionado?.id === c.id ? null : c)}
              className={cn(
                "grid grid-cols-[auto_1fr_auto] gap-4 px-5 py-3.5 items-center cursor-pointer",
                "hover:bg-secondary/30 transition-colors",
                i < clientes.length - 1 && "border-b border-border/20",
                selecionado?.id === c.id && "bg-primary/5 border-l-2 border-l-primary/50"
              )}
            >
              {/* Avatar */}
              <div className={cn(
                "w-8 h-8 rounded-sm flex items-center justify-center text-xs font-medium shrink-0",
                avatarColor(c.nome)
              )}>
                {iniciais(c.nome)}
              </div>

              {/* Nome + email */}
              <div className="min-w-0">
                <div className="text-sm text-foreground/85 truncate">{c.nome}</div>
                {c.email && (
                  <div className="text-xs text-muted-foreground/50 truncate mt-0.5">{c.email}</div>
                )}
              </div>

              {/* Data */}
              <span className="text-xs text-muted-foreground/50 whitespace-nowrap">
                {formatDate(c.createdAt)}
              </span>
            </div>
          ))}
        </div>

        {/* ── Painel de Detalhe ─────────────────────────────────────────── */}
        {selecionado && (
          <div className="w-full md:w-72 shrink-0 border border-border/40 rounded-sm overflow-hidden flex flex-col">

            {/* Header do painel */}
            <div className="px-5 py-4 border-b border-border/30 flex items-start justify-between bg-secondary/20">
              <div className="flex items-center gap-3">
                <div className={cn(
                  "w-10 h-10 rounded-sm flex items-center justify-center text-sm font-medium",
                  avatarColor(selecionado.nome)
                )}>
                  {iniciais(selecionado.nome)}
                </div>
                <div>
                  <div className="text-sm font-medium text-foreground/90">{selecionado.nome}</div>
                  <div className="text-xs text-muted-foreground/50">
                    Cliente desde {formatDate(selecionado.createdAt)}
                  </div>
                </div>
              </div>
              <button
                onClick={() => setSelecionado(null)}
                className="text-muted-foreground/40 hover:text-muted-foreground transition-colors mt-0.5"
              >
                <X size={14} strokeWidth={1.5} />
              </button>
            </div>

            {/* Informações */}
            <div className="flex-1 px-5 py-4 space-y-4">
              {selecionado.telefone && (
                <div className="flex items-center gap-2.5">
                  <Phone size={13} className="text-muted-foreground/40" strokeWidth={1.5} />
                  <span className="text-sm text-foreground/70">{selecionado.telefone}</span>
                </div>
              )}
              {selecionado.email && (
                <div className="flex items-center gap-2.5">
                  <Mail size={13} className="text-muted-foreground/40" strokeWidth={1.5} />
                  <span className="text-sm text-foreground/70 truncate">{selecionado.email}</span>
                </div>
              )}
              {selecionado.observacao && (
                <div className="flex items-start gap-2.5">
                  <FileText size={13} className="text-muted-foreground/40 mt-0.5 shrink-0" strokeWidth={1.5} />
                  <span className="text-xs text-muted-foreground/60 leading-relaxed">
                    {selecionado.observacao}
                  </span>
                </div>
              )}

              {!selecionado.telefone && !selecionado.email && !selecionado.observacao && (
                <p className="text-xs text-muted-foreground/40">Nenhum dado de contato registrado.</p>
              )}
            </div>

            {/* Ações */}
            <div className="px-5 py-4 border-t border-border/30 space-y-2">
              <button className="w-full flex items-center justify-between text-sm text-foreground/70 hover:text-primary transition-colors py-1">
                <span>Ver pedidos do cliente</span>
                <ChevronRight size={13} strokeWidth={1.5} />
              </button>
              <button className="w-full flex items-center justify-between text-sm text-muted-foreground/50 hover:text-muted-foreground transition-colors py-1">
                <span>Editar cadastro</span>
                <ChevronRight size={13} strokeWidth={1.5} />
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Rodapé */}
      {!loading && !erro && (
        <p className="text-xs text-muted-foreground/40">
          {clientes.length} cliente{clientes.length !== 1 ? "s" : ""}
          {busca ? ` para "${busca}"` : " cadastrado" + (clientes.length !== 1 ? "s" : "")}
        </p>
      )}
    </div>
  )
}