package impl;

import dominio.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class CampeonatoBrasileiroImplNati {


    private Map<Integer, List<Jogo>> brasileirao;

    private List<Jogo> jogos; //lista com todos os jogos

    private Predicate<Jogo> filtro;


    public CampeonatoBrasileiroImplNati(Path arquivo, int ano) throws IOException {
        this.jogos = lerarquivo(arquivo);
        this.filtro = jogo -> jogo.data().data().getYear() == ano; // definindo filtro
        this.brasileirao = jogos.stream()
                .filter(filtro)
                .collect(Collectors.groupingBy(
                        Jogo::rodada,
                        Collectors.mapping(Function.identity(), Collectors.toList())
                ));
    }

    DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//    DateTimeFormatter formaterTime = DateTimeFormatter.ofPattern("HH'h'mm");

    public List<Jogo> lerarquivo(Path arquivo) throws IOException {
        List<String> lines = Files.readAllLines(arquivo);
        List<Jogo> jogos = new ArrayList<>();
        for (String line : lines.stream().skip(1).toList()) {
            String[] infos = line.split(";");
            Integer rodada = parseInt(infos[0]);
            LocalDate data = LocalDate.parse(infos[1], formater);
//            LocalTime horario = LocalTime.parse(infos[2], formaterTime);
            DayOfWeek diaDaSemana = data.getDayOfWeek();
            DataDoJogo dataDoJogo = new DataDoJogo(data, diaDaSemana);
            String mandante = infos[4];
            String visitante = infos[5];
            String vencedor = infos[6];
            String arena = infos[7];
            Time timeMandante = new Time(mandante);
            Time timeVisitante = new Time(visitante);
            Time timeVencedor = new Time(vencedor);
            Integer mandantePlacar = parseInt(infos[8]);
            Integer visitantePlacar = parseInt(infos[9]);
            String estadoMandante = infos[10];
            String estadoVisitante = infos[11];
            String estadoVencedor = infos[12];

            Jogo jogo = new Jogo(rodada, dataDoJogo, timeMandante, timeVisitante,
                    timeVencedor, arena, mandantePlacar, visitantePlacar, estadoMandante,
                    estadoVisitante, estadoVencedor);
            jogos.add(jogo);
        }
        return jogos;
    }

    //Tabela
    public List<Map.Entry<Time, Criterio>> getTabela() {
        return jogos.stream().filter(filtro)
                .collect(Collectors.groupingBy(
                        Jogo::mandante,
                        Collectors.mapping(Function.identity(), Collectors.toList())
                ))
                .entrySet().stream()
                .map(obj -> {
                    Time timealvo = obj.getKey();
                    List<Jogo> jogosDoTime = obj.getValue();
                    Integer criterio1_Pontos = jogosDoTime.stream()
                            .map(jogo -> jogo.mandantePlacar() < jogo.visitantePlacar() ? 0 : jogo.mandantePlacar() > jogo.visitantePlacar() ? 3 : 1)
                            .reduce(0, (a, b) -> a + b);
                    Integer criterio2_Vitorias = (int) jogosDoTime.stream().filter(jogo -> jogo.mandantePlacar() > jogo.visitantePlacar()).count();
                    return Map.entry(timealvo, new Criterio(criterio1_Pontos, criterio2_Vitorias));
                })
//                .sorted((t1, t2) -> t1.getValue().pontuacaoTotal() - t2.getValue().pontuacaoTotal())
                .sorted(Comparator.comparingInt(t -> t.getValue().pontuacaoTotal()))
//                .map(obj -> )// formatar como String
                .collect(Collectors.toList());
    }




//Estatisticas (Total de gols) - 944
    public int getTotalGols() {
        int golsMandante = (jogos.stream().filter(filtro).map(jogo -> jogo.mandantePlacar()).reduce(0, (a, b) -> a + b));
        int golsVisitante = (jogos.stream().filter(filtro).map(jogo -> jogo.visitantePlacar()).reduce(0, (a, b) -> a + b));
        int totalGols = golsVisitante+golsMandante;
        return totalGols;
    }


//Estatisticas (Total de jogos) - 380
    public Long getTotalJogos (){
        Long totalJogos = jogos.stream().filter(filtro).count();
        return totalJogos;
    }

//Estatisticas (Media de gols) - 2.4842105263157896

    public Double getMediaGols() {
        Double mediaGols = (double) getTotalGols()/getTotalJogos();
        return mediaGols;
    }



// Todos os placares
    public Map<Resultado, Long> todosOsPlacares() {
        return jogos.stream().filter(filtro)
                .map(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));//grouppingBy -> Map
    }


//Estatisticas (Placar mais repetido) - 1 x 1 (57 jogo(s))

    public Map.Entry<Resultado, Long> placarMaisRepetido(){
        return todosOsPlacares()
                .entrySet().stream().max(Map.Entry.comparingByValue()).get();

//        Resultado resultadoMaisFrequente = null;
//        long contagemMaisFrequente = 0;
//        long quantasVezesRepete = 0;
//        for (Map.Entry<Resultado, Long> entry : placarMaisRepetido.entrySet()) {
//            if (entry.getValue() > contagemMaisFrequente) {
//                resultadoMaisFrequente = entry.getKey();
//                contagemMaisFrequente = entry.getValue();
//            }


//        return resultadoMaisFrequente != null ? resultadoMaisFrequente.toString() : "Não há resultados";
    }


//Estatisticas (Placar menos repetido) - 2 x 5 (1 jogo(s))

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        return todosOsPlacares()
                .entrySet().stream().min(Map.Entry.comparingByValue()).get();
    }

//Estatisticas (3 ou mais gols) - 175
public Long gols3OuMais (){
    return jogos.stream().filter(filtro).filter(jogo ->(jogo.mandantePlacar() +  jogo.visitantePlacar()) >= 3).count();
}

    public Long gols3OuMenos (){
        return jogos.stream().filter(filtro).filter(jogo ->(jogo.mandantePlacar() +  jogo.visitantePlacar()) < 3).count();
    }


//Estatisticas (Vitorias Fora de casa) - 101
    public Long vitoriaForaDeCasa(){
        return jogos.stream().filter(filtro).filter(jogo -> (jogo.mandantePlacar() > jogo.visitantePlacar())).count();
    }


//Estatisticas (Vitorias Em casa) - 171
public Long vitoriaEmDeCasa(){
    return jogos.stream().filter(filtro)
            .filter(jogo -> (jogo.visitantePlacar() > jogo.mandantePlacar()))
            .count();
}
//Estatisticas (Empates) - 108
public Long empates(){
    return jogos.stream().filter(filtro)
            .filter(jogo -> (jogo.visitantePlacar()
            .equals( jogo.mandantePlacar()))).count();
}
}




