/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventorydesktop.operation.OperationView;
import easyinventorydesktop.item.ItemEditView;
import easyinventorydesktop.storage.StorageView;
import easyinventorydesktop.unit.UnitEditView;
import easyinventorydesktop.user.UserEditView;
import easyinventorydesktop.warehouse.WarehouseView;
import easyinventoryapi.API;
import easyinventoryapi.APIException;
import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONException;

/**
 *
 * @author 3041TAN-06
 */
public class EasyInventoryDesktop extends Application {
    
    private Localization[] localizations;
    private API api=null;
    private StackPane root;
    private Stage stage;
    
    private Node createControlScene(String username) {
        TabPane tabpane=new TabPane();
        tabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab tab = new Tab();
        tab.setText(Localization.CURRENT.getLocalized("tab.users"));
        tab.setContent(new UserEditView(api));
        tabpane.getTabs().add(tab);
        
        tab = new Tab();
        tab.setText(Localization.CURRENT.getLocalized("tab.warehouses"));
        tab.setContent(new WarehouseView(api));
        tabpane.getTabs().add(tab);
        
        tab = new Tab();
        tab.setText(Localization.CURRENT.getLocalized("tab.items"));
        tab.setContent(new ItemEditView(api));
        tabpane.getTabs().add(tab);
        
        tab = new Tab();
        tab.setText(Localization.CURRENT.getLocalized("tab.units"));
        tab.setContent(new UnitEditView(api));
        tabpane.getTabs().add(tab);
        
        tab = new Tab();
        tab.setText(Localization.CURRENT.getLocalized("tab.storages"));
        tab.setContent(new StorageView(api));
        tabpane.getTabs().add(tab);
        
        tab = new Tab();
        tab.setText(Localization.CURRENT.getLocalized("tab.search"));
        tab.setContent(new SearchView(api));
        tabpane.getTabs().add(tab);
        
        tab = new Tab();
        tab.setText(Localization.CURRENT.getLocalized("tab.operations"));
        tab.setContent(new OperationView(api));
        tabpane.getTabs().add(tab);
        
        tab = new Tab();
        tab.setText(Localization.CURRENT.getLocalized("tab.profile"));
        FlowPane profile=new FlowPane();
        profile.setAlignment(Pos.TOP_CENTER);
        Button change_pass=new Button();
        change_pass.getStyleClass().add("pw-change-button");
        change_pass.setText(Localization.CURRENT.getLocalized("profile.password.change"));
        change_pass.setOnAction(e->{
            new PasswordChangeDialog(api).open();
        });
        profile.getChildren().add(change_pass);
        tab.setContent(profile);
        tabpane.getTabs().add(tab);
        
        Button logout=new Button();
        logout.getStyleClass().add("logout-button");
        logout.setGraphic(new ImageView(new Image("resources/icons/logout.png")));
        logout.setOnAction(_e->{
            root.getChildren().clear();
            root.getChildren().add(createLoginScene());
        });
        
        BorderPane ui=new BorderPane();
        HBox menu=new HBox(new Text(username),logout);
        menu.setAlignment(Pos.CENTER_LEFT);
        ui.setTop(UIUtils.createRightAside(new Label(),menu));
        ui.setCenter(tabpane);
        return ui;
    }
    
    private Node createLocalizationChoice() {
        ChoiceBox<Localization> cb=new ChoiceBox(FXCollections.observableArrayList(localizations));
        cb.getSelectionModel().select(Localization.CURRENT);
        cb.setOnAction(_e->{
            Localization.CURRENT=cb.getValue();
            root.getChildren().clear();
            root.getChildren().add(createLoginScene());
            stage.setTitle(Localization.CURRENT.getLocalized("app.name"));
        });
        return cb;
    }
    
    private Node createLoginScene() {
        
        Label loadingScreen=new Label(Localization.CURRENT.getLocalized("app.loading"));
        loadingScreen.setVisible(false);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getColumnConstraints().add(new ColumnConstraints());
        grid.getColumnConstraints().add(new ColumnConstraints(150));
        grid.setAlignment(Pos.CENTER);
        
        TextField user = new TextField();
        user.getStyleClass().add("login-username");
        user.setPromptText(Localization.CURRENT.getLocalized("login.name"));
        
        TextField server = new TextField();
        server.getStyleClass().add("login-server");
        server.setPromptText(Localization.CURRENT.getLocalized("login.server"));
        
        PasswordField pass = new PasswordField();
        pass.getStyleClass().add("login-password");
        pass.setPromptText(Localization.CURRENT.getLocalized("login.password"));
        
        grid.add(new Text(Localization.CURRENT.getLocalized("login.name")), 0, 0);
        grid.add(user, 1, 0);
        grid.add(new Text(Localization.CURRENT.getLocalized("login.server")), 0, 1);
        grid.add(server, 1, 1);
        grid.add(new Text(Localization.CURRENT.getLocalized("login.password")), 0, 2);
        grid.add(pass, 1, 2);
        
        grid.add(new Text(Localization.CURRENT.getLocalized("localization.choice")), 0, 3);
        grid.add(createLocalizationChoice(), 1, 3);
        
        Button login=new Button(Localization.CURRENT.getLocalized("login.button"));
        login.getStyleClass().add("login-button");
        login.setOnAction(_e->{
            grid.setVisible(false);
            loadingScreen.setVisible(true);
            login.setDisable(true);
            UIUtils.runOnWorker("login.fail",()->{
                return API.connect(new SimpleHTTPConnector(),server.getText(),user.getText(),pass.getText());
            }, r->{
                api=r;
                root.getChildren().clear();
                root.getChildren().add(createControlScene(user.getText()));
            }, ()->{
                grid.setVisible(true);
                loadingScreen.setVisible(false);
                login.setDisable(false);
            });
        });
        
        grid.add(login, 0, 4);
        
        
        StackPane stack = new StackPane();
        stack.getChildren().addAll(grid,loadingScreen);
        
        return stack;
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        try{
            localizations=new Localization[Localization.LANG_CODES.length];
            for(int i=0;i<localizations.length;i++)
                localizations[i]=new Localization(Localization.LANG_CODES[i]);
            
            Localization.CURRENT=localizations[0];
        }catch(IOException | JSONException e) {
            UIUtils.showError(e.getLocalizedMessage());
            return;
        }
        
        Worker.GLOBAL.start();
        
        root = new StackPane();
        root.getChildren().add(createLoginScene());
        
        Scene scene = new Scene(root, 960, 540);
        scene.getStylesheets().add("resources/style/style.css");
        
        primaryStage.setOnCloseRequest(_e->{
            Worker.GLOBAL.interrupt();
        });
        
        primaryStage.setTitle(Localization.CURRENT.getLocalized("app.name"));
        primaryStage.getIcons().add(new Image("resources/icons/logo.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if(api!=null)
            try{api.logout();}catch(APIException | IOException | JSONException e){}
        super.stop();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
