package global;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bruce.util.http.HttpHelper;

import freemarker.ext.servlet.FreemarkerServlet;

import static com.bruce.mvc.Action.Constant.REQ_ATTR_BASE_PATH;

@SuppressWarnings("serial")
public class MyFreemarkerServlet extends FreemarkerServlet
{
	@Override
	protected boolean preprocessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		setBasePath(request);
		return false;
	}

	private void setBasePath(HttpServletRequest request)
	{
		String __base = (String)request.getAttribute(REQ_ATTR_BASE_PATH);

		if(__base == null)
		{
			__base = HttpHelper.getRequestBasePath(request);
			request.setAttribute(REQ_ATTR_BASE_PATH, __base);
		}
	}
}
