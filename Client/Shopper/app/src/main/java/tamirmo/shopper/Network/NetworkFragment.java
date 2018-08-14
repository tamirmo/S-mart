package tamirmo.shopper.Network;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;

public class NetworkFragment extends FragmentWithUpdates {

    // Class attributes
    private Socket serverConnection; // Outgoing requests connection
    private EventConnection eventConnection; // Incoming server events connection

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // Because it is a No-UI fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null; // Because it is a No-UI fragment
    }

    // Starts to receive events from the server
    public void startReceivingEvents(int eventsPort) throws Exception{
        eventConnection = new EventConnection(eventsPort);
        eventConnection.start();
    }

    // Starts a connection with S-Mart server
    public void startServerConnection(String serverIP, int serverPort) throws IOException {
        //serverConnection = new Socket(serverIP, serverPort);
        serverConnection = new Socket();
        serverConnection.connect(new InetSocketAddress(serverIP,serverPort),3000);
    }

    // Sends a request to the server, and receives its response
    public String sendRequest(String request) throws Exception {
        String response;

        // Sends a request to a server
        sendMessage(request, serverConnection);

        // Reads a response from a server
        response = receiveMessage(serverConnection);

        return response;
    }

    // Sends a message to the server
    private void sendMessage(String message, Socket connection) throws IOException {
        OutputStream output;
        PrintWriter writer;

        output = connection.getOutputStream();
        writer = new PrintWriter(output, true);
        writer.println(message);
    }

    // Receives message from the server
    private String receiveMessage(Socket connection) throws IOException {
        InputStream input;
        BufferedReader reader;
        StringBuilder response;
        String line;

        // Reads a response from a server
        input = connection.getInputStream();
        reader = new BufferedReader(new InputStreamReader(input));
        response = new StringBuilder();
        line = reader.readLine();
        response.append(line);

        return response.toString();
    }

    @Override
    public void updateFragment() {
        // It doesn't need to do anything
    }

    // Stops all connections
    public void exit(){
        try {
            eventConnection.interrupt();
            serverConnection.close();
        }catch(Exception e){
            // Nothing to do
        }
    }

    private class EventConnection extends Thread {
        // Class attributes
        private ServerSocket eventConnection;
        private Socket incomingEvent;

        // Class Builder
        public EventConnection(int eventPort) throws Exception{
            eventConnection = new ServerSocket(eventPort);
        }

        // Starts the thread
        public void run(){
            while(true){
                try {
                    incomingEvent = eventConnection.accept();
                    processEvent();
                }catch(InterruptedException e) {
                    break;
                }catch (Exception e){
                    // Try again
                }
            }
        }

        // Receives an event request from the server
        public void processEvent() throws Exception{
            String event;

            // Reads an event request from a server
            event = receiveMessage(incomingEvent);

            // Calls main activity to process it
            ((MainActivity)getActivity()).processEvent(event);
        }
    }
}
