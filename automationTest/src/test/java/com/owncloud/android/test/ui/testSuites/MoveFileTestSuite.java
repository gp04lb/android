/**
 *   ownCloud Android client application
 *
 *   @author purigarcia
 *   Copyright (C) 2015 ownCloud Inc.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.owncloud.android.test.ui.testSuites;

import static org.junit.Assert.*;
import io.appium.java_client.android.AndroidDriver;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

import com.owncloud.android.test.ui.actions.Actions;
import com.owncloud.android.test.ui.groups.*;
import com.owncloud.android.test.ui.models.ElementMenuOptions;
import com.owncloud.android.test.ui.models.FileListView;
import com.owncloud.android.test.ui.models.MoveView;
import com.owncloud.android.test.ui.models.WaitAMomentPopUp;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MoveFileTestSuite{
	AndroidDriver driver;
	Common common;
	@Rule public TestName name = new TestName();
	
	@Before
	public void setUp() throws Exception {
		common=new Common();
		driver=common.setUpCommonDriver();
	}

	@Test
	@Category({NoIgnoreTestCategory.class, SmokeTestCategory.class})
	public void testMoveDownloadedFile () throws Exception {
		WaitAMomentPopUp waitAMomentPopUp;

		FileListView fileListView = Actions.login(Config.URL, Config.user,
				Config.password, Config.isTrusted, driver);
		common.assertIsInFileListView(fileListView);

		//check if the folder already exists and if true, delete them
		Actions.deleteElement(Config.folderWhereMove, fileListView, driver);
		Actions.deleteElement(Config.fileToTest, fileListView, driver);

		//Create the folder where the other is gone to be moved
		waitAMomentPopUp = Actions
				.createFolder(Config.folderWhereMove, fileListView);
		Common.waitTillElementIsNotPresentWithoutTimeout(
				waitAMomentPopUp.getWaitAMomentTextElement(), 100);
		assertTrue(fileListView.getFileElement(Config.folderWhereMove)
				.isDisplayed());

		FileListView fileListViewAfterUploadFile = Actions
				.uploadFile(Config.fileToTest, fileListView);
		assertTrue(fileListViewAfterUploadFile.getFileElement(Config.fileToTest)
				.isDisplayed());

		//select to move the file
		ElementMenuOptions menuOptions = fileListView
				.longPressOnElement(Config.fileToTest);
		MoveView moveView = menuOptions.clickOnMove();

		//to move to a folder
		moveView.tapOnElement(Config.folderWhereMove);
		waitAMomentPopUp = moveView.clickOnChoose();
		Common.waitTillElementIsNotPresentWithoutTimeout(
				waitAMomentPopUp.getWaitAMomentTextElement(), 100);

		//check that the element moved is inside the other
		fileListView.tapOnElement(Config.folderWhereMove);
		Common.waitTillElementIsNotPresentWithoutTimeout(
				fileListView.getProgressCircular(),1000);

		Thread.sleep(1000);
		assertEquals(Config.fileToTest , 
				fileListView.getFileElement(Config.fileToTest).getText());

	}

	@After
	public void tearDown() throws Exception {
		common.takeScreenShotOnFailed(name.getMethodName());
		FileListView fileListView = new FileListView(driver);
		driver.sendKeyEvent(android.view.KeyEvent.KEYCODE_BACK);
		Actions.deleteElement(Config.folderWhereMove, fileListView, driver);
		Actions.deleteElement(Config.fileToTest, fileListView, driver);
		driver.removeApp("com.owncloud.android");
		driver.quit();
	}

}
