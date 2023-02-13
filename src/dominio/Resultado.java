package dominio;

public record Resultado(Integer golMandante,
                        Integer golVisitante){
    @Override
    public String toString() {
        return golMandante + " x " + golVisitante;
    }
}