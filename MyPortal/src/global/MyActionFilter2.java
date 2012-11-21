package global;

import com.bruce.mvc.ActionExecutor;
import com.bruce.mvc.ActionFilter;
import com.bruce.util.Logger;

public class MyActionFilter2 implements ActionFilter
{
	@Override
	public void init()
	{
		Logger.debug("> %s.init()", getClass().getName());
	}

	@Override
	public void destroy()
	{
		Logger.debug("> %s.destroy()", getClass().getName());
	}

	@Override
	public String doFilter(ActionExecutor executor) throws Exception
	{
		Logger.debug(">>> Filter-2");
		String result = executor.invoke();
		Logger.debug("Filter-2 <<<");
		
		return result;
	}
}
