package org.tdd.librarymanagement.bdd;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/bdd/resources/library_management.feature", monochrome = true)
public class LibraryManagementRunnerBDD {

	private static MongoDBContainer mongo;
	public static int mongoPort;

	@BeforeClass
	public static void setUpOnce() {
		FailOnThreadViolationRepaintManager.install();
		mongo = new MongoDBContainer("mongo:4.4");
		mongo.start();
		mongoPort = mongo.getMappedPort(27017);
		System.setProperty("mongo.port", String.valueOf(mongoPort));
	}

}