package global;

import com.bruce.mvc.AbstractActionFilter;
import com.bruce.mvc.ActionExecutor;
import com.bruce.util.Logger;

public class MyActionFilter1 extends AbstractActionFilter
{
	@Override
	public String doFilter(ActionExecutor executor) throws Exception
	{
		Logger.debug(">>> Filter-1");
		String result = executor.invoke();
		Logger.debug("Filter-1 <<<");
		
		return result;
	}
}
