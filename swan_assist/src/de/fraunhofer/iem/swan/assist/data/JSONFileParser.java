package de.fraunhofer.iem.swan.assist.data;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import de.fraunhofer.iem.swan.Parser;
import de.fraunhofer.iem.swan.assist.util.Constants;
import de.fraunhofer.iem.swan.data.Method;

import java.util.HashMap;
import java.util.ResourceBundle;

public class JSONFileParser {

    private String congFilePath;

    public JSONFileParser(String path) {
        congFilePath = path;
    }

    public String getCongFilePath() {
        return congFilePath;
    }

    public void setCongFilePath(String congFilePath) {
        this.congFilePath = congFilePath;
    }


    public HashMap<String, MethodWrapper> parseJSONFileMap() {

        HashMap<String, MethodWrapper> methods = new HashMap<String, MethodWrapper>();
        Parser parser = new Parser(null);
        ResourceBundle resource = ResourceBundle.getBundle("dialog_messages");

        try {
            for (Method method : parser.parseFile(congFilePath)) {

                MethodWrapper methodWrapper = new MethodWrapper(method);

                if(method.getDiscovery().equals(Constants.TRAINING_METHOD))
                    methodWrapper.setTrainingMethod(true);
                else
                    methodWrapper.setTrainingMethod(false);

                methods.put(methodWrapper.getSignature(true), methodWrapper);
            }
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification("SWAN_Assist", resource.getString("Messages.Title.FileLoadError"), resource.getString("Messages.Error.FileLoadError"), NotificationType.ERROR));
        }

        return methods;
    }
}

