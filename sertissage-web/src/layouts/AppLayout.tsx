import { type ReactNode } from "react"
import { Diamond, LayoutDashboard, Package, Layers, Users, LogOut, ChevronRight } from "lucide-react"
import { cn } from "@/lib/utils"

// ─── Tipos ──────────────────────────────────────────────────────────────────

export type Page = "dashboard" | "pedidos" | "estoque" | "clientes"

interface NavItem {
  id: Page
  label: string
  icon: typeof LayoutDashboard
}

interface LayoutProps {
  children: ReactNode
  currentPage: Page
  onNavigate: (page: Page) => void
  nomeUsuario: string
  onLogout: () => void
}

// ─── Dados ───────────────────────────────────────────────────────────────────

const NAV_ITEMS: NavItem[] = [
  { id: "dashboard", label: "Dashboard",  icon: LayoutDashboard },
  { id: "pedidos",   label: "Pedidos",    icon: Package },
  { id: "estoque",   label: "Estoque",    icon: Layers },
  { id: "clientes",  label: "Clientes",   icon: Users },
]

// ─── Componente ─────────────────────────────────────────────────────────────

export default function AppLayout({
  children,
  currentPage,
  onNavigate,
  nomeUsuario,
  onLogout,
}: LayoutProps) {
  return (
    <div className="min-h-screen bg-background flex flex-col">

      {/* ── Top Navigation ──────────────────────────────────────────────── */}
      <header className="border-b border-border/40 bg-background/95 backdrop-blur-sm sticky top-0 z-40">
        <div className="px-6 h-14 flex items-center gap-8">

          {/* Logo */}
          <div className="flex items-center gap-2.5 shrink-0">
            <Diamond size={16} className="text-primary" strokeWidth={1.5} />
            <span className="font-serif text-lg text-foreground/90 tracking-wide">
              Sertissage
            </span>
          </div>

          {/* Divisor vertical */}
          <div className="h-5 w-px bg-border/60" />

          {/* Nav items */}
          <nav className="flex items-center gap-1 flex-1">
            {NAV_ITEMS.map((item) => {
              const Icon = item.icon
              const active = currentPage === item.id
              return (
                <button
                  key={item.id}
                  onClick={() => onNavigate(item.id)}
                  className={cn(
                    "flex items-center gap-2 px-3.5 py-1.5 rounded-sm text-sm transition-all duration-150",
                    active
                      ? "bg-primary/10 text-primary"
                      : "text-muted-foreground hover:text-foreground hover:bg-secondary/60"
                  )}
                >
                  <Icon size={14} strokeWidth={1.5} />
                  <span className={cn("hidden sm:inline", active && "font-medium")}>
                    {item.label}
                  </span>
                </button>
              )
            })}
          </nav>

          {/* Usuário + Logout */}
          <div className="flex items-center gap-3 shrink-0">
            <div className="hidden md:flex flex-col items-end">
              <span className="text-xs text-foreground/70 leading-none">{nomeUsuario}</span>
            </div>
            <button
              onClick={onLogout}
              className="flex items-center gap-1.5 text-xs text-muted-foreground/60 hover:text-muted-foreground transition-colors px-2 py-1 rounded-sm hover:bg-secondary/60"
            >
              <LogOut size={13} strokeWidth={1.5} />
              <span className="hidden sm:inline">Sair</span>
            </button>
          </div>
        </div>

        {/* Breadcrumb / título da página */}
        <div className="px-6 h-9 flex items-center gap-1.5 border-t border-border/20">
          <span className="text-xs text-muted-foreground/50 uppercase tracking-widest">
            Sertissage
          </span>
          <ChevronRight size={10} className="text-muted-foreground/30" />
          <span className="text-xs text-muted-foreground/80 uppercase tracking-widest">
            {NAV_ITEMS.find((n) => n.id === currentPage)?.label}
          </span>
        </div>
      </header>

      {/* ── Conteúdo ────────────────────────────────────────────────────── */}
      <main className="flex-1 p-6 md:p-8 lg:p-10">
        {children}
      </main>

    </div>
  )
}