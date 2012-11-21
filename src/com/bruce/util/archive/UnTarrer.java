package com.bruce.util.archive;

import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Untar;
import org.apache.tools.ant.taskdefs.Untar.UntarCompressionMethod;

/** TAR 解档执行器类 */
public class UnTarrer extends UnArchiver
{
	/** TAR 压缩模式 */
	protected static final String[] COMPRESS_MODES = {"none", "gzip", "bzip2"};

	public UnTarrer(String source)
	{
		super(source);
	}
	
	public UnTarrer(String source, String target)
	{
		super(source, target);
	}
	
	/** 获取解压任务对象 */
	@Override
	protected Expand getExpand()
	{
		Untar ut = new Untar();
		ut.setCompression(getCompressionMethod());
		
		return ut;
	}

	private UntarCompressionMethod getCompressionMethod()
	{
		UntarCompressionMethod method = new UntarCompressionMethod();
		method.setValue(getCompressionMode());
		
		return method;
	}
	
	/** 获取 TAR 文件压缩模式 */
	protected String getCompressionMode()
	{
		return COMPRESS_MODES[0];
	}

}
