<div align="center">

<br/>

# 💎 Sertissage

### Sistema Web de Gestão Operacional e Controle de Estoque para Ourivesarias

<br/>

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![License](https://img.shields.io/badge/licença-acadêmica-blue?style=for-the-badge)

<br/>

> *Da planilha ao sistema — organização, rastreabilidade e controle real para ourivesarias.*

<br/>

</div>

---

## 📌 Sobre o Projeto

O **Sertissage** é um sistema web desenvolvido para organizar e estruturar a gestão de ourivesarias de pequeno e médio porte. Nasce da necessidade real de substituir planilhas desorganizadas e controles manuais por uma solução robusta, rastreável e preparada para crescer.

O projeto está sendo desenvolvido como parte das disciplinas de **Desenvolvimento Web** e **ABEX (2026/1)**, com objetivo de evoluir até se tornar a base de para uma aplicação comercializavel.

---

## 🎯 O Problema

Hoje, a maioria das ourivesarias opera com:

| Problema | Impacto |
|---|---|
| Planilhas desorganizadas | Inconsistência nos dados |
| Controle manual de estoque | Perdas não rastreadas |
| Margens fixas sem custo real | Prejuízo invisível |
| Falta de rastreabilidade | Decisões no escuro |

### O que o Sertissage resolve

```
✔ Organização centralizada dos pedidos
✔ Controle financeiro por movimentação
✔ Rastreabilidade completa de materiais
✔ Base sólida para tomada de decisão
✔ Segurança com isolamento multiempresa
```

---

## 🏗️ Arquitetura

### Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3 |
| Persistência | Spring Data JPA |
| Banco de Dados | PostgreSQL |

### Estrutura de Pacotes

```
br.com.sertissage
│
├── config          # Configurações gerais da aplicação
├── controller      # Endpoints REST
├── service         # Regras de negócio
├── repository      # Acesso a dados
├── domain
│   ├── entity      # Entidades JPA
│   └── enums       # Enumerações de domínio
├── dto             # Objetos de transferência de dados
├── exception       # Tratamento de exceções
└── security        # Autenticação e autorização
```

### Princípios Aplicados

- **Separação de responsabilidades** em camadas bem definidas
- **Regras de negócio** centralizadas na camada Service
- **Movimentações financeiras imutáveis** — sem edição, sem deleção
- **Multi-tenant lógico** com isolamento por `empresa_id`
- **Design extensível** para módulos futuros

---

## 🧠 Regras de Negócio

```
👤  Cada usuário pertence a uma única empresa
🏢  Todo dado de negócio carrega empresa_id
📦  Saldo de estoque é calculado exclusivamente por movimentações
🔒  Movimentações não podem ser editadas ou deletadas
⚙️  Pedido só gera saída de estoque ao entrar em EM_PRODUCAO
⛔  Estoque não pode ficar negativo
🚫  Cancelamento após produção não retorna material ao estoque
📊  Relatórios consideram apenas pedidos com status FINALIZADO
```

---

## 🔄 Fluxo do Pedido

```
ORCAMENTO
    │
    ▼
AGUARDANDO_SINAL
    │
    ▼
APROVADO
    │
    ▼
EM_PRODUCAO ──── (saída de estoque gerada aqui)
    │
    ▼
FINALIZADO
    │
    └──── (a qualquer momento) ──── CANCELADO
```

> O sistema valida todas as transições e executa automaticamente as regras de estoque associadas a cada mudança de estado.

---

## 📦 Modelo de Estoque

Controle por tipo de material: **OURO** e **PRATA**

O saldo é sempre calculado pela fórmula:

```
SALDO = Σ ENTRADAS + Σ AJUSTES − Σ SAÍDAS
```

> Não existe edição direta de saldo. Toda alteração passa por uma movimentação registrada e rastreável.

---

## 🚀 Roadmap

### ✅ MVP — 2026/1
- [x] Estrutura multiempresa (multi-tenant lógico)
- [ ] CRUD completo de clientes
- [ ] Controle de estoque por movimentações
- [ ] Máquina de estados para pedidos
- [ ] Seed inicial de dados para testes

### 🔮 Evolução — 2026/2
- [ ] Relatórios analíticos
- [ ] Dashboard operacional
- [ ] Métricas por canal de origem (Instagram, Loja, Leilão)
- [ ] Base para módulo de análise em Python (TCC)

---

## 🧪 Status do Projeto

```
🚧  MVP em desenvolvimento  —  2026/1
```

---

## 📁 Como Executar (em breve)

> Instruções de setup, variáveis de ambiente e execução serão adicionadas conforme o MVP avança.

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/sertissage.git

# Configure as variáveis de ambiente
cp .env.example .env

# Execute com Docker (em breve)
docker-compose up -d
```

---

## 👤 Autor

<div align="center">

**Eduardo de Paula**

Projeto acadêmico com aplicação real em ourivesaria local.

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/seu-usuario)

</div>

---

<div align="center">

*Feito com ☕ e muito carinho — porque toda ourivesaria merece um sistema à altura das suas joias.*

</div>
