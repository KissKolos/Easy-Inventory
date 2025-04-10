/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import easyinventoryapi.API;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 *
 * @author 3041TAN-06
 * @param <T>
 */
public abstract class BasicView<T> extends BorderPane {
    
    private final int PAGE_SIZE=20;
    
    private final Pane container;
    private final Node loadingScreen;
    private final TextField search;
    private final CheckBox archived;
    private final Button prev_btn,next_btn;
    private int offset=0;
    protected final API api;
    
    public BasicView(API api,Pane container,boolean can_add,boolean has_archived) {
        this.api=api;
        this.container=container;
        StackPane stack = new StackPane();
        
        ScrollPane scr=new ScrollPane(container);
        scr.fitToWidthProperty().setValue(true);
        scr.setStyle("-fx-background: lightgray;-fx-background-color: lightgray;");
        
        loadingScreen=new Label(Localization.CURRENT.getLocalized("app.loading"));
        loadingScreen.setVisible(false);
        
        stack.getChildren().addAll(scr,loadingScreen);
        
        Button reload_btn=new Button();
        reload_btn.getStyleClass().add("reload-button");
        reload_btn.setGraphic(new ImageView(new Image("resources/icons/reload.png")));
        reload_btn.setOnAction((_e)->reloadLater());
                
        search=new TextField();
        search.getStyleClass().add("search");
        search.setOnKeyReleased((_e)->{
            reloadLater();
        });
        
        archived=new CheckBox();
        archived.getStyleClass().add("archived-button");
        archived.setOnAction((_e)->{
            reloadLater();
        });
        
        next_btn=new Button();
        next_btn.getStyleClass().add("next-button");
        next_btn.setGraphic(new ImageView(new Image("resources/icons/next.png")));
        next_btn.setOnAction((_e)->{
            offset+=PAGE_SIZE;
            reloadLater();
        });
        
        prev_btn=new Button();
        prev_btn.getStyleClass().add("prev-button");
        prev_btn.setGraphic(new ImageView(new Image("resources/icons/prev.png")));
        prev_btn.setOnAction((_e)->{
            offset=Math.max(0,offset-PAGE_SIZE);
            reloadLater();
        });
        
        Button add_btn=new Button();
        add_btn.getStyleClass().add("add-button");
        add_btn.setGraphic(new ImageView(new Image("resources/icons/add.png")));
        add_btn.setOnAction((_e)->{
            showAddDialog();
            reloadLater();
        });

        this.setCenter(stack);
        
        HBox bottom=new HBox();
        bottom.setPadding(new Insets(5,5,5,5));
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.getChildren().add(search);
        if(has_archived){
            bottom.getChildren().add(new Label(Localization.CURRENT.getLocalized("list.archived")));
            bottom.getChildren().add(archived);
        }
        bottom.getChildren().addAll(prev_btn,next_btn,reload_btn);
        if(can_add)
            bottom.getChildren().add(add_btn);
        
        this.setBottom(bottom);
        
        this.parentProperty().addListener((s,o,n)->{
            this.reloadLater();
        });
    }
    
    protected void reloadLater() {
        if(!loadingScreen.visibleProperty().get()) {
            loadingScreen.setVisible(true);
            container.getChildren().clear();
            prev_btn.setVisible(offset>0);
            Worker.GLOBAL.addTask(()->{
                try{
                    T[] data=reload(search.getText(),offset,PAGE_SIZE,archived.isSelected());
                    
                    Platform.runLater(()->{
                        next_btn.setVisible(data.length==PAGE_SIZE);
                        for(T d:data) {
                            Node entry=createEntry(d);
                            entry.getStyleClass().add("view-entry");
                            container.getChildren().add(entry);
                        }
                        loadingScreen.setVisible(false);
                    });
                }catch(FormattedException e) {
                    Platform.runLater(()->{
                        UIUtils.showError(e);
                        loadingScreen.setVisible(false);
                    });
                }
            });
        }
    }
    
    protected abstract T[] reload(String q,int offset,int length,boolean archived) throws FormattedException;
    
    protected abstract Node createEntry(T t);
    
    protected void showAddDialog() {
        throw new UnsupportedOperationException();
    }
    
}
