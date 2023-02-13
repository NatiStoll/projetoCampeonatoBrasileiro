package dominio;

public record Criterio(
        int criterio1_Pontos,
        int criterio2_Vitorias
) {

    public int pontuacaoTotal() {
        return criterio1_Pontos*100 + criterio2_Vitorias;
    }
}


