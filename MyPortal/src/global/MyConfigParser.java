package global;

import org.dom4j.Element;

import com.bruce.app.AppConfig;
import com.bruce.app.UserConfigParser;
import com.bruce.util.Logger;
import com.bruce.util.basedao.hbn.HibernateSessionMgr;
import com.bruce.util.basedao.jdbc.ProxoolSessionMgr;
import com.bruce.util.basedao.mybatis.MyBatisSessionMgr;

public class MyConfigParser implements UserConfigParser
{
	private static final String MY_HOME = "my-home";
	
	private static ProxoolSessionMgr jdbcSessionMgr;
	private static MyBatisSessionMgr myBaitsSessionMgr;
	private static HibernateSessionMgr hibernateSessionMgr;
	
	@Override
	public void parse(Element user)
	{
		jdbcSessionMgr		= (ProxoolSessionMgr)AppConfig.getSessionManager("session-mgr-1");
		myBaitsSessionMgr	= (MyBatisSessionMgr)AppConfig.getSessionManager("session-mgr-2");
		hibernateSessionMgr	= (HibernateSessionMgr)AppConfig.getSessionManager("session-mgr-3");
		
		Element mh = user.element(MY_HOME);
		if(mh != null)
		{
			String myHome = mh.getTextTrim();
			Logger.info("My Home is: " + myHome);
		}
	}

	public static final ProxoolSessionMgr getJdbcSessionMgr()
	{
		return jdbcSessionMgr;
	}

	public static MyBatisSessionMgr getMyBaitsSessionMgr()
	{
		return myBaitsSessionMgr;
	}

	public static HibernateSessionMgr getHibernateSessionMgr()
	{
		return hibernateSessionMgr;
	}

}
