import impl.CampeonatoBrasileiroImplNati;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.Scanner;

public class AplicacaoNati {

    public static void main(String[] args) {

        Path file = Path.of("campeonato-brasileiro.csv");

        System.out.println("Filtre a tabela que deseja visualizar \n Digite o ano: ");
        Scanner sc = new Scanner(System.in);
        int ano = sc.nextInt();



        CampeonatoBrasileiroImplNati resultados;
 try {
      resultados = new CampeonatoBrasileiroImplNati(file, ano);
 } catch (IOException e) {
            System.out.println(e);
            return;
        }
        imprimirEstatisticas(resultados);
    }

    private static void imprimirEstatisticas(CampeonatoBrasileiroImplNati brasileirao) {

        System.out.println("Total de gols: " + brasileirao.getTotalGols());
        System.out.println("Total de jogos: " + brasileirao.getTotalJogos());
        System.out.println("Media de gols: " + brasileirao.getMediaGols());
        System.out.println("Placar mais repetido: " + brasileirao.placarMaisRepetido());
        System.out.println("Placar menos repetido: " + brasileirao.placarMenosRepetido());
        System.out.println("3 ou mais: " + brasileirao.gols3OuMais());
        System.out.println("Menos de 3: " + brasileirao.gols3OuMenos());
        System.out.println("Vitórias fora de casa: " + brasileirao.vitoriaForaDeCasa());
        System.out.println("Vitórias em de casa: " + brasileirao.vitoriaEmDeCasa());
        System.out.println("Empates: " + brasileirao.empates());

        System.out.println("\n.:: TABELA CAMPEONATO BRASILEIRO ::.");



        brasileirao.getTabela().forEach(obj -> {
            String imprimir = obj.getKey().nome()+ ", pontos = " + obj.getValue().criterio1_Pontos() + ", vitórias = " + obj.getValue().criterio2_Vitorias();
            System.out.println(imprimir);
        });
    }
}
