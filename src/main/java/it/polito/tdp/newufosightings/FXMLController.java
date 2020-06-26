package it.polito.tdp.newufosightings;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.newufosightings.model.Model;
import it.polito.tdp.newufosightings.model.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

//controller turno A --> switchare al branch master_turnoB per turno B

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea txtResult;

    @FXML
    private TextField txtAnno;

    @FXML
    private Button btnSelezionaAnno;

    @FXML
    private ComboBox<String> cmbBoxForma;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private TextField txtT1;

    @FXML
    private TextField txtAlfa;

    @FXML
    private Button btnSimula;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	String shape = this.cmbBoxForma.getValue();
    	if(shape == null) {
    		txtResult.appendText("Selezionare una forma dell'avvistamento per poter generare il grafo.\n");
    		return;
    	}
    	Integer year = Integer.parseInt(txtAnno.getText());

    	this.model.createGraph(year, shape);
    	
    	txtResult.appendText("Grafo creato!\n\n");
    	
    	txtResult.appendText("Peso degli archi adiacenti a ogni stato:\n");
    	for(State s : this.model.getAllStates()) {
    		txtResult.appendText(s + " - " + this.model.getWeightNeighbors(s) + "\n");
    	}
    	
    	this.btnSimula.setDisable(false);    	
    }

    @FXML
    void doSelezionaAnno(ActionEvent event) {
    	txtResult.clear();
    	Integer year;
    	try {
    		year = Integer.parseInt(txtAnno.getText());
    	} catch(NumberFormatException e) {
    		txtResult.appendText("Errore: inserire un valore numerico nell'apposito campo 'Anno'.\n");
    		return;
    	}
    	if(year < 1910 || year > 2014) {
    		txtResult.appendText("Errore: inserire un anno compreso tra il 1910 e il 2014.\n");
    		return;
    	}
    	this.cmbBoxForma.getItems().clear();
    	this.cmbBoxForma.getItems().addAll(this.model.getShapes());
    	this.btnCreaGrafo.setDisable(false);
    	this.btnSimula.setDisable(true);
    }

    @FXML
    void doSimula(ActionEvent event) {
    	txtResult.clear();
    	txtResult.appendText("La simulazione lavora con i dati dell'ultimo grafo creato.\n"
    			+ "Se sono stati modificati i campi Anno o Forma, creare un nuovo grafo prima di procedere"
    			+ " con la simulazione.\n\n"); 
    	Integer T1;
    	Integer alfa;
    	try {
    		T1 = Integer.parseInt(this.txtT1.getText());
    		alfa = Integer.parseInt(this.txtAlfa.getText());
    	} catch (NumberFormatException e) {
    		txtResult.appendText("Errore: inserire 2 numeri interi nei rispettivi campi T1 e Alfa.\n");
    		return;
    	}
    	
    	if(T1 > 364 || T1 < 1 ) {
    		txtResult.appendText("Il valore T1 deve essere un numero di giorni compreso tra 1 e 364!\n");
    		return;
    	}
    	if(alfa > 100 || T1 < 0 ) {
    		txtResult.appendText("Il valore alfa deve essere un paramentro compreso tra 1 e 100!\n");
    		return;
    	}
    	
    	this.model.simulate(T1, alfa);
    	
    	txtResult.appendText("Livelli di allerta finali per ogni stato:\n\n");

    	Map<State, Double> alertLevel = this.model.getAlertLevel();
    	
    	for(State s : alertLevel.keySet()) {
    		txtResult.appendText(s + ": DEFCON " + alertLevel.get(s) + "\n");
    	}

    }

    @FXML
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
        assert txtAnno != null : "fx:id=\"txtAnno\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
        assert btnSelezionaAnno != null : "fx:id=\"btnSelezionaAnno\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
        assert cmbBoxForma != null : "fx:id=\"cmbBoxForma\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
        assert txtT1 != null : "fx:id=\"txtT1\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
        assert txtAlfa != null : "fx:id=\"txtAlfa\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		this.btnCreaGrafo.setDisable(true);
		this.btnSimula.setDisable(true);
	}
}
