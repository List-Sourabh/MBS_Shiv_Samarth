/*

 * Created by OVK on 13-10-2011
 */
package mbLib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;


public class DatabaseManagement {

	
	public String pkgNm,dbNm,qry;
	public SQLiteDatabase db;
	Cursor cur=null;
	public DatabaseManagement(String pkg_nm, String db_nm)
	{
		pkgNm=pkg_nm;
		dbNm=db_nm;
		db=null;
	}
	public  void initDatabase() {
		try {
			db = SQLiteDatabase.openOrCreateDatabase("/data/data/" + pkgNm
					+ "/databases" + dbNm, null);
		} catch (Exception e) {

			Log.i("Pigmi Appli Error","" + e);
			db = null;
		}
	}
	public String createTable(String tblNm,String coulmnsAndTypes[])
	{
		Log.i("Pigmi Appli Error","Entering in 'createTable' function");
		//if(!db.isOpen())
		initDatabase();
		String str=new String();
		try
		{
			if(coulmnsAndTypes.length%2==0)
			{
				for(int i=0;;)
				{
					str=str+coulmnsAndTypes[i++]+" "+coulmnsAndTypes[i++];
					 if(i>=coulmnsAndTypes.length)
						 break;
					 str=str+" , ";
				}
				qry="CREATE TABLE IF NOT EXISTS " + tblNm + "("
				+ str + ");";
				db.execSQL(qry);
			}
			else
			{
				Log.i("Pigmi Appli Error", "Invalid columns");
			}
		}
		catch(Exception e)
		{
			Log.i("Pigmi Appli Error",""+e);
			qry=e.getMessage();
		}
		db.close();
		Log.i("Pigmi Appli Error","Exiting from 'createTable' function");
		Log.i("Pigmi Appli Error",qry);
		return(qry);
	}
	
	public String insertIntoTable(String tblNm,int cnt,String colNms[],String val[])
	{
		//if(!db.isOpen())	
		initDatabase();
		String cols="",qMarks="";
		int i=0;
		try
		{
			for(i=0;;)
			{
				qMarks=qMarks+" ? ";
				if(++i==cnt)
					break;
				qMarks=qMarks+" , ";
			}
			if(colNms!=null)
			{
				if(cnt==colNms.length)
				{
					for(i=0;i<colNms.length;)
					{
						cols=cols+colNms[i++];
						if(i>=colNms.length)
							break;
						cols=cols+" , ";
					}
					if(i>0)
					{
						qry="INSERT INTO "+tblNm+ " ( "+cols+" )VALUES ( "+ qMarks +" );";
						db.execSQL(qry, val);
						Log.e("Pigmi Appli Error",qry);
						Log.e("Pigmi Appli Error","Record inserted successfully");
					}
				}
				else
				{
					Log.e("Pigmi Appli Error", "Invalid number of columns");
				}
			}
			else
			{
				qry="INSERT INTO "+tblNm+ " VALUES ( "+ qMarks+ " );";
				db.execSQL(qry,val);
				Log.e("Pigmi Appli Error","Record inserted successfully");
			}
		}
		catch(Exception e)
		{
			Log.e("Pigmi Appli Error", ""+e);
		}
		db.close();
		Log.i("Pigmi Appli Error","Exiting from 'insertIntoTable' function");
		return(qry);
	}
	
	public String updateTable(String tblNm,String colNms[],String whrClause,String val[])
	{
		Log.i("Pigmi Appli Error","Entering in 'updateTable' function");
		//if(!db.isOpen())
		initDatabase();
		String str="";
		int i;
		try
		{
			for(i=0;;)
			{
				str=str+colNms[i++] +" = ? ";
				if(i>=colNms.length)
				{
					break;
				}
				str=str+" , ";
			}
			if(whrClause!=null)
				str=str+whrClause;
			qry="update "+tblNm+" set "+str+" ; ";
			Log.i("Pigmi Appli Error", qry);
			Log.i("Pigmi Appli Error", "Count of array="+val.length);
			String collVal="";
			for (int j = 0; j < val.length; j++) {
					collVal = collVal + val[j]+ "#";
			}
			Log.i("Pigmi Appli Error", collVal);
			db.execSQL(qry,val);
		}
		catch(Exception e)
		{
			Log.i("Pigmi Appli Error", ""+e);
		}
		db.close();
		Log.i("Pigmi Appli Error","Exiting from 'updateTable' function");
		return(qry);
	}
	
	public String deleteFromTable(String tblNm,String whrClause,String val[])
	{
		Log.i("Pigmi Appli Error","Entering in 'deleteFromTable' function");
		//if(!db.isOpen())
		initDatabase();
		String str="";
		int i;
		try
		{
			if(whrClause!=null)
			{
				str=str+" where "+whrClause;
			}
			qry="delete from "+tblNm+"  "+str+" ; ";
			Log.i("Pigmi Appli Error",qry);
			db.execSQL(qry,val);
		}
		catch(Exception e)
		{
			Log.i("Pigmi Appli Error", ""+e);
		}
		db.close();
		Log.i("Pigmi Appli Error","Exiting from 'deleteFromTable' function");
		return(qry);
	}
	
	public Cursor selectFromTable(String tblNm,String whrClause,String val[])
	{
		Log.i("Pigmi Appli Error","Entering in 'selectFromTable' function");
		//if(!db.isOpen())
		initDatabase();
		//Cursor cur=null;
		String str="";
		int i;
		try
		{
			if(whrClause!=null)
			{
				str=str+"  "+whrClause;
			}
			qry="select * from  "+ tblNm +"   "+str+" ; ";
			Log.i("Pigmi Appli Error", qry);
			cur=db.rawQuery(qry, val);
			
		}
		catch(Exception e)
		{
			Log.i("Pigmi Appli Error", ""+e);
		}
		//db1.close();
		Log.i("Pigmi Appli Error","Exiting from 'selectFromTable' function");
		//db.close();
		return (cur);
	}
	
	public String dropTable(String tblNm)
	{
		try
		{
		Log.i("Pigmi Appli Error","Entering in 'dropTable' function");
		//if(!db.isOpen())
		initDatabase();
		String qry="Drop table "+tblNm +" ; ";
		db.execSQL(qry);
		}
		catch(Exception e)
		{
			Log.i("Pigmi Appli Error",""+e);
		}
		Log.i("Pigmi Appli Error","Exiting from 'dropTable' function");
		db.close();
		return(qry);
	}
	
	public Cursor executePersonalQuery(String qry,String val[])
	{
		//Cursor cur=null;
		try
		{
			initDatabase();
			cur=db.rawQuery(qry, val);
		}
		catch(Exception e)
		{
			Log.i("Pigmi Appli Error", ""+e);
		}
		//db.close();
		return cur;
	}
	
	
	
		//Added by Mayuri 21-04-15
		public boolean CheckIsDataAlreadyInDBorNot(String TableName,String dbfield, String fieldValue) 
		{//CheckIsDataAlreadyInDBorNot
			
		    initDatabase();
		    String Query = "Select * from " + TableName + " where  LOWER(" + dbfield + ") = '" + fieldValue+"'";
		    System.out.println("Inside CheckIsDataAlreadyInDBorNot Query=="+Query);
		    Cursor cursor = db.rawQuery(Query, null);
	        if(cursor.getCount() <= 0)
	        {
	            return false;
	        }
		    return true;
		}//CheckIsDataAlreadyInDBorNot

	
}
