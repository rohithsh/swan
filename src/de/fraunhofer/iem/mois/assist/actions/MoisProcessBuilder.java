package de.fraunhofer.iem.mois.assist.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import de.fraunhofer.iem.mois.assist.comm.MoisNotifier;
import de.fraunhofer.iem.mois.assist.util.Constants;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

//Executes Susi with updated configuration file on a separate thread.
public class MoisProcessBuilder extends Thread {

    private static HashMap<String, String> parameters;
    private AnActionEvent anActionEvent;

    MoisProcessBuilder(AnActionEvent actionEvent, HashMap<String, String> param) {

        anActionEvent = actionEvent;
        parameters = param;
    }

    public void run() {
        super.run();

        String currentTimestamp = getCurrentTimestamp("yyyy-MM-dd-HHmmss");

        File outputFolder = new File(parameters.get(Constants.MOIS_OUTPUT_DIR));
        outputFolder.mkdirs();

        File logFile = new File(outputFolder, currentTimestamp + Constants.MOIS_LOG_SUFFIX);
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar",
                parameters.get(Constants.MOIS_JAR_DIR),
                parameters.get(Constants.MOIS_SOURCE_DIR),
                parameters.get(Constants.MOIS_TRAIN_DIR),
                parameters.get(Constants.MOIS_CONFIG_FILE),
                parameters.get(Constants.MOIS_OUTPUT_DIR) + File.separator + currentTimestamp + Constants.OUTPUT_TEXT_SUFFIX,
                parameters.get(Constants.MOIS_OUTPUT_DIR) + File.separator + currentTimestamp + Constants.OUTPUT_JSON_SUFFIX);

        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

        String completionTimestamp = getCurrentTimestamp("HH:mm");
        String message = Constants.NOTIFICATION_END_MOIS_SUCCESS + completionTimestamp;

        try {
            Process susiProcess = processBuilder.start();
            int result = susiProcess.waitFor();

            if (result == 0)
                message = Constants.NOTIFICATION_END_MOIS_SUCCESS + completionTimestamp;
            else
                message = Constants.NOTIFICATION_END_MOIS_FAIL;

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
            message = Constants.NOTIFICATION_END_MOIS_FAIL;
        }

        HashMap<String, String> results = new HashMap<String, String>();
        results.put(Constants.MOIS_OUTPUT_FILE, parameters.get(Constants.MOIS_OUTPUT_DIR) + File.separator + currentTimestamp + Constants.OUTPUT_JSON_SUFFIX);
        results.put(Constants.MOIS_OUTPUT_LOG, parameters.get(Constants.MOIS_OUTPUT_DIR) + File.separator + currentTimestamp + Constants.MOIS_LOG_SUFFIX);
        results.put(Constants.MOIS_OUTPUT_MESSAGE, message);

        anActionEvent.getPresentation().setEnabled(true);

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        MessageBus messageBus = project.getMessageBus();
        MoisNotifier publisher = messageBus.syncPublisher(MoisNotifier.END_MOIS_PROCESS_TOPIC);
        publisher.launchMois(results);
    }

    private String getCurrentTimestamp(String dateFormat) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDateTime.now().format(formatter);
    }
}