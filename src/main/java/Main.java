import controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;
import model.Model;
import view.UI;


public class Main extends Application {
    private static UI ui;
    public static void main(String[] args) throws Exception {
        Model model = new Model();
        Controller controller = new Controller(model);
        ui = new UI(controller);
        model.addObserver(ui);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ui.start(primaryStage);
    }
}