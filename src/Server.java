import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
public class Server {
	public static final int port=9000;   //端口号
	static JFrame serveFrame;
	static JTextArea receive=new JTextArea(200, 100);
	static JScrollPane receScroll=new JScrollPane(receive);
	static ServerSocket serve;    //服务器套接字
	Socket client;			//获取的客户端
	Server(){       //窗口及服务端构建
		serveFrame = new JFrame("Serve Window");
		serveFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(receScroll);
		serveFrame.add(panel);
		serveFrame.setSize(400, 400);
		serveFrame.setVisible(true);
		try {
			serve=new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true) {
			try {
				client=serve.accept();    //获取客户端
				serverThread.sockets.add(client);   //将客户端加入List中以便收到消息时及时对每个都发送当前消息
				new serverThread(receive, client).start();	//启动一个专门与客户端交互的线程	
			}
			catch(BindException be) {}
			catch(IOException ioe) {}
		}
	
	}
	public static void main(String args[]) throws IOException {
		new Server();	
	}
}
class serverThread extends Thread{   //负责与客户端交互的线程
	JTextArea receive;  //客户端接收框
	DataInputStream din;
	DataOutputStream dout;
	Socket socket;   //当前客户端
	static ArrayList<Socket> sockets=new ArrayList<Socket>();  //当前已连接的所有客户端
	public serverThread(JTextArea receive, Socket socket) {
		super();
		this.receive = receive;
		this.socket = socket;
	}
	public void run() {    //负责接收消息与发送
		try {
			din=new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(!socket.isClosed()) {    //当客户端未断开时不断接收消息
			String serveRead = null;
			try {
					serveRead = din.readUTF();
			} 
			catch(SocketException se){   //当客户端已断开时停止对其的专门服务
				Thread.currentThread().stop();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			receive.append(serveRead+'\n');  //显示接收的消息
			
			dout = null;
			for(Socket s:sockets){      //对已连接的所有客户端发送消息
				if(!s.isClosed()){
					try {
						dout = new DataOutputStream(s.getOutputStream());
						dout.writeUTF(receive.getText());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
				}
			}
		}			
	
	}	
}