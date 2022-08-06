import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


public class MyClient extends JFrame implements ActionListener 
{
	JLabel l;
	JTextArea sendtxt,recievetxt;
	JButton send,sendimage;
	JLabel imagelabel;

	// For image
	JFileChooser jfc;
	File f;

	MyClient(String s)
	{
		super(s);
		l=new JLabel("Enter the Message");
		sendtxt=new JTextArea();
		recievetxt=new JTextArea();
		
		imagelabel=new JLabel();
		jfc=new JFileChooser();
		
		send=new JButton("SEND");
		sendimage=new JButton("SEND IMAGE");
		l.setBounds(10,10,120,20);
		sendtxt.setBounds(10,40,300,100);
		recievetxt.setBounds(150,200,300,100);
		send.setBounds(10,150,100,50);
		sendimage.setBounds(10,200,100,50);
		imagelabel.setBounds(320,40,300,100);
		add(l); add(sendtxt); add(imagelabel); add(send); add(sendimage); add(recievetxt);
		send.addActionListener(this);
		sendimage.addActionListener(this);

		setLayout(null);
		setSize(800,800);
		setVisible(true);
	}

	Socket s;
	BufferedReader br;
	PrintStream ps;
	Graphics2D graphics;
	BufferedImage bri,image_org,image,readImage;;

	public static void main(String... args)
	{
		MyClient obj=new MyClient("MyChatterBox");

		try
		{
			obj.s=new Socket("localhost",9786);
			System.out.println("Server is connected");
			obj.br=new BufferedReader(new InputStreamReader(obj.s.getInputStream()));
			obj.ps=new PrintStream(obj.s.getOutputStream());
			
			while(true)
			{
				String str=obj.br.readLine();
				
				if(str.equals("exit")==true)
					{
						System.out.print("Connection lost...");
						System.exit(1);
					}	
				if(str.equals("image")==true)
				{
					obj.image = ImageIO.read(obj.s.getInputStream());
					obj.imagelabel.setIcon(new ImageIcon(obj.image));
				}	
				obj.recievetxt.setText(obj.recievetxt.getText()+str+"\n");
			}
		}
		catch(UnknownHostException e)
		{
			System.out.println("Not find the IP-ADDRESS for: "+e);
		}
		catch(Exception e)
		{
			System.out.println("Not Found data for Socket: "+e);
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==sendimage)	
		{	
			int x=jfc.showOpenDialog(null);
		  	if(x==JFileChooser.APPROVE_OPTION)
				{
					/*------- Signal of sending an image------*/
					ps.println("image");
					ps.flush();
				
					int i=0;
					f=jfc.getSelectedFile();
			      	try{
						  imagelabel.setIcon(new ImageIcon(f.getName()));
						  image_org=ImageIO.read(f);
						  image=new BufferedImage(image_org.getWidth(),image_org.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		              	  graphics=image.createGraphics();
						  graphics.drawRenderedImage(image_org,null);
						  graphics.dispose();
						  ImageIO.write(image, "PNG", s.getOutputStream()); 
		              	}
			  	catch(IOException er)	{	 System.out.println(er); }
        		/*---------------*/                                     // added here
				}

          	if(x==JFileChooser.CANCEL_OPTION)
				{
			  		return ;
				}
		}
		else
		{
			String msg=sendtxt.getText();
			sendtxt.setText("");
			ps.println(msg);
			ps.flush();
				if(msg.equals("exit")==true)
						{
							System.out.print("Connection is terminated by Clinet");
							System.exit(1);
						}
		}
	}
}