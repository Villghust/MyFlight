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

		leftPane.add(textField,0,0);
		leftPane.add(btnConsulta1, 1, 0);
		leftPane.add(btnConsulta2, 2, 0);
		leftPane.add(btnConsulta3, 3, 0);
		leftPane.add(btnConsulta4, 4, 0);

		// Escrever o código da companhia aérea no textField para mostra todas as rotas e aeroportos desta mesma companhia.
		btnConsulta1.setOnAction(e -> {
			consulta1(textField.getText().toUpperCase());
		});

		// Mostra todos os aeroportos do mundo ... falta coisa aqui @Fred @Marcelo
		btnConsulta2.setOnAction(e -> {
			consulta2(textField.getText().toUpperCase());
		});

		// Clicar com o botão direito para selecionar o primeiro aeroporto e ...
		// ... escrever no textField o código do segundo aeroporto para mostrar todas as rotas entre eles com até 1 escala.
		btnConsulta3.setOnAction(e -> {
			consulta3(aeroPerto.getCodigo(), textField.getText().toUpperCase());
		});

		// Clicar com o botão direito para selecionar o aeroporto e clicar no botão consulta4 para mostrar os destinos ...
        // ... que o aeroporto tem
		btnConsulta4.setOnAction(e -> {
		    consulta4(aeroPerto.getCodigo());
        });

		pane.setCenter(mapkit);
		pane.setTop(leftPane);

		Scene scene = new Scene(pane, 1080, 720);
		primaryStage.setScene(scene);
//		primaryStage.setFullScreen(true);
		primaryStage.setTitle("Mapas com JavaFX");
		primaryStage.show();

	}

	// Inicializando os dados aqui...
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

		//ArrayList<Aeroporto> aeroportosDoMundo = gerAero.listarTodos();
		ArrayList<Rota> rotasNoMundo = gerRotas.listarRotasDeUmPais(pais);
		List<MyWaypoint> listPontos = new ArrayList<>();

		gerenciador.clear();

		for(Rota r : rotasNoMundo){

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


		// TODO Implementar o for que mostra todas as rotas mundiais.
        // TODO Quando uma rota é exibida, deve-se mostrar também a distância entre os pontos e a aeronave sendo utilizada.
//		Tracado tr;
//
//		for(Rota r : rotasNoMundo){
//			tr = new Tracado();
//			tr.setWidth(r.getOrigem().getNivelDeTrafego()+r.getDestino().getNivelDeTrafego()/3);
//			tr.addPonto(r.getOrigem().getLocal());
//			tr.addPonto(r.getDestino().getLocal());
//			gerenciador.addTracado(tr);
//		}

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
			if(!(tr == null)) gerenciador.addTracado(tr);
			if(!(tr2 == null)) gerenciador.addTracado(tr2);
		}
		gerenciador.setPontos(lstPoints);
		gerenciador.getMapKit().repaint();
	}

	private void consulta4(String origem) {

        List<MyWaypoint> listPoints = new ArrayList<>();
        ArrayList<Rota> rotasDeVoo = gerRotas.buscarOrigem(origem);
        ArrayList<Aeroporto> aeroportosDestino = new ArrayList<>();

        Aeroporto inicio = gerAero.buscarCodigo(origem);

        gerenciador.clear();

        listPoints.add(new MyWaypoint(Color.MAGENTA, inicio.getCodigo(), inicio.getLocal(), 8));

        int aero = 0;
        while(aeroportosDestino.size() < rotasDeVoo.size()) {
            aeroportosDestino.add(rotasDeVoo.get(aero).getDestino()); // Pega todos os aeroportos destino e Set na variável
            listPoints.add(new MyWaypoint(Color.BLUE, aeroportosDestino.get(aero).getCodigo(), aeroportosDestino.get(aero).getLocal(), 8));
            aero++;
        }

        gerenciador.setPontos(listPoints);
        gerenciador.getMapKit().repaint();

        // TODO devemos apresentar os aeroportos que são alcançáveis ATÉ UM DETERMINADO TEMPO DE VOO (ex: 12 horas), com no máximo duas conexões ...
        // ... por enquanto nosso código mostra todos os voos alcançados com no máximo duas conexões. Precisamos implementar essa treta do tempo ai.
        // TODO utilizar o textField para set de tempo de voo.

	}

	private class EventosMouse extends MouseAdapter {
		private int lastButton = -1;

		@Override
		public void mousePressed(MouseEvent e) {
			JXMapViewer mapa = gerenciador.getMapKit().getMainMap();
			GeoPosition loc = mapa.convertPointToGeoPosition(e.getPoint());
//			System.out.println(loc.getLatitude()+", "+loc.getLongitude());
			lastButton = e.getButton();

			// Botão 3: seleciona localização
			if (lastButton == MouseEvent.BUTTON3) {
				gerenciador.setPosicao(loc);
				gerenciador.getMapKit().repaint();
			}

			List<MyWaypoint> lstPoints = new ArrayList<>();

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

			gerenciador.clear();

			lstPoints.add(new MyWaypoint(Color.MAGENTA, aeroPerto.getCodigo(), aeroPerto.getLocal(), 7));

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
