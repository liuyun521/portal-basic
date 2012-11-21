package com.bruce.util.archive;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Tar;
import org.apache.tools.ant.taskdefs.Tar.TarCompressionMethod;
import org.apache.tools.ant.taskdefs.Tar.TarFileSet;

/** TAR 归档执行器类 */
public class Tarrer extends Archiver
{
	/** TAR 文件后缀名 */
	public static final String SUFFIX = ".tar";
	/** TAR 压缩模式 */
	protected static final String[] COMPRESS_MODES = {"none", "gzip", "bzip2"};

	public Tarrer(String source)
	{
		super(source);
	}
	
	public Tarrer(String source, String target)
	{
		super(source, target);
	}
	
	private TarCompressionMethod getCompressionMethod()
	{
		TarCompressionMethod method = new TarCompressionMethod();
		method.setValue(getCompressionMode());
		
		return method;
	}
	
	/** 获取 TAR 文件压缩模式 */
	protected String getCompressionMode()
	{
		return COMPRESS_MODES[0];
	}
	
	/** 获取压缩文件后缀名 */
	@Override
	public String getSuffix()
	{
		return SUFFIX;
	}
	
	/** 获取压缩任务对象 */
	@Override
	protected Task getTask()
	{
		Project project	= new Project();
		Tar tar			= new Tar();
		
		tar.setProject(project);
		createSourceFileSet(tar);

		tar.setDestFile(getTargetFile());
		tar.setCompression(getCompressionMethod());
		
		return tar;
	}

	private TarFileSet createSourceFileSet(Tar tar)
	{
		File f			= new File(source);
		TarFileSet fs	= tar.createTarFileSet();
		
		fillFileSetAttributes(fs, f);
		
		return fs;
	}
}
