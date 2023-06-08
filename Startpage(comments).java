//package game;

// SCENE
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// JSON
import java.io.FileReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Startpage extends Application {

	 private int[][] grid;
	 //private int [][] gridSolution;
	 private TextField selectedTextField;
	 private int elapsedTimeSeconds = 0;
	 private Label timerLabel;
	 private Timeline timer;
	 private Label dateLabel;
	 private String category; // numbers or images
	 private Stage primaryStage;
	 private Scene logoAnimation;
	 private Scene homeScene;
	 private Scene sudokuScene;
	 private Scene scoresScene;
	 private Scene rulesScene;
	 private LocalDate currentDate; //= LocalDate.now();
	 private boolean sudokuCompleted = false;
    
 static void main(String[] args) {
        launch(args);
    }
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		
		// Create the VBox layout
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(30);
        root.setBackground(new Background(new BackgroundFill(Color.SNOW, null, null)));
        
        // Logo icon
        Image icon = new Image("shortLogo.png");
        primaryStage.getIcons().add(icon);     
        
        // Logo
        Image imageLogo = new Image("logo.png");
        ImageView imageViewLogo = new ImageView(imageLogo);
        imageViewLogo.setPreserveRatio(true);
        imageViewLogo.setFitWidth(300); 
        imageViewLogo.setFitHeight(300); 
        // Alignment of logo
        StackPane stackPane = new StackPane(imageViewLogo);
        stackPane.setPadding(new Insets(10));

        // Create the "Play" button
        Button playButton = new Button("Play");
        playButton.setPrefWidth(100);
        playButton.setStyle(" -fx-background-color: linen;" + "-fx-background-radius: 5; -fx-font-family: garamond;");
        
        // Create the "Scores" button
        Button scoresButton = new Button("Scores");
        scoresButton.setPrefWidth(100);
        scoresButton.setStyle(" -fx-background-color: linen;" + "-fx-background-radius: 5; -fx-font-family: garamond;");
        
        // Create the "Rules" button
        Button rulesButton = new Button("Rules");
        rulesButton.setPrefWidth(100);
        rulesButton.setStyle(" -fx-background-color: linen;" + "-fx-background-radius: 5; -fx-font-family: garamond;");
        
        // Date
        dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 16px; -fx-font-family: garamond;");
        BorderPane.setMargin(dateLabel, new Insets(10, 10, 0, 0));
        updateDateLabel();
        
        // Add the elements to the VBox layout
        root.getChildren().addAll(dateLabel, stackPane, playButton, scoresButton, rulesButton);
        
        // Create the scene and set it on the stage
        homeScene = new Scene(root, 400, 450);
        primaryStage.setScene(homeScene);
        primaryStage.setTitle("Daily Sudoku");
        primaryStage.show();
        
        // Event 1: press play
        playButton.setOnAction(event -> {
            primaryStage.setTitle("Sudoku Game"); 
            //sudokuCompleted = true;
           
            if (sudokuCompleted && currentDate.equals(LocalDate.now())) {
            	playButton.setDisable(true);
            	Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Daily Sudoku completed");
                alert.setHeaderText(null);
                alert.setContentText("You have already completed your Sudoku for today, we hope to see you again tomorrow!");
                alert.showAndWait();
            }
            else if(currentDate != LocalDate.now()) {
            	playButton.setDisable(false);
            	currentDate = LocalDate.now();
            	sudokuCompleted = false;
            
            // Get the current date
            currentDate = LocalDate.now(); 
            
            // Generate the data
            loadData(currentDate);
            
            // Create Sudoku Grid (different version depending on category)
            
            // Gridpane for Textfields (add numbers to grid)
        	GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setBackground(new Background(new BackgroundFill(Color.SNOW, null, null)));
            
            // Textfields
            if (category.equals("numbers")) {
            	for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        TextField textField = new TextField();
                        textField.setPrefWidth(40);
                        textField.setPrefHeight(40);
                        textField.setAlignment(Pos.CENTER);
                        textField.setStyle("-fx-control-inner-background: white ;-fx-border-color: linen; -fx-border-width: 0.5px; -fx-font-family: garamond;");

                        textField.setOnMouseClicked(e -> selectedTextField = textField);
                        
                        if (grid[i][j] != 0) {
                            textField.setText(String.valueOf(grid[i][j]));
                            textField.setEditable(false);
                            textField.setStyle("-fx-control-inner-background: whitesmoke;-fx-border-color: whitesmoke; -fx-border-width: 0.5px; -fx-font-family: garamond;");
                        }

                        gridPane.add(textField, j, i);
     
                    // Padding
                    if ((j + 1) % 3 == 0 && j != 8) {
                        GridPane.setMargin(textField, new Insets(0, 10, 0, 0)); // Right margin
                    }
                    if ((i + 1) % 3 == 0 && i != 8) {
                        GridPane.setMargin(textField, new Insets(0, 0, 10, 0)); // Bottom margin
                    } 
                    if (i == 2 | i == 5 && j == 2 | j == 5) {
                    	GridPane.setMargin(textField, new Insets(0, 10, 10, 0));
                    }  
                } 
            	}
            }
            else if(category.equals("images")){
            	for (int i = 0; i < 9; i++) {
                  for (int j = 0; j < 9; j++) {
                      TextField textField = new TextField();
                      textField.setPrefWidth(40);
                      textField.setPrefHeight(40);
                      textField.setAlignment(Pos.CENTER);
                      textField.setStyle("-fx-control-inner-background: white ;-fx-border-color: linen; -fx-border-width: 0.5px;");
                      
                      textField.setOnMouseClicked(e -> selectedTextField = textField);
                      
                      if (grid[i][j] != 0) {
                          textField.setStyle("-fx-background-image: url('image" + grid[i][j] +".png'); " +
                                  "-fx-background-repeat: no-repeat; " +
                                  "-fx-background-size: cover; " +
                                  "-fx-background-position: center;" +
                                  "-fx-border-color: linen;" +
                                  "-fx-border-width: 0.5px;");
                          textField.setEditable(false);
                      }
                      gridPane.add(textField, j, i);
                      
                      // Padding
                      if ((j + 1) % 3 == 0 && j != 8) {
                          GridPane.setMargin(textField, new Insets(0, 10, 0, 0)); // Right margin
                      }
                      if ((i + 1) % 3 == 0 && i != 8) {
                          GridPane.setMargin(textField, new Insets(0, 0, 10, 0)); // Bottom margin
                      } 
                      if (i == 2 | i == 5 && j == 2 | j == 5) {
                      	GridPane.setMargin(textField, new Insets(0, 10, 10, 0));
                      } 
                  }
                }
            }

            // Buttons (for setting numbers)
            HBox buttonBox = new HBox(5);
            buttonBox.setAlignment(Pos.CENTER);
            
            if (category.equals("numbers")) {
                for (int i = 1; i <= 9; i++) {
                    Button button = new Button(String.valueOf(i));
                    button.setPrefWidth(40);
                    button.setPrefHeight(40);
                    button.getStyleClass().add("custom-button");
                    int number = i;
                    button.setOnAction(e -> setNumber(number));
                    buttonBox.getChildren().add(button);
                }
            } 
            else if (category.equals("images")) {
            	for (int i = 1; i < 10; i++) {
                	final int INDEX = i; // Introduce a final variable to capture the value of i
                	
                	Image image = new Image("image"+i+".png");
            		ImageView imageView = new ImageView(image);
            		imageView.setPreserveRatio(true);
            		imageView.setFitWidth(40);
            		
                    Button button = new Button();
                    button.setGraphic(imageView);
                    button.setPrefWidth(40);
                    button.setPrefHeight(40);
                    button.setOnAction(e -> setPicture(INDEX));
                    button.setStyle(" -fx-background-color: white;" + "-fx-background-radius: 5;");
                    buttonBox.getChildren().add(button);
            	}
            }
              
            // Date
            dateLabel = new Label();
            dateLabel.setStyle("-fx-font-size: 16px; -fx-font-family: garamond;");
            BorderPane.setMargin(dateLabel, new Insets(10, 10, 0, 0));
            BorderPane.setAlignment(dateLabel, Pos.TOP_RIGHT);
            updateDateLabel();
            
            // Timer
            timerLabel = new Label("00:00");
            timerLabel.setStyle("-fx-font-size: 16;-fx-font-family: garamond;");
            BorderPane.setMargin(timerLabel, new Insets(5, 10, 0, 0));
            BorderPane.setAlignment(timerLabel, Pos.TOP_RIGHT);
            
            timer = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
                elapsedTimeSeconds++;
                updateTimerLabel();
            }));
            timer.setCycleCount(Timeline.INDEFINITE);
            timer.play();
       
            //Homepage Button
            ImageView iconBack = new ImageView("back.png");
    		iconBack.setPreserveRatio(true);
    		iconBack.setFitWidth(20);
            Button homeButton = new Button();
            homeButton.setPrefHeight(40);
            homeButton.setPrefWidth(20);
            homeButton.setGraphic(iconBack);
            homeButton.setStyle("-fx-background-color: linen;" + "-fx-background-radius: 5;");
           
            homeButton.setOnAction(e -> goBackToHomepage()); 
            
            // Arrangement of scene
            BorderPane sudokuLayout = new BorderPane();
            sudokuLayout.setTop(dateLabel);
            sudokuLayout.setLeft(homeButton);
            sudokuLayout.setCenter(gridPane);
            sudokuLayout.setBottom(buttonBox);
            sudokuLayout.setRight(timerLabel);
            sudokuLayout.setBackground(new Background(new BackgroundFill(Color.SNOW, null, null)));
            sudokuLayout.setPadding(new Insets(10));
            BorderPane.setMargin(buttonBox, new Insets(10,10,10,10));
            
            sudokuScene = new Scene(sudokuLayout, 600, 550);
            
            primaryStage.setTitle("Sudoku Game");
            primaryStage.setScene(sudokuScene);
            primaryStage.show();
            }
        });
        
        // Event 2: press scores
        scoresButton.setOnAction(event -> {
        	// Set up VBox layout
        	VBox vbox = new VBox();
            vbox.setSpacing(20);
            vbox.setPadding(new Insets(30));
            vbox.setStyle("-fx-background-color: snow");
            vbox.setAlignment(Pos.CENTER);
            
            // Button back to homepage
            ImageView iconBack = new ImageView("back.png");
    		iconBack.setPreserveRatio(true);
    		iconBack.setFitWidth(20);
    		
    		Button homeButton = new Button();
            homeButton.setPrefHeight(40);
            homeButton.setPrefWidth(20);
            homeButton.setGraphic(iconBack);
            homeButton.setStyle("-fx-background-color: linen;" + "-fx-background-radius: 5;");
            homeButton.setOnAction(e -> goBackToHomepage()); 
            
            HBox buttonBox = new HBox();
            buttonBox.setAlignment(Pos.TOP_LEFT);
            buttonBox.getChildren().add(homeButton);
            
            // Create the title label
            Label titleLabel = new Label("Scores");
            titleLabel.setFont(Font.font("Garamond", 24));
            
            // Table with day and your ranking and score

            // Graph comparing your score and average score
           
            
            // Add the title and content labels to the VBox
            vbox.getChildren().addAll(buttonBox, titleLabel);

            // Create the scene
            scoresScene = new Scene(vbox, 450, 500);

            // Set the scene on the stage and show the stage
            primaryStage.setScene(scoresScene);
            primaryStage.setTitle("Daily Sudoku Scores");
        });
        
        // Event 3: press rules
        rulesButton.setOnAction( event -> {
        	// Set up the main VBox layout
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new Insets(30));
            vbox.setStyle("-fx-background-color: snow");
            vbox.setAlignment(Pos.CENTER);

            // Create the title label
            Label titleLabel = new Label("Rules");
            titleLabel.setFont(Font.font("Garamond", 24));

            // Create startpage button
            ImageView iconBack = new ImageView("back.png");
    		iconBack.setPreserveRatio(true);
    		iconBack.setFitWidth(20);
    		Button homeButton = new Button();
            homeButton.setPrefHeight(40);
            homeButton.setPrefWidth(20);
            homeButton.setGraphic(iconBack);
            homeButton.setStyle("-fx-background-color: linen;" + "-fx-background-radius: 5;");
           
            homeButton.setOnAction(e -> goBackToHomepage()); 
            
            HBox buttonBox = new HBox();
            buttonBox.setAlignment(Pos.TOP_LEFT);
            buttonBox.getChildren().add(homeButton);
            
            // Create the content label
            Label contentLabel = new Label("Welcome to Daily Sudoku, a delightful journey where each day brings a unique theme to your puzzle-solving adventure!\n\n"
                    + "Prepare yourself for a daily dose of brain-teasing fun, where the rules remain the same, but the elements within the puzzle change.\n\n"
                    + "Every day, you'll encounter a fresh theme, ranging from numbers to shapes, colors, or even your favorite holiday or show. Your task remains constant: Fill the 9x9 grid with the given elements, ensuring that each row, column, and 3x3 box contains all the elements without repetition.\n\n"
                    + "Embrace the variety as you adapt to each theme. Some days, you'll encounter traditional numbers, testing your logical reasoning and number placement skills. Other days may surprise you with shapes, challenging you to arrange them in a harmonious pattern. Then again on others, you might embark on a visual journey, where pictures take the stage.\n\n"
                    + "Enjoy the thrill of Daily Sudoku and let the daily surprises keep you entertained!");
            contentLabel.setFont(Font.font("Garamond", 14));
            contentLabel.setWrapText(true);
            
            // Add the title and content labels to the VBox
            vbox.getChildren().addAll(buttonBox, titleLabel, contentLabel);

            // Create the scene
            rulesScene = new Scene(vbox, 450, 500);

            // Set the scene on the stage and show the stage
            primaryStage.setScene(rulesScene);
            primaryStage.setTitle("Daily Sudoku Rules"); 
        });
	}
	
	private void loadData(LocalDate date) {
    	// Format the date to match the filename pattern
		// formattedDate should be in format yyyymmdd and of type int
        String formattedDate = date.toString(); //"2023-06-09"
        String filename = "src/puzzle_" + formattedDate + ".txt";
        
        /*
        
        
         * Get sudoku from server and solve it in app to be able to check whether an entered is correct or not 
        TestNeo4j greeter = new TestNeo4j("neo4j+s://3de5149f.databases.neo4j.io", "neo4j", "RtLJnX7gMMS4slo1pqPGPpgTxqYJl2O5qvZ9wjaH1M0");
		this.grid = greeter.getDailySudoku(date);
		Generator GeneratorInstance = new Generator();
		this.gridSolution = GeneratorInstance.solveSudoku(this.grid);
		
		 * Get category via modulo of time passed since beginning of year
		LocalDate beginning = 2023-01-01;
        long daysInMs = date.getTime() - beginnig.getTime();
        int daysIn = TimeUnit.DAYS.convert(daysInMs, TimeUnit.MILLISECONDS);
        this.category = daysIn % amountCategories
         */
        
        try {
            // Read the JSON file
        	Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(new FileReader(filename)).getAsJsonObject();

            // Retrieve grid
            this.grid = gson.fromJson(jsonObject.get("grid"), int[][].class);
            
            // Retrieve category
            this.category = jsonObject.get("category").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    // set a Number with Button
    private void setNumber(int number) {
        if (selectedTextField != null && selectedTextField.isEditable()) {
            int row = GridPane.getRowIndex(selectedTextField);
            int column = GridPane.getColumnIndex(selectedTextField);

            selectedTextField.setText(String.valueOf(number));
            
            // Update the Sudoku grid with the entered number
            grid[row][column] = number;
            
            /*
             * check if entered number is equal to number in solution (isValidSudoku and isValidSet not necessary)
            if (number = this.gridSolution[row][column]) {
               selectedTextField.setStyle(""); // Remove any previous red border
                
                // Check if sudoku is completed
                if (isSudokuCompleted()) {
                	//Calculate Duration
                    int minutes = elapsedTimeSeconds / 60;
                    int seconds = elapsedTimeSeconds % 60;
                    String formattedTime = String.format("%02d:%02d", minutes, seconds);
                    
                    // Stop the timer
                    timer.stop();
                	
                	Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Congratulations!");
                    alert.setHeaderText("Sudoku Completed");
                    String message = "Congratulations! You have successfully completed the Sudoku in " + formattedTime + ".";
                    alert.setContentText(message);
                    
                    // Design message
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.setStyle("-fx-background-color: snow;");
                    dialogPane.setGraphic(new ImageView("finish.png"));
                    
                    alert.showAndWait();
                }
                
            } else {
                grid[row][column] = 0; // Reset the grid value if the number is not valid
                selectedTextField.setStyle("-fx-border-color: red; -fx-border-width: 0.5px; -fx-text-fill: red");
            }
             */
            
            // Check if the number is valid in terms of Sudoku rules
            if (isValidSudoku()) {
                selectedTextField.setStyle(""); // Remove any previous red border
                
                // Check if sudoku is completed
                if (isSudokuCompleted()) {
                	//Calculate Duration
                    int minutes = elapsedTimeSeconds / 60;
                    int seconds = elapsedTimeSeconds % 60;
                    String formattedTime = String.format("%02d:%02d", minutes, seconds);
                    
                    // Stop the timer
                    timer.stop();
                	
                	Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Congratulations!");
                    alert.setHeaderText("Sudoku Completed");
                    String message = "Congratulations! You have successfully completed the Sudoku in " + formattedTime + ".";
                    alert.setContentText(message);
                    
                    // Design message
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.setStyle("-fx-background-color: snow;");
                    dialogPane.setGraphic(new ImageView("finish.png"));
                    
                    alert.showAndWait();
                }
                
            } else {
                grid[row][column] = 0; // Reset the grid value if the number is not valid
                selectedTextField.setStyle("-fx-border-color: red; -fx-border-width: 0.5px; -fx-text-fill: red");
            }
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Invalid Move");
            alert.setHeaderText("Select an empty cell");
            alert.setContentText("Please select an empty cell to fill.");
            alert.showAndWait();
        }
    }
    
    private void setPicture(int number) {
        if (selectedTextField != null && selectedTextField.isEditable()) {
            int row = GridPane.getRowIndex(selectedTextField);
            int column = GridPane.getColumnIndex(selectedTextField);
           
            // Update the Sudoku grid with the entered number
            grid[row][column] = number;
            
            /*
             * check if entered number is equal to number in solution (isValidSudoku and isValidSet not necessary)
             * if (number == this.gridSolution[row][column]) {
             * 	 selectedTextField.setStyle("-fx-background-image: url('image" + number +".png'); " +
                        "-fx-background-repeat: no-repeat; " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;" +
                        "-fx-border-color: linen;" +
                        "-fx-border-width: 0.5px;"); // Remove any previous red border
                
                  // Check if sudoku is completed
                  if (isSudokuCompleted()) {
                	
                	sudokuCompleted = true;
                	
                	//Calculate Duration
                    int minutes = elapsedTimeSeconds / 60;
                    int seconds = elapsedTimeSeconds % 60;
                    String formattedTime = String.format("%02d:%02d", minutes, seconds);
                    
                    // Stop the timer
                    timer.stop();
                	
                	Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Congratulations!");
                    alert.setHeaderText("Sudoku Completed");
                    String message = "Congratulations! You have successfully completed the Sudoku in " + formattedTime + ".";
                    alert.setContentText(message);
                    
                    // Design message TBD
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.setStyle("-fx-background-color: snow;");
                    dialogPane.setGraphic(new ImageView("finish.png"));
                    
                    alert.showAndWait();   
             *    }
             *  }
             *  else {
             *    grid[row][column] = 0; // Reset the grid value if the number is not valid
                  selectedTextField.setStyle("-fx-background-image: url('image" + number +".png'); " +
                        "-fx-background-repeat: no-repeat; " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;" +
                        "-fx-border-color: red;" +
                        "-fx-border-width: 0.5px;");
                }
             */

            // Check if the number is valid in terms of Sudoku rules
            if (isValidSudoku()) {
                selectedTextField.setStyle("-fx-background-image: url('image" + number +".png'); " +
                        "-fx-background-repeat: no-repeat; " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;" +
                        "-fx-border-color: linen;" +
                        "-fx-border-width: 0.5px;"); // Remove any previous red border
                
                // Check if sudoku is completed
                if (isSudokuCompleted()) {
                	
                	sudokuCompleted = true;
                	
                	//Calculate Duration
                    int minutes = elapsedTimeSeconds / 60;
                    int seconds = elapsedTimeSeconds % 60;
                    String formattedTime = String.format("%02d:%02d", minutes, seconds);
                    
                    // Stop the timer
                    timer.stop();
                	
                	Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Congratulations!");
                    alert.setHeaderText("Sudoku Completed");
                    String message = "Congratulations! You have successfully completed the Sudoku in " + formattedTime + ".";
                    alert.setContentText(message);
                    
                    // Design message TBD
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.setStyle("-fx-background-color: snow;");
                    dialogPane.setGraphic(new ImageView("finish.png"));
                    
                    alert.showAndWait();   
                }
            } else {
                grid[row][column] = 0; // Reset the grid value if the number is not valid
                selectedTextField.setStyle("-fx-background-image: url('image" + number +".png'); " +
                        "-fx-background-repeat: no-repeat; " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;" +
                        "-fx-border-color: red;" +
                        "-fx-border-width: 0.5px;");
            } 
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Invalid Move");
            alert.setHeaderText("Select an empty cell");
            alert.setContentText("Please select an empty cell to fill.");
            alert.showAndWait();
        }
        
    }
	
 // Check if Sudoku is valid
    private boolean isValidSudoku() {
        // Check each row, column, and 3x3 square for duplicate numbers
        for (int i = 0; i < 9; i++) {
            if (!isValidSet(grid[i])) {
                return false;
            }

            int[] column = new int[9];
            for (int j = 0; j < 9; j++) {
                column[j] = grid[j][i];
            }

            if (!isValidSet(column)) {
                return false;
            }
            
            int[] square = new int[9];
            int rowOffset = (i / 3) * 3;
            int colOffset = (i % 3) * 3;
            int index = 0;
            for (int j = rowOffset; j < rowOffset + 3; j++) {
                for (int k = colOffset; k < colOffset + 3; k++) {
                    square[index++] = grid[j][k];
                }
            }
            if (!isValidSet(square)) {
                return false;
            }
        }
        return true; // Sudoku is valid
    }
    
    
    private boolean isValidSet(int[] set) {
        int[] count = new int[10];
        for (int i : set) {
            if (i != 0 && ++count[i] > 1) {
                return false; // Duplicate number found
            }
        }
        return true; // Set is valid
    }
    
    // Check if Sudoku is completed
    private boolean isSudokuCompleted() {
        // Check if any cell in the grid is still empty (contains 0)
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0) {
                    return false;
                }
            }
        }
        return true; // Sudoku is completed
    }
    
    // Date
    private void updateDateLabel() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM, yyyy");
        String formattedDate = currentDate.format(formatter);
        dateLabel.setText(formattedDate);
    }

    // Timer
    private void updateTimerLabel() {
        int minutes = elapsedTimeSeconds / 60;
        int seconds = elapsedTimeSeconds % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(formattedTime);
    }
	 
	 private void goBackToHomepage() {
		 primaryStage.setScene(homeScene);
		 primaryStage.setTitle("Daily Sudoku");
	 }
	
}



