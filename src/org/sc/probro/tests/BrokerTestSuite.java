package org.sc.probro.tests;

import junit.framework.TestSuite;

public class BrokerTestSuite extends TestSuite {

	public BrokerTestSuite() { 
		super(new Class[] { 
			SupervisorTest.class,
			UsersTest.class,
			OntologiesTest.class,
			RequestsTest.class,
			MakeRequestTest.class,
		});
	}
}
