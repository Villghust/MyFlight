package pucrs.myflight.modelo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class GerenciadorRotas {
    private ArrayList<Rota> rotas;

    public GerenciadorRotas() {
        this.rotas = new ArrayList<>();
    }


    public void carregaRotas(GerenciadorCias gerenciadorCias, GerenciadorAeroportos GerenciadorAeroportos, GerenciadorAeronaves GerenciadorAeronaves) throws IOException {
        Path path2 = Paths.get("routes.dat");
        try (Scanner sc = new Scanner(Files.newBufferedReader(path2, Charset.forName("utf8")))) {
            sc.useDelimiter("[;\n ]");
            String header = sc.nextLine();
            String cia, origem, destino, aeronave;

            while (sc.hasNext()) {
                cia = sc.next();
                origem = sc.next();
                destino = sc.next();
                aeronave = sc.next();

                CiaAerea cia1 = gerenciadorCias.buscarCodigo(cia);
                Aeroporto origem1 = GerenciadorAeroportos.buscarCodigo(origem);
                Aeroporto destino1 = GerenciadorAeroportos.buscarCodigo(destino);
                Aeronave aeronave1 = GerenciadorAeronaves.buscarCodigo(aeronave);
                Rota rota = new Rota(cia1, origem1, destino1, aeronave1);
                adicionar(rota);
            }

        }
    }



        public void ordenarCias(){
            Collections.sort(rotas);
        }


    public void ordenarNomesCias() {
        rotas.sort( (Rota r1, Rota r2) ->
          r1.getCia().getNome().compareTo(
          r2.getCia().getNome()));
    }

    public void ordenarNomesAeroportos() {
        rotas.sort( (Rota r1, Rota r2) ->
                r1.getOrigem().getNome().compareTo(
                r2.getOrigem().getNome()));
    }

    public void ordenarNomesAeroportosCias() {
        rotas.sort( (Rota r1, Rota r2) -> {
           int result = r1.getOrigem().getNome().compareTo(
                   r2.getOrigem().getNome());
           if(result != 0)
               return result;
           return r1.getCia().getNome().compareTo(
                   r2.getCia().getNome());
        });
    }
    public void adicionar(Rota r) {
        rotas.add(r);
    }

    public ArrayList<Rota> listarTodas() {
        return new ArrayList<>(rotas);
    }

    public ArrayList<Rota> buscarOrigem(String codigo) {
        ArrayList<Rota> result = new ArrayList<>();
        for(Rota r: rotas)
            if(r.getOrigem().getCodigo().equals(codigo))
                result.add(r);
        return result;
    }
}
