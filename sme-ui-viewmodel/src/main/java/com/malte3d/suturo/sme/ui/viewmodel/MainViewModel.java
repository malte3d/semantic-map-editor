package com.malte3d.suturo.sme.ui.viewmodel;

import com.malte3d.suturo.commons.messages.Messages;
import javafx.application.HostServices;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MainViewModel {

    @NonNull
    private final HostServices hostServices;

    public void openCopyrightOwnerUrl() {
        hostServices.showDocument(Messages.getString("Application.Help.About.CopyrightOwnerUrl"));
    }
}