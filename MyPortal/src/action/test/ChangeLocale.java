package action.test;

import java.util.Locale;

import com.bruce.mvc.ActionSupport;
import com.bruce.util.GeneralHelper;

public class ChangeLocale extends ActionSupport
{
	private static final String LAN		= "lan";
	private static final String CHINESE	= "chinese";
	private static final String ENGLISH	= "english";
	
	@Override
	public String execute()
	{
		String lan = getParam(LAN);
		
		if(!GeneralHelper.isStrEmpty(lan))
		{
    		if(CHINESE.compareToIgnoreCase(lan) == 0)
    			setLocale(Locale.SIMPLIFIED_CHINESE);
    		else if(ENGLISH.compareToIgnoreCase(lan) == 0)
    			setLocale(Locale.US);
		}
		
		return SUCCESS;
	}
	
	public int[] getSerial()
	{
		// 返回 List
		/*
		final String[] array = {"A", "B", "C", "D", "E", "J", "I", "H", "G", "F"};
		List<String> list = Arrays.asList(array);
		return list;
		*/
		
		// 返回数组
		return new int[] {0, 1, 2, 3, 4, 9, 8, 7, 6, 5};
	}
}
