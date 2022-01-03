package de.fraunhofer.iem.swan;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iem.swan.cli.SwanOptions;
import de.fraunhofer.iem.swan.features.FeaturesHandler;
import de.fraunhofer.iem.swan.features.code.soot.SourceFileLoader;
import de.fraunhofer.iem.swan.io.dataset.SrmList;
import de.fraunhofer.iem.swan.io.dataset.SrmListUtils;
import de.fraunhofer.iem.swan.model.ModelEvaluator;
import de.fraunhofer.iem.swan.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

/**
 * Runner for SWAN
 *
 * @author Lisa Nguyen Quang Do
 */

public class SwanPipeline {

    private static final Logger logger = LoggerFactory.getLogger(SwanPipeline.class);
    public static SwanOptions options;

    public SwanPipeline(SwanOptions options) {
        SwanPipeline.options = options;
    }

    /**
     * Executes the analysis and can also be called from outside by clients.
     *
     * @throws IOException In case an error occurs during the preparation
     *                     or execution of the analysis.
     */
    public void run() throws IOException, InterruptedException {

        long startAnalysisTime = System.currentTimeMillis();

        // Load methods in training dataset
        SrmList dataset = SrmListUtils.importFile(options.getDatasetJson(), options.getTrainDataDir());
        logger.info("Loaded {} training methods, distribution={}", dataset.getMethods().size(), Util.countCategories(dataset.getMethods()));

        //Load methods from the test set
        logger.info("Loading test JARs in {}", options.getTestDataDir());
        SourceFileLoader testDataset = new SourceFileLoader(options.getTestDataDir());
        testDataset.load(dataset.getMethods());

        //Initialize and populate features
        FeaturesHandler featuresHandler = new FeaturesHandler(dataset, testDataset, options);
        featuresHandler.createFeatures();

        //Train and evaluate model for SRM and CWE categories
        ModelEvaluator modelEvaluator = new ModelEvaluator(featuresHandler, options);
        modelEvaluator.trainModel();

        //TODO export final list to JSON file
        String outputFile = options.getOutputDir() + File.separator + "swan-srm-cwe-list.json";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(outputFile), dataset);
        logger.info("SRM/CWE list exported to {}", outputFile);

        long analysisTime = System.currentTimeMillis() - startAnalysisTime;
        logger.info("Total runtime {} minutes", analysisTime / 60000);
    }
}