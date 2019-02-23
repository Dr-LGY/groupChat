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
	public static final int port=9000;   //�˿ں�
	static JFrame serveFrame;
	static JTextArea receive=new JTextArea(200, 100);
	static JScrollPane receScroll=new JScrollPane(receive);
	static ServerSocket serve;    //�������׽���
	Socket client;			//��ȡ�Ŀͻ���
	Server(){       //���ڼ�����˹���
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
				client=serve.accept();    //��ȡ�ͻ���
				serverThread.sockets.add(client);   //���ͻ��˼���List���Ա��յ���Ϣʱ��ʱ��ÿ�������͵�ǰ��Ϣ
				new serverThread(receive, client).start();	//����һ��ר����ͻ��˽������߳�	
			}
			catch(BindException be) {}
			catch(IOException ioe) {}
		}
	
	}
	public static void main(String args[]) throws IOException {
		new Server();	
	}
}
class serverThread extends Thread{   //������ͻ��˽������߳�
	JTextArea receive;  //�ͻ��˽��տ�
	DataInputStream din;
	DataOutputStream dout;
	Socket socket;   //��ǰ�ͻ���
	static ArrayList<Socket> sockets=new ArrayList<Socket>();  //��ǰ�����ӵ����пͻ���
	public serverThread(JTextArea receive, Socket socket) {
		super();
		this.receive = receive;
		this.socket = socket;
	}
	public void run() {    //���������Ϣ�뷢��
		try {
			din=new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(!socket.isClosed()) {    //���ͻ���δ�Ͽ�ʱ���Ͻ�����Ϣ
			String serveRead = null;
			try {
					serveRead = din.readUTF();
			} 
			catch(SocketException se){   //���ͻ����ѶϿ�ʱֹͣ�����ר�ŷ���
				Thread.currentThread().stop();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			receive.append(serveRead+'\n');  //��ʾ���յ���Ϣ
			
			dout = null;
			for(Socket s:sockets){      //�������ӵ����пͻ��˷�����Ϣ
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