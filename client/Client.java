package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Client extends Application{
	public static Socket socket;
	public static BufferedReader reader;
	public static BufferedReader in;
	public static PrintWriter out;
	public static String  line;
	public static boolean matched;
	public static boolean full;
	public static String output = "";
	public static String username = "";
	
    public static void main(String[] args) throws IOException {
        socket = new Socket("127.0.0.1",5200);
        reader = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        matched = false;
        full = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                    	String temp = in.readLine();
                    	//System.out.println(temp);
                        if(temp.contains("match success")) {
                        	matched = true;
                        	Platform.runLater(new Runnable() {
                        	    @Override
                        	    public void run() {
                                	_main.getChildren().removeAll(_btMatch, _btName, _btExit);
                                	_textPane.setPrefSize(313, 388);
                                	_textPane.setLayoutX(0);
                                	_textPane.setLayoutY(0);
                                	_textArea.setPrefSize(280, 33);
                                	_textArea.setLayoutX(0);
                                	_textArea.setLayoutY(389);
                                	_textArea.setFont(new Font("System", 12));
                                	_btSend.setPrefSize(33, 33);
                            		_btSend.setLayoutX(280);
                            		_btSend.setLayoutY(389);
                            		_btSend.setText("->");
                            		_btSend.setFont(new Font("System", 12));
                            		_btSend.setOnMouseClicked(OnSendPressed);
                            		_main.getChildren().addAll(_textPane, _textArea, _btSend);
                        	    }
                        	});
                        }else if(temp.equals("full")) {
                        	if(!full) {
                        		full = true;
                            	Platform.runLater(new Runnable() {
                            	    @Override
                            	    public void run() {
                            			_full.setPrefSize(128, 38);
                            			_full.setLayoutX(92);
                            			_full.setLayoutY(226);
                            			_full.setFont(new Font("System", 18));
                            			_full.setTextFill(Color.web("#ff0000"));
                            			_full.toFront();
                            			_main.getChildren().addAll(_full);
                            	    }
                            	});
                        	}

                        }else if(temp.equals("Exit")) {
                        	out.close();
                			in.close();
                			socket.close();
                        	break;
                        }else if(!temp.equals(null)){
                        	//System.out.println(temp);
                        	output += temp;
                        	text = new Text(output);
                        	output+="\n";
                        	Platform.runLater(new Runnable() {
                        	    @Override
                        	    public void run() {
                        	    	_textPane.setContent(text);;
                        	    	_textPane.setFitToWidth(true);
                        	    }
                        	});
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        launch(args);
    }

	@Override
	public void start(Stage arg0) throws Exception {
		_main.setPrefSize(313, 422);
		_main.setLayoutX(0);
		_main.setLayoutY(0);
		_btMatch.setPrefSize(101, 55);
		_btMatch.setLayoutX(106);
		_btMatch.setLayoutY(171);
		_btMatch.setText("尋找配對");
		_btMatch.setFont(new Font("System", 18));
		_btMatch.setOnMouseClicked(OnMatchPressed);
		_btName.setPrefSize(101, 55);
		_btName.setLayoutX(106);
		_btName.setLayoutY(62);
		_btName.setText("創建用戶");
		_btName.setFont(new Font("System", 18));
		_btName.setOnMouseClicked(OnNamePressed);
		_btExit.setPrefSize(101, 55);
		_btExit.setLayoutX(106);
		_btExit.setLayoutY(278);
		_btExit.setText("關閉程式");
		_btExit.setFont(new Font("System", 18));
		_btExit.setOnMouseClicked(OnExitPressed);
		_main.getChildren().addAll(_btMatch, _btName, _btExit);
		mainscene = new Scene(_main);
		mainstage.setTitle("薛海聊天室");
		mainstage.setScene(mainscene);
		mainstage.setResizable(false);
		mainstage.show();
		
	}
	public static Stage mainstage = new Stage();
	public static Scene mainscene;
	public static Pane _main = new Pane();
	public static Pane _namingPane = new Pane();
	public static ScrollPane _textPane = new ScrollPane();
	public static Button _btMatch = new Button();
	public static Button _btName = new Button();
	public static Button _btExit = new Button();
	public static Button _btSend = new Button();
	public static Button _btConfirm = new Button("確認");
	public static TextArea _textArea = new TextArea();
	public static TextArea _namingArea = new TextArea();
	public static Text text = new Text();
	public static Label _namingLabel = new Label("請輸入使用者名稱：");
	public static Label _full = new Label("目前聊天室已滿");
	EventHandler<MouseEvent> OnMatchPressed = (e) ->{
		System.out.println("match");
		out.println(username+"|matching...");
		out.flush();
	};
	EventHandler<MouseEvent> OnConfirmPressed = (e) ->{ 
		username = _namingArea.getText();
		_namingPane.getChildren().removeAll(_namingLabel, _namingArea, _btConfirm);
		_main.getChildren().removeAll(_namingPane);
		_main.getChildren().addAll(_btName, _btExit, _btMatch);
	};
	EventHandler<MouseEvent> OnNamePressed = (e) ->{
		_namingPane.setPrefSize(200, 200);
		_namingPane.setLayoutX(57);
		_namingPane.setLayoutY(104);
		_namingPane.toFront();
		_namingLabel.setPrefSize(200, 35);
		_namingLabel.setLayoutX(0);
		_namingLabel.setLayoutY(45);
		_namingLabel.setFont(new Font("System", 18));
		_namingLabel.toFront();
		_namingArea.setPrefSize(155, 35);
		_namingArea.setLayoutX(0);
		_namingArea.setLayoutY(81);
		_namingArea.toFront();
		_btConfirm.setPrefSize(42, 38);
		_btConfirm.setLayoutX(156);
		_btConfirm.setLayoutY(81);
		_btConfirm.setFont(new Font("System", 12));
		_btConfirm.setOnMouseClicked(OnConfirmPressed);
		_btConfirm.toFront();
		_main.getChildren().addAll(_namingPane);
		_main.getChildren().removeAll(_btName, _btExit, _btMatch);
		_namingPane.getChildren().addAll(_namingLabel, _namingArea, _btConfirm);
	};
	EventHandler<MouseEvent> OnExitPressed = (e) ->{  
    	out.println("Exit");
    	out.flush();
    	mainstage.close();
        
	};
	static EventHandler<MouseEvent> OnSendPressed = (e) ->{
		if(!_textArea.getText().equals(null)) {
			out.println(_textArea.getText());
			out.flush();
			_textArea.setText("");
		}
	};
}
