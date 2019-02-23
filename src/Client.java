import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Client implements Runnable,ActionListener{
	 JFrame clientFrame;
	 JTextArea receive;
	 JTextArea send;
	 JScrollPane receScroll;
	 JScrollPane sendScroll;
	 JButton sendB;
	 Socket socket;          //客户端套接字
	 Thread thread;
	 DataInputStream din;
	 DataOutputStream dout;
	 StringBuffer randName;     //随机生成的用户ID
	Client() throws UnknownHostException, IOException{       //窗口及客户端构建
		receive=new JTextArea(200, 100);
		send=new JTextArea(200,100);
		receScroll=new JScrollPane(receive);
		sendScroll=new JScrollPane(send);
		sendB=new JButton("Send");
		JLabel rece=new JLabel("Receive :");
		JLabel sendL=new JLabel("Send :");
		sendB.addActionListener(this);
		randName = new StringBuffer();
		char randUpper=(char)(65+((int)(Math.random()*100))%24);  //随机生成大写字母
		randName.append(randUpper);              //名字首个字母大写
		for(int j=0;j<4;j++) {          //随机生成ID
			char randLower=(char)(97+((int)(Math.random()*100))%24);
			randName.append(randLower);
		}
		clientFrame = new JFrame("ID: "+randName);
		clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientFrame.addWindowListener(new WindowAdapter() {   //关闭窗口后关闭客户端连接
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				try {					
					socket.close();				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JPanel panel=new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(rece);
		panel.add(receScroll);
		panel.add(sendL);
		panel.add(sendScroll);
		panel.add(sendB);
		clientFrame.add(panel);
		clientFrame.setSize(400, 400);
		clientFrame.setVisible(true);
		thread=new Thread(this);    
		String hostName;      //主机地址
		hostName="localhost";
		socket=new Socket(hostName, 9000);   //创建客户端
		thread.start();    //开启接收消息线程
		
	}
	public static void main(String []args) throws UnknownHostException, IOException {
		new Client();
	}
	@Override
	public void actionPerformed(ActionEvent e) {   //点击send发送
		// TODO Auto-generated method stub
		DataOutputStream dout=null;
		String sendMsg=randName+": " +send.getText();
		send.setText("");
		try {
			dout = new DataOutputStream(socket.getOutputStream());
			dout.writeUTF(sendMsg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
			try {
				din=new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(true)    //不断接收状态
				try {
					receive.setText(din.readUTF());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
	}
}