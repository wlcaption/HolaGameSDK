package com.qianhuan.yxgsd.holagames.tools.http;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class ReqTask extends AsyncTask<Void, Void, String>
{
    private static final String TAG = ReqTask.class.getSimpleName();
    
    private Delegate delegate = null;
    
    private String reqParams = null;
    
    private String reqUrl = null;
    
    public ReqTask(Delegate deg, String params, String url)
    {
        delegate = deg;
        reqParams = params;
        reqUrl = url;
    }
    
    @Override
    protected String doInBackground(Void... params)
    {
        String result = null;
        try
        {
            /**
             * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷使锟斤拷一锟斤拷一锟斤拷锟斤拷映俅锟斤拷妫拷锟紺P使锟矫帮拷全锟斤拷锟斤拷锟斤拷实锟街斤拷锟斤拷
             */
            Thread.sleep(0);
            result = "result";
            /**
             * 锟斤拷锟斤拷锟街肺猺eqUrl锟斤拷锟斤拷锟斤拷锟絇OST锟斤拷锟斤拷为reqParams锟斤拷使锟斤拷UTF-8锟斤拷锟斤拷锟绞�
             */
            result = sendPost(reqUrl, reqParams);
            //result = NetTool.sendPostRequest(reqUrl, reqParams, "utf-8");
        }
        catch (Exception e)
        {
        }
        return result;
    }
    
    @Override
    protected void onPostExecute(String result)
    {
        delegate.execute(result);
    }
    
    public interface Delegate
    {
        public void execute(String result);
    }
    
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 锟津开猴拷URL之锟斤拷锟斤拷锟斤拷锟�
            URLConnection conn = realUrl.openConnection();
            // 锟斤拷锟斤拷通锟矫碉拷锟斤拷锟斤拷锟斤拷锟斤拷
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 锟斤拷锟斤拷POST锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 锟斤拷取URLConnection锟斤拷锟斤拷锟接︼拷锟斤拷锟斤拷锟斤拷
            out = new PrintWriter(conn.getOutputStream());
            // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
            out.print(param);
            // flush锟斤拷锟斤拷锟斤拷幕锟斤拷锟�
            out.flush();
            // 锟斤拷锟斤拷BufferedReader锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷取URL锟斤拷锟斤拷应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("锟斤拷锟斤拷 POST 锟斤拷锟斤拷锟斤拷锟斤拷斐ｏ拷锟�"+e);
            e.printStackTrace();
        }
        //使锟斤拷finally锟斤拷锟斤拷锟截憋拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
    
}