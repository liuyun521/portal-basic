package com.bruce.util.archive;

/** BZIP2 解压执行器类 */
public class UnBZipper extends UnTarrer
{
	public UnBZipper(String source)
	{
		super(source);
	}
	
	public UnBZipper(String source, String target)
	{
		super(source, target);
	}
	
	/** 获取 BZIP2 文件压缩模式 */
	@Override
	protected String getCompressionMode()
	{
		return COMPRESS_MODES[2];
	}

}
