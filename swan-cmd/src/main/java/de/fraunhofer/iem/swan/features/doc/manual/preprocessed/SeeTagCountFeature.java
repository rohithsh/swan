package de.fraunhofer.iem.swan.features.doc.manual.preprocessed;

import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;

/**
 * Counts the number of see Javadoc tags used in the documentation.
 * See tags have the following format: @see reference
 *
 * @author Oshando Johnson on 02.08.20
 */
public class SeeTagCountFeature extends TagCountFeature implements IDocFeature {

    public SeeTagCountFeature() {
        super();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        unprocessedDocResult.setClassValue(countTags(annotatedMethod.getMethod().getJavadoc().getClassComment(),
                Constants.TAG.SEE));
        unprocessedDocResult.setMethodValue(countTags(annotatedMethod.getMethod().getJavadoc().getMethodComment(),
                Constants.TAG.SEE));

        return unprocessedDocResult;
    }

    @Override
    public String toString() {
        return "SeeTagCountFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "SeeTagCountFeature";
    }
}
