import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.StringTokenizer;
public class TweetUploadtoDB 
{
	public static void main(String args[])throws Exception
	{
		String url = "jdbc:mysql://localhost:3306/";
		String dbname = "TweetsDB";
		String driver = "com.mysql.jdbc.Driver";
		String username = "root";
		String password = "password";
		try { 
			Class.forName(driver).newInstance(); 
			Connection conn = DriverManager.getConnection(url+dbname,username,password);
			Statement st = (Statement) conn.createStatement();
			String path = "C:\\Users\\nisht_000\\Documents\\Sem 4.2\\CZ4034 Info Retrieval\\code-2015-03-14\\code\\data\\preprocessed\\crawled_normalisedMH17_norepeat.tsv";
			final File folder = new File("C:\\Users\\nisht_000\\Documents\\Sem 4.2\\CZ4034 Info Retrieval\\code-2015-03-14\\code\\data\\preprocessed\\");
			int flag=0;
			//for (File fileEntry : folder.listFiles())
			//{
			// File fileEntry = new File();
			//	if (fileEntry != null) {
					//String fileEntrytemp=path + fileEntry.getName().toString();
				//	System.out.println(fileEntrytemp);
					BufferedReader TSVFile = new BufferedReader(new FileReader(path));
					String dataRow = TSVFile.readLine();
					while(dataRow!=null)
					{
						StringTokenizer st1 = new StringTokenizer(dataRow,"\t"); 
						//System.out.println();
						System.out.println(st1.countTokens());
						 
						String[] tweet = new String[18];
						int i=0;
						while(st1.hasMoreElements())
						{
							
							String temp= st1.nextToken().toString().trim();
							if(!temp.contains("\\'"))
								temp = temp.replaceAll("'", "\\\\'");
							
							tweet[i]=temp;
							i++;
						}
						if(tweet[0]!=null )
						{
							
						
								if( tweet[0].equalsIgnoreCase("null") || tweet[0].equalsIgnoreCase("None") == false)
								{
						String sql = "INSERT into TweetsDB.tweets"
								+" VALUES('"+tweet[0]+"', '"+tweet[1]+"', '"+tweet[2]+"', '"+tweet[3]+"', '"+tweet[4]+"', '"+tweet[5]+"', '"+tweet[6]+"', '"+tweet[7]+"', '"+tweet[8]+"', '"+tweet[9]+"', '"+tweet[10]+"', '"+tweet[11]+"', '"+tweet[12]+"','null','null');";
						
						System.out.println(sql);	
						
						st.executeUpdate(sql);
						}}
						dataRow = TSVFile.readLine();
					}
					 
				//}
			//}
			
			
			conn.close(); 
			
			} 
		catch (Exception e) { e.printStackTrace(); }
		}
	}


