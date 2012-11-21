package global;

import com.bruce.mvc.AbstractActionFilter;
import com.bruce.mvc.ActionExecutor;
import com.bruce.util.Logger;

public class UserActionFilter extends AbstractActionFilter
{
	@Override
	public String doFilter(ActionExecutor executor) throws Exception
	{
		Logger.debug(">>> User Action Filter (%s#%s())", 
		executor.getAction().getClass().getName(), 
		executor.getEntryMethod().getName());
		String result = executor.invoke();
		Logger.debug("User Action Filter <<<");
		
		return result;
	}
}
