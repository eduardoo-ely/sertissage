import { useState } from "react"
import LoginPage from "./pages/LoginPage"
import AppLayout, { type Page } from "./layouts/AppLayout"
import DashboardPage from "./pages/DashboardPage"
import PedidosPage from "./pages/PedidosPage"
import EstoquePage from "./pages/EstoquePage"
import ClientesPage from "./pages/ClientesPage"

// ─── Tipos ──────────────────────────────────────────────────────────────────

interface AuthState {
  token: string
  usuarioId: string
  nome: string
  email: string
  empresaId: string
}

// ─── API ─────────────────────────────────────────────────────────────────────

async function loginApi(email: string, senha: string): Promise<AuthState> {
  const res = await fetch("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, senha }),
  })
  if (!res.ok) throw new Error("Credenciais inválidas")
  const d = await res.json()
  return {
    token:     d.token,
    usuarioId: d.usuarioId,
    nome:      d.nome,
    email:     d.email,
    empresaId: d.empresaId,
  }
}

// ─── App ─────────────────────────────────────────────────────────────────────

export default function App() {
  const [auth, setAuth]         = useState<AuthState | null>(null)
  const [page, setPage]         = useState<Page>("dashboard")

  const handleLogin = async ({ email, senha }: { email: string; senha: string }) => {
    const state = await loginApi(email, senha)
    setAuth(state)
  }

  if (!auth) {
    return <LoginPage onLogin={handleLogin} />
  }

  const renderPage = () => {
    switch (page) {
      case "dashboard":
        return (
          <DashboardPage
            token={auth.token}
            empresaId={auth.empresaId}
            onNavigate={(p) => setPage(p)}
          />
        )
      case "pedidos":
        return <PedidosPage token={auth.token} />
      case "estoque":
        return <EstoquePage token={auth.token} />
      case "clientes":
        return <ClientesPage token={auth.token} />
    }
  }

  return (
    <AppLayout
      currentPage={page}
      onNavigate={setPage}
      nomeUsuario={auth.nome}
      onLogout={() => setAuth(null)}
    >
      {renderPage()}
    </AppLayout>
  )
}