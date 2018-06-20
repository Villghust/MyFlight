package pucrs.myflight.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pucrs.myflight.modelo.*;

public class JanelaFX extends Application {

	final SwingNode mapkit = new SwingNode();

	private GerenciadorCias gerCias;
	private GerenciadorAeroportos gerAero;
	private GerenciadorRotas gerRotas;
	private GerenciadorAeronaves gerAvioes;
	private GerenciadorPaises gerPaises;

	private GerenciadorMapa gerenciador;
	private double defaultBoxItemWidth = 150.0;


	private EventosMouse mouse;

	private ObservableList<CiaAerea> comboCiasData;
	private ComboBox<CiaAerea> comboCia;
	private Aeroporto aeroPerto;

    @Override
    public void start(Stage primaryStage) throws Exception {

        setup();

        GeoPosition poa = new GeoPosition(-30.05, -51.18);
        gerenciador = new GerenciadorMapa(poa, GerenciadorMapa.FonteImagens.VirtualEarth);
        mouse = new EventosMouse();
        gerenciador.getMapKit().getMainMap().addMouseListener(mouse);
        gerenciador.getMapKit().getMainMap().addMouseMotionListener(mouse);

        createSwingContent(mapkit);

        BorderPane pane = new BorderPane();
        GridPane leftPane = new GridPane();

        leftPane.setAlignment(Pos.CENTER);
        leftPane.setHgap(10);
        leftPane.setVgap(10);
        leftPane.setPadding(new Insets(10, 10, 10, 10));

        Button btnConsulta1 = new Button("Consulta 1");
        Button btnConsulta2 = new Button("Consulta 2");
        Button btnConsulta3 = new Button("Consulta 3");
        Button btnConsulta4 = new Button("Consulta 4");
        TextField textField = new TextField();
        TextField tfTempo = new TextField();
        ComboBox<CiaAerea> comboCia = new ComboBox<>();
        ComboBox<Pais> comboPais = new ComboBox<>();
        ComboBox<Aeroporto> comboAero1 = new ComboBox<>();
        ComboBox<Aeroporto> comboAero2 = new ComboBox<>();

        comboCia.setItems(FXCollections.observableList(gerCias.listarTodosOrdenado()));
        comboPais.setItems(FXCollections.observableList(gerPaises.listarTodosOrdenado()));
        comboAero1.setItems(FXCollections.observableList(gerAero.listarTodosOrdenado()));
        comboAero2.setItems(FXCollections.observableList(gerAero.listarTodosOrdenado()));

        comboAero1.setMaxWidth(defaultBoxItemWidth);
        comboAero2.setMaxWidth(defaultBoxItemWidth);
        comboCia.setMaxWidth(defaultBoxItemWidth);
        comboPais.setMaxWidth(defaultBoxItemWidth);

        javafx.scene.control.Label lblCia = new javafx.scene.control.Label("Cia Aérea");
        javafx.scene.control.Label lblOrigem = new javafx.scene.control.Label("Origem");
        javafx.scene.control.Label lblDestino = new javafx.scene.control.Label("Destino");
        javafx.scene.control.Label lblTempo = new Label("Tempo");

        leftPane.add(lblCia,0,0);
        leftPane.add(comboCia,0,1);
        leftPane.add(btnConsulta1, 0, 2);
        leftPane.add(comboPais,0,3);
        leftPane.add(btnConsulta2, 0, 4);
        leftPane.add(lblOrigem,0,5);
        leftPane.add(comboAero1,0,6);
        leftPane.add(lblDestino,0,7);
        leftPane.add(comboAero2, 0, 8);
        leftPane.add(btnConsulta3, 0, 9);
        leftPane.add(lblTempo,0,10);
        leftPane.add(tfTempo,0,11);
        leftPane.add(btnConsulta4, 0, 12);

        // Escrever o código da companhia aérea no textField para mostra todas as rotas e aeroportos desta mesma companhia.
        // #DONE
        btnConsulta1.setOnAction(e -> {
            consulta1(comboCia.getValue().getCodigo());
        });

        // Mostra todos os aeroportos do pais selecionado, suas rotas e apresenta os aeroportos com maior e menor tráfego
        // #DONE
        btnConsulta2.setOnAction(e -> {
            consulta2(comboPais.getValue().getCodigo());
        });

        // Clicar com o botão direito para selecionar o primeiro aeroporto e escrever no textField o código do segundo
        // aeroporto para mostrar todas as rotas entre eles com até 1 escala.
        // TODO #3 terminar consulta 3
        btnConsulta3.setOnAction(e -> {
            consulta3(comboAero1.getValue().getCodigo(),comboAero2.getValue().getCodigo());
        });

        // Clicar com o botão direito para selecionar o aeroporto e clicar no botão consulta4 para mostrar os destinos
        // que o aeroporto selecionado tem.
        // TODO #4 terminar consulta 4
        btnConsulta4.setOnAction(e -> {
            consulta4(aeroPerto.getCodigo(), Double.parseDouble(tfTempo.getText()));
        });

        pane.setCenter(mapkit);
        pane.setRight(leftPane);

        Scene scene = new Scene(pane, 1080, 720);
        primaryStage.setScene(scene);
//		primaryStage.setFullScreen(true);
        primaryStage.setTitle("Mapas com JavaFX");
        primaryStage.show();

    }

	private void setup() {

		gerCias = new GerenciadorCias();
		try {
			gerCias.carregaCias("airlines.dat");
		} catch (IOException e) {
			System.out.println("Não foi possível ler airlines.dat!");
		}

		gerAero = new GerenciadorAeroportos();
		try {
			gerAero.carregaAeroportos("airports.dat");
		} catch (IOException e) {
			System.out.println("Não foi possível ler airports.dat!");
		}

		gerAvioes = new GerenciadorAeronaves();
		try {
			gerAvioes.carregaAeronaves("equipment.dat");
		} catch (IOException e) {
			System.out.println("Não foi possível ler equipment.dat!");
		}

		gerRotas = new GerenciadorRotas();
		try {
			gerRotas.carregaRotas(gerCias, gerAero, gerAvioes);
		} catch (IOException e) {
			System.out.println("Não foi possível ler airports.dat!");
		}

		gerPaises = new GerenciadorPaises();
		try {
			gerPaises.carregaPaises("countries.dat");
		} catch (IOException e) {
			System.out.println("Não foi possível ler countries.dat!");
		}

        aeroPerto = gerAero.listarTodos().get(0); // Trocadilho ..

	}

	private void consulta1(String cod) {

		ArrayList<Rota> rotas = gerRotas.listarRotasCias(cod);
		List<MyWaypoint> listPontos = new ArrayList<>();

		gerenciador.clear();
		
		for(Rota r : rotas){

			Tracado tracado = new Tracado();
			tracado.setCor(Color.BLUE);
			tracado.setWidth(1);
			tracado.addPonto(r.getOrigem().getLocal());
			tracado.addPonto(r.getDestino().getLocal());
			gerenciador.addTracado(tracado);

			if(!listPontos.contains(r.getOrigem())){
				listPontos.add(new MyWaypoint(Color.RED, r.getOrigem().getCodigo(), r.getOrigem().getLocal(), 5));
			}

			if(!listPontos.contains(r.getDestino())){
				listPontos.add(new MyWaypoint(Color.RED, r.getDestino().getCodigo(), r.getDestino().getLocal(), 5));
			}
		}

		gerenciador.setPontos(listPontos);
		gerenciador.getMapKit().repaint();
	}

	private void consulta2(String pais) {

		ArrayList<Rota> rotasDoPais = gerRotas.listarRotasDeUmPais(pais);
		ArrayList<Aeroporto> aerosDoPais = gerAero.listarTodosDeUmPais(rotasDoPais);
		List<MyWaypoint> listPontos = new ArrayList<>();

		gerenciador.clear();

		for (Rota r : rotasDoPais) {

			Tracado tracado = new Tracado();
			tracado.setWidth(1);
			tracado.setCor(Color.RED); // Voo internacional volta

			r.getOrigem().addNivelDeTrafego();
			r.getDestino().addNivelDeTrafego();

			if (r.getOrigem().getPais().equals(pais) && r.getDestino().getPais().equals(pais)) { // Voo nacional
				tracado.setCor(Color.BLUE);
			} else if (r.getOrigem().getPais().equals(pais) && !r.getDestino().getPais().equals(pais)) { // Voo internacional ida
				tracado.setCor(Color.GREEN);
			}

			if (r.getOrigem().getPais().equals(pais)) {
				tracado.addPonto(r.getOrigem().getLocal());
				tracado.addPonto(r.getDestino().getLocal());
				gerenciador.addTracado(tracado);
			}

			if (r.getDestino().getPais().equals(pais)) {
				tracado.addPonto(r.getOrigem().getLocal());
				tracado.addPonto(r.getDestino().getLocal());
				gerenciador.addTracado(tracado);
				if (!listPontos.contains(r.getDestino())) {
					listPontos.add(new MyWaypoint(Color.BLUE, r.getDestino().getCodigo(), r.getDestino().getLocal(), 5));
				}
			}

			if (!r.getOrigem().getPais().equals(pais)) {
				tracado.addPonto(r.getOrigem().getLocal());
				tracado.addPonto(r.getDestino().getLocal());
				gerenciador.addTracado(tracado);
				if (!listPontos.contains(r.getOrigem())) {
					listPontos.add(new MyWaypoint(Color.BLACK, r.getOrigem().getCodigo(), r.getOrigem().getLocal(), 5));
				}
			}

			if (!r.getDestino().getPais().equals(pais)) {
				tracado.addPonto(r.getOrigem().getLocal());
				tracado.addPonto(r.getDestino().getLocal());
				gerenciador.addTracado(tracado);
				if (!listPontos.contains(r.getDestino())) {
					listPontos.add(new MyWaypoint(Color.BLACK, r.getDestino().getCodigo(), r.getDestino().getLocal(), 5));
				}
			}
		}

		Aeroporto menorTrafego = aerosDoPais.get(0);
		Aeroporto maiorTrafego = menorTrafego;

		for (Aeroporto aero : aerosDoPais) {
			if (aero.getNivelDeTrafego() > maiorTrafego.getNivelDeTrafego()) {
				maiorTrafego = aero;
			}
			if (aero.getNivelDeTrafego() < menorTrafego.getNivelDeTrafego()) {
				menorTrafego = aero;
			}
		}

		listPontos.add(new MyWaypoint(Color.GREEN, menorTrafego.getCodigo(), menorTrafego.getLocal(), 50));
		listPontos.add(new MyWaypoint(Color.RED, maiorTrafego.getCodigo(), maiorTrafego.getLocal(), 50));

		gerenciador.setPontos(listPontos);
		gerenciador.getMapKit().repaint();
	}

	private void consulta3(String origem, String destino) {

		List<MyWaypoint> lstPoints = new ArrayList<>();

		ArrayList<Rota> rotasIniciais = gerRotas.buscarOrigem(origem);

		Aeroporto inicio = gerAero.buscarCodigo(origem);
		Aeroporto fim = gerAero.buscarCodigo(destino);

		gerenciador.clear();

		lstPoints.add(new MyWaypoint(Color.RED, inicio.getCodigo(), inicio.getLocal(), 5));
		lstPoints.add(new MyWaypoint(Color.RED, fim.getCodigo(), fim.getLocal(), 5));

		for(Rota rOrigem : rotasIniciais){

			Tracado tr = null;
			Tracado tr2 = null;

			if(rOrigem.getDestino().equals(fim)) {
				tr = new Tracado();
				tr.setCor(Color.BLUE);
				tr.setWidth(2);
				tr.addPonto(rOrigem.getOrigem().getLocal());
				tr.addPonto(fim.getLocal());
			} else {
				ArrayList<Rota> conexao = gerRotas.buscarOrigem(rOrigem.getDestino().getCodigo());

				for(Rota r : conexao){

					if(r.getDestino().equals(fim)){

						tr = new Tracado();
						tr.setCor(Color.BLUE);
						tr.setWidth(2);
						tr.addPonto(rOrigem.getOrigem().getLocal());
						tr.addPonto(rOrigem.getDestino().getLocal());

						tr2 = new Tracado();
						tr2.setCor(Color.GREEN);
						tr2.setWidth(2);
						tr2.addPonto(rOrigem.getDestino().getLocal());
						tr2.addPonto(fim.getLocal());

						lstPoints.add(new MyWaypoint(Color.RED, r.getOrigem().getCodigo(), r.getOrigem().getLocal(), 5));
					}
				}
			}

			if(!(tr == null)) {
				gerenciador.addTracado(tr);
			}

			if(!(tr2 == null)) {
				gerenciador.addTracado(tr2);
			}
		}

		gerenciador.setPontos(lstPoints);
		gerenciador.getMapKit().repaint();

		// TODO #3.1 Ao selecionar uma rota, mostrar o tempo aproximado total de vôo e destacá-la no mapa com outra cor.
		// Precisamos descobrir uma maneira de selecionar a rota.

	}

	private void consulta4(String origem) {

        List<MyWaypoint> listPoints = new ArrayList<>();
        ArrayList<Rota> rotasDeVoo = gerRotas.buscarOrigem(origem);
        ArrayList<Aeroporto> aeroportosDestino = new ArrayList<>();

        Aeroporto inicio = gerAero.buscarCodigo(origem);

        gerenciador.clear();

        listPoints.add(new MyWaypoint(Color.BLUE, inicio.getCodigo(), inicio.getLocal(), 10));

        int aero = 0;

        while(aeroportosDestino.size() < rotasDeVoo.size()) {
            aeroportosDestino.add(rotasDeVoo.get(aero).getDestino()); // Pega todos os aeroportos destino e Set na variável
            listPoints.add(new MyWaypoint(Color.BLUE, aeroportosDestino.get(aero).getCodigo(), aeroportosDestino.get(aero).getLocal(), 8));
            aero++;
        }

        gerenciador.setPontos(listPoints);
        gerenciador.getMapKit().repaint();

        // TODO #4.1 devemos apresentar os aeroportos que são alcançáveis ATÉ UM DETERMINADO TEMPO DE VOO (ex: 12 horas), com no máximo duas conexões
		// Utilizar o textField para set de tempo de voo.
		// TODO #4.2 Quando uma rota é exibida, deve-se mostrar também a distância entre os pontos e a aeronave sendo utilizada.
		// Criar dois labels na aplicação: um para mostrar a aeronave e o outro para mostrar a distância. Conforme a rota é selecionada
		// o label deve atualizar o seu texto para o nome da aeronavo e o outro para o valor da distância.

	}

	private class EventosMouse extends MouseAdapter {
		private int lastButton = -1;

		@Override
		public void mousePressed(MouseEvent e) {

			JXMapViewer mapa = gerenciador.getMapKit().getMainMap();
			GeoPosition loc = mapa.convertPointToGeoPosition(e.getPoint());
//			System.out.println(loc.getLatitude()+", "+loc.getLongitude());
			lastButton = e.getButton();
			List<MyWaypoint> lstPoints = new ArrayList<>();

			gerenciador.clear();

			// Botão 3: seleciona aeroporto mais próximo da localização clicada

			if (lastButton == MouseEvent.BUTTON3) {
				gerenciador.setPosicao(loc);
				gerenciador.getMapKit().repaint();
			}

			if (gerenciador.getPosicao() == loc) { // Verificação para evitar os erros ao clicar com o botão 1

				Geo localClicado = new Geo(gerenciador.getPosicao().getLatitude(), gerenciador.getPosicao().getLongitude());
				double distancia = Geo.distancia(localClicado, aeroPerto.getLocal());

				for (Aeroporto aero : gerAero.listarTodos()) {

					Geo pos = aero.getLocal();
					double dist = Geo.distancia(localClicado, pos);

					if (dist <= distancia) {
						aeroPerto = aero;
						distancia = dist;
					}
				}
			}

			lstPoints.add(new MyWaypoint(Color.BLUE, aeroPerto.getCodigo(), aeroPerto.getLocal(), 10));

			gerenciador.setPontos(lstPoints);
			gerenciador.getMapKit().repaint();
		}
	}

	private void createSwingContent(final SwingNode swingNode) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				swingNode.setContent(gerenciador.getMapKit());
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
