import { Button } from "./components/ui/button"
import { Diamond, Package, TrendingUp, Users, ArrowRight } from "lucide-react"

function App() {
  return (
    <div className="min-h-screen bg-background p-8 md:p-12 lg:p-16 flex flex-col gap-10">
      
      {/* Header Minimalista */}
      <header className="flex justify-between items-end border-b border-border/50 pb-6">
        <div className="space-y-1">
          <h1 className="text-4xl font-serif font-light tracking-wide text-foreground">
            Sertissage
          </h1>
          <p className="text-sm text-muted-foreground uppercase tracking-widest">
            Maison de Joaillerie
          </p>
        </div>
        <div className="flex items-center gap-4">
          <Button variant="outline" className="border-border/50 hover:border-primary/50 text-foreground font-light">
            Catálogo
          </Button>
          <Button className="bg-primary hover:bg-primary/90 text-primary-foreground font-medium tracking-wide">
            Novo Pedido
          </Button>
        </div>
      </header>

      {/* Grid de Métricas (Luxurious Dashboard Cards) */}
      <main className="grid grid-cols-1 md:grid-cols-3 gap-6">
        
        {/* Card 1: Pedidos */}
        <div className="bg-card border border-border/50 p-6 rounded-md shadow-2xl shadow-black/50 flex flex-col gap-4 relative overflow-hidden group">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-primary/30 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
          <div className="flex justify-between items-center text-muted-foreground">
            <span className="text-xs uppercase tracking-wider">Pedidos Ativos</span>
            <Package size={16} className="text-primary/70" />
          </div>
          <div className="text-3xl font-serif font-light">12</div>
          <div className="text-xs text-emerald-500/80 flex items-center gap-1 mt-auto">
            <TrendingUp size={12} /> +2 esta semana
          </div>
        </div>

        {/* Card 2: Estoque Ouro */}
        <div className="bg-card border border-border/50 p-6 rounded-md shadow-2xl shadow-black/50 flex flex-col gap-4 relative overflow-hidden group">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-primary/30 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
          <div className="flex justify-between items-center text-muted-foreground">
            <span className="text-xs uppercase tracking-wider">Reserva de Ouro 18k</span>
            <Diamond size={16} className="text-primary/70" />
          </div>
          <div className="text-3xl font-serif font-light flex items-baseline gap-1">
            150<span className="text-lg text-muted-foreground">g</span>
          </div>
          <div className="text-xs text-muted-foreground mt-auto flex items-center gap-1 cursor-pointer hover:text-primary transition-colors">
            Ajustar saldo <ArrowRight size={12} />
          </div>
        </div>

        {/* Card 3: Clientes */}
        <div className="bg-card border border-border/50 p-6 rounded-md shadow-2xl shadow-black/50 flex flex-col gap-4 relative overflow-hidden group">
          <div className="absolute top-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-primary/30 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
          <div className="flex justify-between items-center text-muted-foreground">
            <span className="text-xs uppercase tracking-wider">Clientes Premium</span>
            <Users size={16} className="text-primary/70" />
          </div>
          <div className="text-3xl font-serif font-light">48</div>
          <div className="text-xs text-muted-foreground mt-auto flex items-center gap-1 cursor-pointer hover:text-primary transition-colors">
            Ver carteira <ArrowRight size={12} />
          </div>
        </div>

      </main>
    </div>
  )
}

export default App