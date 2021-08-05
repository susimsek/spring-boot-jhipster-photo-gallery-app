import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import TagComponentsPage from './tag.page-object';
import TagUpdatePage from './tag-update.page-object';
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

describe('Tag e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let tagComponentsPage: TagComponentsPage;
  let tagUpdatePage: TagUpdatePage;
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
    tagComponentsPage = new TagComponentsPage();
    tagComponentsPage = await tagComponentsPage.goToPage(navBarPage);
  });

  it('should load Tags', async () => {
    expect(await tagComponentsPage.title.getText()).to.match(/Tags/);
    expect(await tagComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Tags', async () => {
    const beforeRecordsCount = (await isVisible(tagComponentsPage.noRecords)) ? 0 : await getRecordsCount(tagComponentsPage.table);
    tagUpdatePage = await tagComponentsPage.goToCreateTag();
    await tagUpdatePage.enterData();
    expect(await isVisible(tagUpdatePage.saveButton)).to.be.false;

    expect(await tagComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(tagComponentsPage.table);
    await waitUntilCount(tagComponentsPage.records, beforeRecordsCount + 1);
    expect(await tagComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await tagComponentsPage.deleteTag();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(tagComponentsPage.records, beforeRecordsCount);
      expect(await tagComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(tagComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
