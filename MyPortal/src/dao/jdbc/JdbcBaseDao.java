package dao.jdbc;

import global.MyConfigParser;

import com.bruce.util.basedao.jdbc.AbstractJdbcSessionMgr;
import com.bruce.util.basedao.jdbc.JdbcFacade;

public class JdbcBaseDao extends JdbcFacade
{
	protected JdbcBaseDao()
	{
		this(MyConfigParser.getJdbcSessionMgr());
	}
	
	protected JdbcBaseDao(AbstractJdbcSessionMgr mgr)
	{
		super(mgr);
	}
}
