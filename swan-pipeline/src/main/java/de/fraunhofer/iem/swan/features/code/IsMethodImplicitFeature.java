package de.fraunhofer.iem.swan.features.code;

import de.fraunhofer.iem.swan.data.Category;
import de.fraunhofer.iem.swan.data.Method;
import de.fraunhofer.iem.swan.features.code.type.WeightedFeature;

import java.util.ArrayList;

public class IsMethodImplicitFeature extends WeightedFeature implements IFeatureNew {
    private FeatureResult featureResult;
    private ArrayList<String> featureValues;

    public IsMethodImplicitFeature() {
        this.featureResult = new FeatureResult();
    }

    @Override
    public FeatureResult applies(Method method, Category category) {
        if(method.getName().contains("$"))
            this.featureResult.setBooleanValue(Boolean.TRUE);
        else
            this.featureResult.setBooleanValue(Boolean.FALSE);

        return this.featureResult;
    }

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.BOOLEAN;
    }

    @Override
    public String toString(){ return "IsMethodImplicit"; }

    @Override
    public ArrayList<String> getFeatureValues() {
        this.featureValues = new ArrayList<>();
        this.featureValues.add("true");
        this.featureValues.add("false");
        return this.featureValues;
    }
}
