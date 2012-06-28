package com.khs.sherpa.util;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.khs.sherpa.SherpaTest;

@RunWith(JMock.class)
public class UrlUtilTest extends SherpaTest {
	
	@Test
	public void testGetPath() {
		final HttpServletRequest request = mock(HttpServletRequest.class);
		checking(new Expectations() {{
			oneOf (request).getRequestURI(); will(returnValue("/context/servlet/endpoint"));
			oneOf (request).getContextPath(); will(returnValue("/context"));
			oneOf (request).getServletPath(); will(returnValue("/servlet"));
		}});
		Assert.assertEquals("/endpoint", UrlUtil.getPath(request));
		assertIsSatisfied();
	}
	
	@Test
	public void testGetParameter() {
		final HttpServletRequest request = mock(HttpServletRequest.class);
		
		checking(new Expectations() {{
		    oneOf (request).getParameter("id"); will(returnValue("1"));
		}});
		
		Assert.assertEquals("1", UrlUtil.getRequestParameter(request, "id"));
		assertIsSatisfied();
	}
	
	@Test
	public void testGetRequestUrlParameterOneValue() {
		final HttpServletRequest request = mock(HttpServletRequest.class);
		checking(new Expectations() {{
			allowing (request).getRequestURI(); 	will(returnValue("/context/servlet/endpoint/1"));
			allowing (request).getContextPath(); 	will(returnValue("/context"));
			allowing (request).getServletPath(); 	will(returnValue("/servlet"));
			allowing (request).getMethod();			will(returnValue("GET"));
		}});
		
		
		String id = UrlUtil.getRequestUrlParameter(request, "/endpoint/{id}", "id");
		
		Assert.assertEquals("1", id);
		assertIsSatisfied();
	}
	
	@Test
	public void testGetRequestUrlParameterTwoValue() {
		final HttpServletRequest request = mock(HttpServletRequest.class);
		checking(new Expectations() {{
			allowing (request).getRequestURI(); 	will(returnValue("/context/servlet/endpoint/John/Smith"));
			allowing (request).getContextPath(); 	will(returnValue("/context"));
			allowing (request).getServletPath(); 	will(returnValue("/servlet"));
			allowing (request).getMethod();			will(returnValue("GET"));
		}});
		
		String firstName = UrlUtil.getRequestUrlParameter(request, "/endpoint/{firstName}/{lastName}", "firstName");
		String lastName = UrlUtil.getRequestUrlParameter(request, "/endpoint/{firstName}/{lastName}", "lastName");
		
		Assert.assertEquals("John", firstName);
		Assert.assertEquals("Smith", lastName);
		assertIsSatisfied();
	}
	
	@Test
	public void testGetRequestUrlParameterOneValueNull() {
		final HttpServletRequest request = mock(HttpServletRequest.class);
		checking(new Expectations() {{
			allowing (request).getRequestURI(); 	will(returnValue("/context/servlet/endpoint/1"));
			allowing (request).getContextPath(); 	will(returnValue("/context"));
			allowing (request).getServletPath(); 	will(returnValue("/servlet"));
			allowing (request).getMethod();			will(returnValue("GET"));
		}});
		
		String name = UrlUtil.getRequestUrlParameter(request, "/endpoint/{name}", "firstName");
		
		Assert.assertNull(name);
		assertIsSatisfied();
	}
}
