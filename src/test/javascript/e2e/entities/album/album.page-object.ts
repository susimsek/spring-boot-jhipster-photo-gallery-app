import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import AlbumUpdatePage from './album-update.page-object';

const expect = chai.expect;
export class AlbumDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('galleryApp.album.delete.question'));
  private confirmButton = element(by.id('sys-confirm-delete-album'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class AlbumComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('album-heading'));
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
    await navBarPage.getEntityPage('album');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateAlbum() {
    await this.createButton.click();
    return new AlbumUpdatePage();
  }

  async deleteAlbum() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const albumDeleteDialog = new AlbumDeleteDialog();
    await waitUntilDisplayed(albumDeleteDialog.deleteModal);
    expect(await albumDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/galleryApp.album.delete.question/);
    await albumDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(albumDeleteDialog.deleteModal);

    expect(await isVisible(albumDeleteDialog.deleteModal)).to.be.false;
  }
}
