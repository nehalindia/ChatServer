import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class MyServer extends JFrame implements ActionListener, Runnable
{
	JLabel l;
	JTextArea sendtxt,recievetxt;
	JButton send;

	BufferedImage readImage;

	MyServer(String s)
	{
		super(s);
		l=new JLabel("Enter the Message");
		sendtxt=new JTextArea();
		recievetxt=new JTextArea();
		send=new JButton("SEND");
		l.setBounds(10,10,120,20);
		sendtxt.setBounds(10,40,300,100);
		recievetxt.setBounds(150,200,300,100);
		send.setBounds(10,150,100,50);
		add(l); add(sendtxt); add(send); add(recievetxt);
		send.addActionListener(this);

		setLayout(null);
		setSize(500,400);
		setVisible(true);
	}

	static int i=0;  //USERS ARRAY
	static boolean[] onlineUser=new boolean[5];
	static String[] username=new String[5]; 
	Socket[] s=new Socket[5];
	BufferedReader[] br=new BufferedReader[6];
	PrintStream[] ps=new PrintStream[6];

	public static void main(String... args)
	{
		MyServer obj=new MyServer("Server");

		while(true)			 
		{	
			try
			{	
					ServerSocket ss=new ServerSocket(9786);
					System.out.println("waiting for request...");
					obj.s[i]=ss.accept();
					onlineUser[i]=true;
					Thread t=new Thread(obj);
					i++; 
					System.out.println(obj.s[i-1]);				
					t.start();
					System.out.println("YEAH");
			}
			catch (Exception e)
			{
				// System.out.println("Not Found data for Socket: "+e);
			}	
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		String msg=sendtxt.getText();
		sendtxt.setText("");
		System.out.println(msg);
		for(int j=0;j<i;j++)	
			{
				ps[j].println("Server says :"+msg);
				ps[j].flush();
			}
			if(msg.equals("exit")==true)
					{
						System.out.print("Connection is terminated by Client");
						System.exit(1);
					}
	}

	public void run()
	{
	   final int count=i-1;
	   try {	
			System.out.println("Request accepted");
			
			br[i-1]=new BufferedReader(new InputStreamReader(s[i-1].getInputStream()));
			ps[i-1]=new PrintStream(s[i-1].getOutputStream());
			username[count]=br[count].readLine();
			connectedUsers();
			while(true)
			  {
				String str=br[count].readLine();
                 
                if(str.equals("image")==true) 
				{
					BufferedImage image = ImageIO.read(s[count].getInputStream());
					for(int j=0;j<i;j++)	
					{
        	    			 ps[j].println("image");
	        	    		 ps[j].flush();
	        	    		 ImageIO.write(image,"png",s[j].getOutputStream());
					}
				} 
				else if(str.equals("exit")==true)
					{
							System.out.print("Connection lost...");
							System.exit(1);
					}
						
					else
					{
						for(int j=0;j<i;j++)	
						{		
						   		ps[j].println(username[count]+" : "+str);
						 		ps[j].flush();					 	
						}
						recievetxt.setText(recievetxt.getText()+str+"\n");
					} 
			  }
			}
			catch(Exception e)
			{
				e.printStackTrace(); 
				onlineUser[count]=false;
				connectedUsers();
			}
	}

	public void connectedUsers()
	{
		for(int j=0;j<i;j++)
		{
			System.out.println("Client "+(j+1)+"  "+username[j]+" "+onlineUser[j]);
		}
		
		for(int j=0;j<i;j++)
		{
			ps[j].println("onlineuser");
		    ps[j].flush();
			for(int k=0;k<i;k++)
			{
 				if(onlineUser[k])
 				{	
 					ps[j].println(username[k]);
			    	ps[j].flush();
			    }
			}
		    ps[j].println("endusers");
		    ps[j].flush();
		}
	}
}