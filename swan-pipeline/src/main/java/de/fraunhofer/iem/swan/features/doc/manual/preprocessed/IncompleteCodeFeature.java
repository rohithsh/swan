package de.fraunhofer.iem.swan.features.doc.manual.preprocessed;

import de.fraunhofer.iem.swan.features.doc.manual.IDocFeature;
import de.fraunhofer.iem.swan.features.doc.manual.FeatureResult;
import de.fraunhofer.iem.swan.features.doc.nlp.AnnotatedMethod;
import de.fraunhofer.iem.swan.features.doc.manual.SecurityVocabulary;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;

/**
 * Checks if words that indicate incomplete implementation are used in the doc comment.
 * <p>
 * List of words are found in {@link SecurityVocabulary#INCOMPLETE_CODE_KEYWORDS}
 *
 * @author Oshando Johnson on 02.09.20
 */
public class IncompleteCodeFeature implements IDocFeature {

    private FeatureResult featureResult;

    public IncompleteCodeFeature() {
        featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult evaluate(AnnotatedMethod annotatedMethod) {
        featureResult.setMethodValue(countWords(annotatedMethod.getMethod().getJavadoc().getMethodComment()));
        featureResult.setClassValue(countWords(annotatedMethod.getMethod().getJavadoc().getClassComment()));

        return featureResult;
    }

    private int countWords(String comment) {

        int counter = 0;
        Document document = new Document(comment.toLowerCase());

        for (Sentence sentence : document.sentences()) {

            for (Token token : sentence.tokens()) {
                if (SecurityVocabulary.INCOMPLETE_CODE_KEYWORDS.contains(token.lemma()))
                    counter++;
            }
        }

        return counter;
    }

    @Override
    public String getName() {
        return "IncompleteCodeFeature";
    }

    @Override
    public String toString() {
        return "IncompleteCodeFeature [" + featureResult + "]";
    }

}
