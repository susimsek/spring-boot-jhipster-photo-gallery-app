import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import TagUpdatePage from './tag-update.page-object';

const expect = chai.expect;
export class TagDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('galleryApp.tag.delete.question'));
  private confirmButton = element(by.id('sys-confirm-delete-tag'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class TagComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('tag-heading'));
  noRecords: ElementFinder = element(by.css('#app-view-container .table-responsive div.alert.alert-warning'));
  table: ElementFinder = element(by.css('#app-view-container div.table-responsive > table'));

  records: ElementArrayFinder = this.table.all(by.css('tbody tr'));

  getDetailsButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-info.btn-sm'));
  }

  getEditButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-primary.btn-sm'));
  }

  getDeleteButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-danger.btn-sm'));
  }

  async goToPage(navBarPage: NavBarPage) {
    await navBarPage.getEntityPage('tag');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateTag() {
    await this.createButton.click();
    return new TagUpdatePage();
  }

  async deleteTag() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const tagDeleteDialog = new TagDeleteDialog();
    await waitUntilDisplayed(tagDeleteDialog.deleteModal);
    expect(await tagDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/galleryApp.tag.delete.question/);
    await tagDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(tagDeleteDialog.deleteModal);

    expect(await isVisible(tagDeleteDialog.deleteModal)).to.be.false;
  }
}
