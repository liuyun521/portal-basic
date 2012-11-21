package action.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Level;

import com.bruce.mvc.ActionSupport;
import com.bruce.util.Logger;
import com.bruce.util.http.FileDownloader;
import com.bruce.util.http.HttpHelper;
import com.bruce.util.http.FileDownloader.Result;

public class CheckDownload extends ActionSupport
{
	private static final String FILE_1			= "下载测试 - 项目框架.jpg";
	private static final String FILE_2			= "下载测试 - 文本文件.txt";
	// 相对路径
	private static final String RELATE_PATH		= "download/" + FILE_1;
	// 绝对路径
	private static final String ABSOLUTE_PATH	= "D:\\Server\\apache-tomcat-6.0.35\\webapps\\portal\\download\\" + FILE_2;
	
	@Override
	public String execute() throws Exception
	{
		byte[] bytes		= null;
		InputStream is		= null;
		FileDownloader fdl	= null;
		int type			= getIntParam("type", 1);
		
		if(type == 1)		// 物理文件（绝对路径）
		{
			// 创建 FileDownloader 对象
			fdl	= new FileDownloader(ABSOLUTE_PATH);
			
			// 根据需要调用 setters 设置下载属性
			// fdl.setSaveFileName("我的文件");
		}
		else if(type == 2)	// 物理文件（相对路径）
		{
			// 创建 FileDownloader 对象
			fdl	= new FileDownloader(RELATE_PATH);
		}
		else if(type == 3)	// 字节数组
		{
			InputStream fis = new FileInputStream(ABSOLUTE_PATH);
			bytes = new byte[fis.available()];
			fis.read(bytes);
			
			// 创建 FileDownloader 对象
			fdl = new FileDownloader(bytes, FILE_2);
		}
		else if(type == 4)	// 字节流
		{
			is = new FileInputStream(new File(HttpHelper.getRequestRealPath(getRequest(), RELATE_PATH)));
			
			// 创建 FileDownloader 对象
			fdl = new FileDownloader(is, FILE_1);
		}
		
		// 执行下载
		Result result = fdl.download(getRequest(), getResponse());
		
		// 检查下载结果
		if(result != Result.SUCCESS)
		{
			// 记录日志
			Logger.exception(fdl.getCause(), "download file", Level.ERROR, false);
		}
		
		// 不再转发
		return NONE;
	}
}
