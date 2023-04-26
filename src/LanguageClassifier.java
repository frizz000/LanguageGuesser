import java.util.ArrayList;
import java.util.List;

public class LanguageClassifier {
    private final List<Perceptron> perceptrons;
    public final List<String> languages;

    public LanguageClassifier(List<String> languages, double learningRate) {
        this.languages = languages;
        this.perceptrons = new ArrayList<>();
        for (int i = 0; i < languages.size(); i++) {
            perceptrons.add(new Perceptron(26, learningRate));
        }
    }

    public static double[] extractFeatures(String text) {
        double[] features = new double[26];
        int totalCount = 0;

        for (char c : text.toLowerCase().toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                features[c - 'a']++;
                totalCount++;
            }
        }

        for (int i = 0; i < features.length; i++) {
            features[i] = features[i] / totalCount;
        }
        return features;
    }

    public void train(List<Language> languageDataTrain, List<Language> languageDataTest, int maxEpochs, int printInterval) {
        for (int i = 0; i < languages.size(); i++) {
            String language = languages.get(i);
            Perceptron perceptron = perceptrons.get(i);
            perceptron.train(languageDataTrain, languageDataTest, language, maxEpochs, printInterval, language);
        }
    }

    public String classify(String text) {
        double[] features = extractFeatures(text);
        double maxThreshold = Double.NEGATIVE_INFINITY;
        int maxIndex = -1;

        for (int i = 0; i < perceptrons.size(); i++) {
            Perceptron perceptron = perceptrons.get(i);
            double threshold = perceptron.predict(features);
            if (threshold > maxThreshold) {
                maxThreshold = threshold;
                maxIndex = i;
            }
        }
//        for (int i = 0; i < perceptrons.size(); i++) {
//            Perceptron perceptron = perceptrons.get(i);
//            double threshold = perceptron.predict(features);
//            System.out.println(languages.get(i) + " " + threshold);
//        }
//        System.out.println("=====================================");
        return languages.get(maxIndex);
    }

    public List<Perceptron> getPerceptrons() {
        return perceptrons;
    }
}


