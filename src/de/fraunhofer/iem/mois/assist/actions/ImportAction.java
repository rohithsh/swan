package de.fraunhofer.iem.mois.assist.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.FileSelectedNotifier;
import de.fraunhofer.iem.mois.assist.ui.SummaryToolWindow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * Action to import configuration file.
 *
 * @author Oshando Johnson
 */


public class ImportAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);

        File projectPath;

        if (project.getBasePath() == null)
            projectPath = FileSystemView.getFileSystemView().getDefaultDirectory();
        else
            projectPath = new File(project.getBasePath());

        JFileChooser fileChooser = new JFileChooser(projectPath);
        FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("JSON Files", "json");
        fileChooser.setFileFilter(jsonFilter);

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {

            SummaryToolWindow.CONFIG_FILE_SELECTED = true;
            File selectedFile = fileChooser.getSelectedFile();

            MessageBus messageBus = project.getMessageBus();
            FileSelectedNotifier publisher = messageBus.syncPublisher(FileSelectedNotifier.INITIAL_FILE_NOTIFIER_TOPIC);
            publisher.notifyFileChange(selectedFile.getAbsoluteFile().toString());
        }
    }
}
