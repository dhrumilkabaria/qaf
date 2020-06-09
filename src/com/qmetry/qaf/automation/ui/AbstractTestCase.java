/*******************************************************************************
 * QMetry Automation Framework provides a powerful and versatile platform to
 * author
 * Automated Test Cases in Behavior Driven, Keyword Driven or Code Driven
 * approach
 * Copyright 2016 Infostretch Corporation
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT
 * OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE
 * You should have received a copy of the GNU General Public License along with
 * this program in the name of LICENSE.txt in the root folder of the
 * distribution. If not, see https://opensource.org/licenses/gpl-3.0.html
 * See the NOTICE.TXT file in root folder of this source files distribution
 * for additional information regarding copyright ownership and licenses
 * of other open source software / files used by QMetry Automation Framework.
 * For any inquiry or need additional information, please contact
 * support-qaf@infostretch.com
 *******************************************************************************/

package com.qmetry.qaf.automation.ui;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;

import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.testng.TestNGTestCase;
import com.qmetry.qaf.automation.ui.api.UiTestBase;
import com.qmetry.qaf.automation.util.PropertyUtil;

/**
 * com.qmetry.qaf.automation.core.ui.AbstractTestCase.java
 * 
 * @author chirag.jayswal
 */
public abstract class AbstractTestCase<D, B extends UiTestBase<D>> extends TestNGTestCase {
	/**
	 * @deprecated use {@link #getProps()} instead
	 */
	protected PropertyUtil props;


	public AbstractTestCase() {
		props = ConfigurationManager.getBundle();
	}

	public abstract B getTestBase();

	public D getDriver() {
		return getTestBase().getDriver();
	}


	@AfterMethod(alwaysRun = true)
	final public void afterMethod(ITestContext testContext, ITestResult tr) {
		tearDownPrrallelThreads(testContext, "m");
	}

	@AfterGroups(alwaysRun = true)
	final public void afterGroup(ITestContext testContext) {
		tearDownPrrallelThreads(testContext, "groups");
	}

	@AfterClass(alwaysRun = true)
	final public void afterClass(ITestContext testContext) {
		tearDownPrrallelThreads(testContext, "classes");

	}

	@AfterTest(alwaysRun = true)
	final public void afterTest(ITestContext testContext) {
		tearDownPrrallelThreads(testContext, "tests");
	}

	private void tearDownPrrallelThreads(ITestContext testContext, String type) {
		String useSingleSeleniumInstance =
				ConfigurationManager.getBundle().getString("selenium.singletone", "t");
		// match with first char only so m or method or methods is fine
		if (useSingleSeleniumInstance.toUpperCase().startsWith(
				type.substring(0, 1).toUpperCase()) || type.equalsIgnoreCase("tests")) {
			if (getTestBase() != null) {
				getTestBase().tearDown();
			}
		}
	}
}
