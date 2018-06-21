package pucrs.myflight.modelo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GerenciadorPaises {

    private Map<String, Pais> paises;

    public GerenciadorPaises() { // Construtor
        paises = new HashMap<>();
    }

    public void addPais(Pais V) { // Para adicionar Países..
        paises.put(V.getCodigo(), V);
    }

    public void addAeroporto(String K, Aeroporto V) { // Para adicionar Aeroportos..
        Pais p = paises.get(K);
        p.addAeroporto(V);
    }

    public Map<String, Pais> getPaises() {
        return paises;
    }

    public Pais getPais(String K) {
        return paises.get(K);
    }

    public ArrayList<Aeroporto> getAeroportos(String K) {
        Pais p = paises.get(K);
        return p.getAeroportos();
    }

    public void carregaPaises(String nomeArq) throws IOException {
        Path path = Paths.get(nomeArq);
        try (Scanner sc = new Scanner(Files.newBufferedReader(path, Charset.forName("utf8")))) {
            sc.useDelimiter("[;\n]");
            String header = sc.nextLine();
            String cod, nome;
            while (sc.hasNext()) {
                cod = sc.next();
                nome = sc.next();
                Pais novo = new Pais(cod, nome);
                addPais(novo);
            }
        } catch (IOException erro) {
            System.err.format("Erro de E/S: %s%n", erro);
        }
    }

    public ArrayList<Pais> listarTodosOrdenado() {
        ArrayList<Pais> list = new ArrayList<>(paises.values());
        list.sort(Comparator.comparing(Pais::getCodigo));
        return list;
    }
}