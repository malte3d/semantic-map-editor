package com.malte3d.suturo.sme.ui.view;

import com.malte3d.suturo.commons.Version;
import com.malte3d.suturo.commons.messages.Messages;
import com.malte3d.suturo.sme.ui.viewmodel.MainViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Year;


@Slf4j
@RequiredArgsConstructor
public class MainView {

    private final MainViewModel viewModel;

    @FXML
    Parent mainView;

    @FXML
    public MenuBar menuBar;

    /* File */
    @FXML
    Menu menuFile;
    @FXML
    private MenuItem menuFileNew;
    @FXML
    private MenuItem menuFileOpen;
    @FXML
    private MenuItem menuFileOpenRecent;
    @FXML
    private MenuItem menuFileClose;
    @FXML
    private MenuItem menuFileSave;
    @FXML
    private MenuItem menuFileSaveAs;
    @FXML
    private MenuItem menuFileSettings;
    @FXML
    private MenuItem menuFileExit;

    /* Edit */
    @FXML
    public Menu menuEdit;
    @FXML
    private MenuItem menuEditUndo;
    @FXML
    private MenuItem menuEditRedo;
    @FXML
    private MenuItem menuEditCut;
    @FXML
    private MenuItem menuEditCopy;
    @FXML
    private MenuItem menuEditPaste;
    @FXML
    private MenuItem menuEditDelete;

    /* Help */
    @FXML
    public Menu menuHelp;
    @FXML
    private MenuItem menuHelpAbout;

    public void initialize() {

        initMenuFile();
        initMenuEdit();
        initMenuHelp();
    }

    private void initMenuFile() {

        menuFileNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        menuFileOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        menuFileSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menuFileSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
        menuFileSettings.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.ALT_DOWN));

        menuFileExit.setOnAction(event -> Platform.exit());
    }

    private void initMenuEdit() {

        menuEditUndo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        menuEditRedo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        menuEditCut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        menuEditCopy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        menuEditPaste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        menuEditDelete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
    }

    private void initMenuHelp() {

        menuHelpAbout.setOnAction(event -> showHelpAboutDialog());
    }

    private void showHelpAboutDialog() {

        Hyperlink copyrightOwnerLink = new Hyperlink(Messages.getString("Application.Help.About.CopyrightOwner"));
        copyrightOwnerLink.setOnAction(event -> viewModel.openCopyrightOwnerUrl());

        TextFlow copyrightText = new TextFlow(
                new Text(Messages.getString("Application.Help.About.Copyright", Year.now().getValue())),
                new Text(" "),
                copyrightOwnerLink
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Messages.getString("Application.Help.About.Title"));
        alert.setHeaderText(Messages.getString("Application.Help.About.Header", Version.getVersion(MainView.class)));
        alert.getDialogPane().setContent(copyrightText);
        alert.initOwner(menuBar.getScene().getWindow());
        alert.showAndWait();
    }
}