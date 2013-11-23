package com.example.tao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class register extends Activity {
	private static final String TAG = register.class.getSimpleName();

	Button buttonback;
	Button confirmbutton;
	String name;
	String password;
	String email;
	String tel;
	private static final String HOST = "10.0.0.9";
	private static final int PORT = 4253;
	private DatagramSocket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		ImageButton buttonback = (ImageButton) findViewById(R.id.back);
		Button confirmbutton = (Button) findViewById(R.id.confirmbutton);

		final EditText et2 = (EditText) findViewById(R.id.email);
		final EditText et3 = (EditText) findViewById(R.id.username_in);
		final EditText et4 = (EditText) findViewById(R.id.password_in);
		final EditText et5 = (EditText) findViewById(R.id.tel);

		// @Override
		confirmbutton.setOnClickListener(new OnClickListener() {
			// TODO Auto-generated method stub
			@Override
			public void onClick(View v) {
				email = et2.getText().toString();
				name = et3.getText().toString();
				password = et4.getText().toString();
				tel = et5.getText().toString();

				sendMesssage();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {
			if (null != socket) {
				socket.close();
				socket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMesssage() {
		ConnectTask task = new ConnectTask();
		task.start();
	}

	private void receiveMessage() {
		Receiver task2 = new Receiver();
		task2.start();
	}

	// TODO Auto-generated method stub 发送的线程
	class ConnectTask extends Thread {
		@Override
		public void run() {
			super.run();
			connectServerWithUDPSocket();
			Looper.prepare();

			receiveMessage();
		}

		private void connectServerWithUDPSocket() {
			// TODO Auto-generated method stub
			try {

				// 创建DatagramSocket对象并指定一个端口号，注意，如果客户端需要接收服务器的返回数据,
				// 还需要使用这个端口号来receive，所以一定要记住

				if (null == socket) {
					socket = new DatagramSocket(PORT);
					receiveMessage();
				}
				// 使用InetAddress(Inet4Address).getByName把IP地址转换为网络地址
				// InetAddress serverAddress =
				// InetAddress.getByName("192.168.1.106");
				Inet4Address serverAddress = (Inet4Address) Inet4Address
						.getByName(HOST);

				String str = name + "," + password + "," + email + "," + tel;// 设置要发送的报文
				byte data[] = str.getBytes();// 把字符串str字符串转换为字节数组

				// 创建一个DatagramPacket对象，用于发送数据。
				// 参数一：要发送的数据 参数二：数据的长度 参数三：服务端的网络地址 参数四：服务器端端口号
				DatagramPacket packet = new DatagramPacket(data, data.length,
						serverAddress, PORT);
				socket.send(packet);// 把数据发送到服务端。

			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// 接受的线程
	class Receiver extends Thread {
		@Override
		public void run() {
			super.run();
			ReceiveServerSocketData();
			Looper.prepare();

		}

		private void ReceiveServerSocketData() {
			// DatagramSocket socket;

			while (true) {

				try {
					// 实例化的端口号要和发送时的socket一致，否则收不到data
					// socket = new DatagramSocket(55555);
					byte data[] = new byte[4 * 1024];
					// 参数一:要接受的data 参数二：data的长度

					DatagramPacket packet = new DatagramPacket(data,
							data.length);
					socket.receive(packet);

					// 把接收到的data转换为String字符串
					String result = new String(packet.getData());

					// String result = new String(packet.getData(),
					// packet.getOffset(), packet.getLength());
					Log.i(TAG, result);
					// socket.close();

					if (result.equals("ok") == true) {
						Intent intent = new Intent(register.this, login.class);
						startActivity(intent);
						break;
					} else {

					}

				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
