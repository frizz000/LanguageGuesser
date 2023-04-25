import java.util.List;
import java.util.Random;

public class Perceptron {
    private final double[] weights;
    private final double learningRate;
    private final Random random;
    int noImprovementCounter = 0;
    int noImprovementLimit = 5;

    public Perceptron(int numOfFeatures, double learningRate) {
        this.weights = new double[numOfFeatures + 1];
        this.learningRate = learningRate;
        this.random = new Random();
        initializeWeights();
    }

    private void initializeWeights() {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = random.nextDouble() * 2 - 1;
        }
    }

    public double predict(double[] inputs) {
        double threshold = weights[0];
        for (int i = 0; i < inputs.length; i++) {
            threshold += weights[i + 1] * inputs[i];
        }
        return threshold;
    }

    private double calculateAccuracy(List<Language> languageData, String targetLanguage) {
        int correctPredictions = 0;
        for (Language data : languageData) {
            double[] inputs = LanguageClassifier.extractFeatures(data.getText());
            int label = data.getLanguage().equals(targetLanguage) ? 1 : 0;
            int prediction = predict(inputs) >= 0 ? 1 : 0;
            if (prediction == label) {
                correctPredictions++;
            }
        }
        return (double) correctPredictions / languageData.size();
    }

    public void train(List<Language> languageDataTrain, List<Language> languageDataTest, String targetLanguage, int maxEpochs, int printInterval, String language) {
        int epoch = 0;
        double previousAccuracy = 0;
        boolean training = true;

        while (training && epoch < maxEpochs) {
            for (Language data : languageDataTrain) {
                double[] inputs = LanguageClassifier.extractFeatures(data.getText());
                int label = data.getLanguage().equals(targetLanguage) ? 1 : 0;
                int prediction = predict(inputs) >= 0 ? 1 : 0;
                int error = label - prediction;

                for (int j = 0; j < inputs.length; j++) {
                    weights[j + 1] += learningRate * error * inputs[j];
                }
                weights[0] += learningRate * error;
            }

            double currentAccuracy = calculateAccuracy(languageDataTest, targetLanguage);
            if (epoch % printInterval == 0) {
                System.out.println("Language: " + language + ", Epoch: " + epoch + ", Accuracy: " + String.format("%.2f", currentAccuracy * 100) + "%");
            }

            if (currentAccuracy >= previousAccuracy) {
                previousAccuracy = currentAccuracy;
                noImprovementCounter = 0;
                epoch++;
            } else {
                noImprovementCounter++;
                if (noImprovementCounter >= noImprovementLimit) {
                    training = false;
                }
            }
        }
    }
}
