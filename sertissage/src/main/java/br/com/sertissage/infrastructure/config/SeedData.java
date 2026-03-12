package br.com.sertissage.infrastructure.config;

import br.com.sertissage.domain.entity.CategoriaMaterial;
import br.com.sertissage.domain.entity.Material;
import br.com.sertissage.domain.enums.TipoCategoria;
import br.com.sertissage.domain.repository.CategoriaMaterialRepository;
import br.com.sertissage.domain.repository.MaterialRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SeedData {

    @Bean
    CommandLineRunner seed(
            CategoriaMaterialRepository categoriaRepo,
            MaterialRepository materialRepo
    ) {
        return args -> {

            if (categoriaRepo.count() > 0) {
                System.out.println("[SeedData] Dados já existem. Seed ignorado.");
                return;
            }

            // ── CATEGORIAS ──────────────────────────────────────────────────

            CategoriaMaterial metal = categoriaRepo.save(new CategoriaMaterial(
                TipoCategoria.METAL,
                "Metal",
                "Metais preciosos e não-preciosos usados em joias e peças."
            ));

            CategoriaMaterial pedra = categoriaRepo.save(new CategoriaMaterial(
                TipoCategoria.PEDRA,
                "Pedra",
                "Pedras preciosas e semipreciosas usadas em cravação."
            ));

            CategoriaMaterial insumo = categoriaRepo.save(new CategoriaMaterial(
                TipoCategoria.INSUMO,
                "Insumo",
                "Materiais de consumo e apoio à produção."
            ));

            CategoriaMaterial relojoaria = categoriaRepo.save(new CategoriaMaterial(
                TipoCategoria.RELOJOARIA,
                "Relojoaria",
                "Peças e componentes de relógios."
            ));

            System.out.println("[SeedData] Categorias criadas.");

            // ── MATERIAIS GLOBAIS ────────────────────────────────────────────
            // empresa = null → visíveis para todos os ourives

            List<Material> materiais = List.of(

                // METAL — gramas (g)
                new Material("Ouro 18k",            "Ouro 18 quilates (750‰)",           metal,      "g"),
                new Material("Ouro 14k",            "Ouro 14 quilates (585‰)",           metal,      "g"),
                new Material("Ouro 10k",            "Ouro 10 quilates (417‰)",           metal,      "g"),
                new Material("Prata 925",           "Prata esterlina (925‰)",            metal,      "g"),
                new Material("Prata 950",           "Prata fina (950‰)",                 metal,      "g"),
                new Material("Titânio",             "Titânio para joias e relógios",     metal,      "g"),
                new Material("Aço Inox",            "Aço inoxidável 316L",               metal,      "g"),
                new Material("Platina",             "Platina pura (950‰)",               metal,      "g"),
                new Material("Paládio",             "Paládio para ligas brancas",        metal,      "g"),

                // PEDRA — quilates (ct)
                new Material("Diamante",            "Diamante natural ou sintético",     pedra,      "ct"),
                new Material("Rubi",                "Corindo vermelho",                  pedra,      "ct"),
                new Material("Esmeralda",           "Berilo verde",                      pedra,      "ct"),
                new Material("Safira",              "Corindo azul",                      pedra,      "ct"),
                new Material("Ametista",            "Quartzo violeta",                   pedra,      "ct"),
                new Material("Topázio",             "Topázio natural",                   pedra,      "ct"),
                new Material("Água-marinha",        "Berilo azul claro",                 pedra,      "ct"),
                new Material("Opala",               "Opala preciosa",                    pedra,      "ct"),

                // INSUMO — gramas (g) ou mililitros (ml)
                new Material("Solda de Ouro",       "Solda para ouro em gramas",         insumo,     "g"),
                new Material("Solda de Prata",      "Solda para prata em gramas",        insumo,     "g"),
                new Material("Ácido Nítrico",       "Para testes de pureza",             insumo,     "ml"),
                new Material("Ácido Clorídrico",    "Para limpeza de peças",             insumo,     "ml"),
                new Material("Liga Amarela",        "Liga para ouro amarelo",            insumo,     "g"),
                new Material("Liga Branca",         "Liga para ouro branco",             insumo,     "g"),
                new Material("Cera de Fundição",    "Cera para modelagem",               insumo,     "g"),
                new Material("Polidor",             "Pasta para polimento",              insumo,     "g"),

                // RELOJOARIA — unidade (un)
                new Material("Movimento Quartzo",   "Módulo a quartzo genérico",         relojoaria, "un"),
                new Material("Movimento Automático","Módulo automático",                 relojoaria, "un"),
                new Material("Vidro Mineral",       "Vidro plano mineral",               relojoaria, "un"),
                new Material("Vidro Safira",        "Vidro de safira sintética",         relojoaria, "un"),
                new Material("Coroa",               "Coroa de ajuste de hora",           relojoaria, "un"),
                new Material("Ponteiros",           "Jogo de ponteiros",                 relojoaria, "un"),
                new Material("Pulseira Metal",      "Pulseira em aço inox",              relojoaria, "un"),
                new Material("Pulseira Couro",      "Pulseira em couro legítimo",        relojoaria, "un"),
                new Material("Bateria",             "Bateria SR626SW padrão",            relojoaria, "un")
            );

            materialRepo.saveAll(materiais);

            System.out.println("[SeedData] " + materiais.size() + " materiais globais criados.");
            System.out.println("[SeedData] Seed concluído com sucesso.");
        };
    }
}