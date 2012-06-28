package com.khs.sherpa;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.internal.ExpectationBuilder;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SherpaTest {

	private Mockery context = new JUnit4Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	protected <T> T mock(Class<T> type) {
		return context.mock(type);
	}
	
	protected void checking(ExpectationBuilder expectations) {
		context.checking(expectations);
	}
	
	public void assertIsSatisfied() {
		context.assertIsSatisfied();
	}
}
