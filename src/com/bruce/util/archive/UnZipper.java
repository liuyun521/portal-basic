package com.bruce.util.archive;

import org.apache.tools.ant.taskdefs.Expand;

import com.bruce.util.GeneralHelper;

/** ZIP 解压执行器类 */
public class UnZipper extends UnArchiver
{
	/** 默认文件名编码 */
	public static final String DEFAULT_ENCODING = GeneralHelper.IS_WINDOWS_PLATFORM ? "GBK" : "UTF-8";

	private String encoding = DEFAULT_ENCODING;

	public UnZipper(String source)
	{
		super(source);
	}
	
	public UnZipper(String source, String target)
	{
		super(source, target);
	}
	
	/** 获取文件名编码 */
	public String getEncoding()
	{
		return encoding;
	}

	/** 设置文件名编码 */
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}
	
	/** 获取解压任务对象 */
	@Override
	protected Expand getExpand()
	{
		Expand expand = new Expand();
		
		if(encoding != null)
			expand.setEncoding(encoding);
		
		return expand;
	}

}
