import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import AlbumComponentsPage from './album.page-object';
import AlbumUpdatePage from './album-update.page-object';
import {
  waitUntilDisplayed,
  waitUntilAnyDisplayed,
  click,
  getRecordsCount,
  waitUntilHidden,
  waitUntilCount,
  isVisible,
} from '../../util/utils';

const expect = chai.expect;

describe('Album e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let albumComponentsPage: AlbumComponentsPage;
  let albumUpdatePage: AlbumUpdatePage;
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
    albumComponentsPage = new AlbumComponentsPage();
    albumComponentsPage = await albumComponentsPage.goToPage(navBarPage);
  });

  it('should load Albums', async () => {
    expect(await albumComponentsPage.title.getText()).to.match(/Albums/);
    expect(await albumComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Albums', async () => {
    const beforeRecordsCount = (await isVisible(albumComponentsPage.noRecords)) ? 0 : await getRecordsCount(albumComponentsPage.table);
    albumUpdatePage = await albumComponentsPage.goToCreateAlbum();
    await albumUpdatePage.enterData();
    expect(await isVisible(albumUpdatePage.saveButton)).to.be.false;

    expect(await albumComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(albumComponentsPage.table);
    await waitUntilCount(albumComponentsPage.records, beforeRecordsCount + 1);
    expect(await albumComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await albumComponentsPage.deleteAlbum();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(albumComponentsPage.records, beforeRecordsCount);
      expect(await albumComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(albumComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
