package action.test;

import com.bruce.mvc.ActionSupport;
import com.bruce.mvc.FormBean;
import com.bruce.util.basedao.FacadeProxy;

import dao.jdbc.UserDao;

@FormBean
public class JdbcDeleteUser extends ActionSupport
{
	private int id;
	
	@Override
	public String execute() throws Exception
	{
		UserDao dao = FacadeProxy.getManualCommitProxy(UserDao.class);
		dao.deleteUser(id);
		
		return SUCCESS;
	}

	public void setId(int id)
	{
		this.id = id;
	}
}
