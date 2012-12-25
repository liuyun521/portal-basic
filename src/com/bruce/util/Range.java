/*
 * Copyright Bruce Liang (ldcsaa@gmail.com)
 *
 * Author	: Bruce Liang
 * Bolg		: http://www.cnblogs.com/ldcsaa
 * WeiBo	: http://weibo.com/u/1402935851
 * QQ Group	: http://qun.qq.com/#jointhegroup/gid/75375912
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bruce.util;

/** 数值范围类 */
public class Range<T extends Number>
{
	private T begin;
	private T end;
	
	public Range()
	{
		
	}
	
	public Range(T begin, T end)
	{
		this.begin	= begin;
		this.end		= end;
	}

	public T getBegin()
	{
		return begin;
	}

	public void setBegin(T begin)
	{
		this.begin = begin;
	}

	public T getEnd()
	{
		return end;
	}

	public void setEnd(T end)
	{
		this.end = end;
	}
	
	@Override
	public String toString()
	{
		return String.format("{%s - %s}", begin, end);
	}
}
