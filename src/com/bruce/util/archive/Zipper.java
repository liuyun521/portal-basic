package com.bruce.util.archive;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

import com.bruce.util.GeneralHelper;

/** ZIP 压缩执行器类 */
public class Zipper extends Archiver
{
	/** ZIP 文件后缀名 */
	public static final String SUFFIX = ".zip";
	/** 默认文件名编码 */
	public static final String DEFAULT_ENCODING = GeneralHelper.IS_WINDOWS_PLATFORM ? "GBK" : "UTF-8";

	private String comment;
	private String encoding = DEFAULT_ENCODING;

	
	public Zipper(String source)
	{
		super(source);
	}
	
	public Zipper(String source, String target)
	{
		super(source, target);
	}
	
	/** 获取压缩文件描述 */
	public String getComment()
	{
		return comment;
	}

	/** 设置压缩文件描述 */
	public void setComment(String comment)
	{
		this.comment = comment;
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
		Zip zip			= new Zip();
		FileSet src		= getSourceFileSet();
		
		src.setProject(project);
		zip.setProject(project);
		zip.setDestFile(getTargetFile());
		zip.addFileset(src);
		
		if(encoding != null)
			zip.setEncoding(encoding);
		if(comment != null)
			zip.setComment(comment);
		
		return zip;
	}

	private FileSet getSourceFileSet()
	{
		File f		= new File(source);
		FileSet fs	= new FileSet();
			
		fillFileSetAttributes(fs, f);
		
		return fs;
	}

}
