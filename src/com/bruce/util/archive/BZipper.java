package com.bruce.util.archive;

/** BZIP2 压缩执行器类 */
public class BZipper extends Tarrer
{
	/** BZIP2 文件后缀名 */
	public static final String STUFFIX = ".tar.bz2";

	public BZipper(String source)
	{
		super(source);
	}
	
	public BZipper(String source, String target)
	{
		super(source, target);
	}
	
	/** 获取 BZIP2 文件压缩模式 */
	@Override
	protected String getCompressionMode()
	{
		return COMPRESS_MODES[2];
	}
	
	/** 获取压缩文件后缀名 */
	@Override
	public String getSuffix()
	{
		return STUFFIX;
	}
}
