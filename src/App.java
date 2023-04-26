import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    private LanguageClassifier classifier;
    private List<String> languages;
    private List<Language> languageDataTrain;
    private List<Language> languageDataTest;
    private double learningRate = 1;

    public static void main(String[] args) {
        launch(args);
    }

    private void trainClassifier() {
        classifier = new LanguageClassifier(languages, learningRate);
        classifier.train(languageDataTrain, languageDataTest, 1000, 10);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Language Classifier");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label inputLabel = new Label("Enter text to classify:");
        grid.add(inputLabel, 0, 0);

        TextArea inputTextArea = new TextArea();
        inputTextArea.setWrapText(true);
        inputTextArea.setPrefHeight(100);
        grid.add(inputTextArea, 0, 1, 2, 1);

        Button classifyButton = new Button("Classify");
        HBox hboxBtn = new HBox(10);
        hboxBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hboxBtn.getChildren().add(classifyButton);
        grid.add(hboxBtn, 1, 2);

        Label resultLabel = new Label();
        grid.add(resultLabel, 0, 3, 2, 1);

        Label languagesLabel = new Label();
        grid.add(languagesLabel, 0, 4, 2, 1);

        Label learningRateLabel = new Label("Learning rate (0-1):");
        grid.add(learningRateLabel, 0, 5);

        TextField learningRateInput = new TextField("1");
        grid.add(learningRateInput, 1, 5);

        Button retrainButton = new Button("Retrain");
        grid.add(retrainButton, 1, 6);

        String dataDirectoryTrain = "Data/TrainingData";
        languageDataTrain = FileReader.parse(dataDirectoryTrain);

        String dataDirectoryTest = "Data/TestData";
        languageDataTest = FileReader.parse(dataDirectoryTest);

        languages = new ArrayList<>();
        for (Language data : languageDataTrain) {
            if (!languages.contains(data.getLanguage())) {
                languages.add(data.getLanguage());
            }
        }

        StringBuilder languagesList = new StringBuilder("Languages:\n");
        for (String language : languages) {
            languagesList.append(language).append("\n");
        }
        languagesLabel.setText(languagesList.toString());

        trainClassifier();

        classifyButton.setOnAction(e -> {
            String inputText = inputTextArea.getText();
            inputText = inputText.toLowerCase().replaceAll("[^a-zA-Z]", "");
            String predictedLanguage = classifier.classify(inputText);
            resultLabel.setText("Predicted Language: " + predictedLanguage);

            StringBuilder thresholds = new StringBuilder("Thresholds:\n");
            double[] features = LanguageClassifier.extractFeatures(inputText);
            for (int i = 0; i < classifier.getPerceptrons().size(); i++) {
                Perceptron perceptron = classifier.getPerceptrons().get(i);
                double threshold = perceptron.predict(features);
                String language = languages.get(i);
                thresholds.append(language).append(": ").append(threshold).append("\n");
            }
            languagesLabel.setText(thresholds.toString());
        });

        retrainButton.setOnAction(e -> {
            learningRate = Double.parseDouble(learningRateInput.getText());
            trainClassifier();
        });

        Scene scene = new Scene(grid, 450, 450);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(450);
        primaryStage.setMinHeight(450);
        primaryStage.show();
    }
}
