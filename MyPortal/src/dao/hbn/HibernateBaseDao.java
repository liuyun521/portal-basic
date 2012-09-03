package dao.hbn;

import com.bruce.util.basedao.hbn.HibernateFacade;
import com.bruce.util.basedao.hbn.HibernateSessionMgr;

import global.MyConfigParser;

public class HibernateBaseDao extends HibernateFacade
{
	protected HibernateBaseDao()
	{
		this(MyConfigParser.getHibernateSessionMgr());
	}
	
	protected HibernateBaseDao(HibernateSessionMgr mgr)
	{
		super(mgr);
	}

}
