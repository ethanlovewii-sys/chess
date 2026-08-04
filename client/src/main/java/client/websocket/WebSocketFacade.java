package client.websocket;


import io.javalin.router.Endpoint;
import org.eclipse.jetty.server.session.Session;

public class WebSocketFacade extends Endpoint {

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
