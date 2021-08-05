import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import PhotoComponentsPage from './photo.page-object';
import PhotoUpdatePage from './photo-update.page-object';
import {
  waitUntilDisplayed,
  waitUntilAnyDisplayed,
  click,
  getRecordsCount,
  waitUntilHidden,
  waitUntilCount,
  isVisible,
} from '../../util/utils';
import path from 'path';

const expect = chai.expect;

describe('Photo e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let photoComponentsPage: PhotoComponentsPage;
  let photoUpdatePage: PhotoUpdatePage;
  const username = process.env.E2E_USERNAME ?? 'admin';
  const password = process.env.E2E_PASSWORD ?? 'admin';

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();
    await signInPage.username.sendKeys(username);
    await signInPage.password.sendKeys(password);
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();
    await waitUntilDisplayed(navBarPage.entityMenu);
    await waitUntilDisplayed(navBarPage.adminMenu);
    await waitUntilDisplayed(navBarPage.accountMenu);
  });

  beforeEach(async () => {
    await browser.get('/');
    await waitUntilDisplayed(navBarPage.entityMenu);
    photoComponentsPage = new PhotoComponentsPage();
    photoComponentsPage = await photoComponentsPage.goToPage(navBarPage);
  });

  it('should load Photos', async () => {
    expect(await photoComponentsPage.title.getText()).to.match(/Photos/);
    expect(await photoComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Photos', async () => {
    const beforeRecordsCount = (await isVisible(photoComponentsPage.noRecords)) ? 0 : await getRecordsCount(photoComponentsPage.table);
    photoUpdatePage = await photoComponentsPage.goToCreatePhoto();
    await photoUpdatePage.enterData();
    expect(await isVisible(photoUpdatePage.saveButton)).to.be.false;

    expect(await photoComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(photoComponentsPage.table);
    await waitUntilCount(photoComponentsPage.records, beforeRecordsCount + 1);
    expect(await photoComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await photoComponentsPage.deletePhoto();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(photoComponentsPage.records, beforeRecordsCount);
      expect(await photoComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(photoComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
