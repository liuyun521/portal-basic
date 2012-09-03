package test;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.bruce.util.KV;
import com.bruce.util.http.HttpHelper;

public class TestHttpHelper
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args)
	{
		try
		{
			String srcURL = "http://www.baidu.com/s";
			KV<String, String>[] params = new KV[] {new KV("wd", "portal basic"),  new KV("pn", "10")};
			
			// 生成带参数的 URL 地址
			String URL = HttpHelper.makeURL(srcURL, params);
			// 连接服务器
			HttpURLConnection conn = HttpHelper.getHttpConnection(URL);
			// 读取返回的 HTML
			String str = HttpHelper.readString(conn, false, "GBK");
			
			System.out.println(str);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
