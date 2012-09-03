package com.bruce.util.archive;

/** GZIP 解压执行器类 */
public class UnGZipper extends UnTarrer
{
	public UnGZipper(String source)
	{
		super(source);
	}
	
	public UnGZipper(String source, String target)
	{
		super(source, target);
	}
	
	/** 获取 GZIP 文件压缩模式 */
	@Override
	protected String getCompressionMode()
	{
		return COMPRESS_MODES[1];
	}

}
