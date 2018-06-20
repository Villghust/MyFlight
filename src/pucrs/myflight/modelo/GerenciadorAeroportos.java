package pucrs.myflight.modelo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GerenciadorAeroportos {

    private Map<String, Aeroporto> aeroportos;

    public GerenciadorAeroportos(){
        this.aeroportos = new LinkedHashMap<>();
    }

    public void carregaAeroportos(String nomeArq) throws IOException {
        Path path = Paths.get(nomeArq);
        try (Scanner sc = new Scanner(Files.newBufferedReader(path, Charset.forName("utf8")))) {
            sc.useDelimiter("[;\n]");
            String header = sc.nextLine();
            String cod, nome, latitude, longitude, codigoPais;

            //  String  loc; dividido em lat e long;

            while (sc.hasNext()) {
                cod = sc.next();
                latitude = sc.next().replaceAll("(\r)", "");
                longitude = sc.next().replaceAll("(\r)", "");
                nome = sc.next().replaceAll("(\r)", "");
                codigoPais = sc.next().replaceAll("(\r)", "");

                Geo geo = new Geo(Double.parseDouble(latitude), Double.parseDouble(longitude));
                Aeroporto nova = new Aeroporto(cod, nome, geo, codigoPais);
                adicionar(nova);

            }
        }
    }

//    public void ordenarNomes() {
//        Collections.sort(aeroportos);
//    }

    public void adicionar(Aeroporto aero) {
        aeroportos.put(aero.getCodigo(), aero);
    }

    public ArrayList<Aeroporto> listarTodos() {
        return new ArrayList<>(aeroportos.values());
    }

    public Aeroporto buscarCodigo(String codigo) {
        return aeroportos.get(codigo);
    }

    public ArrayList<Aeroporto> listarTodosDeUmPais(ArrayList<Rota> rotas) {

        ArrayList<Aeroporto> listaDeAero = new ArrayList<>();

        for (Rota r : rotas) {
            if (r.getOrigem().getPais().equals(r.getDestino().getPais())) {
                if (!listaDeAero.contains(r.getOrigem()) && !listaDeAero.contains(r.getDestino())) { // Se a lista de aeroportos n tem nenhum deles, adiciona ambos
                    listaDeAero.add(r.getOrigem());
                    listaDeAero.add(r.getDestino());
                } else if ((listaDeAero.contains(r.getOrigem()) && !listaDeAero.contains(r.getDestino()))) { // Se a lista tem só o de origem, adiciona o de destino
                    listaDeAero.add(r.getDestino());
                } else if (!(listaDeAero.contains(r.getOrigem())) && listaDeAero.contains(r.getDestino())) { // Se a lista tem só o de destino, adiciona o de origem
                    listaDeAero.add(r.getOrigem());
                }
            }
        }
        return listaDeAero;
    }

    public ArrayList<Aeroporto> listarTodosOrdenado() {
        ArrayList<Aeroporto> list = new ArrayList<>(aeroportos.values());
        list.sort(Comparator.comparing(Aeroporto::getCodigo));
        return list;
    }

}
