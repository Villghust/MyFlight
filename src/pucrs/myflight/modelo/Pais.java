package pucrs.myflight.modelo;

import java.util.ArrayList;

public class Pais {

    private String codigo, nome;

    private ArrayList<Aeroporto> aeroportos;

    public Pais(String codigo, String nome){
        this.codigo = codigo;
        this.nome = nome;
        aeroportos = new ArrayList<Aeroporto>();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public ArrayList<Aeroporto> getAeroportos() {
        return aeroportos;
    }

    public void addAeroporto(Aeroporto aero) {
        aeroportos.add(aero);
    }

    @Override
    public String toString() {
        return getNome();
    }
}