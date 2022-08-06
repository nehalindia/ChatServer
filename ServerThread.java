class ServerThread implements Runnable
{

	public void run()
	{
	   final int count=i-1;
	   try {	
			System.out.println("Request accepted");
			br[i-1]=new BufferedReader(new InputStreamReader(s[i-1].getInputStream()));
			ps[i-1]=new PrintStream(s[i-1].getOutputStream());
		}
		catch(Exception e)
		{
			System.out.printlln("Exception in run method "+e);
		}

	}

}