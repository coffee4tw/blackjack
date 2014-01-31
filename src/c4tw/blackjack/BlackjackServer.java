package c4tw.blackjack;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class BlackjackServer implements HttpHandler {

	public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/blackjack", new BlackjackServer());
//        server.setExecutor(null); // creates a default executor
        server.start();
    }
    
    private BlackjackGame game;
    
    public BlackjackServer() {
    	this.game = new BlackjackGame();
    }

	@Override
	public void handle(HttpExchange arg0) throws IOException {
		
		boolean understood = false;
		String response = "";
		
		// parse the request either using the URI or using HTTP arguments
		String path = arg0.getRequestURI().getPath();
		if (path.length() > "/blackjack".length())
			path = path.substring("/blackjack".length());
		String query = arg0.getRequestURI().getQuery();
		System.out.println(path);
		System.out.println(query);
		
		
		// now map new, hit and stand requests
		if ("/hit".equals(path) || "hit".equals(query)) {
			game.hitPlayer();
			understood = true;
		} else if ("/stand".equals(path) || "stand".equals(query)) {
			game.standPlayer();
			understood = true;
		} else if ("/new".equals(path) || "new".equals(query)) {
			game.newGame();
			understood = true;
		} else if ("/blackjack".equals(path) && query == null) {
			understood = true;
		} else {
			understood = false;
		}
		
		if (understood) {
			response = game.getStatus();
			arg0.sendResponseHeaders(200, response.getBytes().length);
		} else {
			response = "Could not handle request. Please pick one of these options: new, hit, stand, or nothing to get the status of the game.";
			arg0.sendResponseHeaders(404, response.getBytes().length);
		}
		
        OutputStream os = arg0.getResponseBody();
        os.write(response.getBytes());
        os.close();
        System.out.println(response);
	}
}
