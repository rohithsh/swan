package de.fraunhofer.iem.mois.assist.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.MoisNotifier;
import de.fraunhofer.iem.mois.assist.data.JSONWriter;
import de.fraunhofer.iem.mois.assist.ui.SummaryToolWindow;
import de.fraunhofer.iem.mois.assist.ui.MoisLauncherDialog;

import javax.swing.FocusManager;
import java.awt.*;

/**
 * Action to start load dialog for configuring MOIS before running.
 * @author Oshando Johnson
 */


public class LaunchMoisAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();

        //Export changes to configuration files
        JSONWriter exportFile = new JSONWriter();

      /*  try {
            exportFile.writeToJsonFile(JSONFileLoader.getMethods(), JSONFileLoader.getConfigurationFile(true));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //Launch Dialog
        Window activeWindow = FocusManager.getCurrentManager().getActiveWindow();

        MoisLauncherDialog dialog = new MoisLauncherDialog(activeWindow, project, true);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {

            MoisProcessBuilder moisProcessThread = new MoisProcessBuilder(anActionEvent, dialog.getParameters());
            moisProcessThread.start();
            anActionEvent.getPresentation().setEnabled(false);
            MoisNotifier publisher = messageBus.syncPublisher(MoisNotifier.START_MOIS_PROCESS_TOPIC);
            publisher.launchMois(null);
        }
    }

    @Override
    public void update(AnActionEvent event) {

        //Disable/Enable action button
        if (SummaryToolWindow.CONFIG_FILE_SELECTED)
            event.getPresentation().setEnabled(true);
        else
            event.getPresentation().setEnabled(false);
    }
}