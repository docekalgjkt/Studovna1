package cz.gjkt.view;

import cz.gjkt.model.Predmet;
import cz.gjkt.model.PredmetyDAOJDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class PredmetyController implements Initializable {

    @FXML
    TableView tableView;

    PredmetyDAOJDBC predmetyDao = new PredmetyDAOJDBC();
    List<Predmet> predmety;
    ObservableList<Predmet> items;
    ObservableList<Predmet> selectedItems = null;


    public void fillTable(){
        predmety = predmetyDao.getAll();
        items = FXCollections.observableList(predmety);
        tableView.setItems(items);
    }

    public void initColumns() {

        TableColumn<String, Predmet> nazevColumn = new TableColumn<>("Název");
        nazevColumn.setCellValueFactory(new PropertyValueFactory<>("nazev"));
        TableColumn<String, Predmet> popisColumn = new TableColumn<>("Popis");
        popisColumn.setCellValueFactory(new PropertyValueFactory<>("popis"));
        TableColumn<String, Predmet> zkratkaColumn = new TableColumn<>("Zkratka");
        zkratkaColumn.setCellValueFactory(new PropertyValueFactory<>("zkratka"));
        zkratkaColumn.setEditable(true);

        tableView.getColumns().addAll(nazevColumn,popisColumn,zkratkaColumn);
    }

    public void handleSelection(){
        TableView.TableViewSelectionModel<Predmet> selectionModel = tableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        selectedItems = selectionModel.getSelectedItems();

        /*selectedItems.addListener(new ListChangeListener<Predmet>() {
            @Override
            public void onChanged(Change<? extends Predmet> change) {
                System.out.println("Selection changed: " + change.getList());
                System.out.println("Selected: " + selectedItems.get(0));
            }
        });*/
    }

    public void handlePridejButton(){
        Dialog<Predmet> dialog = new Dialog<>();
        dialog.setTitle("Nový předmět");
        dialog.setWidth(400);
        dialog.setHeight(300);
        predmetDialog(dialog);

        final Optional<Predmet> vysledek = dialog.showAndWait();
        if(vysledek.isPresent()){
            Predmet novyPredmet = vysledek.get();
            predmety.add(novyPredmet);
            predmetyDao.insert(novyPredmet);
        }
        tableView.refresh();

    }

    private void predmetDialog(Dialog dialog){
        // Vytvoření "potvrzovacího" tlačítka pro potvrzení dialogu
        ButtonType createButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        // Nastavení tlačítek dialogu
        dialog.getDialogPane().getButtonTypes().setAll(createButtonType, ButtonType.CANCEL);

        // Mřížka
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Komponenty
        TextField nazevTextField = new TextField();
        Label nazevLabel = new Label("Název");
        TextArea popisTextArea = new TextArea();
        Label popisLabel = new Label("Popis");
        TextField zkratkaTextField = new TextField();
        Label zkratkaLabel = new Label("Zkratka");

        grid.add(nazevLabel, 0, 0);
        grid.add(nazevTextField, 1, 0);
        grid.add(popisLabel,0,1);
        grid.add(popisTextArea,1,1);
        grid.add(zkratkaLabel,0,2);
        grid.add(zkratkaTextField,1,2);


        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(new Callback<ButtonType, Predmet>() {
            @Override
            public Predmet call(ButtonType param) {
                    Predmet predmet = new Predmet();
                    predmet.setNazev(nazevTextField.getText());
                    predmet.setPopis(popisTextArea.getText());
                    predmet.setZkratka(zkratkaTextField.getText());
                    return  predmet;
            }
        });
    }

    public void handleSmazButton(){

        Predmet item = (Predmet) tableView.getSelectionModel().getSelectedItem();
        System.out.println("Selected: " + item);
        predmety.remove(item);
        predmetyDao.delete(item);
        tableView.refresh();
    }

    public void handleUpravButton(){

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initColumns();
        fillTable();
        handleSelection();
    }
}
