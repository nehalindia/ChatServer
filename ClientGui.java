import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.image.*;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class ClientGui extends JFrame implements ActionListener,FocusListener
{
	static Socket s;	
	PrintStream ps=null;
	BufferedReader br=null;
	JTextArea sendtxt;
	TextArea recievetxt,tf1;
	JButton send,b,sendimagebutton;
	JScrollPane sp;
	
	JLabel sendimage,recieveimage;
	Graphics2D graphics;
	BufferedImage bri,image_org,image,readImage;;
	JFileChooser jfc;
	File f;
	
	JFrame usernameframe;  // For username
	JTextField usernametext;
	String username=null;
	JButton usernamebutton;

	int imagerounder=0;  //will be changed while sending the image
	//CLient side GUI...
     ClientGui(String s)
	{
		super(s);
		JLabel tv=new JLabel("<html><span style='font-size:20px;color:orange'>CaTChYa</span></html>");
		jfc=new JFileChooser();
		recievetxt=new TextArea();
		recievetxt.setEditable(true);
		tf1=new TextArea();
		tf1.setEditable(false);
	    sendtxt=new JTextArea("  Type your text...");
		
		usernameframe=new JFrame("Login");
		usernametext=new JTextField();
		usernametext.setBounds(10,10,100,40);
		usernamebutton=new JButton("OK");
		usernamebutton.setBounds(50,60,80,20);
		usernameframe.add(usernametext);
		usernamebutton.addActionListener(this);
		usernameframe.add(usernamebutton);
		usernameframe.setSize(200,150);
		usernameframe.setLayout(null);
		usernameframe.setVisible(true);
				while(username==null)
						System.out.print("");		

		JLabel tv1=new JLabel("<html><span style='font-size:12px;color:green'>Online</span></html>");
		b=new JButton(new ImageIcon("Logout.jpg"));
		send=new JButton(new ImageIcon("send.jpg"));
		
		sendimagebutton=new JButton("Send Image");

		JLabel smiley=new JLabel(new ImageIcon("smi.gif"));
		
		sendimage=new JLabel("send image");
		recieveimage=new JLabel("recieve image");
		
		b.setBounds(350,10,80,30);
		send.setBounds(360,400,45,40);

		sendimagebutton.setBounds(405,400,45,40);
		
		tv.setBounds(20,10,280,40);
		recievetxt.setBounds(10,80,300,200);

		sendimage.setBounds(10,215,150,200);
		recieveimage.setBounds(165,215,150,200);

		
		add(b);
		b.addActionListener(this);
		send.addActionListener(this);
		sendimagebutton.addActionListener(this);
		sendtxt.addFocusListener(this);
		tf1.setBounds(320,80,130,300);
		sendtxt.setBounds(10,390,340,50);
		smiley.setBounds(20,5,400,80);
		sendtxt.setBackground(Color.CYAN);
		tf1.setBackground(Color.pink);
		//sendtxt.setPlaceholder("Type your text...");
		tv1.setBounds(350,40,100,40);
		add(smiley);
		add(recievetxt);
		add(tv);
		add(tv1);
		add(tf1);
		add(sendtxt);
		add(send);

		add(sendimage);
		add(recieveimage);
		add(sendimagebutton);

		setSize(480,500); 
		getContentPane().setBackground(Color.DARK_GRAY);
		setLayout(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

    }
	
	 public void actionPerformed(ActionEvent e)
	{		 
		 if(e.getSource()==usernamebutton)	
		 {
		 		username= usernametext.getText();
		 		setTitle(username);
		 		usernameframe.setVisible(false);
		 }

		 if(e.getSource()==b){ //  logout
			try 
		 		{  
		 		  s.close();
			 	  System.exit(0);
			 	}
			catch(Exception ex) {}
		 }

		 if(e.getSource()==sendimagebutton)	
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
						  sendimage.setIcon(new ImageIcon( f.getPath() ));
						  System.out.println(f.getPath());
						  image_org=ImageIO.read(f);
						  image=new BufferedImage(image_org.getWidth(),image_org.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		              	  graphics=image.createGraphics();
						  graphics.drawRenderedImage(image_org,null);
						  graphics.dispose();
						  ImageIO.write(image, "PNG", s.getOutputStream());
						  ps.println("Succesfully send");
		              	}
			  	catch(IOException er)	{	 System.out.println(er); }
        		/*---------------*/                                     // added here
				}

          	if(x==JFileChooser.CANCEL_OPTION)
				{
			  		return ;
				}
		}
		
		if(e.getSource()==send)
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

	public void focusGained(FocusEvent e)
	{
		
		 if(e.getSource()==sendtxt)
			 sendtxt.setText("");
	}

	public void focusLost(FocusEvent e)
	{
		if(sendtxt.getText().equals(""))
		sendtxt.setText("  Type your text...");
	}

   public static void main(String[] args)throws Exception
	{
		ClientGui obj=new ClientGui("MyChatterBox");

		try
		{
			s=new Socket("localhost",9786);
			System.out.println("Server is connected");
			obj.br=new BufferedReader(new InputStreamReader(s.getInputStream()));
			obj.ps=new PrintStream(s.getOutputStream());
			obj.ps.println(obj.username);
			obj.ps.flush(); 
			while(true)
			{
				String str=obj.br.readLine();
				if(str.equals("exit")==true)
					{
						System.out.print("Connection lost...");
						System.exit(1);
					}
				else if(str.equals("onlineuser"))
				{
					String user=null,userlist="";
					try{
			 			while(!(user=obj.br.readLine()).equals("endusers"))
			 			{
			 				userlist+=user+"\n";
			 			}
						obj.tf1.setText(userlist); 			
			 		}
			 		catch(Exception ex){ }	
				}		
				else if(str.equals("image")==true)
					{
						obj.image = ImageIO.read(obj.s.getInputStream());
						obj.recieveimage.setIcon(new ImageIcon(obj.image));
						obj.imagerounder=1;
					}
				else 
					if(obj.imagerounder!=2)
						obj.recievetxt.setText(obj.recievetxt.getText()+str+"\n");
				
				if(obj.imagerounder==2)
					   obj.imagerounder=3;

				if(obj.imagerounder==1)
					   obj.imagerounder=2;
				
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
}