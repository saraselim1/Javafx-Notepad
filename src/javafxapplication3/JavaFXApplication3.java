/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author Sara Selim
 */
public class JavaFXApplication3 extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextArea ta = new TextArea();

        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem undoItem = new MenuItem("Undo");
        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem selectItem = new MenuItem("Select All");
        MenuItem compileItem = new MenuItem("Compile");
        MenuItem aboutItem = new MenuItem("About");

        SeparatorMenuItem smi1 = new SeparatorMenuItem();
        SeparatorMenuItem smi2 = new SeparatorMenuItem();
        SeparatorMenuItem smi3 = new SeparatorMenuItem();
        SeparatorMenuItem smi4 = new SeparatorMenuItem();

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(newItem, openItem, saveItem, smi1, exitItem);
        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(undoItem, smi2, cutItem, copyItem, pasteItem, deleteItem, smi3, selectItem, smi4, compileItem);
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(aboutItem);

        MenuBar mb = new MenuBar();
        mb.getMenus().addAll(fileMenu, editMenu, helpMenu);

        BorderPane root = new BorderPane();
        root.setTop(mb);
        root.setCenter(ta);

        Scene scene = new Scene(root, 300, 250);

        if (fileHandeler.file != null) {
            primaryStage.setTitle(fileHandeler.file.getName());
        } else {
            primaryStage.setTitle("Text editor");
        }
        primaryStage.setScene(scene);
        primaryStage.show();

        newItem.setOnAction((ActionEvent event) -> {
            int status = fileHandeler.checkEmpty(ta.getText());
            switch (status) {
                case 3:
                    break;
                case 1:
                case 4:
                case 2:
                    ta.setText(" ");
                    fileHandeler.file = null;
                    break;
            }
        });
        openItem.setOnAction((ActionEvent event) -> {
            int status = fileHandeler.checkEmpty(ta.getText());
            String str;
            switch (status) {
                case 1:
                case 4:
                case 2:
                    str = fileHandeler.openFile();
                    if (str != null) {
                        ta.setText(str);
                    }
                    if (fileHandeler.file != null) {
                        primaryStage.setTitle(fileHandeler.file.getName());
                    } else {
                        primaryStage.setTitle("Text editor");
                    }
                    break;
                case 3:
                    break;
            }
        });
        saveItem.setOnAction((ActionEvent event) -> {
            fileHandeler.saveFile(ta.getText());
            if (fileHandeler.file != null) {
                primaryStage.setTitle(fileHandeler.file.getName());
            } else {
                primaryStage.setTitle("Text editor");
            }
        });
        exitItem.setOnAction((ActionEvent event) -> {
            fileHandeler.exitFile(ta.getText());
        });
        aboutItem.setOnAction((ActionEvent event) -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("About");
            alert.setContentText("Created by Sara Selim ");
            alert.showAndWait();
        });
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            fileHandeler.exitFile(ta.getText());
        });
        cutItem.setOnAction((ActionEvent event) -> {
            ta.cut();
        });
        copyItem.setOnAction((ActionEvent event) -> {
            ta.copy();
        });
        pasteItem.setOnAction((ActionEvent event) -> {
            ta.paste();
        });
        deleteItem.setOnAction((ActionEvent event) -> {
            ta.deleteText(ta.getSelection());
        });
        selectItem.setOnAction((ActionEvent event) -> {
            ta.selectAll();
        });
        undoItem.setOnAction((ActionEvent event) -> {
            ta.undo();
        });
        compileItem.setOnAction((ActionEvent event) -> {
            try {
                if (fileHandeler.file != null) {
                    Runtime.getRuntime().exec("javac " + fileHandeler.file.getAbsolutePath());
                } else {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("Compile");
                    alert.setContentText("you must save the file befor compile! ");
                    alert.showAndWait();
                }

            } catch (IOException ex) {
                Logger.getLogger(fileHandeler.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        ta.textProperty().addListener((final ObservableValue<? extends String> observable, final String oldValue, final String newValue) -> {
            fileHandeler.editd = true;
            fileHandeler.saved = false;
        });

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

class fileHandeler {

    public static boolean saved = false;
    public static boolean editd = false;
    public static File file = null;

    public static int checkEmpty(String str) {
        if (!str.trim().isEmpty() && !saved && editd) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Do you want to save?");

            ButtonType buttonTypeSave = new ButtonType("Save");
            ButtonType buttonTypeNotSave = new ButtonType("Don't Save");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeNotSave, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeSave) {
                saveFile(str);
                if (saved == true) {
                    Alert alert2 = new Alert(AlertType.INFORMATION);
                    alert2.setTitle("Information Dialog");
                    alert2.setHeaderText("Informaion");
                    alert2.setContentText("Your file is saved");
                    alert2.showAndWait();
                    return 1;
                } else {
                    return 3;
                }
            } else if (result.get() == buttonTypeNotSave) {
                return 2;
            } else if (result.get() == buttonTypeCancel) {
                return 3;
            }
        }
        return 4;

    }

    public static void saveFile(String str) {
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        try {
            if (file == null) {
                FileChooser fileChooser = new FileChooser();

                fileChooser.getExtensionFilters().addAll(
                        new ExtensionFilter("Text Files", "*.txt"),
                        new ExtensionFilter("Java Files", "*.java"));

                file = fileChooser.showSaveDialog(null);
            }
            if (file != null) {
                fileWriter = new FileWriter(file);
                printWriter = new PrintWriter(fileWriter);
                printWriter.println(str);
                printWriter.close();
                fileWriter.close();
                saved = true;
                editd = false;
            }
        } catch (IOException ex) {
            saved = false;
            Logger.getLogger(fileHandeler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String openFile() {
        //FileReader fileReader = null;
        FileInputStream fis = null;
        byte[] c = null;

        FileChooser fileChooser = new FileChooser();
        //fileChooser.setFileSelectionMode(FileChooser.FILES_ONLY);

        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"),
                new ExtensionFilter("Java Files", "*.java"));

        //read from file
        file = fileChooser.showOpenDialog(null);
        try {
            if (file != null) {
                fis = new FileInputStream(file);
                c = new byte[fis.available()];
                fis.read(c);
                fis.close();
                editd = false;
                saved = true;

                return new String(c);
            }
        } catch (IOException ex) {
            Logger.getLogger(fileHandeler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public static void exitFile(String str) {
        int status = fileHandeler.checkEmpty(str);
        switch (status) {
            case 1:
            case 2:
            case 4:
                Platform.exit();
                break;
            case 3:
                break;
        }
    }
}
