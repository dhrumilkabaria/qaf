/*******************************************************************************
 * QMetry Automation Framework provides a powerful and versatile platform to author 
 * Automated Test Cases in Behavior Driven, Keyword Driven or Code Driven approach
 *                
 * Copyright 2016 Infostretch Corporation
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
 * OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
 *
 * You should have received a copy of the GNU General Public License along with this program in the name of LICENSE.txt in the root folder of the distribution. If not, see https://opensource.org/licenses/gpl-3.0.html
 *
 * See the NOTICE.TXT file in root folder of this source files distribution 
 * for additional information regarding copyright ownership and licenses
 * of other open source software / files used by QMetry Automation Framework.
 *
 * For any inquiry or need additional information, please contact support-qaf@infostretch.com
 *******************************************************************************/

package com.qmetry.qaf.automation.ui;

import static com.qmetry.qaf.automation.ui.webdriver.ElementFactory.$;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.qmetry.qaf.automation.ui.annotations.PageIdentifier;
import com.qmetry.qaf.automation.ui.api.PageLocator;
import com.qmetry.qaf.automation.ui.api.WebDriverTestPage;
import com.qmetry.qaf.automation.ui.webdriver.ComponentFactory;
import com.qmetry.qaf.automation.ui.webdriver.QAFExtendedWebDriver;
import com.qmetry.qaf.automation.ui.webdriver.QAFExtendedWebElement;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebComponent;
import com.qmetry.qaf.automation.ui.webdriver.QAFWebElement;
import com.qmetry.qaf.automation.util.ClassUtil;

/**
 * com.qmetry.qaf.automation.core.ui.WebDriverBaseTestPage.java
 * <p>
 * This is a base class to develop test page using Webdriver API. This class
 * excepts one parameter that is parent page in hierarchy which must be
 * {@link WebDriverTestPage}. If the page has no parent page then the parameter
 * value should be {@link WebDriverTestPage} interface. You can apply different
 * page design like Hierarchical page, Linked page, Template page.
 * <dl>
 * <dt>Methods that need to be implement
 * <dd></dd>
 * </dl>
 * 
 * @author chirag
 */
public abstract class WebDriverBaseTestPage<P extends WebDriverTestPage>
		extends AbstractTestPage<P, QAFExtendedWebDriver> implements WebDriverTestPage {
	
	protected List<QAFWebElement> pageIdentifiers;

	public WebDriverBaseTestPage() {
		this(null);
	}

	public WebDriverBaseTestPage(P parent) {
		super(new WebDriverTestBase(), parent);
		pageIdentifiers = getPageIdentifiers();
	}

	@Override
	public String getText() {
		return $("tagName=body").getText();
	}

	@Override
	public WebDriverTestBase getTestBase() {
		return (WebDriverTestBase) super.getTestBase();
	}

	@Override
	public boolean isPageActive(PageLocator loc, Object... args) {
		for(QAFWebElement element:pageIdentifiers){
			if(!element.isPresent())
				return false;
		}
		return pageIdentifiers.size()>0;
	}
	
	/**
	 * This method should be called to wait for page load. It will wait for all element annotated with {@link PageIdentifier} to be present. 
	 * Override this method to provide page specific custom wait implementation. 
	 */
	@Override
	public void waitForPageToLoad() {
		driver.waitForAllElementPresent(pageIdentifiers.toArray(new QAFWebElement[]{}));
	}

	@SuppressWarnings("unchecked")
	public <T extends QAFWebComponent> List<T> findElements(String loc, Class<T> t) {
		List<QAFWebElement> eles = driver.findElements(loc);
		List<T> objs = new ArrayList<T>();
		for (QAFWebElement ele : eles) {
			T obj = (T) ComponentFactory.getObject(t.getClass(), loc, this, driver);
			obj.setId(((QAFExtendedWebElement) ele).getId());
			try {
				Field cacheable = obj.getClass().getDeclaredField("cacheable");
				cacheable.setAccessible(true);
				cacheable.set(obj, true);
			} catch (Exception e) {
				logger.error(e);
			}
			objs.add(obj);
		}
		return objs;
	}

	@SuppressWarnings("unchecked")
	public <T extends QAFWebComponent> T findElement(String loc, Class<T> t) {
		T obj = (T) ComponentFactory.getObject(t.getClass(), loc, this, driver);
		obj.getId();
		return obj;
	}

	@Override
	public void waitForAjaxToComplete() {
		getTestBase().getDriver().waitForAjax();
	}

	public void waitForTextPresent(String text) {
		$("partialLinkText=text").waitForPresent();
	}

	private List<QAFWebElement> getPageIdentifiers() {
		List<QAFWebElement> identifiers = new ArrayList<QAFWebElement>();

		Field[] flds = ClassUtil.getAllFields(this.getClass(), WebDriverBaseTestPage.class);
		for (Field fld : flds) {
			if(fld.isAnnotationPresent(PageIdentifier.class) && QAFWebElement.class.isAssignableFrom(fld.getType())){
				try {
					fld.setAccessible(true);
					identifiers.add((QAFWebElement) fld.get(this));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return identifiers;
	}
}
