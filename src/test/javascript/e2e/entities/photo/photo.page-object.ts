import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import PhotoUpdatePage from './photo-update.page-object';

const expect = chai.expect;
export class PhotoDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('galleryApp.photo.delete.question'));
  private confirmButton = element(by.id('sys-confirm-delete-photo'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class PhotoComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('photo-heading'));
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
    await navBarPage.getEntityPage('photo');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreatePhoto() {
    await this.createButton.click();
    return new PhotoUpdatePage();
  }

  async deletePhoto() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const photoDeleteDialog = new PhotoDeleteDialog();
    await waitUntilDisplayed(photoDeleteDialog.deleteModal);
    expect(await photoDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/galleryApp.photo.delete.question/);
    await photoDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(photoDeleteDialog.deleteModal);

    expect(await isVisible(photoDeleteDialog.deleteModal)).to.be.false;
  }
}
