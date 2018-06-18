package tcp;

public interface ITcpServerMessageReceived
{
	void messageReceived(String messageReceived,
                         TcpServerConnectionHandler clientHandler);
}
