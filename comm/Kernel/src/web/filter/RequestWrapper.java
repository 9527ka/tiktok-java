package web.filter;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper
{

	public RequestWrapper(HttpServletRequest request)
	{
		
		super(request);
	}

	@Override
	public String getParameter(String name)
	{

		String value = super.getParameter(name);
		
		if(value != null)
		{
			value = this.filterUserInput(value);
		}
		return value;
	}

	@Override
	public String[] getParameterValues(String name)
	{
		String[] values = super.getParameterValues(name);
		if (values != null)
		{
			for(int i=0, l = values.length; i < l; i++)
			{
				values[i] = this.filterUserInput(values[i]);
			}
		}
		return values;
	}
	
	/**
	 * 过滤用户输入
	 * @param input
	 * @return
	 */
	private String filterUserInput(String input)
	{
		input = Validator.filter(input);
	
		return input==null?input:input.trim();
	}
}
