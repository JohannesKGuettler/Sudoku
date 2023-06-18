package game;

//JavaFX Animation
import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

//JavaFX Application
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

//JavaFX Charts
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

//JavaFX Controls
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

//JavaFX Image
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

//JavaFX Layout
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

//JavaFX Paint
import javafx.scene.paint.Color;

//JavaFX Shapes
import javafx.scene.shape.Rectangle;

//JavaFX Text
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//JavaFX Stage
import javafx.stage.Stage;

//Java Time
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//Java IO
import java.io.FileReader;

//Gson Library
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//Java Reflection
import java.lang.reflect.Type;
import java.util.Arrays;
//Java Utilities
import java.util.Map;

public class Startpage extends Application {

	// Logo Animation
	private static final String WORD = "DAILY SUDOKU";
	private static final Duration DELAY = Duration.seconds(1);
	private static final Duration DURATION = Duration.seconds(0.3);
	private static final Duration TOTAL_DURATION = Duration.seconds(7);
	private Text text;
	private int currentIndex = 0;

	// Sudoku Grid
	private int[][] grid;
	private TextField selectedTextField;
	private int elapsedTimeSeconds = 0;
	
	// Game type
	private int type;
	private Label gameType;

	// Timer
	private Label timerLabel;
	private Timeline timer;
	private String time;

	// Date
	private Label dateLabel;
	private LocalDate currentDate = LocalDate.now();
	private String todayDate;

	// Game State
	private String category; // numbers or images
	private boolean sudokuCompleted = false;
	
	// Scene and Stage
	private Stage primaryStage;
	private Scene logoAnimation;
	private Scene homeScene;
	private Scene sudokuScene;
	private Scene scoresScene;
	private Scene rulesScene;

	// Player
	private String playerName;

	// Buttons
	private Button playButton;
	private Button scoresButton;
	private Button rulesButton;

	/**
	 * Main method to launch JavaFX application
	 * 
	 * @param args the command-line arguments
	 */
	 static void main(String[] args) {
        launch(args);
    }
	
	 /**
	  * The main JavaFX application consisting of 5 scenes: 
	  * 1. logo animation, 2. home scene, 3. daily sudoku scene, 4. daily scores scene, 5. rules scene.
	  *
	  * @param primaryStage the primary stage of the application window
	  * @throws Exception if an exception occurs during application startup
	  */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		
		//// LOGO ANIMATION
		
		//Root pane
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        
        // Text object
        text = new Text();
        text.setFont(Font.font("Garamond", 36));
        
        // Create a rectangle
        Rectangle rectangle = new Rectangle(50, 50, Color.TRANSPARENT);

        // Animation timeline
        TranslateTransition transition = new TranslateTransition(Duration.seconds(2), rectangle);
        transition.setFromY(-250); // Start from above the screen
        transition.setFromX(-120);
        transition.setToY(0); // Stop at the middle of the screen
        
        FillTransition fillTransition = new FillTransition(Duration.seconds(2), rectangle);
        fillTransition.setFromValue(Color.SNOW);
        fillTransition.setToValue(Color.LINEN);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(DELAY, e -> animate()));
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(4.3), e -> {
            transition.play();
            fillTransition.play();
        });
        timeline.getKeyFrames().add(keyFrame);
        
        // Add rectangle and text to root pane
        root.getChildren().add(rectangle);
        root.getChildren().add(text);
        
        // Create Scene
        logoAnimation = new Scene(root, 400, 400);

        // Start animation
        timeline.play();
        
        
        //// HOMEPAGE 
        
        // Create VBox 
        VBox rootHomepage = new VBox();
        rootHomepage.setAlignment(Pos.CENTER);
        rootHomepage.setSpacing(30);
        rootHomepage.setBackground(new Background(new BackgroundFill(Color.SNOW, null, null)));
        
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

        // "Play" button
        playButton = new Button("Play");
        playButton.setPrefWidth(100);
        playButton.setStyle(" -fx-background-color: linen;" + "-fx-background-radius: 5; -fx-font-family: garamond;");
        
        // "Scores" button
        scoresButton = new Button("Scores");
        scoresButton.setPrefWidth(100);
        scoresButton.setStyle(" -fx-background-color: linen;" + "-fx-background-radius: 5; -fx-font-family: garamond;");
        
        // "Rules" button
        rulesButton = new Button("Rules");
        rulesButton.setPrefWidth(100);
        rulesButton.setStyle(" -fx-background-color: linen;" + "-fx-background-radius: 5; -fx-font-family: garamond;");
        
        // Date
        dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 16px; -fx-font-family: garamond;");
        BorderPane.setMargin(dateLabel, new Insets(10, 10, 0, 0));
        updateDateLabel();
        
        // Game Type
        String gameMode;
        if (type == 0) {
        	gameMode = "Today's game mode is: classical";
        }
        else {
        	gameMode = "Today's game mode: diagonal";
        }
        gameType = new Label(gameMode);
        gameType.setStyle("-fx-font-size: 16;-fx-font-family: garamond;");
        BorderPane.setMargin(gameType, new Insets(5, 10, 0, 0));
        BorderPane.setAlignment(gameType, Pos.TOP_LEFT);
        
        // Add elements to VBox
        rootHomepage.getChildren().addAll(dateLabel, stackPane, playButton, scoresButton, rulesButton, gameType);
        
        // Create scene
        homeScene = new Scene(rootHomepage, 400, 450);
        
        // Set stage
        primaryStage.setScene(logoAnimation);
        primaryStage.setTitle("Daily Sudoku");
        primaryStage.show();
        
        // AnimationTimer checking animation progress
        AnimationTimer animationTimer = new AnimationTimer() {
        private long startTime = -1;

            @Override
            public void handle(long now) {
                // Check if animation has started
            	if (startTime == -1) {
                    startTime = now;
                }
                long elapsed = now - startTime;
                //Check if time has reached total duration
                if (elapsed >= TOTAL_DURATION.toMillis() * 1_000_000) {
                    // Animation is completed, switch to the second scene
                    primaryStage.setScene(homeScene);
                    primaryStage.setTitle("Daily Sudoku");
                    primaryStage.show();
                    stop(); // Stop the animation timer
                }
            }
        };
        // Start AnimationTimer
        animationTimer.start();
        
        
        //// DAILY SUDOKU GAME SCENE
        
        // Event 1: press play
        playButton.setOnAction(event -> {
            primaryStage.setTitle("Sudoku Game"); 
            
            // Check if sudoku has already been completed for the day
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
            	sudokuCompleted = false;
            
            // Get current date
            currentDate = LocalDate.now(); 
            
            // Load grid and category for current date
            loadData(currentDate);
            
            // Create Sudoku Grid (different version depending on category)
            
            // Gridpane for Textfields (i.e. add numbers to grid)
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
                          textField.setStyle("-fx-background-image: url('" + todayDate + "image" + grid[i][j] +".png'); " +
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
                	Image image = new Image(todayDate+"image"+i+".png");
            		ImageView imageView = new ImageView(image);
            		imageView.setPreserveRatio(false);
            		imageView.setFitWidth(40);
            		imageView.setFitHeight(40);
            		
                    Button button = new Button();
                    button.setGraphic(imageView);
                    button.setPrefWidth(40);
                    button.setPrefHeight(40);
                    button.setOnAction(e -> setNumber(INDEX));
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
       
            // Homepage Button
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
        
        //// DAILY SCORES SCENE
        
        // Event 2: press scores
        scoresButton.setOnAction(event -> {
        	// VBox Layout
        	VBox vbox = new VBox();
            vbox.setSpacing(20);
            vbox.setPadding(new Insets(50));
            vbox.setStyle("-fx-background-color: snow");
            vbox.setAlignment(Pos.CENTER);
            
            // Homepage button
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
            
            // Title label
            Label titleLabel = new Label("Scores");
            titleLabel.setFont(Font.font("Garamond", 24));
            
            sudokuCompleted = true;
            if (!sudokuCompleted) {
            	// If sudoku is not completed, show text message
            	Label messageLabel = new Label("Please first complete the Sudoku.\nThereafter, the daily scores can be viewed.");
                messageLabel.setFont(Font.font("Garamond", 16));
                
                vbox.getChildren().addAll(buttonBox, titleLabel, messageLabel);
            }
            else {
            	// Show daily scores
            	
            	//Top 10 table
                GridPane gridPane = new GridPane();
                gridPane.setPadding(new Insets(10));
                gridPane.setHgap(100);
                gridPane.setVgap(10);
                gridPane.setAlignment(Pos.CENTER);

                // Title
                Label title = new Label("Top 10 players");
                title.setStyle("-fx-font-family: Garamond; -fx-font-size: 27px;");
                
                // Table headers
                Label rankHeader = new Label("Rank");
                Label nameHeader = new Label("Name");
                Label timeHeader = new Label("Time");

                // Header design
                rankHeader.setStyle("-fx-font-family: Garamond; -fx-font-size: 22px;");
                nameHeader.setStyle("-fx-font-family: Garamond; -fx-font-size: 22px;");
                timeHeader.setStyle("-fx-font-family: Garamond; -fx-font-size: 22px;");

                // Add header to grid pane
                gridPane.addRow(0, rankHeader, nameHeader, timeHeader);

                // Retrieve top 10 (name, time) and min, max, average time from database
                Server greeter = new Server("neo4j+s://72b6cafc.databases.neo4j.io", "neo4j", "fwYW8GEj5aCLmT5PMisyOO-pMmsQckUyO3qWrluxDa8");
                String[][] result = greeter.getBestResults(todayDate);
                greeter.close();
                
                int minTime = Integer.valueOf(result[0][0]);
            	int maxTime = Integer.valueOf(result[0][1]);
            	String[] averageTimeSplit = result[0][2].split("\\.");
            	int averageTime = Integer.valueOf(averageTimeSplit[0]);
            	
                // Create table rows
            	for(int i = 1; i < 11; i++) {
            	// Transform data into label
            	Label rankValue = new Label(String.valueOf(i));
            	 
            	String name = result[i][1].substring(1, result[i][1].length()-1);
            	Label nameValue = new Label(name);
            	 
            	Label timeValue = new Label(String.valueOf(result[i][0]));
                    
            	//Design labels
            	rankValue.setStyle("-fx-font-family: Garamond; -fx-font-size: 16px;");
            	nameValue.setStyle("-fx-font-family: Garamond; -fx-font-size: 16px;");
                timeValue.setStyle("-fx-font-family: Garamond; -fx-font-size: 16px;");

            	//Add rows to gridpane
            	gridPane.addRow(i, rankValue, nameValue, timeValue);
            	}

                // Graph comparing your score, average, min and max scores
            	CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                
                // Remove ticks and legend
                xAxis.setTickMarkVisible(false);
                yAxis.setTickMarkVisible(false);
                barChart.setHorizontalGridLinesVisible(false);
                barChart.setVerticalGridLinesVisible(false);
                barChart.setLegendVisible(false);

                // Set background color
                barChart.lookup(".chart-plot-background").setStyle("-fx-background-color: snow;");

                // Set the font for axis labels
                Font axisLabelFont = Font.font("Garamond", 16);
                xAxis.setTickLabelFont(axisLabelFont);
                yAxis.setTickLabelFont(axisLabelFont);
                
                // Create barchart
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.getData().add(new XYChart.Data<>("Your time", elapsedTimeSeconds));
                series.getData().add(new XYChart.Data<>("Best time", minTime));
                series.getData().add(new XYChart.Data<>("Average time", maxTime));
                series.getData().add(new XYChart.Data<>("Worst time", averageTime));
                barChart.getData().add(series);
          
                // Design barchart
                Color barColor = Color.GHOSTWHITE;
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getXValue().equals("Your time")) {
                    	data.getNode().setStyle("-fx-bar-fill: " + toHexCode(Color.LINEN) + ";");
                    }
                    else {
                    	data.getNode().setStyle("-fx-bar-fill: " + toHexCode(barColor) + ";");
                    }
                }

                // Add title, top 10 gridpane, chart to VBox
                vbox.getChildren().addAll(buttonBox, titleLabel, title, gridPane, barChart);
            }
            
            // Create scene
            scoresScene = new Scene(vbox, 700, 700);

            // Set scene
            primaryStage.setScene(scoresScene);
            primaryStage.setTitle("Daily Sudoku Scores");
        });
        
        //// RULES SCENE
        
        // Event 3: press rules
        rulesButton.setOnAction( event -> {
        	// VBox layout
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new Insets(30));
            vbox.setStyle("-fx-background-color: snow");
            vbox.setAlignment(Pos.CENTER);

            // Title label
            Label titleLabel = new Label("Rules");
            titleLabel.setFont(Font.font("Garamond", 19));

            // Homepage button
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
            
            // Content label
            Label contentLabel = new Label("Welcome to Daily Sudoku, a delightful journey where each day brings a unique theme to your puzzle-solving adventure!\n\n"
                    + "Prepare yourself for a daily dose of brain-teasing fun, where the rules remain the same, but the elements within the puzzle change.\n\n"
                    + "Every day, you'll encounter a fresh theme, ranging from numbers to shapes, colors, or even your favorite holiday or show. Your task remains constant: Fill the 9x9 grid with the given elements, ensuring that each row, column, and 3x3 box contains all the elements without repetition. If the game mode is 'Diagonal' both diagonals are also restricted to containing each element only once.\n\n"
                    + "Embrace the variety as you adapt to each theme. Some days, you'll encounter traditional numbers, testing your logical reasoning and number placement skills. Other days may surprise you with shapes, challenging you to arrange them in a harmonious pattern. Then again on others, you might embark on a visual journey, where pictures take the stage.\n\n"
                    + "Enjoy the thrill of Daily Sudoku and let the daily surprises keep you entertained!");
            contentLabel.setFont(Font.font("Garamond", 14));
            contentLabel.setWrapText(true);
            
            // Add titel and content to VBox
            vbox.getChildren().addAll(buttonBox, titleLabel, contentLabel);

            // Create scene
            rulesScene = new Scene(vbox, 450, 500);

            // Set scene
            primaryStage.setScene(rulesScene);
            primaryStage.setTitle("Daily Sudoku Rules"); 
        });
	}
	
	/**
	 * Animates the text by making the letters "daily sudoku" appear one-by-one.
	 * If the animation is not completed, another keyframe is added.
	 */
	private void animate() {
        if (currentIndex < WORD.length()) {
            String nextLetter = String.valueOf(WORD.charAt(currentIndex));
            text.setText(text.getText() + nextLetter);
            currentIndex++;

            // Schedule the next keyframe
            Timeline timeline = new Timeline(new KeyFrame(DURATION));
            timeline.setOnFinished(e -> animate());
            timeline.play();
        }
    }
	
	/**
	 * Loads daily sudoku grid and category based on given date. 
	 * 
	 * @param date The date for which the sudoku grid and category should be loaded.
	 */
	private void loadData(LocalDate date) {
		// Format date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        this.todayDate = date.format(formatter);

		// Connect to server, download daily sudoku grid and determine type of sudoku (0: classic, 1: diagonal)
		Server greeter = new Server("neo4j+s://72b6cafc.databases.neo4j.io", "neo4j", "fwYW8GEj5aCLmT5PMisyOO-pMmsQckUyO3qWrluxDa8");
		grid = greeter.getDailySudoku(todayDate); // SUBSTITUTE THIS FOR todayDate
		type = greeter.getType();
		greeter.close();
		
        try {
        	// Read JSON file into fileReader
            FileReader fileReader = new FileReader("src/puzzle_category.txt");

            // Create Gson instance
            Gson gson = new Gson();
            
        	// Define type of map
            Type type = new TypeToken<Map<String, String>>(){}.getType();

            // Parse JSON file into map
            Map<String, String> data = gson.fromJson(fileReader, type);

            // Retrieve category
            this.category = data.get(todayDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	/**
	 * Sets the provided number in the selected text field and updates grid accordingly.
	 * In the case the sudoku is of the category images, each image is referenced by a number.
	 * 
	 * @param number Number to be set in selected text field
	 */
    private void setNumber(int number) {
    	// Check if the text field is editable
        if (selectedTextField != null && selectedTextField.isEditable()) {
            int row = GridPane.getRowIndex(selectedTextField);
            int column = GridPane.getColumnIndex(selectedTextField);

            // Update grid with entered number
            grid[row][column] = number;
            selectedTextField.setText(String.valueOf(number));
            
            // Check if number is valid in terms of sudoku rules
            if (isValidSudoku(row, column, number)) {
                if (category.equals("numbers")) {
                	selectedTextField.setStyle(""); // Remove any previous red border
                }
                else if(category.equals("images")) {
                	selectedTextField.setStyle("-fx-background-image: url('"+ todayDate +"image" + number +".png'); " +
                            "-fx-background-repeat: no-repeat; " +
                            "-fx-background-size: cover; " +
                            "-fx-background-position: center;" +
                            "-fx-border-color: linen;" +
                            "-fx-border-width: 0.5px;"); // Remove any previous red border
                }
            	
                // Check if sudoku is completed
                if (isSudokuCompleted()) {   
                	sudokuCompleted = true;
                	
                	//Calculate Duration
                    int minutes = elapsedTimeSeconds / 60;
                    int seconds = elapsedTimeSeconds % 60;
                    time = String.format("%02d:%02d", minutes, seconds);
                    
                    // Stop the timer
                    timer.stop();
                    
                    // Display congratualtions message
                    Alert congratulationsAlert = new Alert(Alert.AlertType.INFORMATION);
                    congratulationsAlert.setTitle("Congratulations!");
                    String message = "Congratulations! You have successfully completed the Sudoku in " + time + ".";
                    congratulationsAlert.setHeaderText(message);
                    
                    // Remove ok Button
                    congratulationsAlert.getButtonTypes().clear();
                    congratulationsAlert.getButtonTypes().add(ButtonType.CANCEL);
                    
                    // Design message
                    DialogPane dialogPane = congratulationsAlert.getDialogPane();
                    dialogPane.setStyle("-fx-background-color: snow;");
                    dialogPane.setGraphic(new ImageView("finish.png"));
                    
                    Label headerLabel = new Label(congratulationsAlert.getHeaderText());
                    headerLabel.setFont(Font.font("Garamond", FontWeight.BOLD, 16));

                    // Promt user for name
                    TextField nameField = new TextField();
                    nameField.setPromptText("Enter your name");

                    // Button for scores scene
                    Button toScoresPageButton = new Button("Go to daily scores");
                    toScoresPageButton.setOnAction(event -> { 
                    	playerName = nameField.getText();
                    	
                    	// Connect to server and save name, time, date
                    	Server greeter = new Server("neo4j+s://72b6cafc.databases.neo4j.io", "neo4j", "fwYW8GEj5aCLmT5PMisyOO-pMmsQckUyO3qWrluxDa8");
                    	greeter.createPlayResult(playerName, Integer.valueOf(elapsedTimeSeconds), todayDate);
                    	
                    	// Close alert
                    	congratulationsAlert.close();
                    	
                    	// Set up VBox layout
                    	VBox vbox = new VBox();
                        vbox.setSpacing(20);
                        vbox.setPadding(new Insets(50));
                        vbox.setStyle("-fx-background-color: snow");
                        vbox.setAlignment(Pos.CENTER);
                        
                        // Homepage button
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
                        
                        // Scene titel label
                        Label titleLabel = new Label("Scores");
                        titleLabel.setFont(Font.font("Garamond", 24));
                       
                        // Top 10 table
                        GridPane gridPane = new GridPane();
                        gridPane.setPadding(new Insets(10));
                        gridPane.setHgap(100);
                        gridPane.setVgap(10);
                        gridPane.setAlignment(Pos.CENTER);

                        // Top 10 table: Title 
                        Label title = new Label("Top 10 players");
                        title.setStyle("-fx-font-family: Garamond; -fx-font-size: 27px;");
                        
                        // Top 10 table: Table headers
                        Label rankHeader = new Label("Rank");
                        Label nameHeader = new Label("Name");
                        Label timeHeader = new Label("Time");

                        // Top 10 table: Design headers
                        rankHeader.setStyle("-fx-font-family: Garamond; -fx-font-size: 22px;");
                        nameHeader.setStyle("-fx-font-family: Garamond; -fx-font-size: 22px;");
                        timeHeader.setStyle("-fx-font-family: Garamond; -fx-font-size: 22px;");

                        // Top 10 table: Add header to grid pane
                        gridPane.addRow(0, rankHeader, nameHeader, timeHeader);
    
                        // Top 10 table: Retrieve top 10 (name, time) and min, max, average time from database
                        String[][] result = greeter.getBestResults(todayDate);
                        greeter.close();
                     
                    	int minTime = Integer.valueOf(result[0][0]);
                    	int maxTime = Integer.valueOf(result[0][1]);
                    	String[] averageTimeSplit = result[0][2].split("\\.");
                    	int averageTime = Integer.valueOf(averageTimeSplit[0]);
                    	
                        // Top 10 table: Create the table rows
                        for(int i = 0; i < 10; i++) {
                        	// Transform data into label
                        	Label rankValue = new Label(String.valueOf(i+1));
                        	
                        	String name = result[i][1].substring(1, result[i][1].length()-1);
                            Label nameValue = new Label(name);
                            Label timeValue = new Label(String.valueOf(result[i][0]));
                            
                            // Apply styles to values
                            rankValue.setStyle("-fx-font-family: Garamond; -fx-font-size: 16px;");
                            nameValue.setStyle("-fx-font-family: Garamond; -fx-font-size: 16px;");
                            timeValue.setStyle("-fx-font-family: Garamond; -fx-font-size: 16px;");

                            // Add rows to gridpane
                            gridPane.addRow(i, rankValue, nameValue, timeValue);
                        }
                   
                    	// Chart comparing your score, average, min and max scores
                    	CategoryAxis xAxis = new CategoryAxis();
                        NumberAxis yAxis = new NumberAxis();
                        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                        
                        // Chart: Remove ticks and legend
                        xAxis.setTickMarkVisible(false);
                        yAxis.setTickMarkVisible(false);
                        barChart.setHorizontalGridLinesVisible(false);
                        barChart.setVerticalGridLinesVisible(false);
                        barChart.setLegendVisible(false);

                        // Chart: Background color
                        barChart.lookup(".chart-plot-background").setStyle("-fx-background-color: snow;");

                        // Chart: Axis labels font
                        Font axisLabelFont = Font.font("Garamond", 16);
                        xAxis.setTickLabelFont(axisLabelFont);
                        yAxis.setTickLabelFont(axisLabelFont);
                        
                        // Chart: Create barchart
                        XYChart.Series<String, Number> series = new XYChart.Series<>();
                        series.getData().add(new XYChart.Data<>("Your time", elapsedTimeSeconds));
                        series.getData().add(new XYChart.Data<>("Best time", minTime));
                        series.getData().add(new XYChart.Data<>("Average time", maxTime));
                        series.getData().add(new XYChart.Data<>("Worst time", averageTime));
                        barChart.getData().add(series);
                        
                        // Chart: Design barchart
                        Color barColor = Color.GHOSTWHITE;
                        for (XYChart.Data<String, Number> data : series.getData()) {
                            if (data.getXValue().equals("Your time")) {
                            	data.getNode().setStyle("-fx-bar-fill: " + toHexCode(Color.LINEN) + ";");
                            }
                            else {
                            	data.getNode().setStyle("-fx-bar-fill: " + toHexCode(barColor) + ";");
                            }
                        }

                        // Add title, top 10 table, and chart to VBox
                        vbox.getChildren().addAll(buttonBox, titleLabel, title, gridPane, barChart);
                                        
                        // Create scene
                        scoresScene = new Scene(vbox, 700, 800);

                        // Set scene
                        primaryStage.setScene(scoresScene);
                        primaryStage.setTitle("Daily Sudoku Scores");
                    	});
                   
                    VBox dialogContent = new VBox(10);
                    dialogContent.getChildren().addAll(nameField, toScoresPageButton);
                    dialogContent.setAlignment(Pos.CENTER);
                    congratulationsAlert.getDialogPane().setContent(dialogContent);
                    congratulationsAlert.initOwner(primaryStage);
                    congratulationsAlert.showAndWait();
                }
                
            } else {
            	// Number violates sudoku rules
                if (category.equals("numbers")) {
                    grid[row][column] = 0; // Reset the grid value if the number is not valid
                	selectedTextField.setStyle("-fx-border-color: red; -fx-border-width: 0.5px; -fx-text-fill: red");
                }
                else if (category.equals("images")) {
                	grid[row][column] = 0; // Reset the grid value if the number is not valid
                	selectedTextField.setStyle("-fx-background-image: url('"+ todayDate +"image" + number +".png'); " +
                            "-fx-background-repeat: no-repeat; " +
                            "-fx-background-size: cover; " +
                            "-fx-background-position: center;" +
                            "-fx-border-color: red;" +
                            "-fx-border-width: 0.5px;");
                } 
            }
        } else {
        	// Not edible cell was clicked
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Invalid Move");
            alert.setHeaderText("Select an empty cell");
            alert.setContentText("Please select an empty cell to fill.");
            alert.showAndWait();
        }
    }
    
    /**
     * Checks if the current sudoku grid is valid according to sudoku rules.
     * @param row index of row of entered number in grid
     * @param column index of column of entered number in grid
     * @param number entered number in grid
     * @return true If the grid is valid, false otherwise
     */
    private boolean isValidSudoku(int row, int column, int number) {
    	int [] rowColumn = new int[] {row, column};
    	// check if number is already in row
    	for(int i=0; i<9; i++) {
    		if (grid[row][i] == number & i != rowColumn[1]) {
    			return false;
   			}
    	}
    			
    	// check if number is already in column
    	for (int i=0; i<9; i++) {
   			if (grid[i][column] == number & i != rowColumn[0]) {
    			return false;
    		}
    	}
    			
   		// get box of checked position
    	int [] box = new int [] {Math.floorDiv(row, 3), Math.floorDiv(column, 3)};
    	int [] checkedPosition = new int [2];
   		// iterate over each element in box to see if number is already in box
    	for (int i=0; i<3; i++) {
    		for (int k=0; k<3; k++) {
    			checkedPosition[0] = box[0]*3 + i;
    			checkedPosition[1] = box[1]*3 + k;
    			if (grid[box[0]*3 + i][box[1]*3 + k] == number & !Arrays.equals(rowColumn, checkedPosition)) {
    				return false;
    			}
    		}
    	}
    	
    	// check diagonals if game type is diagonal
    	if (type == 1) {
    		if (row == 4 & column == 4) {
    			for (int i=0; i<9; i++) {
    				if ((grid[i][i] == number | grid[8-i][i] == number) & i != rowColumn[0]) {
    					return false;
    				}
    			}
    		}
    		else if (row == column) {
    			for (int i=0; i<9; i++) {
    				if (grid[i][i] == number & i != rowColumn[0]) {
    					return false;
    				}
    			}
    		}
    		else if (row + column == 8) {
    			for (int i=0; i<9; i++) {
    				checkedPosition[0] = 8 - i;
    				checkedPosition[1] = i;
    				if (grid[8-i][i] == number & !Arrays.equals(rowColumn, checkedPosition)) {
    					return false;
    				}
    			}
    		}
    	}
    			
    	// return true if number is allowed in cell
    	return true;
    }
    
    /**
     * Checks if the sudoku grid is completed, i.e., all cells are filled with numbers.
     *
     * @return true If the sudoku is completed, false if there is any empty cell
     */
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
    
    /**
     * Updates the date label with the current date in the specified format.
     */
    private void updateDateLabel() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM, yyyy");
        String todayDate = currentDate.format(formatter);
        dateLabel.setText(todayDate);
    }

    /**
     * Updates the timer label with the elapsed time in minutes and seconds.
     */
    private void updateTimerLabel() {
        int minutes = elapsedTimeSeconds / 60;
        int seconds = elapsedTimeSeconds % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(formattedTime);
    }
    
    /**
     * Navigates back to the homepage by setting the primary stage scene and title accordingly.
     */
	 private void goBackToHomepage() {
		 primaryStage.setScene(homeScene);
		 primaryStage.setTitle("Daily Sudoku");
	 }
    
    /**
     * Converts a JavaFX Color object to its hexadecimal code representation.
     *
     * @param color the Color object to convert
     * @return the hexadecimal code representation of the color
     */
    private String toHexCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
