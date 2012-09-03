package dao.mybatis;

import com.bruce.util.basedao.mybatis.MyBatisFacade;
import com.bruce.util.basedao.mybatis.MyBatisSessionMgr;

import global.MyConfigParser;

public class MyBatisBaseDao extends MyBatisFacade
{
	protected MyBatisBaseDao()
	{
		this(MyConfigParser.getMyBaitsSessionMgr());
	}
	
	protected MyBatisBaseDao(MyBatisSessionMgr mgr)
	{
		super(mgr);
	}

}
