package de.fraunhofer.iem.swan.model.toolkit;

import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.MekaFeatureSet;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.Evaluation;
import meka.core.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Meka {

    private MekaFeatureSet features;
    private SwanOptions options;
    private Set<Method> methods;
    private static final Logger logger = LoggerFactory.getLogger(ModelEvaluator.class);

    public Meka(MekaFeatureSet features, SwanOptions options, Set<Method> methods) {
        this.features = features;
        this.options = options;
        this.methods = methods;
    }

    /**
     * Trains and evaluates the model with the given training data and specified classification mode.
     */
    public SrmList trainModel() {

        switch (ModelEvaluator.Phase.valueOf(options.getPhase().toUpperCase())) {
            case VALIDATE:
                crossValidateModel(features.getTrainInstances().get("meka"));
                return null;
            case PREDICT:
                HashMap<String, ArrayList<Category>> predictions = predictModel(features.getTrainInstances().get("meka"), features.getTestInstances().get("meka"), options.getPredictionThreshold());

                for (Method method : methods) {
                    for (Category category : predictions.get(method.getArffSafeSignature())) {
                        method.addCategory(category);
                    }
                }
                return new SrmList(methods);
        }
        return null;
    }

    /**
     * Cross-validates a ML model using the provided instances and outputs metrics.
     *
     * @param instances training instances
     */
    public void crossValidateModel(Instances instances) {

        try {

            BR classifier = new BR();
            String top = "PCut1";
            String verbosity = "7";
            Result result = Evaluation.cvModel(classifier, instances, options.getIterations(), top, verbosity);

            logger.info("Model cross-validation results {}", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Trains a model and uses the trained model to predict the categories for the methods in the test set.
     *
     * @param train     training instances
     * @param test      test instances
     * @param threshold threshold used to determine if a method should be classified into a category
     * @return hash map of method signatures and the categories they're classified into
     */
    public HashMap<String, ArrayList<Category>> predictModel(Instances train, Instances test, double threshold) {

        HashMap<String, ArrayList<Category>> predictions = new HashMap<>();
        try {
            BR classifier = new BR();
            classifier.buildClassifier(train);

            for (int i = 0; i < test.numInstances(); i++) {
                double[] dist = classifier.distributionForInstance(test.instance(i));

                ArrayList<Category> categories = new ArrayList<>();
                for (int p = 0; p < dist.length; p++) {
                    if (dist[p] >= threshold)
                        categories.add(Category.valueOf(test.attribute(p).name().toUpperCase()));
                }
                predictions.put(test.get(i).stringValue(test.attribute("id").index()), categories);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictions;
    }
}