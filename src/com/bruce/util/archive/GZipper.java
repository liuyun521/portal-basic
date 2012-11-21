package com.bruce.util.archive;

/** GZIP 压缩执行器类 */
public class GZipper extends Tarrer
{
	/** GZIP 文件后缀名 */
	public static final String STUFFIX = ".tar.gz";

	public GZipper(String source)
	{
		super(source);
	}
	
	public GZipper(String source, String target)
	{
		super(source, target);
	}
	
	/** 获取 GZIP 文件压缩模式 */
	@Override
	protected String getCompressionMode()
	{
		return COMPRESS_MODES[1];
	}
	
	/** 获取压缩文件后缀名 */
	@Override
	public String getSuffix()
	{
		return STUFFIX;
	}
}
