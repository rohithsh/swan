package de.fraunhofer.iem.swan.features.doc.manual.preprocessed;

import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.nlp.NLPUtils;

/**
 * Counts the number of uppercase words in the original text.
 * Uppercase words can either be abbreviations or warnings.
 *
 * @author Oshando Johnson on 23.07.20
 */
public class UppercaseWordsCountFeature implements IDocFeature {

    private FeatureResult unprocessedDocResult;

    public UppercaseWordsCountFeature() {
        unprocessedDocResult = new FeatureResult();
    }


    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {

        unprocessedDocResult.setClassValue(NLPUtils.regexCounter(annotatedMethod.getMethod().getJavadoc().getClassComment(), Constants.UPPERCASE_WORD_PATTERN));
        unprocessedDocResult.setMethodValue(NLPUtils.regexCounter(annotatedMethod.getMethod().getJavadoc().getMethodComment(), Constants.UPPERCASE_WORD_PATTERN));

        return unprocessedDocResult;
    }

    @Override
    public String toString() {
        return "UppercaseWordsCountFeature [" + unprocessedDocResult + "]";
    }

    @Override
    public String getName() {
        return "UppercaseWordsCountFeature";
    }
}
