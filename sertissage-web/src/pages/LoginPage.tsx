import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Diamond, Eye, EyeOff, Loader2 } from "lucide-react"
import { cn } from "@/lib/utils"

// ─── Tipos ──────────────────────────────────────────────────────────────────

interface LoginForm {
  email: string
  senha: string
}

interface LoginPageProps {
  onLogin?: (form: LoginForm) => Promise<void>
}

// ─── Componente ─────────────────────────────────────────────────────────────

export default function LoginPage({ onLogin }: LoginPageProps) {
  const [form, setForm] = useState<LoginForm>({ email: "", senha: "" })
  const [showSenha, setShowSenha] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!form.email || !form.senha) return

    setError(null)
    setLoading(true)

    try {
      await onLogin?.(form)
    } catch (err: any) {
      const msg = err.response?.data?.message || "Email ou senha inválidos."
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-background flex">

      {/* ── Painel Esquerdo: Marca ─────────────────────────────────────── */}
      <div className="hidden lg:flex lg:w-1/2 flex-col justify-between p-16 border-r border-border/40 relative overflow-hidden">

        {/* Textura de fundo sutil */}
        <div
          className="absolute inset-0 opacity-[0.03]"
          style={{
            backgroundImage: `radial-gradient(circle at 1px 1px, hsl(43 60% 55%) 1px, transparent 0)`,
            backgroundSize: "32px 32px",
          }}
        />

        {/* Ornamento geométrico central */}
        <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
          <div className="relative w-96 h-96">
            <div className="absolute inset-0 border border-primary/10 rotate-45" />
            <div className="absolute inset-8 border border-primary/8 rotate-45" />
            <div className="absolute inset-16 border border-primary/6 rotate-45" />
            <div className="absolute inset-24 border border-primary/5 rotate-45" />
            <div className="absolute inset-0 flex items-center justify-center">
              <div
                className="w-32 h-32 rotate-45 opacity-[0.06]"
                style={{
                  background: "radial-gradient(ellipse at center, hsl(43 60% 55%), transparent)",
                }}
              />
            </div>
          </div>
        </div>

        {/* Logo */}
        <div className="relative z-10 flex items-center gap-3">
          <Diamond size={20} className="text-primary" strokeWidth={1.5} />
          <span className="font-serif text-xl text-foreground/90 tracking-wide">
            Sertissage
          </span>
        </div>

        {/* Tagline central */}
        <div className="relative z-10 space-y-6">
          <div className="w-8 h-px bg-primary/60" />
          <h2 className="font-serif text-4xl font-light text-foreground/90 leading-snug">
            Da bancada<br />
            ao sistema.
          </h2>
          <p className="text-sm text-muted-foreground leading-relaxed max-w-xs">
            Controle de pedidos, estoque e finanças para ourivesarias que exigem precisão em cada detalhe.
          </p>
        </div>

        {/* Rodapé */}
        <p className="relative z-10 text-xs text-muted-foreground/50 tracking-widest uppercase">
          Sistema de Gestão — 2026
        </p>
      </div>

      {/* ── Painel Direito: Formulário ────────────────────────────────── */}
      <div className="flex-1 flex flex-col items-center justify-center p-8 lg:p-16">

        {/* Logo mobile */}
        <div className="lg:hidden flex items-center gap-2 mb-12">
          <Diamond size={18} className="text-primary" strokeWidth={1.5} />
          <span className="font-serif text-xl text-foreground/90 tracking-wide">Sertissage</span>
        </div>

        {/* Card de login */}
        <div className="w-full max-w-sm space-y-8">

          <div className="space-y-2">
            <h1 className="font-serif text-3xl font-light text-foreground">
              Acesso
            </h1>
            <p className="text-sm text-muted-foreground">
              Entre com suas credenciais para continuar.
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">

            <div className="space-y-2">
              <label htmlFor="email" className="text-xs uppercase tracking-widest text-muted-foreground">
                Email
              </label>
              <input
                id="email"
                type="email"
                autoComplete="email"
                placeholder="seu@email.com"
                value={form.email}
                onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
                disabled={loading}
                className={cn(
                  "w-full bg-secondary/60 border border-border/60 rounded-sm px-4 py-3",
                  "text-sm text-foreground placeholder:text-muted-foreground/40",
                  "focus:outline-none focus:border-primary/60 focus:bg-secondary/80 transition-all disabled:opacity-50",
                )}
              />
            </div>

            <div className="space-y-2">
              <label htmlFor="senha" className="text-xs uppercase tracking-widest text-muted-foreground">
                Senha
              </label>
              <div className="relative">
                <input
                  id="senha"
                  type={showSenha ? "text" : "password"}
                  autoComplete="current-password"
                  placeholder="••••••••"
                  value={form.senha}
                  onChange={(e) => setForm((f) => ({ ...f, senha: e.target.value }))}
                  disabled={loading}
                  className={cn(
                    "w-full bg-secondary/60 border border-border/60 rounded-sm px-4 py-3 pr-11",
                    "text-sm text-foreground placeholder:text-muted-foreground/40",
                    "focus:outline-none focus:border-primary/60 focus:bg-secondary/80 transition-all disabled:opacity-50",
                  )}
                />
                <button
                  type="button"
                  onClick={() => setShowSenha((v) => !v)}
                  tabIndex={-1}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground/50 hover:text-muted-foreground transition-colors"
                >
                  {showSenha ? <EyeOff size={15} strokeWidth={1.5} /> : <Eye size={15} strokeWidth={1.5} />}
                </button>
              </div>
            </div>

            {error && (
              <div className="text-xs text-destructive/90 bg-destructive/10 border border-destructive/20 rounded-sm px-4 py-3">
                {error}
              </div>
            )}

            <Button
              type="submit"
              disabled={loading || !form.email || !form.senha}
              className="w-full bg-primary hover:bg-primary/90 text-primary-foreground font-medium tracking-wide h-11 rounded-sm transition-all duration-200"
            >
              {loading ? (
                <div className="flex items-center gap-2">
                  <Loader2 size={14} className="animate-spin" /> 
                  <span>Entrando...</span>
                </div>
              ) : "Entrar"}
            </Button>
          </form>

          <div className="space-y-4">
            <div className="flex items-center gap-3">
              <div className="flex-1 h-px bg-border/40" />
              <span className="text-xs text-muted-foreground/40 uppercase tracking-widest">dev</span>
              <div className="flex-1 h-px bg-border/40" />
            </div>
            <div className="bg-secondary/40 border border-border/30 rounded-sm px-4 py-3 space-y-1">
              <p className="text-xs text-muted-foreground/60 font-mono">dev@sertissage.com</p>
              <p className="text-xs text-muted-foreground/60 font-mono">sertissage123</p>
            </div>
          </div>

        </div>
      </div>
    </div>
  )
}