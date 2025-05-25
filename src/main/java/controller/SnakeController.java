package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class SnakeController implements Initializable {
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGTH = 500;
    private static final int PART_SIZE = 50;
    private static final int MOVE_STEP = 50;
    private List<Rectangle> snake = new ArrayList<>();
    private Rectangle head;
    private Rectangle food;
    private int dx = MOVE_STEP; // Toc do di chuyen ngang
    private int dy = 0; // Toc do di chuyen doc
    private boolean running = true;

    private Timeline timeline;
    @FXML
    private AnchorPane rootPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGTH);

        startGame();

    }

    public void startGame(){
        rootPane.getChildren().clear();
        snake.clear();
        running = true;
        dx = PART_SIZE;
        dy = 0;


        head = new Rectangle(PART_SIZE, PART_SIZE, Color.GREEN);
        head.setX(((WINDOW_WIDTH / PART_SIZE) / 2 - 1) * PART_SIZE);
        head.setY(((WINDOW_HEIGTH / PART_SIZE) / 2 - 1) * PART_SIZE);
        rootPane.getChildren().add(head);
        snake.add(head);
        generateFood();

        timeline = new Timeline(new KeyFrame(Duration.millis(300), e -> gameLoop()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        rootPane.requestFocus();
        rootPane.setOnKeyPressed(this::setDirection);
    }

    public void endGame(){
        running = false;
        timeline.stop();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("GAME OVER");
            alert.setHeaderText("GAME OVER");
            alert.setContentText("Do you want to play again or exit?");

            ButtonType playAgainButton = new ButtonType("Play Again");
            ButtonType exitGame = new ButtonType("Exit Game");
            alert.getButtonTypes().setAll(playAgainButton,exitGame);

            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == playAgainButton){
                startGame();
            }else{
                System.exit(0);
            }});

    }

    private void gameLoop(){
        if(!running) return;

        moveSnake();

        checkCollision();
    }

    private void checkCollision(){
        //va vao tuong
        if(head.getX() > (WINDOW_WIDTH - PART_SIZE) || head.getX() < 0 || head.getY() > (WINDOW_HEIGTH - PART_SIZE) || head.getY() < 0){
            endGame();
        }

        //va vao than ran
        if(snake.size() > 2){
            for(int i = 1; i < snake.size(); i++){
                if(head.getBoundsInParent().intersects(snake.get(i).getBoundsInParent())) {
                    if(head.getY() == snake.get(i).getY()) {
                        if ((head.getX() + PART_SIZE) > snake.get(i).getX() && head.getX() < (snake.get(i).getX() + PART_SIZE)) {
                            endGame();
                            break;
                        }
                    }

                    if(head.getX() == snake.get(i).getX()) {
                        if ((head.getY() + PART_SIZE) > snake.get(i).getY() && head.getY() < (snake.get(i).getY() + PART_SIZE)) {
                            endGame();
                            break;
                        }
                    }
                }
            }
        }
    }

    private void setDirection(KeyEvent e) {
        switch (e.getCode()) {
            case UP:
                dy = -MOVE_STEP;
                dx = 0;
                break;
            case DOWN:
                dy = MOVE_STEP;
                dx = 0;
                break;
            case LEFT:
                dx = -MOVE_STEP;
                dy = 0;
                break;
            case RIGHT:
                dx = MOVE_STEP;
                dy = 0;
                break;
            default:
                break;
        }
    }

    private void moveSnake() {
        //move body snake
        for (int i = snake.size() - 1; i > 0; i--) {
            Rectangle prev = snake.get(i - 1);
            Rectangle curr = snake.get(i);
            curr.setX(prev.getX());
            curr.setY(prev.getY());
        }

        //move head snake
        head.setX(head.getX()+dx);
        head.setY(head.getY()+dy);
        //Check if snake eat food
        if (head.getBoundsInParent().intersects(food.getBoundsInParent())) {
            growSnake();
            generateFood();
        }
    }

    private void growSnake() {
        Rectangle newPart = new Rectangle(PART_SIZE, PART_SIZE, Color.BLUE);
        newPart.setX(snake.get(snake.size()-1).getX());
        newPart.setY(snake.get(snake.size()-1).getY());
        snake.add(newPart);
        rootPane.getChildren().add(newPart);
    }

    private void generateFood() {
        if (food != null) {
            rootPane.getChildren().remove(food);
        }

        food = new Rectangle(PART_SIZE, PART_SIZE, Color.RED);
        Random ran = new Random();
        boolean isValidPosition;
        //Check food position
        do {
            isValidPosition = true;
            food.setX(ran.nextInt((WINDOW_WIDTH / PART_SIZE) - 1) * PART_SIZE);
            food.setY(ran.nextInt((WINDOW_HEIGTH / PART_SIZE) - 1) * PART_SIZE);
            for (Rectangle part : snake) {
                if (part.getBoundsInParent().intersects(food.getBoundsInParent())) {
                    isValidPosition = false;
                    break;
                }
            }
        } while (!isValidPosition);

        rootPane.getChildren().add(food);
    }
}

