package pucrs.myflight.modelo;

public class Aeroporto implements Comparable<Aeroporto> {
	private String codigo;
	private String nome;
	private Geo loc;
	private String codigoPais;
	private int nivelDeTrafego; // Variável para ter o controle de quantas rotas chegam e saem do aeroporto

	public Aeroporto(String codigo, String nome, Geo loc, String codigoPais) {
		this.codigo = codigo;
		this.nome = nome;
		this.loc = loc;
		this.codigoPais = codigoPais;
	}
	
	public String getCodigo() {
		return codigo;
	}

	public String getPais() {
	    return codigoPais;
	}
	
	public String getNome() {
		return nome;
	}
	
	public Geo getLocal() {
		return loc;
	}

	public void addNivelDeTrafego() { // Método para definir a quantidade de rotas que chega e sai do aeroporto
		nivelDeTrafego++;
	}

	public int getNivelDeTrafego() { // Método para coletar a quantidade de rotas que chega e sai do aeroporto
		return nivelDeTrafego;
	}

    @Override
    public String toString() {
        return codigo + " - " + nome + " [" + loc + "]";
    }

	@Override
	public int compareTo(Aeroporto outro) {
		return this.nome.compareTo(outro.nome);
	}
}
