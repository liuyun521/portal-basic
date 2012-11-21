package com.bruce.util.archive;

import java.io.File;

import org.apache.tools.ant.types.FileSet;

/** 压缩执行器基础类 */
abstract public class Archiver extends TaskExecutor
{
	/** 获取压缩文件的后缀名 */
	abstract public String getSuffix();
	
	public Archiver(String source)
	{
		super(source);
	}
	
	public Archiver(String source, String target)
	{
		super(source, target);
	}

	/** 获取输出文件的 {@link File} 对象 */
	protected File getTargetFile()
	{
		if(target != null)
			return new File(target);
		
		return new File(source + getSuffix());
	}
	
	/** 填充 {@link FileSet} 对象属性 */
	protected void fillFileSetAttributes(FileSet fs, File f)
	{
		if(f.isDirectory())
			fs.setDir(f);
		else
			fs.setFile(f);
		
		if(includes != null)
			fs.setIncludes(includes);
		if(excludes != null)
			fs.setExcludes(excludes);
	}

}
